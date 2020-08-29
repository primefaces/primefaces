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
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.primefaces.convert.PatternReader.LiteralToken;
import org.primefaces.convert.PatternReader.PatternLetterToken;
import org.primefaces.convert.PatternReader.PatternToken;
import org.primefaces.convert.PatternReader.TokenFactory;

/**
 * Utility class for converting between {@link SimpleDateFormat} /
 * {@link DateTimeFormatter} patterns and jquery UI datepicker patterns.
 */
public class DateTimePatternConverter implements PatternConverter {
    private static final Logger LOGGER = Logger.getLogger(DateTimePatternConverter.class.getName());

    /**
     * Converts a java date pattern to a jquery UI datepicker pattern
     *
     * @param pattern Pattern to be converted
     * @return The converted jquery UI pattern.
     */
    public String convert(String pattern) {
        return convert(pattern, CombinedDateTimePatternTokenFactory.getInstance());
    }

    /**
     * Converts a Java date pattern with the given pattern letters to a jquery UI
     * datepicker pattern.
     *
     * @param pattern Pattern to be converted
     * @param letters An interpretation for each pattern letter (a-z, A-Z).
     * @return The converted jquery UI datepicker pattern.
     */
    private String convert(String pattern, TokenFactory<JqueryPatternToken> letters) {
        pattern = pattern != null ? pattern : "";
        final List<JqueryPatternToken> tokens;
        try {
            tokens = PatternReader.parsePattern(pattern, letters);
        }
        catch (final Exception e) {
            LOGGER.log(Level.SEVERE, "Could not parse pattern '" + pattern + "'", e);
            return pattern;
        }
        try {
            final JQueryDateTimePatternBuilder builder = new JQueryDateTimePatternBuilder();
            for (final JqueryPatternToken token : tokens) {
                token.buildJqueryUiPattern(builder);
            }
            return builder.toString();
        }
        catch (final Exception e) {
            LOGGER.log(Level.SEVERE, "Could not build converted pattern for '" + pattern + "'", e);
            return pattern;
        }
    }

    public static class JqueryLiteralToken extends LiteralToken implements JqueryPatternToken {
        public JqueryLiteralToken(String text) {
            super(text);
        }

        @Override
        public void buildJqueryUiPattern(JQueryDateTimePatternBuilder builder) {
            builder.appendLiteralText(text);
        }
    }

    public abstract static class JQueryPatternLetterToken extends PatternLetterToken implements JqueryPatternToken {
        public JQueryPatternLetterToken(int repetitions) {
            super(repetitions);
        }
    }

    public static interface JqueryPatternToken extends PatternToken {
        void buildJqueryUiPattern(JQueryDateTimePatternBuilder builder);
    }
}
