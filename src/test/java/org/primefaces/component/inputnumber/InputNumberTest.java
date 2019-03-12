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
package org.primefaces.component.inputnumber;

import org.junit.*;
import org.junit.rules.ExpectedException;

import javax.el.ELContext;
import javax.el.ValueExpression;
import javax.faces.FacesException;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class InputNumberTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private InputNumberRenderer renderer;
    private FacesContext context;
    private ExternalContext externalContext;
    private ELContext elContext;
    private InputNumber inputNumber;
    private ValueExpression valueExpression;

    @Before
    public void setup() {
        renderer = new InputNumberRenderer();
        context = mock(FacesContext.class);
        externalContext = mock(ExternalContext.class);
        elContext = mock(ELContext.class);
        when(context.getExternalContext()).thenReturn(externalContext);
        when(context.getELContext()).thenReturn(elContext);
        inputNumber = mock(InputNumber.class);
        when(inputNumber.getClientId(context)).thenReturn("");
        when(inputNumber.isValid()).thenReturn(true);
        when(inputNumber.getSubmittedValue()).thenCallRealMethod();
        doCallRealMethod().when(inputNumber).setSubmittedValue(anyString());
    }

    @After
    public void teardown() {
        valueExpression = null;
        inputNumber = null;
        elContext = null;
        externalContext = null;
        context = null;
        renderer = null;
    }

    private void setupValues(String submittedValue, boolean disabled, String minValue, String maxValue, boolean primitiveValueBinding) {
        Map<String, String> requestParams = new HashMap<>();
        requestParams.put("_hinput", submittedValue);
        when(externalContext.getRequestParameterMap()).thenReturn(requestParams);
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("readonly", disabled);
        attributes.put("disabled", disabled);
        when(inputNumber.getAttributes()).thenReturn(attributes);
        when(inputNumber.isDisabled()).thenReturn(disabled);
        when(inputNumber.isReadonly()).thenReturn(disabled);
        when(inputNumber.getMinValue()).thenReturn(minValue);
        when(inputNumber.getMaxValue()).thenReturn(maxValue);
        valueExpression = mock(ValueExpression.class);
        when(inputNumber.getValueExpression(anyString())).thenReturn(valueExpression);
        if (primitiveValueBinding) {
            when(valueExpression.getType(elContext)).thenReturn((Class) double.class);
        }
    }

    @Test
    public void testDecodeNegativeWithinDefaultRange() {
        setupValues("-999999999.99", false, null, null, false);
        renderer.decode(context, inputNumber);
        Assert.assertEquals("-999999999.99", inputNumber.getSubmittedValue());
    }

    @Test
    public void testDecodeZeroWithinDefaultRange() {
        setupValues("0", false, null, null, false);
        renderer.decode(context, inputNumber);
        Assert.assertEquals("0", inputNumber.getSubmittedValue());
    }

    @Test
    public void testDecodePositiveWithinDefaultRange() {
        setupValues("999999999.99", false, null, null, false);
        renderer.decode(context, inputNumber);
        Assert.assertEquals("999999999.99", inputNumber.getSubmittedValue());
    }

    @Test
    public void testDecodeWithinRange() {
        setupValues("3.14", false, "-1", "5", false);
        renderer.decode(context, inputNumber);
        Assert.assertEquals("3.14", inputNumber.getSubmittedValue());
    }

    @Test
    public void testDecodeBelowRange() {

        setupValues("-1", false, "0.0", null, false);
        renderer.decode(context, inputNumber);
        Assert.assertEquals("0.0", inputNumber.getSubmittedValue());
    }

    @Test
    public void testDecodeAboveRange() {
        setupValues("2", false, "0.0", "1.0", false);
        renderer.decode(context, inputNumber);
        Assert.assertEquals("1.0", inputNumber.getSubmittedValue());
    }

    @Test
    public void testDecodeInvalidNumber() {
        expectedException.expect(FacesException.class);
        expectedException.expectMessage("Invalid number");
        setupValues("crash", false, null, null, false);
        renderer.decode(context, inputNumber);
    }

    @Test
    public void testDecodeEmptyNonPrimitive() {
        setupValues("", false, "1", "10", false);
        renderer.decode(context, inputNumber);
        Assert.assertEquals("", inputNumber.getSubmittedValue());
    }

    @Test
    public void testDecodeEmptyPrimitive() {
        setupValues("", false, "1", "10", true);
        renderer.decode(context, inputNumber);
        double submittedValue = Double.parseDouble(inputNumber.getSubmittedValue().toString());
        Assert.assertTrue(submittedValue >= 1 && submittedValue <= 10);
    }

    @Test
    public void testDecodeEmptyPrimitiveWithoutMinValue() {
        setupValues("", false, null, "10.0", true);
        renderer.decode(context, inputNumber);
        double submittedValue = Double.parseDouble(inputNumber.getSubmittedValue().toString());
        Assert.assertEquals("10.0", String.valueOf(submittedValue));
    }

    @Test
    public void testDecodeDisabled() {
        setupValues("1", true, "0", "2", false);
        renderer.decode(context, inputNumber);
        Assert.assertEquals(null, inputNumber.getSubmittedValue());
    }

}
