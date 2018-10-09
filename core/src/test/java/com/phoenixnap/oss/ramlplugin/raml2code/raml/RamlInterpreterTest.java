package com.phoenixnap.oss.ramlplugin.raml2code.raml;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.lessThan;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import org.apache.commons.lang3.ArrayUtils;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.phoenixnap.oss.ramlplugin.raml2code.data.ApiBodyMetadata;
import com.phoenixnap.oss.ramlplugin.raml2code.data.ApiResourceMetadata;
import com.phoenixnap.oss.ramlplugin.raml2code.exception.InvalidRamlResourceException;
import com.phoenixnap.oss.ramlplugin.raml2code.helpers.CodeModelHelper;
import com.phoenixnap.oss.ramlplugin.raml2code.helpers.NamingHelper;
import com.phoenixnap.oss.ramlplugin.raml2code.helpers.RamlParser;
import com.phoenixnap.oss.ramlplugin.raml2code.helpers.RamlTypeHelper;
import com.phoenixnap.oss.ramlplugin.raml2code.plugin.Config;
import com.phoenixnap.oss.ramlplugin.raml2code.plugin.TestConfig;
import com.phoenixnap.oss.ramlplugin.raml2code.rules.AbstractRuleTestBase;
import com.phoenixnap.oss.ramlplugin.raml2code.rules.Rule;
import com.phoenixnap.oss.ramlplugin.raml2code.rules.Spring4ControllerDecoratorRule;
import com.phoenixnap.oss.ramlplugin.raml2code.rules.TestPojoConfig;
import com.sun.codemodel.JAnnotationUse;
import com.sun.codemodel.JAnnotationValue;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JFieldVar;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JStringLiteral;
import com.sun.codemodel.writer.SingleStreamCodeWriter;

/**
 * @author aweisser
 */
public class RamlInterpreterTest extends AbstractRuleTestBase {

	protected static final Logger logger = LoggerFactory.getLogger(RamlInterpreterTest.class);

	protected static RamlParser defaultRamlParser;

	@BeforeClass
	public static void initRamlRoot() throws InvalidRamlResourceException {
		loadRaml("raml-interpreter-test-v10.raml");
		defaultRamlParser = new RamlParser("/api");

	}

	@Before
	public void setupModel() {
		jCodeModel = new JCodeModel();
	}

	private void checkThatClassContainsAllFields(JClass clazz, String... fields) {
		assertThat(((JDefinedClass) clazz).fields().keySet(), containsInAnyOrder(fields));
	}

	@Test
	public void factoryShouldCreateRamlRootFromFile() {
		assertThat(AbstractRuleTestBase.RAML, is(notNullValue()));
	}

	@Test
	public void interpretPostRequestBody() {
		assertThat(AbstractRuleTestBase.RAML, is(notNullValue()));
		RamlResource managers = AbstractRuleTestBase.RAML.getResource("/managers");

		RamlDataType managersPostType = managers.getAction(RamlActionType.POST).getBody().get("application/json").getType();
		assertThat(managersPostType, is(notNullValue()));
		ApiBodyMetadata managersPostRequest = RamlTypeHelper.mapTypeToPojo(jCodeModel, AbstractRuleTestBase.RAML,
				managersPostType.getType());
		assertThat(managersPostRequest, is(notNullValue()));
		assertThat(managersPostRequest.getName(), is("Manager"));
		assertThat(managersPostRequest.isArray(), is(false));

		checkModelWithInheritance(jCodeModel);
	}

