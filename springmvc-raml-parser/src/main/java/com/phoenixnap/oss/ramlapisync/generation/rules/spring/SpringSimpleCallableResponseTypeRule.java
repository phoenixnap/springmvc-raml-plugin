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
package com.phoenixnap.oss.ramlapisync.generation.rules.spring;

import static com.phoenixnap.oss.ramlapisync.generation.CodeModelHelper.findFirstClassBySimpleName;

import java.util.List;
import java.util.concurrent.Callable;

import org.springframework.http.ResponseEntity;

import com.phoenixnap.oss.ramlapisync.data.ApiActionMetadata;
import com.phoenixnap.oss.ramlapisync.data.ApiBodyMetadata;
import com.phoenixnap.oss.ramlapisync.generation.rules.Rule;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JType;

/**
 * Creates a org.springframework.http.ResponseEntity as a return type for an endpoint.
 * If the endpoint declares a response body the first type of the response body will used as return type instead.
 * If the endpoints response body is an "array" th
 *
 * #%RAML 0.8
 * title: myapi
 * mediaType: application/json
 * baseUri: /
 *
 * /base:
 *   get:
 *   /{id}:
 *     get:
 *       responses:
 *         200:
 *           body:
 *             application/json:
 *               schema: NamedResponseType
 *               ...
 *
 * OUTPUT:
 * Callable{@literal <}NamedResponseType{@literal >}
 *
 * OR:
 * Callable{@literal <}ArrayList{@literal <}NamedResponseType{@literal >}{@literal >} (if the NamedResponseType is an "array")
 *
 * @author mehdi.jouan
 * @since 0.8.9
 */
public class SpringSimpleCallableResponseTypeRule implements Rule<JDefinedClass, JType, ApiActionMetadata> {

    @Override
    public JType apply(ApiActionMetadata endpointMetadata, JDefinedClass generatableType) {

        JClass callable = generatableType.owner().ref(Callable.class);
        JClass responseType = generatableType.owner().ref(ResponseEntity.class);
        if (!endpointMetadata.getResponseBody().isEmpty()) {
            ApiBodyMetadata apiBodyMetadata = endpointMetadata.getResponseBody().values().iterator().next();
            JClass genericType = findFirstClassBySimpleName(apiBodyMetadata.getCodeModel(), apiBodyMetadata.getName());
            if (apiBodyMetadata.isArray()) {
                JClass arrayType = generatableType.owner().ref(List.class);
                responseType = arrayType.narrow(genericType);
            } else {
               return callable.narrow(genericType);
            }
        }
        return callable.narrow(responseType
            .narrow(generatableType.owner().wildcard()));
    }
}
