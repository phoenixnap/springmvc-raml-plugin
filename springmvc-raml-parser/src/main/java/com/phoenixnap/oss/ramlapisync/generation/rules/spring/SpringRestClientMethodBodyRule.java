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
package com.phoenixnap.oss.ramlapisync.generation.rules.spring;

import static com.phoenixnap.oss.ramlapisync.generation.CodeModelHelper.findFirstClassBySimpleName;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import com.google.common.base.CaseFormat;
import com.phoenixnap.oss.ramlapisync.data.ApiActionMetadata;
import com.phoenixnap.oss.ramlapisync.data.ApiBodyMetadata;
import com.phoenixnap.oss.ramlapisync.data.ApiParameterMetadata;
import com.phoenixnap.oss.ramlapisync.generation.CodeModelHelper;
import com.phoenixnap.oss.ramlapisync.generation.rules.Rule;
import com.phoenixnap.oss.ramlapisync.naming.NamingHelper;
import com.sun.codemodel.JBlock;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JExpression;
import com.sun.codemodel.JInvocation;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JType;
import com.sun.codemodel.JVar;

/**
 * Generates a method body that calls the rest template to execute a REST call.
 *
 * INPUT:
 * #%RAML 0.8
 * title: myapi
 * mediaType: application/json
 * baseUri: /
 * /base:
 *   /{id}
 *     get:
 *
 * OUTPUT:
 * HttpEntity httpEntity = new HttpEntity();
 * return this.restTemplate.exchange(/, GET, httpEntity,responseBody);
 *
 * The name of the field can be configured. Default is "restTemplate".
 *
 * @author Kurt Paris
 * @author Kris Galea
 * @since 0.5.0
 */
public class SpringRestClientMethodBodyRule implements Rule<CodeModelHelper.JExtMethod, JMethod, ApiActionMetadata> {

    private String restTemplateFieldName = "restTemplate";

    private String baseUrlFieldName = "baseUrl";

    public SpringRestClientMethodBodyRule(String restTemplateFieldName, String baseUrlFieldName) {
        if(StringUtils.hasText(restTemplateFieldName)) {
            this.restTemplateFieldName = restTemplateFieldName;
        }
        if (!StringUtils.isEmpty(baseUrlFieldName)){
            this.baseUrlFieldName = baseUrlFieldName;
        }
    }

    @Override
    public JMethod apply(ApiActionMetadata endpointMetadata, CodeModelHelper.JExtMethod generatableType) {
        JBlock body = generatableType.get().body();
        JCodeModel owner = generatableType.owner();
        //build HttpHeaders
        JClass httpHeadersClass = owner.ref(HttpHeaders.class);
        JExpression headersInit = JExpr._new(httpHeadersClass);
        JVar httpHeaders = null;
        if (endpointMetadata.getInjectHttpHeadersParameter()) {
            for (JVar var : generatableType.get().params()) {
                if (var.name().equals("httpHeaders")) {
                    httpHeaders = var;
                    break;
                }
            }
        } else {
            httpHeaders = body.decl(httpHeadersClass, "httpHeaders", headersInit);
        }


        //Declare Arraylist to contain the acceptable Media Types
        body.directStatement("//  Add Accepts Headers and Body Content-Type");
        JClass mediaTypeClass = owner.ref(MediaType.class);
        JClass refArrayListClass = owner.ref(ArrayList.class).narrow(mediaTypeClass);
        JVar acceptsListVar = body.decl(refArrayListClass, "acceptsList", JExpr._new(refArrayListClass));

        //If we have a request body, lets set the content type of our request
        if (endpointMetadata.getRequestBody() != null) {
        	body.invoke(httpHeaders, "setContentType").arg(mediaTypeClass.staticInvoke("valueOf").arg(endpointMetadata.getRequestBodyMime()));
        }

        //If we have response bodies defined, we need to add them to our accepts headers list
        //TODO possibly restrict
        String documentDefaultType = endpointMetadata.getParent().getDocument().getMediaType();
        //If a global mediatype is defined add it
        if (StringUtils.hasText(documentDefaultType)){
        	body.invoke(acceptsListVar, "add").arg(mediaTypeClass.staticInvoke("valueOf").arg(documentDefaultType));
        } else { //default to application/json just in case
        	body.invoke(acceptsListVar, "add").arg(mediaTypeClass.staticInvoke("valueOf").arg("application/json"));
        }

        //Iterate over Response Bodies and add each distinct mime type to accepts headers
        if (endpointMetadata.getResponseBody() != null && !endpointMetadata.getResponseBody().isEmpty()) {
        	for (String responseMime : endpointMetadata.getResponseBody().keySet()) {
        		if (!responseMime.equals(documentDefaultType) && !responseMime.equals("application/json")) {
        			body.invoke(acceptsListVar, "add").arg(mediaTypeClass.staticInvoke("valueOf").arg(responseMime));
        		}
        	}
        }

        //Set accepts list as our accepts headers for the call
        body.invoke(httpHeaders, "setAccept").arg(acceptsListVar);


        //Get the parameters from the model and put them in a map for easy lookup
        List<JVar> params = generatableType.get().params();
        Map<String, JVar> methodParamMap = new LinkedHashMap<>();
        for (JVar param : params) {
        	methodParamMap.put(param.name(), param);
        }

        // Add headers
        for (ApiParameterMetadata parameter : endpointMetadata.getRequestHeaders()) {
            JVar param = methodParamMap.get(NamingHelper.getParameterName(parameter.getName()));
            String javaParamName = NamingHelper.getParameterName(parameter.getName());
            body._if(methodParamMap.get(javaParamName).ne(JExpr._null()))._then().block()
                    .invoke(httpHeaders, "add").arg(parameter.getName()).arg(JExpr.invoke(param, "toString"));
        }

        //Build the Http Entity object
        JClass httpEntityClass = owner.ref(HttpEntity.class);
        JInvocation init = JExpr._new(httpEntityClass);

        if (endpointMetadata.getRequestBody() != null) {
	       init.arg(methodParamMap.get(CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_CAMEL, endpointMetadata.getRequestBody().getName())));
        }
        init.arg(httpHeaders);

