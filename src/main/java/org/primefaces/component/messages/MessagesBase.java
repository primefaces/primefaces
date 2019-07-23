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
package org.primefaces.component.messages;

import javax.faces.component.UIMessages;

import org.primefaces.component.api.UINotification;


public abstract class MessagesBase extends UIMessages implements UINotification {

    public static final String COMPONENT_FAMILY = "org.primefaces.component";

    public static final String DEFAULT_RENDERER = "org.primefaces.component.MessagesRenderer";

    public enum PropertyKeys {

        escape,
        severity,
        closable,
        style,
        styleClass,
        showIcon,
        forType,
        forIgnores,
        skipDetailIfEqualsSummary
    }

    public MessagesBase() {
        setRendererType(DEFAULT_RENDERER);
    }

    @Override
    public String getFamily() {
        return COMPONENT_FAMILY;
    }

    public boolean isEscape() {
        return (Boolean) getStateHelper().eval(PropertyKeys.escape, true);
    }

    public void setEscape(boolean escape) {
        getStateHelper().put(PropertyKeys.escape, escape);
    }

    @Override
    public String getSeverity() {
        return (String) getStateHelper().eval(PropertyKeys.severity, null);
    }

    public void setSeverity(String severity) {
        getStateHelper().put(PropertyKeys.severity, severity);
    }

    public boolean isClosable() {
        return (Boolean) getStateHelper().eval(PropertyKeys.closable, false);
    }

    public void setClosable(boolean closable) {
        getStateHelper().put(PropertyKeys.closable, closable);
    }

    public String getStyle() {
        return (String) getStateHelper().eval(PropertyKeys.style, null);
    }

    public void setStyle(String style) {
        getStateHelper().put(PropertyKeys.style, style);
    }

    public String getStyleClass() {
        return (String) getStateHelper().eval(PropertyKeys.styleClass, null);
    }

    public void setStyleClass(String styleClass) {
        getStateHelper().put(PropertyKeys.styleClass, styleClass);
    }

    public boolean isShowIcon() {
        return (Boolean) getStateHelper().eval(PropertyKeys.showIcon, true);
    }

    public void setShowIcon(boolean showIcon) {
        getStateHelper().put(PropertyKeys.showIcon, showIcon);
    }

    public String getForType() {
        return (String) getStateHelper().eval(PropertyKeys.forType, null);
    }

    public void setForType(String forType) {
        getStateHelper().put(PropertyKeys.forType, forType);
    }

    public String getForIgnores() {
        return (String) getStateHelper().eval(PropertyKeys.forIgnores, null);
    }

    public void setForIgnores(String forIgnores) {
        getStateHelper().put(PropertyKeys.forIgnores, forIgnores);
    }

    @Override
    public boolean isSkipDetailIfEqualsSummary() {
        return (Boolean) getStateHelper().eval(PropertyKeys.skipDetailIfEqualsSummary, false);
    }

    public void setSkipDetailIfEqualsSummary(boolean skipDetailIfEqualsSummary) {
        getStateHelper().put(PropertyKeys.skipDetailIfEqualsSummary, skipDetailIfEqualsSummary);
    }
}