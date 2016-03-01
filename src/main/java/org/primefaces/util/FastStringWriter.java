/*
 * Copyright 2009-2016 PrimeTek.
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
package org.primefaces.util;

import java.io.IOException;
import java.io.Writer;

public class FastStringWriter extends Writer {

	protected StringBuilder builder;

	public FastStringWriter() {
		builder = new StringBuilder();
	}

	public FastStringWriter(int initialCapacity) {
		if (initialCapacity < 0) {
			throw new IllegalArgumentException();
		}

		builder = new StringBuilder(initialCapacity);
	}

	@Override
	public void write(char[] cbuf, int off, int len) throws IOException {
		if ((off < 0) || (off > cbuf.length) || (len < 0)
		    || ((off + len) > cbuf.length) || ((off + len) < 0)) {
			throw new IndexOutOfBoundsException();
		} else if (len == 0) {
			return;
		}

		builder.append(cbuf, off, len);
	}

	@Override
	public void flush() throws IOException {
	}

	@Override
	public void close() throws IOException {
	}

	@Override
	public void write(String str) {
		builder.append(str);
	}

	@Override
	public void write(String str, int off, int len) {
		builder.append(str.substring(off, off + len));
	}

	public StringBuilder getBuffer() {
		return builder;
	}

	@Override
	public String toString() {
		return builder.toString();
	}

	public void reset() {
		builder.setLength(0);
	}
}
