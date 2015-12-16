package com.phoenixnap.oss.ramlapisync.verification.checkers;

import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.raml.model.Action;
import org.raml.model.ActionType;
import org.raml.model.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.phoenixnap.oss.ramlapisync.naming.Pair;
import com.phoenixnap.oss.ramlapisync.verification.Issue;
import com.phoenixnap.oss.ramlapisync.verification.IssueLocation;
import com.phoenixnap.oss.ramlapisync.verification.IssueSeverity;
import com.phoenixnap.oss.ramlapisync.verification.IssueType;
import com.phoenixnap.oss.ramlapisync.verification.RamlResourceVisitorCheck;

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
	public Pair<Set<Issue>, Set<Issue>> check(String name, Resource reference,
			Resource target, IssueLocation location, IssueSeverity maxSeverity) {
		Set<Issue> errors = new LinkedHashSet<>();
		Set<Issue> warnings = new LinkedHashSet<>();
		
		Map<ActionType, Action> referenceActions = reference.getActions();
		Map<ActionType, Action> targetActions = target.getActions();
		
		if (referenceActions != null && referenceActions.size() > 0) {
			for (Entry<ActionType, Action> action : referenceActions.entrySet()) {
				Action targetAction = targetActions.get(action.getKey());
				if (targetAction == null) {
					//Resource (and all children) missing - Log it
					Issue issue = new Issue(maxSeverity, location, IssueType.MISSING, ACTION_MISSING , reference, action.getValue());
					if (maxSeverity.equals(IssueSeverity.ERROR)) {
						logger.error("Expected action missing: "+ action.getKey() + " in " + location.name());
						errors.add(issue);
					} else {
						logger.warn("Expected action missing: "+ action.getKey() + " in " + location.name());
						warnings.add(issue);
					}
				}
			}
		}
		
		
		
		return new Pair<>(warnings, errors);
	}

	
}
