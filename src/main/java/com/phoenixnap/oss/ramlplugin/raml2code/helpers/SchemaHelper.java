/*
 * Copyright 2002-2017 the original author or authors.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with
 * the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See
 * the License for the
 * specific language governing permissions and limitations under the License.
 */
package com.phoenixnap.oss.ramlplugin.raml2code.helpers;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URI;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.jsonschema2pojo.Annotator;
import org.jsonschema2pojo.DefaultGenerationConfig;
import org.jsonschema2pojo.GenerationConfig;
import org.jsonschema2pojo.Jackson2Annotator;
import org.jsonschema2pojo.SchemaGenerator;
import org.jsonschema2pojo.SchemaMapper;
import org.jsonschema2pojo.SchemaStore;
import org.jsonschema2pojo.rules.RuleFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.phoenixnap.oss.ramlplugin.raml2code.data.ApiBodyMetadata;
import com.phoenixnap.oss.ramlplugin.raml2code.plugin.Config;
import com.phoenixnap.oss.ramlplugin.raml2code.raml.RamlParamType;
import com.phoenixnap.oss.ramlplugin.raml2code.raml.RamlRoot;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JPackage;

/**
 * Class containing convenience methods relating to the extracting of
 * information from Java types for use as Parameters. These can either be
 * decomposed into RAML Simple Types (Similar to Java primitives) or JSON Schema
 * for more complex objects
 *
 * @author Kurt Paris
 * @since 0.0.1
 *
 */
public class SchemaHelper {

	protected static final Logger logger = LoggerFactory.getLogger(SchemaHelper.class);

	/**
	 * Utility method that will return a schema if the identifier is valid and
	 * exists in the raml file definition.
	 *
	 * @param schema
	 *            The name of the schema to resolve
	 * @param document
	 *            The Parent Raml Document
	 * @return The full schema if contained in the raml document or null if not
	 *         found
	 */
	public static String resolveSchema(String schema, RamlRoot document) {
		if (document == null || schema == null || schema.indexOf("{") != -1) {
			return null;
		}
		if (document.getSchemas() != null && !document.getSchemas().isEmpty()) {
			for (Map<String, String> map : document.getSchemas()) {
				if (map.containsKey(schema)) {
					return map.get(schema);
				}
			}
		}
		return null;
	}

	/**
	 * Maps simple types supported by RAML into primitives and other simple Java
	 * types
	 *
	 * @param param
	 *            The Type to map
	 * @param format
	 *            Number format specified
	 * @param rawType
	 *            RAML type
	 * @return The Java Class which maps to this Simple RAML ParamType or string
	 *         if one is not found
	 */
	public static Class<?> mapSimpleType(RamlParamType param, String format, String rawType) {

		switch (param) {
			case BOOLEAN:
				return Boolean.class;
			case DATE:
				return mapDateFormat(rawType);
			case INTEGER: {
				Class<?> fromFormat = mapNumberFromFormat(format);
				if (fromFormat == Double.class) {
					throw new IllegalStateException();
				}
				if (fromFormat == null) {
					return Long.class; // retained for backward compatibility
				} else {
					return fromFormat;
				}
			}
			case NUMBER: {
				Class<?> fromFormat = mapNumberFromFormat(format);
				if (fromFormat == null) {
					return BigDecimal.class; // retained for backward
												// compatibility
				} else {
					return fromFormat;
				}
			}
			case FILE:
				return MultipartFile.class;
			default:
				return String.class;
		}

	}

	public static Class<?> mapDateFormat(String rawType) {

		String param = rawType.toUpperCase();
		try {
			switch (param) {
				case "DATE-ONLY":
					String dateType = Config.getPojoConfig().getDateType();
					if (StringUtils.hasText(dateType)) {
						return Class.forName(dateType);
					}
					break;
				case "TIME-ONLY":
					String timeType = Config.getPojoConfig().getTimeType();
					if (StringUtils.hasText(timeType)) {
						return Class.forName(timeType);
					}
					break;
				default:
					String dateTimeType = Config.getPojoConfig().getDateTimeType();
					if (StringUtils.hasText(dateTimeType)) {
						return Class.forName(dateTimeType);
					}
			}
		} catch (ClassNotFoundException e) {
			logger.error("Error trying to find class for date type: " + rawType);
		}
		return Date.class;
	}

	private static Class<?> mapNumberFromFormat(String format) {
		if (format == null) {
			return null;
		}
		if (format.equals("int64") || format.equals("long")) {
			return Long.class;
		} else if (format.equals("int32") || format.equals("int")) {
			return Integer.class;
		} else if (format.equals("int16") || format.equals("int8")) {
			return Short.class;
		} else if (format.equals("double") || format.equals("float")) {
			return Double.class;
		}
		return null;
	}

