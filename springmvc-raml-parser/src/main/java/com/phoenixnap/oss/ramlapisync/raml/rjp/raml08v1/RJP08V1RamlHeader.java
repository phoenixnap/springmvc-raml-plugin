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

import org.raml.model.parameter.Header;

import com.phoenixnap.oss.ramlapisync.raml.RamlHeader;
import com.phoenixnap.oss.ramlapisync.raml.RamlParamType;

/**
 * Implementation based on the Raml 0.8 Parser
 * 
 * @author armin.weisser
 * @author Aleksandar Stojsavljevic
 * @since 0.8.1
 */
public class RJP08V1RamlHeader extends RamlHeader {

    private static RJP08V1RamlModelFactory ramlModelFactory = new RJP08V1RamlModelFactory();

    private final Header header;

    public RJP08V1RamlHeader(Header header) {
        this.header = header;
    }

    @Override
    public String getDisplayName() {
        return header.getDisplayName();
    }

    @Override
    public void setDisplayName(String displayName) {
        header.setDisplayName(displayName);
    }

    @Override
    public RamlParamType getType() {
        return ramlModelFactory.createRamlParamType(header.getType());
    }

    @Override
    public void setType(RamlParamType paramType) {
        header.setType(ramlModelFactory.extractRamlParam(paramType));
    }

    @Override
    public boolean isRequired() {
        return header.isRequired();
    }

    @Override
    public void setRequired(boolean required) {
        header.setRequired(required);
    }

    @Override
    public void setExample(String example) {
        header.setExample(example);
    }

    @Override
    public void setDescription(String description) {
        header.setDescription(description);
    }

    @Override
    public String getExample() {
        return header.getExample();
    }

    @Override
    public String getDescription() {
        return header.getDescription();
    }

	@Override
	public String getDefaultValue() {
		return header.getDefaultValue();
	}
}
