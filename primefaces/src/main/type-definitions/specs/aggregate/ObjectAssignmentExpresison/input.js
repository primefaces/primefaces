/**
 * iface foo
 * @interface
 */
PrimeFaces.ObjectAssignmentExpresison.Foo = {
    /**
     * foo prop x
     * @type {number}
     */
    x: 9,
}

/**
 * iface bar
 * @interface {PrimeFaces.ObjectAssignmentExpresison.Other.Bar2}
 */
PrimeFaces.ObjectAssignmentExpresison.Bar = {
    /**
     * bar prop x
     * @type {number}
     */
    x: 9,
}

/**
 * Should not be picked up
 */
PrimeFaces.ObjectAssignmentExpresison.Baz = {
    /**
     * baz prop x
     * @type {number}
     */
    x: 9,
}

/**
 * Should not be picked up
 * @internal
 * @interface
 */
PrimeFaces.ObjectAssignmentExpresison.Baz2 = {
    /**
     * baz2 prop x
     * @type {number}
     */
    x: 9,
}



/**
 * class hoge
 * @class
 */
PrimeFaces.ObjectAssignmentExpresison.Hoge = {
    /**
     * hoge prop x
     * @type {number}
     */
    x: 9,
}

/**
 * class hogera
 * @class {PrimeFaces.ObjectAssignmentExpresison.Other.Hogera2}
 */
PrimeFaces.ObjectAssignmentExpresison.Hogera = {
    /**
     * hogera prop x
     * @type {number}
     */
    x: 9,
}

/**
 * Should not be picked up
 */
PrimeFaces.ObjectAssignmentExpresison.HogeHoge = {
    /**
     * hogehoge prop x
     * @type {number}
     */
    x: 9,
}

/**
 * Should not be picked up
 * @internal
 * @class
 */
PrimeFaces.ObjectAssignmentExpresison.HogeHoge2 = {
    /**
     * hogehoge2 prop x
     * @type {number}
     */
    x: 9,
}


/**
 * namespace bazbar
 * @namespace
 */
PrimeFaces.ObjectAssignmentExpresison.Bazbar = {
    /**
     * bazbar prop x
     * @type {number}
     */
    x: 9,
}

/**
 * Should not be picked up
 */
PrimeFaces.ObjectAssignmentExpresison.Bazbar2 = {
    /**
     * bazbar2 prop x
     * @type {number}
     */
    x: 9,
}

/**
 * Should not be picked up
 * @namespace
 * @internal
 */
PrimeFaces.ObjectAssignmentExpresison.Bazbar3 = {
    /**
     * bazbar3 prop x
     * @type {number}
     */
    x: 9,
}