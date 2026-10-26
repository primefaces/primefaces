import { core } from "./core.js";
import type { BaseWidgetCfg } from "./core.widget.js";

/**
 * The browser and platform of the user agent, see {@link Environment.browser}. Only the flags of the detected
 * browser and platform are set (e.g. `chrome` and `win`), the others are `undefined`.
 */
export interface BrowserDetection {
    /** Chrome (or another Chromium based browser that is not detected separately). */
    chrome?: boolean;
    /** Firefox and other browsers without a more specific token. */
    mozilla?: boolean;
    /** Microsoft Edge. */
    msedge?: boolean;
    /** Opera. */
    opr?: boolean;
    /** Safari. */
    safari?: boolean;
    /** A browser with a WebKit or Blink engine (Chrome, Edge, Opera, Safari). */
    webkit?: boolean;

    /** Android. */
    android?: boolean;
    /** Chrome OS. */
    cros?: boolean;
    /** iOS (iPhone, iPad or iPod; an iPad with a desktop user agent is detected as `mac`). */
    ios?: boolean;
    /** iPad. */
    ipad?: boolean;
    /** iPhone. */
    iphone?: boolean;
    /** iPod. */
    ipod?: boolean;
    /** Kindle. */
    kindle?: boolean;
    /** Linux. */
    linux?: boolean;
    /** macOS. */
    mac?: boolean;
    /** Amazon Silk. */
    silk?: boolean;
    /** Windows. */
    win?: boolean;

    /** A mobile platform (Android, iOS, Kindle, Silk). */
    mobile?: boolean;
    /** A desktop platform (Chrome OS, Linux, macOS, Windows). */
    desktop?: boolean;

    /** The name of the browser, e.g. `chrome`, empty when unknown. */
    name: string;
    /** The version of the browser, e.g. `120.0.6099.129`. */
    version: string;
    /** The major version of the browser, e.g. `120`. */
    versionNumber: number;
    /** The platform, e.g. `win` or `android`, empty when unknown. */
    platform: string;
}

/**
 * Detects the browser and the platform from the user agent, formerly done by the jQuery Browser plugin (`$.browser`).
 * @returns The detected browser and platform.
 */
function detectBrowser(): BrowserDetection {
    const ua = window.navigator.userAgent.toLowerCase();
    const match = /(edg|edga|edgios)\/([\w.]+)/.exec(ua)
        || /(opr)[/]([\w.]+)/.exec(ua)
        || /(chrome)[ /]([\w.]+)/.exec(ua)
        || /(webkit)[ /]([\w.]+).*(version)[ /]([\w.]+).*(safari)[ /]([\w.]+)/.exec(ua)
        || /(webkit)[ /]([\w.]+)/.exec(ua)
        || (ua.indexOf('compatible') < 0 && /(mozilla)(?:.*? rv:([\w.]+)|)/.exec(ua))
        || [];
    // in this order, e.g. the user agent of Android contains "linux" too
    const platform = ['ipad', 'ipod', 'iphone', 'kindle', 'silk', 'android', 'win', 'mac', 'linux', 'cros'].find((p) => ua.includes(p)) ?? '';
    const name = /^edg/.test(match[1] ?? '') ? 'msedge' : match[5] || match[3] || match[1] || '';

    const browser: BrowserDetection = {
        name,
        // for Safari the version after "version/", not the WebKit version
        version: name ? match[4] || match[2] || '0' : '',
        versionNumber: name ? parseInt(match[4] || match[2] || '0', 10) : 0,
        platform
    };
    const flags = browser as unknown as Record<string, boolean>;
    if (name) {
        flags[name] = true;
    }
    if (platform) {
        flags[platform] = true;
    }
    browser.ios = !!(browser.ipad || browser.iphone || browser.ipod) || undefined;
    browser.mobile = !!(browser.android || browser.ios || browser.kindle || browser.silk) || undefined;
    browser.desktop = !!(browser.cros || browser.mac || browser.linux || browser.win) || undefined;
    browser.webkit = !!(browser.webkit || browser.chrome || browser.opr || browser.safari || browser.msedge) || undefined;
    return browser;
}

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
    browser: BrowserDetection = detectBrowser();
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
        this.mobile = !!this.browser.mobile;
        this.touch = 'ontouchstart' in window || window.navigator.maxTouchPoints > 0 || this.mobile === true;
        this.ios = !!this.browser.ios || (!!this.browser.mac && this.touch);
        this.android = !!this.browser.android;
        this.preferredColorSchemeDark = this.evaluateMediaQuery('(prefers-color-scheme: dark)');
        this.preferredColorSchemeLight = !this.preferredColorSchemeDark;
        this.prefersReducedMotion =  this.evaluateMediaQuery('(prefers-reduced-motion: reduce)');
    }

    /**
     * Gets the currently loaded PrimeFaces theme.
     * @return The current theme, such as `aura` or `lara`. Empty string when no theme is loaded.
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
    isTouchable(cfg: PrimeFacesTypes.widget.PartialWidgetCfg<BaseWidgetCfg>): boolean {
        var widgetTouchable = (cfg == undefined) || (cfg.touchable != undefined ? cfg.touchable : true);
        return this.touch && widgetTouchable;
    }

    /**
     * Gets the user's preferred color scheme set in their operating system.
     * 
     * @return The preferred color scheme, either 'dark' or 'light'
     */
    getOSPreferredColorScheme(): PrimeFacesTypes.ThemeKind {
        // evaluated now, the setting can change while the page is open
        return this.evaluateMediaQuery('(prefers-color-scheme: dark)') ? 'dark' : 'light';
    }

    /**
     * Determines if light or dark contrast is being applied. The themes switch between light and dark with the CSS
     * `color-scheme`: `light`, `dark` or `light dark` (the preferred color scheme of the operating system).
     *
     * @param element The element whose color scheme is used (e.g. a widget inside a dark sidebar), default: the page.
     * @return The theme contrast, either 'dark' or 'light'.
     */
    getThemeContrast(element?: Element): PrimeFacesTypes.ThemeKind {
        var schemes = (window.getComputedStyle(element || document.documentElement).colorScheme || '').split(/\s+/);
        var light = schemes.indexOf('light') >= 0;
        var dark = schemes.indexOf('dark') >= 0;
        if (light && dark) {
            return this.getOSPreferredColorScheme();
        }
        return dark ? 'dark' : 'light';
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
