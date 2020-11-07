package org.primefaces.model.filter;

import javax.faces.context.FacesContext;
import java.util.Locale;
import java.util.function.BiPredicate;

public class StringEqualityFilterConstraint implements FilterConstraint {

    private BiPredicate<String, String> predicate;

    public StringEqualityFilterConstraint(BiPredicate<String, String> predicate) {
        this.predicate = predicate;
    }

    @Override
    public boolean isMatching(FacesContext ctxt, Object value, Object filter, Locale locale) {
        String str = filter == null ? null : filter.toString().trim().toLowerCase(locale);
        return predicate.test(value.toString().toLowerCase(locale), str);
    }
}
