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

import java.io.IOException;
import java.io.Writer;
import java.nio.CharBuffer;
import java.nio.charset.CoderResult;

/**
 * EncodedWriter -- A writer the encodes all input for a specific context and writes the encoded output to another writer.
 *
 * @author Jeff Ichnowski
 */
public class EncodedWriter extends Writer {

    /**
     * Buffer size to allocate.
     *
     */
    static final int BUFFER_SIZE = 1024;
    /**
     * Buffer to use for handling characters remaining in the input buffer after an encode. The value is set high enough to handle
     * the lookaheads of all the encoders in the package.
     */
    static final int LEFT_OVER_BUFFER = 16;

    /**
     * The wrapped writer.
     */
    private Writer _out;

    /**
     * The encoder used to encode input to the output writer.
     */
    private Encoder _encoder;

    /**
     * Where encoded output is buffered before sending on to the output writer.
     */
    private CharBuffer _buffer = CharBuffer.allocate(BUFFER_SIZE);

    /**
     * Some encoders require more input or an explicit end-of-input flag before they will process the remaining characters of an
     * input buffer. Because the writer API cannot pass this information on to the caller (e.g. by returning how many bytes were
     * actually written), this writer implementation must buffer up the remaining characters between calls. The
     * <code>_hasLeftOver</code> boolean is a flag used to indicate that there are left over characters in the buffer.
     */
    private boolean _hasLeftOver;

    /**
     * See comment on _hasLeftOver. This buffer is created on-demand once. Whether it has anything to flush is determined by the
     * _hasLeftOver flag.
     */
    private CharBuffer _leftOverBuffer;

    /**
     * Creates an EncodedWriter that uses the specified encoder to encode all input before sending it to the wrapped writer.
     *
     * @param out the target for all writes
     * @param encoder the encoder to use
     */
    public EncodedWriter(Writer out, Encoder encoder) {
        super(out);

//      Reduntant null check, super(out) checks for null and throws NPE.
//        if (out == null) {
//            throw new NullPointerException("writer must not be null");
//        }
        if (encoder == null) {
            throw new NullPointerException("encoder must not be null");
        }

        _out = out;

        _encoder = encoder;
    }

    /**
     * Creates an EncodedWriter that uses the specified encoder to encode all input before sending it to the wrapped writer. This
     * method is equivalent to calling:
     * <pre>
     *     new EncodedWriter(out, Encoders.forName(contextName));
     * </pre>
     *
     * @param out the target for all writes
     * @param contextName the encoding context name.
     * @throws UnsupportedContextException if the contextName is unrecognized or not supported.
     */
    public EncodedWriter(Writer out, String contextName) throws UnsupportedContextException {
        this(out, Encoders.forName(contextName));
    }

    @Override
    public void write(char[] cbuf, int off, int len) throws IOException {
        synchronized (lock) {
            CharBuffer input = CharBuffer.wrap(cbuf);
            input.limit(off + len).position(off);

            flushLeftOver(input);

            for (;;) {
                CoderResult cr = _encoder.encode(input, _buffer, false);

                if (cr.isUnderflow()) {
                    if (input.hasRemaining()) {
                        if (_leftOverBuffer == null) {
                            _leftOverBuffer = CharBuffer.allocate(LEFT_OVER_BUFFER);
                        }
                        _leftOverBuffer.put(input);
                        _hasLeftOver = true;
                    }
                    return;
                }
                if (cr.isOverflow()) {
                    flushBufferToWriter();
                }
            }
        }
    }

    /**
     * Flushes the contents of the buffer to the writer and resets the buffer to make room for more input.
     *
     * @throws IOException thrown by the wrapped output.
     */
    private void flushBufferToWriter() throws IOException {
        _out.write(_buffer.array(), 0, _buffer.position());
        _buffer.clear();
    }

    /**
     * Flushes the left-over buffer. Characters from the input buffer are used to add more data to the _leftOverBuffer in order to
     * make the flush happen.
     *
     * @param input the next input to encode, or null if at end of file.
     * @throws IOException from the underlying writer.
     */
    private void flushLeftOver(CharBuffer input) throws IOException {
        if (!_hasLeftOver) {
            return;
        }

        for (;;) {
            if (input != null && input.hasRemaining()) {
                _leftOverBuffer.put(input.get());
            }

            _leftOverBuffer.flip();
            CoderResult cr = _encoder.encode(_leftOverBuffer, _buffer, input == null);

            if (cr.isUnderflow()) {
                if (_leftOverBuffer.hasRemaining()) {
                    _leftOverBuffer.compact();
                } else {
                    break;
                }
            }
            if (cr.isOverflow()) {
                flushBufferToWriter();
            }
        }

        _hasLeftOver = false;
        _leftOverBuffer.clear();
    }

    @Override
    public void flush() throws IOException {
        synchronized (lock) {
            flushBufferToWriter();
            _out.flush();
        }
    }

    @Override
    public void close() throws IOException {
        synchronized (lock) {
            flushLeftOver(null);
            flushBufferToWriter();
            _out.close();
        }
    }
}
