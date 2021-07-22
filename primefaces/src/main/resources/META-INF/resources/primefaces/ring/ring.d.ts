/**
 * Namespace for the jQuery Roundabout plugin.
 * 
 * Contains some additional types and interfaces required for the typings.
 * 
 * Roundabout is a jQuery plugin that easily converts unordered lists & other nested HTML structures into entertaining,
 * interactive, turntable-like areas.
 * 
 * See https://github.com/fredleblanc/roundabout.
 */
declare namespace JQueryRoundabout {
    /**
     * Specifier for an axis.
     * - `x`: The horizontal axis.
     * - `x`: The vertical axis.
     */
    export type  Axis = "x" | "y";

    /**
     * The animation method used when animating the roundabout.
     */
    export type AnimationMethod = "next" | "previous" | "nearest"

    /**
     * Callback for various different events triggered by roundabout, such as
     * {@link RoundaboutSettings.btnNextCallback}.
     */
    export type RoundaboutCallback = () => void;

    /**
     * Roundabout comes with many settable configuration options that let you customize how it operates.
     */
    export interface RoundaboutSettings {
        /**
         * When true, Roundabout will automatically advance the moving elements to the next child at a regular interval
         * (settable as autoplayDuration).
         * 
         * Defaults to `false`.
         */
        autoplay: boolean;

        /**
         * The length of time (in milliseconds) between animation triggers when a Roundabout's autoplay is playing.
         * 
         * Defaults to `1000`.
         */
        autoplayDuration: number;

        /**
         * The length of time (in milliseconds) to delay the start of Roundabout's configured autoplay option. This only
         * works with setting autoplay to true, and only on the first start of autoplay.
         * 
         * Defaults to `0`.
         */
        autoplayInitialDelay: number;

        /**
         * When true, Roundabout will pause autoplay when the user moves the cursor over the Roundabout container.
         * 
         * Defaults to `false`.
         */
        autoplayPauseOnHover: boolean;

        /**
         * The starting direction in which Roundabout should face relative to the focusBearing.
         * 
         * Defaults to `0.0`.
         */
        bearing: number;

        /**
         * A jQuery selector of page elements that, when clicked, will trigger the Roundabout to animate to the next
         * moving element.
         * 
         * Defaults to `null`.
         */
        btnNext: string;

        /**
         * A function that will be called once the animation triggered by a btnNext-related click has finished.
         * 
         * Defaults to `function() {}`.
         */
        btnNextCallback: RoundaboutCallback;

        /**
         * A jQuery selector of page elements that, when clicked, will trigger the Roundabout to animate to the previous
         * moving element.
         * 
         * Defaults to `null`.
         */
        btnPrev: string;

        /**
         * A function that will be called once the animation triggered by a btnPrev-related click has finished.
         * 
         * Defaults to `function() {}`.
         */
        btnPrevCallback: RoundaboutCallback;

        /**
         * A jQuery selector of page elements that, when clicked, will start the Roundabout's autoplay feature (if it's
         * currently stopped).
         * 
         * Defaults to `null`.
         */
        btnStartAutoplay: string;

        /**
         * A jQuery selector of page elements that, when clicked, will stop the Roundabout's autoplay feature (if it's
         * current playing).
         * 
         * Defaults to `null`.
         */
        btnStopAutoplay: string;

        /**
         * A jQuery selector of page elements that, when clicked, will toggle the Roundabout's autoplay state (either
         * starting or stopping).
         * 
         * Defaults to `null`.
         */
        btnToggleAutoplay: string;

        /**
         * A jQuery selector of child elements within the elements Roundabout is called upon that will become the moving
         * elements within Roundabout. By default, Roundabout works on unordered lists, but it can be changed to work
         * with any nested set of child elements.
         * 
         * Defaults to `li`.
         */
        childSelector: string;

        /**
         * When true, Roundabout will bring non-focused moving elements into focus when they're clicked. Otherwise,
         * click events won't be captured and will be passed through to the moving child elements.
         * 
         * Defaults to `true`.
         */
        clickToFocus: boolean;

