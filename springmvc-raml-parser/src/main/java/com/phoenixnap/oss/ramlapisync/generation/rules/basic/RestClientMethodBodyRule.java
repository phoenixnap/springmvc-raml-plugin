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
import java.util.List;

import org.raml.model.ActionType;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;

import com.phoenixnap.oss.ramlapisync.data.ApiMappingMetadata;
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
        
        //TODO the following ---------------------
        //handle media type
//        List<MediaType> acceptsList = new ArrayList<>();
//        acceptsList.add(MediaType.valueOf(version));
//        headers.setAccept(acceptsList);
//
//        if (bodyVersion.isPresent()) {
//            headers.setContentType(MediaType.valueOf(bodyVersion.get()));
//        }
//
          //handle query params
//        if (parameters.isPresent()) {
//            entity = getHttpEntity(headers, parameters.get());
//        }
//        else {
//            entity = new HttpEntity<>(null, headers);
//        }
//end TODO --------------------------------
        
        //build request entity holder
        JClass httpEntityClass = new JCodeModel().ref("org.springframework.http.HttpEntity");
        JInvocation init = JExpr._new(httpEntityClass);
        if (generatableType.params()!=null){
            generatableType.params().forEach(p -> init.arg(p));
        }
        init.arg(httpHeaders);
                
        JVar httpEntityVar = generatableType.body().decl(httpEntityClass, "httpEntity", init);        
                        
        //construct the HTTP Method enum 
        JDefinedClass httpMethod = null;
        try {
            httpMethod = new JCodeModel()._class("org.springframework.http.HttpMethod");
        }
        catch (JClassAlreadyExistsException e) {
            
        } 
        
        //build rest template exchange invocation
        JInvocation jInvocation = JExpr._this().ref(restTemplateFieldName).invoke("exchange");
        jInvocation.arg(baseUrl + endpointMetadata.getUrl());
        jInvocation.arg(httpEntityVar); //TODO add http headers 
        jInvocation.arg(httpMethod.enumConstant(endpointMetadata.getActionType().name()));
      
        generatableType.body()._return(jInvocation);

        return generatableType;
    }
  
}
