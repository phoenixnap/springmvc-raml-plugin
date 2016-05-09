/*
 * Copyright 2002-2016 the original author or authors.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package test.phoenixnap.oss.plugin.naming;


import com.phoenixnap.oss.ramlapisync.generation.RamlGenerator;
import com.phoenixnap.oss.ramlapisync.generation.RamlVerifier;
import com.phoenixnap.oss.ramlapisync.parser.ResourceParser;
import com.phoenixnap.oss.ramlapisync.parser.SpringMvcResourceParser;
import com.phoenixnap.oss.ramlapisync.verification.Issue;
import com.phoenixnap.oss.ramlapisync.verification.IssueLocation;
import com.phoenixnap.oss.ramlapisync.verification.IssueSeverity;
import com.phoenixnap.oss.ramlapisync.verification.IssueType;
import com.phoenixnap.oss.ramlapisync.verification.checkers.*;
import org.junit.Test;
import org.raml.model.Raml;
import test.phoenixnap.oss.plugin.naming.testclasses.*;

import java.util.Collections;
import java.util.Iterator;

import static org.junit.Assert.*;



/**
 * Unit tests for the RamlVerifier class and associated Checkers
 * 
 * @author Kurt Paris
 * @since 0.0.2
 *
 */
public class RamlVerifierTest {

	SpringMvcResourceParser parser = new SpringMvcResourceParser(null, "0.0.1", ResourceParser.CATCH_ALL_MEDIA_TYPE, false);
	RamlGenerator generator = new RamlGenerator(parser);



	@Test
	public void test_ResourceExistenceChecker_Success() {
		Raml published = RamlVerifier.loadRamlFromFile("test-simple.raml");
		Class<?>[] classesToGenerate = new Class[] { VerifierTestController.class, SecondVerifierTestController.class };
		Raml computed = generator.generateRamlForClasses("test", "0.0.1", "/", classesToGenerate, Collections.emptySet(), null).getRaml();

		RamlVerifier verifier = new RamlVerifier(published, computed, Collections.singletonList(new ResourceExistenceChecker()), null, null);
		assertFalse("Check that there are no errors and implementation matches raml", verifier.hasErrors());
		assertFalse("Check that there are no warnings and implementation matches raml", verifier.hasWarnings());

	}


	@Test
	public void test_ResourceExistenceChecker_DuplicateCommand() {
		Raml published = RamlVerifier.loadRamlFromFile("test-duplicatecommand.raml");
		Class<?>[] classesToGenerate = new Class[] { StyleCheckResourceDuplicateCommand.class, StyleCheckResourceDuplicateCommandSecond.class };
		Raml computed = generator.generateRamlForClasses("test", "0.0.1", "/", classesToGenerate, Collections.emptySet(), null).getRaml();

		RamlVerifier verifier = new RamlVerifier(published, computed, Collections.singletonList(new ResourceExistenceChecker()), null, null);
		assertFalse("Check that there are no errors and implementation matches raml", verifier.hasErrors());
		assertFalse("Check that there are no warnings and implementation matches raml", verifier.hasWarnings());

	}


	@Test
	public void test_ActionQueryParameterChecker_Success() {
		Raml published = RamlVerifier.loadRamlFromFile("test-queryparam-success.raml");
		Class<?>[] classesToGenerate = new Class[] { ParamTestController.class };
		Raml computed = generator.generateRamlForClasses("test", "0.0.1", "/", classesToGenerate, Collections.emptySet(), null).getRaml();

		RamlVerifier verifier = new RamlVerifier(published, computed, Collections.emptyList(), Collections.singletonList(new ActionQueryParameterChecker()), null);
		assertFalse("Check that there are no errors and implementation matches raml", verifier.hasErrors());
		assertFalse("Check that there are no warnings and implementation matches raml", verifier.hasWarnings());

	}


