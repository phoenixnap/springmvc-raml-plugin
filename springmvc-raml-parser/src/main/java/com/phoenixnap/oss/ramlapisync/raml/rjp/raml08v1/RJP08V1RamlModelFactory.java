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
package com.phoenixnap.oss.ramlapisync.raml.rjp.raml08v1;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.raml.model.Action;
import org.raml.model.ActionType;
import org.raml.model.DocumentationItem;
import org.raml.model.MimeType;
import org.raml.model.ParamType;
import org.raml.model.Raml;
import org.raml.model.Resource;
import org.raml.model.Response;
import org.raml.model.SecurityReference;
import org.raml.model.parameter.FormParameter;
import org.raml.model.parameter.Header;
import org.raml.model.parameter.QueryParameter;
import org.raml.model.parameter.UriParameter;
import org.raml.parser.visitor.RamlDocumentBuilder;

import com.phoenixnap.oss.ramlapisync.data.RamlFormParameter;
import com.phoenixnap.oss.ramlapisync.raml.RamlAction;
import com.phoenixnap.oss.ramlapisync.raml.RamlActionType;
import com.phoenixnap.oss.ramlapisync.raml.RamlDocumentationItem;
import com.phoenixnap.oss.ramlapisync.raml.RamlHeader;
import com.phoenixnap.oss.ramlapisync.raml.RamlMimeType;
import com.phoenixnap.oss.ramlapisync.raml.RamlModelEmitter;
import com.phoenixnap.oss.ramlapisync.raml.RamlModelFactory;
import com.phoenixnap.oss.ramlapisync.raml.RamlParamType;
import com.phoenixnap.oss.ramlapisync.raml.RamlQueryParameter;
import com.phoenixnap.oss.ramlapisync.raml.RamlResource;
import com.phoenixnap.oss.ramlapisync.raml.RamlResponse;
import com.phoenixnap.oss.ramlapisync.raml.RamlRoot;
import com.phoenixnap.oss.ramlapisync.raml.RamlSecurityReference;
import com.phoenixnap.oss.ramlapisync.raml.RamlUriParameter;

/**
 * Implementation based on the Raml 0.8 Parser
 * 
 * @author armin.weisser
 * @since 0.8.1
 */
public class RJP08V1RamlModelFactory implements RamlModelFactory {

    @Override
    public RamlModelEmitter createRamlModelEmitter() {
        return new RJP08V1RamlModelEmitter();
    }

    @Override
    public RamlRoot buildRamlRoot(String ramlFileUrl) {
        return createRamlRoot(new RamlDocumentBuilder().build(ramlFileUrl));
    }

    @Override
    public RamlRoot createRamlRoot() {
        return createRamlRoot(new Raml());
    }

    @Override
    public RamlRoot createRamlRoot(String ramlFileUrl) {
        Raml raml = new RamlDocumentBuilder().build(ramlFileUrl);
        return createRamlRoot(raml);
    }

    RamlRoot createRamlRoot(Raml raml) {
        if (raml == null) {
            return null;
        }
        return new RJP08V1RamlRoot(raml);
    }

    @Override
    public RamlResource createRamlResource() {
        return createRamlResource(new Resource());
    }

    @Override
    public RamlResource createRamlResource(Object resource) {
        if (resource == null) {
            return null;
        }
        return new RJP08V1RamlResource((Resource) resource);
    }

    Resource extractResource(RamlResource ramlResource) {
        if (ramlResource == null) return null;
        return ((RJP08V1RamlResource) ramlResource).getResource();
    }

    @Override
    public RamlAction createRamlAction(Object action) {
        if (action == null) {
            return null;
        }
        return new RJP08V1RamlAction((Action) action);
    }

    @Override
    public RamlAction createRamlAction() {
        return createRamlAction(new Action());
    }

    Action extractAction(RamlAction ramlAction) {
        return ((RJP08V1RamlAction) ramlAction).getAction();
    }

    @Override
    public RamlDocumentationItem createRamlDocumentationItem() {
        return createRamlDocumentationItem(new DocumentationItem());
    }

    @Override
    public RamlDocumentationItem createRamlDocumentationItem(Object documentationItem) {
        return new RJP08V1RamlDocumentationItem((DocumentationItem)documentationItem);
    }

    List<DocumentationItem> extractDocumentationItems(List<RamlDocumentationItem> ramlRocumentationItems) {
        if (ramlRocumentationItems == null) {
            return null;
        }
        return ramlRocumentationItems.stream()
                .map(ramlDocumentationItem -> extractDocumentationItem(ramlDocumentationItem))
                .collect(Collectors.toList());
    }

    private DocumentationItem extractDocumentationItem(RamlDocumentationItem ramlDocumentationItem) {
        return ((RJP08V1RamlDocumentationItem) ramlDocumentationItem).getDocumentationItem();
    }

