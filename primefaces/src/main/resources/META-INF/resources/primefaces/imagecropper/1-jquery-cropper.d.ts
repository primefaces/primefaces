/**
 * Namespace for Cropper jQuery plugin It is used by the {@link PrimeFaces.widget.ImageCropper} widget.
 * 
 * This namespace contains several required interfaces and types.
 */
declare namespace JQueryCropper {
    /**
     * Possible actions for the `cropstart` event.
     * 
     * - `crop`: create a new crop box
     * - `move`: move the canvas (image wrapper)
     * - `zoom`: zoom in / out the canvas (image wrapper) by touch.
     * - `e`: resize the east side of the crop box
     * - `w`: resize the west side of the crop box
     * - `s`: resize the south side of the crop box
     * - `n`: resize the north side of the crop box
     * - `se`: resize the southeast side of the crop box
     * - `sw`: resize the southwest side of the crop box
     * - `ne`: resize the northeast side of the crop box
     * - `nw`: resize the northwest side of the crop box
     * - `all`: move the crop box (all directions)
     * 
     */
    export type CropAction = "crop" | "move" | "zoom" | "e" | "w" | "s" | "n" | "se" | "sw" | "ne" | "nw" | "all";

    /**
     * Details about the `cropstart`, `cropmove`, and `cropend` event.
     */
    export interface CropPhaseDetail {
        /**
         * The original event that occurred and triggered the crop to start, continue, or end.
         */
        originalEvent: MouseEvent | TouchEvent | PointerEvent,

        /**
         * The type of crop action that started.
         */
        action: CropAction,
    }

    /**
     * Details about the `zoom` event.
     */
    export interface ZoomDetail {
        /**
         * The original event that occurred and triggered the zoom.
         */
        originalEvent: WheelEvent | TouchEvent,

        /**
         * The old (current) ratio of the canvas
         */
        oldRatio: number;

        /**
         * The new (next) ratio of the canvas (`canvasData.width` / `canvasData.naturalWidth`)
         */
        ratio: number;
    }

    /**
     * Event that is fired when the crop box starts to change, keeps changing, or stopped changing.
     */
    export interface CropPhaseEvent<
        K extends "cropstart" | "cropmove" | "cropend",
        // eslint-disable-next-line @typescript-eslint/no-explicit-any
        TDelegateTarget = any,
        // eslint-disable-next-line @typescript-eslint/no-explicit-any
        TData = any,
        // eslint-disable-next-line @typescript-eslint/no-explicit-any
        TCurrentTarget = any,
        // eslint-disable-next-line @typescript-eslint/no-explicit-any
        TTarget = any
        > extends JQuery.TriggeredEvent<TDelegateTarget, TData, TCurrentTarget, TTarget> {
        type: K;
        originalEvent: CustomEvent<CropPhaseDetail>;
    }

    /**
     * This event fires when the canvas (image wrapper) or the crop box changed.
     */
    export interface CropEvent<
        // eslint-disable-next-line @typescript-eslint/no-explicit-any
        TDelegateTarget = any,
        // eslint-disable-next-line @typescript-eslint/no-explicit-any
        TData = any,
        // eslint-disable-next-line @typescript-eslint/no-explicit-any
        TCurrentTarget = any,
        // eslint-disable-next-line @typescript-eslint/no-explicit-any
        TTarget = any
        > extends JQuery.TriggeredEvent<TDelegateTarget, TData, TCurrentTarget, TTarget> {
        type: "crop";
        originalEvent: CustomEvent<Cropper.Data>;
    }

    /**
     * This event fires when a cropper instance starts to zoom in or zoom out its canvas (image wrapper).
     */
    export interface ZoomEvent<
        // eslint-disable-next-line @typescript-eslint/no-explicit-any
        TDelegateTarget = any,
        // eslint-disable-next-line @typescript-eslint/no-explicit-any
        TData = any,
        // eslint-disable-next-line @typescript-eslint/no-explicit-any
        TCurrentTarget = any,
        // eslint-disable-next-line @typescript-eslint/no-explicit-any
        TTarget = any
        > extends JQuery.TriggeredEvent<TDelegateTarget, TData, TCurrentTarget, TTarget> {
        type: "zoom";
        originalEvent: CustomEvent<ZoomDetail>;
    }
}

interface JQuery {
    /**
     * Creates a new image {@link Cropper} on the current elements.
     * @param options Optional settings for the image cropper.
     * @return this jQuery instance for chaining.
     */
    cropper(options?: Cropper.Options): this;

    /**
     * Invokes a method on the {@link Cropper} instance and returns the result.
     * @typeparam K Name of a method that exists on the {@link Cropper} instance.
     * @param method Name of a method that exists on the {@link Cropper} instance.
     * @param args Arguments required by the given cropper method.
     * @return The value as it returned by the cropper instance method.
     */
    cropper<
        K extends PrimeFaces.MatchingKeys<Cropper, (...args: never[]) => void>
    >(
        method: K,
        ...args: Parameters<Cropper[K]>
    ): PrimeFaces.ToJQueryUIWidgetReturnType<Cropper, ReturnType<Cropper[K]>, this>;
}

declare namespace JQuery {
    interface TypeToTriggeredEventMap<
        TDelegateTarget,
        TData,
        TCurrentTarget,
        TTarget
        > {
        /**
         * Triggered by the {@link JQuery.cropper|JQuery Cropper plugin}.
         * 
         * This event fires when the canvas (image wrapper) or the crop box starts to change.
         */
        cropstart: JQueryCropper.CropPhaseEvent<"cropstart", TDelegateTarget, TData, TCurrentTarget, TTarget>;

        /**
         * Triggered by the {@link JQuery.cropper|JQuery Cropper plugin}.
         * 
         * This event fires when the canvas (image wrapper) or the crop box is changing.
         */
        cropmove: JQueryCropper.CropPhaseEvent<"cropmove", TDelegateTarget, TData, TCurrentTarget, TTarget>;

        /**
         * Triggered by the {@link JQuery.cropper|JQuery Cropper plugin}.
         * 
         * This event fires when the canvas (image wrapper) or the crop box stops to change.
         */
        cropend: JQueryCropper.CropPhaseEvent<"cropend", TDelegateTarget, TData, TCurrentTarget, TTarget>;

        /**
         * Triggered by the {@link JQuery.cropper|JQuery Cropper plugin}.
         * 
         * This event fires when the canvas (image wrapper) or the crop box changed.
         */
        crop: JQueryCropper.CropEvent<TDelegateTarget, TData, TCurrentTarget, TTarget>;

        /**
         * Triggered by the {@link JQuery.cropper|JQuery Cropper plugin}.
         * 
         * This event fires when a cropper instance starts to zoom in or zoom out its canvas (image wrapper).
         */
        zoom: JQueryCropper.ZoomEvent<TDelegateTarget, TData, TCurrentTarget, TTarget>;
    }
}