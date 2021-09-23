/*
 * The MIT License
 *
 * Copyright (c) 2009-2021 PrimeTek
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

import javax.annotation.PostConstruct;
import javax.faces.model.SelectItem;
import javax.faces.model.SelectItemGroup;
import javax.faces.view.ViewScoped;
import javax.inject.Named;

import org.primefaces.integrationtests.general.utilities.TestUtils;
import org.primefaces.util.LangUtils;

import lombok.Data;

@Named
@ViewScoped
@Data
public class SelectOneMenu007 implements Serializable {

    private static final long serialVersionUID = -7798312444085660208L;

    private String countryGroup;
    private List<SelectItem> countriesGroup;

    @PostConstruct
    public void init() {
        countriesGroup = new ArrayList<>();

        SelectItemGroup europeCountries = new SelectItemGroup("Europe");
        europeCountries.setSelectItems(new SelectItem[] {
            new SelectItem("Germany", "Germany"),
            new SelectItem("Turkey", "Turkey"),
            new SelectItem("Spain", "Spain")
        });

        SelectItemGroup americaCountries = new SelectItemGroup("North America");
        americaCountries.setSelectItems(new SelectItem[] {
            new SelectItem("United States", "United States"),
            new SelectItem("Brazil", "Brazil"),
            new SelectItem("Turks & Caicos", "<span>Turks &amp; Caicos<span>", null, false, true),
            new SelectItem("Mexico", "Mexico")
        });

        countriesGroup.add(europeCountries);
        countriesGroup.add(americaCountries);
    }

    public void submit() {
        if (!LangUtils.isBlank(countryGroup)) {
            TestUtils.addMessage("Country", countryGroup);
        }
    }

}
