/*
 * The MIT License
 *
 * Copyright (c) 2009-2021 PrimeTek
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
package org.primefaces.model.badge;

import java.io.Serializable;

public class DefaultBadgeModel implements BadgeModel, Serializable {

    private static final long serialVersionUID = 1L;

    private String value;
    private String severity;
    private String size;
    private String style;
    private String styleClass;
    private boolean visible = true;

    @Override
    public String getValue() {
        return value;
    }

    @Override
    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String getSeverity() {
        return severity;
    }

    @Override
    public void setSeverity(String severity) {
        this.severity = severity;
    }

    @Override
    public String getSize() {
        return size;
    }

    @Override
    public void setSize(String size) {
        this.size = size;
    }

    @Override
    public String getStyle() {
        return style;
    }

    @Override
    public void setStyle(String style) {
        this.style = style;
    }

    @Override
    public String getStyleClass() {
        return styleClass;
    }

    @Override
    public void setStyleClass(String styleClass) {
        this.styleClass = styleClass;
    }

    @Override
    public boolean isVisible() {
        return visible;
    }

    @Override
    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {

        private final DefaultBadgeModel badgeModel;

        private Builder() {
            badgeModel = new DefaultBadgeModel();
        }

        public Builder value(String value) {
            badgeModel.setValue(value);
            return this;
        }

        public Builder severity(String severity) {
            badgeModel.setSeverity(severity);
            return this;
        }

        public Builder size(String size) {
            badgeModel.setSize(size);
            return this;
        }

        public Builder style(String style) {
            badgeModel.setStyle(style);
            return this;
        }

        public Builder styleClass(String styleClass) {
            badgeModel.setStyleClass(styleClass);
            return this;
        }

        public Builder visible(boolean visible) {
            badgeModel.setVisible(visible);
            return this;
        }

        public DefaultBadgeModel build() {
            return badgeModel;
        }
    }

}
