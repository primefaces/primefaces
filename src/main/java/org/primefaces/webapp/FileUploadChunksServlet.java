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
package org.primefaces.webapp;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;
import org.json.JSONObject;
import org.primefaces.util.FileUploadUtils;

public class FileUploadChunksServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(FileUploadChunksServlet.class.getName());

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            long uploadedBytes = FileUploadUtils.getFileUploadChunkDecoder(req).decodeUploadedBytes(req);
            printUploadedBytes(resp, uploadedBytes);
        }
        catch (Exception ex) {
            sendError(resp, ex);
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException  {
        try {
            FileUploadUtils.getFileUploadChunkDecoder(req).deleteChunks(req);
        }
        catch (IOException ex) {
            sendError(resp, ex);
        }
    }

    protected void printUploadedBytes(HttpServletResponse resp, long uploadedBytes) throws IOException {
        try (PrintWriter w = resp.getWriter()) {
            resp.setContentType("application/json");
            JSONObject json = new JSONObject();
            json.put("uploadedBytes", uploadedBytes);
            w.print(json.toString());
        }
        catch (IOException | JSONException ex) {
            sendError(resp, ex);
        }
    }

    /**
     * Even though the signatures for methods in a servlet include throws IOException, ServletException, it's a bad
     * idea to let such exceptions be thrown. Failure to catch exceptions in a servlet could leave a system in a
     * vulnerable state, possibly resulting in denial-of-service attacks, or the exposure of sensitive information
     * because when a servlet throws an exception, the servlet container typically sends debugging information back
     * to the user. And that information could be very valuable to an attacker.
     *
     * @param resp the HTTP response
     * @param ex the original exception thrown
     */
    private void sendError(HttpServletResponse resp, Exception ex) {
        try {
            LOGGER.log(Level.SEVERE, ex.getMessage(), ex);
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
        catch (IOException ioex) {
            LOGGER.log(Level.SEVERE, ioex.getMessage(), ioex);
        }
    }
}
