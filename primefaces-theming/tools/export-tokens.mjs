// Exports the theme tokens to the W3C Design Tokens Community Group format (DTCG 2025.10,
// https://www.designtokens.org/TR/2025.10/format/): target/tokens/<preset>.light.tokens.json and
// <preset>.dark.tokens.json. The CSS stays the source of truth: presets/<preset>.css + icons/lucide.css, the density
// variants of density/<preset>.css are attached to the affected tokens.
//
// Mapping:
//   - Groups follow the token names: --p-emerald-500 -> emerald.500, --p-form-field-padding-x -> form.field.padding.x,
//     component tokens are grouped by the component block of the preset (--p-button-primary-background ->
//     button.primary.background). A name that is a token and a group at once (--p-icon-sort, --p-icon-sort-asc) puts
//     the token into the reserved "$root" of the group ({icon.sort.$root}).
//   - light-dark(light, dark) is resolved per file. A value that is only var(--p-x) becomes the alias "{x}", typed like
//     its target.
//   - color-mix(), relative colors (oklch(from ...)) and calc() are computed (culori, CSS gamut mapping to sRGB) and
//     keep the original CSS in $extensions["org.primefaces.css"]; "none" shadows become a transparent zero shadow.
//   - $type: color (srgb components + hex), dimension (px, rem), duration (ms, s), fontWeight, fontFamily, number,
//     shadow (single or list, "inset"), strokeStyle. Unitless 0 is a dimension or a duration when the name says so.
//   - DTCG 2025.10 defines no type for strings, files or multi-value shorthands ("file" is only announced). Those
//     tokens (icon url()s, padding/margin shorthands, em sizes, inherit/unset/currentColor, ...) keep the CSS as
//     $value without $type and carry the original value in $extensions["org.primefaces.css"]; strict DTCG tools may
//     ignore them, CSS based tools can use the value as it is.
//   - $extensions["org.primefaces.density"] = { compact, comfortable }: the value of the token in that density.
import fs from 'node:fs';
import path from 'node:path';
import { fileURLToPath } from 'node:url';
import { converter, formatHex, parse, toGamut } from 'culori';

const root = path.join(path.dirname(fileURLToPath(import.meta.url)), '..');
const cssDir = path.join(root, 'src', 'main', 'css');
const outDir = path.join(root, 'target', 'tokens');

const PRESETS = ['aura', 'lara', 'nora', 'metal'];
const ICONS = 'lucide';
const MODES = ['light', 'dark'];
const CSS_EXT = 'org.primefaces.css';
const DENSITY_EXT = 'org.primefaces.density';

const FONT_WEIGHTS = ['thin', 'hairline', 'extra-light', 'ultra-light', 'light', 'normal', 'regular', 'book', 'medium',
    'semi-bold', 'demi-bold', 'bold', 'extra-bold', 'ultra-bold', 'black', 'heavy', 'extra-black', 'ultra-black'];
const STROKE_STYLES = ['solid', 'dashed', 'dotted', 'double', 'groove', 'ridge', 'outset', 'inset'];
const CSS_KEYWORDS = ['inherit', 'initial', 'unset', 'revert', 'revert-layer', 'currentcolor', 'none', 'auto', 'normal'];
const DIMENSION_NAME = /-(width|height|padding|margin|radius|gap|size|offset|top|left|right|bottom|spacing|indent|gutter|x|y|blur|spread)$/;
const COLOR_SPACES = { srgb: 'rgb', 'srgb-linear': 'lrgb', oklab: 'oklab', oklch: 'oklch', lab: 'lab', lch: 'lch' };
const CHANNELS = { rgb: ['r', 'g', 'b'], lrgb: ['r', 'g', 'b'], oklab: ['l', 'a', 'b'], oklch: ['l', 'c', 'h'], lab: ['l', 'a', 'b'], lch: ['l', 'c', 'h'] };
const toRgb = toGamut('rgb', 'oklch');

// ---------- CSS parsing ----------

