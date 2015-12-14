package com.phoenixnap.oss.ramlapisync.style;

import org.raml.model.Action;
import org.raml.model.Resource;

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

	public StyleIssue(IssueLocation location, String description, Resource resource, Action action) {
		super(IssueSeverity.WARNING, location, IssueType.STYLE, description, resource, action);
	}
	
	public StyleIssue(IssueLocation location, String description, String ramlLocation) {
		super(IssueSeverity.WARNING, location, IssueType.STYLE, description, ramlLocation);
	}

}
