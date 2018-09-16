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
package org.primefaces.model.chartjs.options.layout;

/**
 * Options to influence the layout of the chart in its canvas.
 */
public class Layout {

	private Object padding;

	public Object getPadding() {
		return padding;
	}

	/**
	 * No-arg constructor
	 */
	public Layout() {
		
	}
	
	/**
	 * Constructor
	 * 
	 * @param padding
	 *            {@link Integer} or {@code null}
	 */
	public Layout(Integer padding) {
		this.padding = padding;
	}
	
	/**
	 * Constructor
	 * 
	 * @param padding
	 *            {@link Padding} or [@code null}
	 */
	public Layout(Padding padding) {
		this.padding = padding;
	}

	/**
	 * Using an {@link Integer} sets the padding globally for all sides of the
	 * chart (left, top, right, bottom).
	 * 
	 * @param padding
	 *            {@link Integer} or {@code null}
	 */
	public Layout setPadding(Integer padding) {
		this.padding = padding;
		return this;
	}

	/**
	 * Using a {@link Padding} instance allows specifying 4 padding properties
	 * separately.
	 * 
	 * @param padding
	 *            {@link Padding} or [@code null}
	 */
	public Layout setPadding(Padding padding) {
		this.padding = padding;
		return this;
	}

}
