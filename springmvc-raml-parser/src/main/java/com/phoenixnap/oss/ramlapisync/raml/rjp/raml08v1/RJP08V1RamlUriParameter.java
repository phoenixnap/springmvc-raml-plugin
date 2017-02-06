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

import org.raml.model.parameter.UriParameter;

import com.phoenixnap.oss.ramlapisync.raml.RamlParamType;
import com.phoenixnap.oss.ramlapisync.raml.RamlUriParameter;

/**
 * Implementation based on the Raml 0.8 Parser
 * 
 * @author armin.weisser
 * @author Aleksandar Stojsavljevic
 * @since 0.8.1
 */
public class RJP08V1RamlUriParameter extends RamlUriParameter {

    private static RJP08V1RamlModelFactory ramlModelFactory = new RJP08V1RamlModelFactory();

    private final UriParameter uriParameter;

    public RJP08V1RamlUriParameter(UriParameter uriParameter) {
        this.uriParameter = uriParameter;
    }

    /**
     * Expose internal representation only package private
     * @return the internal model
     */
    UriParameter getUriParameter() {
        return uriParameter;
    }

    @Override
    public String getDisplayName() {
        return uriParameter.getDisplayName();
    }

    @Override
    public void setDisplayName(String displayName) {
        uriParameter.setDisplayName(displayName);
    }

    @Override
    public RamlParamType getType() {
        return ramlModelFactory.createRamlParamType(uriParameter.getType());
    }

    @Override
    public void setType(RamlParamType paramType) {
        uriParameter.setType(ramlModelFactory.extractRamlParam(paramType));
    }

    @Override
    public boolean isRequired() {
        return uriParameter.isRequired();
    }

    @Override
    public void setRequired(boolean required) {
        uriParameter.setRequired(required);
    }

    @Override
    public void setExample(String example) {
        uriParameter.setExample(example);
    }

    @Override
    public void setDescription(String description) {
        uriParameter.setDescription(description);
    }

    @Override
    public String getExample() {
        return uriParameter.getExample();
    }

    @Override
    public String getDescription() {
        return uriParameter.getDescription();
    }

	@Override
	public String getDefaultValue() {
		return uriParameter.getDefaultValue();
	}
}
