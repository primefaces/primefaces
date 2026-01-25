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
package org.primefaces.integrationtests.datatable;

import org.primefaces.model.SortMeta;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;

import lombok.Data;

@Named
@ViewScoped
@Data
public class DataTable042 implements Serializable {

    private static final long serialVersionUID = -9070796086139839567L;

    @lombok.Data
    @lombok.AllArgsConstructor
    public static class Data implements Serializable {

        private static final long serialVersionUID = 1L;
        private String textA;
        private String textB;
    }

    private List<Data> data;

    @PostConstruct
    public void refresh() {
        List<Data> newData = new ArrayList<>();
        newData.add(new Data("A1" , "B1"));
        newData.add(new Data("" , ""));
        newData.add(new Data("A3" , "B3"));

        this.data = newData;
    }

    public int legacySort(Object a, Object b, SortMeta sortMeta) {
        return String.CASE_INSENSITIVE_ORDER.compare((String) a, (String) b);
    }

    public int modernSort(Object a, Object b, SortMeta sortMeta) {
        return sortMeta.getNullSortOrder() * String.CASE_INSENSITIVE_ORDER.compare((String) a, (String) b);
    }

}
