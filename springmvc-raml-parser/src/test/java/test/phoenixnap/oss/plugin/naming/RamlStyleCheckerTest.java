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

import static org.junit.Assert.assertFalse;

import java.util.Collections;

import org.junit.Test;
import org.raml.model.Raml;

import test.phoenixnap.oss.plugin.naming.testclasses.SecondVerifierTestController;
import test.phoenixnap.oss.plugin.naming.testclasses.VerifierTestController;

import com.phoenixnap.oss.ramlapisync.generation.RamlGenerator;
import com.phoenixnap.oss.ramlapisync.generation.RamlVerifier;
import com.phoenixnap.oss.ramlapisync.parser.SpringMvcResourceParser;
import com.phoenixnap.oss.ramlapisync.style.ResourceUrlStyleChecker;

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
	public void test_ResourceExistenceChecker_Success() {
		Raml published = RamlVerifier.loadRamlFromFile("test-style-success.raml");
		Class<?>[] classesToGenerate = new Class[] {VerifierTestController.class, SecondVerifierTestController.class};
		Raml computed = generator.generateRamlForClasses("test", "0.0.1", "/", classesToGenerate, Collections.emptySet()).getRaml();
		
		RamlVerifier verifier = new RamlVerifier(published, computed, Collections.emptyList(), Collections.singletonList(new ResourceUrlStyleChecker()));
		assertFalse("Check that raml passes rules", verifier.hasErrors());
		assertFalse("Check that implementation matches rules", verifier.hasWarnings());
		
	}
	
	

}
