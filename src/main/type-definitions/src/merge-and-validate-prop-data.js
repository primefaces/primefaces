//@ts-check

const { handleError, newPropErrorMessage } = require("./error");

/**
 * @param {ObjectCodeProperty} property 
 * @param {PropCodeInfo} propCodeInfo 
 * @param {PropDocInfo} propDocInfo 
 * @param {SeveritySettingsConfig} severitySettings
 */
function mergeAndValidatePropFromCode(property, propCodeInfo, propDocInfo, severitySettings) {
    /** @type {MessageFactory} */
    const factory = message => newPropErrorMessage(message, property);
    if (propDocInfo.readonly && property.setter !== undefined) {
        handleError("tagReadonlyWithSetter", severitySettings,() => factory(`Found @readonly in doc comments for property '${property.name}', but code specifies a setter.`));
        propDocInfo.readonly = false;
    }
    if (propCodeInfo.optional) {
        propDocInfo.optional = propCodeInfo.optional;
    }
    if (propCodeInfo.readonly) {
        propDocInfo.readonly = propCodeInfo.readonly;
    }
    if (propCodeInfo.visibility) {
        propDocInfo.visibility = propCodeInfo.visibility;
    }
}

/**
 * @param {ObjectCodeProperty} property 
 * @param {PropCodeInfo} propCodeInfo 
 * @param {PropDocInfo} propDocInfo 
 * @param {SeveritySettingsConfig} severitySettings
 */
function mergeAndValidatePropToCode(property, propCodeInfo, propDocInfo, severitySettings) {
    propCodeInfo.typedef = propDocInfo.typedef;
    if (propDocInfo.optional) {
        propCodeInfo.optional = propDocInfo.optional;
    }
    if (propDocInfo.readonly) {
        propCodeInfo.readonly = propDocInfo.readonly;
    }
    if (propDocInfo.visibility) {
        propCodeInfo.visibility = propDocInfo.visibility;
    }
}

/**
 * @param {ObjectCodeProperty} property 
 * @param {PropCodeInfo} propCodeInfo 
 * @param {PropDocInfo} propDocInfo 
 * @param {SeveritySettingsConfig} severitySettings
 */
function mergeAndValidatePropData(property, propCodeInfo, propDocInfo, severitySettings) {
    mergeAndValidatePropFromCode(property, propCodeInfo, propDocInfo, severitySettings);
    mergeAndValidatePropToCode(property, propCodeInfo, propDocInfo, severitySettings);
}

module.exports = {
    mergeAndValidatePropData,
};

