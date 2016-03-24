package com.phoenixnap.oss.ramlapisync.generation.rules.spring;

import com.phoenixnap.oss.ramlapisync.data.ApiMappingMetadata;
import com.phoenixnap.oss.ramlapisync.generation.rules.Rule;
import com.sun.codemodel.JAnnotationUse;
import com.sun.codemodel.JMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author armin.weisser
 * @since 0.3.2
 */
public class SpringResponseBodyMethodAnnotationRule implements Rule<JMethod, JAnnotationUse, ApiMappingMetadata> {
    @Override
    public JAnnotationUse apply(ApiMappingMetadata endpointMetadata, JMethod generatableType) {
        if (!endpointMetadata.getResponseBody().isEmpty()) {
            return generatableType.annotate(ResponseBody.class);
        }
        return null;
    }

}
