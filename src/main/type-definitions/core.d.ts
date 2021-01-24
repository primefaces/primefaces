// Type definitions for PrimeFaces
// Project: PrimeFaces https://github.com/primefaces
// Definitions by: Andre Wachsmuth https://github.com/blutorange/

/// <reference types="chart.js" />
/// <reference types="cropperjs" />
/// <reference types="downloadjs" />
/// <reference types="googlemaps" />
/// <reference types="jquery" />
/// <reference types="jqueryui" />
/// <reference types="moment-timezone" />

/// <reference path="PrimeFaces-module.d.ts" />

// Additional required type declarations for PrimeFaces that are not picked up from the source code

/**
 * An object that can be used to emulate classes and a class hierarchy in JavaScript. This works even for old
 * browsers that do no support the native `class` syntax yet. Note however, that this should be mostly compatible
 * with the new `class` syntax of JavaScript, so consider creating your own widgets as a class:
 *
 * ```javascript
 * class MyWidget extends PrimeFaces.widget.BaseWidget {
 *   init(cfg){
 *     // ...
 *   }
 * }
 * ```
 *
 * __Note to typescript users__: You will need to specify the type parameters explicitly. The best way to do so is by
 * defining the interfaces for the classes separately:
 * ```typescript
 * interface BaseWidgetCfg {
 *  prop1: string;
 * }
 * interface AccordionPanelCfg extends BaseWidgetCfg {
 *   prop2: boolean;
 * }
 * interface BaseWidget {
 *   init(cfg: BaseWidgetCfg): void;
 *   method1(x: number): boolean;
 * }
 * interface Accordion extends BaseWidget {
 *   init(cfg: AccordionPanelCfg): void;
 *   method1(): boolean;
 *   method2(): boolean;
 * }
 * ```
 *
 * Now you can use it normally:
 * ```typescript
 * const BaseWidget = Class.extend<BaseWidget, [BaseWidgetCfg]>({
 *   init(cfg: BaseWidgetCfg) {
 *     // ...
 *   },
 *   method1(x: number): boolean {
 *     return x > 0;
 *   },
 * });
 * const Accordion = BaseWidget.extend<Accordion, [AccordionCfg]>({
 *   init(cfg: AccordionCfg) {
 *     this._super(cfg);
 *   },
 *   method1() {
 *     return !this._super(42);
 *   },
 *   method2() {
 *     return true;
 *   }
 * });
 * const base: BaseWidget = new BaseWidget({prop1: ""});
 * const accordion: Accordion = new Accordion({prop1: "", prop2: true});
 * base.method1(2);
 * accordion.method1();
 * accordion.method2();
 * ```
 */
declare const Class: PrimeFaces.Class;

declare namespace PrimeFaces {
    /**
     * Construct a type with the properties of T except for those in type K.
     *
     * Same as the builtin TypeScript type `Omit`, but repeated here to support slightly older TypeScript versions (3.4
     * and lower) that did not include it yet.
     */
    export type Omit<T, K extends keyof any> = Pick<T, Exclude<keyof T, K>>;

    /**
     * Similar to `Partial<Base>`, but in addition to making the properties optional, also makes the properties
     * nullable.
     * @typeparam Base Type of an object with properties to make partial.
     * @return A new type with all properties of the given type `Base` made optional and nullable.
     */
    export type PartialOrNull<Base> = {
        [P in keyof Base]?: Base[P] | null;
    };

    /**
     * Constructs a new type by making all properties `Key` in `Base` optional.
     * ```typescript
     * type X = {foo: string, bar: string, baz: string};
     * type Y = PartialBy<X, "foo" | "baz">;
     * // type Y = {foo?: string, bar: string, baz?: string};
     * ```
     * @typeparam Base Type for which some properties are made optional.
     * @typeparam Key Type of the keys that are made optional.
     * @return A subtype of `Base` with the all properties `Key` made optional.
     */
    export type PartialBy<Base, Key extends keyof Base> = Omit<Base, Key> & Partial<Pick<Base, Key>>;

    /**
     * Constructs a new type by intersecting the given `Base` with `void`. This is a hack, required because some methods
     * of a parent class explicitly return a value, but some derived classes do not.
     * @typeparam Base Base type to intersect
     * @return A new type that is the intersection of the given `Base` with `void`.
     */
    export type ReturnOrVoid<Base> =
        // tslint:disable-next-line
        Base | void;

    /**
     * Constructs a new type that is the union of all types of each property in T.
     * ```typescript
     * type X = {a: ["bar", RegExp], b: ["foo", number]};
     * type Y = ValueOf<X>;
     * // type Y = ["bar", RegExp] | ["foo", number]
     * ```
     * @typeparam T A type with keys.template
     */
    export type ValueOf<T> = T[keyof T];

    /**
     * Constructs an object type a union of two-tuples. The first item in the pair
     * is used as the key, the second item as the type of the property's value.
     * ```typescript
     * type Y = ["bar", RegExp] | ["foo", number];
     * type Z = KeyValueTupleToObject<Y>;
     * // type Z = { bar: RegExp; foo: number; }
     * ```
     * @typeparam T A union of pairs with the key and value for the object's properties.
     */
    export type KeyValueTupleToObject<T extends [keyof any, any]> = {
        [K in T[0]]: Extract<T, [K, any]>[1]
    };

