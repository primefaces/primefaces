//@ts-check

// Takes a method signature and the asscoiated doc comments, and merges and validates this data.

const { handleError, newMethodErrorMessage } = require("./error");
const { assertNever } = require("./lang");
const { getArgumentType } = require("./ts-types");

/**
 * @param {ObjectCodeMethod} method
 * @param {MethodCodeInfo} methodCodeInfo 
 * @param {MethodDocInfo} methodDocInfo 
 * @param {SeveritySettingsConfig} severitySettings
 */
function mergeAndValidateMethodConstructor(method, methodCodeInfo, methodDocInfo, severitySettings) {
    const ctor = methodCodeInfo.isConstructor || methodDocInfo.constructor;
    methodCodeInfo.isConstructor = ctor;
    methodDocInfo.constructor = ctor;
}

/**
 * @param {ObjectCodeMethod} method
 * @param {MethodCodeInfo} methodCodeInfo 
 * @param {MethodDocInfo} methodDocInfo 
 * @param {SeveritySettingsConfig} severitySettings
 */
function mergeAndValidateMethodReturnAndYield(method, methodCodeInfo, methodDocInfo, severitySettings) {
    // return
    if (methodCodeInfo.return.node !== undefined && !methodDocInfo.return.hasReturn) {
        handleError("tagMissingReturn", severitySettings, () => newMethodErrorMessage("Found explicit return statement in method, but no @return tag was specified in the doc comments", method, methodCodeInfo.return.node));
        methodDocInfo.return.hasReturn = true;
        methodDocInfo.return.typedef = "any";
        methodDocInfo.return.description = "";
    }
    const abstractLike = methodDocInfo.abstract || !methodCodeInfo.canCompleteNormally;
    if (methodCodeInfo.return.node === undefined && methodDocInfo.return.hasReturn && !abstractLike) {
        handleError("tagSuperfluousReturn", severitySettings, () => newMethodErrorMessage("@return tag was specified, but method is neither abstract nor does it contain an explicit return statement", method));
        methodDocInfo.return.hasReturn = false;
        methodDocInfo.return.typedef = "any";
        methodDocInfo.return.description = "";
    }
    // yield
    if (methodCodeInfo.yield.node !== undefined && !methodDocInfo.yield.hasYield) {
        handleError("tagMissingYield", severitySettings, () => newMethodErrorMessage("Found explicit yield expression in method, but no @yield tag was specified in the doc comments", method, methodCodeInfo.yield.node));
        methodDocInfo.yield.hasYield = true;
        methodDocInfo.yield.typedef = "any";
        methodDocInfo.yield.description = "";
    }
    if (methodCodeInfo.yield.node === undefined && methodDocInfo.yield.hasYield && !abstractLike) {
        handleError("tagSuperfluousYield", severitySettings, () => newMethodErrorMessage("@yield tag was specified, but method contains no explicit yield expression", method));
        methodDocInfo.yield.hasYield = false;
        methodDocInfo.yield.typedef = "any";
        methodDocInfo.yield.description = "";
    }
    // next
    if (methodCodeInfo.next.node !== undefined && !methodDocInfo.next.hasNext) {
        handleError("tagMissingNext", severitySettings, () => newMethodErrorMessage("Found yield expression in method whose return value appears to be used, but no @next tag was specified in the doc comments", method, methodCodeInfo.next.node));
        methodDocInfo.next.hasNext = true;
        methodDocInfo.next.typedef = "any";
        methodDocInfo.next.description = "";
    }
    if (methodCodeInfo.next.node === undefined && methodDocInfo.next.hasNext && !abstractLike) {
        handleError("tagSuperfluousNext", severitySettings, () => newMethodErrorMessage("@next tag was specified, but method contains yield expression whose return value is used ", method));
        methodDocInfo.next.hasNext = false;
        methodDocInfo.next.typedef = "any";
        methodDocInfo.next.description = "";
    }
    // JavaScript has got no concept of abstract methods, so they need an implementation. This is usually just a
    // 'throw new Error("must be overridden")' without a return statement, so we need to create one.
    if (abstractLike && methodDocInfo.return.hasReturn && methodCodeInfo.return.node === undefined) {
        methodCodeInfo.return.node = {
            argument: {
                name: "syntheticReturn",
                type: "Identifier",
            },            
            type: "ReturnStatement",
        };
    }
    methodCodeInfo.return.typedef = methodDocInfo.return.typedef;
    methodCodeInfo.yield.typedef = methodDocInfo.yield.typedef;
    methodCodeInfo.next.typedef = methodDocInfo.next.typedef;
}

