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

import org.raml.v2.api.model.v10.datamodel.StringTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;

import com.phoenixnap.oss.ramlapisync.naming.RamlTypeHelper;
import com.phoenixnap.oss.ramlapisync.raml.RamlHeader;
import com.phoenixnap.oss.ramlapisync.raml.RamlParamType;

/**
 * Implementation based on the Raml 1.0 Parser
 * 
 * @author Aleksandar Stojsavljevic
 * @since 0.10.0
 */
public class RJP10V2RamlHeader extends RamlHeader {

    private static RJP10V2RamlModelFactory ramlModelFactory = new RJP10V2RamlModelFactory();

    private final TypeDeclaration header;

    public RJP10V2RamlHeader(TypeDeclaration header) {
        this.header = header;
    }

    @Override
    public String getDisplayName() {
        return RamlTypeHelper.getDisplayName(header);
    }

    @Override
    public void setDisplayName(String displayName) {
    	throw new UnsupportedOperationException();
    }

    @Override
    public RamlParamType getType() {
        return ramlModelFactory.createRamlParamType(header.type());
    }

    @Override
    public void setType(RamlParamType paramType) {
    	throw new UnsupportedOperationException();
    }

    @Override
    public boolean isRequired() {
        return RamlTypeHelper.isRequired(header);
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
    public String getExample() {
        return RamlTypeHelper.getExample(header);
    }

    @Override
    public String getDescription() {
        return RamlTypeHelper.getDescription(header);
    }

	@Override
	public String getDefaultValue() {
		return header.defaultValue();
	}

	@Override
   public String getPattern() {
      if (header instanceof StringTypeDeclaration) {
         return ((StringTypeDeclaration) header).pattern();
      }
      return null;
   }
	
	@Override
	public void setType(String type) {
		throw new UnsupportedOperationException();		
	}

	@Override
	public String getFormat() {
		return RamlTypeHelper.getFormat(this.header);
	}
}