	@Test
	public void checkJSR303_RequiredDefaultsToTrue() {
		TestConfig.setBasePackage("com.gen.foo");
		TestConfig.setIncludeJsr303Annotations(true);

		assertThat(AbstractRuleTestBase.RAML, is(notNullValue()));
		RamlResource managers = AbstractRuleTestBase.RAML.getResource("/managers");

		RamlDataType managersPostType = managers.getAction(RamlActionType.POST).getBody().get("application/json").getType();
		assertThat(managersPostType, is(notNullValue()));
		ApiBodyMetadata managersPostRequest = RamlTypeHelper.mapTypeToPojo(jCodeModel, AbstractRuleTestBase.RAML,
				managersPostType.getType());
		assertThat(managersPostRequest, is(notNullValue()));
		assertThat(managersPostRequest.getName(), is("Manager"));
		assertThat(managersPostRequest.isArray(), is(false));

		checkModelWithInheritance(jCodeModel); // ensure that things are still
												// generated well
		JDefinedClass person = (JDefinedClass) CodeModelHelper.findFirstClassBySimpleName(jCodeModel, "Person");
		JDefinedClass manager = (JDefinedClass) CodeModelHelper.findFirstClassBySimpleName(jCodeModel, "Manager");
		JDefinedClass department = (JDefinedClass) CodeModelHelper.findFirstClassBySimpleName(jCodeModel, "Department");

		checkIfGetterContainsAnnotation(true, manager, NotNull.class, "firstname", "lastname", "id", "department", "clearanceLevel");
		checkIfGetterContainsAnnotation(true, person, NotNull.class, "firstname", "lastname", "id");
		checkIfGetterContainsAnnotation(true, department, NotNull.class, "name");

	}

	@Test
	public void checkJSR303() {
		TestConfig.setBasePackage("com.gen.foo");
		TestConfig.setIncludeJsr303Annotations(true);

		assertThat(AbstractRuleTestBase.RAML, is(notNullValue()));
		RamlResource validations = AbstractRuleTestBase.RAML.getResource("/validations");

		RamlDataType validationsGetType = validations.getAction(RamlActionType.GET).getResponses().get("200").getBody()
				.get("application/json").getType();
		assertThat(validationsGetType, is(notNullValue()));
		ApiBodyMetadata validationsGetRequest = RamlTypeHelper.mapTypeToPojo(jCodeModel, AbstractRuleTestBase.RAML,
				validationsGetType.getType());
		assertThat(validationsGetRequest, is(notNullValue()));
		assertThat(validationsGetRequest.getName(), is("Validation"));
		assertThat(validationsGetRequest.isArray(), is(false));

		JDefinedClass validation = (JDefinedClass) CodeModelHelper.findFirstClassBySimpleName(jCodeModel, "Validation");

		checkIfGetterContainsAnnotation(true, validation, NotNull.class, "lastname", "pattern", "length", "id", "anEnum", "anotherEnum");
		checkIfGetterContainsAnnotation(false, validation, NotNull.class, "firstname", "minLength");
		checkIfGetterContainsAnnotation(true, validation, Size.class, "length", "minLength");
		checkIfGetterContainsAnnotation(true, validation, Pattern.class, "pattern");

		checkIfAnnotationHasParameter(validation, Size.class, "length", "min");
		checkIfAnnotationHasParameter(validation, Size.class, "length", "max");
		checkIfAnnotationHasParameter(validation, Size.class, "minLength", "min");
		checkIfAnnotationHasParameter(validation, Pattern.class, "pattern", "regexp");

		checkIfAnnotationHasParameter(validation, DecimalMin.class, "id", "value");
		checkIfAnnotationHasParameter(validation, DecimalMax.class, "id", "value");

		JFieldVar anEnum = getField(validation, "anEnum");
		assertThat(anEnum.type().fullName(), is("com.gen.foo.model.AnEnum"));

		JFieldVar anotherEnum = getField(validation, "anotherEnum");
		assertThat(anotherEnum.type().fullName(), is("com.gen.foo.model.EnumChecks"));

		JDefinedClass enumChecks = (JDefinedClass) CodeModelHelper.findFirstClassBySimpleName(jCodeModel, "EnumChecks");
		String elementAsString = CodeModelHelper.getElementAsString(enumChecks);
		assertThat(elementAsString, not(containsString("(\"value_with_underscore\", \"value_with_underscore\")")));
		assertThat(elementAsString, containsString("FEE(\"fee\")"));
		assertThat(elementAsString, containsString("TESTFEE(\"testfee\")"));
	}

