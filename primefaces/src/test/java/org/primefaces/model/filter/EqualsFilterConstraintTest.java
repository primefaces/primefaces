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

import java.math.BigDecimal;
import java.util.Objects;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EqualsFilterConstraintTest {
    private EqualsFilterConstraint constraint;

    @BeforeEach
    void setup() {
        constraint = new EqualsFilterConstraint();
    }

    @Test
    void testCompareToEquals_Equal() {
        assertTrue(constraint.compareToEquals(BigDecimal.ONE, BigDecimal.ONE));
    }

    @Test
    void testCompareToEquals_NotEqual() {
        assertFalse(constraint.compareToEquals(BigDecimal.ONE, BigDecimal.TEN));
    }

    @Test
    void testCompareToEquals_BigDecimalPlaces() {
        assertTrue(constraint.compareToEquals(new BigDecimal("1.0"), new BigDecimal("1.0000")));
    }

    @Test
    void testIsMatching_SameReference() {
        Object value = "test";
        assertTrue(constraint.isMatching(null, value, value, null));
    }

    @Test
    void testIsMatching_NullValue() {
        assertFalse(constraint.isMatching(null, null, "test", null));
    }

    @Test
    void testIsMatching_NullFilter() {
        assertFalse(constraint.isMatching(null, "test", null, null));
    }

    @Test
    void testIsMatching_EnumValues() {
        assertTrue(constraint.isMatching(null, TestEnum.A, TestEnum.A, null));
        assertFalse(constraint.isMatching(null, TestEnum.A, TestEnum.B, null));
    }

    @Test
    void testIsMatching_EnumValuesAsString() {
        assertTrue(constraint.isMatching(null, TestEnum.A, "A", null));
        assertFalse(constraint.isMatching(null, TestEnum.A, "B", null));
    }

    @Test
    void testIsMatching_ComparableNumbers() {
        assertTrue(constraint.isMatching(null, BigDecimal.ONE, BigDecimal.ONE, null));
        assertFalse(constraint.isMatching(null, BigDecimal.ONE, BigDecimal.TEN, null));
    }

    @Test
    void testIsMatching_NonComparableObjects() {
        Object obj1 = new TestObject("test");
        Object obj2 = new TestObject("test");
        Object obj3 = new TestObject("different");

        assertTrue(constraint.isMatching(null, obj1, obj2, null));
        assertFalse(constraint.isMatching(null, obj1, obj3, null));
    }

    @Test
    void testIsMatching_InvalidComparison() {
        assertThrows(IllegalArgumentException.class, () -> {
            constraint.isMatching(null, BigDecimal.ONE, "1", null);
        });
    }

    private enum TestEnum {
        A, B
    }

    private static class TestObject {
        private final String value;

        TestObject(String value) {
            this.value = value;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            TestObject that = (TestObject) o;
            return Objects.equals(value, that.value);
        }

        @Override
        public int hashCode() {
            return Objects.hash(value);
        }
    }
}
