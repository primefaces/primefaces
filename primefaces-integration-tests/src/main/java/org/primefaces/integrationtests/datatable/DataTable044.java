/*
 * The MIT License
 *
 * Copyright (c) 2009-2023 PrimeTek Informatics
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

import lombok.Getter;
import lombok.Setter;
import org.primefaces.model.DefaultLazyDataModel;
import org.primefaces.model.LazyDataModel;

import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;

@Named
@ViewScoped
public class DataTable044 implements Serializable {
    @Getter @Setter private String programmingLanguageFilter;
    @Getter private LazyDataModel<ProgrammingLanguage> lazyDataModel;

    @Inject private ProgrammingLanguageService service;

    @PostConstruct
    public void init() {
        lazyDataModel = DefaultLazyDataModel.<ProgrammingLanguage>builder()
                .valueSupplier(service::getLangs)
                .filter((ctx, value, filter, locale) -> {
                    if (programmingLanguageFilter == null) {
                        return true;
                    }

                    return ((ProgrammingLanguage) value).getName().contains(programmingLanguageFilter);
                }).build();
    }
}
