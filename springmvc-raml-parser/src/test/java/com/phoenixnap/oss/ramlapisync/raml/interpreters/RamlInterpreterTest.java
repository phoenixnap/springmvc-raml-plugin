package com.phoenixnap.oss.ramlapisync.raml.interpreters;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.lessThan;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Set;

import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.phoenixnap.oss.ramlapisync.data.ApiBodyMetadata;
import com.phoenixnap.oss.ramlapisync.data.ApiResourceMetadata;
import com.phoenixnap.oss.ramlapisync.generation.CodeModelHelper;
import com.phoenixnap.oss.ramlapisync.generation.RamlParser;
import com.phoenixnap.oss.ramlapisync.generation.rules.Rule;
import com.phoenixnap.oss.ramlapisync.generation.rules.Spring4ControllerDecoratorRule;
import com.phoenixnap.oss.ramlapisync.naming.RamlTypeHelper;
import com.phoenixnap.oss.ramlapisync.pojo.PojoGenerationConfig;
import com.phoenixnap.oss.ramlapisync.raml.InvalidRamlResourceException;
import com.phoenixnap.oss.ramlapisync.raml.RamlActionType;
import com.phoenixnap.oss.ramlapisync.raml.RamlDataType;
import com.phoenixnap.oss.ramlapisync.raml.RamlResource;
import com.phoenixnap.oss.ramlapisync.raml.RamlRoot;
import com.phoenixnap.oss.ramlapisync.raml.rjp.raml10v2.RJP10V2RamlModelFactory;
import com.sun.codemodel.JAnnotationUse;
import com.sun.codemodel.JAnnotationValue;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JFieldVar;
import com.sun.codemodel.writer.SingleStreamCodeWriter;

/**
 * @author aweisser
 */
public class RamlInterpreterTest {

    private static RamlRoot ramlRoot;
    
    private static boolean VISUALISE_MODEL_TO_CONSOLE = true;
    
    protected Logger logger = Logger.getLogger(this.getClass());
    protected JCodeModel jCodeModel;

    PojoGenerationConfig config = new PojoGenerationConfig().withPackage("com.gen.foo", "");
    protected static RamlParser defaultRamlParser;

    @BeforeClass
    public static void initRamlRoot() throws InvalidRamlResourceException {
        ramlRoot = new RJP10V2RamlModelFactory().buildRamlRoot("raml/raml-interpreter-test-v10.raml");
        defaultRamlParser = new RamlParser("com.gen.test", "/api", false, false);
        
    }
    
    @Before
    public void setupModel() {
        jCodeModel = new JCodeModel();
    }

    private void checkThatClassContainsAllFields(JClass clazz, String... fields) {
    	assertThat(((JDefinedClass)clazz).fields().keySet(), containsInAnyOrder(fields));
    }
    
    @Test
    public void factoryShouldCreateRamlRootFromFile() {
        assertThat(ramlRoot, is(notNullValue()));
    }
    
    @Test
    public void interpretPostRequestBody() {
        assertThat(ramlRoot, is(notNullValue()));
        RamlResource managers = ramlRoot.getResource("/managers");
        
        RamlDataType managersPostType = managers.getAction(RamlActionType.POST).getBody().get("application/json").getType();
        assertThat(managersPostType, is(notNullValue()));        
        ApiBodyMetadata managersPostRequest = RamlTypeHelper.mapTypeToPojo(config, jCodeModel, ramlRoot, managersPostType.getType(), "testName");
        assertThat(managersPostRequest, is(notNullValue()));        
        assertThat(managersPostRequest.getName(), is("Manager"));      
        assertThat(managersPostRequest.isArray(), is(false)); 
        
		checkModelWithInheritance(jCodeModel);
    }
    
