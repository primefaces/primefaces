Contains type declaration for acorn-walk that make use of "estree".

May be fixed soon, see https://github.com/acornjs/acorn/issues/946

If you remove these type declarations, also remove the corresponding `paths`
entry in the `tsconfig.json`:

```json
{
    "compilerOptions": {
        // Remove from here
        "baseUrl": ".",
        "paths": {
            "acorn-walk": [
                "src/acorn-walk/index.d.ts"
            ]
        }
        // to here
    }
}
```