    /**
     * Constructs a new type by renaming the properties in `Base` according to the `RenameMap`.
     * ```typescript
     * type Z = { bar: RegExp; foo: number; };
     * type S = RenameKeys<Z, {bar: "b", foo: "f"}>;
     * // type S = { b: RegExp; f: number; }
     * ```
     * @typeparam Base Type with properties to rename
     * @typeparam RenameMap Type with string properties that indicate how to rename the keys of `Base`.
     */
    export type RenameKeys<Base, RenameMap extends Record<string, string>> =
        KeyValueTupleToObject<ValueOf<{
            [Key in keyof Base]: [Key extends keyof RenameMap ? RenameMap[Key] : Key, Base[Key]]
        }>>;

    /**
     * Constructs a new type by binding the this context of `Base` to another type `ThisContext`.
     * ```typescript
     * type X = (this: string, x: string) => boolean;
     * type Y = BindThis<X, number>;
     * // type Y = (this: number, x: string) => boolean
     * ```
     * @typeparam Base Type to rebind.
     * @typeparam ThisContext New this context for `Base`.
     * @return If `Base` is a function type, that type with the this context bound to `ThisContext`. Otherwise, returns
     * just `Base`.
     */
    export type BindThis<Base, ThisContext> =
        Base extends (...args: any) => any ?
        (this: ThisContext, ...args: Parameters<Base>) => ReturnType<Base> :
        Base;

    /**
     * Constructs a new type by binding the this context of `Base` to the `ThisContext`. Additionally, also adds a new
     * property `_super` to the this context of `Base` that is also of type `Base`.
     * ```typescript
     * type X = {foo: string, bar: number};
     * type Y = (this: string, k: string) => boolean;
     * type Z = BindThis<Y, X>;
     * // type Z = (this: {foo: string, bar: number, _super: Y}, k: string) => boolean
     * ```
     * @typeparam Base Type to rebind.
     * @typeparam ThisContext New this context for `Base`.
     * @return If `Base` is a function type, that type with the this context bound to `ThisContext` and with an
     * additional property `_super` of type `Base` added. Otherwise, returns just `Base`.
     */
    export type BindThisAndSuper<Base, ThisContext> =
        Base extends (...args: any) => any ?
        (this: ThisContext & { _super: Base }, ...args: Parameters<Base>) => ReturnType<Base> :
        Base;

    /**
     * Constructs a new type that is the intersection of all property names in `Base` whose type is assignable to
     * `Condition`:
     * ```typescript
     * interface User {
     *   name: string;
     *   mail: string;
     *   active: boolean;
     * }
     *
     * type UserStringKeys = MatchingKeys<User, string>;
     * // type UserStringKeys = "name" | "mail";
     * ```
     * @typeparam Base Type from which to pick some properties.
     * @typeparam Condition Type which the properties in the base type have to match.
     * @return A string intersection type of property names from the base type that match the condition.
     */
    type MatchingKeys<Base, Condition> = {
        [Key in keyof Base]: Base[Key] extends Condition ? Key : never;
    }[keyof Base];

    /**
     * Constructs a new type by picking from `Base` all properties that are assignable to `Condition`.
     * ```typescript
     * interface User {
     *   name: string;
     *   mail: string;
     *   active: boolean;
     * }
     *
     * type UserStringProperties = PickMatching<User, string>;
     * // type UserStringProperties = {name: string, mail: string};
     * ```
     * @typeparam Base Type from which to pick some properties.
     * @typeparam Condition Type which the properties in the base type have to match.
     * @return A subtype of the base type with all properties excluded that do not match the condition.
    */
    export type PickMatching<Base, Condition> = Pick<Base, MatchingKeys<Base, Condition>>;

    /**
     * Given the type of the base class and the sub class, constructs a new type for the argument of `Class.extend(...)`,
     * except that all properties are required.
     * @typeparam TBase Type of the base class.
     * @typeparam TSub Type of the subclass.
     * @return A mapped type with properties P. If the property P is function type F, then the this context is bound to
     * TSub. Additionally, if P is a property only of TBase and not of TSub, a special property `_super` of type F is
     * added to the this context.
     */
    export type ClassExtendProps<TBase, TSub> = {
        [P in keyof TSub]: P extends keyof TBase ? BindThisAndSuper<TBase[P], TSub> : BindThis<TSub[P], TSub>
    };

    /**
     * The widget configuration of each widget may contain only some of the declared properties. For example, when the
     * value of a property is equal to the default value, it is not transmitted from the server to the client. Only the
     * two properties `id` and `widgetVar` are guaranteed to be always available.
     * @typeparam TCfg Type of a widget configuration. It must have at least the two properties `id` and `widgetVar`.
     * @return A new type with all properties in the given type made optional, exception for `id` and `widgetVar`.
     */
    export type PartialWidgetCfg<TCfg extends { id: string | string[], widgetVar: string }> =
        Partial<Omit<TCfg, "id" | "widgetVar">> & Pick<TCfg, "id" | "widgetVar">;

