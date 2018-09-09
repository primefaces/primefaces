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

public enum BorderCapStyle {

	/***
	 * The ends of lines are squared off at the endpoints.
	 */
	BUTT,

	/**
	 * The ends of lines are rounded.
	 */
	ROUND,

	/**
	 * The ends of lines are squared off by adding a box with an equal width and
	 * half the height of the line's thickness.
	 */
	SQUARE;

	private final String serialized;

	private BorderCapStyle() {
		serialized = name().toLowerCase(Locale.ENGLISH);
	}

	@Override
	public String toString() {
		return serialized;
	}
}
