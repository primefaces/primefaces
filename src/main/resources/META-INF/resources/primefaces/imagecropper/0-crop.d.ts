/**
 * Namespace for the Jcrop JQueryUI plugin, accessible via `$.Jcrop` and `$.fn.Jcrop`. It is used by the `ImageCropper`
 * widget.
 * 
 * This namespace contains several required interfaces and types.
 */
declare namespace JQueryJcrop {
    /**
     * Represents a cardinal direction with the 4 main direction:
     * - `n`: North
     * - `e`: East
     * - `s`: South
     * - `w`: West
     */
    export type CardinalDirection = "n" | "s" | "e" | "w";

    /**
     * Represents a cardinal direction with 4 additional directions:
     * - `ne`: North east
     * - `se`: South east
     * - `sw`: South west
     * - `nw`: North west
     */
    export type ExtendedCardinalDirection = CardinalDirection | "ne" | "se" | "sw" | "nw";

    /**
     * List of filter names that are available by default. More filters can be registered manually.
     */
    export type DefaultFilterTypes = "backoff" | "constrain" | "extent" | "grid" | "ratio" | "round" | "shader";

    /**
     * Represents a rectangular area in pixels: `[width, height]`.
     */
    export type AreaArray = [number, number];

    /**
     * Represents a rectangular area at a certain position: `[x, y, width, height]`. `x` and `y` are the coordinates of
     * the top-left corner of the rectanular array. The origin of the coordinate system is at the top left corner of the
     * image.
     */
    export type OffsetAreaArray = [number, number, number, number];

    /**
     * Represents a rectangular area at a certain position. The origin of the coordinate system is at the top left
     * corner of the image.
     */
    export interface OffsetArea {
        /**
         * Height of the rectangular area in pixels.
         */
        h: number;
        /**
         * The x coordinate of the top-left corner of the rectangular area in pixels.
         */
        x: number;
        /**
         * The y coordinate of the top-left corner of the rectangular area in pixels.
         */
        y: number;
        /**
         * Width of the rectangular area in pixels.
         */
        w: number;
    }

    /**
     * Represents the dimensions of a rectangular area. Normally used for the dimensions of a selection on the image.
     * The coordinate system has got the origin at the top left corner of the image.
     */
    export interface OffsetAreaWithBottomLeft extends OffsetArea {
        /**
         * The x coordinate of the bottom right corner of the rectangular area in pixels.
         */
        x2: number;
        /**
         * The y coordinate of the bottom right corner of the rectangular area in pixels.
         */
        y2: number;
    }

    /**
     * Represents the configuration for a `Selection`.
     */
    export interface SelectionConfiguration {
        /**
         * Minimum size the selection is allowed to have, defaults to `[8,8]`. `0` means no limit.
         */
        minSize: AreaArray;
        /**
         * Minimum size the selection is allowed to have, defaults to `[0,0]`. `0` means no limit.
         */
        maxSize: AreaArray;
        /**
         * Aspect ration the selection must have. `0` means no restriction.
         */
        aspectRatio: number;
        /**
         * Spacings around the edges of the selection, defaults to no spacing.
         */
        edge: Spacings;
        /**
         * Color of the background of this selection. Defaults to no color.
         */
        bgColor: string | null;
        /**
         * Opacity of the background of this selection.
         */
        bgOpacity: number | null;
        /**
         * The size and position of the selection.
         */
        last: OffsetAreaWithBottomLeft | null;
        /**
         * Whether the selection is currently active.
         */
        active: boolean;
        /**
         * Whether the selection is linked to another selection.
         */
        linked: boolean;
        /**
         * Whether the selection can be deleted. Defaults to `true`.
         */
        canDelete: boolean;
        /**
         * Whether the seelction can be moved via drag & drop. Defaults to `true`.
         */
        canDrag: boolean;
        /**
         * Whether the size of the selection can be changed. Defaults to `true`.
         */
        canResize: boolean;
        /**
         * Whether the area can be made active by selecting it. Defaults to `true`.
         */
        canSelect: boolean;  
    }

