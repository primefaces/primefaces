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
package org.primefaces.showcase.view.theming;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import jakarta.annotation.PostConstruct;
import jakarta.faces.component.UIInput;
import jakarta.faces.event.ComponentSystemEvent;
import jakarta.faces.model.SelectItem;
import jakarta.faces.model.SelectItemGroup;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;

import lombok.Getter;
import lombok.Setter;

/**
 * Backing bean of the theming selection form section (theming/sections/forms-select.xhtml).
 */
@Named("themingFormsSelectView")
@ViewScoped
@Getter
@Setter
public class ThemingFormsSelectView implements Serializable {

    private static final long serialVersionUID = 1L;

    private List<String> cities = List.of("Amsterdam", "Berlin", "London", "Madrid", "New York", "Paris", "Rome", "Tokyo");
    private List<SelectItem> groupedCities;

    private String city = "Paris";
    private String noCity;
    private List<String> selectedCities = new ArrayList<>(List.of("Paris", "Rome"));

    private Boolean checked = Boolean.TRUE;
    private Boolean unchecked = Boolean.FALSE;

    private LocalDate date = LocalDate.now();
    private List<LocalDate> range = new ArrayList<>(List.of(LocalDate.now().minusDays(2), LocalDate.now().plusDays(3)));

    @PostConstruct
    public void init() {
        SelectItemGroup europe = new SelectItemGroup("Europe");
        europe.setSelectItems(new SelectItem[]{new SelectItem("Berlin"), new SelectItem("Paris"), new SelectItem("Rome")});
        SelectItemGroup america = new SelectItemGroup("America");
        america.setSelectItems(new SelectItem[]{new SelectItem("Chicago"), new SelectItem("New York"), new SelectItem("Toronto", "Toronto", null, true)});
        SelectItemGroup asia = new SelectItemGroup("Asia");
        asia.setSelectItems(new SelectItem[]{new SelectItem("Kyoto"), new SelectItem("Tokyo")});
        groupedCities = List.of(europe, america, asia);
    }

    /**
     * Marks the input as invalid, so it renders the p-state-error look without a submit.
     *
     * @param event the preRenderComponent event of an input component
     */
    public void markInvalid(ComponentSystemEvent event) {
        if (event.getComponent() instanceof UIInput input) {
            input.setValid(false);
        }
    }
}
