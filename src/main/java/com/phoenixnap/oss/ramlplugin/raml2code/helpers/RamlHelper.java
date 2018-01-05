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

import org.springframework.util.CollectionUtils;

import com.phoenixnap.oss.ramlplugin.raml2code.raml.RamlAction;
import com.phoenixnap.oss.ramlplugin.raml2code.raml.RamlResponse;

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
}
