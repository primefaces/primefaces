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
package org.primefaces.convert;

import java.util.PrimitiveIterator.OfInt;

import org.primefaces.util.LangUtils;

/**
 * Builder for the pattern format as defined by the jquery UI datepicker, see
 * https://api.jqueryui.com/datepicker; and the jquery timepicker, see
 * http://trentrichardson.com/examples/timepicker. Wraps a {@link StringBuilder}
 * and provides additional convenience methods. If necessary, you can also
 * access the raw {@link #getStringBuilder()}.
 */
public class JQueryDateTimePatternBuilder {
    private static final boolean[] NEEDS_QUOTE = new boolean[256];

    static {
        // date
        NEEDS_QUOTE['d'] = true;
        NEEDS_QUOTE['o'] = true;
        NEEDS_QUOTE['D'] = true;
        NEEDS_QUOTE['m'] = true;
        NEEDS_QUOTE['M'] = true;
        NEEDS_QUOTE['y'] = true;
        NEEDS_QUOTE['@'] = true;
        NEEDS_QUOTE['!'] = true;
        // time
        NEEDS_QUOTE['H'] = true;
        NEEDS_QUOTE['h'] = true;
        NEEDS_QUOTE['m'] = true;
        NEEDS_QUOTE['s'] = true;
        NEEDS_QUOTE['l'] = true;
        NEEDS_QUOTE['c'] = true;
        NEEDS_QUOTE['t'] = true;
        NEEDS_QUOTE['T'] = true;
        NEEDS_QUOTE['z'] = true;
        NEEDS_QUOTE['Z'] = true;
    }

    private final StringBuilder sb = new StringBuilder();

    public JQueryDateTimePatternBuilder appendAmPmHourNoLeadingZeros() {
        sb.append("h");
        return this;
    }

    public JQueryDateTimePatternBuilder appendAmPmHourWithLeadingZeros() {
        sb.append("hh");
        return this;
    }

    public JQueryDateTimePatternBuilder appendAmPmLongLowercase() {
        sb.append("tt");
        return this;
    }

    public JQueryDateTimePatternBuilder appendAmPmLongUppercase() {
        sb.append("TT");
        return this;
    }

    public JQueryDateTimePatternBuilder appendAmPmShortLowercase() {
        sb.append("t");
        return this;
    }

    public JQueryDateTimePatternBuilder appendAmPmShortUppercase() {
        sb.append("T");
        return this;
    }

    public JQueryDateTimePatternBuilder appendDayNameLong() {
        sb.append("DD");
        return this;
    }

    public JQueryDateTimePatternBuilder appendDayNameShort() {
        sb.append("D");
        return this;
    }

    public JQueryDateTimePatternBuilder appendDayOfMonthNoLeadingZeros() {
        sb.append("d");
        return this;
    }

    public JQueryDateTimePatternBuilder appendDayOfMonthWithLeadingZeros() {
        sb.append("dd");
        return this;
    }

    public JQueryDateTimePatternBuilder appendDayOfYearNoLeadingZeros() {
        sb.append("o");
        return this;
    }

    public JQueryDateTimePatternBuilder appendDayOfYearWithLeadingZeros() {
        sb.append("oo");
        return this;
    }

    public JQueryDateTimePatternBuilder appendHourNoLeadingZeros() {
        sb.append("H");
        return this;
    }

    public JQueryDateTimePatternBuilder appendHourWithLeadingZeros() {
        sb.append("HH");
        return this;
    }

    public JQueryDateTimePatternBuilder appendLiteralText(String text) {
        if (!LangUtils.isValueEmpty(text)) {
            if (isQuoteNeeded(text)) {
                sb.append('\'');
                appendEscapedLiteralText(text);
                sb.append('\'');
            }
            else {
                appendEscapedLiteralText(text);
            }
        }
        return this;
    }

    public JQueryDateTimePatternBuilder appendMicrosecondsWithLeadingZeros() {
        sb.append("c");
        return this;
    }

    public JQueryDateTimePatternBuilder appendMillisecondsWithLeadingZeros() {
        sb.append("l");
        return this;
    }

    public JQueryDateTimePatternBuilder appendMinuteNoLeadingZeros() {
        sb.append("m");
        return this;
    }

    public JQueryDateTimePatternBuilder appendMinuteWithLeadingZeros() {
        sb.append("mm");
        return this;
    }

    public JQueryDateTimePatternBuilder appendMonthNameLong() {
        sb.append("MM");
        return this;
    }

    public JQueryDateTimePatternBuilder appendMonthNameShort() {
        sb.append("M");
        return this;
    }

    public JQueryDateTimePatternBuilder appendMonthOfYearNoLeadingZeros() {
        sb.append("m");
        return this;
    }

    public JQueryDateTimePatternBuilder appendMonthOfYearWithLeadingZeros() {
        sb.append("mm");
        return this;
    }

    public JQueryDateTimePatternBuilder appendSecondNoLeadingZeros() {
        sb.append("s");
        return this;
    }

    public JQueryDateTimePatternBuilder appendSecondWithLeadingZeros() {
        sb.append("ss");
        return this;
    }

    public JQueryDateTimePatternBuilder appendTimezoneByProvidedTimezoneList() {
        sb.append("z");
        return this;
    }

    public JQueryDateTimePatternBuilder appendTimezoneIso8601() {
        sb.append("Z");
        return this;
    }

    public JQueryDateTimePatternBuilder appendUnixTimestamp() {
        sb.append("@");
        return this;
    }

    public JQueryDateTimePatternBuilder appendWindowsTicks() {
        sb.append("!");
        return this;
    }

    public JQueryDateTimePatternBuilder appendYearFourDigits() {
        sb.append("yy");
        return this;
    }

    public JQueryDateTimePatternBuilder appendYearTwoDigits() {
        sb.append("y");
        return this;
    }

    public StringBuilder getStringBuilder() {
        return sb;
    }

    @Override
    public String toString() {
        return sb.toString();
    }

    private void appendEscapedLiteralText(String text) {
        for (final OfInt it = text.codePoints().iterator(); it.hasNext();) {
            final int ch = it.nextInt();
            // Literal ' needs to be escaped as ''
            if (ch == '\'') {
                sb.append("''");
            }
            else {
                sb.appendCodePoint(ch);
            }
        }
    }

    private boolean isQuoteNeeded(String text) {
        for (final OfInt it = text.codePoints().iterator(); it.hasNext();) {
            final int ch = it.nextInt();
            if (ch >= 0 && ch < 256 && NEEDS_QUOTE[ch]) {
                return true;
            }
        }
        return false;
    }
}
