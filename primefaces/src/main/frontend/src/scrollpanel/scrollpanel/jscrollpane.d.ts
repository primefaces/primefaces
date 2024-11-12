/**
 * Namespace for the JScrollpane jQuery plugin.
 * 
 * Contains some additional types and interfaces required for the typings.
 */
declare namespace JQueryJScrollPane {
    /**
     * Defines where the arrows should be drawn. Allows you to choose where the arrows appear relative to the tracks and
     * each of the settings has four valid values:
     * - `split`: First arrow will appear before track, second arrow afterwards (the default)
     * - `before`: Both arrows will appear before the track.
     * - `after`: Both arrows will appear after the track (as is default on OSX).
     * - `os`: The positioning of the arrows will be chosen dependant on the users operating system (e.g. the value will
     * be "after" for OSX and "split" or other operating systems).
     */
    export type ArrowPosition = "split" | "before" | "after" | "os";

    /**
     * Optional settings that can be passed when the creating a new JScroll instance to customize its behavior.
     */
    export interface JScrollPaneSettings {
        /**
         * Whether arrows should be shown on the generated scroll pane. When set to false only the scrollbar track and
         * drag will be shown, if set to true then arrows buttons will also be shown.
         */
        showArrows: boolean;

        /**
         * Whether the scroll pane should attempt to maintain it's position whenever it is reinitialized.
         * 
         * If true then the viewport of the scroll pane will remain the same when it is reinitialized, if false then the
         * viewport will jump back up to the top when the scroll pane is reinitialized. See also
         * {@link stickToBottom} and {@link stickToRight}. 
         */
        maintainPosition: boolean;

        /**
         * If {@link maintainPosition} is true and the scroll pane is scrolled to the bottom then the scroll pane then
         * the scroll pane will remain scrolled to the bottom even if new content is added to the pane which makes it
         * taller. 
         */
        stickToBottom: boolean;

        /**
         * If {@link maintainPosition} is true and the scroll pane is scrolled to its right edge then the scroll pane
         * then the scroll pane will remain scrolled to the right edge even if new content is added to the pane which
         * makes it wider. 
         */
        stickToRight: boolean;

        /**
         * Whether JScrollPane should automatically reinitialize itself periodically after you have initially
         * initialized it.
         * 
         * This can help with instances when the size of the content of the scroll pane (or the surrounding element)
         * changes.
         * 
         * However, it does involve an overhead of running a javascript function on a timer so it is recommended only to
         * activate
         * where necessary.
         */
        autoReinitialise: boolean;

        /**
         * The number of milliseconds between each reinitialization (if {@link autoReinitialise} is true). 
         */
        autoReinitialiseDelay: number;

        /**
         * The smallest height that the vertical drag can have. The size of the drag elements is based on the proportion
         * of the size of the content to the size of the viewport but is constrained within the minimum and maximum
         * dimensions given.
         */
        verticalDragMinHeight: number;

        /**
         * The largest height that the vertical drag can have. The size of the drag elements is based on the proportion
         * of the size of the content to the size of the viewport but is constrained within the minimum and maximum
         * dimensions given.
         */
        verticalDragMaxHeight: number;

        /**
         * The smallest width that the horizontal drag can have. The size of the drag elements is based on the
         * proportion of the size of the content to the size of the viewport but is constrained within the minimum and
         * maximum dimensions given.
         */
        horizontalDragMinWidth: number;

        /**
         * The largest width that the horizontal drag can have. The size of the drag elements is based on the proportion
         * of the size of the content to the size of the viewport but is constrained within the minimum and maximum
         * dimensions given.
         */
        horizontalDragMaxWidth: number;

        /**
         * The width of the content of the scroll pane. The default value of undefined will allow JScrollPane to
         * calculate the width of its content. However, in some cases you will want to disable this (e.g. to prevent
         * horizontal scrolling or where the calculation of the size of the content doesn't return reliable results).
         */
        contentWidth: number;

