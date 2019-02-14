/**
 * The MIT License
 *
 * Copyright (c) 2009-2019 PrimeTek
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.primefaces.model.charts.optionconfig.tooltip;

import java.io.IOException;
import java.io.Serializable;

import org.primefaces.util.ChartUtils;
import org.primefaces.util.FastStringWriter;

/**
 * The tooltip configuration is passed into the options.tooltips namespace.
 */
public class Tooltip implements Serializable {

    private static final long serialVersionUID = 1L;

    private boolean enabled = true;
    private String mode;
    private boolean intersect;
    private String position;
    private String backgroundColor;
    private String titleFontFamily;
    private Number titleFontSize;
    private String titleFontStyle;
    private String titleFontColor;
    private Number titleSpacing;
    private Number titleMarginBottom;
    private String bodyFontFamily;
    private Number bodyFontSize;
    private String bodyFontStyle;
    private String bodyFontColor;
    private Number bodySpacing;
    private String footerFontFamily;
    private Number footerFontSize;
    private String footerFontStyle;
    private String footerFontColor;
    private Number footerSpacing;
    private Number footerMarginTop;
    private Number xpadding;
    private Number ypadding;
    private Number caretPadding;
    private Number caretSize;
    private Number cornerRadius;
    private String multiKeyBackground;
    private boolean displayColors;
    private String borderColor;
    private Number borderWidth;

    /**
     * Gets the enabled
     *
     * @return enabled
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * Sets the enabled
     *
     * @param enabled Are on-canvas tooltips enabled
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * Gets the mode
     *
     * @return mode
     */
    public String getMode() {
        return mode;
    }

    /**
     * Sets the mode
     *
     * @param mode Sets which elements appear in the tooltip.
     */
    public void setMode(String mode) {
        this.mode = mode;
    }

    /**
     * Gets the intersect
     *
     * @return intersect
     */
    public boolean isIntersect() {
        return intersect;
    }

    /**
     * Sets the intersect
     *
     * @param intersect if true, the tooltip mode applies only when the mouse position intersects with an element.
     * If false, the mode will be applied at all times.
     */
    public void setIntersect(boolean intersect) {
        this.intersect = intersect;
    }

    /**
     * Gets the position
     *
     * @return position
     */
    public String getPosition() {
        return position;
    }

    /**
     * Sets the position
     *
     * @param position The mode for positioning the tooltip.
     */
    public void setPosition(String position) {
        this.position = position;
    }

    /**
     * Gets the backgroundColor
     *
     * @return backgroundColor
     */
    public String getBackgroundColor() {
        return backgroundColor;
    }

