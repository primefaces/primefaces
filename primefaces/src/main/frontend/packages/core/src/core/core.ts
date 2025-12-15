import Cookies from "js-cookie";

import { ajax, type Ajax } from "./core.ajax.js";
import { csp, type Csp } from "./core.csp.js";
import { clientwindow, type ClientWindow } from "./core.clientwindow.js";
import { env, type Environment } from "./core.env.js";
import { expressions, type Expressions } from "./core.expressions.js";
import { resources, type Resources } from "./core.resources.js";
import { utils, type Utils } from "./core.utils.js";

import { BaseWidget, DeferredWidget, DynamicOverlayWidget, type BaseWidgetCfg } from "./core.widget.js";
import { AjaxExceptionHandler } from "../ajaxexceptionhandler/ajaxexceptionhandler.js";
import { AjaxStatus } from "../ajaxstatus/ajaxstatus.js";
import { Poll } from "../poll/poll.js";

import { validation, type Validation } from "../validation/validation.common.js";
import { validationHighlighter } from "../validation/validation.highlighters.js";

declare global {
    interface Window {
        downloadMonitor?: number | undefined;
    }
}

type HookMap = {[P in keyof PrimeType.HookRegistry]: Set<PrimeType.HookRegistry[P]>};

const LocaleEnUs: PrimeType.Locale = {
    "accept": "Yes",
    "addRule": "Add Rule",
    "am": "AM",
    "apply": "Apply",
    "cancel": "Cancel",
    "choose": "Choose",
    "chooseDate": "Choose Date",
    "chooseMonth": "Choose Month",
    "chooseYear": "Choose Year",
    "clear": "Clear",
    "completed": "Completed",
    "contains": "Contains",
    "custom": "Custom",
    "dateAfter": "Date is after",
    "dateBefore": "Date is before",
    "dateFormat": "mm/dd/yy",
    "dateIs": "Date is",
    "dateIsNot": "Date is not",
    "dayNames": ["Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"],
    "dayNamesMin": ["Su", "Mo", "Tu", "We", "Th", "Fr", "Sa"],
    "dayNamesShort": ["Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"],
    "emptyFilterMessage": "No results found",
    "emptyMessage": "No available options",
    "emptySearchMessage": "No results found",
    "emptySelectionMessage": "No selected item",
    "endsWith": "Ends with",
    "equals": "Equals",
    "fileSizeTypes": ["B", "KB", "MB", "GB", "TB", "PB", "EB", "ZB", "YB"],
    "filter": "Filter",
    "firstDayOfWeek": 0,
    "gt": "Greater than",
    "gte": "Greater than or equal to",
    "lt": "Less than",
    "lte": "Less than or equal to",
    "matchAll": "Match All",
    "matchAny": "Match Any",
    "medium": "Medium",
    "monthNames": ["January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"],
    "monthNamesShort": ["Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"],
    "nextDecade": "Next Decade",
    "nextHour": "Next Hour",
    "nextMinute": "Next Minute",
    "nextMonth": "Next Month",
    "nextSecond": "Next Second",
    "nextYear": "Next Year",
    "noFilter": "No Filter",
    "notContains": "Not contains",
    "notEquals": "Not equals",
    "now": "Now",
    "passwordPrompt": "Enter a password",
    "pending": "Pending",
    "pm": "PM",
    "prevDecade": "Previous Decade",
    "prevHour": "Previous Hour",
    "prevMinute": "Previous Minute",
    "prevMonth": "Previous Month",
    "prevSecond": "Previous Second",
    "prevYear": "Previous Year",
    "reject": "No",
    "removeRule": "Remove Rule",
    "searchMessage": "{0} results are available",
    "selectionMessage": "{0} items selected",
    "showMonthAfterYear": false,
    "startsWith": "Starts with",
    "strong": "Strong",
    "today": "Today",
    "upload": "Upload",
    "weak": "Weak",
    "weekHeader": "Wk",
    "weekNumberTitle": "W",
    "isRTL": false,
    "yearSuffix": "",
    "timeOnlyTitle": "Only Time",
    "timeText": "Time",
    "hourText": "Hour",
    "minuteText": "Minute",
    "secondText": "Second",
    "millisecondText": "Millisecond",
    "year": "Year",
    "month": "Month",
    "week": "Week",
    "day": "Day",
    "list": "Agenda",
    "allDayText": "All Day",
    "moreLinkText": "More...",
    "noEventsText": "No Events",
    "unexpectedError": "Unexpected error",
    "aria": {
        "cancelEdit": "Cancel Edit",
        "close": "Close",
        "collapseLabel": "Collapse",
        "collapseRow": "Row Collapsed",
        "editRow": "Edit Row",
        "expandLabel": "Expand",
        "expandRow": "Row Expanded",
        "falseLabel": "False",
        "filterConstraint": "Filter Constraint",
        "filterOperator": "Filter Operator",
        "firstPageLabel": "First Page",
        "gridView": "Grid View",
        "hideFilterMenu": "Hide Filter Menu",
        "jumpToPageDropdownLabel": "Jump to Page Dropdown",
        "jumpToPageInputLabel": "Jump to Page Input",
        "lastPageLabel": "Last Page",
        "listLabel": "Options List",
        "listView": "List View",
        "maximizeLabel": "Maximize",
        "minimizeLabel": "Minimize",
        "moveAllToSource": "Move All to Source",
        "moveAllToTarget": "Move All to Target",
        "moveBottom": "Move Bottom",
        "moveDown": "Move Down",
        "moveToSource": "Move to Source",
        "moveToTarget": "Move to Target",
        "moveTop": "Move Top",
        "moveUp": "Move Up",
        "navigation": "Navigation",
        "next": "Next",
        "nextPageLabel": "Next Page",
        "nullLabel": "Not Selected",
        "pageLabel": "Page {page}",
        "otpLabel": "Please enter one time password character {0}",
        "passwordHide": "Hide Password",
        "passwordShow": "Show Password",
        "previous": "Previous",
        "prevPageLabel": "Previous Page",
        "rotateLeft": "Rotate Left",
        "rotateRight": "Rotate Right",
        "rowsPerPageLabel": "Rows per page",
        "saveEdit": "Save Edit",
        "scrollTop": "Scroll Top",
        "selectAll": "All items selected",
        "selectColor": "Select Color",
        "selectLabel": "Select",
        "selectRow": "Row Selected",
        "showFilterMenu": "Show Filter Menu",
        "slide": "Slide",
        "slideNumber": "{slideNumber}",
        "star": "1 star",
        "stars": "{star} stars",
        "trueLabel": "True",
        "unselectAll": "All items unselected",
        "unselectLabel": "Unselect",
        "unselectRow": "Row Unselected",
        "zoomImage": "Zoom Image",
        "zoomIn": "Zoom In",
        "zoomOut": "Zoom Out",
        "datatable.sort.ASC": "activate to sort column ascending",
        "datatable.sort.DESC": "activate to sort column descending",
        "datatable.sort.NONE": "activate to remove sorting on column",
        "colorpicker.OPEN": "Open color picker",
        "colorpicker.CLOSE": "Close color picker",
        "colorpicker.CLEAR": "Clear the selected color",
        "colorpicker.MARKER": "Saturation: {s}. Brightness: {v}.",
        "colorpicker.HUESLIDER": "Hue slider",
        "colorpicker.ALPHASLIDER": "Opacity slider",
        "colorpicker.INPUT": "Color value field",
        "colorpicker.FORMAT": "Color format",
        "colorpicker.SWATCH": "Color swatch",
        "colorpicker.INSTRUCTION": "Saturation and brightness selector. Use up, down, left and right arrow keys to select.",
        "spinner.INCREASE": "Increase Value",
        "spinner.DECREASE": "Decrease Value",
        "switch.ON": "On",
        "switch.OFF": "Off",
        "messages.ERROR": "Error",
        "messages.FATAL": "Fatal",
        "messages.INFO": "Information",
        "messages.WARN": "Warning"
    },
};

function toArticulateValidationConfiguration(cfg: PrimeType.validation.ShorthandConfiguration): PrimeType.validation.Configuration {
    for (var option in cfg) {
        if (!cfg.hasOwnProperty(option)) {
            continue;
        }

        // just pass though if no mapping is available
        if (validation.CFG_SHORTCUTS[option]) {
            // @ts-expect-error Can't really make renaming type safe, unless we spell out each key and create a new object
            cfg[validation.CFG_SHORTCUTS[option]] = cfg[option];
            // @ts-expect-error Can't really make renaming type safe, unless we spell out each key and create a new object
            delete cfg[option];
        }
    }

    return cfg as unknown as PrimeType.validation.Configuration;
}


/**
 * This is the main global object for accessing the client-side API of PrimeFaces. Broadly speaking, it consists
 * of the following entries:
 *
 * - {@link Ajax | PrimeFaces.ajax} The AJAX module with functionality for sending AJAX requests
 * - {@link ClientWindow | PrimeFaces.clientwindow} The client window module for multiple window support in PrimeFaces applications.
 * - {@link Csp | PrimeFaces.csp} The  CSP module for the HTTP Content-Security-Policy (CSP) policy `script-src` directive.
 * - `PrimeFaces.dialog` The dialog module with functionality related to the dialog framework
 * - {@link Environment | PrimeFaces.env} The environment module with information about the current browser
 * - {@link Expressions | PrimeFaces.expressions} The search expressions module with functionality for working with search expression
 * - {@link Resources | PrimeFaces.resources} The resources module with functionality for creating resource links
 * - {@link Utils | PrimeFaces.utils} The utility module with functionality that does not fit anywhere else
 * - {{@link widget | PrimeFaces.widget} The registry with all available widget classes
 * - {@link widgets | PrimeFaces.widgets} The registry with all currently instantiated widgets
 * - Several other utility methods defined directly on the `PrimeFaces` object, such as
 * {@link getWidgetById | PrimeFaces.getWidgetById}, or {@link escapeHTML | PrimeFaces.escapeHTML}.
 */