    /**
     * An object that can be used to emulate classes and a class hierarchy in JavaScript. This works even for old
     * browsers that do no support the native `class` syntax yet. Note however, that this should be mostly compatible
     * with the new `class` syntax of JavaScript, so consider creating your own widgets as a class:
     *
     * ```javascript
     * class MyWidget extends PrimeFaces.widget.BaseWidget {
     *   init(cfg){
     *     // ...
     *   }
     * }
     * ```
     *
     * Note for TypeScript users: Normally you should just write a widget as a class that extends from the appropriate
     * base class. If you must use this method you will need to specify the type parameters explicitly. The best way to
     * do so is by defining the interfaces for the classes separately:
     * ```typescript
     * interface BaseWidgetCfg {
     *  prop1: string;
     * }
     * interface AccordionCfg extends BaseWidgetCfg {
     *   prop2: boolean;
     * }
     * interface BaseWidget {
     *   init(cfg: BaseWidgetCfg): void;
     *   method1(x: number): boolean;
     * }
     * interface Accordion extends BaseWidget {
     *   init(cfg: AccordionCfg): void;
     *   method1(): boolean;
     *   method2(): boolean;
     * }
     * ```
     *
     * Now you can use it normally:
     * ```typescript
     * const BaseWidget = Class.extend<BaseWidget, [BaseWidgetCfg]>({
     *   init(cfg: BaseWidgetCfg) {
     *     // ...
     *   },
     *   method1(x: number): boolean {
     *     return x > 0;
     *   },
     * });
     * const Accordion = BaseWidget.extend<Accordion, [AccordionCfg]>({
     *   init(cfg: AccordionCfg) {
     *     this._super(cfg);
     *   },
     *   method1() {
     *     return !this._super(42);
     *   },
     *   method2() {
     *     return true;
     *   }
     * });
     * const base: BaseWidget = new BaseWidget({prop1: ""});
     * const accordion: Accordion = new Accordion({prop1: "", prop2: true});
     * base.method1(2);
     * accordion.method1();
     * accordion.method2();
     * ```
     */
    export interface Class<TBase = Record<string, unknown>> {
        new(): Class<TBase>;
        extend<
            TSub extends { init(...args: TArgs): void } & Omit<TBase, "init">,
            TArgs extends any[],
            >(
                prop: PartialBy<ClassExtendProps<TBase, TSub>, keyof Omit<TBase, "init">>
            ): Class<TSub> & (new (...args: TArgs) => TSub) & { prototype: TSub };
    }

    /**
     * Maps the return type of a method of an instance method of a JQueryUI widget instance to the return type of the
     * JQueryUI wrapper:
     * - When an instance method returns `undefined` or the instance itself, the JQuery instance is returned.
     * - Otherwise, the value of the instance method is returned.
     * @typeparam W Type of the widget instance.
     * @typeparam R Type of a value returned by a widget instance method.
     * @typeparam JQ Type of the JQuery instance.
     * @return The type that is returned by the JQueryUI wrapper method.
     */
    export type ToJQueryUIWidgetReturnType<W, R, JQ> =
        // tslint:disable-next-line
        R extends W | undefined | void
        ? JQ
        : R extends undefined | void
        ? R | JQ
        : R;

    /**
     * An object with all localized strings required on the client side.
     */
    export interface Locale {
        allDayText?: string;
        aria?: Record<string, string>;
        closeText?: string;
        prevText?: string;
        nextText?: string;
        monthNames?: [string, string, string, string, string, string, string, string, string, string, string, string];
        monthNamesShort?: [string, string, string, string, string, string, string, string, string, string, string, string];
        dayNames?: [string, string, string, string, string, string, string];
        dayNamesShort?: [string, string, string, string, string, string, string];
        dayNamesMin?: [string, string, string, string, string, string, string];
        weekHeader?: string;
        weekNumberTitle?: string;
        firstDay?: number;
        isRTL?: boolean;
        showMonthAfterYear?: boolean;
        yearSuffix?: string;
        timeOnlyTitle?: string;
        timeText?: string;
        hourText?: string;
        minuteText?: string;
        secondText?: string;
        currentText?: string;
        year?: string;
        ampm?: boolean;
        month?: string;
        week?: string;
        day?: string;
        noEventsText?: string;
        moreLinkText?: string;
        list?: string;
        messages?: Record<string, string>;
        [i18nKey: string]: any;
    }
}

declare namespace PrimeFaces {
    /**
     * Defines the possible severity levels of a faces message (a message shown to the user).
     *
     * - fatal: Indicates that the message reports a grave error that needs the immediate attention of the reader.
     * - error: Indicates that the message reports an error that occurred, such as invalid user input or database
     * connection failures etc.
     * - warn: Indicates that the message reports a possible issue, but it does not prevent the normal operation of the
     * system.
     * - info: Indicates that the message provides additional information, if the reader is interested.
     */
    export type FacesMessageSeverity = "fatal" | "error" | "warn" | "info";

    /**
     * A converter for converting string values to the correct data type.
     */
    export interface Converter<T = any> {
        /**
         * Converts a string value to the correct data type.
         * @param element Element for which the value was submitted.
         * @param submittedValue The submitted string value
         * @return The converted value.
         */
        convert(element: JQuery, submittedValue: string | null | undefined): T;
    }

    /**
     * A validator for checking whether the value of an element confirms to certain restrictions.
     */
    export interface Validator<T = any> {
        /**
         * Validates the given element. If it is not valid, the error message should be thrown.
         * @param element Element to validate
         * @param value Current value of the element
         * @throws The error message as the string when the element with its current value is not valid.
         */
        validate(element: JQuery, value?: T): void;
    }