    @Test
    public void checkJSR303_RequiredDefaultsToTrue() {
    	PojoGenerationConfig jsr303Config = new PojoGenerationConfig().withPackage("com.gen.foo", "").withJSR303Annotations(true);
        assertThat(ramlRoot, is(notNullValue()));
        RamlResource managers = ramlRoot.getResource("/managers");
        
        RamlDataType managersPostType = managers.getAction(RamlActionType.POST).getBody().get("application/json").getType();
        assertThat(managersPostType, is(notNullValue()));        
        ApiBodyMetadata managersPostRequest = RamlTypeHelper.mapTypeToPojo(jsr303Config, jCodeModel, ramlRoot, managersPostType.getType(), "testName");
        assertThat(managersPostRequest, is(notNullValue()));        
        assertThat(managersPostRequest.getName(), is("Manager"));      
        assertThat(managersPostRequest.isArray(), is(false)); 
        
		checkModelWithInheritance(jCodeModel); //ensure that things are still generated well
		JDefinedClass person = (JDefinedClass) CodeModelHelper.findFirstClassBySimpleName(jCodeModel, "Person");
		JDefinedClass manager = (JDefinedClass) CodeModelHelper.findFirstClassBySimpleName(jCodeModel, "Manager");
		JDefinedClass department = (JDefinedClass) CodeModelHelper.findFirstClassBySimpleName(jCodeModel, "Department");
		
		checkIfFieldContainsAnnotation(true, manager, NotNull.class);
		checkIfFieldContainsAnnotation(true, person, NotNull.class);
		checkIfFieldContainsAnnotation(true, department, NotNull.class);
		
    }
    
    @Test
    public void checkJSR303() {
    	PojoGenerationConfig jsr303Config = new PojoGenerationConfig().withPackage("com.gen.foo", "").withJSR303Annotations(true);
        assertThat(ramlRoot, is(notNullValue()));
        RamlResource validations = ramlRoot.getResource("/validations");
        
        RamlDataType validationsGetType = validations.getAction(RamlActionType.GET).getResponses().get("200").getBody().get("application/json").getType();
        assertThat(validationsGetType, is(notNullValue()));        
        ApiBodyMetadata validationsGetRequest = RamlTypeHelper.mapTypeToPojo(jsr303Config, jCodeModel, ramlRoot, validationsGetType.getType(), "testName");
        assertThat(validationsGetRequest, is(notNullValue()));        
        assertThat(validationsGetRequest.getName(), is("Validation"));      
        assertThat(validationsGetRequest.isArray(), is(false)); 
        
		JDefinedClass validation = (JDefinedClass) CodeModelHelper.findFirstClassBySimpleName(jCodeModel, "Validation");
		
		checkIfFieldContainsAnnotation(true, validation, NotNull.class, "lastname", "pattern", "length", "id", "anEnum", "anotherEnum");
		checkIfFieldContainsAnnotation(false, validation, NotNull.class, "firstname", "minLength");
		checkIfFieldContainsAnnotation(true, validation, Size.class, "length", "minLength");
		checkIfFieldContainsAnnotation(true, validation, Pattern.class, "pattern");
		
		checkIfAnnotationHasParameter(validation, Size.class, "length","min");
		checkIfAnnotationHasParameter(validation, Size.class, "length","max");
		checkIfAnnotationHasParameter(validation, Size.class, "minLength","min");
		checkIfAnnotationHasParameter(validation, Pattern.class, "pattern","regexp");
		
		checkIfAnnotationHasParameter(validation, DecimalMin.class, "id","value");
		checkIfAnnotationHasParameter(validation, DecimalMax.class, "id","value");
		
		JFieldVar anEnum = getField(validation, "anEnum");
		assertThat(anEnum.type().fullName(), is("com.gen.foo.AnEnum"));
		
		JFieldVar anotherEnum = getField(validation, "anotherEnum");
		assertThat(anotherEnum.type().fullName(), is("com.gen.foo.EnumChecks"));
		
    }
    
    private void checkIfAnnotationHasParameter(JDefinedClass classToCheck, Class<?> annotationClass, String field, String param) {
    	JAnnotationUse annotation = getAnnotationForField(classToCheck, annotationClass, field);
		assertThat(annotation, is(notNullValue()));  
		JAnnotationValue annotationParam = annotation.getAnnotationMembers().get(param);
		assertThat(annotationParam, is(notNullValue())); 
    }
    
