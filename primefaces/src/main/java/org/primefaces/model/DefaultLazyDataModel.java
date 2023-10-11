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
import java.util.*;
import java.util.stream.Collectors;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;

import org.primefaces.component.api.UITable;
import org.primefaces.context.PrimeApplicationContext;
import org.primefaces.model.filter.FilterConstraint;
import org.primefaces.util.*;

public class DefaultLazyDataModel<T> extends LazyDataModel<T> {

    private String rowKeyField;
    private FilterConstraint filter;
    private Sorter<T> sorter;
    private SerializableSupplier<List<T>> valuesSupplier;
    private SerializableFunction<T, Object> rowKeyProvider;
    private SerializablePredicate<T> skipFiltering;

    /**
     * For serialization only
     */
    public DefaultLazyDataModel() {
        // NOOP
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

    protected void sort(Map<String, SortMeta> sortBy, List<T> values) {
        if (values == null || values.isEmpty()) {
            return;
        }

        FacesContext context = FacesContext.getCurrentInstance();
        values.sort(SortMetaComparator.reflectionBased(context, (UITable) UIComponent.getCurrentComponent(context), sortBy.values()));

        if (sorter != null) {
            values.sort(sorter);
        }
    }

    protected List<T> filter(List<T> values, Map<String, FilterMeta> filterBy) {
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
                    if (skipFiltering != null && Boolean.TRUE.equals(skipFiltering.test(obj))) {
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
                        Object convertedFilterValue = null;

                        if (fieldValue != null) {
                            Class<?> filterValueClass = filterValue.getClass();
                            if (filterValueClass.isArray() || Collection.class.isAssignableFrom(filterValueClass)) {
                                convertedFilterValue = filterValue;
                            }
                            else {
                                convertedFilterValue = ComponentUtils.convertToType(filterValue, fieldValue.getClass(), getClass());
                            }
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
            if (Objects.equals(rowKey, getRowKey(obj))) {
                return obj;
            }
        }

        return null;
    }

    @Override
    public String getRowKey(T obj) {
        return String.valueOf(rowKeyProvider.apply(obj));
    }

    public static <T> Builder<T> builder() {
        return new Builder<>();
    }

    public static class Builder<T> {
        final DefaultLazyDataModel<T> model;

        public Builder() {
            model = new DefaultLazyDataModel<>();
        }

        public Builder<T> valueSupplier(SerializableSupplier<List<T>> valuesSupplier) {
            model.valuesSupplier = valuesSupplier;
            return this;
        }

        public Builder<T> rowKeyField(String rowKey) {
            model.rowKeyField = rowKey;
            return this;
        }

        public Builder<T> rowKeyProvider(SerializableFunction<T, Object> rowKeyProvider) {
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

        public Builder<T> skipFiltering(SerializablePredicate<T> skipFiltering) {
            model.skipFiltering = skipFiltering;
            return this;
        }

        public Builder<T> sorter(Sorter<T> sorter) {
            model.sorter = sorter;
            return this;
        }

        public DefaultLazyDataModel<T> build() {
            Objects.requireNonNull(model.valuesSupplier, "Value supplier not set");

            boolean requiresRowKeyProvider = model.rowKeyProvider == null && (model.rowKeyConverter != null || model.rowKeyField != null);
            if (requiresRowKeyProvider) {
                if (model.rowKeyConverter != null) {
                    model.rowKeyProvider = model::getRowKeyFromConverter;
                }
                else {
                    Objects.requireNonNull(model.rowKeyField, "rowKeyField is mandatory if no rowKeyProvider nor converter is provided");

                    PropertyDescriptorResolver propResolver =
                            PrimeApplicationContext.getCurrentInstance(FacesContext.getCurrentInstance()).getPropertyDescriptorResolver();
                    model.rowKeyProvider = obj -> propResolver.getValue(obj, model.rowKeyField);
                }
            }
            return model;
        }
    }

    @FunctionalInterface
    public interface Sorter<T> extends Comparator<T>, Serializable {

    }
}
