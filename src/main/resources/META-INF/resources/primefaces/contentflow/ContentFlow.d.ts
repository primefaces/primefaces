// Type declaration for the content flow library

// Global pollution...

/**
 * Additional properties that will be set on the global `Window` object when the `ContentFlow` widget is loaded.
 */
interface Window {
    /**
     * Adds the given listener for the event.
     * 
     * Defined globally by the ContentFlow widget. __Do not use this.__
     * @deprecated
     * @param eventName Name of an event.
     * @param method Listener to add.
     * @param capture Whether to use the capture or bubble pase.
     */
    addEvent(eventName: string, method: (event: Event) => void, capture: boolean): void;
    /**
     * Removes the given listener for the event.
     * 
     * Defined globally by the ContentFlow widget. __Do not use this.__
     * @deprecated
     * @param eventName Name of an event.
     * @param method Listener to remove.
     * @param capture Whether to use the capture or bubble pase.
     */
    removeEvent(eventName: string, method: (event: Event) => void, capture: boolean): void;
}

/**
 * Additional properties that will be set on the global `Math` object when the `ContentFlow` widget is loaded.
 */
interface Math {
    /**
     * 
     * Defined globally by the ContentFlow widget. __Do not use this.__
     * @deprecated
     */
    _2PI05: number;
    /**
     * One-argument error function
     * 
     * Defined globally by the ContentFlow widget. __Do not use this.__
     * @deprecated
     * @param x Input number
     * @return Value of the cumulative gaussian distribution at the given input.
     */
    erf2(x: number): number;
    /**
     * Computes the natural logarithm.
     * 
     * Defined globally by the ContentFlow widget. __Do not use this.__
     * @deprecated
     * @param x Input number
     * @return The natural logarithm of the input number.
     */
    ln(x: number): number;
    /**
     * Computes the logarithm of a value to the given base.
     * 
     * Defined globally by the ContentFlow widget. __Do not use this.__
     * @deprecated
     * @param x Input number
     * @param b Base
     * @return The logarithm of the given input to the given base.
     */
    logerithm(x: number, b: number): number;
    /**
     * Computes the normal distribution of an input
     * 
     * Defined globally by the ContentFlow widget. __Do not use this.__
     * @deprecated
     * @param x Input number
     * @param standardDeviation Standard deviation parameter of the normal distribution.
     * @param mean Mean parameter of the normal distribution
     * @return Value of the normal distribution at the given input
     */
    normDist(x: number, standardDeviation: number, mean: number): number;
    /**
     * Computes the normed normal distribution of an input
     * 
     * Defined globally by the ContentFlow widget. __Do not use this.__
     * @deprecated
     * @param x Input number
     * @param standardDeviation Standard deviation parameter of the normed normal distribution.
     * @param mean Mean parameter of the normed normal distribution
     * @return Value of the normed normal distribution at the given input
     */
    normedNormDist(x: number, standardDeviation: number, mean: number): number;
}

/**
 * Additional properties that will be set on the global `Event` object when the `ContentFlow` widget is loaded.
 */
interface Event {
    /**
     * Stops the given event from propagating.
     * 
     * Defined globally by the ContentFlow widget. __Do not use this.__
     * @deprecated
     * @param event Event ot stop.
     * @return Whether the event was stopped.
     */
    stop(event: Event): boolean;
}

/**
 * Additional properties that will be set on the global `HTMLElement` object when the `ContentFlow` widget is loaded.
 */
