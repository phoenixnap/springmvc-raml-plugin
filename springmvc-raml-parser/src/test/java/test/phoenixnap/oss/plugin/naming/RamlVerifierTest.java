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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Collections;
import java.util.Iterator;

import org.junit.Test;
import org.raml.model.Raml;

import test.phoenixnap.oss.plugin.naming.testclasses.SecondVerifierTestController;
import test.phoenixnap.oss.plugin.naming.testclasses.ThirdVerifierTestController;
import test.phoenixnap.oss.plugin.naming.testclasses.VerifierTestController;

import com.phoenixnap.oss.ramlapisync.generation.RamlGenerator;
import com.phoenixnap.oss.ramlapisync.generation.RamlVerifier;
import com.phoenixnap.oss.ramlapisync.parser.SpringMvcResourceParser;
import com.phoenixnap.oss.ramlapisync.verification.Issue;
import com.phoenixnap.oss.ramlapisync.verification.IssueLocation;
import com.phoenixnap.oss.ramlapisync.verification.IssueSeverity;
import com.phoenixnap.oss.ramlapisync.verification.IssueType;
import com.phoenixnap.oss.ramlapisync.verification.checkers.ActionExistenceChecker;
import com.phoenixnap.oss.ramlapisync.verification.checkers.ResourceExistenceChecker;

/**
 * Unit tests for the RamlVerifier class and associated Checkers
 * 
 * @author Kurt Paris
 * @since 0.0.2
 *
 */
public class RamlVerifierTest {

	SpringMvcResourceParser parser = new SpringMvcResourceParser(null, "0.0.1", "test-type", false);
	RamlGenerator generator = new RamlGenerator(parser);
	
	@Test
	public void test_ResourceExistenceChecker_Success() {
		Raml published = RamlVerifier.loadRamlFromFile("test-simple.raml");
		Class<?>[] classesToGenerate = new Class[] {VerifierTestController.class, SecondVerifierTestController.class};
		Raml computed = generator.generateRamlForClasses("test", "0.0.1", "/", classesToGenerate, Collections.emptySet()).getRaml();
		
		RamlVerifier verifier = new RamlVerifier(published, computed, Collections.singletonList(new ResourceExistenceChecker()), null, null);
		assertFalse("Check that there are no errors and implementation matches raml", verifier.hasErrors());
		assertFalse("Check that there are no warnings and implementation matches raml", verifier.hasWarnings());
		
	}
	
	@Test
	public void test_ActionExistenceChecker_Success() {
		Raml published = RamlVerifier.loadRamlFromFile("test-simple.raml");
		Class<?>[] classesToGenerate = new Class[] {VerifierTestController.class, SecondVerifierTestController.class};
		Raml computed = generator.generateRamlForClasses("test", "0.0.1", "/", classesToGenerate, Collections.emptySet()).getRaml();
		
		RamlVerifier verifier = new RamlVerifier(published, computed, Collections.emptyList(), null, Collections.singletonList(new ActionExistenceChecker()));
		assertFalse("Check that there are no errors and implementation matches raml", verifier.hasErrors());
		assertFalse("Check that there are no warnings and implementation matches raml", verifier.hasWarnings());
		
	}
	
	@Test
	public void test_ActionExistenceChecker_MissingAndExtraActions() {
		Raml published = RamlVerifier.loadRamlFromFile("test-actions-missing.raml");
		Class<?>[] classesToGenerate = new Class[] {VerifierTestController.class, SecondVerifierTestController.class};
		Raml computed = generator.generateRamlForClasses("test", "0.0.1", "/", classesToGenerate, Collections.emptySet()).getRaml();
		
		RamlVerifier verifier = new RamlVerifier(published, computed, Collections.emptyList(), null, Collections.singletonList(new ActionExistenceChecker()));
		assertTrue("Check that there are no errors and implementation matches raml", verifier.hasErrors());
		assertEquals("Check that implementation shuld have 1 errors", 1, verifier.getErrors().size());
		Issue errorIssue = verifier.getErrors().iterator().next();
		TestHelper.verifyIssue(IssueLocation.SOURCE, IssueSeverity.ERROR, IssueType.MISSING, ActionExistenceChecker.ACTION_MISSING, "POST /secondBase/endpointWithURIParam/{uriParam}", errorIssue);
		
		
		
		assertTrue("Check that there are no warnings and implementation matches raml", verifier.hasWarnings());
		assertEquals("Check that implementation shuld have 2 warnings", 2, verifier.getWarnings().size());
		TestHelper.verifyIssuesUnordered(verifier.getWarnings(), new Issue[] {
			new Issue(IssueSeverity.WARNING, IssueLocation.CONTRACT, IssueType.MISSING, ActionExistenceChecker.ACTION_MISSING, "GET /base/endpointWithGetAndPost"),
			new Issue(IssueSeverity.WARNING, IssueLocation.CONTRACT, IssueType.MISSING, ActionExistenceChecker.ACTION_MISSING, "POST /base/endpointWithGetAndPost")});
		
	}
	
