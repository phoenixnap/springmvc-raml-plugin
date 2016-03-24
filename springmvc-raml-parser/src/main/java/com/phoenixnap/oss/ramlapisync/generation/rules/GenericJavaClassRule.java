package com.phoenixnap.oss.ramlapisync.generation.rules;

import com.phoenixnap.oss.ramlapisync.data.ApiControllerMetadata;
import com.phoenixnap.oss.ramlapisync.data.ApiMappingMetadata;
import com.phoenixnap.oss.ramlapisync.generation.rules.basic.MethodCommentRule;
import com.sun.codemodel.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author armin.weisser
 * @since 0.3.2
 */
public class GenericJavaClassRule implements Rule<JCodeModel, JDefinedClass, ApiControllerMetadata> {

    private Rule<JCodeModel, JPackage, ApiControllerMetadata> packageRule;
    private List<Rule<JDefinedClass,JAnnotationUse, ApiControllerMetadata>> classAnnotationRules = new ArrayList<>();
    private Rule<JPackage,JDefinedClass, ApiControllerMetadata> classRule;
    private Optional<Rule<JDefinedClass, JDefinedClass, ApiControllerMetadata>> implementsRule = Optional.empty();
    private List<Rule<JDefinedClass, JFieldVar, ApiControllerMetadata>> fieldDeclerationRules = new ArrayList<>();
    private Rule<JDefinedClass, JMethod, ApiMappingMetadata> methodSignatureRule;
    private Optional<Rule<JMethod, JMethod, ApiMappingMetadata>> metodBodyRule = Optional.empty();
    private List<Rule<JMethod, JAnnotationUse, ApiMappingMetadata>> methodAnnotationRules = new ArrayList<>();
    private MethodCommentRule methodCommentRule;

    public JDefinedClass apply(ApiControllerMetadata metadata, JCodeModel codeModel) {
        JPackage jPackage = packageRule.apply(metadata, codeModel);
        JDefinedClass jClass = classRule.apply(metadata, jPackage);
        implementsRule.ifPresent(rule -> rule.apply(metadata, jClass));
        classAnnotationRules.forEach(rule -> rule.apply(metadata, jClass));
        fieldDeclerationRules.forEach(rule -> rule.apply(metadata, jClass));
        metadata.getApiCalls().forEach( apiMappingMetadata -> {
            JMethod jMethod = methodSignatureRule.apply(apiMappingMetadata, jClass);
            methodCommentRule.apply(apiMappingMetadata, jMethod);
            methodAnnotationRules.forEach(rule -> rule.apply(apiMappingMetadata, jMethod));
            metodBodyRule.ifPresent( rule -> rule.apply(apiMappingMetadata, jMethod));
        });
        return jClass;
    }


    public GenericJavaClassRule setPackageRule(Rule<JCodeModel, JPackage, ApiControllerMetadata> packageRule) {
        this.packageRule = packageRule;
        return this;
    }

    public GenericJavaClassRule addClassAnnotationRule(Rule<JDefinedClass,JAnnotationUse, ApiControllerMetadata> annotationRule) {
        this.classAnnotationRules.add(annotationRule);
        return this;
    }

    public GenericJavaClassRule setClassRule(Rule<JPackage, JDefinedClass, ApiControllerMetadata> classRule) {
        this.classRule = classRule;
        return this;
    }

    public GenericJavaClassRule setMethodSignatureRule(Rule<JDefinedClass, JMethod, ApiMappingMetadata> methodSignatureRule) {
        this.methodSignatureRule = methodSignatureRule;
        return this;
    }

    public GenericJavaClassRule setMetodBodyRule(Rule<JMethod, JMethod, ApiMappingMetadata> metodBodyRule) {
        this.metodBodyRule = Optional.of(metodBodyRule);
        return this;
    }

    public GenericJavaClassRule addFieldDeclerationRule(Rule<JDefinedClass, JFieldVar, ApiControllerMetadata> fieldDeclerationRule) {
        this.fieldDeclerationRules.add(fieldDeclerationRule);
        return this;
    }

    public GenericJavaClassRule setImplementsRule(Rule<JDefinedClass, JDefinedClass, ApiControllerMetadata>  implementsRule) {
        this.implementsRule = Optional.of(implementsRule);
        return this;
    }

    public GenericJavaClassRule addMethodAnnotationRule(Rule<JMethod, JAnnotationUse, ApiMappingMetadata> methodAnnotationRule) {
        this.methodAnnotationRules.add(methodAnnotationRule);
        return this;
    }


    public GenericJavaClassRule setMethodCommentRule(MethodCommentRule methodCommentRule) {
        this.methodCommentRule = methodCommentRule;
        return this;
    }

}
