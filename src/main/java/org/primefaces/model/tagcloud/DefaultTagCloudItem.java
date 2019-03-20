/**
 * The MIT License
 *
 * Copyright (c) 2009-2019 PrimeTek
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
package org.primefaces.model.tagcloud;

import java.io.Serializable;

public class DefaultTagCloudItem implements TagCloudItem, Serializable {

    private static final long serialVersionUID = 1L;

    private String label;

    private String url;

    private int strength = 1;

    public DefaultTagCloudItem() {

    }

    public DefaultTagCloudItem(String label, int strength) {
        this.label = label;
        this.strength = strength;
    }

    public DefaultTagCloudItem(String label, String url, int strength) {
        this(label, strength);
        this.url = url;
    }

    @Override
    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    @Override
    public int getStrength() {
        return strength;
    }

    public void setStrength(int strength) {
        this.strength = strength;
    }

    @Override
    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final TagCloudItem other = (TagCloudItem) obj;
        if ((this.label == null) ? (other.getLabel() != null) : !this.label.equals(other.getLabel())) {
            return false;
        }
        if ((this.url == null) ? (other.getUrl() != null) : !this.url.equals(other.getUrl())) {
            return false;
        }
        if (this.strength != other.getStrength()) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 73 * hash + (this.label != null ? this.label.hashCode() : 0);
        hash = 73 * hash + (this.url != null ? this.url.hashCode() : 0);
        hash = 73 * hash + this.strength;
        return hash;
    }
}
