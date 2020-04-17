/**
 * Namespace for the jQuery knob plugin.
 * 
 * This is canvas based plugin that renders a knob the user can move with the mouse ; no png or jpg sprites required.
 * 
 * See https://github.com/aterrien/jQuery-Knob
 */
declare namespace JQueryKnob {
    /**
     * Models the direction of a rotation, either clockwise or anti-clockwise. 
     */
    export type RotationDirection = "clockwise" | "anticlockwise" | "cw" | "acw";

    /**
     * Settings for the jQuery knob plugin to configure the behavior and appearance.
     */
    export interface KnobSettings {
        /**
         * Arc size in degrees. Defaults to `360`.
         */
        angleArc: number;

        /**
         * Starting angle in degrees. Defaults to `0`.
         */
        angleOffset: number;

        /**
         * Maximum value of the knob. Defaults to `100`.
         */
        max: number;

        /**
         * Minimum value of the knob. Defaults to `0`.
         */
        min: number;

        /**
         * Disables input and events. Defaults to `false`.
         */
        readOnly: boolean;

        /**
         * Indicates in which direction the value of the knob increases. Defaults to `clockwise`.
         */
        rotation: RotationDirection;

        /**
         * Step size for incrementing and decrementing. Defaults to `1`.
         */
        step: number;

        /**
         * Stop at min & max on keydown and mousewheel. Defaults to `true`.
         */
        stopper: boolean;

        /**
         * Background color, i.e. the unfilled part of the knob.
         */
        bgColor: string;

        /**
         * Display mode "cursor", cursor size could be changed passing a numeric value to the option, default width is
         * used when passing boolean value `true`.
         */
        cursor: number | true;

        /**
         * Set to `false` to hide the input. Defaults to `true`.
         */
        displayInput: boolean;

        /**
         * `true` to display the previous value with transparency. Defaults to `false`.
         */
        displayPrevious: boolean;

        /**
         * Foreground color, i.e. the filled part of the knob.
         */
        fgColor: string;

        /**
         * Font family used for displaying the value.
         */
        font: string;

        /**
         * Font weight used for displaying the value.
         */
        fontWeight:  string | number;

        /**
         * Dial height, as a CSS unit.
         */
        height: string;

        /**
         * Input value (number) color as a CSS unit.
         */
        inputColor: string; 

        /**
         * Gauge stroke endings.
         */
        lineCap: "butt" | "round";

        /**
         * Gauge thickness, between 0 and 1.
         */
        thickness: number;

        /**
         * Dial width, as a CSS unit.
         */
        width: string;

        /**
         * Triggered when the Escape key is pressed.
         * @return `false` to abort the cancellation and apply the value.
         */
        cancel(this: KnobInstance): boolean;
        
        /**
         * Invoked at each change of the value.
         * @param currentValue The current value of this knob.
         * @return `false` to abort the change and revert the knob to its original value.
         */
        change(this: KnobInstance, currentValue: number): boolean;

        /**
         * Invoked when drawing the canvas.
         * @return `true` to continue with the default drawing of the knob widget, `false` to abort (such as when
         * performing custom drawing). 
         */
        draw(this: KnobInstance): boolean;

        /**
         * Allows to format output (add unit such as `%` or `ms`).
         * @param currentValue Value to be formatted.
         * @return The formatted value.
         */
        format(this: KnobInstance, currentValue: number): string;

        /**
         * Invoked when the knob is released.
         * @param currentValue The current value of this knob.
         * @return `false` to abort the release and not change the value.
         */
        release(this: KnobInstance, currentValue: number): boolean;
    }

    /**
     * The knob instance controlling the knob. It is passed to the callback methods.
     */
    export interface KnobInstance {
        /**
         * The canvas on which the knob is drawn. 
         */
        g: CanvasRenderingContext2D;

        /**
         * Element for the knob. 
         */
        $: JQuery;

        /**
         * Current settings of the knob. 
         */
        o: KnobSettings;

        /**
         * Input element of the knob. 
         */
        i: JQuery;
    }
}

interface JQuery {
    /**
     * Initializes the knob on the current input element. Creates the canvas with the rendered knob.
     * 
     * Options can also be  provided as data attributes, e.g.:
     * 
     * ```html
     * <input type="text" class="dial" data-min="-50" data-max="50">
     * ```
     * 
     * @param settings Optional settings for the knob.
     * @return this jQuery instance for chaining.
     */
    knob(settings?: Partial<JQueryKnob.KnobSettings>): this;
}
