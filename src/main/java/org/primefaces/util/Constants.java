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
package org.primefaces.util;

public class Constants {

    public static class ContextParams {

        // JSF context params
        public static final String INTERPRET_EMPTY_STRING_AS_NULL = "javax.faces.INTERPRET_EMPTY_STRING_SUBMITTED_VALUES_AS_NULL";

        // PF context params
        public static final String THEME = "primefaces.THEME";
        public static final String FONT_AWESOME = "primefaces.FONT_AWESOME";
        public static final String SUBMIT = "primefaces.SUBMIT";
        public static final String DIRECTION = "primefaces.DIR";
        public static final String RESET_VALUES = "primefaces.RESET_VALUES";
        public static final String PFV_KEY = "primefaces.CLIENT_SIDE_VALIDATION";
        public static final String UPLOADER = "primefaces.UPLOADER";
        public static final String CACHE_PROVIDER = "primefaces.CACHE_PROVIDER";
        public static final String TRANSFORM_METADATA = "primefaces.TRANSFORM_METADATA";
        public static final String LEGACY_WIDGET_NAMESPACE = "primefaces.LEGACY_WIDGET_NAMESPACE";
        public static final String BEAN_VALIDATION_DISABLED = "javax.faces.validator.DISABLE_DEFAULT_BEAN_VALIDATOR";
        public static final String INTERPOLATE_CLIENT_SIDE_VALIDATION_MESSAGES = "primefaces.INTERPOLATE_CLIENT_SIDE_VALIDATION_MESSAGES";
        public static final String EARLY_POST_PARAM_EVALUATION = "primefaces.EARLY_POST_PARAM_EVALUATION";
        public static final String MOVE_SCRIPTS_TO_BOTTOM = "primefaces.MOVE_SCRIPTS_TO_BOTTOM";
        public static final String CSP = "primefaces.CSP";
    }

    public static class RequestParams {

        // JSF request params
        public static final String PARTIAL_REQUEST_PARAM = "javax.faces.partial.ajax";
        public static final String PARTIAL_UPDATE_PARAM = "javax.faces.partial.render";
        public static final String PARTIAL_PROCESS_PARAM = "javax.faces.partial.execute";
        public static final String PARTIAL_SOURCE_PARAM = "javax.faces.source";
        public static final String PARTIAL_BEHAVIOR_EVENT_PARAM = "javax.faces.behavior.event";

        // PF request params
        public static final String RESET_VALUES_PARAM = "primefaces.resetvalues";
        public static final String IGNORE_AUTO_UPDATE_PARAM = "primefaces.ignoreautoupdate";
        public static final String SKIP_CHILDREN_PARAM = "primefaces.skipchildren";
        public static final String NONCE_PARAM = "primefaces.nonce";
    }

    public static final String DOWNLOAD_COOKIE = "primefaces.download";

    public static final String LIBRARY = "primefaces";

    public static final String DYNAMIC_CONTENT_PARAM = "pfdrid";
    public static final String DYNAMIC_CONTENT_CACHE_PARAM = "pfdrid_c";
    public static final String DYNAMIC_CONTENT_TYPE_PARAM = "pfdrt";
    public static final String DYNAMIC_RESOURCES_MAPPING = "primefaces.dynamicResourcesMapping";

    public static final String BARCODE_MAPPING = "primefaces.barcodeMapping";

    public static final String FRAGMENT_ID = "primefaces.fragment";

    public static class DIALOG_FRAMEWORK {

        public static final String OUTCOME = "dialog.outcome";
        public static final String OPTIONS = "dialog.options";
        public static final String PARAMS = "dialog.params";
        public static final String SOURCE_COMPONENT = "dialog.source.component";
        public static final String SOURCE_WIDGET = "dialog.source.widget";
        public static final String CONVERSATION_PARAM = "pfdlgcid";
        public static final String INCLUDE_VIEW_PARAMS = "includeViewParams";
    }

    public static final String EMPTY_STRING = "";

    public static final String CLIENT_BEHAVIOR_RENDERING_MODE = "CLIENT_BEHAVIOR_RENDERING_MODE";

    public static final String DEFAULT_CACHE_REGION = "primefaces.DEFAULT_CACHE_REGION";

    public static final String HELPER_RENDERER = "org.primefaces.HELPER_RENDERER";

    /**
     * @deprecated Use MULTI_VIEW_STATES instead
     */
    @Deprecated
    public static final String TABLE_STATE = "primefaces.TABLE_STATE";

    /**
     * @deprecated Use MULTI_VIEW_STATES instead
     */
    @Deprecated
    public static final String DATALIST_STATE = "primefaces.DATALIST_STATE";

    public static final String MULTI_VIEW_STATES = "primefaces.MULTI_VIEW_STATES";

    private Constants() {
    }
}
