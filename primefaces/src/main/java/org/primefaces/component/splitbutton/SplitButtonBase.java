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
package org.primefaces.component.splitbutton;

import org.primefaces.cdk.api.FacesComponentBase;
import org.primefaces.cdk.api.Property;
import org.primefaces.component.api.AjaxSource;
import org.primefaces.component.api.Confirmable;
import org.primefaces.component.api.MenuItemAware;
import org.primefaces.component.api.StyleAware;
import org.primefaces.component.api.Widget;
import org.primefaces.model.menu.MenuModel;

import jakarta.el.MethodExpression;
import jakarta.faces.component.html.HtmlCommandButton;
import jakarta.faces.event.ActionListener;

@FacesComponentBase
public abstract class SplitButtonBase extends HtmlCommandButton implements AjaxSource, Confirmable, Widget, MenuItemAware, StyleAware {

    public static final String COMPONENT_FAMILY = "org.primefaces.component";

    public static final String DEFAULT_RENDERER = "org.primefaces.component.SplitButtonRenderer";

    public SplitButtonBase() {
        setRendererType(DEFAULT_RENDERER);
    }

    @Override
    public String getFamily() {
        return COMPONENT_FAMILY;
    }

    @Property(description = "A method expression or a string outcome to process when command is executed.", callSuper = true)
    public MethodExpression getAction() {
        return super.getActionExpression();
    }

    @Property(description = "An action listener to process when command is executed.", callSuper = true)
    public ActionListener getActionListener() {
        return super.getActionListeners()[0];
    }

    @Property(defaultValue = "true", description = "Icon of the button.")
    public abstract boolean isAjax();

    @Property(description = "Icon of the button.")
    public abstract String getIcon();

    @Property(defaultValue = "left", description = "Position of the icon.")
    public abstract String getIconPos();

    @Property(description = "Displays button inline instead of fitting the content width, only used by mobile.")
    public abstract boolean isInline();

    @Property(defaultValue = "@(body)", description = "Appends the overlay to the element defined by search expression.")
    public abstract String getAppendTo();

    @Property(description = "Style class of the overlay menu element.")
    public abstract String getMenuStyleClass();

    @Property(description = "A menu model instance to create the items of splitButton programmatically.")
    public abstract MenuModel getModel();

    @Property(description = "Displays an input filter for the list.")
    public abstract boolean isFilter();

    @Property(description = "Match mode for filtering, valid values are startsWith (default), contains, endsWith and custom.")
    public abstract String getFilterMatchMode();

    @Property(description = "Client side function to use in custom filterMatchMode.")
    public abstract String getFilterFunction();

    @Property(description = "Watermark displayed in the filter input field before the user enters a value in an browser.")
    public abstract String getFilterPlaceholder();

    @Property(defaultValue = "true", description = "If true, the button will be disabled during Ajax requests triggered by the button or its menu items.")
    public abstract boolean isDisableOnAjax();

    @Property(defaultValue = "false", description = "Defines if filtering would be done using normalized values (accents will be removed from characters).")
    public abstract boolean isFilterNormalize();

    @Property(defaultValue = "true", description = "Defines if filter input should receive focus when overlay popup is displayed.")
    public abstract boolean isFilterInputAutoFocus();

    @Property(description = "The aria-label attribute is used to define a string that labels the current element for accessibility.")
    public abstract String getAriaLabel();

    @Property(defaultValue = "false", description = "Defines if dynamic loading is enabled for the element's panel."
            + " If the value is \"true\", the overlay is not rendered on page load to improve performance.")
    public abstract boolean isDynamic();
}