/*
 * Copyright 2009-2013 PrimeTek.
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

import javax.faces.FacesException;
import javax.faces.application.ProjectStage;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

/**
 * Helper to generate javascript code of an ajax call
 */
public class WidgetBuilder {
	
    protected ResponseWriter writer;
    protected boolean endFunction = false;
    protected String resourcePath = null;
    protected FacesContext context;
        
    public WidgetBuilder(ResponseWriter writer, FacesContext context) {
    	this.writer = writer;
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
    private WidgetBuilder init(String widgetClass, String widgetVar, String id, String resourcePath, boolean endFunction) throws IOException {

    	if(context.isProjectStage(ProjectStage.Development) && id.equals(widgetVar)) {
        	throw new FacesException("WidgetVar and the generated ClientId should not be identical, " 
        			+ "as it may not work correctly in all browsers. ClientId: "
        			+ id
        			+ "; See: http://stackoverflow.com/questions/9158238/why-js-function-name-conflicts-with-element-id");
        }

    	this.resourcePath = resourcePath;
    	this.endFunction = endFunction;
    	
        writer.write("PrimeFaces.cw('");
        writer.write(widgetClass);
        writer.write("','");
        writer.write(widgetVar);
        writer.write("',{");
        writer.write("id:'");
        writer.write(id);
        writer.write("'");
        
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
    	this.writer.write("$(function(){");
    	this.init(widgetClass, widgetVar, id, null, true);
        
        return this;
    }
    
    public WidgetBuilder initWithDomReady(String widgetClass, String widgetVar, String id, String resourcePath) throws IOException {

    	this.renderScriptBlock(id);
    	this.writer.write("$(function(){");
    	this.init(widgetClass, widgetVar, id, resourcePath, true);
        
        return this;
    }

    public WidgetBuilder initWithWindowLoad(String widgetClass, String widgetVar, String id) throws IOException {
    	
    	this.renderScriptBlock(id);
    	this.writer.write("$(window).load(function(){");
    	this.init(widgetClass, widgetVar, id, null, true);
        
        return this;
    }

    public WidgetBuilder initWithWindowLoad(String widgetClass, String widgetVar, String id, String resourcePath) throws IOException {
    	
    	this.renderScriptBlock(id);
    	this.writer.write("$(window).load(function(){");
    	this.init(widgetClass, widgetVar, id, resourcePath, true);
        
        return this;
    }

    public WidgetBuilder initWithComponentLoad(String widgetClass, String widgetVar, String id, String targetId) throws IOException {
    	
    	this.renderScriptBlock(id);
    	this.writer.write("$(PrimeFaces.escapeClientId('" + targetId + "')).load(function(){");
    	this.init(widgetClass, widgetVar, id, null, true);
        
        return this;
    }
    
    public WidgetBuilder initWithComponentLoad(String widgetClass, String widgetVar, String id, String targetId, String resourcePath) throws IOException {
    	
    	this.renderScriptBlock(id);
    	this.writer.write("$(PrimeFaces.escapeClientId('" + targetId + "')).load(function(){");
    	this.init(widgetClass, widgetVar, id, resourcePath, true);
        
        return this;
    }
    
    private void renderScriptBlock(String id) throws IOException {
        writer.startElement("script", null);
        writer.writeAttribute("id", id + "_s", null);
        writer.writeAttribute("type", "text/javascript", null);
    }

    public WidgetBuilder attr(String name, String value) throws IOException {
        writer.write(",");
        writer.write(name);
        writer.write(":'");
        if (value != null) {
        	writer.write(value);
        }
        writer.write("'");
        
        return this;
    }
    
    public WidgetBuilder nativeAttr(String name, String value) throws IOException {
        writer.write(",");
        writer.write(name);
        writer.write(":");
        if (value != null) {
        	writer.write(value);
        }
        
        return this;
    }
    
    public WidgetBuilder attr(String name, boolean value) throws IOException {
        writer.write(",");
        writer.write(name);
        writer.write(":");
        writer.write(Boolean.toString(value));
        
        return this;
    }
    
    public WidgetBuilder attr(String name, Number value) throws IOException {
        writer.write(",");
        writer.write(name);
        writer.write(":");
        if (value != null) {
        	writer.write(value.toString());
        }
        
        return this;
    }
        
    public WidgetBuilder attr(String name, String value, String defaultValue) throws IOException {
        if(value != null && !value.equals(defaultValue)) {
            writer.write(",");
	        writer.write(name);
	        writer.write(":'");
	        writer.write(value);
	        writer.write("'");
        }
        
        return this;
    }
    
    public WidgetBuilder attr(String name, double value, double defaultValue) throws IOException {
        if(value != defaultValue) {
            writer.write(",");
	        writer.write(name);
	        writer.write(":");
	        writer.write(Double.toString(value));
        }
        
        return this;
    }
    
    public WidgetBuilder attr(String name, int value, int defaultValue) throws IOException {
        if(value != defaultValue) {
            writer.write(",");
	        writer.write(name);
	        writer.write(":");
	        writer.write(Integer.toString(value));
        }
        
        return this;
    }
        
    public WidgetBuilder attr(String name, boolean value, boolean defaultValue) throws IOException {
        if(value != defaultValue) {
            writer.write(",");
	        writer.write(name);
	        writer.write(":");
	        writer.write(Boolean.toString(value));
        }
        
        return this;
    }
    
    public WidgetBuilder callback(String name, String signature, String callback) throws IOException {
        if(callback != null) {
            writer.write(",");
	        writer.write(name);
	        writer.write(":");
	        writer.write(signature);
	        writer.write("{");
	        writer.write(callback);
	        writer.write("}");
        }
        
        return this;
    }
    
    public WidgetBuilder callback(String name, String callback) throws IOException {
        if(callback != null) {
            writer.write(",");
	        writer.write(name);
	        writer.write(":");
	        writer.write(callback);
        }
        
        return this;
    }

    public WidgetBuilder append(String str) throws IOException {
    	writer.write(str);
        
        return this;
    }

    public WidgetBuilder append(char chr) throws IOException {
    	writer.write(chr);
        
        return this;
    }

    public WidgetBuilder append(Number number) throws IOException {
    	writer.write(number.toString());
        
        return this;
    }
    
    public void finish() throws IOException {
        writer.write("}");
        
        if(this.resourcePath != null) {
            writer.write(",'");
	        writer.write(this.resourcePath);
	        writer.write("'");
        } 
        
        writer.write(");");
        
        if(endFunction) {
            writer.write("});");
        }
        
        writer.endElement("script");
    }
}
