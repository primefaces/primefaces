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
package org.primefaces.showcase.view.app;

import org.primefaces.context.PrimeApplicationContext;
import org.primefaces.showcase.domain.Country;

import java.io.Serializable;
import java.util.Locale;

import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Named;

@Named
@SessionScoped
public class App implements Serializable {

    private Country locale = new Country(0, Locale.US);

    /**
     * The direction of the locale: primefaces.DIR in web.xml, unless the user chose one in the theme configurator
     * (pc:themeConfigurator, the effective direction is #{p:dir()}).
     *
     * @return ltr or rtl
     */
    public String getLocaleDir() {
        return locale.isRtl() ? "rtl" : "ltr";
    }

    public Country getLocale() {
        return locale;
    }

    public void setLocale(Country locale) {
        this.locale = locale;
    }

    public String getPrimeFacesVersion() {
        return PrimeApplicationContext.getCurrentInstance(FacesContext.getCurrentInstance()).getEnvironment().getBuildVersion();
    }
}
