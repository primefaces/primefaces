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

import org.primefaces.model.chartjs.enums.HoverMode;
import org.primefaces.model.chartjs.javascript.JavaScriptFunction;

public class Hover {

	/**
	 * @see #setMode(HoverMode)
	 */
	private HoverMode mode;

	/**
	 * @see #setAnimationDuration(Integer)
	 */
	private Integer animationDuration;

	/**
	 * @see #setOnHover(JavaScriptFunction)
	 */
	private JavaScriptFunction onHover;

	/**
	 * @see #setMode(HoverMode)
	 */
	public HoverMode getMode() {
		return mode;
	}

	/**
	 * <p>
	 * Default {@code single}
	 * </p>
	 * 
	 * <p>
	 * Sets which elements hover. Acceptable options are 'single', 'label',
	 * 'x-axis', or 'dataset'.
	 * </p>
	 * <ul>
	 * <li>{@code single} highlights the closest element.
	 * <li>{@code label} highlights elements in all datasets at the same X value.
	 * <li>{@code x-axis} also highlights elements in all datasets at the same X
	 * value, but activates when hovering anywhere within the vertical slice of
	 * the x-axis representing that X value.
	 * <li>{@code dataset} highlights the closest dataset.
	 * </ul>
	 */
	public Hover setMode(HoverMode mode) {
		this.mode = mode;
	    return this;
	}

	/**
	 * @see #setAnimationDuration(Integer)
	 */
	public Integer getAnimationDuration() {
		return animationDuration;
	}

	/**
	 * Default {@code 400}<br>
	 * 
	 * Duration in milliseconds it takes to animate hover style changes
	 */
	public Hover setAnimationDuration(Integer animationDuration) {
		this.animationDuration = animationDuration;
	    return this;
	}

	/**
	 * @see #setOnHover(JavaScriptFunction)
	 */
	public JavaScriptFunction getOnHover() {
		return onHover;
	}

	/**
	 * Default {@code null}<br>
	 * 
	 * Called when any of the events fire. Called in the context of the chart
	 * and passed an array of active elements (bars, points, etc)
	 */
	public Hover setOnHover(JavaScriptFunction onHover) {
		this.onHover = onHover;
	    return this;
	}

}
