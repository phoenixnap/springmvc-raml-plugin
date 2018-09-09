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

import static com.phoenixnap.oss.ramlplugin.raml2code.helpers.CodeModelHelper.findFirstClassBySimpleName;
import static org.springframework.util.StringUtils.uncapitalize;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import org.springframework.http.HttpHeaders;
import org.springframework.util.StringUtils;

import com.phoenixnap.oss.ramlplugin.raml2code.data.ApiActionMetadata;
import com.phoenixnap.oss.ramlplugin.raml2code.data.ApiParameterMetadata;
import com.phoenixnap.oss.ramlplugin.raml2code.helpers.CodeModelHelper;
import com.phoenixnap.oss.ramlplugin.raml2code.helpers.NamingHelper;
import com.phoenixnap.oss.ramlplugin.raml2code.plugin.Config;
import com.phoenixnap.oss.ramlplugin.raml2code.raml.RamlAbstractParam;
import com.phoenixnap.oss.ramlplugin.raml2code.raml.RamlActionType;
import com.phoenixnap.oss.ramlplugin.raml2code.raml.RamlParamType;
import com.phoenixnap.oss.ramlplugin.raml2code.raml.raml10.RJP10V2RamlQueryParameter;
import com.phoenixnap.oss.ramlplugin.raml2code.raml.raml10.RJP10V2RamlUriParameter;
import com.phoenixnap.oss.ramlplugin.raml2code.rules.Rule;
import com.sun.codemodel.JAnnotationUse;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JVar;

/**
 * Generates all method parameters needed for an endpoint defined by
 * ApiMappingMetadata. This includes path variables, request parameters and the
 * request body.
 *
 * INPUT: #%RAML 0.8 title: myapi mediaType: application/json baseUri: / /base:
 * /{id}/elements: get: queryParameters: requiredQueryParam: type: integer
 * required: true optionalQueryParam: type: string optionalQueryParam2: type:
 * number required: false
 *
 * OUTPUT: (String id , Integer requiredQueryParam , String optionalQueryParam ,
 * BigDecimal optionalQueryParam2 )
 *
 * @author armin.weisser
 * @author kurt paris
 * @since 0.4.1
 */
public class MethodParamsRule implements Rule<CodeModelHelper.JExtMethod, JMethod, ApiActionMetadata> {

	boolean addParameterJavadoc = false;
	boolean allowArrayParameters = true;

	public MethodParamsRule() {
		this(false, true);
	}

	/**
	 * If set to true, the rule will also add a parameter javadoc entry
	 * 
	 * @param addParameterJavadoc
	 *            Set to true for javadocs for parameters
	 * @param allowArrayParameters
	 *            If true we will use the component type for array parameters
	 */
	public MethodParamsRule(boolean addParameterJavadoc, boolean allowArrayParameters) {
		this.addParameterJavadoc = addParameterJavadoc;
		this.allowArrayParameters = allowArrayParameters;
	}

	@Override
	public JMethod apply(ApiActionMetadata endpointMetadata, CodeModelHelper.JExtMethod generatableType) {

		List<ApiParameterMetadata> parameterMetadataList = new ArrayList<>();
		parameterMetadataList.addAll(endpointMetadata.getPathVariables());
		parameterMetadataList.addAll(endpointMetadata.getRequestParameters());
		parameterMetadataList.addAll(endpointMetadata.getRequestHeaders());

		parameterMetadataList.forEach(paramMetaData -> {
			paramQueryForm(paramMetaData, generatableType, endpointMetadata);
		});

		if (endpointMetadata.getRequestBody() != null) {
			paramObjects(endpointMetadata, generatableType);
		}

		if (Config.isInjectHttpHeadersParameter()) {
			paramHttpHeaders(generatableType);
		}

		return generatableType.get();
	}

