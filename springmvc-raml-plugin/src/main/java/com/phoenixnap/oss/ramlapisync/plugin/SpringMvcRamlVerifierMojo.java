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
package com.phoenixnap.oss.ramlapisync.plugin;

import com.phoenixnap.oss.ramlapisync.generation.RamlGenerator;
import com.phoenixnap.oss.ramlapisync.generation.RamlVerifier;
import com.phoenixnap.oss.ramlapisync.generation.rules.RamlLoader;
import com.phoenixnap.oss.ramlapisync.parser.ResourceParser;
import com.phoenixnap.oss.ramlapisync.parser.SpringMvcResourceParser;
import com.phoenixnap.oss.ramlapisync.raml.InvalidRamlResourceException;
import com.phoenixnap.oss.ramlapisync.raml.RamlRoot;
import com.phoenixnap.oss.ramlapisync.style.RamlStyleChecker;
import com.phoenixnap.oss.ramlapisync.style.checkers.ActionSecurityResponseChecker;
import com.phoenixnap.oss.ramlapisync.style.checkers.RequestBodySchemaStyleChecker;
import com.phoenixnap.oss.ramlapisync.style.checkers.ResourceCollectionPluralisationChecker;
import com.phoenixnap.oss.ramlapisync.style.checkers.ResourceUrlStyleChecker;
import com.phoenixnap.oss.ramlapisync.style.checkers.ResponseBodySchemaStyleChecker;
import com.phoenixnap.oss.ramlapisync.style.checkers.ResponseCodeDefinitionStyleChecker;
import com.phoenixnap.oss.ramlapisync.verification.Issue;
import com.phoenixnap.oss.ramlapisync.verification.RamlActionVisitorCheck;
import com.phoenixnap.oss.ramlapisync.verification.RamlChecker;
import com.phoenixnap.oss.ramlapisync.verification.RamlResourceVisitorCheck;
import com.phoenixnap.oss.ramlapisync.verification.checkers.ActionContentTypeChecker;
import com.phoenixnap.oss.ramlapisync.verification.checkers.ActionExistenceChecker;
import com.phoenixnap.oss.ramlapisync.verification.checkers.ActionQueryParameterChecker;
import com.phoenixnap.oss.ramlapisync.verification.checkers.ActionResponseBodySchemaChecker;
import com.phoenixnap.oss.ramlapisync.verification.checkers.ResourceExistenceChecker;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Maven Plugin MOJO specific to verification of RAML from implementations in Spring MVC Projects.
 * 
 * @author Kurt Paris
 * @author Micheal Schembri Wismayer
 * @since 0.0.1
 */
@Mojo(name = "verify-springmvc-api-docs", requiresDependencyResolution = ResolutionScope.COMPILE_PLUS_RUNTIME, threadSafe = true)
public class SpringMvcRamlVerifierMojo extends CommonApiSyncMojo {

	/**
	 * Path to the raml document to be verified
	 */
	@Parameter(required = true, readonly = true, defaultValue = "")
	protected String ramlToVerifyPath;
	
	
	/**
	 * Flag that will enable or disable checking of the RAML file against the Server Implementation. If this is set to false it will override other check flags
	 */
	@Parameter(required = false, readonly = true, defaultValue = "true")
	protected Boolean checkRamlAgainstImplementation;
	
	/**
	 * A Portion of the URL of the implemented RAML that will be ignored when verifying
	 */
	@Parameter(required = false, readonly = true, defaultValue = "")
	protected String uriPrefixToIgnore;
	
	/**
	 * Flag that will enable or disable Style Checking of the RAML file. If this is set to false it will override other style check flags
	 */
	@Parameter(required = false, readonly = true, defaultValue = "true")
	protected Boolean performStyleChecks;
	
	/**
	 * Flag that will enable or disable Checks for existence of Resources 
	 */
	@Parameter(required = false, readonly = true, defaultValue = "true")
	protected Boolean checkForResourceExistence;
	
	/**
	 * Flag that will enable or disable Checks for existence of Actions/Verbs
	 */
	@Parameter(required = false, readonly = true, defaultValue = "true")
	protected Boolean checkForActionExistence;
	
	/**
	 * Flag that will enable or disable Checks for compatibility in the type of Actions/Verbs
	 */
	@Parameter(required = false, readonly = true, defaultValue = "true")
	protected Boolean checkForActionContentType;
	
