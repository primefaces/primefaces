import Cookies from "js-cookie";

import { PF as _PF, core, type Core } from "./src/core/core.js";
import { globalAjaxSetup } from "./src/core/core.ajax.js";
import { globalUtilsSetup } from "./src/core/core.utils.js";

import type { AjaxExceptionHandler } from "./src/ajaxexceptionhandler/ajaxexceptionhandler.js";
import type { AjaxStatus } from "./src/ajaxstatus/ajaxstatus.js";
import type { BaseWidget, DeferredWidget, DynamicOverlayWidget } from "./src/core/core.widget.js";
import type { Poll } from "./src/poll/poll.js";

import { registerKillSwitchHookForPoll } from "./src/poll/poll.js";
import { registerCommonConverters } from "./src/validation/validation.converters.js";
import { registerCommonValidationMessages } from "./src/validation/validation.common.js";
import { registerCommonValidators } from "./src/validation/validation.validators.js";
import { registerCommonHighlighters } from "./src/validation/validation.highlighters.js";
import { registerBeanValidationMessages, registerBeanValidationValidators } from "./src/validation/validation.bv.js";

exposeToGlobalScope();

function exposeToGlobalScope() {
    // Do nothing if the core script was already loaded
    if ("PrimeFaces" in window) {
        window.PrimeFaces.debug("PrimeFaces already loaded, ignoring duplicate execution.");
        return;
    }

    // Expose js-cookie to the global scope
    Object.assign(window, {
        Cookies,
        PrimeFaces: core,
        PF: _PF,
    });

    // Seems unused, but define this for now for compatibility
    // TODO Can this be removed?
    Object.assign(core, { util: {} });

    // Register additional components
    registerCommonConverters();
    registerCommonHighlighters();
    registerCommonValidators();
    registerBeanValidationValidators();

    registerCommonValidationMessages();
    registerBeanValidationMessages();

    // Register additional features
    registerKillSwitchHookForPoll();

    // Global setup
    globalAjaxSetup();
    globalUtilsSetup();
}

// General types
declare global {
    namespace PrimeType {
        export type EntityMap = Record<string, string>;

        /**
         * The kind of a theme, either "light" or "dark".
         */
        export type ThemeKind = "light" | "dark";

        /**
         * These string constants represent the current state of the running Faces application in a typical product
         * development lifecycle. The value of this state may be queried at any time after application startup by
         * calling {@link PFSettings.projectStage}.
         * - `Development` - This value indicates the currently running application is right now, at this moment, being
         * developed.
         * - `UnitTest` - This value indicates the currently running application is undergoing unit testing.
         * - `SystemTest` - This value indicates the currently running application is undergoing system testing.
         * - `Production` - This value indicates the currently running application is deployed in production.
         */
        export type ProjectStage = "Development" | "UnitTest" | "SystemTest" | "Production";

        /**
         * A callback that is invoked when the user clicks on an element outside
         * an overlay widget.
         */
        export type OverlayHideCallback =
            /**
             * @param event The event that caused the overlay to be hidden.
             * @param eventTarget The target of the event, wrapped in a JQuery instance.
             */
            (event: JQuery.TriggeredEvent, eventTarget: JQuery) => void;

        /**
         * Used when registering overlay {@link OverlayHideCallback}.
         * 
         * The callback which resolves the elements to ignore when the user
         * clicks outside the overlay. The {@link OverlayHideCallback} is not
         * invoked when the user clicks on one those elements.
         */
        export type OverlayResolveIgnoredElementCallback =
            /**
             * @param event The event that occurred, with the element that was clicked.
             * @returns An optional set of elements to ignore.
             */
            (event: JQuery.TriggeredEvent) => JQuery | null | undefined;

        /**
         * Callback invoked when an element or the window was resized.
         */
        export type ResizeCallback =
            /**
             * @param event The resize event that occurred.
             */
            (event: JQuery.TriggeredEvent) => void;

        /**
         * Callback invoked when a scroll event occurred.
         */
        export type ScrollCallback =
            /**
             * @param event The scroll event that occurred.
             */
            (event: JQuery.TriggeredEvent) => void;

        /**
         * Callback invoked when changes occur that may affect an element's
         * position or dimensions, such as a resize or DOM mutation event.
         */
        export type MutationCallback = () => void;

        /**
         * Custom logger for logging messages. Set your custom logger via
         * `PrimeFaces.logger = ...`.
         */
        export interface Logger {
            /**
             * Logs a message at the debug level.
             * @param message Message or error to log.
             */
            debug: (message: unknown) => void;
            /**
             * Logs a message at the error level.
             * @param message Message or error to log.
             */
            error: (message: unknown) => void;
            /**
             * Logs a message at the info level.
             * @param log Message or error to log
             */
            info: (message: unknown) => void;
            /**
             * Logs a message at the warn level.
             * @param log Message or error to log
             */
            warn: (message: unknown) => void;
        }

        /**
         * CSS transition callbacks that can be passed to the methods in {@link CssTransitionHandler}.
         * @since 10.0.0
         */
        export interface CssTransitionCallback {
            /**
             * Called when the entering process is about to start.
             */
            onEnter?: (this: Window) => void;
            /**
             * Called during the entering process.
             * @this The event that occurred. When animations are globally disabled, this callback may still be called, but
             * no event is passed and the this context is the Window.
             */
            onEntering?: (this: JQuery.TriggeredEvent | Window) => void;
            /**
             * Called when the entering process has finished.
             * @this The event that occurred. When animations are globally disabled, this callback may still be called, but
             * no event is passed and the this context is the Window.
             */
            onEntered?: (this: JQuery.TriggeredEvent | Window) => void;
            /**
             * Called when the exiting process is about to start.
             */
            onExit?: (this: Window) => void;
            /**
             * Called during the exiting process.
             * @this The event that occurred. When animations are globally disabled, this callback may still be called, but
             * no event is passed and the this context is the Window.
             */
            onExiting?: (this: JQuery.TriggeredEvent | Window) => void;
            /**
             * Called when the exiting process has finished.
             * @this The event that occurred. When animations are globally disabled, this callback may still be called, but
             * no event is passed and the this context is the Window.
             */
            onExited?: (this: JQuery.TriggeredEvent | Window) => void;
        }

        /**
         * Methods for a CSS transition that are returned by {@link PrimeFaces.utils.registerCSSTransition}.
         * @since 10.0.0
         */
        export interface CssTransitionHandler {
            /**
             * Should be called when an element gets shown.
             * @param callbacks Optional callbacks that will be invoked at the appropriate time.
             */
            show(callbacks?: CssTransitionCallback): void;
            /**
             * Should be called when an element gets hidden.
             * @param callbacks Optional callbacks that will be invoked at the appropriate time.
             */
            hide(callbacks?: CssTransitionCallback): void;
        }

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
         * A 'FacesMessage' with a short summary message and a more detailed message, as well as a severity level that
         * indicates the type of this message. Used by the client-side validation framework and some widgets such as the
         * growl widget.
         */
        export interface BaseFacesMessage {
            /**
             * A short summary of the message.
             */
            summary: string;
            /**
             * In-depth details of the message.
             */
            detail: string;
            /**
             * The severity of this message, i.e. whether it is an information message, a warning message, or an error
             * message.
             */
            severity?: FacesMessageSeverity;
            /**
             * If the message was successfully rendered by a message/growl component.
             */
            rendered?: boolean;
        }

        /**
         * A 'FacesMessage' with a short summary message and a more detailed message, as well as a severity level that
         * indicates the type of this message. Used by the client-side validation framework and some widgets such as the
         * growl widget.
         * 
         * Similar to {@link BaseFacesMessage}, but `severity` and `rendered` are guaranteed to be present.
         */
        export interface FacesMessage extends BaseFacesMessage {
            /**
             * The severity of this message, i.e. whether it is an information message, a warning message, or an error
             * message.
             */
            severity: FacesMessageSeverity;
            /**
             * If the message was successfully rendered by a message/growl component.
             */
            rendered: boolean;
        }

        /**
         * Represents a deferred render added for a deferred widget.
         * Some widgets need to compute their dimensions based on their parent element(s). This requires that such widgets
         * are not rendered until they have become visible. A widget may not be visible, for example, when it is inside a
         * tab that is not shown when the page is rendered. PrimeFaces provides a global mechanism for widgets to render
         * once they are visible. This is done by keeping a list of widgets that need to be rendered, and checking on every
         * change (AJAX request, tab change etc.) whether any of those have become visible. A widgets should extend
         * `PrimeFaces.widget.DeferredWidget` to make use of this functionality.
         */
        export interface DeferredRender {
            /**
             * Callback that is invoked when the widget _may_ possibly have become visible.
             * Checks whether the widget can be rendered and if so, renders it.
             * @return `true` when the widget was rendered, or `false` when the widget still
             * needs to be rendered later.
             */
            callback(): boolean | undefined | void;
            /**
             * ID of the container that should be visible before the widget can be rendered.
             */
            container: string | string[];
            /**
             * The ID of a deferred widget.
             */
            widget: string | string[];
        }

        export interface PFSettings {
            /**
             * `true` if the empty string and `null` should be treated the same way, or
             * `false` otherwise.
             */
            considerEmptyStringNull: boolean;

            /**
             * The current servlet context path.
             */
            contextPath: string;

            /**
             * If cookies are secured (allowed only via HTTPS).
             */
            cookiesSecure: boolean;

            /**
             * The `same-site` attribute for cookies.
             */
            cookiesSameSite?: "strict" | "Strict" | "lax" | "Lax" | "none" | "None" | undefined;

            /**
             * If AJAX post params are evaluated early.
             */
            earlyPostParamEvaluation?: boolean;

            /**
             * Contains the error pages that may be shown when an error occurs.
             *
             * The key is the name of the error page, e.g. `java.lang.Throwable`, the
             * value the URL of the error page.
             */
            errorPages?: Record<string, string>;

            /**
            * The current locale, such as `en`,`en_US`, or `ja`.
            */
            locale: string;

            /**
             * If AJAX partial-submit is enabled.
             */
            partialSubmit?: boolean;

            /**
             * The Faces project stage.
             */
            projectStage?: ProjectStage;

            /**
             * `true` if empty (input etc.) fields should be validated, or `false` otherwise.
             */
            validateEmptyFields: boolean;

            /**
             * The Faces ID of the current view.
             */
            viewId: string;
        }
    }
}

