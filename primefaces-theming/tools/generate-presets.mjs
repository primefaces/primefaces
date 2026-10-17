// Updates src/main/css/presets/<preset>.css from the PrimeUIX theme presets.
//
// usage: node tools/generate-presets.mjs <path to @primeuix/themes>/dist [aura lara nora]
//
// The preset files are the source of truth and are edited by hand. They contain the PrimeUIX tokens plus the
// PrimeFaces tokens (components and parts PrimeUIX does not have), side by side in the same component blocks.
// This tool only (re)imports the PrimeUIX part: every token PrimeUIX defines gets PrimeUIX's value, every other
// token and every PrimeFaces only component block already in the file is kept as it is.
//
// The preset modules are pure object literals; they are parsed statically and never executed.
// Naming follows PrimeUIX: path segments in kebab case, "root" dropped, prefix "p":
//   button.root.primary.hoverBackground  ->  --p-button-primary-hover-background
//   "{form.field.padding.x}"             ->  var(--p-form-field-padding-x)
// PrimeFaces specific: the primary palette is derived from --p-primary-base (see derivePrimary).
import fs from 'node:fs';
import path from 'node:path';
import { fileURLToPath } from 'node:url';

const cssDir = path.join(path.dirname(fileURLToPath(import.meta.url)), '..', 'src', 'main', 'css');
const outDir = path.join(cssDir, 'presets');
const [, , distDir, ...args] = process.argv;
const presets = args.length ? args : ['aura', 'lara', 'nora'];

if (!distDir) {
    console.error('usage: node tools/generate-presets.mjs <@primeuix/themes>/dist [preset...]');
    process.exit(1);
}

// existing preset file -> { semantic: [[name, value]], components: Map(name -> [[name, value]]) }
// Only the semantic and component blocks are read; primitives always come from PrimeUIX.
function readExisting(file) {
    const result = { semantic: [], components: new Map() };
    if (!fs.existsSync(file)) return result;
    const css = fs.readFileSync(file, 'utf8').replace(/\r\n/g, '\n');
    const decls = (body) => [...body.matchAll(/^\s+(--p-[a-z0-9-]+):\s*(.+);$/gm)].map((m) => [m[1], m[2]]);
    const semantic = css.match(/\n    :root, \[data-p-scope\] \{\n([\s\S]*?)\n    \}\n/);
    if (semantic) result.semantic = decls(semantic[1]);
    for (const m of css.matchAll(/    \/\* ([a-z0-9]+) \*\/\n    :root, \[data-p-scope\] \{\n([\s\S]*?)\n    \}\n/g)) {
        result.components.set(m[1], decls(m[2]));
    }
    return result;
}

// PrimeUIX components PrimeFaces does not have (their tokens are not imported). "sidebar" is the PrimeUIX layout
// sidebar, not p:sidebar (which uses the drawer tokens); the sidebar block only holds the PrimeFaces close button tokens.
const EXCLUDED_COMPONENTS = new Set(['commandmenu', 'compare', 'gallery', 'iftalabel', 'image', 'inlinemessage', 'inputcolor',
    'inputotp', 'inputtags', 'knob', 'metergroup', 'navigationmenu', 'progressspinner', 'ripple', 'scrollarea', 'sidebar',
    'stepper', 'tabview', 'treeselect', 'virtualscroller']);

// PrimeUIX components imported under the name of the PrimeFaces component that uses them
// (p:timeline is the vis-timeline widget with its own tokens; the PrimeUIX timeline styles p:chronoline)
const COMPONENT_RENAMES = { timeline: 'chronoline' };

// Corrections of PrimeUIX values that are wrong upstream (applied on every import)
const UPSTREAM_FIXES = {
    // PrimeUIX 3.0.1 sets these font weights to the font SIZE token
    '--p-datatable-body-cell-font-weight': 'var(--p-typography-font-weight)',
    '--p-treetable-body-cell-font-weight': 'var(--p-typography-font-weight)'
};
const applyUpstreamFixes = (decls) => decls.map(([n, v]) => [n, UPSTREAM_FIXES[n] ?? v]);

