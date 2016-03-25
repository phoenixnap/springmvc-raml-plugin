package com.phoenixnap.oss.ramlapisync.generation.rules.basic;

import com.phoenixnap.oss.ramlapisync.data.ApiMappingMetadata;
import com.phoenixnap.oss.ramlapisync.generation.rules.Rule;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JInvocation;
import com.sun.codemodel.JMethod;
import org.springframework.util.StringUtils;

/**
 * Generates a method body that delegates the method call to a private field.
 * The field is not setup in this rule, so you must make sure, that this field exists in the generated code,
 * befor applying this rule.
 *
 * INPUT:
 * #%RAML 0.8
 * title: myapi
 * mediaType: application/json
 * baseUri: /
 * /base:
 *   /{id}
 *     get:
 *
 * OUTPUT:
 * return this.delegate.getBaseById(id);
 *
 * The name of the field can be configured. Default is "delegate".
 *
 * @author armin.weisser
 * @since 0.3.2
 */
public class DelegatingMethodBodyRule implements Rule<JMethod, JMethod, ApiMappingMetadata> {

    private String delegeeFieldName = "delegate";

    public DelegatingMethodBodyRule(String delegeeFieldName) {
        if(!StringUtils.isEmpty(delegeeFieldName)) {
            this.delegeeFieldName = delegeeFieldName;
        }
    }

    @Override
    public JMethod apply(ApiMappingMetadata endpointMetadata, JMethod generatableType) {
        JInvocation jInvocation = JExpr._this().ref(delegeeFieldName).invoke(generatableType);
        generatableType.params().forEach(p -> jInvocation.arg(p));
        generatableType.body()._return(jInvocation);
        return generatableType;
    }
}
