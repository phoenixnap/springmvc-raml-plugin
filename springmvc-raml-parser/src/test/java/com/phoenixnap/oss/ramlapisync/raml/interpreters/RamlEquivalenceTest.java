package com.phoenixnap.oss.ramlapisync.raml.interpreters;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;
import org.hamcrest.core.IsNull;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.phoenixnap.oss.ramlapisync.generation.RamlParser;
import com.phoenixnap.oss.ramlapisync.raml.InvalidRamlResourceException;
import com.phoenixnap.oss.ramlapisync.raml.RamlAbstractParam;
import com.phoenixnap.oss.ramlapisync.raml.RamlAction;
import com.phoenixnap.oss.ramlapisync.raml.RamlActionType;
import com.phoenixnap.oss.ramlapisync.raml.RamlResource;
import com.phoenixnap.oss.ramlapisync.raml.RamlRoot;
import com.phoenixnap.oss.ramlapisync.raml.RamlUriParameter;
import com.phoenixnap.oss.ramlapisync.raml.rjp.raml08v1.RJP08V1RamlModelFactory;
import com.phoenixnap.oss.ramlapisync.raml.rjp.raml10v2.RJP10V2RamlModelFactory;

/**
 * @author kurtpa
 */
public class RamlEquivalenceTest {

    private static RamlRoot raml10Root;
    private static RamlRoot raml08Root;    
    
    protected Logger logger = Logger.getLogger(this.getClass());

    protected static RamlParser defaultRamlParser;

    @BeforeClass
    public static void initRamlRoot() throws InvalidRamlResourceException {
    	raml10Root = new RJP10V2RamlModelFactory().buildRamlRoot("raml/raml-equivalence-test-v10.raml");
    	raml08Root = new RJP08V1RamlModelFactory().buildRamlRoot("raml/raml-equivalence-test-v08.raml");
        defaultRamlParser = new RamlParser("com.gen.test", "/api", false, false);
        
    }
    
    @Before
    public void setup() {
    }
    
    @Test
    public void factoryShouldCreateRamlRootFromFile() {
        assertRootCreation();
    }

	private void assertRootCreation() {
		assertThat(raml10Root, is(notNullValue()));
        assertThat(raml08Root, is(notNullValue()));
	}
    
    @Test
    public void test_getResource() {
    	assertRootCreation();
    	
        RamlResource managers08 = raml08Root.getResource("/managers");
        RamlResource managers10 = raml10Root.getResource("/managers");
        
        checkResources(managers08, managers10);
        
        RamlResource managersWithoutPreSlash08 = raml08Root.getResource("managers");
        RamlResource managersWithoutPreSlash10 = raml10Root.getResource("managers");
        
        checkResources(managersWithoutPreSlash08, managersWithoutPreSlash10);
        
    }
    
    @Test
    public void test_getResource_Nesting() {
    	assertRootCreation();
        
        RamlResource managersNested08 = raml08Root.getResource("/managers/{managerId}");
        RamlResource managersNested10 = raml10Root.getResource("/managers/{managerId}");
        
        RamlResource managersNested2ndLevel08 = raml08Root.getResource("/managers/{managerId}/office");
        RamlResource managersNested2ndLevel10 = raml10Root.getResource("/managers/{managerId}/office");
        
        assertThat(managersNested2ndLevel08, equalTo(managersNested08.getResource("/office")));
        assertThat(managersNested2ndLevel10, equalTo(managersNested10.getResource("/office")));
        
        assertThat(managersNested08.getResource("office"), equalTo(managersNested10.getResource("office")));       
        
        RamlResource nonexistant08 = raml08Root.getResource("/managers/{managerId}/doesntExist/office");
        RamlResource nonexistant10 = raml10Root.getResource("/managers/{managerId}/doesntExist/office");
        
        checkResources(managersNested08, managersNested10);
        checkResources(managersNested2ndLevel08, managersNested2ndLevel10);
        assertThat(nonexistant08, equalTo(nonexistant10));
        
    }

