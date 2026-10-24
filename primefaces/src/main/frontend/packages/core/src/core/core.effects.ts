import { core } from "./core.js";

/*
 * Animation effects (blind, bounce, clip, drop, explode, fade, fold, highlight, puff, pulsate, scale, shake, size,
 * slide, transfer) and the easing functions formerly provided by jQuery UI, implemented with the Web Animations API.
 *
 * The effects, their options and the easing functions are modelled after jQuery UI (https://api.jqueryui.com/category/effects/),
 * Copyright OpenJS Foundation and other contributors, released under the MIT license.
 */

declare global {
    namespace PrimeType {
        /**
         * Mode of an effect:
         *
         * - `show`: shows the hidden element with the effect.
         * - `hide`: hides the visible element with the effect.
         * - `toggle`: shows the element when it is hidden, hides it otherwise.
         * - `effect`: runs the effect on the element without showing or hiding it.
         * @since 17.0.0
         */
        export type EffectMode = "show" | "hide" | "toggle" | "effect";

        /**
         * Callback invoked when an effect has finished.
         * @since 17.0.0
         */
        export type EffectCallback = (this: HTMLElement) => void;

        /**
         * Options for the effects of {@link PrimeFaces.effects}. Besides the general options, each effect supports
         * its own options, e.g. `direction` for `blind`.
         * @since 17.0.0
         */
        export interface EffectOptions {
            /**
             * The mode of the effect, defaults to the default mode of the effect.
             */
            mode?: EffectMode;
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
            complete?: EffectCallback | null;
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
         * Context passed to an {@link EffectImplementation}.
         * @since 17.0.0
         */
        export interface EffectContext {
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
            options: EffectOptions;
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
             * @return A promise that resolves when the animation finished.
             */
            animate(keyframes: Keyframe[], target?: Element): Promise<void>;
            /**
             * Animates the element through the given keyframes, with the given duration for each step between two
             * keyframes. Each step uses the easing of the effect.
             * @param keyframes The keyframes to animate.
             * @param durations The durations of the steps, one less than the keyframes.
             * @return A promise that resolves when the animation finished.
             */
            animateSteps(keyframes: Keyframe[], durations: number[]): Promise<void>;
            /**
             * Registers a function that is called when the effect has finished or was stopped.
             * @param cleanup The function to call.
             */
            onCleanup(cleanup: () => void): void;
        }

        /**
         * Implementation of an effect registered with {@link PrimeFaces.effects.register}.
         * @since 17.0.0
         */
        export type EffectImplementation = (context: EffectContext) => Promise<void> | void;
    }
}

interface EffectDefinition {
    mode: PrimeType.EffectMode;
    run: PrimeType.EffectImplementation;
}

interface RunningEffect {
    animations: Animation[];
    cleanups: (() => void)[];
    finish: () => void;
}

type Point = [number, number];

const SPEEDS: Record<string, number> = { fast: 200, normal: 400, slow: 600 };

const CSS_EASING = /^(linear|ease|ease-in|ease-out|ease-in-out|step-start|step-end)$|^(cubic-bezier|steps|linear)\(/;

const easingCache: Record<string, string> = {};

const runningEffects: WeakMap<HTMLElement, RunningEffect> = new WeakMap();

function resolveDuration(duration: PrimeType.EffectOptions["duration"]): number {
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
 * Converts the name of a jQuery easing function into a CSS easing function.
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

function px(value: number): string {
    return value + "px";
}

function translate(axis: "x" | "y", value: number): string {
    return axis === "x" ? px(value) + " 0px" : "0px " + px(value);
}

function opacityOf(element: HTMLElement): number {
    const opacity = parseFloat(getComputedStyle(element).opacity);
    return isNaN(opacity) ? 1 : opacity;
}

function isVertical(direction: string): boolean {
    return direction === "up" || direction === "down";
}

function isNegative(direction: string): boolean {
    return direction === "up" || direction === "left";
}

function outerSize(jq: JQuery<HTMLElement>, axis: "x" | "y", includeMargin?: boolean): number {
    return (axis === "x" ? jq.outerWidth(!!includeMargin) : jq.outerHeight(!!includeMargin)) ?? 0;
}

/**
 * Builds the `inset()` of a `clip-path`, that hides the element up to the given side.
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
 * following content moves while the element is clipped.
 */
function collapse(ctx: PrimeType.EffectContext, axis: "x" | "y", part: number): Keyframe {
    const property = axis === "y" ? "marginBottom" : "marginRight";
    return { [property]: px((parseFloat(ctx.jq.css(property)) || 0) - part) };
}

function scaleFactor(direction: string | undefined, percent: number): Point {
    const factor = percent / 100;
    return [direction === "vertical" ? 1 : factor, direction === "horizontal" ? 1 : factor];
}

function parseScale(value: string): Point {
    if (!value || value === "none") {
        return [1, 1];
    }
    const parts = value.split(/\s+/).map((part) => part.endsWith("%") ? parseFloat(part) / 100 : parseFloat(part));
    const x = isNaN(parts[0]!) ? 1 : parts[0]!;
    return [x, parts.length > 1 && !isNaN(parts[1]!) ? parts[1]! : x];
}

function transformOrigin(origin: PrimeType.EffectOptions["origin"]): string {
    const [vertical, horizontal] = origin || ["middle", "center"];
    const y = vertical === "top" ? "0%" : vertical === "bottom" ? "100%" : vertical === "middle" ? "50%" : px(Number(vertical));
    const x = horizontal === "left" ? "0%" : horizontal === "right" ? "100%" : horizontal === "center" ? "50%" : px(Number(horizontal));
    return x + " " + y;
}

/**
 * Shared implementation of the `scale` and `puff` effects.
 */
function scaleEffect(ctx: PrimeType.EffectContext, percent: number, fade: boolean): Promise<void> {
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

const effectDefinitions: Record<string, EffectDefinition> = {
    blind: {
        mode: "hide",
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
                keyframes.push({ translate: translate(axis, sign * distance), opacity: 0 });
            }

            return ctx.animateSteps(keyframes, keyframes.slice(1).map(() => speed));
        }
    },

    clip: {
        mode: "hide",
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
        run(ctx) {
            const hidden = { opacity: 0 };
            const visible = { opacity: opacityOf(ctx.element) };
            return ctx.animate(ctx.mode === "show" ? [hidden, visible] : [visible, hidden]);
        }
    },

    fold: {
        mode: "hide",
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
        run(ctx) {
            return scaleEffect(ctx, parseInt(String(ctx.options.percent), 10) || 150, true);
        }
    },

    pulsate: {
        mode: "show",
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
        run(ctx) {
            const parsed = parseInt(String(ctx.options.percent), 10);
            const percent = isNaN(parsed) ? (ctx.mode !== "effect" ? 0 : 100) : parsed;
            return scaleEffect(ctx, percent, !!ctx.options.fade);
        }
    },

    shake: {
        mode: "effect",
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
 * Easing functions formerly provided by jQuery UI, e.g. `easeInOutCirc`, registered in `$.easing`.
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
     * Checks whether an effect with the given name exists.
     * @param effect The name of an effect.
     * @return `true` if the effect exists.
     */
    exists(effect: string | null | undefined): boolean {
        return !!effect && Object.prototype.hasOwnProperty.call(effectDefinitions, effect);
    }

    /**
     * Registers a custom effect or overrides an existing one.
     * @param effect The name of the effect.
     * @param mode The default mode of the effect.
     * @param implementation The implementation of the effect.
     */
    register(effect: string, mode: PrimeType.EffectMode, implementation: PrimeType.EffectImplementation): void {
        effectDefinitions[effect] = { mode, run: implementation };
    }

    /**
     * Runs an effect on the given elements, in the mode given by the options or else in the default mode of the
     * effect (e.g. `hide` for `blind`, `effect` for `shake`).
     * @param element The elements to animate.
     * @param effect The name of the effect.
     * @param options The options of the effect.
     * @return The given elements, for chaining.
     */
    run<T extends JQuery<any>>(element: T, effect: string, options?: PrimeType.EffectOptions | null): T {
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
     * Shows the given elements with an effect.
     * @param element The elements to show.
     * @param effect The name of the effect. Without an effect, the elements are shown immediately.
     * @param options The options of the effect.
     * @return The given elements, for chaining.
     */
    show<T extends JQuery<any>>(element: T, effect: string | null | undefined, options?: PrimeType.EffectOptions | null): T {
        return this.runMode(element, effect, options, "show");
    }

    /**
     * Hides the given elements with an effect.
     * @param element The elements to hide.
     * @param effect The name of the effect. Without an effect, the elements are hidden immediately.
     * @param options The options of the effect.
     * @return The given elements, for chaining.
     */
    hide<T extends JQuery<any>>(element: T, effect: string | null | undefined, options?: PrimeType.EffectOptions | null): T {
        return this.runMode(element, effect, options, "hide");
    }

    /**
     * Shows the hidden or hides the visible elements with an effect.
     * @param element The elements to toggle.
     * @param effect The name of the effect. Without an effect, the elements are toggled immediately.
     * @param options The options of the effect.
     * @return The given elements, for chaining.
     */
    toggle<T extends JQuery<any>>(element: T, effect: string | null | undefined, options?: PrimeType.EffectOptions | null): T {
        return this.runMode(element, effect, options, "toggle");
    }

    /**
     * Finishes the running effects and animations of the given elements immediately and clears their queue.
     * @param element The elements to stop.
     * @return The given elements, for chaining.
     */
    stop<T extends JQuery<any>>(element: T): T {
        // clear the queue first, so finishing the running effect does not start the next one
        element.clearQueue("fx");
        element.each((_, el) => {
            runningEffects.get(el)?.finish();
        });
        return element.stop(true, true);
    }

    private runMode<T extends JQuery<any>>(element: T, effect: string | null | undefined, options: PrimeType.EffectOptions | null | undefined,
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

    private execute(el: HTMLElement, effect: string, options: PrimeType.EffectOptions, next?: () => void): void {
        const jq = $(el);
        const definition = this.exists(effect) ? effectDefinitions[effect] : undefined;
        const duration = resolveDuration(options.duration);
        const done = () => {
            options.complete?.call(el);
            next?.();
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
            // unknown effect, just show / hide the element
            if (mode !== "effect") {
                jq[mode]();
            }
            done();
            return;
        }

        if (mode !== "effect") {
            jq.show();
        }

        let finished = false;
        const running: RunningEffect = {
            animations: [],
            cleanups: [],
            finish: () => {
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
                done();
            }
        };
        runningEffects.set(el, running);

        const easing = resolveEasing(options.easing);
        const animate = (keyframes: Keyframe[], target: Element = el, animationDuration: number = duration,
                animationEasing: string = easing): Promise<void> => {
            const animation = target.animate(keyframes, { duration: animationDuration, easing: animationEasing, fill: "both" });
            running.animations.push(animation);
            // a cancelled animation rejects, treat it as finished
            return animation.finished.then(() => undefined, () => undefined);
        };

        const context: PrimeType.EffectContext = {
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
        Promise.resolve(result).catch((error: unknown) => core.error(error)).then(running.finish);
    }
}

/**
 * The effects module of PrimeFaces.
 */
export const effects: Effects = new Effects();

export function globalEffectsSetup(): void {
    registerEasings();
}
