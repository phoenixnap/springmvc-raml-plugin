package com.phoenixnap.oss.ramlapisync.generation.rules.basic;

import com.phoenixnap.oss.ramlapisync.data.ApiMappingMetadata;
import com.phoenixnap.oss.ramlapisync.generation.rules.Rule;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;
import com.sun.codemodel.JType;

/**
 * @author armin.weisser
 * @since 0.3.2
 */
public class ControllerMethodSignatureRule implements Rule<JDefinedClass, JMethod, ApiMappingMetadata> {

    private Rule<JDefinedClass, JType, ApiMappingMetadata> responseTypeRule;

    public ControllerMethodSignatureRule(Rule<JDefinedClass, JType, ApiMappingMetadata> responseTypeRule) {
        this.responseTypeRule = responseTypeRule;
    }

    @Override
    public JMethod apply(ApiMappingMetadata endpointMetadata, JDefinedClass generatableType) {
        JType responseType = responseTypeRule.apply(endpointMetadata, generatableType);
        return generatableType.method(JMod.PUBLIC, responseType, endpointMetadata.getName());
    }

}
