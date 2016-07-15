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
package com.phoenixnap.oss.ramlapisync.verification.checkers;

import com.phoenixnap.oss.ramlapisync.naming.Pair;
import com.phoenixnap.oss.ramlapisync.raml.RamlAction;
import com.phoenixnap.oss.ramlapisync.raml.RamlActionType;
import com.phoenixnap.oss.ramlapisync.raml.RamlResource;
import com.phoenixnap.oss.ramlapisync.verification.Issue;
import com.phoenixnap.oss.ramlapisync.verification.IssueLocation;
import com.phoenixnap.oss.ramlapisync.verification.IssueSeverity;
import com.phoenixnap.oss.ramlapisync.verification.IssueType;
import com.phoenixnap.oss.ramlapisync.verification.RamlResourceVisitorCheck;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * A visitor that will be invoked when an action is identified
 * 
 * @author Kurt Paris
 * @since 0.0.2
 *
 */
public class ActionExistenceChecker implements RamlResourceVisitorCheck {
	
	public static String ACTION_MISSING = "Missing Action.";

	/**
	 * Class Logger
	 */
	protected static final Logger logger = LoggerFactory.getLogger(ActionExistenceChecker.class);
	
	@Override
	public Pair<Set<Issue>, Set<Issue>> check(String name, RamlResource reference,
			RamlResource target, IssueLocation location, IssueSeverity maxSeverity) {
		logger.debug("Checking Action " + name);
		Set<Issue> errors = new LinkedHashSet<>();
		Set<Issue> warnings = new LinkedHashSet<>();
		
		Map<RamlActionType, RamlAction> referenceActions = reference.getActions();
		Map<RamlActionType, RamlAction> targetActions = target.getActions();
		
		if (referenceActions != null && referenceActions.size() > 0) {
			for (Entry<RamlActionType, RamlAction> action : referenceActions.entrySet()) {
				RamlAction targetAction = targetActions.get(action.getKey());
				if (targetAction == null) {
					//Resource (and all children) missing - Log it
					Issue issue = new Issue(maxSeverity, location, IssueType.MISSING, ACTION_MISSING , reference, action.getValue());
					RamlCheckerResourceVisitorCoordinator.addIssue(errors, warnings, issue, "Expected action missing: "+ action.getKey() + " in " + location.name());
				}
			}
		}
		
		
		
		return new Pair<>(warnings, errors);
	}

	
}
