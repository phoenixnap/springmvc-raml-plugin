package com.phoenixnap.oss.ramlapisync.style;

import java.util.Collections;
import java.util.Set;

import org.raml.model.Action;
import org.raml.model.ActionType;
import org.raml.model.Resource;
import org.raml.model.parameter.AbstractParam;

import com.phoenixnap.oss.ramlapisync.verification.IssueLocation;

/**
 * Adapter pattern for Raml Style Checkers.
 * 
 * @author Kurt Paris
 * @since 0.0.2
 *
 */
public class RamlStyleCheckerAdapter implements RamlStyleChecker {

	@Override
	public Set<StyleIssue> checkParameterStyle(String name, AbstractParam param) {
		return Collections.emptySet();
	}

	@Override
	public Set<StyleIssue> checkActionStyle(ActionType key, Action value,
			IssueLocation location) {
		return Collections.emptySet();
	}

	@Override
	public Set<StyleIssue> checkResourceStyle(String name, Resource resource,
			IssueLocation location) {
		return Collections.emptySet();
	}

}
