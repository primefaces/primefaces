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

Starting with PrimeFaces 15, we instead opted to rework the code structure to make it easier
for TypeScript to analyze. This way, we can now generate type declarations with only a minimal
amount of customization.

Additionally, we also switched from [Closure Compiler](https://github.com/google/closure-compiler)
to [ESBuild](https://esbuild.github.io/) as a build tool. Due to backwards compatibility concerns
and restrictions imposed by Jakarta Faces, we cannot make full use of ESM (EcmaScript module syntax).
Still, there are some improvements, such as the ability to import dependencies from NPM, instead of
copying their source code directly into our repository.

# Code structure

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

# Linking packages

As mentioned above, we still need to use the global scope for linking code from one
package to code from another package.

Each package can use `import` / `export` syntax internally. All modules reachable via
`import` statements from the `index.ts` file will be included in the bundle.

If a package contains code that needs to be used by another package, it must manually
expose that part to the global scope. For example, the package `filedownload/filedownload`
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
usually happens when we import a dependency from NPM. For example, the package
`schedule/schedule` imports the 3rd party library `@fullcalendar/moment`, which
in turn imports `moment`. But `moment` is already provided and exposed by the
package `moment/moment`. We can't modify the source code of the Full Calendar
library. Instead, we use an ESBuild plugin that replaces the `import "moment"`
and loads moment from the global scope instead.

For that, each package may contain an optional `build-settings.json` file, next
to the `index.ts` file. Here you can specify the name of the third-party library
and the window global from which to load the library. See e.g.
`dist/schedule/schedule/build-settings.json` for an example.

# Building

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