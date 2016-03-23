package com.phoenixnap.oss.ramlapisync.generation.rules;

import com.phoenixnap.oss.ramlapisync.data.ApiControllerMetadata;
import com.phoenixnap.oss.ramlapisync.generation.InvalidModelException;
import com.sun.codemodel.JAnnotationUse;
import com.sun.codemodel.JDefinedClass;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author armin.weisser
 * @since 0.3.2
 */
public class Spring4RequestMappingClassAnnotationRule implements Rule<JDefinedClass, JAnnotationUse, ApiControllerMetadata> {
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
            throw new InvalidModelException("Your model contains an invalid media type", e);
        }

        return requestMapping;
    }

    private String generateMediaType(ApiControllerMetadata controllerMetadata) {
        String ramlMediaType = controllerMetadata.getDocument().getMediaType();
        return MediaType.parseMediaType(ramlMediaType).toString();
    }
}
