/*
 * Copyright 2009-2011 Prime Technology.
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
	
	public static String[] COMMON_EVENTS = {
		"onclick",
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
	
	public static String[] INPUT_TEXT_ATTRS_WITHOUT_EVENTS = {
		"accesskey",
		"alt",
        "autocomplete",
		"dir",
		"disabled",
		"lang",
		"maxlength",
		"readonly",
		"size",
		"style",
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

	public static String[] TEXTAREA_ATTRS = {
		"cols",
		"rows"
	};
	
	public static String[] LINK_EVENTS = ArrayUtils.concat(COMMON_EVENTS, BLUR_FOCUS_EVENTS);
	
	public static String[] BUTTON_EVENTS = ArrayUtils.concat(LINK_EVENTS, CHANGE_SELECT_EVENTS);
	
	public static String[] IMG_ATTRS = ArrayUtils.concat(IMG_ATTRS_WITHOUT_EVENTS, COMMON_EVENTS);
	
	public static String[] LINK_ATTRS = ArrayUtils.concat(LINK_ATTRS_WITHOUT_EVENTS, LINK_EVENTS);
	
	public static String[] BUTTON_ATTRS = ArrayUtils.concat(BUTTON_ATTRS_WITHOUT_EVENTS, BUTTON_EVENTS);	
	
	public static final String[] INPUT_TEXT_ATTRS = ArrayUtils.concat(INPUT_TEXT_ATTRS_WITHOUT_EVENTS, COMMON_EVENTS, CHANGE_SELECT_EVENTS, BLUR_FOCUS_EVENTS);

    public static final String[] INPUT_TEXTAREA_ATTRS = ArrayUtils.concat(INPUT_TEXT_ATTRS, TEXTAREA_ATTRS);

    public static final String[] SELECT_ATTRS = ArrayUtils.concat(SELECT_ATTRS_WITHOUT_EVENTS, COMMON_EVENTS, CHANGE_SELECT_EVENTS, BLUR_FOCUS_EVENTS);
}
