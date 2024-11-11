// @ts-check

/**
 * @typedef {{
 * cjs: unknown;
 * esm: Record<string, unknown>;
 * cjsDefined: boolean;
 * esmDefined: boolean;
 * }} ModuleContainer
 */
undefined;

/**
 * Gets the container for the contents of a module from the global scope.
 * Creates a new container if it doesn't exist.
 * @param {string} scopeName Name of the global variables holding all modules.
 * @param {string} moduleName Name of the module.
 * @returns {ModuleContainer} Container for the module contents.
 */
function getModuleContainer(scopeName, moduleName) {
    const scope = window[scopeName] ??= {};
    return scope[moduleName] ??= { cjs: {}, esm: {}, esmDefined: false, cjsDefined: false };
}

/**
 * Exposes a CommonJS module to the global scope.
 * @param {string} scopeName Scope to expose the module.
 * @param {string} moduleName Name of the module.
 * @param {unknown} module Contents of the module. 
 */
export function exposeCommonJsModule(scopeName, moduleName, module) {
    const container = getModuleContainer(scopeName, moduleName);
    if (!container.cjsDefined) {
        container.cjsDefined = true;
        container.cjs = module;
    }
}

/**
 * Retrieves Common JS module from the global scope.
 * @param {string} scopeName Scope where the module is exposed.
 * @param {string} moduleName Name of the module.
 * @returns {unknown} module Contents of the module. 
 */
export function retrieveLinkedCommonJsModule(scopeName, moduleName) {
    const container = getModuleContainer(scopeName, moduleName);
    if (!container.cjsDefined) {
        throw new Error(`CommonJS module ${moduleName} is not defined in the global scope window.${scopeName}. You need to first import the JavaScript file that contains that module.`);
    }
    return container.cjs;
}

/**
 * Exposes an ESM module to the global scope.
 * @param {string} scopeName Scope to expose the module.
 * @param {string} moduleName Name of the module.
 * @param {Record<string, unknown>} module Contents of the module. 
 */
export function exposeEsmModule(scopeName, moduleName, module) {
    const container = getModuleContainer(scopeName, moduleName);
    if (!container.esmDefined) {
        container.esmDefined = true;
        container.esm = module;
    }
}

/**
 * Retrieves an ESM module from the global scope.
 * @param {string} scopeName Scope where the module is exposed.
 * @param {string} moduleName Name of the module.
 * @returns {unknown} module Contents of the module. 
 */
export function retrieveLinkedEsmModule(scopeName, moduleName) {
    const container = getModuleContainer(scopeName, moduleName);
    if (!container.esmDefined) {
        throw new Error(`ESM module ${moduleName} was not yet defined in the global scope window.${scopeName}. You need to first import the JavaScript file that contains that module.`);
    }
    return container.esm.default;
}
