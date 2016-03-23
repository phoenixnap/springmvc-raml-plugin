package com.phoenixnap.oss.ramlapisync.generation;

import com.phoenixnap.oss.ramlapisync.data.ApiControllerMetadata;
import com.phoenixnap.oss.ramlapisync.data.ApiMappingMetadata;
import com.phoenixnap.oss.ramlapisync.generation.rules.Rule;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JPackage;

/**
 * @author armin.weisser
 */
public class GeneratorRecipeBuilder {

    private Rule<JCodeModel, JPackage, ApiControllerMetadata> packageRule;
    private Rule<JPackage, JDefinedClass, ApiControllerMetadata> classRule;
    private Rule<JDefinedClass, JMethod, ApiMappingMetadata> methodRule;
    private JavaControllerGeneratorRecipe javaControllerGeneratorRecipe;

    public GeneratorRecipeBuilder addPackageRule(Rule<JCodeModel, JPackage, ApiControllerMetadata> packageRule) {
        this.packageRule = packageRule;
        return this;
    }

    public GeneratorRecipeBuilder addClassRule(Rule<JPackage,JDefinedClass, ApiControllerMetadata> classRule) {
        this.classRule = classRule;
        return this;
    }

    public GeneratorRecipeBuilder addMethodRule(Rule<JDefinedClass, JMethod, ApiMappingMetadata> methodRule) {
        this.methodRule = methodRule;
        return this;
    }

    public GeneratorRecipe<JCodeModel, ApiControllerMetadata> build() {
        JavaControllerGeneratorRecipe javaControllerGeneratorRecipe = getJavaControllerGeneratorRecipe();
        javaControllerGeneratorRecipe.setPackageRule(packageRule);

        javaControllerGeneratorRecipe.setClassRule(classRule);
        javaControllerGeneratorRecipe.setMethodSignatureRule(methodRule);
        return null;
    }

    protected JavaControllerGeneratorRecipe getJavaControllerGeneratorRecipe() {
        javaControllerGeneratorRecipe = new JavaControllerGeneratorRecipe();
        return javaControllerGeneratorRecipe;
    }
}
