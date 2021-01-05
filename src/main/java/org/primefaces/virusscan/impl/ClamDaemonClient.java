/*
 * The MIT License
 *
 * Copyright (c) 2009-2021 PrimeTek
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.primefaces.virusscan.impl;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import org.apache.commons.io.IOUtils;

/**
 * Simple client for ClamAV's clamd scanner.
 * Provides straightforward instream scanning.
 * Support for basic INSTREAM scanning and PING command.
 * Clamd protocol is explained here:
 * <p>
 * <a href="http://linux.die.net/man/8/clamd">http://linux.die.net/man/8/clamd</a>
 *
 * @see https://github.com/solita/clamav-java
 */
public class ClamDaemonClient {

    // "do not exceed StreamMaxLength as defined in clamd.conf, otherwise clamd
    // will reply with INSTREAM size limit exceeded and close the connection."
    private static final int CHUNK_SIZE = 2048;
    private static final int DEFAULT_TIMEOUT = 30000;

    private final String hostName;
    private final int port;
    private final int timeout;
    private final int chunkSize;

    /**
     * @param hostName The hostname of the server running clamav-daemon
     * @param port The port that clamav-daemon listens to(By default it might not listen to a port. Check your clamav configuration).
     * @param timeout zero means infinite timeout. Not a good idea, but will be accepted.
     */
    public ClamDaemonClient(final String hostName, final int port, final int timeout, final int chunkSize) {
        if (timeout < 0) {
            throw new IllegalArgumentException("Negative timeout value does not make sense.");
        }
        if (chunkSize <= 0) {
            throw new IllegalArgumentException("Chunk size must be greater than zero.");
        }
        this.hostName = hostName;
        this.port = port;
        this.timeout = timeout;
        this.chunkSize = chunkSize;
    }

    public ClamDaemonClient(final String hostName, final int port) {
        this(hostName, port, DEFAULT_TIMEOUT, CHUNK_SIZE);
    }

    /**
     * Run PING command to CLAMD to test it is responding.
     *
     * @return true if the server responded with proper ping reply.
     * @throws IOException if there is an I/O problem communicating with CLAMD
     */
    public boolean ping() throws IOException {
        try (Socket s = getSocket(); OutputStream outs = s.getOutputStream()) {
            s.setSoTimeout(timeout);
            outs.write(asBytes("zPING\0"));
            outs.flush();
            final byte[] buffer = new byte[4];
            IOUtils.read(s.getInputStream(), buffer);
            return Arrays.equals(buffer, asBytes("PONG"));
        }
    }

    /**
     * Streams the given data to the server in chunks. The whole data is not kept in memory. This method is preferred if you don't want to keep the data in
     * memory, for instance by scanning a file on disk. Since the parameter InputStream is not reset, you can not use the stream afterwards, as it will be left
     * in a EOF-state. If your goal is to scan some data, and then pass that data further, consider using {@link #scan(byte[]) scan(byte[] in)}.
     * <p>
     * Opens a socket and reads the reply. Parameter input stream is NOT closed.
     *
     * @param is data to scan. Not closed by this method!
     * @return server reply
     * @throws IOException if there is an I/O problem
     */
    public byte[] scan(final InputStream is) throws IOException {
        try (Socket s = getSocket(); OutputStream outs = new BufferedOutputStream(s.getOutputStream())) {
            s.setSoTimeout(timeout);

            // handshake
            outs.write(asBytes("zINSTREAM\0"));
            outs.flush();
            final byte[] chunk = new byte[chunkSize];

            // send data
            int readLen = is.read(chunk);
            while (readLen >= 0) {
                // The format of the chunk is: '<length><data>' where <length> is
                // the size of the following data in bytes expressed as a 4 byte
                // unsigned
                // integer in network byte order and <data> is the actual chunk.
                // Streaming is terminated by sending a zero-length chunk.
                final byte[] length = ByteBuffer.allocate(4).putInt(readLen).array();
                outs.write(length);
                outs.write(chunk, 0, readLen);
                readLen = is.read(chunk);
            }

            // terminate scan
            outs.write(new byte[] {0, 0, 0, 0});
            outs.flush();

            // read reply
            try (InputStream clamIs = s.getInputStream()) {
                return readAll(clamIs);
            }
        }
    }

    /**
     * Scans bytes for virus by passing the bytes to clamav
     *
     * @param in data to scan.
     * @return server reply
     * @throws IOException if there is an I/O problem
     */
    public byte[] scan(final byte[] in) throws IOException {
        final ByteArrayInputStream bis = new ByteArrayInputStream(in);
        return this.scan(bis);
    }

    /**
     * Get a socket to the current host and port. Partly implemented as a separate method for unit testing purposes.
     *
     * @return socket to host and port
     * @throws IOException if an I/O error occurs when creating the socket
     */
    protected Socket getSocket() throws IOException {
        return new Socket(hostName, port);
    }

    /**
     * Interpret the result from a ClamAV scan, and determine if the result means the data is clean.
     *
     * @param reply The reply from the server after scanning
     * @return true if no virus was found according to the clamd reply message
     */
    public static boolean isCleanReply(final byte[] reply) {
        final String r = new String(reply, StandardCharsets.US_ASCII);
        return r.contains("OK") && !r.contains("FOUND");
    }

    /**
     * byte conversion based on ASCII character set regardless of the current system locale.
     *
     * @param s the string to get as bytes
     * @return a byte[] array in ASCII charset
     */
    private static byte[] asBytes(final String s) {
        return s.getBytes(StandardCharsets.US_ASCII);
    }

    /**
     * Reads all available bytes from the stream
     *
     * @param is the InputStream to read
     * @return the byte[] array output
     * @throws IOException if any error occurs reading stream
     */
    private static byte[] readAll(final InputStream is) throws IOException {
        final ByteArrayOutputStream tmp = new ByteArrayOutputStream();

        final byte[] buf = new byte[2000];
        int read = is.read(buf);
        while (read > 0) {
            tmp.write(buf, 0, read);
            read = is.read(buf);
        }
        return tmp.toByteArray();
    }
}
