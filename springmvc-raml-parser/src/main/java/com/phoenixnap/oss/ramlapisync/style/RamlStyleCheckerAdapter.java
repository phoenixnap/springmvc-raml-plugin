package com.phoenixnap.oss.ramlapisync.style;

import java.util.Collections;
import java.util.List;

import org.raml.model.Action;
import org.raml.model.ActionType;
import org.raml.model.Resource;
import org.raml.model.parameter.AbstractParam;

import com.phoenixnap.oss.ramlapisync.verification.IssueLocation;

/**
 * Adapter pattern Raml Style Checkers.
 * 
 * @author Kurt Paris
 * @since 0.0.2
 *
 */
public class RamlStyleCheckerAdapter implements RamlStyleChecker {

	@Override
	public List<StyleIssue> checkParameterStyle(String name, AbstractParam param) {
		return Collections.emptyList();
	}

	@Override
	public List<StyleIssue> checkActionStyle(ActionType key, Action value,
			IssueLocation location) {
		return Collections.emptyList();
	}

	@Override
	public List<StyleIssue> checkResourceStyle(String name, Resource resource,
			IssueLocation location) {
		return Collections.emptyList();
	}

}
