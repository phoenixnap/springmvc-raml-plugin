package com.phoenixnap.oss.ramlapisync.raml.rjp.raml10v2;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import java.util.List;
import java.util.Map;

import org.junit.BeforeClass;
import org.junit.Test;
import org.raml.v2.api.model.v10.security.SecurityScheme;

import com.phoenixnap.oss.ramlapisync.raml.InvalidRamlResourceException;
import com.phoenixnap.oss.ramlapisync.raml.RamlAction;
import com.phoenixnap.oss.ramlapisync.raml.RamlActionType;
import com.phoenixnap.oss.ramlapisync.raml.RamlHeader;
import com.phoenixnap.oss.ramlapisync.raml.RamlMimeType;
import com.phoenixnap.oss.ramlapisync.raml.RamlQueryParameter;
import com.phoenixnap.oss.ramlapisync.raml.RamlRoot;
import com.phoenixnap.oss.ramlapisync.raml.RamlSecurityReference;
import com.phoenixnap.oss.ramlapisync.raml.RamlUriParameter;

/**
 * @author Aleksandar Stojsavljevic
 * @since 0.10.0
 */
public class RJP10V2RamlActionTest {

	private static RamlAction ramlGetAction;
	private static RamlAction ramlGetPersonByIdAction;
	private static RamlAction ramlPostAction;
	private static RamlAction ramlGetSubresourceByIdAction;

    @BeforeClass
    public static void initRamlRoot() throws InvalidRamlResourceException {
    	RamlRoot ramlRoot = new RJP10V2RamlModelFactory().buildRamlRoot("raml/raml-action-test-v10.raml");
    	ramlGetAction = ramlRoot.getResource("/persons").getAction(RamlActionType.GET);
    	ramlPostAction = ramlRoot.getResource("/persons").getAction(RamlActionType.POST);
		ramlGetPersonByIdAction = ramlRoot.getResource("/persons").getResource("/{personId}")
				.getAction(RamlActionType.GET);
		ramlGetSubresourceByIdAction = ramlRoot.getResource("/managers").getResource("/{managerId}")
				.getResource("/subresources").getResource("/{subresourceId}").getAction(RamlActionType.GET);
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
		assertThat(testXQueryParameter.getDefaultValue(), equalTo("defvalue"));
    	assertThat(testXQueryParameter.getMinLength().intValue(), equalTo(3));
		assertThat(testXQueryParameter.getMaxLength().intValue(), equalTo(10));
		assertThat(testXQueryParameter.getPattern(), equalTo("^[a-zA-Z]+$"));
        
        RamlQueryParameter testYQueryParameter = queryParameters.get("testY");
    	assertThat(testYQueryParameter.getDisplayName(), equalTo("testY"));
        assertThat(testYQueryParameter.getType().toString(), equalTo("INTEGER"));

		RamlQueryParameter testZQueryParameter = queryParameters.get("testZ");
		assertThat(testZQueryParameter.getType().toString(), equalTo("INTEGER"));
		assertThat(testZQueryParameter.getMinimum().intValue(), equalTo(1));
		assertThat(testZQueryParameter.getMaximum().intValue(), equalTo(10));
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

	@Test
	public void ramlActionShouldReflectSecurity() {
		List<RamlSecurityReference> securedBy = ramlGetAction.getSecuredBy();
		assertThat(securedBy.size(), equalTo(1));
		assertThat(securedBy.get(0).getName(), is(nullValue()));

		securedBy = ramlPostAction.getSecuredBy();
		assertThat(securedBy.size(), equalTo(1));
		assertThat(securedBy.get(0).getName(), is("oauth2"));

		SecurityScheme securityScheme = ((RJP10V2RamlSecurityReference) securedBy.get(0)).getSecuritySchemeRef()
				.securityScheme();

		assertThat(securityScheme.type(), equalTo("OAuth 2.0"));
		assertThat(securityScheme.description().value(), equalTo("OAuth 2.0 Authentication"));
		assertThat(securityScheme.displayName().value(), equalTo("OAuth 2.0 Auth"));
		assertThat(securityScheme.settings().accessTokenUri().value(),
				equalTo("https://accounts.google.com/o/oauth2/token"));
		assertThat(securityScheme.settings().authorizationUri().value(),
				equalTo("https://accounts.google.com/o/oauth2/auth"));
		assertThat(securityScheme.settings().authorizationGrants(), hasItem("authorization_code"));
		assertThat(securityScheme.settings().authorizationGrants(), hasItem("client_credentials"));
		assertThat(securityScheme.settings().authorizationGrants(), hasItem("password"));
		assertThat(securityScheme.settings().authorizationGrants(), hasItem("implicit"));
		assertThat(securityScheme.settings().scopes(), hasItem("openid"));
		assertThat(securityScheme.settings().scopes(), hasItem("session"));
		assertThat(securityScheme.settings().scopes(), hasItem("read"));
		assertThat(securityScheme.settings().scopes(), hasItem("write"));
	}

	@Test
	public void ramlActionShouldReflectUriParameter() {
		RamlUriParameter ramlUriParameter = ramlGetPersonByIdAction.getResource().getUriParameters().get("personId");

		assertThat(ramlUriParameter.getDescription(), equalTo("id of a person"));
		assertThat(ramlUriParameter.getDisplayName(), equalTo("personId"));
		assertThat(ramlUriParameter.getType().toString(), equalTo("INTEGER"));
	}

	@Test
	public void ramlActionShouldReflectResolvedUriParameter() {
		Map<String, RamlUriParameter> resolvedUriParameters = ramlGetSubresourceByIdAction.getResource()
				.getResolvedUriParameters();

		RamlUriParameter managerIdUriParameter = resolvedUriParameters.get("managerId");
		assertThat(managerIdUriParameter.getType().toString(), equalTo("INTEGER"));
		assertThat(managerIdUriParameter.getDescription(), equalTo("id of a manager"));
		assertThat(managerIdUriParameter.isRequired(), is(false));

		RamlUriParameter subresourceIdUriParameter = resolvedUriParameters.get("subresourceId");
		assertThat(subresourceIdUriParameter.getType().toString(), equalTo("NUMBER"));
		assertThat(subresourceIdUriParameter.getDisplayName(), equalTo("subresourceId"));
		assertThat(subresourceIdUriParameter.getDefaultValue(), equalTo("123"));
	}

}