export class Core {
    /**
     * A tracker for the current z-index, used for example when creating multiple modal dialogs.
     */
    zindex: number = 1000;

    /**
     * Global flag for enabling or disabling both jQuery and CSS animations.
     */
    animationEnabled: boolean = true;

    /**
     * Flag for detecting whether animation is currently running. Similar to jQuery.active flag and is useful
     * for scripts or automation tests to determine if the animation is currently running.
     */
    animationActive: boolean = false;

    /**
     * Used to store whether a custom focus has been rendered. This avoids having to retain the last focused element
     * after AJAX update.
     */
    customFocus: boolean = false;

    /**
     * PrimeFaces per defaults hides all overlays on scrolling/resizing to avoid positioning problems.
     * This is really hard to overcome in selenium tests and we can disable this behavior with this setting.
     */
    hideOverlaysOnViewportChange: boolean = true;

    /**
     * A map between some HTML entities and their HTML-escaped equivalent.
     */
    entityMap: PrimeType.EntityMap = {
        '&': '&amp;',
        '<': '&lt;',
        '>': '&gt;',
        '"': '&quot;',
        "'": '&#39;',
        '/': '&#x2F;',
        '`': '&#x60;',
        '=': '&#x3D;'
    };

    private hooks: Partial<HookMap> = {};

    /**
     * Registry with the client-side implementation of some faces converters. The
     * key is the name of the converter, e.g. `jakarta.faces.Length`, the value is
     * the converter implementation.
     */
    converter: Record<string, PrimeType.validation.Converter> = {};

    /**
     * Registry with the client-side implementation of some faces validators.
     * Used for implementing client-side validation for quick feedback. The
     * key is the name of the validator, e.g. `jakarta.faces.LongRange`, the value
     * is the validator implementation.
     */
    validator: PrimeType.validation.ValidatorInstanceMap = {
        Highlighter: validationHighlighter,
    };

    /**
     * This object contains the  widget classes that are currently available. The key is the name of the widget, the
     * value the class (constructor) of the widget. Please note that widgets are usually created by the PrimeFaces
     * framework and should not be created manually.
     *
     * There are a few base classes defined by PrimeFaces that you can use when writing the client-side part of your
     * custom widget:
     *
     * - {@link BaseWidget | PrimeFaces.widget.BaseWidget}: Base class that you should extend if you do not require any
     * advanced functionality.
     * - {@link DeferredWidget | PrimeFaces.widget.DeferredWidget}: When you widget needs to be initialized on the client
     * in a way that requires the element to be visible, you can use this class as a base. A widget may not be visible,
     * for example, when it is inside a dialog or tab. The deferred widget provides the method 
     * {@link DeferredWidget.addDeferredRender | addDeferredRender} (to register a listener) and 
     * {@link DeferredWidget.renderDeferred | renderDeferred} (to render the widget once it is visible).
     * - {@link DynamicOverlayWidget | PrimeFaces.widget.DynamicOverlayWidget}: When your widget is an overlay with
     * dynamically loaded content, you can use this base class.
     */
    widget: PrimeType.WidgetRegistry  = {
        AjaxExceptionHandler,
        AjaxStatus,
        BaseWidget,
        DeferredWidget,
        DynamicOverlayWidget,
        Poll,
    };

    /**
     * A map of all instantiated widgets that are available on the current page.
     * The key is the widget variable of the widget, the value the instantiated widget.
     */
    widgets: PrimeType.WidgetInstanceMap = {};

    /**
     * A list of widget variable IDs (widgetVar) that were once instantiated, but are now removed from the DOM, such as
     * due to the result of an AJAX update request.
     */
    detachedWidgets: string[] = [];

    /**
     * Some widgets need to compute their dimensions based on their parent element(s). This requires that such
     * widgets are not rendered until they have become visible. A widget may not be visible, for example, when it
     * is inside a tab that is not shown when the page is rendered. PrimeFaces provides a global mechanism for
     * widgets to render once they are visible. This is done by keeping a list of widgets that need to be rendered,
     * and checking on every change (AJAX request, tab change etc.) whether any of those have become visible. A
     * widgets should extend `PrimeFaces.widget.DeferredWidget` to make use of this functionality.
     *
     * This is the list of renders for widgets that are currently waiting to become visible.
     */
    deferredRenders: PrimeType.DeferredRender[]= [];

    /**
     * A map with language specific translations. This is a map between the language keys and another map with the i18n
     * keys mapped to the translation.
     */
    locales: PrimeType.Locales = {
        en_US: LocaleEnUs,
        en: LocaleEnUs,
    };

    /**
     * Custom logger for logging messages. Defaults to logging to the console
     * when not given.
     */
    logger: PrimeType.Logger | undefined;

    /**
     * Settings provided by the server. Uses defaults when the core instance is
     * initialized, should have been changed to the settings from the server
     * before the document is ready.
     */
    settings: PrimeType.PFSettings = {
        considerEmptyStringNull: false,
        contextPath: "/",
        cookiesSecure: false,
        locale: "en",
        validateEmptyFields: false,
        viewId: "",
    };

    /**
     * The object with functionality related to sending and receiving AJAX requests that are made by PrimeFaces. Each
     * request receives an XML response, which consists of one or multiple actions that are to be performed. This
     * includes creating new DOM elements, deleting or updating existing elements, or executing some JavaScript.
     */
    readonly ajax: Ajax = ajax;

    /**
     * The object with functionality related to handling the `script-src` directive of the HTTP `Content-Security-Policy`
     * (CSP) policy. This makes use of a nonce (number used once). The server must generate a unique nonce value each
     * time it transmits a policy. 
     */
    readonly csp: Csp = csp;

    /**
     * The object with functionality related to multiple window support in PrimeFaces applications.
     */
    readonly clientwindow: ClientWindow = clientwindow;

    /**
     * The object with functionality related to the browser environment, such as information about the current browser.
     */
    readonly env: Environment = env;

    /**
     * The object providing the entry point for functions related to search expressions. 
     */
    readonly expressions: Expressions = expressions;

    /**
     * The object with functionality related to handling resources on the server, such as CSS and JavaScript files.
     */
    readonly resources: Resources = resources;

    /**
     * The object with various utility methods needed by PrimeFaces.
     */
    readonly utils: Utils = utils;

    /**
     * __PrimeFaces Client Side Validation Framework__
     * 
     * The object for enabling client side validation of form fields.
     */
    readonly validation: Validation = validation;

    private debounceTimer: number | undefined = undefined;

    private localeSettings: PrimeType.Locale | undefined;

    private scrollbarWidth: number | undefined;

    /**
     * Name of the POST parameter that indicates whether the request is an AJAX request.
     */
    readonly PARTIAL_REQUEST_PARAM: string = "jakarta.faces.partial.ajax";

    /**
     * Name of the POST parameter that contains the list of components to be updated.
     */
    readonly PARTIAL_UPDATE_PARAM: string = "jakarta.faces.partial.render";

    /**
     * Name of the POST parameter that contains the list of components to process.
     */
    readonly PARTIAL_PROCESS_PARAM: string = "jakarta.faces.partial.execute";

    /**
     * Name of the POST parameter that indicates which element or component triggered the AJAX request.
     */
    readonly PARTIAL_SOURCE_PARAM: string = "jakarta.faces.source";

    /**
     * Name of the POST parameter that contains the name of the current behavior event.
     */
    readonly BEHAVIOR_EVENT_PARAM: string = "jakarta.faces.behavior.event";

    /**
     * Name of the POST parameter that contains the name of the current partial behavior event.
     */
    readonly PARTIAL_EVENT_PARAM: string = "jakarta.faces.partial.event";

    /**
     * Name of the POST parameter that indicates whether forms should have their values reset.
     */
    readonly RESET_VALUES_PARAM: string = "jakarta.faces.partial.resetValues";

    /**
     * Name of the POST parameter that indicates whether `<p:autoUpdate>` tags should be ignored.
     */
    readonly IGNORE_AUTO_UPDATE_PARAM: string = "primefaces.ignoreautoupdate";

    /**
     * Name of the POST parameter that indicates whether children should be skipped.
     */
    readonly SKIP_CHILDREN_PARAM: string = "primefaces.skipchildren";

    /**
     * Name of the POST parameter that contains the current view state.
     */
    readonly VIEW_STATE: string = "jakarta.faces.ViewState";

    /**
     * Name of the POST parameter with the current client window.
     */
    readonly CLIENT_WINDOW: string = "jakarta.faces.ClientWindow";

    /**
     * Name of the POST parameter that contains the view root.
     */
    readonly VIEW_ROOT: string = "jakarta.faces.ViewRoot";

    /**
     * Name of the POST parameter with the current client ID
     */
    readonly CLIENT_ID_DATA: string = "primefaces.clientid";

    /**
     * Name of the faces resource servlet, eg. `jakarta.faces.resource`.
     */
    readonly RESOURCE_IDENTIFIER: string = 'jakarta.faces.resource';

    /**
     * The current version of PrimeFaces.
     */
    readonly VERSION: string = '${project.version}';


