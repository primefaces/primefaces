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

/**
 * Line elements are used to represent the line in a line chart.
 */
public class LineElements {

	private Line line;
	private Point point;

	/**
	 * No-arg constructor
	 */
	public LineElements() {
		
	}

	/**
	 * Constructor
	 * 
	 * @param line
	 *            {@link Line} instance or {@code null}
	 */
	public LineElements(Line line) {
		this.line = line;
	}

	/**
	 * Constructor
	 * 
	 * @param point
	 *            {@link Point} instance or {@code null}
	 */
	public LineElements(Point point) {
		this.point = point;
	}

	/**
	 * Constructor
	 * 
	 * @param line
	 *            {@link Line} instance or {@code null}
	 * @param point
	 *            {@link Point} instance or {@code null}
	 */
	public LineElements(Line line, Point point) {
		this.line = line;
		this.point = point;
	}

	/**
	 * @return {@link Line} instance or {@code null} if not set
	 * @see #setLine(Line)
	 */
	public Line getLine() {
		return line;
	}

	/**
	 * @param line
	 *            {@link Line} instance or {@code null}
	 * @return this {@link LineElements} instance for chaining
	 */
	public LineElements setLine(Line line) {
		this.line = line;
		return this;
	}

	/**
	 * @return {@link Point} instance or {@code null} if not set
	 * @see #setPoint(Point)
	 */
	public Point getPoint() {
		return point;
	}

	/**
	 * @param point
	 *            {@link Point} instance or {@code null}
	 * @return this {@link LineElements} instance for chaining
	 */
	public LineElements setPoint(Point point) {
		this.point = point;
		return this;
	}

}
