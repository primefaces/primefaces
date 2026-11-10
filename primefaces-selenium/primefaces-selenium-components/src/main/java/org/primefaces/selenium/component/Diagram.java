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

import org.primefaces.selenium.PrimeExpectedConditions;
import org.primefaces.selenium.PrimeSelenium;
import org.primefaces.selenium.component.base.AbstractComponent;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

/**
 * Component wrapper for the PrimeFaces {@code p:diagram}. Connecting or disconnecting endpoints always sends an AJAX
 * request to update the model.
 */
public abstract class Diagram extends AbstractComponent {

    /**
     * Gets the elements.
     *
     * @return the elements
     */
    public List<WebElement> getElements() {
        return findElements(By.cssSelector(":scope > .p-diagram-element"));
    }

    /**
     * Gets an element.
     *
     * @param id the ID of the element in the model
     * @return the element
     */
    public WebElement getElement(String id) {
        return findElement(By.id(getId() + "-" + id));
    }

    /**
     * Gets the number of connections.
     *
     * @return the number of connections
     */
    public int getConnectionCount() {
        waitUntilReady();
        Number count = PrimeSelenium.executeScript("return " + getWidgetByIdScript() + ".canvas.getAllConnections().length;");
        return count.intValue();
    }

    /**
     * Are the two endpoints connected?
     *
     * @param sourceEndPointId the ID of the source endpoint in the model
     * @param targetEndPointId the ID of the target endpoint in the model
     * @return true if connected
     */
    public boolean isConnected(String sourceEndPointId, String targetEndPointId) {
        waitUntilReady();
        return PrimeSelenium.executeScript("return !!(" + findConnectionScript() + ");", sourceEndPointId, targetEndPointId);
    }

    /**
     * Connects two endpoints.
     *
     * @param sourceEndPointId the ID of the source endpoint in the model
     * @param targetEndPointId the ID of the target endpoint in the model
     */
    public void connect(String sourceEndPointId, String targetEndPointId) {
        waitUntilReady();
        PrimeSelenium.guardAjax(getWidgetByIdScript() + ".canvas.connect({uuids: [arguments[0], arguments[1]]});",
                    sourceEndPointId, targetEndPointId);
    }

    /**
     * Removes the connection between two endpoints.
     *
     * @param sourceEndPointId the ID of the source endpoint in the model
     * @param targetEndPointId the ID of the target endpoint in the model
     */
    public void disconnect(String sourceEndPointId, String targetEndPointId) {
        waitUntilReady();
        PrimeSelenium.guardAjax(getWidgetByIdScript() + ".canvas.deleteConnection(" + findConnectionScript() + ");",
                    sourceEndPointId, targetEndPointId);
    }

    /**
     * Script which finds the connection between the endpoints with the UUIDs {@code arguments[0]} and
     * {@code arguments[1]}.
     */
    private String findConnectionScript() {
        return getWidgetByIdScript() + ".canvas.getAllConnections().filter(function(c) {"
                    + "return c.endpoints[0].getUuid() === this[0] && c.endpoints[1].getUuid() === this[1];"
                    + "}, arguments)[0]";
    }

    private void waitUntilReady() {
        PrimeSelenium.waitGui().until(PrimeExpectedConditions.script("var w = " + getWidgetByIdScript() + "; return !!(w && w.canvas);"));
    }
}