    /**
     * A faces message with a short summary message and a more detailed message. Used by the client-side validation
     * framework.
     */
    export interface FacesMessageBase {
        /**
         * A short summary of the message.
         */
        summary: string;

        /**
         * In-depth details of the message.
         */
        detail: string;
    }

    /**
     * A faces message with a short summary message and a more detailed message, as well as a severity level that
     * indicates the type of this message. Used by the client-side validation framework and some widgets such as the
     * growl widget.
     */
    export interface FacesMessage extends FacesMessageBase {
        /**
         * The severity of this message, i.e. whether it is an information message, a warning message, or an error
         * message.
         */
        severity: FacesMessageSeverity;

        /**
         * The severity in I18N human readable text for ARIA screen readers.
         */
        severityText?: string;
    }

    /*
     * __Note__: Do not parametrize the this context via a type parameter. This would require changing the return type
     * of BaseWidget#getBehavior to "PrimeFaces.Behavior<this>"". If that were done, however, it would not be longer be
     * possible to assign, for example, an object of type AccordionPanel to a variable of type BaseWidget - as that
     * would allow calling "getBehavior" on the AccordionPanel and only passing a BaseWidget as the this context.
     */
    /**
     * A callback function for a behavior of a widget. A behavior is a way for associating client-side scripts with UI
     * components that opens all sorts of possibilities, including client-side validation, DOM and style manipulation,
     * keyboard handling, and more. When the behavior is triggered, the configured JavaScript gets executed.
     *
     * Behaviors are often, but not necessarily, AJAX behavior. When triggered, it initiates a request the server and
     * processes the response once it is received. This enables several features such as updating or replacing elements
     * dynamically. You can add an AJAX behavior via `<p:ajax event="name" actionListener="#{...}" onstart="..." />`.
     *
     */
    export type Behavior =
        /**
         * @this This callback takes the widget instance as the this context. This must be the widget instance that owns
         * the behavior. The type is only required to be a {@link BaseWidget} as only common widget properties such as
         * its ID are used.
         * @param ext Additional data to be sent with the AJAX request that is made to the server.
         */
        (this: PrimeFaces.widget.BaseWidget, ext?: Partial<PrimeFaces.ajax.ConfigurationExtender>) => void;

    /**
     * The most recent instance of a {@link PrimeFaces.widget.ConfirmDialog} instance that was opened in response to a
     * global confirmation request.
     */
    export let confirmDialog: PrimeFaces.widget.ConfirmDialog | undefined;

    /**
     * The main container element of the source component that issued the confirmation request.
     */
    export let confirmSource: JQuery | undefined | null;
}

declare namespace PrimeFaces.ajax {

    /**
     * An entry on the {@link JQuery.jqXHR} request object with additional values added by PrimeFaces. For example, when
     * you call `PrimeFaces.current().ajax().addCallbackParam(...)` on the server in a bean method, the added parameters
     * are available in this object. This is also how you can access pass values from the server to the client after
     * calling a remote command. See {@link PrimeFaces.ajax.pfXHR} and {@link PrimeFaces.ab}.
     */
    export type PrimeFacesArgs = Record<string, any>;

    /**
     * Additional settings on a {@link JQuery.jqXHR} request, such as portlet forms and nonces.
     */
    export type PrimeFacesSettings = Record<string, any>;

    /**
     * Callback for an AJAX request that is always called after the request completes, irrespective of whether it
     * succeeded or failed.
     *
     * This is the type of function that you can set as a client side callback for the `oncomplete` attribute of a
     * component or an AJX behavior.
     */
    export type CallbackOncomplete =
        /**
         * @this The current AJAX settings as they were passed to JQuery when the request was made.
         * @param xhrOrErrorThrown Either the XHR request that was made (in case of success), or the error that was
         * thrown (in case of an error).
         * @param status The type of error or success.
         * @param pfArgs Additional arguments returned by PrimeFaces, such as AJAX callback params from beans.
         * @param dataOrXhr Either the XMLDocument (in case of success), or the XHR request (in case of an error).
         */
        (this: JQuery.AjaxSettings, xhrOrErrorThrown: unknown, status: JQuery.Ajax.TextStatus, pfArgs: PrimeFacesArgs, dataOrXhr: XMLDocument | pfXHR) => void;

    /**
     * Callback for an AJAX request that is called in case any error occurred during the request, such as a a network
     * error. Note that this is not called for errors in the application logic, such as when bean validation fails.
     *
     * This is the type of function that you can set as a client side callback for the `onerror` attribute of a
     * component or an AJX behavior.
     */
    export type CallbackOnerror =
        /**
         * @this The current AJAX settings as they were passed to JQuery when the request was made.
         * @param xhr The XHR request that failed.
         * @param status The type of error that occurred.
         * @param errorThrown The error with details on why the request failed.
         */
        (this: JQuery.AjaxSettings, xhr: pfXHR, status: JQuery.Ajax.ErrorTextStatus, errorThrown: string) => void;

    /**
     * Callback for an AJAX request that is called before the request is sent. Return `false` to cancel the request.
     *
     * This is the type of function that you can set as a client side callback for the `onstart` attribute of a
     * component or an AJX behavior.
     */
    export type CallbackOnstart =
        /**
         * @this The {@link PrimeFaces.ajax.Request} singleton instance responsible for handling the request.
         * @param cfg The current AJAX configuration.
         * @return {boolean | undefined} `false` to abort and not send the request, `true` or `undefined` otherwise.
         */
        (this: PrimeFaces.ajax.Request, cfg: Configuration) => boolean;

