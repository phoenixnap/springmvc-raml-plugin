package com.phoenixnap.oss.ramlapisync.generation.rules;

import org.junit.After;
import org.junit.Before;

import com.phoenixnap.oss.ramlapisync.data.ApiResourceMetadata;
import com.phoenixnap.oss.ramlapisync.generation.RamlParser;
import com.phoenixnap.oss.ramlapisync.raml.InvalidRamlResourceException;
import com.phoenixnap.oss.ramlapisync.raml.RamlRoot;
import com.sun.codemodel.JCodeModel;

/**
 * @author armin.weisser
 * @since 0.4.1
 */
public abstract class AbstractEquivalenceRuleTestBase extends AbstractRuleTestBase {

    public static RamlRoot RAML_10;

    protected JCodeModel equivalenceJCodeModel;

    protected RamlParser equivalenceRamlParser;

    private ApiResourceMetadata equivalenceControllerMetadata;

    public AbstractEquivalenceRuleTestBase() {
    	super();
    	equivalenceRamlParser = new RamlParser("com.gen.test", "/api", false, false);
    }

    public static void initRamlModels(String raml) throws InvalidRamlResourceException {
    	String raml_10 = raml.replaceAll("-v08.raml", "-v10.raml");
    	RAML = RamlParser.loadRamlFromFile(raml);
    	RAML_10 = RamlParser.loadRamlFromFile(raml_10);
    }

    @Before
    public void setupModel() {
        super.setupModel();
        equivalenceJCodeModel = new JCodeModel();
    }
    
    protected String serializeEquivalenceModel() {
    	return serializeModel(equivalenceJCodeModel);
    }
    
    
    @After
    public void printCode() {
    	if (VISUALISE_CODE) {
	    	logger.debug("---------------------------------------------------------------------------------------");
	    	logger.debug("---------------------------- GENERATED WITH RJP08V1 Parser ----------------------------");
	    	logger.debug("---------------------------------------------------------------------------------------");
	    	super.printCode();
	    	logger.debug("---------------------------------------------------------------------------------------");
	    	logger.debug("---------------------------- GENERATED WITH RJP10v2 Parser ----------------------------");
	    	logger.debug("---------------------------------------------------------------------------------------");
	        logger.debug(serializeEquivalenceModel());
    	}
    }

    protected void initControllerMetadata(RamlParser par, RamlParser equivalencePar) {
    	initControllerMetadata(par);
    	equivalenceControllerMetadata = equivalencePar.extractControllers(equivalenceJCodeModel, RAML_10).iterator().next();
    }

    protected ApiResourceMetadata getEquivalenceControllerMetadata() {
        if(equivalenceControllerMetadata == null) {
            initControllerMetadata(defaultRamlParser, equivalenceRamlParser);
        }
        return equivalenceControllerMetadata;
    }

  
}
