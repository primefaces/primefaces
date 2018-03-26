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
 * XMLCommentEncoder -- Encodes for the XML/HTML comment context. The sequence "--" is not allowed in comments, and must be
 * removed/replaced. We also must be careful of trailing hyphens at end of input, as they could combine with the external comment
 * ending sequence "-->" to become "--->", which is also invalid. As with all XML-based context, invalid XML characters are not
 * allowed.
 *
 * @author Jeff Ichnowski
 */
class XMLCommentEncoder extends Encoder {

    /**
     * This is the character used to replace a hyphen when a sequence of hypens is encountered.
     */
    static final char HYPHEN_REPLACEMENT = '~';

    // Input:
    // <!-- foo -- bar -->
    // Possible Options:
    // <!-- foo &ndash; bar -->
    // <!-- foo == bar -->
    // <!-- foo __ bar -->
    // <!-- foo - - bar -->
    // <!-- foo \u2010\u2010 bar --> (Unicode Hyphen)
    // <!-- foo \u2013 bar --> (Unicode en-dash)
    // Note: HTML comments differ, in that they cannot start with: ">", "->".
    // On IE, "<!--[if ..." has special interpretation
    // [2] Char ::= #x9 | #xA | #xD | [#x20-#xD7FF] | [#xE000-#xFFFD] | [#x10000-#x10FFFF]
    @Override
    protected int maxEncodedLength(int n) {
        return n;
    }

    @Override
    protected int firstEncodedOffset(String input, int off, int len) {
        final int n = off + len;
        for (int i = off; i < n; ++i) {
            char ch = input.charAt(i);
            if (ch <= Unicode.MAX_ASCII) {
                if (ch == '-') {
                    if (i + 1 < n) {
                        if (input.charAt(i + 1) == '-') {
                            return i;
//                        } else {
//                            // valid
                        }
                    } else {
                        return i;
                    }
                } else if (ch < ' ' && ch != '\n' && ch != '\r' && ch != '\t') {
                    return i;
//                } else {
//                    // valid
                }
            } else if (ch < Character.MIN_HIGH_SURROGATE) {
                if (ch <= Unicode.MAX_C1_CTRL_CHAR && ch != Unicode.NEL) {
                    return i;
//                } else {
//                    // valid
                }
            } else if (ch <= Character.MAX_HIGH_SURROGATE) {
                if (i + 1 < n && Character.isLowSurrogate(input.charAt(i + 1))) {
                    int cp = Character.toCodePoint(ch, input.charAt(i + 1));
                    if (Unicode.isNonCharacter(cp)) {
                        // noncharacter
                        return i;
                    }
                    ++i;
                } else {
                    return i;
                }
            } else if (ch <= Character.MAX_LOW_SURROGATE || ch > '\ufffd'
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
                if (ch == '-') {
                    if (i + 1 < n) {
                        if (in[i + 1] == '-') {
                            if (j + 1 >= m) {
                                return overflow(input, i, output, j);
                            }
                            out[j++] = '-';
                            out[j++] = HYPHEN_REPLACEMENT;
                            ++i;
                        } else {
                            if (j >= m) {
                                return overflow(input, i, output, j);
                            }
                            out[j++] = '-';
                        }
                    } else if (endOfInput) {
                        if (j >= m) {
                            return overflow(input, i, output, j);
                        }
                        out[j++] = HYPHEN_REPLACEMENT;
                    } else {
                        // saw '-' at the end of the buffer, but this is not
                        // end of input, we need to see the next character
                        // before deciding what to do.
                        break;
                    }
                } else if (ch > ' ' || ch == '\n' || ch == '\r' || ch == '\t') {
                    if (j >= m) {
                        return overflow(input, i, output, j);
                    }
                    out[j++] = ch;
                } else {
                    if (j >= m) {
                        return overflow(input, i, output, j);
                    }
                    out[j++] = XMLEncoder.INVALID_CHARACTER_REPLACEMENT;
                }
            } else if (ch < Character.MIN_HIGH_SURROGATE) {
                if (j >= m) {
                    return overflow(input, i, output, j);
                }
                if (ch > Unicode.MAX_C1_CTRL_CHAR || ch == Unicode.NEL) {
                    out[j++] = ch;
                } else {
                    // C1 control code
                    out[j++] = XMLEncoder.INVALID_CHARACTER_REPLACEMENT;
                }
            } else if (ch <= Character.MAX_HIGH_SURROGATE) {
                if (i + 1 < n) {
                    if (Character.isLowSurrogate(in[i + 1])) {
                        int cp = Character.toCodePoint(ch, in[i + 1]);
                        if (Unicode.isNonCharacter(cp)) {
                            // noncharacter
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
        return "XMLCommentEncoder";
    }
}
