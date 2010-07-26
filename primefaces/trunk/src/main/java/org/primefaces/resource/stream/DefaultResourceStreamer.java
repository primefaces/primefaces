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

import java.io.IOException;
import java.io.InputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Default streamer for scripts, css, images and all other content
 */
public class DefaultResourceStreamer implements ResourceStreamer {

	public boolean isAppropriateStreamer(String mimeType) {
		return (mimeType != null);
	}

	public void stream(HttpServletRequest request, HttpServletResponse response, InputStream inputStream) throws IOException {
		byte[] buffer = new byte[2048];
		
		 int length;
		 while ((length = (inputStream.read(buffer))) >= 0) {
			 response.getOutputStream().write(buffer, 0, length);
		 }
	}

}
