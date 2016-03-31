package com.phoenixnap.oss.ramlapisync.generation.serialize;

import com.phoenixnap.oss.ramlapisync.data.ApiBodyMetadata;
import com.phoenixnap.oss.ramlapisync.data.ApiControllerMetadata;
import com.phoenixnap.oss.ramlapisync.data.ApiMappingMetadata;
import com.phoenixnap.oss.ramlapisync.data.ApiParameterMetadata;
import org.raml.model.parameter.UriParameter;
import org.raml.parser.utils.Inflector;
import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * This class provides a general serializition of a .raml spec into a Spring MVC Controller stub.
 *
 * It also implements a basic version of the serialize() method.
 * You can probably subclass this one if you want to implement another serialization strategy
 * and reuse the serialize() method as a template method.
 *
 * All methods called from the serialize() are protected so that a subclass can hook into
 * almost every step of the code generation algorithm.
 *
 * There are two kinds of protected methods.
 * The ones starting with "add" are altering the "gen" field, which holds the generated result.
 * The ones starting with "generate" are somehow functional methods that don't alter the state of the class.
 *
 * TODO Build JCodeModel instead of magic String concatenation.
 * TODO Refactor the different generate and add methods to different Rules (just like org.jsonschema2pojo.rules.Rule ...)
 *
 * @author armin.weisser
 * @author Kurt Paris
 * @since 0.3.1
 */
public class Spring4ControllerSerializer implements ApiControllerMetadataSerializer {

    protected final String header;
    protected final ApiControllerMetadata controller;
    protected String gen;

    public Spring4ControllerSerializer(ApiControllerMetadata controller, String header) {
        this.header = header;
        this.controller = controller;
    }

    @Override
    public String getName() {
        return generateControllerClassName();
    }

    @Override
    public String serialize() {
        gen = "";
        addHeader();
        addPackageSection();
        addImports();
        addClassAnnotations();
        addClassDeclaration();
        addClassFields();
        addClassMethods();
        addCloseClass();
        return gen;
    }

    /**
     * add the given header field to the generated code.
     */
    protected void addHeader() {
        if (StringUtils.hasText(header)) {
            gen += header + "\n";
            gen += "\n";
        }
    }

    /**
     * add the package declaration to the generated code.
     */
    protected void addPackageSection() {
        if (StringUtils.hasText(controller.getBasePackage())) {
            gen += "package " + controller.getBasePackage() + ";\n";
            gen += "\n";
        }
    }

    /**
     * add imports to the generated code.
     */
    protected void addImports() {
        gen += "import org.springframework.http.*; \n";
        gen += "import java.util.*; \n";
        gen += "import org.springframework.web.bind.annotation.*; \n";
        gen += generateModelImport();
        gen += "\n";
    }

    /**
     * add class annoations to the generated code.
     */
    protected void addClassAnnotations() {
        gen += "\n";
        if (StringUtils.hasText(controller.getDescription())) {
            gen += "/**\n";
            gen += " * " + controller.getDescription().replaceAll("\n", "\n *") + "\n";
            gen += " */\n";
        }
        gen += "@" + RestController.class.getSimpleName() + "\n";
        gen += "@" + RequestMapping.class.getSimpleName() + "("+ generateClassRequestMappingAttributes()+")\n";
    }

    /**
     * add the class declaration to the generated code.
     */
    protected void addClassDeclaration() {
        gen += "public class " + generateControllerClassName() + " " + generateImplementsExtends() + " { \n";
        gen += "\n";
    }

    /**
     * add field declerations to the generated code.
     */
    protected void addClassFields() {
    }

    /**
     * add all methods to the generated code.
     */
    protected void addClassMethods() {
        for (ApiMappingMetadata mapping : controller.getApiCalls()) {
            gen += generateMethodForApiCall(mapping);
            gen += "\n";
        }
    }

    /**
     * add the closing '}' to the generated code.
     */
    protected void addCloseClass() {
        gen += "}\n";
    }


    /**
     * @return attribute for @RequestMapping annotation at class level
     */
    private String generateClassRequestMappingAttributes() {
        String mediaType = generateMediaType();
        if(mediaType == null) {
            return "\""+controller.getControllerUrl()+"\"";
        }
        return "value=\""+controller.getControllerUrl()+"\", produces=\"" + mediaType +"\"";
    }

