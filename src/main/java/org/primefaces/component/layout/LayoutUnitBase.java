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
package org.primefaces.component.layout;

import javax.faces.component.UIComponentBase;


public abstract class LayoutUnitBase extends UIComponentBase {

    public static final String COMPONENT_FAMILY = "org.primefaces.component";

    public static final String DEFAULT_RENDERER = "org.primefaces.component.LayoutUnitRenderer";

    public enum PropertyKeys {

        position,
        size,
        resizable,
        closable,
        collapsible,
        header,
        footer,
        minSize,
        maxSize,
        gutter,
        visible,
        collapsed,
        collapseSize,
        style,
        styleClass,
        effect,
        effectSpeed
    }

    public LayoutUnitBase() {
        setRendererType(DEFAULT_RENDERER);
    }

    @Override
    public String getFamily() {
        return COMPONENT_FAMILY;
    }

    public String getPosition() {
        return (String) getStateHelper().eval(PropertyKeys.position, null);
    }

    public void setPosition(String position) {
        getStateHelper().put(PropertyKeys.position, position);
    }

    public String getSize() {
        return (String) getStateHelper().eval(PropertyKeys.size, "auto");
    }

    public void setSize(String size) {
        getStateHelper().put(PropertyKeys.size, size);
    }

    public boolean isResizable() {
        return (Boolean) getStateHelper().eval(PropertyKeys.resizable, false);
    }

    public void setResizable(boolean resizable) {
        getStateHelper().put(PropertyKeys.resizable, resizable);
    }

    public boolean isClosable() {
        return (Boolean) getStateHelper().eval(PropertyKeys.closable, false);
    }

    public void setClosable(boolean closable) {
        getStateHelper().put(PropertyKeys.closable, closable);
    }

    public boolean isCollapsible() {
        return (Boolean) getStateHelper().eval(PropertyKeys.collapsible, false);
    }

    public void setCollapsible(boolean collapsible) {
        getStateHelper().put(PropertyKeys.collapsible, collapsible);
    }

    public String getHeader() {
        return (String) getStateHelper().eval(PropertyKeys.header, null);
    }

    public void setHeader(String header) {
        getStateHelper().put(PropertyKeys.header, header);
    }

    public String getFooter() {
        return (String) getStateHelper().eval(PropertyKeys.footer, null);
    }

    public void setFooter(String footer) {
        getStateHelper().put(PropertyKeys.footer, footer);
    }

    public int getMinSize() {
        return (Integer) getStateHelper().eval(PropertyKeys.minSize, 50);
    }

    public void setMinSize(int minSize) {
        getStateHelper().put(PropertyKeys.minSize, minSize);
    }

    public int getMaxSize() {
        return (Integer) getStateHelper().eval(PropertyKeys.maxSize, 0);
    }

    public void setMaxSize(int maxSize) {
        getStateHelper().put(PropertyKeys.maxSize, maxSize);
    }

    public int getGutter() {
        return (Integer) getStateHelper().eval(PropertyKeys.gutter, 6);
    }

    public void setGutter(int gutter) {
        getStateHelper().put(PropertyKeys.gutter, gutter);
    }

    public boolean isVisible() {
        return (Boolean) getStateHelper().eval(PropertyKeys.visible, true);
    }

    public void setVisible(boolean visible) {
        getStateHelper().put(PropertyKeys.visible, visible);
    }

    public boolean isCollapsed() {
        return (Boolean) getStateHelper().eval(PropertyKeys.collapsed, false);
    }

    public void setCollapsed(boolean collapsed) {
        getStateHelper().put(PropertyKeys.collapsed, collapsed);
    }

    public int getCollapseSize() {
        return (Integer) getStateHelper().eval(PropertyKeys.collapseSize, 25);
    }

    public void setCollapseSize(int collapseSize) {
        getStateHelper().put(PropertyKeys.collapseSize, collapseSize);
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

    public String getEffect() {
        return (String) getStateHelper().eval(PropertyKeys.effect, null);
    }

    public void setEffect(String effect) {
        getStateHelper().put(PropertyKeys.effect, effect);
    }

    public String getEffectSpeed() {
        return (String) getStateHelper().eval(PropertyKeys.effectSpeed, null);
    }

    public void setEffectSpeed(String effectSpeed) {
        getStateHelper().put(PropertyKeys.effectSpeed, effectSpeed);
    }

}