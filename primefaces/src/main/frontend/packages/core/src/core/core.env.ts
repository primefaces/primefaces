import { core } from "./core.js";
import type { BaseWidgetCfg } from "./core.widget.js";
import type { JQueryBrowser } from "../../../jquery/jquery-plugins/src/jquery.browser.d.ts";

/**
 * The class with functionality related to the browser environment, such as information about the current browser.
 */
export class Environment {
    /**
     * `true` if the current browser is a mobile browser, `false` otherwise.
     */
    mobile: boolean = false;
    /**
     * `true` if the current browser supports touch, `false` otherwise.
     */
    touch: boolean = false;
    /**
     * `true` if the current browser is an IOS browser, `false` otherwise.
     */
    ios: boolean = false;
    /**
     * `true` if the current browser is an Android browser, `false` otherwise.
     * @type {boolean}
     */
    android: boolean = false;
    /**
     * The current browser type.
     */
    browser: JQueryBrowser.BrowserDetection | null = null;
    /**
     * `true` if the user's current OS setting prefers dark mode, `false` otherwise.
     */
    preferredColorSchemeDark: boolean = false;
    /**
     * `true` if the user's current OS setting prefers light mode, `false` otherwise.
     */
    preferredColorSchemeLight: boolean = false;
    
    /**
     * `true` if the user's current OS setting prefers reduced motion or animations, `false` otherwise.
     */
    prefersReducedMotion: boolean = false;

    /**
     * Initializes the environment by reading the browser environment.
     */
    constructor() {
        this.browser = jQBrowser;
        this.mobile = this.browser.mobile!;
        this.touch = 'ontouchstart' in window || window.navigator.maxTouchPoints > 0 || this.mobile === true;
        this.ios = this.browser.ios! || (this.browser.mac! && this.touch);
        this.android = this.browser.android!;
        this.preferredColorSchemeDark = this.evaluateMediaQuery('(prefers-color-scheme: dark)');
        this.preferredColorSchemeLight = !this.preferredColorSchemeDark;
        this.prefersReducedMotion =  this.evaluateMediaQuery('(prefers-reduced-motion: reduce)');
    }

    /**
     * Gets the currently loaded PrimeFaces theme.
     * @return The current theme, such as `omega` or `luna-amber`. Empty string when no theme is loaded.
     */
    getTheme(): string {
        var themeLink = core.getThemeLink();
        if (themeLink.length === 0) {
            return "";
        }

        const themeURL = themeLink.attr('href') ?? "";
        const plainURL = themeURL.split('&')[0] ?? "";
        const oldTheme = plainURL.split('ln=primefaces-')[1] ?? "";

        return oldTheme;
    }

    /**
     * A widget is touch enabled if the browser supports touch AND the widget has the touchable property enabled.
     * The default will be true if it widget status can't be determined.
     * 
     * @param cfg the widget configuration.
     * @return `true` if touch is enabled, false if disabled
     */
    isTouchable(cfg: PrimeType.widget.PartialWidgetCfg<BaseWidgetCfg>): boolean {
        var widgetTouchable = (cfg == undefined) || (cfg.touchable != undefined ? cfg.touchable : true);
        return this.touch && widgetTouchable;
    }

    /**
     * Gets the user's preferred color scheme set in their operating system.
     * 
     * @return The preferred color scheme, either 'dark' or 'light'
     */
    getOSPreferredColorScheme(): PrimeType.ThemeKind {
        return this.preferredColorSchemeLight ? 'light' : 'dark';
    }

    /**
     * Based on the current PrimeFaces theme determine if light or dark contrast is being applied.
     * 
     * @return The theme contrast, either 'dark' or 'light'.
     */
    getThemeContrast(): PrimeType.ThemeKind {
        var theme = this.getTheme();
        var darkRegex = /(^(arya|vela|.+-(dim|dark))$)/gm;
        return darkRegex.test(theme) ? 'dark' : 'light';
    }
    
    /**
     * Evaluate a media query and return true/false if its a match.
     *
     * @param mediaQuery The media query to evaluate.
     * @return `true` if it matches the query, `false` if not.
     */
    evaluateMediaQuery(mediaQuery: string): boolean {
        return window.matchMedia && window.matchMedia(mediaQuery).matches;
    }

    /**
     * Media query to determine if screen size is below pixel count.
     * @param The number of pixels to check.
     * @return `true` if screen is less than number of pixels
     */
    isScreenSizeLessThan(pixels: number): boolean {
        return this.evaluateMediaQuery('(max-width: ' + pixels + 'px)');
    }

    /**
     * Media query to determine if screen size is above pixel count.
     * @param pixels the number of pixels to check
     * @return true if screen is greater than number of pixels
     */
    isScreenSizeGreaterThan(pixels: number): boolean {
        return this.evaluateMediaQuery('(min-width: ' + pixels + 'px)');
    }
}

/**
 * The object with functionality related to the browser environment, such as information about the current browser.
 */
export const env: Environment = new Environment();
