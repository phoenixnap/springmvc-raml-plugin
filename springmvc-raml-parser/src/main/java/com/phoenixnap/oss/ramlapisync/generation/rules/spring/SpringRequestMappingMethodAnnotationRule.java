package com.phoenixnap.oss.ramlapisync.generation.rules.spring;

import com.phoenixnap.oss.ramlapisync.data.ApiMappingMetadata;
import com.phoenixnap.oss.ramlapisync.generation.rules.Rule;
import com.sun.codemodel.JAnnotationUse;
import com.sun.codemodel.JMethod;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * @author armin.weisser
 * @since 0.3.2
 */
public class SpringRequestMappingMethodAnnotationRule implements Rule<JMethod, JAnnotationUse, ApiMappingMetadata> {

    @Override
    public JAnnotationUse apply(ApiMappingMetadata endpointMetadata, JMethod generatableType) {
        JAnnotationUse requestMappingAnnotation = generatableType.annotate(RequestMapping.class);
        requestMappingAnnotation.param("value", endpointMetadata.getUrl());
        requestMappingAnnotation.param("method", RequestMethod.valueOf(endpointMetadata.getActionType().name()));
        return requestMappingAnnotation;
    }

}
