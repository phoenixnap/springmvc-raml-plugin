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

import com.phoenixnap.oss.ramlapisync.data.ApiActionMetadata;
import com.phoenixnap.oss.ramlapisync.generation.rules.Rule;
import com.sun.codemodel.JDocComment;
import com.sun.codemodel.JMethod;

/**
 * A simple method comment.
 * If no description is provided by the RAML spec for this enpoint a simple "No description" is added as method comment.
 *
 * @author armin.weisser
 * @since 0.4.1
 */
public class MethodCommentRule implements Rule<JMethod,JDocComment,ApiActionMetadata> {
    @Override
    public JDocComment apply(ApiActionMetadata endpointMetadata, JMethod generatableType) {
        String comments = "No description";
        if(endpointMetadata.getDescription() != null) {
            comments = endpointMetadata.getDescription();
        }
        return generatableType.javadoc().append(comments);
    }
}