        /**
         * A function that will be called once the clickToFocus animation has completed.
         * 
         * Defaults to `function() {}`.
         */
        clickToFocusCallback: RoundaboutCallback;

        /**
         * When true, Roundabout will replace the contents of moving elements with information about the moving elements
         * themselves.
         * 
         * Defaults to `false`.
         */
        debug: boolean;
        
        /**
         * The axis along which drag events are measured.
         * 
         * Defaults to `x`.
         */
        dragAxis: Axis;

        /**
         * Alters the rate at which dragging moves the Roundabout's moving elements. Higher numbers will cause the
         * moving elements to move less.
         * 
         * Defaults to `4`.
         */
        dragFactor: number;

        /**
         * The animation method to use when a dragged Roundabout is dropped.
         * 
         * Defaults to `nearest`.
         */
        dropAnimateTo: AnimationMethod;

        /**
         * A function that will be called once the dropped animation has completed.
         * 
         * Defaults to `function() {}`.
         */
        dropCallback: RoundaboutCallback

        /**
         * The length of time (in milliseconds) the animation will take to animate Roundabout to the appropriate child
         * when the Roundabout is “dropped.”
         * 
         * Defaults to `600`.
         */
        dropDuration: number;

        /**
         * The easing function to use when animating Roundabout after it has been “dropped.” With no other plugins, the
         * standard jQuery easing functions are available. When using the jQuery easing plugin all of its easing functions will also be available.
         * 
         * Defaults to `swing`.
         */
        dropEasing: string;

        /**
         * The length of time Roundabout will take to move from one child element being in focus to another (when an
         * animation is triggered). This value acts as the default for Roundabout, but each animation action can be given
         * a custom duration for that animation.
         * 
         * Defaults to `600`.
         */
        duration: number;

        /**
         * The easing function to use when animating Roundabout. With no other plugins, the standard jQuery easing
         * functions are available. When using the jQuery easing plugin, all of its easing functions will also be
         * available.
         * 
         * Defaults to `swing`.
         */
        easing: string;

        /**
         * Requires event.drag and event.drop plugins by ThreeDubMedia. Allows a user to rotate Roundabout be clicking
         * and dragging the Roundabout area itself.
         * 
         * Defaults to `false`.
         */
        enableDrag: boolean;

        /**
         * The maximum distance two values can be from one another to still be considered equal by Roundabout's
         * standards. This prevents JavaScript rounding errors.
         * 
         * Defaults to `0.001`.
         */
        floatComparisonThreshold: number;

        /**
         * The bearing that Roundabout will use as the focus point. All animations that move Roundabout between children
         * will animate the given child element to this bearing.
         * 
         * Defaults to `0.0`.
         */
        focusBearing: number;

        /**
         * The greatest opacity that will be assigned to a moving element. This occurs when the moving element is at the
         * same bearing as the focusBearing.
         * 
         * Defaults to `1.0`.
         */
        maxOpacity: number;

        /**
         * The greatest size (relative to its starting size) that will be assigned to a moving element. This occurs when
         * the moving element is at the same bearing as the focusBearing.
         * 
         * Defaults to `1.0`.
         */
        maxScale: number;

        /**
         * The greatest z-index that will be assigned to a moving element. This occurs when the moving element is at the
         * same bearing as the focusBearing.
         * 
         * Defaults to `280`.
         */
        maxZ: number;

        /**
         * The lowest opacity that will be assigned to a moving element. This occurs when the moving element is opposite
         * of (that is, 180° away from) the focusBearing.
         * 
         * Defaults to `0.4`.
         */
        minOpacity: number;

        /**
         * The lowest size (relative to its starting size) that will be assigned to a moving element. This occurs when
         * the moving element is opposite of (that is, 180° away from) the focusBearing.
         * 
         * Defaults to `0.4`.
         */
        minScale: number;

        /**
         * The lowest z-index that will be assigned to a moving element. This occurs when the moving element is opposite
         * of (that is, 180° away from) the focusBearing.
         * 
         * Defaults to `100`.
         */
        minZ: number;

