package com.phoenixnap.oss.ramlapisync.generation.rules.basic;

import com.phoenixnap.oss.ramlapisync.data.ApiControllerMetadata;
import com.phoenixnap.oss.ramlapisync.generation.rules.Rule;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JDocComment;

/**
 * @author armin.weisser
 * @since 0.3.2
 */
public class ClassCommentRule implements Rule<JDefinedClass, JDocComment, ApiControllerMetadata> {
    @Override
    public JDocComment apply(ApiControllerMetadata controllerMetadata, JDefinedClass generatableType) {
        String comments = "No description";
        if(controllerMetadata.getDescription() != null) {
            comments = controllerMetadata.getDescription();
        }
        return generatableType.javadoc().append(comments);
    }
}