// Features for the core
declare global {
    namespace PrimeType {
        /**
         * Registry for hooks. Hooks are a way for external code to interact
         * with and adjust core functions. The core provides entry points
         * external code can hook into. 
         *  
         * Includes hooks such as showing confirmation messages in dialogs.
         * The PrimeFaces core does not know about dialogs, but additional
         * scripts may add such functionality.
         */
        export interface HookRegistry { }
    }

    namespace PrimeType.hook {
        /**
         * The confirm hook. Lets external scripts subscribe to emitted
         * confirmation messages. Implementations usually make use of this
         * feature to show confirmation messages, such as in a confirm popup
         * or a confirm dialog.
         */
        export interface Confirm {
            /**
             * Handles an emitted confirmation message, such as by showing it
             * to the user.
             * @param message Message to handle.
             * @returns Whether the message was handled. If true, subsequent
             * registered implementations will not be called anymore.
             */
            handleMessage: (message: confirm.ExtendedConfirmMessage) => boolean;
        }

        /**
         * The dialog hook. Lets external scripts react to requests for
         * opening and closing a dialog. The default implementation is provided
         * by PrimeFace's dialog framework.
         */
        export interface Dialog {
            /**
             * Opens a dialog with the given configuration.
             * @param cfg Dialog configuration to use.
             * @returns Whether the request was handled. If true, subsequent
             * registered implementations will not be called anymore.
             */
            openDialog: (cfg: dialog.DialogConfiguration) => boolean;
            /**
             * Closes a dialog with the given configuration.
             * @param cfg Dialog configuration to use.
             * @returns Whether the request was handled. If true, subsequent
             * registered implementations will not be called anymore.
             */
            closeDialog: (cfg: dialog.DialogConfiguration) => boolean;
        }

        /**
         * The kill switch hook. Lets external scripts react to a kill signal
         * and stop ongoing operations, such as AJAX requests, polling operations
         * etc.
         */
        export interface KillSwitch {
            /**
             * Kills all ongoing actions. Implementations might stop running
             * polling operations, idle monitor listeners, etc.
             * 
             * Errors are caught, logged, and ignored otherwise.
             */
            kill: () => void;
        }

        /**
         * The message render hook. Lets external code hook into how and where
         * messages are rendered.
         * 
         * Default implementations are provided for rendering messages into the
         * message, messages, and growl widget.
         */
        export interface MessageRender {
            clearMessagesForWidget: (widget: BaseWidget) => void;

            renderMessageForWidget: (widget: BaseWidget, messages: PrimeType.FacesMessage[]) => void;

            /**
             * Renders all given messages in the given containers.
             * @param messages The messages to render.
             * @param containers The container for the messages. Note that this JQuery
             * instance may contain multiple elements -- you should iterate over them. 
             */
            renderMessagesInContainers: (messages: Record<string, PrimeType.FacesMessage[]>, containers: JQuery) => void;
        }

        /**
         * The message-in-dialog hook that lets external scripts subscribe
         * to requests to the core for showing messages within a dialog.
         */
        export interface MessageInDialog {
            /**
             * Shows the given message in a dialog.
             * @param message Message to show.
             * @returns Whether the message was handled. If true, subsequent
             * registered implementations will not be called anymore.
             */
            showMessage: (message: PrimeType.hook.messageInDialog.DialogMessageData) => boolean;
        }
    }

    namespace PrimeType.hook.confirm {
        /**
         * Interface for a confirmation message to show, such as in a confirm
         * popup or a confirm dialog. Used by the {@link ConfirmCoreFeature}.
         */
        export interface ConfirmMessage {
            /**
             * Optional code that is run before the message is shown. Must be valid JavaScript code.
             * It is evaluated via {@link PrimeFaces.csp.eval}.
             */
            beforeShow?: string;

            /**
             * If `true`, the message is escaped for HTML. If `false`, the message is
             * interpreted as an HTML string.
             */
            escape?: boolean;

            /**
             * Header of the dialog message.
             */
            header: string;

            /**
             * Optional icon that is shown to the left of the confirm dialog. When not given, defaults to
             * `ui-icon-alert`. Must be a style class of some icon font.
             */
            icon?: string;

            /**
             * Main content of the dialog message.
             */
            message: string;

            /**
             * The CSS class for the yes (deny) button.
             */
            noButtonClass?: string;

            /**
             * The icon for the no (deny) button, a CSS class to add.
             */
            noButtonIcon?: string;

            /**
             * The label for the no (deny) button.
             */
            noButtonLabel?: string;

            /**
             * Optional type of the message that specified its intended use,
             * e.g. `popup`.
             */
            type?: string;

            /**
             * The CSS class for the yes (confirm) button.
             */
            yesButtonClass?: string;

            /**
             * The icon for the yes (confirm) button, a CSS class to add.
             */
            yesButtonIcon?: string;

            /**
             * The label for the yes (confirm) button.
             */
            yesButtonLabel?: string;
        }

        /**
         * An extended {@link ConfirmMessage} with an additional `source` attribute
         * for specifying the source component or form.
         */
        export interface ExtendedConfirmMessage extends ConfirmMessage {
            /**
             * The source component (command button, AJAX callback etc) that triggered the confirmation.
             * When a string, it is interpreted as the client ID of the component. Otherwise, it must be
             * the main DOM element of the source component.
             */
            source: string | HTMLElement | JQuery;
        }
    }

    namespace PrimeType.hook.dialog {
        /**
         * Interface with the options shared by the open / close methods of the
         * `PrimeFaces.dialog.DialogHandler` and the configuration of the
         * `PrimeFaces.widget.Dialog` widget. 
         */
        export interface SharedDialogOptions {
            /**
             * Whether the dialog can be closed by the user.
             */
            closable: boolean;

            /**
             * Whether the dialog is closed when the user presses the escape button.
             */
            closeOnEscape: boolean;

            /**
             * Whether the dialog is closed when the user clicks on the modal background mask.
             */
            dismissibleMask: boolean;

            /**
             * Whether the dialog is draggable.
             */
            draggable: boolean;

            /**
             * Dialog size might exceed the viewport if the content is taller than viewport in terms
             * of height or wider in terms of width. When this is set to `true`, automatically adjust
             * the height and width to fit the dialog within the viewport.
             */
            fitViewport: boolean;

            /**
             * Used by the dialog framework when showing dialogs in iframes. The IFrame to use.
             */
            iframe: JQuery<HTMLIFrameElement>;

            /**
             * One or more CSS classes for the iframe within the dialog.
             */
            iframeStyleClass: string;

            /**
             * The title of the iframe with the dialog.
             */
            iframeTitle: string;

            /**
             * The height of the dialog in pixels. Can also be a CSS string such as "auto".
             */
            height: number | string;

            /**
             * Whether the dialog is maximizable.
             */
            maximizable: boolean;

            /**
             * Whether the dialog is minimizable.
             */
            minimizable: boolean;

            /**
             * Whether the dialog is modal and blocks the main content and other dialogs.
             */
            modal: boolean;

            /**
             * Defines where the dialog should be displayed
             */
            position: string;

            /**
             * Whether the dialog can be resized by the user.
             */
            resizable: boolean;

            /**
             * Use ResizeObserver to automatically adjust dialog-height after e.g. AJAX-updates. Resizable must be set to false
             * to use this option. (Known limitation: Dialog does not automatically resize yet when resizing the browser
             * window.)
             */
            resizeObserver: boolean;

            /**
             * Can be used together with resizeObserver = true. Centers the dialog again after it was resized to ensure the
             * whole dialog is visible onscreen.
             */
            resizeObserverCenter: boolean;

            /**
             * Whether the dialog is responsive. In responsive mode, the dialog adjusts itself based
             * on the screen width.
             */
            responsive: boolean;

            /**
             * One or more CSS classes for the dialog.
             */
            styleClass: string;

            /**
             * The width of the dialog in pixels. Can also be a CSS string such as "auto".
             */
            width: number | string;
        }

        /**
         * Interface of the configuration object for a dialog of the
         * dialog framework. Used by `PrimeFaces.dialog.DialogHandlerCfg`.
         */
        export interface DialogOptions extends SharedDialogOptions {
            /**
             * `true` to prevent the body from being scrolled, `false` otherwise.
             */
            blockScroll: boolean;

            /**
             * Height of the iframe in pixels.
             */
            contentHeight: number;

            /**
             * Width of the iframe in pixels.
             */
            contentWidth: number;

            /**
             * ID of the header element of the dialog.
             */
            headerElement: string;

            /**
             * Client-side callback to invoke when the dialog is
             * closed. Must be a valid JavaScript expression or statement. The
             * this context will point to the DialogHandler instance.
             */
            onHide: string;

            /**
             * Client-side callback to invoke when the dialog is
             * opened. Must be a valid JavaScript expression or statement. The
             * this context will point to the DialogHandler instance.
             */
            onShow: string;

            /**
             * Widget variable name of the dialog widget to target. 
             */
            widgetVar: string;
        }

        /**
         * Interface of the configuration object for a dialog of the dialog framework.
         * Used by `PrimeFaces.dialog.DialogHandler.openDialog`.
         */
        export interface DialogConfiguration {
            /**
             * The options for the dialog.
             */
            options: Partial<DialogOptions>;

            /**
             * PrimeFaces dialog client ID.
             */
            pfdlgcid: string;

            /**
             * ID of the dialog.
             */
            sourceComponentId: string;

            /**
             * Widget variable of the dialog.
             */
            sourceWidgetVar: string;

            /**
             * Source URL for the IFRAME element with the dialog.
             */
            url: string;
        }
    }

    namespace PrimeType.hook.messageInDialog {
        /**
         * Interface for a message received from the server that is to be shown
         * in a dialog, via the dialog framework.
         */
        export interface DialogMessageData {
            /**
             * If `true`, the message is escaped for HTML. If `false`, the message is
             * interpreted as an HTML string.
             */
            escape: boolean;

            /**
             * A short summary of the message.
             */
            summary: string;

            /**
             * In-depth details of the message.
             */
            detail: string;

            /**
             * The severity of this message, i.e. whether it is an information message, a warning message, or an error
             * message.
             * 
             * This is the stringified representation of a FacesMessage's severity, with
             * the severity level's name and its ordinal value, i.e.:
             * - `INFO 0`
             * - `WARN 1`
             * - `ERROR 2`
             * - `FATAL 3` 
             */
            severity: string;
        }
    }
}

