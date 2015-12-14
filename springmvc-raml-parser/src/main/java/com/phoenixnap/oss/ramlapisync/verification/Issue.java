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

}
