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
package org.primefaces.model.seo;

import java.io.Serializable;
import java.util.List;

/**
 * JSON for Linking Data Model.
 * Data is messy and disconnected. JSON-LD organizes and connects it, creating a better Web.
 *
 * @see <a href="https://json-ld.org/">JSON for Linking Data</a>
 * @since 12.0.0
 */
public class JsonLDModel implements Serializable {

    private static final long serialVersionUID = 1L;

    private String context;

    private String type;

    private List<JsonLDItem> items;

    private String itemListType;

    public JsonLDModel() { }

    public JsonLDModel(String context, String type) {
        this.context = context;
        this.type = type;
    }

    public JsonLDModel(String context, String type, String itemListType) {
        this(context, type);
        this.itemListType = itemListType;
    }

    public JsonLDModel(String context, String type, String itemListType, List<JsonLDItem> items) {
        this(context, type, itemListType);
        this.items = items;
    }

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<JsonLDItem> getItems() {
        return items;
    }

    public void setItems(List<JsonLDItem> items) {
        this.items = items;
    }

    public String getItemListType() {
        return itemListType;
    }

    public void setItemListType(String itemListType) {
        this.itemListType = itemListType;
    }
}
