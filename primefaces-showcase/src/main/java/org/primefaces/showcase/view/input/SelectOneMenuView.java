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

import org.primefaces.showcase.domain.Country;
import org.primefaces.showcase.service.CountryService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.model.SelectItem;
import jakarta.faces.model.SelectItemGroup;
import jakarta.inject.Inject;
import jakarta.inject.Named;

@Named
@RequestScoped
public class SelectOneMenuView {

    private String selectedOption;
    private String rtl;
    private String hideNoSelectOption;

    private String countryGroup;
    private List<SelectItem> countriesGroup;

    private String city;
    private Map<String, String> cities = new HashMap<>();

    private Country country;
    private List<Country> countries;

    private String option;
    private List<String> options;

    private String longItemLabel;
    private String labeled;

    private String icon = "flag";

    @Inject
    private CountryService service;

    @PostConstruct
    public void init() {

        countriesGroup = new ArrayList<>();

        SelectItemGroup europeCountries = new SelectItemGroup("Europe Countries");
        europeCountries.setSelectItems(new SelectItem[]{
            new SelectItem("Germany", "Germany"),
            new SelectItem("Greece", "Greece"),
            new SelectItem("Turkey", "Turkey"),
            new SelectItem("Slovakia", "Slovakia"),
            new SelectItem("Slovenia", "Slovenia"),
            new SelectItem("Spain", "Spain")
        });

        SelectItemGroup americaCountries = new SelectItemGroup("America Countries");
        americaCountries.setSelectItems(new SelectItem[]{
            new SelectItem("United States", "United States"),
            new SelectItem("Brazil", "Brazil"),
            new SelectItem("Mexico", "Mexico")
        });

        countriesGroup.add(europeCountries);
        countriesGroup.add(americaCountries);

        //cities
        cities = new HashMap<>();
        cities.put("New York", "New York");
        cities.put("London", "London");
        cities.put("Paris", "Paris");
        cities.put("Barcelona", "Barcelona");
        cities.put("Istanbul", "Istanbul");
        cities.put("Berlin", "Berlin");

        //countries
        countries = service.getCountries();

        //options
        options = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            options.add("Option " + i);
        }
    }

    public String getSelectedOption() {
        return selectedOption;
    }

    public void setSelectedOption(String selectedOption) {
        this.selectedOption = selectedOption;
    }

    public String getRtl() {
        return rtl;
    }

    public String getHideNoSelectOption() {
        return hideNoSelectOption;
    }

    public void setHideNoSelectOption(String hideNoSelectOption) {
        this.hideNoSelectOption = hideNoSelectOption;
    }

    public void setRtl(String rtl) {
        this.rtl = rtl;
    }

    public String getCountryGroup() {
        return countryGroup;
    }

    public void setCountryGroup(String countryGroup) {
        this.countryGroup = countryGroup;
    }

    public List<SelectItem> getCountriesGroup() {
        return countriesGroup;
    }

    public void setCountriesGroup(List<SelectItem> countriesGroup) {
        this.countriesGroup = countriesGroup;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public Map<String, String> getCities() {
        return cities;
    }

    public void setCities(Map<String, String> cities) {
        this.cities = cities;
    }

    public Country getCountry() {
        return country;
    }

    public void setCountry(Country country) {
        this.country = country;
    }

    public List<Country> getCountries() {
        return countries;
    }

    public String getOption() {
        return option;
    }

    public void setOption(String option) {
        this.option = option;
    }

    public List<String> getOptions() {
        return options;
    }

    public void setOptions(List<String> options) {
        this.options = options;
    }

    public String getLongItemLabel() {
        return longItemLabel;
    }

    public void setLongItemLabel(String longItemLabel) {
        this.longItemLabel = longItemLabel;
    }

    public String getLabeled() {
        return labeled;
    }

    public void setLabeled(String labeled) {
        this.labeled = labeled;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public void setService(CountryService service) {
        this.service = service;
    }
}
