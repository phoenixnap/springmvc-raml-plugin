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
package com.phoenixnap.oss.ramlplugin.raml2code.rules.basic;

import com.phoenixnap.oss.ramlplugin.raml2code.data.ApiResourceMetadata;
import com.phoenixnap.oss.ramlplugin.raml2code.rules.Rule;
import com.sun.codemodel.JClassAlreadyExistsException;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JPackage;

/**
 * Generates an interface declaration based on the controller name in
 * ApiControllerMetadata. <br>
 *
 * @author Aleksandar Stojsavljevic
 * 
 * @since 0.8.6
 */
public class SpringFeignClientInterfaceDeclarationRule implements Rule<JPackage, JDefinedClass, ApiResourceMetadata> {

	@Override
	public JDefinedClass apply(ApiResourceMetadata controllerMetadata, JPackage generatableType) {

		String controllerClassName = controllerMetadata.getResourceName().concat("Client");
		JDefinedClass definedClass;
		try {
			definedClass = generatableType._interface(controllerClassName);
		} catch (JClassAlreadyExistsException e1) {
			definedClass = generatableType._getClass(controllerClassName);
		}
		return definedClass;
	}

}