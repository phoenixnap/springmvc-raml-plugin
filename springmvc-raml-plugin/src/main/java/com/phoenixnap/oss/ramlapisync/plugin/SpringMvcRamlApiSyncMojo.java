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

import java.io.File;
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
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.phoenixnap.oss.ramlapisync.generation.RamlGenerator;
import com.phoenixnap.oss.ramlapisync.parser.ResourceParser;
import com.phoenixnap.oss.ramlapisync.parser.SpringMvcResourceParser;

/**
 * Maven Plugin MOJO specific to Spring MVC Projects.
 * 
 * @author Kurt Paris
 * @author Micheal Schembri Wismayer
 * @since 0.0.1
 */
@Mojo(name = "generate-springmvc-api-docs", requiresDependencyResolution = ResolutionScope.COMPILE_PLUS_RUNTIME, threadSafe = true)
public class SpringMvcRamlApiSyncMojo extends CommonApiSyncMojo {

	/**
	 * Relative file path where the RAML document will be saved to
	 */
	@Parameter(required = true, readonly = true)
	protected String outputRamlFilePath;

	/**
	 * If this is set to true, we will create the RAML file and directories if they don't exist
	 */
	@Parameter(required = false, readonly = true, defaultValue = "false")
	protected Boolean createPathIfMissing;

	/**
	 * If this is set to true, we will empty the output directory before generation occurs
	 */
	@Parameter(required = false, readonly = true, defaultValue = "false")
	private boolean removeOldOutput;
	
	/**
	 * If this is set to true, we will append the default media type to the global raml mediaType property
	 */
	@Parameter(required = false, readonly = true, defaultValue = "false")
	private boolean includeGlobalMediaType;

	/**
	 * Base URL relative to the generated RAML file for the APIs to be accessed at runtime
	 */
	@Parameter(required = true, readonly = true)
	protected String restBasePath;

	/**
	 * IF this is set to true, we will only parse methods that consume, produce or accept the requested defaultMediaType
	 */
	@Parameter(required = false, readonly = true, defaultValue = "false")
	protected Boolean restrictOnMediaType;

	@SuppressWarnings("unchecked")
	@Override
	protected Class<? extends Annotation>[] getSupportedClassAnnotations() {
		return new Class[] { Controller.class, RestController.class, RequestMapping.class };
	}

	protected void generateRaml() throws MojoExecutionException, MojoFailureException, IOException {

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
				
		
		ResourceParser scanner = new SpringMvcResourceParser(targetPath, version, defaultMediaType, restrictOnMediaType);
		RamlGenerator ramlGenerator = new RamlGenerator(scanner);
		// Process the classes selected and build Raml model
		ramlGenerator
				.generateRamlForClasses(project.getArtifactId(), version, restBasePath, classArray, this.documents);
		
		//Add a global media type
		if (includeGlobalMediaType) {
			ramlGenerator.setRamlMediaType(defaultMediaType);
		}

		// Extract RAML as a string and save to file
		ramlGenerator.outputRamlToFile(this.getFullRamlOutputPath(), createPathIfMissing, removeOldOutput);
	}

	/**
	 * Converts the relative path to the absolute path
	 * @return The absolute path for the Raml file to be saved
	 */
	public String getFullRamlOutputPath() {
		// must get basedir from project to ensure that correct basedir is used when building from parent
		return project.getBasedir() + this.outputRamlFilePath;
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
				generateRaml();
			} catch (IOException e) {
				ClassLoaderUtils.restoreOriginalClassLoader();
				throw new MojoExecutionException(e, "Unexpected exception while executing Raml Sync Plugin.",
						e.toString());
			}
		}
		this.getLog().info("Raml Generation Complete in:" + (System.currentTimeMillis() - startTime) + "ms");
	}
	

}
