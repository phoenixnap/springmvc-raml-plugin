package com.phoenixnap.oss.ramlplugin.raml2code.rules;

import org.junit.After;
import org.junit.Before;

import com.phoenixnap.oss.ramlplugin.raml2code.data.ApiResourceMetadata;
import com.phoenixnap.oss.ramlplugin.raml2code.exception.InvalidRamlResourceException;
import com.phoenixnap.oss.ramlplugin.raml2code.helpers.RamlParser;
import com.phoenixnap.oss.ramlplugin.raml2code.raml.RamlRoot;
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
		equivalenceRamlParser = new RamlParser("/api");
	}

	public static void initRamlModels(String raml) throws InvalidRamlResourceException {
		String raml_10 = raml.replaceAll("-v08.raml", "-v10.raml");
		RAML = RamlLoader.loadRamlFromFile(raml);
		RAML_10 = RamlLoader.loadRamlFromFile(raml_10);
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
		if (equivalenceControllerMetadata == null) {
			initControllerMetadata(defaultRamlParser, equivalenceRamlParser);
		}
		return equivalenceControllerMetadata;
	}

}