    private JFieldVar getField(JDefinedClass classToCheck, String fieldToFind) {
    	for (JFieldVar field : classToCheck.fields().values()) {
    		if( fieldToFind.equals(field.name())) { 
				return field;
    		}
		}
    	return null;
    }
    
    private void checkIfFieldContainsAnnotation(boolean expected, JDefinedClass classToCheck, Class<?> annotationClass, String... fields) {
    	for (JFieldVar field : classToCheck.fields().values()) {
    		if( (fields == null || fields.length == 0 || ArrayUtils.contains(fields, field.name())) 
    				&& !field.name().equals("serialVersionUID")) {
				boolean found = false;
				for(JAnnotationUse annotation : field.annotations()) {
					if (annotation.getAnnotationClass().name().equals(annotationClass.getSimpleName())) {
						found = true;
					}
				}
				assertThat(found, is(expected));
    		}
		}
    }
    
    private JAnnotationUse getAnnotationForField(JDefinedClass classToCheck, Class<?> annotationClass, String field) {
    	for (JFieldVar fieldVar : classToCheck.fields().values()) {
    		if( fieldVar.name().equals(field)) {
				for(JAnnotationUse annotation : fieldVar.annotations()) {
					if (annotation.getAnnotationClass().name().equals(annotationClass.getSimpleName())) {
						return annotation;
					}
				}
    		}
		}
    	return null;
    }
    
    @Test
    public void interpretNestedArrays() {
        assertThat(ramlRoot, is(notNullValue()));
        RamlResource nestedArrayPersons = ramlRoot.getResource("/nestedArrayPersons");
        RamlDataType nestedArrayPersonsGetType = nestedArrayPersons.getAction(RamlActionType.GET).getResponses().get("200").getBody().get("application/json").getType();
        assertThat(nestedArrayPersonsGetType, is(notNullValue()));        
        ApiBodyMetadata nestedArrayPersonsGetRequest = RamlTypeHelper.mapTypeToPojo(config, jCodeModel, ramlRoot, nestedArrayPersonsGetType.getType(), "testName");
        assertThat(nestedArrayPersonsGetRequest, is(notNullValue()));   
        assertThat(nestedArrayPersonsGetRequest.getName(), is("NestedArrayPerson"));
        assertThat(nestedArrayPersonsGetRequest.isArray(), is(true)); 
        String serialiseModel = serialiseModel(jCodeModel);
        int nestedArrayStartIdx = serialiseModel.indexOf("NestedArrayPerson.java--------");
        int nestedArrayEndIdx = serialiseModel.indexOf(".java--------", nestedArrayStartIdx+40);
        int importIdx = serialiseModel.indexOf("import java.util.List;", nestedArrayStartIdx);
        assertThat(importIdx, is(greaterThan(-1)));
        assertThat(importIdx, is(lessThan(nestedArrayEndIdx)));        
		checkIntegration(jCodeModel);
    }
    
    @Test
    public void interpret2ndLevelNestedArrays() {
        assertThat(ramlRoot, is(notNullValue()));
        RamlResource nestedArrayPersons = ramlRoot.getResource("/nestedNestedArrayPersons");
        RamlDataType nestedArrayPersonsGetType = nestedArrayPersons.getAction(RamlActionType.GET).getResponses().get("200").getBody().get("application/json").getType();
        assertThat(nestedArrayPersonsGetType, is(notNullValue()));        
        ApiBodyMetadata nestedArrayPersonsGetRequest = RamlTypeHelper.mapTypeToPojo(config, jCodeModel, ramlRoot, nestedArrayPersonsGetType.getType(), "testName");
        assertThat(nestedArrayPersonsGetRequest, is(notNullValue()));   
        assertThat(nestedArrayPersonsGetRequest.getName(), is("NestedNestedArrayPerson"));
        assertThat(nestedArrayPersonsGetRequest.isArray(), is(true)); 
        String serialiseModel = serialiseModel(jCodeModel);
        int nestedArrayStartIdx = serialiseModel.indexOf("NestedNestedArrayPerson.java--------");
        int nestedArrayEndIdx = serialiseModel.indexOf(".java--------", nestedArrayStartIdx+40);
        int importIdx = serialiseModel.indexOf("import java.util.List;", nestedArrayStartIdx);
        assertThat(importIdx, is(greaterThan(-1)));
        assertThat(importIdx, is(lessThan(nestedArrayEndIdx)));        
		checkIntegration(jCodeModel);
    }
    
