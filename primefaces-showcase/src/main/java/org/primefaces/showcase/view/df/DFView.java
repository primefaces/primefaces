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
package org.primefaces.showcase.view.df;

import org.primefaces.PrimeFaces;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.DialogFrameworkOptions;
import org.primefaces.showcase.domain.Product;

import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Named;

@Named("dfView")
@RequestScoped
public class DFView {

    public void viewProducts() {
        DialogFrameworkOptions options = DialogFrameworkOptions.builder()
                .resizable(false)
                .build();

        PrimeFaces.current().dialog().openDynamic("viewProducts", options, null);
    }

    public void viewProductsCustomized() {
        DialogFrameworkOptions options = DialogFrameworkOptions.builder()
                .modal(true)
                .width("640")
                .height("340")
                .contentHeight("100%")
                .contentWidth("100%")
                .headerElement("customheader")
                .build();

        PrimeFaces.current().dialog().openDynamic("viewProducts", options, null);
    }

    public void viewResponsive() {
        DialogFrameworkOptions options = DialogFrameworkOptions.builder()
                .modal(true)
                .fitViewport(true)
                .responsive(true)
                .width("900px")
                .contentWidth("100%")
                .resizeObserver(true)
                .resizeObserverCenter(true)
                .resizable(false)
                .styleClass("max-w-screen")
                .iframeStyleClass("max-w-screen")
                .build();

        PrimeFaces.current().dialog().openDynamic("viewResponsive", options, null);
    }

    public void viewProductsLargerThanViewport() {
        DialogFrameworkOptions options = DialogFrameworkOptions.builder()
                .modal(true)
                .fitViewport(true)
                .build();

        PrimeFaces.current().dialog().openDynamic("viewProductsLargerThanViewport", options, null);
    }

    public void chooseProduct() {
        DialogFrameworkOptions options = DialogFrameworkOptions.builder()
                .resizable(false)
                .draggable(false)
                .modal(false)
                .build();

        PrimeFaces.current().dialog().openDynamic("selectProduct", options, null);
    }

    public void onProductChosen(SelectEvent event) {
        Product product = (Product) event.getObject();
        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Product Selected", "Name:" + product.getName());

        FacesContext.getCurrentInstance().addMessage(null, message);
    }

    public void showMessage() {
        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Message", " Always Bet on Prime!");

        PrimeFaces.current().dialog().showMessageDynamic(message);
    }
}
