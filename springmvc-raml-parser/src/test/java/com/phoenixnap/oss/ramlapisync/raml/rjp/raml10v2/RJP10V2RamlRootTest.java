package com.phoenixnap.oss.ramlapisync.raml.rjp.raml10v2;

import static com.phoenixnap.oss.ramlapisync.SrpMatchers.emptyMap;
import static com.phoenixnap.oss.ramlapisync.SrpMatchers.mapWithSize;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.anything;
import static org.hamcrest.Matchers.emptyIterable;
import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.everyItem;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.isA;
import static org.hamcrest.Matchers.isEmptyOrNullString;
import static org.hamcrest.Matchers.notNullValue;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.junit.BeforeClass;
import org.junit.Test;
import org.raml.v2.api.model.v10.datamodel.ArrayTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.ObjectTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.StringTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.IntegerTypeDeclaration;

import com.phoenixnap.oss.ramlapisync.raml.InvalidRamlResourceException;
import com.phoenixnap.oss.ramlapisync.raml.RamlActionType;
import com.phoenixnap.oss.ramlapisync.raml.RamlDataType;
import com.phoenixnap.oss.ramlapisync.raml.RamlDocumentationItem;
import com.phoenixnap.oss.ramlapisync.raml.RamlMimeType;
import com.phoenixnap.oss.ramlapisync.raml.RamlResource;
import com.phoenixnap.oss.ramlapisync.raml.RamlRoot;

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
	public void ramlRootShouldReflectDataTypes() {
		Map<String, RamlDataType> personType = ramlRoot.getTypes();

		ObjectTypeDeclaration personDataType = (ObjectTypeDeclaration) personType.get("Person").getType();

		assertThat(personDataType.displayName().value(), equalTo("Person"));
		assertThat(personDataType.type(), equalTo("object"));

		StringTypeDeclaration testXProp = (StringTypeDeclaration) personDataType.properties().get(0);
		assertThat(testXProp.displayName().value(), equalTo("testX"));
		assertThat(testXProp.defaultValue(), equalTo("def_value"));
		assertThat(testXProp.minLength(), equalTo(3));
		assertThat(testXProp.maxLength(), equalTo(10));

		ArrayTypeDeclaration testY = (ArrayTypeDeclaration) personDataType.properties().get(1);
		assertThat(testY.description().value(), equalTo("array attribute"));
		assertThat(testY.example().value(), endsWith("[a,b]"));
		assertThat(testY.items().type(), equalTo("string"));
		assertThat(testY.minItems(), equalTo(2));
		assertThat(testY.maxItems(), equalTo(5));

		ObjectTypeDeclaration managerDataType = (ObjectTypeDeclaration) personType.get("Manager").getType();
		assertThat(managerDataType.type(), equalTo("Person"));

		ArrayTypeDeclaration personsDataType = (ArrayTypeDeclaration) personType.get("Persons").getType();
		assertThat(personsDataType.type(), equalTo("array"));
		assertThat(personsDataType.items().type(), equalTo("Person"));
	}
	
	@Test
	public void ramlRootShouldReflectDataTypesFromLibraries() {
		Map<String, RamlDataType> personType = ramlRoot.getTypes();

		ObjectTypeDeclaration personDataType = (ObjectTypeDeclaration) personType.get("Song").getType();

		assertThat(personDataType.displayName().value(), equalTo("Song"));
		assertThat(personDataType.type(), equalTo("object"));

		StringTypeDeclaration title = (StringTypeDeclaration) personDataType.properties().get(0);
		assertThat(title.displayName().value(), equalTo("title"));
		assertThat(title.example().value(), equalTo("Smells Like Teen Spirit"));
		assertThat(title.minLength(), equalTo(5));
		assertThat(title.maxLength(), equalTo(999));

		StringTypeDeclaration artist = (StringTypeDeclaration) personDataType.properties().get(1);
		assertThat(artist.name(), equalTo("artist"));
		assertThat(artist.required(), equalTo(true));
		
		StringTypeDeclaration album = (StringTypeDeclaration) personDataType.properties().get(2);
		assertThat(album.name(), equalTo("album"));
		assertThat(album.required(), equalTo(false));

		IntegerTypeDeclaration year = (IntegerTypeDeclaration) personDataType.properties().get(3);
		assertThat(year.name(), equalTo("year"));
		assertThat(year.example().value(), equalTo("1991"));
		assertThat(year.required(), equalTo(false));
	}

    @Test
    public void ramlRootShouldHandleEmptySchemasWithoutException() {
        assertThat(ramlRootEmptyValues.getSchemas(), is(emptyIterable()));
    }

    @Test
    public void ramlRootShouldReflectToplevelResources() {
        assertThat(ramlRoot.getResources(), is(mapWithSize(4)));
        assertThat(ramlRoot.getResources().keySet(), hasItems("/persons", "/managers", "/defaultType", "/songs"));

        Iterable<Object> topLevelItems = ramlRoot.getResources().values().stream().collect(Collectors.toList());
        assertThat(topLevelItems, everyItem(is(anything())));

        Iterable<RamlResource> topLevelRamlResources = ramlRoot.getResources().values().stream().collect(Collectors.toList());
        assertThat(topLevelRamlResources, everyItem(isA(RamlResource.class)));
    }

    @Test
    public void ramlRootShouldReflectToplevelResourceByNameIgnoringSlashes() {
    	//Note: The following 2 assertions were removed since they were changed to maintain equivalence with the Raml08 getResource behaviour, 
    	//which will not return these resources if no preceding slash is supplied
        //assertThat(ramlRoot.getResource("persons"), is(notNullValue()));  
    	//assertThat(ramlRoot.getResource("persons/"), is(notNullValue()));
        assertThat(ramlRoot.getResource("/persons"), is(notNullValue()));
        assertThat(ramlRoot.getResource("/persons/"), is(notNullValue()));
    }
    
    @Test
    public void ramlRootShouldReflectBodyWhenDefaultMediaTypeSet() {
        assertThat(ramlRoot.getResource("/defaultType"), is(notNullValue()));
        Map<String, RamlMimeType> body = ramlRoot.getResource("/defaultType").getAction(RamlActionType.GET).getResponses().get("200").getBody();
        assertThat(body.isEmpty(), is(false)); 
    }

    @Test
    public void ramlRootShouldHandleEmptyToplevelResourcesWithoutException() {
        assertThat(ramlRootEmptyValues.getResources(), is(emptyMap()));
    }

	@Test
	public void factoryShouldReflectDocumentation() {
		List<RamlDocumentationItem> documentation = ((RJP10V2RamlRoot) ramlRoot).getDocumentation();

		assertThat(((RJP10V2RamlDocumentationItem) documentation.get(0)).getDocumentationItem().title().value(),
				equalTo("Home"));
		assertThat(((RJP10V2RamlDocumentationItem) documentation.get(0)).getDocumentationItem().content().value(),
				startsWith("Welcome to the _Sample API_ Documentation."));

		assertThat(((RJP10V2RamlDocumentationItem) documentation.get(1)).getDocumentationItem().title().value(),
				equalTo("Legal"));
		assertThat(((RJP10V2RamlDocumentationItem) documentation.get(1)).getDocumentationItem().content().value(),
				startsWith("CWIE (c) All Rights Reserved"));

	}

}
