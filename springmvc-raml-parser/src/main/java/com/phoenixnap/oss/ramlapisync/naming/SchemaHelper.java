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
package com.phoenixnap.oss.ramlapisync.naming;

import java.lang.reflect.Field;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.Date;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.jsonschema2pojo.Annotator;
import org.jsonschema2pojo.DefaultGenerationConfig;
import org.jsonschema2pojo.GenerationConfig;
import org.jsonschema2pojo.Jackson2Annotator;
import org.jsonschema2pojo.SchemaGenerator;
import org.jsonschema2pojo.SchemaMapper;
import org.jsonschema2pojo.SchemaStore;
import org.jsonschema2pojo.rules.RuleFactory;
import org.raml.model.ParamType;
import org.raml.model.Raml;
import org.raml.model.parameter.QueryParameter;
import org.raml.parser.utils.Inflector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.module.jsonSchema.JsonSchema;
import com.fasterxml.jackson.module.jsonSchema.factories.SchemaFactoryWrapper;
import com.fasterxml.jackson.module.jsonSchema.types.ArraySchema;
import com.fasterxml.jackson.module.jsonSchema.types.ObjectSchema;
import com.fasterxml.jackson.module.jsonSchema.types.ValueTypeSchema;
import com.phoenixnap.oss.ramlapisync.data.ApiBodyMetadata;
import com.phoenixnap.oss.ramlapisync.data.ApiParameterMetadata;
import com.phoenixnap.oss.ramlapisync.javadoc.JavaDocEntry;
import com.phoenixnap.oss.ramlapisync.javadoc.JavaDocStore;
import com.sun.codemodel.JCodeModel;
import org.springframework.web.multipart.MultipartFile;

/**
 * Class containing convenience methods relating to the extracting of information from Java types for use as Parameters.
 * These can either be decomposed into RAML Simple Types (Similar to Java primitives) or JSON Schema for more complex
 * objects
 *
 * @author Kurt Paris
 * @since 0.0.1
 *
 */
public class SchemaHelper {

	protected static final Logger logger = LoggerFactory.getLogger(SchemaHelper.class);

	/**
	 * Converts a simple parameter, ie String, or Boxed Primitive into
	 *
	 * @param param The Java Parameter to convert
	 * @param paramComment The associated Javadoc if any
	 * @return A map of query parameters that map into the supplied type
	 */
	public static Map<String, QueryParameter> convertParameterToQueryParameter(final Parameter param,
			final String paramComment) {
		QueryParameter queryParam = new QueryParameter();
		ApiParameterMetadata parameterMetadata = new ApiParameterMetadata(param);

		ParamType type = mapSimpleType(param.getType());

		if (type == null) {
			throw new IllegalArgumentException("This method is only applicable to simple types or primitives");
		}

		if (StringUtils.hasText(paramComment)) {
			queryParam.setDescription(paramComment);
		}

		// Populate parameter model with data such as name, type and required/not

		queryParam.setDisplayName(parameterMetadata.getName());
		queryParam.setType(mapSimpleType(param.getType()));
		if (StringUtils.hasText(parameterMetadata.getExample())) {
			queryParam.setExample(parameterMetadata.getExample());
		}
		queryParam.setRequired(!parameterMetadata.isNullable());
		queryParam.setRepeat(param.getType().isArray()); // TODO we could add validation info
															// here - maybe hook into JSR303
															// annotations
		return Collections.singletonMap(parameterMetadata.getName(), queryParam);
	}

