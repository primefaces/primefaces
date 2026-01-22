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
package org.primefaces.integrationtests.colorpicker;

import org.primefaces.component.colorpicker.ColorPicker;
import org.primefaces.integrationtests.general.utilities.TestUtils;

import java.io.Serializable;

import jakarta.annotation.PostConstruct;
import jakarta.faces.event.AjaxBehaviorEvent;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;

import lombok.Data;

@Named
@ViewScoped
@Data
public class ColorPicker001 implements Serializable {

    private static final long serialVersionUID = 1L;

    private String color;
    private String ajax;
    private String openclose;

    @PostConstruct
    public void init() {
        color = "#2a9d8f";
        ajax = "#e9c46a";
        openclose = "#d62828";
    }

    public void submit() {
        TestUtils.addMessage("Color Saved", getColor());
    }

    public void onColorChange(AjaxBehaviorEvent e) {
        ColorPicker picker = (ColorPicker) e.getComponent();
        TestUtils.addMessage("Color Changed",  String.valueOf(picker.getValue()));
    }

    public void onPopupClosed(AjaxBehaviorEvent e) {
        ColorPicker picker = (ColorPicker) e.getComponent();
        TestUtils.addMessage("Popup Closed",  String.valueOf(picker.getValue()));
    }

    public void onPopupOpened(AjaxBehaviorEvent e) {
        ColorPicker picker = (ColorPicker) e.getComponent();
        TestUtils.addMessage("Popup Opened",  String.valueOf(picker.getValue()));
    }

}
