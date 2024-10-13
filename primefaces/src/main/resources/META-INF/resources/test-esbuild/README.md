This is a test whether we could use esbuild to bundle CSS and JavaScript files.

Run `node build.js` to run esbuild. It creates the output in the `dist` directory. There's `core.js`,
`components.js`, `components.css`, `jquery-plugins.js`; as well as several files for individual components such
as clock or schedule. Unless I missed something, these are all the files that are required (other than `touchswipe.js`
`validation.bv.js`).

# Why use esbuild?

* It's fast. On my machine, `time node build.js` takes about `300ms` (yes, milliseconds), and that is with minification.
* It has no dependencies, unlike e.g. webpack.
* It is a standalone binary that could even be run completely without node
* As it's a bundler, it works well with `@import` and `@export` statements. As I illustrated in the example for
  restructuring the code to better generate documentation, the idea is that we'll only have an entry point that imports
* It can also transpile TypeScript (just as quickly). It doesn't check types, but that doesn't matter for quick
  dev builds. It's faster than running `npx typescript`.
* I got some weird bug when using closure compiler while converting widgets to classes. It reads annotations from
  JSDoc comments, which may result in wrong output. E.g. `@interface` in the doc comments of a class will remove the 

# File sizes

The other point of this demo was to check how using esbuild affects the size of the minified output files. My results
are below. In summary, it's pretty much the same. esbuild's output is sometimes marginally larger, sometimes even
smaller, and after zipping, the different all but disappears completely.

(note: for fair results, I changed the `language_out` for closure compiler to `ECMASCRIPT_2016`).

| file              | closure-compile (zipped) | esbuild (zipped) |
|-------------------|--------------------------|------------------|
| core.js           | 95K (27K)                | 97K (27K)        |
| components.js     | 532K (111K)              | 504K (107K)      |
| components.css    | 110K (19K)               | 109K (19K)       |
| jquery-plugins.js | 143K (41K)               | 144K (41K)       |
| timeline.js       | 574K (157K)              | 586K (168K)      |
| schedule.js       | 305K (86K)               | 307K (85K)       |
| schedule.css      | 24K (5.8K)               | 22K (5.5K)       |
| photocam.js       | 20K (7K)                 | 21K (7K)         |
| inputnumber.js    | 222K (48K)               | 219K (48K)       |
| clock.js          | 20K (7K)                 | 21K (7K)         |
| colorpicker.js    | 19K (6.8K)               | 18K (6.6K)       |
| colorpicker.css   | 9.1K (2.5K)              | 8.7K (2.3K)      |
| stack.js          | 1.4K (686B)              | 1.1K (600B)      |

