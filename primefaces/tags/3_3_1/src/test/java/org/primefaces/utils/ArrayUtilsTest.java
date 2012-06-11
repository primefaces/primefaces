/*
 * Copyright 2009-2012 Prime Teknoloji.
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
package org.primefaces.utils;

import static org.junit.Assert.*;

import org.junit.Test;
import org.primefaces.util.ArrayUtils;

public class ArrayUtilsTest {

	@Test
	public void shouldConcatTwoArrays() {
		String[] arr1 = new String[]{"a","b","c"};
		String[] arr2 = new String[]{"d","e","f"};
		
		String[] result = ArrayUtils.concat(arr1, arr2);
		
		assertEquals(3, arr1.length);
		assertEquals(3, arr2.length);
		assertEquals(6, result.length);
		
		assertEquals("a", result[0]);
		assertEquals("b", result[1]);
		assertEquals("c", result[2]);
		assertEquals("d", result[3]);
		assertEquals("e", result[4]);
		assertEquals("f", result[5]);
	}
}