	/**
	 * Extracts the name from a schema in this order of precedence: 1. If the
	 * schema contains an ID element 2. The name of the schema within the RAML
	 * document 3. The autogenerated name based on the enclosing method
	 *
	 * @param schema
	 *            The Actual JSON Schema
	 * @param schemaName
	 *            The name of the schema within the document
	 * @param fallbackName
	 *            any arbitrary name
	 * @return The Name for this Class (POJO)
	 */
	public static String extractNameFromSchema(String schema, String schemaName, String fallbackName) {
		String resolvedName = null;
		if (schema != null) {
			// if we have an array type we need to recurse into it
			int startIdx = 0;
			String type = extractTopItem("type", schema, startIdx);
			if (type != null && type.equalsIgnoreCase("array")) {
				int itemsIdx = schema.indexOf("\"items\"");
				if (itemsIdx != -1) {
					startIdx = itemsIdx + 7;
				}
				// lets check if we have a ref
				String ref = extractTopItem("$ref", schema, startIdx);
				if (ref != null) {
					logger.info("Loading referenced schema " + ref);
					ref = ref.replace("classpath:", "");
					try {
						schema = IOUtils.toString(Thread.currentThread().getContextClassLoader().getResourceAsStream(ref), "UTF-8");
						startIdx = 0; // reset pointer since we recursed into
										// schema
					} catch (IOException e) {
						logger.info("Erro Loading referenced schema " + ref, e);
					}
				}
			}
			// check if javaType can give us exact name
			String javaType = extractTopItem("javaType", schema, startIdx);
			if (StringUtils.hasText(javaType)) {
				// do stuff to it
				int dotIdx = javaType.lastIndexOf(".");
				if (dotIdx > -1) {
					javaType = javaType.substring(dotIdx + 1);
				}
				resolvedName = javaType;

			} else {
				String id = extractTopItem("id", schema, startIdx);
				if (StringUtils.hasText(id)) {
					// do stuff to it
					if (id.startsWith("urn:") && ((id.lastIndexOf(":") + 1) < id.length())) {
						id = id.substring(id.lastIndexOf(":") + 1);
					} else if (id.startsWith(JSON_SCHEMA_IDENT)) {
						if (id.length() > (JSON_SCHEMA_IDENT.length() + 3)) {
							id = id.substring(JSON_SCHEMA_IDENT.length());
						}
					}

					resolvedName = StringUtils.capitalize(id);

				}
				if (!NamingHelper.isValidJavaClassName(resolvedName)) {
					if (NamingHelper.isValidJavaClassName(schemaName)) {
						return StringUtils.capitalize(schemaName); // try schema
																	// name
					} else {
						resolvedName = fallbackName; // fallback to generated
					}
				}
			}
		}

		return resolvedName;
	}

	/**
	 * Extracts the value of a specified parameter in a schema
	 * 
	 * @param searchString
	 *            element to search for
	 * @param schema
	 *            Schema as a string
	 * @return the value or null if not found
	 */
	private static String extractTopItem(String searchString, String schema, int startIdx) {
		String extracted = null;
		int propIdx = schema.indexOf("\"properties\"", startIdx);
		if (propIdx == -1) {
			propIdx = Integer.MAX_VALUE;
		}
		// check for second
		int idIdx = schema.indexOf("\"" + searchString + "\"", startIdx);
		int secondIdIdx = schema.indexOf("\"" + searchString + "\"", idIdx + 1);
		if (secondIdIdx != -1 && propIdx > secondIdIdx) {
			idIdx = secondIdIdx;
		}
		if (idIdx != -1 && propIdx > idIdx) { // make sure we're not in a nested
												// id
			// find the 1st and second " after the idx
			int valueStartIdx = schema.indexOf("\"", idIdx + (searchString.length() + 2));
			int valueEndIdx = schema.indexOf("\"", valueStartIdx + 1);
			extracted = schema.substring(valueStartIdx + 1, valueEndIdx);
		}
		return extracted;
	}

	private static String JSON_SCHEMA_IDENT = "http://jsonschema.net";

	/**
	 * Maps a JSON Schema to a JCodeModel using JSONSchema2Pojo and encapsulates
	 * it along with some metadata into an {@link ApiBodyMetadata} object.
	 *
	 * @param document
	 *            The Raml document being parsed
	 * @param schema
	 *            The Schema (full schema or schema name to be resolved)
	 * @param basePackage
	 *            The base package for the classes we are generating
	 * @param name
	 *            The suggested name of the class based on the api call and
	 *            whether it's a request/response. This will only be used if no
	 *            suitable alternative is found in the schema
	 * @param schemaLocation
	 *            Base location of this schema, will be used to create absolute
	 *            URIs for $ref tags eg "classpath:/"
	 * @return Object representing this Body
	 */
	public static ApiBodyMetadata mapSchemaToPojo(RamlRoot document, String schema, String basePackage, String name,
			String schemaLocation) {
		String resolvedName = null;
		String schemaName = schema;

		// Check if we have the name of a schema or an actual schema
		String resolvedSchema = SchemaHelper.resolveSchema(schema, document);
		if (resolvedSchema == null) {
			resolvedSchema = schema;
			schemaName = null;
		}

		// Extract name from schema
		resolvedName = extractNameFromSchema(resolvedSchema, schemaName, name);
		JCodeModel codeModel = buildBodyJCodeModel(basePackage, StringUtils.hasText(schemaLocation) ? schemaLocation : "classpath:/",
				resolvedName, resolvedSchema, null);
		if (codeModel != null) {
			if (codeModel.countArtifacts() == 1) {
				try {
					// checking has next twice might be more efficient but this
					// is more readable, if
					// we ever run into speed issues here..optimise
					Iterator<JPackage> packages = codeModel.packages();

					// in the case that we have empty packages we need to skip
					// them to get to the
					// class
					JPackage nextPackage = packages.next();
					while (!nextPackage.classes().hasNext() && packages.hasNext()) {
						nextPackage = packages.next();
					}
					resolvedName = nextPackage.classes().next().name();
				} catch (NullPointerException npe) {
					// do nothing, use previous name
				}
			}
			return new ApiBodyMetadata(resolvedName, resolvedSchema, codeModel);
		} else {
			return null;
		}
	}

