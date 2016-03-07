package com.phoenixnap.oss.ramlapisync.generation.serialize;

import com.phoenixnap.oss.ramlapisync.data.ApiBodyMetadata;
import com.phoenixnap.oss.ramlapisync.data.ApiControllerMetadata;
import com.phoenixnap.oss.ramlapisync.data.ApiMappingMetadata;
import com.phoenixnap.oss.ramlapisync.data.ApiParameterMetadata;
import org.raml.model.parameter.UriParameter;
import org.raml.parser.utils.Inflector;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author armin.weisser
 */
public class Spring4ControllerSerializer implements ApiControllerMetadataSerializer {

    protected final String header;
    protected final ApiControllerMetadata controller;
    protected String gen = "";

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

    protected void addCloseClass() {
        gen += "}\n";
    }

    protected void addClassMethods() {
        for (ApiMappingMetadata mapping : controller.getApiCalls()) {
            gen += generateMethodForApiCall(mapping);
            gen += "\n";
        }
    }

    protected void addClassDeclaration() {
        gen += "public class " + generateControllerClassName() + " " + generateImplementsExtends() + " { \n";
        gen += "\n";
    }

    protected void addClassAnnotations() {
        gen += "\n";
        if (StringUtils.hasText(controller.getDescription())) {
            gen += "/**\n";
            gen += " * " + controller.getDescription().replaceAll("\n", "\n *") + "\n";
            gen += " */\n";
        }
        gen += "@" + RestController.class.getSimpleName() + "\n";
        gen += "@" + RequestMapping.class.getSimpleName() + "(\"" + controller.getUrl() + "\")\n";
    }

    protected void addImports() {
        gen += "import org.springframework.http.*; \n";
        gen += "import java.util.*; \n";
        gen += "import org.springframework.web.bind.annotation.*; \n";
        gen += "import " + (StringUtils.hasText(controller.getBasePackage()) ? controller.getBasePackage() + "." : "")
                + "model.*; \n"; // TODO make this import only if we have 1 or more bodies
        gen += "\n";
    }

    protected void addPackageSection() {
        if (StringUtils.hasText(controller.getBasePackage())) {
            gen += "package " + controller.getBasePackage() + ";\n";
            gen += "\n";
        }
    }

    protected void addHeader() {
        if (StringUtils.hasText(header)) {
            gen += header + "\n";
            gen += "\n";
        }
    }


    protected String generateImplementsExtends() {
        return "";
    }

    protected void addClassFields() {
    }

    protected String generateControllerClassName() {
        return controller.getName();
    }

    protected String generateParameters(ApiMappingMetadata mapping, Function<ApiParameterMetadata, String> mappingFunction) {
        List<ApiParameterMetadata> parameterMetadataList = new ArrayList<>();
        parameterMetadataList.addAll(mapping.getPathVariables());
        parameterMetadataList.addAll(mapping.getRequestParameters());
        return parameterMetadataList.stream().map(mappingFunction).collect(Collectors.toList()).stream().collect(Collectors.joining(","));
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
        String gen = "";
        gen += generateMethodComments(mapping);
        gen += generateMethodAnnotation(mapping);
        gen += "\tpublic " + generateMethodResponseType(mapping) + " " + mapping.getName() + " (" + generateAllMethodParameters(mapping) + ") ";
        gen += "{ \n";
        gen += generateMethodBody(mapping);
        gen += "\t}\n";
        return gen;
    }

    protected String generateMethodComments(ApiMappingMetadata mapping) {
        String gen = "";
        gen += "\t/**\n";
        gen += "\t * "
                + ((mapping.getDescription() != null) ? mapping.getDescription().replaceAll("\n", "\n\t *")
                : "No description") + "\n";
        gen += "\t */\n";
        return gen;
    }

    private String generateMethodAnnotation(ApiMappingMetadata mapping) {
        return "\t@" + RequestMapping.class.getSimpleName() +"(value=\"" + mapping.getUrl() + "\", method=RequestMethod."+mapping.getActionType().name()+")\n";
    }

    protected String generateAllMethodParameters(ApiMappingMetadata mapping) {
        String parameters = generateParameters(mapping, apiParameterMetadata -> generateParameter(apiParameterMetadata));
        if (mapping.getRequestBody() != null) {
            ApiBodyMetadata apiBodyMetadata = mapping.getRequestBody();
            Arrays.asList(parameters, generateRequestBodyParameter(apiBodyMetadata)).stream().collect(Collectors.joining(","));
        }
        return parameters;
    }

    protected String generateRequestBodyParameter(ApiBodyMetadata apiBodyMetadata) {
        String annotation = generateRequestBodyParamaterAnnotation();
        return annotation + apiBodyMetadata.getName() + " " + Inflector.camelize(apiBodyMetadata.getName());
    }

    protected String generateRequestBodyParamaterAnnotation() {
        return "@" + RequestBody.class.getSimpleName() + " ";
    }

    protected String generateMethodResponseType(ApiMappingMetadata mapping) {
        String response = "ResponseEntity";
        if (!mapping.getResponseBody().isEmpty()) {
            ApiBodyMetadata apiBodyMetadata = mapping.getResponseBody().values().iterator().next();
            response = "@" + ResponseBody.class.getSimpleName() +" ";
            if (apiBodyMetadata.isArray()) {
                response += ArrayList.class.getSimpleName() + "<" + apiBodyMetadata.getName() + ">";
            } else {
                response += apiBodyMetadata.getName();
            }

        }
        return response;
    }

    protected String generateMethodBody(ApiMappingMetadata mapping) {
        String methodBody = "\t\n";
        methodBody += "\t\t //TODO Autogenerated Method Stub. Implement me please.\n";
        methodBody += "\t\t return null;\n";
        return methodBody;
    }

    /**
     * Generates a string representation for a java parameter representing this api parameter TODO Note: Currently
     * Experimental - will be moved to templating engine
     *
     * @param param The parameter to represent
     * @return The Java string representation of the parameter
     */
    protected String generateParameter(ApiParameterMetadata param) {
        String annotation = generateParameterAnnotation(param);
        return annotation + param.getType().getSimpleName() + " " + param.getName();
    }

    protected String generateParameterAnnotation(ApiParameterMetadata param) {
        String annotation = "@";
        if (param.getRamlParam() != null && param.getRamlParam() instanceof UriParameter) {
            annotation += PathVariable.class.getSimpleName();
        } else {
            annotation += RequestParam.class.getSimpleName();
        }

        annotation += " ";
        return annotation;
    }
}
