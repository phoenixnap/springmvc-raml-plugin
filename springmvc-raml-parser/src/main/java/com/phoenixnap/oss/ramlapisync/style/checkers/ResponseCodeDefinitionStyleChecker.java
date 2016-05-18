package com.phoenixnap.oss.ramlapisync.style.checkers;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.raml.model.Action;
import org.raml.model.ActionType;
import org.raml.model.Raml;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.util.CollectionUtils;
import org.springframework.util.MultiValueMap;

import com.phoenixnap.oss.ramlapisync.style.RamlStyleCheckerAdapter;
import com.phoenixnap.oss.ramlapisync.style.StyleIssue;
import com.phoenixnap.oss.ramlapisync.verification.IssueLocation;

/**
 * 
 * Style checker that will check for existence of valid schemas in request bodies
 * 
 * @author kurtpa
 * @since 0.5.2
 *
 */
public class ResponseCodeDefinitionStyleChecker extends RamlStyleCheckerAdapter {
	
	/**
	 * Class Logger
	 */
	protected static final Logger logger = LoggerFactory.getLogger(ResponseCodeDefinitionStyleChecker.class);
	
	public static String DESCRIPTION = "%s Verb should define %s (%i) response";

	private MultiValueMap<String, HttpStatus> statusChecks;

	public ResponseCodeDefinitionStyleChecker(MultiValueMap<String, HttpStatus> statusChecks) {
		this.statusChecks = statusChecks;
	}

	
	@Override
	public Set<StyleIssue> checkActionStyle(ActionType key, Action value,
			IssueLocation location, Raml raml) {
		logger.debug("Checking Action: " + key);
		Set<StyleIssue> issues = new LinkedHashSet<>();
		
		//Do we have a check for this verb?
		if (statusChecks.containsKey(key.name())) {			
			List<HttpStatus> statuses = statusChecks.get(key);
			if (!CollectionUtils.isEmpty(statuses)) {
				for (HttpStatus check : statuses) {
					if (value.getResponses() == null
							|| !value.getResponses().containsKey(check.value())) {
						issues.add(new StyleIssue(location, String.format(DESCRIPTION, key, check.name(), check.value()), value.getResource(), value));
					} 
				}
				
			}
		}
		
		return issues;
	}

	
	
		
}
