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
package org.primefaces.showcase.view.panel;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;

@Named
@ViewScoped
public class DeferredView implements Serializable {

    private String basic;
    private Map<String, String> skeleton = new HashMap<>();
    private String manual;
    private String tab;
    private String visible;

    public void loadBasic() throws InterruptedException {
        Thread.sleep(1000); // Sleep a bit to show the loading indicator
        basic = "The data of the content is loaded in the listener of the load event, the default loading indicator is "
                + "displayed meanwhile. See xhtml and DeferredView.java";
    }

    public void loadSkeleton() throws InterruptedException {
        Thread.sleep(1000); // Sleep a bit to show the skeletons
        skeleton.put("title", "Deferred loading facet");
        skeleton.put("body", "The \"loading\" facet replaces the default loading indicator, typically with skeletons "
                + "in the shape of the content.");
    }

    public void loadManual() throws InterruptedException {
        Thread.sleep(1000); // Sleep a bit to show the loading indicator
        manual = "With mode=\"manual\" the content is loaded with load() of the client side component, here by the "
                + "button.";
    }

    public void loadTab() throws InterruptedException {
        Thread.sleep(1000); // Sleep a bit to show the skeleton
        tab = "With mode=\"visible\" the content is loaded when the component becomes visible, also inside tabs, "
                + "dialogs and scroll containers.";
    }

    public void loadVisible() throws InterruptedException {
        Thread.sleep(1000); // Sleep a bit to show the skeleton
        visible = "With mode=\"visible\" the content is loaded when the component is scrolled into view.";
    }

    public String getBasic() {
        return basic;
    }

    public Map<String, String> getSkeleton() {
        return skeleton;
    }

    public String getManual() {
        return manual;
    }

    public String getTab() {
        return tab;
    }

    public String getVisible() {
        return visible;
    }

}
