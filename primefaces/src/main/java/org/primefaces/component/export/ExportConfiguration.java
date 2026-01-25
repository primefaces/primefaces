/*
 * The MIT License
 *
 * Copyright (c) 2009-2026 PrimeTek Informatics
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

import java.io.OutputStream;

import jakarta.el.MethodExpression;

public class ExportConfiguration {

    private boolean pageOnly;
    private boolean selectionOnly;
    private boolean visibleOnly;
    private boolean exportHeader;
    private boolean exportFooter;
    private String encodingType;
    private MethodExpression preProcessor;
    private MethodExpression postProcessor;
    private ExporterOptions options;
    private MethodExpression onTableRender;
    private MethodExpression onRowExport;
    private OutputStream outputStream;
    private Integer bufferSize;

    public ExportConfiguration() {
        // NOOP
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private final ExportConfiguration config;

        Builder() {
            config = new ExportConfiguration();
        }

        public Builder pageOnly(boolean pageOnly) {
            this.config.pageOnly = pageOnly;
            return this;
        }

        public Builder selectionOnly(boolean selectionOnly) {
            this.config.selectionOnly = selectionOnly;
            return this;
        }

        public Builder visibleOnly(boolean visibleOnly) {
            this.config.visibleOnly = visibleOnly;
            return this;
        }

        public Builder exportHeader(boolean exportHeader) {
            this.config.exportHeader = exportHeader;
            return this;
        }

        public Builder exportFooter(boolean exportFooter) {
            this.config.exportFooter = exportFooter;
            return this;
        }

        public Builder encodingType(String encodingType) {
            this.config.encodingType = encodingType;
            return this;
        }

        public Builder preProcessor(MethodExpression preProcessor) {
            this.config.preProcessor = preProcessor;
            return this;
        }

        public Builder postProcessor(MethodExpression postProcessor) {
            this.config.postProcessor = postProcessor;
            return this;
        }

        public Builder options(ExporterOptions options) {
            this.config.options = options;
            return this;
        }

        public Builder onTableRender(MethodExpression onTableRender) {
            this.config.onTableRender = onTableRender;
            return this;
        }

        public Builder onRowExport(MethodExpression onRowExport) {
            this.config.onRowExport = onRowExport;
            return this;
        }

        public Builder outputStream(OutputStream os) {
            this.config.outputStream = os;
            return this;
        }

        public Builder bufferSize(Integer bufferSize) {
            this.config.bufferSize = bufferSize;
            return this;
        }

        public ExportConfiguration build() {
            return config;
        }
    }

    @Override
    public String toString() {
        return "ExportConfiguration{" +
                "pageOnly=" + pageOnly +
                ", selectionOnly=" + selectionOnly +
                ", visibleOnly=" + visibleOnly +
                ", exportHeader=" + exportHeader +
                ", exportFooter=" + exportFooter +
                ", encodingType='" + encodingType + '\'' +
                ", preProcessor=" + preProcessor +
                ", postProcessor=" + postProcessor +
                ", options=" + options +
                ", onTableRender=" + onTableRender +
                ", onRowExport=" + onRowExport +
                ", outputStream=" + outputStream +
                ", bufferSize=" + bufferSize +
                '}';
    }

    public boolean isPageOnly() {
        return pageOnly;
    }

    public boolean isSelectionOnly() {
        return selectionOnly;
    }

    public boolean isVisibleOnly() {
        return visibleOnly;
    }

    public boolean isExportHeader() {
        return exportHeader;
    }

    public boolean isExportFooter() {
        return exportFooter;
    }

    public String getEncodingType() {
        return encodingType;
    }

    public MethodExpression getPreProcessor() {
        return preProcessor;
    }

    public MethodExpression getPostProcessor() {
        return postProcessor;
    }

    public ExporterOptions getOptions() {
        return options;
    }

    public MethodExpression getOnTableRender() {
        return onTableRender;
    }

    public MethodExpression getOnRowExport() {
        return onRowExport;
    }

    public OutputStream getOutputStream() {
        return outputStream;
    }

    public Integer getBufferSize() {
        return bufferSize;
    }
}