/**
 * Takes a look at each argument as specified by the JavaScript code, and checks
 * if there is a corresponding doc entry.
 * @param {ObjectCodeMethod} method
 * @param {MethodCodeInfo} methodCodeInfo 
 * @param {MethodDocInfo} methodDocInfo 
 * @param {SeveritySettingsConfig} severitySettings
 */
function mergeAndValidateMethodArgsFromCode(method, methodCodeInfo, methodDocInfo, severitySettings) {
    /** @type {MessageFactory} */
    const factory = message => newMethodErrorMessage(message, method);

    // Inspect normal (top-level) parameters function foo(x,y,z){}
    methodCodeInfo.arguments.forEach((argument, index) => {
        switch (argument.type) {
            case "param": {
                const name = argument.name;
                const param = methodDocInfo.variables.get(name);
                // Symbol must be documented with @param
                if (param === undefined) {
                    handleError("tagMissingIdentParameter", severitySettings, () => factory(`Found argument '${name}', but no corresponding '@param {type} ${name}' tag was specified in the doc comments`));
                    methodDocInfo.variables.set(name, {
                        name: name,
                        typedef: getArgumentType("ident", argument.rest),
                        initializer: argument.initializer,
                        required: argument.required,                    
                        description: "",
                    });
                }
                else {
                    if (!argument.required) {
                        param.required = false;
                    }
                }
                break;
            }
            case "object":
            case "array": {
                const pattern = methodDocInfo.patterns.get(index);
                if (pattern === undefined) {
                    handleError("tagMissingPattern", severitySettings, () => factory(`Found ${argument.type} destructuring pattern for argument '${index}', but no corresponding '@pattern {type} ${index}' was specified in the doc comments`));
                    methodDocInfo.patterns.set(index, {
                        index: index,
                        typedef: getArgumentType(argument.type, argument.rest),
                        initializer: argument.initializer,
                        required: argument.required,
                        description: "",
                    });
                }
                else {
                    if (!argument.required) {
                        pattern.required = false;
                    }
                }
                break;
            }
            default: assertNever(argument);
        }
    });

    // Inspect variables, including destructured symbols: function foo([x,y], {a,b}]){}
    for (const codeVar of methodCodeInfo.variables) {
        const name = codeVar.name;
        // Symbol must be documented with @param
        const docVar = methodDocInfo.variables.get(name);
        if (docVar === undefined) {
            handleError("tagMissingIdentParameter", severitySettings, () => factory(`Found variable '${name}' in method signature, but no corresponding '@param {type} ${name}' was specified in the doc comments`));
            methodDocInfo.variables.set(name, {
                name: name,
                typedef: "", // no typedef neccessary
                initializer: codeVar.initializer,
                required: codeVar.initializer !== "",
                description: "",
            });
        }
        else {
            if (codeVar.initializer === "") {
                // No initializer given in the code
                codeVar.initializer = docVar.initializer;
            }
            else {
                if (docVar.initializer !== "" && codeVar.initializer !== docVar.initializer) {
                    handleError("tagConflictingParamInitializers", severitySettings, () => factory(`Conflicting initializers for parameter '${name}': code specifies initializer '${codeVar.initializer}', but doc comments specifiy '${docVar.initializer}'`));
                }
                docVar.initializer = codeVar.initializer;                    
            }
        }
    }
}

