package com.phoenixnap.oss.ramlapisync.generation.rules.spring;

import com.phoenixnap.oss.ramlapisync.data.ApiControllerMetadata;
import com.phoenixnap.oss.ramlapisync.generation.rules.Rule;
import com.sun.codemodel.JAnnotationUse;
import com.sun.codemodel.JDefinedClass;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author armin.weisser
 * @since 0.3.2
 */
public class Spring4RestControllerAnnotationRule implements Rule<JDefinedClass, JAnnotationUse, ApiControllerMetadata> {
    @Override
    public JAnnotationUse apply(ApiControllerMetadata controllerMetadata, JDefinedClass generatableType) {
        return generatableType.annotate(RestController.class);
    }
}
