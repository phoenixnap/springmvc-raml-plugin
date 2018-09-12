package com.phoenixnap.oss.ramlplugin.raml2code.rules;

import static org.junit.Assert.assertEquals;

import org.hamcrest.MatcherAssert;

public abstract class GitHubAbstractRuleTestBase extends AbstractRuleTestBase {

	public static final String RESOURCE_BASE = AbstractRuleTestBase.RESOURCE_BASE + "github/";
	public static final String DEFAULT_GITHUB_VALIDATOR_BASE = "validations/github/";

	private String gitHubValidatorBase = DEFAULT_GITHUB_VALIDATOR_BASE;

	protected void verifyGeneratedCode(String name, String generatedCode) throws Exception {
		String removedSerialVersionUID = removeSerialVersionUID(generatedCode);
		String expectedCode = getTextFromFile(this.gitHubValidatorBase + name + ".java.txt");

		try {
			MatcherAssert.assertThat(name + " is not generated correctly.", removedSerialVersionUID,
					new IsEqualIgnoringLeadingAndEndingWhiteSpaces(expectedCode));
		} catch (AssertionError e) {
			// We let assertEquals fail here instead, because better IDE support
			// for multi line string diff.
			assertEquals(expectedCode, removedSerialVersionUID);
		}
	}

	public static void loadRaml(String ramlFileName) {
		AbstractRuleTestBase.RAML = RamlLoader.loadRamlFromFile(RESOURCE_BASE + ramlFileName);
	}

	/**
	 * @param gitHubValidatorBase
	 *            the gitHubValidatorBase to set
	 */
	public void setGitHubValidatorBase(String gitHubValidatorBase) {
		this.gitHubValidatorBase = gitHubValidatorBase;
	}
}
