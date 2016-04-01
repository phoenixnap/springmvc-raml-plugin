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

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

import org.raml.model.Action;
import org.raml.model.ActionType;
import org.raml.model.Raml;
import org.raml.model.Resource;

import com.phoenixnap.oss.ramlapisync.naming.NamingHelper;


/**
 * 
 * Class containing the data required to successfully generate code for an api rest controller within spring mvc
 * 
 * @author Kurt Paris
 * @since 0.2.1
 *
 */	
public class ApiControllerMetadata {
	
	public static final String CONTROLLER_SUFFIX = "Controller";
	
	private String controllerUrl;
	private transient Resource resource;
	private String basePackage;
	private Raml document;
	
	Set<ApiMappingMetadata> apiCalls = new LinkedHashSet<>();
	
	public ApiControllerMetadata(String controllerUrl, Resource resource, String basePackage, Raml document) {
		super();
		this.controllerUrl = controllerUrl;
		this.resource = resource;
		this.basePackage = basePackage;
		this.document = document;
	} 
	
	
	public void addApiCall(Resource resource, ActionType actionType, Action action) {
		apiCalls.add(new ApiMappingMetadata(this, resource, actionType, action));
	}
	
	public void addApiCall(Resource resource, ActionType actionType, Action action, String responseContentType) {
		apiCalls.add(new ApiMappingMetadata(this, resource, actionType, action, responseContentType));
	}
	
    public Set<ApiMappingMetadata> getApiCalls() {
		return Collections.unmodifiableSet(apiCalls);
	}
    
    public String getName() {
    	
    	String name = NamingHelper.getResourceName(resource);
    	if (name != null) {
    		return name + CONTROLLER_SUFFIX;
    	}
    	return CONTROLLER_SUFFIX; //TODO Is there a better way? should this even happen though? really?
    	
    }


	public Resource getResource() {
		return resource;
	}
	
	public String getResourceUri() {
		return resource.getUri();
	}

	public String getControllerUrl() {
		return controllerUrl;
	}


	public String toString() {
    	return "Controller "+getName()+"["+ getControllerUrl() +"]";
    	
    }

	public String getBasePackage() {
		return basePackage;
	}	
	
	public Set<ApiBodyMetadata> getDependencies() {
		Set<ApiBodyMetadata> dependencies = new LinkedHashSet<>();
		for (ApiMappingMetadata method : apiCalls) {
			if (method.getRequestBody() != null) {
				dependencies.add(method.getRequestBody());
			}
			dependencies.addAll(method.getResponseBody().values());
		}
		return dependencies;
	}

	public String getDescription() {
		return resource.getDescription();
	}


	public Raml getDocument() {
		return document;
	}
}
