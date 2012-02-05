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
package org.primefaces.util;

public class ArrayUtils {
	
	private ArrayUtils() {}

	public static boolean contains(String[] array, String searchedText) {

		if (array == null || array.length == 0)
			return false;

		for (int i = 0; i < array.length; i++) {
			if (array[i].equalsIgnoreCase(searchedText))
				return true;
		}

		return false;
	}
	
	public static String[] concat(String[] array1, String[] array2) {
		int length1 = array1.length;
		int length2 = array2.length;
		int length = length1 + length2;
		
		String[] dest = new String[length];
		
		System.arraycopy(array1, 0, dest, 0, length1);
		System.arraycopy(array2, 0, dest, length1, length2);
		
		return dest;
	}
	
	public static String[] concat(String[]... arrays) {
		int destSize = 0;
		for (int i = 0; i < arrays.length; i++) {
			destSize += arrays[i].length;
		}
		String[] dest = new String[destSize];
		int lastIndex = 0;
		for (int i = 0; i < arrays.length; i++) {
			String[] array = arrays[i];
			System.arraycopy(array, 0, dest, lastIndex, array.length);
			lastIndex += array.length;
		}
		
		return dest;
	}
}
