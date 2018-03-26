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
 * XMLEncoder -- encoder for XML attribute and content data. It uses XML entity
 * entity ("&amp;...;") to encode valid but significant characters. Characters
 * that are invalid according to the XML specification are replaced by a space
 * character (U+0020). This encoder supports several modes of operation,
 * allowing for varying contexts, such as: attribute data between single-quotes,
 * attribute data between double-quotes, attribute data with indeterminate
 * quotes, content, or a context safe for all of the above.
 *
 * @author jeffi
 */
class XMLEncoder extends Encoder {

    // [2] Char ::= #x9 | #xA | #xD | [#x20-#xD7FF] | [#xE000-#xFFFD] | [#x10000-#x10FFFF]
    // Unicode Noncharacters (Unicode Standard 16.7)
    //  U+FFFE &  U+FFFF
    // U+1FFFE & U+1FFFF
    // U+2FFFE & U+2FFFF
    // ...
    // U+10FFFE & U+10FFFF
    // U+FDD0 .. U+FDEF
    // Control Characters
    // U+0000 .. U+001F <-- CR, LF, TAB are in this range and ok.
    // U+007f .. U+009F <-- U+85 = NEL (next line) = CR+LF in one = ok.
    // Note: the standard says it is a good practice to replace noncharacters
    // with U+FFFD "replacement character".
    /**
     * A bit-mask of valid characters with code-points in the range 0--63.
     */
    private static final long BASE_VALID_MASK
            = (1L << '\t') | (1L << '\r') | (1L << '\n');

    /**
     * Maximum number of encoded characters per input character.
     */
    static final int MAX_ENCODED_CHAR_LENGTH = 5;
    /**
     * The encoded length of an ampersand.
     */
    static final int AMP_LENGTH = 5;
    /**
     * The encoded length of a less-than sign.
     */
    static final int LT_LENGTH = 4;
    /**
     * The encoded length of a greater-than sign.
     */
    static final int GT_LENGTH = 4;
    /**
     * The encoded length of an apostrophe.
     */
    static final int APOS_LENGTH = 5;
    /**
     * The encoded length of a double-quotation character.
     */
    static final int QUOT_LENGTH = 5;

    /**
     * An enum of supported "modes" of operation for the XMLEncoder.
     */
    enum Mode {

        /**
         * All significant characters are encoded (&amp; &lt; &gt; ' "). This
         * mode is safe for use in either content or attributes. See note on
         * {@link #CONTENT} for explanation of why '>' is encoded.
         */
        ALL("&<>\'\""),
        /**
         * Characters are encoded for content (a.k.a. "CharData"). This means
         * &amp; &lt; and &gt;. Note: &gt; only requires encoding if it follows
         * "]]". However for maximum compatibility and to avoid the overhead of
         * looking for "]]", we just always encode '>' to '&amp;gt;'.
         */
        CONTENT("&<>"),
        /**
         * Characters are encoded for attribute values--either single or double
         * quoted. This means the characters &amp; &lt ' and " are encoded.
         * Note: &gt; is NOT encoded, and thus this mode is not suitable for
         * content.
         */
        ATTRIBUTE("&<\'\""),
        /**
         * Characters are encoded for single-quoted attribute values. Thus, the
         * same as {@link #ATTRIBUTE} except ' is not encoded.
         */
        SINGLE_QUOTED_ATTRIBUTE("&<\'"),
        /**
         * Characters are encoded for double-quoted attribute values. Thus, the
         * same as {@link #ATTRIBUTE} except " is not encoded.
         */
        DOUBLE_QUOTED_ATTRIBUTE("&<\""),;

        /**
         * The bit-mask of characters that do not need encoding in this mode.
         */
        private final long _validMask;

        /**
         * Sole constructor.
         *
         * @param encodedChars -- a string of characters must be encoded in this
         * mode. This string is converted to a bit-mask.
         */
        Mode(String encodedChars) {
            long encodeMask = 0;
            for (int i = 0, n = encodedChars.length(); i < n; ++i) {
                encodeMask |= 1L << encodedChars.charAt(i);
            }
            _validMask = BASE_VALID_MASK | ((-1L << ' ') & ~(encodeMask));
        }

        /**
         * Accessor for {@link #_validMask}.
         *
         * @return {@link #_validMask}
         */
        long validMask() {
            return _validMask;
        }
    }

    /**
     * Character to use as a replacement for invalid characters (Not to be
     * confused with characters that require encoding). Invalid characters have
     * no encoding, and are not allowed in the context.
     */
    static final char INVALID_CHARACTER_REPLACEMENT = ' ';

