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

import static com.phoenixnap.oss.ramlapisync.generation.CodeModelHelper.findFirstClassBySimpleName;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import com.phoenixnap.oss.ramlapisync.data.ApiBodyMetadata;
import com.phoenixnap.oss.ramlapisync.data.ApiMappingMetadata;
import com.phoenixnap.oss.ramlapisync.data.ApiParameterMetadata;
import com.phoenixnap.oss.ramlapisync.generation.rules.Rule;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JClassAlreadyExistsException;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;
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
public class RestClientMethodBodyRule implements Rule<JMethod, JMethod, ApiMappingMetadata> {

    private String restTemplateFieldName = "restTemplate";
    
    private String baseUrlFieldName = "baseUrl";
    
    public RestClientMethodBodyRule(String restTemplateFieldName, String baseUrlFieldName) {
        if(StringUtils.hasText(restTemplateFieldName)) {
            this.restTemplateFieldName = restTemplateFieldName;            
        }
        if (!StringUtils.isEmpty(baseUrlFieldName)){
            this.baseUrlFieldName = baseUrlFieldName;
        }
    }

    @Override
    public JMethod apply(ApiMappingMetadata endpointMetadata, JMethod generatableType) {
        
        //build HttpHeaders   
        JClass httpHeadersClass = new JCodeModel().ref(HttpHeaders.class);        
        JExpression headersInit = JExpr._new(httpHeadersClass);
        JVar httpHeaders = generatableType.body().decl(httpHeadersClass, "httpHeaders", headersInit);
        
        
        //Declare Arraylist to contain the acceptable Media Types
        generatableType.body().directStatement("//  Add Accepts Headers and Body Content-Type");
        JClass mediaTypeClass = new JCodeModel().ref(MediaType.class);
        JClass refArrayListClass = new JCodeModel().ref(ArrayList.class).narrow(mediaTypeClass);
        JVar acceptsListVar = generatableType.body().decl(refArrayListClass, "acceptsList", JExpr._new(refArrayListClass));        

        //If we have a request body, lets set the content type of our request
        if (endpointMetadata.getRequestBody() != null) {
        	generatableType.body().invoke(httpHeaders, "setContentType").arg(mediaTypeClass.staticInvoke("valueOf").arg(endpointMetadata.getRequestBodyMime()));
        }
        
        //If we have response bodies defined, we need to add them to our accepts headers list
        //TODO possibly restrict
        String documentDefaultType = endpointMetadata.getParent().getDocument().getMediaType();
        //If a global mediatype is defined add it
        if (StringUtils.hasText(documentDefaultType)){
        	generatableType.body().invoke(acceptsListVar, "add").arg(mediaTypeClass.staticInvoke("valueOf").arg(documentDefaultType));
        } else { //default to application/json just in case
        	generatableType.body().invoke(acceptsListVar, "add").arg(mediaTypeClass.staticInvoke("valueOf").arg("application/json"));
        }
        
        //Iterate over Response Bodies and add each distinct mime type to accepts headers
        if (endpointMetadata.getResponseBody() != null && !endpointMetadata.getResponseBody().isEmpty()) {
        	for (String responseMime : endpointMetadata.getResponseBody().keySet()) {
        		if (!responseMime.equals(documentDefaultType) && !responseMime.equals("application/json")) {
        			generatableType.body().invoke(acceptsListVar, "add").arg(mediaTypeClass.staticInvoke("valueOf").arg(responseMime));
        		}
        	}
        }
       
        //Set accepts list as our accepts headers for the call
        generatableType.body().invoke(httpHeaders, "setAccept").arg(acceptsListVar);
        
        //Get the parameters from the model and put them in a map for easy lookup
        List<JVar> params = generatableType.params();
        Map<String, JVar> methodParamMap = new LinkedHashMap<>();
        for (JVar param : params) {
        	methodParamMap.put(param.name(), param);
        }	
        
        //Build the Http Entity object
        JClass httpEntityClass = new JCodeModel().ref(HttpEntity.class);
        JInvocation init = JExpr._new(httpEntityClass);
        
        if (endpointMetadata.getRequestBody() != null) {
	       init.arg(methodParamMap.get(endpointMetadata.getRequestBody().getName()));
        }
        init.arg(httpHeaders);
        
        //Build the URL variable                
        JExpression urlRef = JExpr.ref(baseUrlFieldName);
        JType urlClass = new JCodeModel()._ref(String.class);
        JExpression targetUrl = urlRef.invoke("concat").arg(endpointMetadata.getResource().getUri());        
        JVar url = generatableType.body().decl(urlClass, "url", targetUrl);
        JVar uriBuilderVar = null;
        JVar uriComponentVar = null;
                
       
        
        //Initialise the UriComponentsBuilder
    	JClass builderClass = new JCodeModel().ref(UriComponentsBuilder.class);
    	JExpression builderInit = builderClass.staticInvoke("fromHttpUrl").arg(url);
        
        //If we have any Query Parameters, we will use the URIBuilder to encode them in the URL
        if (!CollectionUtils.isEmpty(endpointMetadata.getRequestParameters())) {
            //iterate over the parameters and add calls to .queryParam
            for (ApiParameterMetadata parameter : endpointMetadata.getRequestParameters()) {
            	builderInit = builderInit.invoke("queryParam").arg(parameter.getName()).arg(methodParamMap.get(parameter.getName()));
            }         
        }
        //Add these to the code model
        uriBuilderVar = generatableType.body().decl(builderClass, "builder", builderInit);
        
        JClass componentClass = new JCodeModel().ref(UriComponents.class);
    	JExpression component = uriBuilderVar.invoke("build");
    	uriComponentVar = generatableType.body().decl(componentClass, "uriComponents", component);
        
        //build request entity holder                
        JVar httpEntityVar = generatableType.body().decl(httpEntityClass, "httpEntity", init);        
                        
        //construct the HTTP Method enum 
        JDefinedClass httpMethod = null;
        try {
            httpMethod = new JCodeModel()._class("org.springframework.http.HttpMethod");
        }
        catch (JClassAlreadyExistsException e) {
            
        } 
        
        //Create Map with Uri Path Variables
        JClass uriParamMap = new JCodeModel().ref(Map.class).narrow(String.class, Object.class);
        JExpression uriParamMapInit = JExpr._new(new JCodeModel().ref(HashMap.class));
        JVar uriParamMapVar = generatableType.body().decl(uriParamMap, "uriParamMap", uriParamMapInit);
        
        //get all uri params from metadata set and add them to the param map in code 
        if (!CollectionUtils.isEmpty(endpointMetadata.getPathVariables())) {
        	endpointMetadata.getPathVariables().forEach(p -> generatableType.body().invoke(uriParamMapVar, "put").arg(p.getName()).arg(methodParamMap.get(p.getName())));
        	JInvocation expandInvocation = uriComponentVar.invoke("expand").arg(uriParamMapVar);

        	generatableType.body().assign(uriComponentVar, expandInvocation);        	
        }
        
        //Determining response entity type 
        JClass returnType = null;
        if (!endpointMetadata.getResponseBody().isEmpty()) {
            ApiBodyMetadata apiBodyMetadata = endpointMetadata.getResponseBody().values().iterator().next();            
            JClass genericType = findFirstClassBySimpleName(apiBodyMetadata.getCodeModel(), apiBodyMetadata.getName());
            if (apiBodyMetadata.isArray()) {
                JClass arrayType = new JCodeModel().ref(List.class);
                returnType = arrayType.narrow(genericType);
            } else {
                returnType = genericType;
            }
        } else {
            returnType = new JCodeModel().ref(Object.class);
        }
        
        JExpression returnExpression = JExpr.dotclass(returnType);//assume not parameterized by default
        //check if return is parameterized
        if (!CollectionUtils.isEmpty(returnType.getTypeParameters())) {
            //if yes - build the parameterized type reference and change returnExpression
            //ParameterizedTypeReference<List<String>> typeRef = new ParameterizedTypeReference<List<String>>() {};
            //Create Map with Uri Path Variables
            JClass paramTypeRefClass = new JCodeModel().ref(ParameterizedTypeReference.class);
            paramTypeRefClass = paramTypeRefClass.narrow(returnType);
            
            JExpression paramTypeRefInit = JExpr._new(new JCodeModel().anonymousClass(paramTypeRefClass));
            returnExpression = generatableType.body().decl(paramTypeRefClass, "typeReference", paramTypeRefInit);
        }
        
        
        //build rest template exchange invocation
        JInvocation jInvocation = JExpr._this().ref(restTemplateFieldName).invoke("exchange");
       
        
        jInvocation.arg(uriComponentVar.invoke("encode").invoke("toUri"));
        jInvocation.arg(httpEntityVar); 
        jInvocation.arg(httpMethod.enumConstant(endpointMetadata.getActionType().name()));
        jInvocation.arg(returnExpression);
        
        generatableType.body()._return(jInvocation);

        return generatableType;
    }
  
}
