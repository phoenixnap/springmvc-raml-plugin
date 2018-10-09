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
package com.phoenixnap.oss.ramlplugin.raml2code.rules;

import java.util.Map;

import org.apache.commons.lang3.BooleanUtils;
import org.springframework.util.CollectionUtils;

import com.phoenixnap.oss.ramlplugin.raml2code.data.ApiActionMetadata;
import com.phoenixnap.oss.ramlplugin.raml2code.data.ApiResourceMetadata;
import com.phoenixnap.oss.ramlplugin.raml2code.rules.spring.SpringControllerInterfaceRule;
import com.phoenixnap.oss.ramlplugin.raml2code.rules.spring.SpringRestControllerAnnotationRule;
import com.sun.codemodel.JAnnotationUse;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JMethod;

/**
 * A code generation Rule that provides a standalone Controller interface with
 * Spring4 annotations. The goal is to generate code that does not have to be
 * manually extended by the user. A raml endpoint called /people for example
 * would lead to the following interface only:
 *
 * // 1. Controller Interface {@literal @}RestController
 * {@literal @}RequestMapping("/people") interface PeopleController {
 * {@literal @}RequestMapping(value="", method=RequestMethod.GET) ResponseEntity
 * getPeople(); }
 *
 * Now all the user has to do is to implement a this interface. This way he can
 * implement the endpoint without altering the generated code.
 *
 * @author armin.weisser
 * @since 0.4.1
 */
public class Spring4ControllerInterfaceRule extends SpringControllerInterfaceRule {

	protected Rule<JDefinedClass, JAnnotationUse, ApiResourceMetadata> getControllerAnnotationRule() {
		return new SpringRestControllerAnnotationRule();
	}

	@Override
	protected Rule<JMethod, JAnnotationUse, ApiActionMetadata> getResponseBodyAnnotationRule() {
		return null; // ResponseBody not needed for RestController
	}

	@Override
	public void applyConfiguration(Map<String, String> configuration) {
		super.applyConfiguration(configuration);
		if (!CollectionUtils.isEmpty(configuration)) {
			if (configuration.containsKey(SHORTCUT_METHOD_MAPPINGS)) {
				setUseShortcutMethodMappings(BooleanUtils.toBoolean(configuration.get(SHORTCUT_METHOD_MAPPINGS)));
			}
		}
	}

}
