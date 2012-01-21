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
package org.primefaces.component.button;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.faces.application.ConfigurableNavigationHandler;
import javax.faces.application.NavigationCase;
import javax.faces.component.UIComponent;
import javax.faces.component.UIParameter;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import org.primefaces.renderkit.CoreRenderer;
import org.primefaces.util.HTML;

public class ButtonRenderer extends CoreRenderer {

    @Override
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
        Button button = (Button) component;

        encodeMarkup(context, button);
        encodeScript(context, button);
    }

    public void encodeMarkup(FacesContext context, Button button) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
		String clientId = button.getClientId(context);
        String value = (String) button.getValue();
        String icon = button.resolveIcon();

		writer.startElement("button", button);
		writer.writeAttribute("id", clientId, "id");
		writer.writeAttribute("name", clientId, "name");
        writer.writeAttribute("type", "button", null);
		writer.writeAttribute("class", button.resolveStyleClass(), "styleClass");

		renderPassThruAttributes(context, button, HTML.BUTTON_ATTRS, HTML.CLICK_EVENT);

        if(button.isDisabled()) 
            writer.writeAttribute("disabled", "disabled", "disabled");
        
		writer.writeAttribute("onclick", buildOnclick(context, button), null);

		//icon
        if(icon != null) {
            String defaultIconClass = button.getIconPos().equals("left") ? HTML.BUTTON_LEFT_ICON_CLASS : HTML.BUTTON_RIGHT_ICON_CLASS; 
            String iconClass = defaultIconClass + " " + icon;
            
            writer.startElement("span", null);
            writer.writeAttribute("class", iconClass, null);
            writer.endElement("span");
        }
        
        //text
        writer.startElement("span", null);
        writer.writeAttribute("class", HTML.BUTTON_TEXT_CLASS, null);
        
        if(value == null)
            writer.write("ui-button");
        else
            writer.writeText(value, "value");
        
        writer.endElement("span");
			

		writer.endElement("button");
    }

    public void encodeScript(FacesContext context, Button button) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
		String clientId = button.getClientId(context);
		boolean hasValue = (button.getValue() != null);

        startScript(writer, clientId);
        
        writer.write("PrimeFaces.cw('Button','" + button.resolveWidgetVar() + "',{");
        writer.write("id:'" + clientId + "'");

		if(button.getImage() != null) {
			writer.write(",text:" + hasValue);
			writer.write(",icons:{");
			writer.write("primary:'" + button.getImage() + "'");
			writer.write("}");
		}

		writer.write("});");

		endScript(writer);
    }

    protected String buildOnclick(FacesContext context, Button button) {
        String href = button.getHref();
        String userOnclick = button.getOnclick();
        StringBuilder onclick = new StringBuilder();
        String url;
        
        if(userOnclick != null) {
            onclick.append(userOnclick).append(";");
        }
        
        String onclickBehaviors = getOnclickBehaviors(context, button);
        if(onclickBehaviors != null) {
            onclick.append(onclickBehaviors).append(";");
        }
        
        if(href != null) {
            url = getResourceURL(context, href);
        } 
        else {
            NavigationCase navCase = findNavigationCase(context, button);
            String toViewId = navCase.getToViewId(context);
            boolean isIncludeViewParams = isIncludeViewParams(button, navCase);
            Map<String, List<String>> params = getParams(navCase, button);

            url = context.getApplication().getViewHandler().getBookmarkableURL(context, toViewId, params, isIncludeViewParams);

            //fragment
            if(button.getFragment() != null) {
                url += "#" + button.getFragment();
            }
        }
        
        if(url != null) {
            onclick.append("window.location.href='").append(url).append("';");
        }
        
        return onclick.toString();
    }

    protected NavigationCase findNavigationCase(FacesContext context, Button button) {
        ConfigurableNavigationHandler navHandler = (ConfigurableNavigationHandler) context.getApplication().getNavigationHandler();
        String outcome = button.getOutcome();
        
        if(outcome == null) {
            outcome = context.getViewRoot().getViewId();
        }
        
        return navHandler.getNavigationCase(context, null, outcome);
    }

    /**
     * Find all parameters to include by looking at nested uiparams and params of navigation case
     */
    protected Map<String, List<String>> getParams(NavigationCase navCase, Button button) {
        Map<String, List<String>> params = new LinkedHashMap<String, List<String>>();

        //UIParams
        for(UIComponent child : button.getChildren()) {
            if(child.isRendered() && (child instanceof UIParameter)) {
                UIParameter uiParam = (UIParameter) child;
                
                if(!uiParam.isDisable()) {
                    List<String> paramValues = params.get(uiParam.getName());
                    if(paramValues == null) {
                        paramValues = new ArrayList<String>();
                        params.put(uiParam.getName(), paramValues);
                    }

                    paramValues.add(String.valueOf(uiParam.getValue()));
                }
            }
        }

        //NavCase Params
        Map<String, List<String>> navCaseParams = navCase.getParameters();
        if(navCaseParams != null && !navCaseParams.isEmpty()) {
            for(Map.Entry<String,List<String>> entry : navCaseParams.entrySet()) {
                String key = entry.getKey();

                //UIParams take precedence
                if(!params.containsKey(key)) {
                    params.put(key, entry.getValue());
                }
            }
        }

        return params;
    }

    protected boolean isIncludeViewParams(Button button, NavigationCase navCase) {
        return button.isIncludeViewParams() || navCase.isIncludeViewParams();
    }
}