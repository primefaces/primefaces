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
package org.primefaces.showcase.view.theming;

import java.io.Serializable;
import java.util.List;

import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;

import lombok.Getter;
import lombok.Setter;

/**
 * Backing bean of the theming-next/layout.xhtml demo page (layout, base classes and utilities).
 */
@Named("themingNextLayoutView")
@ViewScoped
@Getter
@Setter
public class ThemingNextLayoutView implements Serializable {

    private static final long serialVersionUID = 1L;

    private List<String> cities = List.of("Berlin", "London", "Madrid", "Paris", "Rome", "Vienna");
    private String city;
    private String firstname;
    private String lastname;
    private int requests;

    /**
     * Slow action, so ajaxStatus and blockUI stay visible for a moment.
     */
    public void slow() {
        try {
            Thread.sleep(1500);
        }
        catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        requests++;
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO, "Done", "Request " + requests + " completed"));
    }

    /**
     * Fast action (lifecycle demo).
     */
    public void fast() {
        requests++;
    }
}
