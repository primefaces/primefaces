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
import org.primefaces.model.filter.FilterConstraint;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import jakarta.faces.context.FacesContext;

import org.apache.commons.collections4.ComparatorUtils;

/**
 * Dummy implementation of LazyDataModel that uses a list to mimic a real datasource like a database.
 */
public class CustomerLazyDataModel extends LazyDataModel<Customer> {

    private static final long serialVersionUID = 1L;

    private List<Customer> datasource;

    public CustomerLazyDataModel(List<Customer> datasource) {
        this.datasource = datasource;
    }

    @Override
    public Customer getRowData(String rowKey) {
        for (Customer customer : datasource) {
            if (customer.getId() == Integer.parseInt(rowKey)) {
                return customer;
            }
        }

        return null;
    }

    @Override
    public String getRowKey(Customer customer) {
        return String.valueOf(customer.getId());
    }

    @Override
    public int count(Map<String, FilterMeta> filterBy) {
        return (int) datasource.stream().filter(o -> filter(FacesContext.getCurrentInstance(), filterBy.values(), o)).count();
    }

    @Override
    public List<Customer> load(int offset, int pageSize, Map<String, SortMeta> sortBy, Map<String, FilterMeta> filterBy) {
        // apply filters
        List<Customer> customers = datasource.stream()
                .filter(o -> filter(FacesContext.getCurrentInstance(), filterBy.values(), o))
                .collect(Collectors.toList());

        // sort
        if (!sortBy.isEmpty()) {
            List<Comparator<Customer>> comparators = sortBy.values().stream().map(o -> new CustomerLazySorter(o.getField(), o.getOrder()))
                    .collect(Collectors.toList());
            Comparator<Customer> cp = ComparatorUtils.chainedComparator(comparators); // from apache
            customers.sort(cp);
        }

        // apply offset & limit
        return customers.subList(offset, Math.min(offset + pageSize, customers.size()));
    }

    private boolean filter(FacesContext context, Collection<FilterMeta> filterBy, Object o) {
        boolean matching = true;

        for (FilterMeta filter : filterBy) {
            FilterConstraint constraint = filter.getConstraint();
            Object filterValue = filter.getFilterValue();

            try {
                Object columnValue = String.valueOf(getPropertyValueViaReflection(o, filter.getField()));
                matching = constraint.isMatching(context, columnValue, filterValue, Locale.getDefault());
            }
            catch (ReflectiveOperationException | IntrospectionException e) {
                System.err.println("Error getting property value via reflection: " + e.getMessage());
                matching = false;
            }

            if (!matching) {
                break;
            }
        }

        return matching;
    }

    public Object getPropertyValueViaReflection(Object o, String field) throws ReflectiveOperationException, IllegalArgumentException, IntrospectionException {
        return new PropertyDescriptor(field, o.getClass()).getReadMethod().invoke(o);
    }
}
