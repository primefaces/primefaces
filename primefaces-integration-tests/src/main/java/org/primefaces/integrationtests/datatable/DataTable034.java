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

import org.primefaces.model.FilterMeta;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortMeta;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.enterprise.context.SessionScoped;
import jakarta.enterprise.inject.spi.CDI;
import jakarta.inject.Named;

import lombok.Data;
import lombok.Getter;

@Named
@RequestScoped
@Data
public class DataTable034 implements Serializable {

    private static final long serialVersionUID = -7518459955779385834L;

    protected MyLazyDataModel lazyDataModel;

    @PostConstruct
    public void init() {
        lazyDataModel = new MyLazyDataModel();
    }

    public static class MyLazyDataModel extends LazyDataModel<ProgrammingLanguage> implements Serializable {
        @Override
        public int count(Map<String, FilterMeta> filterMeta) {
            return CDI.current().select(MyDataStorage.class).get().getLangs().size();
        }

        @Override
        public List<ProgrammingLanguage> load(int first, int pageSize, Map<String, SortMeta> sortMeta, Map<String, FilterMeta> filterMeta) {
            return CDI.current().select(MyDataStorage.class).get().getLangs();
        }
    }

    @SessionScoped
    public static class MyDataStorage implements Serializable {

        @Getter
        private ArrayList<ProgrammingLanguage> langs;

        @PostConstruct
        public void init() {
            langs = new ArrayList<>();
            for (int i = 1; i <= 75; i++) {
                langs.add(new ProgrammingLanguage(i, "Language " + i, 1990 + (i % 10), ProgrammingLanguage.ProgrammingLanguageType.COMPILED));
            }
        }
    }
}
