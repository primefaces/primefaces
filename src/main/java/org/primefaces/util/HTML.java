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

public class HTML {
	
	public static String[] CLICK_EVENT = {"onclick"};

	public static String[] BLUR_FOCUS_EVENTS = {
		"onblur",
		"onfocus"
	};
	
	public static String[] CHANGE_SELECT_EVENTS = {
		"onchange",
		"onselect"
	};
	    
    public static String[] COMMON_EVENTS_WITHOUT_CLICK = {
		"ondblclick",
		"onkeydown",
		"onkeypress",
		"onkeyup",
		"onmousedown",
		"onmousemove",
		"onmouseout",
		"onmouseover",
		"onmouseup"
	};
	
	//StyleClass is omitted
	public static String[] IMG_ATTRS_WITHOUT_EVENTS = {
		"alt",
		"width",
		"height",
		"title",
		"dir",
		"lang",
		"ismap",
		"usemap",
		"style"
	};
	
	//StyleClass is omitted
	public static String[] LINK_ATTRS_WITHOUT_EVENTS = {
		"accesskey",
		"charset",
		"coords",
		"dir",
		"disabled",
		"hreflang",
		"rel",
		"rev",
		"shape",
		"tabindex",
		"style",
		"target",
		"title",
		"type"
	};
	
	//StyleClass is omitted
	public static String[] BUTTON_ATTRS_WITHOUT_EVENTS = {
		"accesskey",
		"alt",
		"dir",
		"label",
		"lang",
		"style",
		"tabindex",
		"title",
		"type"
	};
	
	//StyleClass is omitted
	public static String[] MEDIA_ATTRS = {
		"height",
		"width",
		"style"
	};

    //disabled, readonly, style, styleClass handles by component renderer
	public static String[] INPUT_TEXT_ATTRS_WITHOUT_EVENTS = {
		"accesskey",
		"alt",
        "autocomplete",
		"dir",
		"lang",
		"maxlength",
        "placeholder",
		"size",
		"tabindex",
		"title"
	};

    public static String[] SELECT_ATTRS_WITHOUT_EVENTS = {
		"accesskey",
		"dir",
		"disabled",
		"lang",
		"readonly",
		"style",
		"tabindex",
		"title"
	};

	public static String[] TEXTAREA_ATTRS_WITHOUT_EVENTS = {
		"cols",
		"rows",
        "accesskey",
		"alt",
        "autocomplete",
        "placeholder",
		"dir",
		"lang",
		"size",
		"tabindex",
		"title",
        "maxlength"
	};
    
    //StyleClass is omitted
	public static String[] LABEL_ATTRS_WITHOUT_EVENTS = {
		"accesskey",
		"dir",
		"lang",
		"style",
		"tabindex",
		"title"
	};
    
    public static final String[] BODY_ATTRS = ArrayUtils.concat(COMMON_EVENTS_WITHOUT_CLICK, CLICK_EVENT, new String[] {
		"dir",
		"lang",
		"style",
        "title",
		"onload",
		"onunload"
	});
    
    public static String[] COMMON_EVENTS = ArrayUtils.concat(COMMON_EVENTS_WITHOUT_CLICK, CLICK_EVENT);
    
    public static String[] OUTPUT_EVENTS = ArrayUtils.concat(COMMON_EVENTS, BLUR_FOCUS_EVENTS);
    
    public static String[] OUTPUT_EVENTS_WITHOUT_CLICK = ArrayUtils.concat(COMMON_EVENTS_WITHOUT_CLICK, BLUR_FOCUS_EVENTS);
	
	public static String[] BUTTON_EVENTS = ArrayUtils.concat(OUTPUT_EVENTS, CHANGE_SELECT_EVENTS);
    
    public static String[] BUTTON_EVENTS_WITHOUT_CLICK = ArrayUtils.concat(OUTPUT_EVENTS_WITHOUT_CLICK, CHANGE_SELECT_EVENTS);
	
	public static String[] IMG_ATTRS = ArrayUtils.concat(IMG_ATTRS_WITHOUT_EVENTS, COMMON_EVENTS);
	
	public static String[] LINK_ATTRS = ArrayUtils.concat(LINK_ATTRS_WITHOUT_EVENTS, OUTPUT_EVENTS);
    
    public static String[] LABEL_ATTRS = ArrayUtils.concat(LABEL_ATTRS_WITHOUT_EVENTS, OUTPUT_EVENTS);
	
	public static String[] BUTTON_ATTRS = ArrayUtils.concat(BUTTON_ATTRS_WITHOUT_EVENTS, BUTTON_EVENTS);	
	
    public static final String[] INPUT_TEXT_EVENTS = ArrayUtils.concat(COMMON_EVENTS, CHANGE_SELECT_EVENTS, BLUR_FOCUS_EVENTS);
    public static final String[] INPUT_TEXT_ATTRS = ArrayUtils.concat(INPUT_TEXT_ATTRS_WITHOUT_EVENTS, INPUT_TEXT_EVENTS);
    
    public static final String[] INPUT_TEXTAREA_ATTRS = ArrayUtils.concat(TEXTAREA_ATTRS_WITHOUT_EVENTS, COMMON_EVENTS, CHANGE_SELECT_EVENTS, BLUR_FOCUS_EVENTS);

