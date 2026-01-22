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
package org.primefaces.util;

import java.util.List;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LangUtilsTest {

    private static final String FOO = "foo";
    private static final String SENTENCE = "foo bar baz";

    @Test
    void getTypeFromCollectionProperty_Simple() {
        Class type = LangUtils.getTypeFromCollectionProperty(new SimpleClass(), "strings");

        assertEquals(String.class, type);
    }

    @Test
    void getTypeFromCollectionProperty_Inheritance() {
        Class type = LangUtils.getTypeFromCollectionProperty(new ConcreteClass(), "ints");

        assertEquals(Integer.class, type);
    }

    @Test
    void getTypeFromCollectionProperty_GenericInheritance() {
        Class type = LangUtils.getTypeFromCollectionProperty(new ConcreteGenericClass(), "values");

        assertEquals(String.class, type);
    }

    @Test
    void getTypeFromCollectionProperty_GenericInheritance_Multilevel() {
        Class type = LangUtils.getTypeFromCollectionProperty(new DetailedConcreteGenericClass(), "values");

        assertEquals(String.class, type);
    }

    @Test
    void countMatches_char() {
        assertEquals(0, LangUtils.countMatches(null, 'D'));
        assertEquals(5, LangUtils.countMatches("one long someone sentence of one", ' '));
        assertEquals(6, LangUtils.countMatches("one long someone sentence of one", 'o'));
    }

    @Test
    void substring() {
        assertNull(LangUtils.substring(null, 0, 0));
        assertNull(LangUtils.substring(null, 1, 2));
        assertEquals("", LangUtils.substring("", 0, 0));
        assertEquals("", LangUtils.substring("", 1, 2));
        assertEquals("", LangUtils.substring("", -2, -1));

        assertEquals("", LangUtils.substring(SENTENCE, 8, 6));
        assertEquals(FOO, LangUtils.substring(SENTENCE, 0, 3));
        assertEquals("o", LangUtils.substring(SENTENCE, -9, 3));
        assertEquals(FOO, LangUtils.substring(SENTENCE, 0, -8));
        assertEquals("o", LangUtils.substring(SENTENCE, -9, -8));
        assertEquals(SENTENCE, LangUtils.substring(SENTENCE, 0, 80));
        assertEquals("", LangUtils.substring(SENTENCE, 2, 2));
        assertEquals("b", LangUtils.substring("abc", -2, -1));
    }

    @Test
    void isParsable() {
        assertFalse(LangUtils.isNumeric(null));
        assertFalse(LangUtils.isNumeric(""));
        assertFalse(LangUtils.isNumeric("0xC1AB"));
        assertFalse(LangUtils.isNumeric("65CBA2"));
        assertFalse(LangUtils.isNumeric("pendro"));
        assertFalse(LangUtils.isNumeric("64, 2"));
        assertFalse(LangUtils.isNumeric("64.2.2"));
        assertFalse(LangUtils.isNumeric("64."));
        assertFalse(LangUtils.isNumeric("64L"));
        assertFalse(LangUtils.isNumeric("-"));
        assertFalse(LangUtils.isNumeric("--2"));
        assertTrue(LangUtils.isNumeric("64.2"));
        assertTrue(LangUtils.isNumeric("64"));
        assertTrue(LangUtils.isNumeric("018"));
        assertTrue(LangUtils.isNumeric(".18"));
        assertTrue(LangUtils.isNumeric("-65"));
        assertTrue(LangUtils.isNumeric("-018"));
        assertTrue(LangUtils.isNumeric("-018.2"));
        assertTrue(LangUtils.isNumeric("-.236"));
    }

    @Test
    void toCapitalCase() {
        assertEquals("", LangUtils.toCapitalCase(null));
        assertEquals("", LangUtils.toCapitalCase(""));
        assertEquals("", LangUtils.toCapitalCase(" "));
        assertEquals("This Is A Test", LangUtils.toCapitalCase("thisIsATest"));
        assertEquals("Uppercase First Char", LangUtils.toCapitalCase("UppercaseFirstChar"));
        assertEquals("My Über String", LangUtils.toCapitalCase("myÜberString"));
    }

    @Test
    void normalize() {
        assertEquals("test", LangUtils.normalize("test", false));
        assertEquals("test", LangUtils.normalize("test", true));
        assertEquals("TEST", LangUtils.normalize("TEST", false));
        assertEquals("TEST", LangUtils.normalize("TEST", true));
        assertEquals("tést", LangUtils.normalize("tést", false));
        assertEquals("test", LangUtils.normalize("tést", true));
        assertEquals("t e s t", LangUtils.normalize("t e s t", false));
        assertEquals("t e s t", LangUtils.normalize("t e s t", true));
        assertEquals("", LangUtils.normalize("", false));
        assertEquals("", LangUtils.normalize("", true));
        assertEquals(Constants.EMPTY_STRING, LangUtils.normalize("", true));
        assertEquals("123", LangUtils.normalize("123", false));
        assertEquals("123", LangUtils.normalize("123", true));
        assertEquals("123", LangUtils.normalize("123", false));
        assertEquals("123", LangUtils.normalize("123", true));
        assertEquals("test123", LangUtils.normalize("test123", false));
        assertEquals("test123", LangUtils.normalize("test123", true));
        assertEquals("tést123", LangUtils.normalize("tést123", false));
        assertEquals("test123", LangUtils.normalize("tést123", true));
        SimpleClass simpleClass = new SimpleClass();
        assertEquals(simpleClass, LangUtils.normalize(simpleClass, true));
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
        private Container container;

        public List<Integer> getInts() {
            return ints;
        }

        public void setInts(List<Integer> ints) {
            this.ints = ints;
        }

        public Container getContainer() {
            return container;
        }

        public void setContainer(Container container) {
            this.container = container;
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

    class Container {

        private Container container;
        private String string;

        public String getString() {
            return string;
        }

        public void setString(String string) {
            this.string = string;
        }

        public Container getContainer() {
            return container;
        }

        public void setContainer(Container container) {
            this.container = container;
        }
    }
}
