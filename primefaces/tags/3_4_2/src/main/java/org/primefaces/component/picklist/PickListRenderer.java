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
package org.primefaces.component.picklist;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import org.primefaces.component.column.Column;

import org.primefaces.model.DualListModel;
import org.primefaces.renderkit.CoreRenderer;
import org.primefaces.util.HTML;

public class PickListRenderer extends CoreRenderer {
	
	@Override
	public void decode(FacesContext context, UIComponent component) {
		PickList pickList = (PickList) component;
		String clientId = pickList.getClientId(context);
		Map<String,String[]> params = context.getExternalContext().getRequestParameterValuesMap();
		
		String sourceParamKey = clientId + "_source";
		String targetParamKey = clientId + "_target";
        
        String[] sourceParam = params.containsKey(sourceParamKey) ? params.get(sourceParamKey) : new String[]{};
		String[] targetParam = params.containsKey(targetParamKey) ? params.get(targetParamKey) : new String[]{};

		pickList.setSubmittedValue(new String[][]{sourceParam, targetParam});
        
        decodeBehaviors(context, pickList);
	}
	
	@Override
	public void encodeEnd(FacesContext facesContext, UIComponent component) throws IOException {
		PickList pickList = (PickList) component;
		
		encodeMarkup(facesContext, pickList);
		encodeScript(facesContext, pickList);
	}

    protected void encodeMarkup(FacesContext context, PickList pickList) throws IOException {
		ResponseWriter writer = context.getResponseWriter();
		String clientId = pickList.getClientId(context);
		DualListModel model = (DualListModel) pickList.getValue();
        String styleClass = pickList.getStyleClass();
        styleClass = styleClass == null ? PickList.CONTAINER_CLASS : PickList.CONTAINER_CLASS + " " + styleClass;

		writer.startElement("table", pickList);
		writer.writeAttribute("id", clientId, "id");
        writer.writeAttribute("class", styleClass, null);
		if(pickList.getStyle() != null) {
            writer.writeAttribute("style", pickList.getStyle(), null);
        }

		writer.startElement("tbody", null);
		writer.startElement("tr", null);

        //Target List Reorder Buttons
        if(pickList.isShowSourceControls()) {
            encodeListControls(context, pickList, PickList.SOURCE_CONTROLS);
        }
 
		//Source List
		encodeList(context, pickList, clientId + "_source", PickList.SOURCE_CLASS, model.getSource(), pickList.getFacet("sourceCaption"), pickList.isShowSourceFilter());

		//Buttons
		writer.startElement("td", null);
        encodeButton(context, pickList.getAddLabel(), PickList.ADD_BUTTON_CLASS, PickList.ADD_BUTTON_ICON_CLASS);
        encodeButton(context, pickList.getAddAllLabel(), PickList.ADD_ALL_BUTTON_CLASS, PickList.ADD_ALL_BUTTON_ICON_CLASS);
        encodeButton(context, pickList.getRemoveLabel(), PickList.REMOVE_BUTTON_CLASS, PickList.REMOVE_BUTTON_ICON_CLASS);
        encodeButton(context, pickList.getRemoveAllLabel(), PickList.REMOVE_ALL_BUTTON_CLASS, PickList.REMOVE_ALL_BUTTON_ICON_CLASS);
		writer.endElement("td");

		//Target List
		encodeList(context, pickList, clientId + "_target", PickList.TARGET_CLASS, model.getTarget(), pickList.getFacet("targetCaption"), pickList.isShowTargetFilter());

        //Target List Reorder Buttons
        if(pickList.isShowTargetControls()) {
            encodeListControls(context, pickList, PickList.TARGET_CONTROLS);
        }

		writer.endElement("tr");
		writer.endElement("tbody");

		writer.endElement("table");
	}
	
