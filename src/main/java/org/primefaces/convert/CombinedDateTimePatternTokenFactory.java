/*
 * The MIT License
 *
 * Copyright (c) 2009-2020 PrimeTek
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

import org.primefaces.convert.DateTimePatternConverter.JQueryPatternLetterToken;
import org.primefaces.convert.DateTimePatternConverter.JqueryLiteralToken;
import org.primefaces.convert.DateTimePatternConverter.JqueryPatternToken;
import org.primefaces.convert.PatternReader.TokenFactory;

/**
 * A combination of the pattern letters as defined by {@link SimpleDateFormat}
 * and {@link DateTimeFormatter}. When the interpretation of a pattern letter
 * differs between both schemes, an appropriate default is used, as defined by
 * previous PrimeFaces (&lt; 9) versions. This mainly concerns 'u', which is the
 * 'Day number of week' in the {@link SimpleDateFormat}, but the 'year' in the
 * {@link DateTimeFormatter}.
 */
public class CombinedDateTimePatternTokenFactory implements TokenFactory<JqueryPatternToken> {
    private static final Logger LOGGER = Logger.getLogger(DateTimePatternConverter.class.getName());

    private static TokenFactory<JqueryPatternToken> instance = new CombinedDateTimePatternTokenFactory();

    private CombinedDateTimePatternTokenFactory() {
    }

    @Override
    public JqueryPatternToken getForLiteral(String text) {
        return new JqueryLiteralToken(text);
    }

    @Override
    public JqueryPatternToken getForTokenLetter(int letter, int repetitions) {
        switch (letter) {
            // date
            case 'u':
            case 'y':
                return new YearToken(repetitions);
            case 'M':
                return new MonthToken(repetitions);
            case 'd':
                return new DayOfMonthToken(repetitions);
            case 'D':
                return new DayOfYearToken(repetitions);
            case 'E':
                return new DayOfWeekToken(repetitions);

            // time
            case 'h':
                return new AmPmHourToken(repetitions);
            case 'H':
                return new HourToken(repetitions);
            case 'm':
                return new MinuteToken(repetitions);
            case 's':
                return new SecondToken(repetitions);
            case 'S':
                return new MillisecondToken(repetitions);
            case 'a':
                return new AmPmToken(repetitions);

            // time zone
            case 'z':
                return new ZonezToken(repetitions);
            case 'Z':
                return new ZoneZToken(repetitions);
            case 'X':
                return new ZoneXToken(repetitions);

            default:
                final String letterString = Character.toString((char) letter);
                LOGGER.log(Level.WARNING, "Found unsupported letter '" + letterString
                        + "' in pattern. Date / times will be formatted differently on the server and client. Use a different pattern.");
                return new PassthroughToken(letterString, repetitions);
        }
    }

    public static TokenFactory<JqueryPatternToken> getInstance() {
        return instance;
    }

    /**
     * Half-hour (0-11) token 'h' and 'h'. 'h' is the hour without leading zeros,
     * more than one 'h' is the hour with leading zeros.
     */
    private static class AmPmHourToken extends JQueryPatternLetterToken {
        public AmPmHourToken(int repetitions) {
            super(repetitions);
        }

        @Override
        public void buildJqueryUiPattern(JQueryDateTimePatternBuilder builder) {
            if (repetitions == 1) {
                builder.appendAmPmHourNoLeadingZeros();
            }
            else {
                builder.appendAmPmHourWithLeadingZeros();
            }
        }
    }

    /**
     * AM / PM token 'a'. Either the uppercase `AM` or `PM`.
     */
    private static class AmPmToken extends JQueryPatternLetterToken {
        public AmPmToken(int repetitions) {
            super(repetitions);
        }

        @Override
        public void buildJqueryUiPattern(JQueryDateTimePatternBuilder builder) {
            builder.appendAmPmLongUppercase();
        }
    }

    /**
     * Day of month token 'd' and 'dd'. `d` is without leading zeros, 'dd' and more
     * is with leading zeros.
     */
    private static class DayOfMonthToken extends JQueryPatternLetterToken {
        public DayOfMonthToken(int repetitions) {
            super(repetitions);
        }

        @Override
        public void buildJqueryUiPattern(JQueryDateTimePatternBuilder builder) {
            if (repetitions == 1) {
                builder.appendDayOfMonthNoLeadingZeros();
            }
            else {
                builder.appendDayOfMonthWithLeadingZeros();
            }
        }
    }

    /**
     * Day of week token 'E' and 'EE', 'EEE', and 'EEEE'. Four and more 'E' are
     * interpreted as a long day name, everything else as a short day name.
     */
    private static class DayOfWeekToken extends JQueryPatternLetterToken {
        public DayOfWeekToken(int repetitions) {
            super(repetitions);
        }

        @Override
        public void buildJqueryUiPattern(JQueryDateTimePatternBuilder builder) {
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
    }

    /**
     * Day of the year token 'D', 'DD, 'DDD'.
     * <ul>
     * <li>D and DD is the day of the year without leading zeros</li>
     * <li>DDD is a three-digit day of the year with leading zeros.</li>
     * </ul>
     * More than three 'D' are interpreted as 'DDD'.
     */
    private static class DayOfYearToken extends JQueryPatternLetterToken {
        public DayOfYearToken(int repetitions) {
            super(repetitions);
        }

