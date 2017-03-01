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
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.util.CollectionUtils;
import org.springframework.util.MultiValueMap;

import com.phoenixnap.oss.ramlapisync.raml.RamlAction;
import com.phoenixnap.oss.ramlapisync.raml.RamlActionType;
import com.phoenixnap.oss.ramlapisync.raml.RamlRoot;
import com.phoenixnap.oss.ramlapisync.style.RamlStyleCheckerAdapter;
import com.phoenixnap.oss.ramlapisync.style.StyleIssue;
import com.phoenixnap.oss.ramlapisync.verification.IssueLocation;

/**
 * 
 * Style checker that will check for existence of valid schemas in request bodies
 * 
 * @author kurtpa
 * @since 0.5.2
 *
 */
public class ResponseCodeDefinitionStyleChecker extends RamlStyleCheckerAdapter {
	
	/**
	 * Class Logger
	 */
	protected static final Logger logger = LoggerFactory.getLogger(ResponseCodeDefinitionStyleChecker.class);
	
	public static String DESCRIPTION = "%s Verb should define %s (%d) response";

	private MultiValueMap<String, HttpStatus> statusChecks;

	public ResponseCodeDefinitionStyleChecker(MultiValueMap<String, HttpStatus> statusChecks) {
		this.statusChecks = statusChecks;
	}

	
	@Override
	public Set<StyleIssue> checkActionStyle(RamlActionType key, RamlAction value,
											IssueLocation location, RamlRoot raml) {
		logger.debug("Checking Action: " + key);
		Set<StyleIssue> issues = new LinkedHashSet<>();
		
		//Do we have a check for this verb?
		if (statusChecks.containsKey(key.name())) {			
			List<HttpStatus> statuses = statusChecks.get(key.name());
			if (!CollectionUtils.isEmpty(statuses)) {
				for (HttpStatus check : statuses) {
					if (value.getResponses() == null
							|| !value.getResponses().containsKey(String.valueOf(check.value()))) {
						issues.add(new StyleIssue(location, String.format(DESCRIPTION, key, check.name(), check.value()), value.getResource(), value));
					} 
				}
				
			}
		}
		
		return issues;
	}

	
	
		
}
