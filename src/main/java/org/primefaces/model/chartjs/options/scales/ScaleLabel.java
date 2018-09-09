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

import org.primefaces.model.chartjs.color.Color;
import org.primefaces.model.chartjs.enums.FontStyle;

import java.math.BigDecimal;

/**
 * The grid line configuration is nested under the scale configuration in the
 * scaleLabel key. It defines options for the scale title.
 * 
 */
public class ScaleLabel {

	/**
	 * @see #setDisplay(Boolean)
	 */
	private Boolean display;

	/**
	 * @see #setLabelString(String)
	 */
	private String labelString;

	/**
	 * @see #setFontColor(Color)
	 */
	private Color fontColor;

	/**
	 * @see #setFontFamily(String)
	 */
	private String fontFamily;

	/**
	 * @see #setFontSize(BigDecimal)
	 */
	private BigDecimal fontSize;

	/**
	 * @see #setFontStyle(FontStyle)
	 */
	private FontStyle fontStyle;

	/**
	 * @see #setDisplay(Boolean)
	 */
	public Boolean getDisplay() {
	    return display;
	}

	/**
	 * <p>
	 * Default {@code false}
	 * </p>
	 */
	public ScaleLabel setDisplay(Boolean display) {
	    this.display = display;
	    return this;
	}

	/**
	 * @see #setLabelString(String)
	 */
	public String getLabelString() {
	    return labelString;
	}

	/**
	 * <p>
	 * The text for the title. (i.e. "# of People", "Response Choices")
	 * </p>
	 * 
	 * <p>
	 * Default {@code ""}
	 * </p>
	 */
	public ScaleLabel setLabelString(String labelString) {
	    this.labelString = labelString;
	    return this;
	}

	/**
	 * @see #setFontColor(Color)
	 */
	public Color getFontColor() {
	    return fontColor;
	}

	/**
	 * <p>
	 * Font color for the scale title.
	 * </p>
	 * 
	 * <p>
	 * Default {@code "#666"}
	 * </p>
	 */
	public ScaleLabel setFontColor(Color fontColor) {
	    this.fontColor = fontColor;
	    return this;
	}

	/**
	 * @see #setFontFamily(String)
	 */
	public String getFontFamily() {
	    return fontFamily;
	}

	/**
	 * <p>
	 * Font family for the scale title, follows CSS font-family options.
	 * </p>
	 * 
	 * <p>
	 * Default {@code "'Helvetica Neue', 'Helvetica', 'Arial', sans-serif"}
	 * </p>
	 */
	public ScaleLabel setFontFamily(String fontFamily) {
	    this.fontFamily = fontFamily;
	    return this;
	}

	/**
	 * @see #setFontSize(BigDecimal)
	 */
	public BigDecimal getFontSize() {
	    return fontSize;
	}

	/**
	 * <p>
	 * Font size for the scale title.
	 * </p>
	 * 
	 * <p>
	 * Default {@code 12}
	 * </p>
	 */
	public ScaleLabel setFontSize(BigDecimal fontSize) {
	    this.fontSize = fontSize;
	    return this;
	}

	/**
	 * @see #setFontStyle(FontStyle)
	 */
	public FontStyle getFontStyle() {
	    return fontStyle;
	}

	/**
	 * <p>
	 * Font style for the scale title, follows CSS font-style options (i.e. normal, italic, oblique, initial, inherit).
	 * </p>
	 * 
	 * <p>
	 * Default {@code "normal"}
	 * </p>
	 */
	public ScaleLabel setFontStyle(FontStyle fontStyle) {
	    this.fontStyle = fontStyle;
	    return this;
	}

}