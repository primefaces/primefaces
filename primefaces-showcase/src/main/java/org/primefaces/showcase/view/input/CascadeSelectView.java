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
package org.primefaces.showcase.view.input;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.faces.model.SelectItem;
import javax.faces.model.SelectItemGroup;
import javax.inject.Named;
import java.util.ArrayList;
import java.util.List;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import org.primefaces.event.SelectEvent;
import org.primefaces.showcase.domain.Place;

@Named
@RequestScoped
public class CascadeSelectView {

    private List<SelectItem> countries;
    private String selection;
    private String selection2;

    @PostConstruct
    public void init() {
        countries = new ArrayList<>();
        SelectItemGroup group1 = new SelectItemGroup("Australia");
        group1.setValue(new Place("Australia", "au", "country"));
        SelectItemGroup group2 = new SelectItemGroup("Canada");
        group2.setValue(new Place("Canada", "ca", "country"));
        SelectItemGroup group3 = new SelectItemGroup("United States");
        group3.setValue(new Place("United States", "us", "country"));

        SelectItemGroup group11 = new SelectItemGroup("New South Wales");
        group11.setValue(new Place("New South Wales", null, "states"));
        SelectItemGroup group12 = new SelectItemGroup("Queensland");
        group12.setValue(new Place("Queensland", null, "states"));

        SelectItemGroup group21 = new SelectItemGroup("Quebec");
        group21.setValue(new Place("Quebec", null, "states"));
        SelectItemGroup group22 = new SelectItemGroup("Ontario");
        group22.setValue(new Place("Ontario", null, "states"));

        SelectItemGroup group31 = new SelectItemGroup("California");
        group31.setValue(new Place("California", null, "states"));
        SelectItemGroup group32 = new SelectItemGroup("Florida");
        group32.setValue(new Place("Florida", null, "states"));
        SelectItemGroup group33 = new SelectItemGroup("Texas");
        group33.setValue(new Place("Texas", null, "states"));

        SelectItem option111 = new SelectItem(new Place("Sydney", null, "city"));
        SelectItem option112 = new SelectItem(new Place("Newcastle", null, "city"));
        SelectItem option113 = new SelectItem(new Place("Wollongong", null, "city"));
        group11.setSelectItems(new SelectItem[]{option111, option112, option113});

        SelectItem option121 = new SelectItem(new Place("Brisbane", null, "city"));
        SelectItem option122 = new SelectItem(new Place("Townsville", null, "city"));
        group12.setSelectItems(new SelectItem[]{option121, option122});

        SelectItem option211 = new SelectItem(new Place("Montreal", null, "city"));
        SelectItem option212 = new SelectItem(new Place("Quebec City", null, "city"));
        group21.setSelectItems(new SelectItem[]{option211, option212});

        SelectItem option221 = new SelectItem(new Place("Ottawa", null, "city"));
        SelectItem option222 = new SelectItem(new Place("Toronto", null, "city"));
        group22.setSelectItems(new SelectItem[]{option221, option222});

        SelectItem option311 = new SelectItem(new Place("Los Angeles", null, "city"));
        SelectItem option312 = new SelectItem(new Place("San Diego", null, "city"));
        SelectItem option313 = new SelectItem(new Place("San Francisco", null, "city"));
        group31.setSelectItems(new SelectItem[]{option311, option312, option313});

        SelectItem option321 = new SelectItem(new Place("Jacksonville", null, "city"));
        SelectItem option322 = new SelectItem(new Place("Miami", null, "city"));
        SelectItem option323 = new SelectItem(new Place("Tampa", null, "city"));
        SelectItem option324 = new SelectItem(new Place("Orlando", null, "city"));
        group32.setSelectItems(new SelectItem[]{option321, option322, option323, option324});

        SelectItem option331 = new SelectItem(new Place("Austin", null, "city"));
        SelectItem option332 = new SelectItem(new Place("Dallas", null, "city"));
        SelectItem option333 = new SelectItem(new Place("Houston", null, "city"));
        group33.setSelectItems(new SelectItem[]{option331, option332, option333});

        group1.setSelectItems(new SelectItem[]{group11, group12});
        group2.setSelectItems(new SelectItem[]{group21, group22});
        group3.setSelectItems(new SelectItem[]{group31, group32, group33});

        countries.add(group1);
        countries.add(group2);
        countries.add(group3);
    }

    public void onItemSelect(SelectEvent event) {
        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Selected City", (String) event.getObject());
        FacesContext.getCurrentInstance().addMessage(null, message);
    }

    public List<SelectItem> getCountries() {
        return countries;
    }

    public String getSelection() {
        return selection;
    }

    public void setSelection(String selection) {
        this.selection = selection;
    }

    public String getSelection2() {
        return selection2;
    }

    public void setSelection2(String selection2) {
        this.selection2 = selection2;
    }
}
