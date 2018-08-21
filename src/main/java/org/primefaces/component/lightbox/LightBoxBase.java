/**
 * Copyright 2009-2018 PrimeTek.
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
package org.primefaces.component.lightbox;

import javax.faces.component.UIComponentBase;

import org.primefaces.component.api.Widget;
import org.primefaces.util.ComponentUtils;


abstract class LightBoxBase extends UIComponentBase implements Widget {

    public static final String COMPONENT_FAMILY = "org.primefaces.component";

    public static final String DEFAULT_RENDERER = "org.primefaces.component.LightBoxRenderer";

    public enum PropertyKeys {

        widgetVar,
        style,
        styleClass,
        width,
        height,
        iframe,
        iframeTitle,
        visible,
        onShow,
        onHide
    }

    public LightBoxBase() {
        setRendererType(DEFAULT_RENDERER);
    }

    @Override
    public String getFamily() {
        return COMPONENT_FAMILY;
    }

    public String getWidgetVar() {
        return (String) getStateHelper().eval(PropertyKeys.widgetVar, null);
    }

    public void setWidgetVar(String widgetVar) {
        getStateHelper().put(PropertyKeys.widgetVar, widgetVar);
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

    public String getWidth() {
        return (String) getStateHelper().eval(PropertyKeys.width, null);
    }

    public void setWidth(String width) {
        getStateHelper().put(PropertyKeys.width, width);
    }

    public String getHeight() {
        return (String) getStateHelper().eval(PropertyKeys.height, null);
    }

    public void setHeight(String height) {
        getStateHelper().put(PropertyKeys.height, height);
    }

    public boolean isIframe() {
        return (Boolean) getStateHelper().eval(PropertyKeys.iframe, false);
    }

    public void setIframe(boolean iframe) {
        getStateHelper().put(PropertyKeys.iframe, iframe);
    }

    public String getIframeTitle() {
        return (String) getStateHelper().eval(PropertyKeys.iframeTitle, null);
    }

    public void setIframeTitle(String iframeTitle) {
        getStateHelper().put(PropertyKeys.iframeTitle, iframeTitle);
    }

    public boolean isVisible() {
        return (Boolean) getStateHelper().eval(PropertyKeys.visible, false);
    }

    public void setVisible(boolean visible) {
        getStateHelper().put(PropertyKeys.visible, visible);
    }

    public String getOnShow() {
        return (String) getStateHelper().eval(PropertyKeys.onShow, null);
    }

    public void setOnShow(String onShow) {
        getStateHelper().put(PropertyKeys.onShow, onShow);
    }

    public String getOnHide() {
        return (String) getStateHelper().eval(PropertyKeys.onHide, null);
    }

    public void setOnHide(String onHide) {
        getStateHelper().put(PropertyKeys.onHide, onHide);
    }

    @Override
    public String resolveWidgetVar() {
        return ComponentUtils.resolveWidgetVar(getFacesContext(), this);
    }
}