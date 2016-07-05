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
import com.phoenixnap.oss.ramlapisync.generation.CodeModelHelper.JExtMethod;
import com.phoenixnap.oss.ramlapisync.generation.rules.Rule;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JInvocation;
import com.sun.codemodel.JMethod;

import org.springframework.util.StringUtils;

/**
 * Generates a method body that delegates the method call to a private field.
 * The field is not setup in this rule, so you must make sure, that this field exists in the generated code,
 * befor applying this rule.
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
 * return this.delegate.getBaseById(id);
 *
 * The name of the field can be configured. Default is "delegate".
 *
 * @author armin.weisser
 * @since 0.4.1
 */
public class DelegatingMethodBodyRule implements Rule<JExtMethod, JMethod, ApiActionMetadata> {

    private String delegateFieldName = "delegate";

    public DelegatingMethodBodyRule(String delegateFieldName) {
        if(StringUtils.hasText(delegateFieldName)) {
            this.delegateFieldName = delegateFieldName;
        }
    }

    @Override
    public JMethod apply(ApiActionMetadata endpointMetadata, JExtMethod generatableType) {
    	JMethod jMethod = generatableType.get();
        JInvocation jInvocation = JExpr._this().ref(delegateFieldName).invoke(jMethod);
        jMethod.params().forEach(p -> jInvocation.arg(p));
        jMethod.body()._return(jInvocation);
        return jMethod;
    }
}
