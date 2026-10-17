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

import org.primefaces.showcase.domain.Country;
import org.primefaces.showcase.service.CountryService;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.annotation.PostConstruct;
import jakarta.faces.component.UIInput;
import jakarta.faces.event.ComponentSystemEvent;
import jakarta.faces.model.SelectItem;
import jakarta.faces.model.SelectItemGroup;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import lombok.Getter;
import lombok.Setter;

/**
 * Backing bean of the theming-next/forms-select.xhtml demo page.
 */
@Named("themingNextFormsSelectView")
@ViewScoped
@Getter
@Setter
public class ThemingNextFormsSelectView implements Serializable {

    private static final long serialVersionUID = 1L;

    @Inject
    private transient CountryService countryService;

    private List<String> cities = List.of("Amsterdam", "Berlin", "London", "Madrid", "New York", "Paris", "Rome", "Tokyo");
    private List<SelectItem> groupedCities;
    private List<SelectItem> categories;
    private List<Country> countries;

    private String city = "Paris";
    private String noCity;
    private List<String> selectedCities = new ArrayList<>(List.of("Paris", "Rome"));
    private List<String> manyCities = new ArrayList<>(List.of("Amsterdam", "Berlin", "London", "Madrid", "Paris"));
    private List<String> noCities = new ArrayList<>();
    private Country country;
    private List<Country> selectedCountries = new ArrayList<>();
    private String category = "Option 1.2.2";

    private Boolean checked = Boolean.TRUE;
    private Boolean unchecked = Boolean.FALSE;
    private Boolean triNull;
    private Boolean triTrue = Boolean.TRUE;
    private Boolean triFalse = Boolean.FALSE;

    private LocalDate date = LocalDate.now();
    private LocalDate noDate;
    private List<LocalDate> range = new ArrayList<>(List.of(LocalDate.now().minusDays(2), LocalDate.now().plusDays(3)));
    private List<LocalDate> multi = new ArrayList<>(List.of(LocalDate.now().minusDays(5), LocalDate.now(), LocalDate.now().plusDays(4)));
    private LocalDateTime dateTime = LocalDateTime.now().withSecond(0).withNano(0);
    private LocalTime time = LocalTime.of(13, 45);
    private LocalDate minDate = LocalDate.now().minusDays(10);
    private LocalDate maxDate = LocalDate.now().plusDays(10);

    @PostConstruct
    public void init() {
        countries = new ArrayList<>(countryService.getCountries().subList(0, 8));
        country = countries.get(2);
        selectedCountries.add(countries.get(0));
        selectedCountries.add(countries.get(3));

        SelectItemGroup europe = new SelectItemGroup("Europe");
        europe.setSelectItems(new SelectItem[]{new SelectItem("Berlin"), new SelectItem("Paris"), new SelectItem("Rome")});
        SelectItemGroup america = new SelectItemGroup("America");
        america.setSelectItems(new SelectItem[]{new SelectItem("Chicago"), new SelectItem("New York"), new SelectItem("Toronto", "Toronto", null, true)});
        SelectItemGroup asia = new SelectItemGroup("Asia");
        asia.setSelectItems(new SelectItem[]{new SelectItem("Kyoto"), new SelectItem("Tokyo")});
        groupedCities = List.of(europe, america, asia);

        SelectItemGroup group11 = new SelectItemGroup("Group 1.1");
        group11.setSelectItems(new SelectItem[]{new SelectItem("Option 1.1.1"), new SelectItem("Option 1.1.2")});
        SelectItemGroup group12 = new SelectItemGroup("Group 1.2");
        group12.setSelectItems(new SelectItem[]{new SelectItem("Option 1.2.1"), new SelectItem("Option 1.2.2"), new SelectItem("Option 1.2.3")});
        SelectItemGroup group1 = new SelectItemGroup("Group 1");
        group1.setSelectItems(new SelectItem[]{group11, group12});
        SelectItemGroup group2 = new SelectItemGroup("Group 2");
        group2.setSelectItems(new SelectItem[]{new SelectItem("Option 2.1"), new SelectItem("Option 2.2")});
        categories = List.of(group1, group2, new SelectItem("Option 3"));
    }

    /**
     * Marks the input as invalid, so it renders the ui-state-error look without a submit.
     *
     * @param event the preRenderComponent event of an input component
     */
    public void markInvalid(ComponentSystemEvent event) {
        if (event.getComponent() instanceof UIInput input) {
            input.setValid(false);
        }
    }
}
