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
package org.primefaces.integrationtests.menubar;

import org.primefaces.integrationtests.general.utilities.TestUtils;
import org.primefaces.model.menu.DefaultMenuItem;
import org.primefaces.model.menu.DefaultMenuModel;
import org.primefaces.model.menu.DefaultSubMenu;
import org.primefaces.model.menu.MenuModel;

import java.io.Serializable;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Named;

import lombok.Data;

@Named
@RequestScoped
@Data
public class MenuBar003 implements Serializable {

    private static final long serialVersionUID = 5366641524856531279L;

    private MenuModel model;

    @PostConstruct
    public void init() {
        model = new DefaultMenuModel();

        //First submenu
        DefaultSubMenu firstSubmenu = DefaultSubMenu.builder()
                .label("Options")
                .build();

        DefaultMenuItem item = DefaultMenuItem.builder()
                .value("Save (Non-Ajax)")
                .icon("pi pi-save")
                .ajax(false)
                .command("#{menuBar002.save}")
                .update("msgs")
                .build();
        firstSubmenu.getElements().add(item);

        item = DefaultMenuItem.builder()
                .value("Update")
                .icon("pi pi-refresh")
                .command("#{menuBar002.update}")
                .update("msgs")
                .build();
        firstSubmenu.getElements().add(item);

        item = DefaultMenuItem.builder()
                .value("Delete")
                .icon("pi pi-times")
                .command("#{menuBar002.delete}")
                .update("msgs")
                .build();
        firstSubmenu.getElements().add(item);

        model.getElements().add(firstSubmenu);

        //Second submenu
        DefaultSubMenu secondSubmenu = DefaultSubMenu.builder()
                .label("Navigations")
                .build();

        item = DefaultMenuItem.builder()
                .value("Website")
                .url("http://www.primefaces.org")
                .icon("pi pi-external-link")
                .build();
        secondSubmenu.getElements().add(item);

        item = DefaultMenuItem.builder()
                .value("Internal")
                .icon("pi pi-upload")
                .command("#{menuBar002.redirect}")
                .build();
        secondSubmenu.getElements().add(item);

        model.getElements().add(secondSubmenu);
    }

    public void save() {
        TestUtils.addMessage("Save", "Data saved");
    }

    public void update() {
        TestUtils.addMessage("Update", "Data updated");
    }

    public void delete() {
        TestUtils.addMessage("Delete", "Data deleted");
    }

}
