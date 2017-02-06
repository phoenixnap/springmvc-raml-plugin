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

import com.phoenixnap.oss.ramlapisync.raml.RamlAction;
import com.phoenixnap.oss.ramlapisync.raml.RamlActionType;
import com.phoenixnap.oss.ramlapisync.raml.RamlRoot;
import com.phoenixnap.oss.ramlapisync.style.RamlStyleCheckerAdapter;
import com.phoenixnap.oss.ramlapisync.style.StyleIssue;
import com.phoenixnap.oss.ramlapisync.verification.IssueLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedHashSet;
import java.util.Set;


/**
 * Action style checker that enforces that 401 and 403 responses are defined when a security scheme is defined for an API
 * 
 * @author Kurt Paris
 * @since 0.0.2
 *
 */
public class ActionSecurityResponseChecker extends RamlStyleCheckerAdapter {
	
	/**
	 * Class Logger
	 */
	protected static final Logger logger = LoggerFactory.getLogger(ActionSecurityResponseChecker.class);
	
	public static String DESCRIPTION = "Secured Resources should define 401 and 403 responses";
	
	@Override
	public Set<StyleIssue> checkActionStyle(RamlActionType key, RamlAction value,
											IssueLocation location, RamlRoot raml) {
		logger.debug("Checking Action: " + key);
		Set<StyleIssue> issues = new LinkedHashSet<>();
		
		//check if we have a security scheme defined for this action
		if (value.getSecuredBy() != null 
				&& value.getSecuredBy().size() > 0 
				&& value.getSecuredBy().get(0).getName() != null
				&& (!value.getSecuredBy().get(0).getName().equals("null")
						|| value.getSecuredBy().size() > 1 )) {
			if (value.getResponses() == null
					|| !value.getResponses().containsKey("401")
					|| !value.getResponses().containsKey("403")) {
				issues.add(new StyleIssue(location, DESCRIPTION, value.getResource(), value));
			} 
		}
		
		return issues;
	}

	

}
