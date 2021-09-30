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
package org.primefaces.integrationtests.autocomplete;

import lombok.Data;
import org.primefaces.integrationtests.general.model.Driver;
import org.primefaces.integrationtests.general.service.RealDriverService;
import org.primefaces.integrationtests.general.utilities.TestUtils;

import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

@Named
@ViewScoped
@Data
public class AutoComplete005 implements Serializable {

    private static final long serialVersionUID = 5157497001324985194L;

    @Inject
    private RealDriverService service;

    private List<Driver> allDrivers;

    private List<Driver> selectedDrivers;

    @PostConstruct
    public void init() {
        allDrivers = service.getDrivers();
    }

    public List<Driver> completeDriver(String query) {
        String queryLowerCase = query.toLowerCase();
        return allDrivers.stream().filter(d -> d.getName().toLowerCase().contains(queryLowerCase)).collect(Collectors.toList());
    }

    public void submit() {
        if (selectedDrivers != null) {
            String selectedDriversMsg = selectedDrivers.stream().map(d -> d.getId() + " - " + d.getName()).collect(Collectors.joining(", "));
            TestUtils.addMessage("Selected Drivers", selectedDriversMsg);
        }
    }

}
