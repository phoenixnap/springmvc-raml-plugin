/*
 * Copyright 2002-2016 the original author or authors.
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

/**
 * Different Levels of severity associated with an Issue
 * 
 * @author Kurt Paris
 * @since 0.0.2
 *
 */
public enum IssueSeverity {
	
	/**
	 * Breaking Issue. The implementation contains something that will not work as defined in the contract or is missing part of the definted implementation
	 */
	ERROR, 
	
	/**
	 * Non breaking Issue. This could be an unexposed or missing bit of optional functionality or functionality that has been exposed with minor discrepancies that should still cause it to work
	 */
	WARNING;

}
