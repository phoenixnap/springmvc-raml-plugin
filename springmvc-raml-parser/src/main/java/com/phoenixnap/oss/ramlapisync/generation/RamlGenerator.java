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
package com.phoenixnap.oss.ramlapisync.generation;

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

import org.raml.emitter.RamlEmitter;
import org.raml.model.DocumentationItem;
import org.raml.model.Raml;
import org.raml.model.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.phoenixnap.oss.ramlapisync.data.ApiDocumentMetadata;
import com.phoenixnap.oss.ramlapisync.parser.ResourceParser;

/**
 * Class containing RAML generation driver. Methods in this class are used to orchestrate the process of extracting
 * information from classes, generating RAML and storing this as a String or in a File.
 * 
 * @author Kurt Paris
 * @since 0.0.1
 *
 */
public class RamlGenerator {

	/**
	 * Class Logger
	 */
	protected static final Logger logger = LoggerFactory.getLogger(RamlGenerator.class);

	/**
	 * Raml Model
	 */
	private Raml raml;

	/**
	 * Code Scanner
	 */
	private ResourceParser scanner;

	public RamlGenerator(ResourceParser scanner) {
		this.scanner = scanner;
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
		Raml raml = new Raml();

		raml.setBaseUri(baseUri);
		raml.setVersion(version);
		raml.setTitle(title);

		if (documents != null && documents.size() > 0) {
			raml.setDocumentation(generateDocuments(documents));
		}

		logger.info("Generating Raml for " + title + " v" + version + " from " + classesToGenerate.length
				+ " annotated classes");
		Arrays.asList(classesToGenerate).forEach(item -> {
			Resource resource = scanner.extractResourceInfo(item);
			if (resource.getRelativeUri().equals("/")) { // root should never be added directly
					for (Resource cResource : resource.getResources().values()) {
						mergeResources(raml, cResource, false);
					}
				} else {
					mergeResources(raml, resource, true);
				}

			});
		this.raml = raml;
		return this;
	}

	/**
	 * Merges two RAML Resources trees together. This is non-recursive and could currently lose children in lower
	 * levels.
	 * @param raml
	 * @param resource
	 * @param addActions
	 */
	private void mergeResources(Raml raml, Resource resource, boolean addActions) {
		Resource existingResource = raml.getResource(resource.getRelativeUri());
		if (existingResource == null) {
			raml.getResources().put(resource.getRelativeUri(), resource);
		} else {
			// TODO implement a better merge.
			existingResource.getResources().putAll(resource.getResources());
			if (addActions) {
				existingResource.getActions().putAll(resource.getActions());
			}
		}
	}

	/**
	 * Parses the list of document models and adds them as RAML documentItem nodes in the RAML document.
	
	 * @param documents A set of metadata identifying the documents to be included
	 * @return A List of RAML document items containing the supplied documents
	 */
	protected List<DocumentationItem> generateDocuments(Set<ApiDocumentMetadata> documents) {
		List<DocumentationItem> documentInfos = new ArrayList<>();
		for (ApiDocumentMetadata documentInfo : documents) {
			logger.info("Adding document: " + documentInfo.getDocumentTitle());
			DocumentationItem documentItem = new DocumentationItem();

			documentItem.setContent("!include " + documentInfo.getDocumentPath());
			documentItem.setTitle(documentInfo.getDocumentTitle());
			documentInfos.add(documentItem);
		}
		return documentInfos;
	}

	private static final Pattern INCLUDE_FIXER_PATTERN = Pattern.compile("\"(\\!include [^\"]*)\"");

	/**
	 * Raml post-processor that will be run before the final RAML string is output to overcome a limitation due to
	 * character escaping.
	 * 
	 * @param preRaml The raw RAML as a string before processing has been applied
	 * @return  The RAML document as a String after post processing has been applied
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
		RamlEmitter ramlEmitter = new RamlEmitter();
		return postProcessRaml(ramlEmitter.dump(this.raml));
	}

	/**
	 * Emits the RAML model into its string representation and saves it as a file in the specified path
	 * 
	 * @param path The path to which the RAML document will be saved
	 * @return A file handle to the created document file
	 * @throws FileNotFoundException if the supplied path does not exist
	 */
	public File outputRamlToFile(String path) throws FileNotFoundException {
		if (this.raml == null) {
			return null;
		}
		FileOutputStream fos = null;
		File file = new File(path);
		
		try {
			
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
	 * Returns the RAML Model
	 * 
	 * @return the RAML model
	 */
	public Raml getRaml() {
		return raml;
	}

}
