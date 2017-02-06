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

import org.springframework.http.ResponseEntity;

import com.phoenixnap.oss.ramlapisync.data.ApiBodyMetadata;
import com.phoenixnap.oss.ramlapisync.data.ApiActionMetadata;
import com.phoenixnap.oss.ramlapisync.generation.rules.Rule;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JType;

/**
 * Creates a {@link ResponseEntity} as a return type for an endpoint.
 * If the endpoint declares a response body the first type of the response body will used as return type instead.
 * <br>
 * INPUT:
 * <pre class="code">
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
 * </pre>
 * OUTPUT:
 * <pre class="code">
 * ResponseEntity{@literal <}NamedResponseType{@literal >}
 * </pre>
 * OR:
 * <pre class="code">
 * ResponseEntity{@literal <}List{@literal <}NamedResponseType{@literal >}{@literal >} (if the NamedResponseType is an "array")
 * </pre>
 * 
 * @author Aleksandar Stojsavljevic
 * @since 0.8.6
 */
public class SpringFeignClientResponseTypeRule implements Rule<JDefinedClass, JType, ApiActionMetadata> {

    @Override
    public JType apply(ApiActionMetadata endpointMetadata, JDefinedClass generatableType) {
        JClass responseEntity = generatableType.owner().ref(ResponseEntity.class);
        if (!endpointMetadata.getResponseBody().isEmpty()) {
            ApiBodyMetadata apiBodyMetadata = endpointMetadata.getResponseBody().values().iterator().next();
            JClass genericType = findFirstClassBySimpleName(apiBodyMetadata.getCodeModel(), apiBodyMetadata.getName());
            if (apiBodyMetadata.isArray()) {
                JClass arrayType = generatableType.owner().ref(List.class);
                responseEntity = responseEntity.narrow(arrayType.narrow(genericType));
            } else {
                responseEntity = responseEntity.narrow(genericType);
            }
        }
        return responseEntity;
    }
}
