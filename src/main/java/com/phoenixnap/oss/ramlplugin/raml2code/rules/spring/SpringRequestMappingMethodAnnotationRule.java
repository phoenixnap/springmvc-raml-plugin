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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.phoenixnap.oss.ramlplugin.raml2code.data.ApiActionMetadata;
import com.phoenixnap.oss.ramlplugin.raml2code.data.ApiParameterMetadata;
import com.phoenixnap.oss.ramlplugin.raml2code.rules.Rule;
import com.sun.codemodel.JAnnotationArrayMember;
import com.sun.codemodel.JAnnotationUse;
import com.sun.codemodel.JMethod;

/**
 * Adds a {@literal @}RequestMapping annotation at method level. The "value" of
 * the {@literal @}RequestMapping is relativ URL of the current endpoint The
 * "method" attribute is set to the appropriate RequestMethod constant.
 *
 * INPUT: #%RAML 0.8 title: myapi mediaType: application/json baseUri: /api
 * /base: /{id}: get:
 *
 * OUTPUT: {@literal @}RequestMapping(value="{id}", method=RequestMethod.GET)
 *
 * @author armin.weisser
 * @since 0.4.1
 */
public class SpringRequestMappingMethodAnnotationRule implements Rule<JMethod, JAnnotationUse, ApiActionMetadata> {

	protected static final Logger logger = LoggerFactory.getLogger(SpringRequestMappingMethodAnnotationRule.class);

	@Override
	public JAnnotationUse apply(ApiActionMetadata endpointMetadata, JMethod generatableType) {
		JAnnotationUse requestMappingAnnotation = generatableType.annotate(RequestMapping.class);

		String url = endpointMetadata.getUrl();
		JAnnotationArrayMember jAnnotationArrayMember = requestMappingAnnotation.paramArray("value");
		boolean paramSet = false;

		// collect all optional uri parameters (path variables)
		Iterator<ApiParameterMetadata> iterator = endpointMetadata.getPathVariables().iterator();
		List<ApiParameterMetadata> optionalUriParameters = new ArrayList<>();
		while (iterator.hasNext()) {
			ApiParameterMetadata apiParameterMetadata = iterator.next();
			if (apiParameterMetadata.isNullable() && doesURLContainsParamName(url, apiParameterMetadata)) {
				optionalUriParameters.add(apiParameterMetadata);
			}
		}
		if (!optionalUriParameters.isEmpty()) {

			if (optionalUriParameters.size() > 1) {
				logger.warn(
						"{} optional path variables (uriParameters) found which can lead to unpredictable results. Please consider refactoring your API!",
						optionalUriParameters.size());
			}

			// for optional params we need to set two values: with param
			jAnnotationArrayMember.param(url);

			Set<String> urls = new HashSet<>();
			for (int i = 1; i <= optionalUriParameters.size(); i++) {
				// we need to get all combinations of optional params - since
				// any combination can be present/missing
				List<List<ApiParameterMetadata>> combination = combination(optionalUriParameters, i);
				for (List<ApiParameterMetadata> list : combination) {
					String urlWithoutParam = url;
					for (ApiParameterMetadata apiParameterMetadata : list) {
						// and without it
						urlWithoutParam = urlWithoutParam.replace(getApiParameterMetadataURLPart(apiParameterMetadata), "");
						urlWithoutParam = urlWithoutParam.replaceAll("//", "/");
					}
					urls.add(urlWithoutParam);
				}
			}

			for (String urlWithoutParam : urls) {
				jAnnotationArrayMember.param(urlWithoutParam);
			}
			paramSet = true;
		}

		if (!paramSet) {
			requestMappingAnnotation.param("value", url);
		}

		requestMappingAnnotation.param("method", RequestMethod.valueOf(endpointMetadata.getActionType().name()));
		return requestMappingAnnotation;
	}

	protected boolean doesURLContainsParamName(String url, ApiParameterMetadata apiParameterMetadata) {
		return url.contains(getApiParameterMetadataURLPart(apiParameterMetadata));
	}

	protected String getApiParameterMetadataURLPart(ApiParameterMetadata apiParameterMetadata) {
		return "{" + apiParameterMetadata.getName() + "}";
	}

	public static List<List<ApiParameterMetadata>> combination(List<ApiParameterMetadata> values, int size) {

		if (0 == size) {
			return Collections.singletonList(Collections.<ApiParameterMetadata>emptyList());
		}
		if (values.isEmpty()) {
			return Collections.emptyList();
		}

		List<List<ApiParameterMetadata>> combination = new LinkedList<>();

		ApiParameterMetadata actual = values.iterator().next();

		List<ApiParameterMetadata> subSet = new LinkedList<>(values);
		subSet.remove(actual);

		List<List<ApiParameterMetadata>> subSetCombination = combination(subSet, size - 1);

		for (List<ApiParameterMetadata> set : subSetCombination) {
			List<ApiParameterMetadata> newSet = new LinkedList<>(set);
			newSet.add(0, actual);
			combination.add(newSet);
		}

		combination.addAll(combination(subSet, size));
		return combination;
	}
}
