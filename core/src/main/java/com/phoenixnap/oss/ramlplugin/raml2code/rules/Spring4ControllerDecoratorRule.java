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
package com.phoenixnap.oss.ramlplugin.raml2code.rules;

import com.phoenixnap.oss.ramlplugin.raml2code.data.ApiActionMetadata;
import com.phoenixnap.oss.ramlplugin.raml2code.data.ApiResourceMetadata;
import com.phoenixnap.oss.ramlplugin.raml2code.rules.spring.SpringControllerDecoratorRule;
import com.phoenixnap.oss.ramlplugin.raml2code.rules.spring.SpringRestControllerAnnotationRule;
import com.sun.codemodel.JAnnotationUse;
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
public class Spring4ControllerDecoratorRule extends SpringControllerDecoratorRule {

	@Override
	protected Rule<JDefinedClass, JAnnotationUse, ApiResourceMetadata> getControllerAnnotationRule() {
		return new SpringRestControllerAnnotationRule();
	}

	@Override
	protected Rule<JMethod, JAnnotationUse, ApiActionMetadata> getResponseBodyAnnotationRule() {
		return null; // ResponseBody not needed for RestController
	}
}
