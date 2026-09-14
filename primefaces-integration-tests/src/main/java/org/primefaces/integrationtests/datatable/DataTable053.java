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
package org.primefaces.integrationtests.datatable;

import org.primefaces.integrationtests.general.utilities.TestUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;

import lombok.Data;

/**
 * A collection based filter which is bound to a filter facet, see GitHub #12885.
 */
@Named
@ViewScoped
@Data
public class DataTable053 implements Serializable {

    private static final long serialVersionUID = 1L;

    private List<ProgrammingLanguage> langs;

    /** the filter value, an empty collection means "no filter", it must not become null */
    private List<ProgrammingLanguage.ProgrammingLanguageType> selectedTypes = new ArrayList<>();

    @PostConstruct
    public void init() {
        langs = new ArrayList<>();
        langs.add(new ProgrammingLanguage(1, "Java", 1995, ProgrammingLanguage.ProgrammingLanguageType.COMPILED));
        langs.add(new ProgrammingLanguage(2, "Python", 1991, ProgrammingLanguage.ProgrammingLanguageType.INTERPRETED));
        langs.add(new ProgrammingLanguage(3, "C#", 2000, ProgrammingLanguage.ProgrammingLanguageType.COMPILED));
    }

    public List<ProgrammingLanguage.ProgrammingLanguageType> getTypes() {
        return Arrays.asList(ProgrammingLanguage.ProgrammingLanguageType.values());
    }

    public void showFilter() {
        String value = selectedTypes == null
                ? "null"
                : selectedTypes.stream().map(t -> t == null ? "null" : t.name()).collect(Collectors.joining(","));
        TestUtils.addMessage("filter", "[" + value + "]");
    }

}