// AJAX types
declare global {
    /**
     * Namespace related to AJAX functionality, such as sending post-back requests
     * to the server and handling the response.
     */
    namespace PrimeType.ajax {
        /**
         * An entry on the {@link JQuery.jqXHR} request object with additional values added by PrimeFaces. For example, when
         * you call `PrimeFaces.current().ajax().addCallbackParam(...)` on the server in a bean method, the added parameters
         * are available in this object. This is also how you can access pass values from the server to the client after
         * calling a remote command. See {@link PrimeFaces.ajax.pfXHR} and {@link PrimeFaces.ab}.
         */
        export type PrimeFacesArgs = Record<string, unknown>;
        /**
         * Additional settings on a {@link JQuery.jqXHR | jqXHR} request, such as portlet forms and nonces.
         */
        export interface PrimeFacesSettings extends JQuery.AjaxSettings<PrimeFacesSettings> {
            /**
             * Selector to resolve all forms which needs to be updated with a new ViewState. This is required in
             * portlets, as the DOM contains forms of multiple Faces views / applications.
             */
            portletForms?: string | null | undefined;
            /**
             * The source that triggered the AJAX request.
             */
            source?: string | HTMLElement | JQuery<HTMLElement> | undefined;
            beforeSend?(this: PrimeFacesSettings, jqXHR: pfXHR, settings: PrimeFacesSettings): false | void;
            nonce?: string;
        }
        /**
         * Callback for an AJAX request that is always called after the request completes, irrespective of whether it
         * succeeded or failed.
         *
         * The parameters passed to this function depend on whether the requested was successful.
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
             * @param pfArgs Additional arguments returned by PrimeFaces, such as AJAX callback params from beans (in
             * case of success); or undefined (in case of an error).
             * @param dataOrXhr Either the XMLDocument (in case of success), or the XHR request (in case of an error).
             */
            (
                this: PrimeFacesSettings,
                xhrOrErrorThrown: unknown,
                status: JQuery.Ajax.TextStatus,
                pfArgs: PrimeFacesArgs | undefined,
                dataOrXhr: XMLDocument | pfXHR
            ) => void;
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
            (this: PrimeFacesSettings, xhr: pfXHR, status: JQuery.Ajax.ErrorTextStatus, errorThrown: string) => void;
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
            (this: AjaxRequest, cfg: Configuration) => boolean;
        /**
         * Callback for an AJAX request that is called when the request succeeds.
         *
         * This is the type of function that you can set as a client side callback for the `onsuccess` attribute of a
         * component or an AJX behavior.
         */
        export type CallbackOnsuccess =
            /**
             * @this The current AJAX settings as they were passed to JQuery when the request was made.
             * @param data The XML document representing the partial response returned the Faces application in response
             * to the faces request. It usually looks like this: `<changes>...</changes>`
             * @param status The type of success, usually `success`.
             * @param xhr The XHR request that succeeded.
             * @return `true` if this handler already handle and/or parsed the response, `false` or `undefined` otherwise.
             */
            (this: PrimeFacesSettings, data: XMLDocument, status: JQuery.Ajax.SuccessTextStatus, xhr: pfXHR) => boolean | undefined;
        /**
         * The XHR request object used by PrimeFaces. It extends the `jqXHR` object as used by JQuery, but adds additional
         * properties specific to PrimeFaces.
         * @typeParam P Data made available by the server via {@link pfXHR.pfArgs}.
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
             * An array with scripts added by PrimeFaces.current().executeScript(...).
             * These will be invoked the same way as the oncomplete attribute of an ajax source.
             */
            pfScripts?: string[];
            /**
             * Additional settings, such as portlet forms and nonces.
             */
            pfSettings?: PrimeFacesSettings;
        }
        /**
         * Represents the data of a PrimeFaces AJAX request. This is the value that is returned by {@link PrimeFaces.ab} and
         * {@link PrimeFaces.ajax.Request.handle}.
         * @typeParam P Record type of the data made available in the property {@link PrimeFaces.ajax.pfXHR.pfArgs} by the
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
             *       <update id="content:jakarta.faces.ViewState:0"><![CDATA[3644438980748093603:2519460806875181703]]></update>
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
            jqXHR: pfXHR<P>;
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
            jqXHR?: pfXHR;
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
         * @typeParam K Name of this parameter.
         * @typeParam V Type of the value of the callback parameter. Please note that it will be converted to string
         * before it is passed to the server.
         */
        export interface RequestParameter<K extends string = string, V = unknown> {
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
         * @typeParam TWidget Type of the widget which
         * triggered the AJAX request.
         */
        export interface UpdateHandler<Widget extends BaseWidget = BaseWidget> {
            /**
             * The widget which triggered the AJAX request.
             */
            widget: Widget;
            /**
             * The handle function which is given the HTML string of the update
             * @param content The new HTML content from the update.
             */
            handle(this: Widget, content: string): void;
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
            async?: boolean;
            /**
             * Delay in milliseconds. If less than this delay elapses between AJAX requests, only the most recent one is
             * sent and all other requests are discarded. If this option is not specified, no delay is used.
             */
            delay?: number;
            /**
             * A PrimeFaces client-side search expression (such as `@widgetVar` or `@(.my-class)` for locating the form
             * to with the input elements that are serialized. If not given, defaults to the enclosing form.
             */
            formId?: string;
            /**
             * Params to be added early when preparing an AJAX request, if
             * {@link PFSettings.earlyPostParamEvaluation | earlyPostParamEvaluation}
             * is enabled. 
             */
            earlyPostParams?: PrimeType.ajax.RequestParameter<string, string | Blob>[];
            /**
             * The AJAX behavior event that triggered the AJAX request.
             */
            event?: string;
            /**
             * Additional options that can be passed when sending an AJAX request to override the current options.
             */
            ext?: Partial<ConfigurationExtender>;
            /**
             * Additional search expression that is added to the `process` option.
             */
            fragmentProcess?: string;
            /**
             * Additional search expression that is added to the `update` option.
             */
            fragmentUpdate?: string;
            /**
             * Whether this AJAX request is global, ie whether it should trigger the global `<p:ajaxStatus />`. Defaults
             * to `true`.
             */
            global?: boolean;
            /**
             * `true` if components with `<p:autoUpdate/`> should be ignored and updated only if specified explicitly
             * in the `update` option; or `false` otherwise. Defaults to `false`.
             */
            ignoreAutoUpdate?: boolean;
            /**
             * Callback that is always called after the request completes, irrespective of whether it succeeded or
             * failed.
             */
            oncomplete?: CallbackOncomplete;
            /**
             * Callback that is called in case any error occurred during the request, such as a a network error. Note
             * that this is not called for errors in the application logic, such as when bean validation fails.
             */
            onerror?: CallbackOnerror;
            /**
             * Callback that is called before the request is sent. Return `false` to cancel the request.
             */
            onstart?: CallbackOnstart;
            /**
             * Callback that is called when the request succeeds.
             */
            onsuccess?: CallbackOnsuccess;
            /**
             * Additional parameters that are passed to the server. These can be accessed as follows:
             *
             * ```java
             * final String myParam = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("myParam");
             * ```
             */
            params?: RequestParameter[];
            /**
             * `true` to perform a partial submit and not send the entire form data, but only the processed components;
             * or `false` to send the entire form data. Defaults to `false`.
             */
            partialSubmit?: boolean;
            /**
             * A CSS selector for finding the input elements of partially processed components. Defaults to `:input`.
             */
            partialSubmitFilter?: string;
            /**
             * A (client-side) PrimeFaces search expression for the components to process in the AJAX request.
             */
            process?: string;
            /**
             * A promise object that is resolved when the AJAX request is complete. You can use this option to register
             * a custom callback. Please note that usually you do not have to set this option explicitly, you can use the
             * return value of {@link PrimeFaces.ab} or {@link PrimeFaces.ajax.Request.handle}. It will create a new promise
             * object when none was provided, and return that.
             */
            promise?: JQuery.Deferred<ResponseData>;
            /**
             * `true` if the AJAX request is a reset request that resets the value of all form elements to their
             * initial values, or `false` otherwise. Defaults to `false`.
             */
            resetValues?: boolean;
            /**
             * `true` if child components should be skipped for the AJAX request, `false` otherwise. Used only by a few
             * specific components.
             */
            skipChildren?: boolean;
            /**
             * The source that triggered the AJAX request. Either a client ID or
             * an (HTML) element.
             */
            source?: string | JQuery<HTMLElement> | HTMLElement;
            /**
             * Set a timeout (in milliseconds) for the request. A value of 0 means there will be no timeout.
             */
            timeout?: number;
            /**
             * A (client-side) PrimeFaces search expression for the components to update in the AJAX request.
             */
            update?: string;
        }
        /**
         * Additional options that can be passed when sending an AJAX request to override the current options.
         */
        export type ConfigurationExtender = Pick<Configuration, "update" | "process" | "onstart" | "params" | "partialSubmit" | "onerror" | "onsuccess" | "oncomplete"> & {
            /**
             * If given, this function is called once for each component. It is passed that serialized values for the
             * component and should return the filtered values that are to be sent to the server. If not given, no
             * values are filtered, and all values are send to the server.
             * @param componentPostParams The serialized values of a component.
             * @return The filtered values that are to be sent to the server.
             */
            partialSubmitParameterFilter?: (this: AjaxRequest, componentPostParams: JQuery.NameValuePair[]) => JQuery.NameValuePair[];
        };
        /**
         * Mapping from articulate to shortname names for the options passed to AJAX calls made by PrimeFaces.
         */
        export interface ArticulateToShorthandConfigurationMap {
            source: "s";
            formId: "f";
            process: "p";
            update: "u";
            event: "e";
            async: "a";
            global: "g";
            delay: "d";
            timeout: "t";
            skipChildren: "sc";
            ignoreAutoUpdate: "iau";
            partialSubmit: "ps";
            partialSubmitFilter: "psf";
            resetValues: "rv";
            fragmentProcess: "fp";
            fragmentUpdate: "fu";
            params: "pa";
            onstart: "onst";
            onerror: "oner";
            onsuccess: "onsu";
            oncomplete: "onco";
        }
        /**
         * Mapping from articulate to shortname names for the options passed to AJAX calls made by PrimeFaces.
         */
        export type ShorthandToArticulateConfigurationMap = PrimeType.InvertRecord<ArticulateToShorthandConfigurationMap>;

        /**
         * Options passed to AJAX calls made by PrimeFaces. This is the same as `Configuration`, but with shorter
         * option names and is used mainly by the method `PrimeFaces.ab`. See `Configuration` for a detailed description
         * of these options.
         */
        export type ShorthandConfiguration = RenameKeys<Configuration, ArticulateToShorthandConfigurationMap>;

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
         * @typeParam T Record type with the param names and the corresponding param values.
         * @return An array type of {@link PrimeFaces.ajax.RequestParameter | request parameters} where the `name` can be
         * one of the keys of `T` and the `value` is the corresponding value from `T`. Array values are mapped to the item
         * type, so that `RemoteCommandParams<{names: string[]}>` is the same as `RemoteCommandParams<{names: string}>`.
         * This is done because multiple values for the same name should be send by including multiple items in the request
         * callback parameter array.
         */
        export type RemoteCommandParams<T extends Record<string, unknown> = Record<string, unknown>> = {
            [P in keyof T]: P extends string ? RequestParameter<P, T[P] extends (infer R)[] ? R : T[P]> : never;
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
         * @typeParam T Object type with the param names and the corresponding param values.
         * @typeParam R Object type of the data returned by the remote command.
         */
        export type RemoteCommand<T extends Record<string, unknown> = Record<string, unknown>, R extends PrimeFacesArgs = PrimeFacesArgs> =
            /**
             * @param params Optional parameters that are passed to the remote command.
             * @return A promise that is settled when the remote command it complete. It is resolved with the data received
             * from the server, and rejected when a network or server error occurred.
             */
            (params?: RemoteCommandParams<T>) => Promise<ResponseData<R>>;
    }
}

// Validation types
declare global {
    /**
     * Namespace for functionality related to validation, such as client-side
     * validation logic.
     */
    namespace PrimeType.validation {
        /**
         * Type of the value of an element that is about to be submitted, passed
         * e.g. to {@link Converter.convert}.
         */
        export type SubmittedValue = string | number | string[] | FileList | null;

        /**
         * Type of the parameters when formatting messages with placeholders such
         * as `{0}` or `{2}`. Can be either a string or a number. Non-strings
         * are converted to strings as appropriate. See e.g.
         * {@link ValidationUtils.getMessage getMessage}
         */
        export type MessageFormatParameter = string | number;

        /**
         * A converter for converting string values to the correct data type.
         * @typeParam T Type of the converted value returned by the converter.
         */
        export interface Converter<T = unknown> {
            /**
             * Converts a string value to the correct data type.
             * @param element Element for which the value was submitted.
             * @param submittedValue The submitted string value
             * @return The converted value, `null` when no value is present.
             */
            convert(element: JQuery, submittedValue: SubmittedValue): T | null;
        }

        /**
         * A validator for checking whether the value of an element confirms to certain restrictions.
         */
        export interface Validator {
            /**
             * Validates the given element. If it is not valid, an error message
             * of type {@link BaseFacesMessage} (or an array thereof) should be
             * thrown (via a `throw ...` statement).
             * @param element Element to validate
             * @param value Current value of the element
             * @throws The error message when the element with its current value
             * is not valid. Can be either a {@link BaseFacesMessage} or an
             * array thereof.
             */
            // TODO: Would it not be more idiomatic to create a new class
            // ValidationError that extends from Error and throw that?
            validate(element: JQuery, value?: unknown): void;
        }

        /**
         * The validation result.
         */
        export interface ValidationResult {
            /**
             * A map between the client ID of an element and a list of faces message for that element.
             */
            messages: Record<string, FacesMessage[]>;
            /**
             * If the result is valid / if it has any validation errors.
             */
            valid: boolean;
            /**
             * If the result has any unrendered message.
             */
            hasUnrenderedMessage: boolean;
        }

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
             * @param element An element to unhighlight.
             */
            unhighlight(element: JQuery): void;
        }

        /**
         * The options that can be passed to the Validation method. Note that you do not have to provide a value
         * for all these property. Most methods such as `PrimeFaces.vb` have got sensible defaults in case you
         * do not.
         */
        export interface Configuration {
            /**
             * The source that triggered the validation.
             */
            source?: string | JQuery | HTMLElement;
            /**
             * `true` if the validation is triggered by AJAXified component. Defaults to `false`.
             */
            ajax?: boolean;
            /**
             * A (client-side) PrimeFaces search expression for the components to process in the validation.
             */
            process?: string;
            /**
             * A (client-side) PrimeFaces search expression for the components to update in the validation.
             */
            update?: string;
            /**
             * `true` if invalid elements should be highlighted as invalid. Default is `true`.
             */
            highlight?: boolean;
            /**
             * `true` if the first invalid element should be focussed. Default is `true`.
             */
            focus?: boolean;
            /**
             * `true` to log messages that do not have a valid target and are thus not shown on the UI.
             */
            logUnrenderedMessages?: boolean;
            /**
             * `true` if messages should be rendered. Default is `true`.
             */
            renderMessages?: boolean;
            /**
             * `true` if invisible elements should be validated. Default is `false`.
             */
            validateInvisibleElements?: boolean;
        }

        /**
         * A map with all registered validator. Key is the name of the validator,
         * the value is the validator implementation.
         * 
         * @implNote This really should be just a `Record<string, Validator>`.
         * However, for legacy reasons, PrimeFaces also includes the
         * {@link ValidationHighlighter} in this "map" as
         * `PrimeFaces.validation.Highlighter`. A Validator is incompatible with
         * a ValidationHighlighter, so we need to spell out the keys explicitly.
         */
        export interface ValidatorInstanceMap {
            /**
             * When an element is invalid due to a validation error, the user needs to be informed. This highlighter is
             * responsible for changing the visual state of an element so that the user notices the invalid element.
             */
            Highlighter: ValidationHighlighter;
        }

        /**
         * Options passed to `PrimeFaces.vb` as shortcut. This is the same as `Configuration`, but with shorter
         * option names and is used mainly by the method `PrimeFaces.vb`. See `Configuration` for a detailed description
         * of these options.
         */
        export type ShorthandConfiguration = RenameKeys<Configuration, {
            source: "s";
            ajax: "a";
            process: "p";
            update: "u";
            highlight: "h";
            focus: "f";
            renderMessages: "r";
            validateInvisibleElements: "v";
        }>;
    }
}

