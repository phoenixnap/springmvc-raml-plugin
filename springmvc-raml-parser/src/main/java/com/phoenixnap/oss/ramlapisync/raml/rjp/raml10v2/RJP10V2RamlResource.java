package com.phoenixnap.oss.ramlapisync.raml.rjp.raml10v2;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.raml.v2.api.model.v10.methods.Method;
import org.raml.v2.api.model.v10.resources.Resource;

import com.phoenixnap.oss.ramlapisync.raml.RamlAction;
import com.phoenixnap.oss.ramlapisync.raml.RamlActionType;
import com.phoenixnap.oss.ramlapisync.raml.RamlResource;
import com.phoenixnap.oss.ramlapisync.raml.RamlUriParameter;

/**
 * @author aweisser
 * @author Aleksandar Stojsavljevic
 * @since 0.10.0
 */
public class RJP10V2RamlResource implements RamlResource {
	
	private static RJP10V2RamlModelFactory ramlModelFactory = new RJP10V2RamlModelFactory();
	
    private final Resource delegate;

    public RJP10V2RamlResource(Resource resource) {
        this.delegate = resource;
    }

    @Override
    public Map<String, RamlResource> getResources() {
        Object o = delegate;
        throw new UnsupportedOperationException();
    }

    @Override
    public void addResource(String path, RamlResource childResource) {
        throw new UnsupportedOperationException();
    }

    @Override
    public RamlResource getResource(String path) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void removeResource(String firstResourcePart) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void addResources(Map<String, RamlResource> resources) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getRelativeUri() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Map<RamlActionType, RamlAction> getActions() {
    	Map<RamlActionType, RamlAction> actions = new HashMap<RamlActionType, RamlAction>();
    	for(Method method : this.delegate.methods()){
    		actions.put(RamlActionType.valueOf(method.method().toUpperCase()), new RJP10V2RamlAction(method));
    	}
    	return actions;
    }

    @Override
    public Map<String, RamlUriParameter> getUriParameters() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void addUriParameter(String name, RamlUriParameter uriParameter) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Map<String, RamlUriParameter> getResolvedUriParameters() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getUri() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getDescription() {
    	return this.delegate.description().value();
    }
    
    @Override
    public String getDisplayName() {
    	return this.delegate.displayName().value();
    }

    @Override
    public RamlResource getParentResource() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setParentResource(RamlResource parentResource) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getParentUri() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setParentUri(String parentUri) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setRelativeUri(String relativeUri) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setDisplayName(String displayName) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setDescription(String description) {
        throw new UnsupportedOperationException();
    }

    @Override
    public RamlAction getAction(RamlActionType actionType) {
        List<Method> methods = delegate.methods();
        for(Method method : methods){
        	if(method.method().equalsIgnoreCase(actionType.toString())){
        		return ramlModelFactory.createRamlAction(method);
        	}
        }
        return null;
    }

    @Override
    public void addAction(RamlActionType apiAction, RamlAction action) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void addActions(Map<RamlActionType, RamlAction> actions) {
        throw new UnsupportedOperationException();
    }

    Resource getResource() {
        return this.delegate;
    }
}
