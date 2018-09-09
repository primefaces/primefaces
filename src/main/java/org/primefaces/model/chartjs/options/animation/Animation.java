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

import org.primefaces.model.chartjs.enums.Easing;
import org.primefaces.model.chartjs.javascript.JavaScriptFunction;

public class Animation<T extends Animation<T>> {

	/**
	 * @see #setDuration(Integer)
	 */
	private Integer duration;

	/**
	 * @see #setEasing(Easing)
	 */
	private Easing easing;

	/**
	 * @see #setOnProgress(JavaScriptFunction onProgress)
	 */
	private JavaScriptFunction onProgress;

	/**
	 * @see #setOnComplete(JavaScriptFunction onComplete)
	 */
	private JavaScriptFunction onComplete;

	/**
	 * @return duration as {@link Integer} or {@code null}
	 * @see #setDuration(Integer)
	 */
	public Integer getDuration() {
		return duration;
	}

	/**
	 * <p>
	 * Default {@code 1000}
	 * </p>
	 * 
	 * <p>
	 * The number of milliseconds an animation takes.
	 * </p>
	 */
	@SuppressWarnings("unchecked")
	public T setDuration(Integer duration) {
		this.duration = duration;
		return (T) this;
	}

	/**
	 * @return {@link Easing} or {@code null}
	 * @see #setEasing(Easing)
	 */
	public Easing getEasing() {
		return easing;
	}

	/**
	 * <p>
	 * Default {@link Easing#EASE_OUT_QUART}
	 * </p>
	 * 
	 * <p>
	 * Easing function to use.
	 * </p>
	 * 
	 * <p>
	 * Easing functions adapted from Robert Penner's easing equations
	 * http://www.robertpenner.com/easing/
	 * </p>
	 */
	@SuppressWarnings("unchecked")
	public T setEasing(Easing easing) {
		this.easing = easing;
		return (T) this;
	}

	/**
	 * @return {@link JavaScriptFunction} or {@code null}
	 * @see #setOnProgress(JavaScriptFunction onProgress)
	 */
	public JavaScriptFunction getOnProgress() {
		return onProgress;
	}

	/**
	 * <p>
	 * Default {@code none}
	 * </p>
	 * 
	 * <p>
	 * Callback called on each step of an animation. Passed a single argument,
	 * an object, containing the chart instance and an object with details of
	 * the animation.
	 * </p>
	 */
	@SuppressWarnings("unchecked")
	public T setOnProgress(JavaScriptFunction onProgress) {
		this.onProgress = onProgress;
		return (T) this;
	}

	/**
	 * @return {@link JavaScriptFunction} or {@code null}
	 * @see #setOnComplete(JavaScriptFunction)
	 */
	public JavaScriptFunction getOnComplete() {
		return onComplete;
	}

	/**
	 * <p>
	 * Default {@code none}
	 * </p>
	 * 
	 * <p>
	 * Callback called at the end of an animation. Passed the same arguments as
	 * onProgress
	 * </p>
	 */
	@SuppressWarnings("unchecked")
	public T setOnComplete(JavaScriptFunction onComplete) {
		this.onComplete = onComplete;
		return (T) this;
	}

}