	private void checkIfAnnotationHasParameter(JDefinedClass classToCheck, Class<?> annotationClass, String field, String param) {
		JAnnotationUse annotation = getAnnotationForGetter(classToCheck, annotationClass, field);
		assertThat(annotation, is(notNullValue()));
		JAnnotationValue annotationParam = annotation.getAnnotationMembers().get(param);
		assertThat(annotationParam, is(notNullValue()));
	}

	private JFieldVar getField(JDefinedClass classToCheck, String fieldToFind) {
		for (JFieldVar field : classToCheck.fields().values()) {
			if (fieldToFind.equals(field.name())) {
				return field;
			}
		}
		return null;
	}

	private JMethod getMethod(JDefinedClass classToCheck, String methodToFind) {
		for (JMethod method : classToCheck.methods()) {
			if (methodToFind.equals(method.name())) {
				return method;
			}
		}
		return null;
	}

	private void checkIfFieldContainsAnnotation(boolean expected, JDefinedClass classToCheck, Class<?> annotationClass, String... fields) {
		for (JFieldVar field : classToCheck.fields().values()) {
			if ((fields == null || fields.length == 0 || ArrayUtils.contains(fields, field.name()))
					&& !field.name().equals("serialVersionUID")) {
				boolean found = false;
				for (JAnnotationUse annotation : field.annotations()) {
					if (annotation.getAnnotationClass().name().equals(annotationClass.getSimpleName())) {
						found = true;
					}
				}
				assertThat(found, is(expected));
			}
		}
	}

	private void checkIfGetterContainsAnnotation(boolean expected, JDefinedClass classToCheck, Class<?> annotationClass, String... fields) {
		List<String> expectedMethodNames = Arrays.asList(fields).stream().map(field -> "get" + NamingHelper.convertToClassName(field))
				.collect(Collectors.toList());
		Map<String, JMethod> actualMethods = classToCheck.methods().stream()
				.collect(Collectors.toMap(method -> method.name(), method -> method));

		for (String expectedMethodName : expectedMethodNames) {
			if (actualMethods.keySet().contains(expectedMethodName)) {
				boolean found = false;
				for (JAnnotationUse annotation : actualMethods.get(expectedMethodName).annotations()) {
					if (annotation.getAnnotationClass().name().equals(annotationClass.getSimpleName())) {
						found = true;
					}
				}
				assertThat(found, is(expected));
			} else {
				fail();
			}
		}
	}

	private JAnnotationUse getAnnotationForField(JDefinedClass classToCheck, Class<?> annotationClass, String field) {
		for (JFieldVar fieldVar : classToCheck.fields().values()) {
			if (fieldVar.name().equals(field)) {
				for (JAnnotationUse annotation : fieldVar.annotations()) {
					if (annotation.getAnnotationClass().name().equals(annotationClass.getSimpleName())) {
						return annotation;
					}
				}
			}
		}
		return null;
	}

	private JAnnotationUse getAnnotationForGetter(JDefinedClass classToCheck, Class<?> annotationClass, String field) {

		Optional<JMethod> methodOptional = classToCheck.methods().stream()
				.filter(method -> method.name().equals("get" + NamingHelper.convertToClassName(field))).findFirst();
		if (methodOptional.isPresent()) {
			Optional<JAnnotationUse> findFirst = methodOptional.get().annotations().stream()
					.filter(annotation -> annotation.getAnnotationClass().name().equals(annotationClass.getSimpleName())).findFirst();
			return findFirst.get();
		}

		return null;
	}

