* The build now uses ESBuild to bundle and minify the files. Takes less than
  1s on my machine.
* I moved all frontend source files to `src/main/frontend`. This is also the
  node root folder with the `package.json`. During the build, the bundled files
  are generated in `src/main/generated-resources`. Personally, I don't like
  putting files in `src/main/resources` that aren't actually supposed to be
  included as resource files in the JAR. The actual resources are generated,
  and that's what `src/main/generated-resources` is for.
* The general directory layout is as follows. I might change this later, but
  for now I wanted to keep it straight-forward.
  * `src/main/frontend/src` - All CSS and JavaScript source files.
  * `src/main/frontend/bundles` - Bundles that will end up in the JAR file. Each
    bundle imports those parts of the src directory that it needs.
  * `src/main/frontend/build/build.mjs` - The configuration for ESBuild that
    tells ESBuild to process the bundles. 
* All external libraries where I could confirm that we made no changes relative
  to the official version on NPM are now included via NPM. This reduces the amount
  of code in our repo, makes it easier to know which version we are using, and allows
  dependabot to bump versions. It also makes it easy to know which external libraries
  we are using, and it would even be possible to generate a licenses file from that.
* For external libraries where we only made small changes are also loaded from NPM.
  Our changes are applied as a diff file, using Yarn's builtin
  [patch protocol](https://yarnpkg.com/features/patching).
* Resources such as fonts and images in CSS files can now be referenced normally
  via relative paths (`./images/loader.gif`). I added an ESBuild plugin that
  converts the URLs to EL resource expression
  (`#{resource['primefaces:fileupload/images/loader.gif']}`).


These files don't seem to be used anymore, can they be removed?

* primefaces/src/main/resources/META-INF/resources/primefaces/log/icons.png
* primefaces/src/main/resources/META-INF/resources/primefaces/log/panel.png
* primefaces/src/main/resources/META-INF/resources/primefaces/rating/delete.gif
* primefaces/src/main/resources/META-INF/resources/primefaces/rating/star.gif