        /**
         * When true, reverses the direction in which Roundabout will operate. By default, next animations will rotate
         * moving elements in a clockwise direction and previous animations will be counterclockwise. Using reflect will
         * flip the two.
         * 
         * Defaults to `false`.
         */
        reflect: boolean;

        /**
         * When true, attaches a resize event onto the window and will automatically relayout Roundabout's child
         * elements as the holder element changes size.
         * 
         * Defaults to `false`.
         */
        responsive: boolean;

        /**
         * The path that moving elements follow. By default, Roundabout comes with one shape, which is lazySusan. When
         * using Roundabout with the Roundabout Shapes plugin, there are many other shapes available.
         * 
         * Defaults to `lazySusan`.
         */
        shape: string;

        /**
         * The child element that will start at the Roundabout's focusBearing on load. This is a zero-based counter
         * based on the order of markup.
         * 
         * Defaults to `0`.
         */
        startingChild: number;

        /**
         * Slightly alters the calculations of moving elements. In the default shape, it adjusts the apparent tilt.
         * Other shapes will differ.
         * 
         * Defaults to `0.0`.
         */
        tilt: number;

        /**
         * When true, a blur event will be triggered on the child element that moves out of the focused position when
         * it does so.
         * 
         * Defaults to `true`.
         */
        triggerBlurEvents: boolean;

        /**
         * When true, a focus event will be triggered on the child element that moves into focus when it does so.
         * 
         * Defaults to `true`.
         */
        triggerFocusEvents: boolean;        
    }
}

interface JQuery {
    /**
     * Initializes roundabout on the current element.
     * @param settings Optional settings for configuring Roundabout.
     * @param onReady A callback function that is invoked once the Roundabout is ready.
     * @return this jQuery instance for chaining.
     */
    roundabout(settings?: Partial<JQueryRoundabout.RoundaboutSettings>, onReady?: JQueryRoundabout.RoundaboutCallback): this;

    /**
     * Initializes roundabout on the current element.
     * @param onReady A callback function that is invoked once the Roundabout is ready.
     * @return this jQuery instance for chaining.
     */
    roundabout(onReady: JQueryRoundabout.RoundaboutCallback): this;

    /**
     * Changes the bearing of the Roundabout.
     * @param method The method to call on the Roundabout instance.
     * @param bearing The new bearing in degrees, a value between `0.0` and `359.9`.
     * @param onChangeComplete 
     * @return this jQuery instance for chaining.
     */
    roundabout(method: "setBearing", bearing: number, onChangeComplete?: JQueryRoundabout.RoundaboutCallback): this;

    /**
     * Alters the bearing of the Roundabout by a given amount, either positive or negative degrees.
     * @param method The method to call on the Roundabout instance.
     * @param delta The amount in degrees by which the bearing will change, either positive or negative.
     * @param onChangeComplete Callback function that is invoked once the change completes.
     * @return this jQuery instance for chaining.
     */
    roundabout(method: "adjustBearing", delta: number, onChangeComplete?: JQueryRoundabout.RoundaboutCallback): this;

    /**
     * Changes the tilt of the Roundabout.
     * @param method The method to call on the Roundabout instance.
     * @param tilt The new tilt in degrees, typically between `-2.0` and `10.0`.
     * @param onChangeComplete Callback function that is invoked once the change completes.
     * @return this jQuery instance for chaining.
     */
    roundabout(method: "setTilt", tilt: number, onChangeComplete?: JQueryRoundabout.RoundaboutCallback): this;

    /**
     * Alters the tilt of the Roundabout by a given amount, either in positive or negative amounts.
     * @param method The method to call on the Roundabout instance.
     * @param delta The amount in degrees by which the tilt will change (either positive or negative).
     * @param onChangeComplete Callback function that is invoked once the change completes.
     * @return this jQuery instance for chaining.
     */
    roundabout(method: "adjustTilt", delta: number, onChangeComplete?: JQueryRoundabout.RoundaboutCallback): this;

