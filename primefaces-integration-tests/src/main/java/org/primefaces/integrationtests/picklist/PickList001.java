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

import org.primefaces.integrationtests.general.model.Driver;
import org.primefaces.integrationtests.general.service.RealDriverService;
import org.primefaces.model.DualListModel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import lombok.Data;

@Named
@ViewScoped
@Data
public class PickList001 implements Serializable {

    private static final long serialVersionUID = 1L;

    @Inject
    private RealDriverService driverService;

    private DualListModel<Driver> standardPickList;
    private DualListModel<Driver> checkPickList;
    private DualListModel<Driver> instantPickList;

    @PostConstruct
    public void init() {
        final List<Driver> standardSource = new ArrayList<>(driverService.getDrivers());
        final List<Driver> standardTarget = new ArrayList<>();
        standardPickList = new DualListModel<>(standardSource, standardTarget);

        final List<Driver> checkSource = new ArrayList<>(driverService.getDrivers());
        final List<Driver> checkTarget = new ArrayList<>();
        checkPickList = new DualListModel<>(checkSource, checkTarget);

        final List<Driver> instantSource = new ArrayList<>(driverService.getDrivers());
        final List<Driver> instantTarget = new ArrayList<>();
        instantPickList = new DualListModel<>(instantSource, instantTarget);
    }
}
