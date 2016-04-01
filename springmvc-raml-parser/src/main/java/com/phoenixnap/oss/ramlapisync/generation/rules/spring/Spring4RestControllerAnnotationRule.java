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
package com.phoenixnap.oss.ramlapisync.generation.rules.spring;

import com.phoenixnap.oss.ramlapisync.data.ApiControllerMetadata;
import com.phoenixnap.oss.ramlapisync.generation.rules.Rule;
import com.sun.codemodel.JAnnotationUse;
import com.sun.codemodel.JDefinedClass;
import org.springframework.web.bind.annotation.RestController;

/**
 * Adds the {@literal @}RestController annotation to the given JDefinedClass
 *
 * @author armin.weisser
 * @since 0.3.2
 */
public class Spring4RestControllerAnnotationRule implements Rule<JDefinedClass, JAnnotationUse, ApiControllerMetadata> {
    @Override
    public JAnnotationUse apply(ApiControllerMetadata controllerMetadata, JDefinedClass generatableType) {
        return generatableType.annotate(RestController.class);
    }
}
