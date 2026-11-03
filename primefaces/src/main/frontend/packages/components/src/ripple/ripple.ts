/**
 * Ripple: on press, an ink circle spreads over buttons, menu items, list options, tabs, ... when the theme enables it
 * (`--p-ripple-opacity` above 0, e.g. Material). Like the Material 3 ripple: a soft-edged circle grows from the pointer
 * to the center of the element, stays while pressed and fades out on release; keyboard activation gets a centered one.
 * The CSS is in ripple.css.
 */

const TARGETS = [
    ".p-button",
    ".p-menuitem-link",
    ".p-tabs-header",
    ".p-accordion-header",
    ".p-panelmenu-header",
    ".p-paginator-page",
    ".p-paginator-first",
    ".p-paginator-prev",
    ".p-paginator-next",
    ".p-paginator-last",
    ".p-selectonemenu-item",
    ".p-selectcheckboxmenu-item",
    ".p-autocomplete-item",
    ".p-selectlistbox-item",
    ".p-cascadeselect-item",
    ".p-orderlist-item",
    ".p-picklist-item",
    ".p-treenode-content",
    ".p-datepicker-calendar td > a",
    ".p-monthpicker-month",
    ".p-yearpicker-year",
    ".p-panel-titlebar-icon",
    ".p-dialog-titlebar-icon",
    ".p-sidebar-close",
    ".p-chkbox-box",
    ".p-radiobutton-box",
    ".p-toggleswitch",
].join(",");

// parts of a pressed element that carry its ripple, centered (steps: the number circle, not the whole item with the title)
const SURFACES = ".p-steps-number, .p-toggleswitch-handler";

// controls with the 40px circle of the Material 3 state layer around them instead of a ripple clipped to their box
const UNBOUNDED = ".p-chkbox-box, .p-radiobutton-box, .p-toggleswitch-handler";

// no ripple: disabled, not clickable (read-only steps, steps that are not executable), text links (breadcrumb)
const SKIP = [
    ".p-disabled",
    ".p-state-disabled",
    ":disabled",
    "[aria-disabled='true']",
    ".p-steps-readonly",
    ".p-steps-item:not(.p-steps-item-executable)",
    ".p-breadcrumb",
].join(",");

// Material 3 (Material Web) geometry: the circle starts at 20% of the element, ends 10px beyond its diagonal plus a
// soft edge of 35% of the element (at least 75px)
const INITIAL_SCALE = 0.2;
const PADDING = 10;
const SOFT_EDGE_RATIO = 0.35;
const SOFT_EDGE_MIN = 75;

// touch presses start after a moment, so scrolling does not ripple
const TOUCH_DELAY = 150;

function seconds(value: string): number {
    const n = parseFloat(value);
    return value.trim().endsWith("ms") ? n / 1000 : n;
}

function hostOf(target: EventTarget | null): HTMLElement | null {
    const pressed = target instanceof Element ? target.closest<HTMLElement>(TARGETS) : null;
    if (!pressed || pressed.closest(SKIP)) {
        return null;
    }
    return pressed.querySelector<HTMLElement>(SURFACES) ?? pressed;
}

/**
 * Places the container over a static host. The host is not made relative: that would move its absolute children
 * (the sublists of p:cascadeSelect are positioned by the widget against the containing block of the item).
 */
function cover(host: HTMLElement, container: HTMLElement, unbounded: boolean): void {
    const s = container.style;
    s.inset = "auto";
    s.top = s.left = "0px";
    s.translate = "none";
    const origin = container.getBoundingClientRect();
    const box = host.getBoundingClientRect();
    if (unbounded) {
        s.top = `${box.top + box.height / 2 - origin.top}px`;
        s.left = `${box.left + box.width / 2 - origin.left}px`;
        s.translate = "";
    }
    else {
        s.top = `${box.top - origin.top}px`;
        s.left = `${box.left - origin.left}px`;
        s.width = `${box.width}px`;
        s.height = `${box.height}px`;
    }
}

