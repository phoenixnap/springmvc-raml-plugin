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
package com.phoenixnap.oss.ramlapisync.generation;

import com.phoenixnap.oss.ramlapisync.data.ApiDocumentMetadata;
import com.phoenixnap.oss.ramlapisync.naming.RamlHelper;
import com.phoenixnap.oss.ramlapisync.parser.ResourceParser;
import com.phoenixnap.oss.ramlapisync.raml.RamlDocumentationItem;
import com.phoenixnap.oss.ramlapisync.raml.RamlModelEmitter;
import com.phoenixnap.oss.ramlapisync.raml.RamlModelFactoryOfFactories;
import com.phoenixnap.oss.ramlapisync.raml.RamlResource;
import com.phoenixnap.oss.ramlapisync.raml.RamlRoot;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Class containing RAML generation driver. Methods in this class are used to orchestrate the process of extracting
 * information from classes, generating RAML and storing this as a String or in a File.
 * 
 * @author Kurt Paris
 * @since 0.0.1
 *
 */
public class RamlGenerator {

	private static final String DEFAULT_RAML_FILENAME = "api.raml";

	private static final String RAML_EXTENSION = ".raml";

	private static final String pathSeparator = System.getProperty("path.separator");

	private static final Pattern INCLUDE_FIXER_PATTERN = Pattern.compile("\"(\\!include [^\"]*)\"");


	/**
	 * Class Logger
	 */
	protected static final Logger logger = LoggerFactory.getLogger(RamlGenerator.class);

	/**
	 * Raml Model
	 */
	private RamlRoot raml;

	/**
	 * Code Scanner
	 */
	private ResourceParser scanner;

	/**
	 * Default constructor. A scanner is not mandatory.
	 */
	public RamlGenerator() {
	}

	/**
	 * @param scanner The resource parsing engine. Only required for RAML generation
	 */
	public RamlGenerator(ResourceParser scanner) {
		this();
		this.scanner = scanner;
	}

	/**
	 * Adds a global media type to the document
	 * 
	 * @param mediaType The default media type
	 */
	public void setRamlMediaType(String mediaType) {
		if (this.raml != null) {
			this.raml.setMediaType(mediaType);
		}
	}

	/**
	 * Parses classes array supplied and builds a Raml Model from any request mappings defined inside
	 * 
	 * @param title Document Title
	 * @param version Version of the Api
	 * @param baseUri Base uri of the api at runtime
	 * @param classesToGenerate List of classes to parse
	 * @param documents A set of documents to be included
	 * @return This object for chaining
	 */
	public RamlGenerator generateRamlForClasses(String title, String version, String baseUri,
			Class<?>[] classesToGenerate, Set<ApiDocumentMetadata> documents) {

		assertResourceParser();

		RamlRoot raml = RamlModelFactoryOfFactories.createRamlModelFactoryV08().createRamlRoot();
		raml.setBaseUri(baseUri);
		raml.setVersion(version);
		raml.setTitle(title);

		if (documents != null && documents.size() > 0) {
			raml.setDocumentation(generateDocuments(documents));
		}

		logger.info("Generating Raml for " + title + " v" + version + " from " + classesToGenerate.length
				+ " annotated classes");
		Arrays.asList(classesToGenerate).forEach(item -> {
			RamlResource resource = scanner.extractResourceInfo(item);
			if (resource.getRelativeUri().equals("/")) { // root should never be added directly
					for (RamlResource cResource : resource.getResources().values()) {
						RamlHelper.mergeResources(raml, cResource, true);
					}
				} else {
					RamlHelper.mergeResources(raml, resource, true);
				}

			});
		this.raml = raml;
		return this;
	}

	/**
	 * for some of the public methods a ResourceParser has to be set.
	 */
	private void assertResourceParser() {
		if(scanner == null) {
			throw new IllegalStateException("Please make sure to setup a ResourceParser before calling this method.");
		}
	}

	

	/**
	 * Parses the list of document models and adds them as RAML documentItem nodes in the RAML document.
	 * 
	 * @param documents A set of metadata identifying the documents to be included
	 * @return A List of RAML document items containing the supplied documents
	 */
	protected List<RamlDocumentationItem> generateDocuments(Set<ApiDocumentMetadata> documents) {
		List<RamlDocumentationItem> documentInfos = new ArrayList<>();
		for (ApiDocumentMetadata documentInfo : documents) {
			logger.info("Adding document: " + documentInfo.getDocumentTitle());
			RamlDocumentationItem documentItem = RamlModelFactoryOfFactories.createRamlModelFactoryV08().createRamlDocumentationItem();

			documentItem.setContent("!include " + documentInfo.getDocumentPath());
			documentItem.setTitle(documentInfo.getDocumentTitle());
			documentInfos.add(documentItem);
		}
		return documentInfos;
	}

