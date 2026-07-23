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
package org.primefaces.integrationtests.megamenu;

import org.primefaces.integrationtests.general.utilities.TestUtils;
import org.primefaces.model.menu.DefaultMenuColumn;
import org.primefaces.model.menu.DefaultMenuItem;
import org.primefaces.model.menu.DefaultMenuModel;
import org.primefaces.model.menu.DefaultSubMenu;
import org.primefaces.model.menu.MenuModel;

import java.io.Serializable;

import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;

import lombok.Data;

@Named
@ViewScoped
@Data
public class MegaMenu003 implements Serializable {

    private static final long serialVersionUID = 5366641524856531279L;

    private MenuModel model;

    @PostConstruct
    public void init() {
        model = new DefaultMenuModel();

        // Root submenu (mega panel)
        DefaultSubMenu options = DefaultSubMenu.builder()
                .label("Options")
                .icon("pi pi-cog")
                .build();

        // Column 1 - "File" group
        DefaultSubMenu fileGroup = DefaultSubMenu.builder()
                .label("File")
                .build();
        fileGroup.getElements().add(DefaultMenuItem.builder()
                .value("Save")
                .icon("pi pi-save")
                .command("#{megaMenu003.save}")
                .update("msgs")
                .build());
        fileGroup.getElements().add(DefaultMenuItem.builder()
                .value("Update")
                .icon("pi pi-refresh")
                .command("#{megaMenu003.update}")
                .update("msgs")
                .build());

        DefaultMenuColumn column1 = DefaultMenuColumn.builder()
                .addElement(fileGroup)
                .build();
        options.getElements().add(column1);

        // Column 2 - "Edit" group
        DefaultSubMenu editGroup = DefaultSubMenu.builder()
                .label("Edit")
                .build();
        editGroup.getElements().add(DefaultMenuItem.builder()
                .value("Delete")
                .icon("pi pi-times")
                .command("#{megaMenu003.delete}")
                .update("msgs")
                .build());

        DefaultMenuColumn column2 = DefaultMenuColumn.builder()
                .addElement(editGroup)
                .build();
        options.getElements().add(column2);

        model.getElements().add(options);
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
