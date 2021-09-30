/**
 * Namespace for the ColorPicker JQuery plugin, available as `JQuery.fn.ColorPicker`. Contains some additional
 * types and interfaces required for the typings.
 * 
 * See also https://www.eyecon.ro/colorpicker
 */
declare namespace JQueryColorPicker {
    /**
     * Represents a color as an RGB hex string. The format is either `#RRGGBB` or `RRGGBB`, where `R`, `G`, and `B`
     * each stand for a hex digit `0` to `F`.
     */
    type RgbHexString = string;
    /**
     * Represents a color in RGB coordinates
     */
    interface RgbColor {
        /**
         * Red channel of this color, in the range `0...255`.
         */
        r: number;
        /**
         * Green channel of this color, in the range `0...255`.
         */
        g: number;
        /**
         * Blue channel of this color, in the range `0...255`.
         */
        b: number;
    } 
    /**
     * Represents a color in HSL coordinates
     */
    interface HsbColor {
        /**
         * Hue channel of this color, in the range `0...360`
         */
        h: number;
        /**
         * Saturation channel of this color, in the range `0...100`
         */
        s: number;
        /**
         * Brightness channel of this color, in the range `0...100`
         */
        b: number;
    } 
    /**
     * Optional settings that can be passed to the color picker when it is initialized on an input element. 
     */
    interface CreateOptions {
        /**
         * The color to display initially.
         */
        color: JQueryColorPicker.RgbHexString | RgbColor | HsbColor;
        /**
         * Called before the color picker is shown.
         * @param colorPicker DOM element of the color picker to be shown.
         * @return If `false` is returned, prevents the color picker from being displayed.
         */
        onShow(colorPicker: HTMLElement): boolean;
        /**
         * Called before the color picker is hidden.
         * @param colorPicker DOM element of the color picker to be hidden.
         * @return If `false` is returned, prevents the color picker from being hidden.
         */
        onHide(colorPicker: HTMLElement): boolean;
        /**
         * Called when the color is changed.
         * @param hsb New color in HSB coordinates.
         * @param hsb New color as a RGB hex string.
         * @param hsb New color RGB coordinates.
         */
        onChange(hsb: HsbColor, hex: RgbHexString, rgb: RgbColor): void;
    }
}
// Additional methods added to JQuery by the ColorPicker plugin
interface JQuery {
    /**
     * Creates a new color picker on the current element.
     * @param opts Options for the color picker to be created, such as the initial color and some callbacks.
     * @return this for chaining.
     */
    ColorPicker(opts?: Partial<JQueryColorPicker.CreateOptions>): this,
    /**
     * Hides this color picker, if it is displayed as a popup.
     * @return this for chaining.
     */
    ColorPickerHide(): this;
    /**
     * Brings up this color picker, if it is displayed as a popup.
     * @return this for chaining.
     */
    ColorPickerShow(): this;
    /**
     * Sets the currently displayed color of this color picker.
     * @param color The new color to display.
     * @return this for chaining.
     */
    ColorPickerSetColor(color: JQueryColorPicker.RgbHexString | JQueryColorPicker.RgbColor | JQueryColorPicker.HsbColor): this;
}