/*
 * Copyright 2002-2017 the original author or authors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package com.phoenixnap.oss.ramlapisync.style;

import java.util.Set;

import com.phoenixnap.oss.ramlapisync.raml.RamlAbstractParam;
import com.phoenixnap.oss.ramlapisync.raml.RamlAction;
import com.phoenixnap.oss.ramlapisync.raml.RamlActionType;
import com.phoenixnap.oss.ramlapisync.raml.RamlResource;
import com.phoenixnap.oss.ramlapisync.raml.RamlRoot;
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
	public Set<StyleIssue> checkParameterStyle(String name, RamlAbstractParam param);
	
	/**
	 * Check the style of a particular action
	 * 
	 * @param key The action's verb
	 * @param value The Action from the RAML model
	 * @param location The location where the issue (if any) lies
	 * @param raml The Raml Document being checked
	 * @return A list of style issues or an Empty List if none are found. This method must not return null.
	 */
	public Set<StyleIssue> checkActionStyle(RamlActionType key, RamlAction value, IssueLocation location, RamlRoot raml);

	/**
	 * Check the style of a particular resource. This will be called on all child resources by the coordinator.
	 * 
	 * @param name The name of the resource (relative URL)
	 * @param resource The Resource from the RAML model
	 * @param location The location where the issue (if any) lies
	 * @param raml The Raml Document being checked
	 * @return A list of style issues or an Empty List if none are found. This method must not return null.
	 */
	public Set<StyleIssue> checkResourceStyle(String name, RamlResource resource, IssueLocation location, RamlRoot raml);

}
