/**
 * Namespace for the rangy TouchSwipe plugin.
 * 
 * Contains some additional types and interfaces required for the typings.
 * 
 * A jQuery plugin to be used on touch devices such as iPad, iPhone, Android etc.
 * 
 * Detects single and multiple finger swipes, pinches and falls back to mouse 'drags' on the desktop.
 * 
 * Time and distance thresholds can be set to distinguish between swipe gesture and slow drag.
 * 
 * Allows exclusion of child elements (interactive elements) as well allowing page scrolling or page zooming depending on configuration.
 * 
 * See https://github.com/mattbryson/TouchSwipe-Jquery-Plugin.
 */
declare namespace JQueryTouchSwipe {

    /**
     * Event handler for pointer related events.
     */
    export type PointerHandler =
        /**
         * @param event The original event object
         * @param target The element clicked on.
         */
        (this: HTMLElement, event: JQuery.TriggeredEvent, target: HTMLElement) => void;

    /**
     * Event handler for swipe related events.
     */
    export type SwipeHandler =
        /**
         * @param event The original event object
         * @param direction The direction the user swiped in.
         * @param distance The distance the user swiped.
         * @param duration The duration of the swipe in milliseconds.
         * @param fingerCount The number of fingers used.
         * @param fingerData The coordinates of fingers in event.
         * @param currentDirection The current direction the user is swiping.
         */
        (
            this: HTMLElement,
            event: JQuery.TriggeredEvent,
            direction: DirectionsEnum[keyof DirectionsEnum],
            distance: number,
            duration: number,
            fingerCount: FingersEnum[keyof FingersEnum],
            fingerData: FingerData,
            currentDirection: DirectionsEnum[keyof DirectionsEnum]
        ) => void;

    /**
     * Event handler for swipe related events during a certain phase.
     */
    export type SwipePhaseHandler =
        /**
         * @param event The original event object
         * @param phase The phase of the swipe event.
         * @param direction The direction the user swiped in.
         * @param distance The distance the user swiped.
         * @param duration The duration of the swipe in milliseconds.
         * @param fingerCount The number of fingers used.
         * @param fingerData The coordinates of fingers in event.
         * @param currentDirection The current direction the user is swiping.
         */
        (
            this: HTMLElement,
            event: JQuery.TriggeredEvent,
            phase: PhasesEnum[keyof PhasesEnum],
            direction: DirectionsEnum[keyof DirectionsEnum],
            distance: number,
            duration: number,
            fingerCount: FingersEnum[keyof FingersEnum],
            fingerData: FingerData,
            currentDirection: DirectionsEnum[keyof DirectionsEnum]
        ) => void;

    /**
     * Event handler for pinch related events.
     */
    export type PinchHandler =
        /**
         * @param event The original event object
         * @param direction The direction the user swiped in.
         * @param distance The distance the user swiped.
         * @param duration The duration of the swipe in milliseconds.
         * @param fingerCount The number of fingers used.
         * @param zoom The zoom/scale level the user pinched too, 0-1.
         * @param currentDirection The current direction the user is swiping.
         */
        (
            this: HTMLElement,
            event: JQuery.TriggeredEvent,
            direction: DirectionsEnum[keyof DirectionsEnum],
            distance: number,
            duration: number,
            fingerCount: FingersEnum[keyof FingersEnum],
            zoom: number,
            currentDirection: DirectionsEnum[keyof DirectionsEnum]
        ) => void;

    /**
     * Event handler for pinch related events during a certain phase.
     */
    export type PinchPhaseHandler =
        /**
         * @param event The original event object
         * @param phase The phase of the swipe event.
         * @param direction The direction the user swiped in.
         * @param distance The distance the user swiped.
         * @param duration The duration of the swipe in milliseconds.
         * @param fingerCount The number of fingers used.
         * @param zoom The zoom/scale level the user pinched too, 0-1.
         * @param currentDirection The current direction the user is swiping.
         */
        (
            this: HTMLElement,
            event: JQuery.TriggeredEvent,
            phase: PhasesEnum[keyof PhasesEnum],
            direction: DirectionsEnum[keyof DirectionsEnum],
            distance: number,
            duration: number,
            fingerCount: FingersEnum[keyof FingersEnum],
            zoom: number,
            currentDirection: DirectionsEnum[keyof DirectionsEnum]
        ) => void;

