/*
 * The MIT License
 *
 * Copyright (c) 2009-2022 PrimeTek
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
    private boolean visibleOnly;
    private boolean exportHeader;
    private boolean exportFooter;
    private String encodingType;
    private MethodExpression preProcessor;
    private MethodExpression postProcessor;
    private ExporterOptions options;
    private MethodExpression onTableRender;

    public ExportConfiguration() {
        // NOOP
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private String outputFileName;
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

        Builder() {
        }

        public Builder outputFileName(String outputFileName) {
            this.outputFileName = outputFileName;
            return this;
        }

        public Builder pageOnly(boolean pageOnly) {
            this.pageOnly = pageOnly;
            return this;
        }

        public Builder selectionOnly(boolean selectionOnly) {
            this.selectionOnly = selectionOnly;
            return this;
        }

        public Builder visibleOnly(boolean visibleOnly) {
            this.visibleOnly = visibleOnly;
            return this;
        }

        public Builder exportHeader(boolean exportHeader) {
            this.exportHeader = exportHeader;
            return this;
        }

        public Builder exportFooter(boolean exportFooter) {
            this.exportFooter = exportFooter;
            return this;
        }

        public Builder encodingType(String encodingType) {
            this.encodingType = encodingType;
            return this;
        }

        public Builder preProcessor(MethodExpression preProcessor) {
            this.preProcessor = preProcessor;
            return this;
        }

        public Builder postProcessor(MethodExpression postProcessor) {
            this.postProcessor = postProcessor;
            return this;
        }

        public Builder options(ExporterOptions options) {
            this.options = options;
            return this;
        }

        public Builder onTableRender(MethodExpression onTableRender) {
            this.onTableRender = onTableRender;
            return this;
        }

        public ExportConfiguration build() {
            ExportConfiguration config = new ExportConfiguration();
            config.outputFileName = this.outputFileName;
            config.pageOnly = this.pageOnly;
            config.selectionOnly = this.selectionOnly;
            config.visibleOnly = this.visibleOnly;
            config.exportHeader = this.exportHeader;
            config.exportFooter = this.exportFooter;
            config.encodingType = this.encodingType;
            config.preProcessor = this.preProcessor;
            config.postProcessor = this.postProcessor;
            config.options = this.options;
            config.onTableRender = this.onTableRender;
            return config;
        }

        @Override
        public String toString() {
            return "ExportConfiguration.ExportConfigurationBuilder(outputFileName=" + this.outputFileName + ", pageOnly=" + this.pageOnly + ", selectionOnly="
                        + this.selectionOnly + ", visibleOnly=" + this.visibleOnly + ", exportHeader=" + this.exportHeader + ", exportFooter="
                        + this.exportFooter + ", encodingType=" + this.encodingType + ", preProcessor=" + this.preProcessor + ", postProcessor="
                        + this.postProcessor + ", options=" + this.options + ", onTableRender=" + this.onTableRender + ")";
        }
    }

    @Override
    public String toString() {
        return "ExportConfiguration(outputFileName=" + this.outputFileName + ", pageOnly=" + this.pageOnly + ", selectionOnly=" + this.selectionOnly
                    + ", visibleOnly=" + this.visibleOnly + ", exportHeader=" + this.exportHeader + ", exportFooter=" + this.exportFooter + ", encodingType="
                    + this.encodingType + ", preProcessor=" + this.preProcessor + ", postProcessor=" + this.postProcessor + ", options=" + this.options
                    + ", onTableRender=" + this.onTableRender + ")";
    }

    public String getOutputFileName() {
        return outputFileName;
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

}
