package org.primefaces.util;

import java.beans.PropertyDescriptor;
import javax.faces.FacesException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class PropertyDescriptorResolverTest {

    @Test
    public void testGetPropertyDescriptor() {
        PropertyDescriptorResolver propResolver = new PropertyDescriptorResolver.DefaultResolver();

        B b = new B("b", true, Boolean.FALSE, DummyEnum.X);

        PropertyDescriptor pd = propResolver.get(A.class, "name");
        Assertions.assertNotNull(pd);
        Assertions.assertEquals(String.class, pd.getPropertyType());

        pd = propResolver.get(B.class, "bool1");
        Assertions.assertNotNull(pd);
        Assertions.assertEquals(boolean.class, pd.getPropertyType());

        pd = propResolver.get(B.class, "bool2");
        Assertions.assertNotNull(pd);
        Assertions.assertEquals(Boolean.class, pd.getPropertyType());

        pd = propResolver.get(C.class, "b.dummyEnum");
        Assertions.assertNotNull(pd);
        Assertions.assertEquals(DummyEnum.class, pd.getPropertyType());

        Assertions.assertThrows(FacesException.class, () -> propResolver.get(C.class, "unknown"));
    }

    @Test
    public void testGetValueFromPropertyDescriptor() {
        PropertyDescriptorResolver propResolver = new PropertyDescriptorResolver.DefaultResolver();

        B b = new B("b", true, Boolean.FALSE, DummyEnum.X);
        C c = new C(b);

        Object obj = propResolver.getValue(c, "b.dummyEnum");
        Assertions.assertEquals(DummyEnum.X, obj);

        obj = propResolver.getValue(c, "b");
        Assertions.assertEquals(b, obj);

        Assertions.assertThrows(FacesException.class, () -> propResolver.getValue(b, "unknown"));
    }

    private abstract class A {

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
}
