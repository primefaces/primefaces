/*
 * Copyright 2009-2014 PrimeTek.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.primefaces.util;

import java.io.IOException;

import javax.faces.context.FacesContext;

/**
 * Helper to generate javascript code of an ajax call
 */
public class WidgetBuilder {

    protected boolean endFunction = false;
    protected String resourcePath = null;
    protected FacesContext context;
        
    public WidgetBuilder(FacesContext context) {
    	this.context = context;
    }
    
    /**
     *
     * @param widgetClass   Constructor name of the widget
     * @param widgetVar     Name of the client side widget
     * @param id            Client id of the component
     * @param onload        Flag to define if widget should be created on document load
     * @throws IOException 
     */
    protected WidgetBuilder init(String widgetClass, String widgetVar, String id, String resourcePath, boolean endFunction) throws IOException {
    	this.resourcePath = resourcePath;
    	this.endFunction = endFunction;
    	
        context.getResponseWriter().write("PrimeFaces.cw(\"");
        context.getResponseWriter().write(widgetClass);
        context.getResponseWriter().write("\",\"");
        context.getResponseWriter().write(widgetVar);
        context.getResponseWriter().write("\",{");
        context.getResponseWriter().write("id:\"");
        context.getResponseWriter().write(id);
        context.getResponseWriter().write("\"");

        return this;
    }

    public WidgetBuilder init(String widgetClass, String widgetVar, String id) throws IOException {
    	this.renderScriptBlock(id);
    	this.init(widgetClass, widgetVar, id, null, false);
        
        return this;
    }
    
    public WidgetBuilder init(String widgetClass, String widgetVar, String id, String resourcePath) throws IOException {
    	this.renderScriptBlock(id);
    	this.init(widgetClass, widgetVar, id, resourcePath, false);
        
        return this;
    }

    public WidgetBuilder initWithDomReady(String widgetClass, String widgetVar, String id) throws IOException {

    	this.renderScriptBlock(id);
    	context.getResponseWriter().write("$(function(){");
    	this.init(widgetClass, widgetVar, id, null, true);
        
        return this;
    }
    
    public WidgetBuilder initWithDomReady(String widgetClass, String widgetVar, String id, String resourcePath) throws IOException {

    	this.renderScriptBlock(id);
    	context.getResponseWriter().write("$(function(){");
    	this.init(widgetClass, widgetVar, id, resourcePath, true);
        
        return this;
    }

    public WidgetBuilder initWithWindowLoad(String widgetClass, String widgetVar, String id) throws IOException {
    	
    	this.renderScriptBlock(id);
    	context.getResponseWriter().write("$(window).load(function(){");
    	this.init(widgetClass, widgetVar, id, null, true);
        
        return this;
    }

    public WidgetBuilder initWithWindowLoad(String widgetClass, String widgetVar, String id, String resourcePath) throws IOException {
    	
    	this.renderScriptBlock(id);
    	context.getResponseWriter().write("$(window).load(function(){");
    	this.init(widgetClass, widgetVar, id, resourcePath, true);
        
        return this;
    }

    public WidgetBuilder initWithComponentLoad(String widgetClass, String widgetVar, String id, String targetId) throws IOException {
    	
    	this.renderScriptBlock(id);
    	context.getResponseWriter().write("$(PrimeFaces.escapeClientId(\"" + targetId + "\")).load(function(){");
    	this.init(widgetClass, widgetVar, id, null, true);
        
        return this;
    }
    
    public WidgetBuilder initWithComponentLoad(String widgetClass, String widgetVar, String id, String targetId, String resourcePath) throws IOException {
    	
    	this.renderScriptBlock(id);
    	context.getResponseWriter().write("$(PrimeFaces.escapeClientId(\"" + targetId + "\")).load(function(){");
    	this.init(widgetClass, widgetVar, id, resourcePath, true);
        
        return this;
    }
    
    private void renderScriptBlock(String id) throws IOException {
        context.getResponseWriter().startElement("script", null);
        context.getResponseWriter().writeAttribute("id", id + "_s", null);
        context.getResponseWriter().writeAttribute("type", "text/javascript", null);
    }

    public WidgetBuilder attr(String name, String value) throws IOException {
        if (value != null) {
            context.getResponseWriter().write(",");
            context.getResponseWriter().write(name);
            context.getResponseWriter().write(":\"");
        	context.getResponseWriter().write(value);
            context.getResponseWriter().write("\"");
        }

        return this;
    }
    
