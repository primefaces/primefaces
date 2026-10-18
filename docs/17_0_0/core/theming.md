# Theming (design tokens)

PrimeFaces 17 comes with a new theming based on **design tokens**: every color, radius, spacing, shadow, icon and
duration is a CSS variable (`--p-primary-color`, `--p-form-field-background`, `--p-button-border-radius`, ...).
The look of an application is customized with plain CSS: no SASS, no build step, no theme to fork.

Four presets are available:

- **Aura**
- **Lara**
- **Nora**
- **Metal**: graphite and brushed aluminium (light), black metal (dark), an orange focus ring
- **Glass**: Apple Liquid Glass style, translucent overlays, menus, bars and buttons over a calm content layer

Aura, Lara and Nora started from [PrimeUIX](https://github.com/primefaces/primeuix) (the theming of PrimeReact / PrimeVue),
so the token names follow the same convention. They are maintained for PrimeFaces and are not kept in sync with PrimeUIX.

Light and dark mode are built in, each preset supports both.

?> The new theming is independent from the [legacy themes](/core/themes.md) (saga, arya, ...), which keep working
unchanged. See [Compatibility](#compatibility).

## Configuration

Add the `primefaces-theming` dependency:

```xml
<dependency>
    <groupId>org.primefaces</groupId>
    <artifactId>primefaces-theming</artifactId>
    <version>${primefaces.version}</version>
</dependency>
```

and select a preset with the `primefaces.THEME` context parameter:

```xml
<context-param>
    <param-name>primefaces.THEME</param-name>
    <param-value>aura</param-value>   <!-- aura | lara | nora | metal | glass | library:name -->
</context-param>
```

`library:name` selects a [custom theme](#custom-theme).

### What is rendered

A token theme is rendered as two stylesheets in the head:

1. `primefaces-theming:components.css`: the styles of all components, the icons and the CSS of the third-party
   libraries the components use. It is the same for every theme, so it stays cached when the theme changes.
2. the tokens of the theme, e.g. `primefaces-theming:aura.css` (or your custom `library:name`).

The CSS of the PrimeFaces core library (`components.css` of `primefaces`) is **not** loaded with a token theme,
neither on the initial request nor in ajax updates; the theme contains the complete styling of the components.

PrimeIcons are still loaded (unless `primefaces.PRIME_ICONS` is `false`) for the icons the application sets itself,
e.g. `<p:commandButton icon="pi pi-check"/>`. The icons the components draw themselves come from the theme,
see [Icons](#icons).

## Light and dark mode

The mode follows the standard CSS property `color-scheme`. The default is `light dark`, which follows the
operating system setting.

```css
:root {
    color-scheme: dark;        /* light | dark | light dark (system, default) */
}

.sidebar {
    color-scheme: dark;        /* a dark area inside a light page */
}
```

Switching at runtime, e.g. from a toggle button, only needs to change this property:

```js
document.documentElement.style.colorScheme = 'dark';
```

## Primary color

The primary color is derived from one token, `--p-primary-base`. The whole primary palette (`--p-primary-50` to
`--p-primary-950`) and all primary tokens (buttons, selection, focus ring, ...) follow it.

```css
:root {
    --p-primary-base: var(--p-indigo-500);    /* a palette color of the preset */
}
```

```css
:root {
    --p-primary-base: #0a7d5a;                /* or any CSS color */
}
```

The text color on the primary color (`--p-primary-contrast-color`) is computed automatically: white on dark
primary colors, dark text on light ones (yellow, lime, ...), so every primary color stays readable.

## Scoped overrides

Tokens can be overridden for a part of the page. Add the `data-p-scope` attribute to the element, so all tokens
that depend on the overridden one are recalculated inside it:

```xml
<div class="admin" data-p-scope="">
    <p:commandButton value="Delete"/>
</div>
```

```css
.admin[data-p-scope] {
    --p-primary-base: var(--p-red-600);
}
```

!> Inside a `[data-p-scope]` element the theme defaults are declared again. Global overrides of colors and icons that
should also reach into scopes must target `:root, [data-p-scope]` instead of `:root` alone.

## Overriding tokens

Any token can be overridden in the application CSS. Tokens are named
`--p-<component>-<section>-<token>`, for example:

```css
:root {
    --p-button-border-radius: 2rem;
    --p-form-field-background: var(--p-surface-50);
    --p-datatable-header-cell-background: var(--p-surface-100);
    --p-dialog-header-padding: 1.5rem;
}
```

There are three levels of tokens:

- **primitive** tokens: the palettes (`--p-blue-500`, `--p-surface-200`, ...),
- **semantic** tokens: the meaning (`--p-primary-color`, `--p-form-field-border-color`, `--p-focus-ring-color`, ...),
- **component** tokens: one component (`--p-button-*`, `--p-datatable-*`, ...). They reference the semantic tokens.

Override a semantic token to change all components at once, a component token to change one component.

To find the right token, use the showcase:

- **Token reference** (`/theming-next/tokens.xhtml`): all tokens of the current theme, searchable, with their
  computed values, color swatches and icon previews.
- **Theme Designer** (`/theming-next/designer.xhtml`): edit the main tokens live and copy the generated CSS into
  your application.

## Density

The spacing of forms, lists, tables, menus etc. can be made more compact or more comfortable with the
`data-p-density` attribute:

```xml
<html xmlns="http://www.w3.org/1999/xhtml" data-p-density="compact">   <!-- compact | comfortable -->
```

On another element than `<html>`, `data-p-scope` is needed as well:

```xml
<div data-p-density="compact" data-p-scope="">
    <p:dataTable ...>
</div>
```

## Icons

The icons the components draw themselves (toggler, close, sort, dropdown, pagination, ...) are SVG images defined by
tokens, not glyphs of an icon font. They are drawn in the current text color and sized by the font size.
There are two levels:

```css
/* the icon set: one token per meaning */
--p-icon-close
--p-icon-collapsed
--p-icon-expanded
--p-icon-loading
...

/* per component: --p-<component>-<control>[-<state>]-icon, default: the token of the icon set */
--p-tree-toggle-collapsed-icon: var(--p-icon-collapsed);
--p-tabs-close-icon: var(--p-icon-close);
```

Override them like any other token:

```css
:root { --p-icon-close: url("/myapp/icons/close.svg"); }                    /* everywhere */
:root { --p-tree-toggle-collapsed-icon: url("data:image/svg+xml,..."); }    /* one component */
.sidebar { --p-icon-expanded: url("/myapp/icons/minus.svg"); }             /* one area */
```

- Any SVG works (Font Awesome, PrimeIcons, Tabler, ...); only its shape is used, the color is the text color.
- Use **absolute paths** or **data URIs**. A relative `url()` inside a CSS variable is resolved against the stylesheet
  that uses it (the theme), not the one that declares it.
- Directional icons (arrows, chevrons) are mirrored automatically in RTL, one icon per direction is enough.
- A complete other icon set is a CSS file with all `--p-icon-*` tokens, included after the theme.

The default icon set is [Lucide](https://lucide.dev). The token reference page shows all icon tokens with a preview.

## Motion

Transitions use duration and easing tokens, which can be overridden like any other token:

```css
:root {
    --p-transition-duration: 0.2s;
    --p-transition-easing: cubic-bezier(0.4, 0, 0.2, 1);       /* something moves within the page */
    --p-transition-easing-enter: cubic-bezier(0, 0, 0.2, 1);   /* something appears */
    --p-transition-easing-exit: cubic-bezier(0.4, 0, 1, 1);    /* something disappears */
    --p-overlay-enter-duration: 0.12s;                         /* overlays opening */
    --p-overlay-leave-duration: 0.1s;                          /* overlays closing */
}
```

## User preferences and accessibility

The theme respects the preferences of the user without configuration:

- **Reduced motion** (`prefers-reduced-motion`): transitions and CSS animations are reduced to a minimum.
- **Print**: light colors, no shadows, scrollable tables print all rows, overlays, growl and paginators are hidden.
- **High contrast mode** (Windows forced colors): icons stay visible, focus rings are outlines and are kept.
- **Target size**: small icon controls (chip remove, password toggle, breadcrumb home, ...) get a 24px hit area
  (WCAG 2.2).
- Native controls of the application (checkbox, radio, range, progress) use the primary color, the text selection
  the highlight colors.

Some JavaScript driven animations (e.g. showing or collapsing panels) do not respect reduced motion yet.

## Variants

Some variants are selected with a style class (`styleClass`, `panelStyleClass`):

| Class | On | Effect |
|---|---|---|
| `ui-selectonemenu-sm` / `-lg`, `ui-selectcheckboxmenu-sm` / `-lg`, `ui-cascadeselect-sm` / `-lg` | the component (`styleClass`) | small / large field (like `ui-inputtext-sm` / `ui-button-sm`) |
| `ui-chkbox-sm` / `-lg`, `ui-radiobutton-sm` / `-lg` | the checkbox / radio or any ancestor (e.g. `p:selectManyCheckbox`) | small / large boxes |
| `ui-badge-sm` | p:badge | small badge (`size` has large / xlarge) |
| `ui-select-checkmark` | p:selectOneMenu `panelStyleClass` | check in front of the selected option |
| `ui-listbox-checkmark`, `ui-listbox-striped` | p:selectOneListbox / p:selectManyMenu | check in front of selected options / striped options |
| `ui-message-outlined`, `ui-message-simple`, `ui-message-sm` / `-lg` | p:message, p:messages, p:staticMessage | outlined (border, no background) / simple (text only) / sizes; combinable |
| `severity="secondary"` / `"contrast"` | p:staticMessage | additional severities |
| `ui-tag-contrast`, `ui-badge-contrast` | p:tag, p:badge | contrast severity (also with `outlined` / `rounded`) |
| `ui-float-label-in`, `ui-float-label-on` | the `span.ui-float-label` | label inside the field at the top / label on the border |
| `ui-blockui-light` | p:blockUI | light loading mask with a spinner (default: modal mask) |
| `ui-ajaxstatus-bar` | an element in the start facet of p:ajaxStatus | indeterminate progress bar at the top of the viewport |

```xml
<p:selectOneMenu value="#{bean.city}" styleClass="ui-selectonemenu-sm" panelStyleClass="ui-select-checkmark">
    <f:selectItems value="#{bean.cities}"/>
</p:selectOneMenu>

<p:messages styleClass="ui-message-outlined ui-message-sm"/>

<p:ajaxStatus>
    <f:facet name="start">
        <div class="ui-ajaxstatus-bar"/>
    </f:facet>
</p:ajaxStatus>
```

Without a class, p:blockUI without content shows a centered spinner and p:commandLink shows a spinner while its ajax
request runs.

?> Component attributes for these variants (e.g. `size="small"`, `variant="outlined"`) will follow. They will render
the same style classes, so the classes keep working.

## Custom theme

A custom theme is a CSS file with tokens only, packaged as a resource of your application or library and selected
with `library:name`:

```xml
<context-param>
    <param-name>primefaces.THEME</param-name>
    <param-value>mytheme:mytheme.css</param-value>
</context-param>
```

The file is rendered after `components.css` instead of a preset. Start from a copy of a preset
(`aura.css`, `lara.css`, `nora.css`, `metal.css` or `glass.css` of the `primefaces-theming` jar) or from the CSS of the Theme Designer.

For small changes a custom theme is not needed: override the tokens in the CSS of your application on top of a preset.

### Materials: gradients, texture and depth

The **Metal** preset shows that a theme can go beyond flat colors with tokens only. Background tokens that the
components apply with the `background` shorthand (buttons, form fields, checkbox / radio, toggleSwitch, select buttons,
progressBar, slider knob) also accept gradients and several layers. These tokens add depth (all `none` / `0px` /
`transparent` in Aura, Lara and Nora):

| Token | Effect |
|---|---|
| `--p-button-texture` | layer above the button background, e.g. a brushed grain (an SVG noise image) |
| `--p-button-shadow` | shadow of a button, e.g. a light line below ("machined into the surface") |
| `--p-button-active-shadow`, `--p-button-active-offset` | pressed state: inner shadow and a small downward shift |
| `--p-button-label-shadow`, `--p-button-outlined-label-shadow` | text shadow of the label (engraved look) |
| `--p-button-outlined-background` | background of outlined buttons (transparent by default) |
| `--p-toggleswitch-handle-shadow` | shadow of the toggleSwitch knob |
| `--p-document-background` | page background (`<html>`), e.g. a wallpaper behind glass |
| `--p-overlay-backdrop-filter` | `backdrop-filter` of overlays (dialogs, popovers, dropdowns, menus, tooltips, growl) |
| `--p-navigation-backdrop-filter` | `backdrop-filter` of the toolbar |
| `--p-button-backdrop-filter` | `backdrop-filter` of buttons |
| `--p-mask-backdrop-filter` | `backdrop-filter` of the modal mask |

The **Glass** preset uses the backdrop tokens with translucent backgrounds (`blur(30px) saturate(180%)` for overlays).
Content containers deliberately get no backdrop filter: it would make them the containing block of overlays inside
them.

Two techniques the Metal preset uses:

- **An edge lit from above**: a face layer clipped to the padding box and an edge layer clipped to the border box, in
  one token; the border color is `transparent` so the edge layer shows through (and the forced colors mode of Windows
  still draws a border).
- **One token for light and dark**: `light-dark()` only takes colors, so the gradients use it in their color stops.
  Mixing a color with itself keeps it, which switches a gradient direction per mode:
  `color-mix(in oklch, B, light-dark(black, B) 22%)` darkens only in light mode.

```css
/* brass buttons: a warm face with a lighter edge on top */
:root {
    --p-button-primary-background:
        linear-gradient(180deg, #e2c27a, #b88a2e 55%, #8a6420) padding-box,
        linear-gradient(180deg, #f3dfa8, #6b4c16) border-box;
    --p-button-primary-hover-background:
        linear-gradient(180deg, #ebcd8a, #c4963a 55%, #966e26) padding-box,
        linear-gradient(180deg, #f3dfa8, #6b4c16) border-box;
    --p-button-primary-border-color: transparent;
    --p-button-primary-hover-border-color: transparent;
    --p-button-primary-color: #2b1d05;
    --p-button-shadow: inset 0 1px 0 rgb(255 255 255 / 0.4), 0 1px 2px rgb(0 0 0 / 0.25);
    --p-button-active-offset: 1px;
}
```

?> Tokens that are also used as plain colors (e.g. `--p-content-background`, `--p-primary-color`) must stay colors.
Check the text contrast against every color stop of a gradient (WCAG AA: 4.5:1).

### Cascade layers

The complete theme lives in CSS cascade layers (`primefaces.tokens`, `primefaces.vendor`, `primefaces.components`,
`primefaces.behaviors`). Application CSS outside of a layer always wins over the theme, regardless of specificity and
without `!important`:

```css
/* wins over the theme, although the selector is simple */
.ui-button {
    text-transform: uppercase;
}
```

Prefer tokens where one exists; style rules are the fallback for everything tokens do not cover.

### Third-party CSS

The CSS of the libraries the components use (jQuery UI, Coloris, cropperjs, Quill, vis-timeline, FullCalendar, ...)
is included in `components.css` and styled with the tokens; there is nothing extra to include.

### Compression

PrimeFaces does not compress resources. Enable gzip or brotli in your server or proxy: `components.css` is about
540 KB raw and about 77 KB compressed (plus about 24 KB compressed for the preset).

## Design tokens export

The tokens of each preset are exported in the [W3C Design Tokens format](https://www.designtokens.org/) (DTCG), one
file per preset and mode (`aura.light.tokens.json`, `aura.dark.tokens.json`, ...). They are generated by the build of
`primefaces-theming` into `target/tokens`. Import them into design tools like Figma (Tokens Studio) or use them with
Style Dictionary to keep design and implementation in sync. References between tokens are kept as aliases. Values the format has no type for (gradients and layered backgrounds of Metal, icon urls,
shorthands) are exported as CSS strings, resolved for the mode of the file.

## Compatibility

The new theming is **intentionally breaking**, it is not a drop-in replacement for the legacy themes and there is no
compatibility layer. Switch to it deliberately:

- The legacy themes (`primefaces-themes`, saga, arya, ...) keep working unchanged.
- The CSS of the PrimeFaces core is not loaded with a token theme. Application CSS that overrides PrimeFaces styles
  by selector (written against the core CSS or a legacy `theme.css`) may need adjustments; customize with tokens
  instead.
- There is no SASS: colors, radius, density, icons etc. are CSS variables.
- Icons drawn by the components come from icon tokens, not from PrimeIcons or Font Awesome classes.
- Canvas based components (p:chart, p:knob) take their colors from their JavaScript options, not from the tokens.

Further breaking changes in the components (icon attributes, `ui-*` to `p-*` style classes, ...) are listed in the
[migration guide](../migrationguide/migrationguide.md) when they are done.

## Browser requirements

The theme uses `light-dark()`, `color-mix()`, relative colors, `:has()` and container queries and requires:

- Chrome / Edge 123+
- Firefox 128+
- Safari 18+
