/**
 * @param {ObjectCodeProperty} property Individual properties of the object.
 * @return {PropCodeInfo}
 */
function createPropCodeInfo(property) {
    return {
        name: property.name,
        readonly: property.getter !== undefined && property.setter == undefined,
        optional: property.setter !== undefined && property.getter == undefined,
        typedef: "",
        visibility: undefined,
    };
}

module.exports = {
    createPropCodeInfo,
}