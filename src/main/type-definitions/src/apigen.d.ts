
// Contains all types that are used by the *.js files via doc comment annotations

// --------------   MESSAGE TYPES   ----------------- //

interface SeveritySettingsConfig {
    unsupportedTag: SeverityLevel,

    codeDuplicateProperty: SeverityLevel;
    codeDuplicateMethod: SeverityLevel;

    docOnGetterAndSetter: SeverityLevel;
    docParseError: SeverityLevel;

    namespaceInvalidMethodSignature: SeverityLevel;

    tsDiagnosticsError: SeverityLevel;
    tsDiagnosticsWarning: SeverityLevel;
    tsDiagnosticsSuggestion: SeverityLevel;
    tsDiagnosticsMessage: SeverityLevel;
    tsSuperfluousOverride: SeverityLevel;
    tsMissingParentDoc: SeverityLevel;
    tsMissingOverride: SeverityLevel;

    tagTooFewParams: SeverityLevel;
    tagInitializerForRest: SeverityLevel;
    tagNotPlain: SeverityLevel;
    tagOptionalForRest: SeverityLevel;
    tagNameInvalidIndex: SeverityLevel;
    tagInvalidPattern: SeverityLevel;
    tagTypeOnDestructuredParameter: SeverityLevel;
    tagReadonlyWithSetter: SeverityLevel;

    tagConflictingConstantType: SeverityLevel;
    tagConflictingParamInitializers: SeverityLevel;
    tagConflictingPatternInitializers: SeverityLevel;
    tagConflictingMethodAndProp: SeverityLevel;
    tagConflictingMethodInComments: SeverityLevel;
    tagConflictingPropInComments: SeverityLevel;

    tagMissingConstant: SeverityLevel;
    tagMissingIdentParameter: SeverityLevel;
    tagMissingFunction: SeverityLevel;
    tagMissingMethod: SeverityLevel;
    tagMissingName: SeverityLevel;
    tagMissingNext: SeverityLevel;
    tagMissingParam: SeverityLevel;
    tagMissingParamForStructure: SeverityLevel;
    tagMissingParentProp: SeverityLevel;
    tagMissingPattern: SeverityLevel;
    tagMissingReturn: SeverityLevel;
    tagMissingStructure: SeverityLevel;
    tagMissingType: SeverityLevel;
    tagMissingTypedef: SeverityLevel;
    tagMissingYield: SeverityLevel;

    tagDuplicateAbstract: SeverityLevel;
    tagDuplicateAsync: SeverityLevel;
    tagDuplicateClassOrInterface: SeverityLevel;
    tagDuplicateConstructor: SeverityLevel;
    tagDuplicateDefault: SeverityLevel;
    tagDuplicateExtends: SeverityLevel;
    tagDuplicateGenerator: SeverityLevel;
    tagDuplicateImplements: SeverityLevel;
    tagDuplicateMethod: SeverityLevel;
    tagDuplicateNext: SeverityLevel;
    tagDuplicateParameter: SeverityLevel;
    tagDuplicatePattern: SeverityLevel;
    tagDuplicateReadonly: SeverityLevel;
    tagDuplicateReturn: SeverityLevel;
    tagDuplicateStructure: SeverityLevel;
    tagDuplicateTemplate: SeverityLevel;
    tagDuplicateThis: SeverityLevel;
    tagDuplicateType: SeverityLevel;
    tagDuplicateVisibilityModifier: SeverityLevel;
    tagDuplicateYield: SeverityLevel;

    tagSuperfluousReturn: SeverityLevel;
    tagSuperfluousParameter: SeverityLevel;
    tagSuperfluousDesc: SeverityLevel;
    tagSuperfluousNext: SeverityLevel;
    tagSuperfluousType: SeverityLevel;
    tagSuperfluousPattern: SeverityLevel;
    tagSuperfluousYield: SeverityLevel;
    tagSuperfluousTypedef: SeverityLevel;

    propDuplicateIfaceOrClass: SeverityLevel;
    propInvalidIfaceOrClass: SeverityLevel;

