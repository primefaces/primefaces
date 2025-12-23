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
package org.primefaces.component.inputtextarea;

import org.primefaces.cdk.api.FacesBehaviorEvent;
import org.primefaces.cdk.api.FacesBehaviorEvents;
import org.primefaces.cdk.api.Property;
import org.primefaces.component.api.AbstractPrimeHtmlInputTextArea;
import org.primefaces.component.api.CountCharactersAware;
import org.primefaces.component.api.MixedClientBehaviorHolder;
import org.primefaces.component.api.Widget;
import org.primefaces.event.SelectEvent;

import jakarta.el.MethodExpression;
import jakarta.faces.event.AjaxBehaviorEvent;

@FacesBehaviorEvents({
    @FacesBehaviorEvent(name = "itemSelect", event = SelectEvent.class, description = "Fires when an item is selected."),
    @FacesBehaviorEvent(name = "query", event = AjaxBehaviorEvent.class, description = "Fires when a search query is triggered.")
})
public abstract class InputTextareaBase extends AbstractPrimeHtmlInputTextArea implements Widget, CountCharactersAware, MixedClientBehaviorHolder {

    public static final String COMPONENT_FAMILY = "org.primefaces.component";

    public static final String DEFAULT_RENDERER = "org.primefaces.component.InputTextareaRenderer";

    public InputTextareaBase() {
        setRendererType(DEFAULT_RENDERER);
    }

    @Override
    public String getFamily() {
        return COMPONENT_FAMILY;
    }

    @Property(description = "Adds a new line when Enter key is pressed.", defaultValue = "false")
    public abstract boolean isAddLine();

    @Property(description = "When enabled, textarea automatically resizes its height based on user input.", defaultValue = "true")
    public abstract boolean isAutoResize();

    @Property(description = "MethodExpression to provide suggestions for autocomplete functionality.")
    public abstract MethodExpression getCompleteMethod();

    @Property(description = "Minimum number of characters to trigger autocomplete query.", defaultValue = "3")
    public abstract int getMinQueryLength();

    @Property(description = "Delay in milliseconds before triggering autocomplete query.", defaultValue = "700")
    public abstract int getQueryDelay();

    @Property(description = "Maximum height in pixels before scrolling is enabled.", defaultValue = "2147483647")
    public abstract int getScrollHeight();
}
