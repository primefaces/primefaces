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
package org.primefaces.component.blockui;

import java.io.IOException;
import javax.faces.FacesException;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import org.primefaces.renderkit.CoreRenderer;

public class BlockUIRenderer extends CoreRenderer {
    
    @Override
	public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
		BlockUI blockUI = (BlockUI) component;
        
        encodeMarkup(context, component);
        encodeScript(context, blockUI);
	}
    
    protected void encodeScript(FacesContext context, BlockUI blockUI) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String clientId = blockUI.getClientId(context);
        String triggers = getTriggers(context, blockUI);
        UIComponent block = blockUI.findComponent(blockUI.getBlock());
        if(block == null) {
            throw new FacesException("Cannot find component with identifier \"" + blockUI.getBlock() + "\" in view.");
        }
        
        startScript(writer, null);
        
        writer.write("$(function() {");
        writer.write("PrimeFaces.cw('BlockUI','" + blockUI.resolveWidgetVar() + "',{");
        writer.write("id:'" + clientId + "'");
        writer.write(",block:'" + block.getClientId(context) + "'");
        if(triggers != null) {
            writer.write(",triggers:'" + triggers + "'");
        }
        if(blockUI.isBlocked()) {
            writer.write(",blocked:true");
        }
        
        writer.write("});});");
        
        endScript(writer);
    }
    
    protected void encodeMarkup(FacesContext context, UIComponent component) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        BlockUI blockUI = (BlockUI) component;
        String clientId = blockUI.getClientId(context);
        
        writer.startElement("div", blockUI);
        writer.writeAttribute("id", clientId, "id");
        writer.writeAttribute("class", "ui-blockui-content ui-widget ui-widget-content ui-corner-all ui-helper-hidden ui-shadow", null);
        
        renderChildren(context, blockUI);
        
        writer.endElement("div");
    }
    
    protected String getTriggers(FacesContext context, BlockUI blockUI) {
        String trigger = blockUI.getTrigger();
        
        if(trigger != null) {
            StringBuilder builder = new StringBuilder();
            String[] ids = trigger.split("[,\\s]+");
            
            for (int i = 0; i < ids.length; i++) {
                String id = ids[i];
                UIComponent component = blockUI.findComponent(id);
                
                if(component == null)
                    throw new FacesException("Cannot find component with identifier \"" + id + "\" in view.");
                else
                    builder.append(component.getClientId(context));   
                
                if(i < (ids.length - 1))
                    builder.append(",");
            }
            
            return builder.toString();
        }
        else {
            return null;
        }        
    }
    
    @Override
    public void encodeChildren(FacesContext context, UIComponent component) throws IOException {
        //Do nothing
    }

    @Override
    public boolean getRendersChildren() {
        return true;
    }
}
