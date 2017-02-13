package com.phoenixnap.oss.ramlapisync.raml.interpreters;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.phoenixnap.oss.ramlapisync.data.ApiBodyMetadata;
import com.phoenixnap.oss.ramlapisync.generation.RamlParser;
import com.phoenixnap.oss.ramlapisync.naming.RamlTypeHelper;
import com.phoenixnap.oss.ramlapisync.raml.InvalidRamlResourceException;
import com.phoenixnap.oss.ramlapisync.raml.RamlActionType;
import com.phoenixnap.oss.ramlapisync.raml.RamlDataType;
import com.phoenixnap.oss.ramlapisync.raml.RamlResource;
import com.phoenixnap.oss.ramlapisync.raml.RamlRoot;
import com.phoenixnap.oss.ramlapisync.raml.rjp.raml10v2.RJP10V2RamlModelFactory;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.writer.SingleStreamCodeWriter;

/**
 * @author aweisser
 */
public class RamlInterpreterTest {

    private static RamlRoot ramlRoot;
    
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

    @Test
    public void factoryShouldCreateRamlRootFromFile() {
        assertThat(ramlRoot, is(notNullValue()));
        RamlResource managers = ramlRoot.getResource("/managers");
        
        RamlDataType managersPostType = managers.getAction(RamlActionType.POST).getBody().get("application/json").getType();
        assertThat(managersPostType, is(notNullValue()));        
        ApiBodyMetadata managersPostRequest = RamlTypeHelper.mapTypeToPojo(jCodeModel, ramlRoot, managersPostType.getType(), "com.gen.foo", "testName");
        assertThat(managersPostRequest, is(notNullValue()));
        
        RamlDataType managersGetType = managers.getAction(RamlActionType.GET).getResponses().get("200").getBody().get("application/json").getType();
        assertThat(managersGetType, is(notNullValue()));        
        ApiBodyMetadata managersGetRequest = RamlTypeHelper.mapTypeToPojo(jCodeModel, ramlRoot, managersGetType.getType(), "com.gen.foo", "testName");
        assertThat(managersGetRequest, is(notNullValue()));
        
    	ByteArrayOutputStream bos = new ByteArrayOutputStream();
		try {
			jCodeModel.build(new SingleStreamCodeWriter(bos));
		} catch (IOException e) {
			//do nothing
		}
		System.out.println(bos.toString());
        
    }

   
}