// Base widget types
declare global {
    namespace PrimeType {
        /**
         * Shortcut for the type of a specific widget. For example, if you want to
         * write a function that takes a single parameter named `commandButton`
         * that must be a CommandButton instance:
         * 
         * ```ts
         * function processCommandButton(widget: PrimeType.Widget<"CommandButton">): void {
         *   // do something with command button
         * }
         * ```
         */
        export type Widget<Name extends keyof WidgetRegistry> = InstanceType<PrimeType.WidgetRegistry[Name]>;

        /**
         * A map of all instantiated widgets that are available on the current page.
         */
        export type WidgetInstanceMap = Record<string, BaseWidget<any>>;

        /**
         * This interface contains the mapping between the name of a widget type and its implementing class. The key
         * is the name of the widget, the value the class (constructor) of the widget. Please note that widgets are
         * usually created by the PrimeFaces framework and should not be created manually.
         * 
         * At runtime, the widget registry is available via `PrimeFaces.widget`, see {@link PrimeFaces.widget} for
         * more details.
         * 
         * If you are authoring custom widgets, you can make your widgets available to consumer by extending this
         * interface:
         * 
         * ```ts
         * import { YourWidget } from "./src/widgets/your-widget.js";
         * declare global {
         *   namespace PrimeType {
         *     export interface WidgetRegistry {
         *       YourWidget: typeof YourWidget;
         *     }
         *   }
         *   namespace PrimeType.widget {
         *     export type YourWidgetCfg = import("./src/widgets/your-widget.js").YourWidgetCfg;
         *   }
         * }
         * ```  
         */
        export interface WidgetRegistry { }
    }
    namespace PrimeType.widget {
        /**
         * Gets the configuration of a widget.
         * 
         * ```ts
         * // PrimeType.widget.AjaxStatusCfg
         * type Cfg = PrimeType.widget.WidgetCfg<PrimeType.WidgetRegistry["AjaxStatus"]>
         * ```
         */
        export type WidgetCfg<Widget extends PrimeType.Newable<[], BaseWidget<any>>> = Widget extends PrimeType.Newable<[], BaseWidget<infer Cfg>> ? Cfg : never;

        /**
         * Interface of the context menu widget configuration available to other
         * widgets that wish to provide custom context menu integration.
         * 
         * Other widgets can implement the
         * {@link BaseWidget.bindContextMenu bindContextMenu} method to do so.
         * If they do, they get passed the widget instance and its configuration.
         */
        export interface ContextMenuLikeWidgetCfg extends BaseWidgetCfg {
            /**
             * Event that triggers this context menu, usually a (right) mouse click.
             */
            event: string;

            /**
             * Type of the target nodes to attach to. Used e.g. by the tree table widget.
             * 
             * TreeTable has special integration with context menu, you can even
             * match different context menus with different tree nodes using
             * this `nodeType` option of context menu that matches the tree node
             * type.
             */
            nodeType: string;

            /**
             * Type of the selection mode, used e.g. by the data table widget.
             * 
             * DataTable has special integration with the context menu.
             */
            selectionMode: "single" | "multiple";
        }

        /**
         * Interface of the context menu widget available to other widgets that
         * wish to provide custom context menu integration. 
         * 
         * Other widgets can implement the
         * {@link BaseWidget.bindContextMenu bindContextMenu} method to do so.
         * If they do, they get passed the widget instance and its configuration.
         */
        export interface ContextMenuLikeWidget extends BaseWidget<ContextMenuLikeWidgetCfg> {
            show(e: JQuery.TriggeredEvent): void;
            hide(): void;
        }

        /**
         * Configuration for a {@link BaseWidget widget} with the dynamic overlay feature. The
         * {@link DynamicOverlayFeatureWidgetCfg.appendTo | appendTo} attribute specifies a target
         * where the overlay should be appended to.
         */
        export interface DynamicOverlayFeatureWidgetCfg extends BaseWidgetCfg {
            /**
             * The search expression for the element to which the overlay panel should be appended. Defaults to the
             * body.
             */
            appendTo: string | null;
        }

        /**
         * A {@link BaseWidget widget} with the dynamic overlay feature. The {@link DynamicOverlayFeatureWidgetCfg.appendTo | appendTo}
         * attribute specifies a target where the overlay should be appended to.
         * @typeParam Cfg Type of the widget configuration.
         */
        export interface DynamicOverlayFeatureWidget<Cfg extends DynamicOverlayFeatureWidgetCfg> extends BaseWidget<Cfg> { }

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
         */
        export type Behavior =
            /**
             * @this This callback takes the widget instance as the this context. This must be the widget instance that owns
             * the behavior. The type is only required to be a {@link BaseWidget} as only common widget properties such as
             * its ID are used.
             * @param ext Additional data to be sent with the AJAX request that is made to the server.
             */
            (this: BaseWidget, ext?: Partial<ajax.ConfigurationExtender>) => void;

        /**
         * The widget configuration of each widget may contain only some of the declared properties. For example, when the
         * value of a property is equal to the default value, it is not transmitted from the server to the client. Only the
         * two properties `id` and `widgetVar` are guaranteed to be always available.
         * @typeParam Cfg Type of a widget configuration. It must have at least the two properties `id` and `widgetVar`.
         * @return A new type with all properties in the given type made optional, except for `id` and `widgetVar`.
         */
        export type PartialWidgetCfg<Cfg extends {
            id: string | string[];
            widgetVar: string;
        }> = Partial<Omit<Cfg, "id" | "widgetVar">> & Pick<Cfg, "id" | "widgetVar">;

        /**
         * Same as {@link PartialWidgetCfg}, but with the widgetVar being optional. Used by the `PrimeFaces.cw` function
         * when creating widgets, which receives the widget variable explicitly as a separate argument.
         * @typeParam Cfg Type of a widget configuration. It must have at least the on property `id`.
         * @return A new type with all properties in the given type made optional, except for `id`.
         */
        export type PartialCreateWidgetCfg<Cfg extends {
            id: string | string[];
            widgetVar: string;
        }> = Partial<Omit<Cfg, "id">> & Pick<Cfg, "id">;

        /**
         * A destroy listener for a PrimeFaces widget. It is invoked when the
         * widget is removed, such as during AJAX updates. Use {@link BaseWidget.addDestroyListener} to add a destroy
         * listener.
         * @typeParam Widget The type of the widget that is being destroyed.
         */
        export type DestroyListener<Widget> =
            /**
             * @param widget The widget that is being destroyed.
             */
            (this: Widget, widget: Widget) => void;

        /**
         * A refresh listener for a PrimeFaces widget. It is invoked when the
         * widget is reloaded, such as during AJAX updates. Use {@link BaseWidget.addRefreshListener} to add a refresh
         * listener.
         * @typeParam TWidget The type of the widget that is being refreshed.
         */
        export type RefreshListener<Widget> =
            /**
             * @param widget The widget that is being refreshed.
             */
            (this: Widget, widget: Widget) => void;

        /**
         * A callback for a PrimeFaces widget. An optional callback that is invoked before a widget is created, at the
         * beginning of the {@link BaseWidget.init | init} method. This is usually specified via the `widgetPreConstruct`
         * attribute on the Faces component.
         * @param The widget configuration.
         */
        export type PreConstructCallback =
            /**
             * @param widgetCfg The configuration of the widget that is about to be constructed.
             */
            (this: null, widgetCfg: PrimeType.widget.PartialWidgetCfg<import("./src/core/core.widget.js").BaseWidgetCfg>) => void;

        /**
         * An optional callback that is invoked before a widget is about to be
         * destroyed, e.g., when the component was removed at the end of an AJAX update. This is called at the beginning
         * of the {@link BaseWidget.destroy | destroy} method. This is usually specified via the `widgetPreDestroy`
         * attribute on the Faces component.
         */
        export type PreDestroyCallback =
            /**
             * @param widget The widget that is about to be destroyed.
             */
            (this: BaseWidget, widget: BaseWidget) => void;

        /**
         * An optional callback that is invoked after a widget was refreshed
         * after an AJAX update, at the end of the {@link BaseWidget.refresh | refresh} method. This is usually specified
         * via the `widgetPostRefresh` attribute on the Faces component.
         */
        export type PostRefreshCallback =
            /**
             * @param widget The widget that was refreshed.
             */
            (this: BaseWidget, widget: BaseWidget) => void;

        /**
         * A callback for a PrimeFaces widget. An optional callback that is
         * invoked after a widget was created successfully, at the end of the {@link BaseWidget.init | init} method. This is
         * usually specified via the `widgetPostConstruct` attribute on the Faces component. Note that this is also called
         * during a `refresh` (AJAX update).
         */
        export type PostConstructCallback =
            /**
             * @param widget The widget that was constructed.
             */
            (this: BaseWidget, widget: BaseWidget) => void;

    }
}

