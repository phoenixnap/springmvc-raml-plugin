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

import java.math.BigDecimal;

import org.raml.model.parameter.QueryParameter;

import com.phoenixnap.oss.ramlapisync.raml.RamlParamType;
import com.phoenixnap.oss.ramlapisync.raml.RamlQueryParameter;

/**
 * Implementation based on the Raml 0.8 Parser
 * 
 * @author armin.weisser
 * @author Aleksandar Stojsavljevic
 * @since 0.8.1
 */
public class RJP08V1RamlQueryParameter extends RamlQueryParameter {

    private static RJP08V1RamlModelFactory ramlModelFactory = new RJP08V1RamlModelFactory();

    private final QueryParameter queryParameter;

    public RJP08V1RamlQueryParameter(QueryParameter queryParameter) {
        this.queryParameter = queryParameter;
    }

    /**
     * Expose internal representation only package private
     * @return the internal model
     */
    QueryParameter getQueryParameter() {
        return queryParameter;

    }

    @Override
    public void setType(RamlParamType paramType) {
        queryParameter.setType(ramlModelFactory.extractRamlParam(paramType));
    }

    @Override
    public void setRequired(boolean required) {
        queryParameter.setRequired(required);
    }

    @Override
    public void setExample(String example) {
        queryParameter.setExample(example);
    }

    @Override
    public void setDescription(String description) {
        queryParameter.setDescription(description);
    }

    @Override
    public boolean isRequired() {
        return queryParameter.isRequired();
    }

    @Override
    public RamlParamType getType() {
        return ramlModelFactory.createRamlParamType(queryParameter.getType());
    }

    @Override
    public String getExample() {
        return queryParameter.getExample();
    }

    @Override
    public void setDisplayName(String displayName) {
        queryParameter.setDisplayName(displayName);
    }

    @Override
    public String getDescription() {
        return queryParameter.getDescription();
    }

    @Override
    public void setRepeat(boolean repeat) {
        queryParameter.setRepeat(repeat);
    }

    @Override
    public Integer getMinLength() {
        return queryParameter.getMinLength();
    }

    @Override
    public Integer getMaxLength() {
        return queryParameter.getMaxLength();
    }

    @Override
    public BigDecimal getMinimum() {
        return queryParameter.getMinimum();
    }

    @Override
    public BigDecimal getMaximum() {
        return queryParameter.getMaximum();
    }

    @Override
    public String getPattern() {
        return queryParameter.getPattern();
    }

    @Override
    public String getDisplayName() {
        return queryParameter.getDisplayName();
    }

    @Override
    public boolean isRepeat() {
        return queryParameter.isRepeat();
    }

	@Override
	public String getDefaultValue() {
		return queryParameter.getDefaultValue();
	}
	
	@Override
	public void setType(String type) {
		throw new UnsupportedOperationException();		
	}

	@Override
	public String getFormat() {
		return null;
	}
}
