# Tailwind CSS

The [design token theming](/core/theming.md) works together with [Tailwind CSS](https://tailwindcss.com/) v4:
Tailwind for the layout of the page, PrimeFaces for the components, both with the colors, radius and shadows of the
PrimeFaces preset. Switching the preset, the primary color or light / dark mode changes both at once, at runtime,
without rebuilding the CSS.

A complete Maven project (Jetty, Tailwind built by Maven, no local Node needed) is the
[tailwind sample](https://github.com/primefaces/primefaces-samples/tree/master/tailwind) in `primefaces-samples`.

?> Tailwind CSS v4 is required (cascade layers, `@theme inline`, `@source inline`). The legacy themes (saga, arya, ...)
are not supported.

## Setup

### 1. Build Tailwind

Tailwind compiles a CSS file from your sources; the result is a normal JSF resource. With the Tailwind CLI:

```bash
npm install -D tailwindcss @tailwindcss/cli
npx @tailwindcss/cli -i src/main/tailwind/app.css -o src/main/webapp/resources/app/tailwind.css --minify
```

In a Maven build, run it with the [frontend-maven-plugin](https://github.com/eirslett/frontend-maven-plugin) in the
`generate-resources` phase (see the sample). During development use `--watch` instead of `--minify`.

### 2. The input file

`src/main/tailwind/app.css`:

```css
@import "tailwindcss";

/* colors, radius, shadows and font from the PrimeFaces tokens, see below */
@import "./primefaces.css";

/* where Tailwind looks for class names: your Facelets (path relative to this file) */
@source "../webapp";
```

### 3. The layer order

PrimeFaces puts its theme into the cascade layer `primefaces` (`primefaces.tokens`, `primefaces.components`, ...),
Tailwind into `theme`, `base`, `components` and `utilities`. The first statement that names the layers fixes their
order, so declare it in the head before any stylesheet:

```xml
<h:head>
    <f:facet name="first">
        <style>@layer theme, base, primefaces, components, utilities;</style>
    </f:facet>
    <h:outputStylesheet library="app" name="tailwind.css"/>
</h:head>
```

| Layer | Content | Effect |
|-------|---------|--------|
| `theme` | Tailwind's variables | |
| `base` | Tailwind's reset ("preflight") | below PrimeFaces: does not reset the components |
| `primefaces` | the PrimeFaces theme | |
| `components` | your `@apply` classes | above the theme |
| `utilities` | Tailwind utilities | win over the theme: `<p:commandButton styleClass="rounded-full shadow-md"/>` |
| (no layer) | your plain CSS | wins over everything, without `!important` |

!> Without this statement the layers are ordered by first appearance, and depending on the order of the stylesheets
Tailwind's reset can end up above the theme and break the components.

## Token mapping

Save this as `src/main/tailwind/primefaces.css`. `@theme inline` makes the utilities use `var(--p-...)` directly, so
they follow the preset, a [scoped primary color](/core/theming.md#scoped-overrides) and light / dark mode at runtime.
The semantic colors switch between light and dark by themselves, no `dark:` needed.

The names follow [tailwindcss-primeui](https://github.com/primefaces/tailwindcss-primeui) of PrimeReact and PrimeVue,
so the same utilities work in all Prime libraries. Severity, form field, shadow, radius scale and motion are additions
of PrimeFaces.

```css
@theme inline {
    /* primary */
    --color-primary: var(--p-primary-color);
    --color-primary-emphasis: var(--p-primary-hover-color);
    --color-primary-contrast: var(--p-primary-contrast-color);
    --color-primary-50: var(--p-primary-50);
    --color-primary-100: var(--p-primary-100);
    --color-primary-200: var(--p-primary-200);
    --color-primary-300: var(--p-primary-300);
    --color-primary-400: var(--p-primary-400);
    --color-primary-500: var(--p-primary-500);
    --color-primary-600: var(--p-primary-600);
    --color-primary-700: var(--p-primary-700);
    --color-primary-800: var(--p-primary-800);
    --color-primary-900: var(--p-primary-900);
    --color-primary-950: var(--p-primary-950);

    /* surface (the gray scale of the preset) */
    --color-surface-0: var(--p-surface-0);
    --color-surface-50: var(--p-surface-50);
    --color-surface-100: var(--p-surface-100);
    --color-surface-200: var(--p-surface-200);
    --color-surface-300: var(--p-surface-300);
    --color-surface-400: var(--p-surface-400);
    --color-surface-500: var(--p-surface-500);
    --color-surface-600: var(--p-surface-600);
    --color-surface-700: var(--p-surface-700);
    --color-surface-800: var(--p-surface-800);
    --color-surface-900: var(--p-surface-900);
    --color-surface-950: var(--p-surface-950);

    /* semantic */
    --color-surface: var(--p-content-border-color);
    --color-content: var(--p-content-background);
    --color-emphasis: var(--p-content-hover-background);
    --color-highlight: var(--p-highlight-background);
    --color-highlight-emphasis: var(--p-highlight-focus-background);
    --color-highlight-contrast: var(--p-highlight-color);
    --color-color: var(--p-text-color);
    --color-muted-color: var(--p-text-muted-color);

    /* severity */
    --color-success: var(--p-message-success-color);
    --color-info: var(--p-message-info-color);
    --color-warn: var(--p-message-warn-color);
    --color-danger: var(--p-message-error-color);

    /* form fields */
    --color-field: var(--p-form-field-background);
    --color-field-border: var(--p-form-field-border-color);
    --color-placeholder: var(--p-form-field-placeholder-color);

    /* radius */
    --radius-border: var(--p-content-border-radius);
    --radius-xs: var(--p-border-radius-xs);
    --radius-sm: var(--p-border-radius-sm);
    --radius-md: var(--p-border-radius-md);
    --radius-lg: var(--p-border-radius-lg);
    --radius-xl: var(--p-border-radius-xl);

    /* depth */
    --shadow-card: var(--p-card-shadow);
    --shadow-overlay: var(--p-overlay-popover-shadow);
    --shadow-modal: var(--p-overlay-modal-shadow);

    /* motion */
    --default-transition-duration: var(--p-transition-duration);

    --font-sans: var(--p-typography-font-family);
}
```

### Utilities

| Utilities | Token | Use |
|-----------|-------|-----|
| `bg-primary`, `text-primary`, `border-primary`, ... | `--p-primary-color` | primary color |
| `text-primary-contrast` | `--p-primary-contrast-color` | text on `bg-primary` |
| `bg-primary-emphasis` | `--p-primary-hover-color` | hover of primary |
| `bg-primary-50` ... `bg-primary-950` | `--p-primary-50` ... | primary palette |
| `bg-surface-0` ... `bg-surface-950` | `--p-surface-0` ... | gray scale of the preset |
| `bg-content` | `--p-content-background` | cards, panels |
| `border-surface` | `--p-content-border-color` | borders |
| `bg-emphasis` | `--p-content-hover-background` | hover, subtle areas |
| `bg-highlight`, `text-highlight-contrast`, `bg-highlight-emphasis` | `--p-highlight-*` | selection |
| `text-color`, `text-muted-color` | `--p-text-color`, `--p-text-muted-color` | text |
| `text-success`, `text-info`, `text-warn`, `text-danger` | `--p-message-*-color` | status |
| `bg-field`, `border-field-border`, `placeholder:text-placeholder` | `--p-form-field-*` | own inputs |
| `rounded-border` | `--p-content-border-radius` | radius of cards and panels |
| `rounded-xs` ... `rounded-xl` | `--p-border-radius-xs` ... | radius scale |
| `shadow-card`, `shadow-overlay`, `shadow-modal` | `--p-card-shadow`, `--p-overlay-*-shadow` | depth |
| `transition-*` | `--p-transition-duration` | default duration (0s when animations are off) |
| `font-sans` | `--p-typography-font-family` | font |

The radius scale has the same steps as Tailwind's defaults, so `rounded-md` keeps its meaning and follows the radius
of the theme.

### Any other token

Component tokens are not mapped: they belong to the theme, and mapping them would add hundreds of utilities. Use
Tailwind's variable shorthand for the few cases you need one:

```xml
<div class="bg-(--p-datatable-header-cell-background) text-(--p-datatable-header-cell-color)">...</div>
```

## Building blocks

Reusable classes go into Tailwind's `components` layer: above the theme, below the utilities, so `class="card mt-6"`
still works.

```css
@layer components {
    .card {
        @apply bg-content text-color border border-surface rounded-border shadow-card p-6;
    }
}
```

## Dark mode

PrimeFaces switches light / dark with `color-scheme` (the tokens use `light-dark()`), see
[Light and dark mode](/core/theming.md#light-and-dark-mode). The mapped utilities follow by themselves.

Tailwind's `dark:` variant reacts to `prefers-color-scheme` only. For fixed Tailwind palette colors that must change
with the mode of your application, bind it to the attribute your application sets together with `color-scheme`:

```css
@custom-variant dark (&:where([data-dark="true"], [data-dark="true"] *));
```

```xml
<div class="bg-amber-100 text-amber-900 dark:bg-amber-950 dark:text-amber-200">...</div>
```

## Things to know

### Use plain HTML for the layout

Layout is Tailwind's job: `<div class="grid grid-cols-1 gap-6 lg:grid-cols-4">` with the components inside.
`h:panelGroup` or `p:panelGrid` are not needed for layout.

```xml
<div class="grid grid-cols-1 gap-4 md:grid-cols-2">
    <div class="flex flex-col gap-2 md:col-span-2">
        <p:outputLabel for="customer" value="Customer"/>
        <p:inputText id="customer" value="#{order.customer}" styleClass="w-full"/>
    </div>
    ...
</div>
```

### Class names must be complete

Tailwind scans the files for class names, it does not run them. A name built at runtime is not found:

```xml
<!-- not found: Tailwind never sees "col-span-2" -->
<div class="col-span-#{bean.span}">

<!-- found -->
<div class="#{bean.wide ? 'col-span-2' : 'col-span-1'}">
```

List names you cannot write out (and names rendered in Java) with `@source inline` in `app.css`, brace expansion
included:

```css
@source inline("col-span-{1..12}");
```

### p:panelGrid layout="tailwind"

The renderer writes `grid-cols-*` / `col-span-*` in Java, so they are in no Facelet. Add them to `app.css`:

```css
@source inline("grid gap-4 {,sm:,lg:}grid-cols-{1..12} col-span-{1..11} col-span-full row-span-{1..6}");
```

Each cell must be one component, e.g. `p:column`: Facelets merges consecutive plain HTML into a single child, which
becomes a single cell. Remove the panel look with `styleClass="ui-panelgrid-blank"`, the padding of the grid and its
cells with `contentStyleClass="p-0 *:p-0"`.

### Utilities on components

`styleClass` goes to the root element of a component. For components that wrap an input (`p:inputNumber`,
`p:datePicker`, ...), `w-full` sizes the wrapper; use `inputStyleClass` for the input itself. Size `p:dialog` with
its `width` attribute: the widget positions the dialog itself, size utilities on it fight with that.

### Comments in Facelets

HTML comments become components unless `jakarta.faces.FACELETS_SKIP_COMMENTS` is `true`. In a `p:panelGrid` such a
component is an (empty) cell.