// Locale types
declare global {
    namespace PrimeType {
        export interface LocaleAria {
            "cancelEdit"?: string;
            "close"?: string;
            "collapseLabel"?: string;
            "collapseRow"?: string;
            "editRow"?: string;
            "expandLabel"?: string;
            "expandRow"?: string;
            "falseLabel"?: string;
            "filterConstraint"?: string;
            "filterOperator"?: string;
            "firstPageLabel"?: string;
            "gridView"?: string;
            "hideFilterMenu"?: string;
            "jumpToPageDropdownLabel"?: string;
            "jumpToPageInputLabel"?: string;
            "lastPageLabel"?: string;
            "listLabel"?: string;
            "listView"?: string;
            "maximizeLabel"?: string;
            "minimizeLabel"?: string;
            "moveAllToSource"?: string;
            "moveAllToTarget"?: string;
            "moveBottom"?: string;
            "moveDown"?: string;
            "moveToSource"?: string;
            "moveToTarget"?: string;
            "moveTop"?: string;
            "moveUp"?: string;
            "navigation"?: string;
            "next"?: string;
            "nextPageLabel"?: string;
            "nullLabel"?: string;
            "pageLabel"?: string;
            "otpLabel"?: string;
            "passwordHide"?: string;
            "passwordShow"?: string;
            "previous"?: string;
            "prevPageLabel"?: string;
            "rotateLeft"?: string;
            "rotateRight"?: string;
            "rowsPerPageLabel"?: string;
            "saveEdit"?: string;
            "scrollTop"?: string;
            "selectColor"?: string;
            "selectAll"?: string;
            "selectLabel"?: string;
            "selectRow"?: string;
            "showFilterMenu"?: string;
            "slide"?: string;
            "slideNumber"?: string;
            "star"?: string;
            "stars"?: string;
            "trueLabel"?: string;
            "unselectAll"?: string;
            "unselectLabel"?: string;
            "unselectRow"?: string;
            "zoomImage"?: string;
            "zoomIn"?: string;
            "zoomOut"?: string;
            "datatable.sort.ASC"?: string;
            "datatable.sort.DESC"?: string;
            "datatable.sort.NONE"?: string;
            "colorpicker.OPEN"?: string;
            "colorpicker.CLOSE"?: string;
            "colorpicker.CLEAR"?: string;
            "colorpicker.MARKER"?: string;
            "colorpicker.HUESLIDER"?: string;
            "colorpicker.ALPHASLIDER"?: string;
            "colorpicker.INPUT"?: string;
            "colorpicker.FORMAT"?: string;
            "colorpicker.SWATCH"?: string;
            "colorpicker.INSTRUCTION"?: string;
            "spinner.INCREASE"?: string;
            "spinner.DECREASE"?: string;
            "switch.ON"?: string;
            "switch.OFF"?: string;
            "messages.ERROR"?: string;
            "messages.FATAL"?: string;
            "messages.INFO"?: string;
            "messages.WARN"?: string;
        }

        /**
         * An object with all localized strings required on the client side.
         */
        export interface Locale {
            accept?: string;
            addRule?: string;
            am?: string;
            apply?: string;
            cancel?: string;
            choose?: string;
            chooseDate?: string;
            chooseMonth?: string;
            chooseYear?: string;
            clear?: string;
            completed?: string;
            contains?: string;
            custom?: string;
            dateAfter?: string;
            dateBefore?: string;
            dateFormat?: string;
            dateIs?: string;
            dateIsNot?: string;
            dayNames?: [
                string,
                string,
                string,
                string,
                string,
                string,
                string
            ];
            dayNamesMin?: [
                string,
                string,
                string,
                string,
                string,
                string,
                string
            ];
            dayNamesShort?: [
                string,
                string,
                string,
                string,
                string,
                string,
                string
            ];
            decimalSeparator?: string;
            emptyFilterMessage?: string;
            emptyMessage?: string;
            emptySearchMessage?: string;
            emptySelectionMessage?: string;
            endsWith?: string;
            equals?: string;
            fileSizeTypes: ["B", "KB", "MB", "GB", "TB", "PB", "EB", "ZB", "YB"],
            filter?: string;
            firstDayOfWeek: number,
            groupingSeparator?: string;
            gt?: string;
            gte?: string;
            lt?: string;
            lte?: string;
            matchAll?: string;
            matchAny?: string;
            medium?: string;
            monthNames: [
                string,
                string,
                string,
                string,
                string,
                string,
                string,
                string,
                string,
                string,
                string,
                string
            ];
            monthNamesShort: [
                string,
                string,
                string,
                string,
                string,
                string,
                string,
                string,
                string,
                string,
                string,
                string
            ];
            nextDecade?: string;
            nextHour?: string;
            nextMinute?: string;
            nextMonth?: string;
            nextSecond?: string;
            nextYear?: string;
            noFilter?: string;
            notContains?: string;
            notEquals?: string;
            now?: string;
            passwordPrompt?: string;
            pending?: string;
            pm?: string;
            prevDecade?: string;
            prevHour?: string;
            prevMinute?: string;
            prevMonth?: string;
            prevSecond?: string;
            prevYear?: string;
            reject?: string;
            removeRule?: string;
            searchMessage?: string;
            selectionMessage?: string;
            showMonthAfterYear: boolean;
            startsWith?: string;
            strong?: string;
            today?: string;
            upload?: string;
            weak?: string;
            weekHeader?: string;
            weekNumberTitle?: string;
            isRTL: boolean,
            yearSuffix: string;
            timeOnlyTitle?: string;
            timeText?: string;
            hourText?: string;
            minuteText?: string;
            secondText?: string;
            millisecondText?: string;
            year?: string;
            month?: string;
            week?: string;
            day?: string;
            list?: string;
            allDayText?: string;
            moreLinkText?: string;
            noEventsText?: string;
            aria: LocaleAria;

            unexpectedError: string;

            /**
             * Messages for client-side validation.
             */
            messages?: Record<string, string>;

            // legacy
            closeText?: string;
            prevText?: string;
            nextText?: string;
            firstDay?: number;
            currentText?: string;
            ampm?: boolean;

            // generic
            [i18nKey: string]: unknown;
        }

        /**
         * Map from the name of a locale to the localized values for that value.
         */
        export type Locales = Record<string, Locale>;
    }
}

