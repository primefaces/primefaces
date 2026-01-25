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
package org.primefaces.showcase.view.input;

import org.primefaces.component.selectmanybutton.SelectManyButton;

import java.util.List;

import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.event.AjaxBehaviorEvent;
import jakarta.inject.Named;

@Named
@RequestScoped
public class SelectManyButtonView {

    private List<String> selectedOptions;
    private List<String> selectedColors;
    private List<String> selectedDevices;

    public void onChange(AjaxBehaviorEvent event) {
        Object value = ((SelectManyButton) event.getSource()).getValue();
        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Selected Devices",
                value != null ? value.toString() : "None selected.");
        FacesContext.getCurrentInstance().addMessage(null, message);
    }

    public List<String> getSelectedOptions() {
        return selectedOptions;
    }

    public void setSelectedOptions(List<String> selectedOptions) {
        this.selectedOptions = selectedOptions;
    }

    public List<String> getSelectedColors() {
        return selectedColors;
    }

    public void setSelectedColors(List<String> selectedColors) {
        this.selectedColors = selectedColors;
    }

    public List<String> getSelectedDevices() {
        return selectedDevices;
    }

    public void setSelectedDevices(List<String> selectedDevices) {
        this.selectedDevices = selectedDevices;
    }
}