    tagOverriddenMissingDesc: SeverityLevel;
    symbolOverriddenMissingDesc: SeverityLevel;
    symbolMissingDesc: SeverityLevel;
    propMissingDesc: SeverityLevel;
    tagParamMissingDesc: SeverityLevel;
    tagReturnMissingDesc: SeverityLevel;
    tagTemplateMissingDesc: SeverityLevel;
    tagTypedefMissingDesc: SeverityLevel;
    tagMissingDesc: SeverityLevel;
}


// -----------------   TYPES   -------------------- //

type AcornSyntaxError = {
    loc: {
        line: number,
        column: number,
    },
    raisedAt: number,
    pos: number
} & Error;

type ArgumentInfo = ArgArray | ArgObject | ArgParam;

type ConstantSignatureConverter = (signature: ConstantSignature) => string[];

type ConstantType = "constant" | "let";

type DeclarationFileType = "module" | "ambient";

type DocumentableHandler = (
    node: import("estree").Node,
    program: CommentedAst<import("estree").Program>,
    inclusionHandler: InclusionHandler,
    severitySettings: SeveritySettingsConfig,
) => DocumentableHandlerResult | undefined;

type ExportInfoType = "interface" | "class" | "namespace" | "unspecified";

type Json = null | boolean | number | string | JsonArray | JsonObject;
type JsonArray = Json[];
type JsonObject = {[key: string]: Json};

type MessageFactory = (message: string) => string;

type MethodSignatureConverter = (signature: MethodSignature, ambientContext: boolean) => string[];

type PropSignatureConverter = (signature: PropSignature, typeReferencesNamespace: boolean) => string[];

type Result<T> = undefined | void | T | Promise<T | undefined>;

type ResultType<T extends (...args: any) => any> = T extends (...args: any) => Result<infer R> ? R : unknown;

type ResultFactory<T> = undefined | (() => Result<T>);

type SeverityLevel = "fatal" | "error" | "warning" | "ignore" | "info";

type StartTestFn = (cliArgs: TestCliArgs) => Promise<void>;

type ParametersFromSecond<T extends (x: any, ...args: any) => any> = T extends (x: any, ...args: infer P) => any ? P : never;

type AsyncReturnType<T extends (...args: any) => any> = T extends (...args: any) => Promise<infer R> ? R : any;

type VisibilityModifier = "public" | "private" | "protected";

type TsType = import("typescript").Type;
type TsSignature = import("typescript").Signature;
type TsNode = import("typescript").Node;
type ConstructorLikeNode = import("typescript").ConstructSignatureDeclaration | import("typescript").ConstructorDeclaration;
type TypeLikeNode = import("typescript").ClassLikeDeclaration | import("typescript").InterfaceDeclaration;
type MethodLikeNode = import("typescript").MethodSignature | import("typescript").MethodDeclaration;
type CallOrMethodLikeNode = CallLikeNode | MethodLikeNode;
type CallLikeNode = import("typescript").CallSignatureDeclaration;
type PropertyLikeNode = import("typescript").PropertySignature | import("typescript").PropertyDeclaration;

type TypedefTagHandler =
    /**
     * @param tag A tag of a type definition to handle.
     * @param tags A list of all tags of the typedef.
     * @param logMissing Whether to log an error if no typedef was found.
     */
    (tag: import("comment-parser").Tag, allTags: import("comment-parser").Tag[], logMissing: boolean) => boolean;

// ----------------- INTERFACES ------------------- //

interface ArgArray {
    initializer: string;
    required: boolean;
    rest: boolean;
    type: "array";
    typedef: string;
    node: import("estree").ArrayPattern;
}

interface ArgObject {
    initializer: string;
    required: boolean;
    rest: boolean;
    type: "object";
    typedef: string;
    node: import("estree").ObjectPattern;
}

interface ArgParam {
    type: "param";
    typedef: string;
    rest: boolean;
    required: boolean;
    initializer: string;
    name: string;
}

interface ArgSignature {
    type: ArgumentInfo["type"];
    typedef: string;
    param: string;
    rest: string;
    optional: string;
    initializer: string;
}

/** Files to include in the package to publish. Key is path to file, value is file content. */
type NpmPublishFiles = Record<string, string>;