// AJAX status widget
declare global {
    namespace PrimeType.widget.AjaxStatus {
        /**
         * Available
         * types of AJAX related events to which you can listen.
         */
        export type AjaxStatusEventType = "start" | "success" | "error" | "complete" | "facesComplete";

        /**
         * Callback for when an AJAX request starts. Usually set via
         * `<p:ajaxStatus onstart="..."/>`. This callback applies when `<p:ajax />` is used.
         */
        export type PfAjaxStartCallback = (this: Document, xhr: JQuery.TriggeredEvent, ...args: unknown[]) => void;

        /**
         * Callback for when an AJAX request succeeds. Usually set
         * via `<p:ajaxStatus onsuccess="..."/>`. This callback applies when `<p:ajax />` is used.
         */
        export type PfAjaxSuccessCallback =
            /**
             * @param xhr The request that succeeded.
             * @param settings The settings of the jQuery
             * AJAX request.
             */
            (this: Document, xhr: JQuery.jqXHR, settings: JQuery.AjaxSettings) => void;

        /**
         * Callback for when an AJAX request fails. Usually set via
         * `<p:ajaxStatus onerror="..."/>`. This callback applies when `<p:ajax />` is used.
         */
        export type PfAjaxErrorCallback =
            /**
             * @param xhr The request that failed.
             * @param settings The settings of the jQuery
             * AJAX request.
             * @param errorThrown The error that cause the request to
             * fail.
             */
            (this: Document, xhr: JQuery.jqXHR, settings: JQuery.AjaxSettings, errorThrown: string) => void;

        /**
         * Callback for when an AJAX request completes, either
         * successfully or with an error. Usually set via `<p:ajaxStatus oncomplete="..."/>`. This callback applies when
         * `<p:ajax />` is used.
         */
        export type PfAjaxCompleteCallback =
            /**
             * @param xhr The request that succeeded.
             * @param settings The settings of the jQuery
             * AJAX request.
             */
            (this: Document, xhr: JQuery.jqXHR, settings: JQuery.AjaxSettings, args: PrimeType.ajax.PrimeFacesArgs) => void;

        /**
         * Maps between the
         * {@link AjaxStatusEventType} and the corresponding event handlers. Used by the {@link AjaxStatus} component.
         */
        export interface EventToCallbackMap {
            /**
             * Callback for when an AJAX request completes, either successfully or with an error. Usually set via
             * `<p:ajaxStatus oncomplete="..."/>`.
             */
            complete: PfAjaxCompleteCallback | faces.ajax.OnEventCallback | faces.ajax.OnErrorCallback;
            /**
             * Callback
             * for when an AJAX request fails. Usually set via `<p:ajaxStatus onerror="..."/>`.
             */
            error: PfAjaxErrorCallback | faces.ajax.OnErrorCallback;
            /**
             * Callback for when an AJAX request completes, either successfully or with an error. Usually set via
             * `<p:ajaxStatus oncomplete="..."/>`.
             */
            facesComplete: PfAjaxCompleteCallback | faces.ajax.OnEventCallback | faces.ajax.OnErrorCallback;
            /**
             * Callback
             * for when an AJAX request starts. Usually set via `<p:ajaxStatus onstart="..."/>`.
             */
            start: PfAjaxStartCallback | faces.ajax.OnEventCallback;
            /**
             * Callback for when an AJAX request succeeds. Usually set via `<p:ajaxStatus onsuccess="..."/>`.
             */
            success: PfAjaxSuccessCallback | faces.ajax.OnEventCallback;
        }
    }
}

