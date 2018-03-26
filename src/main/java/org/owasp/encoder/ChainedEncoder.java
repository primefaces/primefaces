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
 * ChainedEncoder -- An encoder that chains together two encoders in
 * order.  This is included as an example, but not actually exposed or
 * used as it requires an internal buffer making it not thread-safe.
 * Sequences of 3 or more encodings require chaining together chained
 * encoders.
 *
 * @author Jeff Ichnowski
 */
class ChainedEncoder extends Encoder {
    /** The first encoder to apply in sequence. */
    final Encoder _first;
    /** The second encoder to apply in sequence. */
    final Encoder _last;

    /**
     * The buffer used to store the output of the first encoder before
     * sending as input to the second encoder.
     */
    // TODO: because of this buffer, the ChainedEncoder is the only stateful
    // encoder.  This needs to be removed somehow, or state control APIs need
    // to be added (E.g. reset/flush)
    final CharBuffer _buffer = CharBuffer.allocate(1024);

    /**
     * Creates an ChainedEncoder that applies the encoding sequence
     * {@code input --> first --> last --> output}.
     *
     * @param first the first encoder to apply
     * @param last the second/last encoder to apply.
     */
    ChainedEncoder(Encoder first, Encoder last) {
        _first = first;
        _last = last;
    }

    /**
     * Encodes an input string to an output string.
     *
     * @param str the string to encode
     * @return the encoded string.
     */
    public String encode(String str) {
        if (str == null) {
            str = "null";
        }

        int n = str.length();
        int j = _first.firstEncodedOffset(str, 0, n);
        if (j == n) {
            // string is unchanged after encoding with the first encoder
            return Encode.encode(_last, str);
        }

        final int remaining = n - j;
        final int m = j + _first.maxEncodedLength(n);
        CharBuffer input = CharBuffer.allocate(m);
        str.getChars(0, j, input.array(), 0);
        str.getChars(j, n, input.array(), m - remaining);

        input.limit(m).position(m - remaining);
        CharBuffer tmp = input.duplicate();
        tmp.position(j);

        CoderResult cr = _first.encode(input, tmp, true);
        assert cr.isUnderflow() : "maxEncodedLength was incorrect";

        CharBuffer output = CharBuffer.allocate(_last.maxEncodedLength(tmp.position()));
        tmp.flip();

        cr = _last.encode(tmp, output, true);
        assert cr.isUnderflow() : "maxEncodedLength was incorrect";

        return new String(output.array(), 0, output.position());
    }

    @Override
    protected int firstEncodedOffset(String input, int off, int len) {
        int i = _first.firstEncodedOffset(input, off, len);
        return _last.firstEncodedOffset(input, off, i - off);
    }

    @Override
    protected int maxEncodedLength(int n) {
        return _last.maxEncodedLength(
            _first.maxEncodedLength(n));
    }

//    @Override
//    protected int encode(char[] input, int off, int len, char[] buffer, int j, boolean endOfInput) {
//        char[] tmp = new char[_first.maxEncodedLength(len)];
//        int i = _first.encode(input, off, len, tmp, 0, endOfInput);
//        return _last.encode(tmp, 0, i, buffer, j, endOfInput);
//    }

    @Override
    public CoderResult encode(CharBuffer input, CharBuffer output, boolean endOfInput) {
        for (;;) {
            CoderResult cr1 = _first.encode(input, _buffer, endOfInput);
            _buffer.flip();
            CoderResult cr2 = _last.encode(_buffer, output, endOfInput && cr1.isUnderflow());
            _buffer.compact();

            if (cr2.isOverflow()) {
                // no more room in output, even if we still have buffered input.
                return cr2;
            }

            if (cr1.isUnderflow()) {
                // we've consumed everything from input
                return cr2;
            }
        }
    }

    @Override
    protected CoderResult encodeArrays(CharBuffer input, CharBuffer output, boolean endOfInput) {
        return encode(input, output, endOfInput); // TODO: invert.
    }

    @Override
    public String toString() {
        return "["+_first+","+_last+"]";
    }
}
