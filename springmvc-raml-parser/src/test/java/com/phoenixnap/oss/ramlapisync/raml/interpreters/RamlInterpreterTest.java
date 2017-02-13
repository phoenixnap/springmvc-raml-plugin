package com.phoenixnap.oss.ramlapisync.raml.interpreters;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import org.apache.log4j.Logger;
import org.junit.BeforeClass;
import org.junit.Test;

import com.phoenixnap.oss.ramlapisync.data.ApiResourceMetadata;
import com.phoenixnap.oss.ramlapisync.generation.RamlParser;
import com.phoenixnap.oss.ramlapisync.raml.InvalidRamlResourceException;
import com.phoenixnap.oss.ramlapisync.raml.RamlRoot;
import com.phoenixnap.oss.ramlapisync.raml.rjp.raml10v2.RJP10V2RamlModelFactory;
import com.sun.codemodel.JCodeModel;

/**
 * @author aweisser
 */
public class RamlInterpreterTest {

    private static RamlRoot ramlRoot;
    
    protected Logger logger = Logger.getLogger(this.getClass());
    protected JCodeModel jCodeModel;

    protected static RamlParser defaultRamlParser;

    private ApiResourceMetadata controllerMetadata;


    @BeforeClass
    public static void initRamlRoot() throws InvalidRamlResourceException {
        ramlRoot = new RJP10V2RamlModelFactory().buildRamlRoot("raml/raml-interpreter-test-v10.raml");
        defaultRamlParser = new RamlParser("com.gen.test", "/api", false, false);
    }

    @Test
    public void factoryShouldCreateRamlRootFromFile() {
        assertThat(ramlRoot, is(notNullValue()));
        
        
    }

   
}
