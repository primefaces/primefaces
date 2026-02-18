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
package org.primefaces.component.commandlink;


import org.primefaces.cdk.api.FacesBehaviorEvent;
import org.primefaces.cdk.api.FacesBehaviorEvents;
import org.primefaces.cdk.api.FacesComponentBase;
import org.primefaces.cdk.api.Property;
import org.primefaces.component.api.AjaxSource;
import org.primefaces.component.api.Confirmable;
import org.primefaces.component.api.Widget;
import org.primefaces.event.SelectEvent;

import jakarta.el.MethodExpression;
import jakarta.faces.component.html.HtmlCommandLink;
import jakarta.faces.event.ActionListener;
import jakarta.faces.event.AjaxBehaviorEvent;

@FacesComponentBase
@FacesBehaviorEvents({
    @FacesBehaviorEvent(name = "click", event = AjaxBehaviorEvent.class, description = "Fires when the button is clicked.", defaultEvent = true),
    @FacesBehaviorEvent(name = "dialogReturn", event = SelectEvent.class, description = "Fired when a dialog is closed.")
})
public abstract class CommandLinkBase extends HtmlCommandLink implements AjaxSource, Widget, Confirmable {

    public static final String COMPONENT_FAMILY = "org.primefaces.component";

    public static final String DEFAULT_RENDERER = "org.primefaces.component.CommandLinkRenderer";

    public CommandLinkBase() {
        setRendererType(DEFAULT_RENDERER);
    }

    @Override
    public String getFamily() {
        return COMPONENT_FAMILY;
    }

    @Property(description = "A method expression or a string outcome to process when command is executed.", skipAccessors = true)
    public MethodExpression getAction() {
        return super.getActionExpression();
    }

    @Property(description = "An action listener to process when command is executed.", skipAccessors = true)
    public ActionListener getActionListener() {
        return super.getActionListeners()[0];
    }

    @Property(defaultValue = "true", description = "Specifies the submit mode, when set to true (default), submit would be made with Ajax.")
    public abstract boolean isAjax();

    @Property(description = "When set to true client side validation is enabled, global setting is required to be enabled as a prerequisite.")
    public abstract boolean isValidateClient();

    @Property(defaultValue = "The aria-label attribute is used to define a string that labels the current element for accessibility.")
    public abstract String getAriaLabel();

    @Property(defaultValue = "true", description = "If true, the button will be disabled during Ajax requests triggered by the button.")
    public abstract boolean isDisableOnAjax();
}