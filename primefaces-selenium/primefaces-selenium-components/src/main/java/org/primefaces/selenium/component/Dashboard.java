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
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Action;

/**
 * Component wrapper for the PrimeFaces {@code p:dashboard}. The panels are reordered by dragging them by their title bar:
 * in the legacy dashboard they are sorted within and between the columns, in the responsive dashboard
 * ({@code responsive="true"}) two panels swap their places.
 */
public abstract class Dashboard extends AbstractComponent {

    /**
     * Is this the responsive dashboard?
     *
     * @return true if responsive, false if it has the legacy columns
     */
    public boolean isResponsive() {
        return getWidgetConfiguration().optBoolean("responsive", false);
    }

    /**
     * Can the panels be reordered ({@code reordering="true"})?
     *
     * @return true if the panels can be dragged
     */
    public boolean isReordering() {
        return !getWidgetConfiguration().optBoolean("disabled", false);
    }

    /**
     * Is this component AJAX enabled with "reorder"?
     *
     * @return true if AJAX enabled false if not
     */
    public boolean isReorderAjaxified() {
        return ComponentUtils.hasAjaxBehavior(getRoot(), "reorder");
    }

    /**
     * Gets the columns of the legacy dashboard, or the cells holding one panel each of the responsive dashboard.
     *
     * @return the columns
     */
    public List<WebElement> getColumns() {
        return findElements(By.cssSelector(":scope > .p-dashboard-column, :scope > .p-dashboard-panel"));
    }

    /**
     * Gets the panels of a column.
     *
     * @param columnIndex index of the column
     * @return the panels from top to bottom
     */
    public List<WebElement> getPanels(int columnIndex) {
        return getColumns().get(columnIndex).findElements(By.cssSelector(":scope > .p-panel"));
    }

    /**
     * Gets the client ids of the panels, per column.
     *
     * @return the ids of the panels of each column, from top to bottom
     */
    public List<List<String>> getPanelIds() {
        return getColumns().stream()
                    .map(column -> column.findElements(By.cssSelector(":scope > .p-panel")).stream()
                                .map(panel -> panel.getDomAttribute("id"))
                                .collect(Collectors.toList()))
                    .collect(Collectors.toList());
    }

    /**
     * Gets a panel of this dashboard.
     *
     * @param clientId the client id of the panel
     * @return the panel
     */
    public WebElement getPanel(String clientId) {
        return findElement(By.id(clientId));
    }

    /**
     * Drags a panel by its title bar onto the title bar of another panel. In the legacy dashboard it is moved before
     * the other panel, in the responsive dashboard the two panels swap their places. Waits for the "reorder" AJAX
     * behavior, so the panel must really change its place.
     *
     * @param panel the panel to move
     * @param targetPanel the panel to drop it on
     */
    public void movePanel(WebElement panel, WebElement targetPanel) {
        drag(panel, getTitlebar(targetPanel), 0, 0);
    }

    /**
     * Drags a panel of the legacy dashboard by its title bar to the end of a column, below its last panel. Waits for the
     * "reorder" AJAX behavior, so the panel must really change its place.
     *
     * @param panel the panel to move
     * @param columnIndex index of the column
     */
    public void movePanelToColumn(WebElement panel, int columnIndex) {
        WebElement column = getColumns().get(columnIndex);
        // the column has a drop area below its last panel (padding-bottom)
        drag(panel, column, 0, column.getSize().getHeight() / 2 - 20);
    }

    private WebElement getTitlebar(WebElement panel) {
        return panel.findElement(By.cssSelector(":scope > .p-panel-titlebar"));
    }

    private void drag(WebElement panel, WebElement target, int xOffset, int yOffset) {
        Action action = Draggable.dragAndDropAction(getWebDriver(), getTitlebar(panel), target, xOffset, yOffset);
        if (isReordering() && isReorderAjaxified()) {
            PrimeSelenium.guardAjax(action).perform();
        }
        else {
            action.perform();
        }
        Draggable.waitForDragEnd();
    }
}