    @Test
    public void interpretGetResponseBody() {
        assertThat(ramlRoot, is(notNullValue()));
        RamlResource managers = ramlRoot.getResource("/managers");
        RamlDataType managersGetType = managers.getAction(RamlActionType.GET).getResponses().get("200").getBody().get("application/json").getType();
        assertThat(managersGetType, is(notNullValue()));        
        ApiBodyMetadata managersGetRequest = RamlTypeHelper.mapTypeToPojo(config, jCodeModel, ramlRoot, managersGetType.getType(), "testName");
        assertThat(managersGetRequest, is(notNullValue()));   
        assertThat(managersGetRequest.getName(), is("Manager"));
        assertThat(managersGetRequest.isArray(), is(true)); 
     
		checkModelWithInheritance(jCodeModel);
		checkIntegration(jCodeModel);
    }
    
    @Test
    public void interpretGetResponseBodyAsArray() {
        assertThat(ramlRoot, is(notNullValue()));
        RamlResource persons = ramlRoot.getResource("/persons");
        RamlResource personLists = ramlRoot.getResource("/personLists");
        
        RamlDataType personsGetType = persons.getAction(RamlActionType.GET).getResponses().get("200").getBody().get("application/json").getType();
        RamlDataType personListsGetType = personLists.getAction(RamlActionType.GET).getResponses().get("200").getBody().get("application/json").getType();
        
        assertThat(personsGetType, is(notNullValue()));
        assertThat(personListsGetType, is(notNullValue()));  
        
        ApiBodyMetadata personsGetRequest = RamlTypeHelper.mapTypeToPojo(config, jCodeModel, ramlRoot, personsGetType.getType(), "testName");
        assertThat(personsGetRequest, is(notNullValue()));   
        assertThat(personsGetRequest.getName(), is("Person"));
        assertThat(personsGetRequest.isArray(), is(true)); 
     
		checkModel(jCodeModel);
		checkIntegration(jCodeModel);
        
		setupModel();
        ApiBodyMetadata personListsGetRequest = RamlTypeHelper.mapTypeToPojo(config, jCodeModel, ramlRoot, personListsGetType.getType(), "testName");
        assertThat(personListsGetRequest, is(notNullValue()));   
        assertThat(personListsGetRequest.getName(), is("Person"));
        assertThat(personListsGetRequest.isArray(), is(true)); 
     
		checkModel(jCodeModel);
		checkIntegration(jCodeModel);
    }
    
    @Test
    public void interpretDeleteResponseBody() {
        assertThat(ramlRoot, is(notNullValue()));
        RamlResource managers = ramlRoot.getResource("/managers");
        RamlDataType managersDeleteType = managers.getAction(RamlActionType.DELETE).getResponses().get("200").getBody().get("application/json").getType();
        assertThat(managersDeleteType, is(notNullValue()));     
        
        ApiBodyMetadata managersDeleteRequest = RamlTypeHelper.mapTypeToPojo(config, jCodeModel, ramlRoot, managersDeleteType.getType(), "testName");
        assertThat(managersDeleteRequest, is(nullValue()));   
    }
       
    private void checkIntegration(JCodeModel codeModel) {
    	RamlParser defaultRamlParser = new RamlParser("com.gen.test", "/api", false, false);
    	Rule<JCodeModel, JDefinedClass, ApiResourceMetadata> rule = new Spring4ControllerDecoratorRule();
    	Set<ApiResourceMetadata> extractControllers = defaultRamlParser.extractControllers(codeModel, ramlRoot);
    	for (ApiResourceMetadata controller : extractControllers) {
    		rule.apply(controller, codeModel);
    	}
       
    }
    

	@Test
	public void checkDefaultTypeOfItemInArray() {

		JFieldVar field = getField(getResponsePOJO("/validations"), "testDefArray");
		assertThat(field.type().fullName(), is("java.util.List<Object>"));
	}

