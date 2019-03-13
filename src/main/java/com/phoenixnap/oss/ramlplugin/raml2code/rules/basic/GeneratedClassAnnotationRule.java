/*
 * Copyright 2002-2019 the original author or authors.
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
package com.phoenixnap.oss.ramlplugin.raml2code.rules.basic;

import javax.annotation.Generated;

import com.phoenixnap.oss.ramlplugin.raml2code.data.ApiResourceMetadata;
import com.phoenixnap.oss.ramlplugin.raml2code.plugin.Config;
import com.phoenixnap.oss.ramlplugin.raml2code.rules.Rule;
import com.sun.codemodel.JAnnotationUse;
import com.sun.codemodel.JDefinedClass;

/**
 * Adds the {@literal @}Generated annotation to the given JDefinedClass
 *
 * @author Aleksandar Stojsavljevic (aleksandars@ccbill.com)
 * @since 2.1.0
 */
public class GeneratedClassAnnotationRule implements Rule<JDefinedClass, JAnnotationUse, ApiResourceMetadata> {

	@Override
	public JAnnotationUse apply(ApiResourceMetadata controllerMetadata, JDefinedClass generatableType) {
		if (Config.isGeneratedAnnotation()) {
			return generatableType.annotate(Generated.class).param("value", Config.DEFAULT_GENERATED_ANNOTATION_VALUE);
		}
		return null;
	}
}