    /**
     * Animates the Roundabout to the nearest child. This animation will not move the Roundabout if any child is already
     * in focus.
     * @param method The method to call on the Roundabout instance.
     * @param duration The length of time (in milliseconds) that the animation will take to complete; uses Roundabout’s
     * configured duration if no value is set here
     * @param easing The name of the easing function to use for movement; uses Roundabout’s configured easing if no
     * value is set here.
     * @param onChangeComplete Callback function that is invoked once the change completes.
     * @return this jQuery instance for chaining.
     */
    roundabout(method: "animateToNearestChild", duration: number, easing: string, onChangeComplete?: JQueryRoundabout.RoundaboutCallback): this;

    /**
     * Animates the Roundabout to the nearest child. This animation will not move the Roundabout if any child is already
     * in focus.
     * @param method The method to call on the Roundabout instance.
     * @param onChangeComplete Callback function that is invoked once the change completes.
     * @return this jQuery instance for chaining.
     */
    roundabout(method: "animateToNearestChild", onChangeComplete?: JQueryRoundabout.RoundaboutCallback): this;

    /**
     * Animates the Roundabout to the given childPosition, which is a zero-based counter of children based on the order
     * of markup.
     * @param method The method to call on the Roundabout instance.
     * @param childPosition The zero-based child to which Roundabout will animate.
     * @param duration The length of time (in milliseconds) that the animation will take to complete; uses Roundabout’s
     * configured duration if no value is set here
     * @param easing The name of the easing function to use for movement; uses Roundabout’s configured easing if no
     * value is set here.
     * @param onChangeComplete Callback function that is invoked once the change completes.
     * @return this jQuery instance for chaining.
     */
    roundabout(method: "animateToChild", childPosition: number, duration: number, easing: string, onChangeComplete?: JQueryRoundabout.RoundaboutCallback): this;

    /**
     * Animates the Roundabout to the given childPosition, which is a zero-based counter of children based on the order
     * of markup.
     * @param method The method to call on the Roundabout instance.
     * @param childPosition The zero-based child to which Roundabout will animate.
     * @param onChangeComplete Callback function that is invoked once the change completes.
     * @return this jQuery instance for chaining.
     */
    roundabout(method: "animateToChild", childPosition: number, onChangeComplete?: JQueryRoundabout.RoundaboutCallback): this;

    /**
     * Animates the Roundabout to the next child element.
     * @param method The method to call on the Roundabout instance.
     * @param duration The length of time (in milliseconds) that the animation will take to complete; uses Roundabout’s
     * configured duration if no value is set here
     * @param easing The name of the easing function to use for movement; uses Roundabout’s configured easing if no
     * value is set here.
     * @param onChangeComplete Callback function that is invoked once the change completes.
     * @return this jQuery instance for chaining.
     */
    roundabout(method: "animateToNextChild", duration: number, easing: string, onChangeComplete?: JQueryRoundabout.RoundaboutCallback): this;

    /**
     * Animates the Roundabout to the next child element.
     * @param method The method to call on the Roundabout instance.
     * @param onChangeComplete Callback function that is invoked once the change completes.
     * @return this jQuery instance for chaining.
     */
    roundabout(method: "animateToNextChild", onChangeComplete?: JQueryRoundabout.RoundaboutCallback): this;

    /**
     * Animates the Roundabout to the previous child element.
     * @param method The method to call on the Roundabout instance.
     * @param duration The length of time (in milliseconds) that the animation will take to complete; uses Roundabout’s
     * configured duration if no value is set here
     * @param easing The name of the easing function to use for movement; uses Roundabout’s configured easing if no
     * value is set here.
     * @param onChangeComplete Callback function that is invoked once the change completes.
     * @return this jQuery instance for chaining.
     */
    roundabout(method: "animateToPreviousChild", duration: number, easing: string, onChangeComplete?: JQueryRoundabout.RoundaboutCallback): this;

    /**
     * Animates the Roundabout to the previous child element.
     * @param method The method to call on the Roundabout instance.
     * @param onChangeComplete Callback function that is invoked once the change completes.
     * @return this jQuery instance for chaining.
     */
    roundabout(method: "animateToPreviousChild", onChangeComplete?: JQueryRoundabout.RoundaboutCallback): this;

