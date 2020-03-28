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