	protected JVar paramQueryForm(ApiParameterMetadata paramMetaData, CodeModelHelper.JExtMethod generatableType,
			ApiActionMetadata endpointMetadata) {

		String javaName = paramMetaData.getJavaName();

		if (addParameterJavadoc) {
			String paramComment = "";
			if (paramMetaData.getRamlParam() != null && StringUtils.hasText(paramMetaData.getRamlParam().getDescription())) {
				paramComment = NamingHelper.cleanForJavadoc(paramMetaData.getRamlParam().getDescription());
			}
			generatableType.get().javadoc().addParam(javaName + " " + paramComment);
		}

		JClass type = null;
		if (paramMetaData.getRamlParam() instanceof RJP10V2RamlUriParameter && paramMetaData.isNullable()) {
			// for optional uri parameters use java.util.Optional since Spring
			// doesn't support optional uri parameters
			type = generatableType.owner().ref(Optional.class).narrow(paramMetaData.getType());
		}
		if (type == null) {
			type = generatableType.owner().ref(paramMetaData.getType());
		}

		if (!allowArrayParameters && paramMetaData.isArray()) {
			type = generatableType.owner().ref(paramMetaData.getType().getComponentType());
		} else {
			// TODO should this be blank?
		}

		// data types as query parameters
		RamlAbstractParam ramlParam = paramMetaData.getRamlParam();
		if (ramlParam.getType() == RamlParamType.DATA_TYPE && ramlParam instanceof RJP10V2RamlQueryParameter) {
			JClass jc = findFirstClassBySimpleName(paramMetaData.getCodeModel(), ramlParam.getRawType());
			return generatableType.get().param(jc, paramMetaData.getName());
		}

		JVar jVar = generatableType.get().param(type, javaName);
		if (paramMetaData.getRamlParam().getPattern() != null) {
			jVar.annotate(Pattern.class).param("regexp", paramMetaData.getRamlParam().getPattern());
		}

		if (paramMetaData.getRamlParam().getMinLength() != null || paramMetaData.getRamlParam().getMaxLength() != null) {
			JAnnotationUse jAnnotationUse = jVar.annotate(Size.class);
			if (paramMetaData.getRamlParam().getMinLength() != null) {
				jAnnotationUse.param("min", paramMetaData.getRamlParam().getMinLength());
			}
			if (paramMetaData.getRamlParam().getMaxLength() != null) {
				jAnnotationUse.param("max", paramMetaData.getRamlParam().getMaxLength());
			}
		}

		if (paramMetaData.getRamlParam().getMinimum() != null) {
			jVar.annotate(Min.class).param("value", paramMetaData.getRamlParam().getMinimum().longValue());
		}
		if (paramMetaData.getRamlParam().getMaximum() != null) {
			jVar.annotate(Max.class).param("value", paramMetaData.getRamlParam().getMaximum().longValue());
		}
		return jVar;
	}

	protected JVar paramObjects(ApiActionMetadata endpointMetadata, CodeModelHelper.JExtMethod generatableType) {
		String requestBodyName = endpointMetadata.getRequestBody().getName();
		boolean array = endpointMetadata.getRequestBody().isArray();

		List<JCodeModel> codeModels = new ArrayList<>();
		if (endpointMetadata.getRequestBody().getCodeModel() != null) {
			codeModels.add(endpointMetadata.getRequestBody().getCodeModel());
		}

		if (generatableType.owner() != null) {
			codeModels.add(generatableType.owner());
		}

		JClass requestBodyType = findFirstClassBySimpleName(codeModels.toArray(new JCodeModel[codeModels.size()]), requestBodyName);
		if (allowArrayParameters && array) {
			JClass arrayType = generatableType.owner().ref(List.class);
			requestBodyType = arrayType.narrow(requestBodyType);
		}
		if (addParameterJavadoc) {
			generatableType.get().javadoc().addParam(uncapitalize(requestBodyName) + " The Request Body Payload");
		}
		JVar param = generatableType.get().param(requestBodyType, uncapitalize(requestBodyName));
		if (Config.getPojoConfig().isIncludeJsr303Annotations() && !RamlActionType.PATCH.equals(endpointMetadata.getActionType())) {
			// skip Valid annotation for PATCH actions since it's a partial
			// update so some required fields might be omitted
			param.annotate(Valid.class);
		}
		return param;
	}

	protected JVar paramHttpHeaders(CodeModelHelper.JExtMethod generatableType) {
		JVar paramHttpHeaders = generatableType.get().param(HttpHeaders.class, "httpHeaders");
		if (addParameterJavadoc) {
			generatableType.get().javadoc().addParam("httpHeaders The HTTP headers for the request");
		}
		return paramHttpHeaders;
	}

}
