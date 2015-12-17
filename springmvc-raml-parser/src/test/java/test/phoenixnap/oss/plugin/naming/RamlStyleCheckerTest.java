/*
 * Copyright 2002-2015 the original author or authors.
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
package test.phoenixnap.oss.plugin.naming;

import static org.junit.Assert.*;

import java.util.Collections;

import org.junit.Test;
import org.raml.model.Raml;

import com.phoenixnap.oss.ramlapisync.generation.RamlGenerator;
import com.phoenixnap.oss.ramlapisync.generation.RamlVerifier;
import com.phoenixnap.oss.ramlapisync.parser.SpringMvcResourceParser;
import com.phoenixnap.oss.ramlapisync.style.checkers.ActionSecurityResponseChecker;
import com.phoenixnap.oss.ramlapisync.style.checkers.ResourceCollectionPluralisationChecker;
import com.phoenixnap.oss.ramlapisync.style.checkers.ResourceUrlStyleChecker;
import com.phoenixnap.oss.ramlapisync.verification.Issue;
import com.phoenixnap.oss.ramlapisync.verification.IssueLocation;
import com.phoenixnap.oss.ramlapisync.verification.IssueSeverity;
import com.phoenixnap.oss.ramlapisync.verification.IssueType;
import com.phoenixnap.oss.ramlapisync.verification.checkers.ActionExistenceChecker;

/**
 * Unit tests for the Style Checkers
 * 
 * @author Kurt Paris
 * @since 0.0.2
 *
 */
public class RamlStyleCheckerTest {

	SpringMvcResourceParser parser = new SpringMvcResourceParser(null, "0.0.1", "test-type", false);
	RamlGenerator generator = new RamlGenerator(parser);
	
	@Test
	public void test_UrlStyleChecker_Success() {
		Raml published = RamlVerifier.loadRamlFromFile("test-style-success.raml");
		
		RamlVerifier verifier = new RamlVerifier(published, null, Collections.emptyList(), null, null, Collections.singletonList(new ResourceUrlStyleChecker()));
		assertFalse("Check that raml passes rules", verifier.hasErrors());
		assertFalse("Check that implementation matches rules", verifier.hasWarnings());
		
	}
	
	@Test
	public void test_PluralChecker_Success() {
		Raml published = RamlVerifier.loadRamlFromFile("test-style-pluralised-success.raml");
		
		RamlVerifier verifier = new RamlVerifier(published, null, Collections.emptyList(), null, null, Collections.singletonList(new ResourceCollectionPluralisationChecker()));
		assertFalse("Check that raml passes rules", verifier.hasErrors());
		assertFalse("Check that implementation matches rules", verifier.hasWarnings());
		
	}
	
	@Test
	public void test_PluralChecker_Fails() {
		Raml published = RamlVerifier.loadRamlFromFile("test-style-pluralised-singularcollection.raml");
		
		RamlVerifier verifier = new RamlVerifier(published, null, Collections.emptyList(), null, null, Collections.singletonList(new ResourceCollectionPluralisationChecker()));
		assertFalse("Check that raml passes rules", verifier.hasErrors());
		assertTrue("Check that implementation matches rules", verifier.hasWarnings());
		assertEquals("Check that implementation shuld have 1 warning", 1, verifier.getWarnings().size());
		Issue errorIssue = verifier.getWarnings().iterator().next();
		TestHelper.verifyIssue(IssueLocation.CONTRACT, IssueSeverity.WARNING, IssueType.STYLE, ResourceCollectionPluralisationChecker.DESCRIPTION, "/ignored/house", errorIssue);
		
	}
	
	@Test
	public void test_ActionSecurityResponseCheck_Success() {
		Raml published = RamlVerifier.loadRamlFromFile("test-style-actionsecurity-success.raml");
		
		RamlVerifier verifier = new RamlVerifier(published, null, Collections.emptyList(), null, null, Collections.singletonList(new ActionSecurityResponseChecker()));
		assertFalse("Check that raml passes rules", verifier.hasErrors());
		assertFalse("Check that implementation matches rules", verifier.hasWarnings());
	}
	
	@Test
	public void test_ActionSecurityResponseCheck_Fails() {
		Raml published = RamlVerifier.loadRamlFromFile("test-style-actionsecurity-fails.raml");
		
		RamlVerifier verifier = new RamlVerifier(published, null, Collections.emptyList(), null, null, Collections.singletonList(new ActionSecurityResponseChecker()));
		assertFalse("Check that raml passes rules", verifier.hasErrors());
		assertTrue("Check that implementation matches rules", verifier.hasWarnings());
		assertEquals("Check that implementation shuld have 2 warnings", 2, verifier.getWarnings().size());
		TestHelper.verifyIssuesUnordered(verifier.getWarnings(), new Issue[] {
			new Issue(IssueSeverity.WARNING, IssueLocation.CONTRACT, IssueType.STYLE, ActionSecurityResponseChecker.DESCRIPTION, "GET /ignored/houses/{houseId}"),
			new Issue(IssueSeverity.WARNING, IssueLocation.CONTRACT, IssueType.STYLE, ActionSecurityResponseChecker.DESCRIPTION, "GET /ignored/houses")});
	}
	

}
