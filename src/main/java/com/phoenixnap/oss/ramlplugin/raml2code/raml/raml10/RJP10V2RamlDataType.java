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

import org.raml.v2.api.model.v10.datamodel.ObjectTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;

import com.phoenixnap.oss.ramlplugin.raml2code.raml.RamlDataType;

/**
 * Raml 1.0 implementation for RAML Data type support. Note this may become very
 * similar to the Mimetype under the hood, however logically it is quite
 * different and this distinction may simplify the parser logic. If not we can
 * refactor this away
 * 
 * @author Kurt Paris
 * @since 0.10.0
 *
 */
public class RJP10V2RamlDataType implements RamlDataType {

	private final TypeDeclaration dataType;

	public RJP10V2RamlDataType(TypeDeclaration dataType) {
		this.dataType = dataType;
	}

	@Override
	public TypeDeclaration getType() {
		return this.dataType;
	}

	@Override
	public String getDiscriminatorValue() {
		if (this.dataType instanceof ObjectTypeDeclaration) {
			return ((ObjectTypeDeclaration) this.dataType).discriminatorValue();
		}
		return null;
	}
}
