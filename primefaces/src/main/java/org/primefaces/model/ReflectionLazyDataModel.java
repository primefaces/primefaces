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

import org.primefaces.model.filter.FilterConstraint;
import org.primefaces.util.SerializableFunction;
import org.primefaces.util.SerializableSupplier;

import javax.faces.FacesException;
import javax.faces.context.FacesContext;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.text.Collator;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.faces.convert.Converter;
import org.primefaces.util.LocaleUtils;

public class ReflectionLazyDataModel<T> extends AbstractLazyDataModel<T> {

    // TODO AppScoped
    protected static ConcurrentHashMap<String, Map<String, PropertyDescriptor>> pdCache = new ConcurrentHashMap<>();

    private SerializableSupplier<List<T>> valuesSupplier;
    private String rowKey;
    private Function<T, String> rowKeyProvider;
    private FilterConstraint filter;
    private SerializableFunction<T, Boolean> skipFiltering;
    private Sorter<T> sorter;

    /**
     * For serialization only
     */
    public ReflectionLazyDataModel() {

    }

    public ReflectionLazyDataModel(SerializableSupplier<List<T>> valuesSupplier) {
        this.valuesSupplier = valuesSupplier;
    }

    @Override
    public int count(Map<String, FilterMeta> filterBy) {
        List<T> values = this.valuesSupplier.get();
        List<T> filteredValues = filter(values, filterBy);

        return filteredValues.size();
    }

    @Override
    public List<T> load(int first, int pageSize, Map<String, SortMeta> sortBy, Map<String, FilterMeta> filterBy) {
        List<T> values = this.valuesSupplier.get();
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
        Locale locale = LocaleUtils.getCurrentLocale(context);
        Collator collator = Collator.getInstance(locale);

        values.sort((obj1, obj2) -> {
            for (SortMeta sortMeta : sortBy.values()) {
                Object value1 = getFieldValue(obj1, sortMeta.getField());
                Object value2 = getFieldValue(obj2, sortMeta.getField());

                try {
                    int result;

                    if (sortMeta.getFunction() == null) {
                        //Empty check
                        if (value1 == null && value2 == null) {
                            result = 0;
                        }
                        else if (value1 == null) {
                            result = sortMeta.getNullSortOrder();
                        }
                        else if (value2 == null) {
                            result = -1 * sortMeta.getNullSortOrder();
                        }
                        else if (value1 instanceof String && value2 instanceof String) {
                            if (sortMeta.isCaseSensitiveSort()) {
                                result = collator.compare(value1, value2);
                            }
                            else {
                                String str1 = (((String) value1).toLowerCase(locale));
                                String str2 = (((String) value2).toLowerCase(locale));

                                result = collator.compare(str1, str2);
                            }
                        }
                        else {
                            result = ((Comparable<Object>) value1).compareTo(value2);
                        }
                    }
                    else {
                        result = (Integer) sortMeta.getFunction().invoke(context.getELContext(), new Object[] {value1, value2});
                    }

                    return sortMeta.getOrder().isAscending() ? result : -1 * result;
                }
                catch (Exception e) {
                    throw new FacesException(e);
                }
            }
            return 0;
        });

        if (sorter != null) {
            values.sort(sorter);
        }
    }

