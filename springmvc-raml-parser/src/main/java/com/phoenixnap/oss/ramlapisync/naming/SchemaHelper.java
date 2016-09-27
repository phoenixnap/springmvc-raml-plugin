/*
 * Copyright 2002-2016 the original author or authors.
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
package com.phoenixnap.oss.ramlapisync.naming;


import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.net.URI;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.apache.commons.io.IOUtils;
import org.jsonschema2pojo.Annotator;
import org.jsonschema2pojo.DefaultGenerationConfig;
import org.jsonschema2pojo.GenerationConfig;
import org.jsonschema2pojo.Jackson2Annotator;
import org.jsonschema2pojo.SchemaGenerator;
import org.jsonschema2pojo.SchemaMapper;
import org.jsonschema2pojo.SchemaStore;
import org.jsonschema2pojo.rules.RuleFactory;
import org.raml.parser.utils.Inflector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

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
import com.phoenixnap.oss.ramlapisync.raml.RamlMimeType;
import com.phoenixnap.oss.ramlapisync.raml.RamlModelFactory;
import com.phoenixnap.oss.ramlapisync.raml.RamlModelFactoryOfFactories;
import com.phoenixnap.oss.ramlapisync.raml.RamlParamType;
import com.phoenixnap.oss.ramlapisync.raml.RamlQueryParameter;
import com.phoenixnap.oss.ramlapisync.raml.RamlRoot;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JPackage;



/**
 * Class containing convenience methods relating to the extracting of information from Java types
 * for use as Parameters.
 * These can either be decomposed into RAML Simple Types (Similar to Java primitives) or JSON Schema
 * for more complex
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
     * @param param
     *            The Java Parameter to convert
     * @param paramComment
     *            The associated Javadoc if any
     * @return A map of query parameters that map into the supplied type
     */
    public static Map<String, RamlQueryParameter> convertParameterToQueryParameter(final Parameter param,
            final String paramComment) {
        RamlQueryParameter queryParam = RamlModelFactoryOfFactories.createRamlModelFactory().createRamlQueryParameter();
        ApiParameterMetadata parameterMetadata = new ApiParameterMetadata(param);

        RamlParamType type = mapSimpleType(param.getType());

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
     * Utility method that will return a schema if the identifier is valid and exists in the raml
     * file definition.
     *
     * @param schema
     *            The name of the schema to resolve
     * @param document
     *            The Parent Raml Document
     * @return The full schema if contained in the raml document or null if not found
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
     * Breaks down a class into component fields which are mapped as Query Parameters. If Javadoc is
     * supplied, this will
     * be injected as comments
     *
     * @param param
     *            The Parameter representing the class to be converted into query parameters
     * @param javaDocStore
     *            The associated JavaDoc (if any)
     * @return a Map of Parameter RAML models keyed by parameter name
     */
    public static Map<String, RamlQueryParameter> convertClassToQueryParameters(final Parameter param,
            final JavaDocStore javaDocStore) {
        final Map<String, RamlQueryParameter> outParams = new TreeMap<>();

        if (param == null || param.equals(Void.class)) {
            return outParams;
        }
        final ApiParameterMetadata parameterMetadata = new ApiParameterMetadata(param);

        if (mapSimpleType(param.getType()) != null) {
            throw new IllegalArgumentException(
                    "This method should only be called on non primitive classes which will be broken down into query parameters");
        }

        try {
            RamlModelFactory ramlModelFactory = RamlModelFactoryOfFactories.createRamlModelFactory();
            for (Field field : param.getType().getDeclaredFields()) {
                if (!java.lang.reflect.Modifier.isStatic(field.getModifiers())
                        && !java.lang.reflect.Modifier.isTransient(field.getModifiers())
                        && !java.lang.reflect.Modifier.isVolatile(field.getModifiers())) {
                    RamlQueryParameter queryParam = ramlModelFactory.createRamlQueryParameter();

                    // Check if we have comments
                    JavaDocEntry paramComment = javaDocStore == null ? null : javaDocStore.getJavaDoc(field.getName());
                    if (paramComment != null && StringUtils.hasText(paramComment.getComment())) {
                        queryParam.setDescription(paramComment.getComment());
                    }

                    // Populate parameter model with data such as name, type and
                    // required/not
                    queryParam.setDisplayName(field.getName());
                    RamlParamType simpleType = mapSimpleType(field.getType());
                    queryParam.setType(simpleType == null ? RamlParamType.STRING : simpleType);
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

        }
        catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }


    /**
     * Uses Jackson object mappers to convert an ajaxcommandparameter annotated type into its
     * JSONSchema representation.
     * If Javadoc is supplied, this will be injected as comments
     *
     * @param clazz
     *            The Class to convert
     * @param responseDescription
     *            The javadoc description supplied if available
     * @param javaDocStore
     *            The Entire java doc store available
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
        }
        catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }


    /**
     * Uses Jackson object mappers to convert a Pojo into its JSONSchema representation. If Javadoc
     * is supplied, this
     * will be injected as comments
     *
     * @param clazz
     *            The Class to be inspected
     * @param responseDescription
     *            The description to be embedded in the response
     * @param javaDocStore
     *            Associated JavaDoc for this class that can be embedded in the schema
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
        }
        catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }


    private static JsonSchema extractSchemaInternal(Type clazz, Type genericType, String responseDescription,
            JavaDocStore javaDocStore, ObjectMapper m)
            throws JsonMappingException {
        SchemaFactoryWrapper visitor = new SchemaFactoryWrapper();
        if (genericType != null) {
            try {
                m.acceptJsonFormatVisitor(m.constructType(genericType), visitor);
            }
            catch (Exception ex) {
                logger.error("Unable to add JSON visitor for " + genericType.toString());
            }
        }
        try {
            m.acceptJsonFormatVisitor(m.constructType(clazz), visitor);
        }
        catch (Exception ex) {
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
        }
        else if (jsonSchema instanceof ValueTypeSchema && StringUtils.hasText(responseDescription)) {
            ValueTypeSchema valueTypeSchema = (ValueTypeSchema) jsonSchema;
            valueTypeSchema.setDescription(responseDescription);
        }
        else if (jsonSchema instanceof ArraySchema && genericType != null) {
            ArraySchema arraySchema = (ArraySchema) jsonSchema;
            arraySchema.setItemsSchema(extractSchemaInternal(genericType, TypeHelper.inferGenericType(genericType),
                    responseDescription, javaDocStore, m));

        }
        return jsonSchema;
    }


    /**
     * Maps primitives and other simple Java types into simple types supported by RAML
     *
     * @param clazz
     *            The Class to map
     * @return The Simple RAML ParamType which maps to this class or null if one is not found
     */
    public static RamlParamType mapSimpleType(Class<?> clazz) {
        Class<?> targetClazz = clazz;
        if (targetClazz.isArray() && clazz.getComponentType() != null) {
            targetClazz = clazz.getComponentType();
        }
        if (targetClazz.equals(Long.TYPE) || targetClazz.equals(Long.class) || targetClazz.equals(Integer.TYPE)
                || targetClazz.equals(Integer.class) || targetClazz.equals(Short.TYPE)
                || targetClazz.equals(Short.class) || targetClazz.equals(Byte.TYPE) || targetClazz.equals(Byte.class)) {
            return RamlParamType.INTEGER;
        }
        else if (targetClazz.equals(Float.TYPE) || targetClazz.equals(Float.class) || targetClazz.equals(Double.TYPE)
                || targetClazz.equals(Double.class) || targetClazz.equals(BigDecimal.class)) {
            return RamlParamType.NUMBER;
        }
        else if (targetClazz.equals(Boolean.class) || targetClazz.equals(Boolean.TYPE)) {
            return RamlParamType.BOOLEAN;
        }
        else if (targetClazz.equals(String.class)) {
            return RamlParamType.STRING;
        }
        return null; // default to string
    }


    /**
     * Maps simple types supported by RAML into primitives and other simple Java types
     *
     * @param param
     *            The Type to map
     * @return The Java Class which maps to this Simple RAML ParamType or string if one is not found
     */
    public static Class<?> mapSimpleType(RamlParamType param) {

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


    /**
     * Extracts the name from a schema in this order of precedence:
     * 1. If the schema contains an ID element
     * 2. The name of the schema within the RAML document
     * 3. The autogenerated name based on the enclosing method
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
        	//if we have an array type we need to recurse into it
        	int startIdx = 0;
        	String type =  extractTopItem("type", schema, startIdx);
        	if (type != null && type.equalsIgnoreCase("array")) {
        		int itemsIdx = schema.indexOf("\"items\"");
        		if (itemsIdx != -1) {
        			startIdx = itemsIdx + 7;
        		}
        		//lets check if we have a ref
        		String ref = extractTopItem("$ref", schema, startIdx);
        		if (ref != null) {
        			logger.info("Loading referenced schema " + ref);
        			ref = ref.replace("classpath:", "");
        			try {
						schema = IOUtils.toString(
								Thread.currentThread().getContextClassLoader().getResourceAsStream(ref),
							      "UTF-8"
							    );
						startIdx = 0; //reset pointer since we recursed into schema
					} catch (IOException e) {
						logger.info("Erro Loading referenced schema " + ref, e);
					}
        		}
        	}
        	// check if javaType can give us exact name
        	String javaType =  extractTopItem("javaType", schema, startIdx);
        	if (StringUtils.hasText(javaType)) {
        		//do stuff to it
        		int dotIdx = javaType.lastIndexOf(".");
        		if (dotIdx > -1) {
        			javaType = javaType.substring(dotIdx+1);
        		}
        		resolvedName = javaType;
        		
        	} else {
        		String id =  extractTopItem("id", schema, startIdx);
        		if (StringUtils.hasText(id)) {
            		//do stuff to it
        			 if (id.startsWith("urn:") && ((id.lastIndexOf(":") + 1) < id.length())) {
                         id = id.substring(id.lastIndexOf(":") + 1);
                     }
                     else if (id.startsWith(JSON_SCHEMA_IDENT)) {
                         if (id.length() > (JSON_SCHEMA_IDENT.length() + 3)) {
                             id = id.substring(JSON_SCHEMA_IDENT.length());
                         }
                     }
                     
                     resolvedName = StringUtils.capitalize(id);
                     
            	}
        		 if (!NamingHelper.isValidJavaClassName(resolvedName)) {
        	            if (NamingHelper.isValidJavaClassName(schemaName)) {
        	                return Inflector.capitalize(schemaName); // try schema name
        	            }
        	            else {
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
     * @param searchString element to search for
     * @param schema Schema as a string
     * @return the value or null if not found
     */
	private static String extractTopItem(String searchString, String schema, int startIdx) {
		String extracted = null;
		int propIdx = schema.indexOf("\"properties\"", startIdx);
		if (propIdx == -1) {
			propIdx = Integer.MAX_VALUE;
		}
		//check for second
		int idIdx = schema.indexOf("\"" + searchString + "\"", startIdx);
		int secondIdIdx = schema.indexOf("\"" + searchString + "\"", idIdx + 1);
		if (secondIdIdx != -1 && propIdx > secondIdIdx) {
			idIdx = secondIdIdx;
		}
		if (idIdx != -1 && propIdx > idIdx) { // make sure we're not in a nested id
			// find the 1st and second " after the idx
			int valueStartIdx = schema.indexOf("\"", idIdx + (searchString.length()+2));
			int valueEndIdx = schema.indexOf("\"", valueStartIdx + 1);
			extracted = schema.substring(valueStartIdx + 1, valueEndIdx);
		}
		return extracted;
	}

    private static String JSON_SCHEMA_IDENT = "http://jsonschema.net";



    /**
     * Maps a JSON Schema to a JCodeModel using JSONSchema2Pojo and encapsulates it along with some
     * metadata into an {@link ApiBodyMetadata} object.
     *
     * @param document
     *            The Raml document being parsed
     * @param schema
     *            The Schema (full schema or schema name to be resolved)
     * @param basePackage
     *            The base package for the classes we are generating
     * @param name
     *            The suggested name of the class based on the api call and whether it's a
     *            request/response. This will only be used if no suitable alternative is found in
     *            the schema
     * @param schemaLocation
     *            Base location of this schema, will be used to create absolute URIs for $ref tags
     *            eg "classpath:/"
     * @return Object representing this Body
     */
    public static ApiBodyMetadata mapSchemaToPojo(RamlRoot document, String schema, String basePackage, String name, String schemaLocation) {
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
        JCodeModel codeModel = buildBodyJCodeModel(basePackage, StringUtils.hasText(schemaLocation) ? schemaLocation : "classpath:/", resolvedName, resolvedSchema, null, null);
        if (codeModel != null) {
            if (codeModel.countArtifacts() == 1) {
                try {
                    // checking has next twice might be more efficient but this is more readable, if
                    // we ever run into speed issues here..optimise
                    Iterator<JPackage> packages = codeModel.packages();

                    // in the case that we have empty packages we need to skip them to get to the
                    // class
                    JPackage nextPackage = packages.next();
                    while (!nextPackage.classes().hasNext() && packages.hasNext()) {
                        nextPackage = packages.next();
                    }
                    resolvedName = nextPackage.classes().next().name();
                }
                catch (NullPointerException npe) {
                    // do nothing, use previous name
                }
            }
            return new ApiBodyMetadata(resolvedName, resolvedSchema, codeModel);
        }
        else {
            return null;
        }
    }


    /**
     * Builds a JCodeModel for classes that will be used as Request or Response bodies
     *
     * @param basePackage
     *            The package we will be using for the domain objects
     * @param schemaLocation
     *            The location of this schema, will be used to create absolute URIs for $ref tags eg
     *            "classpath:/"
     * @param name
     *            The class name
     * @param schema
     *            The JSON Schema representing this class
     * @param config
     *            JsonSchema2Pojo configuration. if null a default config will be used
     * @param annotator
     *            JsonSchema2Pojo annotator. if null a default annotator will be used
     * @return built JCodeModel
     */
    public static JCodeModel buildBodyJCodeModel(String basePackage, String schemaLocation, String name, String schema, GenerationConfig config, Annotator annotator) {
        JCodeModel codeModel = new JCodeModel();
        SchemaStore schemaStore = new SchemaStore();

        if (config == null) {
            config = getDefaultGenerationConfig();

        }
        if (annotator == null) {
            annotator = new Jackson2Annotator();
        }
        RuleFactory ruleFactory = new RuleFactory(config, annotator, schemaStore);


        SchemaMapper mapper = new SchemaMapper(ruleFactory,
                new SchemaGenerator());
        boolean useParent = StringUtils.hasText(schemaLocation);
        try {
            if (useParent) {
                mapper.generate(codeModel, name, basePackage, schema, new URI(schemaLocation));
            }
            else {
                mapper.generate(codeModel, name, basePackage, schema);
            }

        }
        catch (Exception e) {
            // TODO make this smarter by checking refs
            if (useParent && e.getMessage().contains("classpath")) {
                logger.debug("Referenced Schema contains self $refs or not found in classpath. Regenerating model withouth classpath: for " + name);
                codeModel = new JCodeModel();
                try {
                    mapper.generate(codeModel, name, basePackage, schema);
                    return codeModel;
                }
                catch (IOException e1) {
                    // do nothing
                }
            }
            logger.error("Error generating pojo from schema" + name, e);
            return null;
        }
        return codeModel;
    }


    /**
     * Returns a configuration for the JSON Schema 2 POJO that is in line with the defaults used in
     * the plugin so far
     *
     * @return Default Generation Config
     */
    public static GenerationConfig getDefaultGenerationConfig() {
        return getGenerationConfig(true, false, false, false);
    }


    /**
     * Returns a generation config with the supplied parameters. If any of these parameters are
     * supplied null, it will
     * use the value defined in the default configuration
     *
     * @param generateBuilders
     *            Enables or disables {@link GenerationConfig#isGenerateBuilders()}
     * @param includeAdditionalProperties
     *            Enables or disables {@link GenerationConfig#isIncludeAdditionalProperties()}
     * @param includeDynamicAccessors
     *            Enables or disables {@link GenerationConfig#isIncludeDynamicAccessors()}
     * @param useLongIntegers
     *            Enables or disables {@link GenerationConfig#isUseLongIntegers()}
     * @return The GenerationConfig
     */
    public static GenerationConfig getGenerationConfig(Boolean generateBuilders, Boolean includeAdditionalProperties, Boolean includeDynamicAccessors, Boolean useLongIntegers) {
        return new DefaultGenerationConfig() {

            @Override
            public boolean isGenerateBuilders() { // set config option by overriding method
                if (generateBuilders != null) {
                    return generateBuilders;
                }
                else {
                    return true;
                }
            }


            @Override
            public boolean isIncludeAdditionalProperties() {
                if (includeAdditionalProperties != null) {
                    return includeAdditionalProperties;
                }
                else {
                    return false;
                }
            }


            @Override
            public boolean isIncludeDynamicAccessors() {
                if (includeDynamicAccessors != null) {
                    return includeDynamicAccessors;
                }
                else {
                    return false;
                }
            }


            @Override
            public boolean isUseLongIntegers() {
                if (useLongIntegers != null) {
                    return useLongIntegers;
                }
                else {
                    return super.isUseLongIntegers();
                }
            }
        };
    }


    /**
     * Checks if a Map of mime types contains at least 1 valid JsonSchema.
     *
     * @param body
     *            The request/response body
     * @param document
     *            the RAML document being checked
     * @param checkForValidSchema
     *            if false, we will omit checks to see if the schema is valid
     * @return true if at least 1 valid schema exists
     */
    public static boolean containsBodySchema(Map<String, RamlMimeType> body, RamlRoot document, boolean checkForValidSchema) {
        if (CollectionUtils.isEmpty(body)) {
            return false;
        }
        // successful response
        for (Entry<String, RamlMimeType> bodyMime : body.entrySet()) {
            RamlMimeType mime = bodyMime.getValue();
            if (mime != null && StringUtils.hasText(mime.getSchema())) {
                if (checkForValidSchema) {
                    try {
                        ApiBodyMetadata pojo = SchemaHelper.mapSchemaToPojo(document, mime.getSchema(), "com.phoenixnap.oss.stylecheck", "ClazzUnderCheck", null);
                        if (pojo == null) {
                            return false;
                        }
                        else {
                            return true;
                        }

                    }
                    catch (Exception ex) {
                        // Do Nothing
                        logger.warn("Possible Schema excheption", ex);
                        return false;
                    }
                }
                return true;
            }
        }
        return false;
    }
}
