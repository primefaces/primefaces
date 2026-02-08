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
package org.primefaces.component.menuitem;

import org.primefaces.cdk.api.FacesBehaviorEvent;
import org.primefaces.cdk.api.FacesBehaviorEvents;
import org.primefaces.cdk.api.FacesComponentBase;
import org.primefaces.cdk.api.Property;
import org.primefaces.component.api.AjaxSource;
import org.primefaces.component.api.DialogReturnAware;
import org.primefaces.component.api.StyleAware;
import org.primefaces.component.api.UIOutcomeTarget;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.menu.MenuItem;
import org.primefaces.util.Callbacks;

import jakarta.faces.component.UICommand;
import jakarta.faces.event.AjaxBehaviorEvent;

@FacesComponentBase
@FacesBehaviorEvents({
    @FacesBehaviorEvent(name = "click", event = AjaxBehaviorEvent.class, description = "Fires when menu item is clicked.", defaultEvent = true),
    @FacesBehaviorEvent(name = DialogReturnAware.EVENT_DIALOG_RETURN, event = SelectEvent.class, description = "Fires when a dialog returns a value.")
})
public abstract class UIMenuItemBase extends UICommand implements AjaxSource, UIOutcomeTarget, MenuItem,
        StyleAware {

    public static final String COMPONENT_FAMILY = "org.primefaces.component";

    public UIMenuItemBase() {
        setRendererType(null);
    }

    @Override
    public String getFamily() {
        return COMPONENT_FAMILY;
    }

    @Override
    @Property(description = "The URL to redirect to after the menu item has been clicked. Similar to outcome which allows to specify " +
        "a navigation case, but the value is not touched (no prepending of the contextPath, not appending the sessionId or windowId), just encoded.")
    public abstract String getUrl();

    @Override
    @Property(description = "Target window for the link.")
    public abstract String getTarget();

    @Override
    @Property(description = "Client-side javascript callback to execute when menu item is clicked.")
    public abstract String getOnclick();

    @Override
    @Property(defaultValue = "false", description = "Disables the component.")
    public abstract boolean isDisabled();

    @Override
    @Property(defaultValue = "0", description = "Timeout in milliseconds for ajax request, whereas 0 means no timeout.")
    public abstract int getTimeout();

    @Override
    @Property(defaultValue = "true", description = "Specifies the submit mode, when set to true (default), submit would be made with Ajax.")
    public abstract boolean isAjax();

    @Override
    @Property(description = "Icon of the menu item.")
    public abstract String getIcon();

    @Override
    @Property(defaultValue = "left", description = "Position of the icon, valid values are \"left\" and \"right\".")
    public abstract String getIconPos();

    @Override
    @Property(description = "If true, components which autoUpdate=\"true\" will not be updated for this request. " +
        "If not specified, or the value is false, no such indication is made.")
    public abstract boolean isIgnoreAutoUpdate();

    @Override
    @Property(description = "Title text of the menu item.")
    public abstract String getTitle();

    @Override
    @Property(description = "Used to resolve a navigation case.")
    public abstract String getOutcome();

    @Override
    @Property(defaultValue = "false", description = "Whether to include page parameters in target URI.")
    public abstract boolean isIncludeViewParams();

    @Override
    @Property(description = "Identifier of the target page which should be scrolled to.")
    public abstract String getFragment();

    @Override
    @Property(defaultValue = "false", description = "Disable appending the on the rendering of this element.")
    public abstract boolean isDisableClientWindow();

    @Override
    @Property(description = "Inline style of the container element.")
    public abstract String getContainerStyle();

    @Override
    @Property(description = "Style class of the container element.")
    public abstract String getContainerStyleClass();

    @Override
    @Property(defaultValue = "true", description = "Defines if label of the component is escaped or not.")
    public abstract boolean isEscape();

    @Override
    @Property(description = "Relationship between the current document and the linked resource.")
    public abstract String getRel();

    @Override
    @Property(description = "If true, unresolvable components (ComponentNotFoundException) referenced in the update/process attribute are ignored.")
    public abstract boolean isIgnoreComponentNotFound();

    @Override
    @Property(description = "The aria-label attribute is used to define a string that labels the current element for accessibility.")
    public abstract String getAriaLabel();

    @Override
    @Property(description = "Badge value to display on the menu item.")
    public abstract Object getBadge();

    @Override
    public Callbacks.SerializableFunction<MenuItem, String> getFunction() {
        return null;
    }
}
