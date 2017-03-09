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
package com.phoenixnap.oss.ramlapisync.style;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.phoenixnap.oss.ramlapisync.naming.Pair;
import com.phoenixnap.oss.ramlapisync.raml.RamlAction;
import com.phoenixnap.oss.ramlapisync.raml.RamlActionType;
import com.phoenixnap.oss.ramlapisync.raml.RamlQueryParameter;
import com.phoenixnap.oss.ramlapisync.raml.RamlResource;
import com.phoenixnap.oss.ramlapisync.raml.RamlRoot;
import com.phoenixnap.oss.ramlapisync.raml.RamlUriParameter;
import com.phoenixnap.oss.ramlapisync.verification.Issue;
import com.phoenixnap.oss.ramlapisync.verification.IssueLocation;
import com.phoenixnap.oss.ramlapisync.verification.IssueSeverity;
import com.phoenixnap.oss.ramlapisync.verification.IssueType;
import com.phoenixnap.oss.ramlapisync.verification.RamlChecker;

/**
 * Provides a Vistor pattern approach to Style Checks. Iterates through the model and invokes callbacks on specific checkers.
 * 
 * @author Kurt Paris
 * @since 0.0.2
 *
 */
public class RamlStyleCheckVisitorCoordinator implements RamlChecker {
	
	/**
	 * Boolean flag to enable style checking of code too. Since RAML and code should be in transformToUnmodifiableMap this could be kept off to improve performance
	 */
	private boolean ignoreCodeStyle = true;
	
	private Set<Issue> warnings = new LinkedHashSet<>();
	
	private List<RamlStyleChecker> checkers;
	
	public RamlStyleCheckVisitorCoordinator (List<RamlStyleChecker> styleChecks) {		
		checkers = new ArrayList<>();
		checkers.addAll(styleChecks);	
	}
	
	/**
	 * Performs a specific check across two Raml Models. 
	 * 
	 * @param published The Raml as published in the contract
	 * @param implemented The Raml as generated from the implementation
	 * @return A pair containing a list of Warnings and an empty list of Errors (as first and second respectively)
	 */
	public Pair<Set<Issue>, Set<Issue>> check (RamlRoot published, RamlRoot implemented) {
		
		checkChildren(published.getResources(), published, IssueLocation.CONTRACT);
		if (!ignoreCodeStyle && implemented != null) {
			checkChildren(implemented.getResources(), implemented, IssueLocation.SOURCE);
		}
		
		return new Pair<>(warnings, Collections.emptySet());
	}



	private void checkChildren(Map<String, RamlResource> resources, RamlRoot raml, IssueLocation location) {
		if (resources != null) {
			for (Entry<String, RamlResource> entry : resources.entrySet()) {
				RamlResource resource = entry.getValue();
				for (RamlStyleChecker checker : checkers) {
					warnings.addAll(checker.checkResourceStyle(entry.getKey(), resource, location, raml));
				}
				
				Map<String, RamlUriParameter> uriParameters = resource.getUriParameters();
				if(uriParameters != null) {
					for (Entry<String, RamlUriParameter> uriParameter : uriParameters.entrySet()) {
						for (RamlStyleChecker checker : checkers) {
							warnings.addAll(checker.checkParameterStyle(uriParameter.getKey(), uriParameter.getValue()));
						}
					}
				}
				
				Map<RamlActionType, RamlAction> actions = resource.getActions();
				if (actions != null) {
					for (Entry<RamlActionType, RamlAction> actionEntry : actions.entrySet()) {
						for (RamlStyleChecker checker : checkers) {
							warnings.addAll(checker.checkActionStyle(actionEntry.getKey(), actionEntry.getValue(), location, raml));
						}
						
						/*
						 * If we have query parameters in this call check it 
						 */
						Map<String, RamlQueryParameter> queryParameters = actionEntry.getValue().getQueryParameters();
						if(queryParameters != null) {
							for (Entry<String, RamlQueryParameter> queryParam : queryParameters.entrySet()) {
								for (RamlStyleChecker checker : checkers) {
									warnings.addAll(checker.checkParameterStyle(queryParam.getKey(), queryParam.getValue()));
								}
							}
						}
						
						
					}
				}
				checkChildren(resource.getResources(), raml, location);
			}
		}
	}
	
	
	
	protected final void addIssue(IssueLocation location, String description, String ramlLocation) {
		warnings.add(new Issue(IssueSeverity.WARNING, location, IssueType.STYLE, description, ramlLocation));
	}

}