	/**
	 *  that will enable or disable checks for existence of query parameters
	 */
	@Parameter(required = false, readonly = true, defaultValue = "true")
	protected Boolean checkForQueryParameterExistence;
	
	
	/**
	 * Flag that will enable or disable checks for plural resources names
	 */
	@Parameter(required = false, readonly = true, defaultValue = "true")
	protected Boolean checkForPluralisedResourceNames;
	
	/**
	 * Flag that will enable or disable checks for special characters in URLs
	 */
	@Parameter(required = false, readonly = true, defaultValue = "true")
	protected Boolean checkForSpecialCharactersInResourceNames;
	
	/**
	 * Flag that will enable or disable checks for 401 and 403 responses for secured resources
	 */
	@Parameter(required = false, readonly = true, defaultValue = "true")
	protected Boolean checkForDefinitionOf40xResponseInSecuredResource;
	
	/**
	 * Comma seperated list of HTTP Methods to check for schemas being present in the successful response Body.
	 */
	@Parameter(required = false, readonly = true, defaultValue = "")
	protected String checkForSchemaInSuccessfulResponseBody;
	
	/**
	 * Flag that will enable or disable checks for 400 and 500 error codes in certain verbs
	 */
	@Parameter(required = false, readonly = true, defaultValue = "false")
	protected Boolean checkForDefinitionOfErrorCodes;
	
	
	/**
	 * Comma seperated list of HTTP Methods to check for schemas being present in the request Body.
	 */
	@Parameter(required = false, readonly = true, defaultValue = "")
	protected String checkForSchemaInRequestBody;
	
	/**
	 * Flag that will enable or disable checks for 404 responses get requests
	 */
	@Parameter(required = false, readonly = true, defaultValue = "false")
	protected Boolean checkForDefinitionOf404ResponseInGetRequest;

	/**
	 * Flag that will enable or disable checks for response body schema
	 */
	@Parameter(required = false, readonly = true, defaultValue = "false")
	protected Boolean checkForResponseBodySchema;

	/**
	 * Flag that will enable or disable braking of the build if Warnings are found
	 */
	@Parameter(required = false, readonly = true, defaultValue = "false")
	protected Boolean breakBuildOnWarnings;
	
	/**
	 * Flag that will enable or disable logging of warning level issues to standard out if found
	 */
	@Parameter(required = false, readonly = true, defaultValue = "true")
	protected Boolean logWarnings;
	
	/**
	 * Flag that will enable or disable logging of error level Issues to standard out if found
	 */
	@Parameter(required = false, readonly = true, defaultValue = "true")
	protected Boolean logErrors;
	
	@SuppressWarnings("unchecked")
	@Override
	protected Class<? extends Annotation>[] getSupportedClassAnnotations() {
		return new Class[] { Controller.class, RestController.class };
	}

