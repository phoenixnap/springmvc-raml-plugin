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


import com.phoenixnap.oss.ramlapisync.raml.rjp.raml08v1.RJP08V1RamlModelFactory;
import com.phoenixnap.oss.ramlapisync.raml.rjp.raml10v2.RJP10V2RamlModelFactory;
import org.raml.v2.api.RamlModelBuilder;
import org.raml.v2.api.RamlModelResult;
import org.raml.v2.api.model.common.ValidationResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * Factory for creating different instances of RamlModelFactory.
 * 
 * @author armin.weisser
 * @since 0.8.1
 */
public abstract class RamlModelFactoryOfFactories {
	
	/**
	 * Class Logger
	 */
	protected static final Logger logger = LoggerFactory.getLogger(RamlModelFactoryOfFactories.class);

    /**
     * @return a RJP08V1RamlModelFactory instance.
     */
    public static RamlModelFactory createRamlModelFactoryV08() {
        return createRamlModelFactoryFor(RamlVersion.V08);
    }

    /**
     * 
     * Creates a Model factory for a specific version of raml
     * 
     * @param ramlVersion The Version of raml for which to create a factory
     * @return The Factory instance for this version of RAML
     */
    public static RamlModelFactory createRamlModelFactoryFor(RamlVersion ramlVersion) {
        switch(ramlVersion) {
            case V08: return new RJP08V1RamlModelFactory();
            case V10: return new RJP10V2RamlModelFactory();
            default: throw new UnsupportedRamlVersionError(RamlVersion.V08, RamlVersion.V10);
        }
    }
    
    /**
     * 
     * Creates a Model factory for a specific raml document based on the documents version
     * 
     * @param ramlURL The raml file for which to create a factory
     * @return The Factory instance for this RAML document
     */
    public static RamlModelFactory createRamlModelFactoryFor(String ramlURL) {
    	return createRamlModelFactoryFor(ramlURL, null);
    }
    
    
    /**
     * 
     * Creates a Model factory for a specific raml document based on the documents version
     * if the ramlVersion is specified, the model factory will only be created if the document is compatible with this specified version
     * 
     * @param ramlURL The raml file for which to create a factory
     * @param ramlVersion (nullable) The Version of raml for which to create a factory
     * @return The Factory instance for this RAML document
     */
    public static RamlModelFactory createRamlModelFactoryFor(String ramlURL, RamlVersion ramlVersion) {
    	RamlModelResult ramlModelResult = new RamlModelBuilder().buildApi(ramlURL);
    	if (ramlModelResult.hasErrors()) {
    		logger.error("Loaded RAML has validation errors: "+ StringUtils.collectionToCommaDelimitedString(ramlModelResult.getValidationResults()));
    	}
        if (ramlModelResult.isVersion10() 
        		&& (ramlVersion == null || RamlVersion.V10.equals(ramlVersion))) {
        	logger.info("RJP10V2RamlModelFactory Instantiated");
        	return new RJP10V2RamlModelFactory();
        }
        if ((!ramlModelResult.hasErrors() && RamlVersion.V08.equals(ramlVersion)) //To keep legacy support try load using the 08 if requested specifically
        		|| (ramlModelResult.isVersion08() && (ramlVersion == null || RamlVersion.V08.equals(ramlVersion)))) {
        	logger.info("RJP08V1RamlModelFactory Instantiated");
        	return new RJP08V1RamlModelFactory();
        }

        if (containsUnsupportedVersionError(ramlModelResult.getValidationResults()) || !isSupportedRamlVersionCombination(ramlVersion, ramlModelResult)) {
            throw new UnsupportedRamlVersionError(RamlVersion.V08, RamlVersion.V10);
        }

        throw new InvalidRamlError(ramlURL, ramlModelResult.getValidationResults());
    }

    private static boolean containsUnsupportedVersionError(List<ValidationResult> validationResults) {
        if (validationResults != null) {
            for (ValidationResult result : validationResults) {
                if (result.getMessage() != null && result.getMessage().contains("Unsupported version")) {
                    return true;
                }
            }
        }
        return false;
    }

    private static boolean isSupportedRamlVersionCombination(RamlVersion ramlVersion, RamlModelResult ramlModelResult) {
        if (ramlVersion != null) {
            if (RamlVersion.V08.equals(ramlVersion) && ramlModelResult.isVersion08()) {
                return true;
            }
            if (RamlVersion.V10.equals(ramlVersion) && ramlModelResult.isVersion10()) {
                return true;
            }
            return false;
        } else {
            return true; // add unsupported ramlModelResult.isVersionXX() here and return false
        }
    }

}
