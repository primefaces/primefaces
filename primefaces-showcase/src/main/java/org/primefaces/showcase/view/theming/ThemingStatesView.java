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
package org.primefaces.showcase.view.theming;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

import jakarta.faces.component.UIInput;
import jakarta.faces.event.ComponentSystemEvent;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;

import lombok.Getter;
import lombok.Setter;

/**
 * Backing bean of the theming states section (theming/sections/states.xhtml): the same values for every state.
 */
@Named
@ViewScoped
@Getter
@Setter
public class ThemingStatesView implements Serializable {

    private static final long serialVersionUID = 1L;

    private String text = "PrimeFaces";
    private String longText = "Every color, size and spacing comes from a design token.";
    private Double amount = 1234.56;
    private String mask = "(555) 123-4567";
    private String password = "Secret#2026";
    private String otp = "1234";
    private String phone = "+12015550123";
    private String fruit = "Apple";
    private List<String> chips = new ArrayList<>(List.of("Aura", "Lara"));
    private LocalDate date = LocalDate.now();
    private List<String> cities = List.of("Amsterdam", "Berlin", "London", "Paris", "Rome", "Tokyo");
    private String city = "Paris";
    private List<String> selectedCities = new ArrayList<>(List.of("Paris", "Rome"));
    private String cascadeCity = "Berlin";
    private String listCity = "Berlin";
    private int spinner = 5;
    private int slider = 40;
    private Integer rating = 3;
    private boolean toggle = true;
    private boolean checkbox = true;
    private String radio = "Option 1";
    private String button = "Day";
    private String color = "#10b981";

    /**
     * Marks the input as invalid while rendering if its "invalid" attribute is true, to show the invalid state without a
     * submit. An invalid input renders its submitted value, so the value is set as the browser would submit it.
     *
     * @param event the preRenderComponent event of an input component
     */
    public void markInvalid(ComponentSystemEvent event) {
        if (event.getComponent() instanceof UIInput input && Boolean.TRUE.equals(input.getAttributes().get("invalid"))) {
            if (input.getSubmittedValue() == null) {
                input.setSubmittedValue(asSubmitted(input.getValue()));
            }
            input.setValid(false);
        }
    }

    private static Object asSubmitted(Object value) {
        if (value instanceof Collection<?> values) {
            return values.stream().map(String::valueOf).toArray(String[]::new);
        }
        if (value instanceof LocalDate date) {
            return date.format(DateTimeFormatter.ofPattern("MM/dd/yyyy", Locale.US));
        }
        return value == null ? null : String.valueOf(value);
    }

    /**
     * Suggestions for the autoComplete.
     *
     * @param query the text typed by the user
     * @return fruits containing the query
     */
    public List<String> completeFruit(String query) {
        String q = query == null ? "" : query.toLowerCase();
        return List.of("Apple", "Apricot", "Banana", "Cherry", "Grape", "Lemon", "Mango", "Orange", "Peach", "Pear").stream()
                .filter(fruit -> fruit.toLowerCase().contains(q))
                .toList();
    }
}
