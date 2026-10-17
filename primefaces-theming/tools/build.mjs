// Builds the themes into target/classes/META-INF/resources/primefaces-theming/, minified:
//   components.css  = icon set + third-party CSS + the component CSS, the same for every theme (stays cached)
//   <preset>.css    = presets/<preset>.css + density/<preset>.css, the tokens of a theme
// A theme is rendered as components.css followed by its tokens (built-in <preset>.css or a custom "library:name").
//
// The order of the component CSS is defined here, once: base -> shared -> components -> behaviors, files inside a
// folder alphabetically.
// Cascade layers (lowest first): tokens, vendor (third-party CSS), components (base + shared + components: specificity
// and source order decide, so a component overrides a shared building block on equal specificity), behaviors (drag &
// drop, resize, sticky: override any component). Application CSS is unlayered and always wins without !important.
import fs from 'node:fs';
import path from 'node:path';
import { fileURLToPath } from 'node:url';
import * as esbuild from 'esbuild';

const root = path.join(path.dirname(fileURLToPath(import.meta.url)), '..');
const cssDir = path.join(root, 'src', 'main', 'css');
const outDir = path.join(root, 'target', 'classes', 'META-INF', 'resources', 'primefaces-theming');

const ORDER = ['base', 'shared', 'components', 'behaviors'];
const LAYERS = { base: 'components', shared: 'components', components: 'components', behaviors: 'behaviors' };

// Third-party CSS the components need. The core JS uses the same libraries, so the versions in package.json must be
// the ones the core resolves (checked below). The jScrollPane CSS is not needed: PrimeFaces patches the plugin to render
// its own ui-scrollpanel-* classes, which components/scrollpanel.css styles. PrimeIcons are loaded separately.
const VENDOR = [
    'jquery-ui/themes/base/core.css',
    'jquery-ui/themes/base/draggable.css',
    'jquery-ui/themes/base/resizable.css',
    'jquery-ui/themes/base/selectable.css',
    'jquery-ui/themes/base/sortable.css',
    'jquery-ui/themes/base/datepicker.css',
    'jquery-ui/themes/base/slider.css',
    '@melloware/coloris/dist/coloris.css',
    'jsplumb/css/jsplumbtoolkit-defaults.css',
    'cropperjs/dist/cropper.css',
    'quill/dist/quill.snow.css',
    'vis-timeline/styles/vis-timeline-graph2d.css',
    '@fullcalendar/common/main.css',
    '@fullcalendar/daygrid/main.css',
    '@fullcalendar/timegrid/main.css',
    '@fullcalendar/list/main.css'
];
const CORE_PACKAGE_JSON = path.join(root, '..', 'primefaces', 'src', 'main', 'frontend', 'package.json');
const PRESETS = ['aura', 'lara', 'nora'];
const ICONS = 'lucide';
const minify = process.env.THEMING_MINIFY !== 'false';

const LAYER_ORDER = '@layer primefaces.tokens, primefaces.vendor, primefaces.components, primefaces.behaviors;\n';

const readFolder = (folder) => {
    const dir = path.join(cssDir, folder);
    if (!fs.existsSync(dir)) return '';
    const css = fs.readdirSync(dir).filter((f) => f.endsWith('.css')).sort()
        .map((f) => `/* ${folder}/${f} */\n` + fs.readFileSync(path.join(dir, f), 'utf8'))
        .join('\n');
    return `@layer primefaces.${LAYERS[folder]} {\n${css}\n}\n`;
};

const transform = async (css, name) => {
    const result = await esbuild.transform(css, { loader: 'css', minify, sourcefile: name, logLevel: 'warning' });
    for (const w of result.warnings) console.warn(`${name}: ${w.text}`);
    return result.code;
};

// url("package/path.svg") -> data URI of the SVG in node_modules; "?fill" fills the outline (e.g. a selected star)
function inlineIcons(css) {
    return css.replace(/url\("([a-z@][\w@./-]*\.svg)(\?fill)?"\)/g, (_, file, fill) => {
        let svg = fs.readFileSync(path.join(root, 'node_modules', file), 'utf8')
            .replace(/<!--[\s\S]*?-->/g, '')
            .replace(/\s+class="[^"]*"/, '')
            .replace(/\s+/g, ' ')
            .replace(/> </g, '><')
            .trim();
        if (fill) svg = svg.replace('fill="none"', 'fill="#000"');
        return `url("data:image/svg+xml,${svg.replace(/"/g, "'").replace(/[<>#%\n]/g, encodeURIComponent)}")`;
    });
}

// fails the build when a vendor package version differs from the version the core uses
function checkVendorVersions() {
    if (!fs.existsSync(CORE_PACKAGE_JSON)) return;
    const core = JSON.parse(fs.readFileSync(CORE_PACKAGE_JSON, 'utf8'));
    const coreDeps = { ...core.dependencies, ...core.devDependencies };
    const own = JSON.parse(fs.readFileSync(path.join(root, 'package.json'), 'utf8')).devDependencies;
    const packages = [...new Set(VENDOR.map((f) => (f.startsWith('@') ? f.split('/').slice(0, 2) : f.split('/').slice(0, 1)).join('/')))];
    const errors = [];
    for (const pkg of packages) {
        const coreSpec = coreDeps[pkg];
        const version = own[pkg];
        if (!coreSpec) {
            errors.push(`${pkg}: not a dependency of the core`);
            continue;
        }
        // "1.2.3", "~1.2.3" or "^1.2.3": the version used here must be the base version of the core spec
        const coreVersion = coreSpec.replace(/^[~^]/, '');
        if (coreVersion !== version) errors.push(`${pkg}: ${version} here, ${coreSpec} in the core`);
    }
    if (errors.length) throw new Error('Vendor CSS versions differ from primefaces/src/main/frontend/package.json:\n  ' + errors.join('\n  '));
}

// bundles the vendor CSS (resolving @imports and inlining images/fonts) into the vendor layer
async function buildVendor() {
    const result = await esbuild.build({
        stdin: { contents: VENDOR.map((f) => `@import "${f}";`).join('\n'), loader: 'css', resolveDir: root },
        bundle: true,
        write: false,
        logLevel: 'warning',
        loader: { '.png': 'dataurl', '.gif': 'dataurl', '.svg': 'dataurl', '.woff': 'dataurl', '.woff2': 'dataurl', '.ttf': 'dataurl', '.eot': 'dataurl' }
    });
    return `@layer primefaces.vendor {\n${result.outputFiles[0].text}\n}\n`;
}

checkVendorVersions();
fs.mkdirSync(outDir, { recursive: true });

const icons = inlineIcons(fs.readFileSync(path.join(cssDir, 'icons', `${ICONS}.css`), 'utf8'));
const components = (await buildVendor()) + ORDER.map(readFolder).join('\n');
const componentsOut = await transform(LAYER_ORDER + icons + components, 'components.css');
fs.writeFileSync(path.join(outDir, 'components.css'), componentsOut);
console.log(`components.css: ${(componentsOut.length / 1024).toFixed(0)} KB`);

for (const preset of PRESETS) {
    const tokens = ['presets', 'density'].map((folder) => fs.readFileSync(path.join(cssDir, folder, `${preset}.css`), 'utf8'));
    const out = await transform(LAYER_ORDER + tokens.join('\n'), `${preset}.css`);
    fs.writeFileSync(path.join(outDir, `${preset}.css`), out);
    console.log(`${preset}.css: ${(out.length / 1024).toFixed(0)} KB`);
}
