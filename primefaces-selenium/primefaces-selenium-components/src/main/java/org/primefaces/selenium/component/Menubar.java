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
package org.primefaces.selenium.component;

import org.primefaces.selenium.PrimeSelenium;
import org.primefaces.selenium.component.base.AbstractInputComponent;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

/**
 * Component wrapper for the PrimeFaces {@code p:menubar}.
 */
public abstract class Menubar extends AbstractInputComponent {

    public WebElement findMenuitemByValue(String value) {
        return findMenuitemByValue(this, value);
    }

    public WebElement findMenuitemByValue(WebElement parentElt, String value) {
        List<WebElement> subElements = parentElt.findElements(By.cssSelector("ul > li.ui-menuitem"));
        return subElements.stream()
                .filter(e -> e.getText().equals(value))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("no menuitem with value '" + value + "'"));
    }

    /**
     * Select L1 menuitem
     * @param value
     * @return
     */
    public WebElement selectMenuitemByValue(String value) {
        Actions actions = new Actions(getWebDriver());
        actions.moveToElement(this.getWrappedElement()).build().perform(); // FF - workaround when clicking on multiple menuitems successively
        return selectMenuitemByValue(this, value);
    }

    /**
     * Select L2+ menuitem
     * @param parentElt parent menuitem
     * @param value
     * @return
     */
    public WebElement selectMenuitemByValue(WebElement parentElt, String value) {
        WebElement elt = findMenuitemByValue(parentElt, value);

        if ((PrimeSelenium.hasCssClass(elt, "ui-menu-parent")) && isToggleEventHover()) {
            Actions actions = new Actions(getWebDriver());
            actions.moveToElement(elt).build().perform();
        }
        else {
            WebElement eltA = elt.findElement(By.xpath("./a"));
            if (isAjaxified(eltA, "onclick")) {
                PrimeSelenium.guardAjax(elt).click();
            }
            else {
                elt.click();
            }
            // some more cases?
        }

        return  elt;
    }

    public boolean isToggleEventHover() {
        return "hover".equals(getWidgetConfiguration().getString("toggleEvent"));
    }

    public boolean isToggleEventClick() {
        return "click".equals(getWidgetConfiguration().getString("toggleEvent"));
    }
}
