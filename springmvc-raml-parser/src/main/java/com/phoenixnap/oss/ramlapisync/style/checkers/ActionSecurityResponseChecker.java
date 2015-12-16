package com.phoenixnap.oss.ramlapisync.style.checkers;

import java.util.LinkedHashSet;
import java.util.Set;

import org.raml.model.Action;
import org.raml.model.ActionType;

import com.phoenixnap.oss.ramlapisync.style.RamlStyleCheckerAdapter;
import com.phoenixnap.oss.ramlapisync.style.StyleIssue;
import com.phoenixnap.oss.ramlapisync.verification.IssueLocation;


/**
 * Action style checker that enforces that 401 & 403 responses are defined when a security scheme is defined for an API
 * 
 * @author Kurt Paris
 * @since 0.0.2
 *
 */
public class ActionSecurityResponseChecker extends RamlStyleCheckerAdapter {
	
	
	@Override
	public Set<StyleIssue> checkActionStyle(ActionType key, Action value,
			IssueLocation location) {
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
				issues.add(new StyleIssue(location, "Secured Resources should define 401 and 403 responses", value.getResource(), value));
			} 
		}
		
		return issues;
	}

	

}
