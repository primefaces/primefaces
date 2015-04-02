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

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import org.primefaces.expression.SearchExpressionFacade;

/**
 * Helper to generate javascript code of a client side validation*
 */
public class CSVBuilder {
    
    protected StringBuilder buffer;
    protected FacesContext context;
    
    public CSVBuilder(FacesContext context) {
    	this.context = context;
        this.buffer = new StringBuilder();
    }
    
    public CSVBuilder init() {
    	buffer.append("if(PrimeFaces.vb({");
    	return this;
    }
    
    public CSVBuilder source(String source) {
        if(source == null || source.equals("this"))
            buffer.append("s:").append("this");
        else
            buffer.append("s:").append("'").append(source).append("'");

        return this;
    }
    
    public CSVBuilder ajax(boolean value) {
        if(value) {
            buffer.append(",a:").append("true");
        }
                
        return this;
    }
    
    public CSVBuilder process(UIComponent component, String expressions) {        
        if(expressions != null && expressions.trim().length() > 0) {
        	String resolvedExpressions = SearchExpressionFacade.resolveClientIds(context, component, expressions, SearchExpressionFacade.Options.NONE);
            buffer.append(",p:'").append(resolvedExpressions).append("'");
        }
        
        return this;
    }
    
    public CSVBuilder update(UIComponent component, String expressions) {        
        if(expressions != null && expressions.trim().length() > 0) {
        	String resolvedExpressions = SearchExpressionFacade.resolveClientIds(
        			context, component, expressions, SearchExpressionFacade.Options.VALIDATE_RENDERER);
            buffer.append(",u:'").append(resolvedExpressions).append("'");
        }
        
        return this;
    }
    
    public CSVBuilder command(String command) {
        buffer.append("})){").append(command).append("}");
        
        return this;
    }
    
    public String build() {        
        buffer.append("else{return false;}");
        
        String request = buffer.toString();

        reset();
        
        return request;
    }
    
    public void reset() {
        buffer.setLength(0);
    }
}
