package com.phoenixnap.oss.ramlapisync.raml.rjp.raml10v2;

import com.phoenixnap.oss.ramlapisync.raml.RamlRoot;
import com.phoenixnap.oss.ramlapisync.raml.InvalidRamlResourceException;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

/**
 * @author aweisser
 */
public class RJP10V2RamlModelHeaderTest {

    private static RamlRoot ramlRoot, ramlRootEmptyValues;

    @BeforeClass
    public static void initRamlRoot() throws InvalidRamlResourceException {
        ramlRoot = new RJP10V2RamlModelFactory().buildRamlRoot("raml/raml-model-header-test-v10.raml");
        ramlRootEmptyValues = new RJP10V2RamlModelFactory().buildRamlRoot("raml/raml-model-header-test-emptyValues-v10.raml");
    }

    @Test
    public void factoryShouldCreateRamlRootFromFile() {
        assertThat(ramlRoot, is(notNullValue()));
        assertThat(ramlRootEmptyValues, is(notNullValue()));
    }

    @Test
    public void ramlRootShouldReflectBaseUri() {
        assertThat(ramlRoot.getBaseUri(), equalTo("api"));
    }

    @Test
    public void ramlRootShouldHandleEmptyBaseUriWithoutException() {
        assertThat(ramlRootEmptyValues.getBaseUri(), isEmptyOrNullString());
    }


    @Test
    public void ramlRootShouldReflectMediaType() {
        assertThat(ramlRoot.getMediaType(), equalTo("application/json"));
    }

    @Test
    public void ramlRootShouldHandleEmptyMediaTypeWithoutException() {
        assertThat(ramlRootEmptyValues.getMediaType(), isEmptyOrNullString());
    }

    @Test
    public void ramlRootShouldReflectSchemas() {
        Map<String, String> personType = ramlRoot.getSchemas().get(0);
        assertThat(personType, notNullValue());
        assertThat(personType.keySet().iterator().next(), equalTo("Person"));
        assertThat(personType.values().iterator().next(), equalTo("object"));

        Map<String, String> managerType = ramlRoot.getSchemas().get(1);
        assertThat(managerType, notNullValue());
        assertThat(managerType.keySet().iterator().next(), equalTo("Manager"));
        assertThat(managerType.values().iterator().next(), equalTo("Person"));
    }

    @Test
    public void ramlRootShouldHandleEmptySchemasWithoutException() {
        assertThat(ramlRootEmptyValues.getSchemas(), is(emptyIterable()));
    }

}
