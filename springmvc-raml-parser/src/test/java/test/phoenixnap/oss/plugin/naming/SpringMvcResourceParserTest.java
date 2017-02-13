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
import com.phoenixnap.oss.ramlapisync.data.ApiResourceMetadata;
import com.phoenixnap.oss.ramlapisync.data.RamlFormParameter;
import com.phoenixnap.oss.ramlapisync.generation.RamlParser;
import com.phoenixnap.oss.ramlapisync.generation.RamlVerifier;
import com.phoenixnap.oss.ramlapisync.javadoc.JavaDocEntry;
import com.phoenixnap.oss.ramlapisync.javadoc.JavaDocExtractor;
import com.phoenixnap.oss.ramlapisync.javadoc.JavaDocStore;
import com.phoenixnap.oss.ramlapisync.naming.RamlHelper;
import com.phoenixnap.oss.ramlapisync.parser.FileSearcher;
import com.phoenixnap.oss.ramlapisync.parser.SpringMvcResourceParser;
import com.phoenixnap.oss.ramlapisync.raml.RamlAction;
import com.phoenixnap.oss.ramlapisync.raml.RamlActionType;
import com.phoenixnap.oss.ramlapisync.raml.RamlMimeType;
import com.phoenixnap.oss.ramlapisync.raml.RamlModelFactoryOfFactories;
import com.phoenixnap.oss.ramlapisync.raml.RamlParamType;
import com.phoenixnap.oss.ramlapisync.raml.RamlQueryParameter;
import com.phoenixnap.oss.ramlapisync.raml.RamlResource;
import com.phoenixnap.oss.ramlapisync.raml.RamlResponse;
import com.phoenixnap.oss.ramlapisync.raml.RamlRoot;
import com.phoenixnap.oss.ramlapisync.raml.RamlUriParameter;
import com.sun.codemodel.JCodeModel;

import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;

import test.phoenixnap.oss.plugin.naming.testclasses.BugController;
import test.phoenixnap.oss.plugin.naming.testclasses.MultipleContentTypeTestController;
import test.phoenixnap.oss.plugin.naming.testclasses.NoValueController;
import test.phoenixnap.oss.plugin.naming.testclasses.ShorthandTestController;
import test.phoenixnap.oss.plugin.naming.testclasses.TestController;
import test.phoenixnap.oss.plugin.naming.testclasses.UriPrefixIgnoredController;
import test.phoenixnap.oss.plugin.naming.testclasses.WrappedResponseBodyTestController;

