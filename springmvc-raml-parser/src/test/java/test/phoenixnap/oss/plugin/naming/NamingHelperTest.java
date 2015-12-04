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

import org.junit.Test;

import test.phoenixnap.oss.plugin.naming.testclasses.CamelCaseTest;
import test.phoenixnap.oss.plugin.naming.testclasses.ServicesControllerImpl;

import com.phoenixnap.oss.ramlapisync.naming.NamingHelper;

/**
 * Unit tests for the NamingHelper class
 * 
 * @author Kurt Paris
 * @since 0.0.1
 *
 */
public class NamingHelperTest {

	@Test
	public void test_convertClassName_Success() {
		assertEquals("Should not kill all names", "services",
				NamingHelper.convertClassName(ServicesControllerImpl.class));
		assertEquals("CamelCaseCheck", "camelCaseTest", NamingHelper.convertClassName(CamelCaseTest.class));
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
		assertEquals("Clean Leading", "asdasd",
				NamingHelper.cleanLeadingAndTrailingNewLineAndChars("   -*\n\t     asdasd"));
		assertEquals("Clean Trailing", "asdasd",
				NamingHelper.cleanLeadingAndTrailingNewLineAndChars("asdasd   -*\n\t     "));
		assertEquals("Clean Both", "asdasd",
				NamingHelper.cleanLeadingAndTrailingNewLineAndChars("   -*\n\t     asdasd   -*\n\t     "));
	}

}
