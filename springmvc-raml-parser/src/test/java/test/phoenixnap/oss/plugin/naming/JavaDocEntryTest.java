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

import org.junit.Test;

import com.phoenixnap.oss.ramlapisync.javadoc.JavaDocEntry;

import static org.junit.Assert.*;

/**
 * Tests for the JavaDoc Parser
 * 
 * @author Kurt Paris
 * @since 0.0.1
 *
 */
public class JavaDocEntryTest {

	@Test
	public void parse_emptyJavaDoc() {
		String rawJavaDoc = "/**" + "\n			* " + "\n			*/";

		JavaDocEntry entry = new JavaDocEntry(rawJavaDoc);

		assertEquals("Check main comment", "", entry.getComment());
		assertEquals("Check return comment", null, entry.getReturnTypeComment());
		assertEquals("Check error comments", 0, entry.getErrorComments().size());
		assertEquals("Check parameter comments", 0, entry.getParameterComments().size());
	}

	@Test
	public void parse_commentJavaDoc() {
		String expectedComment = "This comment i really dont expect to lose £$!as";
		String rawJavaDoc = "/**" + "\n			* " + expectedComment + "\n			*/";

		JavaDocEntry entry = new JavaDocEntry(rawJavaDoc);

		assertEquals("Check main comment", expectedComment, entry.getComment());
		assertEquals("Check return comment", null, entry.getReturnTypeComment());
		assertEquals("Check error comments", 0, entry.getErrorComments().size());
		assertEquals("Check parameter comments", 0, entry.getParameterComments().size());
	}
	
	@Test
	public void parse_commentJavaDocWithLink() {
		String expectedComment = "This comment i really dont expect to lose £$!as";
		String expectedLink = "TestEntity";
		String rawJavaDoc = "/**" + "\n			* " + expectedComment + "{@link "+expectedLink+ "}\n			*/";

		JavaDocEntry entry = new JavaDocEntry(rawJavaDoc);

		assertEquals("Check main comment", expectedComment+expectedLink, entry.getComment());
		assertEquals("Check return comment", null, entry.getReturnTypeComment());
		assertEquals("Check error comments", 0, entry.getErrorComments().size());
		assertEquals("Check parameter comments", 0, entry.getParameterComments().size());
	}
	
	@Test
	public void parse_commentJavaDocWithInheritDoc() {
		String expectedComment = "This comment i really dont expect to lose £$!as";
		String expectedLink = "TestEntity";
		String rawJavaDoc = "/**" + "\n			* " + expectedComment + "{@inheritDoc "+expectedLink+ "}\n			*/";

		JavaDocEntry entry = new JavaDocEntry(rawJavaDoc);

		assertEquals("Check main comment", expectedComment+expectedLink, entry.getComment());
		assertEquals("Check return comment", null, entry.getReturnTypeComment());
		assertEquals("Check error comments", 0, entry.getErrorComments().size());
		assertEquals("Check parameter comments", 0, entry.getParameterComments().size());
	}

	@Test
	public void parse_multiLineCommentJavaDoc() {
		String expectedComment = "This comment i really dont " + "\nexpect to lose £$!as but just in case email me"
				+ "at 1234@bob.com";
		String rawJavaDoc = "/**" + "\n			* " + expectedComment + "\n			*/";

		JavaDocEntry entry = new JavaDocEntry(rawJavaDoc);

		assertEquals("Check main comment", expectedComment, entry.getComment());
		assertEquals("Check return comment", null, entry.getReturnTypeComment());
		assertEquals("Check error comments", 0, entry.getErrorComments().size());
		assertEquals("Check parameter comments", 0, entry.getParameterComments().size());
	}

