/*
 * The MIT License
 *
 * Copyright (c) 2009-2026 PrimeFaces
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
import org.primefaces.selenium.component.base.AbstractComponent;
import org.primefaces.selenium.component.base.ComponentUtils;

import java.util.List;
import java.util.stream.Collectors;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;

/**
 * Component wrapper for the PrimeFaces {@code p:tagCloud}.
 */
public abstract class TagCloud extends AbstractComponent {

    /**
     * Gets the links of all tags.
     *
     * @return the tag links
     */
    public List<WebElement> getTags() {
        return findElements(By.cssSelector("li > a"));
    }

    /**
     * Gets the labels of all tags.
     *
     * @return the tag labels
     */
    public List<String> getLabels() {
        return getTags().stream().map(WebElement::getText).collect(Collectors.toList());
    }

    /**
     * Gets the link of a tag.
     *
     * @param label the label of the tag
     * @return the tag link
     */
    public WebElement getTag(String label) {
        return getTags().stream()
                    .filter(tag -> label.equals(tag.getText()))
                    .findFirst()
                    .orElseThrow(() -> new NoSuchElementException("Tag not found: " + label));
    }

    /**
     * Gets the strength of a tag, which defines its size.
     *
     * @param label the label of the tag
     * @return the strength
     */
    public int getStrength(String label) {
        String styleClass = getTag(label).findElement(By.xpath("..")).getDomAttribute("class");
        for (String cls : styleClass.split(" ")) {
            if (cls.startsWith("p-tagcloud-strength-")) {
                return Integer.parseInt(cls.substring("p-tagcloud-strength-".length()));
            }
        }
        throw new IllegalStateException("No strength for tag: " + label);
    }

    /**
     * Is this component AJAX enabled with "select"?
     *
     * @return true if AJAX enabled false if not
     */
    public boolean isSelectAjaxified() {
        return ComponentUtils.hasAjaxBehavior(getRoot(), "select");
    }

    /**
     * Clicks a tag. Tags without URL fire the "select" behavior, tags with URL navigate.
     *
     * @param label the label of the tag
     */
    public void select(String label) {
        WebElement tag = getTag(label);
        boolean withUrl = !"#".equals(tag.getDomAttribute("href"));
        if (!withUrl && isSelectAjaxified()) {
            PrimeSelenium.guardAjax(tag).click();
        }
        else if (withUrl && !"_blank".equals(tag.getDomAttribute("target"))) {
            PrimeSelenium.guardHttp(tag).click();
        }
        else {
            tag.click();
        }
    }
}
