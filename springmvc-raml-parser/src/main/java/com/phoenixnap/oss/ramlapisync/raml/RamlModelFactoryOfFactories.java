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


import org.raml.v2.api.RamlModelBuilder;
import org.raml.v2.api.RamlModelResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import com.phoenixnap.oss.ramlapisync.raml.rjp.raml08v1.RJP08V1RamlModelFactory;
import com.phoenixnap.oss.ramlapisync.raml.rjp.raml10v2.RJP10V2RamlModelFactory;

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
     * @param ramlVersion
     * @return
     */
    public static RamlModelFactory createRamlModelFactoryFor(RamlVersion ramlVersion) {
        switch(ramlVersion) {
            case V08: return new RJP08V1RamlModelFactory();
            case V10: return new RJP10V2RamlModelFactory();
            default: throw new UnsupportedRamlVersionError(RamlVersion.V08, RamlVersion.V10);
        }
    }
    
    public static RamlModelFactory createRamlModelFactoryFor(String ramlURL) {
    	return createRamlModelFactoryFor(ramlURL, null);
    }
    
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
        throw new UnsupportedRamlVersionError(RamlVersion.V08, RamlVersion.V10);
    }

}
