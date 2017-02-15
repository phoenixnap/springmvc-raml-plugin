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

import java.util.List;
import java.util.Map;

import org.raml.v2.api.model.v10.datamodel.ExternalTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.ObjectTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;

import com.phoenixnap.oss.ramlapisync.data.RamlFormParameter;
import com.phoenixnap.oss.ramlapisync.naming.RamlTypeHelper;
import com.phoenixnap.oss.ramlapisync.raml.RamlDataType;
import com.phoenixnap.oss.ramlapisync.raml.RamlMimeType;

/**
 * Implementation based on the Raml 1.0 Parser
 * 
 * @author Aleksandar Stojsavljevic
 * @since 0.10.0
 */
public class RJP10V2RamlMimeType implements RamlMimeType {


    private final TypeDeclaration mimeType;


    public RJP10V2RamlMimeType(TypeDeclaration mimeType) {
        this.mimeType = mimeType;
    }

    /**
     * Expose internal representation only package private
     * @return the internal model
     */
    TypeDeclaration getMimeType() {
        return mimeType;
    }

    @Override
    public Map<String, List<RamlFormParameter>> getFormParameters() {
    	throw new UnsupportedOperationException();
    }

    @Override
    public void setFormParameters(Map<String, List<RamlFormParameter>> formParameters) {
    	throw new UnsupportedOperationException();
    }

    @Override
    public String getSchema() {
    	if (RamlTypeHelper.isSchemaType(mimeType)) {
    		return ((ExternalTypeDeclaration) mimeType).schemaContent();
    	} else {
    		return null;
    	}
    }

    @Override
    public void setSchema(String schema) {
    	throw new UnsupportedOperationException();
    }

    @Override
    public void setExample(String example) {
    	throw new UnsupportedOperationException();
    }

    @Override
    public void addFormParameters(String name, List<RamlFormParameter> ramlFormParameters) {
    	throw new UnsupportedOperationException();
    }

	@Override
	public RamlDataType getType() {
   		return new RJP10V2RamlDataType(mimeType);
	}

	@Override
	public void setType(RamlDataType type) {
		throw new UnsupportedOperationException();
	}
	
	

}
