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
import org.primefaces.model.chartjs.javascript.JavaScriptFunction;

public class Tooltips {

	/**
	 * @see #setEnabled(Boolean)
	 */
	private Boolean enabled;

	/**
	 * @see #setCustom(JavaScriptFunction)
	 */
	private JavaScriptFunction custom;

	/**
	 * @see #setMode(String)
	 */
	private String mode;

	/**
	 * @see #setItemSort(JavaScriptFunction)
	 */
	private JavaScriptFunction itemSort;

	/**
	 * @see #setBackgroundColor(Color)
	 */
	private Color backgroundColor;

	/**
	 * @see #setTitleFontFamily(String)
	 */
	private String titleFontFamily;

	/**
	 * @see #setTitleFontSize(Integer)
	 */
	private Integer titleFontSize;

	/**
	 * @see #setTitleFontStyle(FontStyle)
	 */
	private FontStyle titleFontStyle;

	/**
	 * @see #setTitleFontColor(Color)
	 */
	private Color titleFontColor;

	/**
	 * @see #setTitleSpacing(Integer)
	 */
	private Integer titleSpacing;

	/**
	 * @see #setTitleMarginBottom(Integer)
	 */
	private Integer titleMarginBottom;

	/**
	 * @see #setBodyFontFamily(String)
	 */
	private String bodyFontFamily;

	/**
	 * @see #setBodyFontSize(Integer)
	 */
	private Integer bodyFontSize;

	/**
	 * @see #setBodyFontStyle(FontStyle)
	 */
	private FontStyle bodyFontStyle;

	/**
	 * @see #setBodyFontColor(Color)
	 */
	private Color bodyFontColor;

	/**
	 * @see #setBodySpacing(Integer)
	 */
	private Integer bodySpacing;

	/**
	 * @see #setFooterFontFamily(String)
	 */
	private String footerFontFamily;

	/**
	 * @see #setFooterFontSize(Integer)
	 */
	private Integer footerFontSize;

	/**
	 * @see #setFooterFontStyle(FontStyle)
	 */
	private FontStyle footerFontStyle;

	/**
	 * @see #setFooterFontColor(Color)
	 */
	private Color footerFontColor;

	/**
	 * @see #setFooterSpacing(Integer)
	 */
	private Integer footerSpacing;

	/**
	 * @see #setFooterMarginTop(Integer)
	 */
	private Integer footerMarginTop;

	/**
	 * @see #setXPadding(Integer)
	 */
	private Integer xPadding;

	/**
	 * @see #setYPadding(Integer)
	 */
	private Integer yPadding;

	/**
	 * @see #setCaretSize(Integer)
	 */
	private Integer caretSize;

	/**
	 * @see #setCornerRadius(Integer)
	 */
	private Integer cornerRadius;

	/**
	 * @see #setMultiKeyBackground(Color)
	 */
	private Color multiKeyBackground;

//	/**
//	 * @see #setCallbacks(Callbacks)
//	 */
//	private Callbacks callbacks;

	/**
	 * @see #setEnabled(Boolean)
	 */
	public Boolean getEnabled() {
	    return enabled;
	}

	/**
	 * <p>
	 * Are tooltips enabled
	 * </p>
	 * 
	 * <p>
	 * Default {@code true}
	 * </p>
	 */
	public Tooltips setEnabled(Boolean enabled) {
	    this.enabled = enabled;
	    return this;
	}

	/**
	 * @see #setCustom(JavaScriptFunction)
	 */
	public JavaScriptFunction getCustom() {
	    return custom;
	}

	/**
	 * <p>
	 * See section below
	 * </p>
	 * 
	 * <p>
	 * Default {@code null}
	 * </p>
	 */
	public Tooltips setCustom(JavaScriptFunction custom) {
	    this.custom = custom;
	    return this;
	}

	/**
	 * @see #setMode(String)
	 */
	public String getMode() {
	    return mode;
	}

