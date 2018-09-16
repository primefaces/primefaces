/*
	Copyright 2017 Marceau Dewilde <m@ceau.be>

	Licensed under the Apache License, Version 2.0 (the "License");
	you may not use this file except in compliance with the License.
	You may obtain a copy of the License at

		http://www.apache.org/licenses/LICENSE-2.0

	Unless required by applicable law or agreed to in writing, software
	distributed under the License is distributed on an "AS IS" BASIS,
	WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
	See the License for the specific language governing permissions and
	limitations under the License.
*/
package org.primefaces.model.chartjs.enums;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;
import java.util.Locale;

public enum BorderJoinStyle {

	/**
	 * Rounds off the corners of a shape by filling an
	 * additional sector of disc centered at the common endpoint of connected
	 * segments. The radius for these rounded corners is equal to the line
	 * width.
	 */
	ROUND,
	
	/**
	 * Fills an additional triangular area between the common
	 * endpoint of connected segments, and the separate outside rectangular
	 * corners of each segment.
	 */
	BEVEL,
	
	/**
	 * Connected segments are joined by extending their outside edges to connect
	 * at a single point, with the effect of filling an additional
	 * lozenge-shaped area. This setting is effected by the miterLimit property.
	 */
	MITER;

	private final String serialized;

	private BorderJoinStyle() {
        serialized = name().toLowerCase(Locale.ENGLISH);
	}

	@Override
	public String toString() {
		return serialized;
	}

}
