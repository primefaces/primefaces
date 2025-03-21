/*
 * The MIT License
 *
 * Copyright (c) 2009-2025 PrimeTek Informatics
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

public class MenuItem implements Serializable {

    private static final long serialVersionUID = 1L;
    private final String label;
    private String url;
    private List<MenuItem> menuItems;
    private String badge;
    private String badgeSeverity;
    private MenuItem parent;
    private String imageSrc;

    public MenuItem(String label, String url) {
        this.label = label;
        this.url = url;
    }

    public MenuItem(String label, String url, String badge) {
        this.label = label;
        this.url = url;
        this.badge = badge;
    }

    public MenuItem(String label, String url, String badge, String imageSrc) {
        this.label = label;
        this.url = url;
        this.badge = badge;
        this.imageSrc = imageSrc;
    }

    public MenuItem(String label, List<MenuItem> menuItems) {
        this.label = label;
        this.menuItems = menuItems;
    }

    public MenuItem(String label, List<MenuItem> menuItems, String badge) {
        this.label = label;
        this.menuItems = menuItems;
        this.badge = badge;
    }

    public String getLabel() {
        return label;
    }

    public String getUrl() {
        return url;
    }

    public String getBadge() {
        return badge;
    }

    public String getBadgeSeverity() {
        if (badgeSeverity != null) {
            return badgeSeverity;
        }
        badgeSeverity = "primary";
        if ("Deprecated".equalsIgnoreCase(badge)) {
            badgeSeverity = "warning";
        }
        if ("New".equalsIgnoreCase(badge)) {
            badgeSeverity = "success";
        }
        return badgeSeverity;
    }

    public List<MenuItem> getMenuItems() {
        return menuItems;
    }

    public MenuItem getParent() {
        return parent;
    }

    public void setParent(MenuItem parent) {
        this.parent = parent;
    }

    public String getImageSrc() {
        return imageSrc;
    }

    @Override
    public int hashCode() {
        return Objects.hash(label, url);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        MenuItem other = (MenuItem) obj;
        return Objects.equals(label, other.label) && Objects.equals(url, other.url);
    }

    @Override
    public String toString() {
        return "MenuItem [label=" + label + ", url=" + url + "]";
    }
}