    /**
     * Callback for an AJAX request that is called when the request succeeds.
     *
     * This is the type of function that you can set as a client side callback for the `onsuccess` attribute of a
     * component or an AJX behavior.
     */
    export type CallbackOnsuccess =
        /**
         * @this The current AJAX settings as they were passed to JQuery when the request was made.
         * @param data The XML document representing the partial response returned the JSF application in response
         * to the faces request. It usually looks like this: `<changes>...</changes>`
         * @param status The type of success, usually `success`.
         * @param xhr The XHR request that succeeded.
         * @return `true` if this handler already handle and/or parsed the response, `false` or `undefined` otherwise.
         */
        (this: JQuery.AjaxSettings, data: XMLDocument, status: JQuery.Ajax.SuccessTextStatus, xhr: pfXHR) => boolean | undefined;

    /**
     * The XHR request object used by PrimeFaces. It extends the `jqXHR` object as used by JQuery, but adds additional
     * properties specific to PrimeFaces.
     * @typeparam P Data made available by the server via {@link pfXHR.pfArgs}.
     */
    export interface pfXHR<P extends PrimeFacesArgs = PrimeFacesArgs> extends JQuery.jqXHR {
        /**
         * An object with additional values added by PrimeFaces. For example, when you call
         * `PrimeFaces.current().ajax().addCallbackParam(...)` on the server in a bean method, the added parameters are
         * available in this object. This is also how you can access pass values from the server to the client after
         * calling a remote command.  See {@link PrimeFaces.ajax.pfXHR} and {@link PrimeFaces.ab}.
         */
        pfArgs?: P;

        /**
         * Additional settings, such as portlet forms and nonces.
         */
        pfSettings?: PrimeFacesSettings;
    }

    /**
     * Represents the data of a PrimeFaces AJAX request. This is the value that is returned by {@link PrimeFaces.ab} and
     * {@link PrimeFaces.ajax.Request.handle}.
     * @typeparam P Record type of the data made available in the property {@link PrimeFaces.ajax.pfXHR.pfArgs} by the
     * server.
     */
    export interface ResponseData<P extends PrimeFacesArgs = PrimeFacesArgs> {
        /**
         * The XML document that was returned by the server. This may include several elements such as `update` for DOM
         * updates that need to be performed, `executeScript` for running JavaScript code. A typical response might look
         * as follows:
         *
         * ```xml
         * <?xml version="1.0" encoding="UTF-8"?>
         * <partial-response>
         *    <changes>
         *       <update id="content:msgs"><![CDATA[
         *           <span id="content:msgs" class="ui-growl-pl">...</span>
         *           <script id="content:msgs_s" type="text/javascript">...</script>
         *       ]]></update>
         *       <update id="content:javax.faces.ViewState:0"><![CDATA[3644438980748093603:2519460806875181703]]></update>
         *    </changes>
         * </partial-response>
         * ```
         */
        document: XMLDocument;

        /**
         * The jQuery XHR request object that was used for the request.
         *
         * __Note__: This object has a `pfArgs` entry that contains the values added to the response by the server. See
         * {@link PrimeFaces.ajax.pfXHR.pfArgs} and {@link PrimeFaces.ajax.RemoteCommand}.
         */
        jqXHR: PrimeFaces.ajax.pfXHR<P>;

        /**
         * A string describing the type of success. Usually the HTTP status text.
         */
        textStatus: JQuery.Ajax.SuccessTextStatus;
    }

    /**
     * Represents the data passed to the exception handler of the promise returned by {@link PrimeFaces.ab} and
     * {@link PrimeFaces.ajax.Request.handle}.
     */
    export interface FailedRequestData {
        /**
         * An optional exception message, if an error occurred.
         */
        errorThrown: string;

        /**
         * The jQuery XHR request object that was used for the request. May not be available when no HTTP request was
         * sent, such as when validation failed.
         */
        jqXHR?: PrimeFaces.ajax.pfXHR;

        /**
         * A string describing the type of error that occurred.
         */
        textStatus: JQuery.Ajax.SuccessTextStatus;
    }

    /**
     * Describes a server callback parameter for an AJAX call. For example, when you call a
     * `<p:remoteCommand name="myCommand" />` from the client, you may pass additional parameters to the backing
     * bean like this:
     *
     * ```javascript
     * myCommand([
     *   {
     *     name: "MyParam",
     *     value: "MyValue",
     *   }
     * ]);
     * ```
     *
     * In the backing bean, you can access this parameter like this:
     *
     * ```java
     * final String myParam = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("myParam");
     * ```
     *
     * @typeparam K Name of this parameter.
     * @typeparam V Type of the value of the callback parameter. Please note that it will be converted to string
     * before it is passed to the server.
     */
    export interface RequestParameter<K extends string = string, V = any> {
        /**
         * The name of the parameter to pass to the server.
         */
        name: K;

        /**
         * The value of the parameter to pass to the server.
         */
        value: V;
    }

