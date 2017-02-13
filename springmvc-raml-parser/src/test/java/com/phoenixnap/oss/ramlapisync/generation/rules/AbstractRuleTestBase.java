package com.phoenixnap.oss.ramlapisync.generation.rules;

import com.phoenixnap.oss.ramlapisync.data.ApiActionMetadata;
import com.phoenixnap.oss.ramlapisync.data.ApiResourceMetadata;
import com.phoenixnap.oss.ramlapisync.generation.RamlParser;
import com.phoenixnap.oss.ramlapisync.generation.RamlVerifier;
import com.phoenixnap.oss.ramlapisync.raml.RamlRoot;
import com.phoenixnap.oss.ramlapisync.raml.InvalidRamlResourceException;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.writer.SingleStreamCodeWriter;
import org.apache.log4j.Logger;
import org.hamcrest.MatcherAssert;
import org.hamcrest.text.IsEqualIgnoringWhiteSpace;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

/**
 * @author armin.weisser
 * @since 0.4.1
 */
public abstract class AbstractRuleTestBase {

    public static final String RESOURCE_BASE = "rules/";
    public static RamlRoot RAML;

    protected Logger logger = Logger.getLogger(this.getClass());
    protected JCodeModel jCodeModel;

    protected RamlParser defaultRamlParser;

    private ApiResourceMetadata controllerMetadata;

    public AbstractRuleTestBase() {
        defaultRamlParser = new RamlParser("com.gen.test", "/api", false, false);
    }

    @BeforeClass
    public static void initRaml() throws InvalidRamlResourceException {
        RAML = RamlVerifier.loadRamlFromFile(RESOURCE_BASE + "test-single-controller.raml");
    }

    @Before
    public void setupModel() {
        jCodeModel = new JCodeModel();
    }

    protected void initControllerMetadata(RamlParser par) {
        controllerMetadata = par.extractControllers(jCodeModel, RAML).iterator().next();
    }

    protected ApiResourceMetadata getControllerMetadata() {
        if(controllerMetadata == null) {
            initControllerMetadata(defaultRamlParser);
        }
        return controllerMetadata;
    }


    protected ApiActionMetadata getEndpointMetadata() {
        return getEndpointMetadata(1);
    }

    protected ApiActionMetadata getEndpointMetadata(int number) {
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
        String expectedCode = getTextFromFile(RESOURCE_BASE + name + ".java.txt");
        String generatedCode = serializeModel();

        try {
            MatcherAssert.assertThat(name + " is not generated correctly.", generatedCode, new IsEqualIgnoringLeadingAndEndingWhiteSpaces(expectedCode));
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

    public static class IsEqualIgnoringLeadingAndEndingWhiteSpaces extends IsEqualIgnoringWhiteSpace {

        public IsEqualIgnoringLeadingAndEndingWhiteSpaces(String string) {
            super(string);
        }

        public String stripSpace(String toBeStripped) {
            String result = "";
            BufferedReader bufReader = new BufferedReader(new StringReader(toBeStripped));
            String line;
            try {
                while( (line=bufReader.readLine()) != null ) {
                    result += super.stripSpace(line);
                }
            } catch (IOException e) {
                return e.getMessage();
            }
            return result;
        }

    }
}
