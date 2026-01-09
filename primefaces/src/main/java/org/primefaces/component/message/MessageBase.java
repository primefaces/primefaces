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
package org.primefaces.component.message;

import org.primefaces.cdk.api.FacesComponentBase;
import org.primefaces.cdk.api.Property;
import org.primefaces.component.api.StyleAware;
import org.primefaces.component.api.UINotification;
import org.primefaces.component.api.Widget;

import jakarta.faces.component.UIMessage;

@FacesComponentBase
public abstract class MessageBase extends UIMessage implements UINotification, Widget, StyleAware {

    public static final String COMPONENT_FAMILY = "org.primefaces.component";

    public static final String DEFAULT_RENDERER = "org.primefaces.component.MessageRenderer";

    public MessageBase() {
        setRendererType(DEFAULT_RENDERER);
    }

    @Override
    public String getFamily() {
        return COMPONENT_FAMILY;
    }

    @Property(defaultValue = "both", description = "Defines display mode. Valid values are \"text\", \"icon\" and \"both\".")
    public abstract String getDisplay();

    @Property(defaultValue = "true", description = "Defines whether html would be escaped or not.")
    public abstract boolean isEscape();

    @Property(description = "Type of the \"for\" attribute. Valid values are \"key\" and \"expression\".")
    public abstract String getForType();

    @Property(description = "Defines a list of keys and clientIds, which should NOT be rendered by this component. Separated by space or comma.")
    public abstract String getForIgnores();

    @Property(defaultValue = "true", description = "Specifies if the summary of the FacesMessage should be displayed.")
    @Override
    public abstract boolean isShowSummary();

    @Property(defaultValue = "true", description = "Specifies if the detail of the FacesMessage should be displayed.")
    @Override
    public abstract boolean isShowDetail();

    @Property(description = "Identifier of the component whose messages to display only, takes precendence when used with globalOnly.")
    @Override
    public abstract String getFor();
}