interface PublishCliArgs {
    /** Either `public` or `protected`. */
    access: import("npm-registry-client").Access;
    /** Whether to prompt the user if they did pass credentials via the CLI. */
    askForMissingCredentials: boolean;
    /** Credentials for publishing. */
    credentials: import("npm-registry-client").Credentials;
    /** Path to the directory with the generated declaration files. */
    declarations: {
        /** Entry declaration file */
        entry: string,
        /** More declaration files referenced by the entry declaration file */
        referenced: string[],
    };
    /** If set, do not publish to NPM, but only write tarball to this file. */
    dryRun?: string;
    /** package.json fields to exclude */
    excludedPackageJsonFields: string[];
    /** Extra files to include in the published package */
    extraFiles: string[];
    /** Path to the package.json file to use */
    packageJson: string;
    /** Path to a file to be used as the README.md for the package */
    readme?: string;
    /** URL to the npm registry. */
    registry: string;
    /** Root directory to which other paths are relative to. */
    rootDir: string;
    /** Version to publish */
    version: {
        major?: number;
        minor?: number;
        patch?: number;    
    },
}

interface BuildCommentMapState {
    map: Map<import("estree").Node, CommentData[]>;
    index: number;
    len: number;
    start: number;
    comments: CommentData[];
}

interface CliArgs {
    /**
     * A comma-separated list of JSDoc tags that, when present, result in a symbol being excluded from the
     * generated documentation, such as `private,hidden`.
     */
    exclusionTags: string[],
    /**
     * A comma separated list of third-party node modules that should be included in the generated documentation, such
     * as `jquery,chart.js`. Defaults to reading the dependencies of the `package.json`.
     */
    additionalEntries: string[],
    /**
     * Path to the directory with the individual components. Child directories must be the widget folders.
     */
    inputDir: string;
    /**
     * Directory where the type declaration output file(s) are written to.
     */
    declarationOutputDir: string;
    /**
     * Filename (without path) of the generated `*.d.ts` file
     */
    outputFilename: string;
    /**
     * Path of the package.json file of the project.
     */
    packageJson: string;
    /**
     * Filename (without path) of the generated `*.module.d.ts` file
     */
    moduleOutputFilename: string;
    /** Root directory of the project (e.g. maven project) */
    rootDir: string;
    /**
     * Overrides for the default severity settings.
     */
    severitySettings: Partial<SeveritySettingsConfig>;
    /**
     * Path where the typedoc are written to.
     */
    typedocOutputDir: string;
    /**
     * `true` to skip the generation of typedocs from the type declaration file, `false` otherwise.
     */
    skipTypedocGenerate: boolean;
    /**
     * `true` to skip running `eslint-typescript` on the generated type declaration file, `false` otherwise.
     */
    skipEsLint: boolean;
    /**
     * `true` to skip the post process pass of the declaration file, `false` otherwise. The postprocess pass resolves
     * `@inheritdoc` annotations.
     */
    skipPostProcess: boolean;
    /**
     * `true` to make the output more verbose, `false` otherwise.
     */
    verbose: boolean;
}

interface TestCliArgs {
    items: string[];
    tests: string[];
    verbose: boolean;
}

interface CodeConstant {
    declaration: import("estree").VariableDeclaration | undefined;
    name: string;
    jsdoc: import("comment-parser").Comment;
    node: import("estree").Node;
}

interface CodeWithDocComment {
    signature: PropSignature;
    doc: string[];
}

interface Component {
    files: string[];
    name: string;
    path: string;
    typedefFiles: string[];
}

interface CommentedAst<TNode extends import("estree").Node> {
    node: TNode;
    comments: CommentData[];
    map: Map<import("estree").Node, CommentData[]>;
}

interface CommentData {
    isBlockComment: boolean;
    content: string;
    start: number;
    end: number;
}

interface ConstantDoc {
    doc: string[];
    signature: ConstantSignature;
}

interface CommentDocInfo {
    docs: ObjectShapeDoc | undefined;
    nested: Map<string, CommentDocInfo>;
}

interface ConstantCodeInfo {
    name: string;
    optional: boolean;
    type: ConstantType | undefined,
    typedef: string;
}

interface ConstantDocInfo {
    additionalTags: import("comment-parser").Tag[];
    description: string;
    name: string,
    optional: boolean;
    type: ConstantType | undefined;
    typedef: string;
    typedefs: DocInfoTypedef[];
}

interface ConstantDocResult {
    docs: string[];
    typedefs: DocInfoTypedef[];
}

