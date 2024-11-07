/**
 * @param {string} name 
 */
function linkModuleViaGlobalScope(name) {
    const module = require(name);
    window["PrimeFacesLibs"] ??= {};
    Object.assign(window["PrimeFacesLibs"], { [name]: module });
};

module.exports.linkModuleViaGlobalScope = linkModuleViaGlobalScope;