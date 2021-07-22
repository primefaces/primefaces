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
package org.primefaces.integrationtests.general.converter;

import org.primefaces.integrationtests.general.model.Driver;
import org.primefaces.integrationtests.general.service.GeneratedDriverService;

import javax.enterprise.inject.spi.CDI;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.faces.convert.FacesConverter;
import javax.inject.Inject;
import javax.inject.Named;

@Named
@FacesConverter(value = "generatedDriverConverter", managed = true)
public class GeneratedDriverConverter implements Converter {

    @Inject
    private GeneratedDriverService driverService;

    @Override
    public Object getAsObject(FacesContext fc, UIComponent uic, String value) {
        if (driverService == null) {
            /**
             * Not ideal work-around because @Inject does not work for managed
             * FacesConverter on TomEE + Mojarra as of march 2021.
             */
            driverService = CDI.current().select(GeneratedDriverService.class).get();
        }

        if (value != null && value.trim().length() > 0) {
            try {
                int id = Integer.parseInt(value);
                return driverService.getDrivers().stream().filter(d -> d.getId() == id).findFirst().get();
            }
            catch (NumberFormatException e) {
                throw new ConverterException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Conversion Error", "Not a valid driver."));
            }
        }
        else {
            return null;
        }
    }

    @Override
    public String getAsString(FacesContext fc, UIComponent uic, Object driver) {
        if (driver != null) {
            return String.valueOf(((Driver) driver).getId());
        }
        else {
            return null;
        }
    }
}
