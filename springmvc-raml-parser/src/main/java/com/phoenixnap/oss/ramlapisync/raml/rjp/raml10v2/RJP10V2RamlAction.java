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
package com.phoenixnap.oss.ramlapisync.raml.rjp.raml10v2;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.raml.v2.api.model.v10.methods.Method;

import com.phoenixnap.oss.ramlapisync.raml.RamlAction;
import com.phoenixnap.oss.ramlapisync.raml.RamlActionType;
import com.phoenixnap.oss.ramlapisync.raml.RamlHeader;
import com.phoenixnap.oss.ramlapisync.raml.RamlMimeType;
import com.phoenixnap.oss.ramlapisync.raml.RamlQueryParameter;
import com.phoenixnap.oss.ramlapisync.raml.RamlResource;
import com.phoenixnap.oss.ramlapisync.raml.RamlResponse;
import com.phoenixnap.oss.ramlapisync.raml.RamlSecurityReference;

/**
 * Implementation based on the Raml 1.0 Parser
 * 
 * @author Aleksandar Stojsavljevic
 * @since 0.10.0
 */
public class RJP10V2RamlAction implements RamlAction {

    private static RJP10V2RamlModelFactory ramlModelFactory = new RJP10V2RamlModelFactory();

    private final Method method;

    private Map<String, RamlResponse> responses = new LinkedHashMap<>();

    private Map<String, RamlMimeType> body = new LinkedHashMap<>();

    private Map<String, RamlHeader> headers = new LinkedHashMap<>();

    private Map<String, RamlQueryParameter> queryParameters = new LinkedHashMap<>();

    public RJP10V2RamlAction(Method method) {
        this.method = method;
    }

    /**
     * Expose internal representation only package private
     * @return the internal model
     */
    Method getMethod() {
        return method;
    }

    @Override
    public RamlActionType getType() {
        return RamlActionType.valueOf(this.method.method().toUpperCase());
    }

    @Override
    public Map<String, RamlQueryParameter> getQueryParameters() {
    	return ramlModelFactory.transformToUnmodifiableMap(
                method.queryParameters(),
                queryParameters,
                ramlModelFactory::createRamlQueryParameter,
                r -> r.displayName().value());
    }

    @Override
    public Map<String, RamlResponse> getResponses() {
    	throw new UnsupportedOperationException();
    }

    @Override
    public void addResponse(String httpStatus, RamlResponse response) {
    	throw new UnsupportedOperationException();
    }

    @Override
    public RamlResource getResource() {
        return ramlModelFactory.createRamlResource(method.resource());
    }

    @Override
    public Map<String, RamlHeader> getHeaders() {
    	return ramlModelFactory.transformToUnmodifiableMap(
                method.headers(),
                headers,
                ramlModelFactory::createRamlHeader,
                r -> r.displayName().value());
    }

    @Override
    public Map<String, RamlMimeType> getBody() {
    	return ramlModelFactory.transformToUnmodifiableMap(
                method.body(),
                body,
                ramlModelFactory::createRamlMimeType,
                r -> r.displayName().value());
    }

    @Override
    public void setBody(Map<String, RamlMimeType> body) {
    	throw new UnsupportedOperationException();
    }

    @Override
    public boolean hasBody() {
        return !method.body().isEmpty();
    }

    @Override
    public String getDescription() {
        return method.description().value();
    }

    @Override
    public void setDescription(String description) {
    	throw new UnsupportedOperationException();
    }
    
    @Override
	public String getDisplayName() {
		return method.displayName().value();
	}

	@Override
	public void setDisplayName(String displayName) {
		throw new UnsupportedOperationException();
	}

    @Override
    public void setResource(RamlResource resource) {
    	throw new UnsupportedOperationException();
    }

    @Override
    public void setType(RamlActionType actionType) {
    	throw new UnsupportedOperationException();
    }

    @Override
    public List<RamlSecurityReference> getSecuredBy() {
        return ramlModelFactory.createRamlSecurityReferences(method.securedBy());
    }

    @Override
    public void addQueryParameters(Map<String, RamlQueryParameter> queryParameters) {
    	throw new UnsupportedOperationException();
    }

    private void addQueryParameter(String key, RamlQueryParameter ramlQueryParameter) {
    	throw new UnsupportedOperationException();
    }
}
