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

/**
 * Display formats are used to configure how different time units are formed
 * into strings for the axis tick marks. See
 * <a href="http://momentjs.com/docs/#/displaying/format/">moment.js</a> for the
 * allowable format strings.
 */
public class DisplayFormats {

	private String millisecond;
	private String second; 
	private String minute; 
	private String hour; 
	private String day; 
	private String week;
	private String month; 
	private String quarter;
	private String year; 

	/**
	 * @return a {@link String} or {@code null} if not set
	 * @see #setMillisecond(String)
	 */
	public String getMillisecond() {
		return millisecond;
	}

	/**
	 * Default: 'SSS [ms]'
	 * 
	 * @param millisecond
	 *            a {@code String} or {@code null}
	 */
	public void setMillisecond(String millisecond) {
		this.millisecond = millisecond;
	}

	/**
	 * @return a {@link String} or {@code null} if not set
	 * @see #setSecond(String)
	 */
	public String getSecond() {
		return second;
	}

	/**
	 * Default: 'h:mm:ss a'
	 * 
	 * @param second
	 *            a {@code String} or {@code null}
	 */
	public void setSecond(String second) {
		this.second = second;
	}

	/**
	 * @return a {@link String} or {@code null} if not set
	 * @see #setMinute(String)
	 */
	public String getMinute() {
		return minute;
	}

	/**
	 * Default: 'h:mm:ss a'
	 * 
	 * @param minute
	 *            a {@code String} or {@code null}
	 */
	public void setMinute(String minute) {
		this.minute = minute;
	}

	/**
	 * @return a {@link String} or {@code null} if not set
	 * @see #setHour(String)
	 */
	public String getHour() {
		return hour;
	}

	/**
	 * Default: 'MMM D, hA'
	 * 
	 * @param hour
	 *            a {@code String} or {@code null}
	 */
	public void setHour(String hour) {
		this.hour = hour;
	}

	/**
	 * @return a {@link String} or {@code null} if not set
	 * @see #setDay(String)
	 */
	public String getDay() {
		return day;
	}

	/**
	 * Default: 'll'
	 * 
	 * @param day
	 *            a {@code String} or {@code null}
	 */
	public void setDay(String day) {
		this.day = day;
	}

	/**
	 * @return a {@link String} or {@code null} if not set
	 * @see #setWeek(String)
	 */
	public String getWeek() {
		return week;
	}

	/**
	 * Default: 'll'
	 * 
	 * @param week
	 *            a {@code String} or {@code null}
	 */
	public void setWeek(String week) {
		this.week = week;
	}

	/**
	 * @return a {@link String} or {@code null} if not set
	 * @see #setMonth(String)
	 */
	public String getMonth() {
		return month;
	}

	/**
	 * Default: 'MMM YYYY'
	 * 
	 * @param month
	 *            a {@code String} or {@code null}
	 */
	public void setMonth(String month) {
		this.month = month;
	}

	/**
	 * @return a {@link String} or {@code null} if not set
	 * @see #setQuarter(String)
	 */
	public String getQuarter() {
		return quarter;
	}

	/**
	 * Default: '[Q]Q - YYYY'
	 * 
	 * @param quarter
	 *            a {@code String} or {@code null}
	 */
	public void setQuarter(String quarter) {
		this.quarter = quarter;
	}

	/**
	 * @return a {@link String} or {@code null} if not set
	 * @see #setYear(String)
	 */
	public String getYear() {
		return year;
	}

	/**
	 * Default: 'YYYY'
	 * 
	 * @param year
	 *            a {@code String} or {@code null}
	 */
	public void setYear(String year) {
		this.year = year;
	}

}
