package com.phoenixnap.oss.ramlapisync.generation.rules.basic;

import com.phoenixnap.oss.ramlapisync.data.ApiMappingMetadata;
import com.phoenixnap.oss.ramlapisync.generation.rules.Rule;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JInvocation;
import com.sun.codemodel.JMethod;
import org.springframework.util.StringUtils;

/**
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
