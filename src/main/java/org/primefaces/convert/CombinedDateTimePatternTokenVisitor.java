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

import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.primefaces.convert.PatternReader.TokenVisitor;

/**
 * A combination of the pattern letters as defined by {@link SimpleDateFormat}
 * and {@link DateTimeFormatter}. When the interpretation of a pattern letter
 * differs between both schemes, an appropriate default is used, as defined by
 * previous PrimeFaces (&lt; 9) versions. This mainly concerns 'u', which is the
 * 'Day number of week' in the {@link SimpleDateFormat}, but the 'year' in the
 * {@link DateTimeFormatter}.
 */
public class CombinedDateTimePatternTokenVisitor implements TokenVisitor {
    private static final Logger LOGGER = Logger.getLogger(DateTimePatternConverter.class.getName());

    private final JQueryDateTimePatternBuilder builder;

    /**
     * Creates a new visitor for a Java date time pattern. Creates a Query UI date picker.
     * @param builder Builder for creating the JQuery UI date picker pattern.
     */
    public CombinedDateTimePatternTokenVisitor(JQueryDateTimePatternBuilder builder) {
        this.builder = builder;
    }

    @Override
    public void visitLiteral(String text) {
        visitLiteralToken(text, builder);
    }

    @Override
    public void visitTokenLetter(int letter, int repetitions) {
        switch (letter) {
            // date
            case 'u':
            case 'y':
                visitYearToken(repetitions, builder);
                break;
            case 'M':
                visitMonthToken(repetitions, builder);
                break;
            case 'd':
                visitDayOfMonthToken(repetitions, builder);
                break;
            case 'D':
                visitDayOfYearToken(repetitions, builder);
                break;
            case 'E':
                visitDayOfWeekToken(repetitions, builder);
                break;

            // time
            case 'h':
                visitAmPmHourToken(repetitions, builder);
                break;
            case 'H':
                visitHourToken(repetitions, builder);
                break;
            case 'm':
                visitMinuteToken(repetitions, builder);
                break;
            case 's':
                visitSecondToken(repetitions, builder);
                break;
            case 'S':
                visitMillisecondToken(repetitions, builder);
                break;
            case 'a':
                visitAmPmToken(repetitions, builder);
                break;

            // time zone
            case 'z':
            case 'Z':
                visitZoneZToken(repetitions, builder);
                break;
            case 'X':
                visitZoneXToken(repetitions, builder);
                break;

            default:
                final String letterString = Character.toString((char) letter);
                if (LOGGER.isLoggable(Level.WARNING)) {
                    LOGGER.log(Level.WARNING, "Found unsupported letter '" + letterString
                                + "' in pattern. Date / times will be formatted differently on the server and client. Use a different pattern.");
                }
                visitDefault(letterString, repetitions, builder);
        }
    }

    /**
     * Half-hour (0-11) token 'h' and 'h'. 'h' is the hour without leading zeros,
     * more than one 'h' is the hour with leading zeros.
     */
    private static void visitAmPmHourToken(int repetitions, JQueryDateTimePatternBuilder builder) {
        if (repetitions == 1) {
            builder.appendAmPmHourNoLeadingZeros();
        }
        else {
            builder.appendAmPmHourWithLeadingZeros();
        }
    }

    /**
     * AM / PM token 'a'. Either the uppercase `AM` or `PM`.
     */
    private static void visitAmPmToken(int repetitions, JQueryDateTimePatternBuilder builder) {
        builder.appendAmPmLongUppercase();
    }

    /**
     * Day of month token 'd' and 'dd'. `d` is without leading zeros, 'dd' and more
     * is with leading zeros.
     */
    private static void visitDayOfMonthToken(int repetitions, JQueryDateTimePatternBuilder builder) {
        if (repetitions == 1) {
            builder.appendDayOfMonthNoLeadingZeros();
        }
        else {
            builder.appendDayOfMonthWithLeadingZeros();
        }
    }

    /**
     * Day of week token 'E' and 'EE', 'EEE', and 'EEEE'. Four and more 'E' are
     * interpreted as a long day name, everything else as a short day name.
     */
    private static void visitDayOfWeekToken(int repetitions, JQueryDateTimePatternBuilder builder) {
        switch (repetitions) {
            case 1:
            case 2:
            case 3:
                builder.appendDayNameShort();
                break;
            default: // 4+
                builder.appendDayNameLong();
                break;
        }
    }

