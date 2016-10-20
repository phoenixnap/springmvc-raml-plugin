package com.phoenixnap.oss.ramlapisync.raml;

/**
 * @author aweisser
 */
public class RamlSpecNotFullySupportedException extends RuntimeException {
    public RamlSpecNotFullySupportedException(String simpleMessage) {
        super(simpleMessage + "\n Please feel free to contribute to https://github.com/phoenixnap/springmvc-raml-plugin");
    }
}
