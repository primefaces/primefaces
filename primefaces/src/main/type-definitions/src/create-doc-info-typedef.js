// @ts-check

const { Tags } = require("./constants");
const { NativeInsertionOrderMap } = require("./InsertionOrderMap");
const { strJoin } = require("./lang");
const { handleError, newNodeErrorMessage } = require("./error");
const { checkTagHasDescription, checkTagHasNoDescription, checkTagHasName, checkTagHasType, checkTagIsPlain } = require("./doc-comment-check-tags");

/**
 * @param {import("estree").Node} node
 * @param {DocInfoTypedef[]} typedefs 
 * @param {string[]} path 
 * @param {number} offset
 * @return {DocInfoTypedef | undefined}
 */
function getOrCreateTypedefFunction(node, typedefs, path, offset) {
  const typedef = getOrCreateTypedef(typedefs, path, offset);
  if (typedef !== undefined) {
    if (typedef.function === undefined) {
      typedef.function = createEmptyTypedocFunction(node);
    }
    return typedef;
  }
  else {
    return undefined;
  }
}

/**
 * @param {DocInfoTypedef[]} typedefs 
 * @param {string[]} path 
 * @param {number} offset
 * @return {DocInfoTypedef | undefined}
 */
function getOrCreateTypedef(typedefs, path, offset) {
  const typedefName = strJoin(path.slice(0, offset < 0 ? offset : undefined), ".");
  const all = typedefs.filter(x => x.name === typedefName);
  if (all.length === 1) {
    const typedef = all[0];
    return typedef;
  }
  else if (all.length > 1) {
    return undefined;
  }
  else {
    return undefined;
  }
}

/**
 * @param {import("estree").Node} node
 * @return {TypedefFunctionInfo}
 */
function createEmptyTypedocFunction(node) {
  return {
    async: false,
    constructor: false,
    destructuring: new Map(),
    generator: false,
    next: undefined,
    node: node,
    params: new NativeInsertionOrderMap,
    return: undefined,
    templates: new Map(),
    thisTypedef: "",
    yield: undefined,
  };
}

/**
 * @param {import("estree").Node} node
 * @param {DocInfoTypedef[]} typedefs 
 * @param {string[]} path 
 * @param {number} index
 * @return {DestructuringInfo | undefined} The destructuring info at the given path.
 */
function getOrCreateTypedefDestructuring(node, typedefs, path, index) {
  const typedef = getOrCreateTypedefFunction(node, typedefs, path, -1);
  if (typedef && typedef.function) {
    const destructuring = typedef.function.destructuring.get(index);
    return getOrCreateDestructuring(typedef.function, destructuring, index);
  }
  else {
    return undefined;
  }
}

/**
 * @param {Pick<MethodDocShape, "destructuring">} method
 * @param {DestructuringInfo | undefined} destructuring
 * @param {number} index Index of the pattern or structure tag.
 * @return {DestructuringInfo} The destructuring info at the given path.
 */
function getOrCreateDestructuring(method, destructuring, index) {
  if (destructuring === undefined) {
    destructuring = {
      pattern: undefined,
      structure: undefined,
    }
    method.destructuring.set(index, destructuring);
    return destructuring;
  }
  return destructuring;
}

/**
 * @param {import("estree").Node} node
 * @param {SeveritySettingsConfig} severitySettings
 * @param {DocInfoTypedef[]} typedefs 
 * @return {TypedefTagHandlers}
 */
