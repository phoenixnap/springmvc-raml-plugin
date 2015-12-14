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

import test.phoenixnap.oss.plugin.naming.testclasses.SecondVerifierTestController;
import test.phoenixnap.oss.plugin.naming.testclasses.ThirdVerifierTestController;
import test.phoenixnap.oss.plugin.naming.testclasses.VerifierTestController;

import com.phoenixnap.oss.ramlapisync.generation.RamlGenerator;
import com.phoenixnap.oss.ramlapisync.generation.RamlVerifier;
import com.phoenixnap.oss.ramlapisync.parser.SpringMvcResourceParser;
import com.phoenixnap.oss.ramlapisync.verification.ResourceExistenceChecker;

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
		
		RamlVerifier verifier = new RamlVerifier(published, computed, Collections.singletonList(new ResourceExistenceChecker()));
		assertFalse("Check that there are no errors and implementation matches raml", verifier.hasErrors());
		assertFalse("Check that there are no warnings and implementation matches raml", verifier.hasWarnings());
		
	}
	
	@Test
	public void test_ResourceExistenceChecker_MissingResourceInRaml() {
		Raml published = RamlVerifier.loadRamlFromFile("test-simple.raml");
		Class<?>[] classesToGenerate = new Class[] {VerifierTestController.class, SecondVerifierTestController.class, ThirdVerifierTestController.class};
		Raml computed = generator.generateRamlForClasses("test", "0.0.1", "/", classesToGenerate, Collections.emptySet()).getRaml();
		
		RamlVerifier verifier = new RamlVerifier(published, computed, Collections.singletonList(new ResourceExistenceChecker()));
		assertFalse("Check that there are no errors since the missing resource will get marked as a warning", verifier.hasErrors());
		assertTrue("Check that there are warnings since there are more resources implemented than declared in raml", verifier.hasWarnings());
		
	}
	
	@Test
	public void test_ResourceExistenceChecker_MissingResourceInImplementation() {
		Raml published = RamlVerifier.loadRamlFromFile("test-simple.raml");
		Class<?>[] classesToGenerate = new Class[] {VerifierTestController.class};
		Raml computed = generator.generateRamlForClasses("test", "0.0.1", "/", classesToGenerate, Collections.emptySet()).getRaml();
		
		RamlVerifier verifier = new RamlVerifier(published, computed, Collections.singletonList(new ResourceExistenceChecker()));
		assertTrue("Check that there are errors since there is part of our contract not implemented", verifier.hasErrors());
		assertFalse("Check that there are no warnings", verifier.hasWarnings());
		
	}
	
	@Test
	public void test_ResourceExistenceChecker_UriParamAlwaysWarning() {
		Raml published = RamlVerifier.loadRamlFromFile("test-uriparam.raml");
		Class<?>[] classesToGenerate = new Class[] {SecondVerifierTestController.class};
		Raml computed = generator.generateRamlForClasses("test", "0.0.1", "/", classesToGenerate, Collections.emptySet()).getRaml();
		
		RamlVerifier verifier = new RamlVerifier(published, computed, Collections.singletonList(new ResourceExistenceChecker()));
		assertFalse("Check that there are no errors", verifier.hasErrors());
		assertTrue("Check that there is warning for UriParam", verifier.hasWarnings());
		
	}

}
