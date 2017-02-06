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
package com.phoenixnap.oss.ramlapisync.generation.rules.basic;

import com.phoenixnap.oss.ramlapisync.data.ApiActionMetadata;
import com.phoenixnap.oss.ramlapisync.data.ApiParameterMetadata;
import com.phoenixnap.oss.ramlapisync.generation.CodeModelHelper;
import com.phoenixnap.oss.ramlapisync.generation.rules.Rule;
import com.phoenixnap.oss.ramlapisync.naming.NamingHelper;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JVar;

import org.springframework.http.HttpHeaders;
import org.springframework.util.StringUtils;

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
 * @author kurt paris
 * @since 0.4.1
 */
public class MethodParamsRule implements Rule<CodeModelHelper.JExtMethod, JMethod, ApiActionMetadata> {

	boolean addParameterJavadoc = false;
	boolean allowArrayParameters = true;
	
	public MethodParamsRule () {
		this(false, true);
	}
	
	/**
	 * If set to true, the rule will also add a parameter javadoc entry
	 * 
	 * @param addParameterJavadoc Set to true for javadocs for parameters
	 */
	public MethodParamsRule (boolean addParameterJavadoc, boolean allowArrayParameters) {
		this.addParameterJavadoc = addParameterJavadoc;
		this.allowArrayParameters = allowArrayParameters;
	}
	
    @Override
    public JMethod apply(ApiActionMetadata endpointMetadata, CodeModelHelper.JExtMethod generatableType) {

        List<ApiParameterMetadata> parameterMetadataList = new ArrayList<>();
        parameterMetadataList.addAll(endpointMetadata.getPathVariables());
        parameterMetadataList.addAll(endpointMetadata.getRequestParameters());
        parameterMetadataList.addAll(endpointMetadata.getRequestHeaders());

        parameterMetadataList.forEach( paramMetaData -> {
            paramQueryForm(paramMetaData, generatableType);
        });

        if (endpointMetadata.getRequestBody() != null) {
            paramObjects(endpointMetadata, generatableType);
        }

       if (endpointMetadata.getInjectHttpHeadersParameter()) {
            paramHttpHeaders(generatableType);
       }

        return generatableType.get();
    }

	protected JVar paramQueryForm(ApiParameterMetadata paramMetaData, CodeModelHelper.JExtMethod generatableType) {
      String javaName = NamingHelper.getParameterName(paramMetaData.getName());
    	if (addParameterJavadoc) {
			String paramComment = "";
			if (paramMetaData.getRamlParam() != null && StringUtils.hasText(paramMetaData.getRamlParam().getDescription())) {
				 paramComment = NamingHelper.cleanForJavadoc(paramMetaData.getRamlParam().getDescription());
			}
	    	generatableType.get().javadoc().addParam(javaName + " " + paramComment);
    	}
    	Class<?> type = paramMetaData.getType();
    	if (!allowArrayParameters && paramMetaData.isArray() ) {
    		type = type.getComponentType();
    	} else {
    		
    	}
    	return generatableType.get().param(type, javaName);
    }

    protected JVar paramObjects(ApiActionMetadata endpointMetadata, CodeModelHelper.JExtMethod generatableType) {
        String requestBodyName = endpointMetadata.getRequestBody().getName();
        List<JCodeModel> codeModels = new ArrayList<>();
        if (endpointMetadata.getRequestBody().getCodeModel()!=null){
            codeModels.add(endpointMetadata.getRequestBody().getCodeModel());
        }
        
        if ( generatableType.owner()!=null){
            codeModels.add(generatableType.owner());
        }
                
        JClass requestBodyType = findFirstClassBySimpleName(codeModels.toArray(new JCodeModel[codeModels.size()]), requestBodyName);
        if (addParameterJavadoc) {
        	generatableType.get().javadoc().addParam(uncapitalize(requestBodyName) + " The Request Body Payload");
        }
        return generatableType.get().param(requestBodyType, uncapitalize(requestBodyName));
    }

   protected JVar paramHttpHeaders(CodeModelHelper.JExtMethod generatableType) {
      JVar paramHttpHeaders = generatableType.get().param(HttpHeaders.class, "httpHeaders");
      if (addParameterJavadoc) {
          generatableType.get().javadoc().addParam("httpHeaders The HTTP headers for the request");
      }
      return paramHttpHeaders;
   }

}
