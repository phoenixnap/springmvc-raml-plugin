package com.phoenixnap.oss.ramlapisync.verification;

/**
 * Different Levels of severity associated with an Issue
 * 
 * @author Kurt Paris
 * @since 0.0.2
 *
 */
public enum IssueType {
	
	/**
	 * The item was not found as expected
	 */
	MISSING,	
	
	
	/**
	 * The item was declared as expected
	 */
	STYLE;
}
