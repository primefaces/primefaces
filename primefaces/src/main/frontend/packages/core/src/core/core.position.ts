/*
 * Positioning of an element relative to another element, a mouse event or the window.
 *
 * Port of the jQuery UI position utility (https://api.jqueryui.com/position/),
 * Copyright OpenJS Foundation and other contributors, released under the MIT license.
 */

declare global {
    namespace PrimeFacesTypes.utils {
        /**
         * Target of {@link PositionOptions.of}: a CSS selector, a DOM element, the window, the document, a jQuery
         * instance or a mouse event (positions at the mouse cursor).
         * @since 17.0.0
         */
        export type PositionTarget = string | Element | Window | Document | JQuery | Event | JQuery.Event;

        /**
         * Collision handling of {@link PositionOptions.collision}. One value applies to both axes, two
         * space-separated values (e.g. `flip fit`) apply to the horizontal and vertical axis.
         *
         * - `flip`: flips the element to the opposite side of the target if it does not fit.
         * - `fit`: shifts the element away from the edge of the container until it fits.
         * - `flipfit`: first `flip`, then `fit`.
         * - `none`: no collision detection.
         * @since 17.0.0
         */
        export type PositionCollision = "flip" | "fit" | "flipfit" | "none" | (string & {});

        /**
         * The position of the element passed to {@link PositionOptions.using}.
         * @since 17.0.0
         */
        export interface PositionCoordinates {
            /** The CSS `top` in pixels to apply, relative to the offset parent of the element. */
            top: number;
            /** The CSS `left` in pixels to apply, relative to the offset parent of the element. */
            left: number;
        }

        /**
         * Details about the computed position passed to {@link PositionOptions.using}.
         * @since 17.0.0
         */
        export interface PositionFeedback {
            /** The target with its offset relative to the document and its outer size. */
            target: { element: JQuery; left: number; top: number; width: number; height: number };
            /** The positioned element with its computed offset relative to the document and its outer size. */
            element: { element: JQuery; left: number; top: number; width: number; height: number };
            /** Horizontal position of the element relative to the target, e.g. `left` when it is left of the target. */
            horizontal: "left" | "right" | "center";
            /** Vertical position of the element relative to the target, e.g. `top` when it is above the target. */
            vertical: "top" | "bottom" | "middle";
            /** The axis along which the element is farther away from the target, i.e. the more significant one. */
            important: "horizontal" | "vertical";
        }

        /**
         * Options for {@link PrimeFaces.utils.position}, compatible with the options of the former jQuery UI
         * `$.fn.position(options)`.
         * @since 17.0.0
         */
        export interface PositionOptions {
            /**
             * Position on the element to align with the target, as `horizontal vertical`, e.g. `left top` or
             * `left+10 top-5%`. Horizontal values are `left`, `center` and `right`, vertical values are `top`,
             * `center` and `bottom`. Defaults to `center`.
             */
            my?: string;
            /**
             * Position on the target to align the element against, same format as {@link my}. Defaults to `center`.
             */
            at?: string;
            /**
             * The target to position against. Without a target, the element is not positioned.
             */
            of?: PositionTarget | null;
            /**
             * Collision handling when the element overflows the {@link within} container. Defaults to `flip`.
             */
            collision?: PositionCollision;
            /**
             * Container used for the collision detection. Defaults to the window.
             */
            within?: string | Element | Window | JQuery | null;
            /**
             * When given, called instead of setting the position. Receives the CSS `top` / `left` to apply (relative to
             * the offset parent, as `$.fn.offset` computes them) and feedback about the position relative to the target.
             */
            using?: (this: HTMLElement, position: PositionCoordinates, feedback: PositionFeedback) => void;
        }
    }
}

/**
 * Size and offset of a positioning target.
 */
interface Dimensions {
    /** The outer width in pixels, `0` for a mouse event. */
    width: number;
    /** The outer height in pixels, `0` for a mouse event. */
    height: number;
    /** The offset relative to the document; the scroll position for the window, the mouse position for an event. */
    offset: PrimeFacesTypes.utils.PositionCoordinates;
}

/**
 * Information about the container used for the collision detection, see {@link PrimeFacesTypes.utils.PositionOptions.within}.
 */
