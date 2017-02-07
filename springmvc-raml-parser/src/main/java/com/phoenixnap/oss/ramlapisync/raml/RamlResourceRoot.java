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
package com.phoenixnap.oss.ramlapisync.raml;

import java.util.Map;

/**
 * Abstract Representation of a Raml Resource.
 * RamlResourceRoot is an element that can contain other resources.
 * 
 * @author armin.weisser
 * @since 0.8.1
 */
public interface RamlResourceRoot {

    /**
     * Given a path with more than one segment the getResource(String) method will recursively lookup a matching resource.
     * @param path a relative or absolute URI
     * @return the child RamlResource that matches the given path.
     */
    default RamlResource getResource(String path) {
        String[] segments = path.split("/");
        RamlResourceRoot current = this;
        RamlResource resource = null;
        for(String segment: segments) {
            if(segment != null && !"".equals(segment)) {
                resource = current.getResources().get("/" + segment);
                current = resource;
            }
        }
        return resource;
    }

    /**
     * @return all direct child resources of this resource.
     */
    Map<String, RamlResource> getResources();

    void addResource(String path, RamlResource childResource);

    void removeResource(String firstResourcePart);

    void addResources(Map<String, RamlResource> resources);
}