	@Test
	public void checkTypeOfFile() {

		JFieldVar field = getField(getResponsePOJO("/validations"), "fileObject");
		assertThat(field.type().fullName(), is("Object"));
	}
	
	@Test
	public void checkBigDecimals() {
		PojoGenerationConfig jsr303Config = new PojoGenerationConfig().withPackage("com.gen.foo", "").withBigDecimals(true);
        assertThat(ramlRoot, is(notNullValue())); 
        RamlResource bigStuff = ramlRoot.getResource("/bigStuff");
        
        RamlDataType getType = bigStuff.getAction(RamlActionType.GET).getResponses().get("200").getBody().get("application/json").getType();
        assertThat(getType, is(notNullValue()));        
        ApiBodyMetadata validationsGetRequest = RamlTypeHelper.mapTypeToPojo(jsr303Config, jCodeModel, ramlRoot, getType.getType(), "testName");
        JFieldVar field = getField((JDefinedClass) CodeModelHelper.findFirstClassBySimpleName(validationsGetRequest.getCodeModel(), "BigStuff"), "theDecimal");
        assertThat(field.type().fullName(), is(BigDecimal.class.getName()));
	}
	
	@Test
	public void checkBigInteger() {
		PojoGenerationConfig jsr303Config = new PojoGenerationConfig().withPackage("com.gen.foo", "").withBigIntegers(true);
        assertThat(ramlRoot, is(notNullValue())); 
        RamlResource bigStuff = ramlRoot.getResource("/bigStuff");
        
        RamlDataType getType = bigStuff.getAction(RamlActionType.GET).getResponses().get("200").getBody().get("application/json").getType();
        assertThat(getType, is(notNullValue()));        
        ApiBodyMetadata validationsGetRequest = RamlTypeHelper.mapTypeToPojo(jsr303Config, jCodeModel, ramlRoot, getType.getType(), "testName");
        JFieldVar field = getField((JDefinedClass) CodeModelHelper.findFirstClassBySimpleName(validationsGetRequest.getCodeModel(), "BigStuff"), "theInteger");
        assertThat(field.type().fullName(), is(BigInteger.class.getName()));
	}

	@Test
	public void checkTypeOfDates() {

		JDefinedClass pojo = getResponsePOJO("/validations");
		
		JFieldVar field = getField(pojo, "dateO");
		assertThat(field.type().fullName(), is("java.util.Date"));

		field = getField(pojo, "timeO");
		assertThat(field.type().fullName(), is("java.util.Date"));

		field = getField(pojo, "dateTO");
		assertThat(field.type().fullName(), is("java.util.Date"));

		field = getField(pojo, "dateT");
		assertThat(field.type().fullName(), is("java.util.Date"));
	}

	private JDefinedClass getResponsePOJO(String resource) {

		assertThat(ramlRoot, is(notNullValue()));
		PojoGenerationConfig jsr303Config = new PojoGenerationConfig().withPackage("com.gen.foo", "")
				.withJSR303Annotations(true);
		RamlResource validations = ramlRoot.getResource(resource);

		RamlDataType validationsGetType = validations.getAction(RamlActionType.GET).getResponses().get("200").getBody()
				.get("application/json").getType();
		RamlTypeHelper.mapTypeToPojo(jsr303Config, jCodeModel, ramlRoot, validationsGetType.getType(), "testName");

		return (JDefinedClass) CodeModelHelper.findFirstClassBySimpleName(jCodeModel, "Validation");
	}
    
    
    @After
    public void visualiseTest() {
    	if (VISUALISE_MODEL_TO_CONSOLE) {
    		visualiseModel(jCodeModel);
    	}
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
	
	private void visualiseModel(JCodeModel codeModel) {
		System.out.println(serialiseModel(codeModel));
	}
	
	private String serialiseModel(JCodeModel codeModel) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
		try {
			jCodeModel.build(new SingleStreamCodeWriter(bos));
			return bos.toString();
		} catch (IOException e) {
			//do nothing
		}
		return "";
	}
	
	

   
}
