package com.phoenixnap.oss.ramlapisync.generation.serialize;

import com.phoenixnap.oss.ramlapisync.data.ApiBodyMetadata;
import com.phoenixnap.oss.ramlapisync.data.ApiControllerMetadata;
import com.phoenixnap.oss.ramlapisync.data.ApiMappingMetadata;

/**
 * @author armin.weisser
 */
public class Spring4ControllerInterfaceWithAnnotationsSerializer extends Spring4ControllerSerializer {

    public Spring4ControllerInterfaceWithAnnotationsSerializer(ApiControllerMetadata controller, String header) {
        super(controller, header);
    }


    @Override
    protected void addClassDeclaration() {
        gen += "public interface " + generateControllerClassName() + " { \n";
        gen += "\n";
    }

    @Override
    protected String generateMethodForApiCall(ApiMappingMetadata mapping) {
        String gen = "";
        gen += generateMethodComments(mapping);
        gen += generateMethodAnnotation(mapping);
        gen += "\t" + generateMethodResponseType(mapping) + " " + mapping.getName() + " (" + generateMethodParameters(mapping) + ");";
        gen += "\n";
        return gen;
    }

    protected String generateMethodParameters(ApiMappingMetadata mapping) {
        return generateMethodParameters(mapping, parameterFullStrategy(), requestBodyParameterAllStrategy());
    }

    protected String generateMethodResponseType(ApiMappingMetadata mapping) {
        String response = "ResponseEntity";
        if (!mapping.getResponseBody().isEmpty()) {
            ApiBodyMetadata apiBodyMetadata = mapping.getResponseBody().values().iterator().next();
            response += "<" + generateRequestBodyParameterType(apiBodyMetadata) +">";
        }
        return response;
    }
}
