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
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.SuffixFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;

/**
 * Class containing utility methods for loading different sets of classes during Plugin runtime
 * 
 * @author Kurt Paris
 * @since 0.0.1
 *
 */
public final class ClassLoaderUtils {
	
	protected static final Logger logger = LoggerFactory.getLogger(ClassLoaderUtils.class);

	private static ClassLoader originalClassLoader;

	private ClassLoaderUtils() {

	}

	public static void addLocationsToClassLoader(MavenProject mavenProject) throws MojoExecutionException {

		List<URL> urls = Lists.newArrayList();
		try {
			urls.add(new File(mavenProject.getBuild().getOutputDirectory()).toURI().toURL());

			// Getting all artifacts locations
			Set<Artifact> artifacts = mavenProject.getArtifacts();
			
			for (Artifact artifact : artifacts) {
				urls.add(artifact.getFile().toURI().toURL());
			}
			
		} catch (MalformedURLException e) {
			// DO NOTHING
		}

		/*
		 * this was failing when executing goal on a maven project if (originalClassLoader != null) { throw new
		 * MojoExecutionException( "Context setting of the current thread ClassLoader is allowed only once."); }
		 */

		// Store ClassLoader before applying modifications.
		originalClassLoader = Thread.currentThread().getContextClassLoader();

		Thread.currentThread().setContextClassLoader(
				new URLClassLoader(urls.toArray(new URL[urls.size()]), originalClassLoader));

	}

	public static List<String> loadPackages(MavenProject mavenProject) throws MojoExecutionException {

		List<String> packages = Lists.newArrayList();

		logger.info("Loading packages in " + mavenProject.getBuild().getSourceDirectory() + "...");
		File rootDir = new File(mavenProject.getBuild().getSourceDirectory() + "//");
		Collection<File> files = FileUtils
				.listFilesAndDirs(rootDir, DirectoryFileFilter.DIRECTORY, TrueFileFilter.TRUE);
		for (File file : files) {
			String pack = file.toString().replace(rootDir.toString(), "").replace(File.separator, ".");
			if (pack.startsWith(".")) {
				pack = pack.substring(1, pack.length());
			}
			if (!pack.isEmpty()) {
				packages.add(pack);
			}
		}
		
		return packages;
	}

	public static List<String> loadClasses(MavenProject mavenProject) throws MojoExecutionException {

		List<String> classes = Lists.newArrayList();

		File rootDir = new File(mavenProject.getBuild().getSourceDirectory());
		Collection<File> files = FileUtils.listFiles(rootDir, new SuffixFileFilter(".java"), TrueFileFilter.TRUE);
		for (File file : files) {
			String clazz = file.getName().replace(".java", "");
			if (!clazz.isEmpty()) {
				classes.add(clazz);
			}
		}
		return classes;
	}

	public static void restoreOriginalClassLoader() throws MojoExecutionException {

		if (originalClassLoader == null) {
			throw new MojoExecutionException("Original ClassLoader not available.");
		}
		Thread.currentThread().setContextClassLoader(originalClassLoader);
	}

}
