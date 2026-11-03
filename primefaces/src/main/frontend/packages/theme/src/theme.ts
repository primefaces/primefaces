/**
 * Theme settings of the user (theme configurator): color scheme, density, input style and token overrides, kept in
 * localStorage and set on `:root` (`<html>`). The HeadRenderer renders this script in the head and it is not moved to
 * the bottom (`primefaces.THEME_CONFIGURATOR`), so the stored settings are applied before the first paint. It needs
 * neither jQuery nor the PrimeFaces core. The preset and the direction are chosen on the server (cookies
 * `primefaces.theme` and `primefaces.dir`).
 */

/**
 * The settings, a missing one keeps the value of the page.
 */
export interface ThemeSettings {
    scheme?: "light" | "dark";
    density?: "normal" | "compact" | "comfortable";
    input?: "outlined" | "filled";
    /** token overrides, name (`--p-*`) -> value */
    tokens?: Record<string, string>;
}

export interface ThemeStore {
    /** The stored settings, a new object on each call. */
    load(): ThemeSettings;
    /** Stores the settings and applies them. */
    save(settings: ThemeSettings): void;
    /** Applies the settings without storing them, e.g. as a preview. */
    apply(settings: ThemeSettings): void;
}

const KEY = "primefaces.theme.settings";
const SCHEMES = ["light", "dark"];
const DENSITIES = ["normal", "compact", "comfortable"];
const INPUTS = ["outlined", "filled"];

export function createThemeStore(root: HTMLElement): ThemeStore {
    // the values of the page itself, restored when a setting is removed
    const initial = {
        scheme: root.style.colorScheme,
        density: root.getAttribute("data-p-density"),
        filled: root.classList.contains("p-input-filled"),
    };
    // the tokens set by apply
    let applied: string[] = [];

    function load(): ThemeSettings {
        let stored: unknown = null;
        try {
            stored = JSON.parse(localStorage.getItem(KEY) || "null");
        }
        catch (e) {
            // private mode, blocked storage or an invalid value: the page defaults
        }
        return sanitize(stored);
    }

    function save(settings: ThemeSettings): void {
        const clean = sanitize(settings);
        try {
            if (Object.keys(clean).length) {
                localStorage.setItem(KEY, JSON.stringify(clean));
            }
            else {
                localStorage.removeItem(KEY);
            }
        }
        catch (e) {
            // not stored, still applied
        }
        apply(clean);
    }

    function apply(settings: ThemeSettings): void {
        const clean = sanitize(settings);
        root.style.colorScheme = clean.scheme ?? initial.scheme;
        const density = clean.density ?? initial.density;
        if (density) {
            root.setAttribute("data-p-density", density);
        }
        else {
            root.removeAttribute("data-p-density");
        }
        root.classList.toggle("p-input-filled", clean.input ? clean.input === "filled" : initial.filled);

        const tokens = clean.tokens ?? {};
        applied.filter(name => !(name in tokens)).forEach(name => root.style.removeProperty(name));
        applied = Object.keys(tokens);
        applied.forEach(name => root.style.setProperty(name, tokens[name] ?? null));
    }

    return { load, save, apply };
}

/**
 * Only known values and `--p-*` token names: stored values are trusted, stored names are not (a `display` would hide
 * the page).
 */
function sanitize(value: unknown): ThemeSettings {
    const settings: ThemeSettings = {};
    if (!value || typeof value !== "object") {
        return settings;
    }
    const raw = value as Record<string, unknown>;
    if (typeof raw["scheme"] === "string" && SCHEMES.includes(raw["scheme"])) {
        settings.scheme = raw["scheme"] as ThemeSettings["scheme"];
    }
    if (typeof raw["density"] === "string" && DENSITIES.includes(raw["density"])) {
        settings.density = raw["density"] as ThemeSettings["density"];
    }
    if (typeof raw["input"] === "string" && INPUTS.includes(raw["input"])) {
        settings.input = raw["input"] as ThemeSettings["input"];
    }
    const tokens = raw["tokens"];
    if (tokens && typeof tokens === "object") {
        const clean: Record<string, string> = {};
        for (const name of Object.keys(tokens)) {
            const token = (tokens as Record<string, unknown>)[name];
            if (name.startsWith("--p-") && (typeof token === "string" || typeof token === "number")) {
                clean[name] = String(token);
            }
        }
        if (Object.keys(clean).length) {
            settings.tokens = clean;
        }
    }
    return settings;
}
