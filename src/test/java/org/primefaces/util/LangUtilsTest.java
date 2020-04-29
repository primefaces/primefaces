/*
 * The MIT License
 *
 * Copyright (c) 2009-2019 PrimeTek
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
package org.primefaces.util;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class LangUtilsTest {

    @Test
    public void getTypeFromCollectionProperty_Simple() {
        Class type = LangUtils.getTypeFromCollectionProperty(new SimpleClass(), "strings");

        assertEquals(String.class, type);
    }

    @Test
    public void getTypeFromCollectionProperty_Inheritance() {
        Class type = LangUtils.getTypeFromCollectionProperty(new ConcreteClass(), "ints");

        assertEquals(Integer.class, type);
    }

    @Test
    public void getTypeFromCollectionProperty_GenericInheritance() {
        Class type = LangUtils.getTypeFromCollectionProperty(new ConcreteGenericClass(), "values");

        assertEquals(String.class, type);
    }

    @Test
    public void getTypeFromCollectionProperty_GenericInheritance_Multilevel() {
        Class type = LangUtils.getTypeFromCollectionProperty(new DetailedConcreteGenericClass(), "values");

        assertEquals(String.class, type);
    }

    class SimpleClass {
        private List<String> strings;

        public List<String> getStrings() {
            return strings;
        }

        public void setStrings(List<String> strings) {
            this.strings = strings;
        }
    }

    abstract class AbstractClass {
        private List<Integer> ints;

        public List<Integer> getInts() {
            return ints;
        }

        public void setInts(List<Integer> ints) {
            this.ints = ints;
        }
    }

    class ConcreteClass extends AbstractClass {

    }

    abstract class AbstractGenericClass<T> {
        private List<T> values;

        public List<T> getValues() {
            return values;
        }

        public void setValues(List<T> values) {
            this.values = values;
        }
    }

    class ConcreteGenericClass extends AbstractGenericClass<String> {

    }

    class DetailedConcreteGenericClass extends ConcreteGenericClass {

    }
}
