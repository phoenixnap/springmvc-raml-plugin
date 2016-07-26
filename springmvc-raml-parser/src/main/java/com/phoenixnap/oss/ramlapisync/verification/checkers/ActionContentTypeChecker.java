/*
 * Copyright 2002-2016 the original author or authors.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package com.phoenixnap.oss.ramlapisync.verification.checkers;


import com.phoenixnap.oss.ramlapisync.naming.Pair;
import com.phoenixnap.oss.ramlapisync.naming.RamlHelper;
import com.phoenixnap.oss.ramlapisync.parser.ResourceParser;
import com.phoenixnap.oss.ramlapisync.raml.RamlAction;
import com.phoenixnap.oss.ramlapisync.raml.RamlActionType;
import com.phoenixnap.oss.ramlapisync.raml.RamlResource;
import com.phoenixnap.oss.ramlapisync.raml.RamlResponse;
import com.phoenixnap.oss.ramlapisync.verification.Issue;
import com.phoenixnap.oss.ramlapisync.verification.IssueLocation;
import com.phoenixnap.oss.ramlapisync.verification.IssueSeverity;
import com.phoenixnap.oss.ramlapisync.verification.IssueType;
import com.phoenixnap.oss.ramlapisync.verification.RamlActionVisitorCheck;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedHashSet;
import java.util.Set;



/**
 * A checker that will check request and response media types.
 * 
 * @author Kurt Paris
 * @since 0.1.0
 *
 */
public class ActionContentTypeChecker
		implements RamlActionVisitorCheck {

	public static String REQUEST_BODY_MISSING = "Body required but not found in target";
	public static String RESPONSE_BODY_MISSING = "Response Body required but not found in target";
	/**
	 * Class Logger
	 */
	protected static final Logger logger = LoggerFactory.getLogger(ActionContentTypeChecker.class);



	@Override
	public Pair<Set<Issue>, Set<Issue>> check(RamlActionType name, RamlAction reference, RamlAction target, IssueLocation location, IssueSeverity maxSeverity) {
		logger.debug("Checking action " + name);
		Set<Issue> errors = new LinkedHashSet<>();
		Set<Issue> warnings = new LinkedHashSet<>();
		Issue issue;

		// Only apply this checker in the source
		if (location.equals(IssueLocation.CONTRACT)) {
			return new Pair<>(warnings, errors);
		}

		RamlResource referenceRamlResource = reference.getResource();

		// Request First
		// First lets check if we have defined a request media type.
		if (reference.getBody() != null && !reference.getBody().isEmpty()) {

			// lets check if we have a catch all on the implementation
			if (target.getBody() == null || target.getBody().isEmpty()) {
				issue = new Issue(maxSeverity, location, IssueType.MISSING, REQUEST_BODY_MISSING, referenceRamlResource, reference);
				RamlCheckerResourceVisitorCoordinator.addIssue(errors, warnings, issue, REQUEST_BODY_MISSING);
			}
			if (target.getBody() != null && target.getBody().containsKey(ResourceParser.CATCH_ALL_MEDIA_TYPE)) {
				// catch all will be able to handle any request.
			}
			else {
				for (String key : reference.getBody().keySet()) {
					if (!target.getBody().containsKey(key)) {
						issue = new Issue(maxSeverity, location, IssueType.MISSING, REQUEST_BODY_MISSING, referenceRamlResource, reference);
						RamlCheckerResourceVisitorCoordinator.addIssue(errors, warnings, issue, REQUEST_BODY_MISSING + " " + key);
					}
				}
			}
		}
		
		//Now the response
		RamlResponse response = RamlHelper.getSuccessfulResponse(reference);
		//successful response
		if (response != null && response.getBody() != null && !response.getBody().isEmpty()) {
			RamlResponse targetResponse = RamlHelper.getSuccessfulResponse(target);
			
			if (targetResponse == null) {
				issue = new Issue(maxSeverity, location, IssueType.MISSING, RESPONSE_BODY_MISSING, referenceRamlResource, reference);
				RamlCheckerResourceVisitorCoordinator.addIssue(errors, warnings, issue, RESPONSE_BODY_MISSING);
			} else {
				// successful response
				boolean found = false;
				for (String key : response.getBody().keySet()) {
					logger.debug("Processing key {}.", key);
					if (targetResponse.getBody().containsKey(key)) {
						found = true;
						break;
					}
					if (!found && !targetResponse.getBody().containsKey(ResourceParser.CATCH_ALL_MEDIA_TYPE)) {
						issue = new Issue(maxSeverity, location, IssueType.MISSING, RESPONSE_BODY_MISSING, referenceRamlResource, reference);
						RamlCheckerResourceVisitorCoordinator.addIssue(errors, warnings, issue, RESPONSE_BODY_MISSING + " " + response.getBody().values());
					}
				}
			}

		}

		return new Pair<>(warnings, errors);
	}


}