        /**
         * Whether to use animation when calling {@link JScrollPaneInstance.scrollTo|} or
         * {@link JScrollPaneInstance.scrollBy}. You can control the animation speed and easing by using the
         * {@link animateDuration} and {@link animateEase} settings or if you want to exercise more complete control
         * then you can override the animate API method.
         */
        animateScroll: boolean;

        /**
         * The number of milliseconds taken to animate to a new position.
         */
        animateDuration: number;

        /**
         * The type of easing to use when animating to a new position.
         */
        animateEase: string;

        /**
         * Whether internal links on the page should be hijacked so that if they point so content within a JScrollPane
         * then they automatically scroll the JScrollPane to the correct place.
         */
        hijackInternalLinks: boolean;

        /**
         * The amount of space between the side of the content and the vertical scrollbar. 
         */
        verticalGutter: number;

        /**
         * The amount of space between the bottom of the content and the horizontal scrollbar. 
         */
        horizontalGutter: number;

        /**
         * A multiplier which is used to control the amount that the scroll pane scrolls each time the mouse wheel is
         * turned. 
         */
        mouseWheelSpeed: number;

        /**
         * A multiplier which is used to control the amount that the scroll pane scrolls each time on of the arrow
         * buttons is pressed.
         */
        arrowButtonSpeed: number;

        /**
         * The number of milliseconds between each repeated scroll event when the mouse is held down over one of the
         * arrow keys.
         */
        arrowRepeatFreq: number;

        /**
         * Whether the arrow buttons should cause the JScrollPane to scroll while you are hovering over them. 
         */
        arrowScrollOnHover: boolean;

        /**
         * Where the vertical arrows should appear relative to the vertical track.
         */
        verticalArrowPositions: ArrowPosition;

        /**
         * Where the horizontal arrows should appear relative to the horizontal track.
         */
        horizontalArrowPositions: ArrowPosition;

        /**
         * Whether keyboard navigation should be enabled (e.g. whether the user can focus the scroll pane and then use
         * the arrow (and other) keys to navigate around. 
         */
        enableKeyboardNavigation: boolean;

        /**
         * Whether the focus outline should be hidden in all browsers. For best accessibility you should not change this
         * option. You can style the outline with the CSS property outline and outline-offset. 
         */
        hideFocus: boolean;

        /**
         * Whether clicking on the track (e.g. the area behind the drag) should scroll towards the point clicked on.
         * 
         * Defaults to true as this is the native behavior in these situations. 
         */
        clickOnTrack: boolean;

        /**
         * A multiplier which is used to control the amount that the scroll pane scrolls each
         * {@link trackClickRepeatFreq} while the mouse button is held down over the track. 
         */
        trackClickSpeed: number;

        /**
         * The number of milliseconds between each repeated scroll event when the mouse is held down over the track.
         */
        trackClickRepeatFreq: number;
    }

    /**
     * The JScroll instance that is created when initializing an element via the jQuery plugin. It can be accessed via
     * the `jsp` data attribute of the jQuery element: `$(scrollPanelElement).data("jsp")`.
     */
    interface JScrollPaneInstance {
        /**
         * Reinitializes the scroll pane (if it's internal dimensions have changed since the last time it was
         * initialized).
         * 
         * The settings object which is passed in will override any settings from the previous time it was
         * initialized - if you don't pass any settings then the ones from the previous initialization will be used.
         */
        reinitialise(options?: Partial<JScrollPaneSettings>): void;

        /**
         * Scrolls the specified element into view so that it can be seen within the viewport.
         * @param ele A CSS selector, HTML element of jQuery wrapper object to scroll to.
         * @param stickToTop If it is true then the element will appear at the top of the viewport, if it is false
         * then the viewport will scroll as little as possible to show the element.
         * @param animate Whether an animation should occur. If you don't provide this argument then the
         * {@link JQueryJScrollPane.JScrollPaneSettings.animateScroll|animateScroll} value from the settings object is
         * used instead.
         */
        scrollToElement(ele: string | HTMLElement | JQuery, stickToTop?: boolean, animate?: boolean): void;

