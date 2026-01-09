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
package org.primefaces.integrationtests.selectonemenu;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import jakarta.annotation.PostConstruct;
import jakarta.faces.model.SelectItem;
import jakarta.faces.model.SelectItemGroup;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;

import lombok.Data;

@Named
@ViewScoped
@Data
public class SelectOneMenu002 implements Serializable {

    private static final long serialVersionUID = 6109117006170737298L;

    private List<SelectItem> cars;
    private String car;

    @PostConstruct
    public void init() {
        //cars
        SelectItemGroup g1 = new SelectItemGroup("<span style=\"color:darkgreen\">German</span> Cars");
        g1.setEscape(false);
        g1.setDescription("High quality cars from good-old germany.");

        SelectItem mercedes = new SelectItem("Mercedes", "Mer<span style=\"color:red\">cedes</span>");
        mercedes.setEscape(false);
        g1.setSelectItems(new SelectItem[] {
            new SelectItem("BMW", "BMW"),
            mercedes,
            new SelectItem("Volkswagen", "Volkswagen")});

        SelectItemGroup g2 = new SelectItemGroup("American <Cars>");
        g2.setSelectItems(new SelectItem[] {
            new SelectItem("Chrysler", "Chry<sler"),
            new SelectItem("blank", "&nbsp;"),
            new SelectItem("GM", "GM"),
            new SelectItem("Ford & Lincoln", "Ford & Lincoln"),
            new SelectItem("GitHub \"9336\" Quoted", "GitHub \"9336\" Quoted"),
            new SelectItem("< GitHub <i>9336</i>", "< GitHub <i>9336</i>", "", false, true),
            new SelectItem("< GitHub <i>9336</i>", "< GitHub <i>9336</i>", "", false, false)});

        cars = new ArrayList<>();
        cars.add(g1);
        cars.add(g2);
    }

}
