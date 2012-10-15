/*
 * Copyright 2009-2012 Prime Teknoloji.
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

/**
 * Helper to generate javascript code of an ajax call
 */
public class WidgetBuilder {
    
    protected StringBuilder buffer;
        
    private WidgetBuilder() {}

    /**
     *
     * @param widgetClass   Constructor name of the widget
     * @param widgetVar     Name of the client side widget
     * @param id            Client id of the component
     */
    public WidgetBuilder(String widgetClass, String widgetVar, String id) {
        buffer = new StringBuilder();
        buffer.append("PrimeFaces.cw('").append(widgetClass).append("','").append(widgetVar).append("',{");
        buffer.append("id:'").append(id).append("'");
    }
    
    public WidgetBuilder attr(String name, String value, String defaultValue) {
        if(value != null && !value.equals(defaultValue)) {
            buffer.append(",").append(name).append(":'").append(value).append("'");
        }
        
        return this;
    }
    
    public WidgetBuilder attr(String name, double value, double defaultValue) {
        if(value != defaultValue) {
            buffer.append(",").append(name).append(":").append(value);
        }
        
        return this;
    }
    
    public WidgetBuilder attr(String name, int value, int defaultValue) {
        if(value != defaultValue) {
            buffer.append(",").append(name).append(":").append(value);
        }
        
        return this;
    }
        
    public WidgetBuilder attr(String name, boolean value, boolean defaultValue) {
        if(value != defaultValue) {
            buffer.append(",").append(name).append(":").append(value);
        }
        
        return this;
    }
    
    public WidgetBuilder callback(String name, String signature, String callback) {
        if(callback != null) {
            buffer.append(",").append(name).append(":").append(signature).append("{").append(callback).append("}");
        }
        
        return this;
    }
    
    public WidgetBuilder append(String str) {
        buffer.append(str);
        
        return this;
    }

    public String build() {
        buffer.append("});");
        
        String script = buffer.toString();
        buffer.setLength(0);
        
        return script;
    }
}
