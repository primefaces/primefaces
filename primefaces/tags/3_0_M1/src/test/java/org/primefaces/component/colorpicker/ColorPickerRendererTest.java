/*
 * Copyright 2009 Prime Technology.
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
package org.primefaces.component.colorpicker;

import java.awt.Color;

import javax.faces.convert.ConverterException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class ColorPickerRendererTest {

	private ColorPickerRenderer renderer;

	@Before
	public void setup() {
		renderer = new ColorPickerRenderer();
	}

	@After
	public void after() {
		renderer = null;
	}

	@Test
	public void formatAsRGB() {
		ColorPicker picker = new ColorPicker();
		Color color = new Color(150, 200, 210);		
		picker.setValue(color);

		String rgb = renderer.getValueAsString(null, picker);
		
		assertEquals("150,200,210", rgb);
		
		picker.setValue(null);
		
		rgb = renderer.getValueAsString(null, picker);
		assertEquals(null, rgb);
	}

	@Test
	public void shouldConvertSubmittedValueAsColor() {
		ColorPicker picker = new ColorPicker();
		
		Color color = (Color) renderer.getConvertedValue(null, picker, "150,200,100");
		
		assertEquals(150, color.getRed());
		assertEquals(200, color.getGreen());
		assertEquals(100, color.getBlue());
	}

	@Test
	public void shouldThrowConverterExceptionForBadInput() {
		ColorPicker picker = new ColorPicker();
		
		try {
			renderer.getConvertedValue(null, picker, "realmadrid");
			fail();
		}catch(ConverterException exception) {
			
		}
	}
}