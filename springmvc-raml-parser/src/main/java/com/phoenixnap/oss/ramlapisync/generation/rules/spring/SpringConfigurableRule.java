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

import java.util.Map;

import org.apache.commons.lang3.BooleanUtils;
import org.springframework.util.CollectionUtils;

import com.phoenixnap.oss.ramlapisync.data.ApiResourceMetadata;
import com.phoenixnap.oss.ramlapisync.generation.rules.ConfigurableRule;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;

/**
 * Common parent for configurable spring rules
 * 
 * @author kurtpa
 * @since 0.9.1
 *
 */
public abstract class SpringConfigurableRule implements ConfigurableRule<JCodeModel, JDefinedClass, ApiResourceMetadata> {
	
 	public static final String CALLABLE_RESPONSE_CONFIGURATION = "callableResponse";

 	public static final String PARAMETER_JAVADOC_CONFIGURATION = "addParameterJavadoc";
 			
 	public static final String ARRAY_PARAMETER_CONFIGURATION = "allowArrayParameters";
 	
 	

    private boolean callableResponse = false;
    private boolean addParameterJavadoc = false;
    private boolean allowArrayParameters = true;

   
    
    @Override
    public void applyConfiguration(Map<String, String> configuration) {
        if(!CollectionUtils.isEmpty(configuration)) {
            if(configuration.containsKey(CALLABLE_RESPONSE_CONFIGURATION)) {
                setCallableResponse(BooleanUtils.toBoolean(configuration.get(CALLABLE_RESPONSE_CONFIGURATION)));
            }
            if(configuration.containsKey(PARAMETER_JAVADOC_CONFIGURATION)) {
            	setAddParameterJavadoc(BooleanUtils.toBoolean(configuration.get(PARAMETER_JAVADOC_CONFIGURATION)));
            }
            if(configuration.containsKey(ARRAY_PARAMETER_CONFIGURATION)) {
            	setAllowArrayParameters(BooleanUtils.toBoolean(configuration.get(ARRAY_PARAMETER_CONFIGURATION)));
            }
        }
    }

	public boolean isAddParameterJavadoc() {
		return addParameterJavadoc;
	}

	public void setAddParameterJavadoc(boolean addParameterJavadoc) {
		this.addParameterJavadoc = addParameterJavadoc;
	}

	public boolean isAllowArrayParameters() {
		return allowArrayParameters;
	}

	public void setAllowArrayParameters(boolean allowArrayParameters) {
		this.allowArrayParameters = allowArrayParameters;
	}
	
	public boolean isCallableResponse() {
		return callableResponse;
	}

	public void setCallableResponse(boolean callableResponse) {
		this.callableResponse = callableResponse;
	}

}
