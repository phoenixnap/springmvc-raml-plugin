/*
 * Copyright 2002-2016 the original author or authors.
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
package com.phoenixnap.oss.ramlapisync.generation.rules.spring;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.phoenixnap.oss.ramlapisync.data.ApiControllerMetadata;
import com.phoenixnap.oss.ramlapisync.generation.rules.GenericJavaClassRule;
import com.phoenixnap.oss.ramlapisync.generation.rules.Rule;
import com.phoenixnap.oss.ramlapisync.generation.rules.basic.ClassAnnotationRule;
import com.phoenixnap.oss.ramlapisync.generation.rules.basic.ClassCommentRule;
import com.phoenixnap.oss.ramlapisync.generation.rules.basic.ClassFieldDeclarationRule;
import com.phoenixnap.oss.ramlapisync.generation.rules.basic.ClientInterfaceDeclarationRule;
import com.phoenixnap.oss.ramlapisync.generation.rules.basic.ControllerMethodSignatureRule;
import com.phoenixnap.oss.ramlapisync.generation.rules.basic.ImplementsControllerInterfaceRule;
import com.phoenixnap.oss.ramlapisync.generation.rules.basic.MethodCommentRule;
import com.phoenixnap.oss.ramlapisync.generation.rules.basic.MethodParamsRule;
import com.phoenixnap.oss.ramlapisync.generation.rules.basic.PackageRule;
import com.phoenixnap.oss.ramlapisync.generation.rules.basic.ResourceClassDeclarationRule;
import com.phoenixnap.oss.ramlapisync.generation.rules.basic.RestClientMethodBodyRule;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;

/**
 * A code generation Rule that provides a Spring4 Controller based on a decorator pattern.
 * The goal is to generate code that does not have to be manually extended by the user.
 * A raml endpoint called /people for example implies two generated artefacts:
 *
 * // 1. Controller Interface
 * interface PeopleClient {
 *     ResponseEntity getPeople();
 * }
 *
 * // 2. An implementation of the Controller Interface using Spring RestTemplate
 * {@literal @}Component
 * class PeopleClientImpl implements PeopleClient {
 *
 *     {@literal @}Autowired
 *     PeopleController peopleControllerDelegate;
 *
 *     {@literal @}RequestMapping(value="", method=RequestMethod.GET)
 *     public ResponseEntity getPeople() {
 *         return this.peopleControllerDelegate.getPeople();
 *     }
 * }
 *
 * Now all the user has to do is to implement a Spring-Bean called "PeopleControllerDelegate".
 * This way he can implement the endpoint without altering the generated code.
 *
 * @author kurt paris
 * @author kristian galea
 * @since 0.5.0
 */
public class SpringRestTemplateClientRule implements Rule<JCodeModel, JDefinedClass, ApiControllerMetadata> {

    @Override
    public final JDefinedClass apply(ApiControllerMetadata metadata, JCodeModel generatableType) {

        JDefinedClass generatedInterface = new GenericJavaClassRule()
                .setPackageRule(new PackageRule())
                .setClassCommentRule(new ClassCommentRule())
                .setClassRule(new ClientInterfaceDeclarationRule())  //MODIFIED
                .setMethodCommentRule(new MethodCommentRule())
                .setMethodSignatureRule(new ControllerMethodSignatureRule(
                        new SpringResponseEntityRule(),
                        new MethodParamsRule()))
                .apply(metadata, generatableType);

        String restTemplateName = "restTemplate";
        
        GenericJavaClassRule delegateGenerator = new GenericJavaClassRule()
                .setPackageRule(new PackageRule())
                .setClassCommentRule(new ClassCommentRule())
                .addClassAnnotationRule(new ClassAnnotationRule(Component.class))                
                .setClassRule(new ResourceClassDeclarationRule(ClientInterfaceDeclarationRule.CLIENT_SUFFIX + "Impl"))   //MODIFIED
                .setImplementsExtendsRule(new ImplementsControllerInterfaceRule(generatedInterface))
                .addFieldDeclarationRule(new ClassFieldDeclarationRule(restTemplateName, RestTemplate.class)) //Modified
                .setMethodCommentRule(new MethodCommentRule())                
                .setMethodSignatureRule(new ControllerMethodSignatureRule(
                        new SpringResponseEntityRule(),
                        new MethodParamsRule()))
                .setMethodBodyRule(new RestClientMethodBodyRule("http://localhost",restTemplateName));

        return delegateGenerator.apply(metadata, generatableType);
    }
    
    
}
