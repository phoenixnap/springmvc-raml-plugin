/*
 * Copyright 2002-2017 the original author or authors.
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
package com.phoenixnap.oss.ramlapisync.generation.rules.spring;

import com.phoenixnap.oss.ramlapisync.data.ApiResourceMetadata;
import com.phoenixnap.oss.ramlapisync.generation.exception.InvalidCodeModelException;
import com.phoenixnap.oss.ramlapisync.generation.rules.Rule;
import com.sun.codemodel.JAnnotationUse;
import com.sun.codemodel.JDefinedClass;

import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Adds a {@literal @}RequestMapping annotation at class level
 * The "value" of the {@literal @}RequestMapping is the controller url from the ApiControllerMetadata instance.
 * If the ApiControllerMetadata defines an explicit media type the "produces" attribute will be set to this media type.
 *
 * INPUT:
 * #%RAML 0.8
 * title: myapi
 * mediaType: application/json
 * baseUri: /api
 * /base:
 *   get:
 *
 * OUTPUT:
 * {@literal @}RequestMapping(value="/api/base", produces="application/json")
 *
 * @author armin.weisser
 * @since 0.4.1
 */
public class SpringRequestMappingClassAnnotationRule implements Rule<JDefinedClass, JAnnotationUse, ApiResourceMetadata> {
    @Override
    public JAnnotationUse apply(ApiResourceMetadata controllerMetadata, JDefinedClass generatableType) {
        JAnnotationUse requestMapping = generatableType.annotate(RequestMapping.class);
        requestMapping.param("value", controllerMetadata.getControllerUrl());
        try {
            String mediaType = generateMediaType(controllerMetadata);
            if(mediaType != null) {
                requestMapping.param("produces", mediaType);
            }
        } catch (Exception e) {
            throw new InvalidCodeModelException("Your model contains an invalid media type", e);
        }

        return requestMapping;
    }

    private String generateMediaType(ApiResourceMetadata controllerMetadata) {
        String ramlMediaType = controllerMetadata.getDocument().getMediaType();
        if (!StringUtils.hasText(ramlMediaType)) {
        	return null;
        }
        return MediaType.parseMediaType(ramlMediaType).toString();
    }
}
