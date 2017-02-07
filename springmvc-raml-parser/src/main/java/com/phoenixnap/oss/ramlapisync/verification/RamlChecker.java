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
import com.phoenixnap.oss.ramlapisync.raml.RamlRoot;

import java.util.Set;

/**
 * Parent Interface for all Raml Checkers. Implement this interface and add it to the RamlVerifier to enable a check
 * 
 * @author Kurt Paris
 * @since 0.0.2
 *
 */
public interface RamlChecker {
	
	/**
	 * Performs a specific check across two Raml Models. 
	 * 
	 * @param published The Raml as published in the contract
	 * @param implemented The Raml as generated from the implementation
	 * @return A pair containing a set of Warnings and Errors (as first and second respectively)
	 */
	public Pair<Set<Issue>, Set<Issue>> check (RamlRoot published, RamlRoot implemented);

}
