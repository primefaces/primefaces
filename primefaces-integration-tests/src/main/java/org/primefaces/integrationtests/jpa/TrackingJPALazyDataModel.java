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
package org.primefaces.integrationtests.jpa;

import org.primefaces.integrationtests.datatable.LazyDataModelCallTracker;
import org.primefaces.model.FilterMeta;
import org.primefaces.model.JPALazyDataModel;
import org.primefaces.model.SortMeta;
import org.primefaces.util.Callbacks.SerializableSupplier;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

import jakarta.faces.context.FacesContext;
import jakarta.persistence.EntityManager;

public class TrackingJPALazyDataModel<T> extends JPALazyDataModel<T> {

    private static final long serialVersionUID = 1L;

    private final String beanName;

    public TrackingJPALazyDataModel(SerializableSupplier<EntityManager> entityManagerSupplier, String beanName,
            Class<T> entityClass, Function<T, Integer> rowKeyProvider) {
        this.beanName = beanName;
        new JPALazyDataModel.Builder<>(this)
                .entityClass(entityClass)
                .entityManager(entityManagerSupplier)
                .rowKeyField("id")
                .rowKeyType(Integer.class)
                .rowKeyProvider(rowKeyProvider::apply)
                .build();
    }

    @Override
    public int count(Map<String, FilterMeta> filterBy) {
        getTracker().recordCountCall();
        return super.count(filterBy);
    }

    @Override
    public List<T> load(int first, int pageSize, Map<String, SortMeta> sortBy,
            Map<String, FilterMeta> filterBy) {
        getTracker().recordLoadCall(first, pageSize);
        return super.load(first, pageSize, sortBy, filterBy);
    }

    @Override
    public T getRowData(String rowKey) {
        getTracker().recordGetRowDataCall(rowKey);
        return super.getRowData(rowKey);
    }

    private LazyDataModelCallTracker getTracker() {
        FacesContext context = FacesContext.getCurrentInstance();
        String expression = "#{" + beanName + ".lazyDataModelCallTracker}";
        return (LazyDataModelCallTracker) context.getApplication().evaluateExpressionGet(context, expression,
                Object.class);
    }
}