    @Override
    public RamlActionType createRamlActionType(Object type) {
        return RamlActionType.valueOf(((ActionType)type).name());
    }

    ActionType extractActionType(RamlActionType ramlActionType) {
        return ActionType.valueOf(ramlActionType.name());
    }

    @Override
    public RamlResponse createRamlResponse() {
        return createRamlResponse(new Response());
    }

    public RamlResponse createRamlResponse(Object response) {
        if(response == null) {
            return null;
        }
        return new RJP08V1RamlResponse((Response)response);
    }


    Response extractResponse(RamlResponse ramlResponse) {
        return ((RJP08V1RamlResponse)ramlResponse).getResponse();
    }

    @Override
    public RamlMimeType createRamlMimeType() {
        return createRamlMimeType(new MimeType());
    }

    @Override
    public RamlMimeType createRamlMimeTypeWithMime(String mime) {
        return createRamlMimeType(new MimeType(mime));
    }

    @Override
    public RamlMimeType createRamlMimeType(Object mimeType) {
        return new RJP08V1RamlMimeType((MimeType)mimeType);
    }

    @Override
    public RamlHeader createRamlHeader(Object header) {
        return new RJP08V1RamlHeader((Header)header);
    }

    @Override
    public RamlUriParameter createRamlUriParameter(Object uriParameter) {
        return new RJP08V1RamlUriParameter((UriParameter)uriParameter);
    }

    Map<String, MimeType> extractBody(Map<String, RamlMimeType> ramlBody) {
        Map<String, MimeType> body = new LinkedHashMap<>(ramlBody.size());
        for(String key: ramlBody.keySet()) {
            body.put(key, extractMimeType(ramlBody.get(key)));
        }
        return body;
    }

    MimeType extractMimeType(RamlMimeType ramlMimeType) {
        return ((RJP08V1RamlMimeType)ramlMimeType).getMimeType();
    }

    UriParameter extractUriParameter(RamlUriParameter ramlUriParameter) {
        return ((RJP08V1RamlUriParameter)ramlUriParameter).getUriParameter();
    }

    @Override
    public RamlUriParameter createRamlUriParameterWithName(String name) {
        return new RJP08V1RamlUriParameter(new UriParameter(name));
    }

    @Override
    public RamlQueryParameter createRamlQueryParameter() {
        return createRamlQueryParameter(new QueryParameter());
    }

    @Override
    public RamlQueryParameter createRamlQueryParameter(Object queryParameter) {
        return new RJP08V1RamlQueryParameter((QueryParameter)queryParameter);
    }

    QueryParameter extractQueryParameter(RamlQueryParameter ramlQueryParameter) {
        return ((RJP08V1RamlQueryParameter)ramlQueryParameter).getQueryParameter();
    }

    Map<String, List<FormParameter>> extractFormParameters(Map<String, List<RamlFormParameter>> ramlFormParameters) {
        Map<String, List<FormParameter>> formParameters = new LinkedHashMap<>(ramlFormParameters.size());
        for(String key: ramlFormParameters.keySet()) {
            formParameters.put(key, extractFormParameters(ramlFormParameters.get(key)));
        }
        return formParameters;
    }

    List<FormParameter> extractFormParameters(List<RamlFormParameter> ramlFormParameters) {
        return ramlFormParameters.stream().map(this::extractFormParameter).collect(Collectors.toList());
    }

    FormParameter extractFormParameter(RamlFormParameter ramlFormParameter) {
        return ((RJP08V1RamlFormParameter)ramlFormParameter).getFormParameter();
    }

    @Override
    public RamlFormParameter createRamlFormParameter() {
        return createRamlFormParameter(new FormParameter());
    }

    @Override
    public List<RamlFormParameter> createRamlFormParameters(List<? extends Object> formParameters) {
        return formParameters.stream().map(this::createRamlFormParameter).collect(Collectors.toList());
    }

    @Override
    public RamlFormParameter createRamlFormParameter(Object formParameter) {
        return new RJP08V1RamlFormParameter((FormParameter)formParameter);
    }

    @Override
    public List<RamlSecurityReference> createRamlSecurityReferences(List<? extends Object> securityReferences) {
        return securityReferences.stream().map(this::createRamlSecurityReference).collect(Collectors.toList());
    }

    @Override
    public RamlSecurityReference createRamlSecurityReference(Object securityReference) {
        return new RJP08V1RamlSecurityReference((SecurityReference)securityReference);
    }

    @Override
    public RamlParamType createRamlParamType(Object paramType) {
        return RamlParamType.valueOf(((ParamType)paramType).name());
    }

    ParamType extractRamlParam(RamlParamType ramlParamType) {
        return ParamType.valueOf(ramlParamType.name());
    }
}
