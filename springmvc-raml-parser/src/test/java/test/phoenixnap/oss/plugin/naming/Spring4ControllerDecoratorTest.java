package test.phoenixnap.oss.plugin.naming;

import com.phoenixnap.oss.ramlapisync.data.ApiControllerMetadata;
import com.phoenixnap.oss.ramlapisync.generation.RamlParser;
import com.phoenixnap.oss.ramlapisync.generation.RamlSpring4DecoratorGenerator;
import com.phoenixnap.oss.ramlapisync.generation.RamlVerifier;
import com.phoenixnap.oss.ramlapisync.generation.serialize.ApiControllerMetadataSerializer;
import org.apache.commons.lang.StringUtils;
import org.junit.Test;
import org.raml.model.Raml;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalToIgnoringWhiteSpace;
import static org.junit.Assert.assertEquals;

/**
 * @author armin.weisser
 */
public class Spring4ControllerDecoratorTest {

    private static final String RESOURE_BASE = "decorator/";
    private RamlSpring4DecoratorGenerator generator = new RamlSpring4DecoratorGenerator();

    @Test
    public void test_simple_Success() throws Exception {
        Raml published = RamlVerifier.loadRamlFromFile(RESOURE_BASE + "test-simple-decorator.raml");
        RamlParser par = new RamlParser("com.gen.test");
        Set<ApiControllerMetadata> controllersMetadataSet = par.extractControllers(published);

        assertEquals(2, controllersMetadataSet.size());

        for(ApiControllerMetadata apiControllerMetadata: controllersMetadataSet) {
            verifyGenerateClassFor(apiControllerMetadata);
        }

    }

    private void verifyGenerateClassFor(ApiControllerMetadata apiControllerMetadata) throws Exception {
        List<ApiControllerMetadataSerializer> serializers = generator.generateClassForRaml(apiControllerMetadata, "");
        ApiControllerMetadataSerializer interfaceSerializer = serializers.get(0);
        ApiControllerMetadataSerializer decoratorSerializer = serializers.get(1);
        verifySerializer(interfaceSerializer);
        verifySerializer(decoratorSerializer);
    }

    private void verifySerializer(ApiControllerMetadataSerializer serializer) throws Exception {
        String expectedCode = getTextFromFile(RESOURE_BASE + serializer.getName() + ".java.txt");
        String generatedCode = serializer.serialize();

        try {
            assertThat(serializer.getName() + " is not generated correctly.", generatedCode, equalToIgnoringWhiteSpace(expectedCode));
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
