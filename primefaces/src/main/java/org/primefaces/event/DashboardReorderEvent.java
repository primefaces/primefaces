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
package org.primefaces.event;

import java.io.Serial;

import jakarta.faces.component.UIComponent;
import jakarta.faces.component.behavior.Behavior;

/**
 * Fired when a panel of a dashboard was moved (classic dashboard) or swapped with another panel (responsive dashboard).
 * The model of the dashboard is already updated when the listener is called.
 */
public class DashboardReorderEvent extends AbstractAjaxBehaviorEvent {

    @Serial
    private static final long serialVersionUID = 1L;

    private final String panelId;
    private final int columnIndex;
    private final int itemIndex;
    private final int senderColumnIndex;
    private final int senderItemIndex;

    public DashboardReorderEvent(UIComponent component, Behavior behavior, String panelId, int columnIndex, int itemIndex,
            int senderColumnIndex, int senderItemIndex) {
        super(component, behavior);
        this.panelId = panelId;
        this.columnIndex = columnIndex;
        this.itemIndex = itemIndex;
        this.senderColumnIndex = senderColumnIndex;
        this.senderItemIndex = senderItemIndex;
    }

    /**
     * @return the id of the moved panel
     */
    public String getPanelId() {
        return panelId;
    }

    /**
     * @return the index of the column the panel is in now
     */
    public int getColumnIndex() {
        return columnIndex;
    }

    /**
     * @return the index of the panel in its column now
     */
    public int getItemIndex() {
        return itemIndex;
    }

    /**
     * @return the index of the column the panel was in before, -1 for a new panel dropped from outside of the dashboard
     */
    public int getSenderColumnIndex() {
        return senderColumnIndex;
    }

    /**
     * @return the index of the panel in its column before, -1 for a new panel dropped from outside of the dashboard
     */
    public int getSenderItemIndex() {
        return senderItemIndex;
    }
}
