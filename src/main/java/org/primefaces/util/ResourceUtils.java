/*
 * Copyright 2014 tandraschko.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.primefaces.util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.FacesException;
import javax.faces.application.Application;
import javax.faces.application.Resource;
import javax.faces.application.ResourceHandler;
import javax.faces.component.UIComponent;
import javax.faces.component.UIOutput;
import javax.faces.context.FacesContext;

public class ResourceUtils {

    private static final Logger LOG = Logger.getLogger(ResourceUtils.class.getName());
    
    public static final String RENDERER_SCRIPT = "javax.faces.resource.Script";
    public static final String RENDERER_STYLESHEET = "javax.faces.resource.Stylesheet";
   
    public static void addComponentResource(FacesContext context, String name, String library, String target) {

        Application application = context.getApplication();

        UIComponent componentResource = application.createComponent(UIOutput.COMPONENT_TYPE);
        componentResource.setRendererType(application.getResourceHandler().getRendererTypeForResourceName(name));
        componentResource.getAttributes().put("name", name);
        componentResource.getAttributes().put("library", library);
        componentResource.getAttributes().put("target", target);

        context.getViewRoot().addComponentResource(context, componentResource, target);
    }

    public static void addComponentResource(FacesContext context, String name, String library) {
        addComponentResource(context, name, library, "head");
    }
   
    public static void addComponentResource(FacesContext context, String name) {
        addComponentResource(context, name, Constants.LIBRARY, "head");
    }
   
    public static boolean isScript(UIComponent component) {
        return RENDERER_SCRIPT.equals(component.getRendererType());
    }

    public static boolean isStylesheet(UIComponent component) {
        return RENDERER_STYLESHEET.equals(component.getRendererType());
    }
   
    public static ArrayList<ResourceInfo> getComponentResources(FacesContext context) {
        ArrayList<ResourceInfo> resourceInfos = new ArrayList<ResourceInfo>();
        
        List<UIComponent> resources = context.getViewRoot().getComponentResources(context, "head");
        if (resources != null) {
            for (UIComponent resource : resources) {
                ResourceUtils.ResourceInfo resourceInfo = newResourceInfo(resource);
                if (resourceInfo != null && !resourceInfos.contains(resourceInfo)) {
                    resourceInfos.add(resourceInfo);
                }
            }
        }
        
        return resourceInfos;
    }
    
    public static ArrayList<String> filterStylesheets(FacesContext context, ArrayList<ResourceInfo> resourceInfos) {
        if (resourceInfos == null || resourceInfos.isEmpty()) {
            return null;
        }
        
        ResourceHandler resourceHandler = context.getApplication().getResourceHandler();
        
        ArrayList<String> stylesheets = new ArrayList<String>();
        for (ResourceInfo resourceInfo : resourceInfos) {
            if (isStylesheet(resourceInfo.getResource()) && !isInline(resourceInfo)) {
                Resource resource;
                if (ComponentUtils.isValueBlank(resourceInfo.getLibrary())) {
                    resource = resourceHandler.createResource(resourceInfo.getName());
                }
                else {
                    resource = resourceHandler.createResource(resourceInfo.getName(), resourceInfo.getLibrary());
                }

                if (resource == null) {
                    LOG.log(Level.WARNING, "Resource not found, ignore it. Name: " + resourceInfo.getName() + ", Library: " + resourceInfo.getLibrary());
                }
                else {
                    stylesheets.add(resource.getRequestPath());
                }
            }
        }
        return stylesheets;
    }
    
    public static ArrayList<String> filterScripts(FacesContext context, ArrayList<ResourceInfo> resourceInfos) {
        if (resourceInfos == null || resourceInfos.isEmpty()) {
            return null;
        }
        
        ResourceHandler resourceHandler = context.getApplication().getResourceHandler();
        
        ArrayList<String> scripts = new ArrayList<String>();
        for (ResourceInfo resourceInfo : resourceInfos) {
            if (isScript(resourceInfo.getResource()) && !isInline(resourceInfo)) {
                Resource resource;
                if (ComponentUtils.isValueBlank(resourceInfo.getLibrary())) {
                    resource = resourceHandler.createResource(resourceInfo.getName());
                }
                else {
                    resource = resourceHandler.createResource(resourceInfo.getName(), resourceInfo.getLibrary());
                }
                
                if (resource == null) {
                    LOG.log(Level.WARNING, "Resource not found, ignore it. Name: " + resourceInfo.getName() + ", Library: " + resourceInfo.getLibrary());
                }
                else {
                    scripts.add(resource.getRequestPath());
                }
            }
        }
        return scripts;
    }
    
    public static boolean isInline(ResourceInfo resourceInfo) {
        if (resourceInfo != null) {
            return ComponentUtils.isValueBlank(resourceInfo.getLibrary()) && ComponentUtils.isValueBlank(resourceInfo.getName());
        }

        return false;
    }
    
    public static ResourceInfo newResourceInfo(UIComponent component) {
       
        if (!(component instanceof UIOutput)) {
            return null;
        }
       
        String library = (String) component.getAttributes().get("library");
        String name = (String) component.getAttributes().get("name");
       
        return new ResourceInfo(library, name, component);
    }
   
    public static Resource newResource(ResourceInfo resourceInfo, FacesContext context) {
        Resource resource = context.getApplication().getResourceHandler().createResource(resourceInfo.getName(), resourceInfo.getLibrary());
       
        if (resource == null) {
            throw new FacesException("Resource '" + resourceInfo.getName() + "' in library '" + resourceInfo.getLibrary() + "' not found!");
        }
       
        return resource;
    }

    public static class ResourceInfo implements Serializable {

        private String library;
        private String name;
        private UIComponent resource;

        public ResourceInfo(String library, String name, UIComponent resource) {
            this.library = library;
            this.name = name;
            this.resource = resource;
        }

        public String getLibrary() {
            return library;
        }

        public void setLibrary(String library) {
            this.library = library;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public UIComponent getResource() {
            return resource;
        }

        public void setResource(UIComponent resource) {
            this.resource = resource;
        }

        @Override
        public int hashCode() {
            int hash = 7;
            hash = 41 * hash + (this.library != null ? this.library.hashCode() : 0);
            hash = 41 * hash + (this.name != null ? this.name.hashCode() : 0);
            return hash;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final ResourceInfo other = (ResourceInfo) obj;
            if ((this.library == null) ? (other.library != null) : !this.library.equals(other.library)) {
                return false;
            }
            if ((this.name == null) ? (other.name != null) : !this.name.equals(other.name)) {
                return false;
            }
            return true;
        }
    }
}