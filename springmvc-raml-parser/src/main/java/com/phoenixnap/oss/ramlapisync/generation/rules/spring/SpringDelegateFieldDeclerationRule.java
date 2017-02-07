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

import com.phoenixnap.oss.ramlapisync.data.ApiResourceMetadata;
import com.phoenixnap.oss.ramlapisync.generation.exception.RuleCanNotProcessModelException;
import com.phoenixnap.oss.ramlapisync.generation.rules.Rule;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JFieldVar;
import com.sun.codemodel.JMod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

/**
 * Creates a private field declaration with a Spring {@literal @}Autowired annotation.
 * The type of the field is derived from the super class of the given JDefinedClass.
 * The name of the field can be injected by the caller. Default is "delegate".
 *
 * EXAMPLE OUTPUT:
 * {@literal @}Autowired
 * BaseClass delegate;
 *
 * @author armin.weisser
 * @since 0.4.1
 */
public class SpringDelegateFieldDeclerationRule implements Rule<JDefinedClass, JFieldVar, ApiResourceMetadata> {

    private String delegateFieldName = "delegate";

    public SpringDelegateFieldDeclerationRule(String delegateFieldName) {
        if(StringUtils.hasText(delegateFieldName)) {
            this.delegateFieldName = delegateFieldName;
        }
    }

    @Override
    public JFieldVar apply(ApiResourceMetadata controllerMetadata, JDefinedClass generatableType) {
        if(!generatableType._implements().hasNext()) {
            throw new RuleCanNotProcessModelException("The class "+generatableType.fullName()+ " does not implement a super class that can be delegated to.");
        }
        JClass controllerInterface = generatableType._implements().next();
        JFieldVar field = generatableType.field(JMod.PRIVATE, controllerInterface, delegateFieldName);
        field.annotate(Autowired.class);
        return field;
    }
}
