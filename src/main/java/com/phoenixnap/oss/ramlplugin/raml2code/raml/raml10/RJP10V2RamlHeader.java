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
import com.phoenixnap.oss.ramlplugin.raml2code.raml.RamlHeader;
import com.phoenixnap.oss.ramlplugin.raml2code.raml.RamlParamType;

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
	public RamlParamType getType() {
		return ramlModelFactory.createRamlParamType(header.type());
	}

	@Override
	public boolean isRequired() {
		return RamlTypeHelper.isRequired(header);
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
	public String getFormat() {
		return RamlTypeHelper.getFormat(this.header);
	}

	@Override
	public Integer getMinLength() {
		if (header instanceof StringTypeDeclaration) {
			return ((StringTypeDeclaration) header).minLength();
		}
		return null;
	}

	@Override
	public Integer getMaxLength() {
		if (header instanceof StringTypeDeclaration) {
			return ((StringTypeDeclaration) header).maxLength();
		}
		return null;
	}

	@Override
	public BigDecimal getMinimum() {
		if (header instanceof NumberTypeDeclaration) {
			Double minimum = ((NumberTypeDeclaration) header).minimum();
			if (minimum != null) {
				return BigDecimal.valueOf(minimum);
			}
		}
		return null;
	}

	@Override
	public BigDecimal getMaximum() {
		if (header instanceof NumberTypeDeclaration) {
			Double maximum = ((NumberTypeDeclaration) header).maximum();
			if (maximum != null) {
				return BigDecimal.valueOf(maximum);
			}
		}
		return null;
	}

	@Override
	public String getName() {
		return this.header.name();
	}

	@Override
	public List<AnnotationRef> getAnnotations() {
		return this.header.annotations();
	}

	@Override
	public String getRawType() {
		return this.header.type();
	}
}