interface ConstantDef {
    comments: CommentData[];
    declaration: import("estree").VariableDeclaration | undefined;
    kind: "constant";
    name: string;
    namespace: string[];
    node: import("estree").Node;
    variableKind: ConstantType | undefined,
}

interface ConstantInfo {
    name: string;
    description: string;
}

interface ConstantSignature {
    constantType: ConstantType;
    name: string;
    type: string;
}

interface DestructuringInfo {
    pattern: import("comment-parser").Tag | undefined;
    structure: import("comment-parser").Tag | undefined;
}

interface DocInfoNext {
    typedef: string;
    hasNext: boolean;
    description: string;
}

interface DocInfoReturn {
    typedef: string;
    hasReturn: boolean;
    description: string;
}

interface DocInfoPattern {
    description: string;
    index: number;
    initializer: string;
    required: boolean;
    typedef: string;
}

interface DocInfoTemplate {
    description: string;
    extends: string;
    initializer: string;
    name: string;
}

interface DocInfoTypedef {
    description: string;
    function: TypedefFunctionInfo | undefined;
    name: string;
    node: import("estree").Node;
    templates: Map<string, import("comment-parser").Tag>;
    typedef: string;
}

interface DocInfoVariable {
    name: string;
    typedef: string;
    initializer: string;
    required: boolean;
    description: string;
}

interface DocInfoYield {
    typedef: string;
    hasYield: boolean;
    description: string;
}

interface DocResult {
    external: string[];
    internal: string[];
}

interface DocumentableInfo {
    constants: ConstantDef[],
    functions: FunctionDef[];
    objects: ObjectDef[];
}

interface DocumentableHandlerResult {
    constants: ConstantDef[],
    functions: FunctionDef[];
    objects: ObjectDef[];
    recurse: boolean;
}

interface DocumentableState {

}

interface ExportInfo {
    description: string,
    name: string;
    type: ExportInfoType;
}

interface FunctionDef {
    comments: CommentData[];
    functionNode: import("estree").FunctionExpression | import("estree").FunctionDeclaration;
    method: import("estree").MethodDefinition | undefined;
    kind: "function",
    name: string;
    namespace: string[];
    spec: TypeSpec | undefined;
}

interface TypeSpec {
    abstract: boolean;
    additionalTags: import("comment-parser").Tag[];
    description: string,
    extends: Set<string>;
    generics: DocInfoTemplate[]
    implements: Set<string>;
    type: Exclude<ExportInfoType, "namespace">;
}

interface InclusionHandler {
    isIncludeFunction(jsdoc: import("comment-parser").Comment | undefined, functionName: string): boolean;
    isIncludeProperty(jsdoc: import("comment-parser").Comment | undefined, propertyName: string): boolean;
    isIncludeType(jsdoc: import("comment-parser").Comment | undefined, typeName: string): boolean;
    isIncludeVariable(jsdoc: import("comment-parser").Comment | undefined, variableName: string): boolean;
}

interface IndentSpec {
    close: string[];
    indent: number;
    open: string[];
}

interface InsertionOrderMap<K, V> extends Map<K, V> {
    toArray(): V[];
}

interface MemberPathState {
    path: string[];
    valid: boolean;
}

interface MethodCodeInfo {
    abstract: boolean;
    arguments: ArgumentInfo[];
    canCompleteNormally: boolean;
    isAsync: boolean;
    isConstructor: boolean;
    isGenerator: boolean;
    generics: DocInfoTemplate[];
    name: string;
    next: NextInfo,
    return: ReturnInfo;
    thisTypedef: string;
    variables: VariableInfo[];
    visibility: VisibilityModifier | undefined;
    yield: YieldInfo,
}

interface MethodDoc {
    doc: string[];
    signature: MethodSignature;
}

interface MethodDocInfo {
    abstract: boolean,
    constructor: boolean,
    additionalTags: import("comment-parser").Tag[];
    description: string;
    next: DocInfoNext;
    patterns: Map<number, DocInfoPattern>;
    return: DocInfoReturn;
    templates: DocInfoTemplate[];
    thisTypedef: string;
    typedefs: DocInfoTypedef[];
    variables: Map<string, DocInfoVariable>;
    yield: DocInfoYield;
    visibility: VisibilityModifier | undefined;
}

