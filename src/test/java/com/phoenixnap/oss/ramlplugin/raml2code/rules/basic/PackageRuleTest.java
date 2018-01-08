package com.phoenixnap.oss.ramlplugin.raml2code.rules.basic;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import com.phoenixnap.oss.ramlplugin.raml2code.plugin.TestConfig;
import com.phoenixnap.oss.ramlplugin.raml2code.rules.AbstractRuleTestBase;
import com.sun.codemodel.JPackage;

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
		String emptyBasePackage = "     ";
		TestConfig.setBasePackage(emptyBasePackage);

		JPackage jPackage = rule.apply(getControllerMetadata(), jCodeModel);
		assertThat(jPackage, is(notNullValue()));
		assertThat(jPackage.name(), equalTo(""));
	}

	@Test
	public void applyPackageRule_shouldCreate_emptyBasePackage_onEmptyPackage() {
		String emptyBasePackage = "     ";
		TestConfig.setBasePackage(emptyBasePackage);

		JPackage jPackage = rule.apply(getControllerMetadata(), jCodeModel);
		assertThat(jPackage, is(notNullValue()));
		assertThat(jPackage.name(), equalTo(""));
	}

	@Test
	public void applyPackageRule_shouldCreate_emptyBasePackage_onWhitespacePackage() {
		String emptyBasePackage = "     ";
		TestConfig.setBasePackage(emptyBasePackage);

		JPackage jPackage = rule.apply(getControllerMetadata(), jCodeModel);
		assertThat(jPackage, is(notNullValue()));
		assertThat(jPackage.name(), equalTo(""));
	}

}
