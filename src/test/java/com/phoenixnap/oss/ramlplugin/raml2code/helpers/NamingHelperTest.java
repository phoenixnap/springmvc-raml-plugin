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
package com.phoenixnap.oss.ramlplugin.raml2code.helpers;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.phoenixnap.oss.ramlplugin.raml2code.plugin.TestConfig;

/**
 * Unit tests for the NamingHelper class
 * 
 * @author Kurt Paris
 * @since 0.0.1
 *
 */
public class NamingHelperTest {

	@Test
	public void test_getAllResourcesNames_Success() {

		String url = "/services/things";

		TestConfig.resetConfig();
		assertEquals("Should deal with depth=1", "Things", NamingHelper.getAllResourcesNames(url, false));
		assertEquals("Should deal with depth=1 and singularization", "Thing", NamingHelper.getAllResourcesNames(url, true));

		TestConfig.setResourceDepthInClassNames(-1);
		assertEquals("Should deal with unlimited depth", "ServicesThings", NamingHelper.getAllResourcesNames(url, false));
		assertEquals("Should deal with unlimited depth and singularization", "ServiceThing", NamingHelper.getAllResourcesNames(url, true));

		TestConfig.setResourceDepthInClassNames(2);
		assertEquals("Should deal with depth=2", "ServicesThings", NamingHelper.getAllResourcesNames(url, false));
		assertEquals("Should deal with depth=2 and singularization", "ServiceThing", NamingHelper.getAllResourcesNames(url, true));

		TestConfig.setResourceDepthInClassNames(-1);
		TestConfig.setReverseOrderInClassNames(Boolean.TRUE);
		assertEquals("Should deal with unlimited depth and reversed order", "ThingsServices",
				NamingHelper.getAllResourcesNames(url, false));
		assertEquals("Should deal with unlimited depth, singularization and reversed order", "ThingService",
				NamingHelper.getAllResourcesNames(url, true));

		TestConfig.setResourceDepthInClassNames(1);
		assertEquals("Should deal with depth=1 and reversed order", "Things", NamingHelper.getAllResourcesNames(url, false));
		assertEquals("Should deal with depth=1, singularization and reversed order", "Thing", NamingHelper.getAllResourcesNames(url, true));

		TestConfig.setResourceDepthInClassNames(2);
		assertEquals("Should deal with depth=2 and reversed order", "ThingsServices", NamingHelper.getAllResourcesNames(url, false));
		assertEquals("Should deal with depth=2, singularization and reversed order", "ThingService",
				NamingHelper.getAllResourcesNames(url, true));

		url = "/services/things/quotes";

		TestConfig.setResourceDepthInClassNames(-1);
		TestConfig.setResourceTopLevelInClassNames(1);
		TestConfig.setReverseOrderInClassNames(Boolean.FALSE);

		assertEquals("Should deal with unlimited depth and top-level=1", "ThingsQuotes", NamingHelper.getAllResourcesNames(url, false));
		assertEquals("Should deal with unlimited depth, top-level=1 and singularization", "ThingQuote",
				NamingHelper.getAllResourcesNames(url, true));

		TestConfig.setResourceDepthInClassNames(2);
		assertEquals("Should deal with depth=2 and top-level=1", "ThingsQuotes", NamingHelper.getAllResourcesNames(url, false));
		assertEquals("Should deal with depth=2, top-level=1 and singularization", "ThingQuote",
				NamingHelper.getAllResourcesNames(url, true));

		TestConfig.setResourceDepthInClassNames(-1);
		TestConfig.setReverseOrderInClassNames(Boolean.TRUE);
		assertEquals("Should deal with unlimited depth, top-level=1 and reversed order", "QuotesThings",
				NamingHelper.getAllResourcesNames(url, false));
		assertEquals("Should deal with unlimited depth, top-level=1, singularization and reversed order", "QuoteThing",
				NamingHelper.getAllResourcesNames(url, true));

		TestConfig.setResourceDepthInClassNames(2);
		assertEquals("Should deal with depth=2, top-level=1 and reversed order", "QuotesThings",
				NamingHelper.getAllResourcesNames(url, false));
		assertEquals("Should deal with depth=2, top-level=1, singularization and reversed order", "QuoteThing",
				NamingHelper.getAllResourcesNames(url, true));

		TestConfig.resetConfig();
	}

