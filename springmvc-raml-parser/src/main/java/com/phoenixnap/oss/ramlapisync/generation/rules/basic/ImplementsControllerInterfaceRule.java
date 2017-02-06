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

import com.phoenixnap.oss.ramlapisync.data.ApiResourceMetadata;
import com.phoenixnap.oss.ramlapisync.generation.rules.Rule;
import com.sun.codemodel.JDefinedClass;

/**
 * Generates an implements expression based on a given JDefinedClass.
 *
 * INPUT:
 * JDefinedClass interfaceType
 *
 * OUTPUT:
 * implements {@literal <}interfaceType{@literal >}
 *
 * @author armin.weisser
 * @since 0.4.1
 */
public class ImplementsControllerInterfaceRule implements Rule<JDefinedClass, JDefinedClass, ApiResourceMetadata> {

    private final JDefinedClass interfaceType;

    public ImplementsControllerInterfaceRule(JDefinedClass interfaceType) {
        this.interfaceType = interfaceType;
    }

    @Override
    public JDefinedClass apply(ApiResourceMetadata controllerMetadata, JDefinedClass generatableType) {
        return generatableType._implements(this.interfaceType);
    }

}