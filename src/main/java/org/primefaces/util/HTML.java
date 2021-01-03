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
package org.primefaces.util;

import java.util.List;

public class HTML {

    public static final List<String> CLICK_EVENT = LangUtils.unmodifiableList("onclick");

    public static final List<String> TAB_INDEX = LangUtils.unmodifiableList("tabindex");

    public static final List<String> TITLE = LangUtils.unmodifiableList("title");

    public static final List<String> BLUR_FOCUS_EVENTS = LangUtils.unmodifiableList(
        "onblur",
        "onfocus"
    );

    public static final List<String> CHANGE_SELECT_EVENTS = LangUtils.unmodifiableList(
        "onchange",
        "onselect"
    );

    public static final List<String> COMMON_EVENTS_WITHOUT_CLICK = LangUtils.unmodifiableList(
        "ondblclick",
        "onkeydown",
        "onkeypress",
        "onkeyup",
        "onmousedown",
        "onmousemove",
        "onmouseout",
        "onmouseover",
        "onmouseup",
        "onwheel",
        "oncut",
        "oncopy",
        "onpaste",
        "oncontextmenu",
        "oninput",
        "oninvalid",
        "onreset",
        "onsearch",
        "ondrag",
        "ondragend",
        "ondragenter",
        "ondragleave",
        "ondragover",
        "ondragstart",
        "ondrop",
        "onscroll"
    );

    //StyleClass is omitted
    public static final List<String> IMG_ATTRS_WITHOUT_EVENTS = LangUtils.unmodifiableList(
        "alt",
        "width",
        "height",
        "title",
        "dir",
        "lang",
        "ismap",
        "usemap",
        "style"
    );

    //StyleClass is omitted
    public static final List<String> LINK_ATTRS_WITHOUT_EVENTS = LangUtils.unmodifiableList(
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
    );

    public static final List<String> LINK_ATTRS_WITHOUT_EVENTS_AND_STYLE = LangUtils.unmodifiableList(
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
        "target",
        "title",
        "type"
    );

    //StyleClass is omitted
    public static final List<String> BUTTON_ATTRS_WITHOUT_EVENTS = LangUtils.unmodifiableList(
        "accesskey",
        "alt",
        "dir",
        "label",
        "lang",
        "style",
        "tabindex",
        "title",
        "type"
    );

    //disabled, readonly, style, styleClass handles by component renderer
    public static final List<String> INPUT_TEXT_ATTRS_WITHOUT_EVENTS = LangUtils.unmodifiableList(
        "accesskey",
        "alt",
        "autocomplete",
        "dir",
        "lang",
        "maxlength",
        "placeholder",
        "inputmode",
        "size",
        "tabindex",
        "title"
    );

    public static final List<String> SELECT_ATTRS_WITHOUT_EVENTS = LangUtils.unmodifiableList(
        "accesskey",
        "dir",
        "disabled",
        "lang",
        "readonly",
        "style",
        "tabindex",
        "title"
    );

    public static final List<String> TEXTAREA_ATTRS_WITHOUT_EVENTS = LangUtils.unmodifiableList(
        "cols",
        "rows",
        "accesskey",
        "alt",
        "autocomplete",
        "placeholder",
        "inputmode",
        "dir",
        "lang",
        "size",
        "tabindex",
        "title",
        "maxlength"
    );

    //StyleClass is omitted
    public static final List<String> LABEL_ATTRS_WITHOUT_EVENTS = LangUtils.unmodifiableList(
        "accesskey",
        "dir",
        "lang",
        "style",
        "tabindex",
        "title"
    );

    public static final List<String> BODY_ATTRS = LangUtils.concat(COMMON_EVENTS_WITHOUT_CLICK, CLICK_EVENT, LangUtils.unmodifiableList(
        "dir",
        "lang",
        "style",
        "title",
        "onload",
        "onunload"
    ));

    public static final List<String> MEDIA_ATTRS = LangUtils.unmodifiableList(
        "autoplay",
        "controls",
        "crossOrigin",
        "currentTime",
        "defaultMuted",
        "defaultPlaybackRate",
        "loop",
        "muted",
        "playbackRate",
        "preload",
        "volume"
    );

