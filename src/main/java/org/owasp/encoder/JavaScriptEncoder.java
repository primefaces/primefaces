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
 * JavaScriptEncoder -- An encoder for JavaScript string contexts.
 *
 * @author jeffi
 */
class JavaScriptEncoder extends Encoder {

    /**
     * Mode of operation constants for the JavaScriptEncoder.
     */
    enum Mode {
        /**
         * Standard encoding of JavaScript Strings. Escape sequences are chosen
         * according to what is the shortest sequence possible for the
         * character.
         */
        SOURCE,
        /**
         * Encoding for use in HTML attributes. Quote characters are escaped
         * using hex encodes instead of backslashes. The alternate would be to
         * use a sequence of encodes that would actually be longer. In this mode
         * double-quote is "\x22" and single-quote is "\x27". (In HTML
         * attributes the alternate would be encoding "\"" and "\'" with entity
         * escapes to "\&amp;#34;" and "\&amp;39;").
         */
        ATTRIBUTE,
        /**
         * Encoding for use in HTML script blocks. The main concern here is
         * permaturely terminating a script block with a closing "&lt;/" inside
         * the string. This encoding escapes "/" as "\/" to prevent such
         * termination.
         */
        BLOCK,
        /**
         * Encodes for use in either HTML script attributes or blocks.
         * Essentially this is both special escapes from HTML_ATTRIBUTE and
         * HTML_CONTENT combined.
         */
        HTML,;
    }

    /**
     * The mode of operations--used for toString implementation.
     */
    private final Mode _mode;
    /**
     * True if quotation characters should be hex encoded. Hex encoding quotes
     * allows JavaScript to be included in XML attributes without additional
     * XML-based encoding.
     */
    private final boolean _hexEncodeQuotes;
    /**
     * An array of 4 32-bit integers used as bitmasks to check if a character
     * needs encoding or not. If the bit is set, the character is valid and does
     * not need encoding.
     */
    private final int[] _validMasks;
    /**
     * True if the output should only include ASCII characters. Valid non-ASCII
     * characters that would normally not be encoded, will be encoded.
     */
    private final boolean _asciiOnly;

    /**
     * Constructs a new JavaScriptEncoder for the specified contextual mode.
     *
     * @param mode the mode of operation
     * @param asciiOnly true if only ASCII characters should be included in the
     * output (all code-points outside the ASCII range will be encoded).
     */
    JavaScriptEncoder(Mode mode, boolean asciiOnly) {
        // TODO: after some testing it appears that an array of int masks
        // is faster than two longs, or an array of longs or an array of bytes
        // the other encoders based upon masks should be switched to ints.
        // (to be clear, it's much faster on 32-bit VMS, and just slightly
        // faster on 64-bit VMS)
        _mode = mode;

        // Note: this probably needs to be repeated everywhere this trick is
        // used, but here seems like as good a place as any.  According to
        // the Java spec (x << y) where x and y are integers, is evaluated
        // as (x << (y & 31)).  Or put another way, only the lower 5 bits
        // of the shift amount are considered.
        _validMasks = new int[]{
            0,
            -1 & ~((1 << '\'') | (1 << '\"')),
            -1 & ~((1 << '\\')),
            asciiOnly ? ~(1 << Unicode.DEL) : -1,};

        if (mode == Mode.BLOCK || mode == Mode.HTML) {
            // in <script> blocks, we need to prevent the browser from seeing
            // "</anything>" and "<!--". To do so we escape "/" as "\/" and
            // escape "-" as "\-".  Both could be solved with a hex encoding
            // on "<" but we figure "<" appears often in script strings and
            // the backslash encoding is more readable than a hex encoding.
            // (And note, a backslash encoding would not prevent the exploits
            // on "</...>" and "<!--".
            // In short "</script>" is escaped as "<\/script>" and "<!--" is
            // escaped as "<!\-\-".
            _validMasks[1] &= ~((1 << '/') | (1 << '-'));
        }
        if (mode != Mode.SOURCE) {
            _validMasks[1] &= ~(1 << '&');
        }

        _asciiOnly = asciiOnly;
        _hexEncodeQuotes = (mode == Mode.ATTRIBUTE || mode == Mode.HTML);
    }

