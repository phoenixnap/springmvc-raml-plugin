package com.phoenixnap.oss.ramlplugin.raml2code.raml;

import java.util.List;

public interface RamlSecurityScheme {

	String getName();

	String getType();

	List<String> getAuthorizationGrants();
}
