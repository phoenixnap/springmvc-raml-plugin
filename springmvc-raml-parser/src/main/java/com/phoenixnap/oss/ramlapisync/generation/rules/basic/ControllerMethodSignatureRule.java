package com.phoenixnap.oss.ramlapisync.generation.rules.basic;

import com.phoenixnap.oss.ramlapisync.data.ApiMappingMetadata;
import com.phoenixnap.oss.ramlapisync.generation.CodeModelHelper;
import com.phoenixnap.oss.ramlapisync.generation.rules.Rule;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;
import com.sun.codemodel.JType;

import static com.phoenixnap.oss.ramlapisync.generation.CodeModelHelper.ext;

/**
 * @author armin.weisser
 * @since 0.3.2
 */
public class ControllerMethodSignatureRule implements Rule<JDefinedClass, JMethod, ApiMappingMetadata> {

    private Rule<JDefinedClass, JType, ApiMappingMetadata> responseTypeRule;
    private Rule<CodeModelHelper.JExtMethod, JMethod, ApiMappingMetadata> paramsRule;

    public ControllerMethodSignatureRule(
            Rule<JDefinedClass, JType, ApiMappingMetadata> responseTypeRule,
            Rule<CodeModelHelper.JExtMethod, JMethod, ApiMappingMetadata> paramsRule)
    {
        this.responseTypeRule = responseTypeRule;
        this.paramsRule = paramsRule;
    }

    @Override
    public JMethod apply(ApiMappingMetadata endpointMetadata, JDefinedClass generatableType) {
        JType responseType = responseTypeRule.apply(endpointMetadata, generatableType);
        JMethod jMethod = generatableType.method(JMod.PUBLIC, responseType, endpointMetadata.getName());
        jMethod = paramsRule.apply(endpointMetadata, ext(jMethod, generatableType.owner()));
        return jMethod;
    }

}
