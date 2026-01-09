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
package org.primefaces.model;

import org.primefaces.component.api.UITable;
import org.primefaces.context.PrimeApplicationContext;
import org.primefaces.model.filter.FilterConstraint;
import org.primefaces.util.Callbacks;
import org.primefaces.util.ComponentUtils;
import org.primefaces.util.LocaleUtils;
import org.primefaces.util.PropertyDescriptorResolver;
import org.primefaces.util.SortTableComparator;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.convert.Converter;

/**
 * Default implementation of the {@link LazyDataModel}, which implements sorting and filtering via reflection.
 * It provides some useful features to get more control over filtering,
 * without lossing the power and performance of the {@link LazyDataModel}.
 *
 * @param <T> The result obj
 */
public class DefaultLazyDataModel<T> extends LazyDataModel<T> {

    private static final long serialVersionUID = 1L;

    private static final Logger LOGGER = Logger.getLogger(DefaultLazyDataModel.class.getName());

    protected String rowKeyField;
    protected FilterConstraint filter;
    protected Sorter<T> sorter;
    protected ValuesSupplier<T> valuesSupplier;
    protected Callbacks.SerializableFunction<T, Object> rowKeyProvider;
    protected Callbacks.SerializablePredicate<T> skipFiltering;
    protected Callbacks.SerializablePredicate<FilterMeta> ignoreFilter;

    /**
     * For serialization only
     */
    public DefaultLazyDataModel() {
        // NOOP
    }

    @Override
    public int count(Map<String, FilterMeta> filterBy) {
        return 0;
    }

    @Override
    public List<T> load(int first, int pageSize, Map<String, SortMeta> sortBy, Map<String, FilterMeta> filterBy) {
        List<T> values = valuesSupplier.get(filterBy);
        List<T> filteredValues = filter(values, filterBy);

        setRowCount(filteredValues.size());
        first = recalculateFirst(first, pageSize, getRowCount());

        sort(filteredValues);

        if (pageSize == 0) {
            return filteredValues;
        }

        return filteredValues.stream().skip(first).limit(pageSize).collect(Collectors.toList());
    }

    protected void sort(List<T> values) {
        if (values == null || values.isEmpty()) {
            return;
        }

        FacesContext context = FacesContext.getCurrentInstance();
        UIComponent source = UIComponent.getCurrentComponent(context);
        if (source instanceof UITable) {
            values.sort(SortTableComparator.comparingField(context, (UITable<?>) source));
        }

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
        UIComponent source = UIComponent.getCurrentComponent(context);

        FilterMeta globalFilter = filterBy.get(FilterMeta.GLOBAL_FILTER_KEY);
        boolean hasGlobalFilter = (globalFilter != null && globalFilter.isActive()) || filter != null;

        AtomicReference<Object> fieldValueHolder = new AtomicReference<>();

        return values.stream()
                .filter(obj -> {
                    // always include the current obj in the result?
                    if (skipFiltering != null && skipFiltering.test(obj)) {
                        return true;
                    }

                    boolean localMatch = true;
                    boolean globalMatch = false;

                    // global filtering
                    if (hasGlobalFilter) {
                        if (globalFilter != null && globalFilter.isActive()) {
                            globalMatch = globalFilter.getConstraint().isMatching(context, obj, globalFilter.getFilterValue(), locale);
                        }
                        if (filter != null && !globalMatch) {
                            globalMatch = filter.isMatching(context, obj, null, locale);
                        }
                    }

                    // local filtering
                    for (FilterMeta filterMeta : filterBy.values()) {
                        // skip filter
                        if (filterMeta.getField() == null || filterMeta.getFilterValue() == null || filterMeta.isGlobalFilter()) {
                            continue;
                        }

                        // ignore this filter?
                        if (ignoreFilter != null && ignoreFilter.test(filterMeta)) {
                            continue;
                        }

                        fieldValueHolder.set(null);

                        // in case its generated, we can just directly use reflection
                        if (filterMeta.isFilterByGenerated()) {
                            fieldValueHolder.set(propResolver.getValue(obj, filterMeta.getField()));
                        }
                        // otherwise it's a user-defined filterBy expression
                        else {
                            if (source instanceof UITable) {
                                UITable<?> table = (UITable<?>) source;
                                table.invokeOnColumn(filterMeta.getColumnKey(), (column) -> {
                                    Object localValue = ComponentUtils.executeInRequestScope(context, table.getVar(), obj,
                                            () -> filterMeta.getLocalValue(context.getELContext(), column));
                                    fieldValueHolder.set(localValue);
                                });
                            }
                        }

                        Object fieldValue = fieldValueHolder.get();
                        Object filterValue = filterMeta.getFilterValue();
                        Object convertedFilterValue = null;

                        if (fieldValue != null) {
                            Class<?> filterValueClass = filterValue.getClass();
                            if (filterValueClass.isArray() || Collection.class.isAssignableFrom(filterValueClass)) {
                                convertedFilterValue = filterValue;
                            }
                            else {
                                convertedFilterValue = ComponentUtils.convertToType(filterValue, fieldValue.getClass(), LOGGER);
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
        List<T> values = Objects.requireNonNullElseGet(valuesSupplier.get(Collections.emptyMap()), Collections::emptyList);
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

    public static <T> Builder<T, ? extends DefaultLazyDataModel<T>> builder() {
        return new Builder<>(new DefaultLazyDataModel<>());
    }

    public static class Builder<T, TM extends DefaultLazyDataModel<T>> {
        protected TM model;

        public Builder(TM model) {
            this.model = model;
        }

        public Builder<T, TM> valueSupplier(ValuesSupplier<T> valuesSupplier) {
            model.valuesSupplier = valuesSupplier;
            return this;
        }

        public Builder<T, TM> rowKeyField(String rowKey) {
            model.rowKeyField = rowKey;
            return this;
        }

        public Builder<T, TM> rowKeyProvider(Callbacks.SerializableFunction<T, Object> rowKeyProvider) {
            model.rowKeyProvider = rowKeyProvider;
            return this;
        }

        public Builder<T, TM> rowKeyConverter(Converter<T> rowKeyConverter) {
            model.rowKeyConverter = rowKeyConverter;
            return this;
        }

        public Builder<T, TM> filter(FilterConstraint filter) {
            model.filter = filter;
            return this;
        }

        /**
         * Registers a callback that can decide whether to always include the given object in the result,
         * independent of the current filters.
         *
         * @param skipFiltering the callback
         * @return the current builder
         */
        public Builder<T, TM> skipFiltering(Callbacks.SerializablePredicate<T> skipFiltering) {
            model.skipFiltering = skipFiltering;
            return this;
        }

        /**
         * Registers a callback that can decide to ignore the given filter completely.
         * You may manually respect the filter in the {@link #valuesSupplier}.
         *
         * @param ignoreFilter the callback
         * @return the current builder
         */
        public Builder<T, TM> ignoreFilter(Callbacks.SerializablePredicate<FilterMeta> ignoreFilter) {
            model.ignoreFilter = ignoreFilter;
            return this;
        }

        public Builder<T, TM> sorter(Sorter<T> sorter) {
            model.sorter = sorter;
            return this;
        }

        public TM build() {
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

    @FunctionalInterface
    public interface ValuesSupplier<T> extends Serializable {

        List<T> get(Map<String, FilterMeta> filterBy);
    }
}