    /**
     * Represents a selection (`crop area`) of the image. In multi mode, multiple selections can be set on an image.
     */
    export interface Selection extends SelectionConfiguration {
        /**
         * Container element of this selection.
         */
        element: JQuery;
        /**
         * A list of filters that are active for this selection.
         */
        filter: Filter[];
        /**
         * Initializes this selection, normally called automatically when creating a new Selection via
         * `Jcrop#newSelection`. 
         * @param jcrop The Jcrop instance this selection should be associated with.
         */
        init(jcrop: Jcrop): void;
        /**
         * Applies the given set of options to this selection.
         * @param options New options to set.
         * @return this for chaining.
         */
        setOptions(options: Partial<SelectionConfiguration>): this;
        /**
         * Refreshes this selection and redraws it.
         */
        refresh(): void;
        /**
         * Adds a filter to the list of active filters of this selection.
         * @param filter Filter to add.
         */
        addFilter(filter: Filter): void;
        /**
         * Checks whether the given filter is active for this selection.
         * @param filter Filter to check,
         * @return `true` if the given filter is active for this selection, `false` otherwise.
         */
        hasFilter(filter: Filter): boolean;
        /**
         * Removes all filters from this selection.
         */
        clearFilters(): void;
        /**
         * Removes the given filter from the list of active filters of this selection.
         * @param tag Unique name of the filter to remove.
         */
        removeFilter(tag: string): void;
        /**
         * Changes whether this selection is allowed to be selected.
         * @param allow `true` to allow selecting this selection, `false` otherwise.
         */
        allowSelect(allow?: boolean): this;
        /**
         * Changes whether the size of this selection is allowed to be changed.
         * @param allow `true` to allow resizing this selection, `false` otherwise.
         */
        allowResize(allow?: boolean): this;
        /**
         * Changes whether this selection is allowed to be moved via dragging.
         * @param allow `true` to allow dragging this selection, `false` otherwise.
         */
        allowDrag(allow?: boolean): this;
        /**
         * Move this selection to the back, so that other selections may now be in front of it. Also makes this
         * selection the active selection.
         */
        toBack(): void;
        /**
         * Move this selection to the back, so this selections may is now  in front of all other selections. Also makes
         * this selection the active selection.
         */
        toFront(): void;
        /**
         * Smoothly animates this current selection to the given position and size.
         * @param dimensions New position and size for this current selection.
         * @param callback Callback that is invoked when animation is done.
         */
        animateTo(dimensions: OffsetAreaArray, callback?: () => void): void;
        /**
         * Moves this selection to the center of the image.
         * @param instantaneously `false` animate the transition, `true` otherwise. Implementation note:
         * __Due to a bug, settings this to `true` will currently result in a runtime error.__
         */
        center(instantaneously?: boolean): void;
        /**
         * Moves this selection to the specified position (relative to the top-left corner of the image).
         * @param x x position in pixels to move to
         * @param y y position in pixels to move to
         */
        moveTo(x: number, y: number): void;
        /**
         * Changes the size of this selection to the specified width and height.
         * @param width New width in pixels.
         * @param height New height in pixels.
         */
        resize(width: number, height: number): void;
        /**
         * Computes the position and size of this selection from the DOM element.
         * @return The current position and size of this selection.
         */
        get(): OffsetAreaWithBottomLeft;
    }

    /**
     * Represents the configuration of the key watcher for the image cropper.
     */
    export interface KeyWatcherConfiguration {
        /**
         * Event name to listen to for keyboard events. Defaults to `keydown.jcrop`.
         */
        eventName: string;
    }

    /**
     * Interface for the key watch of the image cropper, provides support for interacting with selections via the
     * keyboard (moving, deleting etc.).
     */
    export interface KeyWatcher {
        /**
         * The Jcrop instance associated with this key watcher.
         */
        core: Jcrop;
        /**
         * Disables this key watcher so that selections cannot be moved or deleted with the keyboard anymore.
         */
        disable(): void;
        /**
         * Disables this key watcher so that selections can nnow be moved or deleted with the keyboard.
         */
        enable(): void;
    }

