/*
 * Copyright 2009-2013 PrimeTek.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.primefaces.component.inputnumber;

import org.junit.*;
import org.junit.rules.ExpectedException;

import javax.el.ELContext;
import javax.el.ValueExpression;
import javax.faces.FacesException;
import javax.faces.component.behavior.ClientBehavior;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.assertEquals;

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
		context = niceMock(FacesContext.class);
		externalContext = niceMock(ExternalContext.class);
		elContext = niceMock(ELContext.class);
		expect(context.getExternalContext()).andReturn(externalContext).anyTimes();
		expect(context.getELContext()).andReturn(elContext).anyTimes();
		inputNumber = partialMockBuilder(InputNumber.class)
				.addMockedMethods("getClientId", "getClientBehaviors", "isDisabled", "isReadonly", "getMinValue", "getMaxValue", "getValueExpression", "isValid").createMock();
		expect(inputNumber.getClientId(context)).andReturn("").anyTimes();
		expect(inputNumber.getClientBehaviors()).andReturn(new HashMap<String, List<ClientBehavior>>()).anyTimes();
		expect(inputNumber.isValid()).andReturn(true).anyTimes();
		replay(context);
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
		Map<String, String> requestParams = new HashMap<String, String>();
		requestParams.put("_hinput", submittedValue);
		expect(externalContext.getRequestParameterMap()).andReturn(requestParams).anyTimes();
		expect(inputNumber.isDisabled()).andReturn(disabled).anyTimes();
		expect(inputNumber.isReadonly()).andReturn(disabled).anyTimes();
		expect(inputNumber.getMinValue()).andReturn(minValue).anyTimes();
		expect(inputNumber.getMaxValue()).andReturn(maxValue).anyTimes();
		valueExpression = niceMock(ValueExpression.class);
		expect(inputNumber.getValueExpression(anyObject(String.class))).andReturn(valueExpression).anyTimes();
		if (primitiveValueBinding) {
			expect(valueExpression.getType(elContext)).andReturn((Class) double.class).anyTimes();
		}
		replay(externalContext, valueExpression, inputNumber);
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
		Assert.assertEquals("10.0", inputNumber.getSubmittedValue());
	}
	
	@Test
	public void testDecodeDisabled() {
		setupValues("1", true, "0", "2", false);
		renderer.decode(context, inputNumber);
		Assert.assertEquals(null, inputNumber.getSubmittedValue());
	}
	
}