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

import com.phoenixnap.oss.ramlapisync.raml.RamlAction;
import com.phoenixnap.oss.ramlapisync.raml.RamlResource;
import org.springframework.util.StringUtils;

/**
 * Data object which identifies a discrepancy between two RAML models
 * 
 * @author Kurt Paris
 * @since 0.0.2
 *
 */
public class Issue {

	private IssueSeverity severity;
	private IssueLocation location;
	private IssueType type;

	private String description;
	private String ramlLocation;
	private RamlResource resourceLocation;
	private RamlAction action;
	private String parameter;
	
	public Issue(IssueSeverity severity, IssueLocation location, IssueType type,
			String description, RamlResource resource, RamlAction action, String parameter) {
		super();
		this.severity = severity;
		this.location = location;
		this.type = type;
		this.description = description;
		this.resourceLocation = resource;
		this.action = action;
		this.parameter = parameter;
	}

	public Issue(IssueSeverity severity, IssueLocation location, IssueType type,
				 String description, RamlResource resource, RamlAction action) {
		this(severity, location, type, description, resource, action, null);
	}

	public Issue(IssueSeverity severity, IssueLocation location, IssueType type,
			String description, String ramlLocation) {
		super();
		this.severity = severity;
		this.type = type;
		this.location = location;
		this.description = description;
		this.ramlLocation = ramlLocation;
		this.resourceLocation = null;
		this.action = null;
		this.parameter = null;
	}

	/**
	 * Uniform way of identifying a location in the Raml file based on the
	 * Resource and Action
	 * 
	 * @param resource The Resource that this Issue relates to
	 * @param action The Action that this Issue relates to
	 * @param parameter The parameter name that this Issue Relates to
	 * @return String identifying the location of this issue
	 */
	public static String buildRamlLocation(RamlResource resource, RamlAction action, String parameter) {
		String outLocation = resource.getUri();
		if (action != null && action.getType() != null) {
			outLocation = action.getType().name() + " " + outLocation;
		}
		if (StringUtils.hasText(parameter)) {
			outLocation = parameter + " : " + outLocation;
		}
		return outLocation;
	}

	
	public IssueType getType() {
		return type;
	}
	
	public IssueSeverity getSeverity() {
		return severity;
	}

	public IssueLocation getLocation() {
		return location;
	}

	public String getDescription() {
		return description;
	}

	/**
	 * Gets the location of the Issue as a string, or builds it from the Resource/Action/Parameter info stored.
	 * 
	 * @return The location as a string
	 */
	public String getRamlLocation() {
		if (ramlLocation == null) {
			ramlLocation = buildRamlLocation(resourceLocation, action, parameter);
		} 
		return ramlLocation;
	}

	public RamlResource getResourceLocation() {
		return resourceLocation;
	}

	public RamlAction getAction() {
		return action;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;

		result = prime * result
				+ ((description == null) ? 0 : description.hashCode());
		result = prime * result
				+ ((location == null) ? 0 : location.hashCode());
		String ramlLocation = getRamlLocation();
		result = prime * result
				+ ((ramlLocation == null) ? 0 : ramlLocation.hashCode());
		
		result = prime * result
				+ ((severity == null) ? 0 : severity.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!Issue.class.isAssignableFrom(obj.getClass()))
			return false;
		Issue other = (Issue) obj;		
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (!description.equals(other.description))
			return false;
		if (location != other.location)
			return false;
		String ramlLocation = getRamlLocation();
		String otherRamlLocation = other.getRamlLocation();
		if (ramlLocation == null) {
			if (otherRamlLocation != null)
				return false;
		} else if (!ramlLocation.equals(otherRamlLocation))
			return false;
		if (severity != other.severity)
			return false;
		if (type != other.type)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Issue [severity=" + severity + ", location=" + location
				+ ", type=" + type + ", description=" + description
				+ ", ramlLocation=" + getRamlLocation() + "]";
	}
	
	
	

}