    export interface KeyWatcherStatic {
        defaults: KeyWatcherConfiguration;
        /**
         * Creates and initializes a new key watcher for keyboard support.
         * @param core The Jcrop instance to which this key cropper should be associated.
         */
        new(core: Jcrop): KeyWatcher;
    }

    export interface SelectionStatic {
        /**
         * Default settings used when creating new selections.
         */
        defaults: SelectionConfiguration;
        /**
         * Creates a new selection. Afterwards, you should call `init` on the selection. Prefer using
         * `Jcrop#newSelection` that performs this automatically.
         */
        new(): Selection;
    }

    /**
     * Represents a filter that may be registered with the Jcrop.
     */
    export interface Filter {
        /**
         * Unique name of the filter. This is also the name that must be used in the  `Configuration#applyFilters`
         * setting.
         */
        tag: string;
        /**
         * Priority of this filter. Used to determine an order in which the registered filters are applied.
         */
        priority: number;
        /**
         * Called with a crop area. May modify the given crop area and should return it.
         * @param area Current crop area to filter.
         * @param type The type of action that triggered the filter. `move` is when a selected was dragged.
         * @param selection The current selection.
         * @return The (possibly modified) crop area or a new crop area to use.
         */
        filter(area: OffsetAreaWithBottomLeft, ord: ExtendedCardinalDirection | "move" | undefined , selection: Selection): OffsetAreaWithBottomLeft;
        /**
         * Called when a selection is added or selected.
         * @param selection The newly added or selected selection.
         */
        refresh(selection: Selection): void;
    }

    /**
     * Represents the static methods of a filter class for the image cropper.
     */
    export type FilterStatic = new() => Filter;

    export interface JcropUi {
        /**
         * The key watcher currently in use by this image cropper.
         */
        keyboard: KeyWatcher;
        /**
         * The currently active selection. `undefined` if no selection is currently active.
         */
        selection: Selection | undefined;
        /**
         * A list of all selections set on the image (whether active or not).
         */
        multi: Selection[];
    }

