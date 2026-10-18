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

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;

import lombok.Data;

/**
 * A sorted and filtered DataTable whose value is replaced by an unsorted list, see GitHub #15268.
 */
@Named
@ViewScoped
@Data
public class DataTable056 implements Serializable {

    @Serial private static final long serialVersionUID = 1L;

    private List<ProgrammingLanguage> langs;
    private List<ProgrammingLanguage> filteredLangs;

    @PostConstruct
    public void init() {
        refresh();
    }

    /** reloads the data (unsorted), like a "reload from database" */
    public void refresh() {
        langs = new ArrayList<>();
        langs.add(new ProgrammingLanguage(1, "Java", 1995, ProgrammingLanguage.ProgrammingLanguageType.COMPILED));
        langs.add(new ProgrammingLanguage(2, "Python", 1991, ProgrammingLanguage.ProgrammingLanguageType.INTERPRETED));
        langs.add(new ProgrammingLanguage(3, "C#", 2000, ProgrammingLanguage.ProgrammingLanguageType.COMPILED));
        langs.add(new ProgrammingLanguage(4, "Kotlin", 2011, ProgrammingLanguage.ProgrammingLanguageType.COMPILED));
        langs.add(new ProgrammingLanguage(5, "Ada", 1980, ProgrammingLanguage.ProgrammingLanguageType.COMPILED));
        filteredLangs = new ArrayList<>(langs);
    }

    public void edit(ProgrammingLanguage lang) {
        TestUtils.addMessage("edit", lang.getName());
    }

}
