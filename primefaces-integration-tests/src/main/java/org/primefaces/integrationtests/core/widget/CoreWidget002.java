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
package org.primefaces.integrationtests.core.widget;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;

import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;

import lombok.Data;

/**
 * Widgets with overlays and global listeners (DatePicker, Slider, Knob, KeyFilter, Signature, Spinner), updated and
 * removed by AJAX.
 */
@Named
@ViewScoped
@Data
public class CoreWidget002 implements Serializable {

    @Serial private static final long serialVersionUID = 1L;

    private boolean rendered = true;
    private LocalDate date1;
    private LocalDate date2;
    private Integer slider = 30;
    private Integer knob = 40;
    private String signature;
    private Integer spinner = 5;
    private String filtered;

    public void toggle() {
        rendered = !rendered;
    }
}
