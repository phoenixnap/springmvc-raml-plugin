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

import com.phoenixnap.oss.ramlapisync.naming.RamlTypeHelper;
import com.phoenixnap.oss.ramlapisync.raml.RamlMimeType;
import com.phoenixnap.oss.ramlapisync.raml.RamlResponse;

import java.util.LinkedHashMap;
import java.util.Map;

import org.raml.v2.api.model.v10.bodies.Response;

/**
 * Implementation based on the Raml 1.0 Parser
 * 
 * @author Aleksandar Stojsavljevic
 * @since 0.10.0
 */
public class RJP10V2RamlResponse implements RamlResponse {

    private static RJP10V2RamlModelFactory ramlModelFactory = new RJP10V2RamlModelFactory();

    private final Response response;

    private Map<String, RamlMimeType> body = new LinkedHashMap<>();

    public RJP10V2RamlResponse(Response response) {
        this.response = response;
        ramlModelFactory.transformToUnmodifiableMap(
        		response.body(),
        		body,
                ramlModelFactory::createRamlMimeType,
                r -> RamlTypeHelper.getDisplayName(r));
    }

    /**
     * Expose internal representation only package private
     * @return the internal model
     */
    Response getResponse() {
        return response;
    }

    @Override
    public void addToBody(String key, RamlMimeType value) {
    	throw new UnsupportedOperationException();
    }

    @Override
    public Map<String, RamlMimeType> getBody() {
    	return body;
    }

    @Override
    public void setBody(Map<String, RamlMimeType> body) {
    	throw new UnsupportedOperationException();
    }

    @Override
    public boolean hasBody() {
        return !response.body().isEmpty();
    }


    @Override
    public void setDescription(String description) {
    	throw new UnsupportedOperationException();
    }

    @Override
    public String getDescription() {
        return response.description().value();
    }


}
