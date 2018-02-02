/**
 * Copyright 2009-2018 PrimeTek.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
import org.primefaces.config.PrimeConfiguration;
import org.primefaces.config.StartupPrimeConfiguration;
import org.primefaces.util.Constants;
import org.primefaces.webapp.MultipartRequest;

public class FileUploadFilter implements Filter {

    private final static Logger logger = Logger.getLogger(FileUploadFilter.class.getName());

    private final static String THRESHOLD_SIZE_PARAM = "thresholdSize";

    private final static String UPLOAD_DIRECTORY_PARAM = "uploadDirectory";

    private String thresholdSize;

    private String uploadDir;

    private boolean bypass;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        String uploader = filterConfig.getServletContext().getInitParameter(Constants.ContextParams.UPLOADER);

        if (uploader == null || uploader.equals("auto")) {
            PrimeConfiguration config = new StartupPrimeConfiguration();
            bypass = config.isAtLeastJSF22();
        }
        else if (uploader.equals("native")) {
            bypass = true;
        }
        else if (uploader.equals("commons")) {
            bypass = false;
        }

        thresholdSize = filterConfig.getInitParameter(THRESHOLD_SIZE_PARAM);
        uploadDir = filterConfig.getInitParameter(UPLOAD_DIRECTORY_PARAM);

        if (logger.isLoggable(Level.FINE)) {
            logger.fine("FileUploadFilter initiated successfully");
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
            if (logger.isLoggable(Level.FINE)) {
                logger.fine("Parsing file upload request");
            }

            ServletFileUpload servletFileUpload = new ServletFileUpload(createFileItemFactory(httpServletRequest));
            MultipartRequest multipartRequest = new MultipartRequest(httpServletRequest, servletFileUpload);

            if (logger.isLoggable(Level.FINE)) {
                logger.fine("File upload request parsed succesfully, continuing with filter chain with a wrapped multipart request");
            }

            filterChain.doFilter(multipartRequest, response);
        }
        else {
            filterChain.doFilter(request, response);
        }
    }

    @Override
    public void destroy() {
        if (logger.isLoggable(Level.FINE)) {
            logger.fine("Destroying FileUploadFilter");
        }
    }

    protected FileItemFactory createFileItemFactory(HttpServletRequest httpServletRequest) {
        DiskFileItemFactory diskFileItemFactory = new DiskFileItemFactory();
        if (thresholdSize != null) {
            diskFileItemFactory.setSizeThreshold(Integer.valueOf(thresholdSize));
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
