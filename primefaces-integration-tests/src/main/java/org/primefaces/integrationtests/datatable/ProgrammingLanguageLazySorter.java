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

import java.lang.reflect.Field;
import java.util.Comparator;

import org.primefaces.model.SortMeta;
import org.primefaces.model.SortOrder;

public class ProgrammingLanguageLazySorter implements Comparator<ProgrammingLanguage> {

    private final String sortField;

    private final SortOrder sortOrder;

    public ProgrammingLanguageLazySorter(SortMeta sortMeta) {
        sortField = sortMeta.getField();
        sortOrder = sortMeta.getOrder();
    }

    public ProgrammingLanguageLazySorter(String sortField, SortOrder sortOrder) {
        this.sortField = sortField;
        this.sortOrder = sortOrder;
    }

    @Override
    public int compare(ProgrammingLanguage lang1, ProgrammingLanguage lang2) {
        try {
            Field field = getLangField(sortField);
            Object value1 = field.get(lang1);
            Object value2 = field.get(lang2);

            int value = ((Comparable) value1).compareTo(value2);

            return SortOrder.ASCENDING.equals(sortOrder) ? value : -1 * value;
        }
        catch (Exception e) {
            throw new RuntimeException();
        }
    }

    private Field getLangField(String name) throws NoSuchFieldException {
        Field field = ProgrammingLanguage.class.getDeclaredField(name);
        field.setAccessible(true);
        return field;
    }
}
