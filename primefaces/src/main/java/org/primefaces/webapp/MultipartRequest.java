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

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import java.util.Map.Entry;

public class MultipartRequest extends HttpServletRequestWrapper {

    private static final Logger LOGGER = Logger.getLogger(MultipartRequest.class.getName());

    private Map<String, List<String>> formParams;
    private Map<String, List<FileItem>> fileParams;
    private Map<String, String[]> parameterMap;
    private File uploadDirectory;

    public MultipartRequest(HttpServletRequest request, ServletFileUpload servletFileUpload) throws IOException {
        super(request);
        formParams = new LinkedHashMap<>();
        fileParams = new LinkedHashMap<>();

        this.uploadDirectory = ((DiskFileItemFactory) servletFileUpload.getFileItemFactory()).getRepository();
        parseRequest(request, servletFileUpload);
    }

    @SuppressWarnings("unchecked")
    private void parseRequest(HttpServletRequest request, ServletFileUpload servletFileUpload) throws IOException {
        try {
            List<FileItem> fileItems = servletFileUpload.parseRequest(request);

            for (FileItem item : fileItems) {
                if (item.isFormField()) {
                    List<String> items = formParams.computeIfAbsent(item.getFieldName(), k -> new ArrayList<>());
                    items.add(getItemString(item));
                }
                else {
                    List<FileItem> items = fileParams.computeIfAbsent(item.getFieldName(), k -> new ArrayList<>());
                    items.add(item);
                }
            }
        }
        catch (FileUploadException e) {
            throw new IOException("Error in parsing fileupload request", e);
        }
    }

    private String getItemString(FileItem item) {
        try {
            String characterEncoding = getRequest().getCharacterEncoding();
            return (characterEncoding == null) ? item.getString() : item.getString(characterEncoding);
        }
        catch (UnsupportedEncodingException e) {
            LOGGER.log(Level.SEVERE, "Unsupported character encoding " + getRequest().getCharacterEncoding(), e);
            return item.getString();
        }
    }

    @Override
    public String getParameter(String name) {
        if (formParams.containsKey(name)) {
            List<String> values = formParams.get(name);
            if (values.isEmpty()) {
                return "";
            }
            else {
                return values.get(0);
            }
        }
        else {
            return super.getParameter(name);
        }
    }

    @Override
    public Map<String, String[]> getParameterMap() {
        if (parameterMap == null) {
            Map<String, String[]> map = new LinkedHashMap<>();

            for (Entry<String, List<String>> entry : formParams.entrySet()) {
                map.put(entry.getKey(), entry.getValue().toArray(new String[0]));
            }

            map.putAll(super.getParameterMap());

            parameterMap = Collections.unmodifiableMap(map);
        }

        return parameterMap;
    }

    @Override
    public Enumeration<String> getParameterNames() {
        Set<String> paramNames = new LinkedHashSet<>(formParams.keySet());

        Enumeration<String> original = super.getParameterNames();
        while (original.hasMoreElements()) {
            paramNames.add(original.nextElement());
        }

        return Collections.enumeration(paramNames);
    }

    @Override
    public String[] getParameterValues(String name) {
        if (formParams.containsKey(name)) {
            List<String> values = formParams.get(name);
            if (values.isEmpty()) {
                return new String[0];
            }
            else {
                return values.toArray(new String[0]);
            }
        }
        else {
            return super.getParameterValues(name);
        }
    }

    public FileItem getFileItem(String name) {
        if (fileParams.containsKey(name)) {
            List<FileItem> items = fileParams.get(name);

            return items.get(0);
        }
        else {
            return null;
        }
    }

    public List<FileItem> getFileItems(String name) {
        if (fileParams.containsKey(name)) {
            return fileParams.get(name);
        }

        return Collections.emptyList();
    }

    public File getUploadDirectory() {
        return uploadDirectory;
    }
}
