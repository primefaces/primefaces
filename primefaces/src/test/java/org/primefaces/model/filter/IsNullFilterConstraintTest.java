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
package org.primefaces.model.filter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class IsNullFilterConstraintTest {

    private IsNullFilterConstraint constraint;

    @BeforeEach
    void setup() {
        constraint = new IsNullFilterConstraint();
    }

    @Test
    void testIsMatching_Null() {
        assertTrue(constraint.isMatching(null, null, "ignored", null));
    }

    @Test
    void testIsMatching_EmptyString_DoesNotMatch() {
        // unlike IsEmptyFilterConstraint, a non-null blank string is NOT null
        assertFalse(constraint.isMatching(null, "", "ignored", null));
    }

    @Test
    void testIsMatching_NonNullValue() {
        assertFalse(constraint.isMatching(null, "test", "ignored", null));
    }

    @Test
    void testIsMatching_IgnoresFilterValue() {
        assertTrue(constraint.isMatching(null, null, null, null));
    }
}
