package com.phoenixnap.oss.ramlapisync.verification.checkers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.raml.model.Action;
import org.raml.model.ActionType;
import org.raml.model.Raml;
import org.raml.model.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.phoenixnap.oss.ramlapisync.naming.Pair;
import com.phoenixnap.oss.ramlapisync.verification.Issue;
import com.phoenixnap.oss.ramlapisync.verification.IssueLocation;
import com.phoenixnap.oss.ramlapisync.verification.IssueSeverity;
import com.phoenixnap.oss.ramlapisync.verification.IssueType;
import com.phoenixnap.oss.ramlapisync.verification.RamlActionVisitorCheck;
import com.phoenixnap.oss.ramlapisync.verification.RamlChecker;
import com.phoenixnap.oss.ramlapisync.verification.RamlResourceVisitorCheck;

/**
 * Raml checker that cross checks Resources between 2 RAML models. Only directly corresponding resources will be parsed 
 * 
 * @author Kurt Paris
 * @since 0.0.2
 *
 */
public class RamlCheckerResourceVisitorCoordinator implements RamlChecker {
	
	
	/**
	 * Class Logger
	 */
	protected static final Logger logger = LoggerFactory.getLogger(RamlCheckerResourceVisitorCoordinator.class);
	
	
	private Set<Issue> errors = new LinkedHashSet<>();
	private Set<Issue> warnings = new LinkedHashSet<>();
	
	private List<RamlActionVisitorCheck> actionCheckers;
	private List<RamlResourceVisitorCheck> resourceCheckers;
	
	public RamlCheckerResourceVisitorCoordinator (List<RamlActionVisitorCheck> actionCheckers, List<RamlResourceVisitorCheck> resourceCheckers) {		
		this.actionCheckers = new ArrayList<>();
		this.resourceCheckers = new ArrayList<>();
		this.actionCheckers.addAll(actionCheckers);
		this.resourceCheckers.addAll(resourceCheckers);	
	}

	@Override
	public Pair<Set<Issue>, Set<Issue>> check(Raml published, Raml implemented) {
		
		if (actionCheckers.size() == 0 && resourceCheckers.size() == 0) {
			return new Pair<Set<Issue>, Set<Issue>>(Collections.emptySet(), Collections.emptySet());
		}
		
		logger.info("Performing Resource and Action Visitor Checks");
		if (published != null && implemented == null) {
			errors.add(new Issue(IssueSeverity.ERROR, IssueLocation.CONTRACT, IssueType.MISSING, "Completely Missing Implementation for RAML file", "/"));
		} else if (published == null && implemented != null) {
			errors.add(new Issue(IssueSeverity.ERROR, IssueLocation.SOURCE, IssueType.MISSING, "Completely Missing RAML file", "/"));
		} else {
			logger.debug("Checking resources using contract as reference. Warnings only");
			// First Check for missing in implementation
			check(published.getResources(), implemented.getResources(), IssueLocation.SOURCE, IssueSeverity.ERROR);
			
			logger.debug("Checking resources using implementation as reference. Warnings only");
			// Now check for missing in contract
			check(implemented.getResources(), published.getResources(), IssueLocation.CONTRACT, IssueSeverity.WARNING);
		}
		
		return new Pair<Set<Issue>, Set<Issue>>(warnings, errors);
		
	}

	private void check(Map<String, Resource> referenceResourcesMap, Map<String, Resource> targetResourcesMap, IssueLocation location, IssueSeverity severity) {
		Set<String> referenceResources = referenceResourcesMap != null ? referenceResourcesMap.keySet() : Collections.<String>emptySet() ;
		Set<String> targetResources = targetResourcesMap != null ? targetResourcesMap.keySet() : Collections.<String>emptySet();
		
		
		for (String resource : referenceResources) {			
			if (targetResources.contains(resource)) {
				logger.debug("Expecting and found resource: "+ resource); 
				Resource reference = referenceResourcesMap.get(resource);
				Resource target = targetResourcesMap.get(resource);
				for (RamlResourceVisitorCheck resourceCheck : resourceCheckers) {
					Pair<Set<Issue>, Set<Issue>> check = resourceCheck.check(resource, reference, target, location, severity);
					if (check != null && check.getFirst() != null) {
						warnings.addAll(check.getFirst());
					}
					if (check != null && check.getSecond() != null) {
						errors.addAll(check.getSecond());
					}
				}
				
				Map<ActionType, Action> referenceActions = reference.getActions();
				Map<ActionType, Action> targetActions = target.getActions();
				if (referenceActions != null && referenceActions.size() > 0 && targetActions != null && targetActions.size() > 0) {
					for (Entry<ActionType, Action> action : referenceActions.entrySet()) {
						Action targetAction = targetActions.get(action.getKey());
						if (targetAction != null) {
							for (RamlActionVisitorCheck actionCheck : actionCheckers) {
								Pair<Set<Issue>, Set<Issue>> check = actionCheck.check(action.getKey(), action.getValue(), targetAction);
								if (check != null && check.getFirst() != null) {
									warnings.addAll(check.getFirst());
								}
								if (check != null && check.getSecond() != null) {
									errors.addAll(check.getSecond());
								}
							}
						}
					}
				}
				
				//Happy Days Exact Match - recurse to check children
				check(referenceResourcesMap.get(resource).getResources(), targetResourcesMap.get(resource).getResources(), location, severity);
			} 
		}
		
	}

}
