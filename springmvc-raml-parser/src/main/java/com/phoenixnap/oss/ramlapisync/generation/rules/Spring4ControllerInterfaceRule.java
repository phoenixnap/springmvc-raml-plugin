package com.phoenixnap.oss.ramlapisync.generation.rules;

import com.phoenixnap.oss.ramlapisync.data.ApiControllerMetadata;
import com.phoenixnap.oss.ramlapisync.generation.rules.basic.ControllerInterfaceDeclarationRule;
import com.phoenixnap.oss.ramlapisync.generation.rules.basic.ControllerMethodSignatureRule;
import com.phoenixnap.oss.ramlapisync.generation.rules.basic.PackageRule;
import com.phoenixnap.oss.ramlapisync.generation.rules.spring.Spring4RestControllerAnnotationRule;
import com.phoenixnap.oss.ramlapisync.generation.rules.spring.SpringRequestMappingClassAnnotationRule;
import com.phoenixnap.oss.ramlapisync.generation.rules.spring.SpringRequestMappingMethodAnnotationRule;
import com.phoenixnap.oss.ramlapisync.generation.rules.spring.SpringResponseEntityRule;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;

/**
 * @author armin.weisser
 * @since 0.3.2
 */
public class Spring4ControllerInterfaceRule implements Rule<JCodeModel, JDefinedClass, ApiControllerMetadata> {

    @Override
    public JDefinedClass apply(ApiControllerMetadata metadata, JCodeModel generatableType) {

        GenericJavaClassRule generator = new GenericJavaClassRule();
        generator.setPackageRule(new PackageRule());
        generator.addClassAnnotationRule(new Spring4RestControllerAnnotationRule());
        generator.addClassAnnotationRule(new SpringRequestMappingClassAnnotationRule());
        generator.setClassRule(new ControllerInterfaceDeclarationRule());
        generator.addMethodAnnotationRule(new SpringRequestMappingMethodAnnotationRule());
        generator.setMethodSignatureRule(new ControllerMethodSignatureRule(
                new SpringResponseEntityRule()
        ));

        return generator.apply(metadata, generatableType);
    }
}
