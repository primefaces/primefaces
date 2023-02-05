package org.primefaces.model.filter;

import java.util.Map;

import org.primefaces.model.MatchMode;
import org.primefaces.util.MapBuilder;

public final class FilterConstraints {

    private FilterConstraints() {
        // NOOP
    }

    private static final Map<MatchMode, FilterConstraint> ALL_CONSTRAINTS = MapBuilder.<MatchMode, FilterConstraint>builder()
            .put(MatchMode.STARTS_WITH, new StartsWithFilterConstraint())
            .put(MatchMode.NOT_STARTS_WITH, new NegationFilterConstraintWrapper(new StartsWithFilterConstraint()))
            .put(MatchMode.ENDS_WITH, new EndsWithFilterConstraint())
            .put(MatchMode.NOT_ENDS_WITH, new NegationFilterConstraintWrapper(new EndsWithFilterConstraint()))
            .put(MatchMode.CONTAINS, new ContainsFilterConstraint())
            .put(MatchMode.NOT_CONTAINS, new NegationFilterConstraintWrapper(new ContainsFilterConstraint()))
            .put(MatchMode.EXACT, new ExactFilterConstraint())
            .put(MatchMode.NOT_EXACT, new NegationFilterConstraintWrapper(new ExactFilterConstraint()))
            .put(MatchMode.LESS_THAN, new LessThanFilterConstraint())
            .put(MatchMode.LESS_THAN_EQUALS, new LessThanEqualsFilterConstraint())
            .put(MatchMode.GREATER_THAN, new GreaterThanFilterConstraint())
            .put(MatchMode.GREATER_THAN_EQUALS, new GreaterThanEqualsFilterConstraint())
            .put(MatchMode.EQUALS, new EqualsFilterConstraint())
            .put(MatchMode.NOT_EQUALS, new NegationFilterConstraintWrapper(new EqualsFilterConstraint()))
            .put(MatchMode.IN, new InFilterConstraint())
            .put(MatchMode.NOT_IN, new NegationFilterConstraintWrapper(new InFilterConstraint()))
            .put(MatchMode.GLOBAL, new GlobalFilterConstraint())
            .put(MatchMode.BETWEEN, new BetweenFilterConstraint())
            .put(MatchMode.NOT_BETWEEN, new NegationFilterConstraintWrapper(new BetweenFilterConstraint()))
            .build();

    public static FilterConstraint of(MatchMode mode) {
        return ALL_CONSTRAINTS.get(mode);
    }
}
