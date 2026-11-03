/**
 * Theme configurator (composite component pc:themeConfigurator): preset, color scheme, density, input style, direction,
 * primary color and the main tokens, changed live. Color scheme, density, input style and tokens are stored and applied
 * by theme.js (primefaces.THEME_CONFIGURATOR, before the first paint), so this script may run late. Preset and
 * direction are rendered by the server: stored as cookies, then the page is reloaded.
 *
 * Works by event delegation on the data-tc-* attributes of the markup, for every configurator of the page (also after
 * an AJAX update). Dispatches p-theme-change on the document after each change.
 */

type ThemeSettings = ReturnType<Window["PrimeFacesTheme"]["load"]>;

const ROOT = ".p-themeconfigurator";
const SURFACE = ["50", "100", "200", "300", "400", "500", "600", "700", "800", "900", "950"];
const RADIUS = ["sm", "md", "lg", "xl"];

// segmented token settings (the values of "theme" are the preset's own)
const SHAPES: Record<string, string> = { pill: "999px", square: "0" };
const RIPPLE: Record<string, string> = { on: "0.12", off: "0" };
const POPOVER_SHADOW = "0 12px 32px -8px rgb(0 0 0 / 0.28), 0 4px 10px -4px rgb(0 0 0 / 0.18)";
const DEEP_SHADOWS: Record<string, string> = {
    "--p-overlay-select-shadow": POPOVER_SHADOW,
    "--p-overlay-popover-shadow": POPOVER_SHADOW,
    "--p-overlay-navigation-shadow": POPOVER_SHADOW,
    "--p-overlay-modal-shadow": "0 32px 64px -12px rgb(0 0 0 / 0.4), 0 12px 24px -8px rgb(0 0 0 / 0.25)",
    "--p-card-shadow": "0 8px 24px -6px rgb(0 0 0 / 0.18), 0 2px 6px rgb(0 0 0 / 0.1)",
    "--p-button-raised-shadow": "0 6px 16px -4px rgb(0 0 0 / 0.35)",
};
const MOTION: Record<string, Record<string, string>> = {
    slow: { "--p-transition-duration": "0.6s", "--p-form-field-transition-duration": "0.6s", "--p-mask-transition-duration": "0.8s" },
    off: { "--p-transition-duration": "0s", "--p-form-field-transition-duration": "0s", "--p-mask-transition-duration": "0s" },
};
const EXTRA = ["--p-button-border-radius", "--p-ripple-opacity", "--p-typography-font-family"]
    .concat(Object.keys(DEEP_SHADOWS), Object.keys(MOTION["slow"] ?? {}), SURFACE.map(s => `--p-surface-${s}`));

// the click actions: data-tc-<action>="<value>"
const ACTIONS = ["preset", "scheme", "input", "density", "dir", "shape", "shadow", "motion", "ripple", "primary", "surface", "radius",
    "reset", "toggle", "close", "copy"];
const ACTION_SELECTOR = ACTIONS.map(a => `[data-tc-${a}]`).join(",");

const root = document.documentElement;
let settings: ThemeSettings = {};
let tokens: Record<string, string> = {};
// the theme values of the tokens the configurator shows, without its overrides
const defaults: Record<string, string> = {};
// the configurators already refreshed, an AJAX update only refreshes new ones
const refreshed = new WeakSet<HTMLElement>();
let frame = 0;
// the running cross-fade of a change
let transition: ViewTransition | null = null;

export function setup(): void {
    if (!window.PrimeFacesTheme) {
        console.warn("pc:themeConfigurator needs the context parameter primefaces.THEME_CONFIGURATOR=true");
        return;
    }
    settings = window.PrimeFacesTheme.load();
    tokens = settings.tokens = settings.tokens ?? {};

    document.addEventListener("click", onClick);
    document.addEventListener("input", onInput);
    document.addEventListener("change", onChange);
    document.addEventListener("pointerdown", onPointerDown);
    document.addEventListener("keydown", onKeyDown);
    // a configurator updated by AJAX shows the current values again, other updates need no refresh
    window.$?.(document).on("pfAjaxComplete", () => {
        if (Array.from(document.querySelectorAll<HTMLElement>(ROOT)).some(c => !refreshed.has(c))) {
            refresh();
        }
    });

    applyMotion();
    refresh();
    restoreScroll();
}