	@Test
	public void test_ResourceExistenceChecker_MissingResourceInRaml() {
		Raml published = RamlVerifier.loadRamlFromFile("test-simple.raml");
		Class<?>[] classesToGenerate = new Class[] {VerifierTestController.class, SecondVerifierTestController.class, ThirdVerifierTestController.class};
		Raml computed = generator.generateRamlForClasses("test", "0.0.1", "/", classesToGenerate, Collections.emptySet()).getRaml();
		
		RamlVerifier verifier = new RamlVerifier(published, computed, Collections.singletonList(new ResourceExistenceChecker()), null, null);
		assertFalse("Check that there are no errors since the missing resource will get marked as a warning", verifier.hasErrors());
		assertTrue("Check that there are warnings since there are more resources implemented than declared in raml", verifier.hasWarnings());
		assertEquals("Check that implementation should have 1 warnings", 1, verifier.getWarnings().size());
		Iterator<Issue> iterator = verifier.getWarnings().iterator();
		TestHelper.verifyIssue(IssueLocation.CONTRACT, IssueSeverity.WARNING, IssueType.MISSING, ResourceExistenceChecker.RESOURCE_MISSING, "/thirdBase", iterator.next());
		
	}
	
	@Test
	public void test_ResourceExistenceChecker_MissingResourceInImplementation() {
		Raml published = RamlVerifier.loadRamlFromFile("test-simple.raml");
		Class<?>[] classesToGenerate = new Class[] {VerifierTestController.class};
		Raml computed = generator.generateRamlForClasses("test", "0.0.1", "/", classesToGenerate, Collections.emptySet()).getRaml();
		
		RamlVerifier verifier = new RamlVerifier(published, computed, Collections.singletonList(new ResourceExistenceChecker()), null, null);
		assertTrue("Check that there are errors since there is part of our contract not implemented", verifier.hasErrors());
		assertEquals("Check that implementation should have 1 error", 1, verifier.getErrors().size());
		Iterator<Issue> iterator = verifier.getErrors().iterator();
		TestHelper.verifyIssue(IssueLocation.SOURCE, IssueSeverity.ERROR, IssueType.MISSING, ResourceExistenceChecker.RESOURCE_MISSING, "/secondBase", iterator.next());
		assertFalse("Check that there are no warnings", verifier.hasWarnings());
		
	}
	
	@Test
	public void test_ResourceExistenceChecker_UriParamAlwaysWarning() {
		Raml published = RamlVerifier.loadRamlFromFile("test-uriparam.raml");
		Class<?>[] classesToGenerate = new Class[] {SecondVerifierTestController.class};
		Raml computed = generator.generateRamlForClasses("test", "0.0.1", "/", classesToGenerate, Collections.emptySet()).getRaml();
		
		RamlVerifier verifier = new RamlVerifier(published, computed, Collections.singletonList(new ResourceExistenceChecker()), null, null);
		assertFalse("Check that there are no errors", verifier.hasErrors());
		assertTrue("Check that there is warning for UriParam", verifier.hasWarnings());
		assertEquals("Check that implementation should have 2 warnings", 2, verifier.getWarnings().size());
		TestHelper.verifyIssuesUnordered(verifier.getWarnings(), new Issue[] {
			new Issue(IssueSeverity.WARNING, IssueLocation.SOURCE, IssueType.MISSING, ResourceExistenceChecker.RESOURCE_MISSING, "/secondBase/endpointWithURIParam/{differentNameParam}"),
			new Issue(IssueSeverity.WARNING, IssueLocation.CONTRACT, IssueType.MISSING, ResourceExistenceChecker.RESOURCE_MISSING, "/secondBase/endpointWithURIParam/{uriParam}")});
	}

}
