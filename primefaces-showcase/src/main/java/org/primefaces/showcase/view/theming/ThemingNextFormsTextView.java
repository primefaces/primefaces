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
import java.util.ArrayList;
import java.util.List;

import jakarta.faces.component.UIInput;
import jakarta.faces.event.ComponentSystemEvent;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;

import lombok.Getter;
import lombok.Setter;

/**
 * Backing bean of the theming-next/forms-text.xhtml demo page.
 */
@Named
@ViewScoped
@Getter
@Setter
public class ThemingNextFormsTextView implements Serializable {

    private static final long serialVersionUID = 1L;

    private String text = "PrimeFaces";
    private String emptyText;
    private String longText = "Theming next: every color, size and spacing comes from a design token.";
    private String mask = "(555) 123-4567";
    private Double amount = 1234.56;
    private String password = "Secret#2026";
    private List<String> chips = new ArrayList<>(List.of("Aura", "Lara", "Nora"));
    private List<String> emptyChips = new ArrayList<>();
    private String color = "#10b981";
    private String colorInline = "#6366f1";
    private String keyboard;
    private Integer rating = 3;
    private Integer ratingEmpty;
    private int slider = 40;
    private int sliderVertical = 60;
    private int rangeFrom = 20;
    private int rangeTo = 80;
    private int spinner = 5;
    private double spinnerDecimal = 2.5;
    private String signature;
    private String editor = "<p>Hello <strong>PrimeFaces</strong>, this is the <em>text editor</em>.</p>";
    private int knob = 60;
    private String cascade;
    private String autoText = "Apple";
    private List<String> autoTexts = new ArrayList<>(List.of("Apple", "Banana"));

    /**
     * Marks the input as invalid while rendering, to show the invalid state without a submit.
     *
     * @param event the preRenderComponent event of an input component
     */
    public void markInvalid(ComponentSystemEvent event) {
        if (event.getComponent() instanceof UIInput input) {
            input.setValid(false);
        }
    }

    /**
     * Simple suggestions for the autoComplete demos.
     *
     * @param query the text typed by the user
     * @return fruits containing the query
     */
    public List<String> completeFruit(String query) {
        String q = query == null ? "" : query.toLowerCase();
        List<String> result = new ArrayList<>();
        for (String fruit : List.of("Apple", "Apricot", "Avocado", "Banana", "Blackberry", "Blueberry", "Cherry", "Coconut",
                "Grape", "Kiwi", "Lemon", "Lime", "Mango", "Melon", "Orange", "Papaya", "Peach", "Pear", "Pineapple", "Plum")) {
            if (fruit.toLowerCase().contains(q)) {
                result.add(fruit);
            }
        }
        return result;
    }
}
