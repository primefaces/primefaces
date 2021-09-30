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
package org.primefaces.integrationtests.datatable;

import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;

@Named
@ApplicationScoped
public class ProgrammingLanguageService {

    public List<ProgrammingLanguage> getLangs() {
        List<ProgrammingLanguage> progLanguages = new ArrayList<>();
        progLanguages.add(new ProgrammingLanguage(1, "Java", 1995, ProgrammingLanguage.ProgrammingLanguageType.COMPILED));
        progLanguages.add(new ProgrammingLanguage(2, "C#", 2000, ProgrammingLanguage.ProgrammingLanguageType.COMPILED));
        progLanguages.add(new ProgrammingLanguage(3, "JavaScript", 1995, ProgrammingLanguage.ProgrammingLanguageType.INTERPRETED));
        progLanguages.add(new ProgrammingLanguage(4, "TypeScript", 2012, ProgrammingLanguage.ProgrammingLanguageType.INTERPRETED));
        progLanguages.add(new ProgrammingLanguage(5, "Python", 1990, ProgrammingLanguage.ProgrammingLanguageType.INTERPRETED));

        return progLanguages;
    }

    public ProgrammingLanguage create(Integer id, String language) {
        return new ProgrammingLanguage(id, language, 1987, ProgrammingLanguage.ProgrammingLanguageType.INTERPRETED);
    }
}