	@Test
	public void interpretNestedArrays() {
		assertThat(AbstractRuleTestBase.RAML, is(notNullValue()));
		RamlResource nestedArrayPersons = AbstractRuleTestBase.RAML.getResource("/nestedArrayPersons");
		RamlDataType nestedArrayPersonsGetType = nestedArrayPersons.getAction(RamlActionType.GET).getResponses().get("200").getBody()
				.get("application/json").getType();
		assertThat(nestedArrayPersonsGetType, is(notNullValue()));
		ApiBodyMetadata nestedArrayPersonsGetRequest = RamlTypeHelper.mapTypeToPojo(jCodeModel, AbstractRuleTestBase.RAML,
				nestedArrayPersonsGetType.getType());
		assertThat(nestedArrayPersonsGetRequest, is(notNullValue()));
		assertThat(nestedArrayPersonsGetRequest.getName(), is("NestedArrayPerson"));
		assertThat(nestedArrayPersonsGetRequest.isArray(), is(true));
		String serialiseModel = serialiseModel(jCodeModel);
		int nestedArrayStartIdx = serialiseModel.indexOf("NestedArrayPerson.java--------");
		int nestedArrayEndIdx = serialiseModel.indexOf(".java--------", nestedArrayStartIdx + 40);
		int importIdx = serialiseModel.indexOf("import java.util.List;", nestedArrayStartIdx);
		assertThat(importIdx, is(greaterThan(-1)));
		assertThat(importIdx, is(lessThan(nestedArrayEndIdx)));
		checkIntegration(jCodeModel);
	}

	@Test
	public void interpret2ndLevelNestedArrays() {
		assertThat(AbstractRuleTestBase.RAML, is(notNullValue()));
		RamlResource nestedArrayPersons = AbstractRuleTestBase.RAML.getResource("/nestedNestedArrayPersons");
		RamlDataType nestedArrayPersonsGetType = nestedArrayPersons.getAction(RamlActionType.GET).getResponses().get("200").getBody()
				.get("application/json").getType();
		assertThat(nestedArrayPersonsGetType, is(notNullValue()));
		ApiBodyMetadata nestedArrayPersonsGetRequest = RamlTypeHelper.mapTypeToPojo(jCodeModel, AbstractRuleTestBase.RAML,
				nestedArrayPersonsGetType.getType());
		assertThat(nestedArrayPersonsGetRequest, is(notNullValue()));
		assertThat(nestedArrayPersonsGetRequest.getName(), is("NestedNestedArrayPerson"));
		assertThat(nestedArrayPersonsGetRequest.isArray(), is(true));
		String serialiseModel = serialiseModel(jCodeModel);
		int nestedArrayStartIdx = serialiseModel.indexOf("NestedNestedArrayPerson.java--------");
		int nestedArrayEndIdx = serialiseModel.indexOf(".java--------", nestedArrayStartIdx + 40);
		int importIdx = serialiseModel.indexOf("import java.util.List;", nestedArrayStartIdx);
		assertThat(importIdx, is(greaterThan(-1)));
		assertThat(importIdx, is(lessThan(nestedArrayEndIdx)));
		checkIntegration(jCodeModel);
	}

	@Test
	public void interpretGetResponseBodyInheritanceModel() {
		assertThat(AbstractRuleTestBase.RAML, is(notNullValue()));
		RamlResource songs = AbstractRuleTestBase.RAML.getResource("/songs");
		RamlDataType songsGetType = songs.getAction(RamlActionType.GET).getResponses().get("200").getBody().get("application/json")
				.getType();
		assertThat(songsGetType, is(notNullValue()));
		ApiBodyMetadata songsGetRequest = RamlTypeHelper.mapTypeToPojo(jCodeModel, AbstractRuleTestBase.RAML, songsGetType.getType());
		assertThat(songsGetRequest, is(notNullValue()));

		assertThat(songsGetRequest.isArray(), is(false));

		JDefinedClass responsePOJO = getResponsePOJO("/songs", "Song");
		assertThat(responsePOJO.name(), is("Song"));
		JFieldVar jFieldVar = responsePOJO.fields().get("fee");
		assertThat(jFieldVar.type().name(), is("FeeCategory"));
		assertThat(jFieldVar.type().getClass().getName(), is(JDefinedClass.class.getName()));
		checkIntegration(jCodeModel);
	}

