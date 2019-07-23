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
package org.primefaces.model.menu;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class DefaultSubMenu implements Submenu, Serializable {

    private static final long serialVersionUID = 1L;

    private String id;
    private String style;
    private String styleClass;
    private String icon;
    private String label;
    private boolean disabled;
    private List<MenuElement> elements;
    private boolean rendered = true;
    private boolean expanded = false;

    public DefaultSubMenu() {
        elements = new ArrayList<>();
    }

    public DefaultSubMenu(String label) {
        this.label = label;
        elements = new ArrayList<>();
    }

    public DefaultSubMenu(String label, String icon) {
        this.label = label;
        this.icon = icon;
        elements = new ArrayList<>();
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String getStyle() {
        return style;
    }

    public void setStyle(String style) {
        this.style = style;
    }

    @Override
    public String getStyleClass() {
        return styleClass;
    }

    public void setStyleClass(String styleClass) {
        this.styleClass = styleClass;
    }

    @Override
    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    @Override
    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    @Override
    public boolean isDisabled() {
        return disabled;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    @Override
    public List<MenuElement> getElements() {
        return elements;
    }

    public void setElements(List<MenuElement> elements) {
        this.elements = elements;
    }

    @Override
    public boolean isRendered() {
        return rendered;
    }

    public void setRendered(boolean rendered) {
        this.rendered = rendered;
    }

    @Override
    public boolean isExpanded() {
        return expanded;
    }

    public void setExpanded(boolean expanded) {
        this.expanded = expanded;
    }

    @Override
    public Object getParent() {
        return null;
    }

    @Override
    public String getClientId() {
        return id;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private DefaultSubMenu subMenu;

        private Builder() {
            subMenu = new DefaultSubMenu();
        }

        public Builder id(String id) {
            subMenu.setId(id);
            return this;
        }

        public Builder style(String style) {
            subMenu.setStyle(style);
            return this;
        }

        public Builder styleClass(String styleClass) {
            subMenu.setStyleClass(styleClass);
            return this;
        }

        public Builder icon(String icon) {
            subMenu.setIcon(icon);
            return this;
        }

        public Builder label(String label) {
            subMenu.setLabel(label);
            return this;
        }

        public Builder disabled(boolean disabled) {
            subMenu.setDisabled(disabled);
            return this;
        }

        public Builder elements(List<MenuElement> elements) {
            subMenu.setElements(elements);
            return this;
        }

        public Builder addElement(MenuElement element) {
            subMenu.getElements().add(element);
            return this;
        }

        public Builder rendered(boolean rendered) {
            subMenu.setRendered(rendered);
            return this;
        }

        public Builder expanded(boolean expanded) {
            subMenu.setExpanded(expanded);
            return this;
        }

        public DefaultSubMenu build() {
            return subMenu;
        }
    }
}
