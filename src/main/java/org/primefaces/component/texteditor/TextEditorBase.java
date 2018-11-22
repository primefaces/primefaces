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
package org.primefaces.component.texteditor;

import javax.faces.component.UIInput;
import javax.faces.component.behavior.ClientBehaviorHolder;

import org.primefaces.component.api.Widget;
import org.primefaces.util.ComponentUtils;


abstract class TextEditorBase extends UIInput implements Widget, ClientBehaviorHolder {

    public static final String COMPONENT_FAMILY = "org.primefaces.component";

    public static final String DEFAULT_RENDERER = "org.primefaces.component.TextEditorRenderer";

    public enum PropertyKeys {

        widgetVar,
        height,
        readonly,
        style,
        styleClass,
        placeholder,
        toolbarVisible,
        allowBlocks,
        allowFormatting,
        allowLinks,
        allowStyles,
        allowImages
    }

    public TextEditorBase() {
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

    public int getHeight() {
        return (Integer) getStateHelper().eval(PropertyKeys.height, Integer.MIN_VALUE);
    }

    public void setHeight(int height) {
        getStateHelper().put(PropertyKeys.height, height);
    }

    public boolean isReadonly() {
        return (Boolean) getStateHelper().eval(PropertyKeys.readonly, false);
    }

    public void setReadonly(boolean readonly) {
        getStateHelper().put(PropertyKeys.readonly, readonly);
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

    public String getPlaceholder() {
        return (String) getStateHelper().eval(PropertyKeys.placeholder, null);
    }

    public void setPlaceholder(String placeholder) {
        getStateHelper().put(PropertyKeys.placeholder, placeholder);
    }

    public boolean isToolbarVisible() {
        return (Boolean) getStateHelper().eval(PropertyKeys.toolbarVisible, true);
    }

    public void setToolbarVisible(boolean toolbarVisible) {
        getStateHelper().put(PropertyKeys.toolbarVisible, toolbarVisible);
    }

    public boolean isAllowBlocks() {
        return (Boolean) getStateHelper().eval(PropertyKeys.allowBlocks, true);
    }

    public void setAllowBlocks(boolean allowBlocks) {
        getStateHelper().put(PropertyKeys.allowBlocks, allowBlocks);
    }

    public boolean isAllowFormatting() {
        return (Boolean) getStateHelper().eval(PropertyKeys.allowFormatting, true);
    }

    public void setAllowFormatting(boolean allowFormatting) {
        getStateHelper().put(PropertyKeys.allowFormatting, allowFormatting);
    }

    public boolean isAllowLinks() {
        return (Boolean) getStateHelper().eval(PropertyKeys.allowLinks, true);
    }

    public void setAllowLinks(boolean allowLinks) {
        getStateHelper().put(PropertyKeys.allowLinks, allowLinks);
    }

    public boolean isAllowStyles() {
        return (Boolean) getStateHelper().eval(PropertyKeys.allowStyles, true);
    }

    public void setAllowStyles(boolean allowStyles) {
        getStateHelper().put(PropertyKeys.allowStyles, allowStyles);
    }

    public boolean isAllowImages() {
        return (Boolean) getStateHelper().eval(PropertyKeys.allowImages, true);
    }

    public void setAllowImages(boolean allowImages) {
        getStateHelper().put(PropertyKeys.allowImages, allowImages);
    }

    @Override
    public String resolveWidgetVar() {
        return ComponentUtils.resolveWidgetVar(getFacesContext(), this);
    }
}