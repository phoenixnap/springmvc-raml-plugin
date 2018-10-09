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

import org.springframework.web.bind.annotation.RestController;

import com.phoenixnap.oss.ramlplugin.raml2code.data.ApiResourceMetadata;
import com.phoenixnap.oss.ramlplugin.raml2code.rules.Rule;
import com.sun.codemodel.JAnnotationUse;
import com.sun.codemodel.JDefinedClass;

/**
 * Adds the {@literal @}Controller or {@literal @}RestController (depending on
 * Spring Version - 4 by default) annotation to the given JDefinedClass
 *
 *
 * @author kurt paris
 * @author armin.weisser
 * @since 0.4.1
 */
public class SpringRestControllerAnnotationRule implements Rule<JDefinedClass, JAnnotationUse, ApiResourceMetadata> {

	@Override
	public JAnnotationUse apply(ApiResourceMetadata controllerMetadata, JDefinedClass generatableType) {
		return generatableType.annotate(RestController.class);
	}
}
