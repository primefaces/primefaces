/**
 * The MIT License
 *
 * Copyright (c) 2009-2019 PrimeTek
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.primefaces.application;

import org.primefaces.PrimeFaces;
import org.primefaces.util.Constants;
import org.primefaces.util.EscapeUtils;

import javax.faces.application.ConfigurableNavigationHandler;
import javax.faces.application.NavigationCase;
import javax.faces.context.FacesContext;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class DialogNavigationHandler extends ConfigurableNavigationHandler {

    private ConfigurableNavigationHandler base;

    public DialogNavigationHandler(ConfigurableNavigationHandler base) {
        this.base = base;
    }

    @Override
    public void handleNavigation(FacesContext context, String fromAction, String outcome) {
        Map<Object, Object> attrs = context.getAttributes();
        String dialogOutcome = (String) attrs.get(Constants.DIALOG_FRAMEWORK.OUTCOME);

        if (dialogOutcome != null) {
            Map<String, String> requestParams = context.getExternalContext().getRequestParameterMap();
            NavigationCase navCase = getNavigationCase(context, fromAction, dialogOutcome);
            String toViewId = navCase.getToViewId(context);
            Map<String, Object> options = (Map<String, Object>) attrs.get(Constants.DIALOG_FRAMEWORK.OPTIONS);
            Map<String, List<String>> params = (Map<String, List<String>>) attrs.get(Constants.DIALOG_FRAMEWORK.PARAMS);

            if (params == null) {
                params = Collections.emptyMap();
            }

            boolean includeViewParams = false;
            if (options != null && options.containsKey(Constants.DIALOG_FRAMEWORK.INCLUDE_VIEW_PARAMS)) {
                includeViewParams = (Boolean) options.get(Constants.DIALOG_FRAMEWORK.INCLUDE_VIEW_PARAMS);
            }

            String url = context.getApplication().getViewHandler().getBookmarkableURL(context, toViewId, params, includeViewParams);
            url = EscapeUtils.forJavaScript(url);

            StringBuilder sb = new StringBuilder();
            String sourceComponentId = (String) attrs.get(Constants.DIALOG_FRAMEWORK.SOURCE_COMPONENT);
            String sourceWidget = (String) attrs.get(Constants.DIALOG_FRAMEWORK.SOURCE_WIDGET);
            String pfdlgcid = requestParams.get(Constants.DIALOG_FRAMEWORK.CONVERSATION_PARAM);
            if (pfdlgcid == null) {
                pfdlgcid = UUID.randomUUID().toString();
            }
            pfdlgcid = EscapeUtils.forJavaScript(pfdlgcid);

            sb.append("PrimeFaces.openDialog({url:'").append(url).append("',pfdlgcid:'").append(pfdlgcid)
                    .append("',sourceComponentId:'").append(sourceComponentId).append("'");

            if (sourceWidget != null) {
                sb.append(",sourceWidgetVar:'").append(sourceWidget).append("'");
            }

            sb.append(",options:{");
            if (options != null && !options.isEmpty()) {
                for (Iterator<String> it = options.keySet().iterator(); it.hasNext();) {
                    String optionName = it.next();
                    Object optionValue = options.get(optionName);

                    sb.append(optionName).append(":");
                    if (optionValue instanceof String) {
                        sb.append("'").append(EscapeUtils.forJavaScript((String) optionValue)).append("'");
                    }
                    else {
                        sb.append(optionValue);
                    }

                    if (it.hasNext()) {
                        sb.append(",");
                    }
                }
            }
            sb.append("}});");

            PrimeFaces.current().executeScript(sb.toString());
            sb.setLength(0);
        }
        else {
            base.handleNavigation(context, fromAction, outcome);
        }
    }

    @Override
    public NavigationCase getNavigationCase(FacesContext context, String fromAction, String outcome) {
        return base.getNavigationCase(context, fromAction, outcome);
    }

    @Override
    public Map<String, Set<NavigationCase>> getNavigationCases() {
        return base.getNavigationCases();
    }
}
