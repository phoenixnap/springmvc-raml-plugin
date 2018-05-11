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
package com.phoenixnap.oss.ramlplugin.raml2code.raml.raml10;

import java.math.BigDecimal;
import java.util.List;

import org.raml.v2.api.model.v10.datamodel.NumberTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.StringTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;
import org.raml.v2.api.model.v10.declarations.AnnotationRef;

import com.phoenixnap.oss.ramlplugin.raml2code.helpers.RamlTypeHelper;
import com.phoenixnap.oss.ramlplugin.raml2code.raml.RamlParamType;
import com.phoenixnap.oss.ramlplugin.raml2code.raml.RamlUriParameter;

/**
 * Implementation based on the Raml 1.0 Parser
 * 
 * @author Aleksandar Stojsavljevic
 * @since 0.10.0
 */
public class RJP10V2RamlUriParameter extends RamlUriParameter {

	private static RJP10V2RamlModelFactory ramlModelFactory = new RJP10V2RamlModelFactory();

	private final TypeDeclaration uriParameter;

	public RJP10V2RamlUriParameter(TypeDeclaration uriParameter) {
		this.uriParameter = uriParameter;
	}

	/**
	 * Expose internal representation only package private
	 * 
	 * @return the internal model
	 */
	TypeDeclaration getUriParameter() {
		return uriParameter;
	}

	@Override
	public String getDisplayName() {
		return RamlTypeHelper.getDisplayName(this.uriParameter);
	}

	@Override
	public RamlParamType getType() {
		return ramlModelFactory.createRamlParamType(this.uriParameter.type());
	}

	@Override
	public boolean isRequired() {
		return RamlTypeHelper.isRequired(this.uriParameter);
	}

	@Override
	public String getExample() {
		return RamlTypeHelper.getExample(this.uriParameter);
	}

	@Override
	public String getDescription() {
		return RamlTypeHelper.getDescription(this.uriParameter);
	}

	@Override
	public String getDefaultValue() {
		return this.uriParameter.defaultValue();
	}

	@Override
	public String getPattern() {
		if (uriParameter instanceof StringTypeDeclaration) {
			return ((StringTypeDeclaration) uriParameter).pattern();
		}
		return null;
	}

	@Override
	public String getFormat() {
		return RamlTypeHelper.getFormat(this.uriParameter);
	}

	@Override
	public Integer getMinLength() {
		if (uriParameter instanceof StringTypeDeclaration) {
			return ((StringTypeDeclaration) uriParameter).minLength();
		}
		return null;
	}

	@Override
	public Integer getMaxLength() {
		if (uriParameter instanceof StringTypeDeclaration) {
			return ((StringTypeDeclaration) uriParameter).maxLength();
		}
		return null;
	}

	@Override
	public BigDecimal getMinimum() {
		if (uriParameter instanceof NumberTypeDeclaration) {
			Double minimum = ((NumberTypeDeclaration) uriParameter).minimum();
			if (minimum != null) {
				return BigDecimal.valueOf(minimum);
			}
		}
		return null;
	}

	@Override
	public BigDecimal getMaximum() {
		if (uriParameter instanceof NumberTypeDeclaration) {
			Double maximum = ((NumberTypeDeclaration) uriParameter).maximum();
			if (maximum != null) {
				return BigDecimal.valueOf(maximum);
			}
		}
		return null;
	}

	@Override
	public String getName() {
		return this.uriParameter.name();
	}

	@Override
	public List<AnnotationRef> getAnnotations() {
		return this.uriParameter.annotations();
	}

	@Override
	public String getRawType() {
		return this.uriParameter.type();
	}
}
