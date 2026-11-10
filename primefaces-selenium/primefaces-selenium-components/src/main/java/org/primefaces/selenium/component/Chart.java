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

import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Action;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;

/**
 * Component wrapper for the PrimeFaces {@code p:chart} (chart.js).
 */
public abstract class Chart extends AbstractComponent {

    @FindBy(tagName = "canvas")
    private WebElement canvas;

    /**
     * Gets the canvas the chart is drawn on.
     *
     * @return the canvas element
     */
    public WebElement getCanvas() {
        return canvas;
    }

    /**
     * Has the chart.js instance been created?
     *
     * @return true if the chart is rendered
     */
    public boolean isRendered() {
        return Boolean.TRUE.equals(PrimeSelenium.executeScript("return " + getWidgetByIdScript() + ".chart != null;"));
    }

    /**
     * Gets the chart type of the chart.js instance.
     *
     * @return the type like {@code bar} or {@code line}
     */
    public String getType() {
        return PrimeSelenium.executeScript("return " + getWidgetByIdScript() + ".chart.config.type;");
    }

    /**
     * Gets the labels of the chart.js instance.
     *
     * @return the labels
     */
    public List<String> getLabels() {
        List<Object> labels = PrimeSelenium.executeScript("return " + getWidgetByIdScript() + ".chart.data.labels;");
        return labels.stream().map(String::valueOf).collect(Collectors.toList());
    }

    /**
     * Gets the number of datasets of the chart.js instance.
     *
     * @return the number of datasets
     */
    public int getDatasetCount() {
        Number count = PrimeSelenium.executeScript("return " + getWidgetByIdScript() + ".chart.data.datasets.length;");
        return count.intValue();
    }

    /**
     * Is this component AJAX enabled with "itemSelect"?
     *
     * @return true if AJAX enabled false if not
     */
    public boolean isItemSelectAjaxified() {
        return ComponentUtils.hasAjaxBehavior(getRoot(), "itemSelect");
    }

    /**
     * Clicks the center of a data item (bar, point, arc) on the canvas, which fires the "itemSelect" behavior. The chart
     * should not be animating, e.g. use {@code animation: false} in the chart options.
     *
     * @param datasetIndex index of the dataset
     * @param index index of the item in the dataset
     */
    public void selectItem(int datasetIndex, int index) {
        WebElement canvasElement = getCanvas();
        List<Object> offset = PrimeSelenium.executeScript("var chart = " + getWidgetByIdScript() + ".chart;"
                    + "var point = chart.getDatasetMeta(arguments[0]).data[arguments[1]].getCenterPoint(true);"
                    + "var rect = arguments[2].getBoundingClientRect();"
                    + "return [point.x - rect.width / 2, point.y - rect.height / 2];",
                    datasetIndex, index, canvasElement);
        int x = (int) Math.round(((Number) offset.get(0)).doubleValue());
        int y = (int) Math.round(((Number) offset.get(1)).doubleValue());

        // offsets are relative to the center of the canvas
        Action action = new Actions(getWebDriver()).moveToElement(canvasElement, x, y).click().build();
        if (isItemSelectAjaxified()) {
            PrimeSelenium.guardAjax(action).perform();
        }
        else {
            action.perform();
        }
    }

    /**
     * Exports the chart as an image.
     *
     * @return the data URL of the image, like {@code data:image/png;base64,...}
     */
    public String exportAsImage() {
        return PrimeSelenium.executeScript("return " + getWidgetByIdScript() + ".exportAsImage().src;");
    }
}
