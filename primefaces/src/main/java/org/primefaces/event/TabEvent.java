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
package org.primefaces.event;

import org.primefaces.component.tabview.Tab;

import jakarta.faces.component.UIComponent;
import jakarta.faces.component.behavior.Behavior;

public abstract class TabEvent<T> extends AbstractAjaxBehaviorEvent {

    private transient Tab tab;
    private T data;
    private String type;
    private int index;

    public TabEvent(UIComponent component, Behavior behavior, Tab tab, T data, String type, int index) {
        super(component, behavior);
        this.tab = tab;
        this.data = data;
        this.type = type;
        this.index = index;
    }

    public Tab getTab() {
        return tab;
    }

    @Deprecated
    public void setTab(Tab tab) {
        this.tab = tab;
    }

    public T getData() {
        return data;
    }

    @Deprecated
    public void setData(T data) {
        this.data = data;
    }

    public int getIndex() {
        return index;
    }

    public String getType() {
        return type;
    }
}
