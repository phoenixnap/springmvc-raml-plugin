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
package com.phoenixnap.oss.ramlapisync.style.checkers;

import java.util.LinkedHashSet;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Pattern;

import org.raml.parser.utils.Inflector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.phoenixnap.oss.ramlapisync.naming.NamingHelper;
import com.phoenixnap.oss.ramlapisync.raml.RamlActionType;
import com.phoenixnap.oss.ramlapisync.raml.RamlResource;
import com.phoenixnap.oss.ramlapisync.raml.RamlRoot;
import com.phoenixnap.oss.ramlapisync.style.RamlStyleCheckerAdapter;
import com.phoenixnap.oss.ramlapisync.style.StyleIssue;
import com.phoenixnap.oss.ramlapisync.verification.IssueLocation;

/**
 * Style checker that ensures that collection resources are defined in the plural form
 * 
 * @author Kurt Paris
 * @since 0.0.2
 *
 */
public class ResourceCollectionPluralisationChecker extends RamlStyleCheckerAdapter {
	
	/**
	 * Class Logger
	 */
	protected static final Logger logger = LoggerFactory.getLogger(ResourceCollectionPluralisationChecker.class);
	
	public static String DESCRIPTION = "Collections of Resources should be Pluralised in the URL";

	public static String ID_RESOURCE_REGEX = "[/]{0,1}\\{([^\\}]*)\\}";
	private static Pattern ID_RESOURCE_PATTERN = Pattern.compile(ID_RESOURCE_REGEX); 
	
	@Override
	public Set<StyleIssue> checkResourceStyle(String name, RamlResource resource,
			IssueLocation location, RamlRoot raml) {
		logger.debug("Checking resource " + name);
		Set<StyleIssue> issues = new LinkedHashSet<>();
		
		//dont apply if we are an id resource ourselves
		if (ID_RESOURCE_PATTERN.matcher(name).find() || "/".equals(name)) {
			return issues;
		}
		//Lets check if this is a plural collection
		//if should have at least one subresource with an ID as a URI param.
		boolean hasIdSubresource = false;
		boolean hasVerb = false;
		for (Entry<String, RamlResource> subResourceEntry : resource.getResources().entrySet()) {
			if (ID_RESOURCE_PATTERN.matcher(subResourceEntry.getKey()).find()) {
				hasIdSubresource = true;
				
			}
			RamlResource subResource = subResourceEntry.getValue();
			//it should have a get or a post request on it.			
			if (subResource != null
					&& (subResource.getAction(RamlActionType.POST) != null
						|| subResource.getAction(RamlActionType.GET) != null)) {
				hasVerb = true;
			}
			if (hasIdSubresource && hasVerb) {
				logger.debug("Collection Resource identified: " + name);
				if (NamingHelper.singularize(name).equals(name) && !Inflector.pluralize(name).equals(name)) {
					issues.add(new StyleIssue(location, DESCRIPTION , resource, null));
				}
				break;
			}
		}
		
		
		
		
		return issues;
	}
	

}