interface WithinInfo {
    /** The container, the window by default. */
    element: JQuery<any>;
    /** Whether the container is the window. */
    isWindow: boolean;
    /** Whether the container is the document. */
    isDocument: boolean;
    /** The offset of the container relative to the document, `0` / `0` for the window and the document. */
    offset: PrimeFacesTypes.utils.PositionCoordinates;
    /** The horizontal scroll position of the container. */
    scrollLeft: number;
    /** The vertical scroll position of the container. */
    scrollTop: number;
    /** The outer width of the container. */
    width: number;
    /** The outer height of the container. */
    height: number;
}

/**
 * Data passed to the {@link CollisionHandler}s for adjusting the position of one element.
 */
interface CollisionData {
    /** The outer width of the target. */
    targetWidth: number;
    /** The outer height of the target. */
    targetHeight: number;
    /** The outer width of the element. */
    elemWidth: number;
    /** The outer height of the element. */
    elemHeight: number;
    /** The left and top margin of the element, which are part of its collision box. */
    collisionPosition: { marginLeft: number; marginTop: number };
    /** The width of the collision box: outer width, horizontal margins and the scrollbar of the container. */
    collisionWidth: number;
    /** The height of the collision box: outer height, vertical margins and the scrollbar of the container. */
    collisionHeight: number;
    /** The sum of the `my` and `at` offsets in pixels, as `[horizontal, vertical]`; inverted when flipping. */
    offset: [number, number];
    /** The parsed `my` position as `[horizontal, vertical]`, e.g. `["left", "top"]`. */
    my: [string, string];
    /** The parsed `at` position as `[horizontal, vertical]`, e.g. `["left", "bottom"]`. */
    at: [string, string];
    /** The container used for the collision detection. */
    within: WithinInfo;
}

/**
 * Adjusts the computed position of an element along one axis when it overflows its container, see
 * {@link PrimeFacesTypes.utils.PositionCollision}.
 * @param position The position of the element relative to the document, modified in place.
 * @param data The dimensions of the element, the target and the container.
 */
type CollisionHandler = (position: PrimeFacesTypes.utils.PositionCoordinates, data: CollisionData) => void;

/** Matches the horizontal keywords of a `my` / `at` position. */
const R_HORIZONTAL = /left|center|right/;
/** Matches the vertical keywords of a `my` / `at` position. */
const R_VERTICAL = /top|center|bottom/;
/** Matches the offset of a `my` / `at` position, e.g. `+10` or `-5%`. */
const R_OFFSET = /[+-]\d+(\.[\d]+)?%?/;
/** Matches the keyword of a `my` / `at` position without its offset. */
const R_POSITION = /^\w+/;
/** Matches a percentage offset. */
const R_PERCENT = /%$/;

/**
 * Converts the offsets of a `my` / `at` position into pixels, where percentages relate to the given size.
 * @param offsets The horizontal and vertical offset, e.g. `["+10", "-50%"]`.
 * @param width The width that horizontal percentages relate to.
 * @param height The height that vertical percentages relate to.
 * @returns The horizontal and vertical offset in pixels.
 */
function getOffsets(offsets: [string | number, string | number], width: number, height: number): [number, number] {
    return [
        parseFloat(String(offsets[0])) * (R_PERCENT.test(String(offsets[0])) ? width / 100 : 1),
        parseFloat(String(offsets[1])) * (R_PERCENT.test(String(offsets[1])) ? height / 100 : 1)
    ];
}

/**
 * Reads a CSS property of an element as integer, e.g. a margin.
 * @param element The element to read.
 * @param property The name of the CSS property.
 * @returns The value of the property in pixels, `0` if it is not a number.
 */
function parseCss(element: HTMLElement, property: string): number {
    return parseInt($.css(element, property), 10) || 0;
}

/**
 * Checks whether the given object is a window.
 * @param obj The object to check.
 * @returns `true` if the object is a window.
 */
function isWindow(obj: unknown): obj is Window {
    return obj != null && obj === (obj as Window).window;
}

/**
 * Checks whether the given object is an event, i.e. the target is the position of the mouse.
 * @param obj The object to check.
 * @returns `true` if the object is a native or jQuery event.
 */
function isEvent(obj: unknown): obj is { pageX: number; pageY: number } {
    return obj != null && typeof (obj as Event).preventDefault === "function";
}

/**
 * Determines the size and offset of a positioning target: the size of the viewport for the window and the document,
 * a point at the mouse position for an event, and the outer size and offset for an element.
 * @param elem The target, an element, the window, the document or an event wrapped in jQuery.
 * @returns The size and offset of the target.
 */