    /**
     * Day of the year token 'D', 'DD, 'DDD'.
     * <ul>
     * <li>D and DD is the day of the year without leading zeros</li>
     * <li>DDD is a three-digit day of the year with leading zeros.</li>
     * </ul>
     * More than three 'D' are interpreted as 'DDD'.
     */
    private static void visitDayOfYearToken(int repetitions, JQueryDateTimePatternBuilder builder) {
        if (repetitions == 1) {
            builder.appendDayOfYearNoLeadingZeros();
        }
        else {
            // 2+
            builder.appendDayOfYearWithLeadingZeros();
        }
    }

    /**
     * Full-hour (0-23) token 'H' and 'HH'. 'H' is the hour without leading zeros,
     * more than one 'H' is the hour with leading zeros.
     */
    private static void visitHourToken(int repetitions, JQueryDateTimePatternBuilder builder) {
        if (repetitions == 1) {
            builder.appendHourNoLeadingZeros();
        }
        else {
            builder.appendHourWithLeadingZeros();
        }
    }

    /**
     * Visits a literal text token. Literal text is not modified in any way and inserted as literal
     * text into the JQuery date time pattern.
     */
    private static void visitLiteralToken(String text, JQueryDateTimePatternBuilder builder) {
        builder.appendLiteralText(text);
    }

    /**
     * Millisecond token 'S'. Always interpreted as optional leading zeros.
     *
     */
    private static void visitMillisecondToken(int repetitions, JQueryDateTimePatternBuilder builder) {
        // actually also accepts no leading zeros
        builder.appendMillisecondsWithLeadingZeros();
    }

    /**
     * Minute token 'm' and 'mm'. 'm' is the minute without leading zeros, more than
     * one 'm' is the minute with leading zeros (two digits).
     */
    private static void visitMinuteToken(int repetitions, JQueryDateTimePatternBuilder builder) {
        if (repetitions == 1) {
            builder.appendMinuteNoLeadingZeros();
        }
        else {
            builder.appendMinuteWithLeadingZeros();
        }
    }

    /**
     * Month token 'M', 'MM', 'MMM', 'MMMM'.
     * <ul>
     * <li>M is a numeric month without leading zeros</li>
     * <li>MM is a numeric month with leading zeros</li>
     * <li>MMM is a short month name</li>
     * <li>MMMM is a long month name</li>
     * </ul>
     * More than four 'M' are treated as 'MMMM'.
     */
    private static void visitMonthToken(int repetitions, JQueryDateTimePatternBuilder builder) {
        // M and MM is a numeric month
        // MMM and MMMM are month names
        switch (repetitions) {
            case 1:
                builder.appendMonthOfYearNoLeadingZeros();
                break;
            case 2:
                builder.appendMonthOfYearWithLeadingZeros();
                break;
            case 3:
                builder.appendMonthNameShort();
                break;
            default: // 4+
                builder.appendMonthNameLong();
                break;
        }
    }

    private static void visitDefault(CharSequence string, int repetitions, JQueryDateTimePatternBuilder builder) {
        for (int i = 0; i < repetitions; ++i) {
            builder.getStringBuilder().append(string);
        }
    }

    /**
     * Second token 's' and 'ss'. 's' is the second without leading zeros, more than
     * one 's' is the second with leading zeros (two digits).
     */
    private static void visitSecondToken(int repetitions, JQueryDateTimePatternBuilder builder) {
        if (repetitions == 1) {
            builder.appendSecondNoLeadingZeros();
        }
        else {
            builder.appendSecondWithLeadingZeros();
        }
    }

    /**
     * Year token 'Y', 'YY, 'YYY, 'YYYY'. 'YY' is the short year (truncated to two
     * digits), everything else is a full year. Also allows 'u' (to support
     * {@link DateTimeFormatter}) and 'Y'.
     */
    private static void visitYearToken(int repetitions, JQueryDateTimePatternBuilder builder) {
        if (repetitions == 2) {
            builder.appendYearTwoDigits();
        }
        else {
            builder.appendYearFourDigits();
        }
    }

    /**
     * Time zone 'X', 'XX', and 'XXX' token.
     */
    private static void visitZoneXToken(int repetitions, JQueryDateTimePatternBuilder builder) {
        switch (repetitions) {
            case 1:
            case 2:
                builder.appendTimezoneByProvidedTimezoneList();
                break;
            default: // 3+
                builder.appendTimezoneIso8601();
                break;
        }
    }

    /**
     * Time zone 'Z' or 'z' token.
     */
    private static void visitZoneZToken(int repetitions, JQueryDateTimePatternBuilder builder) {
        builder.appendTimezoneByProvidedTimezoneList();
    }
}
