module.exports = {
    "env": {
        "browser": true,
        "es6": true,
        "node": true,
    },
    "parser": "@typescript-eslint/parser",
    "parserOptions": {
        "project": "tsconfig.json",
        "sourceType": "module",
    },
    "plugins": [
        "@typescript-eslint/eslint-plugin",
        "@typescript-eslint/tslint",
        "import",
        "jsdoc",
        "eslint-comments"
    ],
    "settings": {
        "jsdoc": {
            "overrideReplacesDocs": false,
            "augmentsExtendsReplacesDocs": false,
            "implementsReplacesDocs": false,
            "tagNamePreference": {
                "returns": "return",
            },
        }
    },
    "rules": {
        // JSDoc rules
        "jsdoc/check-alignment": "error",
        "jsdoc/check-examples": "off",
        "jsdoc/check-indentation": "off",
        "jsdoc/check-param-names": "error",
        "jsdoc/check-property-names": "error",
        "jsdoc/check-syntax": "off",
        "jsdoc/check-tag-names": ["error", {
            "definedTags": [
                "typeparam",
            ],
        }],
        "jsdoc/check-types": "error",

        "jsdoc/require-description": "error",
        "jsdoc/require-description-complete-sentence": "off",
        "jsdoc/require-example": "off",
        "jsdoc/require-hyphen-before-param-description": "off",
        "jsdoc/require-jsdoc": "error",
        "jsdoc/require-param": "error",
        "jsdoc/require-param-description": "error",
        "jsdoc/require-param-name": "error",
        "jsdoc/require-param-type": "off",
        "jsdoc/require-returns": "error",
        "jsdoc/require-returns-description": "error",
        "jsdoc/require-returns-check": "off",
        "jsdoc/require-returns-type": "off",

        "jsdoc/implements-on-classes": "off",
        "jsdoc/match-description": "error",
        "jsdoc/newline-after-description": "off",
        "jsdoc/no-types": "error",
        "jsdoc/no-undefined-types": "off",
        "jsdoc/valid-types": "off",

        // Typescript rules
        "@typescript-eslint/adjacent-overload-signatures": "off",
        "@typescript-eslint/array-type": "error",
        "@typescript-eslint/ban-ts-comment": "error",
        "@typescript-eslint/ban-types": ["error", {
            types: {
                "Object": {
                    message: "Avoid using the `Object` type. Prefer using a specific shape, such `{key: string}`?",
                    fixWith: "any",
                },
                "object": {
                    message: "Avoid using the `object` type. Prefer using a specific shape, such `{key: string}`?",
                    fixWith: "{[key: any]: any} | null",
                },
                "Boolean": {
                    message: "Avoid using the `Boolean` type. Did you mean `boolean`?",
                    fixWith: "boolean",
                },
                "Number": {
                    message: "Avoid using the `Number` type. Did you mean `number`?",
                    fixWith: "number",
                },
                "String": {
                    message: "Avoid using the `String` type. Did you mean `string`?",
                    fixWith: "string",
                },
                "Symbol": {
                    message: "Avoid using the `Symbol` type. Did you mean `symbol`?",
                    fixWith: "symbol",
                },
                "Function": {
                    message: "Avoid using the `Function` type. Prefer a specific function type, like `() => void`.",
                    fixWith: "((...args: any[]) => any)",
                },
            }
        }],
        "@typescript-eslint/class-name-casing": "off",
        "@typescript-eslint/explicit-member-accessibility": [
            "off",
            {
                "overrides": {
                    "constructors": "off"
                }
            }
        ],
        "@typescript-eslint/consistent-type-definitions": ["error", "interface"],
        "@typescript-eslint/indent": "off", // file is reformatted automatically
        "@typescript-eslint/interface-name-prefix": "off",
        "@typescript-eslint/member-delimiter-style": "error",
        "@typescript-eslint/no-empty-interface": "off",
        "@typescript-eslint/no-explicit-any": "off",
        "@typescript-eslint/no-extra-semi": ["error"],
        "@typescript-eslint/no-inferrable-types": "off",
        "@typescript-eslint/no-misused-new": "off",
        "@typescript-eslint/no-namespace": "off",
        "@typescript-eslint/no-non-null-assertion": "error",
        "@typescript-eslint/no-parameter-properties": "off",
        "@typescript-eslint/no-require-imports": "error",
        "@typescript-eslint/no-triple-slash-reference": "off",
        "@typescript-eslint/no-unnecessary-qualifier": "off",
        "@typescript-eslint/no-use-before-declare": "off",
        "@typescript-eslint/no-var-requires": "error",
        "@typescript-eslint/prefer-for-of": "error",
        "@typescript-eslint/prefer-function-type": "error",
        "@typescript-eslint/prefer-namespace-keyword": "error",
        "@typescript-eslint/type-annotation-spacing": "off",
        "@typescript-eslint/quotes": ["error", "double"],
        "@typescript-eslint/semi": ["error"],
        "@typescript-eslint/unified-signatures": "off",
        "@typescript-eslint/typedef": ["error"],

        // eslint-plugin-import rules
        "import/no-duplicates": ["error"],
        "import/no-default-export": ["error"],

        // eslint-comments/recommended rules
        "eslint-comments/disable-enable-pair": "error",
        "eslint-comments/no-aggregating-enable": "error",
        "eslint-comments/no-duplicate-disable": "error",
        "eslint-comments/no-unlimited-disable": "error",
        "eslint-comments/no-unused-disable": "error",
        "eslint-comments/no-unused-enable": "error",

        // General rules
        "arrow-body-style": "error",
        "arrow-parens": [
            "error",
            "as-needed",
        ],
        "capitalized-comments": ["error", "always", {
            "ignorePattern": "tslint",
        }],
        "spaced-comment": ["error", "always", {
            "block": {
                "balanced": true,
            },
            "line": {
                "markers": ["/"], // TS triple slash directive
            },
        }],
        "class-methods-use-this": "error",
        "complexity": "off",
        "constructor-super": "error",
        "curly": "error",
        "dot-notation": "error",
        "eol-last": "error",
        "guard-for-in": "error",
        "linebreak-style": [
            "error",
            "unix",
        ],
        "max-classes-per-file": "off",
        "max-lines": "off",
        "member-ordering": "off",
        "new-parens": "error",
        "newline-per-chained-call": "error",
        "no-bitwise": "error",
        "no-caller": "error",
        "no-cond-assign": "error",
        "no-console": "off",
        "no-debugger": "error",
        "no-empty": "error",
        "no-eval": "error",
        "no-extra-bind": "error",
        "no-extra-semi": "off", // see @typescript-eslint/no-extra-semi
        "no-fallthrough": "off",
        "no-invalid-this": "off",
        "no-irregular-whitespace": "error",
        "no-magic-numbers": "off",
        "no-multiple-empty-lines": "error",
        "no-new-wrappers": "error",
        "no-plusplus": [
            "error",
            {
                "allowForLoopAfterthoughts": true,
            },
        ],
        "no-trailing-spaces": "error",
        "no-throw-literal": "error",
        "no-undef-init": "off",
        "no-unsafe-finally": "error",
        "no-unused-labels": "error",
        "no-var": "error",
        "object-shorthand": "error",
        "one-var": "off",
        "prefer-const": "error",
        "prefer-template": "off",
        "quotes": "off", // see @typescript-eslint/quotes
        "quote-props": [
            "error",
            "as-needed",
        ],
        "radix": "error",
        "semi": "off", // see @typescript-eslint/semi
        "space-before-function-paren": "off",
        "use-isnan": "error",
        "valid-typeof": "off",
        "yoda": "off",

        // Old tslint rules - remove once supported by typescript-eslint
        "@typescript-eslint/tslint/config": [
            "error",
            {
                "rules": {
                    "align": true,
                    "completed-docs": false,
                    "encoding": true,
                    "invalid-void": true,
                    "number-literal-format": true,
                    "prefer-method-signature": true,
                },
            },
        ],
    },
};
