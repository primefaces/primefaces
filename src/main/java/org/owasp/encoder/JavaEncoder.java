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
 * JavaEncoder -- Encoder for Java based strings. Useful if in Java code
 * generators to generate efficiently encoded strings for arbitrary data. This
 * encoder uses the minimal sequence of characters required to encode a
 * character (e.g. standard backslash escapes, such as "\n", "\\" , "\'", octal
 * escapes, and unicode escapes). This encoder does NOT check UTF-16 surrogate
 * pair sequences. The target output context supports mismatched UTF-16 pairs
 * (e.g. it will compile, run, etc... with them).
 *
 * @author Jeff Ichnowski
 */
class JavaEncoder extends Encoder {

    /**
     * The length of a Unicode escape, e.g. "\\u1234".
     */
    static final int U_ESCAPE_LENGTH = 6;
    /**
     * The length of a octal escape sequence, e.g. "\377".
     */
    static final int OCT_ESCAPE_LENGTH = 4;
    /**
     * Number of bits to shift for each octal unit.
     */
    static final int OCT_SHIFT = 3;
    /**
     * The bit-mask for an octal unit.
     */
    static final int OCT_MASK = 7;

    @Override
    protected int maxEncodedLength(int n) {
        // "\\u####"
        return n * U_ESCAPE_LENGTH;
    }

    @Override
    protected int firstEncodedOffset(String input, int off, int len) {
        final int n = off + len;
        for (int i = off; i < n; ++i) {
            char ch = input.charAt(i);
            if (ch >= ' ' && ch <= '~') {
                if (ch == '\\' || ch == '\'' || ch == '\"') {
                    return i;
                }
            } else {
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

        charLoop:
        for (; i < n; ++i) {
            final char ch = in[i];
            if (ch >= ' ' && ch <= '~') {
                if (ch == '\\' || ch == '\'' || ch == '\"') {
                    if (j + 1 >= m) {
                        return overflow(input, i, output, j);
                    }
                    out[j++] = '\\';
                    out[j++] = ch;
                } else {
                    if (j >= m) {
                        return overflow(input, i, output, j);
                    }
                    out[j++] = ch;
                }
            } else {
                switch (ch) {
                    case '\b':
                        if (j + 1 >= m) {
                            return overflow(input, i, output, j);
                        }
                        out[j++] = '\\';
                        out[j++] = 'b';
                        break;
                    case '\t':
                        if (j + 1 >= m) {
                            return overflow(input, i, output, j);
                        }
                        out[j++] = '\\';
                        out[j++] = 't';
                        break;
                    case '\n':
                        if (j + 1 >= m) {
                            return overflow(input, i, output, j);
                        }
                        out[j++] = '\\';
                        out[j++] = 'n';
                        break;
                    case '\f':
                        if (j + 1 >= m) {
                            return overflow(input, i, output, j);
                        }
                        out[j++] = '\\';
                        out[j++] = 'f';
                        break;
                    case '\r':
                        if (j + 1 >= m) {
                            return overflow(input, i, output, j);
                        }
                        out[j++] = '\\';
                        out[j++] = 'r';
                        break;
                    default:
                        if (ch <= '\377') {
                            longEscapeNeeded:
                            {
                                if (ch <= '\37') {
                                    // "short" octal escapes: '\0' to '\37'
                                    // cannot be followed by '0' to '7' thus
                                    // require a lookahead to use.
                                    if (i + 1 < n) {
                                        char la = in[i + 1];
                                        if ('0' <= la && la <= '7') {
                                            break longEscapeNeeded;
                                        }
                                    } else if (!endOfInput) {
                                        // need more characters to see if we can use
                                        // a short octal escape.
                                        break charLoop;
                                    }

                                    if (ch <= '\7') {
                                        if (j + 1 >= m) {
                                            return overflow(input, i, output, j);
                                        }
                                        out[j++] = '\\';
                                        out[j++] = (char) (ch + '0');
                                    } else {
                                        if (j + 2 >= m) {
                                            return overflow(input, i, output, j);
                                        }
                                        out[j++] = '\\';
                                        out[j++] = (char) ((ch >>> OCT_SHIFT) + '0');
                                        out[j++] = (char) ((ch & OCT_MASK) + '0');
                                    }

                                    continue;
                                }
                            }

                            if (j + OCT_ESCAPE_LENGTH > m) {
                                return overflow(input, i, output, j);
                            }
                            out[j++] = '\\';
                            out[j++] = (char) ((ch >>> 2 * OCT_SHIFT) + '0');
                            out[j++] = (char) (((ch >>> OCT_SHIFT) & OCT_MASK) + '0');
                            out[j++] = (char) ((ch & OCT_MASK) + '0');
                        } else {
                            if (j + U_ESCAPE_LENGTH > m) {
                                return overflow(input, i, output, j);
                            }
                            out[j++] = '\\';
                            out[j++] = 'u';
                            out[j++] = HEX[ch >>> 3 * HEX_SHIFT];
                            out[j++] = HEX[(ch >>> 2 * HEX_SHIFT) & HEX_MASK];
                            out[j++] = HEX[(ch >>> HEX_SHIFT) & HEX_MASK];
                            out[j++] = HEX[ch & HEX_MASK];
                        }
                }
            }
        }

        return underflow(input, i, output, j);
    }
}
