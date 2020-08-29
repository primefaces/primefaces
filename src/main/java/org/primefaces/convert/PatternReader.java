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

import java.util.ArrayList;
import java.util.List;
import java.util.PrimitiveIterator.OfInt;

public class PatternReader<T extends PatternReader.PatternToken> {
    private static final int INVALID_CODEPOINT = -1;

    private final List<T> tokens;

    private OfInt iterator;

    private int peeked = INVALID_CODEPOINT;

    private StringBuilder literals = new StringBuilder();

    private final TokenFactory<T> factory;

    public PatternReader(CharSequence sequence, TokenFactory<T> factory) {
        this.iterator = sequence.codePoints().iterator();
        this.tokens = new ArrayList<>(sequence.length());
        this.factory = factory;
    }

    private void addLiteral(int literal) {
        if (literal != INVALID_CODEPOINT) {
            literals.appendCodePoint(literal);
        }
    }

    private void addPatternLetter(int letter, int repetitions) {
        finishLiterals();
        final T token = factory.getForTokenLetter(letter, repetitions);
        if (token != null) {
            tokens.add(token);
        }
    }

    private int consumeChar() {
        final int peeked = this.peeked;
        if (peeked == INVALID_CODEPOINT) {
            return iterator.hasNext() ? iterator.nextInt() : INVALID_CODEPOINT;
        }
        else {
            this.peeked = INVALID_CODEPOINT;
            return peeked;
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
        addPatternLetter(letter, repetitions);
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
            tokens.add(factory.getForLiteral(literals.toString()));
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

    public static <T extends PatternToken> List<T> parsePattern(CharSequence sequence, TokenFactory<T> factory) {
        final PatternReader<T> reader = new PatternReader<>(sequence, factory);
        reader.consumePattern();
        return reader.tokens;
    }

    public static class LiteralToken implements PatternToken {
        protected final String text;

        public LiteralToken(String text) {
            this.text = text;
        }

        public String getText() {
            return text;
        }
    }

    public abstract static class PatternLetterToken implements PatternToken {
        protected final int repetitions;

        public PatternLetterToken(int repetitions) {
            this.repetitions = repetitions;
        }

        public int getRepetitions() {
            return repetitions;
        }
    }

    public static interface PatternToken {
    }

    public static interface TokenFactory<T extends PatternToken> {
        T getForLiteral(String text);

        T getForTokenLetter(int letter, int repetitions);
    }
}