	@Test
	public void test_ActionContentTypeChecker_Success() {
		Raml published = RamlVerifier.loadRamlFromFile("test-contenttype-success.raml");
		Class<?>[] classesToGenerate = new Class[] { ContentTypeTestController.class };
		Raml computed = generator.generateRamlForClasses("test", "0.0.1", "/", classesToGenerate, Collections.emptySet(), null).getRaml();

		RamlVerifier verifier = new RamlVerifier(published, computed, Collections.emptyList(), Collections.singletonList(new ActionContentTypeChecker()), null);
		assertFalse("Check that there are no errors and implementation matches raml", verifier.hasErrors());
		assertFalse("Check that there are no warnings and implementation matches raml", verifier.hasWarnings());

	}


	@Test
	public void test_ActionMultiContentTypeChecker_Success() {
		Raml published = RamlVerifier.loadRamlFromFile("test-multicontenttype-success.raml");
		Class<?>[] classesToGenerate = new Class[] { MultiContentTypeTestController.class };
		Raml computed = generator.generateRamlForClasses("test", "0.0.1", "/", classesToGenerate, Collections.emptySet(), null).getRaml();

		RamlVerifier verifier = new RamlVerifier(published, computed, Collections.emptyList(), Collections.singletonList(new ActionContentTypeChecker()), null);
		assertFalse("Check that there are no errors and implementation matches raml", verifier.hasErrors());
		assertFalse("Check that there are no warnings and implementation matches raml", verifier.hasWarnings());

	}


	@Test
	public void test_ActionResponseBodySchemaChecker_Success() {
		Raml published = RamlVerifier.loadRamlFromFile("test-responsebody-success.raml");
		Class<?>[] classesToGenerate = new Class[] { ResponseBodyTestController.class };
		Raml computed = generator.generateRamlForClasses("test", "0.0.1", "/", classesToGenerate, Collections.emptySet(), null).getRaml();

		RamlVerifier verifier = new RamlVerifier(published, computed, Collections.emptyList(), Collections.singletonList(new ActionResponseBodySchemaChecker()), null);
		assertFalse("Check that there are no errors and implementation matches raml", verifier.hasErrors());
		assertFalse("Check that there are no warnings and implementation matches raml", verifier.hasWarnings());

	}


	@Test
	public void test_ActionContentTypeChecker_WarningDifferentType() {
		Raml published = RamlVerifier.loadRamlFromFile("test-responsebody-differenttype.raml");
		Class<?>[] classesToGenerate = new Class[] { ResponseBodyTestController.class };
		Raml computed = generator.generateRamlForClasses("test", "0.0.1", "/", classesToGenerate, Collections.emptySet(), null).getRaml();

		RamlVerifier verifier = new RamlVerifier(published, computed, Collections.emptyList(), Collections.singletonList(new ActionResponseBodySchemaChecker()), null);
		assertFalse("Check that there are errors", verifier.hasErrors());
		assertTrue("Check that there are warnings", verifier.hasWarnings());
		assertEquals("Check that implementation should have 1 warnings", 1, verifier.getWarnings().size());

		Issue warnIssue = verifier.getWarnings().iterator().next();
		TestHelper.verifyIssue(IssueLocation.SOURCE, IssueSeverity.WARNING, IssueType.DIFFERENT, ActionResponseBodySchemaChecker.RESPONSE_BODY_FIELDDIFFERENT, "element2 : POST /base/endpointWithResponseType", warnIssue);

	}


	@Test
	public void test_ActionContentTypeChecker_ErrorMissingFieldInRaml() {
		Raml published = RamlVerifier.loadRamlFromFile("test-responsebody-missing.raml");
		Class<?>[] classesToGenerate = new Class[] { ResponseBodyTestController.class };
		Raml computed = generator.generateRamlForClasses("test", "0.0.1", "/", classesToGenerate, Collections.emptySet(), null).getRaml();

		RamlVerifier verifier = new RamlVerifier(published, computed, Collections.emptyList(), Collections.singletonList(new ActionResponseBodySchemaChecker()), null);
		assertTrue("Check that there are errors", verifier.hasErrors());
		assertFalse("Check that there are warnings", verifier.hasWarnings());
		assertEquals("Check that implementation should have 1 errors", 1, verifier.getErrors().size());

		Issue errorIssue = verifier.getErrors().iterator().next();
		TestHelper.verifyIssue(IssueLocation.CONTRACT, IssueSeverity.ERROR, IssueType.MISSING, ActionResponseBodySchemaChecker.RESPONSE_BODY_FIELDMISSING, "element3 : POST /base/endpointWithResponseType", errorIssue);

	}


