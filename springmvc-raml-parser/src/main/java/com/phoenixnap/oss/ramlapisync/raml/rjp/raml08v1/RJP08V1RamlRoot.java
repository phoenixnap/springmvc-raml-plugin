/*
 * Copyright 2002-2017 the original author or authors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package com.phoenixnap.oss.ramlapisync.raml.rjp.raml08v1;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.raml.model.Raml;

import com.phoenixnap.oss.ramlapisync.raml.RamlDataType;
import com.phoenixnap.oss.ramlapisync.raml.RamlDocumentationItem;
import com.phoenixnap.oss.ramlapisync.raml.RamlResource;
import com.phoenixnap.oss.ramlapisync.raml.RamlRoot;

/**
 * Implementation based on the Raml 0.8 Parser
 * 
 * @author armin.weisser
 * @since 0.8.1
 */
public class RJP08V1RamlRoot implements RamlRoot {

    private static RJP08V1RamlModelFactory ramlModelFactory = new RJP08V1RamlModelFactory();

    private final Raml raml;

    private Map<String, RamlResource> resources = new LinkedHashMap<>();

    public RJP08V1RamlRoot(Raml raml) {
        if(raml == null) {
            throw new IllegalArgumentException("The Raml instance must not be null");
        }
        this.raml = raml;
    }

    /**
     * Expose internal representation only package private
     * @return the internal model
     */
    Raml getRaml() {
        return raml;
    }

    @Override
    public void addResource(String path, RamlResource childResource) {
        raml.getResources().put(path, ramlModelFactory.extractResource(childResource));
        resources.put(path, childResource);
    }

    @Override
    public Map<String, RamlResource> getResources() {
        return ramlModelFactory.transformToUnmodifiableMap(raml.getResources(), resources, ramlModelFactory::createRamlResource);
    }

    @Override
    public void removeResource(String firstResourcePart) {
        raml.getResources().remove(firstResourcePart);
        resources.remove(firstResourcePart);
    }

    @Override
    public void addResources(Map<String, RamlResource> resources) {
        for(String key: resources.keySet()) {
            addResource(key, resources.get(key));
        }
    }

    @Override
    public RamlResource getResource(String path) {
        return ramlModelFactory.createRamlResource(raml.getResource(path));
    }

    @Override
    public String getMediaType() {
        return raml.getMediaType();
    }

    @Override
    public List<Map<String, String>> getSchemas() {
        return raml.getSchemas();
    }

    @Override
    public void setBaseUri(String baseUri) {
        raml.setBaseUri(baseUri);
    }

    @Override
    public void setVersion(String version) {
        raml.setVersion(version);
    }

    @Override
    public void setTitle(String title) {
        raml.setTitle(title);
    }

    @Override
    public void setDocumentation(List<RamlDocumentationItem> documentationItems) {
        raml.setDocumentation(ramlModelFactory.extractDocumentationItems(documentationItems));
    }

    @Override
    public void setMediaType(String mediaType) {
        raml.setMediaType(mediaType);
    }

    @Override
    public String getBaseUri() {
        return raml.getBaseUri();
    }

	@Override
	public Map<String, RamlDataType> getTypes() {
		throw new UnsupportedOperationException();
	}
}
