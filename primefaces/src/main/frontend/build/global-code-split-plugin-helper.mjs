// @ts-check

/**
 * @typedef {{
 * cjs: unknown;
 * esm: Record<string, unknown>;
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
    return scope[moduleName] ??= { cjs: {}, esm: {} }
}

/**
 * Exposes a CommonJS module to the global scope.
 * @param {string} scopeName Scope to expose the module.
 * @param {string} moduleName Name of the module.
 * @param {unknown} module Contents of the module. 
 */
export function exposeCommonJsModule(scopeName, moduleName, module) {
    const container = getModuleContainer(scopeName, moduleName);
    container.cjs = module;
}

/**
 * Retrieves Common JS module from the global scope.
 * @param {string} scopeName Scope where the module is exposed.
 * @param {string} moduleName Name of the module.
 * @returns {unknown} module Contents of the module. 
 */
export function retrieveLinkedCommonJsModule(scopeName, moduleName) {
    const container = getModuleContainer(scopeName, moduleName);
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
    container.esm = module;
}

/**
 * Retrieves an ESM module from the global scope.
 * @param {string} scopeName Scope where the module is exposed.
 * @param {string} moduleName Name of the module.
 * @returns {unknown} module Contents of the module. 
 */
export function retrieveLinkedEsmModule(scopeName, moduleName) {
    const container = getModuleContainer(scopeName, moduleName);
    return container.esm.default;
}