function getDimensions(elem: JQuery<any>): Dimensions {
    const raw = elem[0];
    if (raw.nodeType === 9) {
        return { width: elem.width() ?? 0, height: elem.height() ?? 0, offset: { top: 0, left: 0 } };
    }
    if (isWindow(raw)) {
        return {
            width: elem.width() ?? 0,
            height: elem.height() ?? 0,
            offset: { top: elem.scrollTop() ?? 0, left: elem.scrollLeft() ?? 0 }
        };
    }
    if (isEvent(raw)) {
        return { width: 0, height: 0, offset: { top: raw.pageY, left: raw.pageX } };
    }
    return {
        width: elem.outerWidth() ?? 0,
        height: elem.outerHeight() ?? 0,
        offset: elem.offset() ?? { top: 0, left: 0 }
    };
}

/**
 * Determines the space taken by the scrollbars of the container, which is added to the collision box of the element.
 * The window and the document have no scrollbars of their own in this sense.
 * @param within The container used for the collision detection.
 * @returns The width of the vertical scrollbar and the height of the horizontal scrollbar, `0` if there is none.
 */
function getScrollInfo(within: WithinInfo): { width: number; height: number } {
    const overflowX = within.isWindow || within.isDocument ? "" : within.element.css("overflow-x");
    const overflowY = within.isWindow || within.isDocument ? "" : within.element.css("overflow-y");
    const hasOverflowX = overflowX === "scroll" || (overflowX === "auto" && within.width < within.element[0].scrollWidth);
    const hasOverflowY = overflowY === "scroll" || (overflowY === "auto" && within.height < within.element[0].scrollHeight);
    return {
        width: hasOverflowY ? PrimeFaces.calculateScrollbarWidth() : 0,
        height: hasOverflowX ? PrimeFaces.calculateScrollbarWidth() : 0
    };
}

/**
 * Collects the information about the container used for the collision detection.
 * @param element The `within` option, defaults to the window.
 * @returns The offset, scroll position and size of the container.
 */
function getWithinInfo(element: PrimeFacesTypes.utils.PositionOptions["within"]): WithinInfo {
    const withinElement: JQuery<any> = $((element || window) as any);
    const isElemWindow = isWindow(withinElement[0]);
    const isDocument = !!withinElement[0] && withinElement[0].nodeType === 9;
    const hasOffset = !isElemWindow && !isDocument;
    return {
        element: withinElement,
        isWindow: isElemWindow,
        isDocument: isDocument,
        offset: hasOffset ? (withinElement.offset() ?? { left: 0, top: 0 }) : { left: 0, top: 0 },
        scrollLeft: withinElement.scrollLeft() ?? 0,
        scrollTop: withinElement.scrollTop() ?? 0,
        width: withinElement.outerWidth() ?? 0,
        height: withinElement.outerHeight() ?? 0
    };
}

/**
 * The `fit` collision handling: shifts the element away from the edges of the container until it fits. An element
 * larger than the container is aligned with the edge it overflowed initially.
 */
