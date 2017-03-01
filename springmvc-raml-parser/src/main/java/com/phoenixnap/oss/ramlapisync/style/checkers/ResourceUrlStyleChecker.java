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
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.commons.lang.CharUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.phoenixnap.oss.ramlapisync.naming.NamingHelper;
import com.phoenixnap.oss.ramlapisync.raml.RamlResource;
import com.phoenixnap.oss.ramlapisync.raml.RamlRoot;
import com.phoenixnap.oss.ramlapisync.style.RamlStyleCheckerAdapter;
import com.phoenixnap.oss.ramlapisync.style.StyleIssue;
import com.phoenixnap.oss.ramlapisync.verification.IssueLocation;

/**
 * Style checker that ensures that certain characters are not used in resource URLs
 * 
 * @author Kurt Paris
 * @since 0.0.2
 *
 */
public class ResourceUrlStyleChecker extends RamlStyleCheckerAdapter {
	/**
	 * Class Logger
	 */
	protected static final Logger logger = LoggerFactory.getLogger(ResourceUrlStyleChecker.class);
	
	private static String CHARS_NOT_ALLOWED_REGEX = "(-|:|%)+";
	private static Pattern CHARS_NOT_ALLOWED = Pattern.compile(CHARS_NOT_ALLOWED_REGEX); 
	
	public static String SPECIAL_CHARS_IN_URL =  "Special Characters in regex: " + CHARS_NOT_ALLOWED_REGEX + " not allowed in URL";
	public static String CAPITALISED_RESOURCE = "Resource URLs Should not be capitalized";
	
	@Override
	public Set<StyleIssue> checkResourceStyle(String name, RamlResource resource,
			IssueLocation location, RamlRoot raml) {
		logger.debug("Checking resource " + name);
		Set<StyleIssue> issues = new LinkedHashSet<>();
		
		if (!NamingHelper.isUriParamResource(name) && CHARS_NOT_ALLOWED.matcher(name).find()) {
			issues.add(new StyleIssue(location, SPECIAL_CHARS_IN_URL , resource, null));
		}
		
		if (CharUtils.isAsciiAlphaUpper(name.charAt(0))) {
			issues.add(new StyleIssue(location, CAPITALISED_RESOURCE, resource, null));
		}
		return issues;
	}
	
	

}
