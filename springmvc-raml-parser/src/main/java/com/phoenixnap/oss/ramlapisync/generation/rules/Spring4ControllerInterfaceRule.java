package com.phoenixnap.oss.ramlapisync.generation.rules;

import com.phoenixnap.oss.ramlapisync.data.ApiControllerMetadata;
import com.phoenixnap.oss.ramlapisync.generation.rules.basic.*;
import com.phoenixnap.oss.ramlapisync.generation.rules.spring.*;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;

/**
 * A code generation Rule that provides a standalone Controller interface with Spring4 annotations.
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
 * @since 0.3.2
 */
public class Spring4ControllerInterfaceRule implements Rule<JCodeModel, JDefinedClass, ApiControllerMetadata> {

    @Override
    public JDefinedClass apply(ApiControllerMetadata metadata, JCodeModel generatableType) {

        GenericJavaClassRule generator = new GenericJavaClassRule()
                .setPackageRule(new PackageRule())
                .setClassCommentRule(new ClassCommentRule())
                .addClassAnnotationRule(new Spring4RestControllerAnnotationRule())
                .addClassAnnotationRule(new SpringRequestMappingClassAnnotationRule())
                .setClassRule(new ControllerInterfaceDeclarationRule())
                .setMethodCommentRule(new MethodCommentRule())
                .addMethodAnnotationRule(new SpringRequestMappingMethodAnnotationRule())
                .setMethodSignatureRule(new ControllerMethodSignatureRule(
                        new SpringResponseEntityRule(),
                        new SpringMethodParamsRule())
                );
        return generator.apply(metadata, generatableType);
    }
}
