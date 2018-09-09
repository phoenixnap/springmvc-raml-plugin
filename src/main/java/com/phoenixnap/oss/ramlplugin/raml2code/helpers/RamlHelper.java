/*
 * Copyright 2002-2017 the original author or authors.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package com.phoenixnap.oss.ramlplugin.raml2code.helpers;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.util.CollectionUtils;

import com.phoenixnap.oss.ramlplugin.raml2code.raml.RamlAction;
import com.phoenixnap.oss.ramlplugin.raml2code.raml.RamlResource;
import com.phoenixnap.oss.ramlplugin.raml2code.raml.RamlResponse;
import com.phoenixnap.oss.ramlplugin.raml2code.raml.RamlRoot;
import com.phoenixnap.oss.ramlplugin.raml2code.raml.RamlSecurityReference;
import com.phoenixnap.oss.ramlplugin.raml2code.raml.RamlSecurityScheme;

/**
 * Class containing utility methods for modifying Raml models
 * 
 * @author kurtpa
 * @since 0.5.3
 *
 */
public class RamlHelper {

	/**
	 * Gets the successful response from an action (200 or 201)
	 * 
	 * @param action
	 *            The action to parse
	 * @return The Successful response or null if not found
	 */
	public static RamlResponse getSuccessfulResponse(RamlAction action) {
		String[] successfulResponses = new String[] { "200", "201", "202" };
		for (String code : successfulResponses) {
			if (action != null && !CollectionUtils.isEmpty(action.getResponses()) && action.getResponses().containsKey(code)) {
				return action.getResponses().get(code);
			}
		}
		return null;
	}

	/**
	 * Returns authorization grant for provided action. It searches for
	 * authorization grants defined for provided action, some of parent
	 * resources or the root of the document. If authorization grants found is a
	 * list - the method will return the first grant in the list.
	 * 
	 * @param action
	 *            action to find grant for
	 * @param document
	 *            root raml document
	 * @return first grant found, null otherwise
	 */
	public static String getFirstAuthorizationGrant(RamlAction action, RamlRoot document) {
		List<String> grants = getAuthorizationGrants(action, document);
		if (grants.isEmpty()) {
			return null;
		}
		return grants.get(0);
	}

	private static List<String> getAuthorizationGrants(RamlAction action, RamlRoot document) {
		List<String> grants = new ArrayList<>();
		List<RamlSecurityReference> securityRefs = getSecurityRef(action, document);
		for (RamlSecurityReference securityRef : securityRefs) {
			List<String> authorizationGrants = securityRef.getAuthorizationGrants();
			for (String authorizationGrant : authorizationGrants) {
				grants.add(authorizationGrant);
			}
		}
		if (!grants.isEmpty()) {
			return grants;
		}

		return document.getSecuritySchemes().stream().map(RamlHelper::getAuthorizationGrants).flatMap(List::stream)
				.collect(Collectors.toList());
	}

	private static List<String> getAuthorizationGrants(RamlSecurityScheme securityScheme) {
		return securityScheme.getAuthorizationGrants();
	}

	private static List<RamlSecurityReference> getSecurityRef(RamlAction action, RamlRoot document) {

		List<RamlSecurityReference> securedBy = action.getSecuredBy();
		if (!securedBy.isEmpty()) {
			return securedBy;
		}

		RamlResource resource = action.getResource();
		while (resource != null && securedBy.isEmpty()) {
			securedBy = resource.getSecuredBy();
			resource = resource.getParentResource();
		}
		if (!securedBy.isEmpty()) {
			return securedBy;
		}

		return document.getSecuredBy();
	}

	/**
	 * Remove duplicates from provided list.
	 * 
	 * @param list
	 *            list with duplicates
	 * @return list without duplicates
	 */
	public static List<String> removeDuplicates(List<String> list) {
		return list.stream().distinct().collect(Collectors.toList());
	}
}