interface HTMLElement {
    /**
     * Adds the given class to this element.
     * 
     * Defined globally by the ContentFlow widget. __Do not use this.__
     * @deprecated
     * @param className Name of a class to add.
     */
    addClassName(className: string): void;
    /**
     * Adds the given listener for the event.
     * 
     * Defined globally by the ContentFlow widget. __Do not use this.__
     * @deprecated
     * @param eventName Name of an event.
     * @param method Listener to add.
     * @param capture Whether to use the capture or bubble pase.
     */
    addEvent(eventName: string, method: (event: Event) => void, capture?: boolean): void;
    /**
     * Finds the position of this element
     * 
     * Defined globally by the ContentFlow widget. __Do not use this.__
     * @deprecated
     * @return The position of this element
     */
    findPos(): ContentFlowGlobal.TopLeftPoint;
    /**
     * Finds children of this element with a given CSS class.
     * 
     * Defined globally by the ContentFlow widget. __Do not use this.__
     * @deprecated
     * @param className Class name of the children.
     * @return Children of this element with the given class name.
     */
    getChildrenByClassName(className: string): HTMLElement[];
    /**
     * Finds the size and width of this element.
     * 
     * Defined globally by the ContentFlow widget. __Do not use this.__
     * @deprecated
     * @return The dimensions of this element.
     */
    getDimensions(): ContentFlowGlobal.RectangularSize;
    /**
     * Checks whether the given CSS class name is set on this element.
     * 
     * Defined globally by the ContentFlow widget. __Do not use this.__
     * @deprecated
     * @param className Class name to check
     * @return Whether this element has the class set
     */
    hasClassname(className: string): boolean;
    /**
     * Removes the given class from this element.
     * 
     * Defined globally by the ContentFlow widget. __Do not use this.__
     * @deprecated
     * @param className Class to remove
     */
    removeClassName(className: string): void;
    /**
     * Removes the given listener for the event.
     * 
     * Defined globally by the ContentFlow widget. __Do not use this.__
     * @deprecated
     * @param eventName Name of an event.
     * @param method Listener to remove.
     * @param capture Whether to use the capture or bubble pase.
     */
    removeEvent(eventName: string, method: (event: Event) => void, capture: boolean): void;
    /**
     * Adds the given CSS class if not present, or removes it otherwise.
     * 
     * Defined globally by the ContentFlow widget. __Do not use this.__
     * @deprecated
     * @param className Class name to toggle.
     */
    toggleClassName(className: string): void;
}

/**
 * The object with global settings for the content flow gallery. Stores, for example, a list of all current content flow
 * instances.
 */
declare namespace ContentFlowGlobal {
    /**
     * Where ever you can use a position keyword you can also use an integer for an exact position. A sanity check will
     * be done automatically.
     * 
     * - start / first: First item
     * - end / last: Last item
     * - middle / center: Item in the middle
     * - pre / previous / left: Previous item
     * - next / right: Next item
     * - visible / visiblePre / visibleLeft: Leftmost visible item
     * - visibleNext / visibleLeft: Rightmost visible item 
     */
    export type PositionKeyword =
        "start" | "first" |
        "end" | "last" |
        "middle" | "center" |
        "pre" | "previous" | "left" |
        "next" | "right" |
        "visible" | "visiblePre" | "visibleLeft" |
        "visibleNext" | "visibleRight";

    /**
     * A relative position for an item.
     */
    export type RelativePosition =
        "top left" | "top center" | "top right" |
        "bottom left" | "bottom center" | "bottom right" |
        "above left" | "above center" | "above right" |
        "below left" | "below center" | "below right";

    /**
     * An RGB hex string in the format `#RRGGBB`. where `R`, `G`, and `B` are each hex digit `0...F`.
     */
    export type RgbHexString = string;

    /**
     * List of browser kinds for which information is available in the `ContentFlowGlobal.Browser` object.
     */
    export type BrowserKind = "Opera" | "IE" | "IE6" | "IE7" | "IE8" | "WebKit" | "iPhone" | "Chrome" | "Safari" | "Konqueror" | "Konqueror4" | "Gecko" | "Gecko19";

    /**
     * A callback function that is invoked when a keydown event is triggered within the content flow gallery.
     */
    export type KeydownHandler = (this: ContentFlowGlobal.ContentFlow) => void; 

    /**
     * A generic event handler that is invoked when an event occured.
     */
    export type EventHandler =
    /**
     * @param event The event that was triggered.
     */
    (event: Event) => void;

    /**
     * Describes a recangular size with a width and height.
     */
    export interface RectangularSize {
        /**
         * Width of the rectangle.
         */
        width: number;
        /**
         * Height of the rectangle.
         */
        height: number;
    }

    /**
     * Describes the top-left corner of a shape.
     */
    export interface TopLeftPoint {
        /**
         * The x position of the top left corner.
         */
        top: number;
        /**
         * The y position of the top left corner.
         */
        left: number;
    }

