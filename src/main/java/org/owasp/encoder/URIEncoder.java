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
 * URIEncoder -- An encoder for URI based contexts.
 *
 * @author jeffi
 */
class URIEncoder extends Encoder {

    /**
     * Number of characters in the range '0' to '9'.
     */
    static final int CHARS_0_TO_9 = 10;
    /**
     * Number of characters in the range 'a' to 'z'.
     */
    static final int CHARS_A_TO_Z = 26;
    /**
     * Number of bits in a long.
     */
    static final int LONG_BITS = 64;

    /**
     * Maximum number of characters quired to encode a single input character.
     */
    static final int MAX_ENCODED_CHAR_LENGTH = 9;
    /**
     * Number of characters used to '%' encode a single hex-value.
     */
    static final int PERCENT_ENCODED_LENGTH = 3;
    /**
     * Maximum code-point value that can be encoded with 2 utf-8 bytes.
     */
    static final int MAX_UTF8_2_BYTE = 0x7ff;
    /**
     * When the encoded output requires 2 bytes, this is the high bits of the first byte.
     */
    static final int UTF8_2_BYTE_FIRST_MSB = 0xc0;
    /**
     * When the encoded output requires 3 bytes, this is the high bits of the first byte.
     */
    static final int UTF8_3_BYTE_FIRST_MSB = 0xe0;
    /**
     * When the encoded output requires 4 bytes, this is the high bits of the first byte.
     */
    static final int UTF8_4_BYTE_FIRST_MSB = 0xf0;
    /**
     * For all characters in a 2-4 byte encoded sequence after the first this is the high bits of the input bytes.
     */
    static final int UTF8_BYTE_MSB = 0x80;

    /**
     * UTF-8 encodes 6-bits of the code-point in each output UTF-8 byte.
     */
    static final int UTF8_SHIFT = 6;
    /**
     * This is the mask containing 6-ones in the lower 6-bits.
     */
    static final int UTF8_MASK = 0x3f;

    /**
     * The character to use when replacing an invalid character.
     */
    static final char INVALID_REPLACEMENT_CHARACTER = '-';

    /**
     * RFC 3986 -- "The uppercase hexadecimal digits 'A' through 'F' are equivalent to the lowercase digits 'a' through 'f',
     * respectively. If two URIs differ only in the case of hexadecimal digits used in percent- encoded octets, they are
     * equivalent. For consistency, URI producers and normalizers should use uppercase hexadecimal digits for all percent-
     * encodings."
     */
    static final char[] UHEX = "0123456789ABCDEF".toCharArray();

    // ASCII table
    // 0x20:   ! " # $ % & ' ( ) * + , - . / 0 1 2 3 4 5 6 7 8 9 : ; < = > ?
    // 0x40: @ A B C D E F G H I J K L M N O P Q R S T U V W X Y Z [ \ ] ^ _
    // 0x60: ` a b c d e f g h i j k l m n o p q r s t u v w x y z { | } ~
    // ASCII table of RFC 3986 "Unreserved Characters"
    // 0x20:                           - .   0 1 2 3 4 5 6 7 8 9
    // 0x40:   A B C D E F G H I J K L M N O P Q R S T U V W X Y Z         _
    // 0x60:   a b c d e f g h i j k l m n o p q r s t u v w x y z       ~
    // Note: (1L << n) - 1 is bit arithmetic to get n 1 bits.
    // e.g. (1L << 10) = binary 10000000000
    //      binary 10000000000 = 1111111111
    /**
     * RFC 3986 Unreserved Characters. The first 64.
     * <pre>
     *     unreserved  = ALPHA / DIGIT / "-" / "." / "_" / "~"
     * </pre>
     */
    static final long UNRESERVED_MASK_LOW
            = (((1L << CHARS_0_TO_9) - 1) << '0') | (1L << '-') | (1L << '.');

    /**
     * RFC 3986 Unreserved Characters. The second 64.
     * <pre>
     *     unreserved  = ALPHA / DIGIT / "-" / "." / "_" / "~"
     * </pre>
     */
    static final long UNRESERVED_MASK_HIGH
            = (((1L << CHARS_A_TO_Z) - 1) << ('a' - LONG_BITS))
            | (((1L << CHARS_A_TO_Z) - 1) << ('A' - LONG_BITS))
            | (1L << ('_' - LONG_BITS)) | (1L << ('~' - LONG_BITS));

