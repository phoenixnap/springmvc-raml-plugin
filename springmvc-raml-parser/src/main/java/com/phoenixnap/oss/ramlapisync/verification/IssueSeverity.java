package com.phoenixnap.oss.ramlapisync.verification;

/**
 * Different Levels of severity associated with an Issue
 * 
 * @author Kurt Paris
 * @since 0.0.2
 *
 */
public enum IssueSeverity {
	
	/**
	 * Breaking Issue. The implementation contains something that will not work as defined in the contract or is missing part of the definted implementation
	 */
	ERROR, 
	
	/**
	 * Non breaking Issue. This could be an unexposed or missing bit of optional functionality or functionality that has been exposed with minor discrepancies that should still cause it to work
	 */
	WARNING;

}
