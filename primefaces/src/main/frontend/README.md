This folder contains the frontend resources for PrimeFaces (JavaScript, CSS, images).
The following gives a short explanation regarding the folder structure and an
overview of the build process.

# Background

Historically, PrimeFaces just consisted of a bunch of JavaScript and CSS files, without any
module system or build process whatsoever (other than minifying each file for the release build).
Any linking between files was done implicitly via the global scope (`window` / `globalThis`).

Furthermore, all widget classes used a custom class emulation script based on functions
and prototypes. That works, but this kind of code structure makes it hard for tools such
to analyze the code statically.

At some point in time (around PrimeFaces 8/10?), we added types as JSDoc annotation and generated
a type declaration file from that, as well as an API documentation page. Owing to the class
emulation code structure, we had to write custom code to parse all files and extract the intended
classes. That was hard to maintain, as it made heavy use of the TypeScript API, which is prone to
change between major releases.

Starting with PrimeFaces 16, we instead opted to rework the code structure to make it easier
for TypeScript to analyze. This way, we can now generate type declarations with only a minimal
amount of customization.

Additionally, we also switched from [Closure Compiler](https://github.com/google/closure-compiler)
to [ESBuild](https://esbuild.github.io/) as a build tool. Due to backwards compatibility concerns
and restrictions imposed by Jakarta Faces, we cannot make full use of ESM (EcmaScript module syntax).
Still, there are some improvements, such as the ability to import dependencies from NPM, instead of
copying their source code directly into our repository.

# Code structure

__TL;DR__ One package (TypeScript project reference) for each JavaScript / CSS file in the PrimeFaces JAR.

When you take a look at the final PrimeFaces.jar file, you'll notice it contains a bunch of
bundled JavaScript files:

- META-INF/resources/primefaces/jquery/jquery.js
- META-INF/resources/primefaces/core.js
- META-INF/resources/primefaces/components.js
- META-INF/resources/primefaces/components.css
- META-INF/resources/primefaces/colorpicker/colorpicker.js
- ...

For each such file, there's one package inside the `packages` folder. Each package
folder contains an `index.ts` file, representing the entry point for that bundle.
If custom CSS is needed, there also an `index.css`:

- META-INF/resources/primefaces/jquery/jquery/index.ts
- META-INF/resources/primefaces/core/index.ts
- META-INF/resources/primefaces/components/index.ts
- META-INF/resources/primefaces/components/index.css
- META-INF/resources/primefaces/colorpicker/colorpicker/index.ts
- ...

Each package folder also contains `tsconfig.json`. We use
[TypeScript project references](https://www.typescriptlang.org/docs/handbook/project-references.html)
to separate the individual packages from each other. The `references` section of each
`tsconfig.json` contains the (implicit) dependencies of each package, mirroring the
`@ResourceDependency` annotations on each Java widget class.

# Building

__TL;DR__ Just use `mvn package ...`, or `yarn run build` if you ever need.

Building is done by Maven, via the
[frontend-maven-plugin](https://github.com/eirslett/frontend-maven-plugin), which
takes care of downloading node and executing the appropriate scripts.

If you ever need to build the frontend projects manually, take a look at the
`scripts` section of the `package.json`. It contains:

- `build:bundle` - Run ESBuild to bundle up all code.
- `bundle:types` - Run TypeScript to generate the type declarations file.
- `bundle:docs` - Run TypeDoc to generate the doc pages. Requires `bundle:types` to have run.
- `clean` - Remove all build artifacts.
- `verify` - Run various checks that prevent common mistakes.

So for example, open a CLI, go to the directory that contains this README, and
run `yarn install` (if you haven't done so yet), and then `yarn run build:bundle`
to build all files via ESBuild.

For the development build (non-minified), set the environment variable `NODE_ENV=development`.

# Image and font resources

__TL;DR__ You don't have to do anything. This is for information purposes.

You may want to reference images and font files in CSS files, e.g.

```css
background:transparent url("./password-meter.png") no-repeat left top;
```

For Jakarta Faces, you'd normally need to use a resource EL expression. The build
uses the [esbuild-plugin-faces-resource-loader](https://github.com/blutorange/esbuild-plugin-faces-resource-loader/)
to create these resource EL expressions automatically. So you can just use the
CSS code above, where `./password-meter.png` is relative to the CSS file. This
also improves IDE support.

During the build, it gets transformed to:

```css
background: transparent url("#{resource['primefaces:components/src/forms/password-meter.png']}") no-repeat left top;
```

# Linking packages

__TL;DR__ Use `import` within a package, use global variables between different packages. 

As mentioned above, we still need to use the global scope for linking code from one
package to code from another package.

Each package can use `import` / `export` syntax internally. All modules reachable via
`import` statements from the `index.ts` file will be included in the bundle.

If a package contains code that needs to be used by another package, it must manually
expose that part to the global scope. For example, the package `jquery/jquery`
exposes jquery via `window.$` and `window.jQuery`. Or the package `filedownload/filedownload`
contains the following code to make the download function available via `window.download`:

```js
import { startDownload } from './pf.filedownload.js';
Object.assign(window, { download: startDownload });
```

Other packages can then access `window.download`.

To make TypeScript understand these modification to the global scope, the package
`filedownload/filedownload` first augments the global module:

```ts
import type { startDownload } from './pf.filedownload.js';
declare global {
    export const download = startDownload;
}
```

Then, another package that wishes to access `window.download` declares
a references to `filedownload/filedownload` in its `tsconfig.json` and
loads the types:

```json
{
	"extends": "../../../tsconfig-base.json",
	"compilerOptions": {
		"types": ["../../filedownload/filedownload/dist"],
	},
	"references": [{ "path": "../../filedownload/filedownload" }]
}
```

Finally, there are some cases where a package needs to import a dependency
that was already exposed to the global scope by a different package. This
usually happens when we import a 3rd party package from NPM.

For example, the package `schedule/schedule` imports the 3rd party library
`@fullcalendar/moment`, which in turn imports `moment`. But `moment` is already
provided and exposed by the package `moment/moment`.

We can't really modify the source code of the Full Calendar library. Instead, we
use an esbuild plugin that replaces the `import "moment"` and loads moment from
the global scope instead.

For that, each package may contain an optional `build-settings.json` file, next
to the `index.ts` file. Here you can specify the name of the third-party library
and the window global from which to load the library. See e.g.
`dist/schedule/schedule/build-settings.json` for an example.

# Patching packages

__TL;DR__ `yarn patch <package>` for a new patch, `yarn patch-update-squash <package>` to edit an existing patch.

We use some old 3rd party packages that aren't maintained actively anymore and
contain bugs that need to be fixed. Ideally, for the future, we'd replace them
with active packages and fix bugs upstream.

Sometimes fixing bugs upstream might not be feasible. In such cases, we need to
patch these packages manually. yarn provides a built-in mechanism for this, see
[yarn patch](https://yarnpkg.com/cli/patch) and
[package patching](https://yarnpkg.com/features/patching).

Previously, we just dumped the 3rd party library's source code into our own 
repository and modified that source code -- which made it hard to understand
what was changed and made it hard to update. 

Patches are stored in `.yarn/patches`. For each patch file, we also create an
accompanying `*.md` file that briefly explains the patch. This is not required,
but makes it easier to understand why and what was done.

The following is basically just copied from
[yarn's official docs](https://yarnpkg.com/features/patching):

## Adding a new patch

When you need to patch a third party package that does not have a patch yet,
first think again if you really need to and whether there is a different
solution. If you really need to patch the package:

```sh
# <package> is the name of the NPM package, e.g. webcamjs
yarn patch <package>
```

This command will cause a package to be extracted in a temporary directory
intended to be editable at will. The temporary directory is printed to stdout.

Once you're done with your changes, run `yarn patch-commit -s <path>` (with `path`
being the temporary directory you received) to generate a patchfile and register
it into your top-level manifest via the [patch: protocol](https://yarnpkg.com/protocol/patch).

Then, run `yarn install` to ensure everything is up-to-date.

## Updating an existing patch

Same as above, but use the following command instead (see
[here](https://github.com/yarnpkg/berry/issues/3851#issuecomment-2676144254) and
[here](https://github.com/blutorange/yarn-patch-package-update-squash) for why) 

```sh
# <package> is the name of the NPM package, e.g. webcamjs
yarn patch-update-squash <package>
```

# Troubleshooting

##  [ERROR] Could not resolve "..."

When you:

* are on a Windows machine
* have the repository on a drive letter different from your `%HOMEDRIVE%`
* are running yarn manually, i.e. not via Maven

Then you might get many error like `[ERROR] Could not resolve "..."` when you
try to run `yarn run build` or `yarn run build:bundle`.

This is because Windows does not support relative paths between drives. You
need to change yarn's cache folder to the same drive where the repository is.

To solve this issue, set the following environment variable (the Maven build
does this automatically):

```
# Adjust the drive letter accordingly
YARN_GLOBAL_FOLDER=D:\.yarn\berry\cache
```

See [evanw/esbuild#3131](https://github.com/evanw/esbuild/issues/3131).