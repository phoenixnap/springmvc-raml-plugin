package com.phoenixnap.oss.ramlapisync.raml.interpreters;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;
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
    public void checkRoots() {
    	assertRootCreation();
    	
        RamlResource managers08 = raml08Root.getResource("/managers");
        RamlResource managers10 = raml10Root.getResource("/managers");
        
        checkResources(managers08, managers10);
        
    }
    
    @Test
    public void test_getResource_Nesting() {
    	assertRootCreation();
    	
        RamlResource managers08 = raml08Root.getResource("/managers");
        RamlResource managers10 = raml10Root.getResource("/managers");
        
        RamlResource managersNested08 = raml08Root.getResource("/managers/{managerId}");
        RamlResource managersNested10 = raml10Root.getResource("/managers/{managerId}");
        
        RamlResource managersNested2ndLevel08 = raml08Root.getResource("/managers/{managerId}/office");
        RamlResource managersNested2ndLevel10 = raml10Root.getResource("/managers/{managerId}/office");
        
        RamlResource nonexistant08 = raml08Root.getResource("/managers/{managerId}/doesntExist/office");
        RamlResource nonexistant10 = raml10Root.getResource("/managers/{managerId}/doesntExist/office");
        
        checkResources(managers08, managers10);
        checkResources(managersNested08, managersNested10);
        checkResources(managersNested2ndLevel08, managersNested2ndLevel10);
        assertThat(nonexistant08, equalTo(nonexistant10));
        
    }

	private void checkResources(RamlResource resource08, RamlResource resource10) {
		logger.debug("Checking resources: " + resource08.getRelativeUri() + " against " + resource10.getRelativeUri());
		assertThat(resource08.getRelativeUri(), equalTo(resource10.getRelativeUri()));
        assertThat(resource08.getUri(), equalTo(resource10.getUri()));
        if (resource08.getParentResource() != null || resource10.getParentResource() != null) {
        	assertThat(resource08.getParentResource().getUri(), equalTo(resource10.getParentResource().getUri()));
        	assertThat(resource08.getParentResource().getRelativeUri(), equalTo(resource10.getParentResource().getRelativeUri()));
        }
        
        checkActions(resource08.getActions(), resource10.getActions());
        assertThat(resource08.getUriParameters().size(), equalTo(resource10.getUriParameters().size()));
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
    		logger.debug("Checking actions: " + action08.getType() + " against " + action10.getType());
    		assertThat(action08.getType(), equalTo(action10.getType()));
    		assertThat(action08.getBody().size(), equalTo(action10.getBody().size()));
    	}
	}

}
