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

import com.phoenixnap.oss.ramlapisync.naming.NamingHelper;
import com.phoenixnap.oss.ramlapisync.naming.Pair;
import com.phoenixnap.oss.ramlapisync.raml.RamlResource;
import com.phoenixnap.oss.ramlapisync.raml.RamlRoot;
import com.phoenixnap.oss.ramlapisync.verification.Issue;
import com.phoenixnap.oss.ramlapisync.verification.IssueLocation;
import com.phoenixnap.oss.ramlapisync.verification.IssueSeverity;
import com.phoenixnap.oss.ramlapisync.verification.IssueType;
import com.phoenixnap.oss.ramlapisync.verification.RamlChecker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * Raml checker that cross checks the existence of Resources 
 * 
 * @author Kurt Paris
 * @since 0.0.2
 *
 */
public class ResourceExistenceChecker implements RamlChecker {
	
	public static String RESOURCE_MISSING = "Missing Resource.";
	public static String CONTRACT_MISSING = "Completely Missing RAML file";
	public static String IMPLEMENTATION_MISSING = "Completely Missing Implementation for RAML file";
	public static String RESOURCE_WITH_DISCREPANCY = "Resource declared with case, spacing or special character differences.";
	
	/**
	 * Class Logger
	 */
	protected static final Logger logger = LoggerFactory.getLogger(ResourceExistenceChecker.class);
	
	
	private Set<Issue> errors = new LinkedHashSet<>();
	private Set<Issue> warnings = new LinkedHashSet<>();

	@Override
	public Pair<Set<Issue>, Set<Issue>> check(RamlRoot published, RamlRoot implemented) {
		logger.info("Performing Resource Existence Checks");
		if (published != null && implemented == null) {
			errors.add(new Issue(IssueSeverity.ERROR, IssueLocation.CONTRACT, IssueType.MISSING, IMPLEMENTATION_MISSING, "/"));
		} else if (published == null && implemented != null) {
			errors.add(new Issue(IssueSeverity.ERROR, IssueLocation.SOURCE, IssueType.MISSING, CONTRACT_MISSING, "/"));
		} else {
			logger.debug("Checking for any missing resources in implementation");
			// First Check for missing in implementation
			check(published.getResources(), implemented.getResources(), IssueLocation.SOURCE, IssueSeverity.ERROR, false);
			
			logger.debug("Checking for any missing resources in RAML");
			// Now check for missing in contract
			check(implemented.getResources(), published.getResources(), IssueLocation.CONTRACT, IssueSeverity.WARNING, false);
		}
		
		return new Pair<Set<Issue>, Set<Issue>>(warnings, errors);
		
	}

	private void check(Map<String, RamlResource> referenceResourcesMap, Map<String, RamlResource> targetResourcesMap, IssueLocation location, IssueSeverity severity, boolean exact) {
		Set<String> referenceResources = referenceResourcesMap != null ? referenceResourcesMap.keySet() : Collections.<String>emptySet() ;
		Set<String> targetResources = targetResourcesMap != null ? targetResourcesMap.keySet() : Collections.<String>emptySet();
		
		Map<String, RamlResource> implementedCleanedResources = clean(targetResourcesMap);
		
		for (String resource : referenceResources) {			
			if (targetResources.contains(resource)) {
				logger.debug("Expecting and found resource: "+ resource);
				//Happy Days Exact Match - recurse to check children
				check(referenceResourcesMap.get(resource).getResources(), targetResourcesMap.get(resource).getResources(), location, severity, exact);
			} else {
				String cleanedResource = clean(resource);
				if (implementedCleanedResources.keySet().contains(cleanedResource) && !exact){			
					logger.debug("Expecting resource " + resource + " and found with minor differences: "+ cleanedResource);
					//Discrepancies in spacing or special chars - issue warning;
					warnings.add(new Issue(IssueSeverity.WARNING, location, IssueType.MISSING, RESOURCE_WITH_DISCREPANCY , referenceResourcesMap.get(resource), null));
					check(referenceResourcesMap.get(resource).getResources(), implementedCleanedResources.get(cleanedResource).getResources(), location, severity, exact);
				} else {
					boolean foundAlternateUriParam = false;
					if (NamingHelper.isUriParamResource(resource)) {
						for (String potentialUriParam : targetResources) {
							if (NamingHelper.isUriParamResource(potentialUriParam)) {
								foundAlternateUriParam = true;
							}
						}
					}
					
					//Resource (and all children) missing - Log it
					Issue issue = new Issue(foundAlternateUriParam ? IssueSeverity.WARNING : severity, location, IssueType.MISSING, RESOURCE_MISSING , referenceResourcesMap.get(resource), null);
					RamlCheckerResourceVisitorCoordinator.addIssue(errors, warnings, issue, "Expected resource missing: "+ resource + " in " + location.name());
				}
			}
		}
		
	}

	

	/**
	 * Cleans a set of strings removing special characters and reducing the string to lower case
	 * 
	 * @param publishedResources
	 * @return
	 */
	private Map<String, RamlResource> clean(Map<String, RamlResource> publishedResources) {
		Map<String, RamlResource> cleanedSet = new HashMap<>();
		if (publishedResources == null) {
			return cleanedSet;
		}
		for (Entry<String, RamlResource> resource : publishedResources.entrySet()) {
			cleanedSet.put(clean(resource.getKey()), resource.getValue());
		}
		return cleanedSet;
	}

	private String clean(String resource) {
		if (resource == null) {
			return null;
		}
		return NamingHelper.cleanLeadingAndTrailingNewLineAndChars(resource.toLowerCase());
	}

}
