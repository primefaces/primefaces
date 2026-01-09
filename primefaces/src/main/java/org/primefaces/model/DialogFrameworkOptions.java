/*
 * The MIT License
 *
 * Copyright (c) 2009-2026 PrimeTek Informatics
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

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class DialogFrameworkOptions implements Serializable {

    private static final long serialVersionUID = 8394142502801415235L;

    private String widgetVar;
    private boolean modal = false;
    private boolean resizable = true;
    private boolean draggable = true;
    private String width;
    private String height;
    private String contentWidth;
    private String contentHeight;
    private boolean closable = true;
    private boolean includeViewParams = false;
    private String headerElement;
    private boolean minimizable = false;
    private boolean maximizable = false;
    private boolean closeOnEscape = false;
    private Integer minWidth;
    private Integer minHeight;
    private String appendTo;
    private boolean dynamic = false;
    private String showEffect;
    private String hideEffect;
    private String position;
    private boolean fitViewport = false;
    private boolean responsive = false;
    private String focus;
    private String onShow;
    private String onHide;
    private boolean blockScroll = false;
    private String styleClass;
    private String iframeStyleClass;
    private boolean resizeObserver = false;
    private boolean resizeObserverCenter = false;

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

    public boolean isClosable() {
        return closable;
    }

    public void setClosable(boolean closable) {
        this.closable = closable;
    }

    public boolean isIncludeViewParams() {
        return includeViewParams;
    }

    public void setIncludeViewParams(boolean includeViewParams) {
        this.includeViewParams = includeViewParams;
    }

    public String getHeaderElement() {
        return headerElement;
    }

    public void setHeaderElement(String headerElement) {
        this.headerElement = headerElement;
    }

    public boolean isMinimizable() {
        return minimizable;
    }

    public void setMinimizable(boolean minimizable) {
        this.minimizable = minimizable;
    }

    public boolean isMaximizable() {
        return maximizable;
    }

    public void setMaximizable(boolean maximizable) {
        this.maximizable = maximizable;
    }

    public boolean isCloseOnEscape() {
        return closeOnEscape;
    }

    public void setCloseOnEscape(boolean closeOnEscape) {
        this.closeOnEscape = closeOnEscape;
    }

    public Integer getMinWidth() {
        return minWidth;
    }

    public void setMinWidth(Integer minWidth) {
        this.minWidth = minWidth;
    }

    public Integer getMinHeight() {
        return minHeight;
    }

    public void setMinHeight(Integer minHeight) {
        this.minHeight = minHeight;
    }

    public String getAppendTo() {
        return appendTo;
    }

    public void setAppendTo(String appendTo) {
        this.appendTo = appendTo;
    }

    public boolean isDynamic() {
        return dynamic;
    }

    public void setDynamic(boolean dynamic) {
        this.dynamic = dynamic;
    }

    public String getShowEffect() {
        return showEffect;
    }

    public void setShowEffect(String showEffect) {
        this.showEffect = showEffect;
    }

    public String getHideEffect() {
        return hideEffect;
    }

    public void setHideEffect(String hideEffect) {
        this.hideEffect = hideEffect;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public boolean isFitViewport() {
        return fitViewport;
    }

    public void setFitViewport(boolean fitViewport) {
        this.fitViewport = fitViewport;
    }

    public boolean isResponsive() {
        return responsive;
    }

    public void setResponsive(boolean responsive) {
        this.responsive = responsive;
    }

    public String getFocus() {
        return focus;
    }

    public void setFocus(String focus) {
        this.focus = focus;
    }

    public String getOnShow() {
        return onShow;
    }

    public void setOnShow(String onShow) {
        this.onShow = onShow;
    }

    public String getOnHide() {
        return onHide;
    }

    public void setOnHide(String onHide) {
        this.onHide = onHide;
    }

    public boolean isBlockScroll() {
        return blockScroll;
    }

    public void setBlockScroll(boolean blockScroll) {
        this.blockScroll = blockScroll;
    }

    public String getStyleClass() {
        return styleClass;
    }

    public void setStyleClass(String styleClass) {
        this.styleClass = styleClass;
    }

    public String getIframeStyleClass() {
        return iframeStyleClass;
    }

    public void setIframeStyleClass(String iframeStyleClass) {
        this.iframeStyleClass = iframeStyleClass;
    }

    public boolean isResizeObserver() {
        return resizeObserver;
    }

    public void setResizeObserver(boolean resizeObserver) {
        this.resizeObserver = resizeObserver;
    }

    public boolean isResizeObserverCenter() {
        return resizeObserverCenter;
    }

    public void setResizeObserverCenter(boolean resizeObserverCenter) {
        this.resizeObserverCenter = resizeObserverCenter;
    }

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
        opts.put("closable", closable);
        opts.put("includeViewParams", includeViewParams);
        if (LangUtils.isNotBlank(headerElement)) {
            opts.put("headerElement", headerElement);
        }
        opts.put("minimizable", minimizable);
        opts.put("maximizable", maximizable);
        opts.put("closeOnEscape", closeOnEscape);
        if (minWidth != null) {
            opts.put("minWidth", minWidth);
        }
        if (minHeight != null) {
            opts.put("minHeight", minHeight);
        }
        if (LangUtils.isNotBlank(appendTo)) {
            opts.put("appendTo", appendTo);
        }
        opts.put("dynamic", dynamic);
        if (LangUtils.isNotBlank(showEffect)) {
            opts.put("showEffect", showEffect);
        }
        if (LangUtils.isNotBlank(hideEffect)) {
            opts.put("hideEffect", hideEffect);
        }
        if (LangUtils.isNotBlank(position)) {
            opts.put("position", position);
        }
        opts.put("fitViewport", fitViewport);
        opts.put("responsive", responsive);
        if (LangUtils.isNotBlank(focus)) {
            opts.put("focus", focus);
        }
        if (LangUtils.isNotBlank(onShow)) {
            opts.put("onShow", onShow);
        }
        if (LangUtils.isNotBlank(onHide)) {
            opts.put("onHide", onHide);
        }
        opts.put("blockScroll", blockScroll);
        if (LangUtils.isNotBlank(styleClass)) {
            opts.put("styleClass", styleClass);
        }
        if (LangUtils.isNotBlank(iframeStyleClass)) {
            opts.put("iframeStyleClass", iframeStyleClass);
        }
        opts.put("resizeObserver", resizeObserver);
        opts.put("resizeObserverCenter", resizeObserverCenter);

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

        /**
         * Whether the dialog can be closed or not.
         * @param closable
         * @return
         */
        public DialogFrameworkOptions.Builder closable(boolean closable) {
            options.setClosable(closable);
            return this;
        }

        /**
         * When enabled, includes the view parameters.
         * @param includeViewParams
         * @return
         */
        public DialogFrameworkOptions.Builder includeViewParams(boolean includeViewParams) {
            options.setIncludeViewParams(includeViewParams);
            return this;
        }

        /**
         * Client id of the element to display inside header.
         * @param headerElement
         * @return
         */
        public DialogFrameworkOptions.Builder headerElement(String headerElement) {
            options.setHeaderElement(headerElement);
            return this;
        }

        /**
         * Makes dialog minimizable.
         * @param minimizable
         * @return
         */
        public DialogFrameworkOptions.Builder minimizable(boolean minimizable) {
            options.setMinimizable(minimizable);
            return this;
        }

        public DialogFrameworkOptions.Builder maximizable(boolean maximizable) {
            options.setMaximizable(maximizable);
            return this;
        }

        /**
         * Whether the dialog can be closed with escape key.
         * @param closeOnEscape
         * @return
         */
        public DialogFrameworkOptions.Builder closeOnEscape(boolean closeOnEscape) {
            options.setCloseOnEscape(closeOnEscape);
            return this;
        }

        /**
         * Minimum width of a resizable dialog.
         * @param minWidth
         * @return
         */
        public DialogFrameworkOptions.Builder minWidth(Integer minWidth) {
            options.setMinWidth(minWidth);
            return this;
        }

        /**
         * Minimum height of a resizable dialog.
         * @param minHeight
         * @return
         */
        public DialogFrameworkOptions.Builder minHeight(Integer minHeight) {
            options.setMinHeight(minHeight);
            return this;
        }

        /**
         * Appends the dialog to the element defined by the given search expression.
         * @param appendTo
         * @return
         */
        public DialogFrameworkOptions.Builder appendTo(String appendTo) {
            options.setAppendTo(appendTo);
            return this;
        }

        /**
         * Enables lazy loading of the content with ajax.
         * @param dynamic
         * @return
         */
        public DialogFrameworkOptions.Builder dynamic(boolean dynamic) {
            options.setDynamic(dynamic);
            return this;
        }

        /**
         * Effect to use when showing the dialog
         * @param showEffect
         * @return
         */
        public DialogFrameworkOptions.Builder showEffect(String showEffect) {
            options.setShowEffect(showEffect);
            return this;
        }

        /**
         * Effect to use when hiding the dialog
         * @param hideEffect
         * @return
         */
        public DialogFrameworkOptions.Builder hideEffect(String hideEffect) {
            options.setHideEffect(hideEffect);
            return this;
        }

        /**
         * Defines where the dialog should be displayed
         * @param position
         * @return
         */
        public DialogFrameworkOptions.Builder position(String position) {
            options.setPosition(position);
            return this;
        }

        /**
         * Dialog size might exceed viewport if content is bigger than viewport in terms of height.
         * fitViewport option automatically adjusts height to fit dialog within the viewport.
         * @param fitViewport
         * @return
         */
        public DialogFrameworkOptions.Builder fitViewport(boolean fitViewport) {
            options.setFitViewport(fitViewport);
            return this;
        }

        /**
         * In responsive mode, dialog adjusts itself based on screen width.
         * @param responsive
         * @return
         */
        public DialogFrameworkOptions.Builder responsive(boolean responsive) {
            options.setResponsive(responsive);
            return  this;
        }

        /**
         * Defines which component to apply focus by search expression.
         * @param focus
         * @return
         */
        public DialogFrameworkOptions.Builder focus(String focus) {
            options.setFocus(focus);
            return this;
        }

        /**
         * Client side callback to execute when dialog is displayed.
         * @param onShow
         * @return
         */
        public DialogFrameworkOptions.Builder onShow(String onShow) {
            options.setOnShow(onShow);
            return this;
        }

        /**
         * Client side callback to execute when dialog is hidden.
         * @param onHide
         * @return
         */
        public DialogFrameworkOptions.Builder onHide(String onHide) {
            options.setOnHide(onHide);
            return this;
        }

        /**
         * Whether to block scrolling of the document when dialog is modal.
         * @param blockScroll
         * @return
         */
        public DialogFrameworkOptions.Builder blockScroll(boolean blockScroll) {
            options.setBlockScroll(blockScroll);
            return this;
        }

        /**
         * One or more CSS classes for the dialog.
         * @param styleClass
         * @return
         */
        public DialogFrameworkOptions.Builder styleClass(String styleClass) {
            options.setStyleClass(styleClass);
            return this;
        }

        /**
         * One or more CSS classes for the iframe within the dialog.
         * @param iframeStyleClass
         * @return
         */
        public DialogFrameworkOptions.Builder iframeStyleClass(String iframeStyleClass) {
            options.setIframeStyleClass(iframeStyleClass);
            return this;
        }

        /**
         * Use ResizeObserver to automatically adjust dialog-height after e.g. AJAX-updates.
         * Resizeable must be set false to use this option.
         * @param resizeObserver
         * @return
         */
        public DialogFrameworkOptions.Builder resizeObserver(boolean resizeObserver) {
            options.setResizeObserver(resizeObserver);
            return this;
        }

        /**
         * Can be used together with resizeObserver = true.
         * Centers the dialog again after it was resized to ensure the whole dialog is visible onscreen.
         * @param resizeObserverCenter
         * @return
         */
        public DialogFrameworkOptions.Builder resizeObserverCenter(boolean resizeObserverCenter) {
            options.setResizeObserverCenter(resizeObserverCenter);
            return this;
        }

        public DialogFrameworkOptions build() {
            return options;
        }
    }

}
