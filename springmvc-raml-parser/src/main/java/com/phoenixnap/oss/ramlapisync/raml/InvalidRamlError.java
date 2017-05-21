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

/**
 * @author georgkoester
 */
public class InvalidRamlError extends Error {

	private static final long serialVersionUID = 770865086433357483L;
    private final List<?> errors;
    private final String ramlFileUrl;

    public InvalidRamlError(String ramlFileUrl, List<?> errors) {
        this.errors = errors;
        this.ramlFileUrl = ramlFileUrl;
    }

    @Override
    public String getMessage() {
        return this.toString();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " on raml file " + ramlFileUrl + " {" +
                "errors=" + errors +
                '}';
    }
}
