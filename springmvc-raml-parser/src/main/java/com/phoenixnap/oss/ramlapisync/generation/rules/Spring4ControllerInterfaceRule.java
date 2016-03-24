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
