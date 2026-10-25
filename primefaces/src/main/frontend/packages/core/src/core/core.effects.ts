import { core } from "./core.js";

/*
 * Animation effects (blind, bounce, clip, drop, explode, fade, fold, highlight, puff, pulsate, scale, shake, size,
 * slide, transfer) and the easing functions formerly provided by jQuery UI, implemented with the Web Animations API.
 *
 * The effects, their options and the easing functions are modelled after jQuery UI (https://api.jqueryui.com/category/effects/),
 * Copyright OpenJS Foundation and other contributors, released under the MIT license.
 */

declare global {
    namespace PrimeFacesTypes.effects {
        /**
         * Mode of an effect:
         *
         * - `show`: shows the hidden element with the effect.
         * - `hide`: hides the visible element with the effect.
         * - `toggle`: shows the element when it is hidden, hides it otherwise.
         * - `effect`: runs the effect on the element without showing or hiding it.
         * @since 17.0.0
         */
        export type Mode = "show" | "hide" | "toggle" | "effect";

        /**
         * Callback invoked when an effect has finished.
         * @since 17.0.0
         */
        export type Callback = (this: HTMLElement) => void;

        /**
         * Options for the effects of {@link PrimeFaces.effects}. Besides the general options, each effect supports
         * its own options, e.g. `direction` for `blind`.
         * @since 17.0.0
         */
        export interface Options {
            /**
             * The mode of the effect, defaults to the default mode of the effect.
             */
            mode?: Mode;
            /**
             * Duration in milliseconds, or one of `fast` (200), `normal` (400) and `slow` (600). Defaults to 400.
             */
            duration?: number | string | null;
            /**
             * Easing, either the name of a jQuery easing function (e.g. `easeInOutCirc`) or a CSS easing function
             * (e.g. `ease-in`, `cubic-bezier(...)`). Defaults to `swing`.
             */
            easing?: string | null;
            /**
             * Called when the effect has finished.
             */
            complete?: Callback | null;
            /**
             * By default, the effect is queued after the running effects and animations of the element. With
             * `false`, the running and queued effects and animations are finished first and the effect starts
             * immediately.
             */
            queue?: boolean;
            /**
             * Direction of the effect (`blind`, `bounce`, `clip`, `drop`, `scale`, `shake`, `slide`).
             */
            direction?: "up" | "down" | "left" | "right" | "vertical" | "horizontal" | "both";
            /**
             * Distance in pixels (`bounce`, `drop`, `shake`, `slide`).
             */
            distance?: number;
            /**
             * Number of repetitions (`bounce`, `pulsate`, `shake`).
             */
            times?: number;
            /**
             * Number of pieces, should be a perfect square (`explode`).
             */
            pieces?: number;
            /**
             * Percentage to scale to (`puff`, `scale`).
             */
            percent?: number | string;
            /**
             * Whether to fade the element while scaling (`scale`).
             */
            fade?: boolean;
            /**
             * Vanishing point as `[vertical, horizontal]`, e.g. `["middle", "center"]` (`scale`, `puff`).
             */
            origin?: [string | number, string | number];
            /**
             * Highlight background color (`highlight`).
             */
            color?: string;
            /**
             * Size of the folded element in pixels or percent (`fold`).
             */
            size?: number | string;
            /**
             * Whether to fold horizontally first (`fold`).
             */
            horizFirst?: boolean;
            /**
             * Start size (`size`).
             */
            from?: { width?: number; height?: number };
            /**
             * Target size (`size`) or the target element to transfer to (`transfer`).
             */
            to?: { width?: number; height?: number } | string | Element | JQuery;
            /**
             * Additional style class of the transfer element (`transfer`).
             */
            className?: string;
            /**
             * Further options of custom effects.
             */
            [option: string]: unknown;
        }

        /**
         * Context passed to an {@link Implementation}.
         * @since 17.0.0
         */
        export interface Context {
            /**
             * The element to animate.
             */
            element: HTMLElement;
            /**
             * The element to animate, as a jQuery instance.
             */
            jq: JQuery<HTMLElement>;
            /**
             * The resolved mode. For `show` and `hide`, the element is already visible when the effect starts and gets
             * hidden after the effect finished.
             */
            mode: "show" | "hide" | "effect";
            /**
             * The options of the effect.
             */
            options: Options;
            /**
             * The duration in milliseconds.
             */
            duration: number;
            /**
             * The CSS easing function.
             */
            easing: string;
            /**
             * Animates the element (or another element) with the given keyframes. The animation is cancelled when
             * the effect has finished, so the effect styles are removed again.
             * @param keyframes The keyframes to animate.
             * @param target The element to animate, defaults to the element of the effect.
             * @returns A promise that resolves when the animation finished.
             */
            animate(keyframes: Keyframe[], target?: Element): Promise<void>;
            /**
             * Animates the element through the given keyframes, with the given duration for each step between two
             * keyframes. Each step uses the easing of the effect.
             * @param keyframes The keyframes to animate.
             * @param durations The durations of the steps, one less than the keyframes.
             * @returns A promise that resolves when the animation finished.
             */
            animateSteps(keyframes: Keyframe[], durations: number[]): Promise<void>;
            /**
             * Registers a function that is called when the effect has finished or was stopped.
             * @param cleanup The function to call.
             */
            onCleanup(cleanup: () => void): void;
        }

        /**
         * Implementation of an effect registered with {@link PrimeFaces.effects.register}. It receives the
         * {@link Context} and animates the element with {@link Context.animate} or
         * {@link Context.animateSteps}. The effect is finished when the returned promise resolves, or
         * immediately when nothing is returned.
         * @since 17.0.0
         */
        export type Implementation = (context: Context) => Promise<void> | void;
    }
}

/**
 * A registered effect: its default mode and its implementation.
 */
interface EffectDefinition {
    /** The mode used when the options of the effect do not specify one, e.g. `hide` for `blind`. */
    mode: PrimeFacesTypes.effects.Mode;
    /** Animates the element, see {@link PrimeFacesTypes.effects.Implementation}. */
    run: PrimeFacesTypes.effects.Implementation;
}

/**
 * State of the effect currently running on an element, used to finish it early via {@link Effects.stop}.
 */
