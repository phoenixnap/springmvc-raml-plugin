package com.phoenixnap.oss.ramlapisync.raml.rjp.raml10v2;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import java.util.Map;

import org.junit.BeforeClass;
import org.junit.Test;

import com.phoenixnap.oss.ramlapisync.raml.InvalidRamlResourceException;
import com.phoenixnap.oss.ramlapisync.raml.RamlAction;
import com.phoenixnap.oss.ramlapisync.raml.RamlActionType;
import com.phoenixnap.oss.ramlapisync.raml.RamlHeader;
import com.phoenixnap.oss.ramlapisync.raml.RamlMimeType;
import com.phoenixnap.oss.ramlapisync.raml.RamlQueryParameter;
import com.phoenixnap.oss.ramlapisync.raml.RamlRoot;

/**
 * @author Aleksandar Stojsavljevic
 * @since 0.10.0
 */
public class RJP10V2RamlActionTest {

	private static RamlAction ramlGetAction;
    private static RamlAction ramlPostAction;

    @BeforeClass
    public static void initRamlRoot() throws InvalidRamlResourceException {
    	RamlRoot ramlRoot = new RJP10V2RamlModelFactory().buildRamlRoot("raml/raml-action-test-v10.raml");
    	ramlGetAction = ramlRoot.getResource("persons").getAction(RamlActionType.GET);
    	ramlPostAction = ramlRoot.getResource("persons").getAction(RamlActionType.POST);
    }

    @Test
    public void factoryShouldCreateRamlActionFromFile() {
    	assertThat(ramlGetAction, is(notNullValue()));
        assertThat(ramlPostAction, is(notNullValue()));
    }

    @Test
    public void ramlActionShouldReflectDisplayName() {
    	assertThat(ramlGetAction.getDisplayName(), equalTo("Persons"));
    }
    
    @Test
    public void ramlActionShouldReflectDescription() {
    	assertThat(ramlGetAction.getDescription(), equalTo("Get all persons"));
        assertThat(ramlPostAction.getDescription(), equalTo("Create new person"));
    }
    
    @Test
    public void ramlActionShouldReflectQueryParameters() {
    	Map<String, RamlQueryParameter> queryParameters = ramlGetAction.getQueryParameters();
    	
    	RamlQueryParameter testXQueryParameter = queryParameters.get("testX");
    	assertThat(testXQueryParameter.getDisplayName(), equalTo("testX"));
    	assertThat(testXQueryParameter.getType().toString(), equalTo("STRING"));
    	assertThat(testXQueryParameter.getDefaultValue(), equalTo("def_value"));
    	assertThat(testXQueryParameter.getMinLength().intValue(), equalTo(3));
        assertThat(testXQueryParameter.getMaxLength().intValue(), equalTo(10));
        
        RamlQueryParameter testYQueryParameter = queryParameters.get("testY");
    	assertThat(testYQueryParameter.getDisplayName(), equalTo("testY"));
        assertThat(testYQueryParameter.getType().toString(), equalTo("INTEGER"));
    }
    
    @Test
    public void ramlActionShouldReflectHeaders() {
    	Map<String, RamlHeader> headers = ramlGetAction.getHeaders();
    	
    	RamlHeader ramlHeader = headers.get("Accept");
    	assertThat(ramlHeader.getDisplayName(), equalTo("Accept"));
    	assertThat(ramlHeader.getDefaultValue(), equalTo("application/json"));
    	assertThat(ramlHeader.getDescription(), equalTo("Response type acceptable by client"));
    	assertThat(ramlHeader.getType().toString(), equalTo("STRING"));
    }
    
    @Test
    public void ramlActionShouldReflectBody() {
    	Map<String, RamlMimeType> body = ramlPostAction.getBody();
    	
    	RamlMimeType ramlMimeType = body.get("application/json");
    	assertThat(ramlMimeType.getType().getType().type(), equalTo("Person"));
    }

}
