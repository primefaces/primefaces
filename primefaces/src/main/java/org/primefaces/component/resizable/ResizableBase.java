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
package org.primefaces.component.resizable;

import org.primefaces.cdk.api.FacesBehaviorEvent;
import org.primefaces.cdk.api.FacesBehaviorEvents;
import org.primefaces.cdk.api.FacesComponentBase;
import org.primefaces.cdk.api.Property;
import org.primefaces.component.api.Widget;
import org.primefaces.event.ResizeEvent;

import jakarta.faces.component.UIComponentBase;

@FacesComponentBase
@FacesBehaviorEvents({
    @FacesBehaviorEvent(name = "resize", event = ResizeEvent.class, description = "Fires when resizing is done.",
            defaultEvent = true)
})
public abstract class ResizableBase extends UIComponentBase implements Widget {

    public static final String COMPONENT_FAMILY = "org.primefaces.component";

    public static final String DEFAULT_RENDERER = "org.primefaces.component.ResizableRenderer";

    public ResizableBase() {
        setRendererType(DEFAULT_RENDERER);
    }

    @Override
    public String getFamily() {
        return COMPONENT_FAMILY;
    }

    @Property(description = "Identifier of the target component to make resizable.")
    public abstract String getFor();

    @Property(description = "Defines if aspectRatio should be kept or not.",
            defaultValue = "false")
    public abstract boolean isAspectRatio();

    @Property(description = "Displays proxy element instead of actual element.",
            defaultValue = "false")
    public abstract boolean isProxy();

    @Property(description = "Specifies the resize handles.")
    public abstract String getHandles();

    @Property(description = "In ghost mode, resize helper is displayed as the original element with less opacity.",
            defaultValue = "false")
    public abstract boolean isGhost();

    @Property(description = "Enables animation.",
            defaultValue = "false")
    public abstract boolean isAnimate();

    @Property(description = "Effect to use in animation. Default is swing.",
            defaultValue = "swing")
    public abstract String getEffect();

    @Property(description = "Effect duration of animation. Default is normal.",
            defaultValue = "normal")
    public abstract String getEffectDuration();

    @Property(description = "Maximum width boundary in pixels. Default is max integer value.",
            defaultValue = "Integer.MAX_VALUE")
    public abstract int getMaxWidth();

    @Property(description = "Maximum height boundary in pixels. Default is max integer value.",
            defaultValue = "Integer.MAX_VALUE")
    public abstract int getMaxHeight();

    @Property(description = "Minimum width boundary in pixels. Default is min integer value.",
            defaultValue = "Integer.MIN_VALUE")
    public abstract int getMinWidth();

    @Property(description = "Maximum height boundary in pixels. Default is min integer value.",
            defaultValue = "Integer.MIN_VALUE")
    public abstract int getMinHeight();

    @Property(description = "Sets resizable boundaries as the parents size.",
            defaultValue = "false")
    public abstract boolean isContainment();

    @Property(description = "Snaps resizing to grid structure.",
            defaultValue = "1")
    public abstract int getGrid();

    @Property(description = "Client side callback to execute when resizing begins.")
    public abstract String getOnStart();

    @Property(description = "Client side callback to execute during resizing.")
    public abstract String getOnResize();

    @Property(description = "Client side callback to execute after resizing end.")
    public abstract String getOnStop();
}