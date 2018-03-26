// Copyright (c) 2012 Jeff Ichnowski
// All rights reserved.
//
// Redistribution and use in source and binary forms, with or without
// modification, are permitted provided that the following conditions
// are met:
//
//     * Redistributions of source code must retain the above
//       copyright notice, this list of conditions and the following
//       disclaimer.
//
//     * Redistributions in binary form must reproduce the above
//       copyright notice, this list of conditions and the following
//       disclaimer in the documentation and/or other materials
//       provided with the distribution.
//
//     * Neither the name of the OWASP nor the names of its
//       contributors may be used to endorse or promote products
//       derived from this software without specific prior written
//       permission.
//
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
// "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
// LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS
// FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE
// COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
// INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
// (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
// SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
// HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
// STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
// ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
// OF THE POSSIBILITY OF SUCH DAMAGE.

package org.owasp.encoder;

import java.nio.CharBuffer;
import java.nio.charset.CoderResult;

/**
 * CSSEncoder -- Encoder for Cascading-Style-Sheet string and URI contexts.
 * Other contexts, such as color, number (w/unit), etc... are not good targets
 * for "encoding" (e.g. you cannot encode the string "XYZ" into a number),
 * they should instead by validated through other means (such as regular
 * expressions).
 */
class CSSEncoder extends Encoder {

    /** Number of bits in a {@code long}. */
    static final int LONG_BITS = 64;

    /** Length of hex encoding with trailing space {@code "\## "}. */
    static final int HEX_ENCODED_LENGTH = 4;

    /**
     * Encoding mode of operation--specified the set of characters that
     * required encoding.
     */
    enum Mode {
        /**
         * String contexts.  Characters between quotes.
         *
         * <pre>
         *   Not allowed: \n \r \f \\ " '  (everything else is allowed)
         *   Allows: "\\{nl}" (escaped newline)
         * </pre>
         */
        STRING(new ASCIIBits().set(' ', '~').clear("\"\'<&/\\>")),

        /**
         * URL context.  Characters inside a "url(...)".
         *
         * <pre>
         *   Allowed: [!#$%&*-\[\]-~]|{nonascii}|{escape}
         *   Escapes: \\[0-9a-f]{1,6}(\s?)
         *            \\[^\n\r\f0-9a-f]
         * </pre>
         */
        URL(new ASCIIBits().set("!#$%").set('*', '[').set(']', '~').clear("/<>")),

        // In both contexts above '<' is added to protect embedded <style> contexts.

        // Identifier:
        //   First character must be nmstart (no [0-9-])
        //   Subsequent characters are nmchar (w/ [0-9-])
        //   nmchar = [_a-z0-9-]|{nonascii}|{escape}

        // Hash/Name:
        //   all nmchar

        ;

        /** Low bit-mask of unescaped characters.  (Characters 0 to 63) */
        private final long _lowMask;
        /** High bit-mask of unescaped characters.  (Characters 64 to 127) */
        private final long _highMask;

        /**
         * Creates a mode with the specified low and high bit-masks.
         *
         * @param bits the bit-masks of valid characters.
         */
        Mode(ASCIIBits bits) {
            _lowMask = bits._lowerMask;
            _highMask = bits._upperMask;
        }

        /**
         * Accessor for _lowMask.
         * @return _lowMask.
         */
        long lowMask() { return _lowMask; }

        /**
         * Accessor for _highMask.
         * @return _highMask.
         */
        long highMask() { return _highMask; }
    }

    /**
     * Character used when an invalid characters is found.  Invalid
     * characters may not appear with or without encoding.
     */
    static final char INVALID_REPLACEMENT_CHARACTER = '_';

    /** The bit-mask of unescaped characters in the range 0 to 63. */
    final long _lowMask;
    /** The bit-mask of unescaped characters in the range 64 to 127. */
    final long _highMask;
    /** The mode of operation, used primarily for toString. */
    final Mode _mode;

    /**
     * Creates an encoder for the specified mode of operation.
     *
     * @param mode the mode of the encoder.
     */
    CSSEncoder(Mode mode) {
        _mode = mode;
        _lowMask = mode.lowMask();
        _highMask = mode.highMask();
    }

    @Override
    protected int maxEncodedLength(int n) {
        return HEX_ENCODED_LENGTH * n;
    }