    /**
     * Describes a point in a two dimensional coordinate system.
     */
    export interface Point {
        /**
         * First coordinate of the point.
         */
        x: number;
        /**
         * Second coordinate of the point.
         */
        y: number;
    }

    /**
     * Describes the configuration for a content flow gallery.
     */
    export interface Configuration {
        /**
         * Addons the ContentFlow should use. Defaults to `all`
         */
        useAddOns: "all" | "none" | string[];
        /**
         * Grace time in milliseconds for images to load. Defaults to `3000`.
         */
        loadingTimeout: number;
        /**
         * Should the Flow wrap around? Defaults to `true`.
         */
        circularFlow: boolean;
        /**
         * Will turn the ContentFlow 90 degree counterclockwise. This will automatically swap calculated positions and
         * sizes where needed. You do not have to adjust any calculations or sizes. Should work with any AddOn out of
         * the box. Defaults to `false`.
         */
        verticalFlow: boolean;
        /**
         * Number of items to show on either side of the active Item. If set to `0` it will be set to the square root of
         * the number of items in the flow. Defaults to `0`.
         */
        visibleItems: number;
        /**
         * The opacity of the last visible item on either side. The opacity of each item will be calculated by the 
         * `calcOpacity` function. Defaults to `1`.
         */
        endOpacity: number;
        /**
         * Active Content item to start with. Defaults to `center`
         */
        startItem: ContentFlowGlobal.PositionKeyword | number;
        /**
         * Flow will start scrolling on load from this item. If set to `none` the flow will not scroll in. Default to
         * `pre`.
         */
        scrollInFrom: ContentFlowGlobal.PositionKeyword | "none" | number;
        /**
         * Set the size of the reflection image relative to the original image. Defaults to `0.5`.
         */
        reflectionHeight: number;
        /**
         * Set the size of the gap between the image and the reflection image relative to the original image size.
         * Defaults to `0`.
         */
        reflectionGap: number;
        /**
         * Set the "surface"-color of the reflection. If set to 'overlay' the image given by the option
         * `reflectionOverlaySrc` will be lain over the reflection. Defaults to `transparent`.
         */
        reflectionColor: "none" | "transparent" | "overlay" | ContentFlowGlobal.RgbHexString;
        /**
         * Factor by which the item will be scaled. Default to `1`.
         */
        scaleFactor: number;
        /**
         * Factor to scale content images in landscape format by. If set to `max`, the height of an landscape image
         * content will be set to the height of the item. Defaults to `1`.
         */
        scaleFactorLandscape: number | "max";
        /**
         * Factor to scale content images in portrait format by. If set to `max`, the width of an portait image content
         * will be set to the width of the item. Defaults to `1`.
         */
        scaleFactorPortrait: number | "max";
        /**
         * Fixes the item size, to the calculated size. No adjustments will be done. Images will be croped if bigger.
         * Defaults to `false`.
         */
        fixItemSize: boolean;
        /**
         * Maximum item height in px. If set to a value greater than `0` the item size will be calculated from this
         * value instead relative to the width of the ContentFlow. Defaults to `0`.
         */
        maxItemHeight: number;
        /**
         * Position of item relative to it's coordinate. Defaults to `top center`. So by default, the item will be
         * placed above the coordinate point and centered horizontally. If set, this option overrides the
         * `calcRelativeItemPosition` option.
         */
        relativeItemPosition: ContentFlowGlobal.RelativePosition;
        /**
         * A flowSpeedFactor > `1` will speedup the scrollspeed, while a factor between `0` and `1` will slow it down.
         * Defaults to `1`.
         */
        flowSpeedFactor: number;
        /**
         * Determines how hard it is to drag the flow. If set to `0` dragging of the flow is deactivated. Defaults to
         * `1`.
         */
        flowDragFriction: number;
        /**
         * Scales by how many items the flow will be moved with one usage of the mousewheel. Negative values will
         * reverse the scroll direction. If set to `0` scrolling with the mouse wheel is deactivated. Defaults to `1`.
         */
        scrollWheelSpeed: number;
        /**
         * Defines the keyCodes and the functions, which are triggerd on a keydown event within the ContentFlow. All
         * defined functions are bound to the ContentFlow object. To disable this functionality, set this to an empty
         * object.
         * 
         * Defaults to
         * 
         * ```javascript
         * {
         *   13: function () { this._onclickActiveItem(this._activeItem) }, // return/enter key
         *   37: function () { this.moveTo('pre') },         // left arrow
         *   38: function () { this.moveTo('visibleNext') }, // up arrow
         *   39: function () { this.moveTo('next') },        // right arrow
         *   40: function () { this.moveTo('visiblePre') }   // down arrow
         * }
         * ```
         */
        keys: Record<number, ContentFlowGlobal.KeydownHandler>;
        /**
         * Called if an inactive item is clicked.
         * @param item The item on which the event occurred.
         */
        onclickInactiveItem(this: ContentFlowGlobal.ContentFlow, item: ContentFlowItem): void;
        /**
         * Called if the active item is clicked.
         * @param item The item on which the event occurred.
         */
        onclickActiveItem(this: ContentFlowGlobal.ContentFlow, item: ContentFlowItem): void;
        /**
         * Called if the active item becomes an inactive item.
         * @param item The item on which the event occurred.
         */
        onMakeInactive(this: ContentFlowGlobal.ContentFlow, item: ContentFlowItem): void;
        /**
         * Called if an item becomes the active item.
         * @param item The item on which the event occurred.
         */
        onMakeActive(this: ContentFlowGlobal.ContentFlow, item: ContentFlowItem): void;
        /**
         * Called each time a new target is set i.e by calling the moveTo method.
         * @param item The item on which the event occurred.
         */
        onMoveTo(this: ContentFlowGlobal.ContentFlow, item: ContentFlowItem): void;
        /**
         * Called if the target item becomes the active item.
         * @param item The item on which the event occurred.
         */
        onReachTarget(this: ContentFlowGlobal.ContentFlow, item: ContentFlowItem): void;
        /**
         * Called if the `pre` button item is clicked.
         * @param item The event that triggered the action.
         */
        onclickPreButton(this: ContentFlowGlobal.ContentFlow, event: Event) : void;
        /**
         * Called if the `next` button item is clicked.
         * @param item The event that triggered the action.
         */
        onclickNextButton(this: ContentFlowGlobal.ContentFlow, event: Event): void;
        /**
         * Called when ever an item is redrawn. Use with caution, because this method is easily called many thousend
         * times.
         * @param item An item to process.
         */
        onDrawItem(this: ContentFlowGlobal.ContentFlow, item: ContentFlowItem): void;
        /**
         * Called to set the calculation function of the width of each step to get the next position of the flow. `diff`
         * is the `targetItemPosition - currentPosition`
         * @param diff The different between the target item position and the current position in pixels.
         * @return The next position in the flow.
         */
        calcStepWidth(this: ContentFlowGlobal.ContentFlow, diff: number): number;
        /**
         * Called to set the calculation function of the size of a visible item
         * @param item An item to process.
         * @return The calculated size of the item.
         */
        calcSize(this: ContentFlowGlobal.ContentFlow, item: ContentFlowItem): ContentFlowGlobal.RectangularSize;
        /**
         * Called to calculate the position of an item element within the flow.
         * @param item An item to process.
         * @return The calculated position of the item within the flow.
         */
        calcCoordinates(this: ContentFlowGlobal.ContentFlow, item: ContentFlowItem): ContentFlowGlobal.Point;
        /**
         * Called to calculate the position of an item relative to it's coordinates. Please note that this function will
         * be overridden by the `relativeItemPosition` option.
         * @param item An item to process.
         * @return The calculated relative position of the item.
         */
        calcRelativeItemPosition(this: ContentFlowGlobal.ContentFlow, item: ContentFlowItem): ContentFlowGlobal.Point;
        /**
         * Called to set the calculation function of the z-index of each item. The z-index is only valid within the
         * flow itself.
         * @param item An item to process.
         * @return The calculated z-index in the range `-32768...32768`.
         */
        calcZIndex(this: ContentFlowGlobal.ContentFlow, item: ContentFlowItem): number;
        /**
         * Called to set the calculation function of the relative font-size of an item.
         * @param item An item to process.
         * @return The calculated font size, must not be negative.
         */
        calcFontSize(this: ContentFlowGlobal.ContentFlow, item: ContentFlowItem): number;
        /**
         * Called to calculate the opacity of each item.
         * @param item An item to process.
         * @return The calculated opacity in the range `0...1`.
         */
        calcOpacity(this: ContentFlowGlobal.ContentFlow, item: ContentFlowItem): number;
    }

