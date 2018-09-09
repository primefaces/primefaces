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

import java.util.Locale;

public enum HoverMode {

	/**
	 * highlights the closest element
	 */
	SINGLE,
	
	/**
	 * highlights elements in all datasets at the same X value
	 */
	LABEL,
	
	/**
	 * highlights elements in all datasets at the same X
	 * value, activates when hovering anywhere within the vertical slice of
	 * the x-axis representing that X value.
	 */
	X_AXIS,
	
	/**
	 * highlights the closest dataset
	 */
	DATASET;

	private final String serialized;

	private HoverMode() {
		serialized = name().toLowerCase(Locale.ENGLISH);
	}

	@Override
	public String toString() {
		return serialized;
	}

}