const fit: Record<"left" | "top", CollisionHandler> = {
    /**
     * Shifts the element horizontally so it does not overflow the left or right edge of the container.
     * @param position The position of the element, its `left` is adjusted in place.
     * @param data The dimensions of the element, the target and the container.
     */
    left(position, data) {
        const within = data.within;
        const withinOffset = within.isWindow ? within.scrollLeft : within.offset.left;
        const outerWidth = within.width;
        const collisionPosLeft = position.left - data.collisionPosition.marginLeft;
        const overLeft = withinOffset - collisionPosLeft;
        const overRight = collisionPosLeft + data.collisionWidth - outerWidth - withinOffset;

        // element is wider than within
        if (data.collisionWidth > outerWidth) {
            // element is initially over the left side of within
            if (overLeft > 0 && overRight <= 0) {
                const newOverRight = position.left + overLeft + data.collisionWidth - outerWidth - withinOffset;
                position.left += overLeft - newOverRight;
            }
            // element is initially over right side of within
            else if (overRight > 0 && overLeft <= 0) {
                position.left = withinOffset;
            }
            // element is initially over both left and right sides of within
            else if (overLeft > overRight) {
                position.left = withinOffset + outerWidth - data.collisionWidth;
            }
            else {
                position.left = withinOffset;
            }
        }
        // too far left -> align with left edge
        else if (overLeft > 0) {
            position.left += overLeft;
        }
        // too far right -> align with right edge
        else if (overRight > 0) {
            position.left -= overRight;
        }
        // adjust based on position and margin
        else {
            position.left = Math.max(position.left - collisionPosLeft, position.left);
        }
    },
    /**
     * Shifts the element vertically so it does not overflow the top or bottom edge of the container.
     * @param position The position of the element, its `top` is adjusted in place.
     * @param data The dimensions of the element, the target and the container.
     */
    top(position, data) {
        const within = data.within;
        const withinOffset = within.isWindow ? within.scrollTop : within.offset.top;
        const outerHeight = within.height;
        const collisionPosTop = position.top - data.collisionPosition.marginTop;
        const overTop = withinOffset - collisionPosTop;
        const overBottom = collisionPosTop + data.collisionHeight - outerHeight - withinOffset;

        // element is taller than within
        if (data.collisionHeight > outerHeight) {
            // element is initially over the top of within
            if (overTop > 0 && overBottom <= 0) {
                const newOverBottom = position.top + overTop + data.collisionHeight - outerHeight - withinOffset;
                position.top += overTop - newOverBottom;
            }
            // element is initially over bottom of within
            else if (overBottom > 0 && overTop <= 0) {
                position.top = withinOffset;
            }
            // element is initially over both top and bottom of within
            else if (overTop > overBottom) {
                position.top = withinOffset + outerHeight - data.collisionHeight;
            }
            else {
                position.top = withinOffset;
            }
        }
        // too far up -> align with top
        else if (overTop > 0) {
            position.top += overTop;
        }
        // too far down -> align with bottom edge
        else if (overBottom > 0) {
            position.top -= overBottom;
        }
        // adjust based on position and margin
        else {
            position.top = Math.max(position.top - collisionPosTop, position.top);
        }
    }
};

/**
 * The `flip` collision handling: flips the element to the opposite side of the target (mirroring `my`, `at` and
 * their offsets) when it overflows the container, but only if it overflows less on the opposite side.
 */
const flip: Record<"left" | "top", CollisionHandler> = {
    /**
     * Flips the element to the other horizontal side of the target when it overflows the left or right edge of the
     * container, e.g. from the right of the target to its left.
     * @param position The position of the element, its `left` is adjusted in place.
     * @param data The dimensions of the element, the target and the container.
     */
    left(position, data) {
        const within = data.within;
        const withinOffset = within.offset.left + within.scrollLeft;
        const outerWidth = within.width;
        const offsetLeft = within.isWindow ? within.scrollLeft : within.offset.left;
        const collisionPosLeft = position.left - data.collisionPosition.marginLeft;
        const overLeft = collisionPosLeft - offsetLeft;
        const overRight = collisionPosLeft + data.collisionWidth - outerWidth - offsetLeft;
        const myOffset = data.my[0] === "left" ? -data.elemWidth : data.my[0] === "right" ? data.elemWidth : 0;
        const atOffset = data.at[0] === "left" ? data.targetWidth : data.at[0] === "right" ? -data.targetWidth : 0;
        const offset = -2 * data.offset[0];

        if (overLeft < 0) {
            const newOverRight = position.left + myOffset + atOffset + offset + data.collisionWidth - outerWidth - withinOffset;
            if (newOverRight < 0 || newOverRight < Math.abs(overLeft)) {
                position.left += myOffset + atOffset + offset;
            }
        }
        else if (overRight > 0) {
            const newOverLeft = position.left - data.collisionPosition.marginLeft + myOffset + atOffset + offset - offsetLeft;
            if (newOverLeft > 0 || Math.abs(newOverLeft) < overRight) {
                position.left += myOffset + atOffset + offset;
            }
        }
    },
    /**
     * Flips the element to the other vertical side of the target when it overflows the top or bottom edge of the
     * container, e.g. from below the target to above it.
     * @param position The position of the element, its `top` is adjusted in place.
     * @param data The dimensions of the element, the target and the container.
     */
    top(position, data) {
        const within = data.within;
        const withinOffset = within.offset.top + within.scrollTop;
        const outerHeight = within.height;
        const offsetTop = within.isWindow ? within.scrollTop : within.offset.top;
        const collisionPosTop = position.top - data.collisionPosition.marginTop;
        const overTop = collisionPosTop - offsetTop;
        const overBottom = collisionPosTop + data.collisionHeight - outerHeight - offsetTop;
        const myOffset = data.my[1] === "top" ? -data.elemHeight : data.my[1] === "bottom" ? data.elemHeight : 0;
        const atOffset = data.at[1] === "top" ? data.targetHeight : data.at[1] === "bottom" ? -data.targetHeight : 0;
        const offset = -2 * data.offset[1];

        if (overTop < 0) {
            const newOverBottom = position.top + myOffset + atOffset + offset + data.collisionHeight - outerHeight - withinOffset;
            if (newOverBottom < 0 || newOverBottom < Math.abs(overTop)) {
                position.top += myOffset + atOffset + offset;
            }
        }
        else if (overBottom > 0) {
            const newOverTop = position.top - data.collisionPosition.marginTop + myOffset + atOffset + offset - offsetTop;
            if (newOverTop > 0 || Math.abs(newOverTop) < overBottom) {
                position.top += myOffset + atOffset + offset;
            }
        }
    }
};

