package com.phoenixnap.oss.ramlapisync.generation;

import com.phoenixnap.oss.ramlapisync.data.ApiControllerMetadata;
import com.phoenixnap.oss.ramlapisync.generation.serialize.ApiControllerMetadataSerializer;
import com.phoenixnap.oss.ramlapisync.generation.serialize.Spring4ControllerInterfaceWithAnnotationsSerializer;
import com.phoenixnap.oss.ramlapisync.parser.ResourceParser;

import java.util.Arrays;
import java.util.List;

/**
 * Extends the standard RamlGenerator by providing a Spring4 Controller interface.
 * The goal is to generate code that does not have to be manually extended by the user.
 * A raml endpoint called /people for example would lead to the following interface only:
 *
 * // 1. Controller Interface
 * @RestController
 * @RequestMapping("/people")
 * interface PeopleController {
 *     @RequestMapping(value="", method=RequestMethod.GET)
 *     ResponseEntity getPeople();
 * }
 *
 * Now all the user has to do is to implement a this interface.
 * This way he can implement the endpoint without altering the generated code.
 *
 * @author armin.weisser
 * @since 0.3.1
 */
public class RamlSpring4InterfaceOnlyGenerator extends RamlGenerator {

    public RamlSpring4InterfaceOnlyGenerator() {
        super();
    }

    public RamlSpring4InterfaceOnlyGenerator(ResourceParser scanner) {
        super(scanner);
    }

    @Override
    public List<ApiControllerMetadataSerializer> generateClassForRaml(ApiControllerMetadata controller, String header) {
        return Arrays.asList(
                new Spring4ControllerInterfaceWithAnnotationsSerializer(controller, header)
        );
    }
}