	@Test
	public void test_cleanLeadingAndTrailingNewLineAndChars_Empty() {
		assertEquals("EmptyString", "", NamingHelper.cleanLeadingAndTrailingNewLineAndChars(""));
		assertEquals("Null", null, NamingHelper.cleanLeadingAndTrailingNewLineAndChars(null));
	}

	@Test
	public void test_cleanLeadingAndTrailingNewLineAndChars_AsIs() {
		assertEquals("Ignore anything except trailing chars", "asdasd asdasd",
				NamingHelper.cleanLeadingAndTrailingNewLineAndChars("asdasd asdasd"));
	}

	@Test
	public void test_cleanLeadingAndTrailingNewLineAndChars_Cleanup() {
		assertEquals("Clean Leading", "asdasd", NamingHelper.cleanLeadingAndTrailingNewLineAndChars("   -*\n\t     asdasd"));
		assertEquals("Clean Trailing", "asdasd", NamingHelper.cleanLeadingAndTrailingNewLineAndChars("asdasd   -*\n\t     "));
		assertEquals("Clean Both", "asdasd", NamingHelper.cleanLeadingAndTrailingNewLineAndChars("   -*\n\t     asdasd   -*\n\t     "));
	}

	@Test
	public void test_convertTypeToQualifier_Success() {
		assertEquals("Should deal with simple standards cleanly", "AsJson", NamingHelper.convertContentTypeToQualifier("application/json"));
		assertEquals("Should deal with simple standards cleanly", "AsBinary",
				NamingHelper.convertContentTypeToQualifier("application/octet-stream"));
		assertEquals("Should deal with simple standards cleanly", "AsText", NamingHelper.convertContentTypeToQualifier("text/plain"));
		assertEquals("Should deal with simple standards cleanly", "AsText", NamingHelper.convertContentTypeToQualifier("text/html"));

		assertEquals("Should deal extract versions", "V1", NamingHelper.convertContentTypeToQualifier("application/v1+json"));
		assertEquals("Should deal extract versions", "V1",
				NamingHelper.convertContentTypeToQualifier("application/asdasdasdv1asdsad+json"));
		assertEquals("Should deal extract versions", "V1_2",
				NamingHelper.convertContentTypeToQualifier("application/asdasdasdv1.2asdsad+json"));

		assertEquals("Should deal extract versions", "_StuffAsJson", NamingHelper.convertContentTypeToQualifier("application/stuff+json"));

	}

	@Test
	public void test_enumNaming_Success() {
		assertEquals("SOMETHING_WORDY", NamingHelper.cleanNameForJavaEnum("somethingWordy"));
		assertEquals("SOMETHING_WORDY", NamingHelper.cleanNameForJavaEnum("SomethingWordy"));
		assertEquals("SOMETHING_WORDY", NamingHelper.cleanNameForJavaEnum("something Wordy"));
		assertEquals("SOMETHING_WORDY", NamingHelper.cleanNameForJavaEnum("SOMETHING WORDY"));
		assertEquals("SOMETHING_WORDY", NamingHelper.cleanNameForJavaEnum("something wordy"));
		assertEquals("SOME_THING_WORDY", NamingHelper.cleanNameForJavaEnum("some@#%(@%thing wordy"));
		assertEquals("AB_CDEF", NamingHelper.cleanNameForJavaEnum("ABCdef"));
		assertEquals("ABC", NamingHelper.cleanNameForJavaEnum("ABC"));
		assertEquals("A_B_CD", NamingHelper.cleanNameForJavaEnum("a b CD"));
		assertEquals("A_B_C_D", NamingHelper.cleanNameForJavaEnum("a b cD"));

		assertEquals("S_WORD", NamingHelper.cleanNameForJavaEnum("sWORD"));
		assertEquals("S_WORD", NamingHelper.cleanNameForJavaEnum("sWord"));
	}

}
