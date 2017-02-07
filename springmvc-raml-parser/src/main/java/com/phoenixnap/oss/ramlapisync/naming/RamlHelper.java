/*
 * Copyright 2002-2017 the original author or authors.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package com.phoenixnap.oss.ramlapisync.naming;

import com.phoenixnap.oss.ramlapisync.raml.RamlAction;
import com.phoenixnap.oss.ramlapisync.raml.RamlMimeType;
import com.phoenixnap.oss.ramlapisync.raml.RamlResource;
import com.phoenixnap.oss.ramlapisync.raml.RamlResourceRoot;
import com.phoenixnap.oss.ramlapisync.raml.RamlResponse;
import com.phoenixnap.oss.ramlapisync.raml.RamlRoot;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.Map;
import java.util.Map.Entry;

/**
 * Class containing utility methods for modifying Raml models
 * 
 * @author kurtpa
 * @since 0.5.3
 *
 */
public class RamlHelper {
	
	/**
	 * Tree merging algorithm, if a resource already exists it will not overwrite and add all children to the existing resource 
	 * @param existing The existing resource in the model
	 * @param resource The resource to merge in
	 * @param addActions If true it will copy all actions even if the resource itself isnt copied
	 */
	public static void mergeResources(RamlResource existing, RamlResource resource, boolean addActions) {
		Map<String, RamlResource> existingChildResources = existing.getResources();
		Map<String, RamlResource> newChildResources = resource.getResources();
		for (String newChildKey : newChildResources.keySet()) {
			if (!existingChildResources.containsKey(newChildKey)) {
				existing.addResource(newChildKey, newChildResources.get(newChildKey));
			} else {
				mergeResources(existingChildResources.get(newChildKey), newChildResources.get(newChildKey), addActions);
			}			
		}
		
		if (addActions) {
			existing.addActions(resource.getActions());
		}
	}
	
	/**
	 * Merges two RAML Resources trees together. This is non-recursive and could currently lose children in lower
	 * levels.
	 * @param raml The RAML model to merge into
	 * @param resource The candidate resource
	 * @param addActions whether we should add actions
	 */
	public static void mergeResources(RamlRoot raml, RamlResource resource, boolean addActions) {
		RamlResource existingResource = raml.getResource(resource.getRelativeUri());
		if (existingResource == null) {
			raml.addResource(resource.getRelativeUri(), resource);
		} else {
			mergeResources(existingResource, resource, addActions);
		}
	}
	
	/**
	 * Merges together existing actions. At present we are doing the following:
	 * 
	 * - Add Response bodies from the new to existing
	 * 
	 * TODO Other operations that we should consider. Not adding these until an actual usecase crops up.
	 * - Merging Descriptions?
	 * - Copying over Request Data?
	 * - Copying over other responses?
	 * 
	 * @param existingAction The action we already have in our model
	 * @param newAction The action we we want to include in the model
	 */
	public static void mergeActions (RamlAction existingAction, RamlAction newAction) {
		RamlResponse existingSuccessfulResponse = getSuccessfulResponse(existingAction);
		RamlResponse successfulResponse = getSuccessfulResponse(newAction);

		if (existingSuccessfulResponse != null && existingSuccessfulResponse.hasBody() && successfulResponse != null && successfulResponse.hasBody()) {
			for (Entry<String, RamlMimeType> body : successfulResponse.getBody().entrySet()) {
				existingSuccessfulResponse.addToBody(body.getKey(), body.getValue());
			}
		}
	}
	
	/**
	 * Gets the successful response from an action (200 or 201)
	 * 
	 * @param action The action to parse
	 * @return The Successful response or null if not found
	 */
	public static RamlResponse getSuccessfulResponse(RamlAction action) {
		String[] successfulResponses = new String[] {"200", "201", "202"};
		for (String code : successfulResponses) {
			if (action != null && !CollectionUtils.isEmpty(action.getResponses()) && action.getResponses().containsKey(code)) {
				return action.getResponses().get(code);
			}
		}
		return null;
	}

	/**
	 * Removes a section of the tree from the resources in a Raml model and moves all sub resources to the root
	 * 
	 * @param model The Raml model to modify
	 * @param urlPrefixToIgnore The section of the URL to remove
	 */
	public static void removeResourceTree(RamlRoot model, String urlPrefixToIgnore) {
		if (StringUtils.hasText(urlPrefixToIgnore)) {
			String[] urlParts = urlPrefixToIgnore.split("/");
			String firstResourcePart = null;
			RamlResource pointerResource = null;
			for(String part : urlParts) {
				if (StringUtils.hasText(part)) {
					
					if (pointerResource != null) {
						pointerResource = pointerResource.getResource("/"+part);
					} else {
						if (model.getResources().get("/") != null) {
							pointerResource = model.getResource("/").getResource("/"+part); //skip root node
						} else {
							pointerResource = model.getResource("/"+part);
						}
					}
					
					if (pointerResource == null) {						
						throw new IllegalStateException("Attempting to ignore url prefix [" + urlPrefixToIgnore + "] and failed to find resource on [" +part+"]" );
					}
					if (firstResourcePart == null) { 
						firstResourcePart = "/"+part;
					}
				}
			}

			RamlResourceRoot parentResource = model;
			if (model.getResource("/") != null) {
				parentResource = model.getResource("/");
			}
			parentResource.removeResource(firstResourcePart);
			
			removeUri(pointerResource.getResources(), urlPrefixToIgnore);

			parentResource.addResources(pointerResource.getResources());
			
			
			
		}
		
	}

	/**
	 * Adjusts Relative and base uris in the resource objects
	 * 
	 * @param resources resources to check
	 * @param urlPrefixToIgnore uri to remove
	 */
	private static void removeUri(Map<String, RamlResource> resources, String urlPrefixToIgnore) {
		for (RamlResource resource : resources.values()) {
			resource.setParentUri(resource.getParentUri().replace(urlPrefixToIgnore, ""));
			resource.setRelativeUri(resource.getRelativeUri().replace(urlPrefixToIgnore, ""));
			removeUri(resource.getResources(), urlPrefixToIgnore);
		}
	}

}
