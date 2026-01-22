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
package org.primefaces.util;

import org.primefaces.component.api.UITable;
import org.primefaces.context.PrimeApplicationContext;
import org.primefaces.model.SortMeta;
import org.primefaces.model.TreeNode;

import java.text.Collator;
import java.util.Collection;
import java.util.Comparator;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

import jakarta.el.ValueExpression;
import jakarta.faces.FacesException;
import jakarta.faces.context.FacesContext;

public class SortTableComparator implements Comparator<Object> {

    public static final BeanPropertyMapper SORT_BY_VE_MAPPER = new SortByVEMapper();
    public static final BeanPropertyMapper FIELD_MAPPER = new FieldMapper();
    public static final BeanPropertyMapper TREE_NODE_MAPPER = new TreeNodeSortByVEMapper();

    private final FacesContext context;
    private final Collection<SortMeta> sortBy;
    private final UITable<?> table;
    private final Locale locale;
    private final String var;
    private final Collator collator;
    private final BeanPropertyMapper mapper;
    private final AtomicInteger compareResult = new AtomicInteger(0);

    public SortTableComparator(FacesContext context, UITable<?> table, BeanPropertyMapper mapper) {
        this.context = context;
        this.table = table;
        this.sortBy = table.getActiveSortMeta().values();
        this.var = table.getVar();
        this.locale = table.resolveDataLocale(context);
        this.collator = Collator.getInstance(locale);
        this.mapper = Objects.requireNonNull(mapper, "mapper is necessary to extract property value");
    }

    @Override
    public int compare(Object o1, Object o2) {
        compareResult.set(0); //no local variable, avoid redundant creation: reset to 0 instead

        for (SortMeta sortMeta : sortBy) {
            if (mapper.isValueExprBased() && sortMeta.isDynamic()) {
                table.invokeOnColumn(sortMeta.getColumnKey(), column -> {
                    compareResult.set(compareWithMapper(sortMeta, o1, o2));
                });
            }
            else {
                compareResult.set(compareWithMapper(sortMeta, o1, o2));
            }

            if (compareResult.get() != 0) {
                return compareResult.get();
            }
        }

        return 0;
    }

    private int compareWithMapper(SortMeta sortMeta, Object o1, Object o2) {
        final Object fv1 = mapper.map(context, var, sortMeta, o1);
        final Object fv2 = mapper.map(context, var, sortMeta, o2);
        return compare(context, sortMeta, fv1, fv2, collator, locale);
    }

    public static int compare(FacesContext context, SortMeta sortMeta, Object value1, Object value2,
                              Collator collator, Locale locale) {
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
                result = (Integer) sortMeta.getFunction().invoke(context.getELContext(), new Object[]{value1, value2, sortMeta});
            }

            return sortMeta.getOrder().isAscending() ? result : -1 * result;
        }
        catch (Exception e) {
            throw new FacesException(e);
        }
    }

    public static Comparator<Object> comparingSortByVE(FacesContext context, UITable<?> table) {
        return new SortTableComparator(context, table, SORT_BY_VE_MAPPER);
    }

    public static Comparator<Object> comparingField(FacesContext context, UITable<?> table) {
        return new SortTableComparator(context, table, FIELD_MAPPER);
    }

    public static Comparator<Object> comparingTreeNodeSortByVE(FacesContext context, UITable<?> table) {
        return new SortTableComparator(context, table, TREE_NODE_MAPPER);
    }

    public static class SortByVEMapper implements BeanPropertyMapper {

        @Override
        public boolean isValueExprBased() {
            return true;
        }

        @Override
        public Object map(FacesContext context, String var, SortMeta sortMeta, Object obj) {
            ValueExpression ve = sortMeta.getSortBy();
            context.getExternalContext().getRequestMap().put(var, obj);
            return ve.getValue(context.getELContext());
        }
    }

    public static class TreeNodeSortByVEMapper extends SortByVEMapper {

        @Override
        public Object map(FacesContext context, String var, SortMeta sortMeta, Object obj) {
            return super.map(context, var, sortMeta, ((TreeNode) obj).getData());
        }
    }

    public static class FieldMapper implements BeanPropertyMapper {

        @Override
        public boolean isValueExprBased() {
            return false;
        }

        @Override
        public Object map(FacesContext context, String var, SortMeta sortMeta, Object obj) {
            PropertyDescriptorResolver propResolver =
                    PrimeApplicationContext.getCurrentInstance(context).getPropertyDescriptorResolver();
            return propResolver.getValue(obj, sortMeta.getField());
        }
    }

    public interface BeanPropertyMapper {

        boolean isValueExprBased();

        Object map(FacesContext context, String var, SortMeta sortMeta, Object obj);
    }
}
