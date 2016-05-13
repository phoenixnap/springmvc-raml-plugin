package com.phoenixnap.oss.ramlapisync.generation.rules.basic;

import com.phoenixnap.oss.ramlapisync.generation.RamlParser;
import com.phoenixnap.oss.ramlapisync.generation.rules.AbstractRuleTestBase;
import com.sun.codemodel.JPackage;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;

/**
 * @author armin.weisser
 * @since 0.4.1
 */
public class PackageRuleTest extends AbstractRuleTestBase {

    private final PackageRule rule = new PackageRule();

    @Test
    public void applyPackageRule_shouldCreate_validBasePackage() {
        JPackage jPackage = rule.apply(getControllerMetadata(), jCodeModel);
        assertThat(jPackage, is(notNullValue()));
        assertThat(jPackage.name(), equalTo("com.gen.test"));
    }

    @Test
    public void applyPackageRule_shouldCreate_emptyBasePackage_onNullPackage() {
        String emptyBasePackage = null;
        initControllerMetadata(new RamlParser(emptyBasePackage));
        JPackage jPackage = rule.apply(getControllerMetadata(), jCodeModel);
        assertThat(jPackage, is(notNullValue()));
        assertThat(jPackage.name(), equalTo(""));
    }

    @Test
    public void applyPackageRule_shouldCreate_emptyBasePackage_onEmptyPackage() {
        String emptyBasePackage = "";
        initControllerMetadata(new RamlParser(emptyBasePackage));
        JPackage jPackage = rule.apply(getControllerMetadata(), jCodeModel);
        assertThat(jPackage, is(notNullValue()));
        assertThat(jPackage.name(), equalTo(""));
    }

    @Test
    public void applyPackageRule_shouldCreate_emptyBasePackage_onWhitespacePackage() {
        String emptyBasePackage = "     ";
        initControllerMetadata(new RamlParser(emptyBasePackage));
        JPackage jPackage = rule.apply(getControllerMetadata(), jCodeModel);
        assertThat(jPackage, is(notNullValue()));
        assertThat(jPackage.name(), equalTo(""));
    }

}
