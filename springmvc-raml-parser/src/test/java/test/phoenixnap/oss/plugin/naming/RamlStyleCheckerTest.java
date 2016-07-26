/*
 * Copyright 2002-2016 the original author or authors.
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

import com.phoenixnap.oss.ramlapisync.generation.RamlGenerator;
import com.phoenixnap.oss.ramlapisync.generation.RamlVerifier;
import com.phoenixnap.oss.ramlapisync.parser.SpringMvcResourceParser;
import com.phoenixnap.oss.ramlapisync.raml.RamlRoot;
import com.phoenixnap.oss.ramlapisync.style.checkers.ActionSecurityResponseChecker;
import com.phoenixnap.oss.ramlapisync.style.checkers.RequestBodySchemaStyleChecker;
import com.phoenixnap.oss.ramlapisync.style.checkers.ResourceCollectionPluralisationChecker;
import com.phoenixnap.oss.ramlapisync.style.checkers.ResourceUrlStyleChecker;
import com.phoenixnap.oss.ramlapisync.style.checkers.ResponseBodySchemaStyleChecker;
import com.phoenixnap.oss.ramlapisync.style.checkers.ResponseCodeDefinitionStyleChecker;
import com.phoenixnap.oss.ramlapisync.verification.Issue;
import com.phoenixnap.oss.ramlapisync.verification.IssueLocation;
import com.phoenixnap.oss.ramlapisync.verification.IssueSeverity;
import com.phoenixnap.oss.ramlapisync.verification.IssueType;
import org.junit.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

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
		RamlRoot published = RamlVerifier.loadRamlFromFile("test-style-success.raml");
		
		RamlVerifier verifier = new RamlVerifier(published, null, Collections.emptyList(), null, null, Collections.singletonList(new ResourceUrlStyleChecker()));
		assertFalse("Check that raml passes rules", verifier.hasErrors());
		assertFalse("Check that implementation matches rules", verifier.hasWarnings());
		
	}
	
	@Test
	public void test_PluralChecker_Success() {
		RamlRoot published = RamlVerifier.loadRamlFromFile("test-style-pluralised-success.raml");
		
		RamlVerifier verifier = new RamlVerifier(published, null, Collections.emptyList(), null, null, Collections.singletonList(new ResourceCollectionPluralisationChecker()));
		assertFalse("Check that raml passes rules", verifier.hasErrors());
		assertFalse("Check that implementation matches rules", verifier.hasWarnings());
		
	}
	
	@Test
	public void test_PluralChecker_Fails() {
		RamlRoot published = RamlVerifier.loadRamlFromFile("test-style-pluralised-singularcollection.raml");
		
		RamlVerifier verifier = new RamlVerifier(published, null, Collections.emptyList(), null, null, Collections.singletonList(new ResourceCollectionPluralisationChecker()));
		assertFalse("Check that raml passes rules", verifier.hasErrors());
		assertTrue("Check that implementation matches rules", verifier.hasWarnings());
		assertEquals("Check that implementation shuld have 2 warnings", 2, verifier.getWarnings().size());
		TestHelper.verifyIssuesUnordered(verifier.getWarnings(), new Issue[] {
			new Issue(IssueSeverity.WARNING, IssueLocation.CONTRACT, IssueType.STYLE, ResourceCollectionPluralisationChecker.DESCRIPTION, "/ignored/house"),
			new Issue(IssueSeverity.WARNING, IssueLocation.CONTRACT, IssueType.STYLE, ResourceCollectionPluralisationChecker.DESCRIPTION, "/ignored/ball")});
		
	}
	
	@Test
	public void test_ActionSecurityResponseCheck_Success() {
		RamlRoot published = RamlVerifier.loadRamlFromFile("test-style-actionsecurity-success.raml");
		
		RamlVerifier verifier = new RamlVerifier(published, null, Collections.emptyList(), null, null, Collections.singletonList(new ActionSecurityResponseChecker()));
		assertFalse("Check that raml passes rules", verifier.hasErrors());
		assertFalse("Check that implementation matches rules", verifier.hasWarnings());
	}
	
	@Test
	public void test_ActionSecurityResponseCheck_Fails() {
		RamlRoot published = RamlVerifier.loadRamlFromFile("test-style-actionsecurity-fails.raml");
		
		RamlVerifier verifier = new RamlVerifier(published, null, Collections.emptyList(), null, null, Collections.singletonList(new ActionSecurityResponseChecker()));
		assertFalse("Check that raml passes rules", verifier.hasErrors());
		assertTrue("Check that implementation matches rules", verifier.hasWarnings());
		assertEquals("Check that implementation shuld have 2 warnings", 2, verifier.getWarnings().size());
		TestHelper.verifyIssuesUnordered(verifier.getWarnings(), new Issue[] {
			new Issue(IssueSeverity.WARNING, IssueLocation.CONTRACT, IssueType.STYLE, ActionSecurityResponseChecker.DESCRIPTION, "GET /ignored/houses/{houseId}"),
			new Issue(IssueSeverity.WARNING, IssueLocation.CONTRACT, IssueType.STYLE, ActionSecurityResponseChecker.DESCRIPTION, "GET /ignored/houses")});
	}
	
	@Test
	public void test_RequestBodyCheck_Success() {
		RamlRoot published = RamlVerifier.loadRamlFromFile("test-style-missing-request-bodies.raml");
		
		RamlVerifier verifier = new RamlVerifier(published, null, Collections.emptyList(), null, null, Collections.singletonList(new RequestBodySchemaStyleChecker("PUT,POST")));
		assertFalse("Check that raml passes rules", verifier.hasErrors());
		assertFalse("Check that implementation matches rules", verifier.hasWarnings());
		
		//Check string parse resilience
		verifier = new RamlVerifier(published, null, Collections.emptyList(), null, null, Collections.singletonList(new RequestBodySchemaStyleChecker("PUT,  ,POSTIX,  POST  ,, ")));
		assertFalse("Check that raml passes rules", verifier.hasErrors());
		assertFalse("Check that implementation matches rules", verifier.hasWarnings());
		
		verifier = new RamlVerifier(published, null, Collections.emptyList(), null, null, Collections.singletonList(new RequestBodySchemaStyleChecker(null)));
		assertFalse("Check that raml passes rules", verifier.hasErrors());
		assertFalse("Check that implementation matches rules", verifier.hasWarnings());
	}
	
	@Test
	public void test_RequestBodyCheck_Fails() {
		RamlRoot published = RamlVerifier.loadRamlFromFile("test-style-missing-request-bodies-fails.raml");
		
		RamlVerifier verifier = new RamlVerifier(published, null, Collections.emptyList(), null, null, Collections.singletonList(new RequestBodySchemaStyleChecker("PUT,POST")));
		assertFalse("Check that raml passes rules", verifier.hasErrors());
		assertTrue("Check that implementation matches rules", verifier.hasWarnings());
		assertEquals("Check that implementation shuld have 2 warnings", 2, verifier.getWarnings().size());
		TestHelper.verifyIssuesUnordered(verifier.getWarnings(), new Issue[] {
			new Issue(IssueSeverity.WARNING, IssueLocation.CONTRACT, IssueType.STYLE, String.format(RequestBodySchemaStyleChecker.DESCRIPTION, "PUT"), "PUT /base/endpointThatWillBoom"),
			new Issue(IssueSeverity.WARNING, IssueLocation.CONTRACT, IssueType.STYLE, String.format(RequestBodySchemaStyleChecker.DESCRIPTION, "POST"), "POST /base/endpointThatWillBoom")});
	}
	
	@Test
	public void test_ResponseBodyCheck_Success() {
		RamlRoot published = RamlVerifier.loadRamlFromFile("test-style-missing-response-bodies.raml");
		
		RamlVerifier verifier = new RamlVerifier(published, null, Collections.emptyList(), null, null, Collections.singletonList(new ResponseBodySchemaStyleChecker("GET,POST")));
		assertFalse("Check that raml passes rules", verifier.hasErrors());
		assertFalse("Check that implementation matches rules", verifier.hasWarnings());
		
		//Check string parse resilience
		verifier = new RamlVerifier(published, null, Collections.emptyList(), null, null, Collections.singletonList(new ResponseBodySchemaStyleChecker("GET,  ,POSTIX,  POST  ,, ")));
		assertFalse("Check that raml passes rules", verifier.hasErrors());
		assertFalse("Check that implementation matches rules", verifier.hasWarnings());
		
		verifier = new RamlVerifier(published, null, Collections.emptyList(), null, null, Collections.singletonList(new ResponseBodySchemaStyleChecker(null)));
		assertFalse("Check that raml passes rules", verifier.hasErrors());
		assertFalse("Check that implementation matches rules", verifier.hasWarnings());
	}
	
	@Test
	public void test_ResponseBodyCheck_Fails() {
		RamlRoot published = RamlVerifier.loadRamlFromFile("test-style-missing-response-bodies-fails.raml");
		
		RamlVerifier verifier = new RamlVerifier(published, null, Collections.emptyList(), null, null, Collections.singletonList(new ResponseBodySchemaStyleChecker("GET,POST")));
		assertFalse("Check that raml passes rules", verifier.hasErrors());
		assertTrue("Check that implementation matches rules", verifier.hasWarnings());
		assertEquals("Check that implementation shuld have 2 warnings", 2, verifier.getWarnings().size());
		TestHelper.verifyIssuesUnordered(verifier.getWarnings(), new Issue[] {
			new Issue(IssueSeverity.WARNING, IssueLocation.CONTRACT, IssueType.STYLE, String.format(ResponseBodySchemaStyleChecker.DESCRIPTION, "GET"), "GET /base/endpointThatWillBoom"),
			new Issue(IssueSeverity.WARNING, IssueLocation.CONTRACT, IssueType.STYLE, String.format(ResponseBodySchemaStyleChecker.DESCRIPTION, "POST"), "POST /base/endpointThatWillBoom")});
	}
	
	@Test
	public void test_Get404Check_Success() {
		RamlRoot published = RamlVerifier.loadRamlFromFile("test-style-missing-get404.raml");
		MultiValueMap<String, HttpStatus> statusChecks = new LinkedMultiValueMap<>();
		statusChecks.add(HttpMethod.GET.name(), HttpStatus.NOT_FOUND);
		RamlVerifier verifier = new RamlVerifier(published, null, Collections.emptyList(), null, null, Collections.singletonList(new ResponseCodeDefinitionStyleChecker(statusChecks)));
		assertFalse("Check that raml passes rules", verifier.hasErrors());
		assertFalse("Check that implementation matches rules", verifier.hasWarnings());
	}
	
	

	@Test
	public void test_Get404Check_Fails() {
		RamlRoot published = RamlVerifier.loadRamlFromFile("test-style-missing-get404-fails.raml");
		MultiValueMap<String, HttpStatus> statusChecks = new LinkedMultiValueMap<>();
		HttpStatus status = HttpStatus.NOT_FOUND;
		statusChecks.add(HttpMethod.GET.name(), status);
		RamlVerifier verifier = new RamlVerifier(published, null, Collections.emptyList(), null, null, Collections.singletonList(new ResponseCodeDefinitionStyleChecker(statusChecks)));
		
		assertFalse("Check that raml passes rules", verifier.hasErrors());
		assertTrue("Check that implementation matches rules", verifier.hasWarnings());
		assertEquals("Check that implementation shuld have 1 warnings", 1, verifier.getWarnings().size());
		TestHelper.verifyIssuesUnordered(verifier.getWarnings(), new Issue[] {
			new Issue(IssueSeverity.WARNING, IssueLocation.CONTRACT, IssueType.STYLE, String.format(ResponseCodeDefinitionStyleChecker.DESCRIPTION, "GET", status.name(), status.value()), "GET /base/endpointThatWillBoom")});
	}
	
	

}