    export interface AddOnMethods<TCfg = Record<string, unknown>> {
        /**
         * Defaults for the configuration of the add-on.
         */
        conf: Partial<TCfg>;
        /**
         * ContentFlow configuration. Will overwrite the default configuration (or configuration of previously loaded
         * add-ons).
         */
        ContentFlowConf: Partial<Configuration>;
        /**
         * This method will be executed _after_ the initialization of each ContentFlow.
         * @param flow The content flow gallery for which this add-on was created.
         */
        afterContentFlowInit(flow: ContentFlowGlobal.ContentFlow): void;
        init(this: ContentFlowAddOn, addOn: ContentFlowAddOn): void;
        /**
         * This method will be executed for each ContentFlow on the page after the HTML document is loaded (when the whole
         * DOM exists). You can use it to add elements automatically to the flow.
         * @param flow The content flow gallery for which this add-on was created.
         */
        onloadInit(flow: ContentFlowGlobal.ContentFlow): void;
    }

    /**
     * Interface for the content flow class that represents an instantiated content flow gallery. It is responsible
     * for controlling the gallery.
     */
    export interface ContentFlow {
        /**
         * DOM element of the element with the `.ContentFlow` class
         */
        Container: HTMLElement;
        /**
         * DOM element of the element with the `.flow` class
         */
        Flow: HTMLElement;
        /**
         * DOM element of the element with the `.scrollbar` class
         */
        Scrollbar: HTMLElement;
        /**
         * DOM element of the element with the `.slider` class
         */
        Slider: HTMLElement;
        /**
         * The list of items that are currently in this content flow gallery.
         */
        items: ContentFlowItem[];
        /**
         * The current configuration for this content flow gallery.
         */
        conf: ContentFlowGlobal.Configuration;
        /**
         * Information about the current browser environment. For each browser, the value is `true` when the current browser
         * is of that kind, and `false` otherwise.
         */
        Browser: Record<ContentFlowGlobal.BrowserKind, boolean>;
        /**
         * Called to set options after object creation
         * @param config New options to set.
         */
        setConfig(config: Partial<ContentFlowGlobal.Configuration>): void;
        /**
         * Returns the item at given index.
         * @param index 0-based index of an item to get.
         */
        getItem(index: number): ContentFlowItem | undefined;
        /**
         * Called to get the currently active item.
         * @return The currently active item.
         */
        getActiveItem(): ContentFlowItem | undefined;
        /**
         * Called to get the number of items currently in the flow
         * @return The number of items in this content flow gallery.
         */
        getNumberOfItems(): number;
        /**
         * Called to scroll to item.
         * @param item Item to scroll to. If a number and the fractional part is `0`, interpret the value as the 0-index of
         * an item. If a number with a non-zero fractional part, interpret this value as the position to scroll to.
         */
        moveTo(item: number | ContentFlowGlobal.PositionKeyword | ContentFlowItem): void;
        /**
         * Called to reinitialize the size of the flow items, after the size of the flow has changed. Is called if window is
         * resized.
         */
        resize(): void;
        /**
         * Called to add a new item 'element' to the flow at the position index. The element has to be a valid item
         * element. So it must have at least this HTML strucutre:
         * 
         * ```html
         * <div class="item">
         *     <img class="content" src="url/to/image"/>
         * </div>
         * ```
         * 
         * @param element An element to add to this content flow gallery.
         * @param index 0-based index or position where to add the item.
         * @return 0-based index position of added item.
         */
        addItem(element: HTMLElement, index: number | "first" | "start" | "last" | "end"): number;
        /**
         * Called to remove an item from the flow. If the optional parameter `index` is given, the element at the index
         * position will be removed. Otherwise, the currently active item will be removed.
         * @param index Optinal 0-based index of the item to remove
         * @return The removed item node
         */
        rmItem(index?: number): HTMLElement;
    }

