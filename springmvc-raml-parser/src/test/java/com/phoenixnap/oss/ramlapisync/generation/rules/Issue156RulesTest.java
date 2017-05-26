package com.phoenixnap.oss.ramlapisync.generation.rules;

import static org.junit.Assert.fail;

import com.phoenixnap.oss.ramlapisync.raml.InvalidRamlError;
import com.phoenixnap.oss.ramlapisync.raml.UnsupportedRamlVersionError;
import org.junit.Test;

/**
 * @author kurtpa
 * @since 0.4.2
 */
public class Issue156RulesTest extends AbstractRuleTestBase {
	
	@Test
    public void versionCheck_shouldReportUnsupportedVersion() throws Exception {
        try {
            RamlLoader.loadRamlFromFile("issue-156-unsupported.raml");
            fail();
        } catch (UnsupportedRamlVersionError urve) {
            // ok
        }
    }

    @Test
    public void versionCheck_shouldReportErrorAsInvalidRamlError() throws Exception {
        try {
            RamlLoader.loadRamlFromFile("issue-156-normal_invalidity.raml");
            fail();
        } catch (InvalidRamlError ire) {
            // ok
        }
    }
}