        /**
         * Scrolls the pane so that the specified co-ordinates within the content are at the top left of the viewport.
         * @param destX Left position of the viewport to scroll to
         * @param destY Top position of the viewport to scroll to
         * @param animate Whether an animation should occur. If you don't provide this argument then the
         * {@link JQueryJScrollPane.JScrollPaneSettings.animateScroll|animateScroll} value from the settings object is
         * used instead.
         */
        scrollTo(destX: number, destY: number, animate?: boolean): void;

        /**
         * Scrolls the pane so that the specified co-ordinate within the content is at the left of the viewport.
         * @param destX Left position of the viewport to scroll to.
         * @param animate Whether an animation should occur. If you don't provide this argument then the
         * {@link JQueryJScrollPane.JScrollPaneSettings.animateScroll|animateScroll} value from the settings object is
         * used instead.
         */
        scrollToX(destX: number, animate?: boolean): void;

        /**
         * Scrolls the pane so that the specified co-ordinate within the content is at the top of the viewport.
         * @param destY Top position of the viewport to scroll to
         * @param animate Whether an animation should occur. If you don't provide this argument then the
         * {@link JQueryJScrollPane.JScrollPaneSettings.animateScroll|animateScroll} value from the settings object is
         * used instead.
         */
        scrollToY(destY: number, animate?: boolean): void;

        /**
         * Scrolls the pane to the specified percentage of its maximum horizontal scroll position.
         * @param destPercentX Percentage from left of the full width of the viewport to scroll to
         * @param animate Whether an animation should occur. If you don't provide this argument then the
         * {@link JQueryJScrollPane.JScrollPaneSettings.animateScroll|animateScroll} value from the settings object is
         * used instead.
         */
        scrollToPercentX(destPercentX: number, animate?: boolean): void;

        /**
         * Scrolls the pane to the specified percentage of its maximum vertical scroll position.
         * @param destPercentY Percentage from top of the full width of the viewport to scroll to
         * @param animate Whether an animation should occur. If you don't provide this argument then the
         * {@link JQueryJScrollPane.JScrollPaneSettings.animateScroll|animateScroll} value from the settings object is
         * used instead.
         */
        scrollToPercentY(destPercentY: number, animate?: boolean): void;

        /**
         * Scrolls the pane by the specified amount of pixels.
         * @param deltaX Number of pixels to scroll horizontally
         * @param deltaY Number of pixels to scroll vertically
         * @param animate Whether an animation should occur. If you don't provide this argument then the
         * {@link JQueryJScrollPane.JScrollPaneSettings.animateScroll|animateScroll} value from the settings object is
         * used instead.
         */
        scrollBy(deltaX: number, deltaY: number, animate?: boolean): void;

        /**
         * Scrolls the pane by the specified amount of pixels.
         * @param deltaX Number of pixels to scroll horizontally
         * @param animate Whether an animation should occur. If you don't provide this argument then the
         * {@link JQueryJScrollPane.JScrollPaneSettings.animateScroll|animateScroll} value from the settings object is
         * used instead.
         */
        scrollByX(deltaX: number, animate?: boolean): void;

        /**
         * Scrolls the pane by the specified amount of pixels
         * @param deltaY Number of pixels to scroll vertically
         * @param animate Whether an animation should occur. If you don't provide this argument then the
         * {@link JQueryJScrollPane.JScrollPaneSettings.animateScroll|animateScroll} value from the settings object is
         * used instead.
         */
        scrollByY(deltaY: number, animate?: boolean): void;

        /**
         * Positions the horizontal drag at the specified x position (and updates the viewport to reflect this)
         * @param x New position of the horizontal drag.
         * @param animate Whether an animation should occur. If you don't provide this argument then the
         * {@link JQueryJScrollPane.JScrollPaneSettings.animateScroll|animateScroll} value from the settings object is
         * used instead.
         */
        positionDragX(x: number, animate?: boolean): void;

