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
 * <p>This is the low-level encoding API.  For each flavor of encoding
 * there is an instance of this class that performs the actual
 * encoding.  Overriding and implementing Encoders outside of the
 * OWASP Encoder's project is not currently supported.</p>
 *
 * <p>Unless otherwise documented, instances of these classes are
 * thread-safe.  Encoders implementations do not generally carry
 * state, and if they do the state will be flush with a call to {@link
 * #encode(CharBuffer, CharBuffer, boolean)} with
 * {@code endOfInput} set to {@code true}.</p>
 *
 * <p>To use an Encoder instance directly, repeatedly call {@link
 * #encode(CharBuffer, CharBuffer, boolean)} with
 * the {@code endOfInput} parameter set to {@code false} while there
 * is (the possibility of) more input to encode.  Once there is no
 * more input to encode, call {@link #encode(CharBuffer,
 * CharBuffer, boolean)} with {@code endOfInput} set to
 * {@code true} until the method returns {@link
 * CoderResult#UNDERFLOW}.</p>
 *
 * <p>In general, this class is not expected to be needed directly.
 * Use the {@link Encode} fluent interface for encoding Strings or
 * {@link EncodedWriter} for large blocks of contextual encoding.</p>
 *
 * @author Jeff Ichnowski
 * @see Encode
 * @see EncodedWriter
 */
public abstract class Encoder {
    /**
     * Hexadecimal conversion array.  Package private to prevent corruption.
     */
    static final char[] HEX = "0123456789abcdef".toCharArray();

    /**
     * Bit-shift used for encoding values in hexadecimal.
     */
    static final int HEX_SHIFT = 4;

    /**
     * Bit-mask used for encoding values in hexadecimal.
     */
    static final int HEX_MASK = 0xf;

    /**
     * Package-private constructor to prevent having to support
     * external implementations of this class.  This may be opened up
     * in future releases.
     */
    Encoder() {}

    /**
     * <p>This is the kernel of encoding.  Currently only CharBuffers
     * backed by arrays (i.e. {@link CharBuffer#hasArray()}
     * returns {@code true}) are supported.  <strong>Using a
     * direct-mapped CharBuffer will result in an
     * UnsupportedOperationException</strong>, though this behavior
     * may change in future releases.</p>
     *
     * <p>This method should be called repeatedly while {@code
     * endOfInput} set to {@code false} while there is more input.
     * Once there is no more input, this method should be called
     * {@code endOfInput} set to {@code false} until {@link
     * CoderResult#UNDERFLOW} is returned.</p>
     *
     * <p>After any call to this method, except when {@code
     * endOfInput} is {@code true} and the method returns {@code
     * UNDERFLOW}, there may be characters left to encode in the
     * {@code input} buffer (i.e. {@code input.hasRemaining() ==
     * true}).  This will happen when the encoder needs to see more
     * input before determining what to do--for example when encoding
     * for CDATA, if the input ends with {@code "foo]]"}, the encoder
     * will need to see the next character to determine if it is a "&gt;"
     * or not.</p>
     *
     * <p>Example usage:</p>
     * <pre>
     *   CharBuffer input = CharBuffer.allocate(1024);
     *   CharBuffer output = CharBuffer.allocate(1024);
     *   CoderResult cr;
     *   // assuming doRead fills in the input buffer or
     *   // returns -1 at end of input
     *   while(doRead(input) != -1) {
     *     input.flip();
     *     for (;;) {
     *       cr = encoder.encode(input, output, false);
     *       if (cr.isUnderflow()) {
     *         break;
     *       }
     *       if (cr.isOverflow()) {
     *         // assuming doWrite flushes the encoded
     *         // characters somewhere.
     *         output.flip();
     *         doWrite(output);
     *         output.compact();
     *       }
     *     }
     *     input.compact();
     *   }
     *
     *   // at end of input
     *   input.flip();
     *   do {
     *     cr = encoder.encode(input, output, true);
     *     output.flip();
     *     doWrite(output);
     *     output.compact();
     *   } while (cr.isOverflow());
     * </pre>
     *
     * @param input the input buffer to encode
     * @param output the output buffer to receive the encoded results
     * @param endOfInput set to {@code true} if there is no more input, and any
     * remaining characters at the end of input will either be encoded or
     * replaced as invalid.
     * @return Either {@link CoderResult#UNDERFLOW}
     * or {@link CoderResult#OVERFLOW}.  No other
     * CoderResult value will be returned.  Characters or sequences
     * that might conceivably return and invalid or unmappable
     * character result (as part of the nio Charset API) are
     * automatically replaced to avoid security implications.
     */
    public CoderResult encode(CharBuffer input, CharBuffer output, boolean endOfInput) {
        if (input.hasRemaining()) {
            if (input.hasArray() && output.hasArray()) {
                return encodeArrays(input, output, endOfInput);
            } else {
                return encodeBuffers(input, output, endOfInput);
            }
        } else {
            return CoderResult.UNDERFLOW;
        }
    }

    /**
     * The core encoding loop used when both the input and output buffers
     * are array backed.  The loop is expected to fetch the arrays and
     * interact with the arrays directly for performance.
     *
     * @param input the input buffer.
     * @param output the output buffer.
     * @param endOfInput when true, this is the last input to encode
     * @return UNDERFLOW or OVERFLOW
     */
    CoderResult encodeArrays(CharBuffer input, CharBuffer output, boolean endOfInput) {
        throw new UnsupportedOperationException();
    }

    /**
     * The core encoding loop used when either or both input and output
     * buffers are NOT array-backed.  E.g. they are direct buffers or
     * perhaps the input buffer is a read-only wrapper.  In any case,
     * this method is not currently implemented by any of the encoder
     * implementations since it is not expected to be common use-case.
     * The stub is included here for completeness and to demarcate
     * where the non-array-backed use-case would be included.
     *
     * @param input the input buffer.
     * @param output the output buffer.
     * @param endOfInput when true, this is the last input to encode
     * @return never returns.
     * @throws UnsupportedOperationException -- always
     */
    CoderResult encodeBuffers(CharBuffer input, CharBuffer output, boolean endOfInput)
        throws UnsupportedOperationException
    {
        throw new UnsupportedOperationException();
    }

    /**
     * Returns the maximum encoded length (in chars) of an input sequence of
     * {@code n} characters.
     *
     * @param n the number of characters of input
     * @return the worst-case number of characters required to encode
     */
    abstract int maxEncodedLength(int n);

    /**
     * Scans the input string for the first character index that requires
     * encoding.  If the entire input does not require encoding then the
     * length is returned.  This method is used by the Encode.forXYZ methods
     * to return input strings unchanged when possible.
     *
     * @param input the input to check for encoding
     * @param off the offset of the first character to check
     * @param len the number of characters to check
     * @return the index of the first character to encode.  The return value
     * will be {@code off+len} if no characters in the input require encoding.
     */
    abstract int firstEncodedOffset(String input, int off, int len);

    /**
     * Internal helper method to properly position buffers after encoding up
     * until an overflow.
     *
     * @param input the input buffer
     * @param i the array offset in the input buffer (translated to position)
     * @param output the output buffer
     * @param j the array offset in the output buffer (translated to position)
     * @return CoderResult.OVERFLOW
     */
    static CoderResult overflow(CharBuffer input, int i, CharBuffer output, int j) {
        input.position(i - input.arrayOffset());
        output.position(j - output.arrayOffset());
        return CoderResult.OVERFLOW;
    }

    /**
     * Internal helper method to properly position buffers after encoding up
     * until an underflow.
     *
     * @param input the input buffer
     * @param i the array offset in the input buffer (translated to position)
     * @param output the output buffer
     * @param j the array offset in the output buffer (translated to position)
     * @return CoderResult.UNDERFLOW
     */
    static CoderResult underflow(CharBuffer input, int i, CharBuffer output, int j) {
        input.position(i - input.arrayOffset());
        output.position(j - output.arrayOffset());
        return CoderResult.UNDERFLOW;
    }
}
