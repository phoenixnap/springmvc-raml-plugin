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
import com.phoenixnap.oss.ramlplugin.raml2code.rules.GenericJavaClassRule;
import com.phoenixnap.oss.ramlplugin.raml2code.rules.Rule;
import com.phoenixnap.oss.ramlplugin.raml2code.rules.basic.ClassCommentRule;
import com.phoenixnap.oss.ramlplugin.raml2code.rules.basic.ControllerInterfaceDeclarationRule;
import com.phoenixnap.oss.ramlplugin.raml2code.rules.basic.ControllerMethodSignatureRule;
import com.phoenixnap.oss.ramlplugin.raml2code.rules.basic.MethodCommentRule;
import com.phoenixnap.oss.ramlplugin.raml2code.rules.basic.PackageRule;
import com.sun.codemodel.JAnnotationUse;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JMethod;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.util.CollectionUtils;

import java.util.Map;

/**
 * A code generation Rule that provides a standalone Controller interface with
 * Spring4 annotations. The goal is to generate code that does not have to be
 * manually extended by the user. A raml endpoint called /people for example
 * would lead to the following interface only:
 *
 * // 1. Controller Interface {@literal @}RestController
 * {@literal @}RequestMapping("/people") interface PeopleController {
 * {@literal @}RequestMapping(value="", method=RequestMethod.GET) ResponseEntity
 * getPeople(); }
 *
 * Now all the user has to do is to implement a this interface. This way he can
 * implement the endpoint without altering the generated code.
 *
 * @author armin.weisser
 * @author kurt paris
 * @since 0.4.1
 */
public abstract class SpringControllerInterfaceRule extends SpringConfigurableRule {

	@Override
	public final JDefinedClass apply(ApiResourceMetadata metadata, JCodeModel generatableType) {

		GenericJavaClassRule generator = new GenericJavaClassRule().setPackageRule(new PackageRule())
				.setClassCommentRule(new ClassCommentRule()).addClassAnnotationRule(getControllerAnnotationRule())
				.addClassAnnotationRule(new SpringValidatedClassAnnotationRule())
				.addClassAnnotationRule(new SpringRequestMappingClassAnnotationRule())
				.setClassRule(new ControllerInterfaceDeclarationRule()).setMethodCommentRule(new MethodCommentRule())
				.addMethodAnnotationRule(isUseShortcutMethodMappings() ? new SpringShortcutMappingMethodAnnotationRule()
						: new SpringRequestMappingMethodAnnotationRule())
				.addMethodAnnotationRule(getResponseBodyAnnotationRule())
				.setMethodSignatureRule(new ControllerMethodSignatureRule(
						isCallableResponse() ? new SpringCallableResponseEntityRule()
								: isSimpleReturnTypes() ? new SpringObjectReturnTypeRule() : new SpringResponseEntityRule(),
						new SpringMethodParamsRule(isAddParameterJavadoc(), isAllowArrayParameters())));
		return generator.apply(metadata, generatableType);
	}

	@Override
	public void applyConfiguration(Map<String, String> configuration) {
		super.applyConfiguration(configuration);
		if (!CollectionUtils.isEmpty(configuration)) {
			if (configuration.containsKey(SIMPLE_RETURN_TYPES)) {
				setSimpleReturnTypes(BooleanUtils.toBoolean(configuration.get(SIMPLE_RETURN_TYPES)));
			}
		}
	}

	protected abstract Rule<JDefinedClass, JAnnotationUse, ApiResourceMetadata> getControllerAnnotationRule();

	protected abstract Rule<JMethod, JAnnotationUse, ApiActionMetadata> getResponseBodyAnnotationRule();

}
