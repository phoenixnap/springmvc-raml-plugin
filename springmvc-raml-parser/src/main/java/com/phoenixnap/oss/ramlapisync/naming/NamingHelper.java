/*
 * Copyright 2002-2015 the original author or authors.
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
package com.phoenixnap.oss.ramlapisync.naming;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.util.StringUtils;

/**
 * Class containing methods relating to naming converntions and string cleanup for naming
 * 
 * @author Kurt Paris
 * @since 0.0.1
 *
 */
public class NamingHelper {

	private static final Pattern CLASS_SUFFIXES_TO_CLEAN = Pattern.compile(
			"^(.+)(services|service|impl|class|controller)", Pattern.CASE_INSENSITIVE);
	
	/**
	 * Checks if a Resource URI fragment is a URI Parameter. URI parameters are defined as {myParameter}
	 * 
	 * @param resource The Resource key/ relative URL
	 * @return If true this URI is a frament containing a URI parameter
	 */
	public static boolean isUriParamResource(String resource) {
		if (resource == null) {
			return false;
		}
		resource = NamingHelper.cleanLeadingAndTrailingNewLineAndChars(resource.toLowerCase());
		if (resource.startsWith("{") && resource.endsWith("}")) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Utility method to clean New Line,Spaces and other highly useless characters found (mainly in javadoc)
	 * 
	 * @param input The string to be cleaned
	 * @return Cleaned String
	 */
	public static String cleanLeadingAndTrailingNewLineAndChars(String input) {

		if (!StringUtils.hasText(input)) {
			return input;
		}
		String output = input;
		output = output.replaceAll("[\\s]+\\*[\\s]+", " ");
		while (output.startsWith("/") || output.startsWith("\n") || output.startsWith("*") || output.startsWith("-")
				|| output.startsWith("\t") || output.startsWith(" ") || output.startsWith("\\")) {
			output = output.substring(1);
		}

		while (output.endsWith("/") || output.endsWith("\n") || output.endsWith(" ") || output.endsWith(",")
				|| output.endsWith("\t") || output.endsWith("-") || output.endsWith("*")) {
			output = output.substring(0, output.length() - 1);
		}
		return output;
	}

	/**
	 * Convert a class name into its restful Resource representation.
	 *
	 * eg. MonitorServiceImpl becomes Monitor
	 *
	 * @param clazz The Class to name
	 * @return The name for this class
	 */
	public static String convertClassName(Class<?> clazz) {
		String convertedName = clazz.getSimpleName();
		boolean clean = true;
		do {
			Matcher cleaner = CLASS_SUFFIXES_TO_CLEAN.matcher(convertedName);
			if (cleaner.matches()) {
				if (cleaner.group(1) != null && cleaner.group(1).length() > 0) {
					convertedName = cleaner.group(1);
				}
			} else {
				clean = false;
			}
		} while (clean);
		return StringUtils.uncapitalize(convertedName);
	}
	
	/**
	 * Attempts to load system propertes from the string or use included defaults if available
	 * 
	 * @param inputString Strign containing spring property format
	 * @return resolved String
	 */
	public static String resolveProperties(String inputString) {
		if (!StringUtils.hasText(inputString)) {
			return inputString;
		}
		String tempString = inputString.trim();
		String outString = "";
		int startIndex = 0;
		while (tempString.indexOf("${", startIndex) != -1) {
			int startsWithPos = tempString.indexOf("${", startIndex);
			int endsWithPos = tempString.indexOf("}", startsWithPos+2);
			int nextBracket = tempString.indexOf("{", startsWithPos+2);
			if (nextBracket != -1 && endsWithPos > nextBracket) {
				endsWithPos = tempString.indexOf("}", endsWithPos+1);
			}
			int defaultPos = tempString.lastIndexOf(":", endsWithPos);
			if (defaultPos < startsWithPos) {
				defaultPos = -1;
			}
			
			if (startsWithPos != -1 && endsWithPos != -1) {
				String value = tempString.substring(startsWithPos+2,endsWithPos);
				String defaultString;
				String key;				
				
				if (defaultPos != -1) {
					//lets get default.
					defaultString = value.substring(value.lastIndexOf(":") +1);
					key = value.substring(0, value.lastIndexOf(":"));
				} else {
					key = value;
					defaultString = value;
				}
				
				outString += tempString.substring(startIndex, startsWithPos) + System.getProperty(key, defaultString);
				startIndex = endsWithPos+1;
			}		
		}
		if (startIndex < tempString.length()) {
			outString += tempString.substring(startIndex);
		}
		return outString;
	}
	

	
}