	protected void verifyRaml() throws MojoExecutionException, MojoFailureException, IOException, InvalidRamlResourceException  {

		super.prepareRaml();

		Class<?>[] classArray = new Class[annotatedClasses.size()];
		classArray = this.annotatedClasses.toArray(classArray);
		
		//Lets use the base folder if supplied or default to relative scanning
		File targetPath;		
		if (StringUtils.hasText(javaDocPath)) {
			targetPath = new File(javaDocPath);
		} else if (project.getBasedir().getParentFile() != null) {
			targetPath = project.getBasedir().getParentFile();
		} else {
			targetPath = project.getBasedir();
		}
		
		List<RamlChecker> checkers = new ArrayList<>();
		List<RamlActionVisitorCheck> actionCheckers = new ArrayList<>();
		List<RamlResourceVisitorCheck> resourceCheckers = new ArrayList<>();
		RamlRoot implementedRaml = null;
		
		if (checkRamlAgainstImplementation) {
			ResourceParser scanner = new SpringMvcResourceParser(targetPath, version, ResourceParser.CATCH_ALL_MEDIA_TYPE, false);
			RamlGenerator ramlGenerator = new RamlGenerator(scanner);
			// Process the classes selected and build Raml model
			ramlGenerator.generateRamlForClasses(project.getArtifactId(), version, "/", classArray, this.documents);
			implementedRaml = ramlGenerator.getRaml();
			

			if (checkForResourceExistence) {
				checkers.add(new ResourceExistenceChecker());
			}
			if (checkForActionExistence) {
				resourceCheckers.add(new ActionExistenceChecker());
			}
			if (checkForQueryParameterExistence) {
				actionCheckers.add(new ActionQueryParameterChecker());
			}
			if (checkForActionContentType) {
				actionCheckers.add(new ActionContentTypeChecker());
			}
			if(checkForResponseBodySchema) {
				actionCheckers.add(new ActionResponseBodySchemaChecker());
			}
		}
		
		List<RamlStyleChecker> styleCheckers = new ArrayList<>();
		
		
		if (performStyleChecks) {
			if (checkForPluralisedResourceNames) {
				styleCheckers.add(new ResourceCollectionPluralisationChecker());
			}
			if (checkForDefinitionOf40xResponseInSecuredResource) {
				styleCheckers.add(new ActionSecurityResponseChecker());
			}
			if (checkForSpecialCharactersInResourceNames) {
				styleCheckers.add(new ResourceUrlStyleChecker());
			}
			if (StringUtils.hasText(checkForSchemaInRequestBody)) {
				styleCheckers.add(new RequestBodySchemaStyleChecker(checkForSchemaInRequestBody));
			}
			if (StringUtils.hasText(checkForSchemaInSuccessfulResponseBody)) {
				styleCheckers.add(new ResponseBodySchemaStyleChecker(checkForSchemaInSuccessfulResponseBody));
			}
			MultiValueMap<String, HttpStatus> statusChecks = new LinkedMultiValueMap<>();
			if (checkForDefinitionOf404ResponseInGetRequest) {
				statusChecks.add(HttpMethod.GET.name(), HttpStatus.NOT_FOUND);
			}
			if (checkForDefinitionOfErrorCodes) {
				statusChecks.add(HttpMethod.PUT.name(), HttpStatus.BAD_REQUEST);
				statusChecks.add(HttpMethod.POST.name(), HttpStatus.BAD_REQUEST);
				statusChecks.add(HttpMethod.PATCH.name(), HttpStatus.BAD_REQUEST);
				
				statusChecks.add(HttpMethod.GET.name(), HttpStatus.INTERNAL_SERVER_ERROR);
				statusChecks.add(HttpMethod.PATCH.name(), HttpStatus.INTERNAL_SERVER_ERROR);
				statusChecks.add(HttpMethod.PUT.name(), HttpStatus.INTERNAL_SERVER_ERROR);
				statusChecks.add(HttpMethod.POST.name(), HttpStatus.INTERNAL_SERVER_ERROR);
				statusChecks.add(HttpMethod.DELETE.name(), HttpStatus.INTERNAL_SERVER_ERROR);				
			}
			
			if (!statusChecks.isEmpty()) {
				styleCheckers.add(new ResponseCodeDefinitionStyleChecker(statusChecks));
			}
		}
		
		
		RamlRoot loadRamlFromFile = RamlLoader.loadRamlFromFile(ramlToVerifyPath);
		
		RamlVerifier verifier = new RamlVerifier(loadRamlFromFile, implementedRaml, checkers, actionCheckers, resourceCheckers, styleCheckers, StringUtils.hasText(uriPrefixToIgnore) ? uriPrefixToIgnore : null);
		if (verifier.hasWarnings() && logWarnings) {
				for (Issue issue : verifier.getWarnings()) {
					this.getLog().warn(issue.toString());
				}
			}
		if (verifier.hasErrors()) {
			if (logErrors) {
				for (Issue issue : verifier.getErrors()) {
					this.getLog().error(issue.toString());
				}
			}
			throw new IllegalStateException("Errors found when comparing RAML to Spring MVC Implementation");
		}
		if(verifier.hasWarnings() && breakBuildOnWarnings) {
			throw new IllegalStateException("Warnings found when comparing RAML to Spring MVC Implementation and build is set to break on Warnings");
		}
	}
	
	public void execute() throws MojoExecutionException, MojoFailureException {
		long startTime = System.currentTimeMillis();
		if (project.getPackaging().equals("pom")) {
			this.getLog().info("Skipping [pom] project: " + project.getName());

		} else if (project.getPackaging().equals("maven-plugin")) {
			this.getLog().info("Skipping [maven-plugin] project: " + project.getName());

		} else if (!Files.isDirectory(Paths.get(project.getBuild().getSourceDirectory()), LinkOption.NOFOLLOW_LINKS)) {
			this.getLog().info("Skipping project with missing src folder: " + project.getName());

		} else {
			try {
				verifyRaml();
			} catch (IOException e) {
				ClassLoaderUtils.restoreOriginalClassLoader();
				throw new MojoExecutionException(e, "Unexpected exception while executing Raml Sync Plugin.",
						e.toString());
			} catch (InvalidRamlResourceException e) {
	            throw new MojoExecutionException(e, "Supplied RAML has failed validation and cannot be loaded.",
	                     e.toString());
			}

		}
		this.getLog().info("Raml Verification Complete in:" + (System.currentTimeMillis() - startTime) + "ms");
	}

}

