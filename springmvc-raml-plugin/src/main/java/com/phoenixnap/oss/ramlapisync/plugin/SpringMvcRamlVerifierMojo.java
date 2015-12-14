/*
 * Copyright 2002-2015 the original author or authors.
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

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Paths;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.raml.model.Raml;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RestController;

import com.phoenixnap.oss.ramlapisync.generation.RamlGenerator;
import com.phoenixnap.oss.ramlapisync.generation.RamlVerifier;
import com.phoenixnap.oss.ramlapisync.parser.ResourceParser;
import com.phoenixnap.oss.ramlapisync.parser.SpringMvcResourceParser;
import com.phoenixnap.oss.ramlapisync.verification.Issue;

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
	 * Default media Type to be used in returns/consumes where these are not specified in the code
	 */
	@Parameter(required = true, readonly = true, defaultValue = "application/json")
	protected String ramlToVerifyPath;
	
	/**
	 * TODO
	 */
	@Parameter(required = false, readonly = true, defaultValue = "false")
	protected Boolean breakBuildOnWarnings;
	
	/**
	 * TODO
	 */
	@Parameter(required = false, readonly = true, defaultValue = "true")
	protected Boolean logWarnings;
	
	/**
	 * TODO
	 */
	@Parameter(required = false, readonly = true, defaultValue = "true")
	protected Boolean logErrors;
	
	@SuppressWarnings("unchecked")
	@Override
	protected Class<? extends Annotation>[] getSupportedClassAnnotations() {
		return new Class[] { Controller.class, RestController.class };
	}

	protected void verifyRaml() throws MojoExecutionException, MojoFailureException, IOException {

		super.prepareRaml();

		Class<?>[] classArray = new Class[annotatedClasses.size()];
		classArray = this.annotatedClasses.toArray(classArray);
		ResourceParser scanner = new SpringMvcResourceParser(project.getBasedir().getParentFile() != null ? project
				.getBasedir().getParentFile() : project.getBasedir(), version, defaultMediaType, false);
		RamlGenerator ramlGenerator = new RamlGenerator(scanner);
		// Process the classes selected and build Raml model
		ramlGenerator.generateRamlForClasses(project.getArtifactId(), version, "/", classArray, this.documents);
		Raml implementedRaml = ramlGenerator.getRaml();
		
		RamlVerifier verifier = new RamlVerifier(RamlVerifier.loadRamlFromFile(ramlToVerifyPath), implementedRaml, null, null);
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
				throw new MojoExecutionException(e, "Unexpected exception while executing security enforcer.",
						e.toString());
			}
		}
		this.getLog().info("Raml Generation Complete in:" + (System.currentTimeMillis() - startTime) + "ms");
	}

}