    /**
     * An map with the name of an add-on as the key and the add-on object as the value.
     */
    export const AddOns: Record<string, ContentFlowAddOn>;

    /**
     * Information about the current browser environment. For each browser, the value is `true` when the current browser
     * is of that kind, and `false` otherwise.
     */
    export const Browser: Record<ContentFlowGlobal.BrowserKind, boolean>;

    /**
     * A list of `ContentFlow` instances that are currently active.
     */
    export const Flows: ContentFlowGlobal.ContentFlow[];

    /**
     * Finds the configuration of a given add-on.
     * @param addOnName Name of an add-on.
     * @return The configuration of the given add-on.
     */
    export function getAddOnConf(addOnName: string): any;

    /**
     * Sets the configuration of an add-on to a given value.
     * @param addOnName Name of an add-on.
     * @param addOnConf New configuration to set.
     */
    export function setAddOnConf(addOnName: string, addOnConf: any): void;

    /**
     * Loads and executes the given JavaScript file.
     * @param path URL to a JavaScript file.
     */
    export function addScript(path: string): void;

    /**
     * Loads and executes the given JavaScript files.
     * @param basePath Base URL to use
     * @param fileNames List of JavaScript files, relative to the given `basePath`.
     */
    export function addScripts(basePath: string, fileNames: string[]): void;

