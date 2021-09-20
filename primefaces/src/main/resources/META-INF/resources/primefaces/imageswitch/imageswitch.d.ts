/**
 * Namespace for the JQuery Cycle plugin, accessible via `$.fn.cycle`. It is used by the `ImageSwitch` widget.
 * 
 * This namespace contains several required interfaces and types.
 */
declare namespace JQueryCycle {
    /**
     * Transition callback for the image switch.
     */
    export type CallbackAfter = 
    /**
     * @param currSlideElement Current image before the transition
     * @param nextSlideElement Next image to be shown
     * @param options Current image switch options
     * @param forwardFlag `true` if switching the next, `false` if switching to the previous element.
     */
    (this: JQuery, currSlideElement: JQuery, nextSlideElement: JQuery, options: Configuration, forwardFlag: boolean) => void;
    /**
     * Transition callback for the image switch 
     */
    export type CallbackBefore =
    /**
     * @param currSlideElement Current image before the transition
     * @param nextSlideElement Next image to be shown
     * @param options Current image switch options
     * @param forwardFlag `true` if switching the next, `false` if switching to the previous element.
     */
    (this: JQuery, currSlideElement: JQuery, nextSlideElement: JQuery, options: Configuration, forwardFlag: boolean) => void

    /**
     * Callback invoked when the slideshow terminates (use with autostop or nowrap options)
     */
    export type CallbackEnd = 
    /**
     * @param options Current image switch options
     */
    (options: Configuration) => void;

    /**
     * Function used to control the transition
     */
    export type CallbackFxFn =
    /**
     * @param currSlideElement Current image before the transition
     * @param nextSlideElement Next image to be shown
     * @param options Current image switch options
     * @param afterCallback Callback that should be invoked once the transition to the next element is complete.
     * @param forwardFlag `true` if switching the next, `false` if switching to the previous element.
     */
    (currSlideElement: JQuery, nextSlideElement: JQuery, options: Configuration, afterCallback: CallbackAfter, forwardFlag: boolean) => void;

    /**
     * Callback fn for pager events
     */
    export type CallbackOnPagerEvent =
    /**
     * @param index 0-based index of the current image.
     * @param slideElement Element that need to be slided.
     */
    (index: number, slideElement: JQuery) => void;

    /**
     * Callback when switching to the next or previous image (prev/next events).
     */
    export type CallbackOnPrevNextEvent = 
    /**
     * @param isNext `true` if switching to the next image, `false` if switching to the previous image.
     * @param index 0-based index of the current image.
     * @param slideElement Element that need to be slided.
     */
    (isNext: boolean, zeroBasedSlideIndex: number, slideElement: JQuery) => void;

    /**
     * Callback for determining per-slide timeout value
     */
    export type CallbackTimeoutFn =
    /**
     * @param currSlideElement Current image before the transition
     * @param nextSlideElement Next image to be shown
     * @param options Current image switch options
     * @param forwardFlag `true` if switching the next, `false` if switching to the previous element.
     * @return Number in milliseconds for the next timeout.
     */
    (currSlideElement: JQuery, nextSlideElement: JQuery, options: Configuration, forwardFlag: boolean) => number;

    /**
     * Callback fn invoked to update the active pager link (adds/removes activePagerClass style)
     */
    export type CallbackUpdateActivePagerLink =
    /**
     * @param pager The current pager
     * @param currSlideElement Current image before the transition
     * @param activePagerClass The CSS class to be added to active pagers. 
     */
    (pager: JQuery, currSlideElement: JQuery, activePagerClass: string) => void;

    /**
     * Represents a two dimensional point
     */
    export interface Point {
        /**
         * The x coordinate of the point in pixels.
         */
        top: number;

        /**
         * The y coordinate of the point in pixels.
         */
        left: number;
    }

    /**
     * Optional settings for the image switch jQuery plugin that can be passed when initializing an image switch
     * instance.
     */
    export interface Configuration {
        /**
         * Class name used for the active pager link
         */
        activePagerClass: string;

        /**
         * Transition callback (this context set to element that was shown)
         */
        after: null | CallbackAfter;

        /**
         * Allows or prevents click event on pager anchors from bubbling
         */
        allowPagerClickBubble: boolean;

        /**
         * An object of CSS properties and values that the animation will move toward. Properties that define how the
         * slide animates in.
         */
        animIn: null | Record<string, string | number>;

        /**
         * An object of CSS properties and values that the animation will move toward. Properties that define how the
         * slide animates out
         */
        animOut: null | Record<string, string | number>;

        /**
         * A number other than `0` to end slideshow after X transitions (where X == slide count)
         */
        autostop: number;

        /**
         * Number of transitions (optionally used with autostop to define X)
         */
        autostopCount: number;

        /**
         * `true` to start slideshow at last slide and move backwards through the stack
         */
        backwards: boolean;

        /**
         * Transition callback (this context set to element to be shown)
         */
        before: null | CallbackBefore;

        /**
         * `true` if clearType corrections should be applied (for IE)
         */
        cleartype: boolean;

        /**
         * Set to true to disable extra cleartype fixing (leave false to force background color setting on slides)
         */
        cleartypeNoBg: boolean;

        /**
         * Resize container to fit largest slide
         */
        containerResize: number;

        /**
         * `true` to start next transition immediately after current one completes
         */
        continuous: number;

        /**
         * Properties that defined the state of the slide after transitioning out
         */
        cssAfter: null | Record<string, string | number>;

        /**
         * Properties that define the initial state of the slide before transitioning in
         */
        cssBefore: null | Record<string, string | number>;

