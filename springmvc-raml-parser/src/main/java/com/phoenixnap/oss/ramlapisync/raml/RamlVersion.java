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

/**
 * @author aweisser
 */
public enum RamlVersion {

    V08("#%RAML 0.8"),
    V10("#%RAML 1.0");

    private final String identifier;

    RamlVersion(String identifier) {
        this.identifier = identifier;
    }

    /**
     *
     * @param raml content to parse the RAML version from
     * @return RamlVersion that fits to the raml content
     * @throws CannotGetRamlVersionException If we cannot identify the version of this raml string
     */
    public static RamlVersion forRaml(String raml) {
        if(raml.startsWith(V08.identifier)) {
            return V08;
        } else if(raml.startsWith(V10.identifier)) {
            return V10;
        }
        throw new CannotGetRamlVersionException(raml, V08, V10);
    }
}