	@Test
	public void interpretGetResponseBody() {
		assertThat(AbstractRuleTestBase.RAML, is(notNullValue()));
		RamlResource managers = AbstractRuleTestBase.RAML.getResource("/managers");
		RamlDataType managersGetType = managers.getAction(RamlActionType.GET).getResponses().get("200").getBody().get("application/json")
				.getType();
		assertThat(managersGetType, is(notNullValue()));
		ApiBodyMetadata managersGetRequest = RamlTypeHelper.mapTypeToPojo(jCodeModel, AbstractRuleTestBase.RAML, managersGetType.getType());
		assertThat(managersGetRequest, is(notNullValue()));
		assertThat(managersGetRequest.getName(), is("Manager"));
		assertThat(managersGetRequest.isArray(), is(true));

		checkModelWithInheritance(jCodeModel);
		checkIntegration(jCodeModel);
	}

	@Test
	public void interpretGetResponseBodyAsArray() {
		assertThat(AbstractRuleTestBase.RAML, is(notNullValue()));
		RamlResource persons = AbstractRuleTestBase.RAML.getResource("/persons");
		RamlResource personLists = AbstractRuleTestBase.RAML.getResource("/personLists");

		RamlDataType personsGetType = persons.getAction(RamlActionType.GET).getResponses().get("200").getBody().get("application/json")
				.getType();
		RamlDataType personListsGetType = personLists.getAction(RamlActionType.GET).getResponses().get("200").getBody()
				.get("application/json").getType();

		assertThat(personsGetType, is(notNullValue()));
		assertThat(personListsGetType, is(notNullValue()));

		ApiBodyMetadata personsGetRequest = RamlTypeHelper.mapTypeToPojo(jCodeModel, AbstractRuleTestBase.RAML, personsGetType.getType());
		assertThat(personsGetRequest, is(notNullValue()));
		assertThat(personsGetRequest.getName(), is("Person"));
		assertThat(personsGetRequest.isArray(), is(true));

		checkModel(jCodeModel);
		checkIntegration(jCodeModel);

		setupModel();
		ApiBodyMetadata personListsGetRequest = RamlTypeHelper.mapTypeToPojo(jCodeModel, AbstractRuleTestBase.RAML,
				personListsGetType.getType());
		assertThat(personListsGetRequest, is(notNullValue()));
		assertThat(personListsGetRequest.getName(), is("Person"));
		assertThat(personListsGetRequest.isArray(), is(true));

		checkModel(jCodeModel);
		checkIntegration(jCodeModel);
	}

	@Test
	public void interpretDeleteResponseBody() {
		assertThat(AbstractRuleTestBase.RAML, is(notNullValue()));
		RamlResource managers = AbstractRuleTestBase.RAML.getResource("/managers");
		RamlDataType managersDeleteType = managers.getAction(RamlActionType.DELETE).getResponses().get("200").getBody()
				.get("application/json").getType();
		assertThat(managersDeleteType, is(notNullValue()));

		ApiBodyMetadata managersDeleteRequest = RamlTypeHelper.mapTypeToPojo(jCodeModel, AbstractRuleTestBase.RAML,
				managersDeleteType.getType());
		assertThat(managersDeleteRequest, is(nullValue()));
	}

	private void checkIntegration(JCodeModel codeModel) {
		RamlParser defaultRamlParser = new RamlParser("/api");
		Rule<JCodeModel, JDefinedClass, ApiResourceMetadata> rule = new Spring4ControllerDecoratorRule();
		Set<ApiResourceMetadata> extractControllers = defaultRamlParser.extractControllers(codeModel, AbstractRuleTestBase.RAML);
		for (ApiResourceMetadata controller : extractControllers) {
			rule.apply(controller, codeModel);
		}

	}

	@Test
	public void checkDefaultTypeOfItemInArray() {
		JFieldVar field = getField(getResponsePOJO("/validations", "Validation"), "testDefArray");
		assertThat(field.type().fullName(), is("java.util.List<Object>"));
	}