    /**
     * Loads and applies the given CSS file.
     * @param path URL to a CSS file.
     */
    export function addStylesheet(path: string): void;

    /**
     * Loads and applies the given CSS files.
     * @param basePath Base URL to use
     * @param fileNames List of CSS files, relative to the given `basePath`.
     */
    export function addStylesheets(basePath: string, fileNames: string[]): void;
}

/**
 * ContentFlow is a flexible flow written in javascript, which can handle any kind of content.
 * For documentation, see
 * - https://web.archive.org/web/20111231134932/http://www.jacksasylum.eu/ContentFlow/docu.php
 * - https://github.com/tkounenis/bower-contentflow
 */
declare class ContentFlow implements ContentFlowGlobal.ContentFlow {
    /**
     * Creates a new content flow gallery on the given HTML element.
     * @param id ID of the DOM element to use for the content flow gallery.
     * @param options Options for the content flow gallery.
     */
    constructor(id: string, options: Partial<ContentFlowGlobal.Configuration>);
    /**
     * DOM element of the element with the `.ContentFlow` class
     */
    Container: HTMLElement;
    /**
     * DOM element of the element with the `.flow` class
     */
    Flow: HTMLElement;
    /**
     * DOM element of the element with the `.scrollbar` class
     */
    Scrollbar: HTMLElement;
    /**
     * DOM element of the element with the `.slider` class
     */
    Slider: HTMLElement;
    /**
     * The list of items that are currently in this content flow gallery.
     */
    items: ContentFlowItem[];
    /**
     * The current configuration for this content flow gallery.
     */
    conf: ContentFlowGlobal.Configuration;
    /**
     * Information about the current browser environment. For each browser, the value is `true` when the current browser
     * is of that kind, and `false` otherwise.
     */
    Browser: Record<ContentFlowGlobal.BrowserKind, boolean>;
    /**
     * @override
     * @inheritdoc
     */
    setConfig(config: Partial<ContentFlowGlobal.Configuration>): void;
    /**
     * @override
     * @inheritdoc
     */
    getItem(index: number): ContentFlowItem | undefined;
    /**
     * @override
     * @inheritdoc
     */
    getActiveItem(): ContentFlowItem | undefined;
    /**
     * @override
     * @inheritdoc
     */
    getNumberOfItems(): number;
    /**
     * @override
     * @inheritdoc
     */
    moveTo(item: number | ContentFlowGlobal.PositionKeyword | ContentFlowItem): void;
    /**
     * @override
     * @inheritdoc
     */
    resize(): void;
    /**
     * @override
     * @inheritdoc
     */
    addItem(element: HTMLElement, index: number | "first" | "start" | "last" | "end"): number;
    /**
     * @override
     * @inheritdoc
     */
    rmItem(index?: number): HTMLElement;
}

/**
 * Global registry for registering add-on for the content flow gallery. An add-on should create a new instance of this
 * class and pass its implementation as the `methods` argument to the constructor. It will be registered automatically.
 * @typeparam TCfg Type of the configuration for the add-on.
 */
