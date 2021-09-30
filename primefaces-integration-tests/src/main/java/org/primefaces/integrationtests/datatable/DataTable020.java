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

import lombok.Data;
import org.primefaces.model.FilterMeta;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortMeta;

import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Named
@ViewScoped
@Data
public class DataTable020 implements Serializable {

    private static final long serialVersionUID = -7518459955779385834L;

    private LazyDataModel<Integer> lazyModel;

    @PostConstruct
    public void init() {
        lazyModel = new LazyDataModel<Integer>() {
            @Override
            public String getRowKey(Integer integer) {
                return String.valueOf(integer);
            }

            @Override
            public int count(Map<String, FilterMeta> filterBy) {
                return load(0, 0, null, null).size();
            }

            @Override
            public List<Integer> load(int i, int i1, Map<String, SortMeta> map, Map<String, FilterMeta> map1) {
                return IntStream.of(1, 2, 3, 4, 5)
                        .boxed()
                        .collect(Collectors.toList());
            }
        };
    }

    public boolean isChecked() {
        throw new RuntimeException("Unexpected property read");
    }

    public LazyDataModel<Integer> getLazyModel() {
        return lazyModel;
    }
}
