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
package org.primefaces.component.themeswitcher;

import java.io.IOException;

import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import org.primefaces.component.selectonemenu.SelectOneMenu;
import org.primefaces.component.selectonemenu.SelectOneMenuRenderer;

public class ThemeSwitcherRenderer extends SelectOneMenuRenderer {
 
    @Override
	protected void encodeScript(FacesContext context, SelectOneMenu menu) throws IOException {
		ResponseWriter writer = context.getResponseWriter();
        ThemeSwitcher ts = (ThemeSwitcher) menu;
        String clientId = ts.getClientId(context);
        
        startScript(writer, clientId);
		
		writer.write("$(function(){");
		
		writer.write(ts.resolveWidgetVar() + " = new PrimeFaces.widget.ThemeSwitcher('" + ts.getClientId(context) + "', {");
	
        writer.write("effect:'" + menu.getEffect() + "'");
        
        if(menu.getEffectDuration() != 400) writer.write(",effectDuration:" + menu.getEffectDuration());

        encodeClientBehaviors(context, menu);
		
		writer.write("});});");
        
		endScript(writer);
	}
}