    public static final List<String> MEDIA_EVENTS = LangUtils.unmodifiableList(
        "onabort",
        "oncanplay",
        "oncanplaythrough",
        "ondurationchange",
        "onemptied",
        "onended",
        "onerror",
        "onloadeddata",
        "onloadedmetadata",
        "onloadstart",
        "onpause",
        "onplay",
        "onplaying",
        "onprogress",
        "onratechange",
        "onseeked",
        "onseeking",
        "onstalled",
        "onsuspend",
        "ontimeupdate",
        "onvolumechange",
        "onwaiting"
    );

    public static final List<String> COMMON_EVENTS = LangUtils.concat(COMMON_EVENTS_WITHOUT_CLICK, CLICK_EVENT);

    public static final List<String> OUTPUT_EVENTS = LangUtils.concat(COMMON_EVENTS, BLUR_FOCUS_EVENTS);

    public static final List<String> OUTPUT_EVENTS_WITHOUT_CLICK = LangUtils.concat(COMMON_EVENTS_WITHOUT_CLICK, BLUR_FOCUS_EVENTS);

    public static final List<String> BUTTON_EVENTS = LangUtils.concat(OUTPUT_EVENTS, CHANGE_SELECT_EVENTS);

    public static final List<String> BUTTON_EVENTS_WITHOUT_CLICK = LangUtils.concat(OUTPUT_EVENTS_WITHOUT_CLICK, CHANGE_SELECT_EVENTS);

    public static final List<String> IMG_ATTRS = LangUtils.concat(IMG_ATTRS_WITHOUT_EVENTS, COMMON_EVENTS);

    public static final List<String> LINK_ATTRS = LangUtils.concat(LINK_ATTRS_WITHOUT_EVENTS, OUTPUT_EVENTS);

    public static final List<String> LABEL_ATTRS = LangUtils.concat(LABEL_ATTRS_WITHOUT_EVENTS, OUTPUT_EVENTS);
    public static final List<String> LABEL_EVENTS = OUTPUT_EVENTS;

    public static final List<String> BUTTON_ATTRS = LangUtils.concat(BUTTON_ATTRS_WITHOUT_EVENTS, BUTTON_EVENTS);
    public static final List<String> BUTTON_WITH_CLICK_ATTRS = LangUtils.concat(HTML.BUTTON_ATTRS, HTML.CLICK_EVENT);

    public static final List<String> INPUT_TEXT_EVENTS = LangUtils.concat(COMMON_EVENTS, CHANGE_SELECT_EVENTS, BLUR_FOCUS_EVENTS);
    public static final List<String> INPUT_TEXT_ATTRS = LangUtils.concat(INPUT_TEXT_ATTRS_WITHOUT_EVENTS, INPUT_TEXT_EVENTS);

    public static final List<String> INPUT_TEXTAREA_ATTRS = LangUtils.concat(
            TEXTAREA_ATTRS_WITHOUT_EVENTS, COMMON_EVENTS, CHANGE_SELECT_EVENTS, BLUR_FOCUS_EVENTS);

    public static final List<String> SELECT_ATTRS = LangUtils.concat(
            SELECT_ATTRS_WITHOUT_EVENTS, COMMON_EVENTS, CHANGE_SELECT_EVENTS, BLUR_FOCUS_EVENTS);

    public static final List<String> MEDIA_ATTRS_WITH_EVENTS = LangUtils.concat(MEDIA_ATTRS, MEDIA_EVENTS);

