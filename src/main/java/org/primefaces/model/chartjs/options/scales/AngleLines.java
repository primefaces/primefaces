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
package org.primefaces.model.chartjs.options.scales;

import org.primefaces.model.chartjs.color.Color;

/**
 * Options to configure angled lines that radiate from the center of the chart
 * to the point labels.
 */
public class AngleLines {

	/**
	 * @see #setDisplay(Boolean)
	 */
	private Boolean display;

	/**
	 * @see #setColor(Color)
	 */
	private Color color;

	/**
	 * @see #setLineWidth(Integer)
	 */
	private Integer lineWidth;

	/**
	 * @see #setDisplay(Boolean)
	 */
	public Boolean getDisplay() {
	    return display;
	}

	/**
	 * <p>
	 * If true, angle lines are shown.
	 * </p>
	 * 
	 * <p>
	 * Default {@code true}
	 * </p>
	 */
	public AngleLines setDisplay(Boolean display) {
	    this.display = display;
	    return this;
	}

	/**
	 * @see #setColor(Color)
	 */
	public Color getColor() {
	    return color;
	}

	/**
	 * <p>
	 * Color of angled lines
	 * </p>
	 * 
	 * <p>
	 * Default {@code 'rgba(0, 0, 0, 0.1)'}
	 * </p>
	 */
	public AngleLines setColor(Color color) {
	    this.color = color;
	    return this;
	}

	/**
	 * @see #setLineWidth(Integer)
	 */
	public Integer getLineWidth() {
	    return lineWidth;
	}

	/**
	 * <p>
	 * Width of angled lines
	 * </p>
	 * 
	 * <p>
	 * Default {@code 1}
	 * </p>
	 */
	public AngleLines setLineWidth(Integer lineWidth) {
	    this.lineWidth = lineWidth;
	    return this;
	}

}