	@Test
	public void test_ActionContentTypeChecker_ErrorMissingFieldInSource() {
		Raml published = RamlVerifier.loadRamlFromFile("test-responsebody-success.raml");
		Class<?>[] classesToGenerate = new Class[] { ResponseBodyTestControllerError.class };
		Raml computed = generator.generateRamlForClasses("test", "0.0.1", "/", classesToGenerate, Collections.emptySet(), null).getRaml();

		RamlVerifier verifier = new RamlVerifier(published, computed, Collections.emptyList(), Collections.singletonList(new ActionResponseBodySchemaChecker()), null);
		assertTrue("Check that there are errors", verifier.hasErrors());
		assertFalse("Check that there are warnings", verifier.hasWarnings());
		assertEquals("Check that implementation should have 1 errors", 1, verifier.getErrors().size());

		Issue errorIssue = verifier.getErrors().iterator().next();
		TestHelper.verifyIssue(IssueLocation.SOURCE, IssueSeverity.ERROR, IssueType.MISSING, ActionResponseBodySchemaChecker.RESPONSE_BODY_FIELDMISSING, "element3 : POST /base/endpointWithResponseType", errorIssue);

	}


	@Test
	public void test_ActionQueryParameterChecker_SuccessWithPostWarning() {
		Raml published = RamlVerifier.loadRamlFromFile("test-queryparam-success.raml");
		Class<?>[] classesToGenerate = new Class[] { ParamTestController.class, ParamTestControllerPostWarning.class };
		Raml computed = generator.generateRamlForClasses("test", "0.0.1", "/", classesToGenerate, Collections.emptySet(), null).getRaml();

		RamlVerifier verifier = new RamlVerifier(published, computed, Collections.emptyList(), Collections.singletonList(new ActionQueryParameterChecker()), null);
		assertFalse("Check that there are no errors and implementation matches raml", verifier.hasErrors());
		assertTrue("Check that there are warnings", verifier.hasWarnings());
		assertEquals("Check that implementation shuld have 2 warnings", 2, verifier.getWarnings().size());
		TestHelper.verifyIssuesUnordered(verifier.getWarnings(), new Issue[] {
				new Issue(IssueSeverity.WARNING, IssueLocation.SOURCE, IssueType.MISSING, ActionQueryParameterChecker.QUERY_PARAMETER_FOUND_IN_FORM, "param5 : POST /base/endpointWithURIParam/{uriParam}"),
				new Issue(IssueSeverity.WARNING, IssueLocation.SOURCE, IssueType.MISSING, ActionQueryParameterChecker.QUERY_PARAMETER_FOUND_IN_FORM, "param6 : POST /base/endpointWithURIParam/{uriParam}") });


	}


	@Test
	public void test_ActionQueryParameterChecker_MissingParamsInContract() {
		Raml published = RamlVerifier.loadRamlFromFile("test-queryparam-missing.raml");
		Class<?>[] classesToGenerate = new Class[] { ParamTestController.class };
		Raml computed = generator.generateRamlForClasses("test", "0.0.1", "/", classesToGenerate, Collections.emptySet(), null).getRaml();

		RamlVerifier verifier = new RamlVerifier(published, computed, Collections.emptyList(), Collections.singletonList(new ActionQueryParameterChecker()), null);
		assertTrue("Check that there are errors", verifier.hasErrors());
		assertTrue("Check that there are warnings", verifier.hasWarnings());
		assertEquals("Check that implementation should have 1 warnings", 1, verifier.getWarnings().size());
		assertEquals("Check that implementation should have 1 errors", 1, verifier.getErrors().size());

		Issue warnIssue = verifier.getWarnings().iterator().next();
		TestHelper.verifyIssue(IssueLocation.CONTRACT, IssueSeverity.WARNING, IssueType.MISSING, ActionQueryParameterChecker.QUERY_PARAMETER_MISSING, "param4 : GET /base/endpointWithURIParam/{uriParam}", warnIssue);

		Issue errorIssue = verifier.getErrors().iterator().next();
		TestHelper.verifyIssue(IssueLocation.CONTRACT, IssueSeverity.ERROR, IssueType.MISSING, ActionQueryParameterChecker.QUERY_PARAMETER_MISSING, "param3 : GET /base/endpointWithURIParam/{uriParam}", errorIssue);

	}


