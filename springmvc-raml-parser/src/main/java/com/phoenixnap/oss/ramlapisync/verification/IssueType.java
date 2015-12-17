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
	 * The item was found but with different data/config that could cause API failure
	 */
	DIFFERENT,
	
	/**
	 * The item was not found
	 */
	MISSING,	
	
	
	/**
	 * The item was declared as expected
	 */
	STYLE;
}