    @Override
    int maxEncodedLength(int n) {
        return n * 6;
    }

    @Override
    int firstEncodedOffset(String input, int off, int len) {
        final int n = off + len;
        final int[] validMasks = this._validMasks;
        for (int i = off; i < n; ++i) {
            char ch = input.charAt(i);
            if (ch < 128) {
                if ((validMasks[ch >>> 5] & (1 << ch)) == 0) {
                    return i;
                }
            } else if (_asciiOnly || ch == Unicode.LINE_SEPARATOR || ch == Unicode.PARAGRAPH_SEPARATOR) {
                return i;
            }
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

        final int[] validMasks = this._validMasks;

        for (; i < n; ++i) {
            char ch = in[i];

            hexEncoded:
            {
                encoded:
                {
                    if (ch < 128) {
                        if ((validMasks[ch >>> 5] & (1 << ch)) == 0) {
                            break encoded;
                        }
                    } else if (_asciiOnly || ch == Unicode.LINE_SEPARATOR || ch == Unicode.PARAGRAPH_SEPARATOR) {
                        if (ch <= 0xff) {
                            break hexEncoded;
                        }
                        if (j + 6 > m) {
                            return overflow(input, i, output, j);
                        }
                        out[j++] = '\\';
                        out[j++] = 'u';
                        out[j++] = HEX[ch >>> 3 * HEX_SHIFT];
                        out[j++] = HEX[ch >>> 2 * HEX_SHIFT & HEX_MASK];
                        out[j++] = HEX[ch >>> HEX_SHIFT & HEX_MASK];
                        out[j++] = HEX[ch & HEX_MASK];
                        continue;
                    }
                    if (j >= m) {
                        return overflow(input, i, output, j);
                    }
                    out[j++] = ch;
                    continue;
                }

                switch (ch) {
                    case '\b':
                        if (j + 2 > m) {
                            return overflow(input, i, output, j);
                        }
                        out[j++] = '\\';
                        out[j++] = 'b';
                        continue;
                    case '\t':
                        if (j + 2 > m) {
                            return overflow(input, i, output, j);
                        }
                        out[j++] = '\\';
                        out[j++] = 't';
                        continue;
                    case '\n':
                        if (j + 2 > m) {
                            return overflow(input, i, output, j);
                        }
                        out[j++] = '\\';
                        out[j++] = 'n';
                        continue;
                    // Per Mike Samuel "\v should not be used since some
                    // versions of IE treat it as a literal letter 'v'"
//                case 0x0b: // '\v'
//                    if (j+1 >= m) {
//                        return overflow(input, i, output, j);
//                    }
//                    out[j++] = '\\';
//                    out[j++] = 'v';
//                    break;
                    case '\f':
                        if (j + 2 > m) {
                            return overflow(input, i, output, j);
                        }
                        out[j++] = '\\';
                        out[j++] = 'f';
                        continue;
                    case '\r':
                        if (j + 2 > m) {
                            return overflow(input, i, output, j);
                        }
                        out[j++] = '\\';
                        out[j++] = 'r';
                        continue;
                    case '\'':
                    case '\"':
                        if (_hexEncodeQuotes) {
                            break hexEncoded;
                        }
                    // fall through
                    case '\\':
                    case '/':
                    case '-':
                        // We'll only see '/' and '-' here in the BLOCK and HTML
                        // modes otherwise it will be accepted as valid by the
                        // bitmasks.
                        if (j + 2 > m) {
                            return overflow(input, i, output, j);
                        }
                        out[j++] = '\\';
                        out[j++] = ch;
                        continue;
                    default:
                        break;
                }
            }

            if (j + 4 > m) {
                return overflow(input, i, output, j);
            }
            out[j++] = '\\';
            out[j++] = 'x';
            out[j++] = HEX[ch >>> HEX_SHIFT];
            out[j++] = HEX[ch & HEX_MASK];
        }

        return underflow(input, i, output, j);
    }

    @Override
    public String toString() {
        return "JavaScriptEncoder(mode=" + _mode + "," + (_asciiOnly ? "ASCII" : "UNICODE") + ")";
    }
}
