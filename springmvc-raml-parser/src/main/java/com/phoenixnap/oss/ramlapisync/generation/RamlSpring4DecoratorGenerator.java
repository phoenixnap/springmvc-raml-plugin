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
package com.phoenixnap.oss.ramlapisync.generation;

import com.phoenixnap.oss.ramlapisync.data.ApiControllerMetadata;
import com.phoenixnap.oss.ramlapisync.generation.serialize.ApiControllerMetadataSerializer;
import com.phoenixnap.oss.ramlapisync.generation.serialize.Spring4ControllerInterfaceSerializer;
import com.phoenixnap.oss.ramlapisync.generation.serialize.Spring4DecoratorSerializer;
import com.phoenixnap.oss.ramlapisync.parser.ResourceParser;

import java.util.Arrays;
import java.util.List;

/**
 * Extends the standard RamlGenerator by providing a Spring4 Controller based on a decorator pattern.
 * The goal is to generate code that does not have to be manually extended by the user.
 * A raml endpoint called /people for example implies two generated artefacts:
 *
 * // 1. Controller Interface
 * interface PeopleController {
 *     ResponseEntity getPeople();
 * }
 *
 * // 2. A Decorator that implements the Controller Interface
 * // and delegates to another instance of a class implementing the very same controller interface.
 * @RestController
 * @RequestMapping("/people")
 * class PeopleControllerDecorator implements PeopleController {
 *
 *     @Autowired
 *     PeopleController peopleControllerDelegate;
 *
 *     @RequestMapping(value="", method=RequestMethod.GET)
 *     public ResponseEntity getPeople() {
 *         return this.peopleControllerDelegate.getPeople();
 *     }
 * }
 *
 * Now all the user has to do is to implement a Spring-Bean called "PeopleControllerDelegate".
 * This way he can implement the endpoint without altering the generated code.
 *
 * @author armin.weisser
 * @since 0.3.1
 */
public class RamlSpring4DecoratorGenerator extends RamlGenerator {

    public RamlSpring4DecoratorGenerator() {
        super();
    }

    public RamlSpring4DecoratorGenerator(ResourceParser scanner) {
        super(scanner);
    }

    @Override
    public List<ApiControllerMetadataSerializer> generateClassForRaml(ApiControllerMetadata controller, String header) {
        return Arrays.asList(
                new Spring4ControllerInterfaceSerializer(controller, header),
                new Spring4DecoratorSerializer(controller, header)
        );
    }
}
