// Checks the conventions of the theme sources, runs before every build (npm run build):
//   - every var(--p-*) without fallback is defined (in each preset, the icon set or the CSS itself)
//   - all presets define the same tokens
//   - no literal colors outside presets/ and icons/ (only tokens, currentColor, transparent, inherit)
//   - transitions use the motion tokens (no literal curves or durations)
//   - no malformed values (an identifier glued to var(), which makes the browser drop the declaration)
//   - every preset token is used (except the primitive palette, the primary base and the border radius scale)
import fs from 'node:fs';
import path from 'node:path';
import { fileURLToPath } from 'node:url';

const root = path.join(path.dirname(fileURLToPath(import.meta.url)), '..');
const cssDir = path.join(root, 'src', 'main', 'css');
const SOURCE_FOLDERS = ['base', 'shared', 'components', 'behaviors'];

const stripComments = (css) => css.replace(/\/\*[\s\S]*?\*\//g, '');
const readFolder = (folder) => fs.readdirSync(path.join(cssDir, folder)).filter((f) => f.endsWith('.css')).sort()
    .map((f) => ({ file: `${folder}/${f}`, css: stripComments(fs.readFileSync(path.join(cssDir, folder, f), 'utf8').replace(/\r\n/g, '\n')) }));
const defs = (css) => new Set([...css.matchAll(/(--p-[a-z0-9-]+)\s*:/g)].map((m) => m[1]));
const refs = (css, withFallback) => new Set([...css.matchAll(withFallback ? /var\((--p-[a-z0-9-]+)/g : /var\((--p-[a-z0-9-]+)\s*\)/g)].map((m) => m[1]));

const sources = SOURCE_FOLDERS.flatMap(readFolder);
const sourceCss = sources.map((s) => s.css).join('\n');
const presets = readFolder('presets');
const icons = readFolder('icons');
const iconCss = icons.map((s) => s.css).join('\n');
const errors = [];

// 1. undefined tokens, per preset
const ownDefs = new Set([...defs(sourceCss), ...defs(iconCss)]);
for (const preset of presets) {
    const defined = new Set([...defs(preset.css), ...ownDefs]);
    const used = new Set([...refs(sourceCss), ...refs(preset.css), ...refs(iconCss)]);
    const missing = [...used].filter((t) => !defined.has(t));
    if (missing.length) errors.push(`${preset.file}: undefined tokens ${missing.join(' ')}`);
}

// 2. same tokens in all presets
const presetDefs = presets.map((p) => defs(p.css));
const allPresetTokens = new Set(presetDefs.flatMap((d) => [...d]));
presets.forEach((p, i) => {
    const absent = [...allPresetTokens].filter((t) => !presetDefs[i].has(t));
    if (absent.length) errors.push(`${p.file}: missing tokens other presets define: ${absent.join(' ')}`);
});

// 3. literal colors in the component CSS
const COLOR = /#[0-9a-fA-F]{3,8}\b|\b(?:rgba?|hsla?|hwb|lab|lch|oklab|oklch)\(|:\s*(?:white|black|red|green|blue|gray|grey|silver|yellow|orange|purple|pink)\b/;
for (const { file, css } of sources) {
    css.split('\n').forEach((line, i) => {
        if (COLOR.test(line)) errors.push(`${file}:${i + 1}: literal color, use a token: ${line.trim()}`);
    });
}

// 4. transitions use the motion tokens: no literal curves (cubic-bezier, ease*) and no literal durations except 0
//    (linear / steps are no curves; base/document.css shortens everything for prefers-reduced-motion)
for (const { file, css } of sources) {
    if (file === 'base/document.css') continue;
    for (const m of css.matchAll(/(?:^|[{;\s])(transition(?:-[a-z]+)?)\s*:\s*([^;{}]+);/g)) {
        const value = m[2].replace(/\s+/g, ' ').trim();
        const line = css.slice(0, m.index).split('\n').length + (m[0].match(/^\s*\n/) ? 1 : 0);
        if (/cubic-bezier\(|\bease(-in|-out|-in-out)?\b/.test(value)) {
            errors.push(`${file}:${line}: literal easing, use --p-transition-easing(-enter|-exit): ${m[1]}: ${value}`);
        }
        if ([...value.matchAll(/(?:^|[\s,(])(\d*\.?\d+)m?s\b/g)].some((d) => parseFloat(d[1]) !== 0)) {
            errors.push(`${file}:${line}: literal duration, use a --p-*-duration token: ${m[1]}: ${value}`);
        }
    }
}

// 5. malformed values: an identifier glued to var( (e.g. "light-darkvar(") is invalid and drops the declaration
for (const { file, css } of [...presets, ...sources, ...icons]) {
    css.split('\n').forEach((line, i) => {
        if (/[a-z0-9]var\(/.test(line)) errors.push(`${file}:${i + 1}: malformed value: ${line.trim()}`);
    });
}

// 6. unused preset tokens (primitive palette and primary scale are a public API, not checked)
const referenced = new Set([...refs(sourceCss, true), ...refs(iconCss, true), ...presets.flatMap((p) => [...refs(p.css, true)])]);
const isPalette = (t) => /^--p-([a-z]+-\d+|primary-base|border-radius-[a-z]+)$/.test(t);
const unused = [...presetDefs[0]].filter((t) => !referenced.has(t) && !isPalette(t)).sort();
if (unused.length) errors.push(`tokens nobody references (use or delete them): ${unused.join(' ')}`);

if (errors.length) {
    console.error(errors.join('\n'));
    process.exit(1);
}
console.log(`check: ${presets.length} presets, ${presetDefs[0].size} tokens, ${sources.length} files OK`);