/**
 * The collision handlers by the name used in {@link PrimeFacesTypes.utils.PositionOptions.collision}, with one handler for the
 * horizontal (`left`) and one for the vertical (`top`) axis. `none` has no handler, so nothing is adjusted.
 */
const collisionHandlers: Record<string, Record<"left" | "top", CollisionHandler>> = {
    fit,
    flip,
    flipfit: {
        /**
         * Flips the element horizontally if needed, then shifts it until it fits.
         * @param position The position of the element, its `left` is adjusted in place.
         * @param data The dimensions of the element, the target and the container.
         */
        left(position, data) {
            flip.left(position, data);
            fit.left(position, data);
        },
        /**
         * Flips the element vertically if needed, then shifts it until it fits.
         * @param position The position of the element, its `top` is adjusted in place.
         * @param data The dimensions of the element, the target and the container.
         */
        top(position, data) {
            flip.top(position, data);
            fit.top(position, data);
        }
    }
};

/**
 * Parses a `my` / `at` option into the horizontal and vertical position and their offsets. A single keyword is
 * completed with `center` for the other axis, invalid keywords become `center`.
 * @param value The option, e.g. `left+10 top-5%`, `bottom` or `center`.
 * @returns The keywords as `[horizontal, vertical]` and their offsets (`0` or e.g. `+10`, `-5%`).
 */
function parseAlignment(value: string | undefined): { position: [string, string]; offsets: [string | number, string | number] } {
    let pos = (value || "").split(" ");
    if (pos.length === 1) {
        pos = R_HORIZONTAL.test(pos[0]!) ? pos.concat(["center"]) :
            R_VERTICAL.test(pos[0]!) ? ["center"].concat(pos) :
            ["center", "center"];
    }
    const horizontal = R_HORIZONTAL.test(pos[0]!) ? pos[0]! : "center";
    const vertical = R_VERTICAL.test(pos[1]!) ? pos[1]! : "center";

    const horizontalOffset = R_OFFSET.exec(horizontal);
    const verticalOffset = R_OFFSET.exec(vertical);

    return {
        position: [R_POSITION.exec(horizontal)![0], R_POSITION.exec(vertical)![0]],
        offsets: [horizontalOffset ? horizontalOffset[0] : 0, verticalOffset ? verticalOffset[0] : 0]
    };
}

/**
 * Positions the given elements relative to the target given by {@link PrimeFacesTypes.utils.PositionOptions.of}, a replacement
 * for the former jQuery UI `$.fn.position(options)`.
 *
 * The `my` point of each element is aligned with the `at` point of the target, then the `collision` handling moves
 * the element back into the `within` container if it overflows. Finally, the offset of the element is set via
 * `$.fn.offset`, or the `using` callback is called with the computed offset and feedback about the position. For a
 * mouse event as target, the element is positioned at the mouse cursor (`at` is always `left top`).
 *
 * ```js
 * PrimeFaces.utils.position(panel, { my: 'left top', at: 'left bottom', of: input, collision: 'flip' });
 * ```
 * @param element The elements to position.
 * @param options The options for positioning. Without options, a target or elements, nothing is positioned.
 * @returns The given elements, for chaining.
 */