	@Test
	public void checkTypeOfFile() {

		JFieldVar field = getField(getResponsePOJO("/validations", "Validation"), "fileObject");
		assertThat(field.type().fullName(), is("Object"));
	}

	@Test
	public void checkBigDecimals() {
		((TestPojoConfig) Config.getPojoConfig()).setUseBigDecimals(true);

		assertThat(AbstractRuleTestBase.RAML, is(notNullValue()));
		RamlResource bigStuff = AbstractRuleTestBase.RAML.getResource("/bigStuff");

		RamlDataType getType = bigStuff.getAction(RamlActionType.GET).getResponses().get("200").getBody().get("application/json").getType();
		assertThat(getType, is(notNullValue()));
		ApiBodyMetadata validationsGetRequest = RamlTypeHelper.mapTypeToPojo(jCodeModel, AbstractRuleTestBase.RAML, getType.getType());
		JFieldVar field = getField(
				(JDefinedClass) CodeModelHelper.findFirstClassBySimpleName(validationsGetRequest.getCodeModel(), "BigStuff"), "theDecimal");
		assertThat(field.type().fullName(), is(BigDecimal.class.getName()));
	}

	@Test
	public void checkBigInteger() {
		((TestPojoConfig) Config.getPojoConfig()).setUseBigIntegers(true);

		assertThat(AbstractRuleTestBase.RAML, is(notNullValue()));
		RamlResource bigStuff = AbstractRuleTestBase.RAML.getResource("/bigStuff");

		RamlDataType getType = bigStuff.getAction(RamlActionType.GET).getResponses().get("200").getBody().get("application/json").getType();
		assertThat(getType, is(notNullValue()));
		ApiBodyMetadata validationsGetRequest = RamlTypeHelper.mapTypeToPojo(jCodeModel, AbstractRuleTestBase.RAML, getType.getType());
		JFieldVar field = getField(
				(JDefinedClass) CodeModelHelper.findFirstClassBySimpleName(validationsGetRequest.getCodeModel(), "BigStuff"), "theInteger");
		assertThat(field.type().fullName(), is(BigInteger.class.getName()));
	}

	@Test
	public void checkBuilderMethods() {
		((TestPojoConfig) Config.getPojoConfig()).setGenerateBuilders(true);

		assertThat(AbstractRuleTestBase.RAML, is(notNullValue()));
		RamlResource bigStuff = AbstractRuleTestBase.RAML.getResource("/bigStuff");

		RamlDataType getType = bigStuff.getAction(RamlActionType.GET).getResponses().get("200").getBody().get("application/json").getType();
		assertThat(getType, is(notNullValue()));
		ApiBodyMetadata validationsGetRequest = RamlTypeHelper.mapTypeToPojo(jCodeModel, AbstractRuleTestBase.RAML, getType.getType());
		JMethod method = getMethod(
				(JDefinedClass) CodeModelHelper.findFirstClassBySimpleName(validationsGetRequest.getCodeModel(), "BigStuff"),
				"withTheInteger");
		assertThat(method, is(notNullValue()));
	}

	@Test
	public void checkTypeOfDates() throws Exception {

		JDefinedClass pojo = getResponsePOJO("/validations", "Validation");

		JFieldVar field = getField(pojo, "dateO");
		assertThat(field.type().fullName(), is("java.util.Date"));
		assertDateFormatAnnotation(field, "yyyy-MM-dd");

		field = getField(pojo, "timeO");
		assertThat(field.type().fullName(), is("java.util.Date"));
		assertDateFormatAnnotation(field, "HH:mm:ss");

		field = getField(pojo, "dateTO");
		assertThat(field.type().fullName(), is("java.util.Date"));
		assertDateFormatAnnotation(field, "yyyy-MM-dd'T'HH:mm:ss");

		field = getField(pojo, "dateT");
		assertThat(field.type().fullName(), is("java.util.Date"));
		assertDateFormatAnnotation(field, "yyyy-MM-dd'T'HH:mm:ssXXX");

		field = getField(pojo, "datetimeRFC2616");
		assertThat(field.type().fullName(), is("java.util.Date"));
		assertDateFormatAnnotation(field, "EEE, dd MMM yyyy HH:mm:ss z");
	}

