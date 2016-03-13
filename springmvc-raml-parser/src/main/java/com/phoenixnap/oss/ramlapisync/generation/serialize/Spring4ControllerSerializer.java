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
 * @author armin.weisser
 */
public class Spring4ControllerSerializer implements ApiControllerMetadataSerializer {

    // TODO Build JCodeModel instead of magic String concatenation

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
        gen += "@" + RequestMapping.class.getSimpleName() + "("+generateRequestMappingAttributes()+")\n";
    }

    private String generateRequestMappingAttributes() {
        String mediaType = generateMediaType();
        if(mediaType == null) {
            return "\""+controller.getControllerUrl()+"\"";
        }
        return "value=\""+controller.getControllerUrl()+"\", produces=\"" + mediaType +"\"";
    }

    private String generateMediaType() {
        String ramlMediaType = controller.getDocument().getMediaType();
        try {
            return MediaType.parseMediaType(ramlMediaType).toString();
        } catch (Exception e) {
            return null;
        }
    }

    protected void addImports() {
        gen += "import org.springframework.http.*; \n";
        gen += "import java.util.*; \n";
        gen += "import org.springframework.web.bind.annotation.*; \n";
        gen += generateModelImport(); // TODO make this import only if we have 1 or more bodies
        gen += "\n";
    }

    protected String generateModelImport() {
        return "import " + (StringUtils.hasText(controller.getBasePackage()) ? controller.getBasePackage() + "." : "")
                + "model.*; \n";
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
        gen += "\tpublic " + generateMethodResponseType(mapping) + " " + mapping.getName() + " (" + generateMethodParameters(mapping) + ") ";
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

    protected String generateMethodParameters(ApiMappingMetadata mapping) {
        return generateMethodParameters(mapping, parameterFullStrategy(), requestBodyParameterAllStrategy());
    }

    protected String generateMethodParameters(ApiMappingMetadata mapping,
                                              Function<ApiParameterMetadata, String> parameterMappingStrategy,
                                              Function<ApiBodyMetadata, String> requestBodyParameterMappingStrategy) {
        String parameters = generateParameters(mapping, parameterMappingStrategy);
        parameters += generateBodyParameters(mapping, requestBodyParameterMappingStrategy);
        return parameters;
    }

    protected String generateParameters(ApiMappingMetadata mapping, Function<ApiParameterMetadata, String> mappingFunction) {
        List<ApiParameterMetadata> parameterMetadataList = new ArrayList<>();
        parameterMetadataList.addAll(mapping.getPathVariables());
        parameterMetadataList.addAll(mapping.getRequestParameters());
        return generateCommaSeperatedParameters(parameterMetadataList, mappingFunction);
    }

    protected String generateBodyParameters(ApiMappingMetadata mapping, Function<ApiBodyMetadata, String> mappingFunction) {
        if (mapping.getRequestBody() != null) {
            List<ApiBodyMetadata> bodyMetadataList = new ArrayList<>();
            bodyMetadataList.add(mapping.getRequestBody());
            return generateCommaSeperatedParameters(bodyMetadataList, mappingFunction);
        }
        return "";
    }

    protected <T> String generateCommaSeperatedParameters(List<T> parameterMetadataList, Function<T, String> mappingFunction) {
        return parameterMetadataList.stream().map(mappingFunction).collect(Collectors.toList()).stream().collect(Collectors.joining(", "));
    }

    protected Function<ApiBodyMetadata, String> requestBodyParameterAllStrategy() {
        return apiBodyMetadata -> {
            String annotation = generateRequestBodyParamaterAnnotation();
            String type = generateRequestBodyParameterType(apiBodyMetadata) + " ";
            String name = Inflector.camelize(type);
            return annotation + type + name;
        };
    }

    protected Function<ApiBodyMetadata, String> requestBodyParameterNoAnnotationsStrategy() {
        return apiBodyMetadata -> {
            String type = generateRequestBodyParameterType(apiBodyMetadata) + " ";
            String name = Inflector.camelize(type);
            return type + name;
        };
    }

    protected Function<ApiBodyMetadata, String> requestBodyParameterParameterNamesOnlyStrategy() {
        return apiBodyMetadata -> {
            String type = generateRequestBodyParameterType(apiBodyMetadata) + " ";
            String name = Inflector.camelize(type);
            return name;
        };
    }

    protected Function<ApiParameterMetadata, String> parameterFullStrategy() {
        return apiParameterMetadata -> {
            String annotation = generateParameterAnnotation(apiParameterMetadata);
            String type = generateParameterType(apiParameterMetadata);
            String name = apiParameterMetadata.getName();
            return annotation + type + name;
        };
    }

    protected Function<ApiParameterMetadata, String> parameterNoAnnotationsStrategy() {
        return apiParameterMetadata -> {
            String type = generateParameterType(apiParameterMetadata);
            String name = apiParameterMetadata.getName();
            return type + name;
        };
    }

    protected Function<ApiParameterMetadata, String> parameterNamesOnlyStrategy() {
        return apiParameterMetadata -> apiParameterMetadata.getName();
    }

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


    protected String generateRequestBodyParameterType(ApiBodyMetadata apiBodyMetadata) {
        return apiBodyMetadata.getName();
    }


    protected String generateRequestBodyParamaterAnnotation() {
        return "@" + RequestBody.class.getSimpleName() + " ";
    }

    protected String generateMethodBody(ApiMappingMetadata mapping) {
        String methodBody = "\t\n";
        methodBody += "\t\t //TODO Autogenerated Method Stub. Implement me please.\n";
        methodBody += "\t\t return null;\n";
        return methodBody;
    }



    protected String generateParameterType(ApiParameterMetadata param) {
        return param.getType().getSimpleName() + " ";
    }

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