function save(): void {
    window.PrimeFacesTheme.save(settings);
}

function setToken(name: string, value: string | null | undefined): void {
    if (value) {
        tokens[name] = value;
    }
    else {
        delete tokens[name];
    }
}

function setTokens(values: Record<string, string> | undefined, names: string[]): void {
    names.forEach(name => setToken(name, values?.[name]));
}

// "Animations: off" also switches off the jQuery effects
function applyMotion(): void {
    if (window.$) {
        window.$.fx.off = tokens["--p-transition-duration"] === "0s";
    }
}

// a button outside of the configurator opens it with data-tc-toggle="<client id>", or the first overlay without an id
function external(button: HTMLElement): HTMLElement | null {
    const id = button.getAttribute("data-tc-toggle");
    const configurator = id ? document.getElementById(id) : document.querySelector<HTMLElement>(`${ROOT}.p-themeconfigurator-overlay`);
    return configurator?.matches(ROOT) ? configurator : null;
}

function onClick(event: MouseEvent): void {
    // a click that started during a cross-fade hit <html>: the element below in a configurator gets it
    if (event.target === root) {
        const below = document.elementFromPoint(event.clientX, event.clientY);
        if (below instanceof HTMLElement && below !== root && below.closest(ROOT)) {
            below.click();
        }
        return;
    }
    const target = event.target instanceof Element ? event.target.closest<HTMLElement>(ACTION_SELECTOR) : null;
    const configurator = target?.closest<HTMLElement>(ROOT) ?? (target?.hasAttribute("data-tc-toggle") ? external(target) : null);
    if (!target || !configurator) {
        return;
    }
    const action = ACTIONS.find(a => target.hasAttribute(`data-tc-${a}`));
    const value = target.getAttribute(`data-tc-${action}`) ?? "";
    switch (action) {
        case "preset":
            reloadWith("primefaces.theme", value);
            break;
        case "dir":
            reloadWith("primefaces.dir", value);
            break;
        case "scheme":
            settings.scheme = value === "light" || value === "dark" ? value : undefined;
            withTransition(() => change());
            break;
        case "input":
            // explicit, also "outlined" and "normal": the page itself may set p-input-filled or another density
            settings.input = value === "outlined" || value === "filled" ? value : undefined;
            change();
            break;
        case "density":
            settings.density = value === "normal" || value === "compact" || value === "comfortable" ? value : undefined;
            change();
            break;
        case "shape":
            setToken("--p-button-border-radius", SHAPES[value]);
            change();
            break;
        case "shadow":
            Object.keys(DEEP_SHADOWS).forEach(name => setToken(name, value === "none" ? "none" : value === "deep" ? DEEP_SHADOWS[name] : null));
            change();
            break;
        case "motion":
            setTokens(MOTION[value], Object.keys(MOTION["slow"] ?? {}));
            applyMotion();
            change();
            break;
        case "ripple":
            setToken("--p-ripple-opacity", RIPPLE[value]);
            change();
            break;
        case "primary":
            setToken("--p-primary-base", value);
            change();
            break;
        case "surface":
            SURFACE.forEach(s => setToken(`--p-surface-${s}`, value ? `var(--p-${value}-${s})` : null));
            change();
            break;
        case "radius": {
            const values = value ? value.split(",") : [];
            RADIUS.forEach((size, i) => setToken(`--p-border-radius-${size}`, values[i] ? `${values[i]}px` : null));
            change();
            break;
        }
        case "reset":
            reset();
            break;
        case "toggle":
            toggle(configurator);
            break;
        case "close":
            toggle(configurator, false);
            break;
        case "copy":
            copy(target);
            break;
    }
}

