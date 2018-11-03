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
package com.phoenixnap.oss.ramlplugin.raml2code.rules.spring;

import com.phoenixnap.oss.ramlplugin.raml2code.data.ApiActionMetadata;
import com.phoenixnap.oss.ramlplugin.raml2code.data.ApiResourceMetadata;
import com.phoenixnap.oss.ramlplugin.raml2code.plugin.Config;
import com.phoenixnap.oss.ramlplugin.raml2code.rules.GenericJavaClassRule;
import com.phoenixnap.oss.ramlplugin.raml2code.rules.Rule;
import com.phoenixnap.oss.ramlplugin.raml2code.rules.basic.ClassCommentRule;
import com.phoenixnap.oss.ramlplugin.raml2code.rules.basic.ControllerClassDeclarationRule;
import com.phoenixnap.oss.ramlplugin.raml2code.rules.basic.ControllerMethodSignatureRule;
import com.phoenixnap.oss.ramlplugin.raml2code.rules.basic.ImplementMeMethodBodyRule;
import com.phoenixnap.oss.ramlplugin.raml2code.rules.basic.MethodCommentRule;
import com.phoenixnap.oss.ramlplugin.raml2code.rules.basic.PackageRule;
import com.sun.codemodel.JAnnotationUse;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JMethod;

/**
 * A code generation Rule that provides a simple Controller stub class with
 * Spring4 annotations and empty method bodies. This is the default code
 * generation rule formally executed by the
 * RamlGenerator.generateClassForRaml(...) method. A raml endpoint called
 * /people for example would generate an artefact like this:
 *
 * {@literal @}RestController {@literal @}RequestMapping("/people") class
 * PeopleController {
 *
 * {@literal @}RequestMapping(value="", method=RequestMethod.GET) public
 * ResponseEntity getPeople() { return null; // TODO Autogenerated Method Stub.
 * Implement me please. } }
 *
 * After code generation the user has to implement the method bodies. So this
 * solution is mainly usefull for one time code generation.
 *
 * @author armin.weisser
 * @author kurtpa
 * @since 0.4.1
 */
public abstract class SpringControllerStubRule extends SpringConfigurableRule {

	@Override
	public final JDefinedClass apply(ApiResourceMetadata metadata, JCodeModel generatableType) {

		GenericJavaClassRule generator = new GenericJavaClassRule().setPackageRule(new PackageRule())
				.setClassCommentRule(new ClassCommentRule()).addClassAnnotationRule(getControllerAnnotationRule())
				.addClassAnnotationRule(new SpringRequestMappingClassAnnotationRule())
				.addClassAnnotationRule(new SpringValidatedClassAnnotationRule()).setClassRule(new ControllerClassDeclarationRule())
				.setMethodCommentRule(new MethodCommentRule()).addMethodAnnotationRule(new SpringRequestMappingMethodAnnotationRule())
				.addMethodAnnotationRule(getResponseBodyAnnotationRule())
				.setMethodSignatureRule(new ControllerMethodSignatureRule(getReturnTypeRule(false), new SpringMethodParamsRule(
						isAddParameterJavadoc(), isAllowArrayParameters(), !Config.isInjectHttpHeadersParameter())))
				.setMethodBodyRule(new ImplementMeMethodBodyRule());

		return generator.apply(metadata, generatableType);
	}

	protected abstract Rule<JMethod, JAnnotationUse, ApiActionMetadata> getResponseBodyAnnotationRule();

	protected abstract Rule<JDefinedClass, JAnnotationUse, ApiResourceMetadata> getControllerAnnotationRule();
}
