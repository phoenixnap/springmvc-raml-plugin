package com.phoenixnap.oss.ramlapisync.generation;

import com.phoenixnap.oss.ramlapisync.data.ApiControllerMetadata;
import com.phoenixnap.oss.ramlapisync.parser.ResourceParser;

/**
 * Extends the standard RamlGenerator by providing a Spring4 Controller based on a decorator pattern.
 * The goal is to generate code that does not have to be manually extended by the user.
 * A raml endpoint called /people for example implies two generated artefacts:
 * interface PeopleController {
 *     ResponseEntity getPeople();
 * }
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
 * @since 0.2.5
 */
public class RamlSpring4DecoratorGenerator extends RamlGenerator {

    /**
     * Default constructor
     *
     * @param scanner The resource parsing engine. Only required for RAML generation
     */
    public RamlSpring4DecoratorGenerator(ResourceParser scanner) {
        super(scanner);
    }

    @Override
    public String generateClassForRaml(ApiControllerMetadata controller, String header) {
        return super.generateClassForRaml(controller, header);
    }
}
