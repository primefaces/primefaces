/*
 * The MIT License
 *
 * Copyright (c) 2009-2021 PrimeTek
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
package org.primefaces.showcase.view.panel;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import javax.faces.view.ViewScoped;
import javax.inject.Named;

@Named
@ViewScoped
public class OutputPanelView implements Serializable {

    private Map<String, String> data = new HashMap<>();
    private Map<String, String> data2 = new HashMap<>();

    public void loadData() throws InterruptedException {
        Thread.sleep(1000); // Sleep a bit to show the skeletons
        data.put("title", "OutputPanel loading facet");
        data.put("body", "Deferred loading can be used with the \"loading\" facet to show UI while the data is being "
                + "loaded. This is normally combined with the load ajax event with a listener which loads the data you "
                + "want to show in your panel. See xhtml and OutputPanelView.java");
    }

    public void loadData2() throws InterruptedException {
        Thread.sleep(1000); // Sleep a bit to show the skeletons
        data2.put("title", "Deferred loading in Ajax requests");
        data2.put("body", "By default deferred loading does not work with Ajax requests. As a solution you can use the "
                + "\"loaded\" boolean attribute to enforce loading (even in Ajax requests). Normally you would use "
                + "an expression here checking if your data is empty. See xhtml and OutputPanelView.java");
    }

    public Map<String, String> getData() {
        return data;
    }

    public Map<String, String> getData2() {
        return data2;
    }

}
