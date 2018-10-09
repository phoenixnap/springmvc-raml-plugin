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

import com.phoenixnap.oss.ramlplugin.raml2code.data.RamlFormParameter;
import com.phoenixnap.oss.ramlplugin.raml2code.helpers.RamlTypeHelper;
import com.phoenixnap.oss.ramlplugin.raml2code.raml.RamlParamType;

/**
 * Implementation based on the Raml 1.0 Parser
 * 
 * @author Aleksandar Stojsavljevic
 * @since 0.10.0
 */
public class RJP10V2RamlFormParameter extends RamlFormParameter {

	private static RJP10V2RamlModelFactory ramlModelFactory = new RJP10V2RamlModelFactory();

	private final TypeDeclaration formParameter;

	public RJP10V2RamlFormParameter(TypeDeclaration formParameter) {
		this.formParameter = formParameter;
	}

	/**
	 * Expose internal representation only package private
	 * 
	 * @return the internal model
	 */
	TypeDeclaration getFormParameter() {
		return formParameter;
	}

	@Override
	public boolean isRequired() {
		return RamlTypeHelper.isRequired(formParameter);
	}

	@Override
	public RamlParamType getType() {
		return ramlModelFactory.createRamlParamType(formParameter.type());
	}

	@Override
	public String getExample() {
		return RamlTypeHelper.getExample(formParameter);
	}

	@Override
	public String getDescription() {
		return RamlTypeHelper.getDescription(formParameter);
	}

	@Override
	public String getDisplayName() {
		return RamlTypeHelper.getDisplayName(formParameter);
	}

	@Override
	public String getDefaultValue() {
		return formParameter.defaultValue();
	}

	@Override
	public String getPattern() {
		if (formParameter instanceof StringTypeDeclaration) {
			return ((StringTypeDeclaration) formParameter).pattern();
		}
		return null;
	}

	@Override
	public boolean isRepeat() {
		return RamlTypeHelper.isArray(formParameter);
	}

	@Override
	public String getFormat() {
		return RamlTypeHelper.getFormat(this.formParameter);
	}

	@Override
	public Integer getMinLength() {
		if (formParameter instanceof StringTypeDeclaration) {
			return ((StringTypeDeclaration) formParameter).minLength();
		}
		return null;
	}

	@Override
	public Integer getMaxLength() {
		if (formParameter instanceof StringTypeDeclaration) {
			return ((StringTypeDeclaration) formParameter).maxLength();
		}
		return null;
	}

	@Override
	public BigDecimal getMinimum() {
		if (formParameter instanceof NumberTypeDeclaration) {
			Double minimum = ((NumberTypeDeclaration) formParameter).minimum();
			if (minimum != null) {
				return BigDecimal.valueOf(minimum);
			}
		}
		return null;
	}

	@Override
	public BigDecimal getMaximum() {
		if (formParameter instanceof NumberTypeDeclaration) {
			Double maximum = ((NumberTypeDeclaration) formParameter).maximum();
			if (maximum != null) {
				return BigDecimal.valueOf(maximum);
			}
		}
		return null;
	}

	@Override
	public String getName() {
		return this.formParameter.name();
	}

	@Override
	public List<AnnotationRef> getAnnotations() {
		return this.formParameter.annotations();
	}

	@Override
	public String getRawType() {
		return this.formParameter.type();
	}
}
