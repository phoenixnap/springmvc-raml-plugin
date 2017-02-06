/*
 * Copyright 2002-2017 the original author or authors.
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

import com.phoenixnap.oss.ramlapisync.data.ApiActionMetadata;
import com.phoenixnap.oss.ramlapisync.data.ApiParameterMetadata;
import com.phoenixnap.oss.ramlapisync.data.ApiResourceMetadata;
import com.phoenixnap.oss.ramlapisync.generation.RamlGenerator;
import com.phoenixnap.oss.ramlapisync.generation.RamlParser;
import com.phoenixnap.oss.ramlapisync.parser.ResourceParser;
import com.phoenixnap.oss.ramlapisync.parser.SpringMvcResourceParser;
import com.phoenixnap.oss.ramlapisync.raml.RamlRoot;
import org.junit.Test;

import java.util.Set;

import static org.junit.Assert.assertEquals;

/**
 * Unit tests for the RamlVerifier class and associated Checkers
 * 
 * @author Kurt Paris
 * @since 0.2.3
 *
 */
public class BugfixTest {

	SpringMvcResourceParser parser = new SpringMvcResourceParser(null, "0.0.1", ResourceParser.CATCH_ALL_MEDIA_TYPE, false);
	RamlGenerator generator = new RamlGenerator(parser);
	
	@Test
	public void test_Issue15_MissingQueryParameters() {
		RamlRoot loadRamlFromFile = RamlParser.loadRamlFromFile( "issue-15.raml" );
		RamlParser par = new RamlParser("com.gen.test"); 
		Set<ApiResourceMetadata> controllers = par.extractControllers(loadRamlFromFile);
		assertEquals("Expect 1 controller", 1, controllers.size());
		ApiResourceMetadata controller = controllers.iterator().next();
		assertEquals("Expect 1 api call in the controller", 1, controller.getApiCalls().size());
		ApiActionMetadata apiCall = controller.getApiCalls().iterator().next();
		assertEquals("Expect 1 request parameter", 1, apiCall.getRequestParameters().size());
		ApiParameterMetadata parameter = apiCall.getRequestParameters().iterator().next();
		assertEquals("Expect it to be the id", "id", parameter.getName());
		assertEquals("Expect it to be the id", String.class, parameter.getType());
		
	}

}
