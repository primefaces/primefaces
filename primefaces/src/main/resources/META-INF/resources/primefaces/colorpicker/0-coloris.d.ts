/**
 * Converts an input field to a color picker input.
 */
declare function Coloris(opts: Coloris.ColorisOptions): void;

declare namespace Coloris {

  /**
   * All color themes supported by the color picker. More themes might be added
   * in the future.
   */
  type Theme =
    | "default"
    | "large"
    | "polaroid"
    | "pill";

  /**
   * All theme modes.
   */
  type ThemeMode =
    | "light"
    | "dark"
    | "auto";

  /**
   * Color format used by the color picker. The format affects which value is
   * shown in the input field.
   * - `hex` outputs `#RRGGBB` or `#RRGGBBAA`.
   * - `rgb` outputs `rgb(R, G, B)` or `rgba(R, G, B, A)`.
   * - `hsl` outputs `hsl(H, S, L)` or `hsla(H, S, L, A)`.
   * - `auto` guesses the format from the active input field. Defaults to `hex` if
   * it fails.
   * - `mixed` outputs `#RRGGBB` when alpha is 1; otherwise `rgba(R, G, B, A)`.
   */
  type ColorFormat =
    | "hex"
    | "rgb"
    | "hsl"
    | "auto"
    | "mixed";

  interface Accessibility {
    /**
     * @default "Open color picker"
     */
    open: string;

    /**
     * @default "Close color picker"
     */
    close: string;

    /**
     * @default "Saturation: {s}. Brightness: {v}."
     */
    marker: string;

    /**
     * @default "Hue slider"
     */
    hueSlider: string;

    /**
     * @default "Opacity slider"
     */
    alphaSlider: string;

    /**
     * @default "Color swatch"
     */
    input: string;

    /**
    * @default "Color format"
    */
    format: string;

    /**
     * @default "Color swatch"
     */
    swatch: string;

    /**
     * @default "Saturation and brightness selector. Use up, down, left and right arrow keys to select."
     */
    instruction: string;
  }

  /**
  * Configuration for the optional clear button on the color picker.
  */
  interface ClearButtonOptions {
    /**
     * Whether the clear button is displayed when the color picker is opened.
     */
    show: boolean;

    /**
     * The label text shown on the clear button.
     */
    label: string;
  }

  /**
  * Configuration for the optional close button on the color picker.
  */
  interface CloseButtonOptions {
    /**
     * Whether the close button is displayed when the color picker is opened.
     */
    show: boolean;

    /**
     * The label text shown on the close button.
     */
    label: string;
  }

  interface ColorisOptions {
    /**
     * A custom CSS selector to bind the color picker to. This must point to
     * one or more {@link HTMLInputElement}s.
     */
    el: string;

    /**
     * CSS selector for the parent.
     *
     * The default behavior is to append the color picker's dialog to the end of the document's
     * body. but it is possible to append it to a custom parent instead. This is especially useful
     * if the color fields are in a scrollable container and you want color picker' dialog to stay
     * anchored to them. You will need to set the position of the container to relative or absolute.
     * Note: This should be a scrollable container with enough space to display the picker.
     *
     * @default null
     */
    parent?: null | string;

    /**
     * The bound input fields are wrapped in a div that adds a thumbnail
     * showing the current color and a button to open the color picker (for
     * accessibility only).
     *
     * If you wish to keep your fields unaltered, set this to `false`, in which
     * case you will lose the color thumbnail and the accessible button (not
     * recommended).
     *
     * @default true
     */
    wrap?: boolean;
    
    // Set to true to activate basic right-to-left support.
    /**
     * Set to true to activate basic right-to-left support.
     *
     * @default false
     */
    rtl?: boolean;

    /**
     * The color theme to use for the color picker. More themes might be added
     * in the future. Available themes: default, large, polaroid.
     *
     * @default "default"
     */
    theme?: Theme;

    /**
     * Set the theme to light or dark mode:
     *  - light: light mode.
     *  - dark: dark mode.
     *  - auto: automatically enables dark mode when the user prefers a dark color scheme.
     *
     * @default "light"
     */
    themeMode?: ThemeMode,

    /**
     * The margin in pixels between the input fields and the color picker's
     * dialog.
     *
     * @default 2
     */
    margin?: number;

    /**
     * Sets the preferred color string format. The format affects which value is
     * shown in the input field. See {@link ColorFormat} for more details.
     *
     * @default "hex"
     */
    format?: ColorFormat;

    /**
     * Set to true to enable format toggle buttons in the color picker dialog.
     *
     * This will also force the format to auto.
     *
     * @default true
     */
    formatToggle?: boolean;

    /**
     * Focus the color value input when the color picker dialog is opened.
     *
     * @default true
     */
    focusInput?: boolean;

    /**
     * Select and focus the color value input when the color picker dialog is opened.
     *
     * @default false
     */
    selectInput?: boolean;

    /**
     * Set to true to hide all the color picker widgets (spectrum, hue, ...) except the swatches.
     *
     * @default false
     */
    swatchesOnly?: boolean;

    /**
     * Enable or disable alpha support.
     *
     * When disabled, it will strip the alpha value from the existing color
     * value in all formats.
     *
     * @default true
     */
    alpha?: boolean;

    /**
     * Set to true to always include the alpha value in the color value even if the opacity is 100%.
     *
     * @default false
     */
    forceAlpha?: boolean,

    /**
     * Shows a clear button and set its label. By default, no clear button is
     * shown.
     *
     * @default undefined
     */
    clearButton?: ClearButtonOptions;

    /**
     * Shows a close button and set its label. By default, no close button is
     * shown.
     *
     * @default undefined
     */
    closeButton?: CloseButtonOptions;

    /**
     * An array of the desired color swatches to display. If omitted or the
     * array is empty, the color swatches will be disabled.
     *
     * @default []
     */
    swatches?: string[];

    /**
     * Accessibility messages for various aria attribute etc.
     */
    a11y?: Accessibility;

    /**
     * Set to true to use the color picker as an inline widget. In this mode the color picker is
     * always visible and positioned statically within its container, which is by default the body
     * of the document. Use the "parent" option to set a custom container.
     * Note: In this mode, the best way to get the picked color, is listening to the "coloris:pick"
     * event and reading the value from the event detail (See example in the Events section). The
     * other way is to read the value of the input field with the id "clr-color-value".
     */
    inline?: boolean;

    /**
     * In inline mode, this is the default color that is set when the picker is initialized.
     */
    defaultColor?: string;
  }

  /**
   * The color picker dialog can be closed by clicking anywhere on the
   * page or by pressing the ESC on the keyboard. The later will also
   * revert the color to its original value.
   *
   * If you would like to close the dialog programmatically, you can do so
   * by calling this method.
   *
   * @param {boolean} revert When `true`, resets the color to its original
   * value. Defaults to `false`.
   */
  function close(revert?: boolean): void;

  /**
   * Converts an input field to a color picker input.
   */
  function coloris(opts: ColorisOptions): void;

  /**
   * Initializes the Coloris color picker and binds the color picker to all
   * input fields with the `data-coloris` attribute.
   * 
   * When the script file is loaded directly in a browser, this method is
   * called automatically. When called in a module environment (e.g.
   * browserify, rollup, or webpack), you need to call this method once before
   * any other calls to any {@link Coloris} methods. This method checks for
   * when the document is ready, so you do not have to call this method inside
   * some document ready block. 
   */
  function init(): void;
}

export as namespace Coloris;

/**
 * The main entry point or namespace for Coloris. This object is callable and
 * can be used to initialize Coloris. It also contains several utility
 * methods.
 */
export = Coloris;

