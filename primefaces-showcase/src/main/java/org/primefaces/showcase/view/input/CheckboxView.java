/*
 * The MIT License
 *
 * Copyright (c) 2009-2023 PrimeTek Informatics
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

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Stream;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.faces.model.SelectItem;
import javax.faces.model.SelectItemGroup;
import javax.inject.Named;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

import org.primefaces.event.UnselectEvent;
import org.primefaces.showcase.domain.Country;

@Named
@RequestScoped
public class CheckboxView {

    private String[] selectedOptions;
    private String[] selectedOptions2;
    private String[] selectedCities;
    private String[] selectedCities2;
    private List<String> cities;
    private List<SelectItem> countries;
    private String[] selectedCountries;
    private List<SelectItem> countries2;
    private String[] selectedCountries2;

    @PostConstruct
    public void init() {
        cities = new ArrayList<>();
        cities.add("Miami");
        cities.add("London");
        cities.add("Paris");
        cities.add("Istanbul");
        cities.add("Berlin");
        cities.add("Barcelona");
        cities.add("Rome");
        cities.add("Brasilia");
        cities.add("Amsterdam");

        countries = new ArrayList<>();
        SelectItemGroup europeCountries = new SelectItemGroup("European Countries");
        europeCountries.setSelectItems(new SelectItem[]{
            new SelectItem("Germany", "Germany"),
            new SelectItem("Turkey", "Turkey"),
            new SelectItem("Spain", "Spain")
        });

        SelectItemGroup americaCountries = new SelectItemGroup("American Countries");
        americaCountries.setSelectItems(new SelectItem[]{
            new SelectItem("United States", "United States"),
            new SelectItem("Brazil", "Brazil"),
            new SelectItem("Mexico", "Mexico")
        });

        countries.add(europeCountries);
        countries.add(americaCountries);

        countries2 = new ArrayList<>();

        SelectItemGroup europeCountries2 = new SelectItemGroup("European Countries");
        europeCountries2.setSelectItems(isoCodesToSelectItemArray("DE", "TR", "ES"));

        SelectItemGroup americaCountries2 = new SelectItemGroup("American Countries");
        americaCountries2.setSelectItems(isoCodesToSelectItemArray("US", "BR", "MX"));

        countries2.add(europeCountries2);
        countries2.add(americaCountries2);
    }

    public String[] getSelectedOptions() {
        return selectedOptions;
    }

    public void setSelectedOptions(String[] selectedOptions) {
        this.selectedOptions = selectedOptions;
    }

    public String[] getSelectedOptions2() {
        return selectedOptions2;
    }

    public void setSelectedOptions2(String[] selectedOptions2) {
        this.selectedOptions2 = selectedOptions2;
    }

    public String[] getSelectedCities() {
        return selectedCities;
    }

    public void setSelectedCities(String[] selectedCities) {
        this.selectedCities = selectedCities;
    }

    public String[] getSelectedCities2() {
        return selectedCities2;
    }

    public void setSelectedCities2(String[] selectedCities2) {
        this.selectedCities2 = selectedCities2;
    }

    public List<String> getCities() {
        return cities;
    }

    public void setCities(List<String> cities) {
        this.cities = cities;
    }

    public List<SelectItem> getCountries() {
        return countries;
    }

    public void setCountries(List<SelectItem> countries) {
        this.countries = countries;
    }

    public String[] getSelectedCountries() {
        return selectedCountries;
    }

    public void setSelectedCountries(String[] selectedCountries) {
        this.selectedCountries = selectedCountries;
    }

    public List<SelectItem> getCountries2() {
        return countries2;
    }

    public void setCountries2(List<SelectItem> countries2) {
        this.countries2 = countries2;
    }

    public String[] getSelectedCountries2() {
        return selectedCountries2;
    }

    public void setSelectedCountries2(String[] selectedCountries2) {
        this.selectedCountries2 = selectedCountries2;
    }

    public void onItemUnselect(UnselectEvent event) {
        FacesMessage msg = new FacesMessage();
        msg.setSummary("Item unselected: " + event.getObject().toString());
        msg.setSeverity(FacesMessage.SEVERITY_INFO);

        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void selectedOptionsChanged() {
        String message = "selectedOptions changed to: ";
        if (selectedOptions != null) {
            for (int i = 0; i < selectedOptions.length; i++) {
                if (i > 0) {
                    message += ", ";
                }
                message += selectedOptions[i];
            }
        }

        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO, message, null));
    }

    public void selectedOptions2Changed() {
        String message = "selectedOptions2 changed to: ";
        if (selectedOptions2 != null) {
            for (int i = 0; i < selectedOptions2.length; i++) {
                if (i > 0) {
                    message += ", ";
                }
                message += selectedOptions2[i];
            }
        }

        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO, message, null));
    }

    private SelectItem[] isoCodesToSelectItemArray(String... isoCodes) {
        return Stream.of(isoCodes)
                .map(isoCode -> new Locale("", isoCode))
                .map(locale -> new Country(locale.hashCode(), locale))
                .map(country -> new SelectItem(country, country.getName()))
                .toArray(SelectItem[]::new);
    }
}
