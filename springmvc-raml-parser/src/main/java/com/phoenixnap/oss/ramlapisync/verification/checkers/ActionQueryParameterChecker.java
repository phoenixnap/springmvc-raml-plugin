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
package com.phoenixnap.oss.ramlapisync.verification.checkers;

import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;

import com.phoenixnap.oss.ramlapisync.naming.Pair;
import com.phoenixnap.oss.ramlapisync.parser.ResourceParser;
import com.phoenixnap.oss.ramlapisync.raml.RamlAction;
import com.phoenixnap.oss.ramlapisync.raml.RamlActionType;
import com.phoenixnap.oss.ramlapisync.raml.RamlMimeType;
import com.phoenixnap.oss.ramlapisync.raml.RamlQueryParameter;
import com.phoenixnap.oss.ramlapisync.raml.RamlResource;
import com.phoenixnap.oss.ramlapisync.verification.Issue;
import com.phoenixnap.oss.ramlapisync.verification.IssueLocation;
import com.phoenixnap.oss.ramlapisync.verification.IssueSeverity;
import com.phoenixnap.oss.ramlapisync.verification.IssueType;
import com.phoenixnap.oss.ramlapisync.verification.RamlActionVisitorCheck;

/**
 * A visitor that will be invoked when an action is identified
 * 
 * @author Kurt Paris
 * @since 0.0.2
 *
 */
public class ActionQueryParameterChecker implements RamlActionVisitorCheck {
	
	public static String QUERY_PARAMETER_MISSING = "Missing Query Parameter.";
	public static String QUERY_PARAMETER_FOUND_IN_FORM = "Missing Query Parameter but found in Form Parameters";
	public static String INCOMPATIBLE_TYPES = "Incompatible data types";
	public static String INCOMPATIBLE_VALIDATION = "Incompatible validation parameters";
	public static String REQUIRED_PARAM_HIDDEN = "Target requires parameter that is marked not required in reference.";

	/**
	 * Class Logger
	 */
	protected static final Logger logger = LoggerFactory.getLogger(ActionQueryParameterChecker.class);

	@Override
	public Pair<Set<Issue>, Set<Issue>> check(RamlActionType name, RamlAction reference, RamlAction target, IssueLocation location, IssueSeverity maxSeverity) {
		logger.debug("Checking Action " + name);
		Set<Issue> errors = new LinkedHashSet<>();
		Set<Issue> warnings = new LinkedHashSet<>();

		RamlResource referenceRamlResource =reference.getResource();

		//Resource (and all children) missing - Log it
		Issue issue;
		if (reference.getQueryParameters() != null && !reference.getQueryParameters().isEmpty()) {
			for(Entry<String, RamlQueryParameter> cParam : reference.getQueryParameters().entrySet()) {
				logger.debug("ActionQueryParameterChecker Checking param " + cParam.getKey());
				IssueSeverity targetSeverity = maxSeverity;
				if (target.getQueryParameters() == null 
						|| target.getQueryParameters().isEmpty()
						|| !target.getQueryParameters().containsKey(cParam.getKey())) {
					//we have a missing param, in case of required parameters this could break - upgrade severity
					
					if (!cParam.getValue().isRequired()) {
						targetSeverity = IssueSeverity.WARNING; //downgrade to warning for non required parameters
					} else {
						targetSeverity = IssueSeverity.ERROR;
					}
					
					//lets check if they are defined as form parameters since spring does not distinguish this. Do so only if we are checking the contract
					Map<String, RamlMimeType> targetBody = target.getBody();
					if (location == IssueLocation.SOURCE 
							&& targetBody != null 
							&& targetBody.containsKey(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
							&& targetBody.get(MediaType.APPLICATION_FORM_URLENCODED_VALUE) != null
							&& targetBody.get(MediaType.APPLICATION_FORM_URLENCODED_VALUE).getFormParameters() != null
							&& targetBody.get(MediaType.APPLICATION_FORM_URLENCODED_VALUE).getFormParameters().containsKey(cParam.getKey())
							&& ResourceParser.doesActionTypeSupportRequestBody(reference.getType())) {

					   issue = new Issue(IssueSeverity.WARNING, location, IssueType.MISSING, QUERY_PARAMETER_FOUND_IN_FORM , referenceRamlResource, reference, cParam.getKey());
					} else {
					   issue = new Issue(targetSeverity, location, IssueType.MISSING, QUERY_PARAMETER_MISSING , referenceRamlResource, reference, cParam.getKey());
					}
					RamlCheckerResourceVisitorCoordinator.addIssue(errors, warnings, issue, issue.getDescription() + "  "+ cParam.getKey() + " in " + location.name());
				} else {
					RamlQueryParameter referenceParameter = cParam.getValue();
					RamlQueryParameter targetParameter = target.getQueryParameters().get(cParam.getKey());
					
					if (referenceParameter.isRequired() == false && targetParameter.isRequired()) {
						issue = new Issue(maxSeverity, location, IssueType.DIFFERENT, REQUIRED_PARAM_HIDDEN , referenceRamlResource, reference, cParam.getKey());
						RamlCheckerResourceVisitorCoordinator.addIssue(errors, warnings, issue, REQUIRED_PARAM_HIDDEN + " "+ cParam.getKey() + " in " + location.name());
					}
					
					if (referenceParameter.getType() != null && !referenceParameter.getType().equals(targetParameter.getType())) {
						issue = new Issue(IssueSeverity.WARNING, location, IssueType.DIFFERENT, INCOMPATIBLE_TYPES , referenceRamlResource, reference, cParam.getKey());
						RamlCheckerResourceVisitorCoordinator.addIssue(errors, warnings, issue, INCOMPATIBLE_TYPES + " "+ cParam.getKey() + " in " + location.name());
					}
					
					if ( (referenceParameter.getMinLength() != null && !referenceParameter.getMinLength().equals(targetParameter.getMinLength()))
							|| (referenceParameter.getMaxLength() != null && !referenceParameter.getMaxLength().equals(targetParameter.getMaxLength()))
							|| (referenceParameter.getMaximum() != null && !referenceParameter.getMaximum().equals(targetParameter.getMaximum()))
							|| (referenceParameter.getMinimum() != null && !referenceParameter.getMinimum().equals(targetParameter.getMinimum()))
							|| (referenceParameter.getPattern() != null && !referenceParameter.getPattern().equals(targetParameter.getPattern()))) {
						issue = new Issue(IssueSeverity.WARNING, location, IssueType.DIFFERENT, INCOMPATIBLE_VALIDATION , referenceRamlResource, reference, cParam.getKey());
						RamlCheckerResourceVisitorCoordinator.addIssue(errors, warnings, issue, INCOMPATIBLE_VALIDATION + " "+ cParam.getKey() + " in " + location.name());
					}
					
				}								
			}
		}
		return new Pair<>(warnings, errors);
	}
	
	
}
