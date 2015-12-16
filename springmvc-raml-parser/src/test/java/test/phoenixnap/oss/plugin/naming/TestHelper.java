package test.phoenixnap.oss.plugin.naming;

import static org.junit.Assert.*;

import java.util.Set;

import org.apache.commons.lang.ArrayUtils;

import com.phoenixnap.oss.ramlapisync.verification.Issue;
import com.phoenixnap.oss.ramlapisync.verification.IssueLocation;
import com.phoenixnap.oss.ramlapisync.verification.IssueSeverity;
import com.phoenixnap.oss.ramlapisync.verification.IssueType;

/**
 * Unit tests Helper Methods
 * 
 * @author Kurt Paris
 * @since 0.0.1
 *
 */
public class TestHelper {

	/**
	 * Checks an issue's data. Supply parameters null to skip checks;
	 * 
	 * @param location
	 * @param severity
	 * @param type
	 * @param errorIssue
	 * @param description
	 * @param ramlLocation
	 */
	public static void verifyIssue(IssueLocation location,
			IssueSeverity severity, IssueType type, 
			String description, String ramlLocation, Issue errorIssue) {
		if (location != null) {
			assertEquals(location, errorIssue.getLocation());
		}
		if (severity != null) {
			assertEquals(severity, errorIssue.getSeverity());
		}
		if (type != null) {
			assertEquals(type, errorIssue.getType());
		}
		if (description != null) {
			assertEquals(description, errorIssue.getDescription());
		}
		if (ramlLocation != null) {
			assertEquals(ramlLocation, errorIssue.getRamlLocation());
		}
	}

	/**
	 * Verifies that a set of issues are as expected. checks that the same amount of issues and
	 * @param issuesToVerify
	 * @param expectedIssues
	 */
	public static void verifyIssuesUnordered(Set<Issue> issuesToVerify, Issue[] expectedIssues) {
		Issue[] issuesToVerifyArray = issuesToVerify.toArray(new Issue[issuesToVerify.size()]);
		assertEquals(expectedIssues.length, issuesToVerifyArray.length);
		for (Issue expected : expectedIssues) {
			assertTrue("Expecting issue: "+ expected, ArrayUtils.contains(issuesToVerifyArray, expected));
		}
	}
}
