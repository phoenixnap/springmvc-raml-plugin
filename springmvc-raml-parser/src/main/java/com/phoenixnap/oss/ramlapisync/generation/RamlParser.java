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
package com.phoenixnap.oss.ramlapisync.generation;

import java.util.LinkedHashSet;
import java.util.Map.Entry;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.phoenixnap.oss.ramlapisync.data.ApiResourceMetadata;
import com.phoenixnap.oss.ramlapisync.naming.RamlHelper;
import com.phoenixnap.oss.ramlapisync.pojo.PojoGenerationConfig;
import com.phoenixnap.oss.ramlapisync.raml.RamlAction;
import com.phoenixnap.oss.ramlapisync.raml.RamlActionType;
import com.phoenixnap.oss.ramlapisync.raml.RamlResource;
import com.phoenixnap.oss.ramlapisync.raml.RamlResponse;
import com.phoenixnap.oss.ramlapisync.raml.RamlRoot;
import com.sun.codemodel.JCodeModel;


/**
 * 
 * Class containing methods that are used to parse raml files for code generation from the RAML
 * 
 * @author Kurt Paris
 * @since 0.2.1
 *
 */	
public class RamlParser {

	/**
	 * Class Logger
	 */
	protected static final Logger logger = LoggerFactory.getLogger(RamlParser.class);

	/**
	 * Base configuration for code generation
	 */
	private PojoGenerationConfig config;

	/**
	 * The start URL that every controller should be prefixed with
	 */
	private String startUrl = "";
	
	/**
	 * If set to true, we will generate seperate methods for different content types in the RAML
	 */
	protected boolean seperateMethodsByContentType = false;

	/**
	 * If set to true, we will add a HttpHeaders parameter in the action methods
	 */
	protected boolean injectHttpHeadersParameter = false;

	public RamlParser (PojoGenerationConfig config) {
		this.config = config;
	}
	
	public RamlParser (String basePackage) {
		config = new PojoGenerationConfig().withPackage(basePackage, null);
	}
	
	public RamlParser(String basePackage, String startUrl, boolean seperateMethodsByContentType, boolean injectHttpHeadersParameter) {
		this(new PojoGenerationConfig().withPackage(basePackage, null), startUrl, seperateMethodsByContentType, injectHttpHeadersParameter);
	}
	
	public RamlParser(PojoGenerationConfig config, String startUrl, boolean seperateMethodsByContentType, boolean injectHttpHeadersParameter) {
		this(config);
		this.seperateMethodsByContentType = seperateMethodsByContentType;
		this.injectHttpHeadersParameter = injectHttpHeadersParameter;
		this.startUrl = startUrl;
	}

	/**
	 * This method will extract a set of controllers from the RAML file.
	 * These controllers will contain the metadata required by the code generator, including name
	 * any annotations as well as conatining methods
	 * 
	 * @param bodyCodeModel the code model containing body objects
	 * @param raml The raml document to be parsed
	 * @return A set of Controllers representing the inferred resources in the system
	 */
	public Set<ApiResourceMetadata> extractControllers (JCodeModel bodyCodeModel, RamlRoot raml) {
		
		Set<ApiResourceMetadata> controllers = new LinkedHashSet<>();
		if (raml == null) {
			return controllers;
		}
		if (bodyCodeModel == null) {
			bodyCodeModel = new JCodeModel();
		}

		Set<String> names = new LinkedHashSet<>();
		Set<String> namesToDisable = new LinkedHashSet<>();
		//Iterate on all parent resources
		//if we have child resources, just append the url and go down the chain until we hit the first action.
		//if an action is found we need to 
		for (Entry<String, RamlResource> resource : raml.getResources().entrySet()) {
			Set<ApiResourceMetadata> resources = checkResource(bodyCodeModel, startUrl, resource.getValue(), null, raml);
			for (ApiResourceMetadata resourceMetadata : resources) {
				if (names.contains(resourceMetadata.getResourceName())) {
					//collision has occured, lets mark this for 2nd pass
					namesToDisable.add(resourceMetadata.getResourceName());
				}
				names.add(resourceMetadata.getResourceName());
				controllers.add(resourceMetadata);
			}
		}
		
		//second pass, disabling singularisation
		for (ApiResourceMetadata resourceMetadata : controllers) {
			if (namesToDisable.contains(resourceMetadata.getResourceName())) {
				resourceMetadata.setSingularizeName(false);
			}
		}
		
		return controllers;
	}
	
	private boolean shouldCreateController (RamlResource resource) {
		
		//If controller has actions create it
		if (resource.getActions() != null && !resource.getActions().isEmpty()) {
			return true;
		} 
		
		//Lookahead to child resource - if the child has a uriParameter then it's likely that we are at a good resource depth
		if (resource.getResources() != null &&  !resource.getResources().isEmpty()) {
			for (RamlResource childResource : resource.getResources().values()) {
				if (childResource.getUriParameters() != null && !childResource.getUriParameters().isEmpty() 
						|| (childResource.getResolvedUriParameters() != null && !childResource.getResolvedUriParameters().isEmpty())) {
					return true;
				}
			}
		}
		
		return false;
	}
	
	/**
	 * Recursive method to parse resources in a Raml File. It tries to go as deep as possible before creating the root Resource. Once this is done, methods and
	 * child resources will be relative to the root resource
	 * 
	 * @param bodyCodeModel The code model containing body pojos
	 * @param baseUrl The url currently being checked. Used to keep depth
	 * @param resource The Resource in the RAML file being parsed
	 * @param controller The root controller if created for this branch
	 * @param document The raml Document being parse
	 * @return A set of Controllers representing resources in this branch of the tree
	 */
	public Set<ApiResourceMetadata> checkResource(JCodeModel bodyCodeModel, String baseUrl, RamlResource resource, ApiResourceMetadata controller, RamlRoot document) {
		Set<ApiResourceMetadata> controllers = new LinkedHashSet<>();
		//append resource URL to url.
		String url = baseUrl + resource.getRelativeUri();
		if (controller == null && shouldCreateController(resource)) {
			controller = new ApiResourceMetadata(config, bodyCodeModel, url, resource, document);
			controllers.add(controller);
		}
		//extract actions for this resource
		if (resource.getActions() != null && !resource.getActions().isEmpty()) {	
			for (RamlActionType actionType : RamlActionType.values()) {
				if (resource.getActions().containsKey(actionType)) {
					RamlAction childResource = resource.getActions().get(actionType);
					
					//if we have multiple response types in the raml, this should produce different calls
					RamlResponse response = null;
					
					if (childResource.getResponses() != null) {
						response = RamlHelper.getSuccessfulResponse(childResource);
					}
					
					if (seperateMethodsByContentType && response != null && response.hasBody() && response.getBody().size() > 1) {
							for (String responseType : response.getBody().keySet()) {
								controller.addApiCall(resource, actionType, childResource, responseType, injectHttpHeadersParameter);
							}
						
					} else {
						controller.addApiCall(resource, actionType, childResource, null, injectHttpHeadersParameter);
					}
				}
			}
		}
		if (resource.getResources() != null &&  !resource.getResources().isEmpty()) {
			for (Entry<String, RamlResource> childResource : resource.getResources().entrySet()) {
				controllers.addAll(checkResource(bodyCodeModel, url, childResource.getValue(), controller,document));
			}
		}
		return controllers;	
	}

	
}
