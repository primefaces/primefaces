# Theming

The themes of PrimeFaces are based on **design tokens**: every color, radius, spacing, shadow, icon and
duration is a CSS variable (`--p-primary-color`, `--p-form-field-background`, `--p-button-border-radius`, ...).
The look of an application is customized with plain CSS: no SASS, no build step, no theme to fork.

Five themes are built in:

- **Aura**
- **Lara**
- **Nora**
- **Metal**: graphite and brushed aluminium (light), black metal (dark), an orange focus ring
- **Glass**: Apple Liquid Glass style, translucent overlays, menus, bars and buttons over a calm content layer

Aura, Lara and Nora started from [PrimeUIX](https://github.com/primefaces/primeuix) (the theming of PrimeReact / PrimeVue),
so the token names follow the same convention. They are maintained for PrimeFaces and are not kept in sync with PrimeUIX.

Light and dark mode are built in, each preset supports both.

?> PrimeFaces 17 replaced the SASS based themes (saga, arya, vela, the `primefaces-themes` jar, ...) with these themes.
See [Migration from the SASS themes](#migration-from-the-sass-themes).

## Configuration

The themes are part of the `primefaces` jar. Select one with the `primefaces.THEME` context parameter:

```xml
<context-param>
    <param-name>primefaces.THEME</param-name>
    <param-value>aura</param-value>   <!-- aura (default) | lara | nora | metal | glass | none -->
</context-param>
```

The value can be an EL expression, e.g. `#{userSettings.theme}` to switch the theme per user. `none` renders no theme
(the components are unstyled then). A [custom theme](#custom-theme) is selected by its name the same way.

### What is rendered

- `primefaces-<theme>:theme.css`, first in the head: the tokens of the theme.
- `primefaces:components.css`: the styles of the components, the icons and the jQuery UI CSS. It is the same for every
  theme, so it stays cached when the theme changes.
- the CSS of components that are loaded on demand together with their JavaScript, e.g.
  `primefaces:datepicker/datepicker.css`, `schedule/schedule.css` (incl. the FullCalendar CSS),
  `texteditor/texteditor.css` (Quill) or `timeline/timeline.css` (vis-timeline).

PrimeIcons are still loaded (unless `primefaces.PRIME_ICONS` is `false`) for the icons the application sets itself,
e.g. `<p:commandButton icon="pi pi-check"/>`. The icons the components draw themselves come from the icon tokens (in
`components.css`), see [Icons](#icons).

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

!> Inside a `[data-p-scope]` element the theme declares the tokens that reference other tokens again (colors, most
component tokens), so they are recalculated there. Global overrides of such tokens that should also reach into scopes
must target `:root, [data-p-scope]` instead of `:root` alone. `--p-primary-base` and tokens with a literal value (sizes,
paddings, the icons of the icon set) are declared on `:root` only, an override on `:root` reaches into scopes.

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

- **Token reference** (`/theming/tokens.xhtml`): all tokens of the current theme, searchable, with their
  computed values, color swatches and icon previews.
- **Theme configurator** (palette button at the right edge of every showcase page): switch the preset, edit the main
  tokens live and copy the generated CSS into your application.

## Density

The spacing of forms, lists, tables, menus etc. can be made more compact or more comfortable with the
`data-p-density` attribute:

```xml
<html xmlns="http://www.w3.org/1999/xhtml" data-p-density="compact">   <!-- compact | comfortable -->
```

It works on any element, also nested (the nearest `data-p-density` wins) and inside scopes; `normal` restores the
spacing of the theme inside a compact or comfortable area:

```xml
<div data-p-density="compact">
    <p:dataTable ...>
    <div data-p-density="normal">...</div>
</div>
```

Inside a `data-p-density` element the theme declares the tokens that depend on the spacing again (e.g.
`--p-button-padding-x: var(--p-form-field-padding-x)`), so they follow the density. Global overrides of such tokens that
should also apply there must target `:root, [data-p-scope], [data-p-density]`; all other tokens are not touched by the
density.

## Icons

There are two kinds of icons:

- **Icons the application sets**, e.g. `<p:commandButton icon="pi pi-check"/>` or `<p:menuitem icon="fa fa-home"/>`:
  plain CSS classes of an icon library (PrimeIcons, Font Awesome, ...). They are not part of the theme and work as
  before.
- **Icons the components draw themselves**: toggler, close, sort, dropdown, pagination, checkbox check, loading, ...
  They come from **icon tokens** (the icon set in `components.css`) and are configured with CSS only.

The rest of this section is about the second kind.

### How they are drawn

Every component icon is an SVG image in a token. The component shows it as a mask:

```css
.ui-panel .ui-icon-closethick::before {
    content: "\2003";                                        /* 1em wide */
    background: currentColor;                                /* the color */
    mask: var(--p-panel-close-icon) center / contain no-repeat;   /* the shape */
}
```

So:
- **Color:** the icon has the text color of its element (`currentColor`), it follows the color tokens of the component
  (hover, active, disabled) and the color scheme. Only the shape of the SVG is used; its own colors are ignored.
- **Size:** the font size of its element (`1em`). Many components have size tokens for it, e.g.
  `--p-checkbox-icon-size`, `--p-chip-remove-icon-size`, `--p-avatar-icon-size`.
- No icon font is loaded for them and they need no extra request: the default icons are inlined in `components.css`.

### Two levels of tokens

```css
/* 1. the icon set: one token per meaning, used by all components */
--p-icon-close: url("data:image/svg+xml,...");
--p-icon-collapsed: url(...);
--p-icon-expanded: url(...);

/* 2. per component: --p-<component>-<control>[-<state>]-icon, by default a token of the icon set */
--p-panel-close-icon: var(--p-icon-close);
--p-tree-toggle-collapsed-icon: var(--p-icon-collapsed);
--p-tree-toggle-expanded-icon: var(--p-icon-expanded);
--p-datatable-sort-asc-icon: var(--p-icon-sort-asc);
```

Change the icon set token to change an icon in all components, the component token to change it in one component.
The **Theming Tokens** page of the showcase lists all icon tokens with a preview (search for `icon`).

The icon set (default: [Lucide](https://lucide.dev), ISC license):

| Token | Meaning | Token | Meaning |
|---|---|---|---|
| `--p-icon-close` | close, remove | `--p-icon-check` | checked, selected |
| `--p-icon-collapsed` / `-expanded` | toggler (tree, accordion, row) | `--p-icon-indeterminate` | partially checked |
| `--p-icon-dropdown` | dropdown trigger | `--p-icon-clear` | clear a field |
| `--p-icon-up` / `-down` | spinner, move | `--p-icon-search` | search, filter |
| `--p-icon-prev` / `-next` | navigate back / forward | `--p-icon-first` / `-last` | first / last page |
| `--p-icon-top` / `-bottom` | move to top / bottom | `--p-icon-arrow-up` / `-down` / `-left` / `-right` | arrows |
| `--p-icon-sort` / `-sort-asc` / `-sort-desc` | column sorting | `--p-icon-loading` | loading spinner (rotates) |
| `--p-icon-plus` / `-minus` | add / remove, zoom | `--p-icon-zoom-in` / `-zoom-out` | zoom |
| `--p-icon-maximize` / `-restore` | dialog maximize | `--p-icon-show` / `-hide` | password visibility |
| `--p-icon-edit` / `-trash` | row edit / delete | `--p-icon-options` | menu, options |
| `--p-icon-calendar` | date picker | `--p-icon-home` | breadcrumb home |
| `--p-icon-file` | file upload | `--p-icon-star` / `-star-filled` | rating |
| `--p-icon-info` / `-success` / `-warn` / `-error` / `-fatal` | message severities | | |

### Overriding icons

Icon tokens are overridden like any other token, in unlayered application CSS:

```css
/* everywhere */
:root, [data-p-scope] {
    --p-icon-close: url("/myapp/resources/icons/close.svg");
}

/* one component */
:root, [data-p-scope] {
    --p-tree-toggle-collapsed-icon: url("/myapp/resources/icons/plus.svg");
    --p-tree-toggle-expanded-icon: url("/myapp/resources/icons/minus.svg");
}

/* one area of the page */
.sidebar {
    --p-icon-expanded: url("/myapp/resources/icons/chevron-up.svg");
}
```

From a JSF resource, with the resource EL in a stylesheet loaded by `h:outputStylesheet`:

```css
:root, [data-p-scope] {
    --p-icon-close: url("#{resource['myapp:icons/close.svg']}");
}
```

Inline as data URI (no request; `#` must be encoded as `%23`, use single quotes inside):

```css
:root, [data-p-scope] {
    --p-icon-check: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 24 24'%3E%3Cpath d='M20 6 9 17l-5-5' fill='none' stroke='black' stroke-width='2'/%3E%3C/svg%3E");
}
```

Good to know:
- **Any SVG works:** Font Awesome, PrimeIcons, Tabler, Material Symbols, Heroicons, ... all ship their icons as SVG
  files. Outline and filled icons both work; the color does not matter (only the shape is used).
- **Use absolute paths or data URIs.** A relative `url()` inside a CSS variable is resolved against the stylesheet that
  *uses* the variable (the theme resource), not against the stylesheet that declares it.
- **`:root` or `:root, [data-p-scope]`:** the `--p-icon-*` tokens are declared on `:root` only, so overriding them on
  `:root` reaches every scope. Component icon tokens (`--p-tree-toggle-collapsed-icon`, ...) reference the icon set and
  are declared again inside a `data-p-scope` element: override them on `:root, [data-p-scope]`.
- **Size and color** are not part of the icon: change them on the element or with the size / color tokens of the
  component.
- **Directional icons** (arrows, chevrons, first / last, prev / next) are mirrored automatically in right-to-left
  pages, one icon per direction is enough.
- **High contrast mode** (Windows forced colors): the icons are drawn in the forced text color and stay visible.
- **Loading icons** rotate (one animation for all spinners); with `prefers-reduced-motion` they stand still.
- **Hiding an icon:** do not set the token to `none` (without a mask the whole 1em box is painted); hide the element
  with CSS instead, e.g. `.my-panel .ui-panel-titlebar-icon { display: none; }`.

### Another icon set

A complete other icon set is a CSS file that sets all `--p-icon-*` tokens, included after the theme. The component
tokens reference the set, so they follow automatically:

```css
/* resources/myapp/icons.css */
:root, [data-p-scope] {
    --p-icon-close: url("/myapp/resources/icons/x.svg");
    --p-icon-collapsed: url("/myapp/resources/icons/chevron-right.svg");
    --p-icon-expanded: url("/myapp/resources/icons/chevron-down.svg");
    /* ... all --p-icon-* tokens of the table above */
}
```

```xml
<h:outputStylesheet library="myapp" name="icons.css"/>
```

### Font icons instead of SVG

The tokens take images. To draw a component icon with an icon font, override the `::before` of the icon element:

```css
.ui-panel .ui-icon-closethick::before {
    mask: none;
    background: none;
    content: "\f00d";
    font-family: "Font Awesome 6 Free";
    font-weight: 900;
}
```

!> The selectors of the icon elements are the legacy jQuery UI icon classes (`ui-icon-closethick`, ...) for now. They
will be replaced by semantic icon classes named like the tokens (`p-panel-close-icon`), see the
[migration guide](../migrationguide/migrationguide.md) when they are done.
Prefer the tokens; they keep working.

### Icon attributes of the components

Attributes that set an icon a component draws itself (e.g. `p:panel closeIcon`, `p:rowToggler expandIcon`,
`p:spinner upIcon`, `p:chip removeIcon`) will be removed without replacement (planned, breaking): use the icon tokens.
Attributes that set content chosen per instance stay, e.g. `icon` of buttons and menu items or the node icons of
`p:tree`.

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

A custom theme is a CSS file with the tokens of a theme, packaged as `META-INF/resources/primefaces-<name>/theme.css`
in your application or a jar and selected by its name:

```xml
<context-param>
    <param-name>primefaces.THEME</param-name>
    <param-value>mytheme</param-value>   <!-- renders primefaces-mytheme:theme.css -->
</context-param>
```

Start from a copy of a built-in theme (`primefaces-aura/theme.css`, ... in the `primefaces` jar, the sources are in
`primefaces/src/main/frontend/themes`) or from the CSS of the theme configurator of the showcase. Keep the tokens in
the `primefaces.tokens` cascade layer, start the file with the layer order and declare the tokens like the built-in
themes do: literal values on `:root` (they inherit into scopes and density areas), tokens that reference other tokens on
`:root, [data-p-scope]` (recalculated inside a scope), and those that depend on the spacing also on `[data-p-density]`:

```css
@layer primefaces.tokens, primefaces.vendor, primefaces.components, primefaces.behaviors;

@layer primefaces.tokens {
    :root {
        --p-primary-base: #0a7d5a;
        --p-form-field-padding-x: 0.75rem;
        /* ... */
    }

    :root, [data-p-scope] {
        --p-primary-color: light-dark(var(--p-primary-500), var(--p-primary-400));
        /* ... */
    }

    :root, [data-p-scope], [data-p-density] {
        --p-button-padding-x: var(--p-form-field-padding-x);
        /* ... */
    }
}
```

For small changes a custom theme is not needed: override the tokens in the CSS of your application on top of a
built-in theme.

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

The CSS of the libraries the components use is part of the PrimeFaces stylesheets, in the `primefaces.vendor` layer
below the component styles, and styled with the tokens; there is nothing extra to include. It is loaded with the
component that needs it: jQuery UI in `components.css` (its datepicker CSS with p:calendar), Coloris with
p:colorPicker, cropperjs with p:imageCropper, Quill with p:textEditor, vis-timeline with p:timeline, FullCalendar with
p:schedule, jsPlumb with p:diagram.

### Compression

PrimeFaces does not compress resources. Enable gzip or brotli in your server or proxy: `components.css` is about
360 KB minified and about 47 KB compressed, a theme about 210 KB minified and 25 KB compressed.

## Tailwind CSS

The theme works together with Tailwind CSS v4: the utilities (`bg-primary`, `text-muted-color`, `rounded-border`,
...) use the tokens of the preset. See [Tailwind CSS](/core/tailwind.md) for the setup, the token mapping and a sample.

## Design tokens export

The tokens of each preset are exported in the [W3C Design Tokens format](https://www.designtokens.org/) (DTCG), one
file per preset and mode (`aura.light.tokens.json`, `aura.dark.tokens.json`, ...). They are published next to the jar
as `primefaces-<version>-tokens.zip` (Maven `org.primefaces:primefaces:<version>:zip:tokens`). Import them into design
tools like Figma (Tokens Studio) or use them with Style Dictionary to keep design and implementation in sync.
References between tokens are kept as aliases. Values the format has no type for (gradients and layered backgrounds of
Metal, icon urls, shorthands) are exported as CSS strings, resolved for the mode of the file.

## Migration from the SASS themes

The SASS based themes (saga, arya, vela, nova, luna, bootstrap4, material, the old aura, ...) and the
`primefaces-themes` jar were removed in PrimeFaces 17. The design token themes replace them, there is no compatibility
layer:

- Remove the `primefaces-themes` dependency and select one of the themes above. Without `primefaces.THEME` the default
  is `aura`.
- A custom SASS theme (`primefaces-<name>/theme.css` built with the theme designer) does not work anymore: the
  components need the tokens of a theme. Start a [custom theme](#custom-theme) from a built-in one or override tokens.
- Application CSS that overrides PrimeFaces styles by selector (written against the old core CSS or a `theme.css`) may
  need adjustments; customize with tokens instead. All PrimeFaces CSS lives in cascade layers, so unlayered application
  CSS wins without `!important`.
- An unknown theme name (e.g. `saga`, `nova-light`, `aura-light-emerald`) renders a stylesheet that does not exist and
  the components have no tokens. In the `Development` project stage PrimeFaces logs a warning.
- Dark mode is the CSS `color-scheme` of the page (see [Light and dark mode](#light-and-dark-mode)), not a separate
  theme (`arya`, `vela`, `*-dark`).
- **The default follows the operating system** (`color-scheme: light dark`): after the upgrade, users with a dark OS
  setting get dark components. Applications whose own CSS is light only set `:root { color-scheme: light; }`.
- The CSS variables of the SASS themes (`--primary-color`, `--primary-color-text`, `--surface-a` ... `--surface-f`,
  `--surface-ground`, `--surface-border`, `--surface-0` ... `--surface-900`, `--text-color`, `--text-color-secondary`,
  `--font-family`, `--border-radius`, `--focus-ring`, `--maskbg`, the palettes like `--blue-500`, ...) are gone. Use the
  tokens (`--p-primary-color`, `--p-surface-500`, `--p-blue-500`, ...) or map the old names in application CSS:

```css
:root {
    --primary-color: var(--p-primary-color);
    --primary-color-text: var(--p-primary-contrast-color);
    --text-color: var(--p-text-color);
    --text-color-secondary: var(--p-text-muted-color);
    --surface-a: var(--p-content-background);
    --surface-ground: light-dark(var(--p-surface-50), var(--p-surface-950));
    --surface-border: var(--p-content-border-color);
    --surface-hover: var(--p-content-hover-background);
    --border-radius: var(--p-border-radius-md);
    --maskbg: var(--p-mask-background);
}
```

- Icons drawn by the components come from icon tokens, not from PrimeIcons or Font Awesome classes.
- Canvas based components (p:chart, p:knob) take their colors from their JavaScript options, not from the tokens.
- The browser requirements are higher, see [Browser requirements](#browser-requirements).

Further breaking changes in the components (icon attributes, `ui-*` to `p-*` style classes, ...) are listed in the
[migration guide](../migrationguide/migrationguide.md) when they are done.

## Browser requirements

The theme uses `light-dark()`, `color-mix()`, relative colors, `:has()` and CSS nesting and requires:

- Chrome / Edge 123+
- Firefox 128+
- Safari 18+

Themed scrollbars (`scrollbar-width`, `scrollbar-color`) need Safari 18.2, older versions show the default scrollbars.
