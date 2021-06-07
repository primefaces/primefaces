package org.primefaces.renderkit;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Objects;

import javax.el.ELContext;
import javax.el.ELException;
import javax.el.ExpressionFactory;
import javax.faces.application.Application;
import javax.faces.component.UIComponent;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.invocation.InvocationOnMock;
import org.primefaces.component.selectonemenu.SelectOneMenu;

public class SelectRendererTest {
    private static class ErrorString {
        private String value;

        public ErrorString(String value) {
            this.value = value;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            ErrorString other = (ErrorString) obj;
            return Objects.equals(value, other.value);
        }

        public String getValue() {
            return value;
        }

        @Override
        public int hashCode() {
            return Objects.hash(value);
        }

        public void setValue(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return value;
        }
    }

    private static class KvPair {
        private String key;
        private String value;

        public KvPair(String key, String value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            KvPair other = (KvPair) obj;
            return Objects.equals(key, other.key) && Objects.equals(value, other.value);
        }

        public String getKey() {
            return key;
        }

        public String getValue() {
            return value;
        }

        @Override
        public int hashCode() {
            return Objects.hash(key, value);
        }

        public void setKey(String key) {
            this.key = key;
        }

        public void setValue(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return key + "-" + value;
        }
    }

    private static class KvPairConverter implements Converter<KvPair> {
        @Override
        public KvPair getAsObject(FacesContext ctx, UIComponent comp, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            String[] parts = value.split("-");
            return new KvPair(parts[0], parts[1]);
        }

        @Override
        public String getAsString(FacesContext ctx, UIComponent comp, KvPair value) {
            if (value == null) {
                return null;
            }
            return value.toString();
        }
    }
    private SelectRenderer renderer;
    private FacesContext context;
    private ExternalContext externalContext;
    private ELContext elContext;
    private UIComponent component;

    private Application application;

    private ExpressionFactory ef;

    private Converter<KvPair> converter;

    private ErrorString coerceToErrorString(InvocationOnMock invocation) {
        Object arg = invocation.getArgument(0);
        String str = arg != null ? arg.toString() : null;
        if ("err".equals(str)) {
            throw new ELException("Error during coerce");
        }
        return new ErrorString(str + str);
    }

    private KvPair coerceToKeyValuePair(InvocationOnMock invocation) {
        Object arg = invocation.getArgument(0);
        return arg != null ? converter.getAsObject(null, null, arg.toString()) : null;
    }

    private String coerceToString(InvocationOnMock invocation) {
        Object arg = invocation.getArgument(0);
        return arg != null ? arg.toString() : null;
    }

    @Test
    public void isSelected_converter() {
        KvPair kv1 = new KvPair("", "");
        KvPair kv2 = new KvPair("foo", "");
        KvPair kv3 = new KvPair("", "bar");
        KvPair kv4 = new KvPair("foo", "bar");
        KvPair kv5 = new KvPair("baz", "bom");

        assertFalse(renderer.isSelected(context, component, kv2, new KvPair[] {}, converter));
        assertFalse(renderer.isSelected(context, component, kv3, new KvPair[] { kv1 }, converter));
        assertFalse(renderer.isSelected(context, component, kv4, new KvPair[] { kv1 }, converter));
        assertTrue(renderer.isSelected(context, component, kv1, new KvPair[] { kv1 }, converter));
        assertTrue(renderer.isSelected(context, component, kv1, new KvPair[] { kv1, kv2 }, converter));
        assertTrue(renderer.isSelected(context, component, kv2, new KvPair[] { kv1, kv2 }, converter));
        assertTrue(renderer.isSelected(context, component, kv5, new KvPair[] { kv1, kv2, kv3, kv4, kv5 }, converter));
    }

    @Test
    public void isSelected_converter_nonStringValue_stringArray() {
        KvPair kv1 = new KvPair("foo", "bar");
        KvPair kv2 = new KvPair("baz", "bom");

        assertFalse(renderer.isSelected(context, component, kv1, kv1.toString(), converter));
        assertFalse(renderer.isSelected(context, component, kv1, kv2.toString(), converter));
    }

    @Test
    public void isSelected_converter_stringValue_nonStringArray() {
        KvPair kv1 = new KvPair("foo", "bar");
        KvPair kv2 = new KvPair("baz", "bom");

        assertTrue(renderer.isSelected(context, component, kv1.toString(), new KvPair[] { kv1 }, converter));
        assertFalse(renderer.isSelected(context, component, kv1.toString(), new KvPair[] { kv2 }, converter));
    }

    @Test
    public void isSelected_converter_stringValue_stringArray() {
        assertFalse(renderer.isSelected(context, component, "foo", new String[] {}, converter));
        assertFalse(renderer.isSelected(context, component, "foo", new String[] { "" }, converter));
        assertFalse(renderer.isSelected(context, component, "foo", new String[] { "bar" }, converter));
        assertTrue(renderer.isSelected(context, component, "", new String[] { "" }, converter));
        assertTrue(renderer.isSelected(context, component, "foo", new String[] { "foo" }, converter));
        assertTrue(renderer.isSelected(context, component, "foo", new String[] { "bar", "foo" }, converter));
    }

