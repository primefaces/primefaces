# PrimeFaces

Here are the typings for the client-side JavaScript API, please visit
[PrimeFaces.org](https://www.primefaces.org) for more information on PrimeFaces.

[![PrimeFaces Logo](https://www.primefaces.org/wp-content/uploads/2016/10/prime_logo_new.png)](https://www.primefaces.org/showcase)

[PrimeFaces](https://www.primefaces.org/) is one of the most popular UI libraries in Java EE Ecosystem and widely used
by software companies, world renowned brands, banks, financial institutions, insurance companies, universities and more.
Here are [some of the users](https://www.primefaces.org/whouses) who notified us or subscribed to 
[PrimeFaces Support Service](https://www.primefaces.org/support).

This package contains the type declarations file for the client-side JavaScript part of PrimeFaces.

You can also [view the documentation](https://primefaces.github.io/primefaces/jsdocs/index.html) generated via TypeDoc
from this declaration file.

Finally, you may also  be interested in the [PrimeFaces user guide](https://primefaces.github.io/primefaces/).

## Type declarations

PrimeFaces is a global library that adds methods and constants to the global window scope. To use the type declarations
in a JavaScript or TypeScript file, use a
[triple-slash directive](https://www.typescriptlang.org/docs/handbook/triple-slash-directives.html#-reference-types-)
like this (must be at the top of the file):

```javascript
/// <reference types="primefaces" />

// Now your IDE or transpiler can issue a warning that "search" does not exist
// on a jQuery object. You can now also enjoy autocompletion.
PF("dataTable").jq.search("tbody td");
```

Or add it to the compiler settings in your `tsconfig.json`:

```json
{
    "compilerOptions": {
        "types": ["primefaces"]
    }
}
```
