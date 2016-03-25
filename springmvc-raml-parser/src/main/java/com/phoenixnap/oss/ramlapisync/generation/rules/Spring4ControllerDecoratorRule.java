package com.phoenixnap.oss.ramlapisync.generation.rules;

import com.phoenixnap.oss.ramlapisync.data.ApiControllerMetadata;
import com.phoenixnap.oss.ramlapisync.generation.rules.basic.*;
import com.phoenixnap.oss.ramlapisync.generation.rules.spring.*;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;
import org.apache.commons.lang.StringUtils;

/**
 * A code generation Rule that provides a Spring4 Controller based on a decorator pattern.
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
 * @since 0.3.2
 */
public class Spring4ControllerDecoratorRule implements Rule<JCodeModel, JDefinedClass, ApiControllerMetadata> {

    @Override
    public JDefinedClass apply(ApiControllerMetadata metadata, JCodeModel generatableType) {

        JDefinedClass generatedInterface = new GenericJavaClassRule()
                .setPackageRule(new PackageRule())
                .setClassCommentRule(new ClassCommentRule())
                .setClassRule(new ControllerInterfaceDeclarationRule())
                .setMethodCommentRule(new MethodCommentRule())
                .setMethodSignatureRule(new ControllerMethodSignatureRule(
                        new SpringResponseEntityRule(),
                        new MethodParamsRule()))
                .apply(metadata, generatableType);

        String delegateFieldName = StringUtils.uncapitalize(generatedInterface.name()+"Delegate");

        GenericJavaClassRule delegateGenerator = new GenericJavaClassRule()
                .setPackageRule(new PackageRule())
                .setClassCommentRule(new ClassCommentRule())
                .addClassAnnotationRule(new Spring4RestControllerAnnotationRule())
                .addClassAnnotationRule(new SpringRequestMappingClassAnnotationRule())
                .setClassRule(new ControllerClassDeclarationRule("Decorator"))
                .setImplementsExtendsRule(new ImplementsControllerInterfaceRule(generatedInterface))
                .addFieldDeclerationRule(new SpringDelegateFieldDeclerationRule(delegateFieldName))
                .setMethodCommentRule(new MethodCommentRule())
                .addMethodAnnotationRule(new SpringRequestMappingMethodAnnotationRule())
                .setMethodSignatureRule(new ControllerMethodSignatureRule(
                        new SpringResponseEntityRule(),
                        new SpringMethodParamsRule()))
                .setMetodBodyRule(new DelegatingMethodBodyRule(delegateFieldName));

        return delegateGenerator.apply(metadata, generatableType);
    }
}