	private void assertAnnotations(JFieldVar field, String expectedPattern) throws Exception {
		Iterator<JAnnotationUse> iterator = field.annotations().iterator();

		assertEquals(2, field.annotations().size());
		while (iterator.hasNext()) {
			JAnnotationUse jAnnotationUse = iterator.next();
			if (NotNull.class.getName().equals(jAnnotationUse.getAnnotationClass().fullName())) {
				// do nothing
			} else if (JsonFormat.class.getName().equals(jAnnotationUse.getAnnotationClass().fullName())) {
				assertPatternValue(jAnnotationUse, expectedPattern);
			} else {
				fail();
			}
		}
	}

	private void assertDateFormatAnnotation(JFieldVar field, String expectedPattern) throws Exception {
		assertEquals(1, field.annotations().size());
		Optional<JAnnotationUse> optionalAnnotation = field.annotations().stream().findFirst();
		if (optionalAnnotation.isPresent() && JsonFormat.class.getName().equals(optionalAnnotation.get().getAnnotationClass().fullName())) {
			assertPatternValue(optionalAnnotation.get(), expectedPattern);
		} else {
			fail();
		}
	}

	private void assertPatternValue(JAnnotationUse jAnnotationUse, String expectedPattern) throws Exception {
		JAnnotationValue jAnnotationValue = jAnnotationUse.getAnnotationMembers().get("pattern");
		Field value = jAnnotationValue.getClass().getDeclaredField("value");
		value.setAccessible(true);
		JStringLiteral object = (JStringLiteral) value.get(jAnnotationValue);
		assertThat(object.str, is(expectedPattern));
	}

	private JDefinedClass getResponsePOJO(String resource, String pojoName) {

		assertThat(AbstractRuleTestBase.RAML, is(notNullValue()));
		// PojoGenerationConfig jsr303Config = new
		// PojoGenerationConfig().withPackage("com.gen.foo", "")
		// .withJSR303Annotations(true);
		RamlResource validations = AbstractRuleTestBase.RAML.getResource(resource);

		RamlDataType validationsGetType = validations.getAction(RamlActionType.GET).getResponses().get("200").getBody()
				.get("application/json").getType();
		RamlTypeHelper.mapTypeToPojo(jCodeModel, AbstractRuleTestBase.RAML, validationsGetType.getType());

		return (JDefinedClass) CodeModelHelper.findFirstClassBySimpleName(jCodeModel, pojoName);
	}

	private void checkModelWithInheritance(JCodeModel codeModel) {
		checkModel(codeModel);

		JClass manager = CodeModelHelper.findFirstClassBySimpleName(codeModel, "Manager");
		JClass department = CodeModelHelper.findFirstClassBySimpleName(codeModel, "Department");

		assertThat(manager, instanceOf(JDefinedClass.class));
		assertThat(department, instanceOf(JDefinedClass.class));

		checkThatClassContainsAllFields(manager, "clearanceLevel", "department", "serialVersionUID");
		checkThatClassContainsAllFields(department, "name", "serialVersionUID");
	}

	private void checkModel(JCodeModel codeModel) {
		JClass person = CodeModelHelper.findFirstClassBySimpleName(codeModel, "Person");

		assertThat(person, instanceOf(JDefinedClass.class));

		checkThatClassContainsAllFields(person, "id", "firstname", "lastname", "serialVersionUID");
	}

	private String serialiseModel(JCodeModel codeModel) {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		try {
			jCodeModel.build(new SingleStreamCodeWriter(bos));
			return bos.toString();
		} catch (IOException e) {
			// do nothing
		}
		return "";
	}

}
