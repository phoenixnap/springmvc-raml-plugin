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
	 * @param key
	 * @param reference
	 * @param target
	 * @return A pair containing a set of Warnings and Errors (as first and second respectively). This method must not return null.
	 */
	public Pair<Set<Issue>, Set<Issue>> check (ActionType name, Action reference, Action target);


}
