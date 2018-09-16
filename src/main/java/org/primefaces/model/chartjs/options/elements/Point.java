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
import org.primefaces.model.chartjs.enums.PointStyle;

/**
 * <p>
 * Point elements are used to represent the points in a line chart or a bubble
 * chart.
 * </p>
 * <p>
 * When set, these options apply to all objects of that type unless specifically
 * overridden by the configuration attached to a dataset.
 * </p>
 */
public class Point {

	/**
	 * @see #setRadius(Integer)
	 */
	private Integer radius;

	/**
	 * @see #setPointStyle(PointStyle)
	 */
	private PointStyle pointStyle;

	/**
	 * @see #setBackgroundColor(Color)
	 */
	private Color backgroundColor;

	/**
	 * @see #setBorderWidth(Integer)
	 */
	private Integer borderWidth;

	/**
	 * @see #setBorderColor(Color)
	 */
	private Color borderColor;

	/**
	 * @see #setHitRadius(Integer)
	 */
	private Integer hitRadius;

	/**
	 * @see #setHoverRadius(Integer)
	 */
	private Integer hoverRadius;

	/**
	 * @see #setHoverBorderWidth(Integer)
	 */
	private Integer hoverBorderWidth;

	/**
	 * @see #setRadius(Integer)
	 */
	public Integer getRadius() {
		return radius;
	}

	/**
	 * <p>
	 * Default point radius
	 * </p>
	 * 
	 * <p>
	 * Default {@code 3}
	 * </p>
	 */
	public Point setRadius(Integer radius) {
		this.radius = radius;
		return this;
	}

	/**
	 * @see #setPointStyle(PointStyle)
	 */
	public PointStyle getPointStyle() {
		return pointStyle;
	}

	/**
	 * <p>
	 * Default point style
	 * </p>
	 * 
	 * <p>
	 * Default {@code 'circle'}
	 * </p>
	 */
	public Point setPointStyle(PointStyle pointStyle) {
		this.pointStyle = pointStyle;
		return this;
	}

	/**
	 * @see #setBackgroundColor(Color)
	 */
	public Color getBackgroundColor() {
		return backgroundColor;
	}

	/**
	 * <p>
	 * Default point fill color
	 * </p>
	 * 
	 * <p>
	 * Default {@code 'rgba(0,0,0,0.1)'}
	 * </p>
	 */
	public Point setBackgroundColor(Color backgroundColor) {
		this.backgroundColor = backgroundColor;
		return this;
	}

	/**
	 * @see #setBorderWidth(Integer)
	 */
	public Integer getBorderWidth() {
		return borderWidth;
	}

	/**
	 * <p>
	 * Default point stroke width
	 * </p>
	 * 
	 * <p>
	 * Default {@code 1}
	 * </p>
	 */
	public Point setBorderWidth(Integer borderWidth) {
		this.borderWidth = borderWidth;
		return this;
	}

	/**
	 * @see #setBorderColor(Color)
	 */
	public Color getBorderColor() {
		return borderColor;
	}

	/**
	 * <p>
	 * Default point stroke color
	 * </p>
	 * 
	 * <p>
	 * Default {@code 'rgba(0,0,0,0.1)'}
	 * </p>
	 */
	public Point setBorderColor(Color borderColor) {
		this.borderColor = borderColor;
		return this;
	}

	/**
	 * @see #setHitRadius(Integer)
	 */
	public Integer getHitRadius() {
		return hitRadius;
	}

	/**
	 * <p>
	 * Extra radius added to point radius for hit detection
	 * </p>
	 * 
	 * <p>
	 * Default {@code 1}
	 * </p>
	 */
	public Point setHitRadius(Integer hitRadius) {
		this.hitRadius = hitRadius;
		return this;
	}

	/**
	 * @see #setHoverRadius(Integer)
	 */
	public Integer getHoverRadius() {
		return hoverRadius;
	}

	/**
	 * <p>
	 * Default point radius when hovered
	 * </p>
	 * 
	 * <p>
	 * Default {@code 4}
	 * </p>
	 */
	public Point setHoverRadius(Integer hoverRadius) {
		this.hoverRadius = hoverRadius;
		return this;
	}

	/**
	 * @see #setHoverBorderWidth(Integer)
	 */
	public Integer getHoverBorderWidth() {
		return hoverBorderWidth;
	}

	/**
	 * <p>
	 * Default stroke width when hovered
	 * </p>
	 * 
	 * <p>
	 * Default {@code 1}
	 * </p>
	 */
	public Point setHoverBorderWidth(Integer hoverBorderWidth) {
		this.hoverBorderWidth = hoverBorderWidth;
		return this;
	}

}