function createTypedefTagHandler(node, severitySettings, typedefs) {
  /** @type {MessageFactory} */
  const factory = message => newNodeErrorMessage(message, node);
  return {
    createEmpty() {
      return createEmptyTypedocFunction(node);
    },
    async(tag, allTags, logMissing) {
      const parts = tag.name.split(".");
      const typedef = getOrCreateTypedefFunction(node, typedefs, parts, 0);
      checkTagIsPlain(tag, severitySettings, factory, { checkName: false });
      if (typedef && typedef.function) {
        typedef.function.async = true;
        return true;
      }
      else {
        if (logMissing === true) {
          handleError("unsupportedTag", severitySettings, () => factory(`@${tag.type} is not supported in this context. Are you missing a @typedef? Did you make sure to put @typedef first?`));
        }
        return false;
      }
    },
    _constructor(tag, allTags, logMissing) {
      const parts = tag.name.split(".");
      const typedef = getOrCreateTypedefFunction(node, typedefs, parts, 0);
      checkTagIsPlain(tag, severitySettings, factory, { checkName: false });
      if (typedef && typedef.function) {
        typedef.function.constructor = true;
        return true;
      }
      else {
        if (logMissing === true) {
          handleError("unsupportedTag", severitySettings, () => factory(`@${tag.type} is not supported in this context. Are you missing a @typedef? Did you make sure to put @typedef first?`));
        }
        return false;
      }
    },
    generator(tag, allTags, logMissing) {
      const parts = tag.name.split(".");
      const typedef = getOrCreateTypedefFunction(node, typedefs, parts, 0);
      checkTagIsPlain(tag, severitySettings, factory, { checkName: false });
      if (typedef && typedef.function) {
        typedef.function.generator = true;
        return true;
      }
      else {
        if (logMissing === true) {
          handleError("unsupportedTag", severitySettings, () => factory(`@${tag.type} is not supported in this context. Are you missing a @typedef? Did you make sure to put @typedef first?`));
        }
        return false;
      }
    },
    params(tag, allTags, logMissing) {
      const parts = tag.name.split(".");
      const typedef = getOrCreateTypedefFunction(node, typedefs, parts, -1);
      if (typedef && typedef.function) {
        tag.description = checkTagHasDescription(tag, severitySettings, factory, allTags, true);
        tag.name = parts[parts.length - 1];
        typedef.function.params.set(parts[parts.length - 1], tag);
        return true;
      }
      else {
        if (logMissing === true) {
          handleError("unsupportedTag", severitySettings, () => factory(`@${tag.type} is not supported in this context. Are you missing a @typedef? Did you make sure to put @typedef first?`));
        }
        return false;
      }
    },
    return(tag, allTags, logMissing) {
      const parts = tag.name.split(".");
      const typedef = getOrCreateTypedefFunction(node, typedefs, parts, 0);
      if (typedef && typedef.function) {
        tag.description = checkTagHasDescription(tag, severitySettings, factory, allTags, true);
        tag.name = parts[parts.length - 1];
        typedef.function.return = tag;
        return true;
      }
      else {
        if (logMissing === true) {
          handleError("unsupportedTag", severitySettings, () => factory(`@${tag.type} is not supported in this context. Are you missing a @typedef? Did you make sure to put @typedef first?`));
        }
        return false;
      }
    },
    structureOrPattern(tag, allTags, logMissing) {
      checkTagHasName(tag, severitySettings, factory);
      const parts = tag.name.split(".");
      if (parts[parts.length - 1].length > 0) {
        const index = parseInt(parts[parts.length - 1]);
        if (isNaN(index) || index < 0) {
          handleError("tagNameInvalidIndex", severitySettings, () => factory(`'@pattern' must specify an index that is a number greater than or equal to 0, eg. "@pattern {[number, string]} 0"`));
          return false;
        }
        else {
          const typedefDestructuring = getOrCreateTypedefDestructuring(node, typedefs, parts, index);
          if (typedefDestructuring) {
            tag.name = parts[parts.length - 1];
            if (tag.tag === Tags.Pattern) {
              if (typedefDestructuring.pattern !== undefined) {
                handleError("tagDuplicatePattern", severitySettings, () => factory(`Found duplicate tag '@pattern ${tag.name}' in doc comments`));
              }
              tag.description = checkTagHasDescription(tag, severitySettings, factory, allTags, true);
              typedefDestructuring.pattern = tag;
            }
            else if (tag.tag === Tags.Structure) {
              if (typedefDestructuring.structure !== undefined) {
                handleError("tagDuplicateStructure", severitySettings, () => factory(`Found duplicate tag '@structure ${tag.name}' in doc comments`));
              }
              checkTagHasNoDescription(tag, severitySettings, factory, true);
              tag.description = "";
              typedefDestructuring.structure = tag;
            }
            return true;
          }
          else {
            if (logMissing === true) {
              handleError("unsupportedTag", severitySettings, () => factory(`@${tag.type} is not supported in this context. Are you missing a @typedef? Did you make sure to put @typedef first?`));
            }
            return false;
          }
        }
      }
      else {
        return false;
      }
    },
    methodtemplate(tag, allTags, logMissing) {
      const parts = tag.name.split(".");
      const typedef = getOrCreateTypedefFunction(node, typedefs, parts, -1);
      if (typedef !== undefined && typedef.function !== undefined) {
        tag.description = checkTagHasDescription(tag, severitySettings, factory, allTags, true);
        tag.name = parts[parts.length - 1];
        tag.tag = Tags.Template;
        typedef.function.templates.set(parts[parts.length - 1], tag);
        return true;
      }
      else {
        if (logMissing === true) {
          handleError("unsupportedTag", severitySettings, () => factory(`@${tag.type} is not supported in this context. Are you missing a @typedef? Did you make sure to put @typedef first?`));
        }
        return false;
      }
    },
    next(tag, allTags, logMissing) {
      const parts = tag.name.split(".");
      const typedef = getOrCreateTypedefFunction(node, typedefs, parts, 0);
      if (typedef && typedef.function) {
        tag.description = checkTagHasDescription(tag, severitySettings, factory, allTags, true);
        tag.name = "";
        typedef.function.next = tag;
        return true;
      }
      else {
        if (logMissing === true) {
          handleError("unsupportedTag", severitySettings, () => factory(`@${tag.type} is not supported in this context. Are you missing a @typedef? Did you make sure to put @typedef first?`));
        }
        return false;
      }
    },
    template(tag, allTags, logMissing) {
      const parts = tag.name.split(".");
      const typedef = getOrCreateTypedef(typedefs, parts, -1);
      if (typedef !== undefined) {
        tag.description = checkTagHasDescription(tag, severitySettings, factory, allTags, true);
        tag.name = parts[parts.length - 1];
        tag.tag = Tags.Template;
        typedef.templates.set(parts[parts.length - 1], tag);
        return true;
      }
      else {
        if (logMissing === true) {
          handleError("unsupportedTag", severitySettings, () => factory(`@${tag.type} is not supported in this context. Are you missing a @typedef? Did you make sure to put @typedef first?`));
        }
        return false;
      }
    },
    this(tag, allTags, logMissing) {
      const parts = tag.name.split(".");
      const typedef = getOrCreateTypedefFunction(node, typedefs, parts, 0);
      if (typedef && typedef.function) {
        const type = checkTagHasType(tag, severitySettings, factory);
        checkTagHasNoDescription(tag, severitySettings, factory, true);
        typedef.function.thisTypedef = type;
        return true;
      }
      else {
        if (logMissing === true) {
          handleError("unsupportedTag", severitySettings, () => factory(`@${tag.type} is not supported in this context. Are you missing a @typedef? Did you make sure to put @typedef first?`));
        }
        return false;
      }
    },
    yield(tag, allTags, logMissing) {
      const parts = tag.name.split(".");
      const typedef = getOrCreateTypedefFunction(node, typedefs, parts, 0);
      if (typedef && typedef.function) {
        tag.description = checkTagHasDescription(tag, severitySettings, factory, allTags, true);
        tag.name = "";
        typedef.function.yield = tag;
        return true;
      }
      else {
        if (logMissing === true) {
          handleError("unsupportedTag", severitySettings, () => factory(`@${tag.type} is not supported in this context. Are you missing a @typedef? Did you make sure to put @typedef first?`));
        }
        return false;
      }
    },
  };
}

module.exports = {
  createTypedefTagHandler,
};