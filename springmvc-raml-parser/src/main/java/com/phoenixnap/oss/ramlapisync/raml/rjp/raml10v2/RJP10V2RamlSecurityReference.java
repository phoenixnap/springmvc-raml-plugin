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



import org.raml.v2.api.model.v10.security.SecuritySchemeRef;

import com.phoenixnap.oss.ramlapisync.raml.RamlSecurityReference;

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

	SecuritySchemeRef getSecuritySchemeRef() {
		return this.securitySchemeRef;
	}
}
