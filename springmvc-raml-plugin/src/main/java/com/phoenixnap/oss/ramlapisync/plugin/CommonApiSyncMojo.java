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
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.ArrayUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import com.phoenixnap.oss.ramlapisync.data.ApiDocumentMetadata;
import com.google.common.base.Strings;
import com.google.common.reflect.ClassPath;

/**
 * Common Functionality between Spring MVC and Other RAML Syncronizers
 * 
 * @author Kurt Paris
 * @since 0.0.1
 *
 */
public abstract class CommonApiSyncMojo extends AbstractMojo {

	/**
	 * Maven project - required for class scanning
	 */
	@Parameter(defaultValue = "${project}", required = true, readonly = true)
	protected MavenProject project;

	/**
	 * Version of the API being represented in this generation
	 */
	@Parameter(required = false, readonly = true, defaultValue = "1")
	protected String version;

	/**
	 * Default media Type to be used in returns/consumes where these are not specified in the code
	 */
	@Parameter(required = false, readonly = true, defaultValue = "application/json")
	protected String defaultMediaType;

	/**
	 * Relative file path where the RAML document will be saved to
	 */
	@Parameter(required = true, readonly = true)
	protected String outputRamlFilePath;

	/**
	 * Base URL relative to the generated RAML file for the APIs to be accessed at runtime
	 */
	@Parameter(required = true, readonly = true)
	protected String restBasePath;

	/**
	 * TODO filter that allows the plugin to ignore packages other than the ones included
	 */
	@Parameter(readonly = true)
	protected String[] exposedPackages = ArrayUtils.EMPTY_STRING_ARRAY;

	/**
	 * The file extension that will be used to determine files that should be included as documents and linked to the
	 * generated RAML file
	 */
	@Parameter(required = false, readonly = true, defaultValue = DEFAULT_RESOURCE_DOC_SUFFIX)
	protected String documentationSuffix;

	private static final String DEFAULT_RESOURCE_DOC_SUFFIX = "-doc.md";

	/**
	 * Holder for classes matching the annotations which identify them as resources
	 */
	protected List<Class<?>> annotatedClasses = new ArrayList<>();

	/**
	 * Holder for documents matching the suffix
	 */
	protected Set<ApiDocumentMetadata> documents = new LinkedHashSet<>();

	/**
	 * The annotations which identify a class as an API Resource. These could change based on the technology being
	 * parsed
	 */
	protected Class<? extends Annotation>[] supportedClassAnnotations = getSupportedClassAnnotations();

	/**
	 * Main entrypoint for raml generation
	 * @throws MojoExecutionException
	 * @throws MojoFailureException
	 * @throws IOException
	 */
	protected void generateRaml() throws MojoExecutionException, MojoFailureException, IOException {
		ClassLoaderUtils.addLocationsToClassLoader(project);
		List<String> targetPacks = ClassLoaderUtils.loadPackages(project);
		List<String> targetClasses = ClassLoaderUtils.loadClasses(project);

		ClassPath classPath = ClassPath.from(Thread.currentThread().getContextClassLoader());
		for (String pack : targetPacks) {
			scanPack(pack, targetClasses, classPath);
		}

		for (ClassPath.ResourceInfo resourceInfo : classPath.getResources()) {
			if (resourceInfo.getResourceName().endsWith(documentationSuffix)) {
				try {
					documents.add(new ApiDocumentMetadata(resourceInfo, documentationSuffix));
					this.getLog().info("Adding Documentation File " + resourceInfo.getResourceName());
				} catch (Throwable ex) {
					this.getLog().warn("Skipping Resource: Unable to load" + resourceInfo.getResourceName(), ex);
				}
			}
		}

		ClassLoaderUtils.restoreOriginalClassLoader();
	}

	protected void scanPack(String pack, List<String> targetClasses, ClassPath classPath)
			throws MojoExecutionException, IOException {
		this.getLog().info("Scanning package " + pack);

		if (Strings.isNullOrEmpty(pack)) {
			ClassLoaderUtils.restoreOriginalClassLoader();
			throw new MojoExecutionException("Invalid target package: " + pack);
		}

		for (ClassPath.ClassInfo classInfo : classPath.getTopLevelClasses(pack)) {

			try {
				Class<?> c = classInfo.load();
				if (targetClasses.contains(c.getSimpleName())) {
					scanClass(c);
				}
			} catch (Throwable ex) {
				this.getLog().warn("Skipping Class: Unable to load" + classInfo.getName(), ex);
			}
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
				generateRaml();
			} catch (IOException e) {
				ClassLoaderUtils.restoreOriginalClassLoader();
				throw new MojoExecutionException(e, "Unexpected exception while executing security enforcer.",
						e.toString());
			}
		}
		this.getLog().info("Raml Generation Complete in:" + (System.currentTimeMillis() - startTime) + "ms");
	}

	@SuppressWarnings("unchecked")
	protected Class<? extends Annotation>[] getSupportedClassAnnotations() {
		return new Class[0];
	}

	/**
	 * Converts the relative path to the absolute path
	 * @return
	 */
	public String getFullRamlOutputPath() {
		// must get basedir from project to ensure that correct basedir is used when building from parent
		return project.getBasedir() + this.outputRamlFilePath;
	}

	/**
	 * Checks if a class has at least one of the required annotations for mapping
	 * @param c
	 */
	protected final void scanClass(Class<?> c) {
		for (Class<? extends Annotation> cAnnotation : supportedClassAnnotations) {
			if (c.isAnnotationPresent(cAnnotation)) {
				annotatedClasses.add(c);
			}
		}
	}

}
