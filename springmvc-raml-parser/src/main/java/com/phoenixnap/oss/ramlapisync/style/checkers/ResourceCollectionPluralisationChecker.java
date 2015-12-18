package com.phoenixnap.oss.ramlapisync.style.checkers;

import java.util.LinkedHashSet;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Pattern;

import org.raml.model.ActionType;
import org.raml.model.Resource;
import org.raml.parser.utils.Inflector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
	
	/**
	 * Class Logger
	 */
	protected static final Logger logger = LoggerFactory.getLogger(ResourceCollectionPluralisationChecker.class);
	
	public static String DESCRIPTION = "Collections of Resources should be Pluralised in the URL";

	public static String ID_RESOURCE_REGEX = "[/]{0,1}\\{([^\\}]*)\\}";
	private static Pattern ID_RESOURCE_PATTERN = Pattern.compile(ID_RESOURCE_REGEX); 
	
	@Override
	public Set<StyleIssue> checkResourceStyle(String name, Resource resource,
			IssueLocation location) {
		logger.debug("Checking resource " + name);
		Set<StyleIssue> issues = new LinkedHashSet<>();
		
		//dont apply if we are an id resource ourselves
		if (ID_RESOURCE_PATTERN.matcher(name).find() || "/".equals(name)) {
			return issues;
		}
		//Lets check if this is a plural collection
		//if should have at least one subresource with an ID as a URI param.
		boolean hasIdSubresource = false;
		boolean hasVerb = false;
		for (Entry<String, Resource> subResourceEntry : resource.getResources().entrySet()) {
			if (ID_RESOURCE_PATTERN.matcher(subResourceEntry.getKey()).find()) {
				hasIdSubresource = true;
				
			}
			Resource subResource = subResourceEntry.getValue();
			//it should have a get or a post request on it.			
			if (subResource != null
					&& (subResource.getAction(ActionType.POST) != null
						|| subResource.getAction(ActionType.GET) != null)) {
				hasVerb = true;
			}
			if (hasIdSubresource && hasVerb) {
				logger.debug("Collection Resource identified: " + name);
				if (Inflector.singularize(name).equals(name) && !Inflector.pluralize(name).equals(name)) {
					issues.add(new StyleIssue(location, DESCRIPTION , resource, null));
				}
				break;
			}
		}
		
		
		
		
		return issues;
	}
	

}
