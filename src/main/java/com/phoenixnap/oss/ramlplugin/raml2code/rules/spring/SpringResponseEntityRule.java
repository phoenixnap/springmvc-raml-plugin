/*
 * Copyright 2002-2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package com.phoenixnap.oss.ramlplugin.raml2code.rules.spring;

import static com.phoenixnap.oss.ramlplugin.raml2code.helpers.CodeModelHelper.findFirstClassBySimpleName;

import com.phoenixnap.oss.ramlplugin.raml2code.data.ApiActionMetadata;
import com.phoenixnap.oss.ramlplugin.raml2code.data.ApiBodyMetadata;
import com.phoenixnap.oss.ramlplugin.raml2code.rules.Rule;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JType;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import org.springframework.http.ResponseEntity;

/**
 * Creates a org.springframework.http.ResponseEntity as a return type for an
 * endpoint. If the endpoint declares a response body the first type of the
 * response body will added as a generic type to the ResponseEntity.
 *
 * #%RAML 0.8 title: myapi mediaType: application/json baseUri: /
 *
 * /base: get: /{id}: get: responses: 200: body: application/json: schema:
 * NamedResponseType ...
 *
 * OUTPUT: {@literal @}ResponseEntity{@literal <}NamedResponseType{@literal >}
 *
 * @author armin.weisser
 * @since 0.4.1
 */
public class SpringResponseEntityRule implements Rule<JDefinedClass, JType, ApiActionMetadata> {

	@Override
	public JType apply(ApiActionMetadata endpointMetadata, JDefinedClass generatableType) {

		JClass responseEntity = generatableType.owner().ref(ResponseEntity.class);
		if (!endpointMetadata.getResponseBody().isEmpty()) {
			ApiBodyMetadata apiBodyMetadata = endpointMetadata.getResponseBody().values().iterator().next();
			JClass genericType = findFirstClassBySimpleName(apiBodyMetadata.getCodeModel(), apiBodyMetadata.getName());
			if (apiBodyMetadata.isArray()) {
				JClass arrayType = generatableType.owner().ref(List.class);
				return responseEntity.narrow(arrayType.narrow(genericType));
			} else if (BigDecimal.class.getSimpleName().equals(apiBodyMetadata.getName())) {
				JClass bigDecimalType = generatableType.owner().ref(BigDecimal.class);
				return responseEntity.narrow(bigDecimalType);
			} else if (BigInteger.class.getSimpleName().equals(apiBodyMetadata.getName())) {
				JClass bigIntegerType = generatableType.owner().ref(BigInteger.class);
				return responseEntity.narrow(bigIntegerType);
			}
			return responseEntity.narrow(genericType);

		}
		return responseEntity.narrow(generatableType.owner().wildcard());
	}
}
