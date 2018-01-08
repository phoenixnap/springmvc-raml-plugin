package com.phoenixnap.oss.ramlplugin.raml2code.exception;

import java.util.Arrays;

import com.phoenixnap.oss.ramlplugin.raml2code.raml.RamlVersion;

/**
 * @author aweisser
 */
public class UnsupportedRamlVersionException extends RuntimeException {

	private static final long serialVersionUID = -2773078854396182834L;

	public UnsupportedRamlVersionException(RamlVersion... supportedVersions) {
		super("RAML Version is not supported. Supported versions are " + Arrays.asList(supportedVersions));
	}
}
