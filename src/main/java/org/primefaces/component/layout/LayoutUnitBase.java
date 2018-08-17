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
package org.primefaces.component.layout;

import javax.faces.component.UIComponentBase;


abstract class LayoutUnitBase extends UIComponentBase {

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

    public java.lang.String getPosition() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.position, null);
    }

    public void setPosition(java.lang.String _position) {
        getStateHelper().put(PropertyKeys.position, _position);
    }

    public java.lang.String getSize() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.size, "auto");
    }

    public void setSize(java.lang.String _size) {
        getStateHelper().put(PropertyKeys.size, _size);
    }

    public boolean isResizable() {
        return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.resizable, false);
    }

    public void setResizable(boolean _resizable) {
        getStateHelper().put(PropertyKeys.resizable, _resizable);
    }

    public boolean isClosable() {
        return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.closable, false);
    }

    public void setClosable(boolean _closable) {
        getStateHelper().put(PropertyKeys.closable, _closable);
    }

    public boolean isCollapsible() {
        return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.collapsible, false);
    }

    public void setCollapsible(boolean _collapsible) {
        getStateHelper().put(PropertyKeys.collapsible, _collapsible);
    }

    public java.lang.String getHeader() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.header, null);
    }

    public void setHeader(java.lang.String _header) {
        getStateHelper().put(PropertyKeys.header, _header);
    }

    public java.lang.String getFooter() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.footer, null);
    }

    public void setFooter(java.lang.String _footer) {
        getStateHelper().put(PropertyKeys.footer, _footer);
    }

    public int getMinSize() {
        return (java.lang.Integer) getStateHelper().eval(PropertyKeys.minSize, 50);
    }

    public void setMinSize(int _minSize) {
        getStateHelper().put(PropertyKeys.minSize, _minSize);
    }

    public int getMaxSize() {
        return (java.lang.Integer) getStateHelper().eval(PropertyKeys.maxSize, 0);
    }

    public void setMaxSize(int _maxSize) {
        getStateHelper().put(PropertyKeys.maxSize, _maxSize);
    }

    public int getGutter() {
        return (java.lang.Integer) getStateHelper().eval(PropertyKeys.gutter, 6);
    }

    public void setGutter(int _gutter) {
        getStateHelper().put(PropertyKeys.gutter, _gutter);
    }

    public boolean isVisible() {
        return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.visible, true);
    }

    public void setVisible(boolean _visible) {
        getStateHelper().put(PropertyKeys.visible, _visible);
    }

    public boolean isCollapsed() {
        return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.collapsed, false);
    }

    public void setCollapsed(boolean _collapsed) {
        getStateHelper().put(PropertyKeys.collapsed, _collapsed);
    }

    public int getCollapseSize() {
        return (java.lang.Integer) getStateHelper().eval(PropertyKeys.collapseSize, 25);
    }

    public void setCollapseSize(int _collapseSize) {
        getStateHelper().put(PropertyKeys.collapseSize, _collapseSize);
    }

    public java.lang.String getStyle() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.style, null);
    }

    public void setStyle(java.lang.String _style) {
        getStateHelper().put(PropertyKeys.style, _style);
    }

    public java.lang.String getStyleClass() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.styleClass, null);
    }

    public void setStyleClass(java.lang.String _styleClass) {
        getStateHelper().put(PropertyKeys.styleClass, _styleClass);
    }

    public java.lang.String getEffect() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.effect, null);
    }

    public void setEffect(java.lang.String _effect) {
        getStateHelper().put(PropertyKeys.effect, _effect);
    }

    public java.lang.String getEffectSpeed() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.effectSpeed, null);
    }

    public void setEffectSpeed(java.lang.String _effectSpeed) {
        getStateHelper().put(PropertyKeys.effectSpeed, _effectSpeed);
    }

}