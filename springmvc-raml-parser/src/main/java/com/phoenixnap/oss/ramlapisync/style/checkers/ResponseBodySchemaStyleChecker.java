package com.phoenixnap.oss.ramlapisync.style.checkers;

import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.raml.model.Action;
import org.raml.model.ActionType;
import org.raml.model.MimeType;
import org.raml.model.Raml;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import com.phoenixnap.oss.ramlapisync.naming.SchemaHelper;
import com.phoenixnap.oss.ramlapisync.style.RamlStyleCheckerAdapter;
import com.phoenixnap.oss.ramlapisync.style.StyleIssue;
import com.phoenixnap.oss.ramlapisync.verification.IssueLocation;

/**
 * 
 * Style checker that will check for existance of valid schemas in request bodies
 * 
 * @author kurtpa
 * @since 0.5.2
 *
 */
public class ResponseBodySchemaStyleChecker extends RamlStyleCheckerAdapter {

	/**
	 * Class Logger
	 */
	protected static final Logger logger = LoggerFactory.getLogger(ResponseBodySchemaStyleChecker.class);
	
	public static String DESCRIPTION = "Action %s should define response body schema";
	
	private Set<String> actionsToEnforce = new LinkedHashSet<String>();

	public ResponseBodySchemaStyleChecker(String actionTypesToCheck) {
		String[] tokens = StringUtils.delimitedListToStringArray(actionTypesToCheck, ",", " ");
		for (String token : tokens) {
			actionsToEnforce.add(token);
		}
	}

	@Override
	public Set<StyleIssue> checkActionStyle(ActionType key, Action value,
			IssueLocation location, Raml raml) {
		logger.debug("Checking Action: " + key);
		Set<StyleIssue> issues = new LinkedHashSet<>();
		
		//Do we have a check for this verb?
		if (actionsToEnforce.contains(key.name())) {			
			boolean schemaFound = false;
			// Now the response
			if (value.getResponses() != null && !value.getResponses().isEmpty()) {
				if (value.getResponses().containsKey("200") && value.getResponses().get("200").getBody() != null) {
					Map<String, MimeType> successResponse = value.getResponses().get("200").getBody();
					if (SchemaHelper.containsBodySchema(successResponse, raml, true)) {
						schemaFound = true;
					}
				}
				if (value.getResponses().containsKey("201") && value.getResponses().get("201").getBody() != null) {
					Map<String, MimeType> createdResponse = value.getResponses().get("201").getBody();
					if (SchemaHelper.containsBodySchema(createdResponse, raml, true)) {
						schemaFound = true;
					}
				}
			}
			
			if (!schemaFound) {
				issues.add(new StyleIssue(location, String.format(DESCRIPTION, key), value.getResource(), value));
			}
		}
			
		return issues;
	}
	
	
	
}
