/*
 * Copyright 2002-2016 the original author or authors.
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
package com.phoenixnap.oss.ramlapisync.generation.rules.basic;

import com.phoenixnap.oss.ramlapisync.data.ApiMappingMetadata;
import com.phoenixnap.oss.ramlapisync.data.ApiParameterMetadata;
import com.phoenixnap.oss.ramlapisync.generation.CodeModelHelper;
import com.phoenixnap.oss.ramlapisync.generation.rules.Rule;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JVar;

import java.util.ArrayList;
import java.util.List;

import static com.phoenixnap.oss.ramlapisync.generation.CodeModelHelper.findFirstClassBySimpleName;
import static org.springframework.util.StringUtils.uncapitalize;

/**
 * Generates all method parameters needed for an endpoint defined by ApiMappingMetadata.
 * This includes path variables, request parameters and the request body.
 *
 * INPUT:
 * #%RAML 0.8
 * title: myapi
 * mediaType: application/json
 * baseUri: /
 * /base:
 *   /{id}/elements:
 *     get:
 *       queryParameters:
 *         requiredQueryParam:
 *           type: integer
 *           required: true
 *         optionalQueryParam:
 *           type: string
 *         optionalQueryParam2:
 *           type: number
 *           required: false
 *
 * OUTPUT:
 * (String id
 *  , Integer requiredQueryParam
 *  , String optionalQueryParam
 *  , BigDecimal optionalQueryParam2
 * )
 *
 * @author armin.weisser
 * @since 0.4.1
 */
public class MethodParamsRule implements Rule<CodeModelHelper.JExtMethod, JMethod, ApiMappingMetadata> {

    @Override
    public JMethod apply(ApiMappingMetadata endpointMetadata, CodeModelHelper.JExtMethod generatableType) {

        List<ApiParameterMetadata> parameterMetadataList = new ArrayList<>();
        parameterMetadataList.addAll(endpointMetadata.getPathVariables());
        parameterMetadataList.addAll(endpointMetadata.getRequestParameters());

        parameterMetadataList.forEach( paramMetaData -> {
            param(paramMetaData, generatableType);
        });

        if (endpointMetadata.getRequestBody() != null) {
            param(endpointMetadata, generatableType);
        }

        return generatableType.get();
    }

    protected JVar param(ApiParameterMetadata paramMetaData, CodeModelHelper.JExtMethod generatableType) {
        return generatableType.get().param(paramMetaData.getType(), paramMetaData.getName());
    }

    protected JVar param(ApiMappingMetadata endpointMetadata, CodeModelHelper.JExtMethod generatableType) {
        String requestBodyName = endpointMetadata.getRequestBody().getName();
        JClass requestBodyType = findFirstClassBySimpleName(new JCodeModel[]{endpointMetadata.getRequestBody().getCodeModel(), generatableType.owner()}, requestBodyName);
        return generatableType.get().param(requestBodyType, uncapitalize(requestBodyName));
    }

}
