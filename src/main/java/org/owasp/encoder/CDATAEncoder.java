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
 * CDATAEncoder -- encoder for CDATA sections. CDATA sections are generally good
 * for including large blocks of text that contain characters that normally
 * require encoding (ampersand, quotes, less-than, etc...). The CDATA context
 * however still does not allow invalid characters, and can be closed by the
 * sequence "]]>". This encoder removes invalid XML characters, and encodes
 * "]]>" (to "]]]]>&lt;![CDATA[>"). The result is that the data integrity is
 * maintained, but the code receiving the output will have to handle multiple
 * CDATA events. As an alternate approach, the caller could pre-encode "]]>" to
 * something of their choosing (e.g. data.replaceAll("\\]\\]>", "]] >")), then
 * use this encoder to remove any invalid XML characters.
 *
 * @author Jeff Ichnowski
 */
class CDATAEncoder extends Encoder {

    /**
     * The encoding of @{code "]]>"}.
     */
    private static final char[] CDATA_END_ENCODED
            = "]]]]><![CDATA[>".toCharArray();

    /**
     * Length of {@code "]]]]><![CDATA[>"}.
     */
    private static final int CDATA_END_ENCODED_LENGTH = 15;

    /**
     * Length of {@code "]]>"}.
     */
    private static final int CDATA_END_LENGTH = 3;

    @Override
    protected int maxEncodedLength(int n) {
        // "]" becomes "]" (1 -> 1)
        // "]]" becomes "]]" (2 -> 2)
        // "]]>" becomes "]]]]><![CDATA[>" (3 -> 15)
        // "]]>]" becomes "]]]]><![CDATA[>]" (3 -> 15 + 1 -> 1)
        // ...

        int worstCase = n / CDATA_END_LENGTH;
        int remainder = n % CDATA_END_LENGTH;

        return worstCase * CDATA_END_ENCODED_LENGTH + remainder;

//        return (n - remainder) * 5 + remainder;
    }

