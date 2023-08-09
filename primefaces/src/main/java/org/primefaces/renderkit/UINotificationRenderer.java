/*
 * The MIT License
 *
 * Copyright (c) 2009-2023 PrimeTek Informatics
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
package org.primefaces.renderkit;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

import org.primefaces.component.api.UINotification;
import org.primefaces.component.api.UINotifications;
import org.primefaces.expression.SearchExpressionUtils;
import org.primefaces.util.LangUtils;

public class UINotificationRenderer extends CoreRenderer {

    protected boolean shouldRender(UINotification component, FacesMessage message, String severityName) {
        String severityLevel = component.getSeverity();
        return (!message.isRendered() || component.isRedisplay())
                && (severityLevel == null || severityLevel.contains(severityName));
    }

    protected String getSeverityName(FacesMessage message) {
        int ordinal = message.getSeverity().getOrdinal();
        String severity = null;

        if (ordinal == FacesMessage.SEVERITY_INFO.getOrdinal()) {
            severity = "info";
        }
        else if (ordinal == FacesMessage.SEVERITY_ERROR.getOrdinal()) {
            severity = "error";
        }
        else if (ordinal == FacesMessage.SEVERITY_WARN.getOrdinal()) {
            severity = "warn";
        }
        else if (ordinal == FacesMessage.SEVERITY_FATAL.getOrdinal()) {
            severity = "fatal";
        }

        return severity;
    }

    protected String getClientSideSeverity(String severity) {
        if (severity == null) {
            return "all,error"; // validation.js checks if severity contains error.
        }
        return severity;
    }

    public List<FacesMessage> collectFacesMessages(UINotifications uiMessages, FacesContext context) {
        List<FacesMessage> messages = null;

        String _for = uiMessages.getFor();
        if (!isValueBlank(_for)) {
            String forType = uiMessages.getForType();

            // key case
            if (forType == null || "key".equals(forType)) {
                String[] keys = context.getApplication().getSearchExpressionHandler().splitExpressions(context, _for);
                for (String key : keys) {
                    Iterator<FacesMessage> messagesIterator = context.getMessages(key);
                    while (messagesIterator.hasNext()) {
                        if (messages == null) {
                            messages = new ArrayList<>(5);
                        }
                        messages.add(messagesIterator.next());
                    }
                }
            }

            // clientId / SearchExpression case
            if (forType == null || "expression".equals(forType)) {
                List<UIComponent> forComponents = SearchExpressionUtils.contextlessResolveComponents(context, uiMessages, _for,
                        SearchExpressionUtils.HINTS_IGNORE_NO_RESULT);
                for (int i = 0; i < forComponents.size(); i++) {
                    UIComponent forComponent = forComponents.get(i);
                    String forComponentClientId = forComponent.getClientId(context);
                    if (!_for.equals(forComponentClientId)) {

                        Iterator<FacesMessage> messagesIterator = context.getMessages(forComponentClientId);
                        while (messagesIterator.hasNext()) {
                            FacesMessage next = messagesIterator.next();
                            if (messages == null) {
                                messages = new ArrayList<>(5);
                            }
                            if (!messages.contains(next)) {
                                messages.add(next);
                            }
                        }
                    }
                }
            }
        }
        else if (uiMessages.isGlobalOnly()) {
            Iterator<FacesMessage> messagesIterator = context.getMessages(null);
            while (messagesIterator.hasNext()) {
                if (messages == null) {
                    messages = new ArrayList<>(5);
                }
                messages.add(messagesIterator.next());
            }
        }
        else {
            String[] ignores = uiMessages.getForIgnores() == null
                    ? null
                    : context.getApplication().getSearchExpressionHandler().splitExpressions(context, uiMessages.getForIgnores());
            Iterator<String> keyIterator = context.getClientIdsWithMessages();
            while (keyIterator.hasNext()) {
                String key = keyIterator.next();
                if (ignores == null || !LangUtils.contains(ignores, key)) {
                    Iterator<FacesMessage> messagesIterator = context.getMessages(key);
                    while (messagesIterator.hasNext()) {
                        if (messages == null) {
                            messages = new ArrayList<>(5);
                        }
                        messages.add(messagesIterator.next());
                    }
                }
            }
        }

        return messages;
    }
}
