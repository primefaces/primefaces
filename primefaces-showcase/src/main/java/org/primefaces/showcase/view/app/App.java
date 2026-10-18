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
import java.util.List;
import java.util.Locale;

import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Named;

@Named
@SessionScoped
public class App implements Serializable {

    /**
     * The presets of primefaces-theming. The theme configurator (theming/configurator.xhtml) switches with ?theme=...,
     * the choice is kept in the session; color scheme, density, primary color and input style are set on the client.
     */
    public static final List<String> PRESETS = List.of("aura", "lara", "nora", "metal", "glass");

    private String theme = PRESETS.get(0);
    private Country locale = new Country(0, Locale.US);
    // text direction chosen in the theme configurator (?dir=ltr|rtl); null: the direction of the locale
    private String direction;

    /**
     * The current preset (primefaces.THEME in web.xml); a valid ?theme= request parameter switches it.
     *
     * @return aura, lara, nora, metal or glass
     */
    public String getTheme() {
        String requested = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("theme");
        if (requested != null && PRESETS.contains(requested)) {
            theme = requested;
        }
        return theme;
    }

    public void setTheme(String theme) {
        if (PRESETS.contains(theme)) {
            this.theme = theme;
        }
    }

    /**
     * The text direction (primefaces.DIR in web.xml, dir of the html element): a valid ?dir= request parameter sets it,
     * without a choice it follows the locale.
     *
     * @return ltr or rtl
     */
    public String getDir() {
        String requested = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("dir");
        if ("ltr".equals(requested) || "rtl".equals(requested)) {
            direction = requested;
        }
        if (direction != null) {
            return direction;
        }
        return locale.isRtl() ? "rtl" : "ltr";
    }

    public List<String> getPresets() {
        return PRESETS;
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
