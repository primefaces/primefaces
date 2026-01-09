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
package org.primefaces.showcase.view.input;

import org.primefaces.event.SelectEvent;
import org.primefaces.event.ToggleSelectEvent;
import org.primefaces.event.UnselectEvent;
import org.primefaces.showcase.domain.Country;
import org.primefaces.showcase.service.CountryService;

import java.util.ArrayList;
import java.util.List;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.model.SelectItem;
import jakarta.faces.model.SelectItemGroup;
import jakarta.inject.Inject;
import jakarta.inject.Named;

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
    private List<Country> countries2;
    private List<Country> selectedCountries2;
    private List<SelectItem> countries3;
    private List<Country> selectedCountries3;

    @Inject
    private CountryService service;

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

        countries2 = service.getCountries();

        countries3 = new ArrayList<>();

        SelectItemGroup europeCountries3 = new SelectItemGroup("European Countries");
        europeCountries3.setSelectItems(isoCodesToSelectItemArray("DE", "TR", "ES"));

        SelectItemGroup americaCountries3 = new SelectItemGroup("American Countries");
        americaCountries3.setSelectItems(isoCodesToSelectItemArray("US", "BR", "MX"));

        countries3.add(europeCountries3);
        countries3.add(americaCountries3);
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

    public List<Country> getCountries2() {
        return countries2;
    }

    public void setCountries2(List<Country> countries2) {
        this.countries2 = countries2;
    }

    public List<Country> getSelectedCountries2() {
        return selectedCountries2;
    }

    public void setSelectedCountries2(List<Country> selectedCountries2) {
        this.selectedCountries2 = selectedCountries2;
    }

    public List<SelectItem> getCountries3() {
        return countries3;
    }

    public void setCountries3(List<SelectItem> countries3) {
        this.countries3 = countries3;
    }

    public List<Country> getSelectedCountries3() {
        return selectedCountries3;
    }

    public void setSelectedCountries3(List<Country> selectedCountries3) {
        this.selectedCountries3 = selectedCountries3;
    }

    public void onToggleSelect(ToggleSelectEvent event) {
        FacesMessage msg = new FacesMessage();
        msg.setSummary("Toggled: " + event.isSelected());
        msg.setSeverity(FacesMessage.SEVERITY_INFO);

        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void onItemSelect(SelectEvent event) {
        FacesMessage msg = new FacesMessage();
        msg.setSummary("Item selected: " + event.getObject().toString());
        msg.setSeverity(FacesMessage.SEVERITY_INFO);

        FacesContext.getCurrentInstance().addMessage(null, msg);
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
        return CountryService.toCountryStream(isoCodes)
                .map(country -> new SelectItem(country, country.getName()))
                .toArray(SelectItem[]::new);
    }
}
