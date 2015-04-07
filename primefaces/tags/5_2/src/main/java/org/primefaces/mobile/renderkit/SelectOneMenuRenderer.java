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
package org.primefaces.mobile.renderkit;

import java.io.IOException;
import java.util.List;
import javax.faces.component.UISelectOne;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.convert.Converter;
import javax.faces.model.SelectItem;
import org.primefaces.component.selectonemenu.SelectOneMenu;
import org.primefaces.util.WidgetBuilder;

public class SelectOneMenuRenderer extends org.primefaces.component.selectonemenu.SelectOneMenuRenderer {
    
    @Override
    protected String getSubmitParam(FacesContext context, UISelectOne selectOne) {
        return selectOne.getClientId(context);
    }
        
    @Override
    protected void encodeMarkup(FacesContext context, SelectOneMenu menu) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        List<SelectItem> selectItems = getSelectItems(context, menu);
        Converter converter = menu.getConverter();
        Object values = getValues(menu);
        Object submittedValues = getSubmittedValues(menu);
        String clientId = menu.getClientId(context);

        writer.startElement("select", menu);
        writer.writeAttribute("id", clientId, "id");
        writer.writeAttribute("name", clientId, null);
        writer.writeAttribute("data-role", "none", null);
        
        if (menu.isDisabled()) writer.writeAttribute("disabled", "disabled", null);
        if (menu.getOnkeydown() != null) writer.writeAttribute("onkeydown", menu.getOnkeydown(), null);
        if (menu.getOnkeyup() != null) writer.writeAttribute("onkeyup", menu.getOnkeyup(), null);
        if (menu.getStyle() != null) writer.writeAttribute("style", menu.getStyle(), null);
        if (menu.getStyleClass() != null) writer.writeAttribute("class", menu.getStyleClass(), null);
        
        renderOnchange(context, menu);
        renderDynamicPassThruAttributes(context, menu);
        
        encodeSelectItems(context, menu, selectItems, values, submittedValues, converter);

        writer.endElement("select");
    }
    
    @Override
    protected void encodeScript(FacesContext context, SelectOneMenu menu) throws IOException {
        String clientId = menu.getClientId(context);
        WidgetBuilder wb = getWidgetBuilder(context);
        wb.init("SelectOneMenu", menu.resolveWidgetVar(), clientId);               
        wb.finish();
    }
}
