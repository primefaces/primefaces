//@ts-check

const { join, resolve } = require("path");

const LineBreakPattern = /\r\n|\r|\n/g;

const LineBreak = "\n";

/** @type {{ encoding: BufferEncoding | null; withFileTypes: true }} */
const ReadDirOpts = {
    encoding: "utf8",
    withFileTypes: true,
};

/** @type {{ encoding: BufferEncoding, flag: import("fs").OpenMode}} */
const WriteFileOpts = {
    encoding: "utf8",
    flag: "w",
}

/** @type {{encoding: "utf8", flag: string}} */
const ReadFileOpts = {
    encoding: "utf8",
    flag: "r",
};

const CharCodes = {
    CarriageReturn: "\r".charCodeAt(0),
    FormFeed: "\f".charCodeAt(0),
    LineFeed: "\n".charCodeAt(0),
    Space: " ".charCodeAt(0),
    Tab: "\t".charCodeAt(0),
    VerticalTab: "\v".charCodeAt(0),
};

/** @type {Record<number, boolean | undefined>} */
const WhiteSpaceChars = {
    [CharCodes.CarriageReturn]: true,
    [CharCodes.FormFeed]: true,
    [CharCodes.LineFeed]: true,
    [CharCodes.Space]: true,
    [CharCodes.Tab]: true,
    [CharCodes.VerticalTab]: true,
};

const Indentation = "    ";

const DefaultNpmRegistry = "https://registry.npmjs.com";

const Names = {
    PrimeFacesDeclaration: "PrimeFaces.d.ts",
    PrimeFacesModuleDeclaration: "PrimeFaces-module.d.ts",
    NpmPackageJson: "package.json",
    NpmReadme: "README.md",
}

const Paths = {
    AggregateTestDir: resolve(join(__dirname, "..", "specs", "aggregate")),
    BlacklistPath: resolve(join(__dirname, "..", "blacklist.txt")),
    ComponentsTestDir: resolve(join(__dirname, "..", "specs", "components")),
    ComponentsMainDir: resolve(join(__dirname, "..", "..", "resources", "META-INF", "resources", "primefaces")),
    CoreJsfDeclarationFile: resolve(join(__dirname, "..", "core-jsf.d.ts")),
    CoreDeclarationFile: resolve(join(__dirname, "..", "core.d.ts")),
    EsLintRcPath: resolve(__dirname, "..", "eslintrc.js"),
    JsdocReadmePath: resolve(__dirname, "..", "JSDOC.md"),
    NpmNodeModulesDir: resolve(__dirname, "..", "node_modules"), 
    NpmTypesDir: resolve(__dirname, "..", "node_modules", "@types"), 
    NpmRootDir: resolve(__dirname, ".."),
    NpmVirtualDeclarationFile: {
        ambient: resolve(__dirname, "..", "src", Names.PrimeFacesDeclaration),
        module: resolve(__dirname, "..", "src", Names.PrimeFacesModuleDeclaration),
    },
    PackageJsonFile: resolve(join(__dirname, "..", "package.json")),
    PrimeFacesDeclarationFile: resolve(join(__dirname, "..", "..", "..", "..", "docs", Names.PrimeFacesDeclaration)),
    PrimeFacesModuleDeclarationFile: resolve(join(__dirname, "..", "..", "..", "..", "docs", Names.PrimeFacesModuleDeclaration)),
    ProjectRootDir: resolve(__dirname, "..", "..", "..", ".."),
    TargetTestDir: resolve(join(__dirname, "..", "dist", "test")),
    TargetMainDir: resolve(join(__dirname, "..", "..", "..", "..", "target", "generated-resources", "type-definitions")),
    TsConfigPath: resolve(__dirname, "..", "tsconfig.json"),
    TsConfigTypedocPath: resolve(__dirname, "..", "tsconfig.typedoc.json"),
    TsProcessTestDir: resolve(join(__dirname, "..", "specs", "tsprocess")),
    TsValidateTestDir: resolve(join(__dirname, "..", "specs", "tsvalidate")),
}