    /**
     * A shortcut for {@link AjaxRequest.handle | PrimeFaces.ajax.Request.handle(cfg, ext)}, with shorter option names. Sends an AJAX request to
     * the server and processes the response. You can use this method if you need more fine-grained control over which
     * components you want to update or process, or if you need to change some other AJAX options.
     * @param cfg Configuration for the AJAX request, with shorthand
     * options. The individual options are documented in {@link PrimeType.ajax.Configuration}.
     * @param ext Optional extender with additional options that
     * overwrite the options given in `cfg`.
     * @return A promise that resolves once the AJAX requests is done. Use this
     * to run custom JavaScript logic. When the AJAX request succeeds, the promise is fulfilled. Otherwise, when the
     * AJAX request fails, the promise is rejected. If the promise is rejected, the rejection handler receives an object
     * of type {@link PrimeType.ajax.FailedRequestData}.
     */
    ab(
        cfg: PrimeType.ajax.ShorthandConfiguration,
        ext?: PrimeType.ajax.ConfigurationExtender
    ): PromiseLike<PrimeType.ajax.ResponseData> {
        for (var option in cfg) {
            if (!cfg.hasOwnProperty(option)) {
                continue;
            }
    
            // just pass though if no mapping is available
            // @ts-expect-error
            if (ajax.CFG_SHORTCUTS[option]) {
                // @ts-expect-error
                cfg[ajax.CFG_SHORTCUTS[option]] = cfg[option];
                // @ts-expect-error
                delete cfg[option];
            }
        }
    
        return ajax.Request.handle(cfg, ext);
    }

    /**
     * Shortcut for is this CMD on MacOs or CTRL key on other OSes. 
     * @deprecated Use {@link Utils.isMetaKey | PrimeFaces.utils.isMetaKey}
     * @param e The key event that occurred.
     * @return `true` if the key is a meta key, `false` or `undefined` otherwise.
     */
    metaKey(e: JQuery.TriggeredEvent): boolean | undefined {
        return utils.isMetaKey(e);
    }

    /**
     * A shortcut for `PrimeFaces.validation.validate` used by server-side renderers.
     * If the `ajax` attribute is set to `true` (the default is `false`), all inputs configured by the `process` attribute are validated
     * and all messages for the inputs configured by the `update` attribute are rendered.
     * Otherwise, if the `ajax` attribute is set to the `false`, all inputs of the parent form, of the `source` attribute, are processed and updated.
     * @param cfg An configuration.
     * @return `true` if the request would not result in validation errors, or `false` otherwise.
     */
    vb(cfg: PrimeType.validation.ShorthandConfiguration): boolean {
        const config = toArticulateValidationConfiguration(cfg);

        const highlight = config.highlight || true;
        const focus = config.focus || true;
        const renderMessages = config.renderMessages || true;
        const validateInvisibleElements = config.validateInvisibleElements || false;
        const logUnrenderedMessages = config.logUnrenderedMessages || renderMessages;

        const $source = utils.toJQuery(config.source);

        const process = validation.Utils.resolveProcess(config, $source);
        const update = validation.Utils.resolveUpdate(config, $source);

        const result = validation.validate($source, process, update, highlight, focus, renderMessages, validateInvisibleElements, logUnrenderedMessages);
        return result.valid;
    }

    /**
     * A shortcut for `PrimeFaces.validation.validateInstant`. This is used by `p:clientValidator`.
     * @param element The ID of an element to validate, or the element itself.
     * @param highlight If the invalid element should be highlighted.
     * @param renderMessages If messages should be rendered.
     * @returns `true` if the element is valid, or `false` otherwise.
     */
    vi(element: string | HTMLElement | JQuery, highlight: boolean, renderMessages: boolean): boolean {
        return validation.validateInstant(element, highlight, renderMessages);
    }
    
    /**
     * Creates an ID to a CSS ID selector that matches elements with that ID. For example:
     * ```
     * PrimeFaces.escapeClientId("form:input"); // => "#form\\:input"
     * PrimeFaces.escapeClientId("form#input"); // => "#form#input"
     * PrimeFaces.escapeClientId("_5e08119c-7a6e-4fad-8061-51208ade4c4f|2"); // => "#_5e08119c-7a6e-4fad-8061-51208ade4c4f\\|2"
     * ```
     *
     * __Please note that this method does not escape all characters that need to be escaped and will not work with arbitrary IDs__
     * @param id ID to convert.
     * @return A CSS ID selector for the given ID.
     */
    escapeClientId(id: string): string {
        // If the id already starts with '#', remove it to avoid double-escaping, but keep escaping all chars.
        const rawId = id.startsWith("#") ? id.substring(1) : id;
        // Escape ':' and '|' for jQuery usage.
        return "#" + rawId.replace(/([:|])/g, "\\$1");
    }

    /**
     * Registers a listener that will be called as soon as the given element was loaded completely. Please note the
     * listener may be called synchronously (immediately) or asynchronously, depending on whether the element is
     * already loaded.
     * @param element Element to wait for
     * @param listener Listener to call once the element is loaded
     */
    onElementLoad(element: JQuery, listener: () => void): void {
        if (element.prop('complete')) {
            listener();
        }
        else {
            element.on('load', listener);
        }
    }

    /**
     * Finds a registered validator by its ID, if such a validator exists.
     * @param id ID of the validator.
     * @returns The validator with the given ID, or `undefined` if no such
     * validator exists.
     */
    getValidatorById(id: string): PrimeType.validation.Validator | undefined {
        return id !== "Highlighter" 
            ? this.validator[id as Exclude<keyof PrimeType.validation.ValidatorInstanceMap, "Highlighter">] 
            : undefined;
    }

    /**
     * Finds a widget in the current page with the given ID.
     * @param id ID of the widget to retrieve.
     * @return The widget with the given ID, of `null` if no such widget was
     * found.
     */
    getWidgetById(id: string): BaseWidget | null {
        for (var widgetVar in this.widgets) {
            var widget = this.widgets[widgetVar];
            if (widget && widget.id === id) {
                return widget;
            }
        }

        return null;
    }

    /**
     * Finds all widgets in the current page that are of the given type.
     * @typeParam Widget Type of the widgets of interest, e.g. `PrimeFaces.widget.DataTable`.
     * @param type The type (constructor) of the widgets of interest, e.g. `PrimeFaces.widget.DataTable`.
     * @return An array of widgets that are of the requested type. If no suitable widgets
     * are found on the current page, an empty array will be returned.
     */
    getWidgetsByType<Widget extends PrimeType.AnyNewable>(type: Widget): InstanceType<Widget>[] {
        return $.map(this.widgets, (widget) => {
            return widget instanceof type ? widget as InstanceType<Widget> : null;
        });
    }

    /**
     * Finds all widgets in the current page that are of the given type.
     * @typeParam TypeName Type of the widgets of interest, e.g. `DataTable`.
     * @param type The type (name) of the widgets of interest, e.g. `DataTable`.
     * @return An array of widgets that are of the requested type. If no suitable widgets
     * are found on the current page, an empty array will be returned.
     */
    getWidgetsByTypeName<
        TypeName extends keyof PrimeType.WidgetRegistry,
    >(type: TypeName): InstanceType<PrimeType.WidgetRegistry[TypeName]>[] {
        const widgetType = this.getWidgetTypeByName(type);
        return widgetType !== undefined ? this.getWidgetsByType(widgetType) : [];
    }

    /**
     * Finds the type of the widget with the given name. Returns undefined when
     * no such widget was loaded and registered yet.
     * @typeParam TypeName Type name of the widget of interest, e.g. `DataTable`.
     * @param type The type name of the widget of interest, e.g. `DataTable`.
     * @return The type of the widget with the given name. If no suitable widgets
     * are found on the current page, returns undefined.
     */
    getWidgetTypeByName<
        TypeName extends keyof PrimeType.WidgetRegistry,
    >(type: TypeName): PrimeType.WidgetRegistry[TypeName] | undefined {
        return this.widget[type];
    }

    /**
     * Checks if the widget class with the given name was loaded and registered,
     * and, if it was, the given widget is an instance of that class.
     * 
     * If possible, simply check against `PrimeFaces.widget.<name>` if you are
     * certain the widget class was loaded already.
     * @param widget A widget to check.
     * @param typeName The name of a widget class to check against, e.g. `DataTable`.
     * @returns If the widget is an instance of the given widget type. Returns false
     * when the widget class was not yet loaded or registered (in which case you
     * could not possibly have an instance of it...)
     */
    isWidgetOfTypeName(widget: BaseWidget, typeName: string): boolean {
        const type = (this.widget as unknown as Record<string, typeof BaseWidget<any>>)[typeName];
        return type !== undefined ? widget instanceof type : false;
    }

    /**
     * Resolves the given target as a JQuery instance. The target may be one of the
     * following:
     * 
     * - ID of an element: returns the first element with that ID, or an empty
     *   JQuery instance otherwise.
     * - JQuery instance: returns that instance unchanged.
     * - HTML element: returns that element wrapped in a JQuery instance
     * - Widget instance: Returns the main element ({@link BaseWidget.getJQ}) of
     *   the widget. 
     *
     * @param target Either an ID, an HTML element or jQuery instance, or a PrimeFaces
     * widget.
     * @return The resolved JQuery instance.
     */
    resolveAs$(target: string | HTMLElement | JQuery | BaseWidget): JQuery {
        if (target instanceof BaseWidget) {
            return target.getJQ();
        }
        else if (target instanceof $) {
            return target as JQuery;
        }
        else if (target instanceof HTMLElement) {
            return $(target);
        }
        else if (typeof target === 'string') {
            const element = document.getElementById(target);
            return element !== null ? $(element) : $();
        }

        throw new Error("Unsupported type: " + (typeof target));
    }