	@Test
	public void test_ActionQueryParameterChecker_MissingParamsInSource() {
		Raml published = RamlVerifier.loadRamlFromFile("test-queryparam-success.raml");
		Class<?>[] classesToGenerate = new Class[] { ParamTestController.class, ParamTestControllerPostMissing.class };
		Raml computed = generator.generateRamlForClasses("test", "0.0.1", "/", classesToGenerate, Collections.emptySet(), null).getRaml();

		RamlVerifier verifier = new RamlVerifier(published, computed, Collections.emptyList(), Collections.singletonList(new ActionQueryParameterChecker()), null);
		assertTrue("Check that there are errors", verifier.hasErrors());
		assertFalse("Check that there are no warnings and implementation matches raml", verifier.hasWarnings());
		assertEquals("Check that implementation should have 2 errors", 2, verifier.getErrors().size());
		TestHelper.verifyIssuesUnordered(verifier.getErrors(), new Issue[] {
				new Issue(IssueSeverity.ERROR, IssueLocation.SOURCE, IssueType.MISSING, ActionQueryParameterChecker.QUERY_PARAMETER_MISSING, "param5 : POST /base/endpointWithURIParam/{uriParam}"),
				new Issue(IssueSeverity.ERROR, IssueLocation.SOURCE, IssueType.MISSING, ActionQueryParameterChecker.QUERY_PARAMETER_MISSING, "param6 : POST /base/endpointWithURIParam/{uriParam}") });


	}


	@Test
	public void test_ActionQueryParameterChecker_MissingParamsNotRequiredDowngradeToWarning() {
		Raml published = RamlVerifier.loadRamlFromFile("test-queryparam-success.raml");
		Class<?>[] classesToGenerate = new Class[] { ParamTestController.class, ParamTestControllerDowngradeToWarning.class };
		Raml computed = generator.generateRamlForClasses("test", "0.0.1", "/", classesToGenerate, Collections.emptySet(), null).getRaml();

		RamlVerifier verifier = new RamlVerifier(published, computed, Collections.emptyList(), Collections.singletonList(new ActionQueryParameterChecker()), null);
		assertTrue("Check that there are errors", verifier.hasErrors());
		assertTrue("Check that there are warnings", verifier.hasWarnings());
		assertEquals("Check that implementation should have 2 errors", 3, verifier.getErrors().size());
		assertEquals("Check that implementation should have 1 warning", 1, verifier.getWarnings().size());
		TestHelper.verifyIssuesUnordered(verifier.getErrors(), new Issue[] {
				new Issue(IssueSeverity.ERROR, IssueLocation.SOURCE, IssueType.MISSING, ActionQueryParameterChecker.QUERY_PARAMETER_MISSING, "param5 : POST /base/endpointWithURIParam/{uriParam}"),
				new Issue(IssueSeverity.ERROR, IssueLocation.SOURCE, IssueType.MISSING, ActionQueryParameterChecker.QUERY_PARAMETER_MISSING, "param6 : POST /base/endpointWithURIParam/{uriParam}"),
				new Issue(IssueSeverity.ERROR, IssueLocation.SOURCE, IssueType.MISSING, ActionQueryParameterChecker.QUERY_PARAMETER_MISSING, "param3 : GET /base/endpointWithURIParam/{uriParam}") });

		Issue warnIssue = verifier.getWarnings().iterator().next();
		TestHelper.verifyIssue(IssueLocation.SOURCE, IssueSeverity.WARNING, IssueType.MISSING, ActionQueryParameterChecker.QUERY_PARAMETER_MISSING, "param4 : GET /base/endpointWithURIParam/{uriParam}", warnIssue);


	}