    @Override
    protected int firstEncodedOffset(String input, int off, int len) {
        final int n = off + len;
        for (int i = off ; i<n ; ++i) {
            char ch = input.charAt(i);
            if (ch < 2*LONG_BITS) {
                if ((ch < LONG_BITS ? _lowMask & (1L << ch) : _highMask & (1L << (ch - LONG_BITS))) != 0) {
                    continue;
                }
            } else if (ch > '\237' && ch < Unicode.LINE_SEPARATOR || ch > Unicode.PARAGRAPH_SEPARATOR) {
                // "nonascii"
                if (ch < Character.MIN_HIGH_SURROGATE || ch > Character.MAX_LOW_SURROGATE) {
                    // valid
                } else if (ch <= Character.MAX_HIGH_SURROGATE) {
                    if (i+1 < n) {
                        if (Character.isLowSurrogate(input.charAt(i+1))) {
                            // valid
                            ++i;
                        } else {
                            return i;
                        }
                    } else {
                        return i;
                    }
                } else {
                    // invalid
                    return i;
                }
                continue;
            }
            return i;
        }
        return n;
    }

    @Override
    protected CoderResult encodeArrays(CharBuffer input, CharBuffer output, boolean endOfInput) {
        final char[] in = input.array();
        final char[] out = output.array();
        int i = input.arrayOffset() + input.position();
        final int n = input.arrayOffset() + input.limit();
        int j = output.arrayOffset() + output.position();
        final int m = output.arrayOffset() + output.limit();

        for ( ; i<n ; ++i) {
            char ch = in[i];
            if (ch < 2*LONG_BITS) {
                if ((ch < LONG_BITS ? _lowMask & (1L << ch) : _highMask & (1L << (ch - LONG_BITS))) != 0) {
                    if (j >= m) {
                        return overflow(input, i, output, j);
                    }
                    out[j++] = ch;
                    continue;
                }
            } else if (ch > '\237' && ch < Unicode.LINE_SEPARATOR || ch > Unicode.PARAGRAPH_SEPARATOR) {
                // "nonascii"
                if (ch < Character.MIN_HIGH_SURROGATE || ch > Character.MAX_LOW_SURROGATE) {
                    if (j >= m) {
                        return overflow(input, i, output, j);
                    }
                    out[j++] = ch;
                } else if (ch <= Character.MAX_HIGH_SURROGATE) {
                    if (i+1 < n) {
                        if (Character.isLowSurrogate(in[i+1])) {
                            if (j + 1 >= m) {
                                return overflow(input, i, output, j);
                            }
                            out[j++] = ch;
                            out[j++] = in[++i];
                        } else {
                            if (j >= m) {
                                return overflow(input, i, output, j);
                            }
                            out[j++] = INVALID_REPLACEMENT_CHARACTER;
                        }
                    } else if (endOfInput) {
                        if (j >= m) {
                            return overflow(input, i, output, j);
                        }
                        out[j++] = INVALID_REPLACEMENT_CHARACTER;
                    } else {
                        break; // UNDERFLOW
                    }
                } else {
                    // invalid
                    if (j >= m) {
                        return overflow(input, i, output, j);
                    }
                    out[j++] = INVALID_REPLACEMENT_CHARACTER;
                }
                continue;
            }

            // if here, we need to hex escape the character

            // calculate k, the value of j after encoding the character
            int k = j + 1; // +1 for '\\'
            int tmp = ch;
            do {
                tmp >>>= HEX_SHIFT;
                k++;
            } while (tmp != 0);

            // need to look ahead one character to see if we need to
            // introduce a whitespace after the escape to prevent
            // the escape from merging with the following character
            boolean needsSpace = false;
            if (i+1 < n) {
                char la = in[i + 1];
                if ('0' <= la && la <= '9'
                        || 'a' <= la && la <= 'f'
                        || 'A' <= la && la <= 'F'
                        || la == ' '
                        || la == '\n'
                        || la == '\r'
                        || la == '\t'
                        || la == '\f')
                {
                    needsSpace = true;
                    k++;
                }
            } else if (!endOfInput) {
                // CoderResult.UNDERFLOW;
                break;
            }

            // check for overflow before writing anything
            if (k > m) {
                return overflow(input, i, output, j);
            }

            out[j] = '\\';
            j = k;
            // write the escape backwards from the end.
            if (needsSpace) {
                out[--k] = ' ';
            }
            tmp = ch;
            do {
                out[--k] = HEX[tmp & HEX_MASK];
                tmp >>>= HEX_SHIFT;
            } while (tmp != 0);

            assert out[k-1] == '\\';
        }

        return underflow(input, i, output, j);
    }

    @Override
    public String toString() {
        return "CSSEncoder(mode="+_mode+")";
    }
}
