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
package org.primefaces.integrationtests.picklist;

import org.primefaces.model.DualListModel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;

import lombok.Data;

@Named
@ViewScoped
@Data
public class PickList002 implements Serializable {

    private static final long serialVersionUID = 1L;

    private String string;
    private DualListModel<String> cities;
    private final List<String> actualSourceCities = new ArrayList<>();
    private final List<String> actualTargetCities = new ArrayList<>();

    @PostConstruct
    public void init() {
        string = "PickList - no data validation";

        //Cities
        List<String> citiesSource = new ArrayList<>();
        List<String> citiesTarget = new ArrayList<>();

        citiesSource.add("San Francisco");
        citiesSource.add("London");
        citiesSource.add("Paris");
        citiesSource.add("Istanbul");
        citiesSource.add("Berlin");
        citiesSource.add("Barcelona");
        citiesSource.add("Rome");

        cities = new DualListModel<>(citiesSource, citiesTarget);
    }

    public void selectCities() {
        actualSourceCities.clear();
        actualSourceCities.addAll(cities.getSource());
        actualTargetCities.clear();
        actualTargetCities.addAll(cities.getTarget());
    }
}
