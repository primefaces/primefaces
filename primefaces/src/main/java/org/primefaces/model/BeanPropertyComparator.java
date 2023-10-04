package org.primefaces.model;

import javax.el.ValueExpression;
import javax.faces.FacesException;
import javax.faces.context.FacesContext;
import java.text.Collator;
import java.util.Collection;
import java.util.Comparator;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicInteger;

import org.primefaces.application.PropertyDescriptorResolver;
import org.primefaces.component.api.UITable;
import org.primefaces.context.PrimeApplicationContext;

public class BeanPropertyComparator implements Comparator<Object> {

    private final FacesContext context;
    private final Collection<SortMeta> sortBy;
    private final UITable table;
    private final Locale locale;
    private final String var;
    private final Collator collator;
    private final ExtractPropertyMapper mapper;

    public BeanPropertyComparator(FacesContext context, UITable table, Collection<SortMeta> sortBy, ExtractPropertyMapper mapper) {
        this.context = context;
        this.sortBy = sortBy;
        this.table = table;
        this.var = table.getVar();
        this.locale = table.resolveDataLocale(context);
        this.collator = Collator.getInstance(locale);
        this.mapper = mapper;
    }

    public static Comparator<Object> valueExprBased(FacesContext context, UITable table, Collection<SortMeta> sortBy) {
        return new BeanPropertyComparator(context, table, sortBy, valueExprMapper());
    }

    public static Comparator<Object> reflectionBased(FacesContext context, UITable table, Collection<SortMeta> sortBy) {
        return new BeanPropertyComparator(context, table, sortBy, reflectionMapper());
    }

    @Override
    public int compare(Object o1, Object o2) {
        AtomicInteger result = new AtomicInteger(0);
        for (SortMeta sortMeta : sortBy) {
            final Object fv1 = mapper.extract(context, var, sortMeta, o1);
            final Object fv2 = mapper.extract(context, var, sortMeta, o2);

            if (sortMeta.isHeaderRow()) {
                result.set(compare(context, sortMeta, fv1, fv2, collator, locale));
            }
            else {
                // Currently ColumnGrouping supports ui:repeat, therefore we have to use a callback
                // and can't use sortMeta.getComponent()
                // Later when we refactored ColumnGrouping, we may remove #invokeOnColumn as we dont support ui:repeat in other cases

                table.invokeOnColumn(sortMeta.getColumnKey(), column -> {
                    result.set(compare(context, sortMeta, fv1, fv2, collator, locale));
                });
            }

            if (result.get() != 0) {
                return result.get();
            }
        }

        return 0;
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

    public static ExtractPropertyMapper valueExprMapper() {
        return (context, var, sortMeta, o1) -> {
            ValueExpression ve = sortMeta.getSortBy();
            context.getExternalContext().getRequestMap().put(var, o1);
            return ve.getValue(context.getELContext());
        };
    }

    public static ExtractPropertyMapper reflectionMapper() {
        return (context, var, sortMeta, o1) -> {
            PropertyDescriptorResolver propResolver = PrimeApplicationContext.getCurrentInstance(context)
                    .getPropertyDescriptorResolver();
            return propResolver.getValue(o1, sortMeta.getField());
        };
    }

    @FunctionalInterface
    public interface ExtractPropertyMapper {

        Object extract(FacesContext context, String var, SortMeta sortMeta, Object o1);
    }
}