    /**
     * Resolves the given target element reference to an element ID. Returns an empty
     * string if the target does not exist or when it does not have an ID. The target
     * may be one of the following:
     * 
     * - ID of an element: uses the first element with that ID
     * - jQuery instance: uses the first element of the jQuery instance
     * - HTML element: uses that element
     * - Widget instance: uses the base element ({@link BaseWidget.getJQ}) of the widget
     *
     * @param target Either an ID, an HTML element, a jQuery instance, or a
     * PrimeFaces widget.
     * @return The ID of the target.
     */
    resolveAsId(target: string | HTMLElement | JQuery | BaseWidget): string {
        if (target instanceof BaseWidget) {
            var widgetJq = target.getJQ();
            return widgetJq.data(this.CLIENT_ID_DATA) || widgetJq.attr('id');
        }
        else if (target instanceof $) {
            const $target = target as JQuery;
            return $target.data(this.CLIENT_ID_DATA)||$target.attr('id');
        }
        else if (target instanceof HTMLElement) {
            return target.id;
        }
        else if (typeof target === 'string') {
            return target;
        }

        throw new Error("Unsupported type: " + (typeof target));
    }

    /**
     * Gets the form by id or the closest form if the id is not a form itself.
     * In AJAX we also have a fallback for the first form in DOM, this should not be used here.
     *
     * @param id ID of the component to get the closest form or if its a form itself
     * @return The form if found, or an empty JQuery instance otherwise.
     */
    getClosestForm(id: string): JQuery {
        var form = $(this.escapeClientId(id));
        if (!form.is('form')) {
            form = form.closest('form');
        }
        if (form.length === 0) {
            this.error('Form element could not be found for id: ' + id);
        }
        return form;
    }

    /**
     * Adds hidden input elements to the given form. For each key-value pair, a new hidden input element is created
     * with the given value and the key used as the name.
     * @param parent The ID of a FORM element.
     * @param params An object with key-value pairs.
     * @return This object for chaining.
     */
    addSubmitParam(parent: string, params: Record<string, string>): this {
        var form = this.getClosestForm(parent);

        for(var key in params) {
            form.append("<input type=\"hidden\" name=\"" + this.escapeHTML(key) + "\" value=\"" + this.escapeHTML(params[key] ?? "") + "\" class=\"ui-submit-param\"></input>");
        }

        return this;
    }

    /**
     * Submits the given form, and clears all `ui-submit-param`s after that to prevent dom caching issues.
     *
     * If a target is given, it is set on the form temporarily before it is submitted. Afterwards, the original
     * target attribute of the form is restored.
     * @param formId ID of the FORM element.
     * @param target The target attribute to use on the form during the submit process.
     */
    submit(formId: string, target?: string): void {
        var form = this.getClosestForm(formId);
        var prevTarget;

        if (target) {
            prevTarget = form.attr('target');
            form.attr('target', target);
        }

        form.trigger('submit');
        form.children('input.ui-submit-param').remove();

        if (target) {
            if (prevTarget !== undefined) {
                form.attr('target', prevTarget);
            } else {
                form.removeAttr('target');
            }
        }
    }

    /**
     * Aborts all pending AJAX requests. This includes both requests that were already sent but did not receive a
     * response yet, as well as requests that are waiting in the queue and have not been sent yet.
     */
    abortXHRs(): void {
        ajax.Queue.abortAll();
    }

    /**
     * Attaches the given behaviors to the element. For each behavior, an event listener is registered on the
     * element. Then, when the event is triggered, the behavior callback is invoked.
     * @param element The element for which to attach the behaviors.
     * @param behaviors An object with an event name
     * as the key and event handlers for that event as the value. Each event handler is called with the given
     * element as the this context and the event that occurred as the first argument.
     */
    attachBehaviors(element: JQuery, behaviors: Record<string, (this: JQuery, event: JQuery.TriggeredEvent) => void>): void {
        $.each(behaviors, (event, fn) => {
            element.on(event, (e) => {
                fn.call(element, e);
            });
        });
    }

    /**
     * Fetches the value of a cookie by its name
     * @param name Name of a cookie
     * @return The value of the given cookie, or `undefined` if no such cookie exists
     */
    getCookie(name: string): string | undefined {
        return Cookies.get(name);
    }

    /**
     * Sets the value of a specified cookie with additional security configurations.
     * If the page is served over HTTPS and cookies are configured to be secure in the settings,
     * the secure flag will be set. The SameSite attribute is set based on the settings or defaults to 'Lax'.
     * @param name The name of the cookie.
     * @param value The value to set for the cookie.
     * @param cfg Configuration for this cookie: when it expires, its
     * paths and domain and whether it is secure cookie.
     */
    setCookie(name: string, value: string | null, cfg: Partial<Cookies.CookieAttributes>): void {
        cfg.secure = location.protocol === 'https:' && this.settings.cookiesSecure;
        cfg.sameSite = this.settings.cookiesSameSite || "Lax";
        // "None" is only allowed when Secure attribute so default to Lax if unsecure
        if (!cfg.secure && cfg.sameSite === "None") {
            cfg.sameSite = "Lax"
        }
        Cookies.set(name, value ?? "", cfg);
    }

    /**
     * Deletes the given cookie.
     * @param name Name of the cookie to delete
     * @param cfg The cookie configuration used to set the cookie.
     */
    deleteCookie(name: string, cfg?: Partial<Cookies.CookieAttributes>): void {
        Cookies.remove(name, cfg);
    }

    /**
     * Checks whether cookies are enabled in the current browser.
     * @return `true` if cookies are enabled and can be used, `false` otherwise.
     */
    cookiesEnabled(): boolean {
        if (navigator.cookieEnabled) {
            return true;
        } else {
            document.cookie = "testcookie";
            return document.cookie.includes("testcookie");
        }
    }

