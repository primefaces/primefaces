This patch

* replaces the deprecated jQuery function `$.fn.blur` with `$.fn.trigger` (`$(...).trigger("blur")`)
* replaces various CSS classes with their PrimeFaces equivalent, e.g. `jspCap` with `ui-scrollpanel-cap`
* removes newer ES syntax (trailing function arguments), so that the build via Grunt works.
  See https://github.com/vitch/jScrollPane/issues/381

Notes regarding updating the version of jscrollpane. If the patch is not applicable anymore and
you need to edit it, I recommend you just edit the non-minified file `script/jquery.jscrollpane.js`,
and then recreate the minified file.

To do so, copy the temporary folder created when you run `yarn patch` to another temp folder.
Then inside that folder:

* Copy the `package-lock.json` from https://github.com/vitch/jScrollPane to that folder.
* Run `npm install`
* Remove the `prettier` task from the `Gruntfile`
* Run `npm run build`.

This re-creates the minified file `jquery.jscrollpane.min.js`,
which you can now copy back to the original temp folder.