	/**
	 * Raml post-processor that will be run before the final RAML string is output to overcome a limitation due to
	 * character escaping.
	 * 
	 * @param preRaml The raw RAML as a string before processing has been applied
	 * @return The RAML document as a String after post processing has been applied
	 */
	protected String postProcessRaml(String preRaml) {
		Matcher fixIncludes = INCLUDE_FIXER_PATTERN.matcher(preRaml);
		while (fixIncludes.find()) {
			try {
				preRaml = preRaml.replace(fixIncludes.group(0), fixIncludes.group(1));
				logger.info("    RAML Post Processor replacing: [" + fixIncludes.group(0) + "] with ["
						+ fixIncludes.group(1) + "]");
			} catch (Exception ex) {
				logger.error("    RAML Post Processor Exception: " + ex.getMessage());
			}
		}
		return preRaml;
	}

	/**
	 * Emits the RAML model into its string representation
	 * 
	 * @return The RAML document as a String
	 */
	public String outputRamlToString() {
		if (this.raml == null) {
			return "";
		}
		RamlModelEmitter ramlEmitter = RamlModelFactoryOfFactories.createRamlModelFactoryV08().createRamlModelEmitter();
		return postProcessRaml(ramlEmitter.dump(this.raml));
	}

	/**
	 * Emits the RAML model into its string representation and saves it as a file in the specified path
	 * 
	 * @param path The path to which the RAML document will be saved
	 * @param createPathIfMissing Indicates if the path and/or file should be created if it doesn't exist
	 * @param removeOldOutput Indicates if we will empty the output directory before generation occurs
	 * @return A file handle to the created document file
	 * @throws FileNotFoundException if the supplied path does not exist
	 */
	public File outputRamlToFile(String path, Boolean createPathIfMissing, Boolean removeOldOutput) throws FileNotFoundException {
		if (this.raml == null) {
			return null;
		}
		FileOutputStream fos = null;
		File file = getRamlOutputFile(path);

		try {
			prepareDirectories(file, createPathIfMissing, removeOldOutput);

			logger.info("Saving generated raml to " + file.getAbsolutePath());
			fos = new FileOutputStream(file);
			fos.write(outputRamlToString().getBytes());
			fos.flush();
		} catch (FileNotFoundException e) {
			logger.error("Could not save raml - directory enclosing " + file.getAbsolutePath() + " does not exist", e);
			throw e;
		} catch (IOException e) {

			logger.error(e.getMessage(), e);
		} finally {
			if (fos != null) {

				try {
					fos.close();
				} catch (IOException e) {
					logger.error(e.getMessage(), e);
				}

			}
		}
		return file;
	}

	/**
	 * Takes the absolute path and gets the Raml file to be created
	 *
	 * @param path The path for the raml file to be saved in
	 * @return raml file to be generated
	 */
	public File getRamlOutputFile(String path) {
		File file = new File(this.preparePath(path));

		file = this.prepareFile(file);

		return file;
	}

	/**
	 * Checks the path and adds default file name if what was entered appears to be a directory
	 *
	 * @return The absolute path for the RAML file to be saved
	 */
	private String preparePath(String path) {
		// If the path ends with a slash or the system path separator, assume it is a directory
		// and append the default filename
		if(path.endsWith("/") || path.endsWith(pathSeparator)) {
			path += DEFAULT_RAML_FILENAME;
		}
		
		return path;
	}

	/**
	 * Make sure the file to be generated has a .raml extension.
	 * Use the default file name if a directory name was specified.
	 *
	 * @param file file to be generated
	 * @return proper raml file
	 */
	private File prepareFile(File file) {
		if(file.isDirectory()) {
			file = new File(file, DEFAULT_RAML_FILENAME);
		} else {
			if(!file.getName().toLowerCase().endsWith(RAML_EXTENSION)) {
				file = new File(file.getAbsolutePath() + RAML_EXTENSION);
			}
		}

		return file;
	}

	/**
	 * Create and clean the directories specified in the file path if requested
	 *
	 * @param file file to be generated
	 * @param createPathIfMissing should missing directories be created
	 * @param removeOldOutput remove any existing contents from destination
     */
	private void prepareDirectories(File file, Boolean createPathIfMissing, Boolean removeOldOutput) {
		File outputDirectory;

		if(file.isDirectory()) {
			outputDirectory = file;
		} else {
			outputDirectory = file.getParentFile();
		}

		if(!outputDirectory.exists() && createPathIfMissing) {
			if(!outputDirectory.mkdirs()) {
				logger.info("Failed to create directory: " + outputDirectory);
			}
		}

		if(removeOldOutput) {
			try
			{
				FileUtils.cleanDirectory(outputDirectory);
			}
			catch (IOException ioe)
			{
				logger.error("Failed to clean directory: " + outputDirectory, ioe);
			}
		}
	}

	/**
	 * Returns the RAML Model
	 * 
	 * @return the RAML model
	 */
	public RamlRoot getRaml() {
		return raml;
	}

}
