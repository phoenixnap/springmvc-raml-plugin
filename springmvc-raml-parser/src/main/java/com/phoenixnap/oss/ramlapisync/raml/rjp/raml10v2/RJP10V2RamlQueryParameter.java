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

import java.math.BigDecimal;

import org.raml.v2.api.model.v10.datamodel.StringTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;

import com.phoenixnap.oss.ramlapisync.raml.RamlParamType;
import com.phoenixnap.oss.ramlapisync.raml.RamlQueryParameter;

/**
 * Implementation based on the Raml 1.0 Parser
 * 
 * @author Aleksandar Stojsavljevic
 * @since 0.10.0
 */
public class RJP10V2RamlQueryParameter extends RamlQueryParameter {

    private static RJP10V2RamlModelFactory ramlModelFactory = new RJP10V2RamlModelFactory();

    private final TypeDeclaration queryParameter;

    public RJP10V2RamlQueryParameter(TypeDeclaration queryParameter) {
        this.queryParameter = queryParameter;
    }

    /**
     * Expose internal representation only package private
     * @return the internal model
     */
    TypeDeclaration getQueryParameter() {
        return queryParameter;

    }

    @Override
    public void setType(RamlParamType paramType) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setRequired(boolean required) {
    	throw new UnsupportedOperationException();
    }

    @Override
    public void setExample(String example) {
    	throw new UnsupportedOperationException();
    }

    @Override
    public void setDescription(String description) {
    	throw new UnsupportedOperationException();
    }

    @Override
    public boolean isRequired() {
        return queryParameter.required().booleanValue();
    }

    @Override
    public RamlParamType getType() {
        return ramlModelFactory.createRamlParamType(queryParameter.type());
    }

    @Override
    public String getExample() {
        return queryParameter.example().value();
    }

    @Override
    public void setDisplayName(String displayName) {
    	throw new UnsupportedOperationException();
    }

    @Override
    public String getDescription() {
        return queryParameter.description().value();
    }

    @Override
    public void setRepeat(boolean repeat) {
    	throw new UnsupportedOperationException();
    }

    @Override
    public Integer getMinLength() {
    	if(queryParameter instanceof StringTypeDeclaration){
    		return ((StringTypeDeclaration) queryParameter).minLength();
    	}
        throw new UnsupportedOperationException();
    }

    @Override
    public Integer getMaxLength() {
    	if(queryParameter instanceof StringTypeDeclaration){
    		return ((StringTypeDeclaration) queryParameter).maxLength();
    	}
        throw new UnsupportedOperationException();
    }

    @Override
    public BigDecimal getMinimum() {
    	throw new UnsupportedOperationException();
    }

    @Override
    public BigDecimal getMaximum() {
    	throw new UnsupportedOperationException();
    }

    @Override
    public String getPattern() {
    	throw new UnsupportedOperationException();
    }

    @Override
    public String getDisplayName() {
        return this.queryParameter.displayName().value();
    }

    @Override
    public boolean isRepeat() {
    	throw new UnsupportedOperationException();
    }

	@Override
	public String getDefaultValue() {
		return this.queryParameter.defaultValue();
	}
}