    public static final String ARIA_ACTIVEDESCENDANT = "aria-activedescendant";
    public static final String ARIA_ATOMIC = "aria-atomic";
    public static final String ARIA_AUTOCOMPLETE = "aria-autocomplete";
    public static final String ARIA_CHECKED = "aria-checked";
    public static final String ARIA_CONTROLS = "aria-controls";
    public static final String ARIA_DESCRIBEDBY = "aria-describedby";
    public static final String ARIA_DISABLED = "aria-disabled";
    public static final String ARIA_EXPANDED = "aria-expanded";
    public static final String ARIA_HASPOPUP = "aria-haspopup";
    public static final String ARIA_HIDDEN = "aria-hidden";
    public static final String ARIA_INVALID = "aria-invalid";
    public static final String ARIA_LABEL = "aria-label";
    public static final String ARIA_LABELLEDBY = "aria-labelledby";
    public static final String ARIA_LIVE = "aria-live";
    public static final String ARIA_MULITSELECTABLE = "aria-multiselectable";
    public static final String ARIA_OWNS = "aria-owns";
    public static final String ARIA_READONLY = "aria-readonly";
    public static final String ARIA_REQUIRED = "aria-required";
    public static final String ARIA_SELECTED = "aria-selected";
    public static final String ARIA_ROLE = "role";
    public static final String ARIA_ROLE_NONE = "none";
    public static final String ARIA_ROLE_MENU = "menu";
    public static final String ARIA_ROLE_MENUITEM = "menuitem";
    public static final String ARIA_ROLE_MENUBAR = "menubar";

    public static final String BUTTON_TEXT_ONLY_BUTTON_CLASS = "ui-button ui-widget ui-state-default ui-corner-all ui-button-text-only";
    public static final String BUTTON_ICON_ONLY_BUTTON_CLASS = "ui-button ui-widget ui-state-default ui-corner-all ui-button-icon-only";
    public static final String BUTTON_TEXT_ICON_LEFT_BUTTON_CLASS = "ui-button ui-widget ui-state-default ui-corner-all ui-button-text-icon-left";
    public static final String BUTTON_TEXT_ICON_RIGHT_BUTTON_CLASS = "ui-button ui-widget ui-state-default ui-corner-all ui-button-text-icon-right";
    public static final String BUTTON_LEFT_ICON_CLASS = "ui-button-icon-left ui-icon ui-c";
    public static final String BUTTON_RIGHT_ICON_CLASS = "ui-button-icon-right ui-icon ui-c";
    public static final String BUTTON_TEXT_CLASS = "ui-button-text ui-c";
    public static final String BUTTON_TEXT_ONLY_BUTTON_FLAT_CLASS = "ui-button ui-widget ui-state-default ui-button-text-only";

    public static final String CHECKBOX_ALL_CLASS = "ui-chkbox ui-chkbox-all ui-widget";
    public static final String CHECKBOX_CLASS = "ui-chkbox ui-widget";
    public static final String CHECKBOX_BOX_CLASS = "ui-chkbox-box ui-widget ui-corner-all ui-state-default";
    public static final String CHECKBOX_INPUT_WRAPPER_CLASS = "ui-helper-hidden-accessible";
    public static final String CHECKBOX_UNCHECKED_ICON_CLASS = "ui-chkbox-icon ui-icon ui-icon-blank ui-c";
    public static final String CHECKBOX_CHECKED_ICON_CLASS = "ui-chkbox-icon ui-icon ui-icon-check ui-c";
    public static final String CHECKBOX_PARTIAL_CHECKED_ICON_CLASS = "ui-chkbox-icon ui-icon ui-icon-minus ui-c";
    public static final String CHECKBOX_LABEL_CLASS = "ui-chkbox-label";

    public static final String RADIOBUTTON_CLASS = "ui-radiobutton ui-widget";
    public static final String RADIOBUTTON_NATIVE_CLASS = "ui-radiobutton ui-radiobutton-native ui-widget";
    public static final String RADIOBUTTON_BOX_CLASS = "ui-radiobutton-box ui-widget ui-corner-all ui-state-default";
    public static final String RADIOBUTTON_INPUT_WRAPPER_CLASS = "ui-helper-hidden";
    public static final String RADIOBUTTON_UNCHECKED_ICON_CLASS = "ui-radiobutton-icon ui-icon ui-icon-blank ui-c";
    public static final String RADIOBUTTON_CHECKED_ICON_CLASS = "ui-radiobutton-icon ui-icon ui-icon-bullet ui-c";

    public static final String WIDGET_VAR = "data-widget";

    public static class ValidationMetadata {

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

        private ValidationMetadata() {

        }
    }

    private HTML() {
    }
}