    /**
     * The interface for the `Jcrop` class that is the main entry point to the image cropper API. Given an IMAGE element
     * to which Jcrop is attached, you can access an instance of Jcrop like this:
     * 
     * ```javascript
     * const jcrop = $("img.jcrop").Jcrop("api");
     * ``` 
     */
    export interface Jcrop {
        /**
         * Container element of the image croper user interface.
         */
        container: JQuery;
        /**
         * The current configuration of this image cropper. For setting an option, you should use `setOptions`.
         */
        opt: Readonly<Configuration>;
        /**
         * An object for instances related to the user interface, such as the current selection(s).
         */
        ui: JcropUi;
        /**
         * Adds the given filter to this image cropper.
         * @param filter A filter to add.
         * @return this for chaining.
         */
        addFilter(filter: Filter): this;
        /**
         * Smoothly animates the current selection to the given position and size.
         * @param New position and size for the current selection.
         * @return this for chaining.
         */
        animateTo(dimensions: OffsetAreaArray): this;
        /**
         * Deletes the current selection. Compare with `requestDelete`, which checks whether the selection is allowed
         * to be deleted.
         */
        deleteSelection(): void;
        /**
         * Detaches this image cropper from the image, so that no areas can be selected anymore.
         */
        destroy(): void;
        /**
         * @return The current size of the image crop container in pixels.
         */
        getContainerSize(): AreaArray;
        /**
         * Returns the list of filters that are enabled by default.
         * @return A list of filters that are enabled by default.
         */
        getDefaultFilters(): Filter[];
        /**
         * Finds the crop area set on the image.
         * @return The currently selected crop area. When no crop area is selected, the default crop area (usually the
         * entire image).
         */
        getSelection(): OffsetAreaWithBottomLeft;
        /**
         * When in multi mode: Checks whether the image croper has the given selection, that is, whether it is active.
         * @param selection A crop area selection.
         * @return `true` if the given selection is active on the image, `false` otherwise.
         */
        hasSelection(selection: Selection): boolean;
        /**
         * Initializes this image cropper. Usually called automatically when you create a new image cropper, so there is
         * no need to call this manually.
         */
        init(): void;
        /**
         * Selects the enclosing rectangle of all current selections.
         */
        maxSelect(): void;
        /**
         * Adds a new selection to this image cropper, and makes it the active selection.
         * 
         * The new selection can now be controlled by the API or the end user just like any other selection.
         * 
         * New selections created this way are not set to any initial position. You can use `update` for that:
         * 
         * ```javascript
         * const selection = $("img.jcrop").Jcrop("api").newSelection().update(
         *   $.Jcrop.wrapFromXywh([ 10, 20, 200, 400 ])
         * );
         * ```
         * @param selection Selection to add. When not given, a new selection is created.
         * @return The given selection, or the newly created selection.
         */
        newSelection(selection?: Selection): Selection;
        /**
         * Moves the current selection by the given offset. For example, when you pass `10, 20`, the selection is moved
         * by 10 pixels to the right and by 20 pixels to the bottom.
         * @param deltaX Value in pixels to move the current selection in the x direction.
         * @param deltaY Value in pixels to move the current selection in the y direction.
         */
        nudge(deltaX: number, deltaY: number): void;
        /**
         * Redraws the image cropper.
         */
        refresh(): void;
        /**
         * Removes the given filter from this image cropper. Does nothing if the filter was never set.
         * @param filter A filter to remove.
         * @return this for chaining.
         */
        removeFilter(filter: Filter): this;
        /**
         * When in multi mode: Removes the given selections from this image cropper.
         * @param selection Selection to remove
         * @return The current list of selections, not including the removed selection.
         */
        removeSelection(selection: Selection): Selection[];
        /**
         * Similar to `deleteSelection`. Deletes the current selection, but only if it is deletable.
         */
        requestDelete(): void;
        /**
         * Resizes the image crop container to the given width and height.
         */
        resizeContainer(width: number, height: number): void;
        /**
         * Scales the given area by the current scale settings. This creates a new object and does not modify the
         * argument.
         * @param area Area to scale
         * @return The area scaled by the current scale settings.
         */
        scale(area: OffsetAreaWithBottomLeft): OffsetAreaWithBottomLeft;
        /**
         * Replaces the image shown in this image cropper.
         * @param source URL to the new image.
         * @param callback If given, this callback will be invoked with the width and height of the loaded image once
         * it is loaded.
         * @return `undefined` on success, `false` if the image could not be set.
         */
        setImage(source: string, callback?: (this: Jcrop, width: number, height: number) => void): undefined | false;
        /**
         * Updates the options of this instance with the given options. Options not specified are not modified.
         * @return this for chaining.
         */
        setOptions(opts: Partial<Configuration>): this;
        /**
         * Sets the current selection to the given position and size. Similar to `animateTo`, but sets the given
         * position and size immediately.
         * @param dimensions New position and size to set.
         * @return this for chaining.
         */
        setSelect(dimensions: OffsetAreaArray): this;
        /**
         * When in multimode: Marks the given selection as the currently active one.
         * @param selection Existing selection to set as the active one.
         * @return The new active selection.
         */
        setSelection(selection: Selection): Selection;
        /**
         * Scales the given area by the inverse of the current scale settings, such that `scale(unscale(x)) === x`. This
         * creates a new object and does not modify the argument.
         * @param area Area to scale
         * @return The area scaled by the inverse of current scale settings.
         */
        unscale(area: OffsetAreaWithBottomLeft): OffsetAreaWithBottomLeft;
        /**
         * Updates the given crop area.
         * @param area Area to update.
         */
        update(area: OffsetAreaWithBottomLeft): void;
    }
    /**
     * The interface for the static methods on the `Jcrop` class. You can access it via `$.Jcrop`.
     */
    export interface JcropStatic {
        /**
         * Global default settings used when creating new Jcrop instances.
         */
        defaults: Configuration;
        /**
         * Whether the current browser environment supports canvas.
         */
        supportsCanvas: boolean;
        /**
         * Whether the current browser environment supports text inside a canvas
         */
        supportsCanvasText: boolean;
        /**
         * Whether the current browser environment supports drag & drop.
         */
        supportsDragAndDrop: boolean;
        /**
         * Whether the current browser environment supports data URIs
         */
        supportsDataURI: boolean;
        /**
         * Whether the current browser environment supports SVG elements.
         */
        supportsSVG : boolean;
        /**
         * Whether the current browser environment supports inline SVG elements
         */
        supportsInlineSVG: boolean;
        /**
         * Whether the current browser environment supports SVG clip paths
         */
        supportsSVGClipPaths: boolean;
        /**
         * Whether the current browser environment supports CSS transforms.
         */
        supportsCSSTransforms: boolean;
        /**
         * Whether the current browser environment supports touch related functionality.
         */
        supportsTouch: boolean;
        /**
         * Creates a new Jcrop instance for the given image element and makes it croppable.
         * @param element Image element to which to attach Jcrop.
         * @param opt Options for configuring the image cropper.
         */
        new(element: HTMLImageElement | JQuery<HTMLImageElement> | JQuery, opts?: Partial<Configuration>): Jcrop;
        /**
         * Creates a new Jcrop instance for the given image element and makes it croppable.
         * @param element Image element to which to attach Jcrop.
         * @param opt Options for configuring the image cropper.
         * @return The newly created Jcrop instance.
         */
        attach(element: HTMLImageElement | JQuery<HTMLImageElement>, optss?: Partial<Configuration>): Jcrop;
        /**
         * Registers the given filter as a default filter for new Jcrop instances.
         * @param name Unique name of the filter.
         * @param filterConstructor Constructor for creating new instances of the filter.
         */
        registerFilter(name: string, filterConstructor: FilterStatic): void;
        /**
         * Converts the given rectangular area (position + size) from its array representation to its object
         * representation.
         * @param rectangularArea An rectangular area to convert
         * @return The given rectangular area, converted to its object representation. 
         */
        wrapFromXywh(rectangularArea: OffsetAreaArray): OffsetArea;
    }
    /**
     * Represents a set of spacings in each directions.
     */
    export interface Spacings {
        /**
         * Spacing to the north.
         */
        n: number;
        /**
         * Spacing to the south.
         */
        s: number;
        /**
         * Spacing to the east.
         */
        e: number;
        /**
         * Spacing to the west.
         */
        w: number;
    }
    /**
     * Represents the configuration of an image cropper (Jcrop) instance.
     */
    export interface Configuration {
        /**
         * Allow new selections to be created with stage drag. False value disables new selections, overrides multi
         * option
         */
        allowSelect: boolean;
        /**
         * Animation duration (in milliseconds)
         */
        animDuration: number;
        /**
         * Animation easing routine
         */
        animEasing: string;
        /**
         * Enable animation when selection is updated via API
         */
        animation: boolean;
        /**
         * List of filter to apply to the image cropper. Defaults to
         * `[ 'constrain', 'extent', 'backoff', 'ratio', 'shader', 'round' ]`
         */
        applyFilters: (DefaultFilterTypes|string)[];
        /**
         * Sets aspect ratio (divide width / height)
         */
        aspectRatio: number;
        /**
         * Color value (CSS) for background shading
         */
        bgColor: string;
        /**
         * Opacity value for background shading
         */
        bgOpacity: number;
        /**
         * Array of borders to create:, defaults to `['n','s','e','w']`
         */
        borders: CardinalDirection[];
        /**
         * Maximum box height
         */
        boxHeight: number | null;
        /**
         * Maximum box width
         */
        boxWidth: number | null;
        /**
         * Selection can be deleted (via keyboard)
         */
        canDelete: boolean;
        /**
         * Selection can be dragged
         */
        canDrag: boolean;
        /**
         * Selection can be resized
         */
        canResize: boolean;
        /**
         * Selection can be selected
         */
        canSelect: boolean;
        /**
         * Style class for borders.
         */
        css_borders: string;
        /**
         * Style class for buttons
         */
        css_button: string;
        /**
         * Style class for the container
         */
        css_container: string;
        /**
         * Style class during drag
         */
        css_drag: string;
        /**
         * Style class for dragbars
         */
        css_dragbars: string;
        /**
         * Style class for the drag handles
         */
        css_handles: string;
        /**
         * Style class when not draggable
         */
        css_nodrag: string;
        /**
         * Style class for resizable
         */
        css_noresize: string;
        /**
         * Style class for the crop selection
         */
        css_selection: string;
        /**
         * Style class for shades
         */
        css_shades: string;
        /**
         * Target for the drag event where the listener is registered.
         */
        dragEventTarget: HTMLElement | Window | Document;
        /**
         * Array of invisible dragbars to create, defaults to `['n','s','e','w']`
         */
        dragbars: CardinalDirection[];
        /**
         * Margin from edge of container
         */
        edge: Spacings;
        /**
         * Fade duration (in milliseconds)
         */
        fadeDuration: number;
        /**
         * Fade easing routine
         */
        fadeEasing: string;
        /**
         * Enable fading when bgColor or bgOpacity are updated
         */
        fading: boolean;
        /**
         * Array of ord values to set handles for, defaults to ` ['n','s','e','w','ne','se','sw','nw']`
         */
        handles: ExtendedCardinalDirection[];
        /**
         * Constructor for creating new `KeyWatcher` instances.
         */
        keyboardComponent: KeyWatcherStatic;
        /**
         * If set, current selection inherits any setOptions() values
         */
        linkCurrent: boolean;
        /**
         * New selections inherit this setting once
         */
        linked: boolean;
        /**
         * Maxium selection size 
         */
        maxSize: AreaArray;
        /**
         * Minimum selection size
         */
        minSize: AreaArray;
        /**
         * Allow multiple selections
         */
        multi: boolean;
        /**
         * Controls non-current selection behavior when multi is false. If true any existing non-current selections are
         * removed
         */
        multiCleanup: boolean;
        /**
         * Maximum simultaneous selections allowed (0 is no limit) If number of selections >= multiMax value, new
         * selections prohibited
         */
        multiMax: number | false;
        /**
         * Callback invoked when a selection was moved.
         * @param area New crop area coordinates
         */
        onChange(area: OffsetAreaWithBottomLeft): void;
        /**
         * Callback invoked when a selection was selected.
         * @param area New crop area coordinates
         */
        onSelect(area: OffsetAreaWithBottomLeft): void;
        /**
         * Constructor for creating new `Selection` instances.
         */
        selectionComponent: SelectionStatic;
        /**
         * Position and dimensions of the selected crop area.
         */
        setSelect: OffsetAreaArray | null;
        /**
         * Scaling factor in x-direction.
         */
        xscale: number;
        /**
         * Scaling factor in y-direction.
         */
        yscale: number;
    }
}

interface JQuery {
    /**
     * Initializes Jcrop on this element if not yet initialized, or updates the options otherwise.
     * @param options New options forJcrop to set.
     * @param callback Callback that is invoked when Jcrop was created.
     * @return this for chaining.
     */
    Jcrop(options?: Partial<JQueryJcrop.Configuration>, callback?: () => void): this;
    /**
     * Returns the underlying Jcrop object you can use to interact with Jcrop programatically.
     * @param method Method to invoke.
     * @return The underlying Jcrop instance of this Jcrop element.
     */
    Jcrop(method: "api"): JQueryJcrop.Jcrop;
    /**
     * Invokes the given method on the underlying JCrop instance, and returns that instance.
     * @param method Method to invoke.
     * @param args Arguments for the JCrop method.
     * @return The JCrop instance, or `false` if the given method does not exist.
     */
    Jcrop<
        K extends keyof PrimeFaces.PickMatching<JQueryJcrop.Jcrop,(...args: any[]) => any>
    >(method: K, ...args: Parameters<JQueryJcrop.Jcrop[K]>): JQueryJcrop.Jcrop | false;
    
}

interface JQueryStatic {
    Jcrop: JQueryJcrop.JcropStatic;
}