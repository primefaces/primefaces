/*
 * The MIT License
 *
 * Copyright (c) 2009-2023 PrimeTek Informatics
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
//package org.primefaces.model.filter;
//
//import java.util.Locale;
//import java.util.Map;
//import java.util.Objects;
//import java.util.function.Function;
//import javax.persistence.criteria.CriteriaBuilder;
//import javax.persistence.criteria.Expression;
//import javax.persistence.criteria.Predicate;
//
//import org.primefaces.model.MatchMode;
//import org.primefaces.util.Constants;
//import org.primefaces.util.MapBuilder;
//
//public class JPAFilterConstraints {
//
//    private Map<MatchMode, JPAFilterConstraintBuilder> CONSTRAINTS = MapBuilder.builder()
//            .put(MatchMode.STARTS_WITH, new JPAStringFilterConstraint(CriteriaBuilder::like, CriteriaBuilder::notLike)
//            .build();
//
//    interface JPAFilterConstraintBuilder {
//
//        Predicate toPredicate(JPAFilterConstraint constraint);
//    }
//
//    @FunctionalInterface
//    public interface TriFunction<T, U, V, R> {
//        R apply(T var1, U var2, V var3);
//
//        default <W> TriFunction<T, U, V, W> andThen(Function<? super R, ? extends W> after) {
//            Objects.requireNonNull(after);
//            return (t, u, v) -> after.apply(this.apply(t, u, v));
//        }
//    }
//
//    static class JPAFilterConstraint {
//
//        private MatchMode matchMode;
//        private CriteriaBuilder cb;
//        private Expression fieldExpression;
//        private Object filterValue;
//        private Locale locale;
//        private boolean caseSensitive = true;
//        private boolean wildcardSupport = false;
//
//        public static Builder builder() {
//            return new Builder();
//        }
//
//        public static final class Builder {
//            private JPAFilterConstraint jpaFilterConstraint;
//
//            private Builder() {
//                jpaFilterConstraint = new JPAFilterConstraint();
//            }
//
//            public Builder matchMode(MatchMode matchMode) {
//                jpaFilterConstraint.matchMode = matchMode;
//                return this;
//            }
//
//            public Builder cb(CriteriaBuilder cb) {
//                jpaFilterConstraint.cb = cb;
//                return this;
//            }
//
//            public Builder fieldExpression(Expression fieldExpression) {
//                jpaFilterConstraint.fieldExpression = fieldExpression;
//                return this;
//            }
//
//            public Builder filterValue(Object filterValue) {
//                jpaFilterConstraint.filterValue = filterValue;
//                return this;
//            }
//
//            public Builder locale(Locale locale) {
//                jpaFilterConstraint.locale = locale;
//                return this;
//            }
//
//            public JPAFilterConstraint build() {
//
//            }
//        }
//
//        protected String getStringFilterValue() {
//            String value = Objects.toString(filterValue, Constants.EMPTY_STRING);
//            value = caseSensitive ? value : value.toUpperCase(locale);
//            if (wildcardSupport) {
//                value = value.replace("*", "%");
//                value = value.replace("?", "_");
//            }
//            return value;
//        }
//    }
//
//    static class JPAStringFilterConstraint implements JPAFilterConstraintBuilder {
//
//        private TriFunction<CriteriaBuilder, Expression<String>, String, Predicate> biFunction;
//
//        private boolean likeable;
//
//        public JPAStringFilterConstraint(TriFunction<CriteriaBuilder, Expression<String>, String, Predicate> biFunction, boolean likeable) {
//            this.biFunction = biFunction;
//            this.likeable = likeable;
//        }
//
//        @Override
//        public Predicate toPredicate(JPAFilterConstraint constraint) {
//            Expression<String> fieldExprStr = constraint.fieldExpression.as(String.class);
//            if (constraint.caseSensitive) {
//                fieldExprStr = constraint.cb.upper(fieldExprStr);
//            }
//
//            String value = constraint.getStringFilterValue() ;
//
//            return biFunction.apply(constraint.cb, fieldExprStr, constraint.getStringFilterValue());
//        }
//    }
//}
