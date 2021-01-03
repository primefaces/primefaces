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

/**
 * Reads and parses a date time pattern, such as {@code YYYY-mm-dd}. Accepts a visitor object with methods that
 * are called when a token of the pattern is encountered. These callback methods may be used for example to
 * transform the pattern into a different pattern.
 */
public class PatternReader {
    private static final int INVALID_CODEPOINT = -1;

    private OfInt iterator;

    private int peeked = INVALID_CODEPOINT;

    private StringBuilder literals = new StringBuilder();

    private final TokenVisitor visitor;

    /**
     * Creates a new pattern read for processing the given string.
     * @param sequence String with the pattern to process.
     * @param visitor Visitor to call for each encountered token of the pattern.
     */
    private PatternReader(CharSequence sequence, TokenVisitor visitor) {
        this.iterator = sequence.codePoints().iterator();
        this.visitor = visitor;
    }

    private void addLiteral(int literal) {
        if (literal != INVALID_CODEPOINT) {
            literals.appendCodePoint(literal);
        }
    }

    private int consumeChar() {
        final int peek = this.peeked;
        if (peek == INVALID_CODEPOINT) {
            return iterator.hasNext() ? iterator.nextInt() : INVALID_CODEPOINT;
        }
        else {
            this.peeked = INVALID_CODEPOINT;
            return peek;
        }
    }

    private void consumeEscapeOrQuoteLiteral() {
        // discard first quote mark (')
        consumeChar();
        if (peek() == '\'') {
            // literal single quote
            consumeChar();
            addLiteral('\'');
        }
        else {
            consumeQuotedLiteral();
        }
    }

    private void consumePattern() {
        while (!isEoi()) {
            final int ch = peek();
            if (ch == '\'') {
                consumeEscapeOrQuoteLiteral();
            }
            else if ((ch >= 'a' && ch <= 'z') || (ch >= 'A' && ch <= 'Z')) {
                consumePatternLetters();
            }
            else {
                consumeChar();
                addLiteral(ch);
            }
        }
        finishLiterals();
    }

    private void consumePatternLetters() {
        final int letter = consumeChar();
        int repetitions = 1;
        while (peek() == letter) {
            consumeChar();
            repetitions += 1;
        }
        processPatternLetter(letter, repetitions);
    }

    private void consumeQuotedLiteral() {
        while (!isEoi()) {
            final int ch = consumeChar();
            // Literal character in quote
            if (ch != '\'') {
                addLiteral(ch);
            }
            // Literal quote mark ('')
            else if (peek() == '\'') {
                consumeChar();
                addLiteral('\'');
            }
            // End of quoted literal
            else {
                break;
            }
        }
    }

    private void finishLiterals() {
        if (literals.length() > 0) {
            visitor.visitLiteral(literals.toString());
            literals.setLength(0);
        }
    }

    private boolean isEoi() {
        return peeked == INVALID_CODEPOINT && !iterator.hasNext();
    }

    private int peek() {
        if (peeked == INVALID_CODEPOINT && iterator.hasNext()) {
            peeked = iterator.nextInt();
        }
        return peeked;
    }

    private void processPatternLetter(int letter, int repetitions) {
        finishLiterals();
        visitor.visitTokenLetter(letter, repetitions);
    }

    /**
     * Parses the given pattern and calls the visitor for each encountered pattern.
     * @param sequence String with the pattern to process.
     * @param visitor Visitor to call for each encountered token of the pattern.
     */
    public static void parsePattern(CharSequence sequence, TokenVisitor visitor) {
        final PatternReader reader = new PatternReader(sequence, visitor);
        reader.consumePattern();
    }

    /**
     * Visits a date time pattern token when one encountered. All methods receive a custom object.
     */
    public interface TokenVisitor {
        /**
         * Visits a literal token, i.e. plain text without special meaning.
         * @param text Plain text to process.
         */
        void visitLiteral(String text);

        /**
         *
         * @param letter The token letter that was encountered, i.e. {@code Y}.
         * @param repetitions The number of repetitions of the letter. E.g. when a pattern contains {@code YYYY},
         * with method is called once with {@code repetitions} set to {@code 4}.
         */
        void visitTokenLetter(int letter, int repetitions);
    }
}