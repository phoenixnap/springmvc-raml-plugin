package com.phoenixnap.oss.ramlapisync.generation;

import java.util.ArrayList;
import java.util.List;

import org.raml.model.Raml;
import org.raml.parser.visitor.RamlDocumentBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.phoenixnap.oss.ramlapisync.naming.Pair;
import com.phoenixnap.oss.ramlapisync.style.RamlStyleCheckVisitorCoordinator;
import com.phoenixnap.oss.ramlapisync.style.RamlStyleChecker;
import com.phoenixnap.oss.ramlapisync.verification.Issue;
import com.phoenixnap.oss.ramlapisync.verification.RamlChecker;
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
	
	private List<Issue> errors = new ArrayList<>();
	private List<Issue> warnings = new ArrayList<>();
	
	private List<RamlChecker> checkers;
	
	
	/**
	 * Constructor which will accept target/implementation RAML models as well as the the checkers and style checkers to verify. 
	 * Once the data is set validation is triggered
	 * 
	 * @param published The RAML model from the published RAML file
	 * @param implemented The RAML model built from the Spring MVC
	 * @param checkers Checkers that will be applied to the models
	 * @param styleChecks Style Checks that will be applied to the models
	 */
	public RamlVerifier(Raml published, Raml implemented, List<RamlChecker> checkers, List<RamlStyleChecker> styleChecks) {
		this.published = published;
		this.implemented = implemented;
		this.checkers = new ArrayList<>();
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
	 */
	public RamlVerifier(Raml published, Raml implemented, List<RamlChecker> checkers) {
		this (published, implemented, checkers, null);
	}
	
	/**
	 * Checks if there are any errors generated during the comparison
	 * 
	 * @return
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
	 * @return
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
				Pair<List<Issue>,List<Issue>> check = checker.check(published, implemented);
				warnings.addAll(check.getFirst());
				errors.addAll(check.getSecond());
			}
		}
	}
	
	/**
	 * Loads a RAML document from a file. This file can either be a resource on the class path
	 * (in which case the classpath: prefix should be omitted) or a file on disk (in which case
	 * the file: prefix should be included)
	 * 
	 * @param ramlFileUrl
	 * @return Build Raml model
	 */
	public static Raml loadRamlFromFile(String ramlFileUrl) {
		try {
			return new RamlDocumentBuilder().build(ramlFileUrl);
		} catch (NullPointerException npe) {
			logger.error("File not found at " + ramlFileUrl);
			return null;
		}
	}

	public List<Issue> getErrors() {
		return errors;
	}

	public List<Issue> getWarnings() {
		return warnings;
	}

	
	
}

