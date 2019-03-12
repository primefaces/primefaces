/* 
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
		return null;
	}

	@Override
	public void endDocument() throws IOException {

	}

	@Override
	public void endElement(String arg0) throws IOException {
		builder.append("</" + arg0 + ">");
	}

	@Override
	public void flush() throws IOException {

	}

	@Override
	public String getCharacterEncoding() {
		return null;
	}

	@Override
	public String getContentType() {
		return null;
	}

	@Override
	public void startDocument() throws IOException {

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

	}

	@Override
	public void close() throws IOException {

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

        @Override
	public String toString() {
		return builder.toString();
	}
}