// AJAX exception handler widget
declare global {
    namespace PrimeType.widget.AjaxExceptionHandler {
        /**
         * Callback invoked when an AJAX request fails.
         */
        export type OnExceptionCallback =
            /**
             * @param errorName Name of the AJAX error.
             * @param errorMessage Message with details regarding the AJAX error.
             */
            (this: AjaxExceptionHandler, errorName: string, errorMessage: string) => void;
    }
}

// Poll widget
declare global {
    namespace PrimeType.widget.Poll {
        /**
         * Time unit for the polling interval.
         */
        export type IntervalType = "millisecond" | "second";
        /**
         * Optional callback invoked when polling starts. See {@link PollCfg.onActivated}.
         */
        export type OnActivatedCallback =
            /**
             * @returns `false` to cancel polling, any other value to proceed.
             */
            (this: Poll) => boolean | undefined | void;
        /**
         * Optional callback invoked when polling stops. See {@link PollCfg.onDeactivated}.
         */
        export type OnDeactivatedCallback =
            /**
             * @returns `false` to continue polling, any other value to stop.
             */
            (this: Poll) => boolean | undefined | void;
        /**
         * Callback that performs the polling action. See also
         * {@link PollCfg.fn}.
         */
        export type PollingAction = () => void;
    }
}

