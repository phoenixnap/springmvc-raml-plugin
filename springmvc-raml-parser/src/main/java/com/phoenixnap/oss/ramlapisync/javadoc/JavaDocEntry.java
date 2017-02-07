/*
 * Copyright 2002-2017 the original author or authors.
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
package com.phoenixnap.oss.ramlapisync.javadoc;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import com.phoenixnap.oss.ramlapisync.naming.NamingHelper;

/**
 * A class which will accept a raw chunk of javadoc as a string, clean away useless characters and provide easy access
 * to portions of it
 * 
 * @author Kurt Paris
 * @since 0.0.1
 *
 */
public class JavaDocEntry {
	private static final Logger logger = LoggerFactory.getLogger(JavaDocEntry.class);

	/**
	 * Regular expression used to identify any comment text relating to the Class or Method being documented
	 */
	private static final Pattern COMMENT_BLOCK = Pattern.compile(
			"([^@]+)@(return|param|throws|exception|since|version|author)", Pattern.CASE_INSENSITIVE);

	/**
	 * Regular expression used to identify any parameter blocks within the java doc
	 */
	private static final Pattern PARAM_BLOCK = Pattern.compile("(?m)^[\\s|\\*]*@param([^@]+)(\n|\\Z)",
			Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);
	
	/**
	 * Regular expression used to identify any link blocks within the java doc
	 */
	private static final Pattern LINK_BLOCK = Pattern.compile("\\{[\\s]*@[a-zA-Z]+[\\s]*([^@]+)\\}",
			Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);

	/**
	 * Regular Expression used to identify the return portion of the java doc
	 */
	private static final Pattern RETURN_BLOCK = Pattern.compile("(?m)^[\\s|\\*]*@return([^@]+)\n",
			Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);

	/**
	 * The extracted main comment block
	 */
	private String comment;

	/**
	 * Map of method parameters extracted javadoc keyed by parameter name
	 */
	private Map<String, String> parameterComments = new LinkedHashMap<String, String>();

	/**
	 * The extracted javadoc of the return type
	 */
	private String returnTypeComment;

	/**
	 * The extracted javadoc of each exception.//TODO link
	 */
	private Map<Integer, String> errorComments = new LinkedHashMap<Integer, String>();

	/**
	 * Constructor which accepts a raw unprocessed chunk of javadoc and extracts meaningful portions which are stored
	 * for RAML generation
	 * 
	 * @param rawJavaDoc The String representing an raw chunck of javadoc for a class or method
	 */
	public JavaDocEntry(String rawJavaDoc) {
		rawJavaDoc = cleanLinks(rawJavaDoc);
		buildMainComment(rawJavaDoc);
		buildParameterComments(rawJavaDoc);
		buildReturnComments(rawJavaDoc);
	}

	/**
	 * Match any @return entries in the javadoc and extract the documentation relating to the returned object for use
	 * within the method returns
	 * 
	 * @param rawJavaDoc
	 * @param paramMatcher
	 */
	private void buildReturnComments(String rawJavaDoc) {
		Matcher returnMatcher = RETURN_BLOCK.matcher(rawJavaDoc);
		if (returnMatcher.find()) {
			try {
				String rawParam = returnMatcher.group(1);
				returnTypeComment = NamingHelper.cleanLeadingAndTrailingNewLineAndChars(rawParam);

			} catch (Exception ex) {
				logger.warn("****WARNING: Error processing javadoc return type for: " + rawJavaDoc, ex);
			}
		}
	}

	/**
	 * Matches @param entries within a Javadoc block and stores them in a map keyed by parameter name
	 * 
	 * @param rawJavaDoc
	 * @param paramMatcher
	 */
	private void buildParameterComments(String rawJavaDoc) {
		Matcher paramMatcher = PARAM_BLOCK.matcher(rawJavaDoc);

		while (paramMatcher.find()) {
			try {
				String rawParam = paramMatcher.group(1);

				rawParam = NamingHelper.cleanLeadingAndTrailingNewLineAndChars(rawParam);

				if (rawParam != null && rawParam.contains(" ")) {
					String key = NamingHelper.cleanLeadingAndTrailingNewLineAndChars(rawParam.substring(0,
							rawParam.indexOf(" ")));
					String value = NamingHelper.cleanLeadingAndTrailingNewLineAndChars(rawParam.substring(rawParam
							.indexOf(" ")));
					if (StringUtils.hasText(key)) {
						parameterComments.put(key, value);
					}
				}
			} catch (Exception ex) {
				logger.warn("Error processing javadoc parameters for: " + rawJavaDoc, ex);
			}
		}
	}

	/**
	 * Extracts the main comment from a Method or Class Javadoc entry.
	 * 
	 * @param rawJavaDoc
	 */
	private void buildMainComment(String rawJavaDoc) {
		Matcher commentMatcher = COMMENT_BLOCK.matcher(rawJavaDoc);
		// Build the main comment first
		commentMatcher.find();
		try {
			comment = commentMatcher.group(1);
		} catch (Exception ex) {
			comment = rawJavaDoc;
		}
		comment = NamingHelper.cleanLeadingAndTrailingNewLineAndChars(comment).replaceAll("\\n *\\* *", "\n ");
	}

	/**
	 * Returns the Main comment
	 * @return String
	 */
	public String getComment() {
		return comment;
	}

