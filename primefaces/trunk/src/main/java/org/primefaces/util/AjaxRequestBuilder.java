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

import java.util.Iterator;
import java.util.Map;
import javax.faces.component.UIComponent;
import javax.faces.component.UIParameter;
import javax.faces.context.FacesContext;

import org.primefaces.config.ConfigContainer;
import org.primefaces.context.RequestContext;
import org.primefaces.expression.SearchExpressionFacade;

/**
 * Helper to generate javascript code of an ajax call
 */
public class AjaxRequestBuilder {
	
    protected StringBuilder buffer;
    protected FacesContext context;
    
    private boolean preventDefault = false;
    
    public AjaxRequestBuilder(FacesContext context) {
    	this.context = context;
        this.buffer = new StringBuilder();
    }
    
    public AjaxRequestBuilder init() {
    	buffer.append("PrimeFaces.ab({");
    	return this;
    }

    public AjaxRequestBuilder source(String source) {
        if(source != null)
            buffer.append("source:").append("'").append(source).append("'");
        else
            buffer.append("source:").append("this");
        
        return this;
    }
    
    public AjaxRequestBuilder form(String form) {
        if(form != null) {
            buffer.append(",formId:'").append(form).append("'");
        }
        
        return this;
    }
    
    private boolean isValueBlank(String value) {
		if(value == null)
			return true;
		
		return value.trim().equals("");
	}
    
    public AjaxRequestBuilder process(UIComponent component, String expressions) {        
        addExpressions(component, expressions, "process");
        
        return this;
    }
    
    public AjaxRequestBuilder update(UIComponent component, String expressions) {        
        addExpressions(component, expressions, "update");
        
        return this;
    }
    
    private AjaxRequestBuilder addExpressions(UIComponent component, String expressions, String key) {        
        if(!isValueBlank(expressions)) {
        	String resolvedExpressions = SearchExpressionFacade.resolveComponentsForClient(context, component, expressions, true);
            buffer.append(",").append(key).append(":'").append(resolvedExpressions).append("'");
        }
        
        return this;
    }
    
    public AjaxRequestBuilder event(String event) {
        buffer.append(",event:'").append(event).append("'");
        
        return this;
    }
    
    public AjaxRequestBuilder async(boolean async) {
        if(async) {
            buffer.append(",async:true");
        }
        
        return this;
    }
    
    public AjaxRequestBuilder global(boolean global) {
        if(!global) {
            buffer.append(",global:false");
        }
        
        return this;
    }

    public AjaxRequestBuilder ignoreAutoUpdate(boolean ignoreAutoUpdate) {
        if(ignoreAutoUpdate) {
            buffer.append(",ignoreAutoUpdate:true");
        }
        
        return this;
    }

    public AjaxRequestBuilder partialSubmit(boolean value, boolean partialSubmitSet) {
        ConfigContainer config = RequestContext.getCurrentInstance().getApplicationContext().getConfig();
    	
    	//component can override global setting
        boolean partialSubmit = partialSubmitSet ? value : config.isPartialSubmitEnabled();
        
        if(partialSubmit) {
            buffer.append(",partialSubmit:true");
        }
        
        return this;
    }
    
    public AjaxRequestBuilder resetValues(boolean value, boolean resetValuesSet) {
        ConfigContainer config = RequestContext.getCurrentInstance().getApplicationContext().getConfig();
    	
    	//component can override global setting
        boolean resetValues = resetValuesSet ? value : config.isResetValuesEnabled();
        
        if(resetValues) {
            buffer.append(",resetValues:true");
        }
        
        return this;
    }
    
    public AjaxRequestBuilder onstart(String onstart) {
        if(onstart != null) {
            buffer.append(",onstart:function(cfg){").append(onstart).append(";}");
        }
    
        return this;
    }
    
    public AjaxRequestBuilder onerror(String onerror) {
        if(onerror != null) {
            buffer.append(",onerror:function(xhr,status,error){").append(onerror).append(";}");
        }
    
        return this;
    }
    
    public AjaxRequestBuilder onsuccess(String onsuccess) {
        if(onsuccess != null) {
            buffer.append(",onsuccess:function(data,status,xhr){").append(onsuccess).append(";}");
        }
    
        return this;
    }
    
    public AjaxRequestBuilder oncomplete(String oncomplete) {
        if(oncomplete != null) {
            buffer.append(",oncomplete:function(xhr,status,args){").append(oncomplete).append(";}");
        }
    
        return this;
    }
    
    public AjaxRequestBuilder params(UIComponent component) {
        boolean paramWritten = false;
        
        for(UIComponent child : component.getChildren()) {
            if(child instanceof UIParameter) {
                UIParameter parameter = (UIParameter) child;

                if(!paramWritten) {
                    paramWritten = true;
                    buffer.append(",params:[");
                } else {
                    buffer.append(",");
                }

                buffer.append("{name:").append("'").append(parameter.getName()).append("',value:'").append(parameter.getValue()).append("'}");
            }
        }

        if(paramWritten) {
            buffer.append("]");
        }
        
        return this;
    }
    
    public AjaxRequestBuilder params(Map<String,Object> params) {
        if(params != null && !params.isEmpty()) {
            buffer.append(",params:[");
            
            for(Iterator<String> it = params.keySet().iterator(); it.hasNext();) {
                String name = it.next();
                Object value = params.get(name);
                        
                buffer.append("{name:").append("'").append(name).append("',value:'").append(value).append("'}");
                
                if(it.hasNext()) {
                    buffer.append(",");
                }
            }
            
            buffer.append("]");
        }
        
        return this;
    }
    
    public AjaxRequestBuilder passParams() {
        buffer.append(",params:arguments[0]");
        
        return this;
    }
    
    public AjaxRequestBuilder preventDefault() {
        this.preventDefault = true;
        
        return this;
    }

    public StringBuilder getBuffer() {
        return buffer;
    }
      
    public String build() {
        addFragmentConfig();
        
        buffer.append("});");
        
        if(preventDefault) {
            buffer.append("return false;");
        }
        
        String request = buffer.toString();

        reset();
        
        return request;
    }
    
    public String buildBehavior() {
        addFragmentConfig();
        
        buffer.append("}, arguments[1]);");
        
        if(preventDefault) {
            buffer.append("return false;");
        }
        
        String request = "return " + buffer.toString();
        
        reset();
        
        return request;
    }
    
    public void reset() {
        buffer.setLength(0);
        preventDefault = false;
    }
    
    private void addFragmentConfig() {
        Map<Object,Object> attrs = RequestContext.getCurrentInstance().getAttributes();
        Object fragmentId = attrs.get(Constants.FRAGMENT_ID);
        if(fragmentId != null) {
            buffer.append(",fragmentId:'").append(fragmentId).append("'");
            
            if(attrs.containsKey(Constants.FRAGMENT_AUTO_RENDERED))
                buffer.append(",fragmentUpdate:true");
        }
    }
}
