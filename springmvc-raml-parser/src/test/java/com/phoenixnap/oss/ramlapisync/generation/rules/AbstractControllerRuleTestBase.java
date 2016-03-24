package com.phoenixnap.oss.ramlapisync.generation.rules;

import com.phoenixnap.oss.ramlapisync.data.ApiControllerMetadata;
import com.phoenixnap.oss.ramlapisync.data.ApiMappingMetadata;
import com.phoenixnap.oss.ramlapisync.generation.RamlParser;
import com.phoenixnap.oss.ramlapisync.generation.RamlVerifier;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.writer.SingleStreamCodeWriter;
import org.apache.log4j.Logger;
import org.hamcrest.MatcherAssert;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.raml.model.Raml;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.Matchers.equalToIgnoringWhiteSpace;
import static org.junit.Assert.assertEquals;
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

    protected void initControllerMetadata(RamlParser par) {
        controllerMetadata = par.extractControllers(RAML).iterator().next();
    }

    protected ApiControllerMetadata getControllerMetadata() {
        if(controllerMetadata == null) {
            initControllerMetadata(defaultRamlParser);
        }
        return controllerMetadata;
    }


    protected ApiMappingMetadata getEndpointMetadata() {
        return getEndpointMetadata(1);
    }

    protected ApiMappingMetadata getEndpointMetadata(int number) {
        return getControllerMetadata().getApiCalls().stream().skip(number-1).findFirst().get();
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

    protected void verifyGeneratedCode(String name) throws Exception {
        String expectedCode = getTextFromFile(RESOURE_BASE + name + ".java.txt");
        String generatedCode = serializeModel();

        try {
            MatcherAssert.assertThat(name + " is not generated correctly.", generatedCode, equalToIgnoringWhiteSpace(expectedCode));
        } catch (AssertionError e) {
            // We let assertEquals fail here instead, because better IDE support for multi line string diff.
            assertEquals(expectedCode, generatedCode);
        }
    }


    private String getTextFromFile(String resourcePath) throws Exception {
        URI uri = getUri(resourcePath);
        return new String(Files.readAllBytes(Paths.get(uri)), StandardCharsets.UTF_8);
    }

    private URI getUri(String resourcePath) throws URISyntaxException {
        URL resource = getClass().getClassLoader().getResource(resourcePath);
        return resource.toURI();
    }
}
