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

import org.apache.commons.lang.StringUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.phoenixnap.oss.ramlplugin.raml2code.data.ApiActionMetadata;
import com.phoenixnap.oss.ramlplugin.raml2code.rules.Rule;
import com.sun.codemodel.JAnnotationUse;
import com.sun.codemodel.JMethod;

/**
 * Adds one of following: {@literal @}GetMappping, {@literal @}PostMappping,
 * {@literal @}PutMappping, {@literal @}DeleteMappping annotation at method
 * level.
 *
 * @author yura.nosenko
 * @since 0.10.12
 */
public class SpringShortcutMappingMethodAnnotationRule implements Rule<JMethod, JAnnotationUse, ApiActionMetadata> {

	@Override
	public JAnnotationUse apply(ApiActionMetadata endpointMetadata, JMethod generatableType) {
		JAnnotationUse requestMappingAnnotation;
		switch (RequestMethod.valueOf(endpointMetadata.getActionType().name())) {
			case GET:
				requestMappingAnnotation = generatableType.annotate(GetMapping.class);
				break;
			case POST:
				requestMappingAnnotation = generatableType.annotate(PostMapping.class);
				break;
			case PUT:
				requestMappingAnnotation = generatableType.annotate(PutMapping.class);
				break;
			case PATCH:
				requestMappingAnnotation = generatableType.annotate(PatchMapping.class);
				break;
			case DELETE:
				requestMappingAnnotation = generatableType.annotate(DeleteMapping.class);
				break;
			default:
				requestMappingAnnotation = generatableType.annotate(RequestMapping.class);
				requestMappingAnnotation.param("method", RequestMethod.valueOf(endpointMetadata.getActionType().name()));
		}

		if (StringUtils.isNotBlank(endpointMetadata.getUrl())) {
			requestMappingAnnotation.param("value", endpointMetadata.getUrl());
		}
		return requestMappingAnnotation;
	}

}
