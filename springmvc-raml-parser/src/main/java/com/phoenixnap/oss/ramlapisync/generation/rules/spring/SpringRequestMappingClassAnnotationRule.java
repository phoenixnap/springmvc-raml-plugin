package com.phoenixnap.oss.ramlapisync.generation.rules.spring;

import com.phoenixnap.oss.ramlapisync.data.ApiControllerMetadata;
import com.phoenixnap.oss.ramlapisync.generation.exception.InvalidCodeModelException;
import com.phoenixnap.oss.ramlapisync.generation.rules.Rule;
import com.sun.codemodel.JAnnotationUse;
import com.sun.codemodel.JDefinedClass;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Adds a @RequestMapping annotation at class level
 * The "value" of the @RequestMapping is the controller url from the ApiControllerMetadata instance.
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
 * @RequestMapping(value="/api/base", produces="application/json")
 *
 * @author armin.weisser
 * @since 0.3.2
 */
public class SpringRequestMappingClassAnnotationRule implements Rule<JDefinedClass, JAnnotationUse, ApiControllerMetadata> {
    @Override
    public JAnnotationUse apply(ApiControllerMetadata controllerMetadata, JDefinedClass generatableType) {
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

    private String generateMediaType(ApiControllerMetadata controllerMetadata) {
        String ramlMediaType = controllerMetadata.getDocument().getMediaType();
        return MediaType.parseMediaType(ramlMediaType).toString();
    }
}
