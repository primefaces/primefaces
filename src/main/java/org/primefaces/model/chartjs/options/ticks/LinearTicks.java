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
package org.primefaces.model.chartjs.options.ticks;

import java.math.BigDecimal;

public class LinearTicks extends Ticks<LinearTicks> {

	/**
	 * @see #setBeginAtZero(Boolean)
	 */
	private Boolean beginAtZero;

	/**
	 * @see #setMin(BigDecimal)
	 */
	private BigDecimal min;

	/**
	 * @see #setMax(BigDecimal)
	 */
	private BigDecimal max;

	/**
	 * @see #setMaxTicksLimit(BigDecimal)
	 */
	private BigDecimal maxTicksLimit;

	/**
	 * @see #setFixedStepSize(BigDecimal)
	 */
	private BigDecimal fixedStepSize;

	/**
	 * @see #setStepSize(BigDecimal)
	 */
	private BigDecimal stepSize;

	/**
	 * @see #setSuggestedMax(BigDecimal)
	 */
	private BigDecimal suggestedMax;

	/**
	 * @see #setSuggestedMin(BigDecimal)
	 */
	private BigDecimal suggestedMin;

	/**
	 * @see #setBeginAtZero(Boolean)
	 */
	public Boolean getBeginAtZero() {
		return beginAtZero;
	}

	/**
	 * <p>
	 * if true, scale will inclulde 0 if it is not already included.
	 * </p>
	 * 
	 * <p>
	 * Default {@code -}
	 * </p>
	 */
	public LinearTicks setBeginAtZero(Boolean beginAtZero) {
		this.beginAtZero = beginAtZero;
		return this;
	}

	/**
	 * @see #setMin(BigDecimal)
	 */
	public BigDecimal getMin() {
		return min;
	}

	/**
	 * @see #setMin(BigDecimal)
	 */
	public LinearTicks setMin(int min) {
		this.min = new BigDecimal(min);
		return this;
	}

	/**
	 * <p>
	 * User defined minimum number for the scale, overrides minimum value from
	 * data.
	 * </p>
	 * 
	 * <p>
	 * Default {@code -}
	 * </p>
	 */
	public LinearTicks setMin(BigDecimal min) {
		this.min = min;
		return this;
	}

	/**
	 * @see #setMax(BigDecimal)
	 */
	public BigDecimal getMax() {
		return max;
	}

	/**
	 * @see #setMax(BigDecimal)
	 */
	public LinearTicks setMax(int max) {
		this.max = new BigDecimal(max);
		return this;
	}

	/**
	 * <p>
	 * User defined maximum number for the scale, overrides maximum value from
	 * data.
	 * </p>
	 * 
	 * <p>
	 * Default {@code -}
	 * </p>
	 */
	public LinearTicks setMax(BigDecimal max) {
		this.max = max;
		return this;
	}

	/**
	 * @see #setMaxTicksLimit(BigDecimal)
	 */
	public BigDecimal getMaxTicksLimit() {
		return maxTicksLimit;
	}

	/**
	 * <p>
	 * Maximum number of ticks and gridlines to show. If not defined, it will
	 * limit to 11 ticks but will show all gridlines.
	 * </p>
	 * 
	 * <p>
	 * Default {@code 11}
	 * </p>
	 */
	public LinearTicks setMaxTicksLimit(BigDecimal maxTicksLimit) {
		this.maxTicksLimit = maxTicksLimit;
		return this;
	}

	/**
	 * @see #setFixedStepSize(BigDecimal)
	 */
	public BigDecimal getFixedStepSize() {
		return fixedStepSize;
	}

	/**
	 * <p>
	 * User defined fixed step size for the scale. If set, the scale ticks will
	 * be enumerated by multiple of stepSize, having one tick per increment. If
	 * not set, the ticks are labeled automatically using the nice numbers
	 * algorithm.
	 * </p>
	 * 
	 * <p>
	 * Default {@code -}
	 * </p>
	 */
	public LinearTicks setFixedStepSize(BigDecimal fixedStepSize) {
		this.fixedStepSize = fixedStepSize;
		return this;
	}

	/**
	 * @see #setStepSize(BigDecimal)
	 */
	public BigDecimal getStepSize() {
		return stepSize;
	}

	/**
	 * @see #setStepSize(BigDecimal)
	 */
	public LinearTicks setStepSize(double stepSize) {
		this.stepSize = new BigDecimal(String.valueOf(stepSize));
		return this;
	}
	
	/**
	 * @see #setStepSize(BigDecimal)
	 */
	public LinearTicks setStepSize(int stepSize) {
		this.stepSize = new BigDecimal(stepSize);
		return this;
	}
	
	/**
	 * <p>
	 * if defined, it can be used along with the min and the max to give a
	 * custom number of steps. See the example below.
	 * </p>
	 * 
	 * <p>
	 * Default {@code -}
	 * </p>
	 */
	public LinearTicks setStepSize(BigDecimal stepSize) {
		this.stepSize = stepSize;
		return this;
	}

	/**
	 * @see #setSuggestedMax(BigDecimal)
	 */
	public BigDecimal getSuggestedMax() {
		return suggestedMax;
	}

	/**
	 * <p>
	 * User defined maximum number for the scale, overrides maximum value except
	 * for if it is lower than the maximum value.
	 * </p>
	 * 
	 * <p>
	 * Default {@code -}
	 * </p>
	 */
	public LinearTicks setSuggestedMax(BigDecimal suggestedMax) {
		this.suggestedMax = suggestedMax;
		return this;
	}

	/**
	 * @see #setSuggestedMin(BigDecimal)
	 */
	public BigDecimal getSuggestedMin() {
		return suggestedMin;
	}

	/**
	 * <p>
	 * User defined minimum number for the scale, overrides minimum value except
	 * for if it is higher than the minimum value.
	 * </p>
	 * 
	 * <p>
	 * Default {@code -}
	 * </p>
	 */
	public LinearTicks setSuggestedMin(BigDecimal suggestedMin) {
		this.suggestedMin = suggestedMin;
		return this;
	}

}
