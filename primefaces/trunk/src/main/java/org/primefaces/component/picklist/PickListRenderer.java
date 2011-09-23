/*
 * Copyright 2009-2011 Prime Technology.
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
import org.primefaces.json.JSONArray;
import org.primefaces.json.JSONException;

import org.primefaces.model.DualListModel;
import org.primefaces.renderkit.CoreRenderer;

public class PickListRenderer extends CoreRenderer {
	
	@Override
	public void decode(FacesContext context, UIComponent component) {
		PickList pickList = (PickList) component;
		String clientId = pickList.getClientId(context);
		Map<String,String> params = context.getExternalContext().getRequestParameterMap();
		
		String sourceParam = clientId + "_source";
		String targetParam = clientId + "_target";
		if(params.containsKey(sourceParam) && params.containsKey(targetParam)) {
			pickList.setSubmittedValue(new String[]{params.get(sourceParam), params.get(targetParam)});
		}
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
		encodeList(context, pickList, clientId + "_source", PickList.SOURCE_CLASS, model.getSource(), pickList.getFacet("sourceCaption"));

		//Buttons
		writer.startElement("td", null);
        encodeButton(context, pickList, pickList.getAddLabel(), PickList.ADD_BUTTON_CLASS);
        encodeButton(context, pickList, pickList.getAddAllLabel(), PickList.ADD_ALL_BUTTON_CLASS);
        encodeButton(context, pickList, pickList.getRemoveLabel(), PickList.REMOVE_BUTTON_CLASS);
        encodeButton(context, pickList, pickList.getRemoveAllLabel(), PickList.REMOVE_ALL_BUTTON_CLASS);
		writer.endElement("td");

		//Target List
		encodeList(context, pickList, clientId + "_target", PickList.TARGET_CLASS, model.getTarget(), pickList.getFacet("targetCaption"));

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
		
		writer.startElement("script", null);
		writer.writeAttribute("type", "text/javascript", null);

		writer.write(pickList.resolveWidgetVar() + " = new PrimeFaces.widget.PickList('" + clientId + "', {");
        writer.write("pojo:" + (pickList.getConverter() != null));
        writer.write(",effect:'" + pickList.getEffect() + "'");
        writer.write(",effectSpeed:'" + pickList.getEffectSpeed() + "'");
        writer.write(",iconOnly:" + pickList.isIconOnly());
        
        if(pickList.isShowSourceControls()) writer.write(",showSourceControls:true");
        if(pickList.isShowTargetControls()) writer.write(",showTargetControls:true");
        if(pickList.isDisabled()) writer.write(",disabled:true");
        if(pickList.getOnTransfer() != null) writer.write((",onTransfer:function(e) {" + pickList.getOnTransfer() + ";}"));

        writer.write("});");
		
		writer.endElement("script");
	}
    
    protected void encodeListControls(FacesContext context, PickList pickList, String styleClass) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        
        writer.startElement("td", null);
        writer.writeAttribute("class", PickList.SOURCE_CONTROLS, null);
        encodeButton(context, pickList, pickList.getMoveUpLabel(), PickList.MOVE_UP_BUTTON_CLASS);
        encodeButton(context, pickList, pickList.getMoveTopLabel(), PickList.MOVE_TOP_BUTTON_CLASS);
        encodeButton(context, pickList, pickList.getMoveDownLabel(), PickList.MOVE_DOWN_BUTTON_CLASS);
        encodeButton(context, pickList, pickList.getMoveBottomLabel(), PickList.MOVE_BOTTOM_BUTTON_CLASS);
        writer.endElement("td");
    }

    protected void encodeCaption(FacesContext context, UIComponent caption) throws IOException {
        ResponseWriter writer = context.getResponseWriter();

        writer.startElement("div", null);
        writer.writeAttribute("class", PickList.CAPTION_CLASS, null);
        caption.encodeAll(context);
        writer.endElement("div");
    }
	
	protected void encodeButton(FacesContext context, PickList pickList, String label, String styleClass) throws IOException {
		ResponseWriter writer = context.getResponseWriter();
        
        writer.startElement("button", null);
        writer.writeAttribute("type", "button", null);
		writer.writeAttribute("class", styleClass, null);
        writer.write(label);
        writer.endElement("button");
	}
	
	protected void encodeList(FacesContext context, PickList pickList, String listId, String styleClass, List model, UIComponent caption) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
                
        writer.startElement("td", null);
        
        if(caption != null) {
            encodeCaption(context, caption);
            styleClass += " ui-corner-bottom";
        }
        else {
            styleClass += " ui-corner-all";
        }

        writer.startElement("ul", null);
        writer.writeAttribute("class", styleClass, null);

        String values = encodeOptions(context, pickList, model);

        writer.endElement("ul");
		
		encodeListStateHolder(context, listId, values);
        
        writer.endElement("ul");
	}
	
	@SuppressWarnings("unchecked")
	protected String encodeOptions(FacesContext context, PickList pickList, List model) throws IOException {
		ResponseWriter writer = context.getResponseWriter();
		String var = pickList.getVar();
		Converter converter = pickList.getConverter();
        JSONArray jSONArray = new JSONArray();
        
        for(Iterator it = model.iterator(); it.hasNext();) {
            Object item = it.next();
			context.getExternalContext().getRequestMap().put(var, item);
			String value = converter != null ? converter.getAsString(context, pickList, pickList.getItemValue()) : pickList.getItemValue().toString();
			
			writer.startElement("li", null);
            writer.writeAttribute("class", PickList.ITEM_CLASS, null);
			
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
                writer.writeText(pickList.getItemLabel(), null);
            }
                
			writer.endElement("li");
			
			jSONArray.put(value);
		}
		
		context.getExternalContext().getRequestMap().remove(var);
		
		return jSONArray.toString();
	}
	
	protected void encodeListStateHolder(FacesContext context, String clientId, String values) throws IOException {
		ResponseWriter writer = context.getResponseWriter();
		
		writer.startElement("input", null);
		writer.writeAttribute("type", "hidden", null);
		writer.writeAttribute("id", clientId, null);
		writer.writeAttribute("name", clientId, null);
		writer.writeAttribute("value", values, null);
		writer.endElement("input");
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public Object getConvertedValue(FacesContext context, UIComponent component, Object submittedValue) throws ConverterException {
        try {
            PickList pickList = (PickList) component;
            String[] value = (String[]) submittedValue;
            JSONArray sourceList = new JSONArray(value[0]);
            JSONArray targetList = new JSONArray(value[1]);
            DualListModel model = new DualListModel();

            doConvertValue(context, pickList, sourceList, model.getSource());
            doConvertValue(context, pickList, targetList, model.getTarget());

            return model;
        }
        catch(JSONException jSONException) {
            throw new ConverterException(jSONException);
        }
	}
	
	@SuppressWarnings("unchecked")
	protected void doConvertValue(FacesContext context, PickList pickList, JSONArray jSONArray, List model) throws JSONException {
		Converter converter = pickList.getConverter();

        for(int i = 0; i < jSONArray.length(); i++) {
            String item = jSONArray.getString(i);
            
			if(isValueBlank(item))
				continue;
			
			String val = item.trim();
			Object convertedValue = converter != null ? converter.getAsObject(context, pickList, val) : val;
			
			if(convertedValue != null)
				model.add(convertedValue);
		}
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