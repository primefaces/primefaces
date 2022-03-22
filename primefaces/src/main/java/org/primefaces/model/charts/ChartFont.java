/*
 * The MIT License
 *
 * Copyright (c) 2009-2021 PrimeTek
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
package org.primefaces.model.charts;

import java.io.IOException;
import java.io.Serializable;
import java.util.Objects;

import org.primefaces.util.ChartUtils;
import org.primefaces.util.FastStringWriter;
import org.primefaces.util.LangUtils;

/**
 * Chart Font representation.
 */
public class ChartFont implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Default font family for all text, follows CSS font-family options.
     */
    private String family;

    /**
     * Default font size (in px) for text. Does not apply to radialLinear scale point labels.
     */
    private Number size;

    /**
     * Default font style. Does not apply to tooltip title or footer. Does not apply to chart title. Follows CSS font-style options (i.e. normal, italic,
     * oblique, initial, inherit).
     */
    private String style;

    /**
     * Default font weight (boldness).
     */
    private String weight;

    /**
     * Height of an individual line of text.
     */
    private Object lineHeight;

    public ChartFont() {
        // no op
    }

    public String getFamily() {
        return family;
    }

    public void setFamily(String family) {
        this.family = family;
    }

    public Number getSize() {
        return size;
    }

    public void setSize(Number size) {
        this.size = size;
    }

    public String getStyle() {
        return style;
    }

    public void setStyle(String style) {
        this.style = style;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public Object getLineHeight() {
        return lineHeight;
    }

    public void setLineHeight(Object lineHeight) {
        this.lineHeight = lineHeight;
    }

    /**
     * Detect if any 1 field is set we should render.
     *
     * @return true if should be rendered
     */
    public boolean shouldRender() {
        return LangUtils.isNotBlank(this.family) || LangUtils.isNotBlank(this.style) || LangUtils.isNotBlank(this.weight) || this.size != null
                    || this.lineHeight != null;
    }

    /**
     * Write this font out to the writer.
     *
     * @param writer the writer to print to.
     * @throws IOException If an I/O error occurs
     */
    public void write(FastStringWriter writer) throws IOException {
        write(writer, "font", true);
    }

    /**
     * Write this font out to the writer.
     *
     * @param writer the writer to print to.
     * @throws IOException If an I/O error occurs
     */
    public void write(FastStringWriter writer, String name, boolean hasComma) throws IOException {
        if (this.shouldRender()) {
            if (hasComma) {
                writer.write(",");
            }
            writer.write("\"" + name + "\":" + this.encode());
        }
    }

    /**
     * Write the options of font
     *
     * @return options as JSON object
     * @throws java.io.IOException If an I/O error occurs
     */
    public String encode() throws IOException {
        try (FastStringWriter fsw = new FastStringWriter()) {
            fsw.write("{");

            boolean hasComma = false;
            if (LangUtils.isNotBlank(this.family)) {
                ChartUtils.writeDataValue(fsw, "family", this.family, hasComma);
                hasComma = true;
            }
            if (!Objects.isNull(this.size)) {
                ChartUtils.writeDataValue(fsw, "size", this.size, hasComma);
                hasComma = true;
            }
            if (LangUtils.isNotBlank(this.style)) {
                ChartUtils.writeDataValue(fsw, "style", this.style, hasComma);
                hasComma = true;
            }
            if (LangUtils.isNotBlank(this.weight)) {
                ChartUtils.writeDataValue(fsw, "weight", this.weight, hasComma);
                hasComma = true;
            }
            if (!Objects.isNull(this.lineHeight)) {
                ChartUtils.writeDataValue(fsw, "lineHeight", this.lineHeight, hasComma);
                hasComma = true;
            }

            fsw.write("}");

            return fsw.toString();
        }
    }

}
