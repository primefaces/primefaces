package org.primefaces.model.filter;

import javax.faces.context.FacesContext;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class RangeFilterConstraint implements FilterConstraint {

    @Override
    public boolean isMatching(FacesContext ctxt, Object value, Object filter, Locale locale) {
        if (value instanceof LocalDate
                && filter instanceof List) {
            LocalDate in = (LocalDate) value;
            LocalDate start = (LocalDate) ((List)filter).get(0);
            LocalDate end = (LocalDate) ((List)filter).get(1);
            return in.isAfter(start) && in.isBefore(end);
        }
        else  if (value instanceof LocalDateTime
                && filter instanceof List) {
            LocalDateTime in = (LocalDateTime) value;
            LocalDateTime start = (LocalDateTime) ((List) filter).get(0);
            LocalDateTime end = (LocalDateTime) ((List) filter).get(1);
            return in.isAfter(start) && in.isBefore(end);
        }
        else if (value instanceof Date
                && filter instanceof List) {
            Date in = (Date) value;
            Date start = (Date) ((List) filter).get(0);
            Date end = (Date) ((List) filter).get(1);
            return in.after(start) && in.before(end);
        }

        throw new UnsupportedOperationException("Unsupported type: " + filter.getClass());
    }
}
