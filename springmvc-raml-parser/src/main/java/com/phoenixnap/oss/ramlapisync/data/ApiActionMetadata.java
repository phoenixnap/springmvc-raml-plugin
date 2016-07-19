/*
 * Copyright 2002-2016 the original author or authors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package com.phoenixnap.oss.ramlapisync.data;

import com.phoenixnap.oss.ramlapisync.naming.NamingHelper;
import com.phoenixnap.oss.ramlapisync.naming.RamlHelper;
import com.phoenixnap.oss.ramlapisync.naming.SchemaHelper;
import com.phoenixnap.oss.ramlapisync.parser.ResourceParser;
import com.phoenixnap.oss.ramlapisync.raml.RamlAction;
import com.phoenixnap.oss.ramlapisync.raml.RamlActionType;
import com.phoenixnap.oss.ramlapisync.raml.RamlMimeType;
import com.phoenixnap.oss.ramlapisync.raml.RamlResource;
import com.phoenixnap.oss.ramlapisync.raml.RamlResponse;
import com.phoenixnap.oss.ramlapisync.raml.RamlUriParameter;
import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;


/**
 * Class containing the data required to successfully generate code for an api call within a controller
 *
 * @author Kurt Paris
 * @since 0.2.1
 *
 */
public class ApiActionMetadata {

	ApiResourceMetadata parent;
	RamlResource resource;
	RamlActionType actionType;
	RamlAction action;

	String requestBodyMime = null;
	ApiBodyMetadata requestBody = null;
	Map<String, ApiBodyMetadata> responseBody = new LinkedHashMap<>();
	Set<ApiParameterMetadata> pathVariables = null;
	Set<ApiParameterMetadata> requestParameters = null;
	Set<ApiParameterMetadata> requestHeaders = null;
	boolean injectHttpHeadersParameter = false;

	private String responseContentTypeFilter;

	public ApiActionMetadata(ApiResourceMetadata parent, RamlResource resource, RamlActionType actionType, RamlAction action, String responseContentTypeFilter, boolean injectHttpHeadersParameter) {
		super();
		this.parent = parent;
		this.resource = resource;
		this.actionType = actionType;
		this.action = action;
		this.responseContentTypeFilter = responseContentTypeFilter;
		this.injectHttpHeadersParameter = injectHttpHeadersParameter;
		parseRequest();
		parseResponse(responseContentTypeFilter);

	}

	public ApiActionMetadata(ApiResourceMetadata parent, RamlResource resource, RamlActionType actionType, RamlAction action) {
		this(parent, resource, actionType, action, null, false);
	}

	public String toString() {
		return "Method " + getName() + "  Verb [" + actionType + "] Url [" + getUrl() + "] \nConsumes ["
				+ getConsumes() + "] Produces [" + getProduces() + "] with Schema [" + null + "] \nPath Vars ["
				+ StringUtils.collectionToCommaDelimitedString(getPathVariables()) + "] \nRequest Params ["
				+ StringUtils.collectionToCommaDelimitedString(getRequestParameters()) + "] \nRequest Headers ["
				+ StringUtils.collectionToCommaDelimitedString(getRequestHeaders()) + "] \n";

	}

	public Set<ApiParameterMetadata> getPathVariables() {
		if (pathVariables != null) {
			return pathVariables;
		}
		pathVariables = new LinkedHashSet<>();

		RamlResource targetResource = action.getResource();

		do {
			for (Entry<String, RamlUriParameter> param : targetResource.getUriParameters().entrySet()) {
				pathVariables.add(new ApiParameterMetadata(param.getKey(), param.getValue()));
			}
			targetResource = targetResource.getParentResource();
		} while (targetResource != null);

		return pathVariables;
	}

	public Set<ApiParameterMetadata> getRequestParameters() {
		if (requestParameters == null) {
			return Collections.emptySet();
		}

		return requestParameters;
	}

	public Set<ApiParameterMetadata> getRequestHeaders() {
		if (requestHeaders == null) {
			return Collections.emptySet();
		}

		return requestHeaders;
	}


	private void parseRequest() {
		requestParameters = action.getQueryParameters().entrySet().stream()
				.map(param -> new ApiParameterMetadata(param.getKey(), param.getValue()))
				.collect(Collectors.toCollection(LinkedHashSet::new));
		requestHeaders = action.getHeaders().entrySet().stream()
				.map(param -> new ApiParameterMetadata(param.getKey(), param.getValue()))
				.collect(Collectors.toCollection(LinkedHashSet::new));
		if (action.getBody() != null && !action.getBody().isEmpty()) {
			action.getBody().entrySet().forEach(this::collectBodyParams);
		}
	}