    /**
     * The direction constants that are passed to the event handlers. These properties are read-only, attempting to
     * change them will not alter the values passed to the event handlers.
     */
    export interface DirectionsEnum {
        /**
         * Constant indicating the left direction. 
         */
        LEFT: "left";

        /**
         * Constant indicating the right direction. 
         */
        RIGHT: "right";

        /**
         * Constant indicating the up direction. 
         */
        UP: "up";

        /**
         * Constant indicating the down direction. 
         */
        DOWN: "down";

        /**
         * Constant indicating the in direction. 
         */
        IN: "in";

        /**
         * Constant indicating the out direction. 
         */
        OUT: "out";
    }

    /**
     * Constants representing the number of fingers used in a swipe. These are used to set both the value of fingers in
     * the options object, as well as the value of the fingers event property. These properties are read-only,
     * attempting to change them will not alter the values passed to the event handlers.
     */
    export interface FingersEnum {
        /**
         * Constant indicating 1 finger is to be detected / was detected. 
         */
        ONE: 1;

        /**
         * Constant indicating 2 finger is to be detected / was detected. 
         */
        TWO: 2;

        /**
         * Constant indicating 3 finger is to be detected / was detected. 
         */
        THREE: 3;

        /**
         * Constant indicating 4 finger is to be detected / was detected. 
         */
        FOUR: 4;

        /**
         * Constant indicating 5 finger is to be detected / was detected. 
         */
        FIVE: 5;

        /**
         * Constant indicating any combination of finger are to be detected. 
         */
        ALL: "all";
    }

    /**
     * The page scroll constants that can be used to set the value of {@link TouchSwipeSettings.allowPageScroll} option.
     * These properties are read-only.
     */
    export interface PageScrollEnum {
        /**
         * Constant indicating no page scrolling is allowed.
         * 
         * The page will not scroll when user swipes. 
         */
        NONE: "none";

        /**
         * Constant indicating horizontal page scrolling is allowed.
         * 
         * Will force page to scroll on horizontal swipes.
         */
        HORIZONTAL: "horizontal";

        /**
         * Constant indicating vertical page scrolling is allowed.
         * 
         * Will force page to scroll on vertical swipes.
         */
        VERTICAL: "vertical";

        /**
         * Constant indicating either horizontal or vertical will be allowed, depending on the swipe handlers
         * registered.
         * 
         * All undefined swipes will cause the page to scroll in that direction.
         */
        AUTO: "auto";
    }

    /**
     * The phases that a touch event goes through. The phase is passed to the event handlers. These properties are
     * read-only, attempting to change them will not alter the values passed to the event handlers.
     */
    export interface PhasesEnum {
        /**
         * Constant indicating the start phase of the touch event. 
         */
        PHASE_START: "start";

        /**
         * Constant indicating the move phase of the touch event. 
         */
        PHASE_MOVE: "move";

        /**
         * Constant indicating the end phase of the touch event. 
         */
        PHASE_END: "end";

        /**
         * Constant indicating the cancel phase of the touch event. 
         */
        PHASE_CANCEL: "cancel";
    }

    /**
     * Represents some details about the position of the fingers.
     */
    export interface FingerData {
        /**
         * The point where the swipe started. 
         */
        start: CartesianPoint;
 
        /**
         * The point where the finger(s) were more recently. 
         */
        last: CartesianPoint;
 
        /**
         * The point where the swipe ended. 
         */
        end: CartesianPoint;
    }

    /**
     * Represents a two dimensional point in a cartesian coordinate system. 
     */
    export interface CartesianPoint {
        /**
         * The horizontal coordinate of the point. 
         */
        x: number;

        /**
         * The vertical coordinate of the point. 
         */
        y: number;
    }

    export interface TouchSwipeSettings {
        /**
         * A handler triggered when a user reaches {@link longTapThreshold} on the item.
         *
         * Defaults to `null`.
         */
        hold: PointerHandler | null;

