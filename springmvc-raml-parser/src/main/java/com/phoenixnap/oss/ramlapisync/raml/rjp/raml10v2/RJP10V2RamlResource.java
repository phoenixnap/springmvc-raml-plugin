package com.phoenixnap.oss.ramlapisync.raml.rjp.raml10v2;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;
import org.raml.v2.api.model.v10.methods.Method;
import org.raml.v2.api.model.v10.resources.Resource;

import com.phoenixnap.oss.ramlapisync.naming.NamingHelper;
import com.phoenixnap.oss.ramlapisync.naming.RamlTypeHelper;
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
    
    private transient Map<String, RamlResource> childResourceMap;

    public RJP10V2RamlResource(Resource resource) {
        this.delegate = resource;
        
        rebuildChildren();
    }

    private void rebuildChildren() {
    	childResourceMap = new LinkedHashMap<String, RamlResource>();
    	List<Resource> resources = delegate.resources();
    	if (resources != null) {
	    	for (Resource resource : resources) {
	        	childResourceMap.put(resource.relativeUri().value(), new RJP10V2RamlResource(resource));	        	
	        }
	    }
	}

	@Override
    public Map<String, RamlResource> getResources() {
        return childResourceMap;
    }

    @Override
    public void addResource(String path, RamlResource childResource) {
        throw new UnsupportedOperationException();
    }

    @Override
    public RamlResource getResource(String path) {
       return childResourceMap.get(path);
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
		return (this.delegate.relativeUri() == null) ? null : this.delegate.relativeUri().value();
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
    	Map<String, RamlUriParameter> uriParameters = new LinkedHashMap<>();
    	for (TypeDeclaration type : this.delegate.uriParameters()) {
    		RJP10V2RamlUriParameter rjp10v2RamlUriParameter = new RJP10V2RamlUriParameter(type);
    		uriParameters.put(type.name(), rjp10v2RamlUriParameter);
    	}
    	//RJP08 detects and adds uri parameters from url even if there isnt an explicit parameter defined.
    	List<String> missingUriParams = NamingHelper.extractUriParams(this.getRelativeUri());
    	for (String missingParam : missingUriParams) {
    		if (!uriParameters.containsKey(missingParam)) {    			
    			uriParameters.put(missingParam, new RJP10V2RamlUriParameter(RamlTypeHelper.createDefaultStringDeclaration(missingParam)));
    		}
    	}
    	return uriParameters;
    }

    @Override
    public void addUriParameter(String name, RamlUriParameter uriParameter) {
        throw new UnsupportedOperationException();
    }

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.raml.model.Resource#getResolvedUriParameters()
	 * 
	 * 
	 * @return URI parameters defined for the current resource plus all URI
	 * parameters defined in the resource hierarchy
	 *
	 */
    @Override
    public Map<String, RamlUriParameter> getResolvedUriParameters() {
		Map<String, RamlUriParameter> resolvedUriParameters = new HashMap<>();

		RamlResource resource = this;
		while (resource != null) {
			resolvedUriParameters = Stream
					.concat(resolvedUriParameters.entrySet().stream(), resource.getUriParameters().entrySet().stream())
					.collect(Collectors.toMap(entry -> entry.getKey(), entry -> entry.getValue()));
			resource = resource.getParentResource();
		}

		return resolvedUriParameters;
    }

    @Override
    public String getUri() {
    	String outUri = delegate.relativeUri().value();
    	Resource parentResource = delegate.parentResource();
    	while (parentResource != null) {
    		if (parentResource.relativeUri() != null) {
        		outUri = parentResource.relativeUri().value() + outUri;
        	} 
    		parentResource = parentResource.parentResource();
    	}
    	return outUri;
    }

    @Override
    public String getDescription() {
    	return (this.delegate.description() == null) ? null : this.delegate.description().value();
    }
    
    @Override
    public String getDisplayName() {
    	if (this.delegate.displayName() == null) {
    		return null;
    	}
    	//we need to check if the displayname is the relative uri and remove it since this is an inconsistency between 08 and 10. 
    	String value = this.delegate.displayName().value();
    	if (this.getRelativeUri().equals(value)) {
    		return null;
    	} else {
    		return value;
    	}
    }

    @Override
    public RamlResource getParentResource() {
        return (this.delegate.parentResource() == null) ? null : new RJP10V2RamlResource(this.delegate.parentResource());
    }

    @Override
    public void setParentResource(RamlResource parentResource) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getParentUri() {
       RamlResource parentResource = getParentResource();
       if (parentResource == null) {
    	   return "";
       } else {
    	   return parentResource.getUri();
       }
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
