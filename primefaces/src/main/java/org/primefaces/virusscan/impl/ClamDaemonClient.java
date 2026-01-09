/*
 * The MIT License
 *
 * Copyright (c) 2009-2026 PrimeTek Informatics
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
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

/**
 * Simple client for ClamAV's clamd scanner.
 * Provides straightforward instream scanning.
 * Support for basic INSTREAM scanning and PING command.
 * Clamd protocol is explained here:
 * <p>
 * <a href="http://linux.die.net/man/8/clamd">http://linux.die.net/man/8/clamd</a>
 *
 * @see <a href="https://github.com/solita/clamav-java">clamav-java</a>
 */
public class ClamDaemonClient {

    // "do not exceed StreamMaxLength as defined in clamd.conf, otherwise clamd
    // will reply with INSTREAM size limit exceeded and close the connection."
    public static final int DEFAULT_BUFFER = 2048;
    public static final String DEFAULT_HOST = "localhost";
    public static final int DEFAULT_PORT = 3310;
    public static final int DEFAULT_TIMEOUT = 30000;

    private final int bufferSize;
    private final String host;
    private final int port;
    private final int timeout;

    /**
     * @param host The hostname of the server running clamav-daemon
     * @param port The port that clamav-daemon listens to(By default it might not listen to a port. Check your clamav configuration).
     * @param timeout zero means infinite timeout. Not a good idea, but will be accepted.
     * @param bufferSize The buffer (chunk size).
     */
    public ClamDaemonClient(final String host, final int port, final int timeout, final int bufferSize) {
        if (timeout < 0) {
            throw new IllegalArgumentException("Negative timeout value does not make sense.");
        }
        if (bufferSize <= 0) {
            throw new IllegalArgumentException("Chunk size must be greater than zero.");
        }
        this.host = host;
        this.port = port;
        this.timeout = timeout;
        this.bufferSize = bufferSize;
    }

    public ClamDaemonClient() {
        this(DEFAULT_HOST, DEFAULT_PORT, DEFAULT_TIMEOUT, DEFAULT_BUFFER);
    }

    public ClamDaemonClient(final String host, final int port) {
        this(host, port, DEFAULT_TIMEOUT, DEFAULT_BUFFER);
    }

    public int getBufferSize() {
        return bufferSize;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public int getTimeout() {
        return timeout;
    }

    /**
     * Run PING command to CLAMD to test it is responding.
     *
     * @return true if the server responded with proper ping reply.
     * @throws IOException if there is an I/O problem communicating with CLAMD
     */
    public boolean ping() throws IOException {
        try (Socket s = getSocket(); OutputStream os = s.getOutputStream()) {
            os.write(asBytes("zPING\0"));
            os.flush();
            try (InputStream is = s.getInputStream()) {
                byte[] buffer = new byte[4];
                int read = is.read(buffer);
                return read > 0 && Arrays.equals(buffer, asBytes("PONG"));
            }
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
            // handshake
            outs.write(asBytes("zINSTREAM\0"));
            outs.flush();
            final byte[] chunk = new byte[this.bufferSize];

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
        Socket socket =  new Socket();
        socket.setSoTimeout(timeout);
        socket.connect(new InetSocketAddress(host, port), timeout);
        return socket;
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
