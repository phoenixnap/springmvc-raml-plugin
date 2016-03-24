package com.phoenixnap.oss.ramlapisync.generation.rules;

import com.phoenixnap.oss.ramlapisync.data.ApiControllerMetadata;
import com.phoenixnap.oss.ramlapisync.generation.rules.basic.ControllerClassDeclarationRule;
import com.phoenixnap.oss.ramlapisync.generation.rules.basic.ControllerMethodSignatureRule;
import com.phoenixnap.oss.ramlapisync.generation.rules.basic.ImplementMeMethodBodyRule;
import com.phoenixnap.oss.ramlapisync.generation.rules.basic.PackageRule;
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

        GenericJavaClassRule generator = new GenericJavaClassRule();
        generator.setPackageRule(new PackageRule());
        generator.addClassAnnotationRule(new Spring4RestControllerAnnotationRule());
        generator.addClassAnnotationRule(new SpringRequestMappingClassAnnotationRule());
        generator.setClassRule(new ControllerClassDeclarationRule());
        generator.addMethodAnnotationRule(new SpringRequestMappingMethodAnnotationRule());
        generator.addMethodAnnotationRule(new SpringResponseBodyMethodAnnotationRule());
        generator.setMethodSignatureRule(new ControllerMethodSignatureRule(
                new SpringSimpleResponseTypeRule()
        ));
        generator.setMetodBodyRule(new ImplementMeMethodBodyRule());

        return generator.apply(metadata, generatableType);
    }
}
