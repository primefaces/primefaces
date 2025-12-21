/*
 * The MIT License
 *
 * Copyright (c) 2009-2025 PrimeTek Informatics
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
package org.primefaces.component.tooltip;

import org.primefaces.cdk.api.FacesComponentBase;
import org.primefaces.cdk.api.Property;
import org.primefaces.component.api.Widget;

import jakarta.faces.component.UIOutput;

@FacesComponentBase
public abstract class TooltipBase extends UIOutput implements Widget {

    public static final String COMPONENT_FAMILY = "org.primefaces.component";

    public static final String DEFAULT_RENDERER = "org.primefaces.component.TooltipRenderer";

    public TooltipBase() {
        setRendererType(DEFAULT_RENDERER);
    }

    @Override
    public String getFamily() {
        return COMPONENT_FAMILY;
    }

    @Property(description = "Event displaying the tooltip.", implicitDefaultValue = "mouseover")
    public abstract String getShowEvent();

    @Property(description = "Effect to be used for displaying.", defaultValue = "fade")
    public abstract String getShowEffect();

    @Property(description = "Delay time to show tooltip in milliseconds.", defaultValue = "150")
    public abstract int getShowDelay();

    @Property(description = "Event hiding the tooltip.", implicitDefaultValue = "mouseout")
    public abstract String getHideEvent();

    @Property(description = "Effect to be used for hiding", defaultValue = "fade")
    public abstract String getHideEffect();

    @Property(description = "Delay time to hide tooltip in milliseconds.", defaultValue = "0")
    public abstract int getHideDelay();

    @Property(description = "Id of the component to attach the tooltip.")
    public abstract String getFor();

    @Property(description = "Inline style of the tooltip.")
    public abstract String getStyle();

    @Property(description = "Style class of the tooltip.")
    public abstract String getStyleClass();

    @Property(description = "jquery selector for global tooltip.", implicitDefaultValue = "a,:input,:button")
    public abstract String getGlobalSelector();

    @Property(description = "Defines whether html would be escaped or not.]", defaultValue = "true")
    public abstract boolean isEscape();

    @Property(description = "Tooltip position follows pointer on mousemover.", defaultValue = "false")
    public abstract boolean isTrackMouse();

    @Property(description = "Client side callback to execute before tooltip is shown. Returning false will prevent display.")
    public abstract String getBeforeShow();

    @Property(description = "Client side callback to execute after tooltip is hidden.")
    public abstract String getOnHide();

    @Property(description = "Client side callback to execute after tooltip is shown.")
    public abstract String getOnShow();

    @Property(description = "Position of the tooltip, valid values are right, left, top and bottom.", defaultValue = "right")
    public abstract String getPosition();

    @Property(description = "When enabled, event delegation is used for better performance.", defaultValue = "false")
    public abstract boolean isDelegate();

    @Property(description = "Defines which position on the element being positioned to align with the target element: \"horizontal vertical\" alignment. "
            + "A single value such as \"right\" will be normalized to \"right center\", \"top\" will be normalized to \"center top\" "
            + "(following CSS convention). "
            + "Acceptable horizontal values: \"left\", \"center\", \"right\". Acceptable vertical values: \"top\", \"center\", \"bottom\". "
            + "Example: \"left top\" or \"center center\". "
            + "Each dimension can also contain offsets, in pixels or percent, e.g., \"right+10 top-25%\". "
            + "Percentage offsets are relative to the element being positioned. If set overrides the 'position' attribute. Example \"left center\".")
    public abstract String getMy();

    @Property(description = "Defines which position on the target element to align the positioned element against: \"horizontal vertical\" alignment. "
            + "See the my option for full details on possible values. Percentage offsets are relative to the target element. "
            + "If set overrides the 'position' attribute. Example \"right center\".")
    public abstract String getAt();

    @Property(description = "Whether to hide tooltip when hovering over tooltip content.", defaultValue = "true")
    public abstract boolean isAutoHide();
}