    /**
     * RFC 3986 Reserved Characters. The first 64.
     * <pre>
     *   reserved    = gen-delims / sub-delims
     *
     *   gen-delims  = ":" / "/" / "?" / "#" / "[" / "]" / "@"
     *
     *   sub-delims  = "!" / "$" / "&" / "'" / "(" / ")"
     *               / "*" / "+" / "," / ";" / "="
     * </pre>
     */
    static final long RESERVED_MASK_LOW
            = // gen-delims
            (1L << ':') | (1L << '/') | (1L << '?') | (1L << '#')
            | // sub-delims
            (1L << '!') | (1L << '$') | (1L << '&') | (1L << '\'')
            | (1L << '(') | (1L << ')') | (1L << '*') | (1L << '+')
            | (1L << ',') | (1L << ';') | (1L << '=');

    /**
     * The second 64 RFC 3986 Reserved characters.
     */
    static final long RESERVED_MASK_HIGH
            = // gen-delims
            (1L << ('[' - LONG_BITS)) | (1L << (']' - LONG_BITS)) | (1L << ('@' - LONG_BITS));

    /**
     * Encoding mode of operation for URI encodes. The modes define which characters get encoded using %-encoding, and which do
     * not.
     */
    public enum Mode {

        /**
         * In "component" mode, only the unreserved characters are left unescaped. Everything else is escaped.
         */
        COMPONENT(UNRESERVED_MASK_LOW, UNRESERVED_MASK_HIGH),
        /**
         * In "full" mode, all unreserved and reserved characters are left unescaped. Anything else is escaped.
         */
        FULL_URI(UNRESERVED_MASK_LOW | RESERVED_MASK_LOW,
                UNRESERVED_MASK_HIGH | RESERVED_MASK_HIGH),;

        /**
         * The low bit-mask--copied into the _lowMask of the encoder.
         */
        final long _lowMask;
        /**
         * The high bit-mask--copied into the _highMask of the encoder.
         */
        final long _highMask;

        /**
         * Constructor to create a mode with the specified bit-masks.
         *
         * @param low the low bit-mask (characters 0 to 63)
         * @param high the high bit-mask (characters 64 to 127)
         */
        Mode(long low, long high) {
            _lowMask = low;
            _highMask = high;
        }

        /**
         * Accessor for the low bit-mask.
         *
         * @return _lowMask
         */
        long lowMask() {
            return _lowMask;
        }

        /**
         * Accessor for the high bit-mask.
         *
         * @return _highMask
         */
        long highMask() {
            return _highMask;
        }
    }

    /**
     * The bit-mask of characters that do not need to be escaped, for characters with code-points in the range 0 to 63.
     */
    private final long _lowMask;
    /**
     * The bit-mask of characters that do not need to be escaped, for character with code-points in the range 64 to 127.
     */
    private final long _highMask;
    /**
     * The encoding mode for this encoder--used primarily for toString().
     */
    private final Mode _mode;

    /**
     * Constructor equivalent to @{code URIEncoder(Mode.FULL_URI)}.
     */
    URIEncoder() {
        this(Mode.FULL_URI);
    }

    /**
     * Constructor for the URIEncoder the specifies the encoding mode the URIEncoder will use.
     *
     * @param mode the encoding mode for this encoder.
     */
    URIEncoder(Mode mode) {
        _mode = mode;
        _lowMask = mode.lowMask();
        _highMask = mode.highMask();
    }

    @Override
    protected int maxEncodedLength(int n) {
        // '\uffff' becomes 3 UTF-8 bytes, percent encoded to 9 characters.
        //
        // Note: Surrogate pairs (2 chars) become 4 UTF-8 or 12 characters,
        // or 6 encoded characters per input char.

        return n * MAX_ENCODED_CHAR_LENGTH;
    }

