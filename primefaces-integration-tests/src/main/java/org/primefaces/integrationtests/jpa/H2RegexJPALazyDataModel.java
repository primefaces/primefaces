/*
 * The MIT License
 *
 * Copyright (c) 2009-2026 PrimeFaces
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
package org.primefaces.integrationtests.jpa;

import org.primefaces.model.JPALazyDataModel;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Predicate;

/**
 * {@link JPALazyDataModel} plus the one match mode it cannot support out of the box: regular expression
 * matching has no portable JPA equivalent, so every database needs its own function - here H2's
 * {@code REGEXP_LIKE(value, pattern)}.
 * <p>
 * Note that H2 - like most database regex functions - matches a substring, while the in-memory
 * {@code MatchesRegexFilterConstraint} requires the whole value to match; anchor the pattern for identical
 * semantics.
 *
 * @param <T> The model class.
 */
public class H2RegexJPALazyDataModel<T> extends JPALazyDataModel<T> {

    private static final long serialVersionUID = 1L;

    @Override
    protected Predicate createRegexPredicate(CriteriaBuilder cb, Expression<String> fieldExpression, String pattern) {
        return cb.isTrue(cb.function("regexp_like", Boolean.class, fieldExpression, cb.literal(pattern)));
    }
}
