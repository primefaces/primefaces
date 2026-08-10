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

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class InFilterConstraintTest {

    private InFilterConstraint constraint;

    @BeforeEach
    void setup() {
        constraint = new InFilterConstraint();
    }

    @Test
    void testIsMatching_NullFilter() {
        assertFalse(constraint.isMatching(null, "Acme", null, null));
    }

    @Test
    void testIsMatching_Collection() {
        // bean-bound facet (e.g. p:selectManyMenu) already supplies a real Collection
        assertTrue(constraint.isMatching(null, "Acme", List.of("Acme", "Globex"), null));
        assertFalse(constraint.isMatching(null, "Initrode", List.of("Acme", "Globex"), null));
    }

    @Test
    void testIsMatching_Array() {
        assertTrue(constraint.isMatching(null, "Acme", new String[] {"Acme", "Globex"}, null));
        assertFalse(constraint.isMatching(null, "Initrode", new String[] {"Acme", "Globex"}, null));
    }

    @Test
    void testIsMatching_CommaSeparatedString_Splits() {
        // #7427 "In list" typed as free text next to the match-mode dropdown
        assertTrue(constraint.isMatching(null, "Globex", "Acme, Globex, Initech", null));
        assertTrue(constraint.isMatching(null, "Acme", "Acme, Globex, Initech", null));
        assertFalse(constraint.isMatching(null, "Initrode", "Acme, Globex, Initech", null));
    }

    @Test
    void testIsMatching_CommaSeparatedString_TrimsWhitespace() {
        assertTrue(constraint.isMatching(null, "Globex", "Acme,   Globex   ,Initech", null));
    }

    @Test
    void testIsMatching_SingleNonCollectionValue() {
        assertTrue(constraint.isMatching(null, 5, 5, null));
        assertFalse(constraint.isMatching(null, 5, 6, null));
    }
}
