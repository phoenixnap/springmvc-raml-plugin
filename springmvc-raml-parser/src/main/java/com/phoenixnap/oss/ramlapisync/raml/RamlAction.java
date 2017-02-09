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

import java.util.List;
import java.util.Map;

/**
 * Abstract Representation of a Raml Action
 * 
 * @author armin.weisser
 * @since 0.8.1
 */
public interface RamlAction {

    RamlActionType getType();

    Map<String, RamlQueryParameter> getQueryParameters();

    Map<String, RamlResponse> getResponses();

    RamlResource getResource();

    Map<String, RamlHeader> getHeaders();

    Map<String, RamlMimeType> getBody();

    boolean hasBody();

    String getDescription();
    
    void setDescription(String description);
    
    String getDisplayName();
    
    void setDisplayName(String displayName);

    void setBody(Map<String, RamlMimeType> body);

    void setResource(RamlResource resource);

    void setType(RamlActionType actionType);

    List<RamlSecurityReference> getSecuredBy();

    void addResponse(String httpStatus, RamlResponse response);

    void addQueryParameters(Map<String, RamlQueryParameter> queryParameters);
}