        /**
         * How the browser handles page scrolls when the user is swiping on a touchSwipe object. See {@link pageScroll}.  
         *
         * Defaults to `auto`.
         */
        allowPageScroll: PageScrollEnum[keyof PageScrollEnum];

        /**
         * The number of pixels that the user must move their finger back from the original swipe direction to cancel
         * the gesture.
         *
         * Defaults to `null`.
         */
        cancelThreshold: number | null;

        /**
         * A handler triggered when a user double taps on the item. The delay between taps can be set with the
         * {@link doubleTapThreshold} property.
         *
         * Defaults to `null`.
         */
        doubleTap: PointerHandler | null;

        /**
         * Time in milliseconds between 2 taps to count as a double tap.
         *
         * Defaults to `200`.
         */
        doubleTapThreshold: number;

        /**
         * A jQuery selector that specifies child elements that do NOT trigger swipes. By default this excludes elements
         * with the class `.noSwipe`.
         *
         * Defaults to `".noSwipe"`.
         */
        excludedElements: string;

        /**
         * If `true` mouse events are used when run on a non-touch device, `false` will stop swipes being triggered by
         * mouse events on non-touch devices.
         *
         * Defaults to `true`.
         */
        fallbackToMouseEvents: boolean;

        /**
         * Time in milliseconds between releasing multiple fingers.  If 2 fingers are down, and are released one after
         * the other, if they are within this threshold, it counts as a simultaneous release.
         *
         * Defaults to `250`.
         */
        fingerReleaseThreshold: number;

        /**
         * The number of fingers to detect in a swipe. Any swipes that do not meet this requirement will NOT trigger
         * swipe handlers.
         *
         * Defaults to `1`.
         */
        fingers: number;

        /**
         * A handler triggered when a user long taps on the item. The delay between start and end can be set with the
         * {@link longTapThreshold} property.
         *
         * Defaults to `null`.
         */
        longTap: PointerHandler | null;

        /**
         * Time in milliseconds between tap and release for a long tap.
         *
         * Defaults to `500`.
         */
        longTapThreshold: number;

        /**
         * Time, in milliseconds, between `touchStart` and `touchEnd` must NOT exceed in order to be considered a swipe.
         *
         * Defaults to `null`.
         */
        maxTimeThreshold: number | null;

        /**
         * A handler triggered for pinch in events. See also the `pinchIn` event.
         *
         * Defaults to `null`.
         */
        pinchIn: PinchHandler | null;

        /**
         * A handler triggered for pinch out events. See also the `pinchOut` event.
         *
         * Defaults to `null`.
         */
        pinchOut: PinchHandler | null;

        /**
         * A handler triggered for every phase of a pinch. See also the `pinchStatus` event.
         *
         * Defaults to `null`.
         */
        pinchStatus: PinchPhaseHandler;

        /**
         * The number of pixels that the user must pinch their finger by before it is considered a pinch.
         *
         * Defaults to `20`.
         */
        pinchThreshold: number;

        /**
         * By default events are cancelled, so the page does not move. You can disable this so both native events fire
         * as well as your handlers.
         *
         * Defaults to `true`.
         */
        preventDefaultEvents: boolean;

        /**
         * A handler to catch all swipes. See also the `swipe` event.
         *
         * Defaults to `null`.
         */
        swipe: SwipeHandler | null;

        /**
         * A handler that is triggered for `down` swipes. See also the `swipeDown` event.
         *
         * Defaults to `null`.
         */
        swipeDown: SwipeHandler | null;

        /**
         * A handler that is triggered for `left` swipes. See also the `swipeLeft` event.
         *
         * Defaults to `null`.
         */
        swipeLeft: SwipeHandler | null;

        /**
         * A handler that is triggered for `right` swipes. See also the `swipeRight` event.
         *
         * Defaults to `null`.
         */
        swipeRight: SwipeHandler | null;

        /**
         * A handler triggered for every phase of the swipe. See also the `swipeStatus` event.
         *
         * Defaults to `null`.
         */
        swipeStatus: SwipePhaseHandler | null;