/**
 * Starts the ripple on the host, from the pointer (client coordinates) or centered; returns the release.
 */
function press(host: HTMLElement, point?: { clientX: number; clientY: number }, centered = false): () => void {
    const style = getComputedStyle(host);
    if (!(parseFloat(style.getPropertyValue("--p-ripple-opacity")) > 0)) {
        return () => {};
    }

    const container = document.createElement("span");
    const unbounded = host.matches(UNBOUNDED);
    container.className = unbounded ? "p-ink-container p-ink-unbounded" : "p-ink-container";
    const ink = document.createElement("span");
    ink.className = "p-ink";
    container.append(ink);
    host.append(container);
    if (style.position === "static") {
        cover(host, container, unbounded);
    }

    const rect = container.getBoundingClientRect();
    const { width, height } = rect;
    const max = Math.max(width, height);
    const size = Math.max(1, Math.floor(max * INITIAL_SCALE));
    const scale = (Math.hypot(width, height) + PADDING + Math.max(SOFT_EDGE_RATIO * max, SOFT_EDGE_MIN)) / size;
    const fromPoint = point && !centered && !unbounded && !host.matches(SURFACES);
    const x = fromPoint ? point.clientX - rect.left : width / 2;
    const y = fromPoint ? point.clientY - rect.top : height / 2;
    ink.style.width = ink.style.height = `${size}px`;

    // timing of Material Web, relative to the grow duration (450ms there): fade in 105ms, at least 225ms pressed,
    // fade out 375ms (ripple.css gives the opacity, duration and curve)
    const values = getComputedStyle(ink);
    const opacity = parseFloat(values.opacity);
    const duration = seconds(values.transitionDuration) * 1000;
    const easing = values.transitionTimingFunction;
    const half = size / 2;
    ink.animate({ transform: [`translate(${x - half}px, ${y - half}px) scale(1)`,
        `translate(${width / 2 - half}px, ${height / 2 - half}px) scale(${scale})`] }, { duration, easing, fill: "forwards" });
    ink.animate({ opacity: [0, opacity] }, { duration: duration * 7 / 30, fill: "forwards" });
    const start = performance.now();

    return () => {
        const wait = Math.max(0, duration / 2 - (performance.now() - start));
        setTimeout(() => {
            ink.animate({ opacity: [opacity, 0] }, { duration: duration * 5 / 6, fill: "forwards" }).finished
                .then(() => container.remove(), () => container.remove());
        }, wait);
    };
}

function onPointerDown(event: PointerEvent): void {
    const host = event.button === 0 ? hostOf(event.target) : null;
    if (!host) {
        return;
    }
    const point = { clientX: event.clientX, clientY: event.clientY };
    let release: (() => void) | undefined;
    const begin = () => (release ??= press(host, point));
    const timer = event.pointerType === "touch" ? setTimeout(begin, TOUCH_DELAY) : (begin(), undefined);

    // released (or a tap before the delay): fade out; cancelled (scrolling) before the delay: none
    const listening = new AbortController();
    const end = (e: Event) => {
        listening.abort();
        clearTimeout(timer);
        if (e.type !== "pointercancel" || release) {
            begin()();
        }
    };
    const options = { signal: listening.signal, passive: true };
    document.addEventListener("pointerup", end, options);
    document.addEventListener("pointercancel", end, options);
    host.addEventListener("pointerleave", end, options);
}

function onClick(event: MouseEvent): void {
    // keyboard activation (Enter / Space): no pointer, no ripple running yet
    const host = event.detail === 0 ? hostOf(event.target) : null;
    if (host && !host.querySelector(":scope > .p-ink-container")) {
        press(host, undefined, true)();
    }
}

/**
 * Listens for presses on the document; the theme decides per element whether a ripple is drawn.
 */
export function registerRipple(): void {
    document.addEventListener("pointerdown", onPointerDown, { passive: true });
    document.addEventListener("click", onClick, { passive: true });
}
