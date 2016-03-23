package com.phoenixnap.oss.ramlapisync.generation.rules;

import com.phoenixnap.oss.ramlapisync.data.ApiMappingMetadata;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;
import org.springframework.http.ResponseEntity;

/**
 * @author armin.weisser
 * @since 0.3.2
 */
public class Spring4ControllerMethodSignatureRule implements Rule<JDefinedClass, JMethod, ApiMappingMetadata> {

    @Override
    public JMethod apply(ApiMappingMetadata endpointMetadata, JDefinedClass generatableType) {
        return generatableType.method(JMod.PUBLIC, ResponseEntity.class, endpointMetadata.getName());
    }
}
