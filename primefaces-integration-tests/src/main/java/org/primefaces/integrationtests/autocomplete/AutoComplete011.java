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
package org.primefaces.integrationtests.autocomplete;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.convert.Converter;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * POJO AutoComplete with forceSelection and two suggestions sharing the same label, see GitHub #15602.
 */
@Named
@ViewScoped
@Data
public class AutoComplete011 implements Serializable {

    @Serial private static final long serialVersionUID = 1L;

    private static final List<Person> PERSONS = List.of(
            new Person(1, "John Smith", "111"),
            new Person(2, "John Smith", "222"));

    private Person selected;

    private final PersonConverter converter = new PersonConverter();

    public List<Person> complete(String query) {
        return PERSONS;
    }

    @Data
    @AllArgsConstructor
    public static class Person implements Serializable {

        @Serial private static final long serialVersionUID = 1L;

        private int id;
        private String name;
        private String taxId;
    }

    public static class PersonConverter implements Converter<Person>, Serializable {

        @Serial private static final long serialVersionUID = 1L;

        @Override
        public Person getAsObject(FacesContext context, UIComponent component, String value) {
            if (value == null || value.isEmpty()) {
                return null;
            }
            int id = Integer.parseInt(value);
            return PERSONS.stream().filter(p -> p.getId() == id).findFirst().orElse(null);
        }

        @Override
        public String getAsString(FacesContext context, UIComponent component, Person value) {
            return value == null ? "" : String.valueOf(value.getId());
        }
    }

}
