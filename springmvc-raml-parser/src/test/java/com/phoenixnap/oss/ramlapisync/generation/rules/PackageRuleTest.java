package com.phoenixnap.oss.ramlapisync.generation.rules;

import com.phoenixnap.oss.ramlapisync.generation.RamlParser;
import com.sun.codemodel.JPackage;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;

/**
 * @author armin.weisser
 * @since 0.3.2
 */
public class PackageRuleTest extends AbstractControllerRuleTestBase {

    @Test
    public void applyPackageRule_shouldCreate_validBasePackage() {
        JPackage jPackage = new PackageRule().apply(getControllerMetadata(), jCodeModel);
        assertThat(jPackage, is(notNullValue()));
        assertThat(jPackage.name(), equalTo("com.gen.test"));
    }

    @Test
    public void applyPackageRule_shouldCreate_emptyBasePackage_onNullPackage() {
        String emptyBasePackage = null;
        initControllerMetadata(new RamlParser(emptyBasePackage));
        JPackage jPackage = new PackageRule().apply(getControllerMetadata(), jCodeModel);
        assertThat(jPackage, is(notNullValue()));
        assertThat(jPackage.name(), equalTo(""));
    }

    @Test
    public void applyPackageRule_shouldCreate_emptyBasePackage_onEmptyPackage() {
        String emptyBasePackage = "";
        initControllerMetadata(new RamlParser(emptyBasePackage));
        JPackage jPackage = new PackageRule().apply(getControllerMetadata(), jCodeModel);
        assertThat(jPackage, is(notNullValue()));
        assertThat(jPackage.name(), equalTo(""));
    }

    @Test
    public void applyPackageRule_shouldCreate_emptyBasePackage_onWhitespacePackage() {
        String emptyBasePackage = "     ";
        initControllerMetadata(new RamlParser(emptyBasePackage));
        JPackage jPackage = new PackageRule().apply(getControllerMetadata(), jCodeModel);
        assertThat(jPackage, is(notNullValue()));
        assertThat(jPackage.name(), equalTo(""));
    }

}
