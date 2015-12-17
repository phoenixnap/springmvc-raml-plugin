package com.phoenixnap.oss.ramlapisync.verification.checkers;

import java.util.LinkedHashSet;
import java.util.Map.Entry;
import java.util.Set;

import org.raml.model.Action;
import org.raml.model.ActionType;
import org.raml.model.parameter.QueryParameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.phoenixnap.oss.ramlapisync.naming.Pair;
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
	public static String INCOMPATIBLE_TYPES = "Incompatible data types";
	public static String INCOMPATIBLE_VALIDATION = "Incompatible validation parameters";
	public static String REQUIRED_PARAM_HIDDEN = "Target requires parameter that is marked not required in reference.";

	/**
	 * Class Logger
	 */
	protected static final Logger logger = LoggerFactory.getLogger(ActionQueryParameterChecker.class);

	@Override
	public Pair<Set<Issue>, Set<Issue>> check(ActionType name, Action reference, Action target, IssueLocation location, IssueSeverity maxSeverity) {
		Set<Issue> errors = new LinkedHashSet<>();
		Set<Issue> warnings = new LinkedHashSet<>();
		//Resource (and all children) missing - Log it
		Issue issue;
		if (reference.getQueryParameters() != null && !reference.getQueryParameters().isEmpty()) {
			for(Entry<String, QueryParameter> cParam : reference.getQueryParameters().entrySet()) {
				IssueSeverity targetSeverity = maxSeverity;
				if (target.getQueryParameters() == null 
						|| reference.getQueryParameters().isEmpty()
						|| !reference.getQueryParameters().containsKey(cParam.getKey())) {
					if (!cParam.getValue().isRequired()) {
						targetSeverity = IssueSeverity.WARNING; //downgrade to warning for non required parameters
					}
					issue = new Issue(targetSeverity, location, IssueType.MISSING, QUERY_PARAMETER_MISSING , reference.getResource(), reference, cParam.getKey());					
					addIssue(errors, warnings, issue, "Expected query parameter missing: "+ cParam.getKey() + " in " + location.name());
				} else {
					QueryParameter referenceParameter = cParam.getValue();
					QueryParameter targetParameter = target.getQueryParameters().get(cParam.getKey());
					
					if (referenceParameter.isRequired() == false && targetParameter.isRequired()) {
						issue = new Issue(maxSeverity, location, IssueType.DIFFERENT, REQUIRED_PARAM_HIDDEN , reference.getResource(), reference, cParam.getKey());					
						addIssue(errors, warnings, issue, REQUIRED_PARAM_HIDDEN + " "+ cParam.getKey() + " in " + location.name());
					}
					
					if (referenceParameter.getType() != null && !referenceParameter.getType().equals(targetParameter.getType())) {
						issue = new Issue(IssueSeverity.WARNING, location, IssueType.DIFFERENT, INCOMPATIBLE_TYPES , reference.getResource(), reference, cParam.getKey());					
						addIssue(errors, warnings, issue, INCOMPATIBLE_TYPES + " "+ cParam.getKey() + " in " + location.name());
					}
					
					if ( (referenceParameter.getMinLength() != null && !referenceParameter.getMinLength().equals(targetParameter.getMinLength()))
							|| (referenceParameter.getMaxLength() != null && !referenceParameter.getMaxLength().equals(targetParameter.getMaxLength()))
							|| (referenceParameter.getMaximum() != null && !referenceParameter.getMaximum().equals(targetParameter.getMaximum()))
							|| (referenceParameter.getMinimum() != null && !referenceParameter.getMinimum().equals(targetParameter.getMinimum()))
							|| (referenceParameter.getPattern() != null && !referenceParameter.getPattern().equals(targetParameter.getPattern()))) {
						issue = new Issue(IssueSeverity.WARNING, location, IssueType.DIFFERENT, INCOMPATIBLE_VALIDATION , reference.getResource(), reference, cParam.getKey());					
						addIssue(errors, warnings, issue, INCOMPATIBLE_VALIDATION + " "+ cParam.getKey() + " in " + location.name());
					}
					
				}								
			}
		}
		return new Pair<>(warnings, errors);
	}
	

	private void addIssue(Set<Issue> errors, Set<Issue> warnings, Issue issue, String logDescription) {
		if (issue.getSeverity().equals(IssueSeverity.ERROR)) {
			logger.error(logDescription);
			errors.add(issue);
		} else {
			logger.warn(logDescription);
			warnings.add(issue);
		}
	}
	
}