        //Build the URL variable
        JExpression urlRef = JExpr.ref(baseUrlFieldName);
        JType urlClass = owner._ref(String.class);
        JExpression targetUrl = urlRef.invoke("concat").arg(endpointMetadata.getResource().getUri());
        JVar url = body.decl(urlClass, "url", targetUrl);
        JVar uriBuilderVar = null;
        JVar uriComponentVar = null;



        //Initialise the UriComponentsBuilder
    	JClass builderClass = owner.ref(UriComponentsBuilder.class);
    	JExpression builderInit = builderClass.staticInvoke("fromHttpUrl").arg(url);

        //If we have any Query Parameters, we will use the URIBuilder to encode them in the URL
        if (!CollectionUtils.isEmpty(endpointMetadata.getRequestParameters())) {
            //iterate over the parameters and add calls to .queryParam
            for (ApiParameterMetadata parameter : endpointMetadata.getRequestParameters()) {
            	builderInit = builderInit.invoke("queryParam").arg(parameter.getName()).arg(methodParamMap.get(NamingHelper.getParameterName(parameter.getName())));
            }
        }
        //Add these to the code model
        uriBuilderVar = body.decl(builderClass, "builder", builderInit);

        JClass componentClass = owner.ref(UriComponents.class);
    	JExpression component = uriBuilderVar.invoke("build");
    	uriComponentVar = body.decl(componentClass, "uriComponents", component);

        //build request entity holder
        JVar httpEntityVar = body.decl(httpEntityClass, "httpEntity", init);

        //construct the HTTP Method enum
        JClass httpMethod = null;
        try {
            httpMethod = (JClass)owner._ref(HttpMethod.class);
        }
        catch (ClassCastException e) {

        }

        //get all uri params from metadata set and add them to the param map in code
        if (!CollectionUtils.isEmpty(endpointMetadata.getPathVariables())) {
            //Create Map with Uri Path Variables
            JClass uriParamMap = owner.ref(Map.class).narrow(String.class, Object.class);
            JExpression uriParamMapInit = JExpr._new(owner.ref(HashMap.class));
            JVar uriParamMapVar = body.decl(uriParamMap, "uriParamMap", uriParamMapInit);

        	endpointMetadata.getPathVariables().forEach(p -> body.invoke(uriParamMapVar, "put").arg(p.getName()).arg(methodParamMap.get(p.getName())));
        	JInvocation expandInvocation = uriComponentVar.invoke("expand").arg(uriParamMapVar);

        	body.assign(uriComponentVar, expandInvocation);
        }

        //Determining response entity type
        JClass returnType = null;
        if (!endpointMetadata.getResponseBody().isEmpty()) {
            ApiBodyMetadata apiBodyMetadata = endpointMetadata.getResponseBody().values().iterator().next();
            JClass genericType = findFirstClassBySimpleName(apiBodyMetadata.getCodeModel(), apiBodyMetadata.getName());
            if (apiBodyMetadata.isArray()) {
                JClass arrayType = owner.ref(List.class);
                returnType = arrayType.narrow(genericType);
            } else {
                returnType = genericType;
            }
        } else {
            returnType = owner.ref(Object.class);
        }

        JExpression returnExpression = JExpr.dotclass(returnType);//assume not parameterized by default
        //check if return is parameterized
        if (!CollectionUtils.isEmpty(returnType.getTypeParameters())) {
            //if yes - build the parameterized type reference and change returnExpression
            //Due to issue 61, it is generated as
            //class _P extends org.springframework.core.ParameterizedTypeReference<java.util.List<java.lang.String>>
            //ParameterizedTypeReference<List<String>> typeRef = new _P();
            //Create Map with Uri Path Variables
            JClass paramTypeRefClass = owner.ref(ParameterizedTypeReference.class);
            paramTypeRefClass = paramTypeRefClass.narrow(returnType);

            body.directStatement("class _P extends " + paramTypeRefClass.fullName() + "{};");

            JExpression paramTypeRefInit = JExpr._new(owner.directClass("_P"));
            returnExpression = body.decl(paramTypeRefClass, "typeReference", paramTypeRefInit);
        }

        //build rest template exchange invocation
        JInvocation jInvocation = JExpr._this().ref(restTemplateFieldName).invoke("exchange");

        jInvocation.arg(uriComponentVar.invoke("encode").invoke("toUri"));
        jInvocation.arg(httpMethod.staticRef(endpointMetadata.getActionType().name()));
        jInvocation.arg(httpEntityVar);
        jInvocation.arg(returnExpression);

        body._return(jInvocation);

        return generatableType.get();
    }

}
