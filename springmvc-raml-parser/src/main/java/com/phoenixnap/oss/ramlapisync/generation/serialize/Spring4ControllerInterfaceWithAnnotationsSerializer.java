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
package com.phoenixnap.oss.ramlapisync.generation.serialize;

import com.phoenixnap.oss.ramlapisync.data.ApiBodyMetadata;
import com.phoenixnap.oss.ramlapisync.data.ApiControllerMetadata;
import com.phoenixnap.oss.ramlapisync.data.ApiMappingMetadata;

/**
 * Serializer that will create a JAva interface annotated with Spring MVC annotations
 * 
 * @author armin.weisser
 * @since 0.3.1
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
