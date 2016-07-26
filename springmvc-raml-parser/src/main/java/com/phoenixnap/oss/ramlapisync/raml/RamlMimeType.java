/*
 * Copyright 2002-2016 the original author or authors.
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

import com.phoenixnap.oss.ramlapisync.data.RamlFormParameter;

import java.util.List;
import java.util.Map;

/**
 * Abstract Representation of a Raml MimeType
 * 
 * @author armin.weisser
 * @since 0.8.1
 */
public interface RamlMimeType {

    Map<String, List<RamlFormParameter>> getFormParameters();

    void setFormParameters(Map<String, List<RamlFormParameter>> formParameters);

    void addFormParameters(String name, List<RamlFormParameter> formParameters);

    String getSchema();

    void setSchema(String schema);

    void setExample(String example);
}
