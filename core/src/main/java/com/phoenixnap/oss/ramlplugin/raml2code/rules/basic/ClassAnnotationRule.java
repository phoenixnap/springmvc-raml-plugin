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
package com.phoenixnap.oss.ramlplugin.raml2code.rules.basic;

import java.lang.annotation.Annotation;

import com.phoenixnap.oss.ramlplugin.raml2code.data.ApiResourceMetadata;
import com.phoenixnap.oss.ramlplugin.raml2code.rules.Rule;
import com.sun.codemodel.JAnnotationUse;
import com.sun.codemodel.JDefinedClass;

/**
 * Adds the specified annotation to the given JDefinedClass
 *
 * @author kurt paris
 * @since 0.5.0
 */
public class ClassAnnotationRule implements Rule<JDefinedClass, JAnnotationUse, ApiResourceMetadata> {

	/**
	 * Annotation to support
	 */
	private Class<? extends Annotation> annotationType;

	public ClassAnnotationRule(Class<? extends Annotation> annotationType) {
		this.annotationType = annotationType;
	}

	@Override
	public JAnnotationUse apply(ApiResourceMetadata controllerMetadata, JDefinedClass generatableType) {
		return generatableType.annotate(annotationType);
	}
}
