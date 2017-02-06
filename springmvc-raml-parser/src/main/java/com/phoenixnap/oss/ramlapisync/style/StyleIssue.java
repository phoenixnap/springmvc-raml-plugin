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

import com.phoenixnap.oss.ramlapisync.raml.RamlAction;
import com.phoenixnap.oss.ramlapisync.raml.RamlResource;
import com.phoenixnap.oss.ramlapisync.verification.Issue;
import com.phoenixnap.oss.ramlapisync.verification.IssueLocation;
import com.phoenixnap.oss.ramlapisync.verification.IssueSeverity;
import com.phoenixnap.oss.ramlapisync.verification.IssueType;

/**
 * A specific type of issue relating to style
 * 
 * @author Kurt Paris
 * @since 0.0.2
 *
 */
public class StyleIssue extends Issue {

	public StyleIssue(IssueLocation location, String description, RamlResource resource, RamlAction action) {
		super(IssueSeverity.WARNING, location, IssueType.STYLE, description, resource, action);
	}
	
	public StyleIssue(IssueLocation location, String description, String ramlLocation) {
		super(IssueSeverity.WARNING, location, IssueType.STYLE, description, ramlLocation);
	}

}
