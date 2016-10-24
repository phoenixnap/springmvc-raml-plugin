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
package com.phoenixnap.oss.ramlapisync.raml.rjp.raml08v1;

import org.raml.model.parameter.FormParameter;

import com.phoenixnap.oss.ramlapisync.data.RamlFormParameter;
import com.phoenixnap.oss.ramlapisync.raml.RamlParamType;

/**
 * Implementation based on the Raml 0.8 Parser
 * 
 * @author armin.weisser
 * @author Aleksandar Stojsavljevic
 * @since 0.8.1
 */
public class RJP08V1RamlFormParameter extends RamlFormParameter {

    private static RJP08V1RamlModelFactory ramlModelFactory = new RJP08V1RamlModelFactory();

    private final FormParameter formParameter;

    public RJP08V1RamlFormParameter(FormParameter formParameter) {
        this.formParameter = formParameter;
    }

    /**
     * Expose internal representation only package private
     * @return the internal model
     */
    FormParameter getFormParameter() {
        return formParameter;
    }

    @Override
    public void setType(RamlParamType paramType) {
        formParameter.setType(ramlModelFactory.extractRamlParam(paramType));
    }

    @Override
    public void setRequired(boolean required) {
        formParameter.setRequired(required);
    }

    @Override
    public void setExample(String example) {
        formParameter.setExample(example);
    }

    @Override
    public void setDescription(String description) {
        formParameter.setDescription(description);
    }

    @Override
    public boolean isRequired() {
        return formParameter.isRequired();
    }

    @Override
    public RamlParamType getType() {
        return ramlModelFactory.createRamlParamType(formParameter.getType());
    }

    @Override
    public String getExample() {
        return formParameter.getExample();
    }

    @Override
    public void setDisplayName(String displayName) {
        formParameter.setDisplayName(displayName);
    }

    @Override
    public String getDescription() {
        return formParameter.getDescription();
    }

    @Override
    public String getDisplayName() {
        return formParameter.getDisplayName();
    }

	@Override
	public String getDefaultValue() {
		return formParameter.getDefaultValue();
	}
}
