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
package org.primefaces.model.datepicker;

import java.io.Serializable;
import java.util.Objects;

public class DefaultDateMetadata implements DateMetadata, Serializable {

    private static final long serialVersionUID = 2L;

    private boolean disabled;

    private String styleClass;

    public DefaultDateMetadata() {
    }

    @Override
    public boolean isDisabled() {
        return disabled;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    @Override
    public String getStyleClass() {
        return styleClass;
    }

    public void setStyleClass(String styleClass) {
        this.styleClass = styleClass;
    }

    @Override
    public int hashCode() {
        return Objects.hash(disabled, styleClass);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof DefaultDateMetadata)) {
            return false;
        }
        DefaultDateMetadata other = (DefaultDateMetadata) obj;
        return disabled == other.disabled && Objects.equals(styleClass, other.styleClass);
    }

    @Override
    public String toString() {
        return "DefaultDateMetadata{" + "disabled=" + disabled + ", styleClass=" + styleClass + '}';
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {

        private final DefaultDateMetadata dateMetadata;

        private Builder() {
            dateMetadata = new DefaultDateMetadata();
        }

        public Builder disabled(boolean disabled) {
            dateMetadata.setDisabled(disabled);
            return this;
        }

        public Builder styleClass(String styleClass) {
            dateMetadata.setStyleClass(styleClass);
            return this;
        }

        public DefaultDateMetadata build() {
            return dateMetadata;
        }
    }
}
