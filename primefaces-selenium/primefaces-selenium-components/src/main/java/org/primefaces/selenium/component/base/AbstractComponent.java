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
package org.primefaces.selenium.component.base;

import org.json.JSONObject;
import org.openqa.selenium.JavascriptException;
import org.openqa.selenium.WebElement;
import org.primefaces.selenium.AbstractPrimePageFragment;
import org.primefaces.selenium.PrimeSelenium;

public abstract class AbstractComponent extends AbstractPrimePageFragment {

    /**
     * Gets the widget by component id JS function.
     *
     * @return the JS script
     */
    public String getWidgetByIdScript() {
        return ComponentUtils.getWidgetByIdScript(getId());
    }

    /**
     * Gets the current widget's configuration e.g. widget.cfg as a String.
     *
     * @return the String representation of the widget configuration
     */
    public String getWidgetConfigurationAsString() {
        return ComponentUtils.getWidgetConfiguration(getRoot());
    }

    /**
     * Gets the current widget's configuration e.g. widget.cfg as a JSON object.
     *
     * @return the {@link JSONObject} representing the config, useful for assertions
     */
    public JSONObject getWidgetConfiguration() {
        String cfg = getWidgetConfigurationAsString();
        if (cfg == null || cfg.length() == 0) {
            return null;
        }
        return new JSONObject(cfg);
    }

    /**
     * Is the event for the root-element ajaxified?
     *
     * @param event Event with the `on` prefix, such as `onclick` or `onblur`.
     * @return true if using AJAX false if not
     */
    protected boolean isAjaxified(String event) {
        return isAjaxified(getRoot(), event);
    }

    /**
     * Is the event ajaxified?
     *
     * @param element Element for which to do the check. (May be a child element of a complex component.) If no element is passed it defaults to getRoot().
     * @param event Event with the `on` prefix, such as `onclick` or `onblur`.
     * @return true is using AJAX false it not
     */
    protected boolean isAjaxified(WebElement element, String event) {
        if (element == null) {
            element = getRoot();
        }
        Boolean hasCspRegisteredEvent = false;
        try {
            hasCspRegisteredEvent = PrimeSelenium.executeScript("return PrimeFaces.csp.hasRegisteredAjaxifiedEvent('" +
                        element.getAttribute("id") + "', '" + event + "')");
        }
        catch (JavascriptException ex) {
            if (ex.getMessage().contains("PrimeFaces.csp.hasRegisteredAjaxifiedEvent is not a function")) {
                System.err.println("WARNING: 'pfselenium.core.csp.js' missing - not added to the page");
            }
            else {
                throw ex;
            }
        }
        return (ComponentUtils.isAjaxScript(element.getAttribute(event)) || hasCspRegisteredEvent);
    }
}