    /**
     * Generates a unique key for using in HTML5 local storage by combining the context, view, id, and key.
     * @param id ID of the component
     * @param key a unique key name such as the component name
     * @param global if global then do not include the view id
     * @return the generated key comprising of context + view + id + key
     */
    createStorageKey(id: string, key: string, global: boolean | undefined): string {
        var sk = this.settings.contextPath.replace(/\//g, '-')
                + (global ? '' : this.settings.viewId.replace(/\//g, '-'))
                + id + '-'
                + key;
        return sk.toLowerCase();
    }

    /**
     * Updates the class of the given INPUT element to indicate whether the element contains data or not. Used for
     * example in floating labels.
     * @param input The text input to modify
     * @param parent The parent element of the input.
     */
    updateFilledState(input: JQuery, parent: JQuery): void {
        var value = input.val();

        if (typeof(value) === 'undefined') {
            return;
        }

        // normal inputs just check for a value
        var isFilled = typeof value === "number" ? false : value.length > 0;

        // #11974: Autocomplete multiple mode must be handled differently
        if(parent.hasClass('ui-autocomplete-multiple')) {
            isFilled = parent.find('li.ui-autocomplete-token').length > 0;
        }

        if (isFilled) {
            input.addClass('ui-state-filled');

            if(parent.is("div, span:not('.ui-float-label')")) {
                parent.addClass('ui-inputwrapper-filled');
            }
        } else {
            input.removeClass('ui-state-filled');
            parent.removeClass('ui-inputwrapper-filled');
        }
    }

    /**
     * INPUT elements may have different states, such as `hovering` or `focused`. For each state, there is a
     * corresponding style class that is added to the input when it is in that state, such as `ui-state-hover` or
     * `ui-state-focus`. These classes are used by CSS rules for styling. This method sets up an input element so
     * that the classes are added correctly (by adding event listeners).
     * @param input INPUT element to skin
     * @return this for chaining
     */
    skinInput(input: JQuery): this {
        let parent = input.parent();

        // #11974: Autocomplete multiple mode must be handled differently
        if(parent.hasClass('ui-autocomplete-input-token')) {
            parent = parent.parent().parent();
        }

        const updateFilledStateOnBlur = () => {
            if(parent.hasClass('ui-inputwrapper-focus')) {
                parent.removeClass('ui-inputwrapper-focus');
            }
            this.updateFilledState(input, parent);
        };

        this.updateFilledState(input, parent);

        input.on("mouseenter", function() {
            $(this).addClass('ui-state-hover');
        }).on("mouseleave", function() {
            $(this).removeClass('ui-state-hover');
        }).on("focus", function() {
            $(this).addClass('ui-state-focus');

            if(parent.is("div, span:not('.ui-float-label')")) {
                parent.addClass('ui-inputwrapper-focus');
            }
        }).on("blur animationstart", function() {
            //animationstart is to fix autofill issue https://github.com/primefaces/primefaces/issues/12444
            $(this).removeClass('ui-state-focus');

            // if the input is a datepicker or a number input, wait a bit before updating the filled state
            if(input.hasClass('hasDatepicker') || input.attr('inputmode') === 'numeric') {
                setTimeout(function() {
                    updateFilledStateOnBlur();
                }, 150);
            }
            else {
                updateFilledStateOnBlur();
            }
        });

        if(input.is('textarea')) {
            input.attr('aria-multiline', "true");
        }
        
        // ARIA for filter type inputs
        if (input.is('[class*="-filter"]')) {
            var ariaLabel = input.attr('aria-label');
            if (!ariaLabel) {
                input.attr('aria-label', this.getLocaleLabel('filter') ?? "");
            }
        }

        return this;
    }

    /**
     * BUTTON elements may have different states, such as `hovering` or `focused`. For each state, there is a
     * corresponding style class that is added to the button when it is in that state, such as `ui-state-hover` or
     * `ui-state-focus`. These classes are used by CSS rules for styling. This method sets up a button element so
     * that the classes are added correctly (by adding event listeners).
     * @param button BUTTON element to skin
     * @return this for chaining
     */
    skinButton(button: JQuery): this {
        button.on("mouseover", function(){
            var el = $(this);
            if(!button.prop('disabled')) {
                el.addClass('ui-state-hover');
            }
        }).on("mouseout", function() {
            $(this).removeClass('ui-state-active ui-state-hover');
        }).on("mousedown", function() {
            var el = $(this);
            if(!button.prop('disabled')) {
                el.addClass('ui-state-active').removeClass('ui-state-hover');
            }
        }).on("mouseup", function() {
            $(this).removeClass('ui-state-active').addClass('ui-state-hover');
        }).on("focus", function() {
            $(this).addClass('ui-state-focus');
        }).on("blur", function() {
            $(this).removeClass('ui-state-focus ui-state-active');
        }).on("keydown", function(e) {
            if(e.code === 'Space' || e.key === 'Enter') {
                $(this).addClass('ui-state-active');
            }
        }).on("keyup", function() {
            $(this).removeClass('ui-state-active');
        });

        return this;
    }

    /**
     * There are many Close buttons in PF that should get aria-label="close" and role="button".
     * @param element BUTTON or LINK element
     * @return this for chaining
     */
    skinCloseAction(element: JQuery): JQuery {
        if (!element || element.length === 0) return element;
        element.attr('aria-label', this.getAriaLabel('close'));
        element.attr('role', 'button');
        return element;
    }

    /**
     * Applies the inline AJAX status (ui-state-loading) to the given widget / button.
     * 
     * If the widget defines a {@link BaseWidget.disable disable} method, it will
     * be disabled during the AJAX call, if the
     * {@link BaseWidgetCfg.disableOnAjax disableOnAjax} property of its
     * configuration is set to `true`.
     * 
     * It it also defines an {@link BaseWidget.enable enable} method, the widget
     * will be enabled again once the request finishes.
     *
     * @typeParam Cfg Type of the widget configuration.
     * @typeParam Widget Type of the widget
     * @param widget The widget.
     * @param button The button DOM element.
     * @param isXhrSource Callback that checks if the widget is the source of the current AJAX request.
     */
    bindButtonInlineAjaxStatus<
        Cfg extends BaseWidgetCfg,
        Widget extends BaseWidget<Cfg>
    >(
        widget: Widget,
        button: JQuery,
        isXhrSource?: (widget: Widget, settings: JQuery.AjaxSettings) => boolean
    ): void {
        isXhrSource ??= (widget, settings) => ajax.Utils.isXhrSource(widget, settings);

        const $this = this;
        widget.ajaxCount = 0;
        const namespace = '.' + widget.id;
        $(document).on('pfAjaxSend' + namespace, function(_, __, settings) {
            if (isXhrSource.call(this, widget, settings)) {
                widget.ajaxCount++;
                if (widget.ajaxCount > 1) {
                    return;
                }

                button.addClass('ui-state-loading');
                widget.ajaxStart = Date.now();

                if (widget.cfg.disableOnAjax !== false) {
                    widget.disable?.();
                }

                const loadIcon = $('<span class="ui-icon-loading ui-icon ui-c pi pi-spin pi-spinner"></span>');
                const uiIcon = button.find('.ui-icon');
                if (uiIcon.length) {
                    var prefix = 'ui-button-icon-';
                    loadIcon.addClass(prefix + uiIcon.attr('class')?.includes(prefix + 'left') ? 'left' : 'right');
                }
                button.prepend(loadIcon);
            }
        }).on('pfAjaxComplete' + namespace, function(e, xhr, settings, args) {
            if (isXhrSource.call(this, widget, settings)) {
                widget.ajaxCount--;
                if (widget.ajaxCount > 0 || !args || args.redirect) {
                    return;
                }

                $this.queueTask(
                    () => $this.buttonEndAjaxDisabled(widget, button),
                    Math.max(ajax.minLoadAnimation + (widget.ajaxStart ?? 0) - Date.now(), 0)
                );
                widget.ajaxStart = null;
            }
        });
        widget.addDestroyListener(function() {
            $(document).off(namespace);
        });
    }

    /**
     * Ends the AJAX disabled state. If the widget defines an
     * {@link BaseWidget.enable enable} method, it will be enabled at the end
     * of the AJAX request, if the
     * {@link BaseWidgetCfg.disableOnAjax disableOnAjax} property of its
     * configuration is set to `true`.
     *
     * @typeParam Cfg Type of the widget configuration.
     * @typeParam Widget Type of the widget.
     * @param widget the widget.
     * @param button The button DOM element.
     */
    buttonEndAjaxDisabled<
        Cfg extends BaseWidgetCfg,
        Widget extends BaseWidget<Cfg>
    >(widget: Widget, button: JQuery): void {
        button.removeClass('ui-state-loading');

        if (typeof widget.enable === 'function'
            && widget.cfg.disableOnAjax !== false
            && !widget.cfg.disabledAttr) {
            widget.enable();
        }

        button.find('.ui-icon-loading').remove();
    }

    /**
     * SELECT elements may have different states, such as `hovering` or `focused`. For each state, there is a
     * corresponding style class that is added to the select when it is in that state, such as `ui-state-hover` or
     * `ui-state-focus`. These classes are used by CSS rules for styling. This method sets up a select element so
     * that the classes are added correctly (by adding event listeners).
     * @param select SELECT element to skin
     * @return this for chaining
     */
    skinSelect(select: JQuery): this {
        select.on("mouseover", function() {
            var el = $(this);
            if(!el.hasClass('ui-state-focus'))
                el.addClass('ui-state-hover');
        }).on("mouseout", function() {
            $(this).removeClass('ui-state-hover');
        }).on("focus", function() {
            $(this).addClass('ui-state-focus').removeClass('ui-state-hover');
        }).on("blur", function() {
            $(this).removeClass('ui-state-focus ui-state-hover');
        });

        return this;
    }

    /**
     * Logs the given message at the `info` level.
     * @param log Message or error to log
     */
    info(log: unknown): void {
        if(this.logger) {
            this.logger.info(log);
        }
        if (this.isDevelopmentProjectStage() && window.console) {
            console.info(log);
        }
    }

    /**
     * Logs the given message at the `debug` level.
     * @param log Message or error to log
     */
    debug(log: unknown): void {
        if(this.logger) {
            this.logger.debug(log);
        }
        if (this.isDevelopmentProjectStage() && window.console) {
            console.debug(log);
        }
    }

    /**
     * Logs the given message at the `warn` level.
     * @param log Message or error to log
     */
    warn(log: unknown): void {
        if(this.logger) {
            this.logger.warn(log);
        }

        if (this.isDevelopmentProjectStage() && window.console) {
            console.warn(log);
        }
    }

    /**
     * Logs the given message at the `error` level.
     * @param log Message or error to log
     */
    error(log: unknown): void {
        if(this.logger) {
            this.logger.error(log);
        }

        if (this.isDevelopmentProjectStage() && window.console) {
            console.error(log);
        }
    }

    /**
     * Checks whether the current application is running in a development environment or a production environment.
     * @return `true` if this is a development environment, `false` otherwise.
     */
    isDevelopmentProjectStage(): boolean {
        return this.settings.projectStage === 'Development';
    }

    /**
     * Checks whether the current application is running in a production environment.
     * @return `true` if this is a production environment, `false` otherwise.
     */
    isProductionProjectStage(): boolean {
        return this.settings.projectStage === 'Production';
    }

    /**
     * Handles the error case when a widget was requested that is not available. Currently just logs an error
     * message.
     * @param widgetVar Widget variables of a widget
     */
    widgetNotAvailable(widgetVar: string): void {
        this.error("Widget for var '" + widgetVar + "' not available!");
    }

    /**
     * Gets the currently loaded PrimeFaces theme CSS link.
     * @return The full URL to the theme CSS
     */
    getThemeLink(): JQuery {
        var themeLink = $('link[href*="' + this.RESOURCE_IDENTIFIER + '/theme.css"]');
        // portlet
        if (themeLink.length === 0) {
            themeLink = $('link[href*="' + this.RESOURCE_IDENTIFIER + '=theme.css"]');
        }
        return themeLink;
    }

    /**
     * Gets the currently loaded PrimeFaces theme.
     * @return The current theme, such as `omega` or `luna-amber`. Empty string when no theme is loaded.
     */
    getTheme(): string {
        return env.getTheme();
    }

    /**
     * Changes the current theme to the given theme (by exchanging CSS files). Requires that the theme was
     * installed and is available.
     * @param newTheme The new theme, eg. `luna-amber`, `nova-dark`, or `omega`.
     */
    changeTheme(newTheme: string): void {
        if(newTheme && newTheme !== '') {
            const themeLink = this.getThemeLink();

            const themeURL = themeLink.attr('href') ?? "";
            const plainURL = themeURL.split('&')[0] ?? "";
            const oldTheme = plainURL.split('ln=')[1] ?? "";
            const newThemeURL = themeURL.replace(oldTheme, 'primefaces-' + newTheme);

            themeLink.attr('href', newThemeURL);
        }
    }

    /**
     * Creates a regexp that matches the given text literal, and HTML-escapes that result.
     * @param text The literal text to escape.
     * @return A regexp that matches the given text, escaped to be used as a text-literal within an HTML
     * document.
     */
    escapeRegExp(text: string): string {
        return this.escapeHTML(text.replace(/([.?*+^$[\]\\(){}|-])/g, "\\$1"));
    }

    /**
     * Escapes the given value to be used as the content of an HTML element or attribute.
     * @param value A string to be escaped
     * @param preventDoubleEscaping if true will not include ampersand to prevent double escaping
     * @return The given value, escaped to be used as a text-literal within an HTML document.
     */
    escapeHTML(value: string, preventDoubleEscaping?: boolean | undefined): string {
        var regex = preventDoubleEscaping ? /[<>"'`=/]/g : /[&<>"'`=/]/g;
        return String(value).replace(regex, (s) => {
            return this.entityMap[s] ?? "";
        });
    }

    /**
     * Clears the text selected by the user on the current page.
     */
    clearSelection(): void {
        if(window.getSelection) {
            if(window.getSelection()?.empty) {
                window.getSelection()?.empty();
            } else if(window.getSelection()?.removeAllRanges && (window.getSelection()?.rangeCount ?? 0) > 0 && (window.getSelection()?.getRangeAt(0).getClientRects().length ?? 0) > 0) {
                window.getSelection()?.removeAllRanges();
            }
        }
    }

    /**
     * A shortcut for {@link createWidget}.
     * @typeParam WidgetName Name of the widget class, as registered in {@link PrimeType.WidgetRegistry}
     * @param widgetName Name of the widget class, as registered in {@link widget PrimeFaces.widget}
     * @param widgetVar Widget variable of the widget
     * @param cfg Configuration for the widget
     */
    cw<WidgetName extends keyof PrimeType.WidgetRegistry>(
        widgetName: WidgetName,
        widgetVar: string,
        cfg: PrimeType.widget.PartialCreateWidgetCfg<PrimeType.widget.WidgetCfg<PrimeType.WidgetRegistry[WidgetName]>>
    ): void {
        this.createWidget(widgetName, widgetVar, cfg);
    }

    /**
     * Gets the URL for a Faces resource.
     * @deprecated Use {@link getFacesResource} instead.
     * @param name Name of the resource
     * @param library Library of the resource
     * @param version Version of the resource
     * @return The URL for accessing the given resource.
     */
    getFacesResource(name: string, library: string, version: string): string {
        return resources.getFacesResource(name, library, version);
    }

    /**
     * Creates a new widget of the given type and with the given configuration. Registers that widget in the widgets
     * registry {@link widgets}. If this method is called in response to an AJAX request and the method
     * exists already, it is refreshed.
     * @typeParam WidgetName Name of the widget class, as registered in {@link PrimeType.WidgetRegistry}
     * @param widgetName Name of the widget class, as registered in {@link widget | PrimeFaces.widget}
     * @param widgetVar Widget variable of the widget
     * @param cfg Configuration for the widget, without the `widgetVar` property.
     */
    createWidget<WidgetName extends keyof PrimeType.WidgetRegistry>(
        widgetName: WidgetName,
        widgetVar: string,
        partialCfg: PrimeType.widget.PartialCreateWidgetCfg<PrimeType.widget.WidgetCfg<PrimeType.WidgetRegistry[WidgetName]>>
    ): void {
        const cfg = Object.assign(partialCfg, { widgetVar });

        const widgetType = this.widget[widgetName] as PrimeType.Newable<[], InstanceType<PrimeType.WidgetRegistry[WidgetName]>> | undefined;
        if(widgetType) {
            const widget = this.widgets[widgetVar];

            //ajax update
            if(widget && (widget.constructor === this.widget[widgetName])) {
                widget.refresh(cfg);
                if (cfg.postRefresh) {
                    cfg.postRefresh.call(widget, widget);
                }
            }
            //page init
            else {
                if (cfg.preConstruct) {
                    if (!cfg.labels) {
                        cfg.labels = { aria: {} };
                    }
                    if (!cfg.labels.aria) {
                        cfg.labels.aria = {};
                    }
                    cfg.preConstruct.call(null, cfg);
                }
                const newWidget = new widgetType();
                newWidget.init(cfg);
                this.widgets[widgetVar] = newWidget;
                if (cfg.postConstruct) {
                    cfg.postConstruct.call(newWidget, newWidget);
                }
            }
        }
        // widget script not loaded
        else {
            // should be loaded by our dynamic resource handling, log a error
            this.error("Widget class '" + widgetName + "' not found!");
        }
    }
    
    /**
     * Checks whether an items is contained in the given array. The items is compared against the array entries
     * via the `===` operator.
     * @deprecated Use {@link Array.includes}.
     * @param arr An array with items
     * @param item An item to check
     * @return `true` if the given item is in the given array, `false` otherwise.
     */
    inArray(arr: unknown[], item: unknown): boolean {
        return arr.includes(item);
    }

    /**
     * Checks whether a value is of type `number` and is neither `Infinity` nor `NaN`.
     * @param value A value to check
     * @return `true` if the given value is a finite number (neither `NaN` nor +/- `Infinity`),
     * `false` otherwise.
     */
    isNumber(value: unknown): boolean {
        return typeof value === 'number' && isFinite(value);
    }

    /**
     * Attempts to put focus an element:
     *
     * - When `id` is given, puts focus on the element with that `id`
     * - Otherwise, when `context` is given, puts focus on the first focusable element within that context
     * (container)
     * - Otherwise, puts focus on the first focusable element in the page.
     * @param id ID of an element to focus.
     * @param context The ID of a container with an element to focus
     */
    focus(id?: string | null, context?: string): void {
        var selector = ':not(:submit):not(:button):not([readonly]):input:visible:enabled[name]';
        
        // if looking in container like dialog also check for first link
        if (context) {
            var container = $(this.escapeClientId(context));
            if (container.hasClass('ui-dialog')) {
                selector += ', a:first';
            }
        }

        setTimeout(() => {
            var focusFirstElement = (elements: JQuery) => {
                if (!elements || elements.length === 0) {
                    return;
                }
                
                // first element could be the dialog close button
                var firstElement = elements.eq(0);
                // loop over elements looking for an input
                var inputs = elements.filter(":input");
                if (inputs.length > 0) {
                    firstElement = inputs.eq(0);
                }
                
                this.focusElement(firstElement);
            };
        
            if(id) {
                var jq = $(this.escapeClientId(id));

                if(jq.is(selector)) {
                    jq.trigger('focus');
                }
                else {
                    focusFirstElement(jq.find(selector))
                }
            }
            else if(context) {
                    focusFirstElement($(this.escapeClientId(context)).find(selector))
            }
            else {
                focusFirstElement($(selector));
            }
        }, 50);

        // remember that a custom focus has been rendered
        // this avoids to retain the last focus after ajax update
        this.customFocus = true;
    }

    /**
     * Puts focus on the given element.
     * @param el Element to focus
     */
    focusElement(el: JQuery): void {
        if(el.is(':radio')) {
            // github issue: #2582
            if(el.hasClass('ui-helper-hidden-accessible')) {
                el.parent().trigger('focus');
            }
            else {
                var checkedRadio = $(':radio[name="' + CSS.escape(el.attr('name') ?? "") + '"]').filter(':checked');
                if(checkedRadio.length)
                    checkedRadio.trigger('focus');
                else
                    el.trigger('focus');
            }
        }
        else {
            el.trigger('focus');
        }
    }

    /**
     * As a `<p:fileDownload>` process is implemented as a norma, non-AJAX request, `<p:ajaxStatus>` will not work.
     * Still, PrimeFaces provides a feature to monitor file downloads via this client-side function. This is done
     * by sending a cookie with the HTTP response of the file download request. On the client-side, polling is used
     * to check when the cookie is set.
     *
     * The example below displays a modal dialog when a download begins and hides it when the download is complete:
     *
     * Client-side callbacks:
     *
     * ```javascript
     * function showStatus() {
     *   PF('statusDialog').show();
     * }
     * function hideStatus() {
     *   PF('statusDialog').hide();
     * }
     * ```
     *
     * Server-side XHTML view:
     *
     * ```xml
     * <p:commandButton value="Download" ajax="false" onclick="PrimeFaces.monitorDownload(showStatus, hideStatus)">
     *   <p:fileDownload value="#{fileDownloadController.file}"/>
     * </p:commandButton>
     * ```
     * @param start Callback that is invoked when the download starts.
     * @param complete Callback that is invoked when the download ends.
     * @param monitorKey Name of the cookie for monitoring the download. The cookie name defaults to
     * `primefaces.download` + the current viewId. When a monitor key is given, the name of the cookie will consist of a prefix and the
     * given monitor key.
     */
    monitorDownload(start: () => void, complete: () => void, monitorKey?: string): void {
        if(this.cookiesEnabled()) {
            if(start) {
                start();
            }

            var cookieName = 'primefaces.download' + this.settings.viewId.replace(/\//g, '_');
            cookieName = cookieName.substr(0, cookieName.lastIndexOf("."));
            if (monitorKey && monitorKey !== '') {
                cookieName += '_' + monitorKey;
            }

            var cookiePath = this.settings.contextPath;
            if (!cookiePath || cookiePath === '') {
                cookiePath = '/';
            }

            // Probably no reason to expose this to the global scope, just
            // use a static field or a let-binding variable in this module scope.
            window.downloadMonitor = setInterval(() => {
                var downloadComplete = this.getCookie(cookieName);

                if(downloadComplete === 'true') {
                    if(complete) {
                        complete();
                    }
                    clearInterval(window.downloadMonitor);
                    this.setCookie(cookieName, null, { path: cookiePath });
                }
            }, 1000);
        }
    }

    /**
     * Scrolls to a component with given client id or jQuery element. The scroll animation can be customized with a duration
     * and an optional offset from the top of the target element.
     * @param scrollTarget The ID of an element or jQuery element to scroll to
     * @param duration Duration of the scroll animation in milliseconds or a string like 'slow', 'fast'
     * @param topOffset Additional offset in pixels from the top of the target element
     * @example
     * ```js
     * // Scroll to element with ID 'myElement' over 1 second
     * PrimeFaces.scrollTo('myElement', 1000);
     * 
     * // Scroll to jQuery element with 50px offset from top
     * PrimeFaces.scrollTo($('#myElement'), 'slow', 50);
     * ```
     */
    scrollTo(scrollTarget: string | JQuery, duration: "fast" | "slow" | number = 400, topOffset: number = 0): void {
        var element = typeof scrollTarget === 'string' ? $(this.escapeClientId(scrollTarget)) : scrollTarget;
        var offset = element.offset() ?? { top: 0, left: 0 };
        var scrollBehavior = 'scroll-behavior';
        var target = $('html,body');
        var sbValue = target.css(scrollBehavior);
        target.css(scrollBehavior, 'auto');
        target.animate(
                { scrollTop: offset.top - topOffset, scrollLeft: offset.left },
                duration,
                'easeInCirc',
                function(){ target.css(scrollBehavior, sbValue) }
        );
    }


    /**
     * Aligns container scrollbar to keep item in container viewport, algorithm copied from JQueryUI menu widget.
     * @param container The container with a scrollbar that contains the item.
     * @param item The item to scroll into view.
     */
    scrollInView(container: JQuery, item: JQuery): void {
        if(item === null || item.length === 0) {
            return;
        }

        const borderTop = parseFloat(container.css('borderTopWidth')) || 0;
        const paddingTop = parseFloat(container.css('paddingTop')) || 0;
        const offset = (item.offset()?.top ?? 0) - (container.offset()?.top ?? 0) - borderTop - paddingTop;
        const scroll = container.scrollTop() ?? 0;
        const elementHeight = container.height() ?? 0;
        const itemHeight = item.outerHeight(true) ?? 0;

        if(offset < 0) {
            container.scrollTop(scroll + offset);
        }
        else if((offset + itemHeight) > elementHeight) {
            container.scrollTop(scroll + offset - elementHeight + itemHeight);
        }
    }

    /**
     * Finds the width of the scrollbar that is used by the current browser, as scrollbar widths are different for
     * across different browsers.
     * @return The width of the scrollbars of the current browser.
     */
    calculateScrollbarWidth(): number {
        if(!this.scrollbarWidth) {
            var $div = $('<div></div>')
                .css({ width: '100px', height: '100px', overflow: 'auto', position: 'absolute', top: '-1000px', left: '-1000px' })
                .prependTo('body').append('<div></div>').find('div')
                .css({ width: '100%', height: '200px' });
            this.scrollbarWidth = 100 - ($div.width() ?? 0);
            $div.parent().remove();
        }

        return this.scrollbarWidth;
    }

    /**
     * A function that is used as the handler function for HTML event tags (`onclick`, `onkeyup` etc.). When a
     * component has got an `onclick` etc attribute, the JavaScript for that attribute is called by this method.
     * @param element Element on which the event occurred.
     * @param event Event that occurred.
     * @param functions A list of callback  functions. If any returns `false`, the
     * default action of the event is prevented.
     */
    bcn(element: HTMLElement, event: Event, functions: ((this: HTMLElement, event: Event) => boolean | undefined)[]): void {
        if(functions) {
            for(const fn of functions) {
                var retVal = fn.call(element, event);
                if(retVal === false) {
                    if(event.preventDefault) {
                        event.preventDefault();
                    }
                    else {
                        event.returnValue = false;
                    }

                    break;
                }
            }
        }
    }

    /**
     * A function that is used as the handler function for AJAX behaviors. When a component has got an AJAX
     * behavior, the JavaScript that implements behavior's client-side logic is called by this method.
     * @param ext Additional options to override the current
     * options.
     * @param event Event that occurred.
     * @param fns A list of callback functions. If any returns `false`, the other callbacks are not invoked.
     */
    bcnu(
        ext: Partial<PrimeType.ajax.ConfigurationExtender>, event: Event, 
        fns: ((this: this, ext: Partial<PrimeType.ajax.ConfigurationExtender>, event: Event) => boolean | undefined)[]
    ): void {
        if (fns) {
            for (const fn of fns) {
                if (fn.call(this, ext, event) === false) {
                    break;
                }
            }
        }
    }

    /**
     * Converts the provided string to searchable form.
     * 
     * @param string to normalize.
     * @param lowercase flag indicating whether the string should be lower cased.
     * @param normalize flag indicating whether the string should be normalized (accents to be removed
     * from characters).
     * @returns searchable string.
     */
    toSearchable(string: string, lowercase: boolean, normalize: boolean): string {
        if (!string) return '';
        var result = normalize ? string.normalize('NFD').replace(/[\u0300-\u036f]/g, '') : string;
        return lowercase ? result.toLowerCase() : result;
    }

    /**
     * Opens the dialog with the given configuration.
     * @param cfg Configuration of the dialog.
     */
    openDialog(cfg: PrimeType.hook.dialog.DialogConfiguration): void {
        for (const dialogHook of this.hooks.dialog ?? []) {
            const handled = dialogHook.openDialog(cfg);
            if (handled) {
                return;
            }
        }
    }

    /**
     * Close the dialog with the given configuration.
     * @param cfg Configuration of the dialog.
     */
    closeDialog(cfg: PrimeType.hook.dialog.DialogConfiguration): void {
        for (const dialogHook of this.hooks.dialog ?? []) {
            const handled = dialogHook.closeDialog(cfg);
            if (handled) {
                return;
            }
        }
    }

    /**
     * Shows the given message inside a dialog.
     * @param msg Message to show in a dialog.
     */
    showMessageInDialog(msg: PrimeType.hook.messageInDialog.DialogMessageData): void {
        for (const messageInDialogHook of this.hooks.messageInDialog ?? []) {
            const handled = messageInDialogHook.showMessage(msg);
            if (handled) {
                return;
            }
        }
    }

    /**
     * Displays dialog or popup according to the type of confirm component.
     * @param msg Message to show with the confirm dialog or popup.
     */
    confirm(msg: PrimeType.hook.confirm.ExtendedConfirmMessage): void {
        for (const confirmHook of this.hooks.confirm ?? []) {
            const handled = confirmHook.handleMessage(msg);
            if (handled) {
                return;
            }
        }
    }

    /**
     * Registers a hook with this PrimeFaces core instance. A hook
     * can extend the core in various ways to provide additional functionality.
     * 
     * Each hook allows multiple implementation to be registered. The semantics
     * depend on the hook, but usually implementations are invoked in the
     * order they were registered.
     * @typeParam HookName Type of the the hook name to register.
     * @param name Name of the hook to register.
     * @param hook Hook implementation to register.
     */
    registerHook<HookName extends keyof PrimeType.HookRegistry>(
        name: HookName,
        hook: PrimeType.HookRegistry[HookName],
    ): void {
        this.hooks[name] ??= new Set() as HookMap[HookName];
        this.hooks[name].add(hook);
    }

    /**
     * Unregisters a core hook from this PrimeFaces core instance, that was
     * previously registered via {@link registerHook}.
     * 
     * @typeParam HookName Type of the the hook name to register.
     * @param name Name of the hook to unregister.
     * @param hook Hook implementation to register.
     */
    unregisterHook<HookName extends keyof PrimeType.HookRegistry>(
        name: HookName,
        hook: PrimeType.HookRegistry[HookName],
    ): void {
        this.hooks[name]?.delete(hook);
    }

    /**
     * Removes all implementation for the given hook name that were registered via
     * {@link registerHook}.
     * @typeParam HookName Type of the the hook name to register.
     * @param name Name of the hook to unregister.
     */
    clearHook<HookName extends keyof PrimeType.HookRegistry>(name: HookName): void {
        this.hooks[name] = undefined;
    }

    /**
     * Gets all implementation for the given hook that were registered via
     * {@link registerHook}. The returned array is mutable, but mutations
     * will not have any effect. Use {@link registerHook} and
     * {@link unregisterHook} to modify the registered implementations.
     * @typeParam HookName Type of the the hook name to register.
     * @param name Name of the hook to unregister.
     */
    getHook<HookName extends keyof PrimeType.HookRegistry>(
        name: HookName,
    ): PrimeType.HookRegistry[HookName][] {
        return [...this.hooks[name] ?? []];
    }

    /**
     * Some widgets need to compute their dimensions based on their parent element(s). This requires that such
     * widgets are not rendered until they have become visible. A widget may not be visible, for example, when it
     * is inside a tab that is not shown when the page is rendered. PrimeFaces provides a global mechanism for
     * widgets to render once they are visible. This is done by keeping a list of widgets that need to be rendered,
     * and checking on every change (AJAX request, tab change etc.) whether any of those have become visible. A
     * widgets should extend `PrimeFaces.widget.DeferredWidget` to make use of this functionality.
     *
     * Adds a deferred render to the global list.  If this widget has already been added only the last instance
     * will be added to the stack.
     *
     * @param widgetId The ID of a deferred widget.
     * @param containerId ID of the container that should be visible before the widget can be rendered.
     * @param fn Callback that is invoked when the widget _may_ possibly have become visible. Should
     * return `true` when the widget was rendered, or `false` when the widget still needs to be rendered later.
     */
    addDeferredRender(widgetId: string | string[], containerId: string, fn: () => boolean | undefined | void): void {
        // remove existing
        this.deferredRenders = this.deferredRenders.filter(deferredRender => {
            return !(deferredRender.widget === widgetId && deferredRender.container === containerId);
        });

        // add new
        this.deferredRenders.push({ widget: widgetId, container: containerId, callback: fn });
    }

    /**
     * Some widgets need to compute their dimensions based on their parent element(s). This requires that such
     * widgets are not rendered until they have become visible. A widget may not be visible, for example, when it
     * is inside a tab that is not shown when the page is rendered. PrimeFaces provides a global mechanism for
     * widgets to render once they are visible. This is done by keeping a list of widgets that need to be rendered,
     * and checking on every change (AJAX request, tab change etc.) whether any of those have become visible. A
     * widgets should extend `PrimeFaces.widget.DeferredWidget` to make use of this functionality.
     *
     * Removes a deferred render from the global list.
     *
     * @param widgetId The ID of a deferred widget.
     */
    removeDeferredRenders(widgetId: string | string[]): void {
        this.deferredRenders = this.deferredRenders.filter((deferredRender) => deferredRender.widget !== widgetId);
    }

    /**
     * Some widgets need to compute their dimensions based on their parent element(s). This requires that such
     * widgets are not rendered until they have become visible. A widget may not be visible, for example, when it
     * is inside a tab that is not shown when the page is rendered. PrimeFaces provides a global mechanism for
     * widgets to render once they are visible. This is done by keeping a list of widgets that need to be rendered,
     * and checking on every change (AJAX request, tab change etc.) whether any of those have become visible. A
     * widgets should extend `PrimeFaces.widget.DeferredWidget` to make use of this functionality.
     *
     * Invokes all deferred renders. This is usually called when an action was performed that _may_ have resulted
     * in a container now being visible. This includes actions such as an AJAX request request was made or a tab
     * change.
     *
     * @param containerId ID of the container that _may_ have become visible.
     */
    invokeDeferredRenders(containerId: string | string[]): void {
        var widgetsToRemove = [];
        for (const deferredRender of this.deferredRenders) {

            if(deferredRender.container === containerId) {
                var rendered = deferredRender.callback.call(undefined);
                if(rendered) {
                    widgetsToRemove.push(deferredRender.widget);
                }
            }
        }

        for (const widget of widgetsToRemove) {
            this.removeDeferredRenders(widget);
        }
    }

    /**
     * Finds the current locale with the i18n keys and the associated translations. Uses the current language key
     * as specified by {@link PrimeType.PFSettings.locale | PrimeFaces.settings.locale}. When no locale was found for
     * the given locale, falls back to the default English locale.
     * @param cfgLocale Optional configuration locale from the widget
     * @return The current locale with the key-value pairs.
     */
    getLocaleSettings(cfgLocale?: string): PrimeType.Locale {
        let locale: PrimeType.Locale | undefined;

        if(cfgLocale) {
            // widget locale must not be cached since it can change per widget
            locale = this.locales[cfgLocale];
        } else {
            // global settings so return cached value if already loaded
            if(this.localeSettings) {
                return this.localeSettings;
            }
            locale = this.locales[this.settings.locale];
        }

        // try and strip specific language from nl_BE to just nl
        if (!locale) {
            var fullLocaleKey = cfgLocale || this.settings.locale;
            var splitLocaleKey = fullLocaleKey ? fullLocaleKey.split('_')[0] : null;
            if (splitLocaleKey) {
                locale = this.locales[splitLocaleKey];
            }
        }

        // if all else fails default to US English
        if(!locale) {
            locale = this.locales['en_US'];
        }

        // cache default global settings
        if(!cfgLocale) {
            this.localeSettings = locale;
        }

        return locale ?? LocaleEnUs;
    }

    /**
     * Retrieves a localized ARIA label based on the provided key. If the key is not found in the current locale,
     * it falls back to the US English locale. If the key is still not found, it uses a default value or a placeholder
     * indicating the missing key. This method also supports dynamic replacement of placeholders within the label
     * string using the `options` object.
     * 
     * @param key - The key to retrieve the ARIA label for.
     * @param defaultValue - The default value to use if the key is not found.
     * @param options - An object containing placeholder replacements in the format `{placeholderKey: replacementValue}`.
     * @returns string - The localized ARIA label, with placeholders replaced by their corresponding values from `options` if provided.
     */
    getAriaLabel(key: keyof PrimeType.LocaleAria, defaultValue?: string, options?: Record<string, string>): string {
        var ariaLocaleSettings = this.getLocaleSettings()['aria'] || {};
        var label = ariaLocaleSettings[key] || this.locales['en_US']?.['aria']?.[key] || defaultValue || "???" + key + "???";
        if (options) {
            for (const valKey in options) {
                label = label.replace(`{${valKey}}`, options[valKey] ?? "");
            }
        }
        return label.trim();
    }

    /**
     * Attempt to look up the locale key by current locale and fall back to US English if not found.
     * @param {string} key The locale key
     * @return {string} The translation for the given key
     */
    getLocaleLabel<K extends keyof PrimeType.Locale>(key: K): PrimeType.Locale[K] {
        const locale = this.getLocaleSettings();
        return (locale&&locale[key]) ? locale[key] : this.locales['en_US']?.[key];
    }

    /**
     * Loop over all locales and set the label to the new value in all locales.
     * @param localeKey The locale key
     * @param localeValue The locale value
     */
    setGlobalLocaleValue(localeKey: string, localeValue: string): void {
        // Recursive function to iterate over nested objects
        const iterateLocale = (locale: Record<string, unknown>, lkey: string, lvalue: string) => {
            for (var key in locale) {
                if (typeof locale[key] === 'object') {
                    // If the value is an object, call the function recursively
                    iterateLocale(locale[key] as Record<string, unknown>, lkey, lvalue);
                } 
                // Otherwise, set the new value if found
                else if (key === lkey) {
                    locale[key] = lvalue;
                }
            }
        }

        // iterate over all locales and try and set the key in each locale
        for (var lang in this.locales) {
            const locale = this.locales[lang];
            if (typeof locale === 'object') {
                iterateLocale(locale, localeKey, localeValue)
            }
        }
    }

    /**
     * For jQuery 4.0, they deprecated `$.trim` in favor of `String.trim`. However,
     * that does not handle `null` and `undefined`; and jQuery did handle this case.
     * This function allows a drop in replacement.
     *
     * @deprecated Use {@link String.trim | string?.trim() ?? ""}. TypeScript checks
     * for null and undefined.
     * @param value The string to trim.
     * @return Trimmed value.
     */
    trim(value: string | String | undefined | null): string {
        if (!value) {
            return "";
        }

        if (typeof value === 'string' || value instanceof String) {
            return value.trim();
        }

        // return original value if it was not a string
        return value;
    }

    /**
     * Generate a RFC-4122 compliant UUID to be used as a unique identifier.
     *
     * Uses {@link crypto.randomUUID} if available, otherwise falls back to a custom implementation.
     *
     * See https://www.ietf.org/rfc/rfc4122.txt
     *
     * @return A random UUID.
     */
    uuid(): string {
        if (typeof crypto.randomUUID === 'function') {
            return crypto.randomUUID();
        }
        else {
            return "10000000-1000-4000-8000-100000000000".replace(/[018]/g, c => {
                const d = parseInt(c);
                return (d ^ (crypto.getRandomValues(new Uint8Array(1))[0] ?? 0) & 15 >> d / 4).toString(16);
            });
        }
    }

    /**
     * Increment and return the next `z-index` for CSS as a string. If an element is provided, apply the new
     * `z-index` to it.
     * Note that jQuery will no longer accept numeric values in {@link JQuery.css | $.fn.css} as of version 4.0.
     *
     * @param element If given, applies the new `z-index` to that element.
     * @returns The next `z-index` as a string.
     */
    nextZindex(element?: JQuery): string {
        const zIndex = String(++this.zindex);
        if (element) {
            element.css('z-index', zIndex);
        }
        return zIndex;
    }

    /**
     * Converts a date into an ISO-8601 date without using the browser timezone offset.
     *
     * See https://stackoverflow.com/questions/10830357/javascript-toisostring-ignores-timezone-offset
     *
     * @param {Date} date the date to convert
     * @return {string} ISO-8601 version of the date
     */
    toISOString(date: Date): string {
        return new Date(date.getTime() - (date.getTimezoneOffset() * 60000)).toISOString();
    }

    /**
     * Reset any state variables on update="@all".
     */
    resetState(): void {
        // terminate all AJAX requests, pollers, etc
        utils.killswitch();

        this.zindex = 1000;
        this.detachedWidgets = [];
        this.animationActive = false;
        this.customFocus = false;
        this.widgets = {};            
    }

    /**
     * Queue a microtask if delay is 0 or less and setTimeout if > 0.
     *
     * @param fn The function to call after the delay.
     * @param delay The optional delay in milliseconds.
     * @return The id associated to the timeout or undefined if no timeout used.
     */
    queueTask(fn: () => void, delay?: number): number | undefined {
        return utils.queueTask(fn, delay);
    }

    /**
     * Returns the current PrimeFaces and jQuery version as a string and logs it to the console.
     * @returns The current PrimeFaces and jQuery version as a string.
     */
    version(): string {
        const version = 'PrimeFaces ' + this.VERSION + ' (jQuery ' + jQuery.fn.jquery + ' / UI ' + $.ui.version + ')';
        console.log(version);
        return version;
    }

    /**
     * Creates a debounced version of the provided function that delays invoking the function until after the specified delay.
     * The debounced function will only execute once the delay has elapsed and no additional function calls were made.
     * Each new function call resets the delay timer.
     * 
     * @param fn The function to debounce
     * @param delay The number of milliseconds to delay. Defaults to 400ms. Negative values are coerced to 400ms.
     */
    debounce(fn: () => void, delay: number = 400): void {
        if (delay < 0) {
            delay = 400;
        }

        if (this.debounceTimer) {
            clearTimeout(this.debounceTimer);
        }

        this.debounceTimer = this.queueTask(() => {
            fn();
            this.debounceTimer = undefined;
        }, delay);
    }
}

export const core: Core = new Core();

/**
 * Finds and returns a widget
 *
 * Note to TypeScript users: You might want to define a method that takes a widget variables and widget constructor, and
 * check whether the widget is of the given type. If so, you can return the widget and cast it to the desired type:
 * ```ts
 * function getWidget<T extends PrimeType.widget.BaseWidget>(widgetVar: string, widgetClass: PrimeType.Newable<[], T>): T | undefined {
 *   const widget = PrimeFaces.widget[widgetVar];
 *   return widget !== undefined && widget instanceof constructor ? widgetClass : undefined;
 * }
 * ```
 * @param widgetVar The widget variable of a widget.
 * @return The widget instance, or `undefined` if no such widget exists currently.
 */
export function PF(widgetVar: string): BaseWidget | undefined {
    const widgetInstance = core.widgets[widgetVar];

    if (!widgetInstance) {
        core.widgetNotAvailable(widgetVar);
    }

    return widgetInstance;
}
