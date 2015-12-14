package com.phoenixnap.oss.ramlapisync.style;

import java.util.List;

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
	 * @param name
	 * @param param
	 * @return A list of style issues or an Empty List if none are found. This method must not return null.
	 */
	public List<StyleIssue> checkParameterStyle(String name, AbstractParam param);
	
	/**
	 * Check the style of a particular action
	 * 
	 * @param key
	 * @param value
	 * @param location
	 * @return A list of style issues or an Empty List if none are found. This method must not return null.
	 */
	public List<StyleIssue> checkActionStyle(ActionType key, Action value, IssueLocation location);

	/**
	 * Check the style of a particular resource. This will be called on all child resources by the coordinator.
	 * 
	 * @param name
	 * @param resource
	 * @param location
	 * @return A list of style issues or an Empty List if none are found. This method must not return null.
	 */
	public List<StyleIssue> checkResourceStyle(String name, Resource resource, IssueLocation location);

}
