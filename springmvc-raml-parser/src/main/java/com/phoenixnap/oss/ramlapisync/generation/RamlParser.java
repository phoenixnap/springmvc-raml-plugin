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
package com.phoenixnap.oss.ramlapisync.generation;

import java.util.LinkedHashSet;
import java.util.Map.Entry;
import java.util.Set;

import org.raml.model.Action;
import org.raml.model.ActionType;
import org.raml.model.Raml;
import org.raml.model.Resource;
import org.raml.parser.visitor.RamlDocumentBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.phoenixnap.oss.ramlapisync.data.ApiControllerMetadata;


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
	 * Base java package for generates files
	 */
	private String basePackage;
	
	
	public RamlParser (String basePackage) {
		this.basePackage = basePackage;
	}
	
	/**
	 * Class Logger
	 */
	protected static final Logger logger = LoggerFactory.getLogger(RamlParser.class);
	
	/**
	 * This method will extract a set of controllers from the RAML file.
	 * These controllers will contain the metadata required by the code generator, including name
	 * any annotations as well as conatining methods
	 * 
	 * @param raml
	 * @return
	 */
	public Set<ApiControllerMetadata> extractControllers (Raml raml) {
		
		Set<ApiControllerMetadata> controllers = new LinkedHashSet<>();
		if (raml == null) {
			return controllers;
		}
		
		String startUrl = "";
		
		//Iterate on all parent resources
		//if we have child resources, just append the url and go down the chain until we hit the first action.
		//if an action is found we need to 
		for (Entry<String, Resource> resource : raml.getResources().entrySet()) {
			controllers.addAll(checkResource(startUrl, resource.getValue(), null, raml));
		}
		
		return controllers;
	}
	
	private boolean shouldCreateController (Resource resource) {
		
		//If controller has actions create it
		if (resource.getActions() != null && !resource.getActions().isEmpty()) {
			return true;
		} 
		
		//Lookahead to child resource - if the child has a uriParameter then it's likely that we are at a good resource depth
		if (resource.getResources() != null &&  !resource.getResources().isEmpty()) {
			for (Resource childResource : resource.getResources().values()) {				
				if (childResource.getUriParameters() != null && !childResource.getUriParameters().isEmpty() 
						|| (childResource.getResolvedUriParameters() != null && !childResource.getResolvedUriParameters().isEmpty())) {
					return true;
				}
			}
		}
		
		return false;
	}
	
	public Set<ApiControllerMetadata> checkResource(String baseUrl, Resource resource, ApiControllerMetadata controller, Raml document) {
		Set<ApiControllerMetadata> controllers = new LinkedHashSet<>();
		//append resource URL to url.
		String url = baseUrl + resource.getRelativeUri();
		if (controller == null && shouldCreateController(resource)) {
			controller = new ApiControllerMetadata(url, resource, basePackage, document);
			controllers.add(controller);
		}
		//extract actions for this resource
		if (resource.getActions() != null && !resource.getActions().isEmpty()) {			
			for (Entry<ActionType, Action> childResource : resource.getActions().entrySet()) {
				controller.addApiCall(resource, childResource.getKey(), childResource.getValue());
			}
		}
		if (resource.getResources() != null &&  !resource.getResources().isEmpty()) {
			for (Entry<String, Resource> childResource : resource.getResources().entrySet()) {
				controllers.addAll(checkResource(url, childResource.getValue(), controller,document));
			}
		}
		return controllers;	
	}
	
	/**
	 * Loads a RAML document from a file. This method will
	 * 
	 * @param ramlFileUrl The path to the file, this can either be a resource on the class path (in which case the classpath: prefix should be omitted) or a file on disk (in which case the file: prefix should be included)
	 * @return Built Raml model
	 */
	public static Raml loadRamlFromFile(String ramlFileUrl) {
		try {
			return new RamlDocumentBuilder().build(ramlFileUrl);
		} catch (NullPointerException npe) {
			logger.error("File not found at " + ramlFileUrl);
			return null;
		}
	}

	
}