interface MethodDocResult {
    docs: string[];
    typedefs: DocInfoTypedef[];
}

interface MethodDocShape extends TypedefFunctionInfo {
    abstract: boolean;
    constructor: boolean;
    method: import("comment-parser").Tag | undefined;
    name: string;
    visibility: VisibilityModifier | undefined;
}

interface MethodSignature {
    abstract: string;
    args: ArgSignature[];
    async: string;
    constructor: boolean;
    generator: string;
    generics: string;
    name: string;
    returnType: string;
    visibility: VisibilityModifier;
}

interface NamespacedName {
    name: string;
    namespace: string[],
}

interface NamespacedType {
    ifaceSpec: IndentSpec | undefined;
    namespaceSpec: IndentSpec | undefined;
    name: string | undefined;
    namespace: string[];
}

interface NamespaceSpec {
    additionalTags: import("comment-parser").Tag[];
    description: string,
}

interface ObjectCode {
    componentName: string;
    jsdoc: import("comment-parser").Comment;
    methods: ObjectCodeMethod[];
    node: import("estree").Node;
    properties: ObjectCodeProperty[];
}

interface ObjectCodeMethod {
    name: string;
    jsdoc: import("comment-parser").Comment;
    node: import("estree").FunctionExpression | import("estree").FunctionDeclaration;
    method: import("estree").MethodDefinition | undefined;
}

interface ObjectCodeProperty {
    getter: ObjectCodePropertyAccessor | undefined;
    jsdoc: import("comment-parser").Comment;
    name: string;
    node: import("estree").Expression;
    nodeProperty: import("estree").Property,
    setter: ObjectCodePropertyAccessor | undefined;
}

interface ObjectCodePropertyAccessor {
    hasComment: boolean;
    jsdoc: import("comment-parser").Comment;
    name: string;
    node: import("estree").FunctionExpression;
}

interface ObjectDef {
    classDefinition: import("estree").ObjectExpression;
    comments: CommentData[];
    kind: "object",
    name: string;
    namespace: string[];
    spec: TypeSpec;
}

interface ObjectDocInfo {
    description: string;
    shape: ObjectDocShape;
    typedefs: DocInfoTypedef[]
}

interface ObjectDocShape {
    abstract: boolean;
    additionalTags: import("comment-parser").Tag[];
    constants: ConstantInfo[];
    jsdoc: import("comment-parser").Tag | undefined;
    export: ExportInfo;
    extends: Set<string>;
    implements: Set<string>;
    method: MethodDocShape | undefined;
    name: string;
    optional: boolean;
    props: Map<string, ObjectDocShape>;
    readonly: boolean;
    templates: InsertionOrderMap<string, import("comment-parser").Tag>;
    visibility: VisibilityModifier | undefined;
}

interface ObjectShapeDoc {
    abstract: boolean;
    additionalTags: import("comment-parser").Tag[];
    constants: ConstantInfo[];
    export: ExportInfo;
    extends: Set<string>;
    hasProperty: boolean;
    implements: Set<string>;
    method: MethodDoc | undefined;
    name: string;
    optional: boolean;
    props: Map<string, CodeWithDocComment>;
    readonly: boolean,
    templates: DocInfoTemplate[];
    typedef: string;
}

interface PropCodeInfo {
    name: string;
    optional: boolean;
    readonly: boolean;
    typedef: string;
    visibility: VisibilityModifier | undefined;
}

interface PropDocInfo {
    additionalTags: import("comment-parser").Tag[];
    constants: ConstantInfo[],
    description: string;
    name: string;
    optional: boolean;
    readonly: boolean;
    subObject: undefined | {
        description: string,
        tags: import("comment-parser").Tag[],
        type: Exclude<ExportInfoType, "unspecified">,
    };
    typedef: string;
    typedefs: DocInfoTypedef[];
    visibility: VisibilityModifier | undefined;
}

interface PropDocResult {
    docs: string[];
    external: string[],
    subObject: PropSubObject | undefined;
    typedefs: DocInfoTypedef[];
}

interface PropSignature {
    name: string;
    optional: boolean;
    readonly: boolean;
    type: string;
    visibility: VisibilityModifier;
}

