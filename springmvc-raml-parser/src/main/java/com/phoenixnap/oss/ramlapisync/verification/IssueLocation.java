package com.phoenixnap.oss.ramlapisync.verification;

/**
 * Different Levels of severity associated with an Issue
 * 
 * @author Kurt Paris
 * @since 0.0.2
 *
 */
public enum IssueLocation {
	
	/**
	 * The item was not found in the implementation but was exposed in the contract
	 */
	SOURCE,	
	
	
	/**
	 * The item was not found in the contract but was implemented, causing this issue
	 */
	CONTRACT;
}
