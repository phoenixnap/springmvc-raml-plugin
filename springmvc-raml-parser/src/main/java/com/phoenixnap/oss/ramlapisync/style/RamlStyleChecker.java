package com.phoenixnap.oss.ramlapisync.style;

import java.util.Set;

import org.raml.model.Action;
import org.raml.model.ActionType;
import org.raml.model.Resource;
import org.raml.model.parameter.AbstractParam;

import com.phoenixnap.oss.ramlapisync.verification.IssueLocation;

/**
 * Parent Interface for all Raml Style Checkers. Implement this interface and add it to the RamlStyleCheckCoordinator to enable this check
 * 
 * @author Kurt Paris
 * @since 0.0.2
 *
 */
public interface RamlStyleChecker {
	
	/**
	 * Check the style of a particular parameter
	 * 
	 * @param name The parameter name to be checked
	 * @param param The Parameter from the RAML Model
	 * @return A list of style issues or an Empty List if none are found. This method must not return null.
	 */
	public Set<StyleIssue> checkParameterStyle(String name, AbstractParam param);
	
	/**
	 * Check the style of a particular action
	 * 
	 * @param key The action's verb
	 * @param value The Action from the RAML model
	 * @param location The location where the issue (if any) lies
	 * @return A list of style issues or an Empty List if none are found. This method must not return null.
	 */
	public Set<StyleIssue> checkActionStyle(ActionType key, Action value, IssueLocation location);

	/**
	 * Check the style of a particular resource. This will be called on all child resources by the coordinator.
	 * 
	 * @param name The name of the resource (relative URL)
	 * @param resource The Resource from the RAML model
	 * @param location The location where the issue (if any) lies
	 * @return A list of style issues or an Empty List if none are found. This method must not return null.
	 */
	public Set<StyleIssue> checkResourceStyle(String name, Resource resource, IssueLocation location);

}
