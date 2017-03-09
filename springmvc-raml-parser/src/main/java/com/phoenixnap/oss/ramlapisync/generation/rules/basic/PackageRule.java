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
package com.phoenixnap.oss.ramlapisync.generation.rules.basic;

import org.springframework.util.StringUtils;

import com.phoenixnap.oss.ramlapisync.data.ApiResourceMetadata;
import com.phoenixnap.oss.ramlapisync.generation.rules.Rule;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JPackage;

/**
 * Generates a simple package declaration based on the base package defined in ApiControllerMetadata.
 * If no base package is defined the empty root package is used by default.
 *
 * INPUT:
 * ApiControllerMetadata (with base package set to "my.api"
 *
 * OUTPUT:
 * package my.api;
 *
 * @author armin.weisser
 * @since 0.4.1
 */
public class PackageRule implements Rule<JCodeModel, JPackage, ApiResourceMetadata> {

    @Override
    public JPackage apply(ApiResourceMetadata controllerMetadata, JCodeModel generatableType) {
        if(StringUtils.hasText(controllerMetadata.getBasePackage())) {
            return generatableType._package(controllerMetadata.getBasePackage());
        }
        return generatableType.rootPackage();
    }
}
