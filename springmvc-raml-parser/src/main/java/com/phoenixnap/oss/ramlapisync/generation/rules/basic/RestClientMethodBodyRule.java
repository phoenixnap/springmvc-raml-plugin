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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

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
 * @author Kris Galea
 * @since 0.5.0
 */
public class RestClientMethodBodyRule implements Rule<JMethod, JMethod, ApiMappingMetadata> {

    private String restTemplateFieldName = "restTemplate";
    
    private String baseUrl;

    public RestClientMethodBodyRule(String baseUrl, String delegateFieldName) {
        if (baseUrl!=null && !baseUrl.isEmpty()){
            this.baseUrl=baseUrl;
        }else{
            throw new NullPointerException("The baseURL should be set.");
        }
        
        if(StringUtils.hasText(delegateFieldName)) {
            this.restTemplateFieldName = delegateFieldName;
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
        
        //Build the Http Entity object
        JClass httpEntityClass = new JCodeModel().ref("org.springframework.http.HttpEntity");
        JInvocation init = JExpr._new(httpEntityClass);
        if (generatableType.params()!=null){
            generatableType.params().forEach(p -> init.arg(p));
        }
        init.arg(httpHeaders);
        
        //Build the URL variable        
        JClass urlClass = new JCodeModel().ref(String.class);
        String urlString = baseUrl + endpointMetadata.getUrl();
        JVar url = generatableType.body().decl(urlClass, "url", JExpr.lit(urlString));
        JVar uriBuilderVar = null;
        JVar uriComponentVar = null;
        
        //Get the parameters from the model and put them in a map for easy lookup
        List<JVar> params = generatableType.params();
        Map<String, JVar> methodParamMap = new LinkedHashMap<>();
        for (JVar param : params) {
        	methodParamMap.put(param.name(), param);
        
        }	
        
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
             
        //build rest template exchange invocation
        JInvocation jInvocation = JExpr._this().ref(restTemplateFieldName).invoke("exchange");
       
        jInvocation.arg(uriComponentVar.invoke("encode").invoke("toUri"));
        jInvocation.arg(httpEntityVar); //TODO add http headers 
        jInvocation.arg(httpMethod.enumConstant(endpointMetadata.getActionType().name()));
      
        generatableType.body()._return(jInvocation);

        return generatableType;
    }
  
}
