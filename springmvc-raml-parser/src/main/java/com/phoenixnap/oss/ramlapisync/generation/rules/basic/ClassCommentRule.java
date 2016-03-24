package com.phoenixnap.oss.ramlapisync.generation.rules.basic;

import com.phoenixnap.oss.ramlapisync.data.ApiControllerMetadata;
import com.phoenixnap.oss.ramlapisync.generation.CodeModelHelper;
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
        generatableType.javadoc().append(comments);
        generatableType.javadoc().append("\n(Generated with springmvc-raml-parser v."+ CodeModelHelper.getVersion()+")");
        return generatableType.javadoc();
    }
}
