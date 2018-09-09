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
package org.primefaces.model.chartjs.options.elements;

import org.primefaces.model.chartjs.color.Color;

/**
 * <p>
 * Arcs are used in the polar area, doughnut and pie charts.
 * </p>
 * <p>
 * When set, these options apply to all objects of that type unless specifically
 * overridden by the configuration attached to a dataset.
 * </p>
 */
public class Arc {

	/**
	 * @see #setBackgroundColor(Color)
	 */
	private Color backgroundColor;

	/**
	 * @see #setBorderColor(Color)
	 */
	private Color borderColor;

	/**
	 * @see #setBorderWidth(Integer)
	 */
	private Integer borderWidth;

	/**
	 * @see #setBackgroundColor(Color)
	 */
	public Color getBackgroundColor() {
		return this.backgroundColor;
	}

	/**
	 * <p>
	 * Default fill color for arcs. Inherited from the global default
	 * </p>
	 * 
	 * <p>
	 * Default {@code 'rgba(0,0,0,0.1)'}
	 * </p>
	 */
	public Arc setBackgroundColor(Color backgroundColor) {
		this.backgroundColor = backgroundColor;
		return this;
	}

	/**
	 * @see #setBorderColor(Color)
	 */
	public Color getBorderColor() {
		return this.borderColor;
	}

	/**
	 * <p>
	 * Default stroke color for arcs
	 * </p>
	 * 
	 * <p>
	 * Default {@code '#fff'}
	 * </p>
	 */
	public Arc setBorderColor(Color borderColor) {
		this.borderColor = borderColor;
		return this;
	}

	/**
	 * @see #setBorderWidth(Integer)
	 */
	public Integer getBorderWidth() {
		return this.borderWidth;
	}

	/**
	 * <p>
	 * Default stroke width for arcs
	 * </p>
	 * 
	 * <p>
	 * Default {@code 2}
	 * </p>
	 */
	public Arc setBorderWidth(Integer borderWidth) {
		this.borderWidth = borderWidth;
		return this;
	}

}