function parseDeclarations(file, onDecl) {
    let pending = null;
    let block = null;
    for (const line of fs.readFileSync(file, 'utf8').split(/\r?\n/)) {
        const comment = line.match(/^\s*\/\*\s*([a-z0-9-]+)\s*\*\/\s*$/);
        const density = line.match(/\[data-p-density="([a-z]+)"\]/);
        const decl = line.match(/^\s*--p-([a-z0-9-]+)\s*:\s*(.+?);\s*$/);
        if (comment) pending = comment[1];
        else if (line.trim().endsWith('{')) {
            block = density ? { density: density[1] } : { component: pending };
            pending = null;
        }
        else if (line.trim() === '}') block = null;
        else if (decl && block) onDecl(decl[1], decl[2].trim(), block);
    }
}

function loadTokens(preset) {
    const tokens = new Map();
    const add = (name, value, block) => {
        if (block.density) {
            const token = tokens.get(name);
            if (!token) throw new Error(`density/${preset}.css: --p-${name} is not a token of the preset`);
            token.density[block.density] = value;
            return;
        }
        const segments = block.component && name.startsWith(block.component + '-')
            ? [block.component, ...name.slice(block.component.length + 1).split('-')]
            : name.split('-');
        tokens.set(name, { name, raw: value, segments, density: {} });
    };
    parseDeclarations(path.join(cssDir, 'presets', `${preset}.css`), add);
    parseDeclarations(path.join(cssDir, 'icons', `${ICONS}.css`), add);
    parseDeclarations(path.join(cssDir, 'density', `${preset}.css`), add);

    const groups = new Set();
    for (const t of tokens.values()) t.segments.slice(0, -1).forEach((_, i) => groups.add(t.segments.slice(0, i + 1).join('.')));
    for (const t of tokens.values()) {
        t.path = groups.has(t.segments.join('.')) ? [...t.segments, '$root'] : t.segments;
        t.ref = `{${t.path.join('.')}}`;
    }
    return tokens;
}

// ---------- CSS value helpers ----------

function splitTopLevel(s, separator) {
    const parts = [];
    let depth = 0;
    let current = '';
    for (const ch of s) {
        if (ch === '(') depth++;
        else if (ch === ')') depth--;
        const split = depth === 0 && (separator === ' ' ? /\s/.test(ch) : ch === separator);
        if (split) {
            if (current.trim() || separator !== ' ') parts.push(current.trim());
            current = '';
        }
        else current += ch;
    }
    if (current.trim() || (separator !== ' ' && parts.length)) parts.push(current.trim());
    return parts;
}

// replaces every fn(...) (innermost arguments first) by replace(args)
function replaceFunction(s, fn, replace) {
    for (let start = s.lastIndexOf(fn + '('); start >= 0; start = s.lastIndexOf(fn + '(')) {
        let depth = 0;
        let end = start + fn.length;
        for (; end < s.length; end++) {
            if (s[end] === '(') depth++;
            else if (s[end] === ')' && --depth === 0) break;
        }
        s = s.slice(0, start) + replace(splitTopLevel(s.slice(start + fn.length + 1, end), ',')) + s.slice(end + 1);
    }
    return s;
}

const pickMode = (s, mode) => replaceFunction(s, 'light-dark', (args) => args[MODES.indexOf(mode)]);

// calc() and relative color channel expressions: numbers with units, + - * /, clamp/min/max/calc, channel keywords
function evaluate(expression, variables = {}) {
    const tokens = expression.match(/-?\d*\.?\d+(?:e-?\d+)?[a-z%]*|[a-z][a-z-]*|[-+*/(),]/gi) ?? [];
    let i = 0;
    const peek = () => tokens[i];
    const next = () => tokens[i++];
    const combine = (a, op, b) => {
        if (op === '+' || op === '-') {
            if (a.u !== b.u && a.v !== 0 && b.v !== 0) throw new Error(`incompatible units in ${expression}`);
            return { v: op === '+' ? a.v + b.v : a.v - b.v, u: a.u || b.u };
        }
        if (op === '*') {
            if (a.u && b.u) throw new Error(`incompatible units in ${expression}`);
            return { v: a.v * b.v, u: a.u || b.u };
        }
        if (b.u && b.u !== a.u) throw new Error(`incompatible units in ${expression}`);
        return { v: a.v / b.v, u: b.u ? '' : a.u };
    };
    const factor = () => {
        const t = next();
        if (t === '(') {
            const v = expr();
            next();
            return v;
        }
        if (t === '-') {
            const v = factor();
            return { v: -v.v, u: v.u };
        }
        const number = t?.match(/^(-?\d*\.?\d+(?:e-?\d+)?)([a-z%]*)$/i);
        if (number) return { v: parseFloat(number[1]), u: number[2] };
        if (peek() === '(') {
            next();
            const args = [expr()];
            while (next() === ',') args.push(expr());
            const values = args.map((a) => a.v);
            if (t === 'calc') return args[0];
            if (t === 'min') return { v: Math.min(...values), u: args[0].u };
            if (t === 'max') return { v: Math.max(...values), u: args[0].u };
            if (t === 'clamp') return { v: Math.min(Math.max(values[1], values[0]), values[2]), u: args[1].u };
            throw new Error(`unsupported function ${t}() in ${expression}`);
        }
        if (t in variables) return { v: variables[t], u: '' };
        throw new Error(`unsupported token ${t} in ${expression}`);
    };
    const term = () => {
        let v = factor();
        while (peek() === '*' || peek() === '/') v = combine(v, next(), factor());
        return v;
    };
    const expr = () => {
        let v = term();
        while (peek() === '+' || peek() === '-') v = combine(v, next(), term());
        return v;
    };
    const result = expr();
    if (i !== tokens.length) throw new Error(`cannot evaluate ${expression}`);
    return result;
}