// PrimeFaces specific: the text color on the primary color follows the primary color (white, or the upstream dark text
// color when the primary color is light; threshold on the OKLCH lightness per color scheme), so any --p-primary-base reads
function primaryContrast(semanticVars) {
    return semanticVars.map(([n, v]) => {
        const m = n === '--p-primary-contrast-color' && v.match(/^light-dark\(#ffffff, var\(--p-surface-(\d+)\)\)$/);
        if (!m) return [n, v];
        const darkL = { 900: 0.21, 950: 0.141 }[m[1]];
        const auto = (threshold) => `oklch(from var(--p-primary-color) clamp(${darkL}, (${threshold} - l) * 1000, 1) 0 0)`;
        return [n, `light-dark(${auto(0.72)}, ${auto(0.6)})`];
    });
}

// PrimeUIX values win, tokens PrimeUIX does not know are kept (in their original order, after the PrimeUIX ones)
function mergeKeepingExtras(generated, existing) {
    const known = new Set(generated.map(([n]) => n));
    return [...generated, ...existing.filter(([n]) => !known.has(n))];
}

// ---------------------------------------------------------------------------------------------
// static parser for "var a={...},b=...;export{a as default,...}"
// ---------------------------------------------------------------------------------------------
function tokenize(src) {
    const tokens = [];
    let i = 0;
    while (i < src.length) {
        const c = src[i];
        if (/\s/.test(c)) {
            i++;
        }
        else if (c === '"' || c === "'") {
            let j = i + 1, s = '';
            while (src[j] !== c) {
                if (src[j] === '\\') {
                    const n = src[j + 1];
                    s += n === 'n' ? '\n' : n === 't' ? '\t' : n;
                    j += 2;
                }
                else {
                    s += src[j++];
                }
            }
            tokens.push({ t: 'str', v: s });
            i = j + 1;
        }
        else if (/[0-9]/.test(c)) {
            let j = i;
            while (/[0-9.]/.test(src[j])) j++;
            tokens.push({ t: 'num', v: src.slice(i, j) });
            i = j;
        }
        else if (/[A-Za-z_$]/.test(c)) {
            let j = i;
            while (/[\w$]/.test(src[j])) j++;
            tokens.push({ t: 'id', v: src.slice(i, j) });
            i = j;
        }
        else {
            tokens.push({ t: 'p', v: c });
            i++;
        }
    }
    return tokens;
}

function parseModule(src) {
    const tk = tokenize(src);
    const vars = {};
    const exports = {};
    let p = 0;
    const peek = () => tk[p];
    const eat = (v) => {
        const t = tk[p++];
        if (v !== undefined && t.v !== v) throw new Error(`expected '${v}' but got '${t.v}'`);
        return t;
    };
    const value = () => {
        const t = peek();
        if (t.v === '{') return object();
        p++;
        if (t.t === 'str' || t.t === 'num') return t.v;
        if (t.t === 'id' && Object.hasOwn(vars, t.v)) return vars[t.v];
        if (t.t === 'id') return undefined; // e.g. imported css helpers, not a token
        throw new Error(`unexpected '${t.v}'`);
    };
    const object = () => {
        eat('{');
        const o = {};
        while (peek().v !== '}') {
            const k = eat().v;
            o[k] = peek().v === ':' ? (eat(':'), value()) : vars[k];
            if (peek().v === ',') eat(',');
        }
        eat('}');
        return o;
    };

    while (p < tk.length) {
        const t = eat();
        if (t.v === 'var') {
            for (;;) {
                const name = eat().v;
                eat('=');
                vars[name] = value();
                if (peek().v !== ',') break;
                eat(',');
            }
        }
        else if (t.v === 'export') {
            eat('{');
            while (peek().v !== '}') {
                const local = eat().v;
                const name = peek().v === 'as' ? (eat('as'), eat().v) : local;
                exports[name] = vars[local];
                if (peek().v === ',') eat(',');
            }
            eat('}');
        }
        else if (t.v !== ';') {
            throw new Error(`unsupported statement '${t.v}'`);
        }
    }
    return exports;
}

function loadPreset(name) {
    const dir = path.join(distDir, name);
    const preset = { components: {} };
    for (const entry of fs.readdirSync(dir).sort()) {
        const file = path.join(dir, entry, 'index.mjs');
        if (entry === 'css' || !fs.existsSync(file)) continue;
        const exp = parseModule(fs.readFileSync(file, 'utf8'));
        if (entry === 'base') {
            preset.primitive = exp.primitive;
            preset.semantic = exp.semantic;
        }
        else {
            preset.components[COMPONENT_RENAMES[entry] ?? entry] = exp.default;
        }
    }
    return preset;
}

// ---------------------------------------------------------------------------------------------
// tokens -> CSS variables
// ---------------------------------------------------------------------------------------------
const kebab = (s) => s.replace(/([a-z0-9])([A-Z])/g, '$1-$2').toLowerCase();
const varName = (segments) => '--p-' + segments.filter((s) => s !== 'root').map(kebab).join('-');
// References to tokens that do not exist upstream (PrimeUIX 3.0.1 Lara button / treetable)
const REF_FIXES = { 'help.400': 'purple.400', 'danger.400': 'red.400', 'sr.hover.color': 'content.hover.color' };
const renameRef = (ref) => {
    const [head, ...rest] = (REF_FIXES[ref] ?? ref).split('.');
    return [COMPONENT_RENAMES[head] ?? head, ...rest];
};
// PrimeUIX 3.0.1 Lara button: "light-dark{surface.200}, ...)" lacks the opening parenthesis
const fixSyntax = (value) => String(value).replace(/light-dark\{/g, 'light-dark({');
const resolveRefs = (value) => fixSyntax(value).replace(/\{([\w.]+)\}/g, (_, ref) => `var(--p-${renameRef(ref).map(kebab).join('-')})`);

function flatten(obj, segments, out) {
    for (const [key, value] of Object.entries(obj ?? {})) {
        if (key === 'css' || value === undefined) continue;
        if (value !== null && typeof value === 'object') flatten(value, [...segments, key], out);
        else out.push([varName([...segments, key]), resolveRefs(value)]);
    }
    return out;
}

// replaces "primary.50": "{emerald.50}" ... with a palette derived from --p-primary-base
function derivePrimary(semanticVars) {
    const steps = { 50: 'white 92%', 100: 'white 84%', 200: 'white 68%', 300: 'white 48%', 400: 'white 24%',
        600: 'black 16%', 700: 'black 34%', 800: 'black 50%', 900: 'black 62%', 950: 'black 76%' };
    const result = [];
    for (const [name, value] of semanticVars) {
        const m = name.match(/^--p-primary-(\d+)$/);
        if (!m) {
            result.push([name, value]);
            continue;
        }
        if (m[1] === '50') {
            const base = semanticVars.find(([n]) => n === '--p-primary-500')[1];
            result.push(['--p-primary-base', base]);
        }
        result.push([name, m[1] === '500' ? 'var(--p-primary-base)' : `color-mix(in oklch, var(--p-primary-base), ${steps[m[1]]})`]);
    }
    return result;
}

const block = (selector, vars) => `    ${selector} {\n${vars.map(([n, v]) => `        ${n}: ${v};`).join('\n')}\n    }\n`;

for (const name of presets) {
    const preset = loadPreset(name);
    const file = path.join(outDir, `${name}.css`);
    const existing = readExisting(file);
    const title = name[0].toUpperCase() + name.slice(1);
    let css = `/*
 * ${title} preset: the PrimeUIX ${title} tokens plus the PrimeFaces tokens (components/parts PrimeUIX does not have).
 * Edit by hand. tools/generate-presets.mjs re-imports the PrimeUIX values and keeps all PrimeFaces tokens.
 *
 * Token layers (PrimeUIX naming):
 *   primitive  --p-emerald-500, --p-border-radius-md
 *   semantic   --p-primary-color, --p-surface-0, --p-form-field-background, ...
 *   component  --p-button-primary-background, --p-inputtext-padding-x, ... (one block per component)
 *
 * Light and dark mode live side by side via light-dark(); the CSS color-scheme decides:
 *   :root { color-scheme: dark; }   .sidebar { color-scheme: dark; }
 * Primary color: :root { --p-primary-base: var(--p-indigo-500); } (palette 50-950 is derived from it)
 * Scoped overrides: <div data-p-scope style="--p-primary-base: ..."> re-resolves all references inside.
 */
@layer primefaces.tokens {
    :root {
        color-scheme: light dark;
    }

`;
    css += block(':root', flatten(preset.primitive, [], []));
    css += '\n';
    css += block(':root, [data-p-scope]', mergeKeepingExtras(primaryContrast(derivePrimary(flatten(preset.semantic, [], []))), existing.semantic));

    const components = new Map(Object.entries(preset.components)
        .filter(([c]) => !EXCLUDED_COMPONENTS.has(c))
        .map(([c, tokens]) => [c, applyUpstreamFixes(flatten(tokens, [c], []))]));
    for (const [c, decls] of existing.components) {
        components.set(c, mergeKeepingExtras(components.get(c) ?? [], decls));
    }
    for (const c of [...components.keys()].sort()) {
        css += '\n' + block(`/* ${c} */\n    :root, [data-p-scope]`, components.get(c));
    }
    css += '}\n';

    fs.writeFileSync(file, css);
    console.log(`${name}: ${css.split('\n').length} lines -> ${path.relative(process.cwd(), file)}`);
}
