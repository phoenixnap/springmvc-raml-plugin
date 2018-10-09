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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.raml.v2.api.model.v10.datamodel.ObjectTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;

import com.phoenixnap.oss.ramlplugin.raml2code.data.RamlFormParameter;
import com.phoenixnap.oss.ramlplugin.raml2code.raml.RamlDataType;
import com.phoenixnap.oss.ramlplugin.raml2code.raml.RamlMimeType;

/**
 * Implementation based on the Raml 1.0 Parser
 * 
 * @author Aleksandar Stojsavljevic
 * @since 0.10.0
 */
public class RJP10V2RamlMimeType implements RamlMimeType {

	private static RJP10V2RamlModelFactory ramlModelFactory = new RJP10V2RamlModelFactory();

	private final TypeDeclaration mimeType;

	private Map<String, List<RamlFormParameter>> formParameters;

	public RJP10V2RamlMimeType(TypeDeclaration mimeType) {
		this.mimeType = mimeType;
	}

	/**
	 * Expose internal representation only package private
	 * 
	 * @return the internal model
	 */
	TypeDeclaration getMimeType() {
		return mimeType;
	}

	@Override
	public Map<String, List<RamlFormParameter>> getFormParameters() {
		if (formParameters == null) {
			this.formParameters = ((ObjectTypeDeclaration) mimeType).properties().stream()
					.collect(Collectors.toMap(this::getName, this::getList));
		}
		return this.formParameters;
	}

	private String getName(TypeDeclaration typeDeclaration) {
		return typeDeclaration.name();
	}

	private List<RamlFormParameter> getList(TypeDeclaration typeDeclaration) {
		List<RamlFormParameter> list = new ArrayList<>();
		list.add(ramlModelFactory.createRamlFormParameter(typeDeclaration));
		return list;
	}

	@Override
	public String getSchema() {
		return mimeType.type();
	}

	@Override
	public RamlDataType getType() {
		return new RJP10V2RamlDataType(mimeType);
	}
}
