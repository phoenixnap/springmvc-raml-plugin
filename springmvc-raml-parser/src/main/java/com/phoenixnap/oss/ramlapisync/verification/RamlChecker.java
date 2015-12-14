package com.phoenixnap.oss.ramlapisync.verification;

import java.util.List;

import org.raml.model.Raml;

import com.phoenixnap.oss.ramlapisync.naming.Pair;

/**
 * Parent Interface for all Raml Checkers. Implement this interface and add it to the RamlVerifier to enable a check
 * 
 * @author Kurt Paris
 * @since 0.0.2
 *
 */
public interface RamlChecker {
	
	/**
	 * Performs a specific check across two Raml Models. 
	 * 
	 * @param published The Raml as published in the contract
	 * @param implemented The Raml as generated from the implementation
	 * @return A pair containing a list of Warnings and Errors (as first and second respectively)
	 */
	public Pair<List<Issue>, List<Issue>> check (Raml published, Raml implemented);

}
