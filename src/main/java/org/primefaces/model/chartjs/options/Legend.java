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
package org.primefaces.model.chartjs.options;

import org.primefaces.model.chartjs.javascript.JavaScriptFunction;

import java.util.Locale;

public class Legend {

	/**
	 * @see #setDisplay(Boolean)
	 */
	private Boolean display;

	/**
	 * @see #setPosition(Legend.Position)
	 */
	private Position position;

	/**
	 * @see #setFullWidth(Boolean)
	 */
	private Boolean fullWidth;

	/**
	 * @see #setOnClick(JavaScriptFunction)
	 */
	private JavaScriptFunction onClick;

	/**
	 * @see #setLabels(LegendLabels)
	 */
	private LegendLabels labels;

	/**
	 * @see #setDisplay(Boolean)
	 */
	public Boolean getDisplay() {
	    return display;
	}

	/**
	 * Default {@code true}
	 * 
	 * Is the legend displayed
	 */
	public Legend setDisplay(Boolean display) {
	    this.display = display;
	    return this;
	}

	/**
	 * @see #setPosition(Legend.Position)
	 */
	public Position getPosition() {
	    return position;
	}

	/**
	 * Default {@code 'top'}
	 * 
	 * Position of the legend. Options are 'top' or 'bottom'
	 */
	public Legend setPosition(Legend.Position position) {
	    this.position = position;
	    return this;
	}

	/**
	 * @see #setFullWidth(Boolean)
	 */
	public Boolean getFullWidth() {
	    return fullWidth;
	}

	/**
	 * Default {@code true}
	 * 
	 * Marks that this box should take the full width of the canvas (pushing down other boxes)
	 */
	public Legend setFullWidth(Boolean fullWidth) {
	    this.fullWidth = fullWidth;
	    return this;
	}

	/**
	 * @see #setOnClick(JavaScriptFunction)
	 */
	public JavaScriptFunction getOnClick() {
	    return onClick;
	}

	/**
	 * Default {@code function(event, legendItem) {}}
	 * 
	 * A callback that is called when a click is registered on top of a label item
	 */
	public Legend setOnClick(JavaScriptFunction onClick) {
	    this.onClick = onClick;
	    return this;
	}

	/**
	 * @see #setLabels(LegendLabels)
	 */
	public LegendLabels getLabels() {
	    return labels;
	}

	/**
	 * Default {@code -}
	 * 
	 * See the Legend Label Configuration section below.
	 */
	public Legend setLabels(LegendLabels labels) {
	    this.labels = labels;
	    return this;
	}
	
	public static enum Position {

		TOP,
		BOTTOM,
		LEFT,
		RIGHT;

		private final String serialized;

		private Position() {
			serialized = name().toLowerCase(Locale.ENGLISH);
		}

		@Override
		public String toString() {
			return serialized;
		}
	}
	
}