function onInput(event: Event): void {
    const input = event.target;
    if (!(input instanceof HTMLInputElement) || !input.closest(ROOT)) {
        return;
    }
    if (input.type === "color") {
        // continuous: refreshes at most once per frame
        setToken("--p-primary-base", input.value);
        save();
        refreshLater();
    }
    else if (input.type === "range") {
        // continuous like the color: applied at once, the configurator refreshed at most once per frame
        const field = input.closest<HTMLElement>("[data-tc-token]");
        if (field) {
            setToken(field.dataset["tcToken"] ?? "", input.value + (field.dataset["tcUnit"] ?? ""));
            save();
            refreshLater();
        }
    }
}

function onChange(event: Event): void {
    const select = event.target;
    if (!(select instanceof HTMLSelectElement) || !select.closest(ROOT)) {
        return;
    }
    if (select.hasAttribute("data-tc-font")) {
        setToken("--p-typography-font-family", select.value);
        change();
    }
}

// closes an overlay on a click outside of it (the page itself stays usable while it is open)
function onPointerDown(event: PointerEvent): void {
    // during the cross-fade of a change the pointer hits <html>: the cross-fade ends, the click goes on in onClick
    if (transition) {
        transition.skipTransition();
        return;
    }
    const target = event.target instanceof Element ? event.target : null;
    // an external toggle button toggles it on click itself
    if (!target || target.closest("[data-tc-toggle]")) {
        return;
    }
    document.querySelectorAll<HTMLElement>(`${ROOT}.p-themeconfigurator-open`).forEach(configurator => {
        if (!configurator.contains(target) && toggle(configurator, false)) {
            // the click itself may blur the toggle button again: keep it unless the click focused another element
            setTimeout(() => {
                if (!document.activeElement || document.activeElement === document.body) {
                    configurator.querySelector<HTMLElement>("[data-tc-toggle]")?.focus({ preventScroll: true });
                }
            });
        }
    });
}

function onKeyDown(event: KeyboardEvent): void {
    if (event.key === "Escape") {
        document.querySelectorAll<HTMLElement>(`${ROOT}.p-themeconfigurator-open`).forEach(c => toggle(c, false));
    }
}

/**
 * Opens or closes the overlay of a configurator.
 * @returns whether the focus was inside of the panel and went back to the toggle button
 */
function toggle(configurator: HTMLElement, open?: boolean): boolean {
    const value = open ?? !configurator.classList.contains("p-themeconfigurator-open");
    const panel = configurator.querySelector<HTMLElement>(".p-themeconfigurator-panel");
    const button = configurator.querySelector<HTMLElement>("[data-tc-toggle]");
    const restore = !value && !!panel && panel.contains(document.activeElement);
    configurator.classList.toggle("p-themeconfigurator-open", value);
    button?.setAttribute("aria-expanded", String(value));
    if (restore) {
        button?.focus({ preventScroll: true });
    }
    if (value) {
        panel?.focus({ preventScroll: true });
    }
    return restore;
}

function reset(): void {
    Object.keys(tokens).forEach(name => delete tokens[name]);
    settings.density = undefined;
    settings.input = undefined;
    settings.scheme = undefined;
    applyMotion();
    withTransition(() => change());
}

function change(): void {
    save();
    refresh();
}

// stores the preset or the direction for the server and reloads the page, keeps the scroll position
function reloadWith(cookie: string, value: string): void {
    PrimeFaces.setCookie(cookie, value, { path: PrimeFaces.settings.contextPath || "/", expires: 365 });
    try {
        sessionStorage.setItem("primefaces.theme.scroll", String(window.scrollY));
    }
    catch (e) {
        // no scroll position
    }
    window.location.reload();
}

function restoreScroll(): void {
    let scroll: string | null = null;
    try {
        scroll = sessionStorage.getItem("primefaces.theme.scroll");
        sessionStorage.removeItem("primefaces.theme.scroll");
    }
    catch (e) {
        // no scroll position
    }
    if (scroll !== null) {
        const top = parseInt(scroll, 10);
        if (document.readyState === "complete") {
            window.scrollTo(0, top);
        }
        else {
            window.addEventListener("load", () => window.scrollTo(0, top));
        }
    }
}

