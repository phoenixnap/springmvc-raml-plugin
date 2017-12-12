/*
 * Copyright 2002-2017 the original author or authors.
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

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;

import com.phoenixnap.oss.ramlapisync.naming.NamingHelper;
import com.phoenixnap.oss.ramlapisync.naming.RamlHelper;
import com.phoenixnap.oss.ramlapisync.naming.RamlTypeHelper;
import com.phoenixnap.oss.ramlapisync.naming.SchemaHelper;
import com.phoenixnap.oss.ramlapisync.parser.ResourceParser;
import com.phoenixnap.oss.ramlapisync.pojo.PojoGenerationConfig;
import com.phoenixnap.oss.ramlapisync.raml.RamlAction;
import com.phoenixnap.oss.ramlapisync.raml.RamlActionType;
import com.phoenixnap.oss.ramlapisync.raml.RamlDataType;
import com.phoenixnap.oss.ramlapisync.raml.RamlMimeType;
import com.phoenixnap.oss.ramlapisync.raml.RamlQueryParameter;
import com.phoenixnap.oss.ramlapisync.raml.RamlResource;
import com.phoenixnap.oss.ramlapisync.raml.RamlResponse;
import com.phoenixnap.oss.ramlapisync.raml.RamlUriParameter;
import com.phoenixnap.oss.ramlapisync.raml.rjp.raml10v2.RJP10V2RamlQueryParameter;
import com.sun.codemodel.JCodeModel;


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
	Set<ApiParameterMetadata> requestParameters = new LinkedHashSet<>();
	Set<ApiParameterMetadata> requestHeaders = null;
	boolean injectHttpHeadersParameter = false;

	private String responseContentTypeFilter;

	public ApiActionMetadata(PojoGenerationConfig config, ApiResourceMetadata parent, RamlResource resource, RamlActionType actionType, RamlAction action, String responseContentTypeFilter, boolean injectHttpHeadersParameter) {
		super();
		this.parent = parent;
		this.resource = resource;
		this.actionType = actionType;
		this.action = action;
		this.responseContentTypeFilter = responseContentTypeFilter;
		this.injectHttpHeadersParameter = injectHttpHeadersParameter;
		parseRequest(config, parent.getBodyCodeModel());
		parseResponse(config, parent.getBodyCodeModel(), responseContentTypeFilter);

	}

	public ApiActionMetadata(PojoGenerationConfig config, ApiResourceMetadata parent, RamlResource resource, RamlActionType actionType, RamlAction action) {
		this(config, parent, resource, actionType, action, null, false);
	}

	@Override
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
				pathVariables.add(new ApiParameterMetadata(param.getKey(), param.getValue(), null));
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


	private void parseRequest(PojoGenerationConfig config, JCodeModel codeModel) {

		if (action.getQueryParameters() != null && !action.getQueryParameters().isEmpty()) {
			for (Entry<String, RamlQueryParameter> entry : action.getQueryParameters().entrySet()) {
				collectQueryParams(config, codeModel, entry);
			}
		}
		requestHeaders = action.getHeaders().entrySet().stream()
				.map(param -> new ApiParameterMetadata(param.getKey(), param.getValue(), codeModel))
				.collect(Collectors.toCollection(LinkedHashSet::new));
		if (action.getBody() != null && !action.getBody().isEmpty()) {
			for (Entry<String, RamlMimeType> entry : action.getBody().entrySet()) {
				collectBodyParams(config, codeModel, entry);
			}
		}
	}

	private void collectQueryParams(PojoGenerationConfig config, JCodeModel codeModel,
			Entry<String, RamlQueryParameter> queryParameter) {

		if (queryParameter.getValue() instanceof RJP10V2RamlQueryParameter) {

			RamlDataType type = ((RJP10V2RamlQueryParameter) queryParameter.getValue()).getRamlDataType();
			if (type != null) {
				RamlTypeHelper.mapTypeToPojo(config, codeModel, parent.getDocument(), type.getType(), null);
			}
		}
		requestParameters.add(new ApiParameterMetadata(queryParameter.getKey(), queryParameter.getValue(), codeModel));
	}

	private void collectBodyParams(PojoGenerationConfig config, JCodeModel codeModel, Entry<String, RamlMimeType> mime) {
		if (mime.getKey().equals(MediaType.MULTIPART_FORM_DATA_VALUE) && ResourceParser.doesActionTypeSupportMultipartMime(actionType)) {
			collectRequestParamsForMime(action.getBody().get(MediaType.MULTIPART_FORM_DATA_VALUE));
		} else if (mime.getKey().equals(MediaType.APPLICATION_FORM_URLENCODED_VALUE) && ResourceParser.doesActionTypeSupportMultipartMime(actionType)) {
			collectRequestParamsForMime(action.getBody().get(MediaType.APPLICATION_FORM_URLENCODED_VALUE));
		}

		if (ResourceParser.doesActionTypeSupportRequestBody(actionType) 
				&& (mime.getKey().toLowerCase().contains("json") 
						|| mime.getKey().toLowerCase().equals("body"))) {
			// Continue here!
			String schema = mime.getValue().getSchema();
			RamlDataType type = mime.getValue().getType();
			//prefer type if we have it.
			ApiBodyMetadata requestBody = null;
			
			String name = StringUtils.capitalize(getName()) + "Request";
			if (type != null && type.getType() != null) {
				requestBody = RamlTypeHelper.mapTypeToPojo(config, codeModel, parent.getDocument(), type.getType(), name);
			} else if (StringUtils.hasText(schema)) {
				requestBody = SchemaHelper.mapSchemaToPojo(parent.getDocument(), schema, config.getPojoPackage(), name, null);
			}
			if (requestBody != null) {
				setRequestBody(requestBody, mime.getKey());
			}
		}
	}

	private void collectRequestParamsForMime(RamlMimeType requestBody) {
		if(requestBody == null) return;
		for (Entry<String, List<RamlFormParameter>> params : requestBody.getFormParameters().entrySet()) {
			for (RamlFormParameter param : params.getValue()) {
				requestParameters.add(new ApiParameterMetadata(params.getKey(), param, null));
			}
		}
	}

	private void parseResponse(PojoGenerationConfig config, JCodeModel codeModel, String responseContentTypeFilter) {
		RamlResponse response = RamlHelper.getSuccessfulResponse(action);

		if (response != null && response.getBody() != null && !response.getBody().isEmpty()) {
			for (Entry<String, RamlMimeType> body : response.getBody().entrySet()) {
				if (responseContentTypeFilter == null || body.getKey().equals(responseContentTypeFilter)) {
					if (body.getKey().toLowerCase().contains("json") 
							|| body.getKey().toLowerCase().equals("body")) { //if we have a json type we need to return an object
						// Continue here!
						ApiBodyMetadata responseBody = null;
						
						RamlDataType type = body.getValue().getType();
						String schema = body.getValue().getSchema();
						//prefer type if we have it.
						String name = StringUtils.capitalize(getName()) + "Response";
						if (type != null && type.getType() != null) {
							responseBody = RamlTypeHelper.mapTypeToPojo(config, codeModel, parent.getDocument(), type.getType(),  name);
						} else if (StringUtils.hasText(schema)) {
							responseBody = SchemaHelper.mapSchemaToPojo(parent.getDocument(), schema, config.getPojoPackage(), name, null);
						}
						
						if (responseBody != null) {
							this.responseBody.put(body.getKey(), responseBody);
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
		String name = NamingHelper.getActionName(parent.getResource(), resource, actionType);
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
				if (key.equals("body")) {
					try {
						out += parent.getDocument().getMediaType();
					} catch (Exception ex) {						
						// skip						
					}
				} else {
					out += key;
				}
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
