package com.phoenixnap.oss.ramlplugin.raml2code.github;

import static org.junit.Assert.fail;

import org.junit.Test;

import com.phoenixnap.oss.ramlplugin.raml2code.exception.InvalidRamlException;
import com.phoenixnap.oss.ramlplugin.raml2code.exception.UnsupportedRamlVersionException;
import com.phoenixnap.oss.ramlplugin.raml2code.rules.GitHubAbstractRuleTestBase;

/**
 * @author kurtpa
 * @since 0.4.2
 */
public class Issue156RulesTest extends GitHubAbstractRuleTestBase {

	@Test
	public void versionCheck_shouldReportUnsupportedVersion() throws Exception {
		try {
			loadRaml("issue-156-unsupported.raml");
			fail();
		} catch (UnsupportedRamlVersionException urve) {
			// ok
		}
	}

	@Test
	public void versionCheck_shouldReportErrorAsInvalidRamlError() throws Exception {
		try {
			loadRaml("issue-156-normal_invalidity.raml");
			fail();
		} catch (InvalidRamlException ire) {
			// ok
		}
	}
}
