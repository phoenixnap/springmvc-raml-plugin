package com.phoenixnap.oss.ramlapisync.generation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.raml.model.Raml;
import org.raml.parser.visitor.RamlDocumentBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.phoenixnap.oss.ramlapisync.naming.Pair;
import com.phoenixnap.oss.ramlapisync.style.RamlStyleCheckVisitorCoordinator;
import com.phoenixnap.oss.ramlapisync.style.RamlStyleChecker;
import com.phoenixnap.oss.ramlapisync.verification.Issue;
import com.phoenixnap.oss.ramlapisync.verification.RamlActionVisitorCheck;
import com.phoenixnap.oss.ramlapisync.verification.RamlChecker;
import com.phoenixnap.oss.ramlapisync.verification.RamlResourceVisitorCheck;
import com.phoenixnap.oss.ramlapisync.verification.checkers.RamlCheckerResourceVisitorCoordinator;
import com.phoenixnap.oss.ramlapisync.verification.checkers.ResourceExistenceChecker;

/**
 * Engine that aims to compare RAML as loaded from a raml file that is published as our API contract with RAML generated from the Spring MVC implementation
 * 
 * @author Kurt Paris
 * @since 0.0.2
 *
 */
public class RamlVerifier {
	
	/**
	 * Class Logger
	 */
	protected static final Logger logger = LoggerFactory.getLogger(RamlVerifier.class);
	
	private Raml published;
	private Raml implemented;
	
	private Set<Issue> errors = new LinkedHashSet<>();
	private Set<Issue> warnings = new LinkedHashSet<>();
	
	private List<RamlChecker> checkers;
	
	
	/**
	 * Constructor which will accept target/implementation RAML models as well as the the checkers and style checkers to verify. 
	 * Once the data is set validation is triggered
	 * 
	 * @param published The RAML model from the published RAML file
	 * @param implemented The RAML model built from the Spring MVC
	 * @param checkers Checkers that will be applied to the models
	 * @param actionCheckers Checker Visitors that will trigger to compare individual actions
	 * @param resourceCheckers Checker Visitors that will trigger to compare individual resources
	 * @param styleChecks Style Checks that will be applied to the models
	 */
	public RamlVerifier(Raml published, Raml implemented, List<RamlChecker> checkers, List<RamlActionVisitorCheck> actionCheckers, List<RamlResourceVisitorCheck> resourceCheckers, List<RamlStyleChecker> styleChecks) {
		this.published = published;
		this.implemented = implemented;
		this.checkers = new ArrayList<>();
		
		this.checkers.add(new RamlCheckerResourceVisitorCoordinator(actionCheckers == null ? Collections.emptyList() : actionCheckers, resourceCheckers == null ? Collections.emptyList() : resourceCheckers));
		if (checkers == null) {			
			this.checkers.add(new ResourceExistenceChecker());
		} else {
			this.checkers.addAll(checkers);
		}
		if (styleChecks != null) {
			this.checkers.add(new RamlStyleCheckVisitorCoordinator(styleChecks));			
		}
		validate();
	}
	
	/**
	 * Constructor which will accept target/implementation RAML models as well as the the checkers to be applied. Style checking will not be applied
	 * Once the data is set validation is triggered
	 * 
	 * @param published The RAML model from the published RAML file
	 * @param implemented The RAML model built from the Spring MVC
	 * @param checkers Checkers that will be applied to the models
	 * @param actionCheckers Checker Visitors that will trigger to compare individual actions
	 * @param resourceCheckers Checker Visitors that will trigger to compare individual resources
	 */
	public RamlVerifier(Raml published, Raml implemented, List<RamlChecker> checkers, List<RamlActionVisitorCheck> actionCheckers, List<RamlResourceVisitorCheck> resourceCheckers) {
		this (published, implemented, checkers, actionCheckers, resourceCheckers, null);
	}
	
	/**
	 * Checks if there are any errors generated during the comparison
	 * 
	 * @return If true the verification process has found errors
	 */
	public boolean hasErrors() {
		if (errors.size() > 0) {
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * Checks if there are any warnings generated during the comparison
	 * 
	 * @return If true the verification process has found warnings
	 */
	public boolean hasWarnings() {
		if (warnings.size() > 0) {
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * Main orchestration method that will compare two Raml models together and identify discrepancies between the implementation and contract
	 */
	private void validate() {
		if (checkers != null) {
			for (RamlChecker checker : checkers) {
				Pair<Set<Issue>,Set<Issue>> check = checker.check(published, implemented);
				warnings.addAll(check.getFirst());
				errors.addAll(check.getSecond());
			}
		}
	}
	
	/**
	 * Loads a RAML document from a file. 
	 * 
	 * @param ramlFileUrl The path to the file, this can either be a resource on the class path (in which case the classpath: prefix should be omitted) or a file on disk (in which case the file: prefix should be included)
	 * @return Built Raml model
	 */
	public static Raml loadRamlFromFile(String ramlFileUrl) {
		try {
			return new RamlDocumentBuilder().build(ramlFileUrl);
		} catch (NullPointerException npe) {
			logger.error("File not found at " + ramlFileUrl);
			return null;
		}
	}

	/**
	 * Retrieve the Errors identified
	 * 
	 * @return A set of unique Error-Level Issues
	 */
	public Set<Issue> getErrors() {
		return errors;
	}

	/**
	 * Retrieve the Warnings identified
	 * 
	 * @return A set of unique Warning-Level Issues
	 */
	public Set<Issue> getWarnings() {
		return warnings;
	}

	
	
}

