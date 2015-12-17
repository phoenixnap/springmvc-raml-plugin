package com.phoenixnap.oss.ramlapisync.verification;

import java.util.Set;

import org.raml.model.Resource;

import com.phoenixnap.oss.ramlapisync.naming.Pair;

/**
 * A visitor that will be invoked when an resource is identified
 * 
 * @author Kurt Paris
 * @since 0.0.2
 *
 */
public interface RamlResourceVisitorCheck {

	/**
	 * Checks a particular resource
	 * 
	 * @param name The key (relative url) of the resource being checked
	 * @param reference the Resource from the reference RAML model
	 * @param target The Resource from the target RAML model
	 * @param location The location where the issue (if any) lies
	 * @param maxSeverity The maximum allowed severity of Issue that can be generated
	 * @return A pair containing a set of Warnings and Errors (as first and second respectively). This method must not return null.
	 */
	public Pair<Set<Issue>, Set<Issue>> check (String name, Resource reference, Resource target, IssueLocation location, IssueSeverity maxSeverity);


}
