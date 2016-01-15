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

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import org.raml.model.Action;
import org.raml.model.ActionType;
import org.raml.model.MimeType;
import org.raml.model.Resource;
import org.raml.model.Response;
import org.raml.model.parameter.FormParameter;
import org.raml.model.parameter.QueryParameter;
import org.raml.model.parameter.UriParameter;
import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;

import com.phoenixnap.oss.ramlapisync.naming.NamingHelper;

/**
 * Class containing the data required to successfully generate code for an api call within a controller
 * 
 * @author Kurt Paris
 * @since 0.2.1
 *
 */	
public class ApiMappingMetadata {
	
	ApiControllerMetadata parent;
	Resource resource; 
	ActionType actionType;
	Action action;
	
	Set<ApiParameterMetadata> pathVariables = null;
	Set<ApiParameterMetadata> requestParameters = null;
	
	public ApiMappingMetadata(ApiControllerMetadata parent, Resource resource, ActionType actionType,
			Action action) {
		super();
		this.parent = parent;
		this.resource = resource;
		this.actionType = actionType;
		this.action = action;
	}
	
	 public String toString() {
	    	return "Method "+getName()+"  Verb ["+actionType+"] Url ["+getUrl()+"] \nConsumes ["+getConsumes()+"] Produces ["+getProduces()+"] with Schema [" +null+ "] \nPath Vars ["+StringUtils.collectionToCommaDelimitedString(getPathVariables())+"] \nRequest Params ["+StringUtils.collectionToCommaDelimitedString(getRequestParameters())+"] \n";
	    	
	 }
	 
	 public Set<ApiParameterMetadata> getPathVariables() {
		 if (pathVariables != null) {
			 return pathVariables;
		 }
		 pathVariables = new LinkedHashSet<>();
		 
		 Resource targetResource = action.getResource();
		 
		 do {
			 for ( Entry<String, UriParameter> param : targetResource.getUriParameters().entrySet()) {
					 pathVariables.add(new ApiParameterMetadata(param.getKey(), param.getValue()));				 
			 }
			 targetResource = targetResource.getParentResource();
		 } while (targetResource != null);
		
		return pathVariables;
	}

	 public Set<ApiParameterMetadata> getRequestParameters() {
		 if (requestParameters != null) {
			 return requestParameters;
		 }
		 requestParameters = new LinkedHashSet<>();
		 for ( Entry<String, QueryParameter> param : action.getQueryParameters().entrySet()) {
			 requestParameters.add(new ApiParameterMetadata(param.getKey(), param.getValue()));				 
		 }
		 if (ActionType.POST.equals(actionType) && action.getBody() != null && action.getBody().containsKey(MediaType.APPLICATION_FORM_URLENCODED_VALUE)) {
			 MimeType requestBody = action.getBody().get(MediaType.APPLICATION_FORM_URLENCODED_VALUE);
			 for ( Entry<String, List<FormParameter>> params : requestBody.getFormParameters().entrySet()) {
				 for (FormParameter param : params.getValue()) {
					 requestParameters.add(new ApiParameterMetadata(params.getKey(), param));				 
				 }
			 }
		 }
		return requestParameters;
	}

	 public String getUrl() {
		return resource.getUri().replaceAll(parent.getUrl(), "");
	 }
	
	 public String getName() {
		 return NamingHelper.getActionName(parent.getResource(), resource, action, actionType);
	 }
	 
	 public String getProduces() {
		 if (action.getResponses() != null && !action.getResponses().isEmpty() && action.getResponses().containsKey("200")) {
			 	Response response = action.getResponses().get("200");
			 	
				String out = "";
				boolean first = true;
				for (String key : response.getBody().keySet()) {					
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
			//Special Case - ignore application/x-www-form-urlencoded in POST since we will be treating them as request params
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

}
