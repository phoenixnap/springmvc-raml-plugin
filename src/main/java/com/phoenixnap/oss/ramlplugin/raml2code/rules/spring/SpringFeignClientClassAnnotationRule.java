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

import org.springframework.cloud.openfeign.FeignClient;

import com.phoenixnap.oss.ramlplugin.raml2code.data.ApiResourceMetadata;
import com.phoenixnap.oss.ramlplugin.raml2code.rules.Rule;
import com.sun.codemodel.JAnnotationUse;
import com.sun.codemodel.JDefinedClass;

/**
 * Adds a {@literal @}FeignClient annotation at class level. The "url" of the
 * {@literal @}FeignClient is the endpoint url from the ApiControllerMetadata
 * instance. <br>
 * 
 * @author Aleksandar Stojsavljevic
 * @since 0.8.6
 */
public class SpringFeignClientClassAnnotationRule implements Rule<JDefinedClass, JAnnotationUse, ApiResourceMetadata> {
	@Override
	public JAnnotationUse apply(ApiResourceMetadata controllerMetadata, JDefinedClass generatableType) {
		JAnnotationUse feignClient = generatableType.annotate(FeignClient.class);

		feignClient.param("path", controllerMetadata.getControllerUrl());
		feignClient.param("name", getClientName(controllerMetadata));

		return feignClient;
	}

	private String getClientName(ApiResourceMetadata controllerMetadata) {
		String name = controllerMetadata.getResourceName();

		if (name == null || name.length() == 0) {
			return "Client";
		}
		return name.substring(0, 1).toLowerCase() + name.substring(1) + "Client";
	}
}