// ---------- colors ----------

function mixColors(args) {
    const [space, hueMethod = 'shorter'] = args[0].replace(/^in\s+/, '').split(/\s+/).filter((p) => p !== 'hue');
    const mode = COLOR_SPACES[space];
    if (!mode) return null;
    const entries = args.slice(1).map((arg) => {
        const m = arg.match(/^(.*?)(?:\s+(\d*\.?\d+)%)?$/s);
        return { color: evalColor(m[1]), p: m[2] === undefined ? undefined : parseFloat(m[2]) / 100 };
    });
    if (entries.length !== 2 || entries.some((e) => !e.color)) return null;
    let [p1, p2] = entries.map((e) => e.p);
    if (p1 === undefined && p2 === undefined) p1 = p2 = 0.5;
    else if (p1 === undefined) p1 = 1 - p2;
    else if (p2 === undefined) p2 = 1 - p1;
    const sum = p1 + p2;
    if (sum <= 0) return null;
    const [c1, c2] = entries.map((e) => converter(mode)(e.color));
    const a1 = c1.alpha ?? 1;
    const a2 = c2.alpha ?? 1;
    const w1 = p1 / sum;
    const w2 = p2 / sum;
    const alpha = a1 * w1 + a2 * w2;
    const result = { mode, alpha: alpha * Math.min(sum, 1) };
    for (const ch of CHANNELS[mode]) {
        let v1 = c1[ch];
        let v2 = c2[ch];
        const missing = (c, v) => v === undefined || Number.isNaN(v) || (ch === 'h' && c.c < 1e-6);
        if (missing(c1, v1) && missing(c2, v2)) continue;
        if (missing(c1, v1)) v1 = v2;
        if (missing(c2, v2)) v2 = v1;
        if (ch === 'h') {
            let d = v2 - v1;
            if (hueMethod === 'shorter' && Math.abs(d) > 180) d -= Math.sign(d) * 360;
            if (hueMethod === 'longer' && Math.abs(d) < 180 && d !== 0) d -= Math.sign(d) * 360;
            if (hueMethod === 'increasing' && d < 0) d += 360;
            if (hueMethod === 'decreasing' && d > 0) d -= 360;
            result.h = (((v1 + d * w2) % 360) + 360) % 360;
        }
        else result[ch] = alpha === 0 ? v1 * w1 + v2 * w2 : (v1 * a1 * w1 + v2 * a2 * w2) / alpha;
    }
    return result;
}

function relativeColor(fn, inner) {
    const mode = COLOR_SPACES[fn] ?? fn;
    if (!CHANNELS[mode] || mode.endsWith('rgb')) return null;
    const [channels, alphaPart] = splitTopLevel(inner.replace(/^from\s+/, ''), '/');
    const parts = splitTopLevel(channels, ' ');
    if (parts.length !== 4) return null;
    const origin = evalColor(parts[0]);
    if (!origin) return null;
    const o = converter(mode)(origin);
    const variables = { alpha: o.alpha ?? 1 };
    CHANNELS[mode].forEach((ch) => (variables[ch] = o[ch] ?? 0));
    const result = { mode };
    CHANNELS[mode].forEach((ch, idx) => {
        const r = evaluate(parts[idx + 1], variables);
        result[ch] = r.u === '%' ? r.v / 100 : r.v;
    });
    result.alpha = alphaPart ? evaluate(alphaPart, variables).v : variables.alpha;
    return result;
}