    @Override
    protected int firstEncodedOffset(String input, int off, int len) {
        final int n = off + len;
        //int closeCount = 0; //unused...
        for (int i = off; i < n; ++i) {
            char ch = input.charAt(i);
            if (ch <= Unicode.MAX_ASCII) {
                if (ch != ']') {
                    if (ch < ' ' && ch != '\n' && ch != '\r' && ch != '\t') {
                        return i;
//                    } else {
//                        // valid
                    }

                } else if (i + 1 < n) {
                    if (input.charAt(i + 1) != ']') {
                        // "]x" (next character is safe for this to be ']')
                    } else {
                        // "]]?"
                        // keep looping through ']'
                        for (; i + 2 < n && input.charAt(i + 2) == ']'; ++i) {
                            // valid
                        }
                        // at this point we've looped through a sequence
                        // of 2 or more "]", if the next character is ">"
                        // we need to encode "]]>".
                        if (i + 2 < n) {
                            if (input.charAt(i + 2) == '>') {
                                return i;
//                                } else {
//                                    // valid
                            }

                        } else {
                            return n;
                        }
                    }
                } else {
                    return n;
                }
            } else if (ch < Character.MIN_HIGH_SURROGATE) {
                if (ch <= Unicode.MAX_C1_CTRL_CHAR && ch != Unicode.NEL) {
                    return i;
//                } else {
//                    // valid
                }
            } else if (ch <= Character.MAX_HIGH_SURROGATE) {
                if (i + 1 < n) {
                    if (Character.isLowSurrogate(input.charAt(i + 1))) {
                        int cp = Character.toCodePoint(ch, input.charAt(i + 1));
                        if (Unicode.isNonCharacter(cp)) {
                            return i;
                        } else {
                            ++i;
                            // valid pair
                        }
                    } else {
                        return i;
                    }
                } else {
                    // end of input, high without low = invalid
                    return i;
                }
            } else if (// low surrogate without preceding high surrogate
                    ch <= Character.MAX_LOW_SURROGATE
                    // or non-characters
                    || ch > '\ufffd'
                    || ('\ufdd0' <= ch && ch <= '\ufdef'))
            {
                return i;
//            } else {
//                // valid
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

        for (; i < n; ++i) {
            char ch = in[i];
            if (ch <= Unicode.MAX_ASCII) {
                if (ch != ']') {
                    if (j >= m) {
                        return overflow(input, i, output, j);
                    }
                    if (ch >= ' ' || ch == '\n' || ch == '\r' || ch == '\t') {
                        out[j++] = ch;
                    } else {
                        out[j++] = XMLEncoder.INVALID_CHARACTER_REPLACEMENT;
                    }
                } else if (i + 1 < n) {
                    if (in[i + 1] != ']') {
                        // "]x" (next character is safe for this to be ']')
                        if (j >= m) {
                            return overflow(input, i, output, j);
                        }
                        out[j++] = ']';
                    } else {
                        // "]]?"
                        // keep looping through ']'
                        for (; i + 2 < n && in[i + 2] == ']'; ++i) {
                            if (j >= m) {
                                return overflow(input, i, output, j);
                            }
                            out[j++] = ']';
                        }
                        // at this point we've looped through a sequence
                        // of 2 or more "]", if the next character is ">"
                        // we need to encode "]]>".
                        if (i + 2 < n) {
                            if (in[i + 2] == '>') {
                                if (j + CDATA_END_ENCODED_LENGTH > m) {
                                    return overflow(input, i, output, j);
                                }
                                System.arraycopy(CDATA_END_ENCODED, 0, out, j, CDATA_END_ENCODED_LENGTH);
                                j += CDATA_END_ENCODED_LENGTH;
                                i += 2;
                            } else {
                                if (j >= m) {
                                    return overflow(input, i, output, j);
                                }
                                out[j++] = ']';
                            }
                        } else if (endOfInput) {
                            if (j + 2 > m) {
                                return overflow(input, i, output, j);
                            }
                            out[j++] = ']';
                            out[j++] = ']';
                            i = n;
                            break;
                        } else {
                            break;
                        }
                    }
                } else if (endOfInput) {
                    // seen "]", then end of input.
                    if (j >= m) {
                        return overflow(input, i, output, j);
                    }
                    out[j++] = ']';
                    i++;
                    break;
                } else {
                    break;
                }
            } else if (ch < Character.MIN_HIGH_SURROGATE) {
                if (ch > Unicode.MAX_C1_CTRL_CHAR || ch == Unicode.NEL) {
                    if (j >= m) {
                        return overflow(input, i, output, j);
                    }
                    out[j++] = ch;
                } else {
                    // C1 control code
                    if (j >= m) {
                        return overflow(input, i, output, j);
                    }
                    out[j++] = XMLEncoder.INVALID_CHARACTER_REPLACEMENT;
                }
            } else if (ch <= Character.MAX_HIGH_SURROGATE) {
                if (i + 1 < n) {
                    if (Character.isLowSurrogate(in[i + 1])) {
                        int cp = Character.toCodePoint(ch, in[i + 1]);
                        if (Unicode.isNonCharacter(cp)) {
                            if (j >= m) {
                                return overflow(input, i, output, j);
                            }
                            out[j++] = XMLEncoder.INVALID_CHARACTER_REPLACEMENT;
                            ++i;
                        } else {
                            if (j + 1 >= m) {
                                return overflow(input, i, output, j);
                            }
                            out[j++] = ch;
                            out[j++] = in[++i];
                        }
                    } else {
                        // high without low
                        if (j >= m) {
                            return overflow(input, i, output, j);
                        }
                        out[j++] = XMLEncoder.INVALID_CHARACTER_REPLACEMENT;
                    }
                } else if (endOfInput) {
                    // end of input, high without low = invalid
                    if (j >= m) {
                        return overflow(input, i, output, j);
                    }
                    out[j++] = XMLEncoder.INVALID_CHARACTER_REPLACEMENT;
                } else {
                    break;
                }
            } else if (// low surrogate without preceding high surrogate
                    ch <= Character.MAX_LOW_SURROGATE
                    // or non-characters
                    || ch > '\ufffd'
                    || ('\ufdd0' <= ch && ch <= '\ufdef'))
            {
                if (j >= m) {
                    return overflow(input, i, output, j);
                }
                out[j++] = XMLEncoder.INVALID_CHARACTER_REPLACEMENT;
            } else {
                if (j >= m) {
                    return overflow(input, i, output, j);
                }
                out[j++] = ch;
            }
        }
        return underflow(input, i, output, j);
    }

    @Override
    public String toString() {
        return "CDATAEncoder";
    }
}