        @Override
        public void buildJqueryUiPattern(JQueryDateTimePatternBuilder builder) {
            switch (repetitions) {
                case 1:
                    builder.appendDayOfYearNoLeadingZeros();
                    break;
                default: // 2+
                    builder.appendDayOfYearWithLeadingZeros();
                    break;
            }
        }
    }

    /**
     * Full-hour (0-23) token 'H' and 'HH'. 'H' is the hour without leading zeros,
     * more than one 'H' is the hour with leading zeros.
     */
    private static class HourToken extends JQueryPatternLetterToken {
        public HourToken(int repetitions) {
            super(repetitions);
        }

        @Override
        public void buildJqueryUiPattern(JQueryDateTimePatternBuilder builder) {
            if (repetitions == 1) {
                builder.appendHourNoLeadingZeros();
            }
            else {
                builder.appendHourWithLeadingZeros();
            }
        }
    }

    /**
     * Millisecond token 'S'. Always interpreted as optional leading zeros.
     *
     */
    private static class MillisecondToken extends JQueryPatternLetterToken {
        public MillisecondToken(int repetitions) {
            super(repetitions);
        }

        @Override
        public void buildJqueryUiPattern(JQueryDateTimePatternBuilder builder) {
            // actually also accepts no leading zeros
            builder.appendMillisecondsWithLeadingZeros();
        }
    }

    /**
     * Minute token 'm' and 'mm'. 'm' is the minute without leading zeros, more than
     * one 'm' is the minute with leading zeros (two digits).
     */
    private static class MinuteToken extends JQueryPatternLetterToken {
        public MinuteToken(int repetitions) {
            super(repetitions);
        }

        @Override
        public void buildJqueryUiPattern(JQueryDateTimePatternBuilder builder) {
            if (repetitions == 1) {
                builder.appendMinuteNoLeadingZeros();
            }
            else {
                builder.appendMinuteWithLeadingZeros();
            }
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
    private static class MonthToken extends JQueryPatternLetterToken {
        public MonthToken(int repetitions) {
            super(repetitions);
        }

        @Override
        public void buildJqueryUiPattern(JQueryDateTimePatternBuilder builder) {
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
    }

    private static class PassthroughToken implements JqueryPatternToken {
        private CharSequence string;
        private int repetitions;

        public PassthroughToken(CharSequence string, int repetitions) {
            this.string = string;
            this.repetitions = repetitions;
        }

        @Override
        public void buildJqueryUiPattern(JQueryDateTimePatternBuilder builder) {
            for (int i = 0; i < repetitions; ++i) {
                builder.getStringBuilder().append(string);
            }
        }
    }

    /**
     * Second token 's' and 'ss'. 's' is the second without leading zeros, more than
     * one 's' is the second with leading zeros (two digits).
     */
    private static class SecondToken extends JQueryPatternLetterToken {
        public SecondToken(int repetitions) {
            super(repetitions);
        }

        @Override
        public void buildJqueryUiPattern(JQueryDateTimePatternBuilder builder) {
            if (repetitions == 1) {
                builder.appendSecondNoLeadingZeros();
            }
            else {
                builder.appendSecondWithLeadingZeros();
            }
        }
    }

    /**
     * Year token 'Y', 'YY, 'YYY, 'YYYY'. 'YY' is the short year (truncated to two
     * digits), everything else is a full year. Also allows 'u' (to support
     * {@link DateTimeFormatter}) and 'Y'.
     */
    private static class YearToken extends JQueryPatternLetterToken {
        public YearToken(int repetitions) {
            super(repetitions);
        }

        @Override
        public void buildJqueryUiPattern(JQueryDateTimePatternBuilder builder) {
            if (repetitions == 2) {
                builder.appendYearTwoDigits();
            }
            else {
                builder.appendYearFourDigits();
            }
        }
    }

    /**
     * Time zone 'X', 'XX', and 'XXX' token.
     */
    private static class ZoneXToken extends JQueryPatternLetterToken {
        public ZoneXToken(int repetitions) {
            super(repetitions);
        }

        @Override
        public void buildJqueryUiPattern(JQueryDateTimePatternBuilder builder) {
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
    }

    /**
     * Time zone 'z' token.
     */
    private static class ZonezToken extends JQueryPatternLetterToken {
        public ZonezToken(int repetitions) {
            super(repetitions);
        }

        @Override
        public void buildJqueryUiPattern(JQueryDateTimePatternBuilder builder) {
            builder.appendTimezoneByProvidedTimezoneList();
        }
    }

    /**
     * Time zone 'Z' token.
     */
    private static class ZoneZToken extends JQueryPatternLetterToken {
        public ZoneZToken(int repetitions) {
            super(repetitions);
        }

        @Override
        public void buildJqueryUiPattern(JQueryDateTimePatternBuilder builder) {
            builder.appendTimezoneByProvidedTimezoneList();
        }
    }
}
