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

import org.primefaces.model.chartjs.color.Color;
import org.primefaces.model.chartjs.enums.FontStyle;
import org.primefaces.model.chartjs.enums.TitlePosition;

public class Title {

	/**
	 * @see #setDisplay(Boolean)
	 */
	private Boolean display;

	/**
	 * @see #setPosition(TitlePosition)
	 */
	private TitlePosition position;

	/**
	 * @see #setFullWidth(Boolean)
	 */
	private Boolean fullWidth;

	/**
	 * @see #setFontSize(Integer)
	 */
	private Integer fontSize;

	/**
	 * @see #setFontFamily(String)
	 */
	private String fontFamily;

	/**
	 * @see #setFontColor(Color)
	 */
	private Color fontColor;

	/**
	 * @see #setFontStyle(FontStyle)
	 */
	private FontStyle fontStyle;

	/**
	 * @see #setPadding(Integer)
	 */
	private Integer padding;

	/**
	 * @see #setText(String)
	 */
	private String text;

	/**
	 * @see #setDisplay(Boolean)
	 */
	public Boolean getDisplay() {
		return display;
	}

	/**
	 * <p>
	 * Display the title block
	 * </p>
	 * 
	 * <p>
	 * Default {@code false}
	 * </p>
	 */
	public Title setDisplay(Boolean display) {
		this.display = display;
	    return this;
	}

	/**
	 * @see #setPosition(TitlePosition)
	 */
	public TitlePosition getPosition() {
		return position;
	}

	/**
	 * <p>
	 * Position of the title. Only 'top' or 'bottom' are currently allowed
	 * </p>
	 * 
	 * <p>
	 * Default {@code 'top'}
	 * </p>
	 */
	public Title setPosition(TitlePosition position) {
		this.position = position;
	    return this;
	}

	/**
	 * @see #setFullWidth(Boolean)
	 */
	public Boolean getFullWidth() {
		return fullWidth;
	}

	/**
	 * <p>
	 * Marks that this box should take the full width of the canvas (pushing
	 * down other boxes)
	 * </p>
	 * 
	 * <p>
	 * Default {@code true}
	 * </p>
	 */
	public Title setFullWidth(Boolean fullWidth) {
		this.fullWidth = fullWidth;
	    return this;
	}

	/**
	 * @see #setFontSize(Integer)
	 */
	public Integer getFontSize() {
		return fontSize;
	}

	/**
	 * <p>
	 * Font size inherited from global configuration
	 * </p>
	 * 
	 * <p>
	 * Default {@code 12}
	 * </p>
	 */
	public Title setFontSize(Integer fontSize) {
		this.fontSize = fontSize;
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
	 * Font family inherited from global configuration
	 * </p>
	 * 
	 * <p>
	 * Default {@code "'Helvetica Neue', 'Helvetica', 'Arial', sans-serif"}
	 * </p>
	 */
	public Title setFontFamily(String fontFamily) {
		this.fontFamily = fontFamily;
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
	 * Font color inherited from global configuration
	 * </p>
	 * 
	 * <p>
	 * Default {@code "#666"}
	 * </p>
	 */
	public Title setFontColor(Color fontColor) {
		this.fontColor = fontColor;
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
	 * Font styling of the title, follows CSS font-style options (i.e. normal,
	 * italic, oblique, initial, inherit).
	 * </p>
	 * 
	 * <p>
	 * Default {@code 'bold'}
	 * </p>
	 */
	public Title setFontStyle(FontStyle fontStyle) {
		this.fontStyle = fontStyle;
	    return this;
	}

	/**
	 * @see #setPadding(Integer)
	 */
	public Integer getPadding() {
		return padding;
	}

	/**
	 * <p>
	 * Number of pixels to add above and below the title text
	 * </p>
	 * 
	 * <p>
	 * Default {@code 10}
	 * </p>
	 */
	public Title setPadding(Integer padding) {
		this.padding = padding;
	    return this;
	}

	/**
	 * @see #setText(String)
	 */
	public String getText() {
		return text;
	}

	/**
	 * <p>
	 * Title text
	 * </p>
	 * 
	 * <p>
	 * Default {@code ""}
	 * </p>
	 */
	public Title setText(String text) {
		this.text = text;
	    return this;
	}

}
