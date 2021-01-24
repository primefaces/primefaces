//@ts-check

// Parses the JavaScript code of an object for property and method signatures

const { is, findAndParseLastBlockComment } = require("./acorn-util");
const { isParseResultEmpty } = require("./doc-comments");
const { handleError, newMethodErrorMessage, newPropErrorMessage } = require("./error");

/**
 * Parses the comments for the widget class and its properties. Returns all properties and methods
 * that should be included in the type definitions. Furthermore, if the widget itself is marked as
 * `@private`, this method returns `undefined`.
 * @param {ObjectDef} objectDef
 * @param {CommentedAst<import("estree").Program>} commentedProgram
 * @param {InclusionHandler} inclusionHandler
 * @param {SeveritySettingsConfig} severitySettings
 * @return {ObjectCode | undefined} Data with the parsed comments and the nodes for the widget. `undefined` if the
 * widget is private.
 */
function parseObjectCode(objectDef, commentedProgram, inclusionHandler, severitySettings) {
    const jsdoc = findAndParseLastBlockComment(objectDef.comments, severitySettings);
    if (!inclusionHandler.isIncludeType(jsdoc, objectDef.name)) {
        return undefined;        
    }
    /** @type {Map<string, ObjectCodeProperty>} */
    const propertyMap = new Map();
    /** @type {Map<string, ObjectCodeMethod>} */
    const methodMap = new Map();
    /** @type {ObjectCodeMethod[]} */
    const methods = [];
    /** @type {ObjectCodeProperty[]} */
    const properties = [];
    for (const property of objectDef.classDefinition.properties) {
        let propertyName = "";
        if (property.type === "SpreadElement") {
            throw new Error("Spread properties are not supported currently.")
        }
        if (is(property.key, "Identifier")) {
            propertyName = property.key.name;
        }
        else if (is(property.key, "Literal")) {
            propertyName = String(property.key.value);
        }
        const comments = commentedProgram.map.get(property) || [];
        const jsdoc = findAndParseLastBlockComment(comments, severitySettings);
        if (propertyName && inclusionHandler.isIncludeProperty(jsdoc, propertyName)) {
            if (is(property.value, "FunctionExpression")) {
                if (property.kind === "get" || property.kind === "set") {
                    // Widget property: getter or setter
                    // get foo() {}   
                    // set foo() {}
                    const prop = propertyMap.get(propertyName);
                    if (prop !== undefined) {
                        if (
                            (prop.getter === undefined && prop.setter === undefined) ||
                            (property.kind === "get" && prop.getter !== undefined) ||
                            (property.kind === "set" && prop.setter !== undefined)
                        ) {
                            handleError("codeDuplicateProperty", severitySettings, () => newPropErrorMessage(`Found duplicate property '${propertyName}' in object definition.`, prop, property));
                        }
                        else {
                            prop[property.kind === "get" ? "getter" : "setter"] = {
                                name: propertyName,
                                jsdoc: jsdoc,
                                node: property.value,
                                hasComment: isParseResultEmpty(jsdoc),
                            };
                        }
                    }
                    else {
                        /** @type {ObjectCodeProperty} */
                        const prop = {
                            getter: undefined,
                            jsdoc: jsdoc,
                            name: propertyName,
                            node: property.value,
                            nodeProperty: property,
                            setter: undefined,
                        };
                        prop[property.kind === "get" ? "getter" : "setter"] = {
                            name: propertyName,
                            jsdoc: jsdoc,
                            node: property.value,
                            hasComment: isParseResultEmpty(jsdoc),
                        };
                        properties.push(prop);
                        propertyMap.set(propertyName, prop);
                    }
                }
                else {
                    // Widget Method
                    // foo() {}
                    const method = methodMap.get(propertyName);
                    if (method !== undefined) {
                        handleError("codeDuplicateMethod", severitySettings, () => newMethodErrorMessage(`Found duplicate method '${propertyName}' in object definition.`, method, property));
                    }
                    else {
                        /** @type {ObjectCodeMethod} */
                        const method = {
                            jsdoc: jsdoc,
                            method: undefined,
                            name: propertyName,
                            node: property.value
                        };
                        methodMap.set(propertyName, method);
                        methods.push(method);
                    }
                }
            }
            else if (
                (property.key.type === "Identifier" || property.key.type === "Literal") &&
                (
                    property.value.type !== "ObjectPattern" &&
                    property.value.type !== "ArrayPattern"  &&
                    property.value.type !== "AssignmentPattern" &&
                    property.value.type !== "RestElement"
                )
            ) {
                 // Base property
                 // x: 9
                 const prop = propertyMap.get(propertyName);
                 if (prop !== undefined) {
                     handleError("codeDuplicateProperty", severitySettings, () => newPropErrorMessage(`Found duplicate property '${propertyName}' in object definition.`, prop, property));
                 }
                 else {
                     /** @type {ObjectCodeProperty} */
                    const prop = {
                        name: propertyName,
                        jsdoc: jsdoc,
                        node: property.value,
                        nodeProperty: property,
                        getter: undefined,
                        setter: undefined,
                    };
                    propertyMap.set(propertyName, prop);
                    properties.push(prop);    
                 }
            }
        }
    };
    return {
        componentName: objectDef.name,
        jsdoc: jsdoc,
        methods: methods,
        properties: properties,
        node: objectDef.classDefinition,
    };
}

module.exports = {
    parseObjectCode,
}