import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;


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
	private static RamlResource baseResourceTestController;

	public static final String VERSION = "0.0.1";
	public static final String DEFAULT_MEDIA_TYPE = MediaType.APPLICATION_JSON_VALUE;

	public static final String CLASS_JAVADOC = "Some class !\"*#$%£$%\n Javadoc";
	public static final String PARAM_JAVADOC = "Some param !\"*#$%£$%\n Javadoc";
	public static final String COMMENT_JAVADOC = "Some field or method !\"*#$%£$%\n Javadoc";
	public static final String COMMENT_JAVADOC_ESCAPED = "Some field or method !\\\"*#$%£$%\\n Javadoc";
	public static final String RETURN_JAVADOC = "Some return !\"*#$%£$%\n Javadoc";

	private void validateSimpleAjaxResponse(RamlAction action) {
		assertEquals(1, action.getResponses().size());
		RamlResponse response = RamlHelper.getSuccessfulResponse(action);
		assertEquals("Checking return javadoc", RETURN_JAVADOC, response.getDescription());
		assertEquals(1, response.getBody().size());
		assertNotNull("Check Response is there", response.getBody().get(DEFAULT_MEDIA_TYPE));
		assertNotNull("Check Response Schema is there", response.getBody().get(DEFAULT_MEDIA_TYPE).getSchema());
	}
	
	private void validateMultipleResponse(RamlAction action) {
		assertEquals(1, action.getResponses().size());
		RamlResponse response = RamlHelper.getSuccessfulResponse(action);
		assertEquals("Checking return javadoc", RETURN_JAVADOC, response.getDescription());
		assertEquals(2, response.getBody().size());
		assertNotNull("Check Response is there", response.getBody().get(MediaType.APPLICATION_JSON_VALUE));		
		assertNotNull("Check Response is there", response.getBody().get(MediaType.TEXT_PLAIN_VALUE));		
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
	public void test_wrappedResponseBody__Success() {
		RamlResource resourceInfo = parser.extractResourceInfo(WrappedResponseBodyTestController.class);

		RamlResource testResource = resourceInfo.getResource("/base").getResource("/endpointWithResponseType");
		String testClassId = "urn:jsonschema:test:phoenixnap:oss:plugin:naming:testclasses:ThreeElementClass";
		checkResourceWrappedResponse(testResource, testClassId);

		testResource = resourceInfo.getResource("/base").getResource("/endpointWithResponseTypeNonGeneric");
		testClassId = "\"type\" : \"any\"";
		checkResourceWrappedResponse(testResource, testClassId);


	}

	public void checkResourceWrappedResponse(RamlResource testResource, String testClassId) {
		assertEquals("Assert resources size", 2, testResource.getActions().size());
		RamlAction getAction = testResource.getActions().get(RamlActionType.GET);
		RamlAction postAction = testResource.getActions().get(RamlActionType.POST);
		assertNotNull(getAction);
		assertNotNull(postAction);
		assertEquals("Assert Javadoc", COMMENT_JAVADOC, getAction.getDescription());
		assertEquals("Assert Javadoc", COMMENT_JAVADOC, postAction.getDescription());
		assertTrue(getAction.getResponses().get("200").getBody().get("application/test+json").getSchema().contains(testClassId));
		assertTrue(postAction.getResponses().get("200").getBody().get("application/test+json").getSchema().contains(testClassId));
	}


	private static String combineConstantAndName(String constant, String name) {
		return name + constant;
	}


    @Test
    public void test_seperateContentType__Success() throws Exception {
        RamlRoot published = RamlVerifier.loadRamlFromFile("test-responsebody-multipletype.raml");
        RamlParser par = new RamlParser("com.gen.test", "/api", true, false);
        Set<ApiResourceMetadata> controllersMetadataSet = par.extractControllers(new JCodeModel(), published);

        assertEquals(1, controllersMetadataSet.size());
        assertEquals(2, controllersMetadataSet.iterator().next().getApiCalls().size());
        
        //lets check that names wont collide
        Iterator<ApiActionMetadata> apiCallIterator = controllersMetadataSet.iterator().next().getApiCalls().iterator();
		assertTrue(apiCallIterator.next().getName().contains("As"));
		assertTrue(apiCallIterator.next().getName().contains("As"));
        
        //lets check that it switches off correctly
        par = new RamlParser("com.gen.test", "/api", false, false);
        controllersMetadataSet = par.extractControllers(new JCodeModel(), published);
        assertEquals(1, controllersMetadataSet.size());
        assertEquals(1, controllersMetadataSet.iterator().next().getApiCalls().size());
        
        //lets check that names arent changed
        apiCallIterator = controllersMetadataSet.iterator().next().getApiCalls().iterator();
		assertFalse(apiCallIterator.next().getName().contains("As"));
		

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
		RamlResource testResource = baseResourceTestController.getResource("/base").getResource("/simpleMethodAll");
		assertEquals("Assert resources size", RamlActionType.values().length, testResource.getActions().size());
	}
	
	@Test
	public void test_multipleContentType() {
		RamlResource resourceInfo = parser.extractResourceInfo(MultipleContentTypeTestController.class);

		RamlResource testResource = resourceInfo.getResource("/base").getResource("/endpoint");
		assertEquals("Assert resources size", 2, testResource.getActions().size());
		RamlAction getAction = testResource.getActions().get(RamlActionType.GET);
		RamlAction postAction = testResource.getActions().get(RamlActionType.POST);
		assertNotNull(getAction);
		assertNotNull(postAction);
		assertEquals("Assert Javadoc", COMMENT_JAVADOC, getAction.getDescription());
		assertEquals("Assert Javadoc", COMMENT_JAVADOC, postAction.getDescription());
		validateMultipleResponse(getAction);
		validateMultipleResponse(postAction);
	}
	
	@Test
	public void test_uriPrefixIgnored() {
		RamlResource resourceInfo = parser.extractResourceInfo(UriPrefixIgnoredController.class);
		RamlRoot raml = RamlModelFactoryOfFactories.createRamlModelFactoryV08().createRamlRoot();
		RamlHelper.mergeResources(raml, resourceInfo, true);
		RamlHelper.removeResourceTree(raml, UriPrefixIgnoredController.IGNORED);

		RamlResource testResource = raml.getResource("/").getResource("/base").getResource("/endpoint");
		assertEquals("Assert removal", null, raml.getResource("/").getResource("/the"));
		assertFalse("Check URI", testResource.getUri().contains("the"));
		assertEquals("Assert resources size", 1, raml.getResources().size());
		assertEquals("Assert actions size", 2, testResource.getActions().size());
		RamlAction getAction = testResource.getActions().get(RamlActionType.GET);
		RamlAction postAction = testResource.getActions().get(RamlActionType.POST);
		assertNotNull(getAction);
		assertNotNull(postAction);
		assertEquals("Assert Javadoc", COMMENT_JAVADOC, getAction.getDescription());
		assertEquals("Assert Javadoc", COMMENT_JAVADOC, postAction.getDescription());
		validateMultipleResponse(getAction);
		validateMultipleResponse(postAction);
	}

	@Test
	public void test_simpleGetAndPost() {
		RamlResource testResource = baseResourceTestController.getResource("/base").getResource("/simpleMethod");
		assertEquals("Assert resources size", 2, testResource.getActions().size());
		RamlAction getAction = testResource.getActions().get(RamlActionType.GET);
		RamlAction postAction = testResource.getActions().get(RamlActionType.POST);
		assertNotNull(getAction);
		assertNotNull(postAction);
		assertEquals("Assert Javadoc", COMMENT_JAVADOC, getAction.getDescription());
		assertEquals("Assert Javadoc", COMMENT_JAVADOC, postAction.getDescription());
		validateSimpleAjaxResponse(getAction);
		validateSimpleAjaxResponse(postAction);
	}
	
	@Test
	public void test_simpleGetAndPostShorthand() {
		RamlResource parsed = parser.extractResourceInfo(ShorthandTestController.class);
		RamlResource testResource = parsed.getResource("/base").getResource("/simpleMethod");
		assertEquals("Assert resources size", 2, testResource.getActions().size());
		RamlAction getAction = testResource.getActions().get(RamlActionType.GET);
		RamlAction postAction = testResource.getActions().get(RamlActionType.POST);
		assertNotNull(getAction);
		assertNotNull(postAction);
		assertEquals("Assert Javadoc", COMMENT_JAVADOC, getAction.getDescription());
		assertEquals("Assert Javadoc", COMMENT_JAVADOC, postAction.getDescription());
		validateSimpleAjaxResponse(getAction);
		validateSimpleAjaxResponse(postAction);
	}

	@Test
	public void test_simpleGetAndPostWithBodyStringParam() {
		RamlResource testResource = baseResourceTestController.getResource("/base").getResource("/oneParameterBody");
		assertEquals("Assert resources size", 2, testResource.getActions().size());
		RamlAction getAction = testResource.getActions().get(RamlActionType.GET);
		RamlAction postAction = testResource.getActions().get(RamlActionType.POST);
		assertNotNull(getAction);
		assertNotNull(postAction);
		validateSimpleAjaxResponse(getAction);
		validateSimpleAjaxResponse(postAction);

		assertEquals("Assert Javadoc", COMMENT_JAVADOC, getAction.getDescription());
		assertEquals("Assert Javadoc", COMMENT_JAVADOC, postAction.getDescription());

		// validate Post
		RamlMimeType getMimeType = getAction.getBody().get(MediaType.TEXT_PLAIN_VALUE);
		assertNull(getMimeType);
		RamlMimeType postMimeType = postAction.getBody().get(MediaType.TEXT_PLAIN_VALUE);
		assertNotNull(postMimeType);

	}

	@Test
	public void test_simpleGetAndPostWithOneParameter() {
		RamlResource testResource = baseResourceTestController.getResource("/base").getResource("/oneParameter");
		assertEquals("Assert resources size", 2, testResource.getActions().size());
		RamlAction getAction = testResource.getActions().get(RamlActionType.GET);
		RamlAction postAction = testResource.getActions().get(RamlActionType.POST);
		assertNotNull(getAction);
		assertNotNull(postAction);
		validateSimpleAjaxResponse(getAction);
		validateSimpleAjaxResponse(postAction);

		assertEquals("Assert Javadoc", COMMENT_JAVADOC, getAction.getDescription());
		assertEquals("Assert Javadoc", COMMENT_JAVADOC, postAction.getDescription());

		// validate Get
		String paramName = "param1";
		assertEquals("Check that parameter was placed in query", 1, getAction.getQueryParameters().size());
		RamlQueryParameter queryParameter = getAction.getQueryParameters().get(paramName);
		assertEquals("Check that parameter was placed in query", RamlParamType.STRING, queryParameter.getType());
		assertEquals("Assert Javadoc", combineConstantAndName(PARAM_JAVADOC, paramName),
				queryParameter.getDescription());
		assertEquals("Assert Javadoc", COMMENT_JAVADOC, getAction.getDescription());

		// validate Post
		Map<String, List<RamlFormParameter>> formParameters = postAction.getBody()
				.get(MediaType.APPLICATION_FORM_URLENCODED_VALUE).getFormParameters();
		assertEquals("Check that parameter was placed in form", 1, formParameters.size());
		RamlFormParameter formParameter = formParameters.get(paramName).get(0);
		assertEquals("Assert Javadoc", combineConstantAndName(PARAM_JAVADOC, paramName), formParameter.getDescription());
		assertEquals("Check that parameter type is correct", RamlParamType.STRING, formParameter.getType());
	}

	@Test
	public void test_simpleGetAndPostWithTwoParameters() {
		String paramName;
		RamlResource base = baseResourceTestController.getResource("/base");
		RamlResource testResource = base.getResource("/twoParameter");
		assertEquals("Assert resources size", 2, testResource.getActions().size());
		RamlAction getAction = testResource.getActions().get(RamlActionType.GET);
		RamlAction postAction = testResource.getActions().get(RamlActionType.POST);
		assertNotNull(getAction);
		assertNotNull(postAction);
		validateSimpleAjaxResponse(getAction);
		validateSimpleAjaxResponse(postAction);

		// validate Get
		paramName = "param1";
		assertEquals("Check that parameters were placed in query", 2, getAction.getQueryParameters().size());
		RamlQueryParameter queryParameter = getAction.getQueryParameters().get(paramName);
		assertEquals("Check that parameter was placed in query", RamlParamType.INTEGER, queryParameter.getType());
		assertEquals("Assert Javadoc", combineConstantAndName(PARAM_JAVADOC, paramName),
				queryParameter.getDescription());

		paramName = "nameOverride";
		RamlQueryParameter queryParameter2 = getAction.getQueryParameters().get(paramName);
		assertEquals("Check that parameter was placed in query", RamlParamType.STRING, queryParameter2.getType());
		assertEquals("Check that parameter is required", true, queryParameter2.isRequired());
		assertEquals("Assert Javadoc", combineConstantAndName(PARAM_JAVADOC, "param2"),
				queryParameter2.getDescription());

		// validate Post
		paramName = "param1";
		Map<String, List<RamlFormParameter>> formParameters = postAction.getBody()
				.get(MediaType.APPLICATION_FORM_URLENCODED_VALUE).getFormParameters();
		assertEquals("Check that parameter was placed in form", 2, formParameters.size());
		RamlFormParameter formParameter = formParameters.get(paramName).get(0);
		assertEquals("Check that parameter type is correct", RamlParamType.INTEGER, formParameter.getType());
		assertEquals("Assert Javadoc", combineConstantAndName(PARAM_JAVADOC, paramName), formParameter.getDescription());

		paramName = "nameOverride";
		RamlFormParameter formParameter2 = formParameters.get(paramName).get(0);
		assertEquals("Check that parameter type is correct", RamlParamType.STRING, formParameter2.getType());
		assertEquals("Check that parameter is required", true, formParameter2.isRequired());
		assertEquals("Assert Javadoc", combineConstantAndName(PARAM_JAVADOC, "param2"), formParameter2.getDescription());
	}

	@Test
	public void test_simpleGetAndPostWithOneParameterAndPathVariable() {
		RamlResource testResource = baseResourceTestController.getResource("/base").getResource("/oneParameter")
				.getResource("/{pathVariable}");
		assertEquals("Assert resources size", 1, testResource.getUriParameters().size());
		RamlUriParameter uriParameter = testResource.getUriParameters().get("pathVariable");
		assertEquals("Check that parameter was placed in query", RamlParamType.STRING, uriParameter.getType());
		assertEquals("Check that uriparametersare required", true, uriParameter.isRequired());
		assertEquals("Assert Javadoc", combineConstantAndName(PARAM_JAVADOC, "pathVariable"),
				uriParameter.getDescription());

		assertEquals("Assert resources size", 2, testResource.getActions().size());
		RamlAction getAction = testResource.getActions().get(RamlActionType.GET);
		RamlAction postAction = testResource.getActions().get(RamlActionType.POST);
		assertNotNull(getAction);
		assertNotNull(postAction);
		validateSimpleAjaxResponse(getAction);
		validateSimpleAjaxResponse(postAction);

		assertEquals("Assert Javadoc", COMMENT_JAVADOC, getAction.getDescription());
		assertEquals("Assert Javadoc", COMMENT_JAVADOC, postAction.getDescription());

		// validate Get
		String paramName = "param1";
		assertEquals("Check that parameter was placed in query", 1, getAction.getQueryParameters().size());
		RamlQueryParameter queryParameter = getAction.getQueryParameters().get(paramName);
		assertEquals("Check that parameter was placed in query", RamlParamType.STRING, queryParameter.getType());
		assertEquals("Assert Javadoc", combineConstantAndName(PARAM_JAVADOC, paramName),
				queryParameter.getDescription());
		assertEquals("Assert Javadoc", COMMENT_JAVADOC, getAction.getDescription());

		// validate Post
		Map<String, List<RamlFormParameter>> formParameters = postAction.getBody()
				.get(MediaType.APPLICATION_FORM_URLENCODED_VALUE).getFormParameters();
		assertEquals("Check that parameter was placed in form", 1, formParameters.size());
		RamlFormParameter formParameter = formParameters.get(paramName).get(0);
		assertEquals("Assert Javadoc", combineConstantAndName(PARAM_JAVADOC, paramName), formParameter.getDescription());
		assertEquals("Check that parameter type is correct", RamlParamType.STRING, formParameter.getType());
	}

	@Test
	public void test_simpleGetWithMiscCasesAndPathVariable() {
		RamlResource testResource = baseResourceTestController.getResource("/base").getResource("/miscCases")
				.getResource("/{pathVariable}");
		RamlUriParameter uriParameter = testResource.getUriParameters().get("pathVariable");
		assertEquals("Check that parameter was placed in query", RamlParamType.INTEGER, uriParameter.getType());
		assertEquals("Check that uriparametersare required", true, uriParameter.isRequired());
		assertEquals("Assert Javadoc", combineConstantAndName(PARAM_JAVADOC, "pathVariable"),
				uriParameter.getDescription());

		assertEquals("Assert resources size", 1, testResource.getActions().size());
		RamlAction getAction = testResource.getActions().get(RamlActionType.GET);
		assertNotNull(getAction);
		validateSimpleAjaxResponse(getAction);
		assertEquals("Assert Javadoc", COMMENT_JAVADOC, getAction.getDescription());

		// validate Get
		String paramName = "param1";
		assertEquals("Check that parameter was placed in query", 1, getAction.getQueryParameters().size());
		RamlQueryParameter queryParameter = getAction.getQueryParameters().get(paramName);
		assertEquals("Check that parameter was placed in query", RamlParamType.STRING, queryParameter.getType());
		assertEquals("Assert Javadoc", combineConstantAndName(PARAM_JAVADOC, paramName),
				queryParameter.getDescription());
		assertEquals("Check that param is an array", true, queryParameter.isRepeat());
		assertEquals("Assert Javadoc", COMMENT_JAVADOC, getAction.getDescription());
	}

	@Test
	public void test_simpleGetAndPostWithRequestBodyParameter() {
		RamlResource testResource = baseResourceTestController.getResource("/base").getResource("/oneParameterBodyObject");
		assertEquals("Assert resources size", 2, testResource.getActions().size());
		RamlAction getAction = testResource.getActions().get(RamlActionType.GET);
		RamlAction postAction = testResource.getActions().get(RamlActionType.POST);
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
		RamlResource testResource = baseResourceTestController.getResource("/base").getResource("/methodBodyIgnore");
		assertEquals("Assert resources size", 1, testResource.getActions().size());
		RamlAction putAction = testResource.getActions().get(RamlActionType.PUT);
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
		RamlResource testResource = baseResourceTestController.getResource("/base").getResource("/descriptionTest");
		assertEquals("Assert resources size", 1, testResource.getResources().size());
		assertEquals("Assert description", "aaaaaaaaaaaaaaaa", testResource.getDescription());
		
		RamlResource nestedResource = testResource.getResource("/secondBlock");
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
		RamlResource resourceInfo = parser.extractResourceInfo(BugController.class);
		RamlResource testResource = resourceInfo.getResource("/forgotStuff").getResource("/{somethingID}").getResource("/resendStuff");
		assertNotNull(testResource);
	}

	@Test
	public void test_bug_noValueOnMethod() {
		RamlResource resourceInfo = parser.extractResourceInfo(NoValueController.class);
		assertEquals(0, resourceInfo.getResource("/base").getResources().size());
		assertNotNull(resourceInfo);
	}
}
