/*
 * Theme configurator of the showcase (theming/configurator.xhtml): color scheme, density, input style, primary color
 * and the main tokens, set live on :root (<html>) and kept in localStorage. Loaded in <head>, so the stored values are
 * applied before the first paint and a reload (e.g. a preset switch) keeps them. The preset itself is chosen on the server
 * (?theme=..., kept in the session).
 */
(function () {
    var root = document.documentElement;
    var KEYS = { scheme: 'pf-demo-scheme', primary: 'pf-demo-primary', density: 'pf-demo-density', tokens: 'pf-demo-tokens',
        input: 'pf-demo-input' };

    function read(key) {
        try { return localStorage.getItem(key); } catch (e) { return null; }
    }

    function write(key, value) {
        try {
            if (value) {
                localStorage.setItem(key, value);
            }
            else {
                localStorage.removeItem(key);
            }
        } catch (e) { }
    }

    // the token overrides of the configurator, name -> value
    var tokens = {};
    try {
        var stored = JSON.parse(read(KEYS.tokens) || '{}');
        // only --p-* tokens: stored values are trusted, stored names are not (a "display" would hide the page)
        if (stored && typeof stored === 'object') {
            Object.keys(stored).filter(function (name) { return name.indexOf('--p-') === 0; })
                .forEach(function (name) { tokens[name] = String(stored[name]); });
        }
    } catch (e) { }

    // data-dark: the effective color scheme for the showcase layout (code highlighting)
    function syncDark() {
        var scheme = root.style.colorScheme;
        var dark = scheme ? scheme === 'dark' : !!(window.matchMedia && window.matchMedia('(prefers-color-scheme: dark)').matches);
        root.setAttribute('data-dark', dark ? 'true' : 'false');
    }

    // before the first paint
    (function () {
        var scheme = read(KEYS.scheme);
        var primary = read(KEYS.primary);
        var density = read(KEYS.density);
        if (scheme) { root.style.colorScheme = scheme; }
        if (primary) { root.style.setProperty('--p-primary-base', primary); }
        // "normal" is the page default: no attribute
        if (density && density !== 'normal') { root.setAttribute('data-p-density', density); }
        root.classList.toggle('ui-input-filled', read(KEYS.input) === 'filled');
        Object.keys(tokens).forEach(function (name) { root.style.setProperty(name, tokens[name]); });
        syncDark();
    })();
    if (window.matchMedia) {
        window.matchMedia('(prefers-color-scheme: dark)').addEventListener('change', syncDark);
    }

    var SURFACE = ['50', '100', '200', '300', '400', '500', '600', '700', '800', '900', '950'];

    // numeric tokens [token, unit]: the p:slider rows of the configurator (configurator-sliders.xhtml), read on DOMContentLoaded
    var RANGES = [];
    var NAMES = [];

    function collectRanges() {
        RANGES = Array.prototype.map.call(document.querySelectorAll('.cfg-field[data-unit]'), function (field) {
            return [field.getAttribute('data-token'), field.getAttribute('data-unit')];
        });
        NAMES = RANGES.map(function (r) { return r[0]; })
            .concat(['--p-typography-font-family'])
            .concat(SURFACE.map(function (s) { return '--p-surface-' + s; }))
            .concat(EXTRA);
    }

    // segmented token settings: button shape, shadows, animations (the values of "theme" are the preset's own)
    var SHAPES = { pill: '999px', square: '0' };
    var DEEP_SHADOWS = {
        '--p-overlay-select-shadow': '0 12px 32px -8px rgb(0 0 0 / 0.28), 0 4px 10px -4px rgb(0 0 0 / 0.18)',
        '--p-overlay-popover-shadow': '0 12px 32px -8px rgb(0 0 0 / 0.28), 0 4px 10px -4px rgb(0 0 0 / 0.18)',
        '--p-overlay-navigation-shadow': '0 12px 32px -8px rgb(0 0 0 / 0.28), 0 4px 10px -4px rgb(0 0 0 / 0.18)',
        '--p-overlay-modal-shadow': '0 32px 64px -12px rgb(0 0 0 / 0.4), 0 12px 24px -8px rgb(0 0 0 / 0.25)',
        '--p-card-shadow': '0 8px 24px -6px rgb(0 0 0 / 0.18), 0 2px 6px rgb(0 0 0 / 0.1)',
        '--p-button-raised-shadow': '0 6px 16px -4px rgb(0 0 0 / 0.35)'
    };
    var MOTION = {
        slow: { '--p-transition-duration': '0.6s', '--p-form-field-transition-duration': '0.6s', '--p-mask-transition-duration': '0.8s' },
        off: { '--p-transition-duration': '0s', '--p-form-field-transition-duration': '0s', '--p-mask-transition-duration': '0s' }
    };
    var EXTRA = ['--p-button-border-radius'].concat(Object.keys(DEEP_SHADOWS)).concat(Object.keys(MOTION.slow));

    // "Animations: off" also switches off the jQuery effects (fade in / out of the overlays)
    function applyMotion() {
        if (window.jQuery) {
            window.jQuery.fx.off = tokens['--p-transition-duration'] === '0s';
        }
    }

    // the slider being dragged: refresh() must not move it
    var sliding = null;

    var defaults = {};
    var ready = false;

    function saveTokens() {
        write(KEYS.tokens, Object.keys(tokens).length ? JSON.stringify(tokens) : null);
    }

    function setToken(name, value) {
        if (value === '' || value === null || value === undefined) {
            delete tokens[name];
            root.style.removeProperty(name);
        }
        else {
            tokens[name] = value;
            root.style.setProperty(name, value);
        }
    }

    // the theme values: computed without the inline overrides of the configurator
    function readDefaults() {
        var inline = {};
        NAMES.forEach(function (name) {
            inline[name] = root.style.getPropertyValue(name);
            root.style.removeProperty(name);
        });
        var cs = getComputedStyle(root);
        NAMES.forEach(function (name) {
            defaults[name] = cs.getPropertyValue(name).trim();
        });
        NAMES.forEach(function (name) {
            if (inline[name]) { root.style.setProperty(name, inline[name]); }
        });
    }

    function toUnit(value, unit) {
        var n = parseFloat(value);
        if (isNaN(n)) { return 0; }
        if (unit === 'px' && /rem$/.test(value)) { return n * 16; }
        if (unit === 'rem' && /px$/.test(value)) { return n / 16; }
        return n;
    }

    function toHex(color) {
        var probe = document.createElement('span');
        probe.style.color = color;
        document.body.appendChild(probe);
        var computed = getComputedStyle(probe).color;
        probe.remove();
        // only possible for sRGB results
        var rgb = /^rgba?\(/.test(computed) ? computed.match(/[\d.]+/g) : null;
        return rgb && rgb.length >= 3 ? '#' + rgb.slice(0, 3).map(function (c) {
            return ('0' + Math.round(parseFloat(c)).toString(16)).slice(-2);
        }).join('') : null;
    }

    function markSegment(attr, value) {
        document.querySelectorAll('[' + attr + ']').forEach(function (b) {
            b.classList.toggle('ui-state-active', b.getAttribute(attr) === value);
            b.setAttribute('aria-pressed', b.getAttribute(attr) === value);
        });
    }

    function mark() {
        var shape = tokens['--p-button-border-radius'];
        var shadow = tokens['--p-card-shadow'];
        var motion = tokens['--p-transition-duration'];
        markSegment('data-cfg-scheme', root.style.colorScheme || 'system');
        markSegment('data-cfg-input', root.classList.contains('ui-input-filled') ? 'filled' : 'outlined');
        markSegment('data-cfg-density', root.getAttribute('data-p-density') || 'normal');
        markSegment('data-cfg-dir', root.getAttribute('dir') === 'rtl' ? 'rtl' : 'ltr');
        markSegment('data-cfg-shape', shape === SHAPES.pill ? 'pill' : shape === SHAPES.square ? 'square' : 'theme');
        markSegment('data-cfg-shadow', shadow === 'none' ? 'none' : shadow ? 'deep' : 'theme');
        markSegment('data-cfg-motion', motion === '0s' ? 'off' : motion ? 'slow' : 'normal');
    }

    // p:slider renders no ARIA on its handle: role slider, labelled by the label of the row, the token value as text
    function labelHandle(widget, token, value, unit) {
        var handle = widget.jq.children('.ui-slider-handle')[0];
        if (!handle) {
            return;
        }
        handle.id = 'cfg-handle' + token;
        handle.setAttribute('role', 'slider');
        handle.setAttribute('aria-labelledby', 'cfg-label' + token);
        handle.setAttribute('aria-valuemin', widget.cfg.min);
        handle.setAttribute('aria-valuemax', widget.cfg.max);
        handle.setAttribute('aria-valuenow', toUnit(value, unit));
        handle.setAttribute('aria-valuetext', value);
    }

    function refreshControls() {
        RANGES.forEach(function (r) {
            var field = document.querySelector('.cfg-field[data-token="' + r[0] + '"]');
            if (!field) {
                return;
            }
            var value = tokens[r[0]] || defaults[r[0]];
            var slider = field.querySelector('.ui-slider');
            var widget = slider ? PrimeFaces.getWidgetById(slider.id) : null;
            if (widget && widget !== sliding) {
                widget.setValue(toUnit(value, r[1]));
            }
            if (widget) {
                labelHandle(widget, r[0], value, r[1]);
            }
            field.querySelector('output').textContent = value;
            field.classList.toggle('changed', r[0] in tokens);
        });
        var font = document.getElementById('cfgFont');
        if (font) {
            font.value = tokens['--p-typography-font-family'] || '';
            font.parentNode.classList.toggle('changed', '--p-typography-font-family' in tokens);
        }
        var surface = tokens['--p-surface-500'];
        var match = surface && surface.match(/--p-([a-z]+)-500/);
        var select = document.getElementById('cfgSurface');
        if (select) {
            select.value = match ? match[1] : '';
            select.parentNode.classList.toggle('changed', !!match);
        }
        var primary = read(KEYS.primary);
        document.querySelectorAll('.cfg-swatch').forEach(function (b) {
            b.setAttribute('aria-pressed', b.getAttribute('data-cfg-primary') === primary);
        });
        var picker = document.getElementById('cfgPrimary');
        var hex = picker && toHex(getComputedStyle(root).getPropertyValue('--p-primary-500').trim());
        if (hex) {
            picker.value = hex;
        }
    }

    // split like the theme build: literal values on :root, values with var() also on [data-p-scope] (resolved per scope)
    function snippet() {
        var literal = [];
        var referencing = [];
        var add = function (name, value) {
            (/var\(/.test(value) ? referencing : literal).push('    ' + name + ': ' + value + ';');
        };
        var primary = read(KEYS.primary);
        if (primary) {
            add('--p-primary-base', primary);
        }
        NAMES.forEach(function (name) {
            if (name in tokens && tokens[name] !== defaults[name]) {
                add(name, tokens[name]);
            }
        });
        var css = [];
        if (literal.length || referencing.length) {
            css.push('/* literal values on :root (they inherit into scopes and density areas), var() references also on [data-p-scope] */');
        }
        if (literal.length) {
            css.push(':root {', literal.join('\n'), '}');
        }
        if (referencing.length) {
            css.push((literal.length ? '\n' : '') + ':root, [data-p-scope] {', referencing.join('\n'), '}');
        }
        var scheme = root.style.colorScheme;
        if (scheme) {
            css.push((css.length ? '\n' : '') + ':root {\n    color-scheme: ' + scheme + ';\n}');
        }
        var density = root.getAttribute('data-p-density');
        if (density) {
            css.push((css.length ? '\n' : '') + '/* density: <html data-p-density="' + density + '"> */');
        }
        return css.length ? css.join('\n') : '/* no changes: the theme defaults are used */';
    }

    function refresh() {
        syncDark();
        mark();
        if (ready) {
            readDefaults();
            refreshControls();
            var pre = document.getElementById('cfgSnippet');
            if (pre) {
                pre.textContent = snippet();
            }
        }
        // lets pages (e.g. the token reference) react to changes
        document.dispatchEvent(new CustomEvent('pf-demo-change'));
    }

    // at most one refresh per frame, for continuous input (color picker)
    var frame = 0;
    function refreshLater() {
        if (!frame) {
            frame = requestAnimationFrame(function () {
                frame = 0;
                refresh();
            });
        }
    }

    function withTransition(apply) {
        var reduced = window.matchMedia && window.matchMedia('(prefers-reduced-motion: reduce)').matches;
        if (document.startViewTransition && !reduced) {
            document.startViewTransition(apply);
        }
        else {
            apply();
        }
    }

    // reloads the page with a request parameter (preset, direction), keeps the scroll position
    function reloadWith(param, value) {
        var url = new URL(window.location.href);
        url.searchParams.set(param, value);
        url.searchParams.delete('jfwid');
        try { sessionStorage.setItem('pf-demo-scroll', String(window.scrollY)); } catch (e) { }
        window.location.href = url.toString();
    }

    var cfg = window.cfg = {
        scheme: function (value) {
            write(KEYS.scheme, value === 'system' ? null : value);
            withTransition(function () {
                root.style.colorScheme = value === 'system' ? '' : value;
                refresh();
            });
        },
        density: function (value) {
            if (!value || value === 'normal') {
                root.removeAttribute('data-p-density');
            }
            else {
                root.setAttribute('data-p-density', value);
            }
            write(KEYS.density, value === 'normal' ? null : value);
            refresh();
        },
        input: function (value) {
            root.classList.toggle('ui-input-filled', value === 'filled');
            write(KEYS.input, value === 'filled' ? value : null);
            refresh();
        },
        // live: from the oninput of the color picker, refreshes once per frame
        primary: function (value, live) {
            if (value) {
                root.style.setProperty('--p-primary-base', value);
            }
            else {
                root.style.removeProperty('--p-primary-base');
            }
            write(KEYS.primary, value);
            if (live) {
                refreshLater();
            }
            else {
                refresh();
            }
        },
        buttonShape: function (value) {
            setToken('--p-button-border-radius', SHAPES[value] || null);
            saveTokens();
            refresh();
        },
        shadows: function (value) {
            Object.keys(DEEP_SHADOWS).forEach(function (name) {
                setToken(name, value === 'none' ? 'none' : value === 'deep' ? DEEP_SHADOWS[name] : null);
            });
            saveTokens();
            refresh();
        },
        motion: function (value) {
            Object.keys(MOTION.slow).forEach(function (name) {
                setToken(name, MOTION[value] ? MOTION[value][name] : null);
            });
            saveTokens();
            applyMotion();
            refresh();
        },
        // the direction is set on the server (primefaces.DIR: the renderers add their RTL classes): reload with ?dir=...
        dir: function (value) {
            reloadWith('dir', value);
        },
        // onSlide of the p:slider rows (configurator-sliders.xhtml)
        slide: function (widget, value) {
            var field = widget.jq.closest('.cfg-field')[0];
            sliding = widget;
            cfg.set(field.getAttribute('data-token'), value + field.getAttribute('data-unit'));
            sliding = null;
        },
        set: function (name, value) {
            setToken(name, value);
            saveTokens();
            refresh();
        },
        surface: function (palette) {
            SURFACE.forEach(function (s) {
                setToken('--p-surface-' + s, palette ? 'var(--p-' + palette + '-' + s + ')' : null);
            });
            saveTokens();
            refresh();
        },
        radius: function (values) {
            ['sm', 'md', 'lg', 'xl'].forEach(function (size, i) {
                setToken('--p-border-radius-' + size, values ? values[i] + 'px' : null);
            });
            saveTokens();
            refresh();
        },
        resetAll: function () {
            Object.keys(tokens).forEach(function (name) { root.style.removeProperty(name); });
            tokens = {};
            saveTokens();
            root.style.removeProperty('--p-primary-base');
            write(KEYS.primary, null);
            root.removeAttribute('data-p-density');
            write(KEYS.density, null);
            root.classList.remove('ui-input-filled');
            write(KEYS.input, null);
            applyMotion();
            cfg.scheme('system');
        },
        // the preset is set on the server (primefaces.THEME): reload with ?theme=..., keep the scroll position (hash)
        preset: function (name) {
            reloadWith('theme', name);
        },
        toggle: function (open) {
            var value = open === undefined ? !root.classList.contains('cfg-open') : open;
            var panel = document.getElementById('cfgPanel');
            var button = document.getElementById('cfgToggle');
            // closing with the focus inside of the panel: the focus goes back to the toggle button
            var restore = !value && panel && panel.contains(document.activeElement);
            root.classList.toggle('cfg-open', value);
            if (button) {
                button.setAttribute('aria-expanded', value);
                if (restore) {
                    button.focus({ preventScroll: true });
                }
            }
            if (value && panel) {
                panel.focus({ preventScroll: true });
            }
            return restore;
        },
        copy: function (button) {
            var text = snippet();
            var done = function () {
                var label = button.querySelector('.ui-button-text');
                label.textContent = 'Copied';
                setTimeout(function () { label.textContent = 'Copy'; }, 1500);
            };
            if (navigator.clipboard && window.isSecureContext) {
                navigator.clipboard.writeText(text).then(done);
            }
            else {
                var area = document.createElement('textarea');
                area.value = text;
                document.body.appendChild(area);
                area.select();
                document.execCommand('copy');
                area.remove();
                done();
            }
        },
        snippet: snippet
    };

    // closes on a click outside of the panel (the page itself stays usable while it is open)
    document.addEventListener('pointerdown', function (e) {
        if (root.classList.contains('cfg-open') && !e.target.closest('#cfgPanel, #cfgToggle, .ui-colorpicker-container')) {
            if (cfg.toggle(false)) {
                // the click itself may blur the toggle button again: keep it unless the click focused another element
                setTimeout(function () {
                    var button = document.getElementById('cfgToggle');
                    if (button && (!document.activeElement || document.activeElement === document.body)) {
                        button.focus({ preventScroll: true });
                    }
                });
            }
        }
    });

    document.addEventListener('keydown', function (e) {
        if (e.key === 'Escape' && root.classList.contains('cfg-open')) {
            cfg.toggle(false);
        }
    });

    document.addEventListener('DOMContentLoaded', function () {
        collectRanges();
        applyMotion();
        ready = true;
        refresh();
        var scroll = null;
        try {
            scroll = sessionStorage.getItem('pf-demo-scroll');
            sessionStorage.removeItem('pf-demo-scroll');
        } catch (e) { }
        if (scroll !== null) {
            window.addEventListener('load', function () { window.scrollTo(0, parseInt(scroll, 10)); });
        }
    });
})();