    /**
     * @return the media type specefied in the .raml spec, or null if no media type is set.
     */
    private String generateMediaType() {
        String ramlMediaType = controller.getDocument().getMediaType();
        try {
            return MediaType.parseMediaType(ramlMediaType).toString();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * @return the import statetement for the generated model code or an empty string if no model is generated.
     */
    protected String generateModelImport() {
        if(hasResponseModel()) {
            String basePackage = StringUtils.hasText(controller.getBasePackage()) ? controller.getBasePackage() + "." : "";
            return "import " + basePackage + "model.*; \n";
        }
        return "";
    }

    /**
     *
     * @return true, if the controller has a response model
     */
    protected boolean hasResponseModel() {
        return controller.getDependencies().size() > 0;
    }

    /**
     *
     * @return the implements and extends part of the class declaration
     */
    protected String generateImplementsExtends() {
        return "";
    }

    protected String generateControllerClassName() {
        return controller.getName();
    }



    /**
     * Generates a string representation for a java method representing this api endpoint TODO Note: Currently
     * Experimental - will be moved to templating engine
     *
     *
     * @param mapping The api call method to represent
     * @return The java method as a String
     */
    protected String generateMethodForApiCall(ApiMappingMetadata mapping) {
        String methodCode = "";
        methodCode += generateMethodComments(mapping);
        methodCode += generateMethodAnnotation(mapping);
        methodCode += "\tpublic " + generateMethodResponseType(mapping) + " " + mapping.getName() + " (" + generateMethodParameters(mapping) + ") ";
        methodCode += "{ \n";
        methodCode += generateMethodBody(mapping);
        methodCode += "\t}\n";
        return methodCode;
    }

    /**
     *
     * @param mapping The api call method to represent
     * @return comment section of the method declaration
     */
    protected String generateMethodComments(ApiMappingMetadata mapping) {
        String comments = "";
        comments += "\t/**\n";
        comments += "\t * "
                + ((mapping.getDescription() != null) ? mapping.getDescription().replaceAll("\n", "\n\t *")
                : "No description") + "\n";
        comments += "\t */\n";
        return comments;
    }

    /**
     *
     * @param mapping The api call method to represent
     * @return the method annotion (@RequestMapping ...) for this controller method.
     */
    protected String generateMethodAnnotation(ApiMappingMetadata mapping) {
        return "\t@" + RequestMapping.class.getSimpleName() +"(value=\"" + mapping.getUrl() + "\", method=RequestMethod."+mapping.getActionType().name()+")\n";
    }

    /**
     * This method makes use of the generic generateMethodParameters(ApiMappingMetadata, Function, Function) method.
     * In this case a fully blown method signatures are being created (annotion + type + name).
     *
     * @param mapping The api call method to represent
     * @return the method parameters.
     */
    protected String generateMethodParameters(ApiMappingMetadata mapping) {
        return generateMethodParameters(mapping, parameterFullStrategy(), requestBodyParameterAllStrategy());
    }

    /**
     * This is a generic generateMethodParameters(ApiMappingMetadata, Function, Function) method.
     * It makes use of Java 8 Function parameters to externalize the strategy of parameter serialization.
     *
     * The method generates a ',' seperated list of all API Endpoint parameters (@PathVariable and @RequestParam).
     *
     * @param mapping  The api call method to represent
     * @param parameterMappingStrategy The strategy for ApiParameterMetadata parameters
     * @param requestBodyParameterMappingStrategy The strategy for ApiBodyMetadata parameters
     * @return a ',' seperated serialization of all API Endpoint parameters
     */
    protected String generateMethodParameters(ApiMappingMetadata mapping,
                                              Function<ApiParameterMetadata, String> parameterMappingStrategy,
                                              Function<ApiBodyMetadata, String> requestBodyParameterMappingStrategy) {
        String parameters = generateParameters(mapping, parameterMappingStrategy);
        parameters += generateBodyParameters(mapping, requestBodyParameterMappingStrategy);
        return parameters;
    }

    /**
     *
     * @param mapping The api call method to represent
     * @param mappingFunction The strategy for ApiParameterMetadata parameters
     * @return a ',' seperated serialization of ApiParameterMetadata parameters
     */
    protected String generateParameters(ApiMappingMetadata mapping, Function<ApiParameterMetadata, String> mappingFunction) {
        List<ApiParameterMetadata> parameterMetadataList = new ArrayList<>();
        parameterMetadataList.addAll(mapping.getPathVariables());
        parameterMetadataList.addAll(mapping.getRequestParameters());
        return generateCommaSeperatedParameters(parameterMetadataList, mappingFunction);
    }

    /**
     *
     * @param mapping The api call method to represent
     * @param mappingFunction The strategy for ApiBodyMetadata parameters
     * @return a ',' seperated serialization of ApiBodyMetadata parameters
     */
    protected String generateBodyParameters(ApiMappingMetadata mapping, Function<ApiBodyMetadata, String> mappingFunction) {
        if (mapping.getRequestBody() != null) {
            List<ApiBodyMetadata> bodyMetadataList = new ArrayList<>();
            bodyMetadataList.add(mapping.getRequestBody());
            return generateCommaSeperatedParameters(bodyMetadataList, mappingFunction);
        }
        return "";
    }

    /**
     *
     * @param parameterMetadataList values to iterator
     * @param mappingFunction a mapping function
     * @param <T> Any class
     * @return a ',' seperated serialization of parameterMetadataList values mapped by the mapping function.
     */
    protected <T> String generateCommaSeperatedParameters(List<T> parameterMetadataList, Function<T, String> mappingFunction) {
        return parameterMetadataList.stream().map(mappingFunction).collect(Collectors.toList()).stream().collect(Collectors.joining(", "));
    }

    /**
     *
     * @return a fully blown serialization of the ApiBodyMetadata parameters (annotation + type + name).
     */
    protected Function<ApiBodyMetadata, String> requestBodyParameterAllStrategy() {
        return apiBodyMetadata -> {
            String annotation = generateRequestBodyParamaterAnnotation();
            String type = generateRequestBodyParameterType(apiBodyMetadata) + " ";
            String name = Inflector.camelize(type);
            return annotation + type + name;
        };
    }

    /**
     *
     * @return a serialization of the ApiBodyMetadata parameters without annotations (type + name).
     */
    protected Function<ApiBodyMetadata, String> requestBodyParameterNoAnnotationsStrategy() {
        return apiBodyMetadata -> {
            String type = generateRequestBodyParameterType(apiBodyMetadata) + " ";
            String name = Inflector.camelize(type);
            return type + name;
        };
    }

    /**
     *
     * @return a serialization of the ApiBodyMetadata parameter names only (name).
     */
    protected Function<ApiBodyMetadata, String> requestBodyParameterParameterNamesOnlyStrategy() {
        return apiBodyMetadata -> {
            String type = generateRequestBodyParameterType(apiBodyMetadata) + " ";
            String name = Inflector.camelize(type);
            return name;
        };
    }

    /**
     *
     * @return a fully blown serialization of the ApiParameterMetadata parameters (annotation + type + name).
     */
    protected Function<ApiParameterMetadata, String> parameterFullStrategy() {
        return apiParameterMetadata -> {
            String annotation = generateParameterAnnotation(apiParameterMetadata);
            String type = generateParameterType(apiParameterMetadata);
            String name = apiParameterMetadata.getName();
            return annotation + type + name;
        };
    }

    /**
     *
     * @return a serialization of the ApiParameterMetadata parameters without annotations (type + name).
     */
    protected Function<ApiParameterMetadata, String> parameterNoAnnotationsStrategy() {
        return apiParameterMetadata -> {
            String type = generateParameterType(apiParameterMetadata);
            String name = apiParameterMetadata.getName();
            return type + name;
        };
    }

    /**
     *
     * @return a serialization of the ApiParameterMetadata parameter names only (name).
     */
    protected Function<ApiParameterMetadata, String> parameterNamesOnlyStrategy() {
        return apiParameterMetadata -> apiParameterMetadata.getName();
    }

    /**
     *
     * @param mapping The api call method to represent
     * @return the method response type (Spring "ResponseEntity" by default).
     */
    protected String generateMethodResponseType(ApiMappingMetadata mapping) {
        String response = "ResponseEntity";
        if (!mapping.getResponseBody().isEmpty()) {
            ApiBodyMetadata apiBodyMetadata = mapping.getResponseBody().values().iterator().next();
            response = "@" + ResponseBody.class.getSimpleName() +" ";
            if (apiBodyMetadata.isArray()) {
                response += ArrayList.class.getSimpleName() + "<" + generateRequestBodyParameterType(apiBodyMetadata) + ">";
            } else {
                response += generateRequestBodyParameterType(apiBodyMetadata);
            }

        }
        return response;
    }

    /**
     *
     * @param apiBodyMetadata
     * @return the name of the ApiBodyMetadata
     */
    protected String generateRequestBodyParameterType(ApiBodyMetadata apiBodyMetadata) {
        return apiBodyMetadata.getName();
    }

    /**
     *
     * @return the single @RequestBody Annotation
     */
    protected String generateRequestBodyParamaterAnnotation() {
        return "@" + RequestBody.class.getSimpleName() + " ";
    }

    /**
     *
     * @param mapping The api call method to represent
     * @return an empty method body of this api endpoint method.
     */
    protected String generateMethodBody(ApiMappingMetadata mapping) {
        String methodBody = "\t\n";
        methodBody += "\t\t //TODO Autogenerated Method Stub. Implement me please.\n";
        methodBody += "\t\t return null;\n";
        return methodBody;
    }

    /**
     *
     * @param param
     * @return the type of the parameter
     */
    protected String generateParameterType(ApiParameterMetadata param) {
        return param.getType().getSimpleName() + " ";
    }

    /**
     *
     * @param param
     * @return the Annotation of the parameter including (required = false) if the parameter is optional.
     */
    protected String generateParameterAnnotation(ApiParameterMetadata param) {
        String annotation = "@";
        if (param.getRamlParam() != null && param.getRamlParam() instanceof UriParameter) {
            annotation += PathVariable.class.getSimpleName();
        } else {
            annotation += RequestParam.class.getSimpleName();
        }
        // In RAML parameters are optional unless the required attribute is included and its value set to 'true'.
        // In Spring a parameter is required by default unlesse the required attribute is included and its value is set to 'false'
        // So we just need to set required=false if the RAML "required" parameter is not set or explicitly set to false.
        if(param.getRamlParam() != null && !param.getRamlParam().isRequired()) {
            annotation += "(required = false)";
        }

        annotation += " ";
        return annotation;
    }
}