    /**
     * Animates the Roundabout to the given amount of degrees away from its current bearing (either positive or negative
     * degrees).
     * @param method The method to call on the Roundabout instance.
     * @param degrees The amount by which the bearing will change (either positive or negative)
     * @param duration The length of time (in milliseconds) that the animation will take to complete; uses Roundabout’s
     * configured duration if no value is set here
     * @param easing The name of the easing function to use for movement; uses Roundabout’s configured easing if no
     * value is set here.
     * @param onChangeComplete Callback function that is invoked once the change completes.
     * @return this jQuery instance for chaining.
     */
    roundabout(method: "animateToDelta", degrees: number, duration: number, easing: string, onChangeComplete?: JQueryRoundabout.RoundaboutCallback): this;

    /**
     * Animates the Roundabout to the given amount of degrees away from its current bearing (either positive or negative
     * degrees).
     * @param method The method to call on the Roundabout instance.
     * @param degrees The amount by which the bearing will change (either positive or negative)
     * @param onChangeComplete Callback function that is invoked once the change completes.
     * @return this jQuery instance for chaining.
     */
    roundabout(method: "animateToDelta", degrees: number, onChangeComplete?: JQueryRoundabout.RoundaboutCallback): this;

    /**
     * Animates the Roundabout so that a given bearing ends at the configured focusBearing.
     * @param method The method to call on the Roundabout instance.
     * @param degrees A value between `0.0` and `359.9`.
     * @param duration The length of time (in milliseconds) that the animation will take to complete; uses Roundabout’s
     * configured duration if no value is set here
     * @param easing The name of the easing function to use for movement; uses Roundabout’s configured easing if no
     * value is set here.
     * @param onChangeComplete Callback function that is invoked once the change completes.
     * @return this jQuery instance for chaining.
     */
    roundabout(method: "animateBearingToFocus", degrees: number, duration: number, easing: string, onChangeComplete?: JQueryRoundabout.RoundaboutCallback): this;

    /**
     * Animates the Roundabout so that a given bearing ends at the configured focusBearing.
     * 
     * @param method The method to call on the Roundabout instance.
     * @param degrees A value between `0.0` and `359.9`.
     * @param onChangeComplete Callback function that is invoked once the change completes.
     * @return this jQuery instance for chaining.
     */
    roundabout(method: "animateBearingToFocus", degrees: number, onChangeComplete?: JQueryRoundabout.RoundaboutCallback): this;

    /**
     * Starts the Roundabout’s autoplay feature.
     * @param method The method to call on the Roundabout instance.
     * @param onAnimationComplete Callback function that is invoked after each autoplay animation completes.
     * @return this jQuery instance for chaining.
     */
    roundabout(method: "startAutoplay", onAnimationComplete?: JQueryRoundabout.RoundaboutCallback): this;

    /**
     * Stops the Roundabout’s autoplay feature.
     * @param method The method to call on the Roundabout instance.
     * @param keepAutoplayBindings When `true` will not destroy any autoplay mouseenter and mouseleave event bindings
     * that were set by `autoplayPauseOnHover`.
     * @return this jQuery instance for chaining.
     */
    roundabout(method: "stopAutoplay", keepAutoplayBindings?: boolean): this;

    /**
     * Starts or stops the Roundabout’s autoplay feature (based upon its current state).
     * @param method The method to call on the Roundabout instance.
     * @param onAnimationComplete Callback function that is invoked after each autoplay animation completes.
     * @return this jQuery instance for chaining.
     */
    roundabout(method: "toggleAutoplay", onAnimationComplete?: JQueryRoundabout.RoundaboutCallback): this;

    /**
     * Checks to see if the Roundabout’s autoplay feature is currently playing or not.
     * @param method The method to call on the Roundabout instance.
     * @return `true` if autoplay is active, or `false` otherwise.
     */
    roundabout(method: "isAutoplaying"): boolean;

