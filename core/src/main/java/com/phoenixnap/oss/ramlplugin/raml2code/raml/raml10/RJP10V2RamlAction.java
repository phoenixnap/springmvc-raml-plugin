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

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.raml.v2.api.model.v10.declarations.AnnotationRef;
import org.raml.v2.api.model.v10.methods.Method;

import com.phoenixnap.oss.ramlplugin.raml2code.helpers.RamlTypeHelper;
import com.phoenixnap.oss.ramlplugin.raml2code.raml.RamlAction;
import com.phoenixnap.oss.ramlplugin.raml2code.raml.RamlActionType;
import com.phoenixnap.oss.ramlplugin.raml2code.raml.RamlHeader;
import com.phoenixnap.oss.ramlplugin.raml2code.raml.RamlMimeType;
import com.phoenixnap.oss.ramlplugin.raml2code.raml.RamlQueryParameter;
import com.phoenixnap.oss.ramlplugin.raml2code.raml.RamlResource;
import com.phoenixnap.oss.ramlplugin.raml2code.raml.RamlResponse;
import com.phoenixnap.oss.ramlplugin.raml2code.raml.RamlSecurityReference;

/**
 * Implementation based on the Raml 1.0 Parser
 * 
 * @author Aleksandar Stojsavljevic
 * @since 0.10.0
 */
public class RJP10V2RamlAction implements RamlAction {

	private static RJP10V2RamlModelFactory ramlModelFactory = new RJP10V2RamlModelFactory();

	private final Method method;

	private Map<String, RamlResponse> responses = new LinkedHashMap<>();

	private Map<String, RamlMimeType> body = new LinkedHashMap<>();

	private Map<String, RamlHeader> headers = new LinkedHashMap<>();

	private Map<String, RamlQueryParameter> queryParameters = new LinkedHashMap<>();

	public RJP10V2RamlAction(Method method) {
		this.method = method;
	}

	/**
	 * Expose internal representation only package private
	 * 
	 * @return the internal model
	 */
	Method getMethod() {
		return method;
	}

	@Override
	public RamlActionType getType() {
		return RamlActionType.valueOf(this.method.method().toUpperCase());
	}

	@Override
	public Map<String, RamlQueryParameter> getQueryParameters() {
		return ramlModelFactory.transformToUnmodifiableMap(method.queryParameters(), queryParameters,
				ramlModelFactory::createRamlQueryParameter, RamlTypeHelper::getName);
	}

	@Override
	public Map<String, RamlResponse> getResponses() {
		return ramlModelFactory.transformToUnmodifiableMap(method.responses(), responses, ramlModelFactory::createRamlResponse,
				r -> r.code().value());
	}

	@Override
	public RamlResource getResource() {
		return ramlModelFactory.createRamlResource(method.resource());
	}

	@Override
	public Map<String, RamlHeader> getHeaders() {
		return ramlModelFactory.transformToUnmodifiableMap(method.headers(), headers, ramlModelFactory::createRamlHeader,
				RamlTypeHelper::getName);
	}

	@Override
	public Map<String, RamlMimeType> getBody() {
		return ramlModelFactory.transformToUnmodifiableMap(method.body(), body, ramlModelFactory::createRamlMimeType,
				RamlTypeHelper::getName);
	}

	@Override
	public boolean hasBody() {
		return !method.body().isEmpty();
	}

	@Override
	public String getDescription() {
		return (method.description() == null) ? null : method.description().value();
	}

	@Override
	public String getDisplayName() {
		if (method.displayName() == null) {
			return null;
		}
		String value = method.displayName().value();
		try {
			// we need to check if the displayname is the action type and remove
			// it since this is an inconsistency between 08 and 10.
			// It's also a really bad idea to call your post "post" since it
			// adds 0 semantic value.
			RamlActionType.valueOf(value.toUpperCase());
			return null;
		} catch (IllegalArgumentException ex) {
			return value;
		}
	}

	@Override
	public List<RamlSecurityReference> getSecuredBy() {
		return ramlModelFactory.createRamlSecurityReferences(method.securedBy());
	}

	@Override
	public List<AnnotationRef> getAnnotations() {
		return this.method.annotations();
	}
}
