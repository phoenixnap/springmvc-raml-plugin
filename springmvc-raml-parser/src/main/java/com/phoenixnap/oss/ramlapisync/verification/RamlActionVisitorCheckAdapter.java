package com.phoenixnap.oss.ramlapisync.verification;

import java.util.Collections;
import java.util.Set;

import org.raml.model.Action;
import org.raml.model.ActionType;

import com.phoenixnap.oss.ramlapisync.naming.Pair;

/**
 * Adapter pattern for Raml Action Visitors.
 * 
 * @author Kurt Paris
 * @since 0.0.2
 *
 */
public class RamlActionVisitorCheckAdapter implements RamlActionVisitorCheck {

	@Override
	public Pair<Set<Issue>, Set<Issue>> check(ActionType name,
			Action reference, Action target) {
		return new Pair<Set<Issue>, Set<Issue>>(Collections.emptySet(), Collections.emptySet());
	}

	

}
