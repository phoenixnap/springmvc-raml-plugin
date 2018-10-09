/*
 * Copyright 2002-2018 the original author or authors.
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import com.phoenixnap.oss.ramlplugin.raml2code.data.ApiActionMetadata;
import com.phoenixnap.oss.ramlplugin.raml2code.data.ApiParameterMetadata;
import com.phoenixnap.oss.ramlplugin.raml2code.helpers.CodeModelHelper;
import com.sun.codemodel.JAnnotationUse;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JVar;

/**
 * Overrides method parameters set by {@link SpringMethodParamsRule}.
 * 
 * 
 * @author Aleksandar Stojsavljevic (aleksandars@ccbill.com)
 * @since 2.0.4
 */
public class SpringFeignClientMethodParamsRule extends SpringMethodParamsRule {

	private static final List<String> ANNOTATIONS_TO_OVERRIDE = new ArrayList<String>();

	static {
		ANNOTATIONS_TO_OVERRIDE.add(RequestParam.class.getName());
		ANNOTATIONS_TO_OVERRIDE.add(RequestHeader.class.getName());
		ANNOTATIONS_TO_OVERRIDE.add(PathVariable.class.getName());

	}

	@Override
	protected JVar paramQueryForm(ApiParameterMetadata paramMetaData, CodeModelHelper.JExtMethod generatableType,
			ApiActionMetadata endpointMetadata) {

		JVar paramQueryForm = super.paramQueryForm(paramMetaData, generatableType, endpointMetadata);

		// name of request/header/path parameter needs to be set for feign
		// client even when it matches method parameter name
		// if name is already set this will not override it
		Collection<JAnnotationUse> annotations = paramQueryForm.annotations();
		for (JAnnotationUse annotation : annotations) {
			JClass annotationClass = annotation.getAnnotationClass();
			if (ANNOTATIONS_TO_OVERRIDE.contains(annotationClass.fullName())) {
				annotation.param("name", paramMetaData.getName());
			}
		}

		return paramQueryForm;
	}

}