    /**
     * The response of an AJAX request may contain one or more actions such as executing scripts or updating DOM nodes.
     * This interface represents a handler for an `update` action.
     * @typeparam TWidget Type of the widget which
     * triggered the AJAX request.
     */
    export interface UpdateHandler<TWidget extends PrimeFaces.widget.BaseWidget = PrimeFaces.widget.BaseWidget> {
        /**
         * The widget which triggered the AJAX request.
         */
        widget: TWidget,

        /**
         * The handle function which is given the HTML string of the update
         * @param content The new HTML content from the update.
         */
        handle(this: TWidget, content: string): void;
    }

    /**
     * Represents the selection of an INPUT or TEXTAREA element.
     */
    export interface ActiveElementSelection {
        /**
         * Start of the selection, that is, the index of the first selected character.
         */
        start: number;

        /**
         * End of the selection, that is, one plus the index of the last selected character.
         */
        end: number;

        /**
         * The number of selected characters.
         */
        length: number;

        /**
         * The selected text
         */
        text: string;
    }

    /**
     * The options that can be passed to AJAX calls made by PrimeFaces. Note that you do not have to provide a value
     * for all these property. Most methods methods such as `PrimeFaces.ab` have got sensible defaults in case you
     * do not.
     */
    export interface Configuration {
        /**
         * If `true`, the the request is sent immediately. When set to `false`, the AJAX request is added to a
         * global queue to ensure that only one request is active at a time, and that each response is processed
         * in order. Defaults to `false`.
         */
        async: boolean;

        /**
         * Delay in milliseconds. If less than this delay elapses between AJAX requests, only the most recent one is
         * sent and all other requests are discarded. If this option is not specified, no delay is used.
         */
        delay: number;

        /**
         * A PrimeFaces client-side search expression (such as `@widgetVar` or `@(.my-class)` for locating the form
         * to with the input elements that are serialized. If not given, defaults to the enclosing form.
         */
        formId: string;

        /**
         * The AJAX behavior event that triggered the AJAX request.
         */
        event: string;

        /**
         * Additional options that can be passed when sending an AJAX request to override the current options.
         */
        ext: ConfigurationExtender;

        /**
         * Additional search expression that is added to the `process` option.
         */
        fragmentId: string;

        /**
         * Whether this AJAX request is global, ie whether it should trigger the global `<p:ajaxStatus />`. Defaults
         * to `true`.
         */
        global: boolean;

        /**
         * `true` if components with `<p:autoUpdate/`> should be ignored and updated only if specified explicitly
         * in the `update` option; or `false` otherwise. Defaults to `false`.
         */
        ignoreAutoUpdate: boolean;

        /**
         * Callback that is always called after the request completes, irrespective of whether it succeeded or
         * failed.
         */
        oncomplete: CallbackOncomplete;

        /**
         * Callback that is called in case any error occurred during the request, such as a a network error. Note
         * that this is not called for errors in the application logic, such as when bean validation fails.
         */
        onerror: CallbackOnerror;

        /**
         * Callback that is called before the request is sent. Return `false` to cancel the request.
         */
        onstart: CallbackOnstart;

        /**
         * Callback that is called when the request succeeds.
         */
        onsuccess: CallbackOnsuccess;

        /**
         * Additional parameters that are passed to the server. These can be accessed as follows:
         *
         * ```java
         * final String myParam = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("myParam");
         * ```
         */
        params: RequestParameter[];

        /**
         * `true` to perform a partial submit and not send the entire form data, but only the processed components;
         * or `false` to send the entire form data. Defaults to `false`.
         */
        partialSubmit: boolean;

        /**
         * A CSS selector for finding the input elements of partially processed components. Defaults to `:input`.
         */
        partialSubmitFilter: string;

        /**
         * A (client-side) PrimeFaces search expression for the components to process in the AJAX request.
         */
        process: string;

        /**
         * A promise object that is resolved when the AJAX request is complete. You can use this option to register
         * a custom callback. Please note that usually you do not have to set this option explicitly, you can use the
         * return value of {@link PrimeFaces.ab} or {@link PrimeFaces.ajax.Request.handle}. It will create a new promise
         * object when none was provided, and return that.
         */
        promise: Promise<PrimeFaces.ajax.ResponseData>;

        /**
         * `true` if the AJAX request is a reset request that resets the value of all form elements to their
         * initial values, or `false` otherwise. Defaults to `false`.
         */
        resetValues: boolean;

        /**
         * `true` if child components should be skipped for the AJAX request, `false` otherwise. Used only by a few
         * specific components.
         */
        skipChildren: boolean;

        /**
         * The source that triggered the AJAX request.
         */
        source: string | JQuery | HTMLElement;

        /**
         * Set a timeout (in milliseconds) for the request. A value of 0 means there will be no timeout.
         */
        timeout: number;

        /**
         * A (client-side) PrimeFaces search expression for the components to update in the AJAX request.
         */
        update: string;
    }

    /**
     * Additional options that can be passed when sending an AJAX request to override the current options.
     */
    export type ConfigurationExtender = Pick<Configuration, "update" | "process" | "onstart" | "params" | "onerror" | "onsuccess" | "oncomplete"> & {
        /**
         * If given, this function is called once for each component. It is passed that serialized values for the
         * component and should return the filtered values that are to be sent to the server. If not given, no
         * values are filtered, and all values are send to the server.
         * @param componentPostParams The serialized values of a component.
         * @return The filtered values that are to be sent to the server.
         */
        partialSubmitParameterFilter(this: Request, componentPostParams: RequestParameter[]): RequestParameter[];
    };

