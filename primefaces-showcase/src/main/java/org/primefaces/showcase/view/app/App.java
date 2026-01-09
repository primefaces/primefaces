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
package org.primefaces.showcase.view.app;

import org.primefaces.context.PrimeApplicationContext;
import org.primefaces.showcase.domain.Country;

import java.io.Serializable;
import java.util.Locale;

import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;

@Named
@SessionScoped
public class App implements Serializable {

    @Inject private Themes themes;

    private String theme = "saga-blue";
    private boolean darkMode = false;
    private String inputStyle = "outlined";
    private Country locale = new Country(0, Locale.US);

    public String getTheme() {
        return theme;
    }

    public boolean isDarkMode() {
        return darkMode;
    }

    public void setDarkMode(boolean darkMode) {
        this.darkMode = darkMode;
    }

    public void setTheme(String theme) {
        this.theme = theme;
    }

    public String getInputStyle() {
        return inputStyle;
    }

    public void setInputStyle(String inputStyle) {
        this.inputStyle = inputStyle;
    }

    public String getInputStyleClass() {
        return "filled".equals(this.inputStyle) ? "ui-input-filled" : "";
    }

    public Country getLocale() {
        return locale;
    }

    public void setLocale(Country locale) {
        this.locale = locale;
    }

    public void changeTheme(Themes.Theme theme) {
        this.theme = theme.getId();
        this.darkMode = theme.isDark();
    }

    public String getThemeName() {
        for (Themes.Theme theme : themes.getThemes()) {
            if (theme.getId().equals(this.getTheme())) {
                return theme.getName();
            }
        }
        return null;
    }

    public String getThemeImage() {
        for (Themes.Theme theme : themes.getThemes()) {
            if (theme.getId().equals(this.getTheme())) {
                return theme.getImage();
            }
        }
        return null;
    }

    public String getPrimeFacesVersion() {
        return PrimeApplicationContext.getCurrentInstance(FacesContext.getCurrentInstance()).getEnvironment().getBuildVersion();
    }
}