    @Override
    protected int firstEncodedOffset(String input, int off, int len) {
        final int n = off + len;
        for (int i = off; i < n; ++i) {
            char ch = input.charAt(i);
            if (ch <= Unicode.DEL) {
                if (!(ch < LONG_BITS ? (_lowMask & (1L << ch)) != 0 : (_highMask & (1L << (ch - LONG_BITS))) != 0)) {
                    return i;
//                } else {
//                    // valid
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

        for (; i < n; ++i) {
            char ch = in[i];

            if (ch <= Unicode.DEL) {
                if (ch < LONG_BITS ? (_lowMask & (1L << ch)) != 0 : (_highMask & (1L << (ch - LONG_BITS))) != 0) {
                    if (j >= m) {
                        return overflow(input, i, output, j);
                    }
                    out[j++] = ch;
                } else {
                    // one UTF-8 byte
                    if (j + PERCENT_ENCODED_LENGTH > m) {
                        return overflow(input, i, output, j);
                    }
                    out[j++] = '%';
                    out[j++] = UHEX[ch >>> HEX_SHIFT];
                    out[j++] = UHEX[ch & HEX_MASK];
                }
            } else if (ch <= MAX_UTF8_2_BYTE) {
                if (j + 2 * PERCENT_ENCODED_LENGTH > m) {
                    return overflow(input, i, output, j);
                }
                // two UTF-8 bytes
                int b1 = UTF8_2_BYTE_FIRST_MSB | (ch >>> UTF8_SHIFT);
                out[j++] = '%';
                out[j++] = UHEX[b1 >>> HEX_SHIFT];
                out[j++] = UHEX[b1 & HEX_MASK];
                int b2 = UTF8_BYTE_MSB | (ch & UTF8_MASK);
                out[j++] = '%';
                out[j++] = UHEX[b2 >>> HEX_SHIFT];
                out[j++] = UHEX[b2 & HEX_MASK];
            } else if (ch < Character.MIN_HIGH_SURROGATE || ch > Character.MAX_LOW_SURROGATE) {
                if (j + 3 * PERCENT_ENCODED_LENGTH > m) {
                    return overflow(input, i, output, j);
                }
                // three UTF-8 bytes
                int b1 = UTF8_3_BYTE_FIRST_MSB | (ch >>> 2 * UTF8_SHIFT);
                out[j++] = '%';
                out[j++] = UHEX[b1 >>> HEX_SHIFT];
                out[j++] = UHEX[b1 & HEX_MASK];
                int b2 = UTF8_BYTE_MSB | ((ch >>> UTF8_SHIFT) & UTF8_MASK);
                out[j++] = '%';
                out[j++] = UHEX[b2 >>> HEX_SHIFT];
                out[j++] = UHEX[b2 & HEX_MASK];
                int b3 = UTF8_BYTE_MSB | (ch & UTF8_MASK);
                out[j++] = '%';
                out[j++] = UHEX[b3 >>> HEX_SHIFT];
                out[j++] = UHEX[b3 & HEX_MASK];
            } else if (ch <= Character.MAX_HIGH_SURROGATE) {
                // surrogate pair: 2 UTF-16 => 4 UTF-8 bytes
                if (i + 1 < n) {
                    if (Character.isLowSurrogate(in[i + 1])) {
                        if (j + 4 * PERCENT_ENCODED_LENGTH > m) {
                            return overflow(input, i, output, j);
                        }
                        int cp = Character.toCodePoint(ch, in[++i]);
                        int b1 = UTF8_4_BYTE_FIRST_MSB | (cp >>> 3 * UTF8_SHIFT);
                        out[j++] = '%';
                        out[j++] = UHEX[b1 >>> HEX_SHIFT];
                        out[j++] = UHEX[b1 & HEX_MASK];
                        int b2 = UTF8_BYTE_MSB | ((cp >>> 2 * UTF8_SHIFT) & UTF8_MASK);
                        out[j++] = '%';
                        out[j++] = UHEX[b2 >>> HEX_SHIFT];
                        out[j++] = UHEX[b2 & HEX_MASK];
                        int b3 = UTF8_BYTE_MSB | ((cp >>> UTF8_SHIFT) & UTF8_MASK);
                        out[j++] = '%';
                        out[j++] = UHEX[b3 >>> HEX_SHIFT];
                        out[j++] = UHEX[b3 & HEX_MASK];
                        int b4 = UTF8_BYTE_MSB | (cp & UTF8_MASK);
                        out[j++] = '%';
                        out[j++] = UHEX[b4 >>> HEX_SHIFT];
                        out[j++] = UHEX[b4 & HEX_MASK];
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
                    break;
                }
            } else {
                // invalid (low surrogate without preceeding high surrogate)
                if (j >= m) {
                    return overflow(input, i, output, j);
                }
                out[j++] = INVALID_REPLACEMENT_CHARACTER;
            }
        }

        return underflow(input, i, output, j);
    }

    @Override
    public String toString() {
        return "URIEncoder(mode=" + _mode + ")";
    }
}
