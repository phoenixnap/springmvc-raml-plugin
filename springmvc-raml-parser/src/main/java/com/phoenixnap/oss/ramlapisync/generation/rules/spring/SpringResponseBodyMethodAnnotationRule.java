/*
 * Copyright 2002-2017 the original author or authors.
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
package com.phoenixnap.oss.ramlapisync.generation.rules.spring;

import org.springframework.web.bind.annotation.ResponseBody;

import com.phoenixnap.oss.ramlapisync.data.ApiActionMetadata;
import com.phoenixnap.oss.ramlapisync.generation.rules.Rule;
import com.sun.codemodel.JAnnotationUse;
import com.sun.codemodel.JMethod;

/**
 * Adds a @ResponseBody annotation to the method if there is response body declared in this endpoints metadata.
 *
 * @author armin.weisser
 * @since 0.4.1
 */
public class SpringResponseBodyMethodAnnotationRule implements Rule<JMethod, JAnnotationUse, ApiActionMetadata> {
    @Override
    public JAnnotationUse apply(ApiActionMetadata endpointMetadata, JMethod generatableType) {
        if (!endpointMetadata.getResponseBody().isEmpty()) {
            return generatableType.annotate(ResponseBody.class);
        }
        return null;
    }

}
