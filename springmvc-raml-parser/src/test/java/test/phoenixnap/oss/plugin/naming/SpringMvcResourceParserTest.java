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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.raml.model.Action;
import org.raml.model.ActionType;
import org.raml.model.MimeType;
import org.raml.model.ParamType;
import org.raml.model.Resource;
import org.raml.model.Response;
import org.raml.model.parameter.FormParameter;
import org.raml.model.parameter.QueryParameter;
import org.raml.model.parameter.UriParameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;

import test.phoenixnap.oss.plugin.naming.testclasses.BugController;
import test.phoenixnap.oss.plugin.naming.testclasses.TestController;

import com.phoenixnap.oss.ramlapisync.javadoc.JavaDocEntry;
import com.phoenixnap.oss.ramlapisync.javadoc.JavaDocExtractor;
import com.phoenixnap.oss.ramlapisync.javadoc.JavaDocStore;
import com.phoenixnap.oss.ramlapisync.parser.FileSearcher;
import com.phoenixnap.oss.ramlapisync.parser.SpringMvcResourceParser;

/**
 * Unit tests for the Spring MVC Parser
 * 
 * @author Kurt Paris
 * @since 0.0.1
 *
 */
public class SpringMvcResourceParserTest {

	protected static final Logger logger = LoggerFactory.getLogger(FileSearcher.class);

	private static SpringMvcResourceParser parser;
	private static Resource baseResourceTestController;

	public static final String VERSION = "0.0.1";
	public static final String DEFAULT_MEDIA_TYPE = MediaType.APPLICATION_JSON_VALUE;

	public static final String CLASS_JAVADOC = "Some class !\"*#$%£$%\n Javadoc";
	public static final String PARAM_JAVADOC = "Some param !\"*#$%£$%\n Javadoc";
	public static final String COMMENT_JAVADOC = "Some field or method !\"*#$%£$%\n Javadoc";
	public static final String COMMENT_JAVADOC_ESCAPED = "Some field or method !\\\"*#$%£$%\\n Javadoc";
	public static final String RETURN_JAVADOC = "Some return !\"*#$%£$%\n Javadoc";

	private void validateSimpleAjaxResponse(Action action) {
		assertEquals(1, action.getResponses().size());
		Response response = action.getResponses().get("200");
		assertEquals("Checking return javadoc", RETURN_JAVADOC, response.getDescription());
		assertEquals(1, response.getBody().size());
		assertNotNull("Check Response is there", response.getBody().get(DEFAULT_MEDIA_TYPE));
		assertNotNull("Check Response Schema is there", response.getBody().get(DEFAULT_MEDIA_TYPE).getSchema());
	}

	private static String combineConstantAndName(String constant, String name) {
		return name + constant;
	}

	@SuppressWarnings("unchecked")
	@BeforeClass
	public static void getResource() {
		parser = new SpringMvcResourceParser(null, VERSION, DEFAULT_MEDIA_TYPE, false);
		JavaDocExtractor mockJavaDocExtractor = Mockito.mock(JavaDocExtractor.class);
		JavaDocStore mockJavaDocStore = Mockito.mock(JavaDocStore.class);
		JavaDocEntry mockJavaDocEntry = Mockito.mock(JavaDocEntry.class);
		Map<String, String> paramComments = Mockito.mock(Map.class);

		Mockito.when(paramComments.get(Mockito.anyString())).thenAnswer(new Answer<String>() {
			public String answer(InvocationOnMock invocation) throws Throwable {
				return combineConstantAndName(PARAM_JAVADOC, (String) invocation.getArguments()[0]);
			}
		});

		// Set up JavaDoc Extractor;
		Mockito.when(mockJavaDocExtractor.getJavaDoc(Mockito.any())).thenReturn(mockJavaDocStore);
		Mockito.when(mockJavaDocStore.getJavaDocComment(Mockito.any())).thenReturn(CLASS_JAVADOC);
		Mockito.when(mockJavaDocStore.getJavaDoc((Mockito.any(Method.class)))).thenReturn(mockJavaDocEntry);
		Mockito.when(mockJavaDocStore.getJavaDoc((Mockito.any(String.class)))).thenReturn(mockJavaDocEntry);
		Mockito.when(mockJavaDocStore.getJavaDoc(Mockito.any(String.class), Mockito.any(Integer.class))).thenReturn(
				mockJavaDocEntry);

		// Set up the Entry
		Mockito.when(mockJavaDocEntry.getParameterComments()).thenReturn(paramComments);
		Mockito.when(mockJavaDocEntry.getComment()).thenReturn(COMMENT_JAVADOC);
		Mockito.when(mockJavaDocEntry.getReturnTypeComment()).thenReturn(RETURN_JAVADOC);
		parser.setJavaDocs(mockJavaDocExtractor);

		baseResourceTestController = parser.extractResourceInfo(TestController.class);
	}

