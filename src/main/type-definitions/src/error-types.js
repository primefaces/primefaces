//@ts-check

/** 
 * How warnings and checks are handles, eg missing type annotations etc.
 * - `fatal`: Throw an error and make the build fail.
 * - `error`: Log an error at the error level, but continue
 * - `warning`: Log the message at the warning level, and continue
 * - `info`: Log the message at the info level
 * - `level`: Ignore and proceed 
 * @type {{[P in SeverityLevel]: SeverityLevel}}
 */
const Level = {
    error: "error",
    fatal: "fatal",
    ignore: "ignore",
    info: "info",
    warning: "warning",
};

/** @type {SeveritySettingsConfig} */
const DefaultSeveritySettings = {
    unsupportedTag: Level.fatal,
    
    codeDuplicateProperty: Level.fatal,
    codeDuplicateMethod: Level.fatal,

    docOnGetterAndSetter: Level.fatal,
    docParseError: Level.fatal,

    namespaceInvalidMethodSignature: Level.fatal,

    tsDiagnosticsError: Level.fatal,
    tsDiagnosticsWarning: Level.warning,
    tsDiagnosticsSuggestion: Level.info,
    tsDiagnosticsMessage: Level.info,
    tsSuperfluousOverride: Level.fatal,
    tsMissingParentDoc: Level.fatal,
    tsMissingOverride: Level.fatal,

    tagTooFewParams: Level.fatal,
    tagInitializerForRest: Level.fatal,
    tagNotPlain: Level.fatal,
    tagOptionalForRest: Level.fatal,
    tagNameInvalidIndex: Level.fatal,
    tagInvalidPattern: Level.fatal,
    tagTypeOnDestructuredParameter: Level.fatal,
    tagReadonlyWithSetter: Level.fatal,

    tagConflictingConstantType: Level.fatal,
    tagConflictingParamInitializers: Level.fatal,
    tagConflictingPatternInitializers: Level.fatal,
    tagConflictingMethodAndProp: Level.fatal,
    tagConflictingMethodInComments: Level.fatal,
    tagConflictingPropInComments: Level.fatal,

    tagMissingConstant: Level.fatal,
    tagMissingIdentParameter: Level.fatal,
    tagMissingFunction: Level.fatal,
    tagMissingMethod: Level.fatal,
    tagMissingName: Level.fatal,
    tagMissingNext: Level.fatal,
    tagMissingParam: Level.fatal,
    tagMissingParamForStructure: Level.fatal,
    tagMissingParentProp: Level.fatal,
    tagMissingPattern: Level.fatal,
    tagMissingReturn: Level.fatal,
    tagMissingStructure: Level.fatal,
    tagMissingType: Level.fatal,
    tagMissingYield: Level.fatal,
    tagMissingTypedef: Level.fatal,

    tagDuplicateAbstract: Level.fatal,
    tagDuplicateAsync: Level.fatal,
    tagDuplicateClassOrInterface: Level.fatal,
    tagDuplicateConstructor: Level.fatal,
    tagDuplicateDefault: Level.fatal,
    tagDuplicateExtends: Level.fatal,
    tagDuplicateGenerator: Level.fatal,
    tagDuplicateImplements: Level.fatal,
    tagDuplicateMethod: Level.fatal,
    tagDuplicateNext: Level.fatal,
    tagDuplicateParameter: Level.fatal,
    tagDuplicatePattern: Level.fatal,
    tagDuplicateReadonly: Level.fatal,
    tagDuplicateReturn: Level.fatal,
    tagDuplicateStructure: Level.fatal,
    tagDuplicateTemplate: Level.fatal,
    tagDuplicateThis: Level.fatal,
    tagDuplicateType: Level.fatal,
    tagDuplicateVisibilityModifier: Level.fatal,
    tagDuplicateYield: Level.fatal,

    tagSuperfluousReturn: Level.fatal,
    tagSuperfluousParameter: Level.fatal,
    tagSuperfluousDesc: Level.fatal,
    tagSuperfluousNext: Level.fatal,
    tagSuperfluousType: Level.fatal,
    tagSuperfluousPattern: Level.fatal,
    tagSuperfluousYield: Level.fatal,
    tagSuperfluousTypedef: Level.fatal,
    
    propDuplicateIfaceOrClass: Level.fatal,
    propInvalidIfaceOrClass: Level.fatal,

    tagOverriddenMissingDesc: Level.ignore,
    symbolOverriddenMissingDesc: Level.ignore,

    symbolMissingDesc: Level.fatal,
    propMissingDesc: Level.fatal,
    tagMissingDesc: Level.fatal,
    tagParamMissingDesc: Level.fatal,
    tagReturnMissingDesc: Level.fatal,
    tagTemplateMissingDesc: Level.fatal,
    tagTypedefMissingDesc: Level.fatal,
};

/**
 * @param {string | undefined} setting 
 * @return {setting is keyof SeveritySettingsConfig}
 */
function isSeveritySetting(setting) {
    return setting !== undefined && setting in DefaultSeveritySettings;
}

/**
 * @param {string | undefined} level 
 * @return {level is SeverityLevel}
 */
function isSeverityLevel(level) {
    return level !== undefined && level in Level;
}

/**
 * Creates the default severity level of various checks.
 * @param {Partial<SeveritySettingsConfig>} overrides
 * @return {SeveritySettingsConfig}
 */
function createDefaultSeveritySettings(overrides = {}) {
    return Object.assign({}, DefaultSeveritySettings, overrides);
}

function getSortedSeveritySettingKeys() {
    return Object.keys(DefaultSeveritySettings).sort();
}

module.exports = {
    createDefaultSeveritySettings,
    getSortedSeveritySettingKeys,
    isSeverityLevel,
    isSeveritySetting,
    Level,
};