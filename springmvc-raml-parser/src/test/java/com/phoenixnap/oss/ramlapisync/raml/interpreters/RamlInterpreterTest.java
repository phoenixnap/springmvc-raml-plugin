package com.phoenixnap.oss.ramlapisync.raml.interpreters;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

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
import com.sun.codemodel.JClass;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;
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
    	
        rule.apply(defaultRamlParser.extractControllers(codeModel, ramlRoot).iterator().next(), codeModel);
       
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
