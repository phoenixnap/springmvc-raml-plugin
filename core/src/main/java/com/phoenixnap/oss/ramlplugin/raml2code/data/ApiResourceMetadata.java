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
package com.phoenixnap.oss.ramlplugin.raml2code.data;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

import com.phoenixnap.oss.ramlplugin.raml2code.helpers.NamingHelper;
import com.phoenixnap.oss.ramlplugin.raml2code.plugin.Config;
import com.phoenixnap.oss.ramlplugin.raml2code.raml.RamlAction;
import com.phoenixnap.oss.ramlplugin.raml2code.raml.RamlActionType;
import com.phoenixnap.oss.ramlplugin.raml2code.raml.RamlResource;
import com.phoenixnap.oss.ramlplugin.raml2code.raml.RamlRoot;
import com.sun.codemodel.JCodeModel;

/**
 * 
 * Class containing the data required to successfully generate code for an api
 * rest controller within spring mvc
 * 
 * @author Kurt Paris
 * @since 0.2.1
 *
 */
public class ApiResourceMetadata {

	private String controllerUrl;
	private transient RamlResource resource;
	private RamlRoot document;
	private boolean singularizeName = true;
	private JCodeModel bodyCodeModel;

	Set<ApiActionMetadata> apiCalls = new LinkedHashSet<>();

	public ApiResourceMetadata(JCodeModel bodyCodeModel, String controllerUrl, RamlResource resource, RamlRoot document) {
		super();
		this.controllerUrl = controllerUrl;
		this.resource = resource;
		this.document = document;
		this.bodyCodeModel = bodyCodeModel;
	}

	public void addApiCall(RamlResource resource, RamlActionType actionType, RamlAction action, String responseContentType) {
		apiCalls.add(new ApiActionMetadata(this, resource, actionType, action, responseContentType));
	}

	public Set<ApiActionMetadata> getApiCalls() {
		return Collections.unmodifiableSet(apiCalls);
	}

	public String getName() {
		if (Config.getResourceDepthInClassNames() != 1 || Config.getResourceTopLevelInClassNames() != 0
				|| Config.isReverseOrderInClassNames()) {
			return NamingHelper.getAllResourcesNames(controllerUrl, singularizeName);
		} else {
			return NamingHelper.getResourceName(resource, singularizeName);
		}
	}

	public RamlResource getResource() {
		return resource;
	}

	public String getResourceName() {
		return NamingHelper.getResourceName(resource, singularizeName);
	}

	public String getResourceUri() {
		return resource.getUri();
	}

	public String getControllerUrl() {
		return controllerUrl;
	}

	public String toString() {
		return "Controller " + getName() + "[" + getControllerUrl() + "]";

	}

	public Set<ApiBodyMetadata> getDependencies() {
		Set<ApiBodyMetadata> dependencies = new LinkedHashSet<>();
		for (ApiActionMetadata method : apiCalls) {
			if (method.getRequestBody() != null) {
				dependencies.add(method.getRequestBody());
			}
			dependencies.addAll(method.getResponseBody().values());
		}
		return dependencies;
	}

	public Set<ApiParameterMetadata> getParameters() {
		Set<ApiParameterMetadata> parameters = new LinkedHashSet<>();
		for (ApiActionMetadata method : apiCalls) {
			parameters.addAll(method.getRequestHeaders());
			parameters.addAll(method.getRequestParameters());
		}
		return parameters;
	}

	public String getDescription() {
		return resource.getDescription();
	}

	public RamlRoot getDocument() {
		return document;
	}

	public void setSingularizeName(boolean singularizeName) {
		this.singularizeName = singularizeName;
	}

	public JCodeModel getBodyCodeModel() {
		return this.bodyCodeModel;
	}
}
