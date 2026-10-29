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
package org.primefaces.showcase.view.data.datatable;

import org.primefaces.model.JPALazyDataModel;
import org.primefaces.model.LazyDataModel;
import org.primefaces.showcase.domain.CustomerEntity;

import java.io.Serializable;

import jakarta.annotation.PostConstruct;
import jakarta.faces.convert.DateTimeConverter;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Predicate;

@Named("dtLazyJpaView")
@ViewScoped
public class LazyJpaView implements Serializable {

    private static final long serialVersionUID = 1L;

    @Inject
    private EntityManager entityManager;

    private LazyDataModel<CustomerEntity> lazyModel;

    @PostConstruct
    public void init() {
        lazyModel = new JPALazyDataModel.Builder<CustomerEntity, H2LazyDataModel>(new H2LazyDataModel())
                .entityClass(CustomerEntity.class)
                .entityManager(() -> entityManager)
                // case-insensitive text filters: the default is a case-sensitive LIKE
                .caseSensitive(false)
                .build();
    }

    public LazyDataModel<CustomerEntity> getLazyModel() {
        return lazyModel;
    }

    public DateTimeConverter getJoinDateConverter() {
        DateTimeConverter converter = new DateTimeConverter();
        converter.setPattern("yyyy-MM-dd");
        converter.setType("localDate");
        return converter;
    }

    public DateTimeConverter getCheckInTimeConverter() {
        DateTimeConverter converter = new DateTimeConverter();
        converter.setPattern("HH:mm");
        converter.setType("localTime");
        return converter;
    }

    public DateTimeConverter getLastContactConverter() {
        DateTimeConverter converter = new DateTimeConverter();
        converter.setPattern("yyyy-MM-dd HH:mm");
        converter.setType("localDateTime");
        return converter;
    }

    /**
     * {@link JPALazyDataModel} translates every match mode into a criteria predicate, except "matches regex":
     * regular expressions have no portable JPA equivalent, so each database needs its own function - here the
     * {@code REGEXP_LIKE} of the H2 database this example runs on. Without the override, picking "matches
     * regex" from a text column's dropdown throws an {@code UnsupportedOperationException}.
     * <p>
     * Note that H2 - like most database regex functions - matches a substring, while a non-lazy, in-memory
     * filter requires the whole value to match; anchor the pattern for identical semantics.
     */
    public static class H2LazyDataModel extends JPALazyDataModel<CustomerEntity> {

        private static final long serialVersionUID = 1L;

        @Override
        protected Predicate createRegexPredicate(CriteriaBuilder cb, Expression<String> fieldExpression, String pattern) {
            return cb.isTrue(cb.function("regexp_like", Boolean.class, fieldExpression, cb.literal(pattern)));
        }
    }
}
