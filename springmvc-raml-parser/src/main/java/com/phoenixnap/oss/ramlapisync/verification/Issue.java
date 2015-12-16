package com.phoenixnap.oss.ramlapisync.verification;

import org.raml.model.Action;
import org.raml.model.Resource;

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
	private Resource resourceLocation;
	private Action action;

	public Issue(IssueSeverity severity, IssueLocation location, IssueType type,
			String description, Resource resource, Action action) {
		super();
		this.severity = severity;
		this.location = location;
		this.type = type;
		this.description = description;
		this.resourceLocation = resource;
		this.action = action;
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
	}

	/**
	 * Uniform way of identifying a location in the Raml file based on the
	 * Resource and Action
	 * 
	 * @param resource
	 * @param action
	 * @return
	 */
	public static String buildRamlLocation(Resource resource, Action action) {
		String outLocation = resource.getUri();
		if (action != null) {
			outLocation = action.getType().name() + " " + outLocation;
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

	public String getRamlLocation() {
		if (ramlLocation != null) {
			return ramlLocation;
		} else {
			return buildRamlLocation(resourceLocation, action);
		}
	}

	public Resource getResourceLocation() {
		return resourceLocation;
	}

	public Action getAction() {
		return action;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((action == null) ? 0 : action.hashCode());
		result = prime * result
				+ ((description == null) ? 0 : description.hashCode());
		result = prime * result
				+ ((location == null) ? 0 : location.hashCode());
		result = prime * result
				+ ((ramlLocation == null) ? 0 : ramlLocation.hashCode());
		result = prime
				* result
				+ ((resourceLocation == null || resourceLocation.getUri() == null) ? 0 : resourceLocation.getUri().hashCode());
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
		if (getClass() != obj.getClass())
			return false;
		Issue other = (Issue) obj;
		if (action == null) {
			if (other.action != null)
				return false;
		} else if (!action.getType().equals(other.action.getType()))
			return false;
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (!description.equals(other.description))
			return false;
		if (location != other.location)
			return false;
		if (ramlLocation == null) {
			if (other.ramlLocation != null)
				return false;
		} else if (!ramlLocation.equals(other.ramlLocation))
			return false;
		if (resourceLocation == null) {
			if (other.resourceLocation != null)
				return false;
		} else if (!resourceLocation.getUri().equals(other.resourceLocation.getUri()))
			return false;
		if (severity != other.severity)
			return false;
		if (type != other.type)
			return false;
		return true;
	}
	
	

}
