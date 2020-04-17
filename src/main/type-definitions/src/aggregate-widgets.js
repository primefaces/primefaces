//@ts-check

const { is, isIdentMemberExpression, getIdentMemberPath } = require("./acorn-util");

/**
 * An aggregation handler that scans the source code for occurrences of PrimeFaces widgets. These take the form of
 * assignment expressions and look like this:
 * 
 * ```javascript
 * PrimeFaces.widget.WidgetName = PrimeFaces.widget.BaseWidgetName.extend({
 *   // ...
 * });
 * PrimeFaces.widget.BaseWidgetName = Class.extend({
 *   // ...
 * });
 * ```
 * 
 * @type {DocumentableHandler}
 */
const aggregateHandlerWidgets = (node, program, inclusionHandler) => {
    switch (node.type) {
        case "AssignmentExpression": {
            /** @type {undefined | import("estree").Node} */
            let classDefinition;
            if (
                // ... = ...
                node.operator === "=" &&

                // a["foo"].bar = ...
                is(node.left, "MemberExpression") &&
                
                // Primefaces.widget.widgetName = ...
                isIdentMemberExpression(node.left, "PrimeFaces", "widget", "*") &&
                is(node.left.property, "Identifier") &&

                // PrimeFaces.widget.widgetName = foobar(...)
                is(node.right, "CallExpression") &&

                // PrimeFaces.widget.widgetName = a["foo".bar(...)
                is(node.right.callee, "MemberExpression") &&

                // PrimeFaces.widget.widgetName = PrimeFaces.widget.WidgetName.extend(...)
                // PrimeFaces.widget.BaseWidgetName = Class.extend(...)
                (
                    isIdentMemberExpression(node.right.callee, "PrimeFaces", "widget", "*", "extend") ||
                    isIdentMemberExpression(node.right.callee, "Class", "extend")
                ) &&

                // PrimeFaces.widget.widgetName = PrimeFaces.widget.WidgetName.extend(x)
                node.right.arguments.length === 1 &&

                (classDefinition = node.right.arguments[0], true) &&

                // PrimeFaces.widget.widgetName = PrimeFaces.widget.WidgetName.extend({...})
                is(classDefinition, "ObjectExpression")
            ) {
                const leftPath = getIdentMemberPath(node.left);
                const rightPath = getIdentMemberPath(node.right.callee);
                const widgetName = node.left.property.name;
                const widgetExtends = rightPath && rightPath[0] !== "Class" ? rightPath.slice(0, 3).join(".") : "";
                const widgetGenerics = widgetExtends ? [{
                    description: "Type of the configuration object for this widget.",
                    extends: `${widgetName}Cfg`,
                    initializer: `${widgetName}Cfg`,
                    name: "TCfg",
                }] : [];
                const widgetExtendsCfg = widgetExtends ? "<TCfg>" : "";
                /** @type {ObjectDef} */
                const objectDef = {
                    classDefinition: classDefinition,
                    comments: program.map.get(node) || [],
                    kind: "object",
                    name: widgetName,
                    namespace: leftPath ? leftPath.slice(0, -1) : [],
                    spec: {
                        abstract: false,
                        additionalTags: [],
                        description: "",
                        extends: new Set([widgetExtends + widgetExtendsCfg]),
                        implements: new Set(),
                        generics: widgetGenerics,
                        type: "class",
                    },
                };
                return {
                    constants: [],
                    functions: [],
                    objects: [objectDef],
                    recurse: false,
                }
            }
            else {
                return undefined;
            }
        }
        default:
            return undefined;
    }
}

module.exports = {
    aggregateHandlerWidgets,
};