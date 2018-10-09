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
import java.util.Collections;
import java.util.List;

import org.raml.v2.api.model.v10.datamodel.TypeInstance;
import org.raml.v2.api.model.v10.datamodel.TypeInstanceProperty;
import org.raml.v2.api.model.v10.security.SecuritySchemeRef;

import com.phoenixnap.oss.ramlplugin.raml2code.raml.RamlSecurityReference;

/**
 * Implementation based on the Raml 1.0 Parser
 * 
 * @author Aleksandar Stojsavljevic
 * @since 0.10.0
 */
public class RJP10V2RamlSecurityReference implements RamlSecurityReference {

	private final SecuritySchemeRef securitySchemeRef;

	public RJP10V2RamlSecurityReference(SecuritySchemeRef securityReferenceRef) {
		this.securitySchemeRef = securityReferenceRef;
	}

	@Override
	public String getName() {
		if (this.securitySchemeRef == null) {
			return null;
		}
		return this.securitySchemeRef.name();
	}

	public SecuritySchemeRef getSecuritySchemeRef() {
		return this.securitySchemeRef;
	}

	@Override
	public List<String> getAuthorizationGrants() {
		if (!"oauth_2_0".equalsIgnoreCase(getName())) {
			return Collections.emptyList();
		}

		TypeInstance structuredValue = this.securitySchemeRef.structuredValue();
		if (structuredValue == null) {
			return Collections.emptyList();
		}

		List<String> authorizationGrants = new ArrayList<>();
		List<TypeInstanceProperty> properties = structuredValue.properties();
		for (TypeInstanceProperty property : properties) {
			if ("authorizationGrants".equalsIgnoreCase(property.name())) {
				authorizationGrants.add(property.value().value().toString());
			}
		}
		return authorizationGrants;
	}
}