	/**
	 * Builds a JCodeModel for classes that will be used as Request or Response
	 * bodies
	 *
	 * @param basePackage
	 *            The package we will be using for the domain objects
	 * @param schemaLocation
	 *            The location of this schema, will be used to create absolute
	 *            URIs for $ref tags eg "classpath:/"
	 * @param name
	 *            The class name
	 * @param schema
	 *            The JSON Schema representing this class
	 * @param annotator
	 *            JsonSchema2Pojo annotator. if null a default annotator will be
	 *            used
	 * @return built JCodeModel
	 */
	public static JCodeModel buildBodyJCodeModel(String basePackage, String schemaLocation, String name, String schema,
			Annotator annotator) {
		JCodeModel codeModel = new JCodeModel();
		SchemaStore schemaStore = new SchemaStore();

		GenerationConfig config = Config.getPojoConfig();
		if (config == null) {
			config = getDefaultGenerationConfig();
		}
		if (annotator == null) {
			annotator = new Jackson2Annotator(config);
		}
		RuleFactory ruleFactory = new RuleFactory(config, annotator, schemaStore);

		SchemaMapper mapper = new SchemaMapper(ruleFactory, new SchemaGenerator());
		boolean useParent = StringUtils.hasText(schemaLocation);
		try {
			if (useParent) {
				mapper.generate(codeModel, name, basePackage, schema, new URI(schemaLocation));
			} else {
				mapper.generate(codeModel, name, basePackage, schema);
			}

		} catch (Exception e) {
			// TODO make this smarter by checking refs
			if (useParent && e.getMessage().contains("classpath")) {
				logger.debug("Referenced Schema contains self $refs or not found in classpath. Regenerating model withouth classpath: for "
						+ name);
				codeModel = new JCodeModel();
				try {
					mapper.generate(codeModel, name, basePackage, schema);
					return codeModel;
				} catch (IOException e1) {
					// do nothing
				}
			}
			logger.error("Error generating pojo from schema" + name, e);
			return null;
		}
		return codeModel;
	}

	/**
	 * Returns a configuration for the JSON Schema 2 POJO that is in line with
	 * the defaults used in the plugin so far
	 *
	 * @return Default Generation Config
	 */
	public static GenerationConfig getDefaultGenerationConfig() {
		return getGenerationConfig(true, false, false, false);
	}

	/**
	 * Returns a generation config with the supplied parameters. If any of these
	 * parameters are supplied null, it will use the value defined in the
	 * default configuration
	 *
	 * @param generateBuilders
	 *            Enables or disables
	 *            {@link GenerationConfig#isGenerateBuilders()}
	 * @param includeAdditionalProperties
	 *            Enables or disables
	 *            {@link GenerationConfig#isIncludeAdditionalProperties()}
	 * @param includeDynamicAccessors
	 *            Enables or disables
	 *            {@link GenerationConfig#isIncludeDynamicAccessors()}
	 * @param useLongIntegers
	 *            Enables or disables
	 *            {@link GenerationConfig#isUseLongIntegers()}
	 * @return The GenerationConfig
	 */
	public static GenerationConfig getGenerationConfig(Boolean generateBuilders, Boolean includeAdditionalProperties,
			Boolean includeDynamicAccessors, Boolean useLongIntegers) {
		return new DefaultGenerationConfig() {

			@Override
			public boolean isGenerateBuilders() { // set config option by
													// overriding method
				if (generateBuilders != null) {
					return generateBuilders;
				} else {
					return true;
				}
			}

			@Override
			public boolean isIncludeAdditionalProperties() {
				if (includeAdditionalProperties != null) {
					return includeAdditionalProperties;
				} else {
					return false;
				}
			}

			@Override
			public boolean isIncludeDynamicAccessors() {
				if (includeDynamicAccessors != null) {
					return includeDynamicAccessors;
				} else {
					return false;
				}
			}

			@Override
			public boolean isUseLongIntegers() {
				if (useLongIntegers != null) {
					return useLongIntegers;
				} else {
					return super.isUseLongIntegers();
				}
			}
		};
	}
}
