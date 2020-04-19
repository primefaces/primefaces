/**
 * The MIT License
 *
 * Copyright (c) 2009-2019 PrimeTek
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

import org.json.JSONException;
import org.json.JSONObject;
import org.primefaces.util.FileUploadUtils;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FileUploadResumeServlet extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(FileUploadResumeServlet.class.getName());

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
        long uploadedBytes = FileUploadUtils.listChunks(req).stream()
                .mapToLong(this::getSize)
                .sum();

        printUploadedBytes(resp, uploadedBytes);
    }

    protected long getSize(Path p) {
        try {
            return Files.size(p);
        }
        catch (IOException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
        return 0;
    }

    protected void printUploadedBytes(HttpServletResponse response, long uploadedBytes) {
        try (PrintWriter w = response.getWriter()) {
            response.setContentType("application/json");
            JSONObject json = new JSONObject();
            json.put("uploadedBytes", uploadedBytes);
            w.print(json.toString());
        }
        catch (IOException | JSONException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
    }
}
