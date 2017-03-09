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
package com.phoenixnap.oss.ramlapisync.generation.rules.spring;

import org.springframework.cloud.netflix.feign.FeignClient;

import com.phoenixnap.oss.ramlapisync.data.ApiResourceMetadata;
import com.phoenixnap.oss.ramlapisync.generation.rules.GenericJavaClassRule;
import com.phoenixnap.oss.ramlapisync.generation.rules.Rule;
import com.phoenixnap.oss.ramlapisync.generation.rules.basic.ClassCommentRule;
import com.phoenixnap.oss.ramlapisync.generation.rules.basic.ControllerMethodSignatureRule;
import com.phoenixnap.oss.ramlapisync.generation.rules.basic.MethodCommentRule;
import com.phoenixnap.oss.ramlapisync.generation.rules.basic.PackageRule;
import com.phoenixnap.oss.ramlapisync.generation.rules.basic.SpringFeignClientInterfaceDeclarationRule;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;

/**
 * A code generation Rule that provides a standalone {@link FeignClient}.
 * The goal is to generate code that does not have to be manually extended by the user.
 * <br>
 * A raml endpoint called /people exposed on http://somehost:8080 for example would lead to the 
 * following interface:
 *
 * <pre class="code">
 * {@literal @}FeignClient(url = "http://somehost:8080/people", name = "feignClientPeople")
 * interface PeopleFeignClient {
 * 
 *     {@literal @}RequestMapping(value="", method=RequestMethod.GET)
 *     ResponseEntity{@literal <}com.gen.test.model.People{@literal >} getPeople();
 * }
 * </pre>
 *
 * Now all the user has to do is to autowire this interface.
 * This way he can invoke remote endpoint.
 *
 * @author Aleksandar Stojsavljevic
 * @since 0.8.6
 */
public class SpringFeignClientInterfaceDecoratorRule implements Rule<JCodeModel, JDefinedClass, ApiResourceMetadata> {
	
	@Override
    public final JDefinedClass apply(ApiResourceMetadata metadata, JCodeModel generatableType) {

        GenericJavaClassRule generator = new GenericJavaClassRule()
                .setPackageRule(new PackageRule())
                .setClassCommentRule(new ClassCommentRule())
                .addClassAnnotationRule(new SpringFeignClientClassAnnotationRule())
                .setClassRule(new SpringFeignClientInterfaceDeclarationRule())
                .setMethodCommentRule(new MethodCommentRule())
                .addMethodAnnotationRule(new SpringRequestMappingMethodAnnotationRule())
                .setMethodSignatureRule(new ControllerMethodSignatureRule(
                        new SpringFeignClientResponseTypeRule(),
                        new SpringMethodParamsRule())
                );
        return generator.apply(metadata, generatableType);
    }
}
