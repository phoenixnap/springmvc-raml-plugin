package com.phoenixnap.oss.ramlapisync.raml.rjp.raml10v2;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.raml.v2.api.RamlModelBuilder;
import org.raml.v2.api.RamlModelResult;
import org.raml.v2.api.model.v10.api.Api;
import org.raml.v2.api.model.v10.api.DocumentationItem;
import org.raml.v2.api.model.v10.bodies.Response;
import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;
import org.raml.v2.api.model.v10.methods.Method;
import org.raml.v2.api.model.v10.resources.Resource;
import org.raml.v2.api.model.v10.security.SecuritySchemeRef;

import com.phoenixnap.oss.ramlapisync.data.RamlFormParameter;
import com.phoenixnap.oss.ramlapisync.raml.InvalidRamlResourceException;
import com.phoenixnap.oss.ramlapisync.raml.RamlAction;
import com.phoenixnap.oss.ramlapisync.raml.RamlActionType;
import com.phoenixnap.oss.ramlapisync.raml.RamlDocumentationItem;
import com.phoenixnap.oss.ramlapisync.raml.RamlHeader;
import com.phoenixnap.oss.ramlapisync.raml.RamlMimeType;
import com.phoenixnap.oss.ramlapisync.raml.RamlModelEmitter;
import com.phoenixnap.oss.ramlapisync.raml.RamlModelFactory;
import com.phoenixnap.oss.ramlapisync.raml.RamlParamType;
import com.phoenixnap.oss.ramlapisync.raml.RamlQueryParameter;
import com.phoenixnap.oss.ramlapisync.raml.RamlResource;
import com.phoenixnap.oss.ramlapisync.raml.RamlResponse;
import com.phoenixnap.oss.ramlapisync.raml.RamlRoot;
import com.phoenixnap.oss.ramlapisync.raml.RamlSecurityReference;
import com.phoenixnap.oss.ramlapisync.raml.RamlUriParameter;

/**
 * @author aweisser
 * @author Aleksandar Stojsavljevic
 * @since 0.10.0
 */
public class RJP10V2RamlModelFactory implements RamlModelFactory {

    @Override
    public RamlModelEmitter createRamlModelEmitter() {
		return new RJP10V2RamlModelEmitter();
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
        if(resource == null) {
            return null;
        }
        return new RJP10V2RamlResource((Resource)resource);
    }

    @Override
    public RamlAction createRamlAction(Object action) {
    	if (action == null) {
            return null;
        }
        return new RJP10V2RamlAction((Method) action);
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
		return new RJP10V2RamlDocumentationItem((DocumentationItem) documentationItem);
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
    	if(response == null) {
            return null;
        }
        return new RJP10V2RamlResponse((Response)response);
    }

    @Override
    public RamlMimeType createRamlMimeType() {
        throw new UnsupportedOperationException();
    }

    @Override
    public RamlMimeType createRamlMimeType(Object mimeType) {
    	if(mimeType == null) {
            return null;
        }
        return new RJP10V2RamlMimeType((TypeDeclaration) mimeType);
    }

    @Override
    public RamlMimeType createRamlMimeTypeWithMime(String mime) {
        throw new UnsupportedOperationException();
    }

    @Override
    public RamlHeader createRamlHeader(Object haeder) {
    	if(haeder == null) {
            return null;
        }
        return new RJP10V2RamlHeader((TypeDeclaration) haeder);
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
    	if(queryParameter == null) {
            return null;
        }
        return new RJP10V2RamlQueryParameter((TypeDeclaration)queryParameter);
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
		return securityReferences.stream().map(this::createRamlSecurityReference).collect(Collectors.toList());
    }

    @Override
    public RamlSecurityReference createRamlSecurityReference(Object securityReference) {
		return new RJP10V2RamlSecurityReference((SecuritySchemeRef) securityReference);
    }

    @Override
    public RamlParamType createRamlParamType(Object paramType) {
    	if(paramType == null) {
            return RamlParamType.STRING;
        }
    	return RamlParamType.valueOf(((String) paramType).toUpperCase());
    }

	Resource extractResource(RamlResource ramlResource) {
        if (ramlResource == null) {
        	return null;
        }
        return ((RJP10V2RamlResource) ramlResource).getResource();
    }
	
	Response extractResponse(RamlResponse ramlResponse) {
        return ((RJP10V2RamlResponse)ramlResponse).getResponse();
    }
	
	Map<String, TypeDeclaration> extractBody(Map<String, RamlMimeType> ramlBody) {
        Map<String, TypeDeclaration> body = new LinkedHashMap<>(ramlBody.size());
        for(String key: ramlBody.keySet()) {
            body.put(key, extractMimeType(ramlBody.get(key)));
        }
        return body;
    }
	
	TypeDeclaration extractMimeType(RamlMimeType ramlMimeType) {
        return ((RJP10V2RamlMimeType)ramlMimeType).getMimeType();
    }

	TypeDeclaration extractUriParameter(RamlUriParameter ramlUriParameter) {
        return ((RJP10V2RamlUriParameter)ramlUriParameter).getUriParameter();
    }
    
    List<TypeDeclaration> extractFormParameters(List<RamlFormParameter> ramlFormParameters) {
        return ramlFormParameters.stream().map(this::extractFormParameter).collect(Collectors.toList());
    }

    TypeDeclaration extractFormParameter(RamlFormParameter ramlFormParameter) {
        return ((RJP10V2RamlFormParameter)ramlFormParameter).getFormParameter();
    }
    
    Map<String, List<TypeDeclaration>> extractFormParameters(Map<String, List<RamlFormParameter>> ramlFormParameters) {
        Map<String, List<TypeDeclaration>> formParameters = new LinkedHashMap<>(ramlFormParameters.size());
        for(String key: ramlFormParameters.keySet()) {
            formParameters.put(key, extractFormParameters(ramlFormParameters.get(key)));
        }
        return formParameters;
    }
    
    TypeDeclaration extractQueryParameter(RamlQueryParameter ramlQueryParameter) {
        return ((RJP10V2RamlQueryParameter)ramlQueryParameter).getQueryParameter();
    }
}