    /**
     * Options passed to AJAX calls made by PrimeFaces. This is the same as `Configuration`, but with shorter
     * option names and is used mainly by the method `PrimeFaces.ab`. See `Configuration` for a detailed description
     * of these options.
     */
    export type ShorthandConfiguration = RenameKeys<Configuration, {
        source: "s",
        formId: "f",
        process: "p",
        update: "u",
        event: "e",
        async: "a",
        global: "g",
        delay: "d",
        timeout: "t",
        skipChildren: "sc",
        ignoreAutoUpdate: "iau",
        partialSubmit: "ps",
        partialSubmitFilter: "psf",
        resetValues: "rv",
        fragmentId: "fi",
        params: "pa",
        onstart: "onst",
        onerror: "oner",
        onsuccess: "onsu",
        oncomplete: "onco",
    }>;

    /**
     * Helper type for the parameters of the remote command. You can specify an object type with the allowed parameter
     * names and their expected value types. This helps to increase type safety for remote commands. For example, when
     * this remote command with an appropriate bean implementation is defined:
     *
     * ```xml
     * <p:remoteCommand name="RemoteCommands.checkMaturity" ... />
     * ```
     *
     * Then you can declare (or generate automatically from the bean method!) this remote command in TypeScript like
     * this:
     *
     * ```typescript
     * declare const RemoteCommands {
     *   const checkMaturity: RemoteCommand<
     *     {name: string, age: number},
     *     {success: boolean, message: string}
     *   >;
     * }
     *
     * RemoteCommand.checkMaturity( [ { name: "name", value: "John Doe" } ] ) // works
     * RemoteCommand.checkMaturity( [ { name: "age", value: 12 } ] ) // works
     *
     * RemoteCommand.checkMaturity( [ { name: "username", value: "John Doe" } ] ) // error
     * RemoteCommand.checkMaturity( [ { name: "age", value: "12" } ] ) // error
     *
     * const response = await RemoteCommand.checkMaturity( [ { name: "name", value: "John Doe" } ];
     *
     * const success: boolean = response.jqXHR.pfArgs.success; // works
     * const message: string = response.jqXHR.pfArgs.message; // works
     * const message: string = response.jqXHR.pfArgs.errormessage; // error
     * ```
     * @typeparam T Record type with the param names and the corresponding param values.
     * @return An array type of {@link PrimeFaces.ajax.RequestParameter | request parameters} where the `name` can be
     * one of the keys of `T` and the `value` is the corresponding value from `T`. Array values are mapped to the item
     * type, so that `RemoteCommandParams<{names: string[]}>` is the same as `RemoteCommandParams<{names: string}>`.
     * This is done because multiple values for the same name should be send by including multiple items in the request
     * callback parameter array.
     */
    export type RemoteCommandParams<T extends Record<string, any> = Record<string, any>> = {
        [P in keyof T]: P extends string
            ? PrimeFaces.ajax.RequestParameter<P, T[P] extends (infer R)[] ? R : T[P]>
            : never;
    }[keyof T][];

    /**
     * Type for the JavaScript remote command function that is created via
     *
     * ```xml
     * <p:remoteCommand name="myCommand" listener="#{myBean.action}" />
     * ```
     *
     * This creates a variable `window.myCommand` that is of this type. On the client-side, you can pass parameters to
     * the remote command via
     *
     * ```javascript
     * window.myCommand([ { name: "myParamName", value: 9 } ]);
     * ```
     *
     * On the server-side, you can access them as follows:
     *
     * ```java
     * String myParamValue = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("myParamName")
     * ```
     *
     * To send data back to the client, use
     *
     * ```java
     * PrimeFaces.current().ajax().addCallbackParam("returnParamName", true);
     * ```
     *
     * Finally, to access the returned value on the client, do
     *
     * ```javascript
     * try {
     *   const response = await window.myCommand([ { name: "myParamName", value: 9 } ]);
     *   // Success, do something with the data
     *   const value = response.jqXHR.pfArgs.returnParamName;
     * }
     * catch (e) {
     *   // Handle error
     *   console.error("Could not invoke remote command", e);
     * }
     * ```
     *
     * Please note that you should not use async-await if you need to target old browsers, use `then`/`catch` on the
     * returned promise instead. See {@link RemoteCommandParams} for more details on how to use this TypeScript type.
     * @typeparam T Object type with the param names and the corresponding param values.
     * @typeparam R Object type of the data returned by the remote command.
     */
    export type RemoteCommand<
        T extends Record<string, any> = Record<string, any>,
        R extends PrimeFacesArgs = PrimeFacesArgs
    > =
        /**
         * @param params Optional parameters that are passed to the remote command.
         * @return A promise that is settled when the remote command it complete. It is resolved with the data received
         * from the server, and rejected when a network or server error occurred.
         */
        (params?: RemoteCommandParams<T>) => Promise<ResponseData<R>>;
}

declare namespace PrimeFaces.validation {

