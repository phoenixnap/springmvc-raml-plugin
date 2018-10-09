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
package com.phoenixnap.oss.ramlplugin.raml2code.rules.spring;

import java.util.Map;

import org.apache.commons.lang3.BooleanUtils;
import org.springframework.util.CollectionUtils;

import com.phoenixnap.oss.ramlplugin.raml2code.data.ApiActionMetadata;
import com.phoenixnap.oss.ramlplugin.raml2code.data.ApiResourceMetadata;
import com.phoenixnap.oss.ramlplugin.raml2code.rules.ConfigurableRule;
import com.phoenixnap.oss.ramlplugin.raml2code.rules.Rule;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JType;

/**
 * Common parent for configurable spring rules
 * 
 * @author kurtpa
 * @since 0.9.1
 *
 */
public abstract class SpringConfigurableRule implements ConfigurableRule<JCodeModel, JDefinedClass, ApiResourceMetadata> {

	public static final String CALLABLE_RESPONSE_CONFIGURATION = "callableResponse";

	public static final String DEFERRED_RESULT_RESPONSE_CONFIGURATION = "deferredResultResponse";

	public static final String PARAMETER_JAVADOC_CONFIGURATION = "addParameterJavadoc";

	public static final String ARRAY_PARAMETER_CONFIGURATION = "allowArrayParameters";

	public static final String SIMPLE_RETURN_TYPES = "simpleReturnTypes";

	public static final String SHORTCUT_METHOD_MAPPINGS = "useShortcutMethodMappings";

	private boolean callableResponse = false;
	private boolean deferredResultResponse = false;
	private boolean addParameterJavadoc = false;
	private boolean allowArrayParameters = true;
	/**
	 * Can only be set to true if <b>SpringControllerInterface</b> is used for
	 * now
	 */
	private boolean simpleReturnTypes = false;
	private boolean useShortcutMethodMappings = false;

	@Override
	public void applyConfiguration(Map<String, String> configuration) {
		if (!CollectionUtils.isEmpty(configuration)) {
			if (configuration.containsKey(CALLABLE_RESPONSE_CONFIGURATION)) {
				setCallableResponse(BooleanUtils.toBoolean(configuration.get(CALLABLE_RESPONSE_CONFIGURATION)));
			}
			if (configuration.containsKey(DEFERRED_RESULT_RESPONSE_CONFIGURATION)) {
				setDeferredResultResponse(BooleanUtils.toBoolean(configuration.get(DEFERRED_RESULT_RESPONSE_CONFIGURATION)));
			}
			if (configuration.containsKey(PARAMETER_JAVADOC_CONFIGURATION)) {
				setAddParameterJavadoc(BooleanUtils.toBoolean(configuration.get(PARAMETER_JAVADOC_CONFIGURATION)));
			}
			if (configuration.containsKey(ARRAY_PARAMETER_CONFIGURATION)) {
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

	public boolean isUseShortcutMethodMappings() {
		return useShortcutMethodMappings;
	}

	public void setUseShortcutMethodMappings(boolean useShortcutMethodMappings) {
		this.useShortcutMethodMappings = useShortcutMethodMappings;
	}

	public boolean isSimpleReturnTypes() {
		return simpleReturnTypes;
	}

	public void setSimpleReturnTypes(boolean simpleReturnTypes) {
		this.simpleReturnTypes = simpleReturnTypes;
	}

	public boolean isDeferredResultResponse() {
		return deferredResultResponse;
	}

	public void setDeferredResultResponse(boolean deferredResultResponse) {
		this.deferredResultResponse = deferredResultResponse;
	}

	protected Rule<JDefinedClass, JType, ApiActionMetadata> getReturnTypeRule(boolean useSimpleReturnType) {
		if (isCallableResponse()) {
			return new SpringCallableResponseEntityRule();
		} else if (isDeferredResultResponse()) {
			return new SpringDeferredResultResponseEntityRule();
		} else if (useSimpleReturnType && isSimpleReturnTypes()) {
			return new SpringObjectReturnTypeRule();
		} else {
			return new SpringResponseEntityRule();
		}
	}

}
