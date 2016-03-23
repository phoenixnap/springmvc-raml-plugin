package com.phoenixnap.oss.ramlapisync.generation.rules;

import com.phoenixnap.oss.ramlapisync.generation.GeneratedArtefact;
import com.phoenixnap.oss.ramlapisync.generation.JavaControllerGeneratorRecipe;
import org.junit.Test;

import static com.phoenixnap.oss.ramlapisync.generation.Inflector.camelize;

/**
 * @author armin.weisser
 */
public class RecipesTest extends AbstractControllerRuleTestBase {

    @Test
    public void generatorRecipe_SimpleSpring4ControllerStub() {

        JavaControllerGeneratorRecipe generator = new JavaControllerGeneratorRecipe();
        generator.setPackageRule(new PackageRule());
        generator.addClassAnnotationRule(new Spring4RestControllerAnnotationRule());
        generator.addClassAnnotationRule(new Spring4RequestMappingClassAnnotationRule());
        generator.setClassRule(new ControllerClassRule());
        generator.addMethodAnnotationRule(new Spring4RequestMappingMethodAnnotationRule());
        generator.setMethodSignatureRule(new Spring4ControllerMethodSignatureRule());
        generator.setMetodBodyRule(new ImplementMeMethodBodyRule());

        jCodeModel = generator.apply(getControllerMetadata()).getModel();

    }

    @Test
    public void generatorRecipe_Spring4ControllerInterface() {

        JavaControllerGeneratorRecipe generator = new JavaControllerGeneratorRecipe();
        generator.setPackageRule(new PackageRule());
        generator.addClassAnnotationRule(new Spring4RestControllerAnnotationRule());
        generator.addClassAnnotationRule(new Spring4RequestMappingClassAnnotationRule());
        generator.setClassRule(new ControllerInterfaceRule());
        generator.addMethodAnnotationRule(new Spring4RequestMappingMethodAnnotationRule());
        generator.setMethodSignatureRule(new Spring4ControllerMethodSignatureRule());

        jCodeModel = generator.apply(getControllerMetadata()).getModel();

    }

    @Test
    public void generatorRecipe_Spring4ControllerDecorator() {

        JavaControllerGeneratorRecipe generator = new JavaControllerGeneratorRecipe();
        generator.setPackageRule(new PackageRule());
        generator.setClassRule(new ControllerInterfaceRule());
        generator.setMethodSignatureRule(new Spring4ControllerMethodSignatureRule());

        GeneratedArtefact interfaceArtefact = generator.apply(getControllerMetadata());
        jCodeModel = interfaceArtefact.getModel();
        printCode();

        String delegateFieldName = camelize(interfaceArtefact.getTopLevelClass().name()+"Delegate", false);

        generator = new JavaControllerGeneratorRecipe();
        generator.setPackageRule(new PackageRule());
        generator.addClassAnnotationRule(new Spring4RestControllerAnnotationRule());
        generator.addClassAnnotationRule(new Spring4RequestMappingClassAnnotationRule());
        generator.setClassRule(new ControllerClassRule("Decorator"));
        generator.setImplementsRule(new ImplementsControllerInterfaceRule(interfaceArtefact.getTopLevelClass()));
        generator.addFieldDeclerationRule(new SpringDelegateFieldDeclerationRule(delegateFieldName));
        generator.setMethodSignatureRule(new Spring4ControllerMethodSignatureRule());
        generator.setMetodBodyRule(new DelegatingMethodBodyRule(delegateFieldName));

        jCodeModel = generator.apply(getControllerMetadata()).getModel();

    }
}
