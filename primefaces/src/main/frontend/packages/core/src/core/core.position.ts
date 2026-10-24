/*
 * Positioning of an element relative to another element, a mouse event or the window.
 *
 * Port of the jQuery UI position utility (https://api.jqueryui.com/position/),
 * Copyright OpenJS Foundation and other contributors, released under the MIT license.
 */

declare global {
    namespace PrimeType {
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
            top: number;
            left: number;
        }

        /**
         * Details about the computed position passed to {@link PositionOptions.using}.
         * @since 17.0.0
         */
        export interface PositionFeedback {
            target: { element: JQuery; left: number; top: number; width: number; height: number };
            element: { element: JQuery; left: number; top: number; width: number; height: number };
            horizontal: "left" | "right" | "center";
            vertical: "top" | "bottom" | "middle";
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
             * When given, called instead of setting the position. Receives the computed position (as offsets relative
             * to the document) and feedback about the position relative to the target.
             */
            using?: (this: HTMLElement, position: PositionCoordinates, feedback: PositionFeedback) => void;
        }
    }
}

interface Dimensions {
    width: number;
    height: number;
    offset: PrimeType.PositionCoordinates;
}

interface WithinInfo {
    element: JQuery<any>;
    isWindow: boolean;
    isDocument: boolean;
    offset: PrimeType.PositionCoordinates;
    scrollLeft: number;
    scrollTop: number;
    width: number;
    height: number;
}

interface CollisionData {
    targetWidth: number;
    targetHeight: number;
    elemWidth: number;
    elemHeight: number;
    collisionPosition: { marginLeft: number; marginTop: number };
    collisionWidth: number;
    collisionHeight: number;
    offset: [number, number];
    my: [string, string];
    at: [string, string];
    within: WithinInfo;
}

type CollisionHandler = (position: PrimeType.PositionCoordinates, data: CollisionData) => void;

const R_HORIZONTAL = /left|center|right/;
const R_VERTICAL = /top|center|bottom/;
const R_OFFSET = /[+-]\d+(\.[\d]+)?%?/;
const R_POSITION = /^\w+/;
const R_PERCENT = /%$/;

let cachedScrollbarWidth: number | undefined;

function getOffsets(offsets: [string | number, string | number], width: number, height: number): [number, number] {
    return [
        parseFloat(String(offsets[0])) * (R_PERCENT.test(String(offsets[0])) ? width / 100 : 1),
        parseFloat(String(offsets[1])) * (R_PERCENT.test(String(offsets[1])) ? height / 100 : 1)
    ];
}

function parseCss(element: HTMLElement, property: string): number {
    return parseInt($.css(element, property), 10) || 0;
}

function isWindow(obj: unknown): obj is Window {
    return obj != null && obj === (obj as Window).window;
}

function isEvent(obj: unknown): obj is { pageX: number; pageY: number } {
    return obj != null && typeof (obj as Event).preventDefault === "function";
}

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

function scrollbarWidth(): number {
    if (cachedScrollbarWidth !== undefined) {
        return cachedScrollbarWidth;
    }
    const div = $("<div style='display:block;position:absolute;width:200px;height:200px;overflow:hidden;'>"
        + "<div style='height:300px;width:auto;'></div></div>");
    const innerDiv = div.children()[0] as HTMLElement;

    $("body").append(div);
    const w1 = innerDiv.offsetWidth;
    div.css("overflow", "scroll");

    let w2 = innerDiv.offsetWidth;
    if (w1 === w2) {
        w2 = div[0]!.clientWidth;
    }

    div.remove();

    return (cachedScrollbarWidth = w1 - w2);
}

function getScrollInfo(within: WithinInfo): { width: number; height: number } {
    const overflowX = within.isWindow || within.isDocument ? "" : within.element.css("overflow-x");
    const overflowY = within.isWindow || within.isDocument ? "" : within.element.css("overflow-y");
    const hasOverflowX = overflowX === "scroll" || (overflowX === "auto" && within.width < within.element[0].scrollWidth);
    const hasOverflowY = overflowY === "scroll" || (overflowY === "auto" && within.height < within.element[0].scrollHeight);
    return {
        width: hasOverflowY ? scrollbarWidth() : 0,
        height: hasOverflowX ? scrollbarWidth() : 0
    };
}

function getWithinInfo(element: PrimeType.PositionOptions["within"]): WithinInfo {
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

const fit: Record<"left" | "top", CollisionHandler> = {
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

const flip: Record<"left" | "top", CollisionHandler> = {
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

const collisionHandlers: Record<string, Record<"left" | "top", CollisionHandler>> = {
    fit,
    flip,
    flipfit: {
        left(position, data) {
            flip.left(position, data);
            fit.left(position, data);
        },
        top(position, data) {
            flip.top(position, data);
            fit.top(position, data);
        }
    }
};

/**
 * Parses a `my` / `at` option into the horizontal and vertical position and their offsets.
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
 * Positions the given elements relative to the target given by {@link PrimeType.PositionOptions.of}.
 * @param element The elements to position.
 * @param options The options for positioning.
 * @returns The given elements, for chaining.
 */
export function position<T extends JQuery<any>>(element: T, options: PrimeType.PositionOptions | null | undefined): T {
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
    // force left top to allow flipping
    const my = parseAlignment(options.my);
    const at = parseAlignment(isEvent(target[0]) ? "left top" : options.at);
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
            offsetOptions.using = function(this: HTMLElement, props: PrimeType.PositionCoordinates) {
                const left = targetOffset.left - pos.left;
                const right = left + targetWidth - elemWidth;
                const top = targetOffset.top - pos.top;
                const bottom = top + targetHeight - elemHeight;
                const feedback: PrimeType.PositionFeedback = {
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