declare class ContentFlowAddOn<TCfg = any> {
    /**
     * Current configuration of this add-on.
     */
    conf: TCfg;
    /**
     * Information about the current browser environment. For each browser, the value is `true` when the current browser
     * is of that kind, and `false` otherwise.
     */
    Browser: Record<ContentFlowGlobal.BrowserKind, boolean>;
    /**
     * Creates a new content flow add-on.
     * @param name Name of the add-on.
     * @param methods Object with the implementation of the add-on.
     * @param register Whether to register as an add-on to the content flow library automatically.
     */
    constructor(name: string, methods?: Partial<ContentFlowGlobal.AddOnMethods>, register?: boolean);
    addScript: typeof ContentFlowGlobal.addScript;
    addScripts: typeof ContentFlowGlobal.addScripts;
    addStylesheet: typeof ContentFlowGlobal.addStylesheet;
    addStylesheets: typeof ContentFlowGlobal.addStylesheets;
    /**
     * Sets the configuration of this add-on to the given confugration.
     * @param cfg New configuration to set.
     */
    setConfig(cfg: TCfg): void;
    /**
     * Called after this add-on was created.  It's mostly intended to automatically add additional stylesheet and
     * JavaScript files.
     * @param flow The content flow gallery for which this add-on was created.
     */
    protected _init(flow: ContentFlowGlobal.ContentFlow): void;
}

/**
 * A GUI element for the content flow gallery, such as a slider or button, or the main gallery element itself.
 */
declare class ContentFlowGUIElement extends HTMLElement {
    /**
     * Information about the current browser environment. For each browser, the value is `true` when the current browser
     * is of that kind, and `false` otherwise.
     */
    Browser: Record<ContentFlowGlobal.BrowserKind, boolean>;
    /**
     * Creates a new GUI element for the content flow.
     * @param contentFlow Content flow instance for which this GUI element is created.
     * @param element Container element of the GUI element to create.
     */
    constructor(contentFlow: ContentFlowGlobal.ContentFlow, element: HTMLElement);
    /**
     * Initializes this GUI element by setting its size and position.
     */
    setDimensions(): void;
    /**
     * @param eventName Name of an event to listen to.
     * @param method Callback to invoke when the event occurs.
     */
    addObserver(eventName: string, method: ContentFlowGlobal.EventHandler): void;
    /**
     * @param onDrag Callback for when the element is dragged.
     * @param beforeDrag Called before a drag begins.
     * @param afterDrag Called after a drag ended.
     */
    makeDraggable(onDrag: ContentFlowGlobal.EventHandler, beforeDrag: ContentFlowGlobal.EventHandler, afterDrag: ContentFlowGlobal.EventHandler): void;
}

declare class ContentFlowItem {
    /**
     * Creates a new content flow item.
     * @param contentFlow The content flow gallery to which the item belongs to.
     * @param element The DOM element of the content flow item.
     * @param index The index of the content flow item in the gallery.
     */
    constructor(contentFlow: ContentFlowGlobal.ContentFlow, element: HTMLElement, index: number);
    /**
     * DOM element of the item
     */
    element: HTMLElement;
    /**
     * The previous item
     */
    pre: ContentFlowItem;
    /**
     * The next item
     */
    next: ContentFlowItem;
    /**
     * DOM element of the item content
     */
    content: HTMLElement;
    /**
     * DOM element of the item caption
     */
    caption: HTMLElement;
    /**
     * DOM element of the item label
     */
    label: HTMLElement;
    /**
     * Index of item
     */
    index: number;
    /**
     * Index of item
     */
    position: number;
    /**
     * Relative position of item. `(-visibleItems <= relativePosition <= visibleItems)`.
     */
    relativePosition: number;
    /**
     * Normed relative position of item. `(-1 <= relativePosition <= 1)`
     */
    relativePositionNormed: number;
    /**
     * Left (-1), center (0), or right (1).
     */
    side: -1 | 0 | 1;
    /**
     * Size object as returned by calcSize. Both `width` and `height` are within the range `0...1`.
     */
    size: ContentFlowGlobal.RectangularSize;
}