	/**
	 * Utility method that will return a schema if the identifier is valid and exists in the raml file definition.
	 *
	 * @param schema The name of the schema to resolve
	 * @param document The Parent Raml Document
	 * @return The full schema if contained in the raml document or null if not found
	 */
	public static String resolveSchema(String schema, Raml document) {
		if (document == null || schema == null ||schema.indexOf("{") != -1) {
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
	 * Breaks down a class into component fields which are mapped as Query Parameters. If Javadoc is supplied, this will
	 * be injected as comments
	 *
	 * @param param The Parameter representing the class to be converted into query parameters
	 * @param javaDocStore The associated JavaDoc (if any)
	 * @return a Map of Parameter RAML models keyed by parameter name
	 */
	public static Map<String, QueryParameter> convertClassToQueryParameters(final Parameter param,
			final JavaDocStore javaDocStore) {
		final Map<String, QueryParameter> outParams = new TreeMap<>();

		if (param == null || param.equals(Void.class)) {
			return outParams;
		}
		final ApiParameterMetadata parameterMetadata = new ApiParameterMetadata(param);

		if (mapSimpleType(param.getType()) != null) {
			throw new IllegalArgumentException(
					"This method should only be called on non primitive classes which will be broken down into query parameters");
		}

		try {
			for (Field field : param.getType().getDeclaredFields()) {
				if (!java.lang.reflect.Modifier.isStatic(field.getModifiers())
						&& !java.lang.reflect.Modifier.isTransient(field.getModifiers())
						&& !java.lang.reflect.Modifier.isVolatile(field.getModifiers())) {
					QueryParameter queryParam = new QueryParameter();

					// Check if we have comments
					JavaDocEntry paramComment = javaDocStore == null ? null : javaDocStore.getJavaDoc(field.getName());
					if (paramComment != null && StringUtils.hasText(paramComment.getComment())) {
						queryParam.setDescription(paramComment.getComment());
					}

					// Populate parameter model with data such as name, type and
					// required/not
					queryParam.setDisplayName(field.getName());
					ParamType simpleType = mapSimpleType(field.getType());
					queryParam.setType(simpleType == null ? ParamType.STRING : simpleType);
					queryParam.setRequired(parameterMetadata.isNullable());
					queryParam.setRepeat(false); // TODO we could add validation
													// info
													// here - maybe hook into
													// JSR303
													// annotations
					outParams.put(field.getName(), queryParam);
				}
			}
			return outParams;

		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
	}

	/**
	 * Uses Jackson object mappers to convert an ajaxcommandparameter annotated type into its JSONSchema representation.
	 * If Javadoc is supplied, this will be injected as comments
	 *
	 * @param clazz The Class to convert
	 * @param responseDescription The javadoc description supplied if available
	 * @param javaDocStore The Entire java doc store available
	 * @return A string containing the Json Schema
	 */
	public static String convertClassToJsonSchema(ApiParameterMetadata clazz, String responseDescription,
			JavaDocStore javaDocStore) {
		if (clazz == null || clazz.equals(Void.class)) {
			return "{}";
		}
		try {
			ObjectMapper m = new ObjectMapper();
			JsonSchema jsonSchema = extractSchemaInternal(clazz.getType(), clazz.getGenericType(), responseDescription,
					javaDocStore, m);

			return m.writerWithDefaultPrettyPrinter().writeValueAsString(jsonSchema);
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
	}

	/**
	 * Uses Jackson object mappers to convert a Pojo into its JSONSchema representation. If Javadoc is supplied, this
	 * will be injected as comments
	 *
	 * @param clazz The Class to be inspected
	 * @param responseDescription The description to be embedded in the response
	 * @param javaDocStore Associated JavaDoc for this class that can be embedded in the schema
	 * @return Json Schema representing the class in string format
	 */
	public static String convertClassToJsonSchema(Type clazz, String responseDescription, JavaDocStore javaDocStore) {
		if (clazz == null || clazz.equals(Void.class)) {
			return "{}";
		}
		try {
			ObjectMapper m = new ObjectMapper();
			JsonSchema jsonSchema = extractSchemaInternal(clazz, TypeHelper.inferGenericType(clazz),
					responseDescription, javaDocStore, m);

			return m.writerWithDefaultPrettyPrinter().writeValueAsString(jsonSchema);
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
	}

	private static JsonSchema extractSchemaInternal(Type clazz, Type genericType, String responseDescription,
			JavaDocStore javaDocStore, ObjectMapper m) throws JsonMappingException {
		SchemaFactoryWrapper visitor = new SchemaFactoryWrapper();
		if (genericType != null) {
			try {
				m.acceptJsonFormatVisitor(m.constructType(genericType), visitor);
			} catch (Exception ex) {
				logger.error("Unable to add JSON visitor for " + genericType.toString());
			}
		}
		try {
			m.acceptJsonFormatVisitor(m.constructType(clazz), visitor);
		} catch (Exception ex) {
			logger.error("Unable to add JSON visitor for " + clazz.toString());
		}

		JsonSchema jsonSchema = visitor.finalSchema();
		if (jsonSchema instanceof ObjectSchema && javaDocStore != null) {
			ObjectSchema objectSchema = (ObjectSchema) jsonSchema;
			if (objectSchema.getProperties() != null) {
				for (Entry<String, JsonSchema> cSchema : objectSchema.getProperties().entrySet()) {
					JavaDocEntry javaDocEntry = javaDocStore.getJavaDoc(cSchema.getKey());
					if (javaDocEntry != null && StringUtils.hasText(javaDocEntry.getComment())) {
						cSchema.getValue().setDescription(javaDocEntry.getComment());
					}
				}
			}
		} else if (jsonSchema instanceof ValueTypeSchema && StringUtils.hasText(responseDescription)) {
			ValueTypeSchema valueTypeSchema = (ValueTypeSchema) jsonSchema;
			valueTypeSchema.setDescription(responseDescription);
		} else if (jsonSchema instanceof ArraySchema && genericType != null) {
			ArraySchema arraySchema = (ArraySchema) jsonSchema;
			arraySchema.setItemsSchema(extractSchemaInternal(genericType, TypeHelper.inferGenericType(genericType),
					responseDescription, javaDocStore, m));

		}
		return jsonSchema;
	}

	/**
	 * Maps primitives and other simple Java types into simple types supported by RAML
	 *
	 * @param clazz The Class to map
	 * @return The Simple RAML ParamType which maps to this class or null if one is not found
	 */
	public static ParamType mapSimpleType(Class<?> clazz) {
		Class<?> targetClazz = clazz;
		if (targetClazz.isArray() && clazz.getComponentType() != null) {
			targetClazz = clazz.getComponentType();
		}
		if (targetClazz.equals(Long.TYPE) || targetClazz.equals(Long.class) || targetClazz.equals(Integer.TYPE)
				|| targetClazz.equals(Integer.class) || targetClazz.equals(Short.TYPE)
				|| targetClazz.equals(Short.class) || targetClazz.equals(Byte.TYPE) || targetClazz.equals(Byte.class)) {
			return ParamType.INTEGER;
		} else if (targetClazz.equals(Float.TYPE) || targetClazz.equals(Float.class) || targetClazz.equals(Double.TYPE)
				|| targetClazz.equals(Double.class) || targetClazz.equals(BigDecimal.class)) {
			return ParamType.NUMBER;
		} else if (targetClazz.equals(Boolean.class) || targetClazz.equals(Boolean.TYPE)) {
			return ParamType.BOOLEAN;
		} else if (targetClazz.equals(String.class)) {
			return ParamType.STRING;
		}
		return null; // default to string
	}

	/**
	 * Maps simple types supported by RAML into primitives and other simple Java types
	 *
	 * @param param The Type to map
	 * @return The Java Class which maps to this Simple RAML ParamType or string if one is not found
	 */
	public static Class<?> mapSimpleType(ParamType param) {

		switch (param) {
			case BOOLEAN:
				return Boolean.class;
			case DATE:
				return Date.class;
			case INTEGER:
				return Long.class;
			case NUMBER:
				return BigDecimal.class;
			case FILE:
				return MultipartFile.class;
			default:
				return String.class;
		}
	}

	private static String JSON_SCHEMA_IDENT = "http://jsonschema.net";

	/**
	 * Maps a JSON Schema to a JCodeModel using JSONSchema2Pojo and encapsulates it along with some metadata into an {@link ApiBodyMetadata} object.
	 *
	 * @param document The Raml document being parsed
	 * @param schema The Schema (full schema or schema name to be resolved)
	 * @param basePackage The base package for the classes we are generating
	 * @param name The suggested name of the class based on the api call and whether it's a request/response. This will only be used if no suitable alternative is found in the schema
	 * @return Object representing this Body
	 */
	public static ApiBodyMetadata mapSchemaToPojo(Raml document, String schema, String basePackage, String name) {
		String resolvedName = null;
		String schemaName = schema;
		String resolvedSchema = SchemaHelper.resolveSchema(schema, document);
		if (resolvedSchema == null) {
			resolvedSchema = schema;
			schemaName = null;
		}
		if (resolvedSchema.contains("\"id\"")) { //check if id can give us exact name
			int idIdx = resolvedSchema.indexOf("\"id\"");
			//find the  1st and second " after the idx
			int startIdx = resolvedSchema.indexOf("\"", idIdx+ 4);
			int endIdx = resolvedSchema.indexOf("\"", startIdx+1);
			String id = resolvedSchema.substring(startIdx+1, endIdx);
			if (id.startsWith("urn:") && ((id.lastIndexOf(":")+1) < id.length())) {
				id = id.substring(id.lastIndexOf(":")+1);
			} else if (id.startsWith(JSON_SCHEMA_IDENT)) {
				if (id.length() > (JSON_SCHEMA_IDENT.length()+3)) {
					id = id.substring(JSON_SCHEMA_IDENT.length());
				}
			} else {
				resolvedName = StringUtils.capitalize(id);
			}
		}
		if (!NamingHelper.isValidJavaClassName(resolvedName)) {
			if (NamingHelper.isValidJavaClassName(schemaName)) {
				resolvedName = Inflector.capitalize(schemaName); //try schema name
			} else {
				resolvedName = name; //fallback to generated
			}
		}
		JCodeModel codeModel = buildBodyJCodeModel(basePackage, resolvedName, resolvedSchema, null, null);
		if (codeModel != null) {
			return new ApiBodyMetadata(resolvedName, resolvedSchema, codeModel);
		} else {
			return null;
		}
	}

	/**
	 * Builds a JCodeModel for classes that will be used as Request or Response bodies
	 *
	 * @param basePackage The package we will be using for the domain objects
	 * @param name The class name
	 * @param schema The JSON Schema representing this class
	 * @param config JsonSchema2Pojo configuration. if null a default config will be used
	 * @param annotator JsonSchema2Pojo annotator. if null a default annotator will be used
	 * @return built JCodeModel
	 */
	public static JCodeModel buildBodyJCodeModel(String basePackage, String name, String schema, GenerationConfig config, Annotator annotator) {
		JCodeModel codeModel = new JCodeModel();
		SchemaStore schemaStore = new SchemaStore();


		if (config == null) {
				config = new DefaultGenerationConfig() {
				@Override
				public boolean isGenerateBuilders() { // set config option by overriding method
					return true;
				}

				@Override
				public boolean isIncludeAdditionalProperties() {
					return true;
				}

				@Override
				public boolean isIncludeDynamicAccessors() {
					return false;
				}
			};
		}
		if (annotator == null) {
			annotator = new Jackson2Annotator();
		}
		RuleFactory ruleFactory = new RuleFactory(config, annotator, schemaStore);


		SchemaMapper mapper = new SchemaMapper(ruleFactory,
				new SchemaGenerator());
		try {
			mapper.generate(codeModel, name, basePackage, schema);
		} catch (Exception e) {
			logger.error("Error generating pojo from schema "+ name, e);
			return null;
		}
		return codeModel;
	}



}