interface RunningEffect {
    /** The Web Animations started by the effect, cancelled when the effect finishes to remove the effect styles. */
    animations: Animation[];
    /** Functions registered via {@link PrimeFacesTypes.effects.Context.onCleanup}, called when the effect finishes. */
    cleanups: (() => void)[];
    /**
     * Finishes the effect: hides the element in `hide` mode, runs the cleanups, cancels the animations, calls the
     * `complete` callback and dequeues the next effect. Calling it again does nothing.
     * @param stopped `true` when called by {@link Effects.stop}, which cleared the queue already, or when a new effect
     * starts on the element while this one is still registered: the next effect is not dequeued, as that would remove
     * the marker of an effect the `complete` callback just started.
     */
    finish: (stopped?: boolean) => void;
}

/** A pair of numbers, e.g. the horizontal and vertical scale factor. */
type Point = [number, number];

/** Durations in milliseconds of the named speeds, as in jQuery (`$.fx.speeds`). */
const SPEEDS: Record<string, number> = { fast: 200, normal: 400, slow: 600 };

/** Matches the CSS easing functions, which are passed to the Web Animations API unchanged. */
const CSS_EASING = /^(linear|ease|ease-in|ease-out|ease-in-out|step-start|step-end)$|^(cubic-bezier|steps|linear)\(/;

/** CSS `linear()` easing functions sampled from the jQuery easing functions, by name of the jQuery easing. */
const easingCache: Record<string, string> = {};

/** The effect currently running on each element. */
const runningEffects: WeakMap<HTMLElement, RunningEffect> = new WeakMap();

/**
 * Resolves the `duration` option of an effect into milliseconds. Returns `0` when animations are disabled via
 * `$.fx.off` or {@link PrimeFaces.animationEnabled}, so effects finish immediately.
 * @param duration Duration in milliseconds, a named speed (`fast`, `normal`, `slow`) or a numeric string.
 * @returns The duration in milliseconds, never negative; `400` for a missing or invalid duration.
 */
function resolveDuration(duration: PrimeFacesTypes.effects.Options["duration"]): number {
    if ($.fx.off || !core.animationEnabled) {
        return 0;
    }
    if (typeof duration === "number") {
        return Math.max(0, duration);
    }
    if (typeof duration === "string") {
        if (duration in SPEEDS) {
            return SPEEDS[duration]!;
        }
        const parsed = parseFloat(duration);
        if (!isNaN(parsed)) {
            return Math.max(0, parsed);
        }
    }
    return SPEEDS.normal!;
}

/**
 * Converts the name of a jQuery easing function into a CSS easing function. CSS easing functions are returned
 * unchanged, jQuery easing functions from `$.easing` are sampled into a cached CSS `linear()` function and unknown
 * names fall back to `ease`.
 * @param easing The name of a jQuery easing function or a CSS easing function, defaults to `swing`.
 * @returns The CSS easing function for the Web Animations API.
 */
function resolveEasing(easing: string | null | undefined): string {
    const name = easing || "swing";
    if (CSS_EASING.test(name)) {
        return name;
    }

    let cssEasing = easingCache[name];
    if (cssEasing === undefined) {
        const fn = ($.easing as Record<string, ((p: number) => number) | undefined>)[name];
        if (typeof fn === "function") {
            // sample the easing function, linear() supports overshooting values (back, elastic)
            const points: string[] = [];
            const samples = 40;
            for (let i = 0; i <= samples; i++) {
                points.push(String(Math.round(fn(i / samples) * 10000) / 10000));
            }
            cssEasing = "linear(" + points.join(", ") + ")";
        }
        else {
            cssEasing = "ease";
        }
        easingCache[name] = cssEasing;
    }
    return cssEasing;
}

/**
 * Formats a number as CSS pixel length.
 * @param value The length in pixels.
 * @returns The CSS length, e.g. `10px`.
 */
function px(value: number): string {
    return value + "px";
}

/**
 * Builds the value of the CSS `translate` property that moves an element along one axis.
 * @param axis The axis to move along.
 * @param value The distance in pixels, negative to move up or left.
 * @returns The CSS `translate` value, e.g. `10px 0px`.
 */
function translate(axis: "x" | "y", value: number): string {
    return axis === "x" ? px(value) + " 0px" : "0px " + px(value);
}

/**
 * Determines the computed opacity of an element, so effects fading the element restore its own opacity.
 * @param element The element to check.
 * @returns The computed opacity, `1` if it cannot be determined.
 */
function opacityOf(element: HTMLElement): number {
    const opacity = parseFloat(getComputedStyle(element).opacity);
    return isNaN(opacity) ? 1 : opacity;
}

/**
 * Checks whether a direction moves along the vertical axis.
 * @param direction The `direction` option of an effect.
 * @returns `true` for `up` and `down`, `false` otherwise.
 */
function isVertical(direction: string): boolean {
    return direction === "up" || direction === "down";
}

/**
 * Checks whether a direction moves towards the negative coordinates.
 * @param direction The `direction` option of an effect.
 * @returns `true` for `up` and `left`, `false` otherwise.
 */
function isNegative(direction: string): boolean {
    return direction === "up" || direction === "left";
}

/**
 * Determines the outer size of an element along one axis.
 * @param jq The element to measure.
 * @param axis `x` for the outer width, `y` for the outer height.
 * @param includeMargin Whether to include the margin.
 * @returns The outer size in pixels, `0` if it cannot be determined.
 */
function outerSize(jq: JQuery<HTMLElement>, axis: "x" | "y", includeMargin?: boolean): number {
    return (axis === "x" ? jq.outerWidth(!!includeMargin) : jq.outerHeight(!!includeMargin)) ?? 0;
}

/**
 * Builds the `inset()` of a `clip-path`, that hides the element up to the given side.
 * @param side The side from which the element is clipped away, e.g. `bottom` leaves the top edge last.
 * @returns The `clip-path` value that hides the whole element.
 */
function hiddenInset(side: "top" | "right" | "bottom" | "left"): string {
    switch (side) {
        case "top":
            return "inset(100% 0 0 0)";
        case "right":
            return "inset(0 100% 0 0)";
        case "bottom":
            return "inset(0 0 100% 0)";
        default:
            return "inset(0 0 0 100%)";
    }
}

/**
 * A negative bottom (`y`) or right (`x`) margin that removes the given part of the element from the layout, so the
 * following content moves while the element is clipped. The size is pinned, as the negative margin would stretch an
 * element with an automatic size.
 * @param ctx The context of the running effect.
 * @param axis `y` to collapse the height via the bottom margin, `x` to collapse the width via the right margin.
 * @param part The size in pixels to remove from the layout, `0` for the full size.
 * @returns The keyframe properties with the margin and the pinned size.
 */
function collapse(ctx: PrimeFacesTypes.effects.Context, axis: "x" | "y", part: number): Keyframe {
    const property = axis === "y" ? "marginBottom" : "marginRight";
    const size = axis === "y" ? "height" : "width";
    return { [property]: px((parseFloat(ctx.jq.css(property)) || 0) - part), [size]: getComputedStyle(ctx.element)[size] };
}

/**
 * Computes the scale factors of the `scale` and `puff` effects for the given direction.
 * @param direction The `direction` option: `vertical` or `horizontal` scales one axis only, anything else both.
 * @param percent The percentage to scale to.
 * @returns The horizontal and vertical scale factor.
 */
function scaleFactor(direction: string | undefined, percent: number): Point {
    const factor = percent / 100;
    return [direction === "vertical" ? 1 : factor, direction === "horizontal" ? 1 : factor];
}

/**
 * Parses the value of the CSS `scale` property (e.g. `1.5`, `50% 2`), so an inline scale of the element is kept
 * while scaling.
 * @param value The value of the CSS `scale` property.
 * @returns The horizontal and vertical scale factor, `[1, 1]` for an empty value or `none`.
 */
function parseScale(value: string): Point {
    if (!value || value === "none") {
        return [1, 1];
    }
    const parts = value.split(/\s+/).map((part) => part.endsWith("%") ? parseFloat(part) / 100 : parseFloat(part));
    const x = isNaN(parts[0]!) ? 1 : parts[0]!;
    return [x, parts.length > 1 && !isNaN(parts[1]!) ? parts[1]! : x];
}

/**
 * Converts the `origin` option of the `scale` and `puff` effects into a CSS `transform-origin`.
 * @param origin The vanishing point as `[vertical, horizontal]`, with the keywords `top` / `middle` / `bottom` and
 * `left` / `center` / `right` or pixel values. Defaults to `["middle", "center"]`.
 * @returns The CSS `transform-origin` value, e.g. `50% 0%`.
 */
function transformOrigin(origin: PrimeFacesTypes.effects.Options["origin"]): string {
    const [vertical, horizontal] = origin || ["middle", "center"];
    const y = vertical === "top" ? "0%" : vertical === "bottom" ? "100%" : vertical === "middle" ? "50%" : px(Number(vertical));
    const x = horizontal === "left" ? "0%" : horizontal === "right" ? "100%" : horizontal === "center" ? "50%" : px(Number(horizontal));
    return x + " " + y;
}

/**
 * Shared implementation of the `scale` and `puff` effects. Scales the element between its current scale and the
 * given percentage around the `origin`, optionally fading it. In `effect` mode, the element keeps the new scale.
 * @param ctx The context of the running effect.
 * @param percent The percentage to scale to.
 * @param fade Whether to fade the element while scaling.
 * @returns A promise that resolves when the animation finished.
 */
function scaleEffect(ctx: PrimeFacesTypes.effects.Context, percent: number, fade: boolean): Promise<void> {
    const base = parseScale(ctx.element.style.scale);
    const factor = scaleFactor(ctx.options.direction, percent);
    const origin = transformOrigin(ctx.options.origin);
    const opacity = opacityOf(ctx.element);

    const visible: Keyframe = { scale: base[0] + " " + base[1], transformOrigin: origin };
    const scaled: Keyframe = { scale: (base[0] * factor[0]) + " " + (base[1] * factor[1]), transformOrigin: origin };
    if (fade) {
        visible.opacity = opacity;
        scaled.opacity = 0;
    }

    if (ctx.mode === "effect") {
        // the element keeps the new scale
        const x = base[0] * factor[0];
        const y = base[1] * factor[1];
        ctx.onCleanup(() => {
            const identity = x === 1 && y === 1;
            ctx.element.style.scale = identity ? "" : x + " " + y;
            ctx.element.style.transformOrigin = identity ? "" : origin;
        });
    }

    return ctx.animate(ctx.mode === "show" ? [scaled, visible] : [visible, scaled]);
}

/**
 * The effects by name: the built-in effects formerly provided by jQuery UI plus the effects added via
 * {@link Effects.register}.
 */
const effectDefinitions: Record<string, EffectDefinition> = {
    blind: {
        mode: "hide",
        /**
         * Blind: rolls the element up towards one edge with a `clip-path` (`direction` `up` / `vertical` (default),
         * `down`, `left` / `horizontal` or `right`), while the following content moves into the freed space.
         * @param ctx The context of the running effect.
         * @returns A promise that resolves when the animation finished.
         */
        run(ctx) {
            const sides: Record<string, "top" | "right" | "bottom" | "left"> = {
                up: "bottom", vertical: "bottom", down: "top", left: "right", horizontal: "right", right: "left"
            };
            const side = sides[ctx.options.direction || "up"] ?? "bottom";
            const axis = side === "top" || side === "bottom" ? "y" : "x";
            const hidden = { clipPath: hiddenInset(side), ...collapse(ctx, axis, outerSize(ctx.jq, axis)) };
            const visible = { clipPath: "inset(0)", ...collapse(ctx, axis, 0) };
            return ctx.animate(ctx.mode === "show" ? [hidden, visible] : [visible, hidden]);
        }
    },

    bounce: {
        mode: "effect",
        /**
         * Bounce: bounces the element `times` times (default 5) by `distance` (default a third of its size) in the
         * given `direction` (default `up`), halving the distance with each bounce. In `show` mode, the element first
         * drops in while fading in; in `hide` mode, the bounces grow and the element fades out during the last one.
         * @param ctx The context of the running effect.
         * @returns A promise that resolves when the animation finished.
         */
        run(ctx) {
            const direction = ctx.options.direction || "up";
            const axis = isVertical(direction) ? "y" : "x";
            const sign = isNegative(direction) ? -1 : 1;
            const show = ctx.mode === "show";
            const hide = ctx.mode === "hide";
            const times = ctx.options.times || 5;
            const speed = ctx.duration / (times * 2 + (show || hide ? 1 : 0));
            const opacity = opacityOf(ctx.element);
            let distance = ctx.options.distance || outerSize(ctx.jq, axis) / 3;

            const keyframes: Keyframe[] = [];
            if (show) {
                keyframes.push({ translate: translate(axis, sign * distance * 2), opacity: 0 });
            }
            keyframes.push({ translate: translate(axis, 0), opacity });
            if (hide) {
                distance = distance / Math.pow(2, times - 1);
            }
            for (let i = 0; i < times; i++) {
                keyframes.push({ translate: translate(axis, sign * distance) }, { translate: translate(axis, 0) });
                distance = hide ? distance * 2 : distance / 2;
            }
            if (hide) {
                // fade out during the last bounce only
                keyframes[keyframes.length - 1]!.opacity = opacity;
                keyframes.push({ translate: translate(axis, sign * distance), opacity: 0 });
            }

            return ctx.animateSteps(keyframes, keyframes.slice(1).map(() => speed));
        }
    },

    clip: {
        mode: "hide",
        /**
         * Clip: clips the element towards its center with a `clip-path`, `vertical`ly (default), `horizontal`ly or
         * in `both` directions.
         * @param ctx The context of the running effect.
         * @returns A promise that resolves when the animation finished.
         */
        run(ctx) {
            const direction = ctx.options.direction || "vertical";
            const hidden = {
                clipPath: direction === "both" ? "inset(50%)" : direction === "horizontal" ? "inset(0 50%)" : "inset(50% 0)"
            };
            const visible = { clipPath: "inset(0)" };
            return ctx.animate(ctx.mode === "show" ? [hidden, visible] : [visible, hidden]);
        }
    },

    drop: {
        mode: "hide",
        /**
         * Drop: moves the element by `distance` (default half its outer size including the margin) in the given
         * `direction` (default `left`) while fading it out, or the reverse in `show` mode.
         * @param ctx The context of the running effect.
         * @returns A promise that resolves when the animation finished.
         */
        run(ctx) {
            const direction = ctx.options.direction || "left";
            const axis = isVertical(direction) ? "y" : "x";
            const distance = ctx.options.distance || outerSize(ctx.jq, axis, true) / 2;
            const hidden = { translate: translate(axis, (isNegative(direction) ? -1 : 1) * distance), opacity: 0 };
            const visible = { translate: translate(axis, 0), opacity: opacityOf(ctx.element) };
            return ctx.animate(ctx.mode === "show" ? [hidden, visible] : [visible, hidden]);
        }
    },

    explode: {
        mode: "hide",
        /**
         * Explode: splits the element into `pieces` (default 9, rounded to a square grid) that fly apart from the
         * center while fading out, or fly together in `show` mode. The pieces are clones of the element (without
         * IDs) appended to the body and removed when the effect finishes; the element itself is invisible meanwhile.
         * @param ctx The context of the running effect.
         * @returns A promise that resolves when the animations of all pieces finished.
         */
        run(ctx) {
            const rows = ctx.options.pieces ? Math.round(Math.sqrt(ctx.options.pieces)) : 3;
            const show = ctx.mode === "show";
            const jq = ctx.jq;
            const offset = jq.offset() ?? { top: 0, left: 0 };
            const outerWidth = jq.outerWidth() ?? 0;
            const outerHeight = jq.outerHeight() ?? 0;
            const width = Math.ceil(outerWidth / rows);
            const height = Math.ceil(outerHeight / rows);
            const visibility = ctx.element.style.visibility;

            ctx.element.style.visibility = "hidden";
            ctx.onCleanup(() => {
                ctx.element.style.visibility = visibility;
            });

            const zIndex = core.nextZindex();
            const animations: Promise<void>[] = [];
            for (let i = 0; i < rows; i++) {
                const my = i - (rows - 1) / 2;
                for (let j = 0; j < rows; j++) {
                    const mx = j - (rows - 1) / 2;
                    const clone = jq.clone().removeAttr("id");
                    clone.find("[id]").removeAttr("id");
                    clone.css({
                        position: "absolute",
                        visibility: "visible",
                        boxSizing: "border-box",
                        margin: 0,
                        width: outerWidth,
                        height: outerHeight,
                        left: -j * width,
                        top: -i * height
                    });

                    const piece = $("<div class=\"p-effect-explode\"></div>")
                        .css({
                            position: "absolute",
                            overflow: "hidden",
                            zIndex: zIndex,
                            width: width,
                            height: height,
                            left: offset.left + j * width,
                            top: offset.top + i * height
                        })
                        .append(clone)
                        .appendTo(document.body);
                    ctx.onCleanup(() => piece.remove());

                    const scattered = { translate: px(mx * width) + " " + px(my * height), opacity: 0 };
                    const assembled = { translate: "0px 0px", opacity: 1 };
                    animations.push(ctx.animate(show ? [scattered, assembled] : [assembled, scattered], piece[0]!));
                }
            }

            return Promise.all(animations).then(() => undefined);
        }
    },

    fade: {
        mode: "toggle",
        /**
         * Fade: fades the element between its own opacity and transparent.
         * @param ctx The context of the running effect.
         * @returns A promise that resolves when the animation finished.
         */
        run(ctx) {
            const hidden = { opacity: 0 };
            const visible = { opacity: opacityOf(ctx.element) };
            return ctx.animate(ctx.mode === "show" ? [hidden, visible] : [visible, hidden]);
        }
    },

    fold: {
        mode: "hide",
        /**
         * Fold: folds the element like a sheet of paper, first vertically down to `size` (default 15 pixels, or a
         * percentage of the folded dimension), then horizontally; with `horizFirst` the other way round. Each fold
         * takes half of the duration and the height leaves the layout while the element is folded.
         * @param ctx The context of the running effect.
         * @returns A promise that resolves when the animation finished.
         */
        run(ctx) {
            const show = ctx.mode === "show";
            const hide = ctx.mode === "hide";
            const width = ctx.jq.outerWidth() ?? 0;
            const height = ctx.jq.outerHeight() ?? 0;
            const horizFirst = !!ctx.options.horizFirst;
            // [first folded dimension, second folded dimension]
            const distance = horizFirst ? [width, height] : [height, width];
            let size = ctx.options.size || 15;
            const percent = /([0-9]+)%/.exec(String(size));
            if (percent) {
                size = parseInt(percent[1]!, 10) / 100 * distance[hide ? 0 : 1]!;
            }
            const folded = Number(size);

            // the height leaves the layout while it is folded
            const full = { clipPath: "inset(0px 0px 0px 0px)", ...collapse(ctx, "y", 0) };
            const step1 = horizFirst
                ? { clipPath: "inset(0px " + px(width - folded) + " 0px 0px)", ...collapse(ctx, "y", 0) }
                : { clipPath: "inset(0px 0px " + px(height - folded) + " 0px)", ...collapse(ctx, "y", height - folded) };
            const step2 = horizFirst
                ? { clipPath: "inset(0px " + px(width - folded) + " " + px(height) + " 0px)", ...collapse(ctx, "y", height) }
                : { clipPath: "inset(0px " + px(width) + " " + px(height - folded) + " 0px)", ...collapse(ctx, "y", height - folded) };

            const half = ctx.duration / 2;
            return ctx.animateSteps(show ? [step2, step1, full] : [full, step1, step2], [half, half]);
        }
    },

    highlight: {
        mode: "show",
        /**
         * Highlight: animates the background from the highlight `color` (default `#ffff99`) back to the background
         * color of the element; in `hide` mode, the element also fades out.
         * @param ctx The context of the running effect.
         * @returns A promise that resolves when the animation finished.
         */
        run(ctx) {
            const from: Keyframe = { backgroundImage: "none", backgroundColor: ctx.options.color || "#ffff99" };
            const to: Keyframe = { backgroundImage: "none", backgroundColor: getComputedStyle(ctx.element).backgroundColor };
            if (ctx.mode === "hide") {
                from.opacity = opacityOf(ctx.element);
                to.opacity = 0;
            }
            return ctx.animate([from, to]);
        }
    },

    puff: {
        mode: "hide",
        /**
         * Puff: scales the element up to `percent` (default 150) while fading it out, or the reverse in `show` mode.
         * @param ctx The context of the running effect.
         * @returns A promise that resolves when the animation finished.
         */
        run(ctx) {
            return scaleEffect(ctx, parseInt(String(ctx.options.percent), 10) || 150, true);
        }
    },

    pulsate: {
        mode: "show",
        /**
         * Pulsate: blinks the element `times` times (default 5) by animating its opacity. In `show` mode (or when the
         * element is hidden) it starts transparent and ends visible; in `hide` mode it ends transparent.
         * @param ctx The context of the running effect.
         * @returns A promise that resolves when the animation finished.
         */
        run(ctx) {
            const show = ctx.mode === "show";
            const anims = (ctx.options.times || 5) * 2 + (show || ctx.mode === "hide" ? 1 : 0);
            const opacity = opacityOf(ctx.element);
            let start = opacity;
            let animateTo = 0;

            if (show || !ctx.jq.is(":visible")) {
                ctx.jq.show();
                start = 0;
                animateTo = 1;
            }

            const keyframes: Keyframe[] = [{ opacity: start }];
            for (let i = 1; i < anims; i++) {
                keyframes.push({ opacity: animateTo * opacity });
                animateTo = 1 - animateTo;
            }
            keyframes.push({ opacity: animateTo * opacity });

            const speed = ctx.duration / anims;
            return ctx.animateSteps(keyframes, keyframes.slice(1).map(() => speed));
        }
    },

    scale: {
        mode: "effect",
        /**
         * Scale: scales the element to `percent` (default 0 in `show` / `hide` mode, 100 in `effect` mode) in the given
         * `direction` (`both`, `vertical` or `horizontal`) around the `origin`, fading it when `fade` is set.
         * @param ctx The context of the running effect.
         * @returns A promise that resolves when the animation finished.
         */
        run(ctx) {
            const parsed = parseInt(String(ctx.options.percent), 10);
            const percent = isNaN(parsed) ? (ctx.mode !== "effect" ? 0 : 100) : parsed;
            return scaleEffect(ctx, percent, !!ctx.options.fade);
        }
    },

    shake: {
        mode: "effect",
        /**
         * Shake: shakes the element back and forth `times` times (default 3) by `distance` (default 20 pixels) along
         * the axis of the given `direction` (default `left`), ending at its original position.
         * @param ctx The context of the running effect.
         * @returns A promise that resolves when the animation finished.
         */
        run(ctx) {
            const direction = ctx.options.direction || "left";
            const axis = isVertical(direction) ? "y" : "x";
            const distance = (isNegative(direction) ? -1 : 1) * (ctx.options.distance || 20);
            const times = ctx.options.times || 3;
            const speed = Math.round(ctx.duration / (times * 2 + 1));

            const keyframes: Keyframe[] = [{ translate: translate(axis, 0) }, { translate: translate(axis, distance) }];
            const durations = [speed];
            for (let i = 1; i < times; i++) {
                keyframes.push({ translate: translate(axis, -distance) }, { translate: translate(axis, distance) });
                durations.push(speed, speed);
            }
            keyframes.push({ translate: translate(axis, -distance) }, { translate: translate(axis, 0) });
            durations.push(speed, speed / 2);

            return ctx.animateSteps(keyframes, durations);
        }
    },

    size: {
        mode: "effect",
        /**
         * Size: resizes the element with hidden overflow from `from` (default its current size) to `to` (default
         * `0x0`), or the reverse in `show` mode. In `effect` mode, the element keeps the target size.
         * @param ctx The context of the running effect.
         * @returns A promise that resolves when the animation finished.
         */
        run(ctx) {
            const jq = ctx.jq;
            const originalWidth = jq.width() ?? 0;
            const originalHeight = jq.height() ?? 0;
            const borderBox = jq.css("box-sizing") === "border-box";
            const extraWidth = borderBox ? (jq.outerWidth() ?? 0) - originalWidth : 0;
            const extraHeight = borderBox ? (jq.outerHeight() ?? 0) - originalHeight : 0;
            const toSize: { width?: number; height?: number } = $.isPlainObject(ctx.options.to)
                ? ctx.options.to as { width?: number; height?: number }
                : { width: 0, height: 0 };

            let from = { width: ctx.options.from?.width ?? originalWidth, height: ctx.options.from?.height ?? originalHeight };
            let to = { width: toSize.width ?? originalWidth, height: toSize.height ?? originalHeight };
            if (ctx.mode === "show") {
                [from, to] = [to, from];
            }

            /**
             * Creates the keyframe for the given content size, adding padding and border for `border-box` sizing.
             * @param size The content width and height in pixels.
             * @returns The keyframe with the size and hidden overflow.
             */
            const keyframe = (size: { width: number; height: number }): Keyframe => ({
                overflow: "hidden",
                width: px(size.width + extraWidth),
                height: px(size.height + extraHeight)
            });

            if (ctx.mode === "effect") {
                // the element keeps the new size
                ctx.onCleanup(() => {
                    jq.width(to.width).height(to.height);
                });
            }

            return ctx.animate([keyframe(from), keyframe(to)]);
        }
    },

    slide: {
        mode: "show",
        /**
         * Slide: slides the element out of its box by `distance` (default its outer size including the margin) in the
         * given `direction` (default `left`), clipping the part outside the box; in `show` mode it slides in.
         * @param ctx The context of the running effect.
         * @returns A promise that resolves when the animation finished.
         */
        run(ctx) {
            const direction = ctx.options.direction || "left";
            const axis = isVertical(direction) ? "y" : "x";
            const distance = ctx.options.distance || outerSize(ctx.jq, axis, true);
            const sides: Record<string, "top" | "right" | "bottom" | "left"> = { up: "top", down: "bottom", left: "left", right: "right" };
            const hidden = {
                translate: translate(axis, (isNegative(direction) ? -1 : 1) * distance),
                clipPath: hiddenInset(sides[direction] ?? "left")
            };
            const visible = { translate: translate(axis, 0), clipPath: "inset(0)" };
            return ctx.animate(ctx.mode === "show" ? [hidden, visible] : [visible, hidden]);
        }
    },

    transfer: {
        mode: "effect",
        /**
         * Transfer: animates an outline element (style class `p-effect-transfer` plus `className`) from the bounds of
         * the element to the bounds of the target element given by `to`. The outline element is appended to the body
         * and removed when the effect finishes.
         * @param ctx The context of the running effect.
         * @returns A promise that resolves when the animation finished, nothing when the target does not exist.
         */
        run(ctx) {
            const target = $(ctx.options.to as any) as JQuery<HTMLElement>;
            if (!target.length) {
                return;
            }

            const targetFixed = target.css("position") === "fixed";
            const $window = $(window);
            const fixTop = targetFixed ? ($window.scrollTop() ?? 0) : 0;
            const fixLeft = targetFixed ? ($window.scrollLeft() ?? 0) : 0;
            const endPosition = target.offset() ?? { top: 0, left: 0 };
            const startPosition = ctx.jq.offset() ?? { top: 0, left: 0 };

            const transfer = $("<div class=\"p-effect-transfer\"></div>")
                .addClass(ctx.options.className || "")
                .css("position", targetFixed ? "fixed" : "absolute")
                .appendTo(document.body);
            core.nextZindex(transfer);
            ctx.onCleanup(() => transfer.remove());

            return ctx.animate([{
                top: px(startPosition.top - fixTop),
                left: px(startPosition.left - fixLeft),
                width: px(ctx.jq.innerWidth() ?? 0),
                height: px(ctx.jq.innerHeight() ?? 0)
            }, {
                top: px(endPosition.top - fixTop),
                left: px(endPosition.left - fixLeft),
                width: px(target.innerWidth() ?? 0),
                height: px(target.innerHeight() ?? 0)
            }], transfer[0]!);
        }
    }
};

/**
 * Registers the easing functions formerly provided by jQuery UI in `$.easing`: the `easeIn`, `easeOut` and
 * `easeInOut` variants (e.g. `easeInOutCirc`) of `Quad`, `Cubic`, `Quart`, `Quint`, `Expo`, `Sine`, `Circ`,
 * `Elastic`, `Back` and `Bounce`. Existing easing functions with the same name are not overridden.
 */
function registerEasings(): void {
    const baseEasings: Record<string, (p: number) => number> = {
        Quad: (p) => Math.pow(p, 2),
        Cubic: (p) => Math.pow(p, 3),
        Quart: (p) => Math.pow(p, 4),
        Quint: (p) => Math.pow(p, 5),
        Expo: (p) => Math.pow(p, 6),
        Sine: (p) => 1 - Math.cos(p * Math.PI / 2),
        Circ: (p) => 1 - Math.sqrt(1 - p * p),
        Elastic: (p) => p === 0 || p === 1 ? p : -Math.pow(2, 8 * (p - 1)) * Math.sin(((p - 1) * 80 - 7.5) * Math.PI / 15),
        Back: (p) => p * p * (3 * p - 2),
        Bounce: (p) => {
            let pow2: number;
            let bounce = 4;
            while (p < ((pow2 = Math.pow(2, --bounce)) - 1) / 11) {
                // find the matching bounce
            }
            return 1 / Math.pow(4, 3 - bounce) - 7.5625 * Math.pow((pow2 * 3 - 2) / 22 - p, 2);
        }
    };

    const easing = $.easing as Record<string, (p: number) => number>;
    for (const name of Object.keys(baseEasings)) {
        const easeIn = baseEasings[name]!;
        easing["easeIn" + name] ??= easeIn;
        easing["easeOut" + name] ??= (p) => 1 - easeIn(1 - p);
        easing["easeInOut" + name] ??= (p) => p < 0.5 ? easeIn(p * 2) / 2 : 1 - easeIn(p * -2 + 2) / 2;
    }
}

/**
 * The effects module of PrimeFaces, it animates elements with effects such as `fade`, `slide` or `pulsate`.
 *
 * ```js
 * PrimeFaces.effects.show($('#panel'), 'blind', { direction: 'up', duration: 500 });
 * PrimeFaces.effects.run($('#message'), 'pulsate', { times: 3 });
 * ```
 * @since 17.0.0
 */
export class Effects {
    /**
     * Checks whether an effect with the given name exists, either built-in (e.g. `fade`, `slide`) or registered via
     * {@link register}.
     * @param effect The name of an effect.
     * @returns `true` if the effect exists, `false` for an unknown or empty name.
     */
    exists(effect: string | null | undefined): boolean {
        return !!effect && Object.prototype.hasOwnProperty.call(effectDefinitions, effect);
    }

    /**
     * Registers a custom effect or overrides an existing one. The implementation receives a
     * {@link PrimeFacesTypes.effects.Context} and animates the element with its `animate` / `animateSteps` methods; the
     * showing, hiding, queueing and the `complete` callback are handled by this module.
     *
     * ```js
     * PrimeFaces.effects.register('spin', 'effect', (ctx) => ctx.animate([{ rotate: '0deg' }, { rotate: '360deg' }]));
     * ```
     * @param effect The name of the effect.
     * @param mode The default mode of the effect, used when the options do not specify one.
     * @param implementation The implementation of the effect.
     */
    register(effect: string, mode: PrimeFacesTypes.effects.Mode, implementation: PrimeFacesTypes.effects.Implementation): void {
        effectDefinitions[effect] = { mode, run: implementation };
    }

    /**
     * Runs an effect on the given elements, in the mode given by the options or else in the default mode of the
     * effect (e.g. `hide` for `blind`, `effect` for `shake`).
     *
     * By default, the effect is added to the jQuery `fx` queue of each element, so it starts after the running
     * effects and animations; with `queue: false` these are finished first and the effect starts immediately. An
     * explicit `show` / `hide` mode that is already fulfilled only calls `complete`, and an unknown effect shows /
     * hides the element with the jQuery animation (like jQuery UI). When animations are disabled, the effect finishes
     * synchronously.
     * @param element The elements to animate.
     * @param effect The name of the effect.
     * @param options The options of the effect.
     * @returns The given elements, for chaining.
     */
    run<T extends JQuery<any>>(element: T, effect: string, options?: PrimeFacesTypes.effects.Options | null): T {
        const opts = { ...options };
        const queue = opts.queue !== false;

        if (!queue) {
            this.stop(element);
        }

        return element.each((_, el) => {
            if (queue) {
                $(el).queue("fx", (next) => this.execute(el, effect, opts, next));
            }
            else {
                this.execute(el, effect, opts);
            }
        });
    }

    /**
     * Shows the given elements with an effect, see {@link run}. Elements that are already visible are not animated.
     * @param element The elements to show.
     * @param effect The name of the effect. Without an effect, the elements are shown immediately and `complete` is
     * called synchronously.
     * @param options The options of the effect, the `mode` is always `show`.
     * @returns The given elements, for chaining.
     */
    show<T extends JQuery<any>>(element: T, effect: string | null | undefined, options?: PrimeFacesTypes.effects.Options | null): T {
        return this.runMode(element, effect, options, "show");
    }

    /**
     * Hides the given elements with an effect, see {@link run}. Elements that are already hidden are not animated.
     * The elements are hidden (`display: none`) when the effect finished.
     * @param element The elements to hide.
     * @param effect The name of the effect. Without an effect, the elements are hidden immediately and `complete` is
     * called synchronously.
     * @param options The options of the effect, the `mode` is always `hide`.
     * @returns The given elements, for chaining.
     */
    hide<T extends JQuery<any>>(element: T, effect: string | null | undefined, options?: PrimeFacesTypes.effects.Options | null): T {
        return this.runMode(element, effect, options, "hide");
    }

    /**
     * Shows the hidden or hides the visible elements with an effect, see {@link run}. The visibility of each element
     * is checked when its effect starts, i.e. after the effects queued before.
     * @param element The elements to toggle.
     * @param effect The name of the effect. Without an effect, the elements are toggled immediately and `complete` is
     * called synchronously.
     * @param options The options of the effect, the `mode` is always `toggle`.
     * @returns The given elements, for chaining.
     */
    toggle<T extends JQuery<any>>(element: T, effect: string | null | undefined, options?: PrimeFacesTypes.effects.Options | null): T {
        return this.runMode(element, effect, options, "toggle");
    }

    /**
     * Finishes the running effects and animations of the given elements immediately and clears their queue. A running
     * effect jumps to its end state: in `hide` mode the element gets hidden, the effect styles are removed and its
     * `complete` callback is called. Queued effects are discarded without calling their callbacks.
     * @param element The elements to stop.
     * @returns The given elements, for chaining.
     */
    stop<T extends JQuery<any>>(element: T): T {
        // clear the queue and finish the jQuery animations first, so finishing the running effect does not start the
        // next one, and an effect its complete callback starts keeps running and queued
        element.stop(true, true);
        element.each((_, el) => {
            runningEffects.get(el)?.finish(true);
        });
        return element;
    }

    /**
     * Shared implementation of {@link show}, {@link hide} and {@link toggle}: runs the effect in the given mode, or
     * without an effect, shows / hides / toggles the elements immediately via jQuery and calls `complete` for each.
     * @param element The elements to show, hide or toggle.
     * @param effect The name of the effect, empty to change the visibility without effect.
     * @param options The options of the effect.
     * @param mode The mode that overrides the mode of the options.
     * @returns The given elements, for chaining.
     */
    private runMode<T extends JQuery<any>>(element: T, effect: string | null | undefined, options: PrimeFacesTypes.effects.Options | null | undefined,
            mode: "show" | "hide" | "toggle"): T {
        if (!effect) {
            const complete = options?.complete;
            element[mode]();
            if (complete) {
                element.each((_, el) => complete.call(el));
            }
            return element;
        }
        return this.run(element, effect, { ...options, mode });
    }

    /**
     * Runs an effect on a single element. Resolves the mode (`toggle` by the current visibility), shows the element
     * for `show` / `hide`, registers the {@link RunningEffect} so {@link stop} can finish it, builds the
     * {@link PrimeFacesTypes.effects.Context} and calls the implementation of the effect. When the implementation is done
     * (or fails, which is logged), the effect is finished: the element is hidden in `hide` mode, the cleanups run,
     * the animations are cancelled, `complete` is called and the next queued effect starts.
     * @param el The element to animate.
     * @param effect The name of the effect.
     * @param options The options of the effect.
     * @param next Dequeues the next effect of the `fx` queue, when the effect was queued.
     */
    private execute(el: HTMLElement, effect: string, options: PrimeFacesTypes.effects.Options, next?: () => void): void {
        // an effect still running here was not stopped with stop() (e.g. jQuery's stop() cleared the queue): it jumps
        // to its end first, so it does not hide the element or dequeue later; also an effect its complete callback
        // starts, it would be overwritten in runningEffects below
        let previous: RunningEffect | undefined;
        while ((previous = runningEffects.get(el))) {
            previous.finish(true);
        }

        const jq = $(el);
        const definition = this.exists(effect) ? effectDefinitions[effect] : undefined;
        const duration = resolveDuration(options.duration);
        /**
         * Calls the `complete` callback and dequeues the next effect.
         * @param stopped Whether the effect was stopped, the queue was cleared then and nothing is dequeued.
         */
        const done = (stopped: boolean = false) => {
            options.complete?.call(el);
            if (!stopped) {
                next?.();
            }
        };

        // resolve the mode, an explicit mode that is already fulfilled does nothing
        const hidden = jq.is(":hidden");
        const requestedMode = options.mode || definition?.mode || "effect";
        const mode: "show" | "hide" | "effect" = requestedMode === "toggle" ? (hidden ? "show" : "hide") : requestedMode;
        if (options.mode && mode !== "effect" && (hidden ? mode === "hide" : mode === "show")) {
            jq[mode]();
            done();
            return;
        }

        if (!definition) {
            // unknown effect (e.g. a jQuery animation name like fadeIn): like jQuery UI the jQuery animation of show /
            // hide, not queued as this already runs in the queue
            if (mode !== "effect") {
                jq[mode]({ duration, queue: false, complete: () => done() });
            }
            else {
                done();
            }
            return;
        }

        if (mode !== "effect") {
            jq.show();
        }

        let finished = false;
        const running: RunningEffect = {
            animations: [],
            cleanups: [],
            finish: (stopped?: boolean) => {
                if (finished) {
                    return;
                }
                finished = true;
                if (runningEffects.get(el) === running) {
                    runningEffects.delete(el);
                }
                // hide first, so removing the effect styles does not show the element again
                if (mode === "hide") {
                    jq.hide();
                }
                for (const cleanup of running.cleanups) {
                    cleanup();
                }
                for (const animation of running.animations) {
                    animation.cancel();
                }
                done(stopped);
            }
        };
        runningEffects.set(el, running);

        const easing = resolveEasing(options.easing);
        /**
         * Starts a Web Animation that is tracked by the running effect, so it is cancelled when the effect finishes.
         * Does nothing when the effect has already finished. As the `finished` promise does not resolve in hidden
         * tabs, the animation is finished by a timeout as a fallback.
         * @param keyframes The keyframes to animate.
         * @param target The element to animate.
         * @param animationDuration The duration in milliseconds.
         * @param animationEasing The CSS easing function.
         * @returns A promise that resolves when the animation finished or was cancelled.
         */
        const animate = (keyframes: Keyframe[], target: Element = el, animationDuration: number = duration,
                animationEasing: string = easing): Promise<void> => {
            if (finished) {
                return Promise.resolve();
            }
            const animation = target.animate(keyframes, { duration: animationDuration, easing: animationEasing, fill: "both" });
            running.animations.push(animation);
            // the finished promise resolves in the rendering update, which does not run in hidden tabs: finish the
            // animation there; in a visible tab it may just have started later (busy main thread), so check again,
            // but not forever (e.g. an offscreen iframe that does not render)
            const started = performance.now();
            /** Finishes the animation if it can't finish by itself, otherwise checks again later. */
            const fallback = (): void => {
                if (animation.playState === "idle" || animation.playState === "finished") {
                    return;
                }
                if (document.visibilityState === "hidden" || performance.now() - started > animationDuration * 3 + 1000) {
                    animation.finish();
                }
                else {
                    setTimeout(fallback, Math.max(0, animationDuration - (Number(animation.currentTime) || 0)) + 100);
                }
            };
            setTimeout(fallback, animationDuration + 100);
            // a cancelled animation rejects, treat it as finished
            return animation.finished.then(() => undefined, () => undefined);
        };

        const context: PrimeFacesTypes.effects.Context = {
            element: el,
            jq,
            mode,
            options,
            duration,
            easing,
            animate: (keyframes, target) => animate(keyframes, target),
            animateSteps: (keyframes, durations) => {
                const total = durations.reduce((sum, value) => sum + value, 0);
                let elapsed = 0;
                const steps = keyframes.map((keyframe, i) => {
                    const offset = i === 0 || total === 0 ? i / (keyframes.length - 1) : elapsed / total;
                    elapsed += durations[i] ?? 0;
                    return { ...keyframe, offset: i === keyframes.length - 1 ? 1 : offset, easing };
                });
                return animate(steps, el, total, "linear");
            },
            onCleanup: (cleanup) => {
                running.cleanups.push(cleanup);
            }
        };

        let result: Promise<void> | void = undefined;
        try {
            result = definition.run(context);
        }
        catch (error) {
            core.error(error);
        }
        Promise.resolve(result).catch((error: unknown) => core.error(error)).then(() => running.finish());
        if (duration === 0) {
            // without animation, the effect finishes synchronously like in jQuery
            running.finish();
        }
    }
}

/**
 * The effects module of PrimeFaces, available as {@link PrimeFaces.effects}.
 */
export const effects: Effects = new Effects();

/**
 * Global setup of the effects module, called once when PrimeFaces is loaded: registers the jQuery UI easing
 * functions in `$.easing` and lets the `:animated` selector of jQuery match elements with a running effect.
 */
export function globalEffectsSetup(): void {
    registerEasings();

    // the effects are Web Animations, jQuery only knows its own animations
    const animated = $.expr.pseudos["animated"];
    $.expr.pseudos["animated"] = (elem: Element) => runningEffects.has(elem as HTMLElement) || !!animated?.(elem);
}
