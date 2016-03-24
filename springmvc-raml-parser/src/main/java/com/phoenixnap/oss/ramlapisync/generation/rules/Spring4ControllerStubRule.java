package com.phoenixnap.oss.ramlapisync.generation.rules;

import com.phoenixnap.oss.ramlapisync.data.ApiControllerMetadata;
import com.phoenixnap.oss.ramlapisync.generation.rules.basic.*;
import com.phoenixnap.oss.ramlapisync.generation.rules.spring.*;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;

/**
 * @author armin.weisser
 * @since 0.3.2
 */
public class Spring4ControllerStubRule implements Rule<JCodeModel, JDefinedClass, ApiControllerMetadata> {

    @Override
    public JDefinedClass apply(ApiControllerMetadata metadata, JCodeModel generatableType) {

        GenericJavaClassRule generator = new GenericJavaClassRule()
                .setPackageRule(new PackageRule())
                .setClassCommentRule(new ClassCommentRule())
                .addClassAnnotationRule(new Spring4RestControllerAnnotationRule())
                .addClassAnnotationRule(new SpringRequestMappingClassAnnotationRule())
                .setClassRule(new ControllerClassDeclarationRule())
                .setMethodCommentRule(new MethodCommentRule())
                .addMethodAnnotationRule(new SpringRequestMappingMethodAnnotationRule())
                .addMethodAnnotationRule(new SpringResponseBodyMethodAnnotationRule())
                .setMethodSignatureRule(new ControllerMethodSignatureRule(
                        new SpringSimpleResponseTypeRule(),
                        new SpringMethodParamsRule()
                ))
                .setMetodBodyRule(new ImplementMeMethodBodyRule());

        return generator.apply(metadata, generatableType);
    }
}
