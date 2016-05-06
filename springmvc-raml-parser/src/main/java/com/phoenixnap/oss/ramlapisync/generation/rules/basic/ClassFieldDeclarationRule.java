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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import com.phoenixnap.oss.ramlapisync.data.ApiControllerMetadata;
import com.phoenixnap.oss.ramlapisync.generation.rules.Rule;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JFieldVar;
import com.sun.codemodel.JMod;

/**
 * Creates a private field declaration for a RestTemplateClass with a Spring {@literal @}Autowired annotation.
 * 
 * The name of the field can be injected by the caller. Default is "delegate".
 *
 * EXAMPLE OUTPUT:
 * {@literal @}Autowired
 * BaseClass className;
 *
 * @author kurtpa
 * @since 0.5.0
 */
public class ClassFieldDeclarationRule implements Rule<JDefinedClass, JFieldVar, ApiControllerMetadata> {

    private String fieldName = "restTemplate";

    private Class<?> fieldClazz;
    
    private boolean autowire = true;
    
    public ClassFieldDeclarationRule(String restTemplateFieldName, Class<?> fieldClazz, boolean autowire) {
    	this(restTemplateFieldName, fieldClazz);
    	this.autowire = autowire;
    }
    
    public ClassFieldDeclarationRule(String restTemplateFieldName, Class<?> fieldClazz) {
    	if (fieldClazz != null) {
        	this.fieldClazz = fieldClazz;
        }else{
        	throw new IllegalStateException("Class not specified"); 
        }
    	if(StringUtils.hasText(restTemplateFieldName)) {
            this.fieldName = restTemplateFieldName;
        } else {
        	this.fieldName = StringUtils.unqualify(fieldClazz.getSimpleName());
        }
        
    }

    @Override
    public JFieldVar apply(ApiControllerMetadata controllerMetadata, JDefinedClass generatableType) {
        JFieldVar field = generatableType.field(JMod.PRIVATE, this.fieldClazz, this.fieldName);
        if (autowire) {
        	field.annotate(Autowired.class);
        }
        return field;
    }
}