	private void checkResources(RamlResource resource08, RamlResource resource10) {
		if (resource08 == null) {
			logger.debug("Checking resources are null");
			assertThat(resource10, IsNull.nullValue());
		} else {
			logger.debug("Checking resources: " + resource08.getRelativeUri() + " against " + resource10.getRelativeUri());
			assertThat(resource08.getRelativeUri(), equalTo(resource10.getRelativeUri()));
	        assertThat(resource08.getUri(), equalTo(resource10.getUri()));
	        assertThat(resource08.getParentUri(), equalTo(resource10.getParentUri()));
	        
	        assertThat(resource08.getDescription(), equalTo(resource10.getDescription()));
	        assertThat(resource08.getDisplayName(), equalTo(resource10.getDisplayName()));
	        
	        if (resource08.getParentResource() != null || resource10.getParentResource() != null) {
	        	assertThat(resource08.getParentResource().getUri(), equalTo(resource10.getParentResource().getUri()));
	        	assertThat(resource08.getParentResource().getRelativeUri(), equalTo(resource10.getParentResource().getRelativeUri()));
	        }
	        
	        for (RamlActionType actionType : RamlActionType.values()) {
	    		checkAction(resource08.getAction(actionType), resource10.getAction(actionType));
	    	}
	        
	        checkActions(resource08.getActions(), resource10.getActions());
	
	        assertThat(resource08.getUriParameters().size(), equalTo(resource10.getUriParameters().size()));
	        assertThat(resource08.getResolvedUriParameters().size(), equalTo(resource10.getResolvedUriParameters().size()));
	        for (Entry<String, RamlUriParameter> resource : resource08.getUriParameters().entrySet()) {
	        	String key08 = resource.getKey();
	        	RamlUriParameter param08 = resource08.getUriParameters().get(key08);
	        	RamlUriParameter param10 = resource10.getUriParameters().get(key08);
	        	checkParams(param08, param10);
	        }
	       
	        assertThat(resource08.getResources().size(), equalTo(resource10.getResources().size()));
	        for (Entry<String, RamlResource> resource : resource08.getResources().entrySet()) {
	        	String key08 = resource.getKey();
	        	RamlResource child08 = resource08.getResources().get(key08);
	        	RamlResource child10 = resource10.getResources().get(key08);
	        	
	        	checkResources(child08, child10);
	        }
		}
	}
    
    private void checkParams(RamlAbstractParam param08, RamlAbstractParam param10) {
    	assertThat(param08.getType(), equalTo(param10.getType()));
    	assertThat(param08.getDefaultValue(), equalTo(param10.getDefaultValue()));
    	assertThat(param08.getDescription(), equalTo(param10.getDescription()));
    	assertThat(param08.getDisplayName(), equalTo(param10.getDisplayName()));
    	assertThat(param08.getExample(), equalTo(param10.getExample()));
	}

	private void checkActions(Map<RamlActionType, RamlAction> actions08, Map<RamlActionType, RamlAction> actions10) {
    	
    	assertThat(actions08.size(), equalTo(actions10.size()));
		
    	for (Entry<RamlActionType, RamlAction> action : actions08.entrySet()) {
    		RamlActionType key08 = action.getKey();
    		RamlAction action08 = actions08.get(key08);
    		RamlAction action10 = actions10.get(key08);
    		checkAction(action08, action10);
    	}
    	
	}

	private void checkAction(RamlAction action08, RamlAction action10) {
		if (action08 == null) {
			logger.debug("Checking actions are null");
			assertThat(action10, IsNull.nullValue());
		} else {
			logger.debug("Checking actions: " + action08.getType() + " against " + action10.getType());
			assertThat(action08.getType(), equalTo(action10.getType()));
			assertThat(action08.getBody().size(), equalTo(action10.getBody().size()));
			assertThat(action08.getDescription(), equalTo(action10.getDescription()));
			assertThat(action08.getDisplayName(), equalTo(action10.getDisplayName()));
			assertThat(action08.getHeaders().size(), equalTo(action10.getHeaders().size()));
			assertThat(action08.getResource().getUri(), equalTo(action10.getResource().getUri()));
		}
		
	}

}
