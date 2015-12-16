package com.phoenixnap.oss.ramlapisync.verification;

/**
 * The Source locations of the Issue
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
