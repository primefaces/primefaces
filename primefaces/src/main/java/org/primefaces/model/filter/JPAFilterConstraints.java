package org.primefaces.model.filter;

import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiFunction;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;

import org.primefaces.model.MatchMode;
import org.primefaces.util.Constants;
import org.primefaces.util.MapBuilder;

public class JPAFilterConstraints {

    private Map<MatchMode, IJPAFilterConstraint> CONSTRAINTS = MapBuilder.builder()
            .put(MatchMode.STARTS_WITH, new JPAStringFilterConstraint(CriteriaBuilder::like, CriteriaBuilder::notLike))
            .build();

    interface IJPAFilterConstraint {

        Predicate toPredicate();
    }

    static class JPAFilterConstraint {

        MatchMode matchMode;
        CriteriaBuilder cb;
        Expression fieldExpression;
        Object filterValue;
        Locale locale;
        boolean caseSensitive = true;
        boolean wildcardSupport = false;

        public static Builder builder() {
            return new Builder();
        }

        public static final class Builder {
            private JPAFilterConstraint jpaFilterConstraint;

            private Builder() {
                jpaFilterConstraint = new JPAFilterConstraint();
            }

            public Builder matchMode(MatchMode matchMode) {
                jpaFilterConstraint.matchMode = matchMode;
                return this;
            }

            public Builder cb(CriteriaBuilder cb) {
                jpaFilterConstraint.cb = cb;
                return this;
            }

            public Builder fieldExpression(Expression fieldExpression) {
                jpaFilterConstraint.fieldExpression = fieldExpression;
                return this;
            }

            public Builder filterValue(Object filterValue) {
                jpaFilterConstraint.filterValue = filterValue;
                return this;
            }

            public Builder locale(Locale locale) {
                jpaFilterConstraint.locale = locale;
                return this;
            }

            public JPAFilterConstraint build() {

            }
        }

        protected String getStringFilterValue(Object filterValue, Locale locale) {
            String value = Objects.toString(filterValue, Constants.EMPTY_STRING);
            value = caseSensitive ? value : value.toUpperCase(locale);
            if (wildcardSupport) {
                value = value.replace("*", "%");
                value = value.replace("?", "_");
            }
            return value;
        }
    }

    static class JPAStringFilterConstraint extends JPAFilterConstraint implements IJPAFilterConstraint {

        private BiFunction<Expression<String>, String, Predicate> biFunction;
        private BiFunction<Expression<String>, String, Predicate> notBiFunction;

        public JPAStringFilterConstraint(BiFunction<Expression<String>, String, Predicate> biFunction, BiFunction<Expression<String>, String, Predicate> notBiFunction) {
            this.biFunction = biFunction;
            this.notBiFunction = notBiFunction;
        }

        @Override
        public Predicate toPredicate() {
            Expression<String> fieldExpressionAsString = caseSensitive
                    ? fieldExpression.as(String.class)
                    : cb.upper(fieldExpression.as(String.class));

            biFunction.
            return cb.like(fieldExpressionAsString, getStringFilterValue(filterValue, locale) + "%");
        }
    }
}
