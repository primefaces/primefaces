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
package org.primefaces.context;

import java.io.IOException;
import java.util.Map;
import javax.faces.context.PartialResponseWriter;

public class PartialResponseWriterWrapper extends PartialResponseWriter {

    private PartialResponseWriter wrapped;

    public PartialResponseWriterWrapper(PartialResponseWriter wrapped) {
        super(wrapped);
        this.wrapped = wrapped;
    }

    @Override
    public void delete(String targetId) throws IOException {
        wrapped.delete(targetId);
    }

    @Override
    public void endDocument() throws IOException {
        wrapped.endDocument();
    }

    @Override
    public void endError() throws IOException {
        wrapped.endError();
    }

    @Override
    public void endEval() throws IOException {
        wrapped.endEval();
    }

    @Override
    public void endExtension() throws IOException {
        wrapped.endExtension();
    }

    @Override
    public void endInsert() throws IOException {
        wrapped.endInsert();
    }

    @Override
    public void endUpdate() throws IOException {
        wrapped.endUpdate();
    }

    @Override
    public void redirect(String url) throws IOException {
        wrapped.redirect(url);
    }

    @Override
    public void startDocument() throws IOException {
        wrapped.startDocument();
    }

    @Override
    public void startError(String errorName) throws IOException {
        wrapped.startError(errorName);
    }

    @Override
    public void startEval() throws IOException {
        wrapped.startEval();
    }

    @Override
    public void startExtension(Map<String, String> attributes) throws IOException {
        wrapped.startExtension(attributes);
    }

    @Override
    public void startInsertAfter(String targetId) throws IOException {
        wrapped.startInsertAfter(targetId);
    }

    @Override
    public void startInsertBefore(String targetId) throws IOException {
        wrapped.startInsertBefore(targetId);
    }

    @Override
    public void startUpdate(String targetId) throws IOException {
        wrapped.startUpdate(targetId);
    }

    @Override
    public void updateAttributes(String targetId, Map<String, String> attributes) throws IOException {
        wrapped.updateAttributes(targetId, attributes);
    }
}
