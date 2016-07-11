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

import static com.phoenixnap.oss.ramlapisync.generation.CodeModelHelper.ext;

import com.phoenixnap.oss.ramlapisync.data.ApiActionMetadata;
import com.phoenixnap.oss.ramlapisync.generation.CodeModelHelper;
import com.phoenixnap.oss.ramlapisync.generation.rules.Rule;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;
import com.sun.codemodel.JType;

/**
 * Generates a method signature for an endpoint defined by an ApiMappingMetadata instance.
 *
 * INPUT:
 * #%RAML 0.8
 * title: myapi
 * mediaType: application/json
 * baseUri: /
 * /base:
 *   /{id}
 *     get:
 *
 * OUTPUT:
 * public ResponseType getBaseById(String id)
 *
 * OR:
 * public ResponseType{@literal <}MyType{@literal >} getBaseById({@literal @}PathVariable String id)
 *
 * OR:
 * public MyType getBaseById({@literal @}PathVariable String id)
 *
 * The parameter and return type configuration depends on the underlying paramsRule and responseTypeRule.
 *
 * @author armin.weisser
 * @since 0.4.1
 */
public class ControllerMethodSignatureRule implements Rule<JDefinedClass, JMethod, ApiActionMetadata> {

    private Rule<JDefinedClass, JType, ApiActionMetadata> responseTypeRule;
    private Rule<CodeModelHelper.JExtMethod, JMethod, ApiActionMetadata> paramsRule;

    public ControllerMethodSignatureRule(
            Rule<JDefinedClass, JType, ApiActionMetadata> responseTypeRule,
            Rule<CodeModelHelper.JExtMethod, JMethod, ApiActionMetadata> paramsRule)
    {
        this.responseTypeRule = responseTypeRule;
        this.paramsRule = paramsRule;
    }

    @Override
    public JMethod apply(ApiActionMetadata endpointMetadata, JDefinedClass generatableType) {
        JType responseType = responseTypeRule.apply(endpointMetadata, generatableType);
        JMethod jMethod = generatableType.method(JMod.PUBLIC, responseType, endpointMetadata.getName());
        jMethod = paramsRule.apply(endpointMetadata, ext(jMethod, generatableType.owner()));
        return jMethod;
    }

}
