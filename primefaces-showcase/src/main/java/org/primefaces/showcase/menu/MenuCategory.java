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
package org.primefaces.showcase.menu;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

public class MenuCategory extends MenuItem implements Serializable {

    private final String label;
    private List<MenuItem> menuItems;
    private boolean custom;

    public MenuCategory(String label, List<MenuItem> menuItems) {
        super(label, (String) null);
        this.label = label;
        this.menuItems = menuItems;
    }

    public MenuCategory(String label, List<MenuItem> menuItems, boolean custom) {
        super(label, (String) null);
        this.label = label;
        this.menuItems = menuItems;
        this.custom = custom;
    }

    public String getLabel() {
        return label;
    }

    public List<MenuItem> getMenuItems() {
        return menuItems;
    }

    public boolean getCustom() {
        return custom;
    }

    @Override
    public int hashCode() {
        return Objects.hash(label);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        MenuCategory other = (MenuCategory) obj;
        return Objects.equals(label, other.label);
    }

    @Override
    public String toString() {
        return "MenuCategory [label=" + label + "]";
    }
}
