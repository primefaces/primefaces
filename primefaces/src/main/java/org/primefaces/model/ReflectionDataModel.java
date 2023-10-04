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
package org.primefaces.model;

import java.io.Serializable;
import java.text.Collator;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.faces.FacesException;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;

import org.primefaces.application.PropertyDescriptorResolver;
import org.primefaces.component.api.UITable;
import org.primefaces.context.PrimeApplicationContext;
import org.primefaces.model.filter.FilterConstraint;
import org.primefaces.util.LangUtils;
import org.primefaces.util.LocaleUtils;
import org.primefaces.util.SerializableFunction;
import org.primefaces.util.SerializableSupplier;

public class ReflectionDataModel<T> extends LazyDataModel<T> {

    private SerializableSupplier<List<T>> valuesSupplier;
    private String rowKeyField;
    private Function<T, String> rowKeyProvider;
    private FilterConstraint filter;
    private SerializableFunction<T, Boolean> skipFiltering;
    private Sorter<T> sorter;

    /**
     * For serialization only
     */
    public ReflectionDataModel() {

    }

    @Override
    public int count(Map<String, FilterMeta> filterBy) {
        List<T> values = valuesSupplier.get();
        List<T> filteredValues = filter(values, filterBy);
        return filteredValues.size();
    }

    @Override
    public List<T> load(int first, int pageSize, Map<String, SortMeta> sortBy, Map<String, FilterMeta> filterBy) {
        List<T> values = valuesSupplier.get();
        List<T> filteredValues = filter(values, filterBy);

        sort(sortBy, filteredValues);

        if (pageSize == 0) {
            return filteredValues;
        }

        int last = first + pageSize;
        if (last > filteredValues.size()) {
            last = filteredValues.size();
        }
        return filteredValues.subList(first, last);
    }

    public void sort(Map<String, SortMeta> sortBy, List<T> values) {
        if (values == null || values.isEmpty()) {
            return;
        }

        FacesContext context = FacesContext.getCurrentInstance();
        values.sort(BeanPropertyComparator.reflectionBased(context, (UITable) UIComponent.getCurrentComponent(context), sortBy.values()));

        if (sorter != null) {
            values.sort(sorter);
        }
    }

    public List<T> filter(List<T> values, Map<String, FilterMeta> filterBy) {
        if (values == null || values.isEmpty()) {
            return Collections.emptyList();
        }

        FacesContext context = FacesContext.getCurrentInstance();
        PropertyDescriptorResolver propResolver = PrimeApplicationContext.getCurrentInstance(context).getPropertyDescriptorResolver();
        Locale locale = LocaleUtils.getCurrentLocale(context);

        FilterMeta globalFilter = filterBy.get(FilterMeta.GLOBAL_FILTER_KEY);
        boolean hasGlobalFilter = (globalFilter != null && globalFilter.isActive()) || filter != null;

        return values.stream()
                .filter(obj -> {
                    if (skipFiltering != null && Boolean.TRUE.equals(skipFiltering.apply(obj))) {
                        return true;
                    }

                    boolean localMatch = true;
                    boolean globalMatch = false;

                    if (hasGlobalFilter) {
                        if (globalFilter != null && globalFilter.isActive()) {
                            globalMatch = globalFilter.getConstraint().isMatching(context, obj, globalFilter.getFilterValue(), locale);
                        }
                        if (filter != null && !globalMatch) {
                            globalMatch = filter.isMatching(context, obj, null, locale);
                        }
                    }

                    for (FilterMeta filterMeta : filterBy.values()) {
                        if (filterMeta.getField() == null || filterMeta.getFilterValue() == null || filterMeta.isGlobalFilter()) {
                            continue;
                        }

                        Object fieldValue = propResolver.getValue(obj, filterMeta.getField());

                        Object filterValue = filterMeta.getFilterValue();
                        Object convertedFilterValue;

                        Class<?> filterValueClass = filterValue.getClass();
                        if (filterValueClass.isArray() || Collection.class.isAssignableFrom(filterValueClass)) {
                            convertedFilterValue = filterValue;
                        }
                        else {
                            Class<?> fieldType = propResolver.get(obj.getClass(), filterMeta.getField()).getPropertyType();
                            convertedFilterValue = LangUtils.convertToType(filterValue, fieldType, getClass());
                        }

                        localMatch = filterMeta.getConstraint().isMatching(context, fieldValue, convertedFilterValue, locale);
                        if (!localMatch) {
                            break;
                        }
                    }

                    boolean matches = localMatch;
                    if (hasGlobalFilter) {
                        matches = matches && globalMatch;
                    }
                    return matches;
                })
                .collect(Collectors.toList());
    }

    @Override
    public T getRowData(String rowKey) {
        List<T> values = Objects.requireNonNullElseGet(valuesSupplier.get(), Collections::emptyList);
        for (T obj : values) {
            String currentRowKey  = rowKeyProvider.apply(obj);
            if (Objects.equals(rowKey, currentRowKey)) {
                return obj;
            }
        }

        return null;
    }

    @Override
    public String getRowKey(T obj) {
        return rowKeyProvider.apply(obj);
    }

    public static <T> Builder<T> builder() {
        return new Builder<>();
    }

    public static class Builder<T> {
        final ReflectionDataModel<T> model;

        public Builder() {
            model = new ReflectionDataModel<>();
        }

        public Builder<T> valueSupplier(SerializableSupplier<List<T>> valuesSupplier) {
            model.valuesSupplier = valuesSupplier;
            return this;
        }

        public Builder<T> rowKeyField(String rowKey) {
            model.rowKeyField = rowKey;
            return this;
        }

        public Builder<T> rowKeyProvider(SerializableFunction<T, String> rowKeyProvider) {
            model.rowKeyProvider = rowKeyProvider;
            return this;
        }

        public Builder<T> rowKeyConverter(Converter<T> rowKeyConverter) {
            model.rowKeyConverter = rowKeyConverter;
            return this;
        }

        public Builder<T> filter(FilterConstraint filter) {
            model.filter = filter;
            return this;
        }

        public Builder<T> skipFiltering(SerializableFunction<T, Boolean> skipFiltering) {
            model.skipFiltering = skipFiltering;
            return this;
        }

        public Builder<T> sorter(Sorter<T> sorter) {
            model.sorter = sorter;
            return this;
        }

        public ReflectionDataModel<T> build() {
            Objects.requireNonNull(model.valuesSupplier, "Value supplier not set");

            if (model.rowKeyProvider == null) {
                if (model.rowKeyConverter != null) {
                    model.rowKeyProvider = model::getRowKeyFromConverter;
                }
                else {
                    Objects.requireNonNull(model.rowKeyField, "rowKeyField is mandatory if no rowKeyProvider nor converter is provided");
                    model.rowKeyProvider = obj -> {
                        PrimeApplicationContext primeAppContext =
                                PrimeApplicationContext.getCurrentInstance(FacesContext.getCurrentInstance());
                        Object rowKeyValue = primeAppContext.getPropertyDescriptorResolver().getValue(obj, model.rowKeyField);
                        return Objects.toString(rowKeyValue, null);
                    };
                }
            }
            return model;
        }
    }

    @FunctionalInterface
    public interface Sorter<T> extends Comparator<T>, Serializable {

    }
}
