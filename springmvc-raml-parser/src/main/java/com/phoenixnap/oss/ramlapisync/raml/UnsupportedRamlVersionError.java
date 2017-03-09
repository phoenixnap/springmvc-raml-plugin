package com.phoenixnap.oss.ramlapisync.raml;

/**
 * @author aweisser
 */
public class UnsupportedRamlVersionError extends Error {

	private static final long serialVersionUID = -2773078854396182834L;

    public UnsupportedRamlVersionError(RamlVersion... supportedVersions) {
        super("RAML Version is not supported. Supported versions are "+supportedVersions);
    }
}
