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
package org.primefaces.resource.stream;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.primefaces.resource.ResourceUtils;

/**
 * Streams css resources
 */
public class CSSResourceStreamer implements ResourceStreamer {

	public boolean isAppropriateStreamer(String mimeType) {
		return (mimeType != null && mimeType.equals("text/css"));
	}

	public void stream(HttpServletRequest request, HttpServletResponse response, InputStream inputStream) throws IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
		OutputStreamWriter writer = new OutputStreamWriter(response.getOutputStream(), response.getCharacterEncoding());
		String line = null;
		
		while (null != (line = reader.readLine())) {
			String parsedLine = replaceRelativeUrl(request.getContextPath(), line);
			writer.write(parsedLine+"\n");
        }
		
		writer.flush();
		writer.close();
	}
	
	public String replaceRelativeUrl(String contextPath, String input) {
        String replacement = contextPath + ResourceUtils.RESOURCE_VERSION_PATTERN;
        Pattern pattern = Pattern.compile(ResourceUtils.CSS_RESOURCE_PATTERN);

        Matcher matcher = pattern.matcher(input);
        return matcher.replaceAll(replacement);
	}
}