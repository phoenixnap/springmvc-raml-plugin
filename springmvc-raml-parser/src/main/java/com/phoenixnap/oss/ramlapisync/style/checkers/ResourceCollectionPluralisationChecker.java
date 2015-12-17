package com.phoenixnap.oss.ramlapisync.style.checkers;

import java.util.LinkedHashSet;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Pattern;

import org.raml.model.ActionType;
import org.raml.model.Resource;
import org.raml.parser.utils.Inflector;

import com.phoenixnap.oss.ramlapisync.style.RamlStyleCheckerAdapter;
import com.phoenixnap.oss.ramlapisync.style.StyleIssue;
import com.phoenixnap.oss.ramlapisync.verification.IssueLocation;

/**
 * Style checker that ensures that collection resources are defined in the plural form
 * 
 * @author Kurt Paris
 * @since 0.0.2
 *
 */
public class ResourceCollectionPluralisationChecker extends RamlStyleCheckerAdapter {
	
	public static String DESCRIPTION = "Collections of Resources should be Pluralised in the URL";

	public static String ID_RESOURCE_REGEX = "[/]{0,1}\\{([^\\}]*)\\}";
	private static Pattern ID_RESOURCE_PATTERN = Pattern.compile(ID_RESOURCE_REGEX); 
	
	@Override
	public Set<StyleIssue> checkResourceStyle(String name, Resource resource,
			IssueLocation location) {
		Set<StyleIssue> issues = new LinkedHashSet<>();
		
		
		//Lets check if this is a plural collection
		//if should have at least one subresource with an ID as a URI param.
		boolean hasIdSubresource = false;
		for (Entry<String, Resource> subResource : resource.getResources().entrySet()) {
			if (ID_RESOURCE_PATTERN.matcher(subResource.getKey()).find()) {
				hasIdSubresource = true;
				break;
			}
		}
		//it should have a get or a post request on it.
		boolean hasVerb = false;
		if (resource.getAction(ActionType.POST) != null
				|| resource.getAction(ActionType.GET) != null) {
			hasVerb = true;
		}
		
		if (hasIdSubresource && hasVerb) {
			if (Inflector.singularize(name).equals(name) && !Inflector.pluralize(name).equals(name)) {
				issues.add(new StyleIssue(location, DESCRIPTION , resource, null));
			}
		}
		
		return issues;
	}
	

}