    public WidgetBuilder nativeAttr(String name, String value) throws IOException {
        if (value != null) {
            context.getResponseWriter().write(",");
            context.getResponseWriter().write(name);
            context.getResponseWriter().write(":");
        	context.getResponseWriter().write(value);
        }
        
        return this;
    }
    
    public WidgetBuilder nativeAttr(String name, String value, String defaultValue) throws IOException {
        if(value != null && !value.equals(defaultValue)) {
            context.getResponseWriter().write(",");
            context.getResponseWriter().write(name);
            context.getResponseWriter().write(":");
            context.getResponseWriter().write(value);
        }
        
        return this;
    }

    public WidgetBuilder attr(String name, Boolean value) throws IOException {
        if (value != null) {
            context.getResponseWriter().write(",");
            context.getResponseWriter().write(name);
            context.getResponseWriter().write(":");
            context.getResponseWriter().write(Boolean.toString(value));
        }
        
        return this;
    }
    
    public WidgetBuilder attr(String name, Number value) throws IOException {
        if (value != null) {
            context.getResponseWriter().write(",");
            context.getResponseWriter().write(name);
            context.getResponseWriter().write(":");
        	context.getResponseWriter().write(value.toString());
        }
        
        return this;
    }
        
    public WidgetBuilder attr(String name, String value, String defaultValue) throws IOException {
        if(value != null && !value.equals(defaultValue)) {
            context.getResponseWriter().write(",");
	        context.getResponseWriter().write(name);
	        context.getResponseWriter().write(":\"");
	        context.getResponseWriter().write(value);
	        context.getResponseWriter().write("\"");
        }
        
        return this;
    }
    
    public WidgetBuilder attr(String name, double value, double defaultValue) throws IOException {
        if(value != defaultValue) {
            context.getResponseWriter().write(",");
	        context.getResponseWriter().write(name);
	        context.getResponseWriter().write(":");
	        context.getResponseWriter().write(Double.toString(value));
        }
        
        return this;
    }
    
    public WidgetBuilder attr(String name, int value, int defaultValue) throws IOException {
        if(value != defaultValue) {
            context.getResponseWriter().write(",");
	        context.getResponseWriter().write(name);
	        context.getResponseWriter().write(":");
	        context.getResponseWriter().write(Integer.toString(value));
        }
        
        return this;
    }
        
    public WidgetBuilder attr(String name, boolean value, boolean defaultValue) throws IOException {
        if(value != defaultValue) {
            context.getResponseWriter().write(",");
	        context.getResponseWriter().write(name);
	        context.getResponseWriter().write(":");
	        context.getResponseWriter().write(Boolean.toString(value));
        }
        
        return this;
    }
    
    public WidgetBuilder callback(String name, String signature, String callback) throws IOException {
        if(callback != null) {
            context.getResponseWriter().write(",");
	        context.getResponseWriter().write(name);
	        context.getResponseWriter().write(":");
	        context.getResponseWriter().write(signature);
	        context.getResponseWriter().write("{");
	        context.getResponseWriter().write(callback);
	        context.getResponseWriter().write("}");
        }
        
        return this;
    }
    
    public WidgetBuilder callback(String name, String callback) throws IOException {
        if(callback != null) {
            context.getResponseWriter().write(",");
	        context.getResponseWriter().write(name);
	        context.getResponseWriter().write(":");
	        context.getResponseWriter().write(callback);
        }
        
        return this;
    }

    public WidgetBuilder append(String str) throws IOException {
    	context.getResponseWriter().write(str);
        
        return this;
    }

    public WidgetBuilder append(char chr) throws IOException {
    	context.getResponseWriter().write(chr);
        
        return this;
    }

    public WidgetBuilder append(Number number) throws IOException {
    	context.getResponseWriter().write(number.toString());
        
        return this;
    }
    
    public void finish() throws IOException {
        context.getResponseWriter().write("}");
        
        if(this.resourcePath != null) {
            context.getResponseWriter().write(",\"");
	        context.getResponseWriter().write(this.resourcePath);
	        context.getResponseWriter().write("\"");
        } 
        
        context.getResponseWriter().write(");");
        
        if(endFunction) {
            context.getResponseWriter().write("});");
        }
        
        context.getResponseWriter().endElement("script");
    }
}
