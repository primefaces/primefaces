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
package org.primefaces.component.texteditor;

import java.util.List;
import javax.faces.component.UIInput;
import javax.faces.component.behavior.ClientBehaviorHolder;

import org.primefaces.component.api.Widget;

public abstract class TextEditorBase extends UIInput implements Widget, ClientBehaviorHolder {

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
        allowImages,
        formats
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

    public List getFormats() {
        return (List) getStateHelper().eval(PropertyKeys.formats, null);
    }

    public void setFormats(List formats) {
        getStateHelper().put(PropertyKeys.formats, formats);
    }
}