	/**
	 * <p>
	 * Sets which elements appear in the tooltip. Acceptable options are 'single', 'label' or 'x-axis'.  single highlights the closest element.  label highlights elements in all datasets at the same X value.  'x-axis' also highlights elements in all datasets at the same X value, but activates when hovering anywhere within the vertical slice of the x-axis representing that X value.
	 * </p>
	 * 
	 * <p>
	 * Default {@code 'single'}
	 * </p>
	 */
	public Tooltips setMode(String mode) {
	    this.mode = mode;
	    return this;
	}

	/**
	 * @see #setItemSort(JavaScriptFunction)
	 */
	public JavaScriptFunction getItemSort() {
	    return itemSort;
	}

	/**
	 * <p>
	 * Allows sorting of tooltip items. Must implement a function that can be passed to Array.prototype.sort
	 * </p>
	 * 
	 * <p>
	 * Default {@code undefined}
	 * </p>
	 */
	public Tooltips setItemSort(JavaScriptFunction itemSort) {
	    this.itemSort = itemSort;
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
	 * Background color of the tooltip
	 * </p>
	 * 
	 * <p>
	 * Default {@code 'rgba(0,0,0,0.8)'}
	 * </p>
	 */
	public Tooltips setBackgroundColor(Color backgroundColor) {
	    this.backgroundColor = backgroundColor;
	    return this;
	}

	/**
	 * @see #setTitleFontFamily(String)
	 */
	public String getTitleFontFamily() {
	    return titleFontFamily;
	}

	/**
	 * <p>
	 * Font family for tooltip title inherited from global font family
	 * </p>
	 * 
	 * <p>
	 * Default {@code "'Helvetica Neue', 'Helvetica', 'Arial', sans-serif"}
	 * </p>
	 */
	public Tooltips setTitleFontFamily(String titleFontFamily) {
	    this.titleFontFamily = titleFontFamily;
	    return this;
	}

	/**
	 * @see #setTitleFontSize(Integer)
	 */
	public Integer getTitleFontSize() {
	    return titleFontSize;
	}

	/**
	 * <p>
	 * Font size for tooltip title inherited from global font size
	 * </p>
	 * 
	 * <p>
	 * Default {@code 12}
	 * </p>
	 */
	public Tooltips setTitleFontSize(Integer titleFontSize) {
	    this.titleFontSize = titleFontSize;
	    return this;
	}

	/**
	 * @see #setTitleFontStyle(FontStyle)
	 */
	public FontStyle getTitleFontStyle() {
	    return titleFontStyle;
	}

	/**
	 * <p>
	 * 
	 * </p>
	 * 
	 * <p>
	 * Default {@code "bold"}
	 * </p>
	 */
	public Tooltips setTitleFontStyle(FontStyle titleFontStyle) {
	    this.titleFontStyle = titleFontStyle;
	    return this;
	}

	/**
	 * @see #setTitleFontColor(Color)
	 */
	public Color getTitleFontColor() {
	    return titleFontColor;
	}

	/**
	 * <p>
	 * Font color for tooltip title
	 * </p>
	 * 
	 * <p>
	 * Default {@code "#fff"}
	 * </p>
	 */
	public Tooltips setTitleFontColor(Color titleFontColor) {
	    this.titleFontColor = titleFontColor;
	    return this;
	}

	/**
	 * @see #setTitleSpacing(Integer)
	 */
	public Integer getTitleSpacing() {
	    return titleSpacing;
	}

	/**
	 * <p>
	 * Spacing to add to top and bottom of each title line.
	 * </p>
	 * 
	 * <p>
	 * Default {@code 2}
	 * </p>
	 */
	public Tooltips setTitleSpacing(Integer titleSpacing) {
	    this.titleSpacing = titleSpacing;
	    return this;
	}

	/**
	 * @see #setTitleMarginBottom(Integer)
	 */
	public Integer getTitleMarginBottom() {
	    return titleMarginBottom;
	}

	/**
	 * <p>
	 * Margin to add on bottom of title section
	 * </p>
	 * 
	 * <p>
	 * Default {@code 6}
	 * </p>
	 */
	public Tooltips setTitleMarginBottom(Integer titleMarginBottom) {
	    this.titleMarginBottom = titleMarginBottom;
	    return this;
	}

	/**
	 * @see #setBodyFontFamily(String)
	 */
	public String getBodyFontFamily() {
	    return bodyFontFamily;
	}

	/**
	 * <p>
	 * Font family for tooltip items inherited from global font family
	 * </p>
	 * 
	 * <p>
	 * Default {@code "'Helvetica Neue', 'Helvetica', 'Arial', sans-serif"}
	 * </p>
	 */
	public Tooltips setBodyFontFamily(String bodyFontFamily) {
	    this.bodyFontFamily = bodyFontFamily;
	    return this;
	}

	/**
	 * @see #setBodyFontSize(Integer)
	 */
	public Integer getBodyFontSize() {
	    return bodyFontSize;
	}

	/**
	 * <p>
	 * Font size for tooltip items inherited from global font size
	 * </p>
	 * 
	 * <p>
	 * Default {@code 12}
	 * </p>
	 */
	public Tooltips setBodyFontSize(Integer bodyFontSize) {
	    this.bodyFontSize = bodyFontSize;
	    return this;
	}

	/**
	 * @see #setBodyFontStyle(FontStyle)
	 */
	public FontStyle getBodyFontStyle() {
	    return bodyFontStyle;
	}

	/**
	 * <p>
	 * 
	 * </p>
	 * 
	 * <p>
	 * Default {@code "normal"}
	 * </p>
	 */
	public Tooltips setBodyFontStyle(FontStyle bodyFontStyle) {
	    this.bodyFontStyle = bodyFontStyle;
	    return this;
	}

	/**
	 * @see #setBodyFontColor(Color)
	 */
	public Color getBodyFontColor() {
	    return bodyFontColor;
	}

	/**
	 * <p>
	 * Font color for tooltip items.
	 * </p>
	 * 
	 * <p>
	 * Default {@code "#fff"}
	 * </p>
	 */
	public Tooltips setBodyFontColor(Color bodyFontColor) {
	    this.bodyFontColor = bodyFontColor;
	    return this;
	}

	/**
	 * @see #setBodySpacing(Integer)
	 */
	public Integer getBodySpacing() {
	    return bodySpacing;
	}

	/**
	 * <p>
	 * Spacing to add to top and bottom of each tooltip item
	 * </p>
	 * 
	 * <p>
	 * Default {@code 2}
	 * </p>
	 */
	public Tooltips setBodySpacing(Integer bodySpacing) {
	    this.bodySpacing = bodySpacing;
	    return this;
	}

	/**
	 * @see #setFooterFontFamily(String)
	 */
	public String getFooterFontFamily() {
	    return footerFontFamily;
	}

	/**
	 * <p>
	 * Font family for tooltip footer inherited from global font family.
	 * </p>
	 * 
	 * <p>
	 * Default {@code "'Helvetica Neue', 'Helvetica', 'Arial', sans-serif"}
	 * </p>
	 */
	public Tooltips setFooterFontFamily(String footerFontFamily) {
	    this.footerFontFamily = footerFontFamily;
	    return this;
	}

	/**
	 * @see #setFooterFontSize(Integer)
	 */
	public Integer getFooterFontSize() {
	    return footerFontSize;
	}

	/**
	 * <p>
	 * Font size for tooltip footer inherited from global font size.
	 * </p>
	 * 
	 * <p>
	 * Default {@code 12}
	 * </p>
	 */
	public Tooltips setFooterFontSize(Integer footerFontSize) {
	    this.footerFontSize = footerFontSize;
	    return this;
	}

	/**
	 * @see #setFooterFontStyle(FontStyle)
	 */
	public FontStyle getFooterFontStyle() {
	    return footerFontStyle;
	}

	/**
	 * <p>
	 * Font style for tooltip footer.
	 * </p>
	 * 
	 * <p>
	 * Default {@code "bold"}
	 * </p>
	 */
	public Tooltips setFooterFontStyle(FontStyle footerFontStyle) {
	    this.footerFontStyle = footerFontStyle;
	    return this;
	}

	/**
	 * @see #setFooterFontColor(Color)
	 */
	public Color getFooterFontColor() {
	    return footerFontColor;
	}

	/**
	 * <p>
	 * Font color for tooltip footer.
	 * </p>
	 * 
	 * <p>
	 * Default {@code "#fff"}
	 * </p>
	 */
	public Tooltips setFooterFontColor(Color footerFontColor) {
	    this.footerFontColor = footerFontColor;
	    return this;
	}

	/**
	 * @see #setFooterSpacing(Integer)
	 */
	public Integer getFooterSpacing() {
	    return footerSpacing;
	}

	/**
	 * <p>
	 * Spacing to add to top and bottom of each footer line.
	 * </p>
	 * 
	 * <p>
	 * Default {@code 2}
	 * </p>
	 */
	public Tooltips setFooterSpacing(Integer footerSpacing) {
	    this.footerSpacing = footerSpacing;
	    return this;
	}

	/**
	 * @see #setFooterMarginTop(Integer)
	 */
	public Integer getFooterMarginTop() {
	    return footerMarginTop;
	}

	/**
	 * <p>
	 * Margin to add before drawing the footer
	 * </p>
	 * 
	 * <p>
	 * Default {@code 6}
	 * </p>
	 */
	public Tooltips setFooterMarginTop(Integer footerMarginTop) {
	    this.footerMarginTop = footerMarginTop;
	    return this;
	}

	/**
	 * @see #setXPadding(Integer)
	 */
	public Integer getXPadding() {
	    return xPadding;
	}

	/**
	 * <p>
	 * Padding to add on left and right of tooltip
	 * </p>
	 * 
	 * <p>
	 * Default {@code 6}
	 * </p>
	 */
	public Tooltips setXPadding(Integer xPadding) {
	    this.xPadding = xPadding;
	    return this;
	}

	/**
	 * @see #setYPadding(Integer)
	 */
	public Integer getYPadding() {
	    return yPadding;
	}

	/**
	 * <p>
	 * Padding to add on top and bottom of tooltip
	 * </p>
	 * 
	 * <p>
	 * Default {@code 6}
	 * </p>
	 */
	public Tooltips setYPadding(Integer yPadding) {
	    this.yPadding = yPadding;
	    return this;
	}

	/**
	 * @see #setCaretSize(Integer)
	 */
	public Integer getCaretSize() {
	    return caretSize;
	}

	/**
	 * <p>
	 * Size, in px, of the tooltip arrow
	 * </p>
	 * 
	 * <p>
	 * Default {@code 5}
	 * </p>
	 */
	public Tooltips setCaretSize(Integer caretSize) {
	    this.caretSize = caretSize;
	    return this;
	}

	/**
	 * @see #setCornerRadius(Integer)
	 */
	public Integer getCornerRadius() {
	    return cornerRadius;
	}

	/**
	 * <p>
	 * Radius of tooltip corner curves
	 * </p>
	 * 
	 * <p>
	 * Default {@code 6}
	 * </p>
	 */
	public Tooltips setCornerRadius(Integer cornerRadius) {
	    this.cornerRadius = cornerRadius;
	    return this;
	}

	/**
	 * @see #setMultiKeyBackground(Color)
	 */
	public Color getMultiKeyBackground() {
	    return multiKeyBackground;
	}

	/**
	 * <p>
	 * Color to draw behind the colored boxes when multiple items are in the tooltip
	 * </p>
	 * 
	 * <p>
	 * Default {@code "#fff"}
	 * </p>
	 */
	public Tooltips setMultiKeyBackground(Color multiKeyBackground) {
	    this.multiKeyBackground = multiKeyBackground;
	    return this;
	}
//
//	/**
//	 * @see #setCallbacks(Callbacks)
//	 */
//	public Callbacks getCallbacks() {
//	    return this.callbacks;
//	}
//
//	/**
//	 * <p>
//	 * See the callbacks section below
//	 * </p>
//	 * 
//	 * <p>
//	 * Default {@code }
//	 * </p>
//	 */
//	public Tooltips setCallbacks(Callbacks callbacks) {
//	    this.callbacks = callbacks;
//	}


}