    /**
     * The mask of valid characters extracted from the mode for efficiency.
     */
    private final long _validMask;
    /**
     * The mode of operation--only really stored to provide a relevant toString
     * implementation.
     */
    private final Mode _mode;

    /**
     * Default constructor--equivalent to XMLEncoder(Mode.ALL).
     */
    XMLEncoder() {
        this(Mode.ALL);
    }

    /**
     * Creates an XMLEncoder for the specified mode constant.
     *
     * @param mode the mode of the encoder.
     */
    XMLEncoder(Mode mode) {
        _mode = mode;
        _validMask = mode.validMask();
    }

    @Override
    public int maxEncodedLength(int n) {
        // "&amp;" = 5 chars.
        return n * MAX_ENCODED_CHAR_LENGTH;
    }

    @Override
    public int firstEncodedOffset(String input, int off, int len) {
        final int n = off + len;

        for (int i = off; i < n; ++i) {
            char ch = input.charAt(i);
            if (ch < Unicode.DEL) {
                if (ch <= '>' && (_validMask & (1L << ch)) == 0) {
                    // either needs encoding or is invalid
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
            } else if (ch <= Character.MAX_LOW_SURROGATE
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

    /**
     * {@inheritDoc}
     */
    protected CoderResult encodeArrays(CharBuffer input, CharBuffer output, boolean endOfInput) {
        final char[] in = input.array();
        final char[] out = output.array();
        int i = input.arrayOffset() + input.position();
        final int n = input.arrayOffset() + input.limit();
        int j = output.arrayOffset() + output.position();
        final int m = output.arrayOffset() + output.limit();

        for (; i < n; ++i) {
            final char ch = in[i];
            if (ch < Unicode.DEL) {
                if (ch > '>' || ((_validMask & (1L << ch)) != 0)) {
                    // Common case ('>' .. '~') reached in two branches
                    if (j >= m) {
                        return overflow(input, i, output, j);
                    }
                    out[j++] = ch;
                } else {
                    switch (ch) {
                        case '&':
                            if (j + AMP_LENGTH > m) {
                                return overflow(input, i, output, j);
                            }
                            out[j++] = '&';
                            out[j++] = 'a';
                            out[j++] = 'm';
                            out[j++] = 'p';
                            out[j++] = ';';
                            break;
                        case '<':
                            if (j + LT_LENGTH > m) {
                                return overflow(input, i, output, j);
                            }
                            out[j++] = '&';
                            out[j++] = 'l';
                            out[j++] = 't';
                            out[j++] = ';';
                            break;
                        case '>':
                            if (j + GT_LENGTH > m) {
                                return overflow(input, i, output, j);
                            }
                            out[j++] = '&';
                            out[j++] = 'g';
                            out[j++] = 't';
                            out[j++] = ';';
                            break;
                        case '\'':
                            // &apos; is valid in XML, but not in HTML, and numeric code is shorter
                            if (j + APOS_LENGTH > m) {
                                return overflow(input, i, output, j);
                            }
                            out[j++] = '&';
                            out[j++] = '#';
                            out[j++] = '3';
                            out[j++] = '9';
                            out[j++] = ';';
                            break;
                        case '\"':
                            // &quot; is valid in XML and HTML, but numeric code is shorter
                            if (j + QUOT_LENGTH > m) {
                                return overflow(input, i, output, j);
                            }
                            out[j++] = '&';
                            out[j++] = '#';
                            out[j++] = '3';
                            out[j++] = '4';
                            out[j++] = ';';
                            break;
                        default:
                            // invalid character
                            if (j >= m) {
                                return overflow(input, i, output, j);
                            }
                            out[j++] = INVALID_CHARACTER_REPLACEMENT;
                            break;
                    }
                }
            } else if (ch < Character.MIN_HIGH_SURROGATE) {
                if (j >= m) {
                    return overflow(input, i, output, j);
                }
                if (ch > Unicode.MAX_C1_CTRL_CHAR || ch == Unicode.NEL) {
                    out[j++] = ch;
                } else {
                    // C1 control code
                    out[j++] = INVALID_CHARACTER_REPLACEMENT;
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
                            out[j++] = INVALID_CHARACTER_REPLACEMENT;
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
                        out[j++] = INVALID_CHARACTER_REPLACEMENT;
                    }
                } else if (endOfInput) {
                    // end of input, high without low = invalid
                    if (j >= m) {
                        return overflow(input, i, output, j);
                    }
                    out[j++] = INVALID_CHARACTER_REPLACEMENT;
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
                out[j++] = INVALID_CHARACTER_REPLACEMENT;
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
        return "XMLEncoder(" + _mode + ")";
    }
}
