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
package org.primefaces.model.chartjs.options.animation;

public class PolarAnimation extends Animation<PolarAnimation> {

	/**
	 * Default {@code true}
	 * 
	 * @see #setAnimateRotate(Boolean)
	 */
	private Boolean animateRotate;

	/**
	 * Default {@code true}
	 * 
	 * @see #setAnimateScale(Boolean)
	 */
	private Boolean animateScale;

	/**
	 * @see #setAnimateRotate(Boolean)
	 */
	public Boolean getAnimateRotate() {
	    return this.animateRotate;
	}

	/**
	 * If true, will animate the rotation of the chart.
	 */
	public PolarAnimation setAnimateRotate(Boolean animateRotate) {
	    this.animateRotate = animateRotate;
	    return this;
	}

	/**
	 * @see #setAnimateScale(Boolean)
	 */
	public Boolean getAnimateScale() {
	    return this.animateScale;
	}

	/**
	 * If true, will animate scaling the chart.
	 */
	public PolarAnimation setAnimateScale(Boolean animateScale) {
	    this.animateScale = animateScale;
	    return this;
	}

}
