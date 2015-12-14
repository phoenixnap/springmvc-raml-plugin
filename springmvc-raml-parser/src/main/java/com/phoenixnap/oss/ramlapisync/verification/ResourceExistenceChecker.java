package com.phoenixnap.oss.ramlapisync.verification;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.raml.model.Raml;
import org.raml.model.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.phoenixnap.oss.ramlapisync.naming.NamingHelper;
import com.phoenixnap.oss.ramlapisync.naming.Pair;

/**
 * Raml checker that cross checks the existence of Resources 
 * 
 * @author Kurt Paris
 * @since 0.0.2
 *
 */
public class ResourceExistenceChecker implements RamlChecker {
	
	
	/**
	 * Class Logger
	 */
	protected static final Logger logger = LoggerFactory.getLogger(ResourceExistenceChecker.class);
	
	
	private List<Issue> errors = new ArrayList<>();
	private List<Issue> warnings = new ArrayList<>();

	@Override
	public Pair<List<Issue>, List<Issue>> check(Raml published, Raml implemented) {
		logger.info("Performing Resource Existence Checks");
		if (published != null && implemented == null) {
			errors.add(new Issue(IssueSeverity.ERROR, IssueLocation.CONTRACT, IssueType.MISSING, "Completely Missing Implementation for RAML file", "/"));
		} else if (published == null && implemented != null) {
			errors.add(new Issue(IssueSeverity.ERROR, IssueLocation.SOURCE, IssueType.MISSING, "Completely Missing RAML file", "/"));
		} else {
			logger.debug("Checking for any missing resources in implementation");
			// First Check for missing in implementation
			check(published.getResources(), implemented.getResources(), IssueLocation.SOURCE, IssueSeverity.ERROR, false);
			
			logger.debug("Checking for any missing resources in RAML");
			// Now check for missing in contract
			check(implemented.getResources(), published.getResources(), IssueLocation.CONTRACT, IssueSeverity.WARNING, false);
		}
		
		return new Pair<List<Issue>, List<Issue>>(warnings, errors);
		
	}

	private void check(Map<String, Resource> referenceResourcesMap, Map<String, Resource> targetResourcesMap, IssueLocation location, IssueSeverity severity, boolean exact) {
		Set<String> publishedResources = referenceResourcesMap != null ? referenceResourcesMap.keySet() : Collections.<String>emptySet() ;
		Set<String> implementedResources = targetResourcesMap != null ? targetResourcesMap.keySet() : Collections.<String>emptySet();
		
		Map<String, Resource> implementedCleanedResources = clean(targetResourcesMap);
		
		for (String resource : publishedResources) {			
			if (implementedResources.contains(resource)) {
				logger.debug("Expecting and found resource: "+ resource);
				//Happy Days Exact Match - recurse to check children
				check(referenceResourcesMap.get(resource).getResources(), targetResourcesMap.get(resource).getResources(), location, severity, exact);
			} else {
				String cleanedResource = clean(resource);
				if (implementedCleanedResources.keySet().contains(cleanedResource) && !exact){			
					logger.debug("Expecting resource " + resource + " and found with minor differences: "+ cleanedResource);
					//Discrepancies in spacing or special chars - issue warning;
					warnings.add(new Issue(IssueSeverity.WARNING, location, IssueType.MISSING, "Resource declared with case, spacing or special character differences" , referenceResourcesMap.get(resource), null));
					check(referenceResourcesMap.get(resource).getResources(), implementedCleanedResources.get(cleanedResource).getResources(), location, severity, exact);
				} else {
					boolean foundAlternateUriParam = false;
					if (NamingHelper.isUriParamResource(resource)) {
						for (String potentialUriParam : implementedResources) {
							if (NamingHelper.isUriParamResource(potentialUriParam)) {
								foundAlternateUriParam = true;
							}
						}
					}
					
					//Resource (and all children) missing - Log it
					Issue issue = new Issue(severity, location, IssueType.MISSING, "Missing Resource." , referenceResourcesMap.get(resource), null);
					if (severity.equals(IssueSeverity.ERROR) && !foundAlternateUriParam) {
						logger.error("Expected resource missing: "+ resource);
						errors.add(issue);
					} else {
						logger.warn("Expected resource missing: "+ resource);
						warnings.add(issue);
					}
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
	private Map<String, Resource> clean(Map<String, Resource> publishedResources) {		
		Map<String, Resource> cleanedSet = new HashMap<String, Resource>();		
		if (publishedResources == null) {
			return cleanedSet;
		}
		for (Entry<String, Resource> resource : publishedResources.entrySet()) {
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