	@Test
	public void test_controllerdetection() {
		assertEquals("Assert base uri found", "/", baseResourceTestController.getRelativeUri());
		assertEquals("Assert controller found at Base", "/base", baseResourceTestController.getResource("/base")
				.getRelativeUri());
		assertEquals("Assert resources size", 1, baseResourceTestController.getResources().size());
	}

	@Test
	public void test_multipleHttpMethods() {
		Resource testResource = baseResourceTestController.getResource("/base").getResource("/simpleMethodAll");
		assertEquals("Assert resources size", ActionType.values().length, testResource.getActions().size());
	}

	@Test
	public void test_simpleGetAndPost() {
		Resource testResource = baseResourceTestController.getResource("/base").getResource("/simpleMethod");
		assertEquals("Assert resources size", 2, testResource.getActions().size());
		Action getAction = testResource.getActions().get(ActionType.GET);
		Action postAction = testResource.getActions().get(ActionType.POST);
		assertNotNull(getAction);
		assertNotNull(postAction);
		assertEquals("Assert Javadoc", COMMENT_JAVADOC, getAction.getDescription());
		assertEquals("Assert Javadoc", COMMENT_JAVADOC, postAction.getDescription());
		validateSimpleAjaxResponse(getAction);
		validateSimpleAjaxResponse(postAction);
	}

	@Test
	public void test_simpleGetAndPostWithBodyStringParam() {
		Resource testResource = baseResourceTestController.getResource("/base").getResource("/oneParameterBody");
		assertEquals("Assert resources size", 2, testResource.getActions().size());
		Action getAction = testResource.getActions().get(ActionType.GET);
		Action postAction = testResource.getActions().get(ActionType.POST);
		assertNotNull(getAction);
		assertNotNull(postAction);
		validateSimpleAjaxResponse(getAction);
		validateSimpleAjaxResponse(postAction);

		assertEquals("Assert Javadoc", COMMENT_JAVADOC, getAction.getDescription());
		assertEquals("Assert Javadoc", COMMENT_JAVADOC, postAction.getDescription());

		// validate Post
		MimeType getMimeType = getAction.getBody().get(MediaType.TEXT_PLAIN_VALUE);
		assertNull(getMimeType);
		MimeType postMimeType = postAction.getBody().get(MediaType.TEXT_PLAIN_VALUE);
		assertNotNull(postMimeType);

	}

	@Test
	public void test_simpleGetAndPostWithOneParameter() {
		Resource testResource = baseResourceTestController.getResource("/base").getResource("/oneParameter");
		assertEquals("Assert resources size", 2, testResource.getActions().size());
		Action getAction = testResource.getActions().get(ActionType.GET);
		Action postAction = testResource.getActions().get(ActionType.POST);
		assertNotNull(getAction);
		assertNotNull(postAction);
		validateSimpleAjaxResponse(getAction);
		validateSimpleAjaxResponse(postAction);

		assertEquals("Assert Javadoc", COMMENT_JAVADOC, getAction.getDescription());
		assertEquals("Assert Javadoc", COMMENT_JAVADOC, postAction.getDescription());

		// validate Get
		String paramName = "param1";
		assertEquals("Check that parameter was placed in query", 1, getAction.getQueryParameters().size());
		QueryParameter queryParameter = getAction.getQueryParameters().get(paramName);
		assertEquals("Check that parameter was placed in query", ParamType.STRING, queryParameter.getType());
		assertEquals("Assert Javadoc", combineConstantAndName(PARAM_JAVADOC, paramName),
				queryParameter.getDescription());
		assertEquals("Assert Javadoc", COMMENT_JAVADOC, getAction.getDescription());

		// validate Post
		Map<String, List<FormParameter>> formParameters = postAction.getBody()
				.get(MediaType.APPLICATION_FORM_URLENCODED_VALUE).getFormParameters();
		assertEquals("Check that parameter was placed in form", 1, formParameters.size());
		FormParameter formParameter = formParameters.get(paramName).get(0);
		assertEquals("Assert Javadoc", combineConstantAndName(PARAM_JAVADOC, paramName), formParameter.getDescription());
		assertEquals("Check that parameter type is correct", ParamType.STRING, formParameter.getType());
	}

