/*
 * Copyright 2002-2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package com.phoenixnap.oss.ramlapisync.generation.rules;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.phoenixnap.oss.ramlapisync.data.ApiResourceMetadata;
import com.phoenixnap.oss.ramlapisync.data.ApiActionMetadata;
import com.phoenixnap.oss.ramlapisync.generation.CodeModelHelper;
import com.phoenixnap.oss.ramlapisync.generation.CodeModelHelper.JExtMethod;
import com.sun.codemodel.JAnnotationUse;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JDocComment;
import com.sun.codemodel.JFieldVar;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JPackage;

/**
 * This is a configurable template rule which generates a Java artefact (class or interface)
 * based on an ApiControllerMetadata instance.
 * The code generation steps are executed by applying subsequent Rule instances.
 *
 * The caller has to set appropriate Rule instances for each part of the Java class generation.
 *
 * At least a packageRule and a classRule is mandatory.
 * If the ApiControllerMetadata contains one or more endpoints (ApiMappingMetadata)
 * at least methodSignatureRule is mandatory.
 *
 * packageRule              - a package declaration rule
 * classCommentRule         - an optional class comment rule
 * classAnnotationRules     - a set of class annotation rules
 * classRule                - a class declaration rule
 * implementsExtendsRule    - an optional rule to define implements/extends part of the class declaration
 * fieldDeclerationRules    - a set of field declaration rules. May be empty.
 * methodCommentRule        - an optional method comment rule
 * methodAnnotationRules    - a set of method annotation rules. May be empty.
 * methodSignatureRule      - a method signature rule (just the signature, no annotations, no body)
 * metodBodyRule            - an optional method body rule
 *
 * @author armin.weisser
 * @since 0.4.1
 */
public class GenericJavaClassRule implements Rule<JCodeModel, JDefinedClass, ApiResourceMetadata> {

    /**
     * a package declaration rule
     */
    private Rule<JCodeModel, JPackage, ApiResourceMetadata> packageRule;

    /**
     * an optional class comment rule.
     */
    private Optional<Rule<JDefinedClass, JDocComment, ApiResourceMetadata>> classCommentRule =  Optional.empty();

    /**
     * a set of class annotation rules. May be empty.
     */
    private List<Rule<JDefinedClass,JAnnotationUse, ApiResourceMetadata>> classAnnotationRules = new ArrayList<>();

    /**
     * an optional class declaration rule.
     */
    private Rule<JPackage,JDefinedClass, ApiResourceMetadata> classRule;

    /**
     * an optional rule to define implements/extends part of the class declaration
     */
    private Optional<Rule<JDefinedClass, JDefinedClass, ApiResourceMetadata>> implementsExtendsRule = Optional.empty();

    /**
     * a set of field declaration rules. May be empty.
     */
    private List<Rule<JDefinedClass, JFieldVar, ApiResourceMetadata>> fieldDeclerationRules = new ArrayList<>();

    /**
     * an optional method comment rule
     */
    private Optional<Rule<JMethod, JDocComment, ApiActionMetadata>> methodCommentRule = Optional.empty();

    /**
     * a set of method annotation rules. May be empty.
     */
    private List<Rule<JMethod, JAnnotationUse, ApiActionMetadata>> methodAnnotationRules = new ArrayList<>();

    /**
     * a method signature rule (just the signature, no annotations, no body)
     */
    private Rule<JDefinedClass, JMethod, ApiActionMetadata> methodSignatureRule;

    /**
     * an optional method body rule
     */
    private Optional<Rule<JExtMethod, JMethod, ApiActionMetadata>> methodBodyRule = Optional.empty();
    
    /**
     * @throws IllegalStateException if a packageRule or classRule is missing or if the ApiControllerMetadata
     *         requires a missing methodSignatureRule.
     */
    @Override
    public JDefinedClass apply(ApiResourceMetadata metadata, JCodeModel codeModel) {

        if(packageRule == null || classRule == null) {
            throw new IllegalStateException("A packageRule and classRule are mandatory.");
        }
        if(!metadata.getApiCalls().isEmpty() && methodSignatureRule == null) {
            throw new IllegalStateException("Since there are API Calls in the metadata at least a methodSignatureRule is mandatory");
        }

        JPackage jPackage = packageRule.apply(metadata, codeModel);
        JDefinedClass jClass = classRule.apply(metadata, jPackage);
        implementsExtendsRule.ifPresent(rule -> rule.apply(metadata, jClass));
        classCommentRule.ifPresent(rule-> rule.apply(metadata, jClass));
        classAnnotationRules.forEach(rule -> rule.apply(metadata, jClass));
        fieldDeclerationRules.forEach(rule -> rule.apply(metadata, jClass));
        metadata.getApiCalls().forEach( apiMappingMetadata -> {
            JMethod jMethod = methodSignatureRule.apply(apiMappingMetadata, jClass);
            methodCommentRule.ifPresent(rule-> rule.apply(apiMappingMetadata, jMethod));
            methodAnnotationRules.forEach(rule -> rule.apply(apiMappingMetadata, jMethod));
            methodBodyRule.ifPresent( rule -> rule.apply(apiMappingMetadata, CodeModelHelper.ext(jMethod, jClass.owner())));
        });
        return jClass;
    }


    public GenericJavaClassRule setPackageRule(Rule<JCodeModel, JPackage, ApiResourceMetadata> packageRule) {
        this.packageRule = packageRule;
        return this;
    }

    public GenericJavaClassRule addClassAnnotationRule(Rule<JDefinedClass,JAnnotationUse, ApiResourceMetadata> annotationRule) {
        if(annotationRule != null) {
            this.classAnnotationRules.add(annotationRule);
        }
        return this;
    }

    public GenericJavaClassRule setClassCommentRule(Rule<JDefinedClass, JDocComment, ApiResourceMetadata> classCommentRule) {
        this.classCommentRule = Optional.ofNullable(classCommentRule);
        return this;
    }

    public GenericJavaClassRule setClassRule(Rule<JPackage, JDefinedClass, ApiResourceMetadata> classRule) {
        this.classRule = classRule;
        return this;
    }

    public GenericJavaClassRule setMethodSignatureRule(Rule<JDefinedClass, JMethod, ApiActionMetadata> methodSignatureRule) {
        this.methodSignatureRule = methodSignatureRule;
        return this;
    }

    public GenericJavaClassRule setMethodBodyRule(Rule<JExtMethod, JMethod, ApiActionMetadata> methodBodyRule) {
        this.methodBodyRule = Optional.ofNullable(methodBodyRule);
        return this;
    }
    
    public GenericJavaClassRule addFieldDeclarationRule(Rule<JDefinedClass, JFieldVar, ApiResourceMetadata> fieldDeclerationRule) {
        this.fieldDeclerationRules.add(fieldDeclerationRule);
        return this;
    }

    public GenericJavaClassRule setImplementsExtendsRule(Rule<JDefinedClass, JDefinedClass, ApiResourceMetadata> implementsExtendsRule) {
        this.implementsExtendsRule = Optional.ofNullable(implementsExtendsRule);
        return this;
    }

    public GenericJavaClassRule addMethodAnnotationRule(Rule<JMethod, JAnnotationUse, ApiActionMetadata> methodAnnotationRule) {
        if(methodAnnotationRule != null) {
            this.methodAnnotationRules.add(methodAnnotationRule);
        }
        return this;
    }


    public GenericJavaClassRule setMethodCommentRule(Rule<JMethod,JDocComment,ApiActionMetadata> methodCommentRule) {
        this.methodCommentRule = Optional.ofNullable(methodCommentRule);
        return this;
    }

}