// a cross-fade of the whole page; it catches the pointer, see onPointerDown and onClick
function withTransition(apply: () => void): void {
    const reduced = window.matchMedia?.("(prefers-reduced-motion: reduce)").matches;
    if (document.startViewTransition && !reduced) {
        const current = transition = document.startViewTransition(apply);
        const done = (): void => {
            if (transition === current) {
                transition = null;
            }
        };
        // skipped (e.g. by the next click): the change is applied anyway
        current.ready.catch(() => undefined);
        current.finished.then(done, done);
    }
    else {
        apply();
    }
}

function refreshLater(): void {
    if (!frame) {
        frame = requestAnimationFrame(() => {
            frame = 0;
            refresh();
        });
    }
}

function refresh(): void {
    readDefaults();
    mark();
    refreshControls();
    const css = snippet();
    document.querySelectorAll<HTMLElement>(`${ROOT} [data-tc-snippet]`).forEach(pre => pre.textContent = css);
    document.querySelectorAll<HTMLElement>(ROOT).forEach(c => refreshed.add(c));
    // lets pages (e.g. a token reference) react to changes
    document.dispatchEvent(new CustomEvent("p-theme-change"));
}

// the tokens the configurator shows: the ranges of the markup and the fixed ones
function names(): string[] {
    const ranges = Array.from(document.querySelectorAll<HTMLElement>(`${ROOT} [data-tc-token]`), f => f.dataset["tcToken"] ?? "");
    return ranges.concat(EXTRA).filter((name, i, all) => name && all.indexOf(name) === i);
}

// the theme values: computed without the inline overrides of the configurator
function readDefaults(): void {
    const all = names();
    const inline: Record<string, string> = {};
    all.forEach(name => {
        inline[name] = root.style.getPropertyValue(name);
        root.style.removeProperty(name);
    });
    const style = getComputedStyle(root);
    all.forEach(name => defaults[name] = style.getPropertyValue(name).trim());
    all.forEach(name => {
        if (inline[name]) {
            root.style.setProperty(name, inline[name]);
        }
    });
}

function markSegment(action: string, value: string): void {
    document.querySelectorAll<HTMLElement>(`${ROOT} [data-tc-${action}]`).forEach(button => {
        const active = button.getAttribute(`data-tc-${action}`) === value;
        button.classList.toggle("p-state-active", active);
        button.setAttribute("aria-pressed", String(active));
    });
}

function mark(): void {
    const shape = tokens["--p-button-border-radius"];
    const shadow = tokens["--p-card-shadow"];
    const motion = tokens["--p-transition-duration"];
    const ripple = tokens["--p-ripple-opacity"];
    markSegment("scheme", settings.scheme ?? "system");
    markSegment("input", root.classList.contains("p-input-filled") ? "filled" : "outlined");
    markSegment("density", root.getAttribute("data-p-density") ?? "normal");
    markSegment("dir", root.getAttribute("dir") === "rtl" ? "rtl" : "ltr");
    markSegment("shape", shape === SHAPES["pill"] ? "pill" : shape === SHAPES["square"] ? "square" : "theme");
    markSegment("shadow", shadow === "none" ? "none" : shadow ? "deep" : "theme");
    markSegment("motion", motion === "0s" ? "off" : motion ? "slow" : "normal");
    markSegment("ripple", ripple === RIPPLE["off"] ? "off" : ripple ? "on" : "theme");
    document.querySelectorAll<HTMLElement>(`${ROOT} .p-themeconfigurator-swatch[data-tc-primary]`).forEach(swatch => {
        swatch.setAttribute("aria-pressed", String(swatch.getAttribute("data-tc-primary") === tokens["--p-primary-base"]));
    });
    markSegment("surface", tokens["--p-surface-500"]?.match(/--p-([a-z]+)-500/)?.[1] ?? "");
}

function toUnit(value: string, unit: string): number {
    const n = parseFloat(value);
    if (isNaN(n)) {
        return 0;
    }
    if (unit === "px" && /rem$/.test(value)) {
        return n * 16;
    }
    if (unit === "rem" && /px$/.test(value)) {
        return n / 16;
    }
    return n;
}

