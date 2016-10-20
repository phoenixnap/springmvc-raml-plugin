package com.phoenixnap.oss.ramlapisync.raml.rjp.raml10v2;

import com.phoenixnap.oss.ramlapisync.data.RamlFormParameter;
import com.phoenixnap.oss.ramlapisync.raml.*;
import com.phoenixnap.oss.ramlapisync.raml.InvalidRamlResourceException;
import org.raml.v2.api.RamlModelBuilder;
import org.raml.v2.api.RamlModelResult;
import org.raml.v2.api.model.v10.api.Api;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author aweisser
 */
public class RJP10V2RamlModelFactory implements RamlModelFactory {

    @Override
    public RamlModelEmitter createRamlModelEmitter() {
        throw new UnsupportedOperationException();
    }

    @Override
    public RamlRoot buildRamlRoot(String ramlFileUrl) throws InvalidRamlResourceException {
        RamlModelResult ramlModelResult = new RamlModelBuilder().buildApi(ramlFileUrl);
        if (ramlModelResult.hasErrors()) {
            List<String> errors = ramlModelResult.getValidationResults()
                    .stream()
                    .map(validationResult -> validationResult.getMessage())
                    .collect(Collectors.toList());
            throw new InvalidRamlResourceException(ramlFileUrl, errors);
        }

        // The Api is created by RamlModelBuilder during runtime via a yagi ModelProxyBuilder.
        // In org.raml.v2 there is no direct implementation for Api interface during compile time.
        Api api = ramlModelResult.getApiV10();
        return new RJP10V2RamlRoot(api);
    }

    @Override
    public RamlRoot createRamlRoot() {
        throw new UnsupportedOperationException();
    }

    @Override
    public RamlRoot createRamlRoot(String ramlFileUrl) {
        throw new UnsupportedOperationException();
    }

    @Override
    public RamlResource createRamlResource() {
        throw new UnsupportedOperationException();
    }

    @Override
    public RamlResource createRamlResource(Object resource) {
        throw new UnsupportedOperationException();
    }

    @Override
    public RamlAction createRamlAction(Object action) {
        throw new UnsupportedOperationException();
    }

    @Override
    public RamlAction createRamlAction() {
        throw new UnsupportedOperationException();
    }

    @Override
    public RamlDocumentationItem createRamlDocumentationItem() {
        throw new UnsupportedOperationException();
    }

    @Override
    public RamlDocumentationItem createRamlDocumentationItem(Object documentationItem) {
        throw new UnsupportedOperationException();
    }

    @Override
    public RamlActionType createRamlActionType(Object type) {
        throw new UnsupportedOperationException();
    }

    @Override
    public RamlResponse createRamlResponse() {
        throw new UnsupportedOperationException();
    }

    @Override
    public RamlResponse createRamlResponse(Object response) {
        throw new UnsupportedOperationException();
    }

    @Override
    public RamlMimeType createRamlMimeType() {
        throw new UnsupportedOperationException();
    }

    @Override
    public RamlMimeType createRamlMimeType(Object mimeType) {
        throw new UnsupportedOperationException();
    }

    @Override
    public RamlMimeType createRamlMimeTypeWithMime(String mime) {
        throw new UnsupportedOperationException();
    }

    @Override
    public RamlHeader createRamlHeader(Object haeder) {
        throw new UnsupportedOperationException();
    }

    @Override
    public RamlUriParameter createRamlUriParameter(Object o) {
        throw new UnsupportedOperationException();
    }

    @Override
    public RamlUriParameter createRamlUriParameterWithName(String name) {
        throw new UnsupportedOperationException();
    }

    @Override
    public RamlQueryParameter createRamlQueryParameter() {
        throw new UnsupportedOperationException();
    }

    @Override
    public RamlQueryParameter createRamlQueryParameter(Object queryParameter) {
        throw new UnsupportedOperationException();
    }

    @Override
    public RamlFormParameter createRamlFormParameter() {
        throw new UnsupportedOperationException();
    }

    @Override
    public RamlFormParameter createRamlFormParameter(Object formParameter) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<RamlFormParameter> createRamlFormParameters(List<? extends Object> formParameters) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<RamlSecurityReference> createRamlSecurityReferences(List<? extends Object> securityReferences) {
        throw new UnsupportedOperationException();
    }

    @Override
    public RamlSecurityReference createRamlSecurityReference(Object securityReference) {
        throw new UnsupportedOperationException();
    }

    @Override
    public RamlParamType createRamlParamType(Object paramType) {
        throw new UnsupportedOperationException();
    }
}
