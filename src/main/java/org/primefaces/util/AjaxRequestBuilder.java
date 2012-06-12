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

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.faces.component.UIComponent;
import javax.faces.component.UIParameter;
import javax.faces.context.FacesContext;

/**
 * Helper to generate javascript code of an ajax call
 */
public class AjaxRequestBuilder {
    
    protected StringBuilder buffer;
    
    private boolean preventDefault = false;
    
    public AjaxRequestBuilder() {
        buffer = new StringBuilder();
        buffer.append("PrimeFaces.ab({");
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
    
    private String[] parseIds(String ids) {
        Pattern p = Pattern.compile("@\\(.+\\)\\s*");
        Matcher m = p.matcher(ids);
        String selector, regular;
        
        if(m.find()) {
            selector = m.group().trim();
            regular = m.replaceAll("");
        }
        else {
            selector = null;
            regular = ids;
        }
        
        if(isValueBlank(regular)) {
            regular = null;
        }
        
        return new String[]{regular, selector};
    }
    
    private boolean isValueBlank(String value) {
		if(value == null)
			return true;
		
		return value.trim().equals("");
	}
    
    public AjaxRequestBuilder process(FacesContext context, UIComponent component, String ids) {        
        addIds(context, component, ids, "process", "processSelector");
        
        return this;
    }
    
    public AjaxRequestBuilder update(FacesContext context, UIComponent component, String ids) {        
        addIds(context, component, ids, "update", "updateSelector");
        
        return this;
    }
    
    private AjaxRequestBuilder addIds(FacesContext context, UIComponent component, String ids, String key, String keySel) {        
        if(!isValueBlank(ids)) {
            String[] parsed = parseIds(ids);
            String regular = parsed[0];
            String selector = parsed[1];
            
            if(regular != null)
                buffer.append(",").append(key).append(":'").append(ComponentUtils.findClientIds(context, component, regular)).append("'");
            
            if(selector != null)
                buffer.append(",").append(keySel).append(":'").append(selector).append("'");
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
    
    public AjaxRequestBuilder partialSubmit(boolean value, boolean partialSubmitSet) {
        //component can override global setting
        boolean partialSubmit = partialSubmitSet ? value : ComponentUtils.isPartialSubmitEnabled(FacesContext.getCurrentInstance());
        
        if(partialSubmit) {
            buffer.append(",partialSubmit:true");
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
        buffer.append("});");
        
        if(preventDefault) {
            buffer.append("return false;");
        }
        
        String request = buffer.toString();
        buffer.setLength(0);
        
        return request;
    }
    
    public String buildBehavior() {
        buffer.append("}, arguments[1]);");
        
        if(preventDefault) {
            buffer.append("return false;");
        }
        
        String request = buffer.toString();
        buffer.setLength(0);
        
        return request;
    }
}
