# JavaScript API doc generator

This directory contains the script for generating the typescript declaration
file and the typedocs for the JavaScript part of PrimeFaces.

It

1. parses the JavaScript files,
1. extracts the available widgets and their methods,
1. gathers type information from the doc comments,
1. validates that there are no obvious mistakes such as undocumented returns,
1. creates a type declarations file, and
1. generates the JavaScript API documentation as HTML pages.

## Contents

- [Build](#build)
- [Publish to npm](#publish-to-npm)
- [Technical overview](#technical-overview)
- [Documenting a widget](#documenting-a-widget)
    * [Basic widget documentation](#basic-widget-documentation)
        + [Methods](#methods)
        + [Widget header](#widget-header)
        + [Properties](#properties)
        + [Widget configuration](#widget-configuration)
- [Advanced documenting](#advanced-documenting)
    * [Excluding source files](#excluding-source-files)
    * [Additional type declarations](#additional-type-declarations)
    * [Namespaces](#namespaces)
    * [DefinitelyTyped](#definitelytyped)
    * [Overriding a method](#overriding-a-method)
    * [Typedef](#typedef)
    * [Interfaces](#interfaces)
    * [Type parameters](#type-parameters)
    * [Optional parameters and initializers](#optional-parameters-and-initializers)
    * [Destructuring](#destructuring)
    * [Asynchronous methods and generators](#asynchronous-methods-and-generators)
- [Style and conventions](#style-and-conventions)
    * [Typescript types](#typescript-types)
    * [Doc comments](#doc-comments)
    * [Private and public methods](#private-and-public-methods)

# Build

Building is integrated into the maven build process, just run

```sh
mvn clean install -P jsdoc
```

This will generate the type declaration file `target/type-declaration/` and the
API docs in `target/type-declaration/jsdocs`.  It will also lint the
declarations file for common mistakes and errors.

Use the profile `jsdoc-update` instead to to create the JavaScript API
documentation in the `docs/jsdocs` directory:

```sh
mvn clean install -P jsdoc-update
```

__Remember to check the files and upload to github afterwards.__

Use `jsdoc-update` when you wish to update the docs without a release. The docs
are also generated in `docs/jsdocs` when you add the profile `release` profile.
If you run the release command locally on your machine, you only need the
`release` profile, just remember to upload the generated files to github
afterwards.

```sh
mvn clean install -P release
```

To configure how the JavaScript API docs and type declarations file is
generated, modify the `jsdocs` entry in the
`src/main/type-definitions/package.json` file. See the CLI program below for a
detailed explanation of the available options.

---

The maven build uses the
[maven frontend plugin](https://github.com/eirslett/frontend-maven-plugin) to
install a local version of `node.js` in the `target` directory.

If you are working on the scripts for generating the type declaration file, or
want to generate the type declarations file quickly without maven overhead, you
can build directly via npm and node. To do so, navigate to the type definitions
directory and run:

```bash
cd src/main/type-definitions
npm install

# All JavaScript files are annotated with types in the doc comments
# This runs TypeScript on the files and validates them
npm run validate

# There are some tests in the specs directory. This runs all tests.
npm run test

# Run just a specific test
node test/index.js -- TsValidateTest --verbose
# Run just a specific sub test of a test
node test/index.js -- "TsValidateTest#ValidationError" --verbose

# Generates the type declaration file and the API documentation via typedoc
# Writes output to the given directory.
# For local development, use some path outside this project.
npm run generate-d-ts -- \
  --declarationoutputdir /path/to/temporary/output/directory \
  --typedocoutputdir /path/to/temporary/output/directory \
  --packagejson ./package.json
```

To see a list of all available options, run 

```bash
npm run generate-d-ts -- --help
```

# Publish to npm

The type declarations are published to npm when you run `mvn deploy -P release`.
The major and minor version is taken from the `pom.xml`, the patch version is
determined automatically by searching for the next free version. 

To authorize, you need to provide either your npm token or a username, password,
and email (in case you do not provide credentials, publishing is skipped):

```bash
# Using an npm token
mvn deploy -P release "-Dnpm.token=token"

# Using a username, password, and email
mvn deploy -P release \
  "-Dnpm.username=username" \
  "-Dnpm.password=password" \
  "-Dnpm.email=email@example.com"
```

* Other settings such as the package name etc. are taken form the file
  `src/main/type-definitions/package.json`.
* The `LICENSE` and `src/main/type-definitions/NPM.md` are included as well.

To manually publish the current type declarations to npm, run

```bash
mvn \
  org.codehaus.mojo:build-helper-maven-plugin:parse-version \
  com.github.eirslett:frontend-maven-plugin:npm@publish-to-npm \
  "-Dnpm.token=token"

# you can also just use a profile, but this compiles Java etc. and takes longer
mvn package -P jsdoc-publish -DskipTests "-Dnpm.token=token"
```

To publish with a different major and minor version, run

```bash
mvn com.github.eirslett:frontend-maven-plugin:npm@publish-to-npm \
  -DparsedVersion.majorVersion=9 \
  -DparsedVersion.minorVersion=0 \
  "-Dnpm.token=token"
```

Finally, you can also run the publish tool directly via node:

```bash
cd src/main/type-definitions
npm install

# Show all available options
npm run npm-publish -- --help

# Don't publish, just write the tarball that would
# be published to "/tmp/package.tgz"
npm run npm-publish -- \
  --major 9 \
  --minor 0 \
  --extrafiles LICENSE \
  --readme src/main/type-definitions/NPM.md \
  --dryrun /tmp/package.tgz

# Publish to npm using the given version
# Prompts for the credentials
npm run npm-publish -- \
  --major 9 \
  --minor 0 \
  --extrafiles LICENSE \
  --readme src/main/type-definitions/NPM.md
```

# Technical overview

More technically speaking, the following steps are performed to create the the
JavaScript API docs::

* Use [acorn](https://www.npmjs.com/package/acorn) to parse the JavaScript
  source code.
* Walk the syntax tree and look for certain patterns, such as
  `PrimeFaces.widget.xxx = Class.extend({...})` and annotations in doc comments,
  such as `@interface` or `@function`. This produces a list of constants,
  objects, and functions that need to be documented.
* For each item to be documented, gather some information:
    * Look a the source code, such as the method signature or the declared
      properties of an object.
    * Parse the doc comments via
      [comment-parser](https://www.npmjs.com/package/comment-parser) and look
      at tags such as `@param` and `@return`.
    * Check for conflicts, such as parameters declared via `@param` that do no
      occur in the method signature.
    * Create a typescript declarations file from this data.
* Create one type declaration (`*.d.ts`) file with all documented items.
    * Run [TypeScript](https://www.npmjs.com/package/typescript) on the
      generated  type declarations file to check for error, such as types that
      do not exist or cannot be resolved (e.g. a misspelled `Jquery`).
    * Make use of the type info generated by TypeScript to check for superfluous
      or missing `@override` tags.
    * For each doc comment with an `@inheritdoc` tag, copy the description from
      the parent type(s) or method(s). This is done because the support for
      the `@inheritdoc` tag  varies widely between various tools.
    * Run [ESLint](https://eslint.org/) with the
      [TypeScript ESLint plugin](https://typescript-eslint.io/) on the final
      type declarations file to check form common mistakes, such as `Function`
      or `Object`.

If you are wondering, _why not just use JSDoc?_

* I checked out JSDoc and tries to use it for the documentation, but it's got
  several minor issues that could be worked around and one to two major issues:
    * The major issue is that JSDoc uses its own type system that is
      fundamentally incompatible with TypeScript types. For simple types such as
      `string` and `number`, that's not an issues, but JSDoc starts complaining
      as soon you try to use more complex types such as
      `(x: number) => boolean`, `{[key: string]: boolean}`, or
      `import("foo").Bar`.
    * It's also not obvious how to generate a type declaration file via JSDoc.
      There are tools like [tsd-jsdoc](https://github.com/englercj/tsd-jsdoc),
      but they don't solve the issues above and come with their own limitations.
* There's also ESDoc, with similar issues to JSDoc. It's also meant for
  module-like files and hasn't got tags for converting the pseudo widget
  classes.

# Documenting a widget

You can add doc comments and type annotations directly in the JavaScript code.
During the build process, the JavaScript gets parsed and the relevant doc
comments and types are extracted. Based on that, a type declarations file is
generated. If you make any mistakes, you will get a (hopefully helpful) warning
or error when you build the the project.

In general, the syntax closely resembles [jsdoc](https://jsdoc.app), but a few
customizations (mainly new tags) had to be made to allow for the creation of
high-quality type declaration files.

You can always take a look at widgets that are already documented to see how
it's done. You can find them in
`src/main/resources/META-INF/resources/primefaces` Below are a few tips to help
you get started.

## Basic widget documentation

Let's start with a simple case, a widget that has got some

* some methods,
* some internal state (properties),
* and some configuration (`cfg`)

Let's take the AccordionPanel (`accordion.js`) as an example. Without any
documentation, it looks like this (implementation and some details omitted):

```javascript
PrimeFaces.widget.AccordionPanel = PrimeFaces.widget.BaseWidget.extend({
    init: function(cfg) {
        this._super(cfg);
    }

    initActive: function() {},
    
    bindEvents: function() {},
    
    select: function(index) {},
    
    unselect: function(index) {}
});
```

### Methods

First, we start by documenting the methods (the easiest part). We need to add
a short description of what the method does, and figure out the types of the
parameters and the return value. We use the `@param` and `@return` tags for
that.

> __Note:__ All @tags need to be placed after the description of the method.

If a method is not meant to be used from outside the widget, add the `@private`
tag before the other tags. You can also use `@protected` for methods that can
be used by a sub class. If you are wondering how to decide whether a method
should be private or public,
[see below for some guidance](#private-and-public-methods).

> __Note:__ The build process checks for inconsistencies in the class hierarchy.
So for example, you can't make a method public in one class but mark it as
@protected in a sub class; or have a method in a sub class that needs more
parameters than the super class. 

> __Note:__ Methods that start with an underscore `_` are excluded by default.
If you want to include them, add `@include`.)

As a convention, order the tags the way the corresponding tokens would appear
in Java or TypeScript - first `@private`, then [@typeparam](#type-parameters)
and `@param`, then `@return`.

With that in mind, here's how the accordion widget looks like with documented
methods:

<details>
<summary>Click to see code snippet</summary>

```javascript
PrimeFaces.widget.AccordionPanel = PrimeFaces.widget.BaseWidget.extend({

    /**
     * @override
     * @inheritdoc
     * @param {PrimeFaces.PartialWidgetCfg<TCfg, this>} cfg
     */
    init: function(cfg) {
      this._super(cfg);
    }
    
    /**
     * Called when this accordion panel is initialized. Reads the selected
     * panels from the saved state, see also `saveState`.
     * @private
     */
    initActive: function() {
    },

    /**
     * Binds all event listeners required by this accordion panel.
     * @private
     */
    bindEvents: function() {
    },

    /**
     * Activates (opens) the tab with given index. This may fail by returning
     * `false`, such as when a callback is registered that prevent the tab from
     * being opened.
     * @param {number} index 0-based index of the tab to open. Must not be out
     * of range.
     * @return {boolean} `true` when the given panel is now active, `false`
     * otherwise. 
     */
    select: function(index) {
    },

    /**
     * Deactivates (closes) the tab with given index.
     * @param {number} index 0-based index of the tab to close. Must not be out
     * of range.
     */
    unselect: function(index) {
    }
});
```

</details>

See how we added the `@param` tag to the `select` and `unselect` method? You
might have noticed the `@param` tag consist of several parts:

1. The type of the parameter, enclosed in braces (`{number}`).
1. The name of parameter (`index`).
1. A short description of the parameter itself. It can span multiple lines.

> __Note__ The build process checks whether the parameters declared by code
match the parameters you annotated with `@param` tags. So if you misspell then
name of a parameter or forget to document a parameter, you'll be informed when
you run `mvn install`.

The types you can add are `TypeScript` types. The most basic types correspond to
the basic JavaScript types - `number`, `boolean`, `string`, as well as
`undefined` and `null`. But TypeScript offers many more types that help to make
the documentation clearer, such as via [@typedef](#typedef). For a technical overview, see the
TypeScript manual pages on
[Basic Types](https://www.typescriptlang.org/docs/handbook/basic-types.html) and
[Advanced Types](https://www.typescriptlang.org/docs/handbook/advanced-types.html).

The `@return` tags works pretty much the same. The only difference is that
there's no need for a name, as a method can return only one value. Oh, and of
course, no return tag is needed when the method does not return a value.

> __Note__ The build process checks for return values as well. So you'll be
informed in case you forget to document a return value.

But wait, there's more. If you took a close look at the code above, you'll have
noticed there are some strange tags on the `init` method:

```javascript
/**
 * @override
 * @inheritdoc
 * @param {PrimeFaces.PartialWidgetCfg<TCfg, this>} cfg
 */
init: function(cfg) {
  this._super(cfg);
}
```

What's going on with `@override` and `@inheritdoc`? Well, the `AccordionWidget`
actually inherits from the `BaseWidget`. And the base widget already declares
an `init` method. It`s marked as protected, so we can override it. To indicate
that, we add the `@override` tag.

Now remember I said we had add a description for the method and its parameters?
Well, the `BaseWidget` has already got a description. So why repeat it? That's
what the `@override` tag is for. It tells the build process it should copy the
description from the parent method.

Next, we add documentation for the other methods of the widget. A `@param` tag
for the parameters (arguments) and a `@return` tag for the return value. If a
method does not return anything (= no `return <value>` in the code), omit the
`@return` tag.

That leaves us with the pretty complex type for the `cfg` parameter. This is
explained in detail further below. So for now, let's just say that `TCfg` is
a type parameter that represents all the settings of the widget, and
`PartialWidgetCfg` makes it so that all options are optional (can be
`undefined`). Just remember that this is the standard doc comment (boilerplate)
you should always add to the `init` method.

Alright, that covers the basics for methods. On to the widget header.

### Widget header

We also need to add some documentation for the widget class itself. This will
also be the part where we'll document the state and configuration of the widget.

Before we take a look at a sample, let's summarize the information we need to
include (in the order it should appear in the doc comment):

1. A general description of the widget, such as what it does or what it is meant
   for.
1. Additional TypeScript types and interfaces that we need for the widget.
1. A list of all properties (fields) the widget has got, i.e. the widget state.
1. A list of all configuration keys. The configuration is stored in the `cfg`
   property of the widget. This will be an interface and you might think it
   should be placed under (2). But the configuration interface is vital to all
   widgets and as a matter of style, is separated from the other parts.

The second part, additional types and interfaces are covered in the
[advanced section below](#advanced-documenting). For now, let's take a look at
the other three parts. Returning to our accordion example, the header looks like
this:

<details>
<summary>Click to see code snippet</summary>

```javascript
/**
 * __PrimeFaces AccordionPanel Widget__
 * 
 * The AccordionPanel is a container component that displays content in a
 * stacked format.
 * 
 * @prop {JQuery} headers The DOM elements for the header of each tab.
 * @prop {JQuery} panels The DOM elements for the content of each tab panel.
 * @prop {JQuery} stateHolder The DOM elements for the hidden input storing
 * which panels are expanded and collapsed.
 * 
 * @interface {PrimeFaces.widget.AccordionPanelCfg} cfg The configuration for
 * the {@link  AccordionPanel| AccordionPanel widget}. You can access this
 * configuration via {@link PrimeFaces.widget.BaseWidget.cfg|BaseWidget.cfg}.
 * Please note that this configuration is usually meant to be read-only and
 * should not be modified.
 * @extends {PrimeFaces.widget.BaseWidgetCfg} cfg
 * 
 * @prop {number[]} cfg.active List of tabs that are currently active (open).
 * Each item is a 0-based index of a tab.
 * @prop {boolean} cfg.cache `true` if activating a dynamic tab should not load
 * the contents from server again and use the cached contents; or `false` if the
 * caching is disabled.
 * @prop {string} cfg.collapsedIcon The icon class name for the collapsed icon.
 * @prop {boolean} cfg.controlled `true` if a tab controller was specified for
 * this widget; or `false` otherwise. A tab controller is a server side listener
 * that decides whether a tab change or tab close should be allowed.
 * @prop {boolean} cfg.dynamic `true` if the contents of each panel are loaded
 * on-demand via AJAX; `false` otherwise.
 * @prop {string} cfg.expandedIcon The icon class name for the expanded icon.
 * @prop {boolean} cfg.multiple `true` if multiple tabs may be open at the same
 * time; or `false` if opening one tab closes all other tabs.
 * @prop {boolean} cfg.rtl `true` if the current text direction `rtl`
 * (right-to-left); or `false` otherwise.
 */
PrimeFaces.widget.AccordionPanel = PrimeFaces.widget.BaseWidget.extend({

  // omitted for brevity

});
```

</details>

The description is pretty simple and comes at the beginning as usual, before
all tags. As a matter of convention, the first line contains the name of the
widget in bold.

> __Note:__ You can use markdown in the doc comments to add some basic 
formatting.

### Properties

After the description, you can see the documentation for the three properties
of the widget:

```javascript
/**
 * @prop {JQuery} headers The DOM elements for the header of each tab.
 * @prop {JQuery} panels The DOM elements for the content of each tab panel.
 * @prop {JQuery} stateHolder The DOM elements for the hidden input storing
 * which panels are expanded and collapsed.
 */
```

Notice it looks pretty similar to the `@param` tag of methods? It consists of a
type, the name of the property, and a description for the property. Many widget
properties are references to DOM elements in a JQuery wrapper. For these we use
the type `JQuery`, which is defined by the
[type declaration for JQuery](https://github.com/DefinitelyTyped/DefinitelyTyped/tree/master/types/jquery).

> __Note:__ When the description doesn't fit on single line, add a line
break. A line should not be longer than 120 characters (with some minor
exceptions such as long, complex types or long links). Also, don't indent lines,
start immediately after the asterisk (`*`) at the beginning of the line.

### Widget configuration

Did you notice something in the `init` method earlier on? It takes a parameter
called `cfg` and passes it to the super method. This is widget configuration and
it's stored in the property `this.cfg`. Usually this configuration object comes
from the server when it renders the HTML for the widget, and is usually defined
in an `xhtml` file. 

Since this configuration is so fundamental to a widget, the base widget from
which all widgets inherit (with one minor exception) defines a type parameter
for that configuration. It lets the base widget know about the available
configuration properties, which it needs for defining the type of some basic
methods common to all widgets. What that means is that we don't have to worry
about it - all we need to do is document the configuration and tell the bas
class about it:

```javascript
/**
 * @interface {PrimeFaces.widget.AccordionPanelCfg} cfg The configuration for
 * the {@link  AccordionPanel| AccordionPanel widget}. You can access this
 * configuration via {@link PrimeFaces.widget.BaseWidget.cfg|BaseWidget.cfg}.
 * @extends {PrimeFaces.widget.BaseWidgetCfg} cfg
 * 
 * @prop {number[]} cfg.active List of tabs that are currently active (open).
 * Each item is a 0-based index of a tab.
 * 
 * (omitted for brevity)
 */
```

1. The first line declares a new interface for our accordion panel configuration
   that will contain all the available configuration properties.
1. The second lines contains the `@extends` tag. We use it to extend the base
   widget configuration, which contains common properties such as `id` and
   `widgetVar`. Note that we don't have to repeat these properties here.
1. On the third line, we declare the actual properties of the widget
   configuration. This is the same as the widget properties, with one minor
   difference: The name of each configuration __is prefixed with `cfg.` - if we
   had not added this prefix, it would be misinterpreted as a property of the
   widget itself__. Be careful here!

> __Note:__ Another thing the careful reader may have noticed. We can add
references to other symbols (classes, interfaces etc.) via the
`{@link SomeNamespace.SomeClass}`. To change the displayed text of the rendered
link, use a vertical bar: `{@link SomeNamespace.SomeClass|Click here}`.

To finish the widget documentation, we can add additional tags, such as
`@author` (who wrote the widget?) or `@since` (which version was it added?).

# Advanced documenting

Sometimes types are getting more complex. Below are some of the more common
cases. See also the tests in `src/main/type-definitions/specs/` for more
examples on how to use the various tags.

## Excluding source files

All JavaScript files in `src/main/resources/META-INF/resources/primefaces` are
processed by default. Some of those files are third-party libraries and some
have got doc comments that are invalid.

To prevent these libraries from being processed, add them to the blacklist in
`src/main/type-definitions/blacklist.txt`, which looks like this:

```
# List of files to be excluded from the type declaration file.
# Mainly third-party libraries we do not want to document.
chartjs/0-chartjs.js
charts/0-jqPlot.js
keyboard/0-jquery.keypad.js
touch/touchswipe.js
```

## Additional type declarations

If you need to create advanced typings that cannot be done via doc comments, you
can create a separate `*.d.ts` file in a widget directory. As long as it ends
in `.d.ts`, is is picked up automatically:

* src/main/resources/META-INF/resources/primefaces
    * accordion
        * accordion.js
        * 01-SomeTypes.d.ts
        * 02-MoreTypes.d.ts
    * calendar
        * calendar.js
        * AdditionalDeclarations.d.ts

> __Note:__ Separate declaration files are recommended for documenting included
third-party libraries.

In case more multiple type declaration files are found, the files are sorted
alphabetically.

## Namespaces

The type declarations for PrimeFaces are organized via namespaces and do not
make make use of modules. Namespaces are a perfect fit since PrimeFaces is
always loaded into the global window scope and not bundled as a module.

The main namespace is `PrimeFaces`, with sub namespaces such as `widget`,
`ajax` etc. These reflect the actual namespaces as declared by the PrimeFaces
JavaScript.

There are a few rare cases where you need additional type declarations that
augment a module (via `declare module`). Adding these would break the
PrimeFaces declaration file.  To work around this, the build process checks
whether an additional type declaration file contains any `import` or
`declare module` statements. It it does, it's not added to the main
`PrimeFaces.d.ts` file, but to the separate `PrimeFaces-module.d.ts` file. See
`mindmap.d.ts` for an example.

## DefinitelyTyped

DefinitelyTypes is a
[repository for high quality TypeScript type definitions](https://github.com/DefinitelyTyped/DefinitelyTyped). When you document a widget that uses some
third-party library, you should first check there whether they have already got
the types for that library. If they do, you can simply add the types via

```sh
cd src/main/type-definitions/
npm install `@types/<library-name>`
```

> __Note:__ Occasionally the NPM module itself comes bundles with type
declarations. In this case, just install the library directly:
`npm install <library-name>`.

Next, you need to edit the main `pom.xml` and look for the line where the
property `jsdoc.included.modules` is defined. This is a list of all
[NPM modules](https://www.npmjs.com/) modules that should be included in the
generated typedocs. Add the name of the third-party library there.

> __Note:__ Please also update the `JSDOC.md` file where it tells users about
the NPM types they may have to install.

Finally, depending on the type of the declarations, you may have to:

1. Add a
   [triple-slash directive](https://www.typescriptlang.org/docs/handbook/triple-slash-directives.html)
   to the beginning or `core.d.ts` (if it's a global library)
1. Use
  [import types](https://www.typescriptlang.org/docs/handbook/release-notes/typescript-2-9.html#import-types)
  in the widget documentation (if it's a modular library).


## Overriding a method

Sometimes a subclass overrides a method of the base class. In this, case add the
`@override` tag. If you want to inherit the doc comment from the parent class as
well, also add the `@inheritdoc` tag. This tag only inherits the documentation,
not the typings of the method. This means you still need to add the `@param` and
`@return` tags to indicate the type, but you can leave the description empty:

```javascript
PrimeFaces.widget.SubWidget = PrimeFaces.widget.BaseWidget.extend({
    /**
     * @override
     * @inheritdoc
     * @param {number} index
     * @return {JQuery}
     */
    getItem: function(index) {
        // ...
    },
});
```

## Typedef

If you need to add a type for convenience, you can do so via `@typedef` tag:

```javascript
/**
 * @typedef {{r: number, g: number, b: number}} RgbColor
 */
PrimeFaces.widget.Typedef = PrimeFaces.widget.BaseWidget.extend({
    /**
     * @return {RgbColor} color
     */
    getColor: function() {
        // ...
    }
    /**
     * @param {RgbColor} color
     */
    setColor: function(color) {
        // ...
    },    
});
```

This will generate something like:

```typescript
namespace PrimeFaces.widget {
    export type RgbColor = {r: number, g: number, b: number};
    export class Typedef extends BaseWidget<TypedefCfg> {
        getColor(): RgbColor;
        setColor(color: RgbColor): void;
    }
}
```

> __Note:__ You can also use the `@typedef` tag on a method, this will behave
the same way as if you had added it to the widget. But for clarity, prefer to
add the typedef to the widget header.

Some common use cases where a typedef is recommended include:

1. String unions. For example, consider a widget configuration property named
  `selectionMode`. It's not just any string, but can only take the values
  `single` and `multiple`. The type, therefore, should be
  `"single" | "multiple"`. Using a typedef for this has several advantages: (a)
  it follows the DRY principle as you don't have to repeat the string constants
  every time you need to use the type; (b) it lets you document the meaning
  of the individual options in one place; and (c) it doesn't result in the type
  taking up the entire first line of the widget property declaration.
1. Callbacks. Same as for string unions. Consider a widget configuration
   property that is named `onShow`. It's type is a function type with a this
   context, two parameters, and a return value. Refactoring this type into a
   typedef has the same advantages as for string unions. And in addition, it
   also makes it easier to document the parameters (see the example below).

A typedef can also define a function type. In this case you should document
the parameters and the return type. Here's an example of how it works (taken
from the `TabView` widget):

```javascript
/**
 * @typedef PrimeFaces.widget.TabView.OnTabCloseCallback Client side callback to
 * execute on tab close. When the callback returns `false`, the tab is not
 * closed. See also {@link TabViewCfg.onTabClose}.
 * @this {PrimeFaces.widget.TabView} PrimeFaces.widget.TabView.OnTabCloseCallback
 * @param {number} PrimeFaces.widget.TabView.OnTabCloseCallback.index 0-based
 * index of the tab that is about to be closed.
 * @return {boolean} PrimeFaces.widget.TabView.OnTabCloseCallback `true` to
 * close the tab, `false` to keep the tab open.
 */
```

This will result in the following typedef:

```typescript
declare namespace PrimeFaces.widget.TabView {
  /**
   * Client side callback to execute on tab close. When the callback
   * returns `false`, the tab is not closed. See also {@link TabViewCfg.onTabClose}.
   */
  export type OnTabCloseCallback =
  /**
   * @param index 0-based index of the tab that is about to be
   * closed.
   * @return `true` to close the tab, `false` to keep the tab
   * open.
   */
  (this: PrimeFaces.widget.TabView, index: number) => boolean;
}
```

## Interfaces

You can also define an additional interface in a widget header and use it for
the widget's properties and methods. This works the same way as the interface
for the widget configuration - use the `@interface` tag:

```javascript
/**
 * @interface {PrimeFaces.widget.Knob.ColorTheme} ColorTheme A color theme for
 * the knob, consisting of the color for the filled and unfilled part of the
 * knob.
 * @prop {string} ColorTheme.fgColor The foreground color, i.e. the color of the
 * filled part of the knob. Must be a CSS color, e.g. `#ff0000`.
 * @prop {string} ColorTheme.bgColor The background color, i.e. the color of the
 * unfilled part of the knob. Must be a CSS color, e.g. `#ff0000`.
 */
```

The code above create an interface name `Color` theme in the namespace
`PrimeFaces.widget.Knob`. That interfaces declares two properties, `fgColor` and
`bgColor`, both of type `string`:

```typescript
declare namespace PrimeFaces.widget.Knob {
  /**
   * A color theme for the knob, consisting of the color for the
   * filled and unfilled part of the knob.
   */
  export interface ColorTheme {
    /**
     * The background color, i.e. the color of the unfilled part of the knob.
     * Must be a CSS color, e.g. `#ff0000`.
     */
    bgColor: string;
    
    /**
     * The foreground color, i.e. the color of the filled part of the knob. Must
     * be a CSS color, e.g. `#ff0000`.
     */
    fgColor: string;
  }
}
```

## Type parameters

Generic type parameters are supported for methods via the `@template` tag. You
can also specify a bound or default for the type parameter:

```javascript
PrimeFaces.widget.Destructured = PrimeFaces.widget.BaseWidget.extend({
    /**
     * @template T Type of an object
     * @template {keyof T} K Type of the key to access
     * @template [S=T] Type of an object
     * @param {T} object Any object
     * @param {K} key A key in the object
     * @return {T[K] & S} The value  at the given key in the object
     */
    genericMethod: function(object, key) {
        // ...
    },
});
```

This will generate something like:

```typescript
function genericMethod<T, K extends keyof T, S = T>(object: T, key: K): T[K] & S;
```

## Optional parameters and initializers

To indicate that a parameter of a method is optional, enclose its name in
brackets:

```javascript
PrimeFaces.widget.Optional = PrimeFaces.widget.BaseWidget.extend({
    /**
     * @param {number} x
     * @param {number} [y] Don't make the type number | undefined
     * @param {number} [z = 42] Set a default value 
     */
    oneToThreeArgs: function(x,y,z) {
        // ...
    },
});
```

This will generate something like:

```typescript
function oneToThreeArgs(x: number, y?: number, z: number = 42): void;
```

> __Note:__ Boolean arguments that comes at the end of a parameter list are
often a supposed to be optional. For example, a widget method with a final
parameter named `silent` (don't trigger events) - if the caller does not pass
a value, it is treated as `false`.

## Destructuring

Destructured parameters are supported by the parser. But please check out the
style guide for the JavaScript. As of now, ES6 features should not be used.

```javascript
PrimeFaces.widget.Destructured = PrimeFaces.widget.BaseWidget.extend({
    /**
     * You need to add a param annotation for each declared symbol that is made
     * available to the function scope. The type annotation, however, must be
     * declared for the entire positional argument - don't add a type for
     * destructured symbols, and add pattern tag instead.
     * 
     * @param {string} normalParam the param x - type annotation as normal
     * @param y1 the param y1 - no type annotation
     * @param y2 the param y2 - no type annotation
     * @param z the param z - no tag for z2, as z2 does not exist as a variable
     * within the function
     * @param k the param k
     * @pattern {{x: string, y: {y1: number, y2: number}, z2: boolean}} 1 Type
     * declaration for the destructured argument at position=1
     * @pattern {MapFromCharacterToCode} 2 Type declaration for the destructured
     * argument at position=2
     * @return {RegExp} the return value with its type as normal
     */
    destructureObject: function(normalParam, {x, y: {y1, y2}, z2: z }, {k}) {
        /// ... implementation ...
    },

    /**
     * For arrays, it works the same way as it does for objects.
     * 
     * @param {string} normalParam
     * @param y1 the param y1
     * @param y2 the param y2
     * @param z the param z
     * @param k the param k
     * @pattern {[string, [number, number]]} 1
     * @pattern {boolean[]} 2
     * @return {RegExp}
     */
    destructureArray: function(normalParam, [x, [y1, y2]], [z, ...k]) {
        /// ... implementation ...
    }
});
```

If you need to support destructured parameters for a a method in a property
(widget configuration or otherwise), you need to describe the structure
explicitly, as no code is available:

```javascript
/**
 * @param {PrimeFaces.widget.DemoCfg} cfg
 * @structure {{x, y2: y}} cfg.check.1
 * @structure {{z}} cfg.check.2
 * @pattern {{x: number, y2: string}} cfg.check.1
 * @pattern {Vector3} cfg.check.2
 * @method cfg.check
 * @param {boolean} cfg.check.normal
 * @param cfg.check.x
 * @param cfg.check.y
 * @param cfg.check.z
 * @return {string}
 */
PrimeFaces.widget.Demo = PrimeFaces.widget.BaseWidget.extend({
});
```

This will generate something like this:

```typescript
namespace PrimeFaces.widget {
    export class Demo extends BaseWidget<DemoCfg> {
        cfg: DemoCfg;
    }
    export interface DemoCfg {
        check(normal: boolean, {x, y2: y}: {x: number, y2: string}, {z}: Vector3): string;
    }
}
```

## Asynchronous methods and generators

Asynchronous methods and generators are recognized automatically by their
syntax. The return type is modified automatically, so just write down the base
type:

```javascript
PrimeFaces.widget.Destructured = PrimeFaces.widget.BaseWidget.extend({
    /**
     * @return {string}
     */
    doWork: async function() {
        // ...
    },
    /**
     * @yield {string}
     */
    list: function* () {
        // ...
    },
    /**
     * @yield {string}
     */
    listFromNetwork: async function* () {
        // ...
    }
    /**
     * @yield {string}
     * @next {number}
     * @return {boolean}
     */
    complicatedStuff: async function* () {
        return (4 + (yield "")) === 8;
    }
});
```

This will generate something like:

```typescript
function doWork(): Promise<string>;
function list(): Generator<string, void>
function listFromNetwork(): AsyncGenerator<string, void>;
function complicatedStuff(): AsyncGenerator<string, boolean, string>;
```

# Style and conventions

If you want to contribute, that's great. For the sake of consistency, please
follow the basics of our style guide.

## Typescript types

Some guidelines for annotating properties, parameters and return values with
types:

* Follow common typescript conventions, see for example
  [Do's and Don'ts](https://www.typescriptlang.org/docs/handbook/declaration-files/do-s-and-don-ts.html)
* Use `string`, `number`, `boolean` instead of `String`, `Number`, `Boolean`
* Use import types when possible: `import("chart.js").ChartConfiguration`. Not
  possible for global declarations such as `JQuery`.
* Do not use `object` if it can be avoided. If possible, describe the shape of
  the object, such as `{x: string, y: number}`. For generic objects that behave
  like a map, use  for example `Record<string, number>`.
* For arrays, use `string[]` or `JQuery[][]` instead of `Array<string>` and
  `Array<JQuery>[]` 
* Use only `undefined`, unless the code you are describing absolutely requires
  the use of the type `null`.
* But be explicit about `nullness`: If a method may return either a string or
  nothing, the return value is `string | undefined`. (If it never returns a
  value, the return type is `void`.)
* Try making the name of type parameters more descriptive, such as
  `Class<TConfig>` instead of `Class<T>`. But keep them short.
* Add a default for a type parameter whenever possible, especially when the type
  parameter is bounded:
  
```typescript
    export function doSomethingWithWidget<
        TWidget extends PrimeFaces.widget.BaseWidget = PrimeFaces.widget.BaseWidget
    >(widget: TWidget) {
        // ...
    }
```

## Doc comments

These are just some general guidelines to keep the documentation consistent, you
do not have to follow every single rule to the letter.

* Try to make doc comments helpful. For example, if you're documenting a getter
  method, you don't have to spell out `Returns the foo`. Instead, try answering
  questions a consumer of a library might have, such as
    * What is foo?
    * Does it cache the foo or create a new one each time?
    * What's the unit of foo?
    * Does it have any other side effects?
    * If the type is `JQuery`, which elements are allowed exactly? (Usually it
      helps to mention the style class of acceptable elements, such as
      `.ui-chkbox-box`.)
    * (for methods) Can I pass `undefined` and what happens if I do?
    * (for methods) Can it throw an error?
    * (for methods) When does it throw an error?
    * (for methods) What does the method do, are there any performance
      limitations?
    * (for params / return values) If it's a physical value, which unit does it
      have (eg. milliseconds)
    * (for params / return values) If it's an index, is it 0-based or 1-based?
    * (for params / return values) If it's a range, is inclusive (`[1,10]`) or
      exclusive (`(1,10)`)?
    * (for callbacks) What do I need to return for which circumstance?
    * (for callbacks) What happens if I throw an error?
    * ...
* Limit doc comments to at most 120 characters per line. This limit includes the
  indentation at the beginning of the line. The only exception are long types.
* Use markup syntax in doc comments, avoid using raw HTML.
* If a `@param` can be `null` or `undefined`, describe what happens if it is:
  Will it throw an error, will it return immediately without doing anything
  useful, will it substitute some default value (which?), or something else?
* If a `@return` can be `null` or `undefined`, describe why and when this
  happens.
* Keep the description on `@return` and `@param` short and to the point, add
  more details in the main doc comment description.
* Prefer third-person: `Deactivates the behavior` instead of
  `Deactivate the behavior`
* Prefer simple, active voice constructions when possible:
  `So that the user can no longer select a date` instead of
  `So that no date can be selected anymore` or
  `So that no date is selectable any longer`.
* Inside a class, refer to the instance by the word `this`. For example, the
  documentation for the method `start` of the `Clock` widget should read
  `Starts this clock if not already running`, not `Starts (the/a) clock...`
* Prefer American English spellings, eg `behavior`, `color`, `meter`, `license`,
  `localization`, `maximize`, `dialog`, `canceled`, `resizable`, `dependent`,
   `tidbit`.
* All tags such as `@param`, `@return` etc. need to come last, after the
  description. If you try and add a description for a method or property after a
  tag, the parser will interpret it as part of the tag's description.
* Some tags have multiple names, and while neither is wrong, to keep it
  consistent:
    * use `@return` instead of `@returns`
    * use `@prop` instead of `@property`
    * use `@yield` instead of `@yield`
* Keep tags of the same type together. If possible, try to keep tags in this
  order:
    * @typedef
    * @override
    * @inheritdoc
    * @class / @interface
    * @public / @protected / @private / @internal / @abstract
    * @template
    * @prop
    * @method
    * @param
    * @return
    * @see
    * @author

## Private and public methods

There are no hard or clear-cut rules to decide whether a method should be
private or part of the public API. But there are some good indicates.

Ask yourself the following questions:

1. Is this method temporary, deprecated or will it change soon?
1. How "useful" is this method, what could people do if it were exposed?
1. Does the method actually work standalone? Does it take arguments that the
   consumer would not have? Does it require other methods to be called in a
   specific order to work? If a checkbox widget has a method called `toggle`,
   that is useful. But not when the method requires the checkbox element to
   be passed to it - the widget should know about its element, not the
   caller.
1. Is this method essential, or are there other ways to achieve the same goal?
1. How many words do you need to describe what this method does? If you need too
  many, it's probably some very specific private helper method.
1. Is it used only in the `init` method? That's a good sign of a private
  method.

Here are some common traits for methods that should most likely be private:

* Methods that perform initial setup and event binding, often called `bind...`,
 `setup...`, `listen...`, or `attach...`
* Internal logic, such as `clearCache`, `processKeyEvents`, `invoke...` or
  `compute...`.
