/**
 * Namespace for the jQuery Idle Timer plugin.
 * 
 * Fires a custom event when the user is "idle". Idle is defined by not...
 * - moving the mouse
 * - scrolling the mouse wheel
 * - using the keyboard
 * 
 * You can use this plugin via `$.idleTimer()` or `$(document).idleTimer()`.
 * 
 * See https://github.com/thorst/jquery-idletimer
 */
declare namespace JQueryIdleTimer {
    /**
     * Interface with all the methods supported by the jQuery Idle Timer plugin. These metods are available on JQuery
     * instances as well as on the static JQuery object:
     * 
     * ```javascript
     * $.idleTimer(); // shortcut for the below
     * $( document ).idleTimer();
     * ```
     */
    export interface IdleTimerMethods {
        /**
         * There are two ways to instantiate. Either statically, or on an element. Element bound timers will only watch for
         * events inside of them. You may just want page-level activity, in which case you may set up your timers on
         * `document`, `document.documentElement`, and `document.body`.
         * 
         * ```javascript
         * $(function() {
         *   // binds to document - shorthand
         *   $.idleTimer();
         *
         *   // binds to document - explicit
         *   $( document ).idleTimer();
         *
         *   // bind to different element
         *   $( "#myTextArea" ).idleTimer();
         * });
         * ```
         * @param element Element to watch, defaults to the document.
         * @param id Unique ID for this idle timer, to support multiple timers on the same page.
         * @return this JQuery instance for chaining.
         */
        idleTimer(element?: Document | HTMLElement, id?: string): this;

        /**
         * There are two ways to instantiate. Either statically, or on an element. Element bound timers will only watch for
         * events inside of them. You may just want page-level activity, in which case you may set up your timers on
         * `document`, `document.documentElement`, and `document.body`.
         * 
         * ```javascript
         * $(function() {
         *   // binds to document - shorthand
         *   $.idleTimer(1000);
         *
         *   // binds to document - explicit
         *   $( document ).idleTimer(1000);
         *
         *   // bind to different element
         *   $( "#myTextArea" ).idleTimer(1000);
         * });
         * ```
         * 
         * @param idleTimeoutMillis The timeout period in milliseconds.
         * @param element Element to watch, defaults to the document.
         * @param id Unique ID for this idle timer, to support multiple timers on the same page.
         * @return this JQuery instance for chaining.
         */
        idleTimer(idleTimeoutMillis: number, element?: Document | HTMLElement, id?: string): this;

        /**
         * There are two ways to instantiate. Either statically, or on an element. Element bound timers will only watch for
         * events inside of them. You may just want page-level activity, in which case you may set up your timers on
         * `document`, `document.documentElement`, and `document.body`.
         * 
         * ```javascript
         * $(function() {
         *   // binds to document - shorthand
         *   $.idleTimer({
         *     timeout:10000, 
         *     idle:true
         *   });
         *
         *   // binds to document - explicit
         *   $( document ).idleTimer({
         *     timeout:10000, 
         *     idle:true
         *   });
         *
         *   // bind to different element
         *   $( "#myTextArea" ).idleTimer({
         *     timeout:10000, 
         *     idle:true
         *   });
         * });
         * ```
         * 
         * @param options The options for this idle timer. Any options not specified explicitly are set to their default
         * values.
         * @param element Element to watch, defaults to the document.
         * @param id Unique ID for this idle timer, to support multiple timers on the same page.
         * @return this JQuery instance for chaining.
         */
        idleTimer(options: Partial<JQueryIdleTimer.IdleTimerOptions>, element?: Document | HTMLElement, id?: string): this;

        /**
         * Stop the timer, removes data, removes event bindings to come back from this you will need to instantiate again.
         * 
         * @param method The method to be invoked on this idle timer instance.
         * @param element Element to watch, defaults to the document.
         * @param id Unique ID for this idle timer, to support multiple timers on the same page.
         * @return this JQuery instance for chaining.
         */
        idleTimer(method: "destroy", element?: Document | HTMLElement, id?: string): this;

        /**
         * Saves the remaining time, and stops the timer.
         * 
         * @param method The method to be invoked on this idle timer instance.
         * @param element Element to watch, defaults to the document.
         * @param id Unique ID for this idle timer, to support multiple timers on the same page.
         * @return this JQuery instance for chaining.
         */
        idleTimer(method: "pause", element?: Document | HTMLElement, id?: string): this;

        /**
         * Starts the timer with remaining time saved when `pause` was called.
         * 
         * @param method The method to be invoked on this idle timer instance.
         * @param element Element to watch, defaults to the document.
         * @param id Unique ID for this idle timer, to support multiple timers on the same page.
         * @return this JQuery instance for chaining.
         */
        idleTimer(method: "resume", element?: Document | HTMLElement, id?: string): this;

