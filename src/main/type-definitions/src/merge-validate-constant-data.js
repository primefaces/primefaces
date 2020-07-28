//@ts-check

const { handleError, newConstantErrorMessage } = require("./error");

/**
 * @param {CodeConstant} constant
 * @param {ConstantCodeInfo} constantCodeInfo 
 * @param {ConstantDocInfo} constantDocInfo 
 * @param {SeveritySettingsConfig} severitySettings
 */
function mergeAndValidateToDocs(constant, constantCodeInfo, constantDocInfo, severitySettings) {
    /** @type {MessageFactory} */
    const factory = message => newConstantErrorMessage(message, constant);
    if (constantCodeInfo.type && constantDocInfo.type && constantCodeInfo.type !== constantDocInfo.type) {
        handleError("tagConflictingConstantType", severitySettings, () => factory(`Code declares constant ${constant.name} as ${constantCodeInfo.type}, but found '${constantDocInfo.type}' in doc comments`,));
        constantDocInfo.type = constantCodeInfo.type;
    }
    if (!constantDocInfo.type) {
        constantDocInfo.type = constantCodeInfo.type;
    }
    if (constantCodeInfo.optional) {
        constantDocInfo.optional = constantCodeInfo.optional;
    }
}

/**
 * @param {CodeConstant} constant
 * @param {ConstantCodeInfo} constantCodeInfo 
 * @param {ConstantDocInfo} constantDocInfo 
 * @param {SeveritySettingsConfig} severitySettings
 */
function mergeAndValidateToCode(constant, constantCodeInfo, constantDocInfo, severitySettings) {
    constantCodeInfo.type = constantDocInfo.type;
    constantCodeInfo.typedef = constantDocInfo.typedef;
    constantCodeInfo.optional = constantDocInfo.optional;
}

/**
 * @param {CodeConstant} constant
 * @param {ConstantCodeInfo} constantCodeInfo 
 * @param {ConstantDocInfo} constantDocInfo 
 * @param {SeveritySettingsConfig} severitySettings
 */
function mergeAndValidateConstantData(constant, constantCodeInfo, constantDocInfo, severitySettings) {
    mergeAndValidateToDocs(constant, constantCodeInfo, constantDocInfo, severitySettings);
    mergeAndValidateToCode(constant, constantCodeInfo, constantDocInfo, severitySettings);
}

module.exports = {
    mergeAndValidateConstantData,
};