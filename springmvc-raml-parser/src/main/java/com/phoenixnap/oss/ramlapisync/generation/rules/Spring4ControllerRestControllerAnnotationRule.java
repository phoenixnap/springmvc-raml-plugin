package com.phoenixnap.oss.ramlapisync.generation.rules;

import com.phoenixnap.oss.ramlapisync.data.ApiControllerMetadata;
import com.sun.codemodel.JAnnotationUse;
import com.sun.codemodel.JDefinedClass;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author armin.weisser
 */
public class Spring4ControllerRestControllerAnnotationRule implements ControllerRule<JDefinedClass, JAnnotationUse> {
    @Override
    public JAnnotationUse apply(ApiControllerMetadata controllerMetadata, JDefinedClass generatableType) {
        return generatableType.annotate(RestController.class);
    }
}
