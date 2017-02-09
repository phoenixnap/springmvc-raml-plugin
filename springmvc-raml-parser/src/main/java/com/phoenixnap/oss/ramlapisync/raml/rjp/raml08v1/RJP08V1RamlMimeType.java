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

import com.phoenixnap.oss.ramlapisync.data.RamlFormParameter;
import com.phoenixnap.oss.ramlapisync.raml.RamlDataType;
import com.phoenixnap.oss.ramlapisync.raml.RamlMimeType;

import org.raml.model.MimeType;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Implementation based on the Raml 0.8 Parser
 * 
 * @author armin.weisser
 * @since 0.8.1
 */
public class RJP08V1RamlMimeType implements RamlMimeType {

    private static RJP08V1RamlModelFactory ramlModelFactory = new RJP08V1RamlModelFactory();

    private final MimeType mimeType;

    private Map<String, List<RamlFormParameter>> formParameters = new LinkedHashMap<>();

    public RJP08V1RamlMimeType(MimeType mimeType) {
        this.mimeType = mimeType;
    }

    /**
     * Expose internal representation only package private
     * @return the internal model
     */
    MimeType getMimeType() {
        return mimeType;
    }

    @Override
    public Map<String, List<RamlFormParameter>> getFormParameters() {
        return ramlModelFactory.transformToUnmodifiableMap(mimeType.getFormParameters(), formParameters, ramlModelFactory::createRamlFormParameters);
    }

    @Override
    public void setFormParameters(Map<String, List<RamlFormParameter>> formParameters) {
        this.formParameters = formParameters;
        mimeType.setFormParameters(ramlModelFactory.extractFormParameters(formParameters));
    }

    @Override
    public String getSchema() {
        return mimeType.getSchema();
    }

    @Override
    public void setSchema(String schema) {
        mimeType.setSchema(schema);
    }

    @Override
    public void setExample(String example) {
        mimeType.setExample(example);
    }

    @Override
    public void addFormParameters(String name, List<RamlFormParameter> ramlFormParameters) {
        this.formParameters.put(name, ramlFormParameters);
        if(this.mimeType.getFormParameters() == null) {
            this.mimeType.setFormParameters(new LinkedHashMap<>());
        }
        this.mimeType.getFormParameters().put(name, ramlModelFactory.extractFormParameters(ramlFormParameters));
    }

	@Override
	public RamlDataType getType() {
		return null; //RAML 0.8 does not support types so this is always assumed to be empty.
	}

	@Override
	public void setType(RamlDataType type) {
		throw new UnsupportedOperationException();		
	}

}