interface PropSubObject {
    description: string,
    name: string,
    objectNode: import("estree").ObjectExpression,
    propertyNode: import("estree").Property,
    tags: import("comment-parser").Tag[],
}

interface ReturnInfo {
    typedef: string;
    node: import("estree").ReturnStatement | undefined;
}

interface ReturnSpec {
    async: boolean;
    baseTypeNext: string;
    baseTypeReturn: string;
    baseTypeYield: string;
    constructor: boolean;
    generator: undefined | {
        hasNext: boolean,
        hasReturn: boolean,
        hasYield: boolean,
    },
}

interface TsDiagnostics {
    errors: string[];
    messages: string[];
    suggestions: string[];
    warnings: string[];
}

type TsSourceComment = TsSourceCommentPlain | TsSourceCommentDoc;

interface TsSourceCommentPlain {
    kind: "MultiLine" | "SingleLine";
    text: (fullText: string) => string;
}

interface TsSourceCommentDoc {
    kind: "DocComment";
    jsdoc: (fullText: string) => import("comment-parser").Comment;
}

interface TsPostProcessingHooks {
    compilerOptions: Partial<TsHookCompilerOptions>,
    compilerHost: Partial<TsHookCompilerHost>,
    emit: Partial<TsHookEmit>,
    process: Partial<TsHookProcess>;
    validateProgram: Partial<TsHookValidateProgram>;
}

type TsCallSignatureIndex = string;
type TsHookFnValidateReport = (validationMessage: TsGroupedValidationMessages) => Result<Error | Error[]>;
type TsHookFnValidateProgram = (program: import("typescript").Program, sourceFiles: import("typescript").SourceFile[], docCommentAccessor: TsDocCommentAccessor) => Result<TsValidationMessage | TsValidationMessage[]>;
type TsHookFnEmitTransform = (node: import("typescript").Node) => import("typescript").Node;
type TsHookFnCompilerHostCreate = (options: import("typescript").CompilerOptions, sourceFiles: TypeDeclarationBundleFiles) => Result<import("typescript").CompilerHost>;
type TsHookFnCompilerOptionsCreate = () => Result<import("typescript").CompilerOptions>;
type TsHookFnCompilerOptionsModify = (options: import("typescript").CompilerOptions) => Result<import("typescript").CompilerOptions>;
type TsHookFnProcessAst = (program: import("typescript").Program, sourceFiles: import("typescript").SourceFile[], docCommentAccessor: TsDocCommentAccessor, severitySettings: SeveritySettingsConfig) => void;

interface TsValidationMessage {
    message: string,
    severity: "warning" | "suggestion" | "message" | "error";
    type: keyof SeveritySettingsConfig;
}

interface TsCallSignatureLookup {
    indexToName: Map<TsCallSignatureIndex, string>;
    nameToIndex: Map<string, TsCallSignatureIndex>;
}

interface TsCreateSignatureLookupStackItem {
    bindingPattern: import("typescript").BindingPattern;
    path: string[];
}

interface TsGroupedValidationMessages {
    errors: TsValidationMessage[];
    messages: TsValidationMessage[];
    suggestions: TsValidationMessage[];
    warnings: TsValidationMessage[];
}

interface TsHookEmit {
    transform: TsHookFnEmitTransform[];
}

interface TsHookProcess {
    processAst: TsHookFnProcessAst[];
}

interface TsHookValidateProgram {
    report: TsHookFnValidateReport[];
    validate: TsHookFnValidateProgram[];
}

interface TsHookCompilerHost {
    createCompilerHost: TsHookFnCompilerHostCreate;
}

interface TsHookCompilerOptions {
    createCompilerOptions: TsHookFnCompilerOptionsCreate;
    /**
     * Handlers that may modify the compiler options, either by directly modifying the options or by returning a new
     * options object.
     */
    modifyCompilerOptions: TsHookFnCompilerOptionsModify[];
}

interface TsDocCommentAccessor {
    getClosestDocComment(node: import("typescript").Node): import("comment-parser").Comment | undefined;
    getDocCommentCount(node: import("typescript").Node): number;
    getDocComment(node: import("typescript").Node, index: number): import("comment-parser").Comment | undefined;
    getDocComments(node: import("typescript").Node): import("comment-parser").Comment[] | undefined;
    hasDocComment(node: import("typescript").Node): boolean;
    replaceClosestDocComment(node: import("typescript").Node, commment: import("comment-parser").Comment): void;
}

