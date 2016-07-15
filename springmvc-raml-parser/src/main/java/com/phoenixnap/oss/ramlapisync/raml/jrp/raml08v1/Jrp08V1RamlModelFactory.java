package com.phoenixnap.oss.ramlapisync.raml.jrp.raml08v1;

import com.phoenixnap.oss.ramlapisync.raml.RamlAction;
import com.phoenixnap.oss.ramlapisync.raml.RamlActionType;
import com.phoenixnap.oss.ramlapisync.raml.RamlDocumentationItem;
import com.phoenixnap.oss.ramlapisync.raml.RamlModelEmitter;
import com.phoenixnap.oss.ramlapisync.raml.RamlModelFactory;
import com.phoenixnap.oss.ramlapisync.raml.RamlResource;
import com.phoenixnap.oss.ramlapisync.raml.RamlRoot;
import org.raml.model.Action;
import org.raml.model.ActionType;
import org.raml.model.DocumentationItem;
import org.raml.model.Raml;
import org.raml.model.Resource;
import org.raml.parser.visitor.RamlDocumentBuilder;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author armin.weisser
 */
public class Jrp08V1RamlModelFactory implements RamlModelFactory {

    @Override
    public RamlModelEmitter createRamlModelEmitter() {
        return new Jrp08V1RamlModelEmitter();
    }

    @Override
    public RamlRoot buildRamlRoot(String ramlFileUrl) {
        return createRamlRoot(new RamlDocumentBuilder().build(ramlFileUrl));
    }

    @Override
    public RamlRoot createRamlRoot() {
        return createRamlRoot(new Raml());
    }

    @Override
    public RamlRoot createRamlRoot(String ramlFileUrl) {
        Raml raml = new RamlDocumentBuilder().build(ramlFileUrl);
        return createRamlRoot(raml);
    }

    RamlRoot createRamlRoot(Raml raml) {
        if (raml == null) {
            return null;
        }
        return new Jrp08V1RamlRoot(raml);
    }

    @Override
    public RamlResource createRamlResource() {
        return createRamlResource(new Resource());
    }

    @Override
    public RamlResource createRamlResource(Object resource) {
        if (resource == null) {
            return null;
        }
        return new Jrp08V1RamlResource((Resource) resource);
    }

    Resource extractResource(RamlResource ramlResource) {
        if (ramlResource == null) return null;
        return ((Jrp08V1RamlResource) ramlResource).getResource();
    }

    @Override
    public RamlAction createRamlAction(Object action) {
        if (action == null) {
            return null;
        }
        return new Jrp08V1RamlAction((Action) action);
    }

    @Override
    public RamlAction createRamlAction() {
        return createRamlAction(new Action());
    }

    Action extractAction(RamlAction ramlAction) {
        return ((Jrp08V1RamlAction) ramlAction).getAction();
    }

    @Override
    public RamlDocumentationItem createDocumentationItem() {
        return createDocumentationItem(new DocumentationItem());
    }

    @Override
    public RamlDocumentationItem createDocumentationItem(Object documentationItem) {
        return new Jrp08V1RamlDocumentationItem((DocumentationItem)documentationItem);
    }

    List<DocumentationItem> extractDocumentationItems(List<RamlDocumentationItem> ramlRocumentationItems) {
        if (ramlRocumentationItems == null) {
            return null;
        }
        return ramlRocumentationItems.stream()
                .map(ramlDocumentationItem -> extractDocumentationItem(ramlDocumentationItem))
                .collect(Collectors.toList());
    }

    private DocumentationItem extractDocumentationItem(RamlDocumentationItem ramlDocumentationItem) {
        return ((Jrp08V1RamlDocumentationItem) ramlDocumentationItem).getDocumentationItem();
    }

    @Override
    public RamlActionType createActionType(Object type) {
        return RamlActionType.valueOf(((ActionType)type).name());
    }

    ActionType extractActionType(RamlActionType ramlActionType) {
        return ActionType.valueOf(ramlActionType.name());
    }
}