    @Test
    public void isSelected_noConverter() {
        assertFalse(renderer.isSelected(context, component, "foo", new String[] {}, null));
        assertFalse(renderer.isSelected(context, component, "foo", new String[] { "" }, null));
        assertFalse(renderer.isSelected(context, component, "foo", new String[] { "bar" }, null));
        assertTrue(renderer.isSelected(context, component, "", new String[] { "" }, null));
        assertTrue(renderer.isSelected(context, component, "foo", new String[] { "foo" }, null));
        assertTrue(renderer.isSelected(context, component, "foo", new String[] { "bar", "foo" }, null));
    }

    @Test
    public void isSelected_noConverter_exceptionDuringCoerce() {
        assertTrue(renderer.isSelected(context, component, new ErrorString("foo"),
                new ErrorString[] { new ErrorString("foofoo") }, null));
        assertTrue(renderer.isSelected(context, component, new ErrorString("err"),
                new ErrorString[] { new ErrorString("err") }, null));
        assertFalse(renderer.isSelected(context, component, new ErrorString("er"),
                new ErrorString[] { new ErrorString("er") }, null));
    }

    @Test
    public void isSelected_nullArguments() {
        assertTrue(renderer.isSelected(context, component, null, null, null));
        assertFalse(renderer.isSelected(context, component, null, new String[] { "" }, null));
        assertFalse(renderer.isSelected(context, component, "", null, null));
        assertFalse(renderer.isSelected(context, component, "", new String[] { null }, null));
        assertTrue(renderer.isSelected(context, component, null, new String[] { null }, null));
        assertFalse(renderer.isSelected(context, component, "", null, null));
        assertFalse(renderer.isSelected(context, component, new KvPair("", ""), null, null));

        assertTrue(renderer.isSelected(context, component, null, null, converter));
        assertFalse(renderer.isSelected(context, component, null, new String[] { "" }, converter));
        assertFalse(renderer.isSelected(context, component, "", null, converter));
        assertFalse(renderer.isSelected(context, component, "", new String[] { null }, converter));
        assertTrue(renderer.isSelected(context, component, null, new String[] { null }, converter));
        assertFalse(renderer.isSelected(context, component, "", null, converter));
        assertFalse(renderer.isSelected(context, component, new KvPair("", ""), null, converter));
    }

    @Test
    public void isSelected_refEqualArguments() {
        String str = "foo";
        String[] strArr = new String[] { "" };
        KvPair kvPair = new KvPair("foo", "bar");

        assertTrue(renderer.isSelected(context, component, str, str, null));
        assertTrue(renderer.isSelected(context, component, strArr, strArr, null));
        assertTrue(renderer.isSelected(context, component, kvPair, kvPair, null));

        assertTrue(renderer.isSelected(context, component, str, str, converter));
        assertTrue(renderer.isSelected(context, component, strArr, strArr, converter));
        assertTrue(renderer.isSelected(context, component, kvPair, kvPair, converter));
    }

    @Test
    public void isSelected_valueArrayNotAnArray() {
        assertTrue(renderer.isSelected(context, component, new String(""), new String(""), null));
        assertFalse(renderer.isSelected(context, component, new String("foo"), new String(""), null));
        assertTrue(renderer.isSelected(context, component, new KvPair("a", "b"), new KvPair("a", "b"), null));
        assertFalse(renderer.isSelected(context, component, new KvPair("a", "b"), new KvPair("a", "c"), null));

        assertTrue(renderer.isSelected(context, component, new String(""), new String(""), converter));
        assertFalse(renderer.isSelected(context, component, new String("foo"), new String(""), converter));
        assertTrue(renderer.isSelected(context, component, new KvPair("a", "b"), new KvPair("a", "b"), converter));
        assertFalse(renderer.isSelected(context, component, new KvPair("a", "b"), new KvPair("a", "c"), converter));
    }

    @BeforeEach
    public void setup() {
        renderer = mock(SelectRenderer.class);
        when(renderer.isSelected(any(), any(), any(), any(), any())).thenCallRealMethod();
        when(renderer.isSelectValueEqual(any(), any(), any(), any(), any())).thenCallRealMethod();
        when(renderer.coerceToModelType(any(), any(), any())).thenCallRealMethod();

        context = mock(FacesContext.class);

        converter = new KvPairConverter();

        ef = mock(ExpressionFactory.class);
        application = mock(Application.class);
        externalContext = mock(ExternalContext.class);
        elContext = mock(ELContext.class);
        when(ef.coerceToType(any(), eq(String.class))).then(this::coerceToString);
        when(ef.coerceToType(any(), eq(KvPair.class))).then(this::coerceToKeyValuePair);
        when(ef.coerceToType(any(), eq(ErrorString.class))).then(this::coerceToErrorString);
        when(application.getExpressionFactory()).thenReturn(ef);
        when(context.getApplication()).thenReturn(application);
        when(context.getExternalContext()).thenReturn(externalContext);
        when(context.getELContext()).thenReturn(elContext);

        component = new SelectOneMenu();
    }

    @AfterEach
    public void teardown() {
    }
}