	protected void encodeScript(FacesContext context, PickList pickList) throws IOException {
		ResponseWriter writer = context.getResponseWriter();
		String clientId = pickList.getClientId(context);
		
        startScript(writer, clientId);
        
        writer.write("PrimeFaces.cw('PickList','" + pickList.resolveWidgetVar() + "',{");
        writer.write("id:'" + clientId + "'");
        writer.write(",effect:'" + pickList.getEffect() + "'");
        writer.write(",effectSpeed:'" + pickList.getEffectSpeed() + "'");
        
        if(pickList.isShowSourceControls()) writer.write(",showSourceControls:true");
        if(pickList.isShowTargetControls()) writer.write(",showTargetControls:true");
        if(pickList.isDisabled()) writer.write(",disabled:true");
        if(pickList.getFilterMatchMode() != null) writer.write(",filterMatchMode:'" + pickList.getFilterMatchMode() + "'");
        if(pickList.getFilterFunction() != null) writer.write(",filterFunction:" + pickList.getFilterFunction());
        if(pickList.getOnTransfer() != null) writer.write((",onTransfer:function(e) {" + pickList.getOnTransfer() + ";}"));

        encodeClientBehaviors(context, pickList);
        
        writer.write("});");
		
		endScript(writer);
	}
    
    protected void encodeListControls(FacesContext context, PickList pickList, String styleClass) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        
        writer.startElement("td", null);
        writer.writeAttribute("class", styleClass, null);
        encodeButton(context, pickList.getMoveUpLabel(), PickList.MOVE_UP_BUTTON_CLASS, PickList.MOVE_UP_BUTTON_ICON_CLASS);
        encodeButton(context, pickList.getMoveTopLabel(), PickList.MOVE_TOP_BUTTON_CLASS, PickList.MOVE_TOP_BUTTON_ICON_CLASS);
        encodeButton(context, pickList.getMoveDownLabel(), PickList.MOVE_DOWN_BUTTON_CLASS, PickList.MOVE_DOWN_BUTTON_ICON_CLASS);
        encodeButton(context, pickList.getMoveBottomLabel(), PickList.MOVE_BOTTOM_BUTTON_CLASS, PickList.MOVE_BOTTOM_BUTTON_ICON_CLASS);
        writer.endElement("td");
    }

    protected void encodeCaption(FacesContext context, UIComponent caption) throws IOException {
        ResponseWriter writer = context.getResponseWriter();

        writer.startElement("div", null);
        writer.writeAttribute("class", PickList.CAPTION_CLASS, null);
        caption.encodeAll(context);
        writer.endElement("div");
    }
	
	protected void encodeButton(FacesContext context, String title, String styleClass, String icon) throws IOException {
		ResponseWriter writer = context.getResponseWriter();
        
        writer.startElement("button", null);
        writer.writeAttribute("type", "button", null);
		writer.writeAttribute("class", HTML.BUTTON_ICON_ONLY_BUTTON_CLASS + " " + styleClass, null);
        writer.writeAttribute("title", title, null);
        
        //icon
        writer.startElement("span", null);
        writer.writeAttribute("class", HTML.BUTTON_LEFT_ICON_CLASS + " " + icon, null);
        writer.endElement("span");
        
        //text
        writer.startElement("span", null);
        writer.writeAttribute("class", HTML.BUTTON_TEXT_CLASS, null);
        writer.write("ui-button");
        writer.endElement("span");

        writer.endElement("button");
	}
	
	protected void encodeList(FacesContext context, PickList pickList, String listId, String styleClass, List model, UIComponent caption, boolean filter) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
                
        writer.startElement("td", null);
        
        if(filter) {
            encodeFilter(context, pickList, listId + "_filter");
        }
        
        if(caption != null) {
            encodeCaption(context, caption);
            styleClass += " ui-corner-bottom";
        }
        else {
            styleClass += " ui-corner-all";
        }

        writer.startElement("ul", null);
        writer.writeAttribute("class", styleClass, null);

        encodeOptions(context, pickList, model);

        writer.endElement("ul");
		
		encodeListInput(context, listId);
                
        writer.endElement("td");
	}
    
    protected void encodeListInput(FacesContext context, String clientId) throws IOException {
		ResponseWriter writer = context.getResponseWriter();
		
		writer.startElement("select", null);
        writer.writeAttribute("id", clientId, null);
		writer.writeAttribute("name", clientId, null);
		writer.writeAttribute("multiple", "true", null);
        writer.writeAttribute("class", "ui-helper-hidden", null);

        //items generated on client side

		writer.endElement("select");
	}
	
	@SuppressWarnings("unchecked")
	protected void encodeOptions(FacesContext context, PickList pickList, List model) throws IOException {
		ResponseWriter writer = context.getResponseWriter();
		String var = pickList.getVar();
		Converter converter = pickList.getConverter();
        
        for(Iterator it = model.iterator(); it.hasNext();) {
            Object item = it.next();
			context.getExternalContext().getRequestMap().put(var, item);
			String itemValue = converter != null ? converter.getAsString(context, pickList, pickList.getItemValue()) : pickList.getItemValue().toString();
            String itemLabel = pickList.getItemLabel();
            String itemClass = pickList.isItemDisabled() ? PickList.ITEM_CLASS + " " + PickList.ITEM_DISABLED_CLASS : PickList.ITEM_CLASS;
            
            writer.startElement("li", null);
            writer.writeAttribute("class", itemClass, null);
            writer.writeAttribute("data-item-value", itemValue, null);
            writer.writeAttribute("data-item-label", itemLabel, null);
			
            if(pickList.getChildCount() > 0) {
                writer.startElement("table", null);
                writer.startElement("tbody", null);
                writer.startElement("tr", null);
                        
                 for(UIComponent kid : pickList.getChildren()) {
                     if(kid instanceof Column && kid.isRendered()) {
                         Column column = (Column) kid;
                         
                         writer.startElement("td", null);
                         if(column.getStyle() != null) writer.writeAttribute("style", column.getStyle(), null);
                         if(column.getStyleClass() != null) writer.writeAttribute("class", column.getStyleClass(), null);
                         
                         kid.encodeAll(context);
                         writer.endElement("td");
                     }
                 }
                 
                writer.endElement("tr");
                writer.endElement("tbody");
                writer.endElement("table");
            }
            else {
                writer.writeText(itemLabel, null);
            }
                
			writer.endElement("li");
		}
		
		context.getExternalContext().getRequestMap().remove(var);
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public Object getConvertedValue(FacesContext context, UIComponent component, Object submittedValue) throws ConverterException {
        try {
            PickList pickList = (PickList) component;
            String[][] value = (String[][]) submittedValue;
            String[] sourceValue = value[0];
            String[] targetValue = value[1];
            DualListModel model = new DualListModel();

            pickList.populateModel(context, sourceValue, model.getSource());
            pickList.populateModel(context, targetValue, model.getTarget());

            return model;
        }
        catch(Exception exception) {
            throw new ConverterException(exception);
        }
	}

    protected void encodeFilter(FacesContext context, PickList pickList, String name) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        
        writer.startElement("div", null);
        writer.writeAttribute("class", PickList.FILTER_CONTAINER, null);
        
        writer.startElement("input", null);
        writer.writeAttribute("id", name, null);
        writer.writeAttribute("name", name, null);
        writer.writeAttribute("type", "text", null);
        writer.writeAttribute("class", PickList.FILTER_CLASS, null);
        writer.endElement("input");
        
        writer.startElement("span", null);
        writer.writeAttribute("class", "ui-icon ui-icon-search", null);
        writer.endElement("span");
        
        writer.endElement("div");
    }
    
    @Override
    public void encodeChildren(FacesContext context, UIComponent component) throws IOException {
        //Rendering happens on encodeEnd
    }

    @Override
    public boolean getRendersChildren() {
        return true;
    }
}