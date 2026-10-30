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
package org.primefaces.showcase.view.input;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.convert.Converter;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;

@Named
@ViewScoped
public class TriStateManyCheckboxView implements Serializable {

    public enum Preference {
        NEUTRAL, LIKE, DISLIKE
    }

    private List<String> toppings;
    private Map<String, String> basic;
    private Map<String, String> vertical;
    private Map<String, String> ajax;
    private Map<String, Preference> converted;
    private Map<String, String> required;

    @PostConstruct
    public void init() {
        toppings = List.of("Cheese", "Mushroom", "Olives", "Onion", "Pepperoni");

        basic = new LinkedHashMap<>();
        basic.put("Cheese", "1");
        basic.put("Onion", "2");

        vertical = new LinkedHashMap<>(basic);
        ajax = new LinkedHashMap<>();

        converted = new LinkedHashMap<>();
        converted.put("Mushroom", Preference.LIKE);
        converted.put("Pepperoni", Preference.DISLIKE);

        required = new LinkedHashMap<>();
    }

    public void onChange() {
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO, "State changed", String.valueOf(ajax)));
    }

    public void submit() {
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO, "Submitted", String.valueOf(required)));
    }

    /**
     * Converts the states (0, 1, 2) to a {@link Preference} and back.
     */
    public Converter<Preference> getPreferenceConverter() {
        return new Converter<>() {
            @Override
            public Preference getAsObject(FacesContext context, UIComponent component, String value) {
                return Preference.values()[Integer.parseInt(value)];
            }

            @Override
            public String getAsString(FacesContext context, UIComponent component, Preference value) {
                return String.valueOf(value.ordinal());
            }
        };
    }

    public List<String> getToppings() {
        return toppings;
    }

    public Map<String, String> getBasic() {
        return basic;
    }

    public void setBasic(Map<String, String> basic) {
        this.basic = basic;
    }

    public Map<String, String> getVertical() {
        return vertical;
    }

    public void setVertical(Map<String, String> vertical) {
        this.vertical = vertical;
    }

    public Map<String, String> getAjax() {
        return ajax;
    }

    public void setAjax(Map<String, String> ajax) {
        this.ajax = ajax;
    }

    public Map<String, Preference> getConverted() {
        return converted;
    }

    public void setConverted(Map<String, Preference> converted) {
        this.converted = converted;
    }

    public Map<String, String> getRequired() {
        return required;
    }

    public void setRequired(Map<String, String> required) {
        this.required = required;
    }
}
