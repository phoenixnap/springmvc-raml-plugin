package com.phoenixnap.oss.ramlapisync.raml.rjp.raml10v2;

import com.phoenixnap.oss.ramlapisync.raml.RamlDocumentationItem;
import com.phoenixnap.oss.ramlapisync.raml.RamlResource;
import com.phoenixnap.oss.ramlapisync.raml.RamlRoot;
import com.phoenixnap.oss.ramlapisync.raml.RamlSpecNotFullySupportedException;
import org.raml.v2.api.model.v10.api.Api;
import org.raml.v2.api.model.v10.bodies.MimeType;
import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;
import org.raml.v2.api.model.v10.resources.Resource;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author aweisser
 */
public class RJP10V2RamlRoot implements RamlRoot {

    private static RJP10V2RamlModelFactory ramlModelFactory = new RJP10V2RamlModelFactory();

    private final Api api;
    private Map<String, RamlResource> resources = new LinkedHashMap<>();

    public RJP10V2RamlRoot(Api api) {
        this.api = api;
    }

    @Override
    public Map<String, RamlResource> getResources() {
        return ramlModelFactory.transformToUnmodifiableMap(
                api.resources(),
                resources,
                ramlModelFactory::createRamlResource,
                r -> r.relativeUri().value());
    }

    @Override
    public void addResource(String path, RamlResource childResource) {
        api.resources().add(ramlModelFactory.extractResource(childResource));
        resources.put(path, childResource);
    }

    @Override
    public void removeResource(String firstResourcePart) {
    	Iterator<Resource> iterator = api.resources().iterator();
    	while(iterator.hasNext()){
    		Resource resource = iterator.next();
    		if(resource.resourcePath().equals(firstResourcePart)){
    			api.resources().remove(resource);
    		}
    	}
        resources.remove(firstResourcePart);
    }

    @Override
    public void addResources(Map<String, RamlResource> resources) {
    	for(String key: resources.keySet()) {
            addResource(key, resources.get(key));
        }
    }

    @Override
    public String getMediaType() {
        List<MimeType> mediaTypes = this.api.mediaType();
        if(mediaTypes.size() >= 2) {
            throw new RamlSpecNotFullySupportedException("Sorry. Multiple default media types are not supported yet.");
        }
        if(mediaTypes.size() == 0) {
            return null;
        }
        return mediaTypes.stream().findFirst().get().value();
    }

    @Override
    public List<Map<String, String>> getSchemas() {
        /*
         * From https://github.com/raml-org/raml-spec/blob/master/versions/raml-10/raml-10.md/#the-root-of-the-document
         * API definitions should use the "types" node
         * because a future RAML version might remove the "schemas" alias with that node.
         * The "types" node supports XML and JSON schemas.
         */
        return api.types()
                .stream()
                .map(this::typeDeclarationToMap)
                .collect(Collectors.toList());
    }

    private Map<String, String> typeDeclarationToMap(TypeDeclaration typeDeclaration) {
        Map<String, String> nameTypeMapping = new LinkedHashMap<>();
        nameTypeMapping.put(typeDeclaration.name(), typeDeclaration.type());
        return nameTypeMapping;
    }

    @Override
    public void setBaseUri(String baseUri) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setVersion(String version) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setTitle(String title) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setDocumentation(List<RamlDocumentationItem> documentationItems) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setMediaType(String mediaType) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getBaseUri() {
        return this.api.baseUri() != null ? this.api.baseUri().value() : "";
    }
}
