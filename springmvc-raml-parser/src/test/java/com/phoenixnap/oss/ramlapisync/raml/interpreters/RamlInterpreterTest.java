package com.phoenixnap.oss.ramlapisync.raml.interpreters;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.notNullValue;

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
        ApiBodyMetadata managersPostRequest = RamlTypeHelper.mapTypeToPojo(jCodeModel, ramlRoot, managersPostType.getType(), "com.gen.foo", "testName");
        assertThat(managersPostRequest, is(notNullValue()));        
        assertThat(managersPostRequest.getName(), is("Manager"));      
        
		checkModel(jCodeModel);
    }
    
    @Test
    public void interpretGetResponseBody() {
        assertThat(ramlRoot, is(notNullValue()));
        RamlResource managers = ramlRoot.getResource("/managers");
        RamlDataType managersGetType = managers.getAction(RamlActionType.GET).getResponses().get("200").getBody().get("application/json").getType();
        assertThat(managersGetType, is(notNullValue()));        
        ApiBodyMetadata managersGetRequest = RamlTypeHelper.mapTypeToPojo(jCodeModel, ramlRoot, managersGetType.getType(), "com.gen.foo", "testName");
        assertThat(managersGetRequest, is(notNullValue()));   
        assertThat(managersGetRequest.getName(), is("List<Manager>"));     
     
		checkModel(jCodeModel);
		checkIntegration(jCodeModel);
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

	private void checkModel(JCodeModel codeModel) {
		JClass person = CodeModelHelper.findFirstClassBySimpleName(codeModel, "Person");
		JClass manager = CodeModelHelper.findFirstClassBySimpleName(codeModel, "Manager");
		JClass department = CodeModelHelper.findFirstClassBySimpleName(codeModel, "Department");
		
		assertThat(person, instanceOf(JDefinedClass.class));
		assertThat(manager, instanceOf(JDefinedClass.class));
		assertThat(department, instanceOf(JDefinedClass.class));
		
		checkThatClassContainsAllFields(person, "id", "firstname", "lastname", "serialVersionUID");
		checkThatClassContainsAllFields(manager, "clearanceLevel", "department", "serialVersionUID");
		checkThatClassContainsAllFields(department, "name", "serialVersionUID");
	}
	
	private void visualiseModel(JCodeModel codeModel) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
		try {
			jCodeModel.build(new SingleStreamCodeWriter(bos));
			System.out.println(bos.toString());
		} catch (IOException e) {
			//do nothing
		}
	}

   
}
