package com.phoenixnap.oss.ramlapisync.generation;

import com.phoenixnap.oss.ramlapisync.data.ApiControllerMetadata;
import com.phoenixnap.oss.ramlapisync.data.ApiMappingMetadata;
import com.phoenixnap.oss.ramlapisync.generation.rules.Rule;
import com.sun.codemodel.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author armin.weisser
 */
public class JavaControllerGeneratorRecipe implements GeneratorRecipe<GeneratedArtefact, ApiControllerMetadata> {

    private Rule<JCodeModel, JPackage, ApiControllerMetadata> packageRule;
    private List<Rule<JDefinedClass,JAnnotationUse, ApiControllerMetadata>> classAnnotationRules = new ArrayList<>();
    private Rule<JPackage,JDefinedClass, ApiControllerMetadata> classRule;
    private Optional<Rule<JDefinedClass, JDefinedClass, ApiControllerMetadata>> implementsRule = Optional.empty();
    private List<Rule<JDefinedClass, JFieldVar, ApiControllerMetadata>> fieldDeclerationRules = new ArrayList<>();
    private Rule<JDefinedClass, JMethod, ApiMappingMetadata> methodSignatureRule;
    private Optional<Rule<JMethod, JMethod, ApiMappingMetadata>> metodBodyRule = Optional.empty();
    private List<Rule<JMethod, JAnnotationUse, ApiMappingMetadata>> methodAnnotationRules = new ArrayList<>();

    @Override
    public GeneratedArtefact apply(ApiControllerMetadata metadata) {
        JCodeModel codeModel = new JCodeModel();

        JPackage jPackage = packageRule.apply(metadata, codeModel);
        JDefinedClass jClass = classRule.apply(metadata, jPackage);
        implementsRule.ifPresent(rule -> rule.apply(metadata, jClass));
        classAnnotationRules.forEach(rule -> rule.apply(metadata, jClass));
        fieldDeclerationRules.forEach(rule -> rule.apply(metadata, jClass));
        metadata.getApiCalls().forEach( apiMappingMetadata -> {
            JMethod jMethod = methodSignatureRule.apply(apiMappingMetadata, jClass);
            methodAnnotationRules.forEach(rule -> rule.apply(apiMappingMetadata, jMethod));
            metodBodyRule.ifPresent( rule -> rule.apply(apiMappingMetadata, jMethod));
        });

        return new GeneratedArtefact(codeModel, jClass);
    }


    public void setPackageRule(Rule<JCodeModel, JPackage, ApiControllerMetadata> packageRule) {
        this.packageRule = packageRule;
    }

    public void addClassAnnotationRule(Rule<JDefinedClass,JAnnotationUse, ApiControllerMetadata> annotationRule) {
        this.classAnnotationRules.add(annotationRule);
    }

    public void setClassRule(Rule<JPackage, JDefinedClass, ApiControllerMetadata> classRule) {
        this.classRule = classRule;
    }

    public void setMethodSignatureRule(Rule<JDefinedClass, JMethod, ApiMappingMetadata> methodSignatureRule) {
        this.methodSignatureRule = methodSignatureRule;
    }

    public void setMetodBodyRule(Rule<JMethod, JMethod, ApiMappingMetadata> metodBodyRule) {
        this.metodBodyRule = Optional.of(metodBodyRule);
    }

    public void addFieldDeclerationRule(Rule<JDefinedClass, JFieldVar, ApiControllerMetadata> fieldDeclerationRule) {
        this.fieldDeclerationRules.add(fieldDeclerationRule);
    }

    public void setImplementsRule(Rule<JDefinedClass, JDefinedClass, ApiControllerMetadata>  implementsRule) {
        this.implementsRule = Optional.of(implementsRule);
    }

    public void addMethodAnnotationRule(Rule<JMethod, JAnnotationUse, ApiMappingMetadata> methodAnnotationRule) {
        this.methodAnnotationRules.add(methodAnnotationRule);
    }
}