        /**
         * Positions the vertical drag at the specified y position (and updates the viewport to reflect this)
         * @param x New position of the vertical drag.
         * @param animate Whether an animation should occur. If you don't provide this argument then the
         * {@link JQueryJScrollPane.JScrollPaneSettings.animateScroll|animateScroll} value from the settings object is
         * used instead.
         */
        positionDragY(y: number, animate?: boolean): void;

        /**
         * This method is called when JScrollPane is trying to animate to a new position. You can override it if you
         * want to provide advanced animation functionality.
         * @param ele The element whose position is being animated
         * @param prop The property that is being animated
         * @param value The value it's being animated to
         * @param stepCallback A function that will be executed after the animation had finished.
         */
        animate(ele: JQuery, prop: string, value: unknown, stepCallback: (this: HTMLElement) => void): void;

        /**
         * Finds the current x position of the viewport with regards to the content pane.
         * @return The current x position of the viewport with regards to the content pane.
         */
        getContentPositionX(): number;

        /**
         * Finds the current y position of the viewport with regards to the content pane. 
         * @return The current y position of the viewport with regards to the content pane. 
         */
        getContentPositionY(): number;

        /**
         * Finds the width of the content within the scroll pane. 
         * @return The width of the content within the scroll pane. 
         */
        getContentWidth(): number;

        /**
         * Finds the height of the content within the scroll pane. 
         * @return The height of the content within the scroll pane. 
         */
        getContentHeight(): number;

        /**
         * Checks whether or not this scroll pane has a horizontal scrollbar.
         * @return Whether or not this scroll pane has a horizontal scrollbar.
         */
        getIsScrollableH(): boolean;

        /**
         * Finds the horizontal position of the viewport within the pane content
         * @return The horizontal position of the viewport within the pane content.
         */
        getPercentScrolledX(): number;

        /**
         * Finds the vertical position of the viewport within the pane content.
         * @return The vertical position of the viewport within the pane content.
         */
        getPercentScrolledY(): number;

        /**
         * Checks whether or not this scroll pane has a vertical scrollbar.
         * @return Whether or not this scroll pane has a vertical scrollbar. 
         */
        getIsScrollableV(): boolean;

        /**
         * Gets a reference to the content pane. It is important that you use this method if you want to edit the
         * content of your JScrollPane as if you access the element directly then you may have some problems (as your
         * original element has had additional elements for the scrollbars etc added into it). 
         * @return The content pane of this scroll pane.
         */
        getContentPane(): JQuery;

        /**
         * Scrolls this scroll pane down as far as it can currently scroll.
         * @param animate Whether an animation should occur. If you don't provide this argument then the
         * {@link JQueryJScrollPane.JScrollPaneSettings.animateScroll|animateScroll} value from the settings object is
         * used instead.
         */
        scrollToBottom(animate?: boolean): void;

        /**
         * Hijacks the links on the page which link to content inside the scroll pane. If you have changed the content
         * of your page (e.g. via AJAX) and want to make sure any new anchor links to the contents of your scroll pane
         * will work then call this function. 
         */
        hijackInternalLinks(): void;

        /**
         * Destroys the JScrollPane on the instance matching this API object and restores the browser's default
         * behavior.
         */
        destroy(): void;
    }
}

interface JQuery {
   /**
    * Initializes the JScrollPane on the JQuery object.
    * @param settings Optional settings for configuring the scroll pane.
    * @return this jQuery instance for chaining.
	*/
    jScrollPane(settings?: Partial<JQueryJScrollPane.JScrollPaneSettings>): this;
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
         * Triggered by the {@link JQuery.jScrollPane|jQuery JScrollPane plugin}.
         * 
         * Fires when an element is (re)initialized.
         * 
         * Usually receives the following additional arguments:
         * - `isScrollable` (boolean)
         */
        "jsp-initialised": JQuery.TriggeredEvent<TDelegateTarget, TData, TCurrentTarget, TTarget>;

