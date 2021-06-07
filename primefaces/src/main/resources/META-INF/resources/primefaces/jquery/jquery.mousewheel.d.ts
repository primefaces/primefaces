/**
 * Namespace for the jQuery Mouse Wheel plugin.
 * 
 * A jQuery plugin that adds cross-browser mouse wheel support with delta normalization.
 * 
 * In order to use the plugin, simply bind the mousewheel event to an element.
 * 
 * See https://github.com/jquery/jquery-mousewheel
 */
declare namespace JQueryMousewheel {
    /**
     * The event that is triggered when the mousewheel is rotated.
     */
    export interface MousewheelEvent<
        TDelegateTarget = any,
        TData = any,
        TCurrentTarget = any,
        TTarget = any
        > extends JQuery.TriggeredEvent<TDelegateTarget, TData, TCurrentTarget, TTarget> {
        type: "mousewheel";

        /**
         * The normalized horizontal distance the mouse wheel has moved.
         * 
         * Multiply the {@link deltaFactor} by {@link  deltaX} or {@link deltaY} to get the scroll distance that the
         * browser has reported.
         */
        deltaX: number;

        /**
         * The normalized vertical distance the mouse wheel has moved.
         * 
         * Multiply the {@link deltaFactor} by {@link  deltaX} or {@link deltaY} to get the scroll distance that the
         * browser has reported.
         */
        deltaY: number;

        /**
         * The normalization factor for converting the normalized delta to the absolute delta reported by the browser.
         * 
         * Multiply the {@link deltaFactor} by {@link  deltaX} or {@link deltaY} to get the scroll distance that the
         * browser has reported.
         */
        deltaFactor: number;
    }
}

interface JQuery {
    /**
     * Triggers a `mousewheel` event on this element.
     * @return this for chaining.
     */
    mousewheel(): this;
    /**
     * Registers the given event listener for the `mousewheel` event.
     * @param handler Callback to invoke when the event occurs.
     * @return this for chaining.
     */
    mousewheel(handler: JQuery.TypeEventHandler<HTMLElement, undefined, HTMLElement, HTMLElement, "mousewheel">): this;
    /**
     * Removes the given event listener for the `mousewheel` event.
     * @param handler Callback to remove.
     * @return this for chaining.
     */
    unmousewheel(handler: JQuery.TypeEventHandler<HTMLElement, undefined, HTMLElement, HTMLElement, "mousewheel">):  this;
}

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
        ["mousewheel"]: JQueryMousewheel.MousewheelEvent<TDelegateTarget, TData, TCurrentTarget, TTarget>;
   }
}