    /**
     * Changes the length of time (in milliseconds) that the Roundabout’s autoplay feature waits between attempts to
     * animate to the next child.
     * @param method The method to call on the Roundabout instance.
     * @param duration Length of time (in milliseconds) between attempts to have autoplay animate to the next child
     * element.
     * @return this jQuery instance for chaining.
     */
    roundabout(method: "changeAutoplayDuration", duration: number): this;

    /**
     * Repositions child elements based on new contextual information. This is most helpful when the Roundabout element
     * itself changes size and moving child elements within need readjusting.
     * @param method The method to call on the Roundabout instance.
     * @return this jQuery instance for chaining.
     */
    roundabout(method: "relayoutChildren"): this;

    /**
     * Gets the nearest child element to the `focusBearing`. This number is a zero-based counter based on order of
     * markup.
     * @param method The method to call on the Roundabout instance.
     * @return Zero-based index of the nearest child.
     */
    roundabout(method: "getNearestChild"): number;

    /**
     * Gets the child currently in focus. This number is a zero-based counter based on order of markup.
     * @param method The method to call on the Roundabout instance.
     * @return Zero-based index of the focused child.
     */
    roundabout(method: "getChildInFocus", ): number;
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
         * Triggered by the {@link JQuery.roundabout|jQuery Roundabout plugin}.
         * 
         * This event fires on the Roundabout element when its child elements have been repositioned and are in place.
         */
        childrenUpdated: JQuery.TriggeredEvent<TDelegateTarget, TData, TCurrentTarget, TTarget>;

        /**
         * Triggered by the jQuery Roundabout plugin
         * 
         * This event fires on child elements that have been repositioned and are in place.
         */
        reposition: JQuery.TriggeredEvent<TDelegateTarget, TData, TCurrentTarget, TTarget>;

        /**
         * Triggered by the {@link JQuery.roundabout|jQuery Roundabout plugin}.
         * 
         * This event fires on the Roundabout element when its `bearing` has been set.
         */
        bearingSet: JQuery.TriggeredEvent<TDelegateTarget, TData, TCurrentTarget, TTarget>;

        /**
         * Triggered by the {@link JQuery.roundabout|jQuery Roundabout plugin}.
         * 
         * This event fires on moving child elements when an animation causes them pass through the point that is
         * opposite (or 180°) from the `focusBearing` in a clockwise motion.
         */
        moveClockwiseThroughBack: JQuery.EventBase<TDelegateTarget, TData, TCurrentTarget, TTarget>;

        /**
         * Triggered by the {@link JQuery.roundabout|jQuery Roundabout plugin}.
         * 
         * This event fires on moving child elements when an animation causes them to pass through the point that is
         * opposite (or 180°) from the focusBearing in a counterclockwise motion.
         */
        moveCounterclockwiseThroughBack: JQuery.TriggeredEvent<TDelegateTarget, TData, TCurrentTarget, TTarget>;

        /**
         * Triggered by the {@link JQuery.roundabout|jQuery Roundabout plugin}.
         * 
         * This event fires on the Roundabout element at the start of any animation.
         */
        animationStart: JQuery.TriggeredEvent<TDelegateTarget, TData, TCurrentTarget, TTarget>;

        /**
         * Triggered by the {@link JQuery.roundabout|jQuery Roundabout plugin}.
         * 
         * This event fires on the Roundabout element at the end of any animation.
         */
        animationEnd: JQuery.TriggeredEvent<TDelegateTarget, TData, TCurrentTarget, TTarget>;

        /**
         * Triggered by the {@link JQuery.roundabout|jQuery Roundabout plugin}.
         * 
         * This event fires on the Roundabout element when the `autoplay` feature starts.
         */
        autoplayStart: JQuery.TriggeredEvent<TDelegateTarget, TData, TCurrentTarget, TTarget>;

        /**
         * Triggered by the {@link JQuery.roundabout|jQuery Roundabout plugin}.
         * 
         * This event fires on the Roundabout element when the `autoplay` feature stops.
         */
        autoplayStop: JQuery.TriggeredEvent<TDelegateTarget, TData, TCurrentTarget, TTarget>;
    }
}