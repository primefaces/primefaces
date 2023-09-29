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

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.text.Collator;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import javax.faces.FacesException;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;

import org.primefaces.context.PrimeApplicationContext;
import org.primefaces.model.filter.FilterConstraint;
import org.primefaces.util.LangUtils;
import org.primefaces.util.LocaleUtils;
import org.primefaces.util.SerializableFunction;
import org.primefaces.util.SerializableSupplier;

public class ReflectionLazyDataModel<T> extends LazyDataModel<T> {

    private SerializableSupplier<List<T>> valuesSupplier;
    private String rowKey;
    private Function<T, String> rowKeyProvider;
    private FilterConstraint filter;
    private SerializableFunction<T, Boolean> skipFiltering;
    private Sorter<T> sorter;

    private final Pattern NESTED_EXPRESSION_PATTERN = Pattern.compile("\\.");

    /**
     * For serialization only
     */
    public ReflectionLazyDataModel() {

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
        PrimeApplicationContext primeApplicationContext = PrimeApplicationContext.getCurrentInstance(context);
        Locale locale = LocaleUtils.getCurrentLocale(context);
        Collator collator = Collator.getInstance(locale);

        values.sort((obj1, obj2) -> {
            for (SortMeta sortMeta : sortBy.values()) {
                Object value1 = getFieldValue(primeApplicationContext, obj1, sortMeta.getField());
                Object value2 = getFieldValue(primeApplicationContext, obj2, sortMeta.getField());

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
                        result = (Integer) sortMeta.getFunction().invoke(context.getELContext(), new Object[]{value1, value2});
                    }

                    return sortMeta.getOrder().isAscending() ? result : -1 * result;
                } catch (Exception e) {
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
        PrimeApplicationContext primeApplicationContext = PrimeApplicationContext.getCurrentInstance(context);
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

                        Object fieldValue = getFieldValue(primeApplicationContext, obj, filterMeta.getField());
                        Object filterValue = filterMeta.getFilterValue();
                        Object convertedFilterValue;

                        Class<?> filterValueClass = filterValue.getClass();
                        if (filterValueClass.isArray() || Collection.class.isAssignableFrom(filterValueClass)) {
                            convertedFilterValue = filterValue;
                        }
                        else {
                            Class<?> fieldType = getFieldType(primeApplicationContext, fieldValue, filterMeta.getField());
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

    protected Class<?> getFieldType(PrimeApplicationContext primeApplicationContext, Object obj, String fieldName) {
        // join if required; e.g. company.name -> join to company and get "name" field from the joined object
        while (fieldName.contains(".")) {
            String currentName = fieldName.substring(0, fieldName.indexOf("."));
            fieldName = fieldName.substring(currentName.length() + 1);

            try {
                obj = getPropertyDescriptor(primeApplicationContext, obj.getClass(), fieldName).getReadMethod().invoke(obj);
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new FacesException(e);
            }
        }

        return getPropertyDescriptor(primeApplicationContext, obj.getClass(), fieldName).getReadMethod().getReturnType();
    }

    protected Object getFieldValue(PrimeApplicationContext primeApplicationContext, Object obj, String fieldName) {
        // join if required; e.g. company.name -> join to company and get "name" field from the joined object
        while (fieldName.contains(".")) {
            String currentName = fieldName.substring(0, fieldName.indexOf("."));
            fieldName = fieldName.substring(currentName.length() + 1);

            try {
                obj = getPropertyDescriptor(primeApplicationContext, obj.getClass(), fieldName).getReadMethod().invoke(obj);
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new FacesException(e);
            }
        }

        try {
            return getPropertyDescriptor(primeApplicationContext, obj.getClass(), fieldName).getReadMethod().invoke(obj);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new FacesException(e);
        }
    }

    private PropertyDescriptor foo(PrimeApplicationContext primeAppContext, Object obj, String expression) {
        String[] fields = NESTED_EXPRESSION_PATTERN.split(expression);
        for (int i = 0; i < fields.length -1; i++) {
            obj = getPropertyDescriptor(primeAppContext, obj.getClass(), fields[i]).getReadMethod().invoke(obj);
        }

        String last = fields[fields.length - 1];
        return getPropertyDescriptor(primeAppContext, obj.getClass(), last).getReadMethod().invoke(obj);
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

            PrimeApplicationContext primeApplicationContext =
                    PrimeApplicationContext.getCurrentInstance(FacesContext.getCurrentInstance());

            for (T obj : values) {
                String currentRowKey;
                if (this.rowKeyProvider != null) {
                    currentRowKey = this.rowKeyProvider.apply(obj);
                }
                else {
                    Object temp = getFieldValue(primeApplicationContext, obj, this.rowKey);
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
            PrimeApplicationContext primeApplicationContext =
                    PrimeApplicationContext.getCurrentInstance(FacesContext.getCurrentInstance());

            Object rowKeyValue = getFieldValue(primeApplicationContext, obj, rowKey);
            return rowKeyValue == null ? null : rowKeyValue.toString();
        }

        throw new UnsupportedOperationException(
                getMessage("Provide a rowKey / rowKey-Provider / rowKey-Converter or implement getRowKey(T object) in %s"
                        + ", when basic rowKey algorithm is not used [component=%s,view=%s]."));
    }

    protected PropertyDescriptor getPropertyDescriptor(PrimeApplicationContext primeAppContext, Class<?> clazz, String field) {
        String cacheKey = clazz.getName();
        Map<String, PropertyDescriptor> classCache = primeAppContext.getPropertyDescriptorCache()
                .computeIfAbsent(cacheKey, k -> new ConcurrentHashMap<>());
        return classCache.computeIfAbsent(field, k -> {
            try {
                return new PropertyDescriptor(field, clazz);
            } catch (IntrospectionException e) {
                throw new FacesException(e);
            }
        });
    }

    public static <T> Builder<T> builder() {
        return new Builder<>();
    }

    public static class Builder<T> {
        final ReflectionLazyDataModel<T> model;

        public Builder() {
            model = new ReflectionLazyDataModel<>();
        }

        public Builder<T> valueSupplier(SerializableSupplier<List<T>> valuesSupplier) {
            model.valuesSupplier = valuesSupplier;
            return this;
        }

        public Builder<T> rowKey(String rowKey) {
            model.rowKey = rowKey;
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

        public ReflectionLazyDataModel<T> build() {
            Objects.requireNonNull(model.valuesSupplier, "Value supplier not set");
            return model;
        }
    }

    @FunctionalInterface
    public interface Sorter<T> extends Comparator<T>, Serializable {

    }
}
