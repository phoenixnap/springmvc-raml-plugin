package com.phoenixnap.oss.ramlapisync.style.checkers;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.commons.lang.CharUtils;
import org.raml.model.Resource;

import com.phoenixnap.oss.ramlapisync.naming.NamingHelper;
import com.phoenixnap.oss.ramlapisync.style.RamlStyleCheckerAdapter;
import com.phoenixnap.oss.ramlapisync.style.StyleIssue;
import com.phoenixnap.oss.ramlapisync.verification.IssueLocation;

/**
 * Style checker that ensures that certain characters are not used in resource URLs
 * 
 * @author Kurt Paris
 * @since 0.0.2
 *
 */
public class ResourceUrlStyleChecker extends RamlStyleCheckerAdapter {

	private static String CHARS_NOT_ALLOWED_REGEX = "(-|:|%)+";
	private static Pattern CHARS_NOT_ALLOWED = Pattern.compile(CHARS_NOT_ALLOWED_REGEX); 
	
	@Override
	public Set<StyleIssue> checkResourceStyle(String name, Resource resource,
			IssueLocation location) {
		Set<StyleIssue> issues = new LinkedHashSet<>();
		
		if (!NamingHelper.isUriParamResource(name) && CHARS_NOT_ALLOWED.matcher(name).find()) {
			issues.add(new StyleIssue(location, "Special Characters in regex: " + CHARS_NOT_ALLOWED_REGEX + " not allowed in URL", resource, null));
		}
		
		if (CharUtils.isAsciiAlphaUpper(name.charAt(0))) {
			issues.add(new StyleIssue(location, "Resource URLs Should not be capitalized", resource, null));
		}
		return issues;
	}
	
	

}
