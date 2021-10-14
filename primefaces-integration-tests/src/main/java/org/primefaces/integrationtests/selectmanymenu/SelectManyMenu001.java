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
package org.primefaces.integrationtests.selectmanymenu;

import lombok.Data;
import org.primefaces.integrationtests.general.model.Driver;
import org.primefaces.integrationtests.general.service.RealDriverService;
import org.primefaces.integrationtests.general.utilities.TestUtils;

import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Named
@ViewScoped
@Data
public class SelectManyMenu001 implements Serializable {

    private static final long serialVersionUID = 6576774027660483468L;

    @Inject
    private RealDriverService driverService;
    private List<Driver> drivers;

    private List<Driver> selectedDrivers;

    @PostConstruct
    public void init() {
        drivers = driverService.getDrivers();
        selectedDrivers = new ArrayList<>(Arrays.asList(drivers.get(1), drivers.get(2)));
    }

    public void submit() {
        if (selectedDrivers == null || selectedDrivers.isEmpty()) {
            TestUtils.addMessage("no driver selected");
        }
        else {
            TestUtils.addMessage("selected drivers" , selectedDrivers.stream().map(d -> d.getName()).collect(Collectors.joining(",")));
        }
    }

}
