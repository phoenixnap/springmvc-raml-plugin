package com.phoenixnap.oss.ramlapisync.generation.rules.basic;

import com.phoenixnap.oss.ramlapisync.data.ApiMappingMetadata;
import com.phoenixnap.oss.ramlapisync.generation.rules.Rule;
import com.sun.codemodel.JDocComment;
import com.sun.codemodel.JMethod;

/**
 * @author armin.weisser
 */
public class MethodCommentRule implements Rule<JMethod,JDocComment,ApiMappingMetadata> {
    @Override
    public JDocComment apply(ApiMappingMetadata endpointMetadata, JMethod generatableType) {
        String comments = "No description";
        if(endpointMetadata.getDescription() != null) {
            comments = endpointMetadata.getDescription();
        }
        return generatableType.javadoc().append(comments);
    }
}
