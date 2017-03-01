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
package com.phoenixnap.oss.ramlapisync.javadoc;

import java.io.File;
import java.io.FileInputStream;
import java.util.LinkedHashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.phoenixnap.oss.ramlapisync.parser.FileSearcher;

/**
 * Class containing a class-keyed cache and file search for JavaDoc operations
 * 
 * TODO allow user to specify the package structures to search in
 * 
 * @author Kurt Paris
 * @since 0.0.1
 *
 */
public class JavaDocExtractor {

	protected static final Logger logger = LoggerFactory.getLogger(JavaDocExtractor.class);

	protected File baseDir = null;

	/**
	 * Cache of JavaDocStores for each class we visit.
	 */
	protected Map<Class<?>, JavaDocStore> javaDocCache = new LinkedHashMap<Class<?>, JavaDocStore>();

	public JavaDocExtractor(File baseDir) {
		if (baseDir != null && (baseDir.exists() == false || !baseDir.isDirectory())) {
			baseDir = null;
			throw new IllegalStateException("Base Directory Does not exist or is not a directory");
		}
		this.baseDir = baseDir;
	}

	/**
	 * Extracts the Java Doc for a specific class from its Source code as well as any superclasses or implemented
	 * interfaces.
	 * 
	 * @param clazz The Class for which to get javadoc
	 * @return A parsed documentation store with the class's Javadoc or empty if none is found.
	 */
	public JavaDocStore getJavaDoc(Class<?> clazz) {

		if (clazz.isArray()) {
			clazz = clazz.getComponentType();
		}
		// we need to start off from some base directory
		if (baseDir == null || clazz.isPrimitive()) {
			return new JavaDocStore();
		}

		String classPackage = clazz.getPackage() == null ? "" : clazz.getPackage().getName();
		// lets eliminate some stardard stuff
		if (classPackage.startsWith("java")
				|| (classPackage.startsWith("org") && (classPackage.startsWith("org.hibernate")
						|| classPackage.startsWith("org.raml") || classPackage.startsWith("org.springframework")))) {
			return new JavaDocStore();
		}

		if (javaDocCache.containsKey(clazz)) {
			return javaDocCache.get(clazz);
		}
		logger.debug("Getting Javadoc for: " + clazz.getSimpleName());
		JavaDocStore javaDoc = new JavaDocStore();

		try {

			File file = FileSearcher.fileSearch(baseDir, clazz);

			if (file != null) {
				logger.debug("Found: " + file.getAbsolutePath());
				FileInputStream in = new FileInputStream(file);

				CompilationUnit cu;
				try {
					// parse the file
					cu = JavaParser.parse(in);
				} finally {
					in.close();
				}
				// visit and print the class docs names
				new ClassVisitor(javaDoc).visit(cu, null);
				// visit and print the methods names
				new MethodVisitor(javaDoc).visit(cu, null);
				// visit and print the field names
				new FieldVisitor(javaDoc).visit(cu, null);
			} else {
				logger.warn("*** WARNING: Missing Source for: " + clazz.getSimpleName() + ". JavaDoc Unavailable.");
			}
		} catch (Exception ex) {
			logger.error(ex.getMessage(), ex);
		}
		// After we complete this class, we need to check its parents and interfaces to extract as much documentation as
		// possible
		if (clazz.getInterfaces() != null && clazz.getInterfaces().length > 0) {
			for (Class<?> interfaceClass : clazz.getInterfaces()) {
				javaDoc.merge(getJavaDoc(interfaceClass));
			}
		}
		if (clazz.getSuperclass() != null && !clazz.getSuperclass().equals(Object.class)) {
			javaDoc.merge(getJavaDoc(clazz.getSuperclass()));
		}

		javaDocCache.put(clazz, javaDoc);
		return javaDoc;
	}

}