// Re-exports
declare global {
    namespace PrimeType {
        /**
         * The class with functionality related to multiple window support in PrimeFaces applications.
         */
        export type ClientWindow = import("./src/core/core.clientwindow.js").ClientWindow;
        /**
         * The class with functionality related to handling the `script-src` directive of the HTTP `Content-Security-Policy`
         * (CSP) policy. This makes use of a nonce (number used once). The server must generate a unique nonce value each
         * time it transmits a policy. 
         */
        export type Csp = import("./src/core/core.csp.js").Csp;
        /**
         * The class with functionality related to the browser environment, such as information about the current browser.
         */
        export type Environment = import("./src/core/core.env.js").Environment;
        /**
         * The class with functionality related to working with search expressions.
         */
        export type Expressions = import("./src/core/core.expressions.js").Expressions;
        /**
         * The class with functionality related to handling resources on the server, such as CSS and JavaScript files.
         */
        export type Resources = import("./src/core/core.resources.js").Resources;
        /**
         * The class with various utilities needed by PrimeFaces.
         */
        export type Utils = import("./src/core/core.utils.js").Utils;
        /**
         * __PrimeFaces Client Side Validation Framework__
         * 
         * The class for enabling client side validation of form fields.
         */
        export type Validation = import("./src/validation/validation.common.js").Validation;
    }

    namespace PrimeType.ajax {
        /**
         * The class with functionality related to sending and receiving AJAX requests that are made by PrimeFaces. Each
         * request receives an XML response, which consists of one or multiple actions that are to be performed. This
         * includes creating new DOM elements, deleting or updating existing elements, or executing some JavaScript.
         */
        export type Ajax = import("./src/core/core.ajax.js").Ajax;
        /**
         * This class contains functionality related to queuing AJAX requests to ensure that they are (a) sent in the
         * proper order and (b) that each response is processed in the same order as the requests were sent.
         */
        export type AjaxQueue = import("./src/core/core.ajax.js").AjaxQueue;
        /**
         * The class containing low-level functionality related to sending AJAX requests.
         */
        export type AjaxRequest = import("./src/core/core.ajax.js").AjaxRequest;
        /**
         * The class containing low-level functionality related to handling AJAX responses. Note that
         * the different types of AJAX actions are handled by the {@link AjaxResponseProcessor PrimeFaces.ajax.ResponseProcessor}.
         */
        export type AjaxResponse = import("./src/core/core.ajax.js").AjaxResponse;
        /**
         * The class containing low-level functionality related to processing the different types
         * of actions from AJAX responses.
         */
        export type AjaxResponseProcessor = import("./src/core/core.ajax.js").AjaxResponseProcessor;
        /**
         * The class containing utility methods for AJAX requests, primarily used internally.
         */
        export type AjaxUtils = import("./src/core/core.ajax.js").AjaxUtils;
    }

    namespace PrimeType.expressions {
        /**
         * The class providing the entry point for functions related to search expressions. 
         */
        export type SearchExpressionFacade = import("./src/core/core.expressions.js").SearchExpressionFacade;
    }

    namespace PrimeType.validation {
        /**
         * The class that contains functionality related to handling faces messages, especially validation errror messages.
         * Contains methods for clearing message of an element or adding messages to an element.
         */
        export type ValidationContext = import("./src/validation/validation.common.js").ValidationContext;
        /**
         * When an element is invalid due to a validation error, the user needs to be informed. This highlighter is
         * responsible for changing the visual state of an element so that the user notices the invalid element.
         */
        export type ValidationHighlighter = import("./src/validation/validation.highlighters.js").ValidationHighlighter;
        /**
         * The class with mostly internal utility methods used to validate data on the client.
         */
        export type ValidationUtils = import("./src/validation/validation.common.js").ValidationUtils;
    }
}

// Module augmentation
declare global {
    // Extend PrimeFaces namespace with core methods and properties
    namespace PrimeType {
        export interface PrimeFaces extends Core { }
    }

    // Extend Window global
    namespace PrimeType {
        export interface WindowExtensions {
            Cookies: typeof Cookies;
            PrimeFaces: PrimeFaces;
            PF: typeof _PF;
        }
    }

    // Extend widget registry (available widget types)
    namespace PrimeType {
        export interface WidgetRegistry {
            AjaxExceptionHandler: typeof AjaxExceptionHandler;
            AjaxStatus: typeof AjaxStatus;
            BaseWidget: typeof BaseWidget;
            DeferredWidget: typeof DeferredWidget;
            DynamicOverlayWidget: typeof DynamicOverlayWidget;
            Poll: typeof Poll;
        }
    }

    // Extend hook registry
    namespace PrimeType {
        export interface HookRegistry {
            /**
             * The confirm feature that lets external scripts subscribe to emitted
             * confirmation messages. Implementations usually make use of this
             * feature to show the confirmation message to the user.
             * 
             * The default implementation is provided by either the ConfirmDialog
             * widget or PrimeFaces's dialog framework.
             */
            confirm: hook.Confirm;

            /**
             * The dialog feature. Lets external scripts react to requests for
             * opening and closing a dialog.
             * 
             * The default implementation is provided by PrimeFace's dialog framework.
             */
            dialog: hook.Dialog;

            /**
             * The kill switch features. Lets external scripts react to a kill signal
             * and stop ongoing operations, such as AJAX requests, polling operations
             * etc.
             */
            killSwitch: hook.KillSwitch;

            /**
             * The message render hook. Lets external code hook into how and where
             * messages are rendered.
             * 
             * Default implementations are provided for rendering messages into the
             * message, messages, and growl widget.
             */
            messageRender: hook.MessageRender;

            /**
             * The message-in-dialog feature that lets external scripts subscribe
             * to requests to the core for showing messages within a dialog.
             * 
             * The default implementation is provided by PrimeFaces's dialog framework.
             */
            messageInDialog: hook.MessageInDialog;
        }
    }

    // Extend validators (common)
    namespace PrimeType.validation {
        interface ValidatorInstanceMap {
            "jakarta.faces.Length"?: Validator;
            "jakarta.faces.LongRange"?: Validator;
            "jakarta.faces.DoubleRange"?: Validator;
            "jakarta.faces.RegularExpression"?: Validator;
            "primefaces.File"?: Validator;
        }
    }

    // Extend validators (bean validation)
    namespace PrimeType.validation {
        interface ValidatorInstanceMap {
            "AssertFalse"?: Validator;
            "AssertTrue"?: Validator;
            "DecimalMax"?: Validator;
            "DecimalMin"?: Validator;
            "Digits"?: Validator;
            "Email"?: Validator;
            "Future"?: Validator;
            "FutureOrPresent"?: Validator;
            "Max"?: Validator;
            "Min"?: Validator;
            "Negative"?: Validator;
            "NegativeOrZero"?: Validator;
            "NotBlank"?: Validator;
            "NotEmpty"?: Validator;
            "NotNull"?: Validator;
            "Null"?: Validator;
            "Past"?: Validator;
            "PastOrPresent"?: Validator;
            "Pattern"?: Validator;
            "Positive"?: Validator;
            "PositiveOrZero"?: Validator;
            "Size"?: Validator;
        }
    }

    // Extend widget configurations
    namespace PrimeType.widget {
        export type AjaxStatusCfg = import("./src/ajaxstatus/ajaxstatus.js").AjaxStatusCfg;
        export type BaseWidgetCfg = import("./src/core/core.widget.js").BaseWidgetCfg;
        export type DeferredWidgetCfg = import("./src/core/core.widget.js").DeferredWidgetCfg;
        export type DynamicOverlayWidgetCfg = import("./src/core/core.widget.js").DynamicOverlayWidgetCfg;
        export type PollCfg = import("./src/poll/poll.js").PollCfg;
    }

    // Extend globals
    let PrimeFaces: PrimeType.PrimeFaces;
    let PF: typeof _PF;
}
