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
package com.phoenixnap.oss.ramlapisync.verification;

import com.phoenixnap.oss.ramlapisync.naming.Pair;
import com.phoenixnap.oss.ramlapisync.raml.RamlAction;
import com.phoenixnap.oss.ramlapisync.raml.RamlActionType;

import java.util.Set;

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
	public Pair<Set<Issue>, Set<Issue>> check (RamlActionType name, RamlAction reference, RamlAction target, IssueLocation location, IssueSeverity maxSeverity);


}
