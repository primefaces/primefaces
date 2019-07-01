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

public class DefaultMenuColumn implements MenuColumn, Serializable {

    private static final long serialVersionUID = 1L;

    private String id;
    private String style;
    private String styleClass;
    private List<MenuElement> elements;
    private boolean rendered = true;

    public DefaultMenuColumn() {
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
    public boolean isRendered() {
        return rendered;
    }

    public void setRendered(boolean rendered) {
        this.rendered = rendered;
    }

    @Override
    public List<MenuElement> getElements() {
        return elements;
    }

    public void setElements(List<MenuElement> elements) {
        this.elements = elements;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {

        private DefaultMenuColumn column;

        private Builder() {
            column = new DefaultMenuColumn();
        }

        public Builder id(String id) {
            column.setId(id);
            return this;
        }

        public Builder style(String style) {
            column.setStyle(style);
            return this;
        }

        public Builder styleClass(String styleClass) {
            column.setStyleClass(styleClass);
            return this;
        }

        public Builder elements(List<MenuElement> elements) {
            column.setElements(elements);
            return this;
        }

        public Builder addElement(MenuElement element) {
            column.getElements().add(element);
            return this;
        }

        public Builder rendered(boolean rendered) {
            column.setRendered(rendered);
            return this;
        }

        public DefaultMenuColumn build() {
            return column;
        }
    }
}
