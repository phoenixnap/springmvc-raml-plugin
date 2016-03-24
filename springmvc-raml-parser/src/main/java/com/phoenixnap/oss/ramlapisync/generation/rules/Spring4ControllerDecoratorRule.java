package com.phoenixnap.oss.ramlapisync.generation.rules;

import com.phoenixnap.oss.ramlapisync.data.ApiControllerMetadata;
import com.phoenixnap.oss.ramlapisync.generation.rules.basic.*;
import com.phoenixnap.oss.ramlapisync.generation.rules.spring.*;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;
import org.apache.commons.lang.StringUtils;

/**
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
                .setImplementsRule(new ImplementsControllerInterfaceRule(generatedInterface))
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