        /**
         * Additional delay (in ms) for first transition (hint: can be negative)
         */
        delay: number;

        /**
         * Easing for "in" transition
         */
        easeIn: null | string;

        /**
         * Easing for "out" transition
         */
        easeOut: null | string;

        /**
         * Easing method for both in and out transitions
         */
        easing: null | string;

        /**
         * Callback invoked when the slideshow terminates (use with autostop or nowrap options)
         */
        end: null | CallbackEnd;

        /**
         * Force fast transitions when triggered manually (via pager or prev/next); value == time in ms
         */
        fastOnEvent: number;

        /**
         * Force slides to fit container
         */
        fit: number;

        /**
         * Name of transition effect (or comma separated names, ex: 'fade,scrollUp,shuffle')
         */
        fx: string;

        /**
         * Function used to control the transition
         */
        fxFn: null | CallbackFxFn;

        /**
         * Container height (if the 'fit' option is true, the slides will be set to this height as well)
         */
        height: string;

        /**
         * Causes manual transition to stop an active transition instead of being ignored
         */
        manualTrump: boolean;

        /**
         * Data attribute that holds the option data for the slideshow
         */
        metaAttr: string;

        /**
         * Selector for element to use as event trigger for next slide
         */
        next: null | string;

        /**
         * `true` to prevent slideshow from wrapping
         */
        nowrap: number;

        /**
         * Callback fn for pager events
         */
        onPagerEvent: null | CallbackOnPagerEvent;

        /**
         * Callback fn for prev/next events
         */
        onPrevNextEvent: null | CallbackOnPrevNextEvent;

        /**
         * Selector for element to use as pager container
         */
        pager: null | string;

        /**
         * Name of event which drives the pager navigation
         */
        pagerEvent: string;

        /**
         * `true` to enable "pause on hover"
         */
        pause: number;

        /**
         * `true` to pause when hovering over pager link
         */
        pauseOnPagerHover: number;

        /**
         * Selector for element to use as event trigger for previous slide
         */
        prev: null | string;

        /**
         * Event which drives the manual transition to the previous or next slide
         */
        prevNextEvent: string;

        /**
         * `true` for random, false for sequence (not applicable to shuffle fx)
         */
        random: number;

        /**
         * Valid when multiple effects are used; true to make the effect sequence random
         */
        randomizeEffects: number;

        /**
         * Requeue the slideshow if any image slides are not yet loaded
         */
        requeueOnImageNotLoaded: boolean;

        /**
         * Delay in milliseconds for requeue.
         */
        requeueTimeout: number;

        /**
         * Causes animations to transition in reverse (for effects that support it such as scrollHorz/scrollVert/shuffle)
         */
        rev: number;

        /**
         * Coordinates for shuffle animation, ex: `{ top:15, left: 200 }`
         */
        shuffle: null | Point;

        /**
         * CSS selector for selecting slides (if something other than all children is required)
         */
        slideExpr: null | string;

        /**
         * Force slide width/height to fixed size before every transition
         */
        slideResize: number;

        /**
         * Speed of the transition in milliseconds (any valid fx speed value)
         */
        speed: number;

        /**
         * Speed of the 'in' transition in milliseconds
         */
        speedIn: null | number;

        /**
         * Speed of the 'out' transition in milliseconds
         */
        speedOut: null | number;

        /**
         * Zero-based index of the first slide to be displayed
         */
        startingSlide: number;

        /**
         * `true` if in/out transitions should occur simultaneously
         */

         sync: number;
        /**
         * Milliseconds between slide transitions (0 to disable auto advance)
         */
        timeout: number;

        /**
         * Callback for determining per-slide timeout value
         */
        timeoutFn: null | CallbackTimeoutFn;

        /**
         * Callback fn invoked to update the active pager link (adds/removes activePagerClass style)
         */
        updateActivePagerLink: null | CallbackUpdateActivePagerLink;

        /**
         * Container width (if the 'fit' option is true, the slides will be set to this width as well)
         */
        width: null | number;
    }
}
interface JQuery {
    cycle(options: Partial<JQueryCycle.Configuration>): this;
    /**
     * Starts slideshow mode.
     * @param method Name of the method to invoke on the image cycle instance.
     * @return this for chaining.
     */
    cycle(method: "resume"): this;
    /**
     * Stops slideshow mode.
     * @param method Name of the method to invoke on the image cycle instance.
     * @return this for chaining.
     */
    cycle(method: "stop"): this;
    /**
     * Stops or starts slideshow mode.
     * @param method Name of the method to invoke on the image cycle instance.
     * @return this for chaining.
     */
    cycle(method: "toggle"): this;
    /**
     * Switches to the next image.
     * @param method Name of the method to invoke on the image cycle instance.
     * @return this for chaining.
     */
    cycle(method: "next"): this;
    /**
     * Pauses slideshow mode.
     * @param method Name of the method to invoke on the image cycle instance.
     * @return this for chaining.
     */
    cycle(method: "pause"): this;
    /**
     * Switches to the previous image.
     * @param method Name of the method to invoke on the image cycle instance.
     * @return this for chaining.
     */
    cycle(method: "prev"): this;
    /**
     * Removes the slideshow from this element.
     * @param method Name of the method to invoke on the image cycle instance.
     * @return this for chaining.
     */
    cycle(method: "destroy"): this;
    /**
     * Switches to the image with given index.
     * @param index 0-based index of the image to switch to.
     * @return this for chaining.
     */
    cycle(index: number): this;
}