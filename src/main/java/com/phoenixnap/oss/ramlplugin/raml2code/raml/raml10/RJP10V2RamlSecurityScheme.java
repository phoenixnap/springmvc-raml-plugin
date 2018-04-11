package com.phoenixnap.oss.ramlplugin.raml2code.raml.raml10;

import java.util.List;

import org.raml.v2.api.model.v10.security.SecurityScheme;

import com.phoenixnap.oss.ramlplugin.raml2code.raml.RamlSecurityScheme;

public class RJP10V2RamlSecurityScheme implements RamlSecurityScheme {

	private final SecurityScheme securityScheme;

	public RJP10V2RamlSecurityScheme(SecurityScheme securityScheme) {
		this.securityScheme = securityScheme;
	}

	@Override
	public String getName() {
		return this.securityScheme.name();
	}

	@Override
	public String getType() {
		return this.securityScheme.type();
	}

	@Override
	public List<String> getAuthorizationGrants() {
		return this.securityScheme.settings().authorizationGrants();
	}

}