interface TsDocCommentHandler extends TsDocCommentAccessor {
    applyToSourceFile(sourceFile: import("typescript").SourceFile): void;
}

interface TsDocCommentWithPos {
    pos: number;
    doc: import("comment-parser").Comment;
}

type TsOverridenMethodsResult = Map<TsType, CallOrMethodLikeNode[]>;

interface TsNodeMembersResult {
    constructors: ConstructorLikeNode[];
    methods: Map<string, CallOrMethodLikeNode[]>;
    properties: Map<string, PropertyLikeNode[]>;
}

interface TypeDeclarationBundleContent {
    ambient: string[];
    module: string[];
}

interface TypeDeclarationBundleFiles {
    ambient: string;
    module: string;
}

interface TypeDeclarationBundleSourceFiles {
    ambient: import("typescript").SourceFile;
    module: import("typescript").SourceFile;
}

interface TypedefFunctionInfo {
    async: boolean;
    constructor: boolean;
    destructuring: Map<number, DestructuringInfo>;
    generator: boolean;
    next: import("comment-parser").Tag | undefined;
    node: import("estree").Node;
    params: InsertionOrderMap<string, import("comment-parser").Tag>;
    return: import("comment-parser").Tag | undefined;
    templates: Map<string, import("comment-parser").Tag>;
    thisTypedef: string;
    yield: import("comment-parser").Tag | undefined;
}

interface TypedefResult {
    external: string[];
    internal: string[];
}

interface TypedefTagHandlers {
    createEmpty: () => TypedefFunctionInfo;
    async: TypedefTagHandler;
    _constructor: TypedefTagHandler;
    generator: TypedefTagHandler;
    methodtemplate: TypedefTagHandler;
    next: TypedefTagHandler;
    params: TypedefTagHandler;
    return: TypedefTagHandler;
    template: TypedefTagHandler;
    structureOrPattern: TypedefTagHandler;
    this: TypedefTagHandler;
    yield: TypedefTagHandler;
}

interface VariableInfo {
    initializer: string;
    name: string;
    rest: boolean;
    start: number;
}

interface CollectToMapOpts<K, V> {
    filter: (key: K, value: V) => boolean;
    reducer: (oldValue: V, currentValue: V) => V;
}

interface NextInfo {
    typedef: string;
    node: import("estree").YieldExpression | undefined;
}

interface YieldInfo {
    typedef: string;
    node: import("estree").YieldExpression | undefined;
}

interface ResolveInheritDocState {
    docCommentAccessor: TsDocCommentAccessor,
    program: import("typescript").Program;
    methodNodes: Map<TypeLikeNode, TypeLikeNode>;
    methodEdges: Map<TypeLikeNode, Set<TypeLikeNode>>;
    typeChecker: import("typescript").TypeChecker;
    typeNodes: Map<TypeLikeNode, TypeLikeNode>;
    typeEdges: Map<TypeLikeNode, Set<TypeLikeNode>>;
}
interface ResolveInheritDocTagCollection {
    others: import("comment-parser").Tag[],
    flagging: import("comment-parser").Tag[];
    params: Map<string, import("comment-parser").Tag>;
    returns: import("comment-parser").Tag[];
    templates: import("comment-parser").Tag[];
    typeparams: import("comment-parser").Tag[];
    unique: Map<string, import("comment-parser").Tag[]>;
    uniqueName: Map<string, import("comment-parser").Tag[]>;
    uniqueDescription: Map<string, string[]>;
}

interface MergeDocParamsBase {
    baseDoc: import("comment-parser").Comment;
    parentDoc: import("comment-parser").Comment;
    parentTypeName: string;
}

interface MergeDocParamsType extends MergeDocParamsBase {
    baseNode: TypeLikeNode;
    kind: "type";
    parentNode: TypeLikeNode;
}

interface MergeDocParamsMethod extends MergeDocParamsBase {
    baseNode: CallOrMethodLikeNode;
    kind: "method";
    parentNode: CallOrMethodLikeNode;
}

type MergeDocParams = MergeDocParamsMethod | MergeDocParamsType;

interface LoadedTest {
    name: string;
    startTest: StartTestFn;
}