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

public class DefaultSeparator implements Separator, Serializable {

    private static final long serialVersionUID = 1L;

    private String id;
    private String title;
    private String style;
    private String styleClass;
    private boolean rendered = true;

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {

        private DefaultSeparator separator;

        private Builder() {
            separator = new DefaultSeparator();
        }

        public Builder id(String id) {
            separator.setId(id);
            return this;
        }

        public Builder title(String title) {
            separator.setTitle(title);
            return this;
        }

        public Builder style(String style) {
            separator.setStyle(style);
            return this;
        }

        public Builder styleClass(String styleClass) {
            separator.setStyleClass(styleClass);
            return this;
        }

        public Builder rendered(boolean rendered) {
            separator.setRendered(rendered);
            return this;
        }

        public DefaultSeparator build() {
            return separator;
        }
    }
}