        /**
         * Restore initial idle state, and restart the timer.
         * 
         * @param method The method to be invoked on this idle timer instance.
         * @param element Element to watch, defaults to the document.
         * @param id Unique ID for this idle timer, to support multiple timers on the same page.
         * @return this JQuery instance for chaining.
         */
        idleTimer(method: "reset", element?: Document | HTMLElement, id?: string): this;

        /**
         * Get time left until idle. If currently idle, returns 0.
         * 
         * @param method The method to be invoked on this idle timer instance.
         * @param element Element to watch, defaults to the document.
         * @param id Unique ID for this idle timer, to support multiple timers on the same page.
         * @return The time in milliseconds until the user goes idle. If user is already idle, returns `0`.
         */
        idleTimer(method: "getRemainingTime", element?: Document | HTMLElement, id?: string): number;

        /**
         * Get time elapsed since the user went idle or active.
         * - If currently idle, how long the user has been idle.
         * - If currently active, how long the user has been active.
         * 
         * @param method The method to be invoked on this idle timer instance.
         * @param element Element to watch, defaults to the document.
         * @param id Unique ID for this idle timer, to support multiple timers on the same page.
         * @return How long the user has been idle or active, in milliseconds.
         */
        idleTimer(method: "getElapsedTime", element?: Document | HTMLElement, id?: string): number;

        /**
         * Get time the last `active.idleTimer` event was fired.
         * 
         * @param method The method to be invoked on this idle timer instance.
         * @param element Element to watch, defaults to the document.
         * @param id Unique ID for this idle timer, to support multiple timers on the same page.
         * @return A timestamp (milliseconds since 1 January 1970 UTC) for when the most recent time the user went from idle
         * to active. 
         */
        idleTimer(method: "getLastActiveTime", element?: Document | HTMLElement, id?: string): number;

        /**
         * Get whether the user is currently idle.
         * 
         * @param method The method to be invoked on this idle timer instance.
         * @param element Element to watch, defaults to the document.
         * @param id Unique ID for this idle timer, to support multiple timers on the same page.
         * @return `true` if the user is currently idle, or `false` if the user is currently active.
         */
        idleTimer(method: "isIdle", element?: Document | HTMLElement, id?: string): boolean;
    }

    export interface IdleTimerOptions {
        /**
         * List of events that constitute an activity by the user. Defaults to
         * 
         * ```
         * mousemove keydown wheel DOMMouseScroll mousewheel mousedown touchstart touchmove MSPointerDown MSPointerMove
         * ```
         */
        events: string;

        /**
         * Indicates if the user is currently idle. Defaults to `false`.
         */
        idle: boolean;

        /**
         * The timeout period in milliseconds. Defaults to `30000`.
         */
        timeout: number;

        /**
         * If set, the use a local storage key to sync activity across browser tabs or windows.
         */
        timerSyncId: string;
    }

    /**
     * The event that is triggered when the user comes back.
     */
    export interface ActiveEvent<
        TDelegateTarget = any,
        TData = any,
        TCurrentTarget = any,
        TTarget = any
        > extends JQuery.TriggeredEvent<TDelegateTarget, TData, TCurrentTarget, TTarget> {
        type: "active";
    }

    /**
     * The event that is triggered when the user goes idle.
     */
    export interface IdleEvent<
        TDelegateTarget = any,
        TData = any,
        TCurrentTarget = any,
        TTarget = any
        > extends JQuery.TriggeredEvent<TDelegateTarget, TData, TCurrentTarget, TTarget> {
        type: "idle";
    }
}

// Extend $.fn.* plugins
interface JQuery extends JQueryIdleTimer.IdleTimerMethods {
}

// Extend $.* static properties on the JQuery object
interface JQueryStatic extends JQueryIdleTimer.IdleTimerMethods {
}

// Extend available event types
declare namespace JQuery {
    interface TypeToTriggeredEventMap<
        TDelegateTarget,
        TData,
        TCurrentTarget,
        TTarget
        > {
        /**
         * Triggered by the {@link JQuery.idleTimer|jQuery Idle Timer plugin}.
         * 
         * Fired when the user becomes active again.
         * 
         * Usually receives the following additional arguments:
         * - `elem` (JQuery): The element that the event was triggered on
         * - `obj` (object): A copy of the internal data used by idleTimer
         * - `triggerevent` (JQuery.TriggeredEvent): The initial event that triggered the element to become active.
         */
        ["active.idleTimer"]: JQueryIdleTimer.ActiveEvent<TDelegateTarget, TData, TCurrentTarget, TTarget>;

        /**
         * Triggered by the {@link JQuery.idleTimer|jQuery Idle Timer plugin}.
         * 
         * Fired when the user goes idle.
         * 
         * Usually receives the following additional arguments:
         * - `elem` (JQuery): The element that the event was triggered on.
         * - `obj` (object): A copy of the internal data used by idleTimer.
         */
        ["idle.idleTimer"]: JQueryIdleTimer.IdleEvent<TDelegateTarget, TData, TCurrentTarget, TTarget>;
    }
}