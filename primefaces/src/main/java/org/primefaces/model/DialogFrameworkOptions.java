/*
 * The MIT License
 *
 * Copyright (c) 2009-2022 PrimeTek
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
package org.primefaces.model;

import org.primefaces.util.LangUtils;

import java.util.HashMap;
import java.util.Map;

public class DialogFrameworkOptions {

    private String widgetVar;
    private boolean modal = false;
    private boolean resizable = true;
    private boolean draggable = true;
    private String width;
    private String height;
    private String contentWidth;
    private String contentHeight;

    public String getWidgetVar() {
        return widgetVar;
    }

    public void setWidgetVar(String widgetVar) {
        this.widgetVar = widgetVar;
    }

    public boolean isModal() {
        return modal;
    }

    public void setModal(boolean modal) {
        this.modal = modal;
    }

    public boolean isResizable() {
        return resizable;
    }

    public void setResizable(boolean resizable) {
        this.resizable = resizable;
    }

    public boolean isDraggable() {
        return draggable;
    }

    public void setDraggable(boolean draggable) {
        this.draggable = draggable;
    }

    public String getWidth() {
        return width;
    }

    public void setWidth(String width) {
        this.width = width;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public String getContentWidth() {
        return contentWidth;
    }

    public void setContentWidth(String contentWidth) {
        this.contentWidth = contentWidth;
    }

    public String getContentHeight() {
        return contentHeight;
    }

    public void setContentHeight(String contentHeight) {
        this.contentHeight = contentHeight;
    }

    // TODO: https://primefaces.github.io/primefaces/12_0_0/#/core/dialogframework

    public Map<String, Object> toMap() {
        Map<String, Object> opts = new HashMap<>();

        if (LangUtils.isNotBlank(widgetVar)) {
            opts.put("widgetVar", widgetVar);
        }
        opts.put("modal", modal);
        opts.put("resizable", resizable);
        opts.put("draggable", draggable);
        if (LangUtils.isNotBlank(width)) {
            opts.put("width", width);
        }
        if (LangUtils.isNotBlank(height)) {
            opts.put("height", height);
        }
        if (LangUtils.isNotBlank(contentWidth)) {
            opts.put("contentWidth", contentWidth);
        }
        if (LangUtils.isNotBlank(contentHeight)) {
            opts.put("contentHeight", contentHeight);
        }

        // TODO: https://primefaces.github.io/primefaces/12_0_0/#/core/dialogframework

        return opts;
    }

    public static DialogFrameworkOptions.Builder builder() {
        return new DialogFrameworkOptions.Builder();
    }

    public static final class Builder {

        private final DialogFrameworkOptions options;

        private Builder() {
            options = new DialogFrameworkOptions();
        }

        /**
         * Custom widgetVar of the dialog, if not declared it will be automatically created as "id+dlgWidget".
         * @param widgetVar
         * @return
         */
        public DialogFrameworkOptions.Builder widgetVar(String widgetVar) {
            options.setWidgetVar(widgetVar);
            return this;
        }

        /**
         * Controls modality of the dialog.
         * @param modal
         * @return
         */
        public DialogFrameworkOptions.Builder modal(boolean modal) {
            options.setModal(modal);
            return this;
        }

        /**
         * When enabled, makes dialog resizable.
         * @param resizable
         * @return
         */
        public DialogFrameworkOptions.Builder resizable(boolean resizable) {
            options.setResizable(resizable);
            return this;
        }

        /**
         * When enabled, makes dialog draggable.
         * @param draggable
         * @return
         */
        public DialogFrameworkOptions.Builder draggable(boolean draggable) {
            options.setDraggable(draggable);
            return this;
        }

        /**
         * Width of the dialog.
         * @param width
         * @return
         */
        public DialogFrameworkOptions.Builder width(String width) {
            options.setWidth(width);
            return this;
        }

        /**
         * Height of the dialog.
         * @param height
         * @return
         */
        public DialogFrameworkOptions.Builder height(String height) {
            options.setHeight(height);
            return this;
        }

        /**
         * Width of the dialog content. NOTE: 'auto' cannot be used because the dialog is displayed in an IFrame.
         * @param contentWidth
         * @return
         */
        public DialogFrameworkOptions.Builder contentWidth(String contentWidth) {
            options.setContentWidth(contentWidth);
            return this;
        }

        /**
         * Height of the dialog content.
         * @param contentHeight
         * @return
         */
        public DialogFrameworkOptions.Builder contentHeight(String contentHeight) {
            options.setContentHeight(contentHeight);
            return this;
        }

        // TODO: https://primefaces.github.io/primefaces/12_0_0/#/core/dialogframework

        public DialogFrameworkOptions build() {
            return options;
        }
    }

}
