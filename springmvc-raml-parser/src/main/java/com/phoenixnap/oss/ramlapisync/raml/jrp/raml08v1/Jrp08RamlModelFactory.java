package com.phoenixnap.oss.ramlapisync.raml.jrp.raml08v1;

import com.phoenixnap.oss.ramlapisync.raml.RamlAction;
import com.phoenixnap.oss.ramlapisync.raml.RamlActionType;
import com.phoenixnap.oss.ramlapisync.raml.RamlModelFactory;
import com.phoenixnap.oss.ramlapisync.raml.RamlResource;
import org.raml.model.Action;
import org.raml.model.ActionType;
import org.raml.model.Resource;

import java.util.HashMap;
import java.util.Map;

/**
 * @author armin.weisser
 */
public class Jrp08RamlModelFactory implements RamlModelFactory {

    @Override
    public RamlAction createRamlAction() {
        return createRamlAction(new Action());
    }

    @Override
    public RamlAction createRamlAction(Object baseAction) {
        if(baseAction == null) return null;
        return new Jrp08Action((Action)baseAction);
    }

    private Map<ActionType, Action> createActions(Map<RamlActionType, RamlAction> ramlActions) {
        Map<ActionType, Action> actions = new HashMap<>(ramlActions.size());
        ramlActions.keySet().stream().forEach(actionType -> actions.put(RamlActionType.asActionType(actionType), createAction(ramlActions.get(actionType))));
        return actions;
    }

    @Override
    public Action createAction(RamlAction ramlAction) {
        if(ramlAction == null) return null;
        Action action = new Action();
        action.setBody(ramlAction.getBody());
        action.setDescription(ramlAction.getDescription());
        action.setQueryParameters(ramlAction.getQueryParameters());
        action.setResource(createResource(ramlAction.getResource()));
        action.setResponses(ramlAction.getResponses());
        action.setSecuredBy(ramlAction.getSecuredBy());
        action.setType(RamlActionType.asActionType(ramlAction.getType()));
        return action;
    }

    public Resource createResource(RamlResource ramlResource) {
        if(ramlResource == null) return null;
        Resource resource = new Resource();
        resource.setDescription(ramlResource.getDescription());
        resource.setParentResource(createResource(ramlResource.getParentResource()));
        resource.setActions(createActions(ramlResource.getActions()));
        resource.setResources(createResources(ramlResource.getResources()));
        resource.setRelativeUri(ramlResource.getRelativeUri());
        resource.setUriParameters(ramlResource.getUriParameters());
        return resource;
    }

    private Map<String, Resource> createResources(Map<String, RamlResource> ramlResources) {
        Map<String, Resource> resources = new HashMap<>(ramlResources.size());
        ramlResources.keySet().stream().forEach(key -> resources.put(key, createResource(ramlResources.get(key))));
        return resources;
    }

    @Override
    public RamlResource createRamlResource() {
        return createRamlResource(new Resource());
    }

    @Override
    public RamlResource createRamlResource(Object baseResource) {
        if(baseResource == null) return null;
        return new Jrp08RamlResource((Resource)baseResource);
    }

    @Override
    public Map<String, RamlResource> createRamlResources(Map<String, ? extends Object> resources) {
        Map<String, RamlResource> ramlResources = new HashMap<>(resources.size());
        resources.keySet().stream().forEach(key -> ramlResources.put(key, createRamlResource(resources.get(key))));
        return ramlResources;
    }

    @Override
    public Map<RamlActionType, RamlAction> createRamlActions(Map<? extends Object, ? extends Object> actions) {
        Map<RamlActionType, RamlAction> ramlActions = new HashMap<>(actions.size());
        actions.keySet().stream().forEach(key -> ramlActions.put(RamlActionType.asRamlActionType((ActionType) key), createRamlAction(actions.get(key))));
        return ramlActions;
    }

}
