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
package org.primefaces.integrationtests.datatable;

import org.primefaces.integrationtests.jpa.TrackingJPALazyDataModel;
import org.primefaces.integrationtests.jpa.entity.CountryJpaEntity;
import org.primefaces.integrationtests.jpa.entity.CountryJpaEntity.Continent;
import org.primefaces.integrationtests.jpa.service.CountryJpaService;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import lombok.Data;

@Named
@ViewScoped
@Data
public class DataTable050 implements Serializable {

    private static final long serialVersionUID = 1L;

    private final LazyDataModelCallTracker lazyDataModelCallTracker = new LazyDataModelCallTracker();
    private TrackingJPALazyDataModel<CountryJpaEntity> lazyDataModel;
    private List<CountryJpaEntity> selectedCountries;
    private boolean selectionPageOnly = true;

    @Inject
    private CountryJpaService countryJpaService;

    @PostConstruct
    public void init() {
        seedData();
        lazyDataModel = new TrackingJPALazyDataModel<>(countryJpaService.entityManagerSupplier(), "dataTable050",
                CountryJpaEntity.class, CountryJpaEntity::getId);
    }

    private void seedData() {
        if (countryJpaService.isInitialized()) {
            return;
        }
        // Source: UN Demographic Yearbook 2023 (UN WPP 2024 revision), Annex I, Table 5.
        //   Only countries with a 2023 mid-year population estimate are included.
        //   Population rounded to nearest million.
        //   Continent/Region: as listed in source (Africa, America North, America South,
        //   Asia, Europe, Oceania; Australia and New Zealand).
        //   https://unstats.un.org/unsd/demographic-social/products/dyb/dyb_2023/
        List<CountryJpaEntity> countries = new ArrayList<>();
        countries.add(new CountryJpaEntity("Albania", 3, Continent.EUROPE));
        countries.add(new CountryJpaEntity("Algeria", 46, Continent.AFRICA));
        countries.add(new CountryJpaEntity("Angola", 34, Continent.AFRICA));
        countries.add(new CountryJpaEntity("Argentina", 47, Continent.SOUTH_AMERICA));
        countries.add(new CountryJpaEntity("Armenia", 3, Continent.ASIA));
        countries.add(new CountryJpaEntity("Australia", 27, Continent.AUSTRALIA_AND_NEWZEALAND));
        countries.add(new CountryJpaEntity("Austria", 9, Continent.EUROPE));
        countries.add(new CountryJpaEntity("Azerbaijan", 10, Continent.ASIA));
        countries.add(new CountryJpaEntity("Bahrain", 2, Continent.ASIA));
        countries.add(new CountryJpaEntity("Bangladesh", 173, Continent.ASIA));
        countries.add(new CountryJpaEntity("Belarus", 9, Continent.EUROPE));
        countries.add(new CountryJpaEntity("Belgium", 12, Continent.EUROPE));
        countries.add(new CountryJpaEntity("Bolivia", 12, Continent.SOUTH_AMERICA));
        countries.add(new CountryJpaEntity("Botswana", 2, Continent.AFRICA));
        countries.add(new CountryJpaEntity("Brazil", 216, Continent.SOUTH_AMERICA));
        countries.add(new CountryJpaEntity("Bulgaria", 6, Continent.EUROPE));
        countries.add(new CountryJpaEntity("Burkina Faso", 23, Continent.AFRICA));
        countries.add(new CountryJpaEntity("Burundi", 13, Continent.AFRICA));
        countries.add(new CountryJpaEntity("Cambodia", 17, Continent.ASIA));
        countries.add(new CountryJpaEntity("Cameroon", 28, Continent.AFRICA));
        countries.add(new CountryJpaEntity("Canada", 40, Continent.NORTH_AMERICA));
        countries.add(new CountryJpaEntity("Chile", 20, Continent.SOUTH_AMERICA));
        countries.add(new CountryJpaEntity("China", 1411, Continent.ASIA));
        countries.add(new CountryJpaEntity("Colombia", 52, Continent.SOUTH_AMERICA));
        countries.add(new CountryJpaEntity("Costa Rica", 5, Continent.NORTH_AMERICA));
        countries.add(new CountryJpaEntity("Croatia", 4, Continent.EUROPE));
        countries.add(new CountryJpaEntity("Czechia", 11, Continent.EUROPE));
        countries.add(new CountryJpaEntity("Denmark", 6, Continent.EUROPE));
        countries.add(new CountryJpaEntity("Dominican Republic", 11, Continent.NORTH_AMERICA));
        countries.add(new CountryJpaEntity("Ecuador", 18, Continent.SOUTH_AMERICA));
        countries.add(new CountryJpaEntity("Egypt", 105, Continent.AFRICA));
        countries.add(new CountryJpaEntity("Eritrea", 4, Continent.AFRICA));
        countries.add(new CountryJpaEntity("Ethiopia", 107, Continent.AFRICA));
        countries.add(new CountryJpaEntity("Finland", 6, Continent.EUROPE));
        countries.add(new CountryJpaEntity("Georgia", 4, Continent.EUROPE));
        countries.add(new CountryJpaEntity("Germany", 84, Continent.EUROPE));
        countries.add(new CountryJpaEntity("Ghana", 32, Continent.AFRICA));
        countries.add(new CountryJpaEntity("Greece", 10, Continent.EUROPE));
        countries.add(new CountryJpaEntity("Guatemala", 18, Continent.NORTH_AMERICA));
        countries.add(new CountryJpaEntity("Honduras", 10, Continent.NORTH_AMERICA));
        countries.add(new CountryJpaEntity("Hungary", 10, Continent.EUROPE));
        countries.add(new CountryJpaEntity("Indonesia", 279, Continent.ASIA));
        countries.add(new CountryJpaEntity("Iran", 85, Continent.ASIA));
        countries.add(new CountryJpaEntity("Iraq", 43, Continent.ASIA));
        countries.add(new CountryJpaEntity("Italy", 59, Continent.EUROPE));
        countries.add(new CountryJpaEntity("Jordan", 12, Continent.ASIA));
        countries.add(new CountryJpaEntity("Kazakhstan", 20, Continent.ASIA));
        countries.add(new CountryJpaEntity("Kenya", 52, Continent.AFRICA));
        countries.add(new CountryJpaEntity("Kuwait", 5, Continent.ASIA));
        countries.add(new CountryJpaEntity("Kyrgyzstan", 7, Continent.ASIA));
        countries.add(new CountryJpaEntity("Lithuania", 3, Continent.EUROPE));
        countries.add(new CountryJpaEntity("Malaysia", 33, Continent.ASIA));
        countries.add(new CountryJpaEntity("Mauritius", 1, Continent.AFRICA));
        countries.add(new CountryJpaEntity("Mexico", 131, Continent.NORTH_AMERICA));
        countries.add(new CountryJpaEntity("Mongolia", 3, Continent.ASIA));
        countries.add(new CountryJpaEntity("Morocco", 37, Continent.AFRICA));
        countries.add(new CountryJpaEntity("Mozambique", 32, Continent.AFRICA));
        countries.add(new CountryJpaEntity("Myanmar", 56, Continent.ASIA));
        countries.add(new CountryJpaEntity("Namibia", 3, Continent.AFRICA));
        countries.add(new CountryJpaEntity("Netherlands", 18, Continent.EUROPE));
        countries.add(new CountryJpaEntity("New Zealand", 5, Continent.AUSTRALIA_AND_NEWZEALAND));
        countries.add(new CountryJpaEntity("Nicaragua", 7, Continent.NORTH_AMERICA));
        countries.add(new CountryJpaEntity("Niger", 25, Continent.AFRICA));
        countries.add(new CountryJpaEntity("Norway", 5, Continent.EUROPE));
        countries.add(new CountryJpaEntity("Oman", 5, Continent.ASIA));
        countries.add(new CountryJpaEntity("Panama", 4, Continent.NORTH_AMERICA));
        countries.add(new CountryJpaEntity("Peru", 34, Continent.SOUTH_AMERICA));
        countries.add(new CountryJpaEntity("Philippines", 112, Continent.ASIA));
        countries.add(new CountryJpaEntity("Poland", 37, Continent.EUROPE));
        countries.add(new CountryJpaEntity("Portugal", 10, Continent.EUROPE));
        countries.add(new CountryJpaEntity("Qatar", 3, Continent.ASIA));
        countries.add(new CountryJpaEntity("Republic of the Congo", 6, Continent.AFRICA));
        countries.add(new CountryJpaEntity("Romania", 19, Continent.EUROPE));
        countries.add(new CountryJpaEntity("Russian Federation", 146, Continent.EUROPE));
        countries.add(new CountryJpaEntity("Rwanda", 13, Continent.AFRICA));
        countries.add(new CountryJpaEntity("Senegal", 18, Continent.AFRICA));
        countries.add(new CountryJpaEntity("Serbia", 7, Continent.EUROPE));
        countries.add(new CountryJpaEntity("Sierra Leone", 9, Continent.AFRICA));
        countries.add(new CountryJpaEntity("Singapore", 6, Continent.ASIA));
        countries.add(new CountryJpaEntity("Slovakia", 5, Continent.EUROPE));
        countries.add(new CountryJpaEntity("South Africa", 61, Continent.AFRICA));
        countries.add(new CountryJpaEntity("South Korea", 52, Continent.ASIA));
        countries.add(new CountryJpaEntity("South Sudan", 15, Continent.AFRICA));
        countries.add(new CountryJpaEntity("Spain", 48, Continent.EUROPE));
        countries.add(new CountryJpaEntity("Sri Lanka", 22, Continent.ASIA));
        countries.add(new CountryJpaEntity("Sweden", 11, Continent.EUROPE));
        countries.add(new CountryJpaEntity("Tajikistan", 10, Continent.ASIA));
        countries.add(new CountryJpaEntity("Thailand", 67, Continent.ASIA));
        countries.add(new CountryJpaEntity("Trinidad and Tobago", 1, Continent.NORTH_AMERICA));
        countries.add(new CountryJpaEntity("Turkey", 85, Continent.ASIA));
        countries.add(new CountryJpaEntity("Uganda", 46, Continent.AFRICA));
        countries.add(new CountryJpaEntity("United Arab Emirates", 11, Continent.ASIA));
        countries.add(new CountryJpaEntity("United States", 335, Continent.NORTH_AMERICA));
        countries.add(new CountryJpaEntity("Uruguay", 4, Continent.SOUTH_AMERICA));
        countries.add(new CountryJpaEntity("Uzbekistan", 36, Continent.ASIA));
        countries.add(new CountryJpaEntity("Venezuela", 34, Continent.SOUTH_AMERICA));
        countries.add(new CountryJpaEntity("Vietnam", 100, Continent.ASIA));
        countries.add(new CountryJpaEntity("Zimbabwe", 15, Continent.AFRICA));
        countryJpaService.saveAll(countries);
    }

    public void submit() {
        if (selectedCountries != null) {
            FacesMessage msg = new FacesMessage("Selected Country(s)", selectedCountries.stream()
                        .sorted(Comparator.comparing(CountryJpaEntity::getId))
                        .map(c -> c.getId().toString())
                        .collect(Collectors.joining(",")));
            FacesContext.getCurrentInstance().addMessage(null, msg);
        }
    }

    public void toggleSelectPageOnly() {
        setSelectionPageOnly(!isSelectionPageOnly());
    }

    public LazyDataModelCallTracker getLazyDataModelCallTracker() {
        return lazyDataModelCallTracker;
    }
}