    public static final String[] SELECT_ATTRS = ArrayUtils.concat(SELECT_ATTRS_WITHOUT_EVENTS, COMMON_EVENTS, CHANGE_SELECT_EVENTS, BLUR_FOCUS_EVENTS);
    
    public final static String BUTTON_TEXT_ONLY_BUTTON_CLASS = "ui-button ui-widget ui-state-default ui-corner-all ui-button-text-only";
    public final static String BUTTON_ICON_ONLY_BUTTON_CLASS = "ui-button ui-widget ui-state-default ui-corner-all ui-button-icon-only";
    public final static String BUTTON_TEXT_ICON_LEFT_BUTTON_CLASS = "ui-button ui-widget ui-state-default ui-corner-all ui-button-text-icon-left";
    public final static String BUTTON_TEXT_ICON_RIGHT_BUTTON_CLASS = "ui-button ui-widget ui-state-default ui-corner-all ui-button-text-icon-right";
    public final static String BUTTON_LEFT_ICON_CLASS = "ui-button-icon-left ui-icon ui-c";
    public final static String BUTTON_RIGHT_ICON_CLASS = "ui-button-icon-right ui-icon ui-c";
    public final static String BUTTON_TEXT_CLASS = "ui-button-text ui-c";
    public final static String BUTTON_TEXT_ONLY_BUTTON_FLAT_CLASS = "ui-button ui-widget ui-state-default ui-button-text-only";
    
    public final static String CHECKBOX_ALL_CLASS = "ui-chkbox ui-chkbox-all ui-widget";
    public final static String CHECKBOX_CLASS = "ui-chkbox ui-widget";
    public final static String CHECKBOX_BOX_CLASS = "ui-chkbox-box ui-widget ui-corner-all ui-state-default";
    public final static String CHECKBOX_INPUT_WRAPPER_CLASS = "ui-helper-hidden";
    public final static String CHECKBOX_UNCHECKED_ICON_CLASS = "ui-chkbox-icon ui-icon ui-icon-blank ui-c";
    public final static String CHECKBOX_CHECKED_ICON_CLASS = "ui-chkbox-icon ui-icon ui-icon-check ui-c";
    public final static String CHECKBOX_PARTIAL_CHECKED_ICON_CLASS = "ui-chkbox-icon ui-icon ui-icon-minus ui-c";
    public final static String CHECKBOX_LABEL_CLASS = "ui-chkbox-label";
    
    public final static String RADIOBUTTON_CLASS = "ui-radiobutton ui-widget";
    public final static String RADIOBUTTON_NATIVE_CLASS = "ui-radiobutton ui-radiobutton-native ui-widget";
    public final static String RADIOBUTTON_BOX_CLASS = "ui-radiobutton-box ui-widget ui-corner-all ui-state-default";
    public final static String RADIOBUTTON_INPUT_WRAPPER_CLASS = "ui-helper-hidden";
    public final static String RADIOBUTTON_UNCHECKED_ICON_CLASS = "ui-radiobutton-icon ui-icon ui-icon-blank ui-c";
    public final static String RADIOBUTTON_CHECKED_ICON_CLASS = "ui-radiobutton-icon ui-icon ui-icon-bullet ui-c";
    
    public final static String WIDGET_VAR = "data-widget";
    
    public class VALIDATION_METADATA {
        public static final String LABEL = "data-p-label";
        public static final String REQUIRED = "data-p-required";
        public static final String MIN_LENGTH = "data-p-minlength";
        public static final String MAX_LENGTH = "data-p-maxlength";
        public static final String MIN_VALUE = "data-p-minvalue";
        public static final String MAX_VALUE = "data-p-maxvalue";
        public static final String VALIDATOR_IDS = "data-p-val";
        public static final String CONVERTER = "data-p-con";
        public static final String REGEX = "data-p-regex";
        public static final String PATTERN = "data-p-pattern";
        public static final String DATE_STYLE_PATTERN = "data-p-dspattern";
        public static final String TIME_STYLE_PATTERN = "data-p-tspattern";
        public static final String DATETIME_TYPE = "data-p-dttype";
        public static final String REQUIRED_MESSAGE = "data-p-rmsg";
        public static final String VALIDATOR_MESSAGE = "data-p-vmsg";
        public static final String CONVERTER_MESSAGE = "data-p-cmsg";
        public static final String DIGITS_INTEGER = "data-p-dintvalue";
        public static final String DIGITS_FRACTION = "data-p-dfracvalue";
        public static final String MAX_FRACTION_DIGITS = "data-p-maxfrac";
        public static final String MIN_FRACTION_DIGITS = "data-p-minfrac";
        public static final String MAX_INTEGER_DIGITS = "data-p-maxint";
        public static final String MIN_INTEGER_DIGITS = "data-p-minint";
        public static final String INTEGER_ONLY = "data-p-intonly";
        public static final String CURRENCY_SYMBOL = "data-p-curs";
        public static final String CURRENCY_CODE = "data-p-curc";
        public static final String NUMBER_TYPE = "data-p-notype";
        public static final String HIGHLIGHTER = "data-p-hl";
        public static final String GROUPED = "data-p-grouped";
    }
}