	@Test
	public void parse_multiLineCommentMetadataJavaDoc() {
		String expectedComment = "This comment i really dont " + "\nexpect to lose £$!as";
		String rawJavaDoc = "/**" + "\n			* " + "\n 	    * " + expectedComment
				+ "\n 	    * @author asdoiasdoiasjdoaisjasd" + "\n			* @whocares asdiojasdoijasdoiasd"
				+ "\n			* @misspelt asdasdasdd" + "\n			*/";

		JavaDocEntry entry = new JavaDocEntry(rawJavaDoc);
		assertEquals("Check main comment", expectedComment, entry.getComment());
		assertEquals("Check return comment", null, entry.getReturnTypeComment());
		assertEquals("Check error comments", 0, entry.getErrorComments().size());
		assertEquals("Check parameter comments", 0, entry.getParameterComments().size());
	}

	@Test
	public void parse_parameterJavaDoc() {
		String expectedComment = "This comment i really dont " + "\nexpect to lose £$!as";
		String param1Comment = "a comment for this really cool parameter";
		String param2Comment = "a comment with funky chars \"£$^**412312 is really cool parameter";
		String param3EmptyComment = "";
		String param4Comment = "a comment for this really cool parameter" + "\nbut gone multiline oqqow";
		String param5Comment = "the id of ";
		String param5LinkComment = "TestEntity";

		String rawJavaDoc = "/**" + "\n			* " + "\n 	    * " + expectedComment + "\n 	    * @param parameter1 "
				+ param1Comment + "\n 	    * @param parameter2 " + param1Comment
				+ "\n 	    * @param parameter2 "
				+ param2Comment // duplicate parameter will override
				+ "\n 	    * @param parameter3 " + param3EmptyComment + "\n 	    * @param parameter4 " + param4Comment
				+ "\n			* @whocares asdiojasdoijasdoiasd" + "\n			* @misspelt asdasdasdd" + "\n			"
				+ "\n       * @param parameter5 "+ param5Comment +"{@link " + param5LinkComment + "} */";

		JavaDocEntry entry = new JavaDocEntry(rawJavaDoc);
		assertEquals("Check main comment", expectedComment, entry.getComment());
		assertEquals("Check return comment", null, entry.getReturnTypeComment());
		assertEquals("Check error comments", 0, entry.getErrorComments().size());
		assertEquals("Check parameter comments", 4, entry.getParameterComments().size());
		assertEquals("Check parameter content", param1Comment, entry.getParameterComments().get("parameter1"));
		assertEquals("Check parameter content", param2Comment, entry.getParameterComments().get("parameter2"));
		assertEquals("Check parameter content", null, entry.getParameterComments().get("parameter3"));
		assertEquals("Check parameter content", param4Comment, entry.getParameterComments().get("parameter4"));
		assertEquals("Check parameter content with link", param5Comment+param5LinkComment, entry.getParameterComments().get("parameter5"));
	}

	@Test
	public void parse_returnTypeAndParameterJavaDoc() {
		String expectedComment = "This comment i really dont " + "\nexpect to lose £$!as";
		String param1Comment = "a comment for this really cool parameter";
		String param2Comment = "a comment with funky chars \"£$^**412312 is really cool parameter";
		String param3EmptyComment = "";
		String param4Comment = "a comment for this really cool parameter" + "\nbut gone multiline oqqow";

		String returnTypeComment = "this method will return a bunch of stufffffffssss";

		JavaDocEntry entry = buildJavaDocEntry(expectedComment, param1Comment, param2Comment, param3EmptyComment,
				param4Comment, returnTypeComment);
		assertEquals("Check main comment", expectedComment, entry.getComment());
		assertEquals("Check return comment", returnTypeComment, entry.getReturnTypeComment());
		assertEquals("Check error comments", 0, entry.getErrorComments().size());
		assertEquals("Check parameter comments", 3, entry.getParameterComments().size());
		assertEquals("Check parameter content", param1Comment, entry.getParameterComments().get("parameter1"));
		assertEquals("Check parameter content", param2Comment, entry.getParameterComments().get("parameter2"));
		assertEquals("Check parameter content", null, entry.getParameterComments().get("parameter3"));
		assertEquals("Check parameter content", param4Comment, entry.getParameterComments().get("parameter4"));
	}

