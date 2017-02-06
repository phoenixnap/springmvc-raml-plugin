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
package com.phoenixnap.oss.ramlapisync.parser;

import java.io.File;
import java.io.FilenameFilter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class containing fast search functions used to find the Source Code of a class being scanned
 * 
 * @author Kurt Paris
 * @since 0.0.1
 *
 */
public class FileSearcher {

	protected static final Logger logger = LoggerFactory.getLogger(FileSearcher.class);

	/**
	 * Simple java file search method. Speeds up by reducing search space
	 * 
	 * @param file The File representing the directory/file to start searching from 
	 * @param clazz The Target class we are looking for
	 * @return The file representing the file or null if not found
	 */
	public static File fileSearch(File file, Class<?> clazz) {
		logger.debug("Searching in: " + file == null ? null : file.getAbsolutePath());
		FilenameFilter filter = new FilenameFilter() {

			public boolean accept(File dir, String name) {
				return name.equals(clazz.getSimpleName() + ".java");
			}
		};
		File[] dirContents = file.listFiles(filter);
		if (dirContents != null && dirContents.length == 1) {
			return dirContents[0];
		} else {
			File[] fileList = file.listFiles();
			if (fileList != null) {
				for (File recurseFile : fileList) {
					if (recurseFile.isDirectory()) {
						File foundFile;
						// lookahead for actual file
						if (recurseFile.getName().equals("java") && recurseFile.getParent().endsWith("\\main")) {
							foundFile = new File(recurseFile.getParent() + "\\" + recurseFile.getName() + "\\"
									+ clazz.getPackage().getName().replace(".", "\\") + "\\" + clazz.getSimpleName()
									+ ".java");
							if (foundFile != null && foundFile.exists()) {
								return foundFile;
							} else {
								continue;
							}
						}
						if (!(recurseFile.getName().contains("src\\main\\java"))
								&& (recurseFile.getName().contains("static") || recurseFile.getName().contains("bower")
										|| recurseFile.getName().contains("security-enforcer")
										|| recurseFile.getName().contains("webapp")
										|| recurseFile.getName().contains("documentation")
										|| recurseFile.getName().contains("gulp") || recurseFile.getName().contains(
										"node"))) {
							continue;
						}
						if (recurseFile.getName().startsWith(".")) {
							continue;
						}
						// lets kill some cases. - this will remove main/resources or main/test
						if (recurseFile.getParent().endsWith("\\main") && !recurseFile.getName().equals("java")) {
							continue;
						}
						// lets kill some cases.
						if (recurseFile.getName().equals("test") && recurseFile.getParent().endsWith("\\src")) {
							continue;
						}
						// lets kill some cases. removes resources and target scanning
						if ((recurseFile.getName().equals("resources") || recurseFile.getName().equals("target"))
								&& !recurseFile.getAbsolutePath().contains("src\\main\\java")) {
							continue;
						}
						foundFile = fileSearch(recurseFile, clazz);
						if (foundFile != null) {
							return foundFile;
						}
					}
				}
			}
		}
		return null;

	}

}