	private void collectBodyParams(Entry<String, RamlMimeType> mime) {
		if (mime.getKey().equals(MediaType.MULTIPART_FORM_DATA_VALUE) && ResourceParser.doesActionTypeSupportMultipartMime(actionType)) {
			collectRequestParamsForMime(action.getBody().get(MediaType.MULTIPART_FORM_DATA_VALUE));
		} else if (mime.getKey().equals(MediaType.APPLICATION_FORM_URLENCODED_VALUE) && ResourceParser.doesActionTypeSupportMultipartMime(actionType)) {
			collectRequestParamsForMime(action.getBody().get(MediaType.APPLICATION_FORM_URLENCODED_VALUE));
		}

		if (ResourceParser.doesActionTypeSupportRequestBody(actionType) && mime.getKey().toLowerCase().contains("json")) {
			// Continue here!
			String schema = mime.getValue().getSchema();
			if (StringUtils.hasText(schema)) {
				ApiBodyMetadata requestBody = SchemaHelper.mapSchemaToPojo(parent.getDocument(), schema, parent.getBasePackage()
						+ NamingHelper.getDefaultModelPackage(), StringUtils.capitalize(getName()) + "Request", null);
				if (requestBody != null) {
					setRequestBody(requestBody, mime.getKey());
				}
			}
		}
	}

	private void collectRequestParamsForMime(RamlMimeType requestBody) {
		if(requestBody == null) return;
		for (Entry<String, List<RamlFormParameter>> params : requestBody.getFormParameters().entrySet()) {
			for (RamlFormParameter param : params.getValue()) {
				requestParameters.add(new ApiParameterMetadata(params.getKey(), param));
			}
		}
	}

	private void parseResponse(String responseContentTypeFilter) {
		RamlResponse response = RamlHelper.getSuccessfulResponse(action);

		if (response != null && response.getBody() != null && !response.getBody().isEmpty()) {
			for (Entry<String, RamlMimeType> body : response.getBody().entrySet()) {
				if (responseContentTypeFilter == null || body.getKey().equals(responseContentTypeFilter)) {
					if (body.getKey().toLowerCase().contains("json")) { //if we have a json type we need to return an object
						// Continue here!
						String schema = body.getValue().getSchema();
						if (StringUtils.hasText(schema)) {
							ApiBodyMetadata responseBody = SchemaHelper.mapSchemaToPojo(parent.getDocument(), schema,
									parent.getBasePackage() + NamingHelper.getDefaultModelPackage(), StringUtils.capitalize(getName()) + "Response", null);
							if (responseBody != null) {
								this.responseBody.put(body.getKey(), responseBody);
							}
						}
					}
				}
			}
		}
	}

	public String getUrl() {
		return resource.getUri().replace(parent.getResourceUri(), "");
	}

	public String getName() {
		String name = NamingHelper.getActionName(parent.getResource(), resource, action, actionType);
		if (responseContentTypeFilter != null) {
			name += NamingHelper.convertContentTypeToQualifier(responseContentTypeFilter);
		}
		return name;
	}

	public String getProduces() {
		if (responseBody != null && !responseBody.isEmpty()) {
			String out = "";
			boolean first = true;
			for (String key : responseBody.keySet()) {
				if (first) {
					first = false;
				} else {
					out += ",";
				}
				out += key;
			}
			return out;
		}

		return null;
	}

	public String getConsumes() {
		if (action.hasBody()) {
			String out = null;
			boolean first = true;
			// Special Case - ignore application/x-www-form-urlencoded in POST
			// since we will be treating them as request params
			for (String key : action.getBody().keySet()) {
				if (MediaType.APPLICATION_FORM_URLENCODED_VALUE.equals(key)) {
					continue;
				}
				if (first) {
					first = false;
					out = "";
				} else {
					out += ",";
				}
				out += key;
			}
			return out;
		}

		return null;
	}

	public String getDescription() {
		return action.getDescription();
	}

	public ApiResourceMetadata getParent() {
		return parent;
	}

	public void setParent(ApiResourceMetadata parent) {
		this.parent = parent;
	}

	public RamlResource getResource() {
		return resource;
	}

	public void setResource(RamlResource resource) {
		this.resource = resource;
	}

	public RamlActionType getActionType() {
		return actionType;
	}

	public void setActionType(RamlActionType actionType) {
		this.actionType = actionType;
	}

	public RamlAction getAction() {
		return action;
	}

	public void setAction(RamlAction action) {
		this.action = action;
	}

	public ApiBodyMetadata getRequestBody() {
		return requestBody;
	}

	private void setRequestBody(ApiBodyMetadata requestBody, String mimeType) {
		if (this.requestBody == null) {
			this.requestBodyMime = mimeType;
			this.requestBody = requestBody;
		} else {
			throw new IllegalStateException("Body Metadata is immutable");
		}
	}

	public void setPathVariables(Set<ApiParameterMetadata> pathVariables) {
		this.pathVariables = pathVariables;
	}

	public void setRequestParameters(Set<ApiParameterMetadata> requestParameters) {
		this.requestParameters = requestParameters;
	}

	public Map<String, ApiBodyMetadata> getResponseBody() {
		return responseBody;
	}

	public String getRequestBodyMime() {
		return requestBodyMime;
	}

	public void setRequestBodyMime(String requestBodyMime) {
		this.requestBodyMime = requestBodyMime;
	}

	public boolean getInjectHttpHeadersParameter() {
		return injectHttpHeadersParameter;
	}
}
