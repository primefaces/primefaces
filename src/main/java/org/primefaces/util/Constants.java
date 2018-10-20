/**
 * Copyright 2009-2018 PrimeTek.
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

    public static final String TABLE_STATE = "primefaces.TABLE_STATE";

    public static final String DATALIST_STATE = "primefaces.DATALIST_STATE";

    private Constants() {
    }
}
