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

import javax.validation.Valid;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import com.phoenixnap.oss.ramlplugin.raml2code.data.ApiActionMetadata;
import com.phoenixnap.oss.ramlplugin.raml2code.data.ApiParameterMetadata;
import com.phoenixnap.oss.ramlplugin.raml2code.helpers.CodeModelHelper;
import com.phoenixnap.oss.ramlplugin.raml2code.helpers.RamlTypeHelper;
import com.phoenixnap.oss.ramlplugin.raml2code.plugin.Config;
import com.phoenixnap.oss.ramlplugin.raml2code.raml.RamlHeader;
import com.phoenixnap.oss.ramlplugin.raml2code.raml.RamlParamType;
import com.phoenixnap.oss.ramlplugin.raml2code.raml.RamlUriParameter;
import com.phoenixnap.oss.ramlplugin.raml2code.raml.raml10.RJP10V2RamlQueryParameter;
import com.phoenixnap.oss.ramlplugin.raml2code.rules.basic.MethodParamsRule;
import com.sun.codemodel.JAnnotationUse;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JVar;

/**
 * Generates all method parameters with Spring annotations needed for an
 * endpoint defined by ApiMappingMetadata. This includes path variables, request
 * parameters and the request body.
 *
 * INPUT: #%RAML 0.8 title: myapi mediaType: application/json baseUri: / /base:
 * /{id}/elements: get: queryParameters: requiredQueryParam: type: integer
 * required: true optionalQueryParam: type: string default: "someDefault"
 * optionalQueryParam2: type: number required: false default: 3
 *
 * OUTPUT: ({@literal @}PathVariable String id , {@literal @}RequestParam
 * Integer requiredQueryParam , {@literal @}RequestParam(required=false,
 * defaultValue = "someDefault") String optionalQueryParam ,
 * {@literal @}RequestParam(required=false, defaultValue = "3") BigDecimal
 * optionalQueryParam2 )
 *
 * @author armin.weisser
 * @author Aleksandar Stojsavljevic
 * @since 0.4.1
 */
public class SpringMethodParamsRule extends MethodParamsRule {

	public SpringMethodParamsRule() {
		super();
	}

	public SpringMethodParamsRule(boolean addParameterJavadoc, boolean allowArrayParameters) {
		super(addParameterJavadoc, allowArrayParameters);
	}

	@Override
	protected JVar paramQueryForm(ApiParameterMetadata paramMetaData, CodeModelHelper.JExtMethod generatableType,
			ApiActionMetadata endpointMetadata) {
		JVar jVar = super.paramQueryForm(paramMetaData, generatableType, endpointMetadata);
		JAnnotationUse jAnnotationUse;
		if (paramMetaData.getRamlParam() != null && paramMetaData.getRamlParam() instanceof RamlUriParameter) {
			jAnnotationUse = jVar.annotate(PathVariable.class);
			if (!paramMetaData.getName().equals(jVar.name())) {
				jAnnotationUse.param("value", paramMetaData.getName());
			}
			if (!paramMetaData.getRamlParam().isRequired()) {
				jAnnotationUse.param("required", false);
			}
			return jVar;
		} else if (paramMetaData.getRamlParam() != null && paramMetaData.getRamlParam() instanceof RamlHeader) {
			jAnnotationUse = jVar.annotate(RequestHeader.class);
			if (!paramMetaData.getName().equals(jVar.name())) {
				jAnnotationUse.param("name", paramMetaData.getName());
			}
			if (!paramMetaData.getRamlParam().isRequired()) {
				jAnnotationUse.param("required", false);
			}

			if (StringUtils.hasText(paramMetaData.getRamlParam().getDefaultValue())) {
				jAnnotationUse.param("defaultValue", paramMetaData.getRamlParam().getDefaultValue());
				// Supplying a default value implicitly sets required to false.
				jAnnotationUse.param("required", false);
			}

			return jVar;
		} else {
			if (paramMetaData.getRamlParam() == null) {
				return jVar;
			}

			jAnnotationUse = jVar.annotate(RequestParam.class);
			if (!paramMetaData.getName().equals(jVar.name())) {
				jAnnotationUse.param("name", paramMetaData.getName());
			}
			// In RAML parameters are optional unless the required attribute is
			// included and its value set to 'true'.
			// In Spring a parameter is required by default unlesse the required
			// attribute is included and its value is set to 'false'
			// So we just need to set required=false if the RAML "required"
			// parameter is not set or explicitly set to false.
			if (!paramMetaData.getRamlParam().isRequired()) {
				jAnnotationUse.param("required", false);
			}

			if (StringUtils.hasText(paramMetaData.getRamlParam().getDefaultValue())) {
				jAnnotationUse.param("defaultValue", paramMetaData.getRamlParam().getDefaultValue());
				// Supplying a default value implicitly sets required to false.
				jAnnotationUse.param("required", false);
			}

			if (paramMetaData.getRamlParam().getType() == RamlParamType.DATE
					&& paramMetaData.getRamlParam() instanceof RJP10V2RamlQueryParameter) {

				RJP10V2RamlQueryParameter queryParameter = (RJP10V2RamlQueryParameter) paramMetaData.getRamlParam();
				if (StringUtils.hasText(queryParameter.getRawType())) {
					jAnnotationUse = jVar.annotate(DateTimeFormat.class);
					RamlTypeHelper.annotateDateWithPattern(jAnnotationUse, queryParameter.getRawType(), queryParameter.getFormat());
				}
			}
			// In most cases will be JReferencedClass - for a primitive/boxed
			// primitive
			if (Config.getPojoConfig().isIncludeJsr303Annotations() && jVar.type() instanceof JDefinedClass) {
				boolean isPOJO = ((JDefinedClass) jVar.type())._package().name().startsWith(Config.getBasePackage());
				if (isPOJO) {
					jVar.annotate(Valid.class);
				}
			}

			return jVar;
		}
	}

	@Override
	protected JVar paramObjects(ApiActionMetadata endpointMetadata, CodeModelHelper.JExtMethod generatableType) {
		JVar param = super.paramObjects(endpointMetadata, generatableType);
		param.annotate(RequestBody.class);
		return param;
	}

	@Override
	protected JVar paramHttpHeaders(CodeModelHelper.JExtMethod generatableType) {
		JVar paramHttpHeaders = super.paramHttpHeaders(generatableType);
		paramHttpHeaders.annotate(RequestHeader.class);
		return paramHttpHeaders;
	}

}
