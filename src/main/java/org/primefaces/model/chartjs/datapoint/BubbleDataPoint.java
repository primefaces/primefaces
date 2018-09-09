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
package org.primefaces.model.chartjs.datapoint;


import java.math.BigDecimal;

public class BubbleDataPoint {

	/**
	 * @see #setX(BigDecimal)
	 */
	private BigDecimal x;

	/**
	 * @see #setY(BigDecimal)
	 */
	private BigDecimal y;

	/**
	 * @see #setR(BigDecimal)
	 */
	private BigDecimal r;

	public BubbleDataPoint() {
	}

	public BubbleDataPoint(BigDecimal x, BigDecimal y, BigDecimal r) {
		this.x = x;
		this.y = y;
		this.r = r;
	}

	/**
	 * @see #setX(BigDecimal)
	 */
	public BigDecimal getX() {
		return x;
	}

	/**
	 * X Value
	 */
	public BubbleDataPoint setX(int x) {
		this.x = new BigDecimal(x);
		return this;
	}
	
	/**
	 * X Value
	 */
	public BubbleDataPoint setX(double x) {
		this.x = new BigDecimal(String.valueOf(x));
		return this;
	}
	
	/**
	 * X Value
	 */
	public BubbleDataPoint setX(BigDecimal x) {
		this.x = x;
		return this;
	}

	/**
	 * @see #setY(BigDecimal)
	 */
	public BigDecimal getY() {
		return y;
	}

	/**
	 * @see #setY(BigDecimal)
	 */
	public BubbleDataPoint setY(int y) {
		this.y = new BigDecimal(y);
		return this;
	}

	/**
	 * @see #setY(BigDecimal)
	 */
	public BubbleDataPoint setY(double y) {
		this.y = new BigDecimal(String.valueOf(y));
		return this;
	}

	/**
	 * Y Value
	 */
	public BubbleDataPoint setY(BigDecimal y) {
		this.y = y;
		return this;
	}

	/**
	 * @see #setR(BigDecimal)
	 */
	public BigDecimal getR() {
		return r;
	}

	/**
	 * @see #setR(BigDecimal)
	 */
	public BubbleDataPoint setR(double r) {
		this.r = new BigDecimal(String.valueOf(r));
		return this;
	}

	/**
	 * Radius of bubble
	 */
	public BubbleDataPoint setR(BigDecimal r) {
		this.r = r;
		return this;
	}

}