/**
 * @param {MethodCodeInfo} methodCodeInfo 
 * @param {MethodDocInfo} methodDocInfo 
 * @param {SeveritySettingsConfig} severitySettings
 */
function mergeAndValidateMethodModifiers(methodCodeInfo, methodDocInfo, severitySettings) {
    // Abstract modifier can only be specified in comments
    methodCodeInfo.abstract = methodDocInfo.abstract;

    // Visibility modifier can only be specified in comments
    methodCodeInfo.visibility = methodDocInfo.visibility;
}

/**
 * Takes a look at each argument as specified by the JavaScript code, and checks
 * if there is a corresponding doc entry.
 * @param {ObjectCodeMethod} method
 * @param {MethodCodeInfo} methodCodeInfo 
 * @param {MethodDocInfo} methodDocInfo 
 * @param {SeveritySettingsConfig} severitySettings
 */
function mergeAndValidateMethodArgsToCode(method, methodCodeInfo, methodDocInfo, severitySettings) {
    /** @type {MessageFactory} */
    const factory = message => newMethodErrorMessage(message, method);
    const methodCodeVariablesMap = new Map(methodCodeInfo.variables.map(x => [x.name, x]));
    /** @type {Map<string, ArgumentInfo>} */
    const methodCodeArgsMap = new Map();
    methodCodeInfo.arguments.forEach(x => {
        if (x.type === "param") {
            methodCodeArgsMap.set(x.name, x);
        }
    });
    
    // This type, cam only be specified in comments
    methodCodeInfo.thisTypedef = methodDocInfo.thisTypedef;

    // Check all symbols put in the function scope
    // function foo(x, [y,z], {a,b: c}, ...{9: b}){}
    for (const [name, docVariable] of Array.from(methodDocInfo.variables)) {
        const codeVariable = methodCodeVariablesMap.get(name);
        if (codeVariable === undefined) {
            handleError("tagSuperfluousParameter", severitySettings, () => factory(`Found documented parameter '${name}' in doc comments for method '${method.name}', but method signature specifies no such parameter`));
            methodDocInfo.variables.delete(name);
        }
        else {
            const codeIdentArg = methodCodeArgsMap.get(name);
            if (codeIdentArg !== undefined && docVariable.typedef === "") {
                handleError("tagMissingType", severitySettings, () => factory(`Tag '@param ${name}' must specify a type, eg. '@param {string} ${name}'`));
            }
            else if (codeIdentArg === undefined && docVariable.typedef !== "") {
                handleError("tagTypeOnDestructuredParameter", severitySettings, () => factory(`Tag '@param ${name} specifies a type in doc comments, but this is a destructured parameter that cannot specify a type. Use '@pattern {type}' to specify the type for the entire argument`));
                docVariable.typedef = "";
            }
            if (docVariable.initializer !== "" && codeVariable.rest) {
                handleError("tagInitializerForRest", severitySettings, () => factory(`Found initializer for parameter ${name} in doc comments, but code specifies this as a rest parameter (...args) and such arguments cannot have initializers.`));
                docVariable.initializer = "";
            }
            if (!docVariable.required && codeVariable.rest) {
                handleError("tagOptionalForRest", severitySettings, () => factory(`Parameter ${name} was marked as optional, but code specifies this as a rest parameter (...${name}) and such arguments cannot be optional.`));
                docVariable.required = true;
            }
            if (codeVariable.initializer === "") {
                // No initializer given in the code
                codeVariable.initializer = docVariable.initializer;               
            }
            else {
                // Initializer given in the code, must agree with comments (if given)
                if (docVariable.initializer !== "" && codeVariable.initializer !== docVariable.initializer) {
                    handleError("tagConflictingParamInitializers", severitySettings, () => factory(`Conflicting initializers for parameter '${name}': code specifies initializer '${codeVariable.initializer}', but doc comments specifiy '${docVariable.initializer}'`));
                }
                docVariable.initializer = codeVariable.initializer;
            }
        }
    }

    // Check documented patterns
    // function foo([x,y], {a,b}){}
    Array.from(methodDocInfo.patterns).forEach(([index, docPattern]) => {
        const codePattern = methodCodeInfo.arguments[index];
        if (codePattern === undefined) {
            handleError("tagSuperfluousPattern", severitySettings, () => factory(`Doc comments specify '@pattern ${index}', but them method signature does not have this many arguments`));
            methodDocInfo.patterns.delete(index);
        }
        else {
            if (codePattern.type === "param") {
                handleError("tagInvalidPattern", severitySettings, () => factory(`Doc comments specify '@pattern ${index}', but the argument at index ${index} of the method signature neither an array nor an object destructuring pattern`));
                methodDocInfo.patterns.delete(index);
            }
            else {
                if (codePattern.initializer === "") {
                    // No initializer given in the code
                    codePattern.initializer = docPattern.initializer;
                }
                else {
                    if (docPattern.initializer !== "" && codePattern.initializer !== docPattern.initializer) {
                        handleError("tagConflictingPatternInitializers", severitySettings, () => factory(`Conflicting initializers for pattern '${index}': code specifies initializer '${codePattern.initializer}', but doc comments specifiy '${docPattern.initializer}'`));
                    }
                    docPattern.initializer = codePattern.initializer;                    
                }
                codePattern.typedef = docPattern.typedef;
                if (!docPattern.required) {
                    codePattern.required = false;
                }
            }
        }
    });

    // Copy typedef from doc comments for non-pattern arguments
    methodCodeInfo.arguments.forEach((codeArg, index) => {
        if (codeArg.type === "param") {
            // Must exist, we merged or created in the last step
            const docArg = methodDocInfo.variables.get(codeArg.name);
            if (!docArg) {
                handleError("tagMissingParam", severitySettings, () => factory(`Code specifiies parameter '${codeArg.name}', but not corresponding '@param ${codeArg.name}' found in doc comments`));
            }
            else {
                codeArg.typedef = docArg.typedef;
                codeArg.initializer = docArg.initializer;
                codeArg.required = docArg.required;        
            }
        }
        else {
            const docArg = methodDocInfo.patterns.get(index);
            if (docArg !== undefined && !docArg.required) {
                codeArg.required = false;
            }
        }
    });
}

