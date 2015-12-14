package com.phoenixnap.oss.ramlapisync.style;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.commons.lang.CharUtils;
import org.raml.model.Resource;

import com.phoenixnap.oss.ramlapisync.naming.NamingHelper;
import com.phoenixnap.oss.ramlapisync.verification.IssueLocation;

public class ResourceUrlStyleChecker extends RamlStyleCheckerAdapter {

	private static String CHARS_NOT_ALLOWED_REGEX = "(-|:|%)+";
	private static Pattern CHARS_NOT_ALLOWED = Pattern.compile(CHARS_NOT_ALLOWED_REGEX); 
	
	@Override
	public List<StyleIssue> checkResourceStyle(String name, Resource resource,
			IssueLocation location) {
		List<StyleIssue> issues = new ArrayList<>();
		
		if (!NamingHelper.isUriParamResource(name) && CHARS_NOT_ALLOWED.matcher(name).find()) {
			issues.add(new StyleIssue(location, "Special Characters in regex: " + CHARS_NOT_ALLOWED_REGEX + " not allowed in URL", resource, null));
		}
		
		if (CharUtils.isAsciiAlphaUpper(name.charAt(0))) {
			issues.add(new StyleIssue(location, "Resource URLs Should not be capitalized", resource, null));
		}
		return super.checkResourceStyle(name, resource, location);
	}
	
	

}
