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
package com.phoenixnap.oss.ramlplugin.raml2code.rules;

import org.raml.v2.api.RamlModelBuilder;
import org.raml.v2.api.RamlModelResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import com.phoenixnap.oss.ramlplugin.raml2code.exception.InvalidRamlException;
import com.phoenixnap.oss.ramlplugin.raml2code.exception.InvalidRamlResourceException;
import com.phoenixnap.oss.ramlplugin.raml2code.exception.UnsupportedRamlVersionException;
import com.phoenixnap.oss.ramlplugin.raml2code.raml.RamlModelFactory;
import com.phoenixnap.oss.ramlplugin.raml2code.raml.RamlRoot;
import com.phoenixnap.oss.ramlplugin.raml2code.raml.RamlVersion;
import com.phoenixnap.oss.ramlplugin.raml2code.raml.raml10.RJP10V2RamlModelFactory;

/**
 * 
 * Class containing method used to load a raml file
 * 
 * @author kurtpa
 * @since 0.10.3
 */
public class RamlLoader {

	/**
	 * Class Logger
	 */
	protected static final Logger logger = LoggerFactory.getLogger(RamlLoader.class);

	/**
	 * Loads a RAML document from a file. This method will
	 * 
	 * @param ramlFileUrl
	 *            The path to the file, this can either be a resource on the
	 *            class path (in which case the classpath: prefix should be
	 *            omitted) or a file on disk (in which case the file: prefix
	 *            should be included)
	 * @return Built Raml model
	 * @throws InvalidRamlResourceException
	 *             If the Raml Provided isnt correct for the required parser
	 */
	public static RamlRoot loadRamlFromFile(String ramlFileUrl) {
		try {
			return createRamlModelFactoryFor(ramlFileUrl).buildRamlRoot(ramlFileUrl);
		} catch (NullPointerException npe) {
			logger.error("File not found at {}", ramlFileUrl);
			return null;
		}
	}

	private static RamlModelFactory createRamlModelFactoryFor(String ramlURL) {
		RamlModelResult ramlModelResult = new RamlModelBuilder().buildApi(ramlURL);

		if (ramlModelResult.hasErrors()) {
			if (logger.isErrorEnabled()) {
				logger.error("Loaded RAML has validation errors: {}",
						StringUtils.collectionToCommaDelimitedString(ramlModelResult.getValidationResults()));
			}
			throw new InvalidRamlException(ramlURL, ramlModelResult.getValidationResults());
		}

		if (!ramlModelResult.isVersion10()) {
			logger.error("Unsupported version detected!");
			throw new UnsupportedRamlVersionException(RamlVersion.V10);
		}

		logger.info("RJP10V2RamlModelFactory Instantiated");
		return new RJP10V2RamlModelFactory();
	}
}