        /**
         * Triggered by the {@link JQuery.jScrollPane|jQuery JScrollPane plugin}.
         * 
         * Fires continuously whilst vertical scrolling is occurring.
         * 
         * Usually receives the following additional arguments:
         * - `scrollPositionY` (number)
         * - `isAtTop` (boolean)
         * - `isAtBottom` (boolean)
         */
        "jsp-scroll-y": JQuery.TriggeredEvent<TDelegateTarget, TData, TCurrentTarget, TTarget>;

        /**
         * Triggered by the {@link JQuery.jScrollPane|jQuery JScrollPane plugin}.
         * 
         * Fires continuously whilst horizontal scrolling is occurring.
         * 
         * Usually receives the following additional arguments:
         * - `scrollPositionX` (number)
         * - `isAtLeft` (boolean)
         * - `isAtRight` (boolean)
         */
        "jsp-scroll-x": JQuery.TriggeredEvent<TDelegateTarget, TData, TCurrentTarget, TTarget>;

        /**
         * Triggered by the {@link JQuery.jScrollPane|jQuery JScrollPane plugin}.
         * 
         * Fires whenever the state of the arrow buttons changes (i.e. when the scroll reaches or leaves any end of the
         * viewport). Note that this function isn't dependant on having `showArrows` set to true, indeed it is most
         * useful when implementing your own arrow keys.
         * 
         * Usually receives the following additional arguments:
         * - `isAtTop` (boolean)
         * - `isAtBottom` (boolean)
         * - `isAtLeft` (boolean)
         * - `isAtRight` (boolean)
         * 
         */
        "jsp-arrow-change": JQuery.TriggeredEvent<TDelegateTarget, TData, TCurrentTarget, TTarget>;

        /**
         * Triggered by the {@link JQuery.jScrollPane|jQuery JScrollPane plugin}.
         * 
         * Fires only when the scroll is caused by the user. Also, its fired after the actual scroll had happened.
         * 
         * Usually receives the following additional arguments:
         * - `destTop` (number)
         * - `isAtTop` (boolean)
         * - `isAtBottom` (boolean)
         */
        "jsp-user-scroll-y": JQuery.TriggeredEvent<TDelegateTarget, TData, TCurrentTarget, TTarget>;

        /**
         * Triggered by the {@link JQuery.jScrollPane|jQuery JScrollPane plugin}.
         * 
         * Fires only when the scroll is caused by the user. Also, its fired after the actual scroll had happened.
         * 
         * Usually receives the following additional arguments:
         * - `destLeft` (number)
         * - `isAtLeft` (boolean)
         * - `isAtRight` (boolean)
         */
        "jsp-user-scroll-x": JQuery.TriggeredEvent<TDelegateTarget, TData, TCurrentTarget, TTarget>;

        /**
         * Triggered by the {@link JQuery.jScrollPane|jQuery JScrollPane plugin}.
         * 
         * Fired before the actual scroll had happened. This can be used to disable the scroll functionality if the
         * `event.preventDefault()` is called.
         * 
         * Usually receives the following additional arguments:
         * - `destY` (number)
         */
        "jsp-will-scroll-y": JQuery.TriggeredEvent<TDelegateTarget, TData, TCurrentTarget, TTarget>;

        /**
         * Triggered by the {@link JQuery.jScrollPane|jQuery JScrollPane plugin}.
         * 
         * Fired before the actual scroll had happened. This can be used to disable the scroll functionality if the
         * `event.preventDefault()` is called.
         * 
         * Usually receives the following additional arguments:
         * - `destX` (number)
         */
        "jsp-will-scroll-x": JQuery.TriggeredEvent<TDelegateTarget, TData, TCurrentTarget, TTarget>;
    }
}