    public List<T> filter(List<T> values, Map<String, FilterMeta> filterBy) {
        if (values == null || values.isEmpty()) {
            return Collections.emptyList();
        }

        FacesContext context = FacesContext.getCurrentInstance();
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
                    if (filterMeta.isGlobalFilter() || !filterMeta.isActive()) {
                        continue;
                    }

                    Object fieldValue = getFieldValue(obj, filterMeta.getField());
                    Object filterValue = filterMeta.getFilterValue();
                    Object convertedFilterValue;

                    Class<?> filterValueClass = filterValue.getClass();
                    if (filterValueClass.isArray() || Collection.class.isAssignableFrom(filterValueClass)) {
                        convertedFilterValue = filterValue;
                    }
                    else {
                        convertedFilterValue = convertToType(filterValue, getFieldType(fieldValue, filterMeta.getField()));
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

    protected Class<?> getFieldType(Object obj, String fieldName) {
        // join if required; e.g. company.name -> join to company and get "name" field from the joined object
        while (fieldName.contains(".")) {
            String currentName = fieldName.substring(0, fieldName.indexOf("."));
            fieldName = fieldName.substring(currentName.length() + 1);

            try {
                obj = getPropertyDescriptor(obj.getClass(), fieldName).getReadMethod().invoke(obj);
            }
            catch (IllegalAccessException | InvocationTargetException e) {
                throw new FacesException(e);
            }
        }

        return getPropertyDescriptor(obj.getClass(), fieldName).getReadMethod().getReturnType();
    }

    protected Object getFieldValue(Object obj, String fieldName) {
        // join if required; e.g. company.name -> join to company and get "name" field from the joined object
        while (fieldName.contains(".")) {
            String currentName = fieldName.substring(0, fieldName.indexOf("."));
            fieldName = fieldName.substring(currentName.length() + 1);

            try {
                obj = getPropertyDescriptor(obj.getClass(), fieldName).getReadMethod().invoke(obj);
            }
            catch (IllegalAccessException | InvocationTargetException e) {
                throw new FacesException(e);
            }
        }

        try {
            return getPropertyDescriptor(obj.getClass(), fieldName).getReadMethod().invoke(obj);
        }
        catch (IllegalAccessException | InvocationTargetException e) {
            throw new FacesException(e);
        }
    }

    @Override
    public T getRowData(String rowKey) {
        Converter<T> rowKeyConverter = getRowKeyConverter();
        if (rowKeyConverter != null) {
            return super.getRowData(rowKey);
        }

        if (this.rowKeyProvider != null || this.rowKey != null) {
            List<T> values = this.valuesSupplier.get();
            if (values == null || values.isEmpty()) {
                return null;
            }

            for (T obj : values) {
                String currentRowKey;
                if (this.rowKeyProvider != null) {
                    currentRowKey = this.rowKeyProvider.apply(obj);
                }
                else {
                    Object temp = getFieldValue(obj, this.rowKey);
                    currentRowKey = temp == null ? null : temp.toString();
                }

                if (Objects.equals(rowKey, currentRowKey)) {
                    return obj;
                }
            }

            return null;
        }

        throw new UnsupportedOperationException(
                getMessage("Provide a rowKey / rowKey-Provider / rowKey-Converter or implement getRowData(String rowKey) in %s"
                        + ", when basic rowKey algorithm is not used [component=%s,view=%s]."));
    }

    @Override
    public String getRowKey(T obj) {
        Converter<T> rowKeyConverter = getRowKeyConverter();
        if (rowKeyConverter != null) {
            return super.getRowKey(obj);
        }

        if (this.rowKeyProvider != null) {
            return rowKeyProvider.apply(obj);
        }

        if (this.rowKey != null) {
            Object rowKeyValue = getFieldValue(obj, rowKey);
            return rowKeyValue == null ? null : rowKeyValue.toString();
        }

        throw new UnsupportedOperationException(
                getMessage("Provide a rowKey / rowKey-Provider / rowKey-Converter or implement getRowKey(T object) in %s"
                        + ", when basic rowKey algorithm is not used [component=%s,view=%s]."));
    }

    protected PropertyDescriptor getPropertyDescriptor(Class<?> clazz, String fieldName) {
        // use classname instead of clazz, its faster
        Map<String, PropertyDescriptor> classCache = pdCache.get(clazz.getClass().getName());

        // dont use computeIfAbsent, its bit slower
        if (classCache == null) {
            classCache = new ConcurrentHashMap<>();
            pdCache.put(clazz.getClass().getName(), classCache);
        }

        PropertyDescriptor pd = classCache.get(fieldName);
        if (pd == null) {
            try {
                pd = new PropertyDescriptor(fieldName, clazz);
                classCache.put(fieldName, pd);
            }
            catch (IntrospectionException e) {
                throw new FacesException(e);
            }
        }

        return pd;
    }

    public String getRowKey() {
        return rowKey;
    }

    public void setRowKey(String rowKey) {
        this.rowKey = rowKey;
    }

    public Function<T, String> getRowKeyProvider() {
        return rowKeyProvider;
    }

    public void setRowKeyProvider(Function<T, String> rowKeyProvider) {
        this.rowKeyProvider = rowKeyProvider;
    }

    public FilterConstraint getFilter() {
        return filter;
    }

    public void setFilter(FilterConstraint filter) {
        this.filter = filter;
    }

    public SerializableFunction<T, Boolean> getSkipFiltering() {
        return skipFiltering;
    }

    public void setSkipFiltering(SerializableFunction<T, Boolean> skipFiltering) {
        this.skipFiltering = skipFiltering;
    }

    public Sorter<T> getSorter() {
        return sorter;
    }

    public void setSorter(Sorter<T> sorter) {
        this.sorter = sorter;
    }



    public static <T> Builder<T> builder(SerializableSupplier<List<T>> valuesSupplier) {
        return new Builder<>(valuesSupplier);
    }

    public static class Builder<T> {
        private final SerializableSupplier<List<T>> valuesSupplier;
        private String rowKey;
        private SerializableFunction<T, String> rowKeyProvider;
        private Converter<T> rowKeyConverter;
        private FilterConstraint filter;
        private SerializableFunction<T, Boolean> skipFiltering;
        private Sorter<T> sorter;

        public Builder(SerializableSupplier<List<T>> valuesSupplier) {
            this.valuesSupplier = valuesSupplier;
        }

        public Builder<T> rowKey(String rowKey) {
            this.rowKey = rowKey;
            return this;
        }

        public Builder<T> rowKeyProvider(SerializableFunction<T, String> rowKeyProvider) {
            this.rowKeyProvider = rowKeyProvider;
            return this;
        }

        public Builder<T> rowKeyConverter(Converter<T> rowKeyConverter) {
            this.rowKeyConverter = rowKeyConverter;
            return this;
        }

        public Builder<T> filter(FilterConstraint filter) {
            this.filter = filter;
            return this;
        }

        public Builder<T> skipFiltering(SerializableFunction<T, Boolean> skipFiltering) {
            this.skipFiltering = skipFiltering;
            return this;
        }

        public Builder<T> sorter(Sorter<T> sorter) {
            this.sorter = sorter;
            return this;
        }

        public ReflectionLazyDataModel<T> build() {
            ReflectionLazyDataModel<T> model = new ReflectionLazyDataModel<>(valuesSupplier);
            model.rowKey = rowKey;
            model.rowKeyProvider = rowKeyProvider;
            model.filter = filter;
            model.skipFiltering = skipFiltering;
            model.sorter = sorter;
            model.setRowKeyConverter(rowKeyConverter);

            return model;
        }
    }

    @FunctionalInterface
    public static interface Sorter<T> extends Comparator<T>, Serializable {

    }
}