export function position<T extends JQuery<any>>(element: T, options: PrimeFacesTypes.utils.PositionOptions | null | undefined): T {
    if (!options || !options.of || !element || !element.length) {
        return element;
    }

    const target: JQuery<any> = typeof options.of === "string" ? $(document).find(options.of) : $(options.of as any);
    if (!target.length) {
        return element;
    }

    const within = getWithinInfo(options.within);
    const scrollInfo = getScrollInfo(within);
    const collision = (options.collision || "flip").split(" ");
    if (collision.length === 1) {
        collision[1] = collision[0]!;
    }

    const dimensions = getDimensions(target);
    const my = parseAlignment(options.my);
    // force left top for an event to allow flipping
    const at =parseAlignment(isEvent(target[0]) ? "left top" : options.at);
    const targetWidth = dimensions.width;
    const targetHeight = dimensions.height;
    const targetOffset = dimensions.offset;

    // clone to reuse original targetOffset later
    const basePosition = { ...targetOffset };

    if (at.position[0] === "right") {
        basePosition.left += targetWidth;
    }
    else if (at.position[0] === "center") {
        basePosition.left += targetWidth / 2;
    }

    if (at.position[1] === "bottom") {
        basePosition.top += targetHeight;
    }
    else if (at.position[1] === "center") {
        basePosition.top += targetHeight / 2;
    }

    const atOffset = getOffsets(at.offsets, targetWidth, targetHeight);
    basePosition.left += atOffset[0];
    basePosition.top += atOffset[1];

    return element.each(function(this: HTMLElement) {
        const elem = $(this);
        const elemWidth = elem.outerWidth() ?? 0;
        const elemHeight = elem.outerHeight() ?? 0;
        const marginLeft = parseCss(this, "marginLeft");
        const marginTop = parseCss(this, "marginTop");
        const collisionWidth = elemWidth + marginLeft + parseCss(this, "marginRight") + scrollInfo.width;
        const collisionHeight = elemHeight + marginTop + parseCss(this, "marginBottom") + scrollInfo.height;
        const pos = { ...basePosition };
        const myOffset = getOffsets(my.offsets, elemWidth, elemHeight);

        if (my.position[0] === "right") {
            pos.left -= elemWidth;
        }
        else if (my.position[0] === "center") {
            pos.left -= elemWidth / 2;
        }

        if (my.position[1] === "bottom") {
            pos.top -= elemHeight;
        }
        else if (my.position[1] === "center") {
            pos.top -= elemHeight / 2;
        }

        pos.left += myOffset[0];
        pos.top += myOffset[1];

        const data: CollisionData = {
            targetWidth,
            targetHeight,
            elemWidth,
            elemHeight,
            collisionPosition: { marginLeft, marginTop },
            collisionWidth,
            collisionHeight,
            offset: [atOffset[0] + myOffset[0], atOffset[1] + myOffset[1]],
            my: my.position,
            at: at.position,
            within
        };

        collisionHandlers[collision[0]!]?.left(pos, data);
        collisionHandlers[collision[1]!]?.top(pos, data);

        // jQuery calls "using" whenever the key is present, so only add it when given
        const offsetOptions: Record<string, unknown> = { ...pos };
        const userUsing = options.using;
        if (userUsing) {
            // adds feedback as second argument to using callback
            /**
             * Called by `$.fn.offset` instead of setting the offset: computes the feedback about the position of the
             * element relative to the target and passes it to the `using` callback of the options.
             * @param props The CSS `top` / `left` to apply, computed by `$.fn.offset` from the document offset.
             */
            offsetOptions.using = function(this: HTMLElement, props: PrimeFacesTypes.utils.PositionCoordinates) {
                const left = targetOffset.left - pos.left;
                const right = left + targetWidth - elemWidth;
                const top = targetOffset.top - pos.top;
                const bottom = top + targetHeight - elemHeight;
                const feedback: PrimeFacesTypes.utils.PositionFeedback = {
                    target: { element: target, left: targetOffset.left, top: targetOffset.top, width: targetWidth, height: targetHeight },
                    element: { element: elem, left: pos.left, top: pos.top, width: elemWidth, height: elemHeight },
                    horizontal: right < 0 ? "left" : left > 0 ? "right" : "center",
                    vertical: bottom < 0 ? "top" : top > 0 ? "bottom" : "middle",
                    important: "vertical"
                };
                if (targetWidth < elemWidth && Math.abs(left + right) < targetWidth) {
                    feedback.horizontal = "center";
                }
                if (targetHeight < elemHeight && Math.abs(top + bottom) < targetHeight) {
                    feedback.vertical = "middle";
                }
                if (Math.max(Math.abs(left), Math.abs(right)) > Math.max(Math.abs(top), Math.abs(bottom))) {
                    feedback.important = "horizontal";
                }
                userUsing.call(this, props, feedback);
            };
        }

        elem.offset(offsetOptions as JQuery.CoordinatesPartial);
    });
}
