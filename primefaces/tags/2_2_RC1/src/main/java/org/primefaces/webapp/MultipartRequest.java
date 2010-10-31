/*
 * Copyright 2009 Prime Technology.
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
package org.primefaces.webapp;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

public class MultipartRequest extends HttpServletRequestWrapper {

	private static final Logger logger = Logger.getLogger(MultipartRequest.class.getName());
	
	private Map<String, List<String>> formParams;
	
	private Map<String, List<FileItem>> fileParams;
	
	public MultipartRequest(HttpServletRequest request, ServletFileUpload servletFileUpload) throws IOException {
		super(request);
		formParams = new LinkedHashMap<String, List<String>>();
		fileParams = new LinkedHashMap<String, List<FileItem>>();
		
		parseRequest(request, servletFileUpload);
	}

	@SuppressWarnings("unchecked")
	private void parseRequest(HttpServletRequest request, ServletFileUpload servletFileUpload) throws IOException {
		try {
			List<FileItem> fileItems = servletFileUpload.parseRequest(request);
			
			for(FileItem item : fileItems) {
				if(item.isFormField())
					addFormParam(item);
				else
					addFileParam(item);
			}
			
		} catch (FileUploadException e) {
			logger.severe("Error in parsing fileupload request");
			
			throw new IOException(e.getMessage());
		}
	}
	
	private void addFileParam(FileItem item) {
		if(fileParams.containsKey(item.getFieldName())) {
			fileParams.get(item.getFieldName()).add(item);
		} else {
			List<FileItem> items = new ArrayList<FileItem>();
			items.add(item);
			fileParams.put(item.getFieldName(), items);
		}
	}
	
	private void addFormParam(FileItem item) {
		if(formParams.containsKey(item.getFieldName())) {
			formParams.get(item.getFieldName()).add(item.getString());
		} else {
			List<String> items = new ArrayList<String>();
			items.add(item.getString());
			formParams.put(item.getFieldName(), items);
		}
	}
	
	@Override
	public String getParameter(String name) {
		if(formParams.containsKey(name)) {
			List<String> values = formParams.get(name);
			if(values.isEmpty())
				return "";
			else
				return values.get(0);
		}
		else {
			return null;
		}
	}

	@Override
	public Map getParameterMap() {
		return Collections.unmodifiableMap(formParams);
	}

	@Override
	public Enumeration getParameterNames() {
		Set<String> paramNames = new LinkedHashSet<String>();
		paramNames.addAll(formParams.keySet());
		paramNames.addAll(fileParams.keySet());
		
		return Collections.enumeration(paramNames);
	}

	@Override
	public String[] getParameterValues(String name) {
		if(formParams.containsKey(name)) {
			List<String> values = formParams.get(name);
			if(values.isEmpty())
				return new String[0];
			else
				return values.toArray(new String[values.size()]);
		}
		else {
			return null;
		}
	}
	
	public FileItem getFileItem(String name) {
		if(fileParams.containsKey(name)) {
			List<FileItem> items = fileParams.get(name);
			
			return items.get(0);
		} else {
			return null;
		}	
	}

    //Workaround to mimic ajax request since flash does not allow custom request headers
    @Override
    public String getHeader(String name) {
        if(name != null && name.equalsIgnoreCase("Faces-Request"))
            return "partial/ajax";
        else
            return ((HttpServletRequest) getRequest()).getHeader(name);
    }
}