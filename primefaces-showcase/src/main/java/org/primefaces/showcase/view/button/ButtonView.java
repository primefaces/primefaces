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
package org.primefaces.showcase.view.button;

import org.primefaces.model.menu.DefaultMenuItem;
import org.primefaces.model.menu.DefaultMenuModel;
import org.primefaces.model.menu.DefaultSubMenu;
import org.primefaces.model.menu.MenuModel;

import java.util.concurrent.TimeUnit;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Named;

@Named
@RequestScoped
public class ButtonView {

    private MenuModel model;

    @PostConstruct
    public void init() {
        model = new DefaultMenuModel();

        //First submenu
        DefaultMenuItem item = DefaultMenuItem.builder()
                .value("External")
                .url("http://www.primefaces.org")
                .icon("pi pi-home")
                .build();

        DefaultSubMenu firstSubmenu = DefaultSubMenu.builder()
                .label("Dynamic Submenu")
                .addElement(item)
                .build();

        model.getElements().add(firstSubmenu);

        //Second submenu
        item = DefaultMenuItem.builder()
                .id("mniSave")
                .value("Save")
                .icon("pi pi-save")
                .function((i) -> save())
                .update("messages")
                .build();

        DefaultSubMenu secondSubmenu = DefaultSubMenu.builder()
                .label("Dynamic Actions")
                .addElement(item)
                .build();

        item = DefaultMenuItem.builder()
                .value("Delete")
                .icon("pi pi-times")
                .command("#{buttonView.delete}")
                .ajax(false)
                .build();
        secondSubmenu.getElements().add(item);

        model.getElements().add(secondSubmenu);
    }

    public MenuModel getModel() {
        return model;
    }

    public String save() {
        addMessage("Data saved");
        return null;
    }

    public void update() {
        addMessage("Data updated");
    }

    public void delete() {
        addMessage("Data deleted");
    }

    public String sleepAndSave() throws InterruptedException {
        TimeUnit.SECONDS.sleep(1);
        return save();
    }

    public void sleepAndUpdate() throws InterruptedException {
        TimeUnit.SECONDS.sleep(1);
        update();
    }

    public void sleepAndDelete() throws InterruptedException {
        TimeUnit.SECONDS.sleep(1);
        delete();
    }

    public void buttonAction() {
        addMessage("Welcome to PrimeFaces!!");
    }

    public void addMessage(String summary) {
        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, summary, null);
        FacesContext.getCurrentInstance().addMessage(null, message);
    }
}