	@Test
	public void test_simpleGetAndPostWithTwoParameters() {
		String paramName;
		Resource testResource = baseResourceTestController.getResource("/base").getResource("/twoParameter");
		assertEquals("Assert resources size", 2, testResource.getActions().size());
		Action getAction = testResource.getActions().get(ActionType.GET);
		Action postAction = testResource.getActions().get(ActionType.POST);
		assertNotNull(getAction);
		assertNotNull(postAction);
		validateSimpleAjaxResponse(getAction);
		validateSimpleAjaxResponse(postAction);

		// validate Get
		paramName = "param1";
		assertEquals("Check that parameters were placed in query", 2, getAction.getQueryParameters().size());
		QueryParameter queryParameter = getAction.getQueryParameters().get(paramName);
		assertEquals("Check that parameter was placed in query", ParamType.INTEGER, queryParameter.getType());
		assertEquals("Assert Javadoc", combineConstantAndName(PARAM_JAVADOC, paramName),
				queryParameter.getDescription());

		paramName = "nameOverride";
		QueryParameter queryParameter2 = getAction.getQueryParameters().get(paramName);
		assertEquals("Check that parameter was placed in query", ParamType.STRING, queryParameter2.getType());
		assertEquals("Check that parameter is required", true, queryParameter2.isRequired());
		assertEquals("Assert Javadoc", combineConstantAndName(PARAM_JAVADOC, "param2"),
				queryParameter2.getDescription());

		// validate Post
		paramName = "param1";
		Map<String, List<FormParameter>> formParameters = postAction.getBody()
				.get(MediaType.APPLICATION_FORM_URLENCODED_VALUE).getFormParameters();
		assertEquals("Check that parameter was placed in form", 2, formParameters.size());
		FormParameter formParameter = formParameters.get(paramName).get(0);
		assertEquals("Check that parameter type is correct", ParamType.INTEGER, formParameter.getType());
		assertEquals("Assert Javadoc", combineConstantAndName(PARAM_JAVADOC, paramName), formParameter.getDescription());

		paramName = "nameOverride";
		FormParameter formParameter2 = formParameters.get(paramName).get(0);
		assertEquals("Check that parameter type is correct", ParamType.STRING, formParameter2.getType());
		assertEquals("Check that parameter is required", true, formParameter2.isRequired());
		assertEquals("Assert Javadoc", combineConstantAndName(PARAM_JAVADOC, "param2"), formParameter2.getDescription());
	}

	@Test
	public void test_simpleGetAndPostWithOneParameterAndPathVariable() {
		Resource testResource = baseResourceTestController.getResource("/base").getResource("/oneParameter")
				.getResource("/{pathVariable}");
		assertEquals("Assert resources size", 1, testResource.getUriParameters().size());
		UriParameter uriParameter = testResource.getUriParameters().get("pathVariable");
		assertEquals("Check that parameter was placed in query", ParamType.STRING, uriParameter.getType());
		assertEquals("Check that uriparametersare required", true, uriParameter.isRequired());
		assertEquals("Assert Javadoc", combineConstantAndName(PARAM_JAVADOC, "pathVariable"),
				uriParameter.getDescription());

		assertEquals("Assert resources size", 2, testResource.getActions().size());
		Action getAction = testResource.getActions().get(ActionType.GET);
		Action postAction = testResource.getActions().get(ActionType.POST);
		assertNotNull(getAction);
		assertNotNull(postAction);
		validateSimpleAjaxResponse(getAction);
		validateSimpleAjaxResponse(postAction);

		assertEquals("Assert Javadoc", COMMENT_JAVADOC, getAction.getDescription());
		assertEquals("Assert Javadoc", COMMENT_JAVADOC, postAction.getDescription());

		// validate Get
		String paramName = "param1";
		assertEquals("Check that parameter was placed in query", 1, getAction.getQueryParameters().size());
		QueryParameter queryParameter = getAction.getQueryParameters().get(paramName);
		assertEquals("Check that parameter was placed in query", ParamType.STRING, queryParameter.getType());
		assertEquals("Assert Javadoc", combineConstantAndName(PARAM_JAVADOC, paramName),
				queryParameter.getDescription());
		assertEquals("Assert Javadoc", COMMENT_JAVADOC, getAction.getDescription());

		// validate Post
		Map<String, List<FormParameter>> formParameters = postAction.getBody()
				.get(MediaType.APPLICATION_FORM_URLENCODED_VALUE).getFormParameters();
		assertEquals("Check that parameter was placed in form", 1, formParameters.size());
		FormParameter formParameter = formParameters.get(paramName).get(0);
		assertEquals("Assert Javadoc", combineConstantAndName(PARAM_JAVADOC, paramName), formParameter.getDescription());
		assertEquals("Check that parameter type is correct", ParamType.STRING, formParameter.getType());
	}

