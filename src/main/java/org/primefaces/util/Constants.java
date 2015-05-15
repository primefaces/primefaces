/*
 * Copyright 2009-2014 PrimeTek.
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

    public class ContextParams {
        // JSF context params
        public static final String INTERPRET_EMPTY_STRING_AS_NULL = "javax.faces.INTERPRET_EMPTY_STRING_SUBMITTED_VALUES_AS_NULL";
        
        // PF context params
        public static final String THEME = "primefaces.THEME";
        public static final String MOBILE_THEME = "primefaces.mobile.THEME";
        public static final String FONT_AWESOME = "primefaces.FONT_AWESOME";
        public static final String AUTO_UPDATE = "primefaces.AUTO_UPDATE";
        public static final String PUSH_SERVER_URL = "primefaces.PUSH_SERVER_URL";
        public static final String SUBMIT = "primefaces.SUBMIT";
        public static final String DIRECTION = "primefaces.DIR";
        public static final String RESET_VALUES = "primefaces.RESET_VALUES";
        public static final String SECRET_KEY = "primefaces.SECRET";
        public static final String PFV_KEY = "primefaces.CLIENT_SIDE_VALIDATION";
        public static final String UPLOADER = "primefaces.UPLOADER";
        public static final String CACHE_PROVIDER = "primefaces.CACHE_PROVIDER";
        public static final String TRANSFORM_METADATA = "primefaces.TRANSFORM_METADATA";
        public static final String LEGACY_WIDGET_NAMESPACE = "primefaces.LEGACY_WIDGET_NAMESPACE";
        public static final String BEAN_VALIDATION_DISABLED = "javax.faces.validator.DISABLE_DEFAULT_BEAN_VALIDATOR";
    }

    public class RequestParams {
        // JSF request params
        public static final String PARTIAL_REQUEST_PARAM = "javax.faces.partial.ajax";
        public static final String PARTIAL_UPDATE_PARAM = "javax.faces.partial.render";
        public static final String PARTIAL_PROCESS_PARAM = "javax.faces.partial.execute";
        public static final String PARTIAL_SOURCE_PARAM = "javax.faces.source";
        public static final String PARTIAL_BEHAVIOR_EVENT_PARAM = "javax.faces.behavior.event";

        // PF request params
        public static final String RESET_VALUES_PARAM = "primefaces.resetvalues";
        public static final String IGNORE_AUTO_UPDATE_PARAM = "primefaces.ignoreautoupdate";
    }

    public static final String DOWNLOAD_COOKIE = "primefaces.download";

    public final static String LIBRARY = "primefaces";
    
    public final static String PUSH_PATH = "/primepush";
    
    public static final String DYNAMIC_CONTENT_PARAM = "pfdrid";
    public static final String DYNAMIC_CONTENT_CACHE_PARAM = "pfdrid_c";
    public static final String DYNAMIC_CONTENT_TYPE_PARAM = "pfdrt";

    public final static String FRAGMENT_ID = "primefaces.fragment";
    public final static String FRAGMENT_AUTO_RENDERED = "primefaces.fragment.autorendered";
    
    public class DIALOG_FRAMEWORK {
        public final static String OUTCOME = "dialog.outcome";
        public final static String OPTIONS = "dialog.options";
        public final static String PARAMS = "dialog.params";
        public final static String SOURCE_COMPONENT = "dialog.source.component";
        public final static String SOURCE_WIDGET = "dialog.source.widget";
        public final static String CONVERSATION_PARAM = "pfdlgcid";
        public final static String INCLUDE_VIEW_PARAMS = "includeViewParams";
    }
    
    public static final String EMPTY_STRING = "";
    
    public final static String CLIENT_BEHAVIOR_RENDERING_MODE = "CLIENT_BEHAVIOR_RENDERING_MODE";
    
    public final static String DEFAULT_CACHE_REGION = "primefaces.DEFAULT_CACHE_REGION";
    
    public final static String MOBILE_RENDER_KIT_ID = "PRIMEFACES_MOBILE";
    
    public static final String HELPER_RENDERER = "org.primefaces.HELPER_RENDERER";
    
    
}
