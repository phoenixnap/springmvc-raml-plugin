package com.phoenixnap.oss.ramlapisync.verification;

import java.util.Set;

import org.raml.model.Action;
import org.raml.model.ActionType;

import com.phoenixnap.oss.ramlapisync.naming.Pair;

/**
 * A visitor that will be invoked when an action is identified
 * 
 * @author Kurt Paris
 * @since 0.0.2
 *
 */
public interface RamlActionVisitorCheck {


	/**
	 * Checks a particular action
	 * 
	 * @param name The key/verb of the action
	 * @param reference The Action which is the source of truth from the RAML model
	 * @param target The target Action to check against from the RAML model
	 * @param location The location where the issue (if any) lies
	 * @param maxSeverity The maximum severity that can be applied to any errors
	 * @return A pair containing a set of Warnings and Errors (as first and second respectively). This method must not return null.
	 * 
	 */
	public Pair<Set<Issue>, Set<Issue>> check (ActionType name, Action reference, Action target, IssueLocation location, IssueSeverity maxSeverity);


}
