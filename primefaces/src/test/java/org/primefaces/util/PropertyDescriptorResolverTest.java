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

import java.beans.PropertyDescriptor;

import jakarta.faces.FacesException;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PropertyDescriptorResolverTest {

    @Test
    void get() {
        PropertyDescriptorResolver propResolver = new PropertyDescriptorResolver.DefaultResolver();

        PropertyDescriptor pd = propResolver.get(A.class, "name");
        assertNotNull(pd);
        assertEquals(String.class, pd.getPropertyType());

        pd = propResolver.get(B.class, "bool1");
        assertNotNull(pd);
        assertEquals(boolean.class, pd.getPropertyType());

        pd = propResolver.get(B.class, "bool2");
        assertNotNull(pd);
        assertEquals(Boolean.class, pd.getPropertyType());

        pd = propResolver.get(C.class, "b.dummyEnum");
        assertNotNull(pd);
        assertEquals(DummyEnum.class, pd.getPropertyType());

        assertThrows(FacesException.class, () -> propResolver.get(C.class, "unknown"));
    }

    @Test
    void getValue() {
        PropertyDescriptorResolver propResolver = new PropertyDescriptorResolver.DefaultResolver();

        B b = new B("b", true, Boolean.FALSE, DummyEnum.X);
        D d = new D("d", false, Boolean.FALSE, DummyEnum.Z, 123);
        C c = new C(d);

        Object obj = propResolver.getValue(c, "b");
        assertEquals(d, obj);

        obj = propResolver.getValue(c, "b.dummyEnum");
        assertEquals(DummyEnum.Z, obj);

        obj = propResolver.getValue(c, "b.foo");
        assertEquals(123, obj);

        assertThrows(FacesException.class, () -> propResolver.getValue(b, "unknown"));
    }

    @Test
    void getValueReadOnly() {
        PropertyDescriptorResolver propResolver = new PropertyDescriptorResolver.DefaultResolver();

        F f = new F(true, false, "test");
        assertEquals(true, propResolver.getValue(f, "readOnly1"));
        assertEquals(false, propResolver.getValue(f, "readOnly2"));
        assertEquals("test", propResolver.getValue(f, "readOnly3"));

        assertNull(propResolver.get(f.getClass(), "readOnly1").getWriteMethod());
        assertNull(propResolver.get(f.getClass(), "readOnly2").getWriteMethod());
        assertNull(propResolver.get(f.getClass(), "readOnly3").getWriteMethod());
    }

    @Test
    void setValue() {
        PropertyDescriptorResolver propResolver = new PropertyDescriptorResolver.DefaultResolver();

        A a = new A(null, false, false);
        assertNull(propResolver.getValue(a, "name"));
        propResolver.setValue(a, "name", "Hans");
        assertEquals("Hans", propResolver.getValue(a, "name"));

        B b = new B("b", true, Boolean.FALSE, DummyEnum.X);
        D d = new D("d", false, Boolean.FALSE, DummyEnum.Z, 123);
        C c = new C(d);

        assertNotNull(b);
        assertEquals(123, propResolver.getValue(c, "b.foo"));
        propResolver.setValue(c, "b.foo", 124);
        assertEquals(124, propResolver.getValue(c, "b.foo"));

        assertThrows(FacesException.class, () -> propResolver.setValue(new C(null), "b.dummyEnum", null));
    }

    private class A {

        private String name;
        private boolean bool1;
        private Boolean bool2;

        public A(String name, boolean bool1, Boolean bool2) {
            this.name = name;
            this.bool1 = bool1;
            this.bool2 = bool2;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public boolean isBool1() {
            return bool1;
        }

        public void setBool1(boolean bool1) {
            this.bool1 = bool1;
        }

        public Boolean getBool2() {
            return bool2;
        }

        public void setBool2(Boolean bool2) {
            this.bool2 = bool2;
        }
    }

    private class B extends A {

        private DummyEnum dummyEnum;

        public B(String name, boolean bool1, Boolean bool2, DummyEnum dummyEnum) {
            super(name, bool1, bool2);
            this.dummyEnum = dummyEnum;
        }

        public DummyEnum getDummyEnum() {
            return dummyEnum;
        }

        public void setDummyEnum(DummyEnum dummyEnum) {
            this.dummyEnum = dummyEnum;
        }
    }

    private class D extends B {

        private int foo;

        public D(String name, boolean bool1, Boolean bool2, DummyEnum dummyEnum, int foo) {
            super(name, bool1, bool2, dummyEnum);
            this.foo = foo;
        }

        public int getFoo() {
            return foo;
        }

        public void setFoo(int foo) {
            this.foo = foo;
        }
    }

    private class C {
        private B b;

        public C(B b) {
            this.b = b;
        }

        public B getB() {
            return b;
        }

        public void setB(B b) {
            this.b = b;
        }
    }

    private enum DummyEnum {
        X,
        Y,
        Z;
    }

    public class F {
        private boolean readOnly1;
        private boolean readOnly2;
        private String readOnly3;

        public F(boolean readOnly1, boolean readOnly2, String readOnly3) {
            this.readOnly1 = readOnly1;
            this.readOnly2 = readOnly2;
            this.readOnly3 = readOnly3;
        }

        public boolean isReadOnly1() {
            return readOnly1;
        }

        public boolean isReadOnly2() {
            return readOnly2;
        }

        public String getReadOnly3() {
            return readOnly3;
        }
    }
}
