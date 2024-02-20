/*
 * The MIT License
 *
 * Copyright (c) 2009-2024 PrimeTek Informatics
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
package org.primefaces.webapp.filter;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.fileupload.FileItemFactory;

import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.FileCleanerCleanup;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FileCleaningTracker;
import org.primefaces.util.Constants;
import org.primefaces.webapp.MultipartRequest;

@Deprecated(forRemoval = true, since = "14.0.0")
public class FileUploadFilter implements Filter {

    private static final Logger LOGGER = Logger.getLogger(FileUploadFilter.class.getName());

    private static final String THRESHOLD_SIZE_PARAM = "thresholdSize";
    private static final String FILE_COUNT_MAX_PARAM = "fileCountMax";
    private static final String UPLOAD_DIRECTORY_PARAM = "uploadDirectory";

    private String thresholdSize;
    private String fileCountMax;
    private String uploadDir;

    private boolean bypass;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        String uploader = filterConfig.getServletContext().getInitParameter(Constants.ContextParams.UPLOADER);
        bypass = !"commons".equals(uploader);

        thresholdSize = filterConfig.getInitParameter(THRESHOLD_SIZE_PARAM);
        fileCountMax = filterConfig.getInitParameter(FILE_COUNT_MAX_PARAM);
        uploadDir = filterConfig.getInitParameter(UPLOAD_DIRECTORY_PARAM);

        if (LOGGER.isLoggable(Level.FINE)) {
            LOGGER.fine("FileUploadFilter initiated successfully");
        }
        if (LOGGER.isLoggable(Level.WARNING)) {
            LOGGER.warning("FileUploadFilter is deprecated. Please try native Servlet 3.0 uploading and report any issues if it does not work.");
        }
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        if (bypass) {
            filterChain.doFilter(request, response);
            return;
        }

        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        boolean isMultipart = ServletFileUpload.isMultipartContent(httpServletRequest);

        if (isMultipart) {
            if (LOGGER.isLoggable(Level.FINE)) {
                LOGGER.fine("Parsing file upload request");
            }

            ServletFileUpload servletFileUpload = new ServletFileUpload(createFileItemFactory(httpServletRequest));
            if (fileCountMax != null) {
                servletFileUpload.setFileCountMax(Long.parseLong(fileCountMax));
            }
            MultipartRequest multipartRequest = new MultipartRequest(httpServletRequest, servletFileUpload);

            if (LOGGER.isLoggable(Level.FINE)) {
                LOGGER.fine("File upload request parsed succesfully, continuing with filter chain with a wrapped multipart request");
            }

            filterChain.doFilter(multipartRequest, response);
        }
        else {
            filterChain.doFilter(request, response);
        }
    }

    @Override
    public void destroy() {
        if (LOGGER.isLoggable(Level.FINE)) {
            LOGGER.fine("Destroying FileUploadFilter");
        }
    }

    protected FileItemFactory createFileItemFactory(HttpServletRequest httpServletRequest) {
        DiskFileItemFactory diskFileItemFactory = new DiskFileItemFactory();
        if (thresholdSize != null) {
            diskFileItemFactory.setSizeThreshold(Integer.parseInt(thresholdSize));
        }
        if (uploadDir != null) {
            diskFileItemFactory.setRepository(new File(uploadDir));
        }

        FileCleaningTracker fileCleaningTracker = FileCleanerCleanup.getFileCleaningTracker(httpServletRequest.getSession().getServletContext());
        if (fileCleaningTracker != null) {
            diskFileItemFactory.setFileCleaningTracker(fileCleaningTracker);
        }

        return diskFileItemFactory;
    }
}