	@Test
	public void test_ActionExistenceChecker_Success() {
		Raml published = RamlVerifier.loadRamlFromFile("test-simple.raml");
		Class<?>[] classesToGenerate = new Class[] { VerifierTestController.class, SecondVerifierTestController.class };
		Raml computed = generator.generateRamlForClasses("test", "0.0.1", "/", classesToGenerate, Collections.emptySet(), null).getRaml();

		RamlVerifier verifier = new RamlVerifier(published, computed, Collections.emptyList(), null, Collections.singletonList(new ActionExistenceChecker()));
		assertFalse("Check that there are no errors and implementation matches raml", verifier.hasErrors());
		assertFalse("Check that there are no warnings and implementation matches raml", verifier.hasWarnings());

	}


	@Test
	public void test_ActionExistenceChecker_MissingAndExtraActions() {
		Raml published = RamlVerifier.loadRamlFromFile("test-actions-missing.raml");
		Class<?>[] classesToGenerate = new Class[] { VerifierTestController.class, SecondVerifierTestController.class };
		Raml computed = generator.generateRamlForClasses("test", "0.0.1", "/", classesToGenerate, Collections.emptySet(), null).getRaml();

		RamlVerifier verifier = new RamlVerifier(published, computed, Collections.emptyList(), null, Collections.singletonList(new ActionExistenceChecker()));
		assertTrue("Check that there are no errors and implementation matches raml", verifier.hasErrors());
		assertEquals("Check that implementation shuld have 1 errors", 1, verifier.getErrors().size());
		Issue errorIssue = verifier.getErrors().iterator().next();
		TestHelper.verifyIssue(IssueLocation.SOURCE, IssueSeverity.ERROR, IssueType.MISSING, ActionExistenceChecker.ACTION_MISSING, "POST /secondBase/endpointWithURIParam/{uriParam}", errorIssue);


		assertTrue("Check that there are no warnings and implementation matches raml", verifier.hasWarnings());
		assertEquals("Check that implementation shuld have 2 warnings", 2, verifier.getWarnings().size());
		TestHelper.verifyIssuesUnordered(verifier.getWarnings(), new Issue[] {
				new Issue(IssueSeverity.WARNING, IssueLocation.CONTRACT, IssueType.MISSING, ActionExistenceChecker.ACTION_MISSING, "GET /base/endpointWithGetAndPost"),
				new Issue(IssueSeverity.WARNING, IssueLocation.CONTRACT, IssueType.MISSING, ActionExistenceChecker.ACTION_MISSING, "POST /base/endpointWithGetAndPost") });

	}


	@Test
	public void test_ResourceExistenceChecker_MissingResourceInRaml() {
		Raml published = RamlVerifier.loadRamlFromFile("test-simple.raml");
		Class<?>[] classesToGenerate = new Class[] { VerifierTestController.class, SecondVerifierTestController.class, ThirdVerifierTestController.class };
		Raml computed = generator.generateRamlForClasses("test", "0.0.1", "/", classesToGenerate, Collections.emptySet(), null).getRaml();

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
		Class<?>[] classesToGenerate = new Class[] { VerifierTestController.class };
		Raml computed = generator.generateRamlForClasses("test", "0.0.1", "/", classesToGenerate, Collections.emptySet(), null).getRaml();

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
		Class<?>[] classesToGenerate = new Class[] { SecondVerifierTestController.class };
		Raml computed = generator.generateRamlForClasses("test", "0.0.1", "/", classesToGenerate, Collections.emptySet(), null).getRaml();

		RamlVerifier verifier = new RamlVerifier(published, computed, Collections.singletonList(new ResourceExistenceChecker()), null, null);
		assertFalse("Check that there are no errors", verifier.hasErrors());
		assertTrue("Check that there is warning for UriParam", verifier.hasWarnings());
		assertEquals("Check that implementation should have 2 warnings", 2, verifier.getWarnings().size());
		TestHelper.verifyIssuesUnordered(verifier.getWarnings(), new Issue[] {
				new Issue(IssueSeverity.WARNING, IssueLocation.SOURCE, IssueType.MISSING, ResourceExistenceChecker.RESOURCE_MISSING, "/secondBase/endpointWithURIParam/{differentNameParam}"),
				new Issue(IssueSeverity.WARNING, IssueLocation.CONTRACT, IssueType.MISSING, ResourceExistenceChecker.RESOURCE_MISSING, "/secondBase/endpointWithURIParam/{uriParam}") });
	}

}
