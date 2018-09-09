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

import java.math.BigDecimal;

public class Time {

	/**
	 * Default {@code -}
	 * 
	 * @see #setDisplayFormats(DisplayFormats)
	 */
	private DisplayFormats displayFormats;

	/**
	 * Default {@code false}
	 * 
	 * @see #setIsoWeekday(Boolean)
	 */
	private Boolean isoWeekday;

	/**
	 * Default {@code -}
	 * 
	 * @see #setMax(Time)
	 */
	private Time max;

	/**
	 * Default {@code -}
	 * 
	 * @see #setMin(Time)
	 */
	private Time min;

	/**
	 * Default {@code -}
	 * 
	 * @see #setParser(String)
	 */
	private String parser;

	/**
	 * Default {@code -}
	 * 
	 * @see #setRound(String)
	 */
	private String round;

	/**
	 * Default {@code ''}
	 * 
	 * @see #setTooltipFormat(String)
	 */
	private String tooltipFormat;

	/**
	 * Default {@code -}
	 * 
	 * @see #setUnit(String)
	 */
	private String unit;

	/**
	 * Default {@code 1}
	 * 
	 * @see #setUnitStepSize(BigDecimal)
	 */
	private BigDecimal unitStepSize;

	/**
	 * @see #setDisplayFormats(DisplayFormats)
	 */
	public DisplayFormats getDisplayFormats() {
		return displayFormats;
	}

	/**
	 * See Display Formats section below.
	 */
	public Time setDisplayFormats(DisplayFormats displayFormats) {
		this.displayFormats = displayFormats;
	    return this;
	}

	/**
	 * @see #setIsoWeekday(Boolean)
	 */
	public Boolean getIsoWeekday() {
		return isoWeekday;
	}

	/**
	 * If true and the unit is set to 'week', iso weekdays will be used.
	 */
	public Time setIsoWeekday(Boolean isoWeekday) {
		this.isoWeekday = isoWeekday;
	    return this;
	}

	/**
	 * @see #setMax(Time)
	 */
	public Time getMax() {
		return max;
	}

	/**
	 * If defined, this will override the data maximum
	 */
	public Time setMax(Time max) {
		this.max = max;
	    return this;
	}

	/**
	 * @see #setMin(Time)
	 */
	public Time getMin() {
		return min;
	}

	/**
	 * If defined, this will override the data minimum
	 */
	public Time setMin(Time min) {
		this.min = min;
	    return this;
	}

	/**
	 * @see #setParser(String)
	 */
	public String getParser() {
		return parser;
	}

	/**
	 * If defined as a string, it is interpreted as a custom format to be used
	 * by moment to parse the date. If this is a function, it must return a
	 * moment.js object given the appropriate data value.
	 */
	public Time setParser(String parser) {
		this.parser = parser;
	    return this;
	}

	/**
	 * @see #setRound(String)
	 */
	public String getRound() {
		return round;
	}

	/**
	 * If defined, dates will be rounded to the start of this unit. See Time
	 * Units below for the allowed units.
	 */
	public Time setRound(String round) {
		this.round = round;
	    return this;
	}

	/**
	 * @see #setTooltipFormat(String)
	 */
	public String getTooltipFormat() {
		return tooltipFormat;
	}

	/**
	 * The moment js format string to use for the tooltip.
	 */
	public Time setTooltipFormat(String tooltipFormat) {
		this.tooltipFormat = tooltipFormat;
	    return this;
	}

	/**
	 * @see #setUnit(String)
	 */
	public String getUnit() {
		return unit;
	}

	/**
	 * If defined, will force the unit to be a certain type. See Time Units
	 * section below for details.
	 */
	public Time setUnit(String unit) {
		this.unit = unit;
	    return this;
	}

	/**
	 * @see #setUnitStepSize(BigDecimal)
	 */
	public BigDecimal getUnitStepSize() {
		return unitStepSize;
	}

	/**
	 * The number of units between grid lines.
	 */
	public Time setUnitStepSize(BigDecimal unitStepSize) {
		this.unitStepSize = unitStepSize;
	    return this;
	}

}