	/**
	 * Returns the Parameter Comments
	 * @return Map of Parameter comments keyed by Parameter name
	 */
	public Map<String, String> getParameterComments() {
		return parameterComments;
	}

	/**
	 * returns the comment for the return type
	 * @return String with comment for return type
	 */
	public String getReturnTypeComment() {
		return returnTypeComment;
	}

	/**
	 * Gets the Exception comments //TODO
	 * @return Empty Map for now
	 */
	public Map<Integer, String> getErrorComments() {
		return errorComments;
	}

	public String toString() {
		String out = "Comment: || " + comment + " || Params: ";
		for (Entry<String, String> entry : parameterComments.entrySet()) {
			out += entry.getKey() + " : " + entry.getValue() + ", ";
		}
		out += "|| Return Comment: || " + returnTypeComment + " || Errors: ||";
		for (Entry<Integer, String> entry : errorComments.entrySet()) {
			out += entry.getKey() + " : " + entry.getValue() + ", ";
		}
		return out + " ||";
	}

	/**
	 * Removes special characters and other meaningless characters from a string in an attempt to identify how much
	 * meaningful content it contains. this score is returned as a numeric value where higher numbers mean more
	 * meaningful content
	 * 
	 * @param comment The comment to evaluate
	 * @return The score of the comment. Higher scores imply more semantic value.
	 */
	private int getStringScore(String comment) {
		// Empty strings are not really meaningful.
		if (!StringUtils.hasText(comment)) {
			return 0;
		}

		// Ignore Case
		String modifiedString = comment.toLowerCase();

		// remove @see blocks
		modifiedString = modifiedString.replaceAll(
				"@see(\\n| |\\t)+[a-z|0-9|.]*(\\#[a-z|0-9|.]*){0,1}(\\([a-z|0-9|.]*\\)){0,1}", "");

		// remove other crap words
		modifiedString = modifiedString.replaceAll("[^\\w]{0,3}non[^\\w]javadoc[^\\w]{0,3}", "");

		// remove other crap words
		modifiedString = modifiedString.replaceAll("[^\\w]{0,3}inheritdoc[^\\w]{0,3}", "");
		modifiedString = (" " + modifiedString);
		boolean loop = false;

		// loop and remove semantically null words
		do {
			String tempString = modifiedString.replaceAll(
					"[\\s](all|helper|common|functionality|to|useful|toolkit|parent|super)[\\s]", " ");
			if (!tempString.equals(modifiedString)) {
				loop = true;
				modifiedString = tempString;
			} else {
				loop = false;
			}
		} while (loop);

		// Numbers and other characters are less likely to be meaningful
		// remove all non word chars (destructive)
		modifiedString = modifiedString.replaceAll("[^\\w]", "");

		// user the remaining number of characters as the score
		return modifiedString.length();
	}

	/**
	 * We'll check the string scores of both strings and if the newer scores higher we will return true so that the
	 * proposed string will replace the current one
	 * 
	 * @param current The string we currently reference in the Doc Store
	 * @param proposed The proposed string 
	 * @return If true, then the new string is semantically better than the current and should be replaced
	 */
	private boolean shouldReplace(String current, String proposed) {
		if (getStringScore(current) > getStringScore(proposed)) {
			return false;
		} else {
			return true;
		}
	}
	
	/**
	 * Removes {@link or other {@ notation from the javadoc and retains the enclosed data
	 * 
	 * @param target The target comment to clean
	 * @return The comment cleaned from the target characters
	 */
	private String cleanLinks(String target) {
		Matcher linkMatcher = LINK_BLOCK.matcher(target);
		// Build the main comment first
		while (linkMatcher.find()) {
			try {
				target = target.substring(0, linkMatcher.start(0)) + linkMatcher.group(1) + target.substring(linkMatcher.end(1)+1);
			} catch (Exception ex) {
				//do nothing
			}
		}
		return target;
	}

	/**
	 * Due to inheritance we can have multiple java doc entries for the same entry. As such we need to combine or
	 * overwrite and keep the most meaningful set of comments we can for this particular entry
	 * 
	 * Different areas of javadoc (comment, parameter comment, etc) are evaluated seperately so that we keep the most
	 * meaningful fragment.
	 * 
	 * @param entry The JavaDocEntry to be used and integrated into this entry
	 */
	public void merge(JavaDocEntry entry) {
		if (entry != null) {
			if (shouldReplace(this.comment, entry.comment)) {
				this.comment = entry.comment;
			}
			if (shouldReplace(this.returnTypeComment, entry.returnTypeComment)) {
				this.returnTypeComment = entry.returnTypeComment;
			}
			for (Entry<String, String> parameterCommentEntry : entry.parameterComments.entrySet()) {
				if (!this.parameterComments.containsKey(parameterCommentEntry.getKey())
						|| (shouldReplace(this.parameterComments.get(parameterCommentEntry.getKey()),
								parameterCommentEntry.getValue()))) {
					this.parameterComments.put(parameterCommentEntry.getKey(), parameterCommentEntry.getValue());
				}
			}
			for (Entry<Integer, String> errorCommentEntry : entry.errorComments.entrySet()) {
				if (!this.errorComments.containsKey(errorCommentEntry.getKey())
						|| (shouldReplace(this.errorComments.get(errorCommentEntry.getKey()),
								errorCommentEntry.getValue()))) {
					this.errorComments.put(errorCommentEntry.getKey(), errorCommentEntry.getValue());
				}
			}
		}
	}

}
