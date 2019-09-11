/**
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
package org.primefaces.component.export;

import javax.el.MethodExpression;

public class ExportConfiguration {

    private String outputFileName;
    private boolean pageOnly;
    private boolean selectionOnly;
    private String encodingType;
    private MethodExpression preProcessor;
    private MethodExpression postProcessor;
    private ExporterOptions options;
    private MethodExpression onTableRender;

    public String getOutputFileName() {
        return outputFileName;
    }

    public ExportConfiguration setOutputFileName(String outputFileName) {
        this.outputFileName = outputFileName;
        return this;
    }

    public boolean isPageOnly() {
        return pageOnly;
    }

    public ExportConfiguration setPageOnly(boolean pageOnly) {
        this.pageOnly = pageOnly;
        return this;
    }

    public boolean isSelectionOnly() {
        return selectionOnly;
    }

    public ExportConfiguration setSelectionOnly(boolean selectionOnly) {
        this.selectionOnly = selectionOnly;
        return this;
    }

    public String getEncodingType() {
        return encodingType;
    }

    public ExportConfiguration setEncodingType(String encodingType) {
        this.encodingType = encodingType;
        return this;
    }

    public MethodExpression getPreProcessor() {
        return preProcessor;
    }

    public ExportConfiguration setPreProcessor(MethodExpression preProcessor) {
        this.preProcessor = preProcessor;
        return this;
    }

    public MethodExpression getPostProcessor() {
        return postProcessor;
    }

    public ExportConfiguration setPostProcessor(MethodExpression postProcessor) {
        this.postProcessor = postProcessor;
        return this;
    }

    public ExporterOptions getOptions() {
        return options;
    }

    public ExportConfiguration setOptions(ExporterOptions options) {
        this.options = options;
        return this;
    }

    public MethodExpression getOnTableRender() {
        return onTableRender;
    }

    public ExportConfiguration setOnTableRender(MethodExpression onTableRender) {
        this.onTableRender = onTableRender;
        return this;
    }
}
