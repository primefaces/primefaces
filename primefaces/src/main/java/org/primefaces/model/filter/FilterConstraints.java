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
package org.primefaces.model.filter;

import org.primefaces.model.MatchMode;
import org.primefaces.util.MapBuilder;

import java.util.Map;
import java.util.Optional;

import jakarta.faces.FacesException;

public final class FilterConstraints {

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

    private FilterConstraints() {
        // NOOP
    }

    public static FilterConstraint of(MatchMode mode) {
        return Optional.ofNullable(ALL_CONSTRAINTS.get(mode))
                .orElseThrow(() -> new FacesException("No filter constraint found for match mode: " + mode));
    }

    public static FilterConstraint of(String matchMode) {
        MatchMode mode = MatchMode.of(matchMode);
        return of(mode);
    }
}
