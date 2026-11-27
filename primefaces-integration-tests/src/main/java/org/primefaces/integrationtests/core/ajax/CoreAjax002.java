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
package org.primefaces.integrationtests.core.ajax;

import org.primefaces.PrimeFaces;

import java.util.List;

import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Named;

@Named
@RequestScoped
public class CoreAjax002 {

    private static final int COMPLETED_PROGRESS = 100;

    private String property = "initial";
    private String oncomplete;
    private final List<String> items = List.of("item0", "item1");
    private String selectedItem;

    public void modifyProperty() {
        property = "modified";
    }

    public void fullOncomplete3() {
        oncomplete = "$('#output3').append('modified');";
    }

    public void fullOncomplete6() {
        oncomplete = "$('#output6').append('modified');";
    }

    public void fullOncomplete10() {
        oncomplete = "$('#output10').append('modified');";
    }

    public void addCallbackParam() {
        PrimeFaces.current().ajax().addCallbackParam("param", "ok");
    }

    public String getProperty() {
        return property;
    }

    public String getOncomplete() {
        return oncomplete;
    }

    public int getProgress() {
        return FacesContext.getCurrentInstance().isPostback() ? COMPLETED_PROGRESS : 0;
    }

    public List<String> getItems() {
        return items;
    }

    public String getSelectedItem() {
        return selectedItem;
    }

    public void setSelectedItem(String selectedItem) {
        this.selectedItem = selectedItem;
    }
}