function toHex(color: string): string | null {
    const probe = document.createElement("span");
    probe.style.color = color;
    document.body.appendChild(probe);
    const computed = getComputedStyle(probe).color;
    probe.remove();
    // only possible for sRGB results
    const rgb = /^rgba?\(/.test(computed) ? computed.match(/[\d.]+/g) : null;
    return rgb && rgb.length >= 3
        ? "#" + rgb.slice(0, 3).map(c => ("0" + Math.round(parseFloat(c)).toString(16)).slice(-2)).join("")
        : null;
}

function refreshControls(): void {
    document.querySelectorAll<HTMLElement>(`${ROOT} [data-tc-token]`).forEach(field => {
        const token = field.dataset["tcToken"] ?? "";
        const value = tokens[token] ?? defaults[token] ?? "";
        const range = field.querySelector<HTMLInputElement>("input[type=range]");
        // the range being dragged keeps its own value
        if (range && range !== document.activeElement) {
            range.value = String(toUnit(value, field.dataset["tcUnit"] ?? ""));
        }
        range?.setAttribute("aria-valuetext", value);
        const output = field.querySelector("output");
        if (output) {
            output.textContent = value;
        }
        field.classList.toggle("p-themeconfigurator-changed", token in tokens);
    });
    const font = tokens["--p-typography-font-family"] ?? "";
    document.querySelectorAll<HTMLSelectElement>(`${ROOT} select[data-tc-font]`).forEach(select => {
        select.value = font;
        select.parentElement?.classList.toggle("p-themeconfigurator-changed", !!font);
    });
    // the swatch of the preset's own surface
    document.querySelectorAll<HTMLElement>(`${ROOT} [data-tc-surface=""]`).forEach(swatch => {
        swatch.style.background = defaults["--p-surface-500"] ?? "";
    });
    const pickers = document.querySelectorAll<HTMLInputElement>(`${ROOT} input[type=color]`);
    const hex = pickers.length ? toHex(getComputedStyle(root).getPropertyValue("--p-primary-500").trim()) : null;
    if (hex) {
        pickers.forEach(picker => picker.value = hex);
    }
}

/**
 * The CSS of the changes, split like the theme build: literal values on :root, values with var() also on
 * [data-p-scope] (resolved per scope).
 */
function snippet(): string {
    const literal: string[] = [];
    const referencing: string[] = [];
    const add = (name: string, value: string): void => {
        (/var\(/.test(value) ? referencing : literal).push(`    ${name}: ${value};`);
    };
    const primary = tokens["--p-primary-base"];
    if (primary) {
        add("--p-primary-base", primary);
    }
    names().forEach(name => {
        const value = tokens[name];
        if (value !== undefined && value !== defaults[name]) {
            add(name, value);
        }
    });
    const css: string[] = [];
    if (literal.length || referencing.length) {
        css.push("/* literal values on :root (they inherit into scopes and density areas), var() references also on [data-p-scope] */");
    }
    if (literal.length) {
        css.push(":root {", literal.join("\n"), "}");
    }
    if (referencing.length) {
        css.push((literal.length ? "\n" : "") + ":root, [data-p-scope] {", referencing.join("\n"), "}");
    }
    if (settings.scheme) {
        css.push((css.length ? "\n" : "") + `:root {\n    color-scheme: ${settings.scheme};\n}`);
    }
    if (settings.density) {
        css.push((css.length ? "\n" : "") + `/* density: <html data-p-density="${settings.density}"> */`);
    }
    return css.length ? css.join("\n") : "/* no changes: the theme defaults are used */";
}

function copy(button: HTMLElement): void {
    const text = snippet();
    const label = button.querySelector(".p-button-text");
    const done = (): void => {
        if (label) {
            const original = label.textContent;
            label.textContent = "Copied";
            setTimeout(() => label.textContent = original, 1500);
        }
    };
    if (navigator.clipboard && window.isSecureContext) {
        navigator.clipboard.writeText(text).then(done);
    }
    else {
        const area = document.createElement("textarea");
        area.value = text;
        document.body.appendChild(area);
        area.select();
        document.execCommand("copy");
        area.remove();
        done();
    }
}