// Must be all lower-case
const Tags = {
    Abstract: "abstract",
    Async: "async",
    Author: "author",
    Class: "class",
    Const: "const",
    Constant: "constant",
    Constructor: "constructor",
    Copyright: "copyright",
    Default: "default",
    Deprecated: "deprecated",
    Exception: "exception",
    Exclude: "exclude",
    Extends: "extends",
    Func: "func",
    Function: "function",
    Generator: "generator",
    Ignore: "ignore",
    Implements: "implements",
    Include: "include",
    InheritDoc: "inheritdoc",
    Interface: "interface",
    Internal: "internal",
    License: "license",
    Method: "method",
    Methodtemplate: "methodtemplate",
    Namespace: "namespace",
    Next: "next",
    Override: "override",
    Overrides: "overrides",
    Param: "param",
    Pattern: "pattern",
    Private: "private",
    Protected: "protected",
    Prop: "prop",
    Property: "property",
    Public: "public",
    Readonly: "readonly",
    Return: "return",
    Returns: "returns",
    See: "see",
    Since: "since",
    Structure: "structure",
    Template: "template",
    This: "this",
    Throws: "throws",
    Todo: "todo",
    Type: "type",
    Typedef: "typedef",
    Typeparam: "typeparam",
    Version: "version",
    Yield: "yield",
    Yields: "yields",
};

/**
 * Tags for which the name has a semantic meaning and does not belong to the descrption.
 */
const NamedTags = new Set([
    Tags.Class,
    Tags.Const,
    Tags.Constant,
    Tags.Extends,
    Tags.Func,
    Tags.Function,
    Tags.Implements,
    Tags.Interface,
    Tags.Method,
    Tags.Namespace,
    Tags.Param,
    Tags.Pattern,
    Tags.Prop,
    Tags.Property,
    Tags.See,
    Tags.Structure,
    Tags.Template,
    Tags.Typedef,
]);

/**
 * Tags of which only one with the same name and description may be present.
 */
const UniqueDescriptionTags = new Set([
    Tags.Author,
    Tags.Todo,
    Tags.See,
]);

/**
 * Tags of which only one with the same name may be present.
 */
const UniqueNameTags = new Set([
    Tags.Exception,
    Tags.Throws,
]);

/**
 * Tags with content, but only at most one tag may be present.
 */
const UniqueTags = new Set([
    Tags.Copyright,
    Tags.Deprecated,
    Tags.License,
    Tags.Since,
    Tags.Version,
]);

/**
 * Tags that behave like a boolean flag, they are either present or absent.
 */
const FlaggingTags = new Set([
    Tags.Abstract,
    Tags.Async,
    Tags.Exclude,
    Tags.Generator,
    Tags.Ignore,
    Tags.InheritDoc,
    Tags.Internal,
    Tags.Override,
    Tags.Overrides,
    Tags.Private,
    Tags.Protected,
    Tags.Public,
    Tags.Readonly,
]);

/**
 * @type {import("acorn").Options}
 */
const DefaultParseOptions = {
    allowImportExportEverywhere: true,
    allowHashBang: true,
    allowReserved: true,
    allowReturnOutsideFunction: true,
    ecmaVersion: 10,
    locations: true,
    preserveParens: false,
    sourceType: "script",
};

module.exports = {
    CharCodes,
    DefaultNpmRegistry,
    DefaultParseOptions,
    FlaggingTags,
    Indentation,
    LineBreak,
    LineBreakPattern,
    Names,
    NamedTags,
    Paths,
    ReadDirOpts,
    ReadFileOpts,
    Tags,
    UniqueDescriptionTags,
    UniqueNameTags,
    UniqueTags,
    WhiteSpaceChars,
    WriteFileOpts
};