function evalColor(s) {
    s = s.trim();
    if (s.startsWith('color-mix(') && s.endsWith(')')) return mixColors(splitTopLevel(s.slice(10, -1), ','));
    const relative = s.match(/^([a-z]+)\(\s*(from\s[\s\S]*)\)$/);
    if (relative) return relativeColor(relative[1], relative[2]);
    if (/currentcolor/i.test(s) || s.includes('var(')) return null;
    return parse(s) ?? null;
}

const round = (v, digits = 5) => Number(v.toFixed(digits));

function colorValue(color) {
    const rgb = toRgb(color);
    const clamp = (v) => Math.min(1, Math.max(0, v ?? 0));
    const value = { colorSpace: 'srgb', components: ['r', 'g', 'b'].map((ch) => round(clamp(rgb[ch]))) };
    const alpha = color.alpha ?? 1;
    if (alpha < 1) value.alpha = round(clamp(alpha), 4);
    value.hex = formatHex({ ...rgb, r: clamp(rgb.r), g: clamp(rgb.g), b: clamp(rgb.b) });
    return value;
}

// ---------- type detection ----------

function dimension(s, name) {
    const m = s.match(/^(-?\d*\.?\d+)(px|rem)?$/);
    if (!m || (!m[2] && (parseFloat(m[1]) !== 0 || !DIMENSION_NAME.test(name)))) return null;
    return { value: parseFloat(m[1]), unit: m[2] ?? 'px' };
}

function shadow(s) {
    const zero = { value: 0, unit: 'px' };
    const shadows = splitTopLevel(s, ',').map((part) => {
        const tokens = splitTopLevel(part, ' ');
        const inset = tokens[0] === 'inset';
        if (inset) tokens.shift();
        if (tokens.length === 1 && tokens[0] === 'none') {
            return { color: colorValue(parse('transparent')), offsetX: zero, offsetY: zero, blur: zero, spread: zero, ...(inset && { inset }) };
        }
        const lengths = [];
        while (tokens.length && dimension(tokens[0], '-x')) lengths.push(dimension(tokens.shift(), '-x'));
        const color = tokens.length ? evalColor(tokens.join(' ')) : null;
        if (lengths.length < 2 || lengths.length > 4 || !color) return null;
        const [offsetX, offsetY, blur = zero, spread = zero] = lengths;
        return { color: colorValue(color), offsetX, offsetY, blur, spread, ...(inset && { inset }) };
    });
    if (shadows.some((x) => !x)) return null;
    return shadows.length === 1 ? shadows[0] : shadows;
}

function typed(name, s) {
    const keyword = CSS_KEYWORDS.includes(s.toLowerCase());
    if (name.endsWith('shadow')) {
        const value = shadow(s);
        return value && { $type: 'shadow', $value: value };
    }
    if (name.endsWith('font-weight')) {
        if (/^\d+$/.test(s) && +s >= 1 && +s <= 1000) return { $type: 'fontWeight', $value: +s };
        return FONT_WEIGHTS.includes(s) ? { $type: 'fontWeight', $value: s } : null;
    }
    if (name.endsWith('font-family')) return keyword ? null : { $type: 'fontFamily', $value: s };
    if (name.endsWith('style') && STROKE_STYLES.includes(s)) return { $type: 'strokeStyle', $value: s };
    const duration = s.match(/^(\d*\.?\d+)(ms|s)$/);
    if (duration) return { $type: 'duration', $value: { value: parseFloat(duration[1]), unit: duration[2] } };
    if (s === '0' && name.endsWith('duration')) return { $type: 'duration', $value: { value: 0, unit: 'ms' } };
    if (/^calc\(.*\)$/.test(s)) {
        try {
            const r = evaluate(s);
            if (r.u === 'px' || r.u === 'rem') return { $type: 'dimension', $value: { value: round(r.v, 4), unit: r.u } };
        }
        catch {
            return null;
        }
        return null;
    }
    const dim = dimension(s, name);
    if (dim) return { $type: 'dimension', $value: dim };
    if (/^-?\d*\.?\d+$/.test(s)) return { $type: 'number', $value: parseFloat(s) };
    if (keyword) return null;
    try {
        const color = evalColor(s);
        return color && { $type: 'color', $value: colorValue(color) };
    }
    catch {
        return null;
    }
}

