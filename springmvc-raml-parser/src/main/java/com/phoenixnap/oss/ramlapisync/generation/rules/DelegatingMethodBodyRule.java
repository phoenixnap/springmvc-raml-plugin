package com.phoenixnap.oss.ramlapisync.generation.rules;

import com.phoenixnap.oss.ramlapisync.data.ApiMappingMetadata;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JMethod;

/**
 * @author armin.weisser
 * @since 0.3.2
 */
public class DelegatingMethodBodyRule implements Rule<JMethod, JMethod, ApiMappingMetadata> {

    private final String delegeeFieldName;

    public DelegatingMethodBodyRule(String delegeeFieldName) {
        this.delegeeFieldName = delegeeFieldName;
    }

    @Override
    public JMethod apply(ApiMappingMetadata endpointMetadata, JMethod generatableType) {
        generatableType.body()._return(
                JExpr._this().ref(delegeeFieldName).invoke(generatableType)
        );
        return generatableType;
    }
}
