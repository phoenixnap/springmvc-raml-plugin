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

import org.springframework.cloud.netflix.feign.FeignClient;

import com.phoenixnap.oss.ramlplugin.raml2code.rules.spring.SpringFeignClientInterfaceDecoratorRule;

/**
 * A code generation Rule that provides a standalone {@link FeignClient}. The
 * goal is to generate code that does not have to be manually extended by the
 * user. <br>
 *
 * Now all the user has to do is to autowire this interface. This way he can
 * invoke remote endpoint.
 *
 * @author Aleksandar Stojsavljevic
 * @since 0.8.6
 */
public class SpringFeignClientInterfaceRule extends SpringFeignClientInterfaceDecoratorRule {

}
