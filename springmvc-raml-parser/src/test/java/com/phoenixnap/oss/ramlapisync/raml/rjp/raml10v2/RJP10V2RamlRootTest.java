package com.phoenixnap.oss.ramlapisync.raml.rjp.raml10v2;

import com.phoenixnap.oss.ramlapisync.raml.RamlResource;
import com.phoenixnap.oss.ramlapisync.raml.RamlRoot;
import com.phoenixnap.oss.ramlapisync.raml.InvalidRamlResourceException;
import org.junit.BeforeClass;
import org.junit.Test;
import org.raml.model.Raml;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.phoenixnap.oss.ramlapisync.SrpMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

/**
 * @author aweisser
 */
public class RJP10V2RamlRootTest {

    private static RamlRoot ramlRoot, ramlRootEmptyValues;

    @BeforeClass
    public static void initRamlRoot() throws InvalidRamlResourceException {
        ramlRoot = new RJP10V2RamlModelFactory().buildRamlRoot("raml/raml-root-test-v10.raml");
        ramlRootEmptyValues = new RJP10V2RamlModelFactory().buildRamlRoot("raml/raml-root-test-emptyValues-v10.raml");
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

    @Test
    public void ramlRootShouldReflectToplevelResources() {
        assertThat(ramlRoot.getResources(), is(mapWithSize(2)));
        assertThat(ramlRoot.getResources().keySet(), hasItems("/persons", "/managers"));

        Iterable<Object> topLevelItems = ramlRoot.getResources().values().stream().collect(Collectors.toList());
        assertThat(topLevelItems, everyItem(is(anything())));

        Iterable<RamlResource> topLevelRamlResources = ramlRoot.getResources().values().stream().collect(Collectors.toList());
        assertThat(topLevelRamlResources, everyItem(isA(RamlResource.class)));
    }

    @Test
    public void ramlRootShouldReflectToplevelResourceByNameIgnoringSlashes() {
        assertThat(ramlRoot.getResource("persons"), is(notNullValue()));
        assertThat(ramlRoot.getResource("/persons"), is(notNullValue()));
        assertThat(ramlRoot.getResource("/persons/"), is(notNullValue()));
        assertThat(ramlRoot.getResource("persons/"), is(notNullValue()));
    }

    @Test
    public void ramlRootShouldHandleEmptyToplevelResourcesWithoutException() {
        assertThat(ramlRootEmptyValues.getResources(), is(emptyMap()));
    }

}
