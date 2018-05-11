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

import org.raml.v2.api.model.v10.datamodel.ArrayTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.NumberTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.StringTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;
import org.raml.v2.api.model.v10.declarations.AnnotationRef;

import com.phoenixnap.oss.ramlplugin.raml2code.helpers.RamlTypeHelper;
import com.phoenixnap.oss.ramlplugin.raml2code.raml.RamlDataType;
import com.phoenixnap.oss.ramlplugin.raml2code.raml.RamlParamType;
import com.phoenixnap.oss.ramlplugin.raml2code.raml.RamlQueryParameter;

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
	 * 
	 * @return the internal model
	 */
	TypeDeclaration getQueryParameter() {
		return queryParameter;

	}

	@Override
	public boolean isRequired() {
		return RamlTypeHelper.isRequired(queryParameter);
	}

	@Override
	public RamlParamType getType() {
		if (queryParameter instanceof ArrayTypeDeclaration) {
			TypeDeclaration items = ((ArrayTypeDeclaration) queryParameter).items();
			return ramlModelFactory.createRamlParamType(items.type());
		}
		return ramlModelFactory.createRamlParamType(queryParameter.type());
	}

	@Override
	public String getRawType() {
		return queryParameter.type();
	}

	@Override
	public String getExample() {
		return RamlTypeHelper.getExample(queryParameter);
	}

	@Override
	public String getDescription() {
		return RamlTypeHelper.getDescription(queryParameter);
	}

	@Override
	public Integer getMinLength() {
		if (queryParameter instanceof StringTypeDeclaration) {
			return ((StringTypeDeclaration) queryParameter).minLength();
		}
		return null;
	}

	@Override
	public Integer getMaxLength() {
		if (queryParameter instanceof StringTypeDeclaration) {
			return ((StringTypeDeclaration) queryParameter).maxLength();
		}
		return null;
	}

	@Override
	public BigDecimal getMinimum() {
		if (queryParameter instanceof NumberTypeDeclaration) {
			Double minimum = ((NumberTypeDeclaration) queryParameter).minimum();
			if (minimum != null) {
				return BigDecimal.valueOf(minimum);
			}
		}
		return null;
	}

	@Override
	public BigDecimal getMaximum() {
		if (queryParameter instanceof NumberTypeDeclaration) {
			Double maximum = ((NumberTypeDeclaration) queryParameter).maximum();
			if (maximum != null) {
				return BigDecimal.valueOf(maximum);
			}
		}
		return null;
	}

	@Override
	public String getPattern() {
		if (queryParameter instanceof StringTypeDeclaration) {
			return ((StringTypeDeclaration) queryParameter).pattern();
		}
		return null;
	}

	@Override
	public String getDisplayName() {
		return RamlTypeHelper.getDisplayName(queryParameter);
	}

	@Override
	public boolean isRepeat() {
		return RamlTypeHelper.isArray(queryParameter);
	}

	@Override
	public String getDefaultValue() {
		return this.queryParameter.defaultValue();
	}

	@Override
	public String getFormat() {
		return RamlTypeHelper.getFormat(this.queryParameter);
	}

	public RamlDataType getRamlDataType() {
		return new RJP10V2RamlDataType(this.queryParameter);
	}

	@Override
	public String getName() {
		return this.queryParameter.name();
	}

	@Override
	public List<AnnotationRef> getAnnotations() {
		return this.queryParameter.annotations();
	}
}