	@Test
	public void test_simpleGetWithMiscCasesAndPathVariable() {
		Resource testResource = baseResourceTestController.getResource("/base").getResource("/miscCases")
				.getResource("/{pathVariable}");
		UriParameter uriParameter = testResource.getUriParameters().get("pathVariable");
		assertEquals("Check that parameter was placed in query", ParamType.INTEGER, uriParameter.getType());
		assertEquals("Check that uriparametersare required", true, uriParameter.isRequired());
		assertEquals("Assert Javadoc", combineConstantAndName(PARAM_JAVADOC, "pathVariable"),
				uriParameter.getDescription());

		assertEquals("Assert resources size", 1, testResource.getActions().size());
		Action getAction = testResource.getActions().get(ActionType.GET);
		assertNotNull(getAction);
		validateSimpleAjaxResponse(getAction);
		assertEquals("Assert Javadoc", COMMENT_JAVADOC, getAction.getDescription());

		// validate Get
		String paramName = "param1";
		assertEquals("Check that parameter was placed in query", 1, getAction.getQueryParameters().size());
		QueryParameter queryParameter = getAction.getQueryParameters().get(paramName);
		assertEquals("Check that parameter was placed in query", ParamType.STRING, queryParameter.getType());
		assertEquals("Assert Javadoc", combineConstantAndName(PARAM_JAVADOC, paramName),
				queryParameter.getDescription());
		assertEquals("Check that param is an array", true, queryParameter.isRepeat());
		assertEquals("Assert Javadoc", COMMENT_JAVADOC, getAction.getDescription());
	}

	@Test
	public void test_simpleGetAndPostWithRequestBodyParameter() {
		Resource testResource = baseResourceTestController.getResource("/base").getResource("/oneParameterBodyObject");
		assertEquals("Assert resources size", 2, testResource.getActions().size());
		Action getAction = testResource.getActions().get(ActionType.GET);
		Action postAction = testResource.getActions().get(ActionType.POST);
		assertNotNull(getAction);
		assertNotNull(postAction);
		validateSimpleAjaxResponse(getAction);
		validateSimpleAjaxResponse(postAction);

		assertEquals("Assert Javadoc", COMMENT_JAVADOC, getAction.getDescription());
		assertEquals("Assert Javadoc", COMMENT_JAVADOC, postAction.getDescription());

		// validate Get
		assertEquals("Check that parameter was ignored", 0, getAction.getQueryParameters().size());

		// validate Post
		String schema = postAction.getBody().get(MediaType.APPLICATION_JSON_VALUE).getSchema();
		assertNotNull(schema);
		assertTrue(schema.contains("element1"));
		assertTrue(schema.contains("element2"));
		assertTrue(schema.contains("element3"));
		assertTrue(schema.contains(COMMENT_JAVADOC_ESCAPED));
	}
	
	@Test
	public void test_simplePutWithRequestBodyParameterAndIgnoredParams() {
		Resource testResource = baseResourceTestController.getResource("/base").getResource("/methodBodyIgnore");
		assertEquals("Assert resources size", 1, testResource.getActions().size());
		Action putAction = testResource.getActions().get(ActionType.PUT);
		assertNotNull(putAction);
		validateSimpleAjaxResponse(putAction);

		assertEquals("Assert Javadoc", COMMENT_JAVADOC, putAction.getDescription());

		// validate Put
		String schema = putAction.getBody().get(MediaType.APPLICATION_JSON_VALUE).getSchema();
		assertNotNull(schema);
		assertFalse(schema.contains("ignored"));
		assertFalse(schema.toLowerCase().contains("camelcasetest"));
		assertTrue(schema.contains("element1"));
		assertTrue(schema.contains("element2"));
		assertTrue(schema.contains("element3"));
		assertTrue(schema.contains(COMMENT_JAVADOC_ESCAPED));
	}
	
	
	@Test
	public void test_descriptionPath() {
		Resource testResource = baseResourceTestController.getResource("/base").getResource("/descriptionTest");
		assertEquals("Assert resources size", 1, testResource.getResources().size());
		assertEquals("Assert description", "aaaaaaaaaaaaaaaa", testResource.getDescription());
		
		Resource nestedResource = testResource.getResource("/secondBlock");
		assertEquals("Assert resources size", 1, nestedResource.getResources().size());
		assertEquals("Assert description", "bbbbbbbbbbbbbbbbbb", nestedResource.getDescription());
		
		nestedResource = nestedResource.getResource("/thirdBlock");
		assertEquals("Assert resources size", 1, nestedResource.getResources().size());
		assertEquals("Assert description", "cccccccccccccccccc", nestedResource.getDescription());
		
		nestedResource = nestedResource.getResource("/stuff");
		assertEquals("Assert resources size", 1, nestedResource.getActions().size());
		assertEquals("Assert description", "Stuff Resource", nestedResource.getDescription());

	}
	
	@Test
	public void test_bug1_IndexOutOfBounds_PostWithNoBody() { 
		Resource resourceInfo = parser.extractResourceInfo(BugController.class);
		Resource testResource = resourceInfo.getResource("/forgotStuff").getResource("/{somethingID}").getResource("/resendStuff");
		assertNotNull(testResource);
	}

}