    /**
     * Sets the backgroundColor
     *
     * @param backgroundColor Background color of the tooltip.
     */
    public void setBackgroundColor(String backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    /**
     * Gets the titleFontFamily
     *
     * @return titleFontFamily
     */
    public String getTitleFontFamily() {
        return titleFontFamily;
    }

    /**
     * Sets the titleFontFamily
     *
     * @param titleFontFamily title font
     */
    public void setTitleFontFamily(String titleFontFamily) {
        this.titleFontFamily = titleFontFamily;
    }

    /**
     * Gets the titleFontSize
     *
     * @return titleFontSize
     */
    public Number getTitleFontSize() {
        return titleFontSize;
    }

    /**
     * Sets the titleFontSize
     *
     * @param titleFontSize Title font size
     */
    public void setTitleFontSize(Number titleFontSize) {
        this.titleFontSize = titleFontSize;
    }

    /**
     * Gets the titleFontStyle
     *
     * @return titleFontStyle
     */
    public String getTitleFontStyle() {
        return titleFontStyle;
    }

    /**
     * Sets the titleFontStyle
     *
     * @param titleFontStyle Title font style
     */
    public void setTitleFontStyle(String titleFontStyle) {
        this.titleFontStyle = titleFontStyle;
    }

    /**
     * Gets the titleFontColor
     *
     * @return titleFontColor
     */
    public String getTitleFontColor() {
        return titleFontColor;
    }

    /**
     * Sets the titleFontColor
     *
     * @param titleFontColor Title font color
     */
    public void setTitleFontColor(String titleFontColor) {
        this.titleFontColor = titleFontColor;
    }

    /**
     * Gets the titleSpacing
     *
     * @return titleSpacing
     */
    public Number getTitleSpacing() {
        return titleSpacing;
    }

    /**
     * Sets the titleSpacing
     *
     * @param titleSpacing Spacing to add to top and bottom of each title line.
     */
    public void setTitleSpacing(Number titleSpacing) {
        this.titleSpacing = titleSpacing;
    }

    /**
     * Gets the titleMarginBottom
     *
     * @return titleMarginBottom
     */
    public Number getTitleMarginBottom() {
        return titleMarginBottom;
    }

    /**
     * Sets the titleMarginBottom
     *
     * @param titleMarginBottom Margin to add on bottom of title section.
     */
    public void setTitleMarginBottom(Number titleMarginBottom) {
        this.titleMarginBottom = titleMarginBottom;
    }

    /**
     * Gets the bodyFontFamily
     *
     * @return bodyFontFamily
     */
    public String getBodyFontFamily() {
        return bodyFontFamily;
    }

    /**
     * Sets the bodyFontFamily
     *
     * @param bodyFontFamily body line font
     */
    public void setBodyFontFamily(String bodyFontFamily) {
        this.bodyFontFamily = bodyFontFamily;
    }

    /**
     * Gets the bodyFontSize
     *
     * @return bodyFontSize
     */
    public Number getBodyFontSize() {
        return bodyFontSize;
    }

    /**
     * Sets the bodyFontSize
     *
     * @param bodyFontSize Body font size
     */
    public void setBodyFontSize(Number bodyFontSize) {
        this.bodyFontSize = bodyFontSize;
    }

    /**
     * Gets the bodyFontStyle
     *
     * @return bodyFontStyle
     */
    public String getBodyFontStyle() {
        return bodyFontStyle;
    }

    /**
     * Sets the bodyFontStyle
     *
     * @param bodyFontStyle Body font style
     */
    public void setBodyFontStyle(String bodyFontStyle) {
        this.bodyFontStyle = bodyFontStyle;
    }

    /**
     * Gets the bodyFontColor
     *
     * @return bodyFontColor
     */
    public String getBodyFontColor() {
        return bodyFontColor;
    }

    /**
     * Sets the bodyFontColor
     *
     * @param bodyFontColor Body font color
     */
    public void setBodyFontColor(String bodyFontColor) {
        this.bodyFontColor = bodyFontColor;
    }

    /**
     * Gets the bodySpacing
     *
     * @return bodySpacing
     */
    public Number getBodySpacing() {
        return bodySpacing;
    }

    /**
     * Sets the bodySpacing
     *
     * @param bodySpacing Spacing to add to top and bottom of each tooltip item.
     */
    public void setBodySpacing(Number bodySpacing) {
        this.bodySpacing = bodySpacing;
    }

    /**
     * Gets the footerFontFamily
     *
     * @return footerFontFamily
     */
    public String getFooterFontFamily() {
        return footerFontFamily;
    }

    /**
     * Sets the footerFontFamily
     *
     * @param footerFontFamily footer font
     */
    public void setFooterFontFamily(String footerFontFamily) {
        this.footerFontFamily = footerFontFamily;
    }

    /**
     * Gets the footerFontSize
     *
     * @return footerFontSize
     */
    public Number getFooterFontSize() {
        return footerFontSize;
    }

    /**
     * Sets the footerFontSize
     *
     * @param footerFontSize Footer font size
     */
    public void setFooterFontSize(Number footerFontSize) {
        this.footerFontSize = footerFontSize;
    }

    /**
     * Gets the footerFontStyle
     *
     * @return footerFontStyle
     */
    public String getFooterFontStyle() {
        return footerFontStyle;
    }

    /**
     * Sets the footerFontStyle
     *
     * @param footerFontStyle Footer font style
     */
    public void setFooterFontStyle(String footerFontStyle) {
        this.footerFontStyle = footerFontStyle;
    }

    /**
     * Gets the footerFontColor
     *
     * @return footerFontColor
     */
    public String getFooterFontColor() {
        return footerFontColor;
    }

    /**
     * Sets the footerFontColor
     *
     * @param footerFontColor Footer font color
     */
    public void setFooterFontColor(String footerFontColor) {
        this.footerFontColor = footerFontColor;
    }

    /**
     * Gets the footerSpacing
     *
     * @return footerSpacing
     */
    public Number getFooterSpacing() {
        return footerSpacing;
    }

    /**
     * Sets the footerSpacing
     *
     * @param footerSpacing Spacing to add to top and bottom of each footer line.
     */
    public void setFooterSpacing(Number footerSpacing) {
        this.footerSpacing = footerSpacing;
    }

    /**
     * Gets the footerMarginTop
     *
     * @return footerMarginTop
     */
    public Number getFooterMarginTop() {
        return footerMarginTop;
    }

    /**
     * Sets the footerMarginTop
     *
     * @param footerMarginTop Margin to add before drawing the footer.
     */
    public void setFooterMarginTop(Number footerMarginTop) {
        this.footerMarginTop = footerMarginTop;
    }

    /**
     * Gets the xPadding
     *
     * @return xPadding
     */
    public Number getXpadding() {
        return xpadding;
    }

    /**
     * Sets the xPadding
     *
     * @param xpadding Padding to add on left and right of tooltip.
     */
    public void setXpadding(Number xpadding) {
        this.xpadding = xpadding;
    }

    /**
     * Gets the yPadding
     *
     * @return yPadding
     */
    public Number getYpadding() {
        return ypadding;
    }

    /**
     * Sets the yPadding
     *
     * @param ypadding Padding to add on top and bottom of tooltip.
     */
    public void setYpadding(Number ypadding) {
        this.ypadding = ypadding;
    }

    /**
     * Gets the caretPadding
     *
     * @return caretPadding
     */
    public Number getCaretPadding() {
        return caretPadding;
    }

    /**
     * Sets the caretPadding
     *
     * @param caretPadding Extra distance to move the end of the tooltip arrow away from the tooltip point.
     */
    public void setCaretPadding(Number caretPadding) {
        this.caretPadding = caretPadding;
    }

    /**
     * Gets the caretSize
     *
     * @return caretSize
     */
    public Number getCaretSize() {
        return caretSize;
    }

    /**
     * Sets the caretSize
     *
     * @param caretSize Size, in px, of the tooltip arrow.
     */
    public void setCaretSize(Number caretSize) {
        this.caretSize = caretSize;
    }

    /**
     * Gets the cornerRadius
     *
     * @return cornerRadius
     */
    public Number getCornerRadius() {
        return cornerRadius;
    }

    /**
     * Sets the cornerRadius
     *
     * @param cornerRadius Radius of tooltip corner curves.
     */
    public void setCornerRadius(Number cornerRadius) {
        this.cornerRadius = cornerRadius;
    }

    /**
     * Gets the multiKeyBackground
     *
     * @return multiKeyBackground
     */
    public String getMultiKeyBackground() {
        return multiKeyBackground;
    }

    /**
     * Sets the multiKeyBackground
     *
     * @param multiKeyBackground Color to draw behind the colored boxes when multiple items are in the tooltip
     */
    public void setMultiKeyBackground(String multiKeyBackground) {
        this.multiKeyBackground = multiKeyBackground;
    }

    /**
     * Gets the displayColors
     *
     * @return displayColors
     */
    public boolean isDisplayColors() {
        return displayColors;
    }

    /**
     * Sets the displayColors
     *
     * @param displayColors if true, color boxes are shown in the tooltip
     */
    public void setDisplayColors(boolean displayColors) {
        this.displayColors = displayColors;
    }

    /**
     * Gets the borderColor
     *
     * @return borderColor
     */
    public String getBorderColor() {
        return borderColor;
    }

    /**
     * Sets the borderColor
     *
     * @param borderColor Color of the border
     */
    public void setBorderColor(String borderColor) {
        this.borderColor = borderColor;
    }

    /**
     * Gets the borderWidth
     *
     * @return borderWidth
     */
    public Number getBorderWidth() {
        return borderWidth;
    }

    /**
     * Sets the borderWidth
     *
     * @param borderWidth Size of the border
     */
    public void setBorderWidth(Number borderWidth) {
        this.borderWidth = borderWidth;
    }

    /**
     * Write the options of Tooltip
     *
     * @return options as JSON object
     * @throws java.io.IOException If an I/O error occurs
     */
    public String encode() throws IOException {
        FastStringWriter fsw = new FastStringWriter();

        try {
            ChartUtils.writeDataValue(fsw, "enabled", this.enabled, false);
            ChartUtils.writeDataValue(fsw, "mode", this.mode, true);
            ChartUtils.writeDataValue(fsw, "intersect", this.intersect, true);
            ChartUtils.writeDataValue(fsw, "position", this.position, true);
            ChartUtils.writeDataValue(fsw, "backgroundColor", this.backgroundColor, true);
            ChartUtils.writeDataValue(fsw, "titleFontFamily", this.titleFontFamily, true);
            ChartUtils.writeDataValue(fsw, "titleFontSize", this.titleFontSize, true);
            ChartUtils.writeDataValue(fsw, "titleFontStyle", this.titleFontStyle, true);
            ChartUtils.writeDataValue(fsw, "titleFontColor", this.titleFontColor, true);
            ChartUtils.writeDataValue(fsw, "titleSpacing", this.titleSpacing, true);
            ChartUtils.writeDataValue(fsw, "titleMarginBottom", this.titleMarginBottom, true);
            ChartUtils.writeDataValue(fsw, "bodyFontFamily", this.bodyFontFamily, true);
            ChartUtils.writeDataValue(fsw, "bodyFontSize", this.bodyFontSize, true);
            ChartUtils.writeDataValue(fsw, "bodyFontStyle", this.bodyFontStyle, true);
            ChartUtils.writeDataValue(fsw, "bodyFontColor", this.bodyFontColor, true);
            ChartUtils.writeDataValue(fsw, "bodySpacing", this.bodySpacing, true);
            ChartUtils.writeDataValue(fsw, "footerFontFamily", this.footerFontFamily, true);
            ChartUtils.writeDataValue(fsw, "footerFontSize", this.footerFontSize, true);
            ChartUtils.writeDataValue(fsw, "footerFontStyle", this.footerFontStyle, true);
            ChartUtils.writeDataValue(fsw, "footerFontColor", this.footerFontColor, true);
            ChartUtils.writeDataValue(fsw, "footerSpacing", this.footerSpacing, true);
            ChartUtils.writeDataValue(fsw, "footerMarginTop", this.footerMarginTop, true);
            ChartUtils.writeDataValue(fsw, "xPadding", this.xpadding, true);
            ChartUtils.writeDataValue(fsw, "yPadding", this.ypadding, true);
            ChartUtils.writeDataValue(fsw, "caretPadding", this.caretPadding, true);
            ChartUtils.writeDataValue(fsw, "caretSize", this.caretSize, true);
            ChartUtils.writeDataValue(fsw, "cornerRadius", this.cornerRadius, true);
            ChartUtils.writeDataValue(fsw, "multiKeyBackground", this.multiKeyBackground, true);
            ChartUtils.writeDataValue(fsw, "displayColors", this.displayColors, true);
            ChartUtils.writeDataValue(fsw, "borderColor", this.borderColor, true);
            ChartUtils.writeDataValue(fsw, "borderWidth", this.borderWidth, true);
        }
        finally {
            fsw.close();
        }

        return fsw.toString();
    }
}
