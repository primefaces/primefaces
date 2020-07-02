Contains patches for a few packages that fix issues that cannot be resolved
otherwise for now:

* autonumeric
    * Fixes TypeScript typings - they are wrong and do not compiled. This is
      fixed in the current master branch of autonumeric. Patch can be removed
      with the next release of autoNumeric.
* eslint-plugin-jsdoc
    * Patch makes the eslint plugin work with TypeScript's
      `declare function ...` See
      https://github.com/gajus/eslint-plugin-jsdoc/issues/512
