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

import org.apache.commons.lang.StringUtils;

import com.phoenixnap.oss.ramlplugin.raml2code.data.ApiActionMetadata;
import com.phoenixnap.oss.ramlplugin.raml2code.data.ApiResourceMetadata;
import com.phoenixnap.oss.ramlplugin.raml2code.plugin.Config;
import com.phoenixnap.oss.ramlplugin.raml2code.rules.GenericJavaClassRule;
import com.phoenixnap.oss.ramlplugin.raml2code.rules.Rule;
import com.phoenixnap.oss.ramlplugin.raml2code.rules.basic.ClassCommentRule;
import com.phoenixnap.oss.ramlplugin.raml2code.rules.basic.ControllerClassDeclarationRule;
import com.phoenixnap.oss.ramlplugin.raml2code.rules.basic.ControllerInterfaceDeclarationRule;
import com.phoenixnap.oss.ramlplugin.raml2code.rules.basic.ControllerMethodSignatureRule;
import com.phoenixnap.oss.ramlplugin.raml2code.rules.basic.DelegatingMethodBodyRule;
import com.phoenixnap.oss.ramlplugin.raml2code.rules.basic.ImplementsControllerInterfaceRule;
import com.phoenixnap.oss.ramlplugin.raml2code.rules.basic.MethodCommentRule;
import com.phoenixnap.oss.ramlplugin.raml2code.rules.basic.MethodParamsRule;
import com.phoenixnap.oss.ramlplugin.raml2code.rules.basic.PackageRule;
import com.sun.codemodel.JAnnotationUse;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JMethod;

/**
 * A code generation Rule that provides a Spring4 Controller based on a
 * decorator pattern. The goal is to generate code that does not have to be
 * manually extended by the user. A raml endpoint called /people for example
 * implies two generated artefacts:
 *
 * // 1. Controller Interface interface PeopleController { ResponseEntity
 * getPeople(); }
 *
 * // 2. A Decorator that implements the Controller Interface // and delegates
 * to another instance of a class implementing the very same controller
 * interface. {@literal @}RestController {@literal @}RequestMapping("/people")
 * class PeopleControllerDecorator implements PeopleController {
 *
 * {@literal @}Autowired PeopleController peopleControllerDelegate;
 *
 * {@literal @}RequestMapping(value="", method=RequestMethod.GET) public
 * ResponseEntity getPeople() { return
 * this.peopleControllerDelegate.getPeople(); } }
 *
 * Now all the user has to do is to implement a Spring-Bean called
 * "PeopleControllerDelegate". This way he can implement the endpoint without
 * altering the generated code.
 *
 * @author armin.weisser
 * @author kurtpa
 * @since 0.4.1
 */
public abstract class SpringControllerDecoratorRule extends SpringConfigurableRule {

	@Override
	public final JDefinedClass apply(ApiResourceMetadata metadata, JCodeModel generatableType) {

		JDefinedClass generatedInterface = new GenericJavaClassRule().setPackageRule(new PackageRule())
				.setClassCommentRule(new ClassCommentRule()).setClassRule(new ControllerInterfaceDeclarationRule())
				.setMethodCommentRule(new MethodCommentRule())
				.setMethodSignatureRule(new ControllerMethodSignatureRule(getReturnTypeRule(false),
						new MethodParamsRule(isAddParameterJavadoc(), isAllowArrayParameters(), !Config.isInjectHttpHeadersParameter())))
				.apply(metadata, generatableType);

		String delegateFieldName = StringUtils.uncapitalize(generatedInterface.name() + "Delegate");

		GenericJavaClassRule delegateGenerator = new GenericJavaClassRule().setPackageRule(new PackageRule())
				.setClassCommentRule(new ClassCommentRule()).addClassAnnotationRule(getControllerAnnotationRule())
				.addClassAnnotationRule(new SpringRequestMappingClassAnnotationRule())
				.addClassAnnotationRule(new SpringValidatedClassAnnotationRule())
				.setClassRule(new ControllerClassDeclarationRule("Decorator"))
				.setImplementsExtendsRule(new ImplementsControllerInterfaceRule(generatedInterface))
				.addFieldDeclarationRule(new SpringDelegateFieldDeclerationRule(delegateFieldName))
				.setMethodCommentRule(new MethodCommentRule()).addMethodAnnotationRule(new SpringRequestMappingMethodAnnotationRule())
				.addMethodAnnotationRule(getResponseBodyAnnotationRule())
				.setMethodSignatureRule(new ControllerMethodSignatureRule(getReturnTypeRule(false),
						new SpringMethodParamsRule(isAddParameterJavadoc(), isAllowArrayParameters(),
								!Config.isInjectHttpHeadersParameter())))
				.setMethodBodyRule(new DelegatingMethodBodyRule(delegateFieldName));

		return delegateGenerator.apply(metadata, generatableType);
	}

	protected abstract Rule<JDefinedClass, JAnnotationUse, ApiResourceMetadata> getControllerAnnotationRule();

	protected abstract Rule<JMethod, JAnnotationUse, ApiActionMetadata> getResponseBodyAnnotationRule();
}
