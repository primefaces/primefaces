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
        
        writer.write("PrimeFaces.cw('ThemeSwitcher','" + ts.resolveWidgetVar() + "',{");
        writer.write("id:'" + clientId + "'");
        
        if(ts.getEffect() != null) writer.write(",effect:'" + ts.getEffect() + "'");
        if(ts.getEffectSpeed() != null) writer.write(",effectSpeed:'" + ts.getEffectSpeed() + "'");

        encodeClientBehaviors(context, menu);
		
		writer.write("});});");
        
		endScript(writer);
	}
}