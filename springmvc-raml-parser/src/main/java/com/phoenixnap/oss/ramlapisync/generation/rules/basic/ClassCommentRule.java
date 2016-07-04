/*
 * Copyright 2002-2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package com.phoenixnap.oss.ramlapisync.generation.rules.basic;

import com.phoenixnap.oss.ramlapisync.data.ApiResourceMetadata;
import com.phoenixnap.oss.ramlapisync.generation.CodeModelHelper;
import com.phoenixnap.oss.ramlapisync.generation.rules.Rule;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JDocComment;

/**
 * Generates a simple class comment.
 * If no description is provided by the RAML spec a simple "No description" is added as class level comment.
 *
 * @author armin.weisser
 * @since 0.4.1
 */
public class ClassCommentRule implements Rule<JDefinedClass, JDocComment, ApiResourceMetadata> {

    @Override
    public JDocComment apply(ApiResourceMetadata controllerMetadata, JDefinedClass generatableType) {
        String comments = "No description";
        if(controllerMetadata.getDescription() != null) {
            comments = controllerMetadata.getDescription();
        }
        generatableType.javadoc().append(comments);
        generatableType.javadoc().append("\n(Generated with springmvc-raml-parser v."+ CodeModelHelper.getVersion()+")");
        return generatableType.javadoc();
    }
}
