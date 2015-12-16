package com.phoenixnap.oss.ramlapisync.verification;

import java.util.Collections;
import java.util.Set;

import org.raml.model.Resource;

import com.phoenixnap.oss.ramlapisync.naming.Pair;

/**
 * Adapter pattern for Raml Resource Visitors.
 * 
 * @author Kurt Paris
 * @since 0.0.2
 *
 */
public class RamlResourceVisitorCheckAdapter implements RamlResourceVisitorCheck {


	@Override
	public Pair<Set<Issue>, Set<Issue>> check(String name, Resource reference,
			Resource target, IssueLocation location, IssueSeverity maxSeverity) {
		return new Pair<Set<Issue>, Set<Issue>>(Collections.emptySet(), Collections.emptySet());
	}

}