	@Test
	public void merge_nullJavaDoc() {
		String expectedComment = "This comment i really dont " + "\nexpect to lose £$!as";
		String param1Comment = "a comment for this really cool parameter";
		String param2Comment = "a comment with funky chars \"£$^**412312 is really cool parameter";
		String param3EmptyComment = "";
		String param4Comment = "a comment for this really cool parameter" + "\nbut gone multiline oqqow";

		String returnTypeComment = "this method will return a bunch of stufffffffssss";

		JavaDocEntry entry = buildJavaDocEntry(expectedComment, param1Comment, param2Comment, param3EmptyComment,
				param4Comment, returnTypeComment);

		JavaDocEntry newEntry = new JavaDocEntry("/** \n* \n*/");
		entry.merge(newEntry);

		assertEquals("Check main comment", expectedComment, entry.getComment());
		assertEquals("Check return comment", returnTypeComment, entry.getReturnTypeComment());
		assertEquals("Check error comments", 0, entry.getErrorComments().size());
		assertEquals("Check parameter comments", 3, entry.getParameterComments().size());
		assertEquals("Check parameter content", param1Comment, entry.getParameterComments().get("parameter1"));
		assertEquals("Check parameter content", param2Comment, entry.getParameterComments().get("parameter2"));
		assertEquals("Check parameter content", null, entry.getParameterComments().get("parameter3"));
		assertEquals("Check parameter content", param4Comment, entry.getParameterComments().get("parameter4"));
	}

	@Test
	public void merge_betterCommentAndAdditionalParameterJavaDoc() {
		String expectedComment = "This comment i really dont " + "\nexpect to lose £$!as";
		String betterComment = "This comment i really really really really really dont " + "\nexpect to lose £$!as";
		String param1Comment = "a comment for this really cool parameter";
		String param2Comment = "a comment with funky chars \"£$^**412312 is really cool parameter";
		String param3EmptyComment = "";
		String param3NonEmptyComment = "really cool stuff man!";
		String param4Comment = "a comment for this really cool parameter" + "\nbut gone multiline oqqow";

		String returnTypeComment = "this method will return a bunch of stufffffffssss";

		JavaDocEntry entry = buildJavaDocEntry(expectedComment, param1Comment, param2Comment, param3EmptyComment,
				param4Comment, returnTypeComment);

		JavaDocEntry newEntry = buildJavaDocEntry(betterComment, param1Comment, param2Comment, param3NonEmptyComment,
				param4Comment, returnTypeComment);
		entry.merge(newEntry);

		assertEquals("Check main comment", betterComment, entry.getComment());
		assertEquals("Check return comment", returnTypeComment, entry.getReturnTypeComment());
		assertEquals("Check error comments", 0, entry.getErrorComments().size());
		assertEquals("Check parameter comments", 4, entry.getParameterComments().size());
		assertEquals("Check parameter content", param1Comment, entry.getParameterComments().get("parameter1"));
		assertEquals("Check parameter content", param2Comment, entry.getParameterComments().get("parameter2"));
		assertEquals("Check parameter content", param3NonEmptyComment, entry.getParameterComments().get("parameter3"));
		assertEquals("Check parameter content", param4Comment, entry.getParameterComments().get("parameter4"));
	}

	private JavaDocEntry buildJavaDocEntry(String expectedComment, String param1Comment, String param2Comment,
			String param3Comment, String param4Comment, String returnTypeComment) {
		String rawJavaDoc = "/**" + "\n			* " + "\n 	    * " + expectedComment + "\n 	    * @param parameter1 "
				+ param1Comment + "\n 	    * @param parameter2 " + param1Comment
				+ "\n 	    * @param parameter2 "
				+ param2Comment // duplicate parameter will override
				+ "\n 	    * @param parameter3 " + param3Comment + "\n 	    * @param parameter4 " + param4Comment
				+ "\n 	    * @return " + returnTypeComment + "\n 	    * @returns  asdasdasdasdasdasdasd " // misspelt
																											// shouold
																											// be
																											// ignored
				+ "\n			* @whocares asdiojasdoijasdoiasd" + "\n			* @misspelt asdasdasdd" + "\n			*/";

		JavaDocEntry entry = new JavaDocEntry(rawJavaDoc);
		return entry;
	}

}
