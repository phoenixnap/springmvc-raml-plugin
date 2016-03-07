package com.phoenixnap.oss.ramlapisync.generation.serialize;

import com.phoenixnap.oss.ramlapisync.data.ApiControllerMetadata;
import com.phoenixnap.oss.ramlapisync.data.ApiMappingMetadata;
import com.phoenixnap.oss.ramlapisync.data.ApiParameterMetadata;

/**
 * @author armin.weisser
 */
public class Spring4ControllerInterfaceSerializer extends Spring4ControllerSerializer {

    public Spring4ControllerInterfaceSerializer(ApiControllerMetadata controller, String header) {
        super(controller, header);
    }

    @Override
    protected void addClassAnnotations() {
        // nothing
    }

    @Override
    protected void addClassDeclaration() {
        gen += "public interface " + generateControllerClassName() + " { \n";
        gen += "\n";
    }

    @Override
    protected void addClassFields() {
        // nothing
    }

    @Override
    protected String generateMethodForApiCall(ApiMappingMetadata mapping) {
        String gen = "";
        gen += generateMethodComments(mapping);
        gen += "\t" + generateMethodResponseType(mapping) + " " + mapping.getName() + " (" + generateMethodParameters(mapping) + ");";
        gen += "\n";
        return gen;
    }

    protected String generateMethodParameters(ApiMappingMetadata mapping) {
        return generateMethodParameters(mapping, parameterNoAnnotationsStrategy(), requestBodyParameterNoAnnotationsStrategy());
    }

    @Override
    protected String generateParameterAnnotation(ApiParameterMetadata param) {
        return "";
    }

    @Override
    protected String generateRequestBodyParamaterAnnotation() {
        return "";
    }
}
