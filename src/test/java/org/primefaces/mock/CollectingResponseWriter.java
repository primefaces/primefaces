/*
 * Copyright 2009-2015 PrimeTek.
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
package org.primefaces.mock;

import java.io.IOException;
import java.io.Writer;

import javax.faces.component.UIComponent;
import javax.faces.context.ResponseWriter;

public class CollectingResponseWriter extends ResponseWriter {

	private StringBuilder builder = new StringBuilder();
	
	private boolean inXmlTag = false; 
	
    public CollectingResponseWriter() {
        
    }
    
    public CollectingResponseWriter(StringBuilder builder) {
        this.builder = builder;
    }
    
	@Override
	public ResponseWriter cloneWithWriter(Writer arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void endDocument() throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void endElement(String arg0) throws IOException {
		builder.append("</" + arg0 + ">");
	}

	@Override
	public void flush() throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getCharacterEncoding() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getContentType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void startDocument() throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void startElement(String arg0, UIComponent arg1) throws IOException {
		builder.append("<" + arg0);
		inXmlTag = true;
	}

	@Override
	public void writeAttribute(String arg0, Object arg1, String arg2)
			throws IOException {
		builder.append(" " + arg0 + "=\"" + arg1 + "\"");
		
	}

	@Override
	public void writeComment(Object arg0) throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void writeText(Object arg0, String arg1) throws IOException {
		if (inXmlTag) {
			inXmlTag = false;
			builder.append(">");
		}
		builder.append(arg1);
	}

	@Override
	public void writeText(char[] arg0, int arg1, int arg2) throws IOException {
		if (inXmlTag) {
			inXmlTag = false;
			builder.append(">");
		}
		builder.append(arg0);
	}

	@Override
	public void writeURIAttribute(String arg0, Object arg1, String arg2)
			throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void close() throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void write(char[] arg0, int arg1, int arg2) throws IOException {
		if (inXmlTag) {
			inXmlTag = false;
			builder.append(">");
		}
		builder.append(arg0);
	}
	
	@Override
	public void write(String arg0) throws IOException {
		if (inXmlTag) {
			inXmlTag = false;
			builder.append(">");
		}
		builder.append(arg0);
	}

	public String toString() {
		return builder.toString();
	}
}
