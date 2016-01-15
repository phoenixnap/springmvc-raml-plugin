/*
 * Copyright 2002-2016 the original author or authors.
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import com.google.common.base.Strings;
import com.google.common.reflect.ClassPath;
import com.phoenixnap.oss.ramlapisync.data.ApiDocumentMetadata;

/**
 * Common Functionality between Spring MVC and Other RAML Synchronizers
 * 
 * @author Kurt Paris
 * @since 0.0.1
 *
 */
public abstract class CommonApiSyncMojo extends AbstractMojo {

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
	 * Maven project - required for class scanning
	 */
	@Parameter(defaultValue = "${project}", required = true, readonly = true)
	protected MavenProject project;
	
	/**
	 * Path to the raml document to be verified
	 */
	@Parameter(required = false, readonly = true, defaultValue = "")
	protected String javaDocPath;

	/**
	 * Holder for documents matching the suffix
	 */
	protected Set<ApiDocumentMetadata> documents = new LinkedHashSet<>();
	
	private static final String DEFAULT_RESOURCE_DOC_SUFFIX = "-doc.md";
	
	/**
	 * The file extension that will be used to determine files that should be included as documents and linked to the
	 * generated RAML file
	 */
	@Parameter(required = false, readonly = true, defaultValue = DEFAULT_RESOURCE_DOC_SUFFIX)
	protected String documentationSuffix;
	
	/**
	 * Filter that allows the plugin to ignore packages or classes in the list.
	 */
	@Parameter(readonly = true)
	protected List<String> ignoredList = Collections.emptyList();
	
	/**
	 * Filter that allows the plugin to scan packages in dependencies and add them to RAML scans
	 */
	@Parameter(readonly = true)
	protected List<String> dependencyPackagesList = Collections.emptyList();
	
	/**
	 * Holder for classes matching the annotations which identify them as resources
	 */
	protected List<Class<?>> annotatedClasses = new ArrayList<>();

	
	/**
	 * The annotations which identify a class as an API Resource. These could change based on the technology being
	 * parsed
	 */
	protected Class<? extends Annotation>[] supportedClassAnnotations = getSupportedClassAnnotations();

	/**
	 * Main entrypoint for raml generation
	 * @throws MojoExecutionException Kaboom.
	 * @throws MojoFailureException Kaboom.
	 * @throws IOException Kaboom.
	 */
	protected void prepareRaml() throws MojoExecutionException, MojoFailureException, IOException {
		ClassLoaderUtils.addLocationsToClassLoader(project);
		List<String> targetPacks = ClassLoaderUtils.loadPackages(project);
		if (dependencyPackagesList != null && !dependencyPackagesList.isEmpty()) {
			targetPacks.addAll(dependencyPackagesList);
		}

		ClassPath classPath = ClassPath.from(Thread.currentThread().getContextClassLoader());
		for (String pack : targetPacks) {
			scanPack(pack, classPath);
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
	
	/**
	 * Checks if a class has at least one of the required annotations for mapping
	 * @param c The class to be scanned
	 */
	protected final void scanClass(Class<?> c) {
		for (Class<? extends Annotation> cAnnotation : supportedClassAnnotations) {
			if (c.isAnnotationPresent(cAnnotation)) {
				annotatedClasses.add(c);
			}
		}
	}

	protected void scanPack(String pack, ClassPath classPath)
			throws MojoExecutionException, IOException {
		this.getLog().info("Scanning package " + pack);

		if (Strings.isNullOrEmpty(pack)) {
			ClassLoaderUtils.restoreOriginalClassLoader();
			throw new MojoExecutionException("Invalid target package: " + pack);
		}

		for (ClassPath.ClassInfo classInfo : classPath.getTopLevelClasses(pack)) {
			try {
				Class<?> c = classInfo.load();
				
				if (!ignoredList.contains(c.getPackage().getName()) && !ignoredList.contains(c.getName())) {
					scanClass(c);
				}
			} catch (Throwable ex) {
				this.getLog().warn("Skipping Class: Unable to load" + classInfo.getName(), ex);
			}
		}

	}

	@SuppressWarnings("unchecked")
	protected Class<? extends Annotation>[] getSupportedClassAnnotations() {
		return new Class[0];
	}


}