        /**
         * A handler that is triggered for `up` swipes. See also the `swipeUp` event.
         *
         * Defaults to `null`.
         */
        swipeUp: SwipeHandler | null;

        /**
         * A handler triggered when a user just taps on the item, rather than swipes it. If they do not move, tap is
         * triggered, if they do move, it is not.
         *
         * Defaults to `null`.
         */
        tap: PointerHandler | null;

        /**
         * The number of pixels that the user must move their finger by before it is considered a swipe.
         *
         * Defaults to `75`.
         */
        threshold: number;

        /**
         * If `true`, the swipe events are triggered when the touch end event is received (user releases finger).  If
         * `false`, it will be triggered on reaching the threshold, and then cancel the touch event automatically.
         *
         * Defaults to `true`.
         */
        triggerOnTouchEnd: boolean;

        /**
         * If true, then when the user leaves the swipe object, the swipe will end and trigger appropriate handlers.
         *
         * Defaults to `false`.
         */
        triggerOnTouchLeave: boolean;
    }

    export interface TouchSwipeNamespace<TElement = HTMLElement> {
        /**
         * Initializes TouchSwipe with the given settings.
         * @return this jQuery instance for chaining.
         */
        (settings?: Partial<JQueryTouchSwipe.TouchSwipeSettings>): JQuery<TElement>;

        /**
         * Destroy the swipe plugin completely. To use any swipe methods, you must re initialize the plugin.
         * @param method The method to call on the TouchSwipe plugin.
         * @return this jQuery instance for chaining.
         */
        (method: "destroy"): JQuery<TElement>;

        /**
         * Disables the swipe plugin
         * @param method The method to call on the TouchSwipe plugin.
         * @return this jQuery instance for chaining.
         */
        (method: "disable"): JQuery<TElement>;

        /**
         * Re-enables the swipe plugin with the previous configuration
         * @param method The method to call on the TouchSwipe plugin.
         * @return this jQuery instance for chaining.
         */
        (method: "enable"): JQuery<TElement>;

        /**
         * Retrieves the option with the given name.
         * @typeparam K Name of the option to retrieve.
         * @param method The method to call on the TouchSwipe plugin.
         * @param name Name of the option to retrieve.
         * @return The current value of the given option.
         */
        <K extends keyof TouchSwipeSettings>(method: "option", name: K): TouchSwipeSettings[K];

        /**
         * Updates the given option with a new value.
         * @typeparam K Name of the option to retrieve.
         * @param method The method to call on the TouchSwipe plugin.
         * @param name Name of the option to update.
         * @param value New value for the option.
         * @return The current value of the given option.
         */
        <K extends keyof TouchSwipeSettings>(method: "option", name: K, value: TouchSwipeSettings[K]): JQuery<TElement>;

        /**
         * Updates the given options.
         * @param method The method to call on the TouchSwipe plugin.
         * @param settings The new settings to apply.
         * @return The current value of the given option.
         */
        (method: "option", settings: Partial<TouchSwipeSettings>): JQuery<TElement>;

        /**
         * Retrieves the current settings of the TouchSwipe plugin.
         * @param method The method to call on the TouchSwipe plugin.
         * @return The current settings.
         */
        (method: "option"): TouchSwipeSettings;

        /**
         * The direction constants that are passed to the event handlers.
         */
        directions: DirectionsEnum;

        /**
         * Constants representing the number of fingers used in a swipe.
         */
        fingers: FingersEnum;

        /**
         * The page scroll constants that can be used to set the value of
         * {@link JQueryTouchSwipe.TouchSwipeSettings.allowPageScroll|TouchSwipeSettings.allowPageScroll} option.
         */
        pageScroll: PageScrollEnum;

        /**
         * The phases that a touch event goes through.
         */
        phases: PhasesEnum;

        /**
         * The version of the plugin. 
         */
        version: string;
    }
}

interface JQuery<TElement = HTMLElement> {
    /**
     * The main function of the TouchSwipe plugin
     * 
     * Also contains some constants and the default settings.
     */
    swipe: JQueryTouchSwipe.TouchSwipeNamespace<TElement>;
}