// ---------- export ----------

function exportMode(tokens, mode) {
    const concrete = new Map();
    const result = new Map();

    const resolve = (name, stack = []) => {
        if (concrete.has(name)) return concrete.get(name);
        if (stack.includes(name)) throw new Error(`cyclic reference: ${[...stack, name].join(' -> ')}`);
        const value = pickMode(tokens.get(name).raw, mode)
            .replace(/var\(--p-([a-z0-9-]+)\)/g, (m, ref) => (tokens.has(ref) ? resolve(ref, [...stack, name]) : m));
        concrete.set(name, value);
        return value;
    };

    const plain = (v) => !/var\(|color-mix\(|\(\s*from\s|calc\(/.test(v) && !/(^|\s)none$/.test(v);

    const toToken = (name) => {
        if (result.has(name)) return result.get(name);
        const t = tokens.get(name);
        const value = pickMode(t.raw, mode).trim();
        const alias = value.match(/^var\(--p-([a-z0-9-]+)\)$/);
        let token;
        if (alias && tokens.has(alias[1])) {
            const target = toToken(alias[1]);
            token = { ...(target.$type && { $type: target.$type }), $value: tokens.get(alias[1]).ref };
        }
        else {
            token = typed(name, resolve(name)) ?? { $value: value };
            if (!token.$type || !plain(value)) token.$extensions = { [CSS_EXT]: t.raw };
        }
        const density = Object.entries(t.density);
        if (density.length) {
            token.$extensions ??= {};
            token.$extensions[DENSITY_EXT] = Object.fromEntries(density.map(([key, v]) => [key, typed(name, v)?.$value ?? v]));
        }
        result.set(name, token);
        return token;
    };

    for (const name of tokens.keys()) toToken(name);
    return result;
}

function buildTree(preset, mode, tokens, exported) {
    const tree = {
        $description: `PrimeFaces ${preset} theme, ${mode} mode. Generated from src/main/css (presets/${preset}.css, icons/${ICONS}.css, density/${preset}.css) by tools/export-tokens.mjs, do not edit.`
    };
    for (const [name, token] of exported) {
        let group = tree;
        const p = tokens.get(name).path;
        for (const segment of p.slice(0, -1)) group = group[segment] ??= {};
        group[p[p.length - 1]] = token;
    }
    return tree;
}

function validate(tree, file) {
    const errors = [];
    const lookup = (ref) => ref.split('.').reduce((node, segment) => node?.[segment], tree);
    const walk = (node, at) => {
        if (node && typeof node === 'object' && '$value' in node) {
            for (const ref of JSON.stringify(node.$value).match(/\{[^{}"]+\}/g) ?? []) {
                const target = lookup(ref.slice(1, -1));
                if (!target || !('$value' in target)) errors.push(`${at}: ${ref} does not resolve`);
                else if (node.$type && target.$type !== node.$type) errors.push(`${at}: ${ref} is ${target.$type}, not ${node.$type}`);
            }
            return;
        }
        for (const [key, child] of Object.entries(node)) if (!key.startsWith('$') || key === '$root') walk(child, at ? `${at}.${key}` : key);
    };
    walk(tree, '');
    if (errors.length) throw new Error(`${file}:\n  ` + errors.join('\n  '));
}

fs.mkdirSync(outDir, { recursive: true });
for (const preset of PRESETS) {
    const tokens = loadTokens(preset);
    for (const mode of MODES) {
        const exported = exportMode(tokens, mode);
        const file = `${preset}.${mode}.tokens.json`;
        const tree = buildTree(preset, mode, tokens, exported);
        validate(tree, file);
        fs.writeFileSync(path.join(outDir, file), JSON.stringify(tree, null, 2) + '\n');
        const all = [...exported.values()];
        const untyped = all.filter((t) => !t.$type).length;
        const aliases = all.filter((t) => typeof t.$value === 'string' && t.$value.startsWith('{')).length;
        console.log(`${file}: ${all.length} tokens (${aliases} aliases, ${untyped} without $type)`);
    }
}
