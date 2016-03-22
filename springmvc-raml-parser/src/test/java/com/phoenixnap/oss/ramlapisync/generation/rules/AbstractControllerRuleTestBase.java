package com.phoenixnap.oss.ramlapisync.generation.rules;

import com.phoenixnap.oss.ramlapisync.data.ApiControllerMetadata;
import com.phoenixnap.oss.ramlapisync.generation.RamlParser;
import com.phoenixnap.oss.ramlapisync.generation.RamlVerifier;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.writer.SingleStreamCodeWriter;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.raml.model.Raml;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

/**
 * @author armin.weisser
 * @since 0.3.2
 */
public abstract class AbstractControllerRuleTestBase {

    private static final String RESOURE_BASE = "rules/";
    private static Raml RAML;

    protected Logger logger = Logger.getLogger(this.getClass());
    protected JCodeModel jCodeModel;

    private RamlParser defaultRamlParser = new RamlParser("com.gen.test", "/api");
    private ApiControllerMetadata controllerMetadata;

    @BeforeClass
    public static void initRaml() {
        RAML = RamlVerifier.loadRamlFromFile(RESOURE_BASE + "test-single-controller.raml");
    }

    @Before
    public void setupModel() {
        jCodeModel = new JCodeModel();
    }

    public void initControllerMetadata(RamlParser par) {
        controllerMetadata = par.extractControllers(RAML).iterator().next();
    }

    public ApiControllerMetadata getControllerMetadata() {
        if(controllerMetadata == null) {
            initControllerMetadata(defaultRamlParser);
        }
        return controllerMetadata;
    }

    protected String serializeModel() {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            jCodeModel.build(new SingleStreamCodeWriter(bos));
        } catch (IOException e) {
            assertThat(e.getMessage(), is(nullValue()));
        }
        return bos.toString();
    }

    @After
    public void printCode() {
        logger.debug(serializeModel());
    }
}