/**
 * @param {MethodCodeInfo} methodCodeInfo 
 * @param {MethodDocInfo} methodDocInfo 
 * @param {SeveritySettingsConfig} severitySettings
 */
function mergeAndValidateMethodGenerics(methodCodeInfo, methodDocInfo, severitySettings) {
    /** @type {Map<string, DocInfoTemplate>} */
    const map = new Map();
    for (const template of methodCodeInfo.generics) {
        if (!map.has(template.name)) {
            map.set(template.name, template);
        }
    }
    for (const template of methodDocInfo.templates) {
        if (!map.has(template.name)) {
            map.set(template.name, template);
        }
    }
    const templates = Array.from(map.values());
    methodCodeInfo.generics = templates;
    methodDocInfo.templates = templates;
}

/**
 * @param {ObjectCodeMethod} method
 * @param {MethodCodeInfo} methodCodeInfo 
 * @param {MethodDocInfo} methodDocInfo 
 * @param {SeveritySettingsConfig} severitySettings
 */
function mergeAndValidateMethodData(method, methodCodeInfo, methodDocInfo, severitySettings) {
    mergeAndValidateMethodGenerics(methodCodeInfo, methodDocInfo, severitySettings);
    mergeAndValidateMethodModifiers(methodCodeInfo, methodDocInfo, severitySettings);
    mergeAndValidateMethodReturnAndYield(method, methodCodeInfo, methodDocInfo, severitySettings);
    mergeAndValidateMethodConstructor(method, methodCodeInfo, methodDocInfo, severitySettings);
    mergeAndValidateMethodArgsFromCode(method, methodCodeInfo, methodDocInfo, severitySettings);
    mergeAndValidateMethodArgsToCode(method, methodCodeInfo, methodDocInfo, severitySettings);
}

module.exports = {
    mergeAndValidateMethodData,
};