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
package org.primefaces.showcase.view.app;

import javax.enterprise.context.SessionScoped;
import javax.inject.Named;

import org.primefaces.showcase.domain.Country;

import java.io.Serializable;
import java.util.Locale;

@Named
@SessionScoped
public class App implements Serializable {

	private static final long serialVersionUID = 1L;
	private String theme = "saga";
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
        return this.inputStyle.equals("filled") ? "ui-input-filled" : "";
    }

	public Country getLocale() {
		return locale;
	}

	public void setLocale(Country locale) {
		this.locale = locale;
	}

    public void changeTheme(String theme, boolean darkMode) {
        this.theme = theme;
        this.darkMode = darkMode;
    }

    public String getThemeImage() {
    	String result = getTheme();
    	switch (result) {
		case "nova-light":
			result = "nova.png";
			break;
		case "nova-colored":
			result = "nova-accent.png";
			break;
		case "nova-dark":
			result = "nova-alt.png";
			break;
		case "bootstrap4-blue-light":
            result = "bootstrap4-light-blue.svg";
            break;
        case "bootstrap4-blue-dark":
            result = "bootstrap4-dark-blue.svg";
            break;
        case "bootstrap4-purple-light":
            result = "bootstrap4-light-purple.svg";
            break;
        case "bootstrap4-purple-dark":
            result = "bootstrap4-dark-purple.svg";
            break;
        default:
            result += ".png";
			break;
		}
    	return result;
    }
}