    /**
     * When an element is invalid due to a validation error, the user needs to be informed. A highlight handler is
     * responsible for changing the visual state of an element so that the user notices the invalid element. A highlight
     * handler is usually registered for a particular type of element or widget.
     */
    export interface Highlighter {
        /**
         * When an element is invalid due to a validation error, the user needs to be informed. This method must
         * highlight the given element in a way that makes the user notice that the element is invalid.
         * @param element An element to highlight.
         */
        highlight(element: JQuery): void;

        /**
         * When an element is invalid due to a validation error, the user needs to be informed. This method must
         * remove the highlighting of the given element that was added by `highlight`.
         */
        unhighlight(element: JQuery): void;
    }


   /**
     * The options that can be passed to the Validation method. Note that you do not have to provide a value
     * for all these property. Most methods methods such as `PrimeFaces.vb` have got sensible defaults in case you
     * do not.
     */
    export interface Configuration {

        /**
         * The source that triggered the validationt.
         */
        source: string | JQuery | HTMLElement;

        /**
         * `true` if the validation is triggered by AJAXified compoment. Defaults to `false`.
         */
        ajax: boolean;

        /**
         * A (client-side) PrimeFaces search expression for the components to process in the validation.
         */
        process: string;

        /**
         * A (client-side) PrimeFaces search expression for the components to update in the validation.
         */
        update: string,

        /**
         * `true` if invalid elements should be highlighted as invalid. Default is `true`.
         */
        highlight: boolean,

        /**
         * `true` if the first invalid element should be focussed. Default is `true`.
         */
        focus: boolean,

        /**
         * `true` if messages should be rendered. Default is `true`.
         */
        renderMessages: boolean,

        /**
         * `true` if invisible elements should be validated. Default is `false`.
         */
        validateInvisibleElements: boolean;
    }

    /**
     * Options passed to `PrimeFaces.vb` as shortcut. This is the same as `Configuration`, but with shorter
     * option names and is used mainly by the method `PrimeFaces.vb`. See `Configuration` for a detailed description
     * of these options.
     */
    export type ShorthandConfiguration = RenameKeys<Configuration, {
        source: "s",
        ajax: "a",
        process: "p",
        update: "u",
        highlight: "h",
        focus: "f",
        renderMessages: "r",
        validateInvisibleElements: "v";
    }>;
}

// JQuery extensions

/**
 * Here you can find additional methods on JQuery instances defined by various JQuery plugins. These methods are
 * usually defined by certain widgets and may not be available unless the widget and its JavaScript dependencies were
 * loaded.
 */
interface JQuery {
}

/**
 * Here you can find additional properties and methods defined on the global JQuery object, such as `$.browser`. These
 * properties and methods are usually defined by certain widgets and may not be available unless the widget and its
 * JavaScript dependencies were loaded.
 */
interface JQueryStatic {
}

declare namespace JQuery {
    /**
     * This interface contains all known types of events triggered by various jQuery extensions. It maps the name of the
     * event to the type the event that is triggered. Please note that this interface does not define the custom
     * additional arguments that may be passed when triggering event. These are deprecated in favor of using
     * `CustomEvent`.
     */
    interface TypeToTriggeredEventMap<
        TDelegateTarget,
        TData,
        TCurrentTarget,
        TTarget
        > {
        /**
         * Triggered on the document before an AJAX request made by {@link PrimeFaces.ajax} starts.
         *
         * Usually the following arguments are passed to the callback:
         * - {@link PrimeFaces.ajax.pfXHR}: The AJAX request that is about to be sent.
         * - {@link JQuery.AjaxSettings}: The settings of the AJAX request.
         */
        pfAjaxSend: JQuery.TriggeredEvent<TDelegateTarget, TData, TCurrentTarget, TTarget>;

        /**
         * Triggered on the document when an AJAX request made by {@link PrimeFaces.ajax} starts.
         *
         * Usually no arguments are passed to the callback.
         */
        pfAjaxStart: JQuery.TriggeredEvent<TDelegateTarget, TData, TCurrentTarget, TTarget>;

        /**
         * Triggered on the document when an AJAX request made by {@link PrimeFaces.ajax} fails.
         *
         * Usually the following arguments are passed to the callback:
         * - {@link PrimeFaces.ajax.pfXHR}: The AJAX request that failed.
         * - {@link JQuery.AjaxSettings}: The settings of the AJAX request.
         * - A string: The error that occurred.
         */
        pfAjaxError: JQuery.TriggeredEvent<TDelegateTarget, TData, TCurrentTarget, TTarget>;

        /**
         * Triggered on the document when an AJAX request made by {@link PrimeFaces.ajax} succeeds.
         *
         * Usually the following arguments are passed to the callback:
         * - {@link PrimeFaces.ajax.pfXHR}: The AJAX request that was successful.
         * - {@link JQuery.AjaxSettings}: The settings of the AJAX request.
         */
        pfAjaxSuccess: JQuery.TriggeredEvent<TDelegateTarget, TData, TCurrentTarget, TTarget>;

        /**
         * Triggered on the document when an AJAX request completes (both success and failure). Only when `global` is set to `true`.
         *
         * Usually the following arguments are passed to the callback:
         * - {@link PrimeFaces.ajax.pfXHR}: The AJAX request that completed
         * - {@link JQuery.AjaxSettings}: The settings of the AJAX request.
         */
        pfAjaxComplete: JQuery.TriggeredEvent<TDelegateTarget, TData, TCurrentTarget, TTarget>;
    }
}
