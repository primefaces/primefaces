/*
Copyright (c) 2009, Yahoo! Inc. All rights reserved.
Code licensed under the BSD License:
http://developer.yahoo.net/yui/license.txt
version: 2.8.0r4
*/
(function () {

    /**
    * Config is a utility used within an Object to allow the implementer to
    * maintain a list of local configuration properties and listen for changes
    * to those properties dynamically using CustomEvent. The initial values are
    * also maintained so that the configuration can be reset at any given point
    * to its initial state.
    * @namespace YAHOO.util
    * @class Config
    * @constructor
    * @param {Object} owner The owner Object to which this Config Object belongs
    */
    YAHOO.util.Config = function (owner) {

        if (owner) {
            this.init(owner);
        }


    };


    var Lang = YAHOO.lang,
        CustomEvent = YAHOO.util.CustomEvent,
        Config = YAHOO.util.Config;


    /**
     * Constant representing the CustomEvent type for the config changed event.
     * @property YAHOO.util.Config.CONFIG_CHANGED_EVENT
     * @private
     * @static
     * @final
     */
    Config.CONFIG_CHANGED_EVENT = "configChanged";

    /**
     * Constant representing the boolean type string
     * @property YAHOO.util.Config.BOOLEAN_TYPE
     * @private
     * @static
     * @final
     */
    Config.BOOLEAN_TYPE = "boolean";

    Config.prototype = {

        /**
        * Object reference to the owner of this Config Object
        * @property owner
        * @type Object
        */
        owner: null,

        /**
        * Boolean flag that specifies whether a queue is currently
        * being executed
        * @property queueInProgress
        * @type Boolean
        */
        queueInProgress: false,

        /**
        * Maintains the local collection of configuration property objects and
        * their specified values
        * @property config
        * @private
        * @type Object
        */
        config: null,

        /**
        * Maintains the local collection of configuration property objects as
        * they were initially applied.
        * This object is used when resetting a property.
        * @property initialConfig
        * @private
        * @type Object
        */
        initialConfig: null,

        /**
        * Maintains the local, normalized CustomEvent queue
        * @property eventQueue
        * @private
        * @type Object
        */
        eventQueue: null,

        /**
        * Custom Event, notifying subscribers when Config properties are set
        * (setProperty is called without the silent flag
        * @event configChangedEvent
        */
        configChangedEvent: null,

        /**
        * Initializes the configuration Object and all of its local members.
        * @method init
        * @param {Object} owner The owner Object to which this Config
        * Object belongs
        */
        init: function (owner) {

            this.owner = owner;

            this.configChangedEvent =
                this.createEvent(Config.CONFIG_CHANGED_EVENT);

            this.configChangedEvent.signature = CustomEvent.LIST;
            this.queueInProgress = false;
            this.config = {};
            this.initialConfig = {};
            this.eventQueue = [];

        },

        /**
        * Validates that the value passed in is a Boolean.
        * @method checkBoolean
        * @param {Object} val The value to validate
        * @return {Boolean} true, if the value is valid
        */
        checkBoolean: function (val) {
            return (typeof val == Config.BOOLEAN_TYPE);
        },

        /**
        * Validates that the value passed in is a number.
        * @method checkNumber
        * @param {Object} val The value to validate
        * @return {Boolean} true, if the value is valid
        */
        checkNumber: function (val) {
            return (!isNaN(val));
        },

        /**
        * Fires a configuration property event using the specified value.
        * @method fireEvent
        * @private
        * @param {String} key The configuration property's name
        * @param {value} Object The value of the correct type for the property
        */
        fireEvent: function ( key, value ) {
            var property = this.config[key];

            if (property && property.event) {
                property.event.fire(value);
            }
        },

        /**
        * Adds a property to the Config Object's private config hash.
        * @method addProperty
        * @param {String} key The configuration property's name
        * @param {Object} propertyObject The Object containing all of this
        * property's arguments
        */
        addProperty: function ( key, propertyObject ) {
            key = key.toLowerCase();

            this.config[key] = propertyObject;

            propertyObject.event = this.createEvent(key, { scope: this.owner });
            propertyObject.event.signature = CustomEvent.LIST;


            propertyObject.key = key;

            if (propertyObject.handler) {
                propertyObject.event.subscribe(propertyObject.handler,
                    this.owner);
            }

            this.setProperty(key, propertyObject.value, true);

            if (! propertyObject.suppressEvent) {
                this.queueProperty(key, propertyObject.value);
            }

        },

        /**
        * Returns a key-value configuration map of the values currently set in
        * the Config Object.
        * @method getConfig
        * @return {Object} The current config, represented in a key-value map
        */
        getConfig: function () {

            var cfg = {},
                currCfg = this.config,
                prop,
                property;

            for (prop in currCfg) {
                if (Lang.hasOwnProperty(currCfg, prop)) {
                    property = currCfg[prop];
                    if (property && property.event) {
                        cfg[prop] = property.value;
                    }
                }
            }

            return cfg;
        },

        /**
        * Returns the value of specified property.
        * @method getProperty
        * @param {String} key The name of the property
        * @return {Object}  The value of the specified property
        */
        getProperty: function (key) {
            var property = this.config[key.toLowerCase()];
            if (property && property.event) {
                return property.value;
            } else {
                return undefined;
            }
        },

        /**
        * Resets the specified property's value to its initial value.
        * @method resetProperty
        * @param {String} key The name of the property
        * @return {Boolean} True is the property was reset, false if not
        */
        resetProperty: function (key) {

            key = key.toLowerCase();

            var property = this.config[key];

            if (property && property.event) {

                if (this.initialConfig[key] &&
                    !Lang.isUndefined(this.initialConfig[key])) {

                    this.setProperty(key, this.initialConfig[key]);

                    return true;

                }

            } else {

                return false;
            }

        },

        /**
        * Sets the value of a property. If the silent property is passed as
        * true, the property's event will not be fired.
        * @method setProperty
        * @param {String} key The name of the property
        * @param {String} value The value to set the property to
        * @param {Boolean} silent Whether the value should be set silently,
        * without firing the property event.
        * @return {Boolean} True, if the set was successful, false if it failed.
        */
        setProperty: function (key, value, silent) {

            var property;

            key = key.toLowerCase();

            if (this.queueInProgress && ! silent) {
                // Currently running through a queue...
                this.queueProperty(key,value);
                return true;

            } else {
                property = this.config[key];
                if (property && property.event) {
                    if (property.validator && !property.validator(value)) {
                        return false;
                    } else {
                        property.value = value;
                        if (! silent) {
                            this.fireEvent(key, value);
                            this.configChangedEvent.fire([key, value]);
                        }
                        return true;
                    }
                } else {
                    return false;
                }
            }
        },

        /**
        * Sets the value of a property and queues its event to execute. If the
        * event is already scheduled to execute, it is
        * moved from its current position to the end of the queue.
        * @method queueProperty
        * @param {String} key The name of the property
        * @param {String} value The value to set the property to
        * @return {Boolean}  true, if the set was successful, false if
        * it failed.
        */
        queueProperty: function (key, value) {

            key = key.toLowerCase();

            var property = this.config[key],
                foundDuplicate = false,
                iLen,
                queueItem,
                queueItemKey,
                queueItemValue,
                sLen,
                supercedesCheck,
                qLen,
                queueItemCheck,
                queueItemCheckKey,
                queueItemCheckValue,
                i,
                s,
                q;

            if (property && property.event) {

                if (!Lang.isUndefined(value) && property.validator &&
                    !property.validator(value)) { // validator
                    return false;
                } else {

                    if (!Lang.isUndefined(value)) {
                        property.value = value;
                    } else {
                        value = property.value;
                    }

                    foundDuplicate = false;
                    iLen = this.eventQueue.length;

                    for (i = 0; i < iLen; i++) {
                        queueItem = this.eventQueue[i];

                        if (queueItem) {
                            queueItemKey = queueItem[0];
                            queueItemValue = queueItem[1];

                            if (queueItemKey == key) {

                                /*
                                    found a dupe... push to end of queue, null
                                    current item, and break
                                */

                                this.eventQueue[i] = null;

                                this.eventQueue.push(
                                    [key, (!Lang.isUndefined(value) ?
                                    value : queueItemValue)]);

                                foundDuplicate = true;
                                break;
                            }
                        }
                    }

                    // this is a refire, or a new property in the queue

                    if (! foundDuplicate && !Lang.isUndefined(value)) {
                        this.eventQueue.push([key, value]);
                    }
                }

                if (property.supercedes) {

                    sLen = property.supercedes.length;

                    for (s = 0; s < sLen; s++) {

                        supercedesCheck = property.supercedes[s];
                        qLen = this.eventQueue.length;

                        for (q = 0; q < qLen; q++) {
                            queueItemCheck = this.eventQueue[q];

                            if (queueItemCheck) {
                                queueItemCheckKey = queueItemCheck[0];
                                queueItemCheckValue = queueItemCheck[1];

                                if (queueItemCheckKey ==
                                    supercedesCheck.toLowerCase() ) {

                                    this.eventQueue.push([queueItemCheckKey,
                                        queueItemCheckValue]);

                                    this.eventQueue[q] = null;
                                    break;

                                }
                            }
                        }
                    }
                }


                return true;
            } else {
                return false;
            }
        },

        /**
        * Fires the event for a property using the property's current value.
        * @method refireEvent
        * @param {String} key The name of the property
        */
        refireEvent: function (key) {

            key = key.toLowerCase();

            var property = this.config[key];

            if (property && property.event &&

                !Lang.isUndefined(property.value)) {

                if (this.queueInProgress) {

                    this.queueProperty(key);

                } else {

                    this.fireEvent(key, property.value);

                }

            }
        },

        /**
        * Applies a key-value Object literal to the configuration, replacing
        * any existing values, and queueing the property events.
        * Although the values will be set, fireQueue() must be called for their
        * associated events to execute.
        * @method applyConfig
        * @param {Object} userConfig The configuration Object literal
        * @param {Boolean} init  When set to true, the initialConfig will
        * be set to the userConfig passed in, so that calling a reset will
        * reset the properties to the passed values.
        */
        applyConfig: function (userConfig, init) {

            var sKey,
                oConfig;

            if (init) {
                oConfig = {};
                for (sKey in userConfig) {
                    if (Lang.hasOwnProperty(userConfig, sKey)) {
                        oConfig[sKey.toLowerCase()] = userConfig[sKey];
                    }
                }
                this.initialConfig = oConfig;
            }

            for (sKey in userConfig) {
                if (Lang.hasOwnProperty(userConfig, sKey)) {
                    this.queueProperty(sKey, userConfig[sKey]);
                }
            }
        },

        /**
        * Refires the events for all configuration properties using their
        * current values.
        * @method refresh
        */
        refresh: function () {

            var prop;

            for (prop in this.config) {
                if (Lang.hasOwnProperty(this.config, prop)) {
                    this.refireEvent(prop);
                }
            }
        },

        /**
        * Fires the normalized list of queued property change events
        * @method fireQueue
        */
        fireQueue: function () {

            var i,
                queueItem,
                key,
                value,
                property;

            this.queueInProgress = true;
            for (i = 0;i < this.eventQueue.length; i++) {
                queueItem = this.eventQueue[i];
                if (queueItem) {

                    key = queueItem[0];
                    value = queueItem[1];
                    property = this.config[key];

                    property.value = value;

                    // Clear out queue entry, to avoid it being
                    // re-added to the queue by any queueProperty/supercedes
                    // calls which are invoked during fireEvent
                    this.eventQueue[i] = null;

                    this.fireEvent(key,value);
                }
            }

            this.queueInProgress = false;
            this.eventQueue = [];
        },

        /**
        * Subscribes an external handler to the change event for any
        * given property.
        * @method subscribeToConfigEvent
        * @param {String} key The property name
        * @param {Function} handler The handler function to use subscribe to
        * the property's event
        * @param {Object} obj The Object to use for scoping the event handler
        * (see CustomEvent documentation)
        * @param {Boolean} overrideContext Optional. If true, will override
        * "this" within the handler to map to the scope Object passed into the
        * method.
        * @return {Boolean} True, if the subscription was successful,
        * otherwise false.
        */
        subscribeToConfigEvent: function (key, handler, obj, overrideContext) {

            var property = this.config[key.toLowerCase()];

            if (property && property.event) {
                if (!Config.alreadySubscribed(property.event, handler, obj)) {
                    property.event.subscribe(handler, obj, overrideContext);
                }
                return true;
            } else {
                return false;
            }

        },

        /**
        * Unsubscribes an external handler from the change event for any
        * given property.
        * @method unsubscribeFromConfigEvent
        * @param {String} key The property name
        * @param {Function} handler The handler function to use subscribe to
        * the property's event
        * @param {Object} obj The Object to use for scoping the event
        * handler (see CustomEvent documentation)
        * @return {Boolean} True, if the unsubscription was successful,
        * otherwise false.
        */
        unsubscribeFromConfigEvent: function (key, handler, obj) {
            var property = this.config[key.toLowerCase()];
            if (property && property.event) {
                return property.event.unsubscribe(handler, obj);
            } else {
                return false;
            }
        },

        /**
        * Returns a string representation of the Config object
        * @method toString
        * @return {String} The Config object in string format.
        */
        toString: function () {
            var output = "Config";
            if (this.owner) {
                output += " [" + this.owner.toString() + "]";
            }
            return output;
        },

        /**
        * Returns a string representation of the Config object's current
        * CustomEvent queue
        * @method outputEventQueue
        * @return {String} The string list of CustomEvents currently queued
        * for execution
        */
        outputEventQueue: function () {

            var output = "",
                queueItem,
                q,
                nQueue = this.eventQueue.length;

            for (q = 0; q < nQueue; q++) {
                queueItem = this.eventQueue[q];
                if (queueItem) {
                    output += queueItem[0] + "=" + queueItem[1] + ", ";
                }
            }
            return output;
        },

        /**
        * Sets all properties to null, unsubscribes all listeners from each
        * property's change event and all listeners from the configChangedEvent.
        * @method destroy
        */
        destroy: function () {

            var oConfig = this.config,
                sProperty,
                oProperty;


            for (sProperty in oConfig) {

                if (Lang.hasOwnProperty(oConfig, sProperty)) {

                    oProperty = oConfig[sProperty];

                    oProperty.event.unsubscribeAll();
                    oProperty.event = null;

                }

            }

            this.configChangedEvent.unsubscribeAll();

            this.configChangedEvent = null;
            this.owner = null;
            this.config = null;
            this.initialConfig = null;
            this.eventQueue = null;

        }

    };



    /**
    * Checks to determine if a particular function/Object pair are already
    * subscribed to the specified CustomEvent
    * @method YAHOO.util.Config.alreadySubscribed
    * @static
    * @param {YAHOO.util.CustomEvent} evt The CustomEvent for which to check
    * the subscriptions
    * @param {Function} fn The function to look for in the subscribers list
    * @param {Object} obj The execution scope Object for the subscription
    * @return {Boolean} true, if the function/Object pair is already subscribed
    * to the CustomEvent passed in
    */
    Config.alreadySubscribed = function (evt, fn, obj) {

        var nSubscribers = evt.subscribers.length,
            subsc,
            i;

        if (nSubscribers > 0) {
            i = nSubscribers - 1;
            do {
                subsc = evt.subscribers[i];
                if (subsc && subsc.obj == obj && subsc.fn == fn) {
                    return true;
                }
            }
            while (i--);
        }

        return false;

    };

    YAHOO.lang.augmentProto(Config, YAHOO.util.EventProvider);

}());
(function () {

    /**
    * The Container family of components is designed to enable developers to
    * create different kinds of content-containing modules on the web. Module
    * and Overlay are the most basic containers, and they can be used directly
    * or extended to build custom containers. Also part of the Container family
    * are four UI controls that extend Module and Overlay: Tooltip, Panel,
    * Dialog, and SimpleDialog.
    * @module container
    * @title Container
    * @requires yahoo, dom, event
    * @optional dragdrop, animation, button
    */

    /**
    * Module is a JavaScript representation of the Standard Module Format.
    * Standard Module Format is a simple standard for markup containers where
    * child nodes representing the header, body, and footer of the content are
    * denoted using the CSS classes "hd", "bd", and "ft" respectively.
    * Module is the base class for all other classes in the YUI
    * Container package.
    * @namespace YAHOO.widget
    * @class Module
    * @constructor
    * @param {String} el The element ID representing the Module <em>OR</em>
    * @param {HTMLElement} el The element representing the Module
    * @param {Object} userConfig The configuration Object literal containing
    * the configuration that should be set for this module. See configuration
    * documentation for more details.
    */
    YAHOO.widget.Module = function (el, userConfig) {
        if (el) {
            this.init(el, userConfig);
        } else {
        }
    };

    var Dom = YAHOO.util.Dom,
        Config = YAHOO.util.Config,
        Event = YAHOO.util.Event,
        CustomEvent = YAHOO.util.CustomEvent,
        Module = YAHOO.widget.Module,
        UA = YAHOO.env.ua,

        m_oModuleTemplate,
        m_oHeaderTemplate,
        m_oBodyTemplate,
        m_oFooterTemplate,

        /**
        * Constant representing the name of the Module's events
        * @property EVENT_TYPES
        * @private
        * @final
        * @type Object
        */
        EVENT_TYPES = {
            "BEFORE_INIT": "beforeInit",
            "INIT": "init",
            "APPEND": "append",
            "BEFORE_RENDER": "beforeRender",
            "RENDER": "render",
            "CHANGE_HEADER": "changeHeader",
            "CHANGE_BODY": "changeBody",
            "CHANGE_FOOTER": "changeFooter",
            "CHANGE_CONTENT": "changeContent",
            "DESTROY": "destroy",
            "BEFORE_SHOW": "beforeShow",
            "SHOW": "show",
            "BEFORE_HIDE": "beforeHide",
            "HIDE": "hide"
        },

        /**
        * Constant representing the Module's configuration properties
        * @property DEFAULT_CONFIG
        * @private
        * @final
        * @type Object
        */
        DEFAULT_CONFIG = {

            "VISIBLE": {
                key: "visible",
                value: true,
                validator: YAHOO.lang.isBoolean
            },

            "EFFECT": {
                key: "effect",
                suppressEvent: true,
                supercedes: ["visible"]
            },

            "MONITOR_RESIZE": {
                key: "monitorresize",
                value: true
            },

            "APPEND_TO_DOCUMENT_BODY": {
                key: "appendtodocumentbody",
                value: false
            }
        };

    /**
    * Constant representing the prefix path to use for non-secure images
    * @property YAHOO.widget.Module.IMG_ROOT
    * @static
    * @final
    * @type String
    */
    Module.IMG_ROOT = null;

    /**
    * Constant representing the prefix path to use for securely served images
    * @property YAHOO.widget.Module.IMG_ROOT_SSL
    * @static
    * @final
    * @type String
    */
    Module.IMG_ROOT_SSL = null;

    /**
    * Constant for the default CSS class name that represents a Module
    * @property YAHOO.widget.Module.CSS_MODULE
    * @static
    * @final
    * @type String
    */
    Module.CSS_MODULE = "yui-module";

    /**
    * Constant representing the module header
    * @property YAHOO.widget.Module.CSS_HEADER
    * @static
    * @final
    * @type String
    */
    Module.CSS_HEADER = "hd";

    /**
    * Constant representing the module body
    * @property YAHOO.widget.Module.CSS_BODY
    * @static
    * @final
    * @type String
    */
    Module.CSS_BODY = "bd";

    /**
    * Constant representing the module footer
    * @property YAHOO.widget.Module.CSS_FOOTER
    * @static
    * @final
    * @type String
    */
    Module.CSS_FOOTER = "ft";

    /**
    * Constant representing the url for the "src" attribute of the iframe
    * used to monitor changes to the browser's base font size
    * @property YAHOO.widget.Module.RESIZE_MONITOR_SECURE_URL
    * @static
    * @final
    * @type String
    */
    Module.RESIZE_MONITOR_SECURE_URL = "javascript:false;";

    /**
    * Constant representing the buffer amount (in pixels) to use when positioning
    * the text resize monitor offscreen. The resize monitor is positioned
    * offscreen by an amount eqaul to its offsetHeight + the buffer value.
    *
    * @property YAHOO.widget.Module.RESIZE_MONITOR_BUFFER
    * @static
    * @type Number
    */
    // Set to 1, to work around pixel offset in IE8, which increases when zoom is used
    Module.RESIZE_MONITOR_BUFFER = 1;

    /**
    * Singleton CustomEvent fired when the font size is changed in the browser.
    * Opera's "zoom" functionality currently does not support text
    * size detection.
    * @event YAHOO.widget.Module.textResizeEvent
    */
    Module.textResizeEvent = new CustomEvent("textResize");

    /**
     * Helper utility method, which forces a document level
     * redraw for Opera, which can help remove repaint
     * irregularities after applying DOM changes.
     *
     * @method YAHOO.widget.Module.forceDocumentRedraw
     * @static
     */
    Module.forceDocumentRedraw = function() {
        var docEl = document.documentElement;
        if (docEl) {
            docEl.className += " ";
            docEl.className = YAHOO.lang.trim(docEl.className);
        }
    };

    function createModuleTemplate() {

        if (!m_oModuleTemplate) {
            m_oModuleTemplate = document.createElement("div");

            m_oModuleTemplate.innerHTML = ("<div class=\"" +
                Module.CSS_HEADER + "\"></div>" + "<div class=\"" +
                Module.CSS_BODY + "\"></div><div class=\"" +
                Module.CSS_FOOTER + "\"></div>");

            m_oHeaderTemplate = m_oModuleTemplate.firstChild;
            m_oBodyTemplate = m_oHeaderTemplate.nextSibling;
            m_oFooterTemplate = m_oBodyTemplate.nextSibling;
        }

        return m_oModuleTemplate;
    }

    function createHeader() {
        if (!m_oHeaderTemplate) {
            createModuleTemplate();
        }
        return (m_oHeaderTemplate.cloneNode(false));
    }

    function createBody() {
        if (!m_oBodyTemplate) {
            createModuleTemplate();
        }
        return (m_oBodyTemplate.cloneNode(false));
    }

    function createFooter() {
        if (!m_oFooterTemplate) {
            createModuleTemplate();
        }
        return (m_oFooterTemplate.cloneNode(false));
    }

    Module.prototype = {

        /**
        * The class's constructor function
        * @property contructor
        * @type Function
        */
        constructor: Module,

        /**
        * The main module element that contains the header, body, and footer
        * @property element
        * @type HTMLElement
        */
        element: null,

        /**
        * The header element, denoted with CSS class "hd"
        * @property header
        * @type HTMLElement
        */
        header: null,

        /**
        * The body element, denoted with CSS class "bd"
        * @property body
        * @type HTMLElement
        */
        body: null,

        /**
        * The footer element, denoted with CSS class "ft"
        * @property footer
        * @type HTMLElement
        */
        footer: null,

        /**
        * The id of the element
        * @property id
        * @type String
        */
        id: null,

        /**
        * A string representing the root path for all images created by
        * a Module instance.
        * @deprecated It is recommend that any images for a Module be applied
        * via CSS using the "background-image" property.
        * @property imageRoot
        * @type String
        */
        imageRoot: Module.IMG_ROOT,

        /**
        * Initializes the custom events for Module which are fired
        * automatically at appropriate times by the Module class.
        * @method initEvents
        */
        initEvents: function () {

            var SIGNATURE = CustomEvent.LIST;

            /**
            * CustomEvent fired prior to class initalization.
            * @event beforeInitEvent
            * @param {class} classRef class reference of the initializing
            * class, such as this.beforeInitEvent.fire(Module)
            */
            this.beforeInitEvent = this.createEvent(EVENT_TYPES.BEFORE_INIT);
            this.beforeInitEvent.signature = SIGNATURE;

            /**
            * CustomEvent fired after class initalization.
            * @event initEvent
            * @param {class} classRef class reference of the initializing
            * class, such as this.beforeInitEvent.fire(Module)
            */
            this.initEvent = this.createEvent(EVENT_TYPES.INIT);
            this.initEvent.signature = SIGNATURE;

            /**
            * CustomEvent fired when the Module is appended to the DOM
            * @event appendEvent
            */
            this.appendEvent = this.createEvent(EVENT_TYPES.APPEND);
            this.appendEvent.signature = SIGNATURE;

            /**
            * CustomEvent fired before the Module is rendered
            * @event beforeRenderEvent
            */
            this.beforeRenderEvent = this.createEvent(EVENT_TYPES.BEFORE_RENDER);
            this.beforeRenderEvent.signature = SIGNATURE;

            /**
            * CustomEvent fired after the Module is rendered
            * @event renderEvent
            */
            this.renderEvent = this.createEvent(EVENT_TYPES.RENDER);
            this.renderEvent.signature = SIGNATURE;

            /**
            * CustomEvent fired when the header content of the Module
            * is modified
            * @event changeHeaderEvent
            * @param {String/HTMLElement} content String/element representing
            * the new header content
            */
            this.changeHeaderEvent = this.createEvent(EVENT_TYPES.CHANGE_HEADER);
            this.changeHeaderEvent.signature = SIGNATURE;

            /**
            * CustomEvent fired when the body content of the Module is modified
            * @event changeBodyEvent
            * @param {String/HTMLElement} content String/element representing
            * the new body content
            */
            this.changeBodyEvent = this.createEvent(EVENT_TYPES.CHANGE_BODY);
            this.changeBodyEvent.signature = SIGNATURE;

            /**
            * CustomEvent fired when the footer content of the Module
            * is modified
            * @event changeFooterEvent
            * @param {String/HTMLElement} content String/element representing
            * the new footer content
            */
            this.changeFooterEvent = this.createEvent(EVENT_TYPES.CHANGE_FOOTER);
            this.changeFooterEvent.signature = SIGNATURE;

            /**
            * CustomEvent fired when the content of the Module is modified
            * @event changeContentEvent
            */
            this.changeContentEvent = this.createEvent(EVENT_TYPES.CHANGE_CONTENT);
            this.changeContentEvent.signature = SIGNATURE;

            /**
            * CustomEvent fired when the Module is destroyed
            * @event destroyEvent
            */
            this.destroyEvent = this.createEvent(EVENT_TYPES.DESTROY);
            this.destroyEvent.signature = SIGNATURE;

            /**
            * CustomEvent fired before the Module is shown
            * @event beforeShowEvent
            */
            this.beforeShowEvent = this.createEvent(EVENT_TYPES.BEFORE_SHOW);
            this.beforeShowEvent.signature = SIGNATURE;

            /**
            * CustomEvent fired after the Module is shown
            * @event showEvent
            */
            this.showEvent = this.createEvent(EVENT_TYPES.SHOW);
            this.showEvent.signature = SIGNATURE;

            /**
            * CustomEvent fired before the Module is hidden
            * @event beforeHideEvent
            */
            this.beforeHideEvent = this.createEvent(EVENT_TYPES.BEFORE_HIDE);
            this.beforeHideEvent.signature = SIGNATURE;

            /**
            * CustomEvent fired after the Module is hidden
            * @event hideEvent
            */
            this.hideEvent = this.createEvent(EVENT_TYPES.HIDE);
            this.hideEvent.signature = SIGNATURE;
        },

        /**
        * String representing the current user-agent platform
        * @property platform
        * @type String
        */
        platform: function () {
            var ua = navigator.userAgent.toLowerCase();

            if (ua.indexOf("windows") != -1 || ua.indexOf("win32") != -1) {
                return "windows";
            } else if (ua.indexOf("macintosh") != -1) {
                return "mac";
            } else {
                return false;
            }
        }(),

        /**
        * String representing the user-agent of the browser
        * @deprecated Use YAHOO.env.ua
        * @property browser
        * @type String
        */
        browser: function () {
            var ua = navigator.userAgent.toLowerCase();
            /*
                 Check Opera first in case of spoof and check Safari before
                 Gecko since Safari's user agent string includes "like Gecko"
            */
            if (ua.indexOf('opera') != -1) {
                return 'opera';
            } else if (ua.indexOf('msie 7') != -1) {
                return 'ie7';
            } else if (ua.indexOf('msie') != -1) {
                return 'ie';
            } else if (ua.indexOf('safari') != -1) {
                return 'safari';
            } else if (ua.indexOf('gecko') != -1) {
                return 'gecko';
            } else {
                return false;
            }
        }(),

        /**
        * Boolean representing whether or not the current browsing context is
        * secure (https)
        * @property isSecure
        * @type Boolean
        */
        isSecure: function () {
            if (window.location.href.toLowerCase().indexOf("https") === 0) {
                return true;
            } else {
                return false;
            }
        }(),

        /**
        * Initializes the custom events for Module which are fired
        * automatically at appropriate times by the Module class.
        */
        initDefaultConfig: function () {
            // Add properties //
            /**
            * Specifies whether the Module is visible on the page.
            * @config visible
            * @type Boolean
            * @default true
            */
            this.cfg.addProperty(DEFAULT_CONFIG.VISIBLE.key, {
                handler: this.configVisible,
                value: DEFAULT_CONFIG.VISIBLE.value,
                validator: DEFAULT_CONFIG.VISIBLE.validator
            });

            /**
            * <p>
            * Object or array of objects representing the ContainerEffect
            * classes that are active for animating the container.
            * </p>
            * <p>
            * <strong>NOTE:</strong> Although this configuration
            * property is introduced at the Module level, an out of the box
            * implementation is not shipped for the Module class so setting
            * the proroperty on the Module class has no effect. The Overlay
            * class is the first class to provide out of the box ContainerEffect
            * support.
            * </p>
            * @config effect
            * @type Object
            * @default null
            */
            this.cfg.addProperty(DEFAULT_CONFIG.EFFECT.key, {
                suppressEvent: DEFAULT_CONFIG.EFFECT.suppressEvent,
                supercedes: DEFAULT_CONFIG.EFFECT.supercedes
            });

            /**
            * Specifies whether to create a special proxy iframe to monitor
            * for user font resizing in the document
            * @config monitorresize
            * @type Boolean
            * @default true
            */
            this.cfg.addProperty(DEFAULT_CONFIG.MONITOR_RESIZE.key, {
                handler: this.configMonitorResize,
                value: DEFAULT_CONFIG.MONITOR_RESIZE.value
            });

            /**
            * Specifies if the module should be rendered as the first child
            * of document.body or appended as the last child when render is called
            * with document.body as the "appendToNode".
            * <p>
            * Appending to the body while the DOM is still being constructed can
            * lead to Operation Aborted errors in IE hence this flag is set to
            * false by default.
            * </p>
            *
            * @config appendtodocumentbody
            * @type Boolean
            * @default false
            */
            this.cfg.addProperty(DEFAULT_CONFIG.APPEND_TO_DOCUMENT_BODY.key, {
                value: DEFAULT_CONFIG.APPEND_TO_DOCUMENT_BODY.value
            });
        },

        /**
        * The Module class's initialization method, which is executed for
        * Module and all of its subclasses. This method is automatically
        * called by the constructor, and  sets up all DOM references for
        * pre-existing markup, and creates required markup if it is not
        * already present.
        * <p>
        * If the element passed in does not have an id, one will be generated
        * for it.
        * </p>
        * @method init
        * @param {String} el The element ID representing the Module <em>OR</em>
        * @param {HTMLElement} el The element representing the Module
        * @param {Object} userConfig The configuration Object literal
        * containing the configuration that should be set for this module.
        * See configuration documentation for more details.
        */
        init: function (el, userConfig) {

            var elId, child;

            this.initEvents();
            this.beforeInitEvent.fire(Module);

            /**
            * The Module's Config object used for monitoring
            * configuration properties.
            * @property cfg
            * @type YAHOO.util.Config
            */
            this.cfg = new Config(this);

            if (this.isSecure) {
                this.imageRoot = Module.IMG_ROOT_SSL;
            }

            if (typeof el == "string") {
                elId = el;
                el = document.getElementById(el);
                if (! el) {
                    el = (createModuleTemplate()).cloneNode(false);
                    el.id = elId;
                }
            }

            this.id = Dom.generateId(el);
            this.element = el;

            child = this.element.firstChild;

            if (child) {
                var fndHd = false, fndBd = false, fndFt = false;
                do {
                    // We're looking for elements
                    if (1 == child.nodeType) {
                        if (!fndHd && Dom.hasClass(child, Module.CSS_HEADER)) {
                            this.header = child;
                            fndHd = true;
                        } else if (!fndBd && Dom.hasClass(child, Module.CSS_BODY)) {
                            this.body = child;
                            fndBd = true;
                        } else if (!fndFt && Dom.hasClass(child, Module.CSS_FOOTER)){
                            this.footer = child;
                            fndFt = true;
                        }
                    }
                } while ((child = child.nextSibling));
            }

            this.initDefaultConfig();

            Dom.addClass(this.element, Module.CSS_MODULE);

            if (userConfig) {
                this.cfg.applyConfig(userConfig, true);
            }

            /*
                Subscribe to the fireQueue() method of Config so that any
                queued configuration changes are excecuted upon render of
                the Module
            */

            if (!Config.alreadySubscribed(this.renderEvent, this.cfg.fireQueue, this.cfg)) {
                this.renderEvent.subscribe(this.cfg.fireQueue, this.cfg, true);
            }

            this.initEvent.fire(Module);
        },

        /**
        * Initialize an empty IFRAME that is placed out of the visible area
        * that can be used to detect text resize.
        * @method initResizeMonitor
        */
        initResizeMonitor: function () {

            var isGeckoWin = (UA.gecko && this.platform == "windows");
            if (isGeckoWin) {
                // Help prevent spinning loading icon which
                // started with FireFox 2.0.0.8/Win
                var self = this;
                setTimeout(function(){self._initResizeMonitor();}, 0);
            } else {
                this._initResizeMonitor();
            }
        },

        /**
         * Create and initialize the text resize monitoring iframe.
         *
         * @protected
         * @method _initResizeMonitor
         */
        _initResizeMonitor : function() {

            var oDoc,
                oIFrame,
                sHTML;

            function fireTextResize() {
                Module.textResizeEvent.fire();
            }

            if (!UA.opera) {
                oIFrame = Dom.get("_yuiResizeMonitor");

                var supportsCWResize = this._supportsCWResize();

                if (!oIFrame) {
                    oIFrame = document.createElement("iframe");

                    if (this.isSecure && Module.RESIZE_MONITOR_SECURE_URL && UA.ie) {
                        oIFrame.src = Module.RESIZE_MONITOR_SECURE_URL;
                    }

                    if (!supportsCWResize) {
                        // Can't monitor on contentWindow, so fire from inside iframe
                        sHTML = ["<html><head><script ",
                                 "type=\"text/javascript\">",
                                 "window.onresize=function(){window.parent.",
                                 "YAHOO.widget.Module.textResizeEvent.",
                                 "fire();};<",
                                 "\/script></head>",
                                 "<body></body></html>"].join('');

                        oIFrame.src = "data:text/html;charset=utf-8," + encodeURIComponent(sHTML);
                    }

                    oIFrame.id = "_yuiResizeMonitor";
                    oIFrame.title = "Text Resize Monitor";
                    /*
                        Need to set "position" property before inserting the
                        iframe into the document or Safari's status bar will
                        forever indicate the iframe is loading
                        (See YUILibrary bug #1723064)
                    */
                    oIFrame.style.position = "absolute";
                    oIFrame.style.visibility = "hidden";

                    var db = document.body,
                        fc = db.firstChild;
                    if (fc) {
                        db.insertBefore(oIFrame, fc);
                    } else {
                        db.appendChild(oIFrame);
                    }

                    // Setting the background color fixes an issue with IE6/IE7, where
                    // elements in the DOM, with -ve margin-top which positioned them
                    // offscreen (so they would be overlapped by the iframe and its -ve top
                    // setting), would have their -ve margin-top ignored, when the iframe
                    // was added.
                    oIFrame.style.backgroundColor = "transparent";

                    oIFrame.style.borderWidth = "0";
                    oIFrame.style.width = "2em";
                    oIFrame.style.height = "2em";
                    oIFrame.style.left = "0";
                    oIFrame.style.top = (-1 * (oIFrame.offsetHeight + Module.RESIZE_MONITOR_BUFFER)) + "px";
                    oIFrame.style.visibility = "visible";

                    /*
                       Don't open/close the document for Gecko like we used to, since it
                       leads to duplicate cookies. (See YUILibrary bug #1721755)
                    */
                    if (UA.webkit) {
                        oDoc = oIFrame.contentWindow.document;
                        oDoc.open();
                        oDoc.close();
                    }
                }

                if (oIFrame && oIFrame.contentWindow) {
                    Module.textResizeEvent.subscribe(this.onDomResize, this, true);

                    if (!Module.textResizeInitialized) {
                        if (supportsCWResize) {
                            if (!Event.on(oIFrame.contentWindow, "resize", fireTextResize)) {
                                /*
                                     This will fail in IE if document.domain has
                                     changed, so we must change the listener to
                                     use the oIFrame element instead
                                */
                                Event.on(oIFrame, "resize", fireTextResize);
                            }
                        }
                        Module.textResizeInitialized = true;
                    }
                    this.resizeMonitor = oIFrame;
                }
            }
        },

        /**
         * Text resize monitor helper method.
         * Determines if the browser supports resize events on iframe content windows.
         *
         * @private
         * @method _supportsCWResize
         */
        _supportsCWResize : function() {
            /*
                Gecko 1.8.0 (FF1.5), 1.8.1.0-5 (FF2) won't fire resize on contentWindow.
                Gecko 1.8.1.6+ (FF2.0.0.6+) and all other browsers will fire resize on contentWindow.

                We don't want to start sniffing for patch versions, so fire textResize the same
                way on all FF2 flavors
             */
            var bSupported = true;
            if (UA.gecko && UA.gecko <= 1.8) {
                bSupported = false;
            }
            return bSupported;
        },

        /**
        * Event handler fired when the resize monitor element is resized.
        * @method onDomResize
        * @param {DOMEvent} e The DOM resize event
        * @param {Object} obj The scope object passed to the handler
        */
        onDomResize: function (e, obj) {

            var nTop = -1 * (this.resizeMonitor.offsetHeight + Module.RESIZE_MONITOR_BUFFER);

            this.resizeMonitor.style.top = nTop + "px";
            this.resizeMonitor.style.left = "0";
        },

        /**
        * Sets the Module's header content to the string specified, or appends
        * the passed element to the header. If no header is present, one will
        * be automatically created. An empty string can be passed to the method
        * to clear the contents of the header.
        *
        * @method setHeader
        * @param {String} headerContent The string used to set the header.
        * As a convenience, non HTMLElement objects can also be passed into
        * the method, and will be treated as strings, with the header innerHTML
        * set to their default toString implementations.
        * <em>OR</em>
        * @param {HTMLElement} headerContent The HTMLElement to append to
        * <em>OR</em>
        * @param {DocumentFragment} headerContent The document fragment
        * containing elements which are to be added to the header
        */
        setHeader: function (headerContent) {
            var oHeader = this.header || (this.header = createHeader());

            if (headerContent.nodeName) {
                oHeader.innerHTML = "";
                oHeader.appendChild(headerContent);
            } else {
                oHeader.innerHTML = headerContent;
            }

            if (this._rendered) {
                this._renderHeader();
            }

            this.changeHeaderEvent.fire(headerContent);
            this.changeContentEvent.fire();

        },

        /**
        * Appends the passed element to the header. If no header is present,
        * one will be automatically created.
        * @method appendToHeader
        * @param {HTMLElement | DocumentFragment} element The element to
        * append to the header. In the case of a document fragment, the
        * children of the fragment will be appended to the header.
        */
        appendToHeader: function (element) {
            var oHeader = this.header || (this.header = createHeader());

            oHeader.appendChild(element);

            this.changeHeaderEvent.fire(element);
            this.changeContentEvent.fire();

        },

        /**
        * Sets the Module's body content to the HTML specified.
        *
        * If no body is present, one will be automatically created.
        *
        * An empty string can be passed to the method to clear the contents of the body.
        * @method setBody
        * @param {String} bodyContent The HTML used to set the body.
        * As a convenience, non HTMLElement objects can also be passed into
        * the method, and will be treated as strings, with the body innerHTML
        * set to their default toString implementations.
        * <em>OR</em>
        * @param {HTMLElement} bodyContent The HTMLElement to add as the first and only
        * child of the body element.
        * <em>OR</em>
        * @param {DocumentFragment} bodyContent The document fragment
        * containing elements which are to be added to the body
        */
        setBody: function (bodyContent) {
            var oBody = this.body || (this.body = createBody());

            if (bodyContent.nodeName) {
                oBody.innerHTML = "";
                oBody.appendChild(bodyContent);
            } else {
                oBody.innerHTML = bodyContent;
            }

            if (this._rendered) {
                this._renderBody();
            }

            this.changeBodyEvent.fire(bodyContent);
            this.changeContentEvent.fire();
        },

        /**
        * Appends the passed element to the body. If no body is present, one
        * will be automatically created.
        * @method appendToBody
        * @param {HTMLElement | DocumentFragment} element The element to
        * append to the body. In the case of a document fragment, the
        * children of the fragment will be appended to the body.
        *
        */
        appendToBody: function (element) {
            var oBody = this.body || (this.body = createBody());

            oBody.appendChild(element);

            this.changeBodyEvent.fire(element);
            this.changeContentEvent.fire();

        },

        /**
        * Sets the Module's footer content to the HTML specified, or appends
        * the passed element to the footer. If no footer is present, one will
        * be automatically created. An empty string can be passed to the method
        * to clear the contents of the footer.
        * @method setFooter
        * @param {String} footerContent The HTML used to set the footer
        * As a convenience, non HTMLElement objects can also be passed into
        * the method, and will be treated as strings, with the footer innerHTML
        * set to their default toString implementations.
        * <em>OR</em>
        * @param {HTMLElement} footerContent The HTMLElement to append to
        * the footer
        * <em>OR</em>
        * @param {DocumentFragment} footerContent The document fragment containing
        * elements which are to be added to the footer
        */
        setFooter: function (footerContent) {

            var oFooter = this.footer || (this.footer = createFooter());

            if (footerContent.nodeName) {
                oFooter.innerHTML = "";
                oFooter.appendChild(footerContent);
            } else {
                oFooter.innerHTML = footerContent;
            }

            if (this._rendered) {
                this._renderFooter();
            }

            this.changeFooterEvent.fire(footerContent);
            this.changeContentEvent.fire();
        },

        /**
        * Appends the passed element to the footer. If no footer is present,
        * one will be automatically created.
        * @method appendToFooter
        * @param {HTMLElement | DocumentFragment} element The element to
        * append to the footer. In the case of a document fragment, the
        * children of the fragment will be appended to the footer
        */
        appendToFooter: function (element) {

            var oFooter = this.footer || (this.footer = createFooter());

            oFooter.appendChild(element);

            this.changeFooterEvent.fire(element);
            this.changeContentEvent.fire();

        },

        /**
        * Renders the Module by inserting the elements that are not already
        * in the main Module into their correct places. Optionally appends
        * the Module to the specified node prior to the render's execution.
        * <p>
        * For Modules without existing markup, the appendToNode argument
        * is REQUIRED. If this argument is ommitted and the current element is
        * not present in the document, the function will return false,
        * indicating that the render was a failure.
        * </p>
        * <p>
        * NOTE: As of 2.3.1, if the appendToNode is the document's body element
        * then the module is rendered as the first child of the body element,
        * and not appended to it, to avoid Operation Aborted errors in IE when
        * rendering the module before window's load event is fired. You can
        * use the appendtodocumentbody configuration property to change this
        * to append to document.body if required.
        * </p>
        * @method render
        * @param {String} appendToNode The element id to which the Module
        * should be appended to prior to rendering <em>OR</em>
        * @param {HTMLElement} appendToNode The element to which the Module
        * should be appended to prior to rendering
        * @param {HTMLElement} moduleElement OPTIONAL. The element that
        * represents the actual Standard Module container.
        * @return {Boolean} Success or failure of the render
        */
        render: function (appendToNode, moduleElement) {

            var me = this;

            function appendTo(parentNode) {
                if (typeof parentNode == "string") {
                    parentNode = document.getElementById(parentNode);
                }

                if (parentNode) {
                    me._addToParent(parentNode, me.element);
                    me.appendEvent.fire();
                }
            }

            this.beforeRenderEvent.fire();

            if (! moduleElement) {
                moduleElement = this.element;
            }

            if (appendToNode) {
                appendTo(appendToNode);
            } else {
                // No node was passed in. If the element is not already in the Dom, this fails
                if (! Dom.inDocument(this.element)) {
                    return false;
                }
            }

            this._renderHeader(moduleElement);
            this._renderBody(moduleElement);
            this._renderFooter(moduleElement);

            this._rendered = true;

            this.renderEvent.fire();
            return true;
        },

        /**
         * Renders the currently set header into it's proper position under the
         * module element. If the module element is not provided, "this.element"
         * is used.
         *
         * @method _renderHeader
         * @protected
         * @param {HTMLElement} moduleElement Optional. A reference to the module element
         */
        _renderHeader: function(moduleElement){
            moduleElement = moduleElement || this.element;

            // Need to get everything into the DOM if it isn't already
            if (this.header && !Dom.inDocument(this.header)) {
                // There is a header, but it's not in the DOM yet. Need to add it.
                var firstChild = moduleElement.firstChild;
                if (firstChild) {
                    moduleElement.insertBefore(this.header, firstChild);
                } else {
                    moduleElement.appendChild(this.header);
                }
            }
        },

        /**
         * Renders the currently set body into it's proper position under the
         * module element. If the module element is not provided, "this.element"
         * is used.
         *
         * @method _renderBody
         * @protected
         * @param {HTMLElement} moduleElement Optional. A reference to the module element.
         */
        _renderBody: function(moduleElement){
            moduleElement = moduleElement || this.element;

            if (this.body && !Dom.inDocument(this.body)) {
                // There is a body, but it's not in the DOM yet. Need to add it.
                if (this.footer && Dom.isAncestor(moduleElement, this.footer)) {
                    moduleElement.insertBefore(this.body, this.footer);
                } else {
                    moduleElement.appendChild(this.body);
                }
            }
        },

        /**
         * Renders the currently set footer into it's proper position under the
         * module element. If the module element is not provided, "this.element"
         * is used.
         *
         * @method _renderFooter
         * @protected
         * @param {HTMLElement} moduleElement Optional. A reference to the module element
         */
        _renderFooter: function(moduleElement){
            moduleElement = moduleElement || this.element;

            if (this.footer && !Dom.inDocument(this.footer)) {
                // There is a footer, but it's not in the DOM yet. Need to add it.
                moduleElement.appendChild(this.footer);
            }
        },

        /**
        * Removes the Module element from the DOM and sets all child elements
        * to null.
        * @method destroy
        */
        destroy: function () {

            var parent;

            if (this.element) {
                Event.purgeElement(this.element, true);
                parent = this.element.parentNode;
            }

            if (parent) {
                parent.removeChild(this.element);
            }

            this.element = null;
            this.header = null;
            this.body = null;
            this.footer = null;

            Module.textResizeEvent.unsubscribe(this.onDomResize, this);

            this.cfg.destroy();
            this.cfg = null;

            this.destroyEvent.fire();
        },

        /**
        * Shows the Module element by setting the visible configuration
        * property to true. Also fires two events: beforeShowEvent prior to
        * the visibility change, and showEvent after.
        * @method show
        */
        show: function () {
            this.cfg.setProperty("visible", true);
        },

        /**
        * Hides the Module element by setting the visible configuration
        * property to false. Also fires two events: beforeHideEvent prior to
        * the visibility change, and hideEvent after.
        * @method hide
        */
        hide: function () {
            this.cfg.setProperty("visible", false);
        },

        // BUILT-IN EVENT HANDLERS FOR MODULE //
        /**
        * Default event handler for changing the visibility property of a
        * Module. By default, this is achieved by switching the "display" style
        * between "block" and "none".
        * This method is responsible for firing showEvent and hideEvent.
        * @param {String} type The CustomEvent type (usually the property name)
        * @param {Object[]} args The CustomEvent arguments. For configuration
        * handlers, args[0] will equal the newly applied value for the property.
        * @param {Object} obj The scope object. For configuration handlers,
        * this will usually equal the owner.
        * @method configVisible
        */
        configVisible: function (type, args, obj) {
            var visible = args[0];
            if (visible) {
                this.beforeShowEvent.fire();
                Dom.setStyle(this.element, "display", "block");
                this.showEvent.fire();
            } else {
                this.beforeHideEvent.fire();
                Dom.setStyle(this.element, "display", "none");
                this.hideEvent.fire();
            }
        },

        /**
        * Default event handler for the "monitorresize" configuration property
        * @param {String} type The CustomEvent type (usually the property name)
        * @param {Object[]} args The CustomEvent arguments. For configuration
        * handlers, args[0] will equal the newly applied value for the property.
        * @param {Object} obj The scope object. For configuration handlers,
        * this will usually equal the owner.
        * @method configMonitorResize
        */
        configMonitorResize: function (type, args, obj) {
            var monitor = args[0];
            if (monitor) {
                this.initResizeMonitor();
            } else {
                Module.textResizeEvent.unsubscribe(this.onDomResize, this, true);
                this.resizeMonitor = null;
            }
        },

        /**
         * This method is a protected helper, used when constructing the DOM structure for the module
         * to account for situations which may cause Operation Aborted errors in IE. It should not
         * be used for general DOM construction.
         * <p>
         * If the parentNode is not document.body, the element is appended as the last element.
         * </p>
         * <p>
         * If the parentNode is document.body the element is added as the first child to help
         * prevent Operation Aborted errors in IE.
         * </p>
         *
         * @param {parentNode} The HTML element to which the element will be added
         * @param {element} The HTML element to be added to parentNode's children
         * @method _addToParent
         * @protected
         */
        _addToParent: function(parentNode, element) {
            if (!this.cfg.getProperty("appendtodocumentbody") && parentNode === document.body && parentNode.firstChild) {
                parentNode.insertBefore(element, parentNode.firstChild);
            } else {
                parentNode.appendChild(element);
            }
        },

        /**
        * Returns a String representation of the Object.
        * @method toString
        * @return {String} The string representation of the Module
        */
        toString: function () {
            return "Module " + this.id;
        }
    };

    YAHOO.lang.augmentProto(Module, YAHOO.util.EventProvider);

}());
(function () {

    /**
    * Overlay is a Module that is absolutely positioned above the page flow. It
    * has convenience methods for positioning and sizing, as well as options for
    * controlling zIndex and constraining the Overlay's position to the current
    * visible viewport. Overlay also contains a dynamicly generated IFRAME which
    * is placed beneath it for Internet Explorer 6 and 5.x so that it will be
    * properly rendered above SELECT elements.
    * @namespace YAHOO.widget
    * @class Overlay
    * @extends YAHOO.widget.Module
    * @param {String} el The element ID representing the Overlay <em>OR</em>
    * @param {HTMLElement} el The element representing the Overlay
    * @param {Object} userConfig The configuration object literal containing
    * the configuration that should be set for this Overlay. See configuration
    * documentation for more details.
    * @constructor
    */
    YAHOO.widget.Overlay = function (el, userConfig) {
        YAHOO.widget.Overlay.superclass.constructor.call(this, el, userConfig);
    };

    var Lang = YAHOO.lang,
        CustomEvent = YAHOO.util.CustomEvent,
        Module = YAHOO.widget.Module,
        Event = YAHOO.util.Event,
        Dom = YAHOO.util.Dom,
        Config = YAHOO.util.Config,
        UA = YAHOO.env.ua,
        Overlay = YAHOO.widget.Overlay,

        _SUBSCRIBE = "subscribe",
        _UNSUBSCRIBE = "unsubscribe",
        _CONTAINED = "contained",

        m_oIFrameTemplate,

        /**
        * Constant representing the name of the Overlay's events
        * @property EVENT_TYPES
        * @private
        * @final
        * @type Object
        */
        EVENT_TYPES = {
            "BEFORE_MOVE": "beforeMove",
            "MOVE": "move"
        },

        /**
        * Constant representing the Overlay's configuration properties
        * @property DEFAULT_CONFIG
        * @private
        * @final
        * @type Object
        */
        DEFAULT_CONFIG = {

            "X": {
                key: "x",
                validator: Lang.isNumber,
                suppressEvent: true,
                supercedes: ["iframe"]
            },

            "Y": {
                key: "y",
                validator: Lang.isNumber,
                suppressEvent: true,
                supercedes: ["iframe"]
            },

            "XY": {
                key: "xy",
                suppressEvent: true,
                supercedes: ["iframe"]
            },

            "CONTEXT": {
                key: "context",
                suppressEvent: true,
                supercedes: ["iframe"]
            },

            "FIXED_CENTER": {
                key: "fixedcenter",
                value: false,
                supercedes: ["iframe", "visible"]
            },

            "WIDTH": {
                key: "width",
                suppressEvent: true,
                supercedes: ["context", "fixedcenter", "iframe"]
            },

            "HEIGHT": {
                key: "height",
                suppressEvent: true,
                supercedes: ["context", "fixedcenter", "iframe"]
            },

            "AUTO_FILL_HEIGHT" : {
                key: "autofillheight",
                supercedes: ["height"],
                value:"body"
            },

            "ZINDEX": {
                key: "zindex",
                value: null
            },

            "CONSTRAIN_TO_VIEWPORT": {
                key: "constraintoviewport",
                value: false,
                validator: Lang.isBoolean,
                supercedes: ["iframe", "x", "y", "xy"]
            },

            "IFRAME": {
                key: "iframe",
                value: (UA.ie == 6 ? true : false),
                validator: Lang.isBoolean,
                supercedes: ["zindex"]
            },

            "PREVENT_CONTEXT_OVERLAP": {
                key: "preventcontextoverlap",
                value: false,
                validator: Lang.isBoolean,
                supercedes: ["constraintoviewport"]
            }

        };

    /**
    * The URL that will be placed in the iframe
    * @property YAHOO.widget.Overlay.IFRAME_SRC
    * @static
    * @final
    * @type String
    */
    Overlay.IFRAME_SRC = "javascript:false;";

    /**
    * Number representing how much the iframe shim should be offset from each
    * side of an Overlay instance, in pixels.
    * @property YAHOO.widget.Overlay.IFRAME_SRC
    * @default 3
    * @static
    * @final
    * @type Number
    */
    Overlay.IFRAME_OFFSET = 3;

    /**
    * Number representing the minimum distance an Overlay instance should be
    * positioned relative to the boundaries of the browser's viewport, in pixels.
    * @property YAHOO.widget.Overlay.VIEWPORT_OFFSET
    * @default 10
    * @static
    * @final
    * @type Number
    */
    Overlay.VIEWPORT_OFFSET = 10;

    /**
    * Constant representing the top left corner of an element, used for
    * configuring the context element alignment
    * @property YAHOO.widget.Overlay.TOP_LEFT
    * @static
    * @final
    * @type String
    */
    Overlay.TOP_LEFT = "tl";

    /**
    * Constant representing the top right corner of an element, used for
    * configuring the context element alignment
    * @property YAHOO.widget.Overlay.TOP_RIGHT
    * @static
    * @final
    * @type String
    */
    Overlay.TOP_RIGHT = "tr";

    /**
    * Constant representing the top bottom left corner of an element, used for
    * configuring the context element alignment
    * @property YAHOO.widget.Overlay.BOTTOM_LEFT
    * @static
    * @final
    * @type String
    */
    Overlay.BOTTOM_LEFT = "bl";

    /**
    * Constant representing the bottom right corner of an element, used for
    * configuring the context element alignment
    * @property YAHOO.widget.Overlay.BOTTOM_RIGHT
    * @static
    * @final
    * @type String
    */
    Overlay.BOTTOM_RIGHT = "br";

    Overlay.PREVENT_OVERLAP_X = {
        "tltr": true,
        "blbr": true,
        "brbl": true,
        "trtl": true
    };

    Overlay.PREVENT_OVERLAP_Y = {
        "trbr": true,
        "tlbl": true,
        "bltl": true,
        "brtr": true
    };

    /**
    * Constant representing the default CSS class used for an Overlay
    * @property YAHOO.widget.Overlay.CSS_OVERLAY
    * @static
    * @final
    * @type String
    */
    Overlay.CSS_OVERLAY = "yui-overlay";

    /**
    * Constant representing the default hidden CSS class used for an Overlay. This class is
    * applied to the overlay's outer DIV whenever it's hidden.
    *
    * @property YAHOO.widget.Overlay.CSS_HIDDEN
    * @static
    * @final
    * @type String
    */
    Overlay.CSS_HIDDEN = "yui-overlay-hidden";

    /**
    * Constant representing the default CSS class used for an Overlay iframe shim.
    *
    * @property YAHOO.widget.Overlay.CSS_IFRAME
    * @static
    * @final
    * @type String
    */
    Overlay.CSS_IFRAME = "yui-overlay-iframe";

    /**
     * Constant representing the names of the standard module elements
     * used in the overlay.
     * @property YAHOO.widget.Overlay.STD_MOD_RE
     * @static
     * @final
     * @type RegExp
     */
    Overlay.STD_MOD_RE = /^\s*?(body|footer|header)\s*?$/i;

    /**
    * A singleton CustomEvent used for reacting to the DOM event for
    * window scroll
    * @event YAHOO.widget.Overlay.windowScrollEvent
    */
    Overlay.windowScrollEvent = new CustomEvent("windowScroll");

    /**
    * A singleton CustomEvent used for reacting to the DOM event for
    * window resize
    * @event YAHOO.widget.Overlay.windowResizeEvent
    */
    Overlay.windowResizeEvent = new CustomEvent("windowResize");

    /**
    * The DOM event handler used to fire the CustomEvent for window scroll
    * @method YAHOO.widget.Overlay.windowScrollHandler
    * @static
    * @param {DOMEvent} e The DOM scroll event
    */
    Overlay.windowScrollHandler = function (e) {
        var t = Event.getTarget(e);

        // - Webkit (Safari 2/3) and Opera 9.2x bubble scroll events from elements to window
        // - FF2/3 and IE6/7, Opera 9.5x don't bubble scroll events from elements to window
        // - IE doesn't recognize scroll registered on the document.
        //
        // Also, when document view is scrolled, IE doesn't provide a target,
        // rest of the browsers set target to window.document, apart from opera
        // which sets target to window.
        if (!t || t === window || t === window.document) {
            if (UA.ie) {

                if (! window.scrollEnd) {
                    window.scrollEnd = -1;
                }

                clearTimeout(window.scrollEnd);

                window.scrollEnd = setTimeout(function () {
                    Overlay.windowScrollEvent.fire();
                }, 1);

            } else {
                Overlay.windowScrollEvent.fire();
            }
        }
    };

    /**
    * The DOM event handler used to fire the CustomEvent for window resize
    * @method YAHOO.widget.Overlay.windowResizeHandler
    * @static
    * @param {DOMEvent} e The DOM resize event
    */
    Overlay.windowResizeHandler = function (e) {

        if (UA.ie) {
            if (! window.resizeEnd) {
                window.resizeEnd = -1;
            }

            clearTimeout(window.resizeEnd);

            window.resizeEnd = setTimeout(function () {
                Overlay.windowResizeEvent.fire();
            }, 100);
        } else {
            Overlay.windowResizeEvent.fire();
        }
    };

    /**
    * A boolean that indicated whether the window resize and scroll events have
    * already been subscribed to.
    * @property YAHOO.widget.Overlay._initialized
    * @private
    * @type Boolean
    */
    Overlay._initialized = null;

    if (Overlay._initialized === null) {
        Event.on(window, "scroll", Overlay.windowScrollHandler);
        Event.on(window, "resize", Overlay.windowResizeHandler);
        Overlay._initialized = true;
    }

    /**
     * Internal map of special event types, which are provided
     * by the instance. It maps the event type to the custom event
     * instance. Contains entries for the "windowScroll", "windowResize" and
     * "textResize" static container events.
     *
     * @property YAHOO.widget.Overlay._TRIGGER_MAP
     * @type Object
     * @static
     * @private
     */
    Overlay._TRIGGER_MAP = {
        "windowScroll" : Overlay.windowScrollEvent,
        "windowResize" : Overlay.windowResizeEvent,
        "textResize"   : Module.textResizeEvent
    };

    YAHOO.extend(Overlay, Module, {

        /**
         * <p>
         * Array of default event types which will trigger
         * context alignment for the Overlay class.
         * </p>
         * <p>The array is empty by default for Overlay,
         * but maybe populated in future releases, so classes extending
         * Overlay which need to define their own set of CONTEXT_TRIGGERS
         * should concatenate their super class's prototype.CONTEXT_TRIGGERS
         * value with their own array of values.
         * </p>
         * <p>
         * E.g.:
         * <code>CustomOverlay.prototype.CONTEXT_TRIGGERS = YAHOO.widget.Overlay.prototype.CONTEXT_TRIGGERS.concat(["windowScroll"]);</code>
         * </p>
         *
         * @property CONTEXT_TRIGGERS
         * @type Array
         * @final
         */
        CONTEXT_TRIGGERS : [],

        /**
        * The Overlay initialization method, which is executed for Overlay and
        * all of its subclasses. This method is automatically called by the
        * constructor, and  sets up all DOM references for pre-existing markup,
        * and creates required markup if it is not already present.
        * @method init
        * @param {String} el The element ID representing the Overlay <em>OR</em>
        * @param {HTMLElement} el The element representing the Overlay
        * @param {Object} userConfig The configuration object literal
        * containing the configuration that should be set for this Overlay.
        * See configuration documentation for more details.
        */
        init: function (el, userConfig) {

            /*
                 Note that we don't pass the user config in here yet because we
                 only want it executed once, at the lowest subclass level
            */

            Overlay.superclass.init.call(this, el/*, userConfig*/);

            this.beforeInitEvent.fire(Overlay);

            Dom.addClass(this.element, Overlay.CSS_OVERLAY);

            if (userConfig) {
                this.cfg.applyConfig(userConfig, true);
            }

            if (this.platform == "mac" && UA.gecko) {

                if (! Config.alreadySubscribed(this.showEvent,
                    this.showMacGeckoScrollbars, this)) {

                    this.showEvent.subscribe(this.showMacGeckoScrollbars,
                        this, true);

                }

                if (! Config.alreadySubscribed(this.hideEvent,
                    this.hideMacGeckoScrollbars, this)) {

                    this.hideEvent.subscribe(this.hideMacGeckoScrollbars,
                        this, true);

                }
            }

            this.initEvent.fire(Overlay);
        },

        /**
        * Initializes the custom events for Overlay which are fired
        * automatically at appropriate times by the Overlay class.
        * @method initEvents
        */
        initEvents: function () {

            Overlay.superclass.initEvents.call(this);

            var SIGNATURE = CustomEvent.LIST;

            /**
            * CustomEvent fired before the Overlay is moved.
            * @event beforeMoveEvent
            * @param {Number} x x coordinate
            * @param {Number} y y coordinate
            */
            this.beforeMoveEvent = this.createEvent(EVENT_TYPES.BEFORE_MOVE);
            this.beforeMoveEvent.signature = SIGNATURE;

            /**
            * CustomEvent fired after the Overlay is moved.
            * @event moveEvent
            * @param {Number} x x coordinate
            * @param {Number} y y coordinate
            */
            this.moveEvent = this.createEvent(EVENT_TYPES.MOVE);
            this.moveEvent.signature = SIGNATURE;

        },

        /**
        * Initializes the class's configurable properties which can be changed
        * using the Overlay's Config object (cfg).
        * @method initDefaultConfig
        */
        initDefaultConfig: function () {

            Overlay.superclass.initDefaultConfig.call(this);

            var cfg = this.cfg;

            // Add overlay config properties //

            /**
            * The absolute x-coordinate position of the Overlay
            * @config x
            * @type Number
            * @default null
            */
            cfg.addProperty(DEFAULT_CONFIG.X.key, {

                handler: this.configX,
                validator: DEFAULT_CONFIG.X.validator,
                suppressEvent: DEFAULT_CONFIG.X.suppressEvent,
                supercedes: DEFAULT_CONFIG.X.supercedes

            });

            /**
            * The absolute y-coordinate position of the Overlay
            * @config y
            * @type Number
            * @default null
            */
            cfg.addProperty(DEFAULT_CONFIG.Y.key, {

                handler: this.configY,
                validator: DEFAULT_CONFIG.Y.validator,
                suppressEvent: DEFAULT_CONFIG.Y.suppressEvent,
                supercedes: DEFAULT_CONFIG.Y.supercedes

            });

            /**
            * An array with the absolute x and y positions of the Overlay
            * @config xy
            * @type Number[]
            * @default null
            */
            cfg.addProperty(DEFAULT_CONFIG.XY.key, {
                handler: this.configXY,
                suppressEvent: DEFAULT_CONFIG.XY.suppressEvent,
                supercedes: DEFAULT_CONFIG.XY.supercedes
            });

            /**
            * <p>
            * The array of context arguments for context-sensitive positioning.
            * </p>
            *
            * <p>
            * The format of the array is: <code>[contextElementOrId, overlayCorner, contextCorner, arrayOfTriggerEvents (optional), xyOffset (optional)]</code>, the
            * the 5 array elements described in detail below:
            * </p>
            *
            * <dl>
            * <dt>contextElementOrId &#60;String|HTMLElement&#62;</dt>
            * <dd>A reference to the context element to which the overlay should be aligned (or it's id).</dd>
            * <dt>overlayCorner &#60;String&#62;</dt>
            * <dd>The corner of the overlay which is to be used for alignment. This corner will be aligned to the
            * corner of the context element defined by the "contextCorner" entry which follows. Supported string values are:
            * "tr" (top right), "tl" (top left), "br" (bottom right), or "bl" (bottom left).</dd>
            * <dt>contextCorner &#60;String&#62;</dt>
            * <dd>The corner of the context element which is to be used for alignment. Supported string values are the same ones listed for the "overlayCorner" entry above.</dd>
            * <dt>arrayOfTriggerEvents (optional) &#60;Array[String|CustomEvent]&#62;</dt>
            * <dd>
            * <p>
            * By default, context alignment is a one time operation, aligning the Overlay to the context element when context configuration property is set, or when the <a href="#method_align">align</a>
            * method is invoked. However, you can use the optional "arrayOfTriggerEvents" entry to define the list of events which should force the overlay to re-align itself with the context element.
            * This is useful in situations where the layout of the document may change, resulting in the context element's position being modified.
            * </p>
            * <p>
            * The array can contain either event type strings for events the instance publishes (e.g. "beforeShow") or CustomEvent instances. Additionally the following
            * 3 static container event types are also currently supported : <code>"windowResize", "windowScroll", "textResize"</code> (defined in <a href="#property__TRIGGER_MAP">_TRIGGER_MAP</a> private property).
            * </p>
            * </dd>
            * <dt>xyOffset &#60;Number[]&#62;</dt>
            * <dd>
            * A 2 element Array specifying the X and Y pixel amounts by which the Overlay should be offset from the aligned corner. e.g. [5,0] offsets the Overlay 5 pixels to the left, <em>after</em> aligning the given context corners.
            * NOTE: If using this property and no triggers need to be defined, the arrayOfTriggerEvents property should be set to null to maintain correct array positions for the arguments.
            * </dd>
            * </dl>
            *
            * <p>
            * For example, setting this property to <code>["img1", "tl", "bl"]</code> will
            * align the Overlay's top left corner to the bottom left corner of the
            * context element with id "img1".
            * </p>
            * <p>
            * Setting this property to <code>["img1", "tl", "bl", null, [0,5]</code> will
            * align the Overlay's top left corner to the bottom left corner of the
            * context element with id "img1", and then offset it by 5 pixels on the Y axis (providing a 5 pixel gap between the bottom of the context element and top of the overlay).
            * </p>
            * <p>
            * Adding the optional trigger values: <code>["img1", "tl", "bl", ["beforeShow", "windowResize"], [0,5]]</code>,
            * will re-align the overlay position, whenever the "beforeShow" or "windowResize" events are fired.
            * </p>
            *
            * @config context
            * @type Array
            * @default null
            */
            cfg.addProperty(DEFAULT_CONFIG.CONTEXT.key, {
                handler: this.configContext,
                suppressEvent: DEFAULT_CONFIG.CONTEXT.suppressEvent,
                supercedes: DEFAULT_CONFIG.CONTEXT.supercedes
            });

            /**
            * Determines whether or not the Overlay should be anchored
            * to the center of the viewport.
            *
            * <p>This property can be set to:</p>
            *
            * <dl>
            * <dt>true</dt>
            * <dd>
            * To enable fixed center positioning
            * <p>
            * When enabled, the overlay will
            * be positioned in the center of viewport when initially displayed, and
            * will remain in the center of the viewport whenever the window is
            * scrolled or resized.
            * </p>
            * <p>
            * If the overlay is too big for the viewport,
            * it's top left corner will be aligned with the top left corner of the viewport.
            * </p>
            * </dd>
            * <dt>false</dt>
            * <dd>
            * To disable fixed center positioning.
            * <p>In this case the overlay can still be
            * centered as a one-off operation, by invoking the <code>center()</code> method,
            * however it will not remain centered when the window is scrolled/resized.
            * </dd>
            * <dt>"contained"<dt>
            * <dd>To enable fixed center positioning, as with the <code>true</code> option.
            * <p>However, unlike setting the property to <code>true</code>,
            * when the property is set to <code>"contained"</code>, if the overlay is
            * too big for the viewport, it will not get automatically centered when the
            * user scrolls or resizes the window (until the window is large enough to contain the
            * overlay). This is useful in cases where the Overlay has both header and footer
            * UI controls which the user may need to access.
            * </p>
            * </dd>
            * </dl>
            *
            * @config fixedcenter
            * @type Boolean | String
            * @default false
            */
            cfg.addProperty(DEFAULT_CONFIG.FIXED_CENTER.key, {
                handler: this.configFixedCenter,
                value: DEFAULT_CONFIG.FIXED_CENTER.value,
                validator: DEFAULT_CONFIG.FIXED_CENTER.validator,
                supercedes: DEFAULT_CONFIG.FIXED_CENTER.supercedes
            });

            /**
            * CSS width of the Overlay.
            * @config width
            * @type String
            * @default null
            */
            cfg.addProperty(DEFAULT_CONFIG.WIDTH.key, {
                handler: this.configWidth,
                suppressEvent: DEFAULT_CONFIG.WIDTH.suppressEvent,
                supercedes: DEFAULT_CONFIG.WIDTH.supercedes
            });

            /**
            * CSS height of the Overlay.
            * @config height
            * @type String
            * @default null
            */
            cfg.addProperty(DEFAULT_CONFIG.HEIGHT.key, {
                handler: this.configHeight,
                suppressEvent: DEFAULT_CONFIG.HEIGHT.suppressEvent,
                supercedes: DEFAULT_CONFIG.HEIGHT.supercedes
            });

            /**
            * Standard module element which should auto fill out the height of the Overlay if the height config property is set.
            * Supported values are "header", "body", "footer".
            *
            * @config autofillheight
            * @type String
            * @default null
            */
            cfg.addProperty(DEFAULT_CONFIG.AUTO_FILL_HEIGHT.key, {
                handler: this.configAutoFillHeight,
                value : DEFAULT_CONFIG.AUTO_FILL_HEIGHT.value,
                validator : this._validateAutoFill,
                supercedes: DEFAULT_CONFIG.AUTO_FILL_HEIGHT.supercedes
            });

            /**
            * CSS z-index of the Overlay.
            * @config zIndex
            * @type Number
            * @default null
            */
            cfg.addProperty(DEFAULT_CONFIG.ZINDEX.key, {
                handler: this.configzIndex,
                value: DEFAULT_CONFIG.ZINDEX.value
            });

            /**
            * True if the Overlay should be prevented from being positioned
            * out of the viewport.
            * @config constraintoviewport
            * @type Boolean
            * @default false
            */
            cfg.addProperty(DEFAULT_CONFIG.CONSTRAIN_TO_VIEWPORT.key, {

                handler: this.configConstrainToViewport,
                value: DEFAULT_CONFIG.CONSTRAIN_TO_VIEWPORT.value,
                validator: DEFAULT_CONFIG.CONSTRAIN_TO_VIEWPORT.validator,
                supercedes: DEFAULT_CONFIG.CONSTRAIN_TO_VIEWPORT.supercedes

            });

            /**
            * @config iframe
            * @description Boolean indicating whether or not the Overlay should
            * have an IFRAME shim; used to prevent SELECT elements from
            * poking through an Overlay instance in IE6.  When set to "true",
            * the iframe shim is created when the Overlay instance is intially
            * made visible.
            * @type Boolean
            * @default true for IE6 and below, false for all other browsers.
            */
            cfg.addProperty(DEFAULT_CONFIG.IFRAME.key, {

                handler: this.configIframe,
                value: DEFAULT_CONFIG.IFRAME.value,
                validator: DEFAULT_CONFIG.IFRAME.validator,
                supercedes: DEFAULT_CONFIG.IFRAME.supercedes

            });

            /**
            * @config preventcontextoverlap
            * @description Boolean indicating whether or not the Overlay should overlap its
            * context element (defined using the "context" configuration property) when the
            * "constraintoviewport" configuration property is set to "true".
            * @type Boolean
            * @default false
            */
            cfg.addProperty(DEFAULT_CONFIG.PREVENT_CONTEXT_OVERLAP.key, {
                value: DEFAULT_CONFIG.PREVENT_CONTEXT_OVERLAP.value,
                validator: DEFAULT_CONFIG.PREVENT_CONTEXT_OVERLAP.validator,
                supercedes: DEFAULT_CONFIG.PREVENT_CONTEXT_OVERLAP.supercedes
            });
        },

        /**
        * Moves the Overlay to the specified position. This function is
        * identical to calling this.cfg.setProperty("xy", [x,y]);
        * @method moveTo
        * @param {Number} x The Overlay's new x position
        * @param {Number} y The Overlay's new y position
        */
        moveTo: function (x, y) {
            this.cfg.setProperty("xy", [x, y]);
        },

        /**
        * Adds a CSS class ("hide-scrollbars") and removes a CSS class
        * ("show-scrollbars") to the Overlay to fix a bug in Gecko on Mac OS X
        * (https://bugzilla.mozilla.org/show_bug.cgi?id=187435)
        * @method hideMacGeckoScrollbars
        */
        hideMacGeckoScrollbars: function () {
            Dom.replaceClass(this.element, "show-scrollbars", "hide-scrollbars");
        },

        /**
        * Adds a CSS class ("show-scrollbars") and removes a CSS class
        * ("hide-scrollbars") to the Overlay to fix a bug in Gecko on Mac OS X
        * (https://bugzilla.mozilla.org/show_bug.cgi?id=187435)
        * @method showMacGeckoScrollbars
        */
        showMacGeckoScrollbars: function () {
            Dom.replaceClass(this.element, "hide-scrollbars", "show-scrollbars");
        },

        /**
         * Internal implementation to set the visibility of the overlay in the DOM.
         *
         * @method _setDomVisibility
         * @param {boolean} visible Whether to show or hide the Overlay's outer element
         * @protected
         */
        _setDomVisibility : function(show) {
            Dom.setStyle(this.element, "visibility", (show) ? "visible" : "hidden");
            var hiddenClass = Overlay.CSS_HIDDEN;

            if (show) {
                Dom.removeClass(this.element, hiddenClass);
            } else {
                Dom.addClass(this.element, hiddenClass);
            }
        },

        // BEGIN BUILT-IN PROPERTY EVENT HANDLERS //
        /**
        * The default event handler fired when the "visible" property is
        * changed.  This method is responsible for firing showEvent
        * and hideEvent.
        * @method configVisible
        * @param {String} type The CustomEvent type (usually the property name)
        * @param {Object[]} args The CustomEvent arguments. For configuration
        * handlers, args[0] will equal the newly applied value for the property.
        * @param {Object} obj The scope object. For configuration handlers,
        * this will usually equal the owner.
        */
        configVisible: function (type, args, obj) {

            var visible = args[0],
                currentVis = Dom.getStyle(this.element, "visibility"),
                effect = this.cfg.getProperty("effect"),
                effectInstances = [],
                isMacGecko = (this.platform == "mac" && UA.gecko),
                alreadySubscribed = Config.alreadySubscribed,
                eff, ei, e, i, j, k, h,
                nEffects,
                nEffectInstances;

            if (currentVis == "inherit") {
                e = this.element.parentNode;

                while (e.nodeType != 9 && e.nodeType != 11) {
                    currentVis = Dom.getStyle(e, "visibility");

                    if (currentVis != "inherit") {
                        break;
                    }

                    e = e.parentNode;
                }

                if (currentVis == "inherit") {
                    currentVis = "visible";
                }
            }

            if (effect) {
                if (effect instanceof Array) {
                    nEffects = effect.length;

                    for (i = 0; i < nEffects; i++) {
                        eff = effect[i];
                        effectInstances[effectInstances.length] =
                            eff.effect(this, eff.duration);

                    }
                } else {
                    effectInstances[effectInstances.length] =
                        effect.effect(this, effect.duration);
                }
            }

            if (visible) { // Show
                if (isMacGecko) {
                    this.showMacGeckoScrollbars();
                }

                if (effect) { // Animate in
                    if (visible) { // Animate in if not showing
                        if (currentVis != "visible" || currentVis === "") {
                            this.beforeShowEvent.fire();
                            nEffectInstances = effectInstances.length;

                            for (j = 0; j < nEffectInstances; j++) {
                                ei = effectInstances[j];
                                if (j === 0 && !alreadySubscribed(
                                        ei.animateInCompleteEvent,
                                        this.showEvent.fire, this.showEvent)) {

                                    /*
                                         Delegate showEvent until end
                                         of animateInComplete
                                    */

                                    ei.animateInCompleteEvent.subscribe(
                                     this.showEvent.fire, this.showEvent, true);
                                }
                                ei.animateIn();
                            }
                        }
                    }
                } else { // Show
                    if (currentVis != "visible" || currentVis === "") {
                        this.beforeShowEvent.fire();

                        this._setDomVisibility(true);

                        this.cfg.refireEvent("iframe");
                        this.showEvent.fire();
                    } else {
                        this._setDomVisibility(true);
                    }
                }
            } else { // Hide

                if (isMacGecko) {
                    this.hideMacGeckoScrollbars();
                }

                if (effect) { // Animate out if showing
                    if (currentVis == "visible") {
                        this.beforeHideEvent.fire();

                        nEffectInstances = effectInstances.length;
                        for (k = 0; k < nEffectInstances; k++) {
                            h = effectInstances[k];

                            if (k === 0 && !alreadySubscribed(
                                h.animateOutCompleteEvent, this.hideEvent.fire,
                                this.hideEvent)) {

                                /*
                                     Delegate hideEvent until end
                                     of animateOutComplete
                                */

                                h.animateOutCompleteEvent.subscribe(
                                    this.hideEvent.fire, this.hideEvent, true);

                            }
                            h.animateOut();
                        }

                    } else if (currentVis === "") {
                        this._setDomVisibility(false);
                    }

                } else { // Simple hide

                    if (currentVis == "visible" || currentVis === "") {
                        this.beforeHideEvent.fire();
                        this._setDomVisibility(false);
                        this.hideEvent.fire();
                    } else {
                        this._setDomVisibility(false);
                    }
                }
            }
        },

        /**
        * Fixed center event handler used for centering on scroll/resize, but only if
        * the overlay is visible and, if "fixedcenter" is set to "contained", only if
        * the overlay fits within the viewport.
        *
        * @method doCenterOnDOMEvent
        */
        doCenterOnDOMEvent: function () {
            var cfg = this.cfg,
                fc = cfg.getProperty("fixedcenter");

            if (cfg.getProperty("visible")) {
                if (fc && (fc !== _CONTAINED || this.fitsInViewport())) {
                    this.center();
                }
            }
        },

        /**
         * Determines if the Overlay (including the offset value defined by Overlay.VIEWPORT_OFFSET)
         * will fit entirely inside the viewport, in both dimensions - width and height.
         *
         * @method fitsInViewport
         * @return boolean true if the Overlay will fit, false if not
         */
        fitsInViewport : function() {
            var nViewportOffset = Overlay.VIEWPORT_OFFSET,
                element = this.element,
                elementWidth = element.offsetWidth,
                elementHeight = element.offsetHeight,
                viewportWidth = Dom.getViewportWidth(),
                viewportHeight = Dom.getViewportHeight();

            return ((elementWidth + nViewportOffset < viewportWidth) && (elementHeight + nViewportOffset < viewportHeight));
        },

        /**
        * The default event handler fired when the "fixedcenter" property
        * is changed.
        * @method configFixedCenter
        * @param {String} type The CustomEvent type (usually the property name)
        * @param {Object[]} args The CustomEvent arguments. For configuration
        * handlers, args[0] will equal the newly applied value for the property.
        * @param {Object} obj The scope object. For configuration handlers,
        * this will usually equal the owner.
        */
        configFixedCenter: function (type, args, obj) {

            var val = args[0],
                alreadySubscribed = Config.alreadySubscribed,
                windowResizeEvent = Overlay.windowResizeEvent,
                windowScrollEvent = Overlay.windowScrollEvent;

            if (val) {
                this.center();

                if (!alreadySubscribed(this.beforeShowEvent, this.center)) {
                    this.beforeShowEvent.subscribe(this.center);
                }

                if (!alreadySubscribed(windowResizeEvent, this.doCenterOnDOMEvent, this)) {
                    windowResizeEvent.subscribe(this.doCenterOnDOMEvent, this, true);
                }

                if (!alreadySubscribed(windowScrollEvent, this.doCenterOnDOMEvent, this)) {
                    windowScrollEvent.subscribe(this.doCenterOnDOMEvent, this, true);
                }

            } else {
                this.beforeShowEvent.unsubscribe(this.center);

                windowResizeEvent.unsubscribe(this.doCenterOnDOMEvent, this);
                windowScrollEvent.unsubscribe(this.doCenterOnDOMEvent, this);
            }
        },

        /**
        * The default event handler fired when the "height" property is changed.
        * @method configHeight
        * @param {String} type The CustomEvent type (usually the property name)
        * @param {Object[]} args The CustomEvent arguments. For configuration
        * handlers, args[0] will equal the newly applied value for the property.
        * @param {Object} obj The scope object. For configuration handlers,
        * this will usually equal the owner.
        */
        configHeight: function (type, args, obj) {

            var height = args[0],
                el = this.element;

            Dom.setStyle(el, "height", height);
            this.cfg.refireEvent("iframe");
        },

        /**
         * The default event handler fired when the "autofillheight" property is changed.
         * @method configAutoFillHeight
         *
         * @param {String} type The CustomEvent type (usually the property name)
         * @param {Object[]} args The CustomEvent arguments. For configuration
         * handlers, args[0] will equal the newly applied value for the property.
         * @param {Object} obj The scope object. For configuration handlers,
         * this will usually equal the owner.
         */
        configAutoFillHeight: function (type, args, obj) {
            var fillEl = args[0],
                cfg = this.cfg,
                autoFillHeight = "autofillheight",
                height = "height",
                currEl = cfg.getProperty(autoFillHeight),
                autoFill = this._autoFillOnHeightChange;

            cfg.unsubscribeFromConfigEvent(height, autoFill);
            Module.textResizeEvent.unsubscribe(autoFill);
            this.changeContentEvent.unsubscribe(autoFill);

            if (currEl && fillEl !== currEl && this[currEl]) {
                Dom.setStyle(this[currEl], height, "");
            }

            if (fillEl) {
                fillEl = Lang.trim(fillEl.toLowerCase());

                cfg.subscribeToConfigEvent(height, autoFill, this[fillEl], this);
                Module.textResizeEvent.subscribe(autoFill, this[fillEl], this);
                this.changeContentEvent.subscribe(autoFill, this[fillEl], this);

                cfg.setProperty(autoFillHeight, fillEl, true);
            }
        },

        /**
        * The default event handler fired when the "width" property is changed.
        * @method configWidth
        * @param {String} type The CustomEvent type (usually the property name)
        * @param {Object[]} args The CustomEvent arguments. For configuration
        * handlers, args[0] will equal the newly applied value for the property.
        * @param {Object} obj The scope object. For configuration handlers,
        * this will usually equal the owner.
        */
        configWidth: function (type, args, obj) {

            var width = args[0],
                el = this.element;

            Dom.setStyle(el, "width", width);
            this.cfg.refireEvent("iframe");
        },

        /**
        * The default event handler fired when the "zIndex" property is changed.
        * @method configzIndex
        * @param {String} type The CustomEvent type (usually the property name)
        * @param {Object[]} args The CustomEvent arguments. For configuration
        * handlers, args[0] will equal the newly applied value for the property.
        * @param {Object} obj The scope object. For configuration handlers,
        * this will usually equal the owner.
        */
        configzIndex: function (type, args, obj) {

            var zIndex = args[0],
                el = this.element;

            if (! zIndex) {
                zIndex = Dom.getStyle(el, "zIndex");
                if (! zIndex || isNaN(zIndex)) {
                    zIndex = 0;
                }
            }

            if (this.iframe || this.cfg.getProperty("iframe") === true) {
                if (zIndex <= 0) {
                    zIndex = 1;
                }
            }

            Dom.setStyle(el, "zIndex", zIndex);
            this.cfg.setProperty("zIndex", zIndex, true);

            if (this.iframe) {
                this.stackIframe();
            }
        },

        /**
        * The default event handler fired when the "xy" property is changed.
        * @method configXY
        * @param {String} type The CustomEvent type (usually the property name)
        * @param {Object[]} args The CustomEvent arguments. For configuration
        * handlers, args[0] will equal the newly applied value for the property.
        * @param {Object} obj The scope object. For configuration handlers,
        * this will usually equal the owner.
        */
        configXY: function (type, args, obj) {

            var pos = args[0],
                x = pos[0],
                y = pos[1];

            this.cfg.setProperty("x", x);
            this.cfg.setProperty("y", y);

            this.beforeMoveEvent.fire([x, y]);

            x = this.cfg.getProperty("x");
            y = this.cfg.getProperty("y");


            this.cfg.refireEvent("iframe");
            this.moveEvent.fire([x, y]);
        },

        /**
        * The default event handler fired when the "x" property is changed.
        * @method configX
        * @param {String} type The CustomEvent type (usually the property name)
        * @param {Object[]} args The CustomEvent arguments. For configuration
        * handlers, args[0] will equal the newly applied value for the property.
        * @param {Object} obj The scope object. For configuration handlers,
        * this will usually equal the owner.
        */
        configX: function (type, args, obj) {

            var x = args[0],
                y = this.cfg.getProperty("y");

            this.cfg.setProperty("x", x, true);
            this.cfg.setProperty("y", y, true);

            this.beforeMoveEvent.fire([x, y]);

            x = this.cfg.getProperty("x");
            y = this.cfg.getProperty("y");

            Dom.setX(this.element, x, true);

            this.cfg.setProperty("xy", [x, y], true);

            this.cfg.refireEvent("iframe");
            this.moveEvent.fire([x, y]);
        },

        /**
        * The default event handler fired when the "y" property is changed.
        * @method configY
        * @param {String} type The CustomEvent type (usually the property name)
        * @param {Object[]} args The CustomEvent arguments. For configuration
        * handlers, args[0] will equal the newly applied value for the property.
        * @param {Object} obj The scope object. For configuration handlers,
        * this will usually equal the owner.
        */
        configY: function (type, args, obj) {

            var x = this.cfg.getProperty("x"),
                y = args[0];

            this.cfg.setProperty("x", x, true);
            this.cfg.setProperty("y", y, true);

            this.beforeMoveEvent.fire([x, y]);

            x = this.cfg.getProperty("x");
            y = this.cfg.getProperty("y");

            Dom.setY(this.element, y, true);

            this.cfg.setProperty("xy", [x, y], true);

            this.cfg.refireEvent("iframe");
            this.moveEvent.fire([x, y]);
        },

        /**
        * Shows the iframe shim, if it has been enabled.
        * @method showIframe
        */
        showIframe: function () {

            var oIFrame = this.iframe,
                oParentNode;

            if (oIFrame) {
                oParentNode = this.element.parentNode;

                if (oParentNode != oIFrame.parentNode) {
                    this._addToParent(oParentNode, oIFrame);
                }
                oIFrame.style.display = "block";
            }
        },

        /**
        * Hides the iframe shim, if it has been enabled.
        * @method hideIframe
        */
        hideIframe: function () {
            if (this.iframe) {
                this.iframe.style.display = "none";
            }
        },

        /**
        * Syncronizes the size and position of iframe shim to that of its
        * corresponding Overlay instance.
        * @method syncIframe
        */
        syncIframe: function () {

            var oIFrame = this.iframe,
                oElement = this.element,
                nOffset = Overlay.IFRAME_OFFSET,
                nDimensionOffset = (nOffset * 2),
                aXY;

            if (oIFrame) {
                // Size <iframe>
                oIFrame.style.width = (oElement.offsetWidth + nDimensionOffset + "px");
                oIFrame.style.height = (oElement.offsetHeight + nDimensionOffset + "px");

                // Position <iframe>
                aXY = this.cfg.getProperty("xy");

                if (!Lang.isArray(aXY) || (isNaN(aXY[0]) || isNaN(aXY[1]))) {
                    this.syncPosition();
                    aXY = this.cfg.getProperty("xy");
                }
                Dom.setXY(oIFrame, [(aXY[0] - nOffset), (aXY[1] - nOffset)]);
            }
        },

        /**
         * Sets the zindex of the iframe shim, if it exists, based on the zindex of
         * the Overlay element. The zindex of the iframe is set to be one less
         * than the Overlay element's zindex.
         *
         * <p>NOTE: This method will not bump up the zindex of the Overlay element
         * to ensure that the iframe shim has a non-negative zindex.
         * If you require the iframe zindex to be 0 or higher, the zindex of
         * the Overlay element should be set to a value greater than 0, before
         * this method is called.
         * </p>
         * @method stackIframe
         */
        stackIframe: function () {
            if (this.iframe) {
                var overlayZ = Dom.getStyle(this.element, "zIndex");
                if (!YAHOO.lang.isUndefined(overlayZ) && !isNaN(overlayZ)) {
                    Dom.setStyle(this.iframe, "zIndex", (overlayZ - 1));
                }
            }
        },

        /**
        * The default event handler fired when the "iframe" property is changed.
        * @method configIframe
        * @param {String} type The CustomEvent type (usually the property name)
        * @param {Object[]} args The CustomEvent arguments. For configuration
        * handlers, args[0] will equal the newly applied value for the property.
        * @param {Object} obj The scope object. For configuration handlers,
        * this will usually equal the owner.
        */
        configIframe: function (type, args, obj) {

            var bIFrame = args[0];

            function createIFrame() {

                var oIFrame = this.iframe,
                    oElement = this.element,
                    oParent;

                if (!oIFrame) {
                    if (!m_oIFrameTemplate) {
                        m_oIFrameTemplate = document.createElement("iframe");

                        if (this.isSecure) {
                            m_oIFrameTemplate.src = Overlay.IFRAME_SRC;
                        }

                        /*
                            Set the opacity of the <iframe> to 0 so that it
                            doesn't modify the opacity of any transparent
                            elements that may be on top of it (like a shadow).
                        */
                        if (UA.ie) {
                            m_oIFrameTemplate.style.filter = "alpha(opacity=0)";
                            /*
                                 Need to set the "frameBorder" property to 0
                                 supress the default <iframe> border in IE.
                                 Setting the CSS "border" property alone
                                 doesn't supress it.
                            */
                            m_oIFrameTemplate.frameBorder = 0;
                        }
                        else {
                            m_oIFrameTemplate.style.opacity = "0";
                        }

                        m_oIFrameTemplate.style.position = "absolute";
                        m_oIFrameTemplate.style.border = "none";
                        m_oIFrameTemplate.style.margin = "0";
                        m_oIFrameTemplate.style.padding = "0";
                        m_oIFrameTemplate.style.display = "none";
                        m_oIFrameTemplate.tabIndex = -1;
                        m_oIFrameTemplate.className = Overlay.CSS_IFRAME;
                    }

                    oIFrame = m_oIFrameTemplate.cloneNode(false);
                    oIFrame.id = this.id + "_f";
                    oParent = oElement.parentNode;

                    var parentNode = oParent || document.body;

                    this._addToParent(parentNode, oIFrame);
                    this.iframe = oIFrame;
                }

                /*
                     Show the <iframe> before positioning it since the "setXY"
                     method of DOM requires the element be in the document
                     and visible.
                */
                this.showIframe();

                /*
                     Syncronize the size and position of the <iframe> to that
                     of the Overlay.
                */
                this.syncIframe();
                this.stackIframe();

                // Add event listeners to update the <iframe> when necessary
                if (!this._hasIframeEventListeners) {
                    this.showEvent.subscribe(this.showIframe);
                    this.hideEvent.subscribe(this.hideIframe);
                    this.changeContentEvent.subscribe(this.syncIframe);

                    this._hasIframeEventListeners = true;
                }
            }

            function onBeforeShow() {
                createIFrame.call(this);
                this.beforeShowEvent.unsubscribe(onBeforeShow);
                this._iframeDeferred = false;
            }

            if (bIFrame) { // <iframe> shim is enabled

                if (this.cfg.getProperty("visible")) {
                    createIFrame.call(this);
                } else {
                    if (!this._iframeDeferred) {
                        this.beforeShowEvent.subscribe(onBeforeShow);
                        this._iframeDeferred = true;
                    }
                }

            } else {    // <iframe> shim is disabled
                this.hideIframe();

                if (this._hasIframeEventListeners) {
                    this.showEvent.unsubscribe(this.showIframe);
                    this.hideEvent.unsubscribe(this.hideIframe);
                    this.changeContentEvent.unsubscribe(this.syncIframe);

                    this._hasIframeEventListeners = false;
                }
            }
        },

        /**
         * Set's the container's XY value from DOM if not already set.
         *
         * Differs from syncPosition, in that the XY value is only sync'd with DOM if
         * not already set. The method also refire's the XY config property event, so any
         * beforeMove, Move event listeners are invoked.
         *
         * @method _primeXYFromDOM
         * @protected
         */
        _primeXYFromDOM : function() {
            if (YAHOO.lang.isUndefined(this.cfg.getProperty("xy"))) {
                // Set CFG XY based on DOM XY
                this.syncPosition();
                // Account for XY being set silently in syncPosition (no moveTo fired/called)
                this.cfg.refireEvent("xy");
                this.beforeShowEvent.unsubscribe(this._primeXYFromDOM);
            }
        },

        /**
        * The default event handler fired when the "constraintoviewport"
        * property is changed.
        * @method configConstrainToViewport
        * @param {String} type The CustomEvent type (usually the property name)
        * @param {Object[]} args The CustomEvent arguments. For configuration
        * handlers, args[0] will equal the newly applied value for
        * the property.
        * @param {Object} obj The scope object. For configuration handlers,
        * this will usually equal the owner.
        */
        configConstrainToViewport: function (type, args, obj) {
            var val = args[0];

            if (val) {
                if (! Config.alreadySubscribed(this.beforeMoveEvent, this.enforceConstraints, this)) {
                    this.beforeMoveEvent.subscribe(this.enforceConstraints, this, true);
                }
                if (! Config.alreadySubscribed(this.beforeShowEvent, this._primeXYFromDOM)) {
                    this.beforeShowEvent.subscribe(this._primeXYFromDOM);
                }
            } else {
                this.beforeShowEvent.unsubscribe(this._primeXYFromDOM);
                this.beforeMoveEvent.unsubscribe(this.enforceConstraints, this);
            }
        },

         /**
        * The default event handler fired when the "context" property
        * is changed.
        *
        * @method configContext
        * @param {String} type The CustomEvent type (usually the property name)
        * @param {Object[]} args The CustomEvent arguments. For configuration
        * handlers, args[0] will equal the newly applied value for the property.
        * @param {Object} obj The scope object. For configuration handlers,
        * this will usually equal the owner.
        */
        configContext: function (type, args, obj) {

            var contextArgs = args[0],
                contextEl,
                elementMagnetCorner,
                contextMagnetCorner,
                triggers,
                offset,
                defTriggers = this.CONTEXT_TRIGGERS;

            if (contextArgs) {

                contextEl = contextArgs[0];
                elementMagnetCorner = contextArgs[1];
                contextMagnetCorner = contextArgs[2];
                triggers = contextArgs[3];
                offset = contextArgs[4];

                if (defTriggers && defTriggers.length > 0) {
                    triggers = (triggers || []).concat(defTriggers);
                }

                if (contextEl) {
                    if (typeof contextEl == "string") {
                        this.cfg.setProperty("context", [
                                document.getElementById(contextEl),
                                elementMagnetCorner,
                                contextMagnetCorner,
                                triggers,
                                offset],
                                true);
                    }

                    if (elementMagnetCorner && contextMagnetCorner) {
                        this.align(elementMagnetCorner, contextMagnetCorner, offset);
                    }

                    if (this._contextTriggers) {
                        // Unsubscribe Old Set
                        this._processTriggers(this._contextTriggers, _UNSUBSCRIBE, this._alignOnTrigger);
                    }

                    if (triggers) {
                        // Subscribe New Set
                        this._processTriggers(triggers, _SUBSCRIBE, this._alignOnTrigger);
                        this._contextTriggers = triggers;
                    }
                }
            }
        },

        /**
         * Custom Event handler for context alignment triggers. Invokes the align method
         *
         * @method _alignOnTrigger
         * @protected
         *
         * @param {String} type The event type (not used by the default implementation)
         * @param {Any[]} args The array of arguments for the trigger event (not used by the default implementation)
         */
        _alignOnTrigger: function(type, args) {
            this.align();
        },

        /**
         * Helper method to locate the custom event instance for the event name string
         * passed in. As a convenience measure, any custom events passed in are returned.
         *
         * @method _findTriggerCE
         * @private
         *
         * @param {String|CustomEvent} t Either a CustomEvent, or event type (e.g. "windowScroll") for which a
         * custom event instance needs to be looked up from the Overlay._TRIGGER_MAP.
         */
        _findTriggerCE : function(t) {
            var tce = null;
            if (t instanceof CustomEvent) {
                tce = t;
            } else if (Overlay._TRIGGER_MAP[t]) {
                tce = Overlay._TRIGGER_MAP[t];
            }
            return tce;
        },

        /**
         * Utility method that subscribes or unsubscribes the given
         * function from the list of trigger events provided.
         *
         * @method _processTriggers
         * @protected
         *
         * @param {Array[String|CustomEvent]} triggers An array of either CustomEvents, event type strings
         * (e.g. "beforeShow", "windowScroll") to/from which the provided function should be
         * subscribed/unsubscribed respectively.
         *
         * @param {String} mode Either "subscribe" or "unsubscribe", specifying whether or not
         * we are subscribing or unsubscribing trigger listeners
         *
         * @param {Function} fn The function to be subscribed/unsubscribed to/from the trigger event.
         * Context is always set to the overlay instance, and no additional object argument
         * get passed to the subscribed function.
         */
        _processTriggers : function(triggers, mode, fn) {
            var t, tce;

            for (var i = 0, l = triggers.length; i < l; ++i) {
                t = triggers[i];
                tce = this._findTriggerCE(t);
                if (tce) {
                    tce[mode](fn, this, true);
                } else {
                    this[mode](t, fn);
                }
            }
        },

        // END BUILT-IN PROPERTY EVENT HANDLERS //
        /**
        * Aligns the Overlay to its context element using the specified corner
        * points (represented by the constants TOP_LEFT, TOP_RIGHT, BOTTOM_LEFT,
        * and BOTTOM_RIGHT.
        * @method align
        * @param {String} elementAlign  The String representing the corner of
        * the Overlay that should be aligned to the context element
        * @param {String} contextAlign  The corner of the context element
        * that the elementAlign corner should stick to.
        * @param {Number[]} xyOffset Optional. A 2 element array specifying the x and y pixel offsets which should be applied
        * after aligning the element and context corners. For example, passing in [5, -10] for this value, would offset the
        * Overlay by 5 pixels along the X axis (horizontally) and -10 pixels along the Y axis (vertically) after aligning the specified corners.
        */
        align: function (elementAlign, contextAlign, xyOffset) {

            var contextArgs = this.cfg.getProperty("context"),
                me = this,
                context,
                element,
                contextRegion;

            function doAlign(v, h) {

                var alignX = null, alignY = null;

                switch (elementAlign) {

                    case Overlay.TOP_LEFT:
                        alignX = h;
                        alignY = v;
                        break;

                    case Overlay.TOP_RIGHT:
                        alignX = h - element.offsetWidth;
                        alignY = v;
                        break;

                    case Overlay.BOTTOM_LEFT:
                        alignX = h;
                        alignY = v - element.offsetHeight;
                        break;

                    case Overlay.BOTTOM_RIGHT:
                        alignX = h - element.offsetWidth;
                        alignY = v - element.offsetHeight;
                        break;
                }

                if (alignX !== null && alignY !== null) {
                    if (xyOffset) {
                        alignX += xyOffset[0];
                        alignY += xyOffset[1];
                    }
                    me.moveTo(alignX, alignY);
                }
            }

            if (contextArgs) {
                context = contextArgs[0];
                element = this.element;
                me = this;

                if (! elementAlign) {
                    elementAlign = contextArgs[1];
                }

                if (! contextAlign) {
                    contextAlign = contextArgs[2];
                }

                if (!xyOffset && contextArgs[4]) {
                    xyOffset = contextArgs[4];
                }

                if (element && context) {
                    contextRegion = Dom.getRegion(context);

                    switch (contextAlign) {

                        case Overlay.TOP_LEFT:
                            doAlign(contextRegion.top, contextRegion.left);
                            break;

                        case Overlay.TOP_RIGHT:
                            doAlign(contextRegion.top, contextRegion.right);
                            break;

                        case Overlay.BOTTOM_LEFT:
                            doAlign(contextRegion.bottom, contextRegion.left);
                            break;

                        case Overlay.BOTTOM_RIGHT:
                            doAlign(contextRegion.bottom, contextRegion.right);
                            break;
                    }
                }
            }
        },

        /**
        * The default event handler executed when the moveEvent is fired, if the
        * "constraintoviewport" is set to true.
        * @method enforceConstraints
        * @param {String} type The CustomEvent type (usually the property name)
        * @param {Object[]} args The CustomEvent arguments. For configuration
        * handlers, args[0] will equal the newly applied value for the property.
        * @param {Object} obj The scope object. For configuration handlers,
        * this will usually equal the owner.
        */
        enforceConstraints: function (type, args, obj) {
            var pos = args[0];

            var cXY = this.getConstrainedXY(pos[0], pos[1]);
            this.cfg.setProperty("x", cXY[0], true);
            this.cfg.setProperty("y", cXY[1], true);
            this.cfg.setProperty("xy", cXY, true);
        },

        /**
         * Shared implementation method for getConstrainedX and getConstrainedY.
         *
         * <p>
         * Given a coordinate value, returns the calculated coordinate required to
         * position the Overlay if it is to be constrained to the viewport, based on the
         * current element size, viewport dimensions, scroll values and preventoverlap
         * settings
         * </p>
         *
         * @method _getConstrainedPos
         * @protected
         * @param {String} pos The coordinate which needs to be constrained, either "x" or "y"
         * @param {Number} The coordinate value which needs to be constrained
         * @return {Number} The constrained coordinate value
         */
        _getConstrainedPos: function(pos, val) {

            var overlayEl = this.element,

                buffer = Overlay.VIEWPORT_OFFSET,

                x = (pos == "x"),

                overlaySize      = (x) ? overlayEl.offsetWidth : overlayEl.offsetHeight,
                viewportSize     = (x) ? Dom.getViewportWidth() : Dom.getViewportHeight(),
                docScroll        = (x) ? Dom.getDocumentScrollLeft() : Dom.getDocumentScrollTop(),
                overlapPositions = (x) ? Overlay.PREVENT_OVERLAP_X : Overlay.PREVENT_OVERLAP_Y,

                context = this.cfg.getProperty("context"),

                bOverlayFitsInViewport = (overlaySize + buffer < viewportSize),
                bPreventContextOverlap = this.cfg.getProperty("preventcontextoverlap") && context && overlapPositions[(context[1] + context[2])],

                minConstraint = docScroll + buffer,
                maxConstraint = docScroll + viewportSize - overlaySize - buffer,

                constrainedVal = val;

            if (val < minConstraint || val > maxConstraint) {
                if (bPreventContextOverlap) {
                    constrainedVal = this._preventOverlap(pos, context[0], overlaySize, viewportSize, docScroll);
                } else {
                    if (bOverlayFitsInViewport) {
                        if (val < minConstraint) {
                            constrainedVal = minConstraint;
                        } else if (val > maxConstraint) {
                            constrainedVal = maxConstraint;
                        }
                    } else {
                        constrainedVal = minConstraint;
                    }
                }
            }

            return constrainedVal;
        },

        /**
         * Helper method, used to position the Overlap to prevent overlap with the
         * context element (used when preventcontextoverlap is enabled)
         *
         * @method _preventOverlap
         * @protected
         * @param {String} pos The coordinate to prevent overlap for, either "x" or "y".
         * @param {HTMLElement} contextEl The context element
         * @param {Number} overlaySize The related overlay dimension value (for "x", the width, for "y", the height)
         * @param {Number} viewportSize The related viewport dimension value (for "x", the width, for "y", the height)
         * @param {Object} docScroll  The related document scroll value (for "x", the scrollLeft, for "y", the scrollTop)
         *
         * @return {Number} The new coordinate value which was set to prevent overlap
         */
        _preventOverlap : function(pos, contextEl, overlaySize, viewportSize, docScroll) {

            var x = (pos == "x"),

                buffer = Overlay.VIEWPORT_OFFSET,

                overlay = this,

                contextElPos   = ((x) ? Dom.getX(contextEl) : Dom.getY(contextEl)) - docScroll,
                contextElSize  = (x) ? contextEl.offsetWidth : contextEl.offsetHeight,

                minRegionSize = contextElPos - buffer,
                maxRegionSize = (viewportSize - (contextElPos + contextElSize)) - buffer,

                bFlipped = false,

                flip = function () {
                    var flippedVal;

                    if ((overlay.cfg.getProperty(pos) - docScroll) > contextElPos) {
                        flippedVal = (contextElPos - overlaySize);
                    } else {
                        flippedVal = (contextElPos + contextElSize);
                    }

                    overlay.cfg.setProperty(pos, (flippedVal + docScroll), true);

                    return flippedVal;
                },

                setPosition = function () {

                    var displayRegionSize = ((overlay.cfg.getProperty(pos) - docScroll) > contextElPos) ? maxRegionSize : minRegionSize,
                        position;

                    if (overlaySize > displayRegionSize) {
                        if (bFlipped) {
                            /*
                                 All possible positions and values have been
                                 tried, but none were successful, so fall back
                                 to the original size and position.
                            */
                            flip();
                        } else {
                            flip();
                            bFlipped = true;
                            position = setPosition();
                        }
                    }

                    return position;
                };

            setPosition();

            return this.cfg.getProperty(pos);
        },

        /**
         * Given x coordinate value, returns the calculated x coordinate required to
         * position the Overlay if it is to be constrained to the viewport, based on the
         * current element size, viewport dimensions and scroll values.
         *
         * @param {Number} x The X coordinate value to be constrained
         * @return {Number} The constrained x coordinate
         */
        getConstrainedX: function (x) {
            return this._getConstrainedPos("x", x);
        },

        /**
         * Given y coordinate value, returns the calculated y coordinate required to
         * position the Overlay if it is to be constrained to the viewport, based on the
         * current element size, viewport dimensions and scroll values.
         *
         * @param {Number} y The Y coordinate value to be constrained
         * @return {Number} The constrained y coordinate
         */
        getConstrainedY : function (y) {
            return this._getConstrainedPos("y", y);
        },

        /**
         * Given x, y coordinate values, returns the calculated coordinates required to
         * position the Overlay if it is to be constrained to the viewport, based on the
         * current element size, viewport dimensions and scroll values.
         *
         * @param {Number} x The X coordinate value to be constrained
         * @param {Number} y The Y coordinate value to be constrained
         * @return {Array} The constrained x and y coordinates at index 0 and 1 respectively;
         */
        getConstrainedXY: function(x, y) {
            return [this.getConstrainedX(x), this.getConstrainedY(y)];
        },

        /**
        * Centers the container in the viewport.
        * @method center
        */
        center: function () {

            var nViewportOffset = Overlay.VIEWPORT_OFFSET,
                elementWidth = this.element.offsetWidth,
                elementHeight = this.element.offsetHeight,
                viewPortWidth = Dom.getViewportWidth(),
                viewPortHeight = Dom.getViewportHeight(),
                x,
                y;

            if (elementWidth < viewPortWidth) {
                x = (viewPortWidth / 2) - (elementWidth / 2) + Dom.getDocumentScrollLeft();
            } else {
                x = nViewportOffset + Dom.getDocumentScrollLeft();
            }

            if (elementHeight < viewPortHeight) {
                y = (viewPortHeight / 2) - (elementHeight / 2) + Dom.getDocumentScrollTop();
            } else {
                y = nViewportOffset + Dom.getDocumentScrollTop();
            }

            this.cfg.setProperty("xy", [parseInt(x, 10), parseInt(y, 10)]);
            this.cfg.refireEvent("iframe");

            if (UA.webkit) {
                this.forceContainerRedraw();
            }
        },

        /**
        * Synchronizes the Panel's "xy", "x", and "y" properties with the
        * Panel's position in the DOM. This is primarily used to update
        * position information during drag & drop.
        * @method syncPosition
        */
        syncPosition: function () {

            var pos = Dom.getXY(this.element);

            this.cfg.setProperty("x", pos[0], true);
            this.cfg.setProperty("y", pos[1], true);
            this.cfg.setProperty("xy", pos, true);

        },

        /**
        * Event handler fired when the resize monitor element is resized.
        * @method onDomResize
        * @param {DOMEvent} e The resize DOM event
        * @param {Object} obj The scope object
        */
        onDomResize: function (e, obj) {

            var me = this;

            Overlay.superclass.onDomResize.call(this, e, obj);

            setTimeout(function () {
                me.syncPosition();
                me.cfg.refireEvent("iframe");
                me.cfg.refireEvent("context");
            }, 0);
        },

        /**
         * Determines the content box height of the given element (height of the element, without padding or borders) in pixels.
         *
         * @method _getComputedHeight
         * @private
         * @param {HTMLElement} el The element for which the content height needs to be determined
         * @return {Number} The content box height of the given element, or null if it could not be determined.
         */
        _getComputedHeight : (function() {

            if (document.defaultView && document.defaultView.getComputedStyle) {
                return function(el) {
                    var height = null;
                    if (el.ownerDocument && el.ownerDocument.defaultView) {
                        var computed = el.ownerDocument.defaultView.getComputedStyle(el, '');
                        if (computed) {
                            height = parseInt(computed.height, 10);
                        }
                    }
                    return (Lang.isNumber(height)) ? height : null;
                };
            } else {
                return function(el) {
                    var height = null;
                    if (el.style.pixelHeight) {
                        height = el.style.pixelHeight;
                    }
                    return (Lang.isNumber(height)) ? height : null;
                };
            }
        })(),

        /**
         * autofillheight validator. Verifies that the autofill value is either null
         * or one of the strings : "body", "header" or "footer".
         *
         * @method _validateAutoFillHeight
         * @protected
         * @param {String} val
         * @return true, if valid, false otherwise
         */
        _validateAutoFillHeight : function(val) {
            return (!val) || (Lang.isString(val) && Overlay.STD_MOD_RE.test(val));
        },

        /**
         * The default custom event handler executed when the overlay's height is changed,
         * if the autofillheight property has been set.
         *
         * @method _autoFillOnHeightChange
         * @protected
         * @param {String} type The event type
         * @param {Array} args The array of arguments passed to event subscribers
         * @param {HTMLElement} el The header, body or footer element which is to be resized to fill
         * out the containers height
         */
        _autoFillOnHeightChange : function(type, args, el) {
            var height = this.cfg.getProperty("height");
            if ((height && height !== "auto") || (height === 0)) {
                this.fillHeight(el);
            }
        },

        /**
         * Returns the sub-pixel height of the el, using getBoundingClientRect, if available,
         * otherwise returns the offsetHeight
         * @method _getPreciseHeight
         * @private
         * @param {HTMLElement} el
         * @return {Float} The sub-pixel height if supported by the browser, else the rounded height.
         */
        _getPreciseHeight : function(el) {
            var height = el.offsetHeight;

            if (el.getBoundingClientRect) {
                var rect = el.getBoundingClientRect();
                height = rect.bottom - rect.top;
            }

            return height;
        },

        /**
         * <p>
         * Sets the height on the provided header, body or footer element to
         * fill out the height of the container. It determines the height of the
         * containers content box, based on it's configured height value, and
         * sets the height of the autofillheight element to fill out any
         * space remaining after the other standard module element heights
         * have been accounted for.
         * </p>
         * <p><strong>NOTE:</strong> This method is not designed to work if an explicit
         * height has not been set on the container, since for an "auto" height container,
         * the heights of the header/body/footer will drive the height of the container.</p>
         *
         * @method fillHeight
         * @param {HTMLElement} el The element which should be resized to fill out the height
         * of the container element.
         */
        fillHeight : function(el) {
            if (el) {
                var container = this.innerElement || this.element,
                    containerEls = [this.header, this.body, this.footer],
                    containerEl,
                    total = 0,
                    filled = 0,
                    remaining = 0,
                    validEl = false;

                for (var i = 0, l = containerEls.length; i < l; i++) {
                    containerEl = containerEls[i];
                    if (containerEl) {
                        if (el !== containerEl) {
                            filled += this._getPreciseHeight(containerEl);
                        } else {
                            validEl = true;
                        }
                    }
                }

                if (validEl) {

                    if (UA.ie || UA.opera) {
                        // Need to set height to 0, to allow height to be reduced
                        Dom.setStyle(el, 'height', 0 + 'px');
                    }

                    total = this._getComputedHeight(container);

                    // Fallback, if we can't get computed value for content height
                    if (total === null) {
                        Dom.addClass(container, "yui-override-padding");
                        total = container.clientHeight; // Content, No Border, 0 Padding (set by yui-override-padding)
                        Dom.removeClass(container, "yui-override-padding");
                    }

                    remaining = Math.max(total - filled, 0);

                    Dom.setStyle(el, "height", remaining + "px");

                    // Re-adjust height if required, to account for el padding and border
                    if (el.offsetHeight != remaining) {
                        remaining = Math.max(remaining - (el.offsetHeight - remaining), 0);
                    }
                    Dom.setStyle(el, "height", remaining + "px");
                }
            }
        },

        /**
        * Places the Overlay on top of all other instances of
        * YAHOO.widget.Overlay.
        * @method bringToTop
        */
        bringToTop: function () {

            var aOverlays = [],
                oElement = this.element;

            function compareZIndexDesc(p_oOverlay1, p_oOverlay2) {

                var sZIndex1 = Dom.getStyle(p_oOverlay1, "zIndex"),
                    sZIndex2 = Dom.getStyle(p_oOverlay2, "zIndex"),

                    nZIndex1 = (!sZIndex1 || isNaN(sZIndex1)) ? 0 : parseInt(sZIndex1, 10),
                    nZIndex2 = (!sZIndex2 || isNaN(sZIndex2)) ? 0 : parseInt(sZIndex2, 10);

                if (nZIndex1 > nZIndex2) {
                    return -1;
                } else if (nZIndex1 < nZIndex2) {
                    return 1;
                } else {
                    return 0;
                }
            }

            function isOverlayElement(p_oElement) {

                var isOverlay = Dom.hasClass(p_oElement, Overlay.CSS_OVERLAY),
                    Panel = YAHOO.widget.Panel;

                if (isOverlay && !Dom.isAncestor(oElement, p_oElement)) {
                    if (Panel && Dom.hasClass(p_oElement, Panel.CSS_PANEL)) {
                        aOverlays[aOverlays.length] = p_oElement.parentNode;
                    } else {
                        aOverlays[aOverlays.length] = p_oElement;
                    }
                }
            }

            Dom.getElementsBy(isOverlayElement, "DIV", document.body);

            aOverlays.sort(compareZIndexDesc);

            var oTopOverlay = aOverlays[0],
                nTopZIndex;

            if (oTopOverlay) {
                nTopZIndex = Dom.getStyle(oTopOverlay, "zIndex");

                if (!isNaN(nTopZIndex)) {
                    var bRequiresBump = false;

                    if (oTopOverlay != oElement) {
                        bRequiresBump = true;
                    } else if (aOverlays.length > 1) {
                        var nNextZIndex = Dom.getStyle(aOverlays[1], "zIndex");
                        // Don't rely on DOM order to stack if 2 overlays are at the same zindex.
                        if (!isNaN(nNextZIndex) && (nTopZIndex == nNextZIndex)) {
                            bRequiresBump = true;
                        }
                    }
                    if (bRequiresBump) {
                        this.cfg.setProperty("zindex", (parseInt(nTopZIndex, 10) + 2));
                    }
                }
            }
        },

        /**
        * Removes the Overlay element from the DOM and sets all child
        * elements to null.
        * @method destroy
        */
        destroy: function () {

            if (this.iframe) {
                this.iframe.parentNode.removeChild(this.iframe);
            }

            this.iframe = null;

            Overlay.windowResizeEvent.unsubscribe(
                this.doCenterOnDOMEvent, this);

            Overlay.windowScrollEvent.unsubscribe(
                this.doCenterOnDOMEvent, this);

            Module.textResizeEvent.unsubscribe(this._autoFillOnHeightChange);

            if (this._contextTriggers) {
                // Unsubscribe context triggers - to cover context triggers which listen for global
                // events such as windowResize and windowScroll. Easier just to unsubscribe all
                this._processTriggers(this._contextTriggers, _UNSUBSCRIBE, this._alignOnTrigger);
            }

            Overlay.superclass.destroy.call(this);
        },

        /**
         * Can be used to force the container to repaint/redraw it's contents.
         * <p>
         * By default applies and then removes a 1px bottom margin through the
         * application/removal of a "yui-force-redraw" class.
         * </p>
         * <p>
         * It is currently used by Overlay to force a repaint for webkit
         * browsers, when centering.
         * </p>
         * @method forceContainerRedraw
         */
        forceContainerRedraw : function() {
            var c = this;
            Dom.addClass(c.element, "yui-force-redraw");
            setTimeout(function() {
                Dom.removeClass(c.element, "yui-force-redraw");
            }, 0);
        },

        /**
        * Returns a String representation of the object.
        * @method toString
        * @return {String} The string representation of the Overlay.
        */
        toString: function () {
            return "Overlay " + this.id;
        }

    });
}());
(function () {

    /**
    * OverlayManager is used for maintaining the focus status of
    * multiple Overlays.
    * @namespace YAHOO.widget
    * @namespace YAHOO.widget
    * @class OverlayManager
    * @constructor
    * @param {Array} overlays Optional. A collection of Overlays to register
    * with the manager.
    * @param {Object} userConfig  The object literal representing the user
    * configuration of the OverlayManager
    */
    YAHOO.widget.OverlayManager = function (userConfig) {
        this.init(userConfig);
    };

    var Overlay = YAHOO.widget.Overlay,
        Event = YAHOO.util.Event,
        Dom = YAHOO.util.Dom,
        Config = YAHOO.util.Config,
        CustomEvent = YAHOO.util.CustomEvent,
        OverlayManager = YAHOO.widget.OverlayManager;

    /**
    * The CSS class representing a focused Overlay
    * @property OverlayManager.CSS_FOCUSED
    * @static
    * @final
    * @type String
    */
    OverlayManager.CSS_FOCUSED = "focused";

    OverlayManager.prototype = {

        /**
        * The class's constructor function
        * @property contructor
        * @type Function
        */
        constructor: OverlayManager,

        /**
        * The array of Overlays that are currently registered
        * @property overlays
        * @type YAHOO.widget.Overlay[]
        */
        overlays: null,

        /**
        * Initializes the default configuration of the OverlayManager
        * @method initDefaultConfig
        */
        initDefaultConfig: function () {
            /**
            * The collection of registered Overlays in use by
            * the OverlayManager
            * @config overlays
            * @type YAHOO.widget.Overlay[]
            * @default null
            */
            this.cfg.addProperty("overlays", { suppressEvent: true } );

            /**
            * The default DOM event that should be used to focus an Overlay
            * @config focusevent
            * @type String
            * @default "mousedown"
            */
            this.cfg.addProperty("focusevent", { value: "mousedown" } );
        },

        /**
        * Initializes the OverlayManager
        * @method init
        * @param {Overlay[]} overlays Optional. A collection of Overlays to
        * register with the manager.
        * @param {Object} userConfig  The object literal representing the user
        * configuration of the OverlayManager
        */
        init: function (userConfig) {

            /**
            * The OverlayManager's Config object used for monitoring
            * configuration properties.
            * @property cfg
            * @type Config
            */
            this.cfg = new Config(this);

            this.initDefaultConfig();

            if (userConfig) {
                this.cfg.applyConfig(userConfig, true);
            }
            this.cfg.fireQueue();

            /**
            * The currently activated Overlay
            * @property activeOverlay
            * @private
            * @type YAHOO.widget.Overlay
            */
            var activeOverlay = null;

            /**
            * Returns the currently focused Overlay
            * @method getActive
            * @return {Overlay} The currently focused Overlay
            */
            this.getActive = function () {
                return activeOverlay;
            };

            /**
            * Focuses the specified Overlay
            * @method focus
            * @param {Overlay} overlay The Overlay to focus
            * @param {String} overlay The id of the Overlay to focus
            */
            this.focus = function (overlay) {
                var o = this.find(overlay);
                if (o) {
                    o.focus();
                }
            };

            /**
            * Removes the specified Overlay from the manager
            * @method remove
            * @param {Overlay} overlay The Overlay to remove
            * @param {String} overlay The id of the Overlay to remove
            */
            this.remove = function (overlay) {

                var o = this.find(overlay),
                        originalZ;

                if (o) {
                    if (activeOverlay == o) {
                        activeOverlay = null;
                    }

                    var bDestroyed = (o.element === null && o.cfg === null) ? true : false;

                    if (!bDestroyed) {
                        // Set it's zindex so that it's sorted to the end.
                        originalZ = Dom.getStyle(o.element, "zIndex");
                        o.cfg.setProperty("zIndex", -1000, true);
                    }

                    this.overlays.sort(this.compareZIndexDesc);
                    this.overlays = this.overlays.slice(0, (this.overlays.length - 1));

                    o.hideEvent.unsubscribe(o.blur);
                    o.destroyEvent.unsubscribe(this._onOverlayDestroy, o);
                    o.focusEvent.unsubscribe(this._onOverlayFocusHandler, o);
                    o.blurEvent.unsubscribe(this._onOverlayBlurHandler, o);

                    if (!bDestroyed) {
                        Event.removeListener(o.element, this.cfg.getProperty("focusevent"), this._onOverlayElementFocus);
                        o.cfg.setProperty("zIndex", originalZ, true);
                        o.cfg.setProperty("manager", null);
                    }

                    /* _managed Flag for custom or existing. Don't want to remove existing */
                    if (o.focusEvent._managed) { o.focusEvent = null; }
                    if (o.blurEvent._managed) { o.blurEvent = null; }

                    if (o.focus._managed) { o.focus = null; }
                    if (o.blur._managed) { o.blur = null; }
                }
            };

            /**
            * Removes focus from all registered Overlays in the manager
            * @method blurAll
            */
            this.blurAll = function () {

                var nOverlays = this.overlays.length,
                    i;

                if (nOverlays > 0) {
                    i = nOverlays - 1;
                    do {
                        this.overlays[i].blur();
                    }
                    while(i--);
                }
            };

            /**
             * Updates the state of the OverlayManager and overlay, as a result of the overlay
             * being blurred.
             *
             * @method _manageBlur
             * @param {Overlay} overlay The overlay instance which got blurred.
             * @protected
             */
            this._manageBlur = function (overlay) {
                var changed = false;
                if (activeOverlay == overlay) {
                    Dom.removeClass(activeOverlay.element, OverlayManager.CSS_FOCUSED);
                    activeOverlay = null;
                    changed = true;
                }
                return changed;
            };

            /**
             * Updates the state of the OverlayManager and overlay, as a result of the overlay
             * receiving focus.
             *
             * @method _manageFocus
             * @param {Overlay} overlay The overlay instance which got focus.
             * @protected
             */
            this._manageFocus = function(overlay) {
                var changed = false;
                if (activeOverlay != overlay) {
                    if (activeOverlay) {
                        activeOverlay.blur();
                    }
                    activeOverlay = overlay;
                    this.bringToTop(activeOverlay);
                    Dom.addClass(activeOverlay.element, OverlayManager.CSS_FOCUSED);
                    changed = true;
                }
                return changed;
            };

            var overlays = this.cfg.getProperty("overlays");

            if (! this.overlays) {
                this.overlays = [];
            }

            if (overlays) {
                this.register(overlays);
                this.overlays.sort(this.compareZIndexDesc);
            }
        },

        /**
        * @method _onOverlayElementFocus
        * @description Event handler for the DOM event that is used to focus
        * the Overlay instance as specified by the "focusevent"
        * configuration property.
        * @private
        * @param {Event} p_oEvent Object representing the DOM event
        * object passed back by the event utility (Event).
        */
        _onOverlayElementFocus: function (p_oEvent) {

            var oTarget = Event.getTarget(p_oEvent),
                oClose = this.close;

            if (oClose && (oTarget == oClose || Dom.isAncestor(oClose, oTarget))) {
                this.blur();
            } else {
                this.focus();
            }
        },

        /**
        * @method _onOverlayDestroy
        * @description "destroy" event handler for the Overlay.
        * @private
        * @param {String} p_sType String representing the name of the event
        * that was fired.
        * @param {Array} p_aArgs Array of arguments sent when the event
        * was fired.
        * @param {Overlay} p_oOverlay Object representing the overlay that
        * fired the event.
        */
        _onOverlayDestroy: function (p_sType, p_aArgs, p_oOverlay) {
            this.remove(p_oOverlay);
        },

        /**
        * @method _onOverlayFocusHandler
        *
        * @description focusEvent Handler, used to delegate to _manageFocus with the correct arguments.
        *
        * @private
        * @param {String} p_sType String representing the name of the event
        * that was fired.
        * @param {Array} p_aArgs Array of arguments sent when the event
        * was fired.
        * @param {Overlay} p_oOverlay Object representing the overlay that
        * fired the event.
        */
        _onOverlayFocusHandler: function(p_sType, p_aArgs, p_oOverlay) {
            this._manageFocus(p_oOverlay);
        },

        /**
        * @method _onOverlayBlurHandler
        * @description blurEvent Handler, used to delegate to _manageBlur with the correct arguments.
        *
        * @private
        * @param {String} p_sType String representing the name of the event
        * that was fired.
        * @param {Array} p_aArgs Array of arguments sent when the event
        * was fired.
        * @param {Overlay} p_oOverlay Object representing the overlay that
        * fired the event.
        */
        _onOverlayBlurHandler: function(p_sType, p_aArgs, p_oOverlay) {
            this._manageBlur(p_oOverlay);
        },

        /**
         * Subscribes to the Overlay based instance focusEvent, to allow the OverlayManager to
         * monitor focus state.
         *
         * If the instance already has a focusEvent (e.g. Menu), OverlayManager will subscribe
         * to the existing focusEvent, however if a focusEvent or focus method does not exist
         * on the instance, the _bindFocus method will add them, and the focus method will
         * update the OverlayManager's state directly.
         *
         * @method _bindFocus
         * @param {Overlay} overlay The overlay for which focus needs to be managed
         * @protected
         */
        _bindFocus : function(overlay) {
            var mgr = this;

            if (!overlay.focusEvent) {
                overlay.focusEvent = overlay.createEvent("focus");
                overlay.focusEvent.signature = CustomEvent.LIST;
                overlay.focusEvent._managed = true;
            } else {
                overlay.focusEvent.subscribe(mgr._onOverlayFocusHandler, overlay, mgr);
            }

            if (!overlay.focus) {
                Event.on(overlay.element, mgr.cfg.getProperty("focusevent"), mgr._onOverlayElementFocus, null, overlay);
                overlay.focus = function () {
                    if (mgr._manageFocus(this)) {
                        // For Panel/Dialog
                        if (this.cfg.getProperty("visible") && this.focusFirst) {
                            this.focusFirst();
                        }
                        this.focusEvent.fire();
                    }
                };
                overlay.focus._managed = true;
            }
        },

        /**
         * Subscribes to the Overlay based instance's blurEvent to allow the OverlayManager to
         * monitor blur state.
         *
         * If the instance already has a blurEvent (e.g. Menu), OverlayManager will subscribe
         * to the existing blurEvent, however if a blurEvent or blur method does not exist
         * on the instance, the _bindBlur method will add them, and the blur method
         * update the OverlayManager's state directly.
         *
         * @method _bindBlur
         * @param {Overlay} overlay The overlay for which blur needs to be managed
         * @protected
         */
        _bindBlur : function(overlay) {
            var mgr = this;

            if (!overlay.blurEvent) {
                overlay.blurEvent = overlay.createEvent("blur");
                overlay.blurEvent.signature = CustomEvent.LIST;
                overlay.focusEvent._managed = true;
            } else {
                overlay.blurEvent.subscribe(mgr._onOverlayBlurHandler, overlay, mgr);
            }

            if (!overlay.blur) {
                overlay.blur = function () {
                    if (mgr._manageBlur(this)) {
                        this.blurEvent.fire();
                    }
                };
                overlay.blur._managed = true;
            }

            overlay.hideEvent.subscribe(overlay.blur);
        },

        /**
         * Subscribes to the Overlay based instance's destroyEvent, to allow the Overlay
         * to be removed for the OverlayManager when destroyed.
         *
         * @method _bindDestroy
         * @param {Overlay} overlay The overlay instance being managed
         * @protected
         */
        _bindDestroy : function(overlay) {
            var mgr = this;
            overlay.destroyEvent.subscribe(mgr._onOverlayDestroy, overlay, mgr);
        },

        /**
         * Ensures the zIndex configuration property on the managed overlay based instance
         * is set to the computed zIndex value from the DOM (with "auto" translating to 0).
         *
         * @method _syncZIndex
         * @param {Overlay} overlay The overlay instance being managed
         * @protected
         */
        _syncZIndex : function(overlay) {
            var zIndex = Dom.getStyle(overlay.element, "zIndex");
            if (!isNaN(zIndex)) {
                overlay.cfg.setProperty("zIndex", parseInt(zIndex, 10));
            } else {
                overlay.cfg.setProperty("zIndex", 0);
            }
        },

        /**
        * Registers an Overlay or an array of Overlays with the manager. Upon
        * registration, the Overlay receives functions for focus and blur,
        * along with CustomEvents for each.
        *
        * @method register
        * @param {Overlay} overlay  An Overlay to register with the manager.
        * @param {Overlay[]} overlay  An array of Overlays to register with
        * the manager.
        * @return {boolean} true if any Overlays are registered.
        */
        register: function (overlay) {

            var registered = false,
                i,
                n;

            if (overlay instanceof Overlay) {

                overlay.cfg.addProperty("manager", { value: this } );

                this._bindFocus(overlay);
                this._bindBlur(overlay);
                this._bindDestroy(overlay);
                this._syncZIndex(overlay);

                this.overlays.push(overlay);
                this.bringToTop(overlay);

                registered = true;

            } else if (overlay instanceof Array) {

                for (i = 0, n = overlay.length; i < n; i++) {
                    registered = this.register(overlay[i]) || registered;
                }

            }

            return registered;
        },

        /**
        * Places the specified Overlay instance on top of all other
        * Overlay instances.
        * @method bringToTop
        * @param {YAHOO.widget.Overlay} p_oOverlay Object representing an
        * Overlay instance.
        * @param {String} p_oOverlay String representing the id of an
        * Overlay instance.
        */
        bringToTop: function (p_oOverlay) {

            var oOverlay = this.find(p_oOverlay),
                nTopZIndex,
                oTopOverlay,
                aOverlays;

            if (oOverlay) {

                aOverlays = this.overlays;
                aOverlays.sort(this.compareZIndexDesc);

                oTopOverlay = aOverlays[0];

                if (oTopOverlay) {
                    nTopZIndex = Dom.getStyle(oTopOverlay.element, "zIndex");

                    if (!isNaN(nTopZIndex)) {

                        var bRequiresBump = false;

                        if (oTopOverlay !== oOverlay) {
                            bRequiresBump = true;
                        } else if (aOverlays.length > 1) {
                            var nNextZIndex = Dom.getStyle(aOverlays[1].element, "zIndex");
                            // Don't rely on DOM order to stack if 2 overlays are at the same zindex.
                            if (!isNaN(nNextZIndex) && (nTopZIndex == nNextZIndex)) {
                                bRequiresBump = true;
                            }
                        }

                        if (bRequiresBump) {
                            oOverlay.cfg.setProperty("zindex", (parseInt(nTopZIndex, 10) + 2));
                        }
                    }
                    aOverlays.sort(this.compareZIndexDesc);
                }
            }
        },

        /**
        * Attempts to locate an Overlay by instance or ID.
        * @method find
        * @param {Overlay} overlay  An Overlay to locate within the manager
        * @param {String} overlay  An Overlay id to locate within the manager
        * @return {Overlay} The requested Overlay, if found, or null if it
        * cannot be located.
        */
        find: function (overlay) {

            var isInstance = overlay instanceof Overlay,
                overlays = this.overlays,
                n = overlays.length,
                found = null,
                o,
                i;

            if (isInstance || typeof overlay == "string") {
                for (i = n-1; i >= 0; i--) {
                    o = overlays[i];
                    if ((isInstance && (o === overlay)) || (o.id == overlay)) {
                        found = o;
                        break;
                    }
                }
            }

            return found;
        },

        /**
        * Used for sorting the manager's Overlays by z-index.
        * @method compareZIndexDesc
        * @private
        * @return {Number} 0, 1, or -1, depending on where the Overlay should
        * fall in the stacking order.
        */
        compareZIndexDesc: function (o1, o2) {

            var zIndex1 = (o1.cfg) ? o1.cfg.getProperty("zIndex") : null, // Sort invalid (destroyed)
                zIndex2 = (o2.cfg) ? o2.cfg.getProperty("zIndex") : null; // objects at bottom.

            if (zIndex1 === null && zIndex2 === null) {
                return 0;
            } else if (zIndex1 === null){
                return 1;
            } else if (zIndex2 === null) {
                return -1;
            } else if (zIndex1 > zIndex2) {
                return -1;
            } else if (zIndex1 < zIndex2) {
                return 1;
            } else {
                return 0;
            }
        },

        /**
        * Shows all Overlays in the manager.
        * @method showAll
        */
        showAll: function () {
            var overlays = this.overlays,
                n = overlays.length,
                i;

            for (i = n - 1; i >= 0; i--) {
                overlays[i].show();
            }
        },

        /**
        * Hides all Overlays in the manager.
        * @method hideAll
        */
        hideAll: function () {
            var overlays = this.overlays,
                n = overlays.length,
                i;

            for (i = n - 1; i >= 0; i--) {
                overlays[i].hide();
            }
        },

        /**
        * Returns a string representation of the object.
        * @method toString
        * @return {String} The string representation of the OverlayManager
        */
        toString: function () {
            return "OverlayManager";
        }
    };
}());
(function () {

    /**
    * Tooltip is an implementation of Overlay that behaves like an OS tooltip,
    * displaying when the user mouses over a particular element, and
    * disappearing on mouse out.
    * @namespace YAHOO.widget
    * @class Tooltip
    * @extends YAHOO.widget.Overlay
    * @constructor
    * @param {String} el The element ID representing the Tooltip <em>OR</em>
    * @param {HTMLElement} el The element representing the Tooltip
    * @param {Object} userConfig The configuration object literal containing
    * the configuration that should be set for this Overlay. See configuration
    * documentation for more details.
    */
    YAHOO.widget.Tooltip = function (el, userConfig) {
        YAHOO.widget.Tooltip.superclass.constructor.call(this, el, userConfig);
    };

    var Lang = YAHOO.lang,
        Event = YAHOO.util.Event,
        CustomEvent = YAHOO.util.CustomEvent,
        Dom = YAHOO.util.Dom,
        Tooltip = YAHOO.widget.Tooltip,
        UA = YAHOO.env.ua,
        bIEQuirks = (UA.ie && (UA.ie <= 6 || document.compatMode == "BackCompat")),

        m_oShadowTemplate,

        /**
        * Constant representing the Tooltip's configuration properties
        * @property DEFAULT_CONFIG
        * @private
        * @final
        * @type Object
        */
        DEFAULT_CONFIG = {

            "PREVENT_OVERLAP": {
                key: "preventoverlap",
                value: true,
                validator: Lang.isBoolean,
                supercedes: ["x", "y", "xy"]
            },

            "SHOW_DELAY": {
                key: "showdelay",
                value: 200,
                validator: Lang.isNumber
            },

            "AUTO_DISMISS_DELAY": {
                key: "autodismissdelay",
                value: 5000,
                validator: Lang.isNumber
            },

            "HIDE_DELAY": {
                key: "hidedelay",
                value: 250,
                validator: Lang.isNumber
            },

            "TEXT": {
                key: "text",
                suppressEvent: true
            },

            "CONTAINER": {
                key: "container"
            },

            "DISABLED": {
                key: "disabled",
                value: false,
                suppressEvent: true
            },

            "XY_OFFSET": {
                key: "xyoffset",
                value: [0, 25],
                suppressEvent: true
            }
        },

        /**
        * Constant representing the name of the Tooltip's events
        * @property EVENT_TYPES
        * @private
        * @final
        * @type Object
        */
        EVENT_TYPES = {
            "CONTEXT_MOUSE_OVER": "contextMouseOver",
            "CONTEXT_MOUSE_OUT": "contextMouseOut",
            "CONTEXT_TRIGGER": "contextTrigger"
        };

    /**
    * Constant representing the Tooltip CSS class
    * @property YAHOO.widget.Tooltip.CSS_TOOLTIP
    * @static
    * @final
    * @type String
    */
    Tooltip.CSS_TOOLTIP = "yui-tt";

    function restoreOriginalWidth(sOriginalWidth, sForcedWidth) {

        var oConfig = this.cfg,
            sCurrentWidth = oConfig.getProperty("width");

        if (sCurrentWidth == sForcedWidth) {
            oConfig.setProperty("width", sOriginalWidth);
        }
    }

    /*
        changeContent event handler that sets a Tooltip instance's "width"
        configuration property to the value of its root HTML
        elements's offsetWidth if a specific width has not been set.
    */

    function setWidthToOffsetWidth(p_sType, p_aArgs) {

        if ("_originalWidth" in this) {
            restoreOriginalWidth.call(this, this._originalWidth, this._forcedWidth);
        }

        var oBody = document.body,
            oConfig = this.cfg,
            sOriginalWidth = oConfig.getProperty("width"),
            sNewWidth,
            oClone;

        if ((!sOriginalWidth || sOriginalWidth == "auto") &&
            (oConfig.getProperty("container") != oBody ||
            oConfig.getProperty("x") >= Dom.getViewportWidth() ||
            oConfig.getProperty("y") >= Dom.getViewportHeight())) {

            oClone = this.element.cloneNode(true);
            oClone.style.visibility = "hidden";
            oClone.style.top = "0px";
            oClone.style.left = "0px";

            oBody.appendChild(oClone);

            sNewWidth = (oClone.offsetWidth + "px");

            oBody.removeChild(oClone);
            oClone = null;

            oConfig.setProperty("width", sNewWidth);
            oConfig.refireEvent("xy");

            this._originalWidth = sOriginalWidth || "";
            this._forcedWidth = sNewWidth;
        }
    }

    // "onDOMReady" that renders the ToolTip

    function onDOMReady(p_sType, p_aArgs, p_oObject) {
        this.render(p_oObject);
    }

    //  "init" event handler that automatically renders the Tooltip

    function onInit() {
        Event.onDOMReady(onDOMReady, this.cfg.getProperty("container"), this);
    }

    YAHOO.extend(Tooltip, YAHOO.widget.Overlay, {

        /**
        * The Tooltip initialization method. This method is automatically
        * called by the constructor. A Tooltip is automatically rendered by
        * the init method, and it also is set to be invisible by default,
        * and constrained to viewport by default as well.
        * @method init
        * @param {String} el The element ID representing the Tooltip <em>OR</em>
        * @param {HTMLElement} el The element representing the Tooltip
        * @param {Object} userConfig The configuration object literal
        * containing the configuration that should be set for this Tooltip.
        * See configuration documentation for more details.
        */
        init: function (el, userConfig) {


            Tooltip.superclass.init.call(this, el);

            this.beforeInitEvent.fire(Tooltip);

            Dom.addClass(this.element, Tooltip.CSS_TOOLTIP);

            if (userConfig) {
                this.cfg.applyConfig(userConfig, true);
            }

            this.cfg.queueProperty("visible", false);
            this.cfg.queueProperty("constraintoviewport", true);

            this.setBody("");

            this.subscribe("changeContent", setWidthToOffsetWidth);
            this.subscribe("init", onInit);
            this.subscribe("render", this.onRender);

            this.initEvent.fire(Tooltip);
        },

        /**
        * Initializes the custom events for Tooltip
        * @method initEvents
        */
        initEvents: function () {

            Tooltip.superclass.initEvents.call(this);
            var SIGNATURE = CustomEvent.LIST;

            /**
            * CustomEvent fired when user mouses over a context element. Returning false from
            * a subscriber to this event will prevent the tooltip from being displayed for
            * the current context element.
            *
            * @event contextMouseOverEvent
            * @param {HTMLElement} context The context element which the user just moused over
            * @param {DOMEvent} e The DOM event object, associated with the mouse over
            */
            this.contextMouseOverEvent = this.createEvent(EVENT_TYPES.CONTEXT_MOUSE_OVER);
            this.contextMouseOverEvent.signature = SIGNATURE;

            /**
            * CustomEvent fired when the user mouses out of a context element.
            *
            * @event contextMouseOutEvent
            * @param {HTMLElement} context The context element which the user just moused out of
            * @param {DOMEvent} e The DOM event object, associated with the mouse out
            */
            this.contextMouseOutEvent = this.createEvent(EVENT_TYPES.CONTEXT_MOUSE_OUT);
            this.contextMouseOutEvent.signature = SIGNATURE;

            /**
            * CustomEvent fired just before the tooltip is displayed for the current context.
            * <p>
            *  You can subscribe to this event if you need to set up the text for the
            *  tooltip based on the context element for which it is about to be displayed.
            * </p>
            * <p>This event differs from the beforeShow event in following respects:</p>
            * <ol>
            *   <li>
            *    When moving from one context element to another, if the tooltip is not
            *    hidden (the <code>hidedelay</code> is not reached), the beforeShow and Show events will not
            *    be fired when the tooltip is displayed for the new context since it is already visible.
            *    However the contextTrigger event is always fired before displaying the tooltip for
            *    a new context.
            *   </li>
            *   <li>
            *    The trigger event provides access to the context element, allowing you to
            *    set the text of the tooltip based on context element for which the tooltip is
            *    triggered.
            *   </li>
            * </ol>
            * <p>
            *  It is not possible to prevent the tooltip from being displayed
            *  using this event. You can use the contextMouseOverEvent if you need to prevent
            *  the tooltip from being displayed.
            * </p>
            * @event contextTriggerEvent
            * @param {HTMLElement} context The context element for which the tooltip is triggered
            */
            this.contextTriggerEvent = this.createEvent(EVENT_TYPES.CONTEXT_TRIGGER);
            this.contextTriggerEvent.signature = SIGNATURE;
        },

        /**
        * Initializes the class's configurable properties which can be
        * changed using the Overlay's Config object (cfg).
        * @method initDefaultConfig
        */
        initDefaultConfig: function () {

            Tooltip.superclass.initDefaultConfig.call(this);

            /**
            * Specifies whether the Tooltip should be kept from overlapping
            * its context element.
            * @config preventoverlap
            * @type Boolean
            * @default true
            */
            this.cfg.addProperty(DEFAULT_CONFIG.PREVENT_OVERLAP.key, {
                value: DEFAULT_CONFIG.PREVENT_OVERLAP.value,
                validator: DEFAULT_CONFIG.PREVENT_OVERLAP.validator,
                supercedes: DEFAULT_CONFIG.PREVENT_OVERLAP.supercedes
            });

            /**
            * The number of milliseconds to wait before showing a Tooltip
            * on mouseover.
            * @config showdelay
            * @type Number
            * @default 200
            */
            this.cfg.addProperty(DEFAULT_CONFIG.SHOW_DELAY.key, {
                handler: this.configShowDelay,
                value: 200,
                validator: DEFAULT_CONFIG.SHOW_DELAY.validator
            });

            /**
            * The number of milliseconds to wait before automatically
            * dismissing a Tooltip after the mouse has been resting on the
            * context element.
            * @config autodismissdelay
            * @type Number
            * @default 5000
            */
            this.cfg.addProperty(DEFAULT_CONFIG.AUTO_DISMISS_DELAY.key, {
                handler: this.configAutoDismissDelay,
                value: DEFAULT_CONFIG.AUTO_DISMISS_DELAY.value,
                validator: DEFAULT_CONFIG.AUTO_DISMISS_DELAY.validator
            });

            /**
            * The number of milliseconds to wait before hiding a Tooltip
            * after mouseout.
            * @config hidedelay
            * @type Number
            * @default 250
            */
            this.cfg.addProperty(DEFAULT_CONFIG.HIDE_DELAY.key, {
                handler: this.configHideDelay,
                value: DEFAULT_CONFIG.HIDE_DELAY.value,
                validator: DEFAULT_CONFIG.HIDE_DELAY.validator
            });

            /**
            * Specifies the Tooltip's text.
            * @config text
            * @type String
            * @default null
            */
            this.cfg.addProperty(DEFAULT_CONFIG.TEXT.key, {
                handler: this.configText,
                suppressEvent: DEFAULT_CONFIG.TEXT.suppressEvent
            });

            /**
            * Specifies the container element that the Tooltip's markup
            * should be rendered into.
            * @config container
            * @type HTMLElement/String
            * @default document.body
            */
            this.cfg.addProperty(DEFAULT_CONFIG.CONTAINER.key, {
                handler: this.configContainer,
                value: document.body
            });

            /**
            * Specifies whether or not the tooltip is disabled. Disabled tooltips
            * will not be displayed. If the tooltip is driven by the title attribute
            * of the context element, the title attribute will still be removed for
            * disabled tooltips, to prevent default tooltip behavior.
            *
            * @config disabled
            * @type Boolean
            * @default false
            */
            this.cfg.addProperty(DEFAULT_CONFIG.DISABLED.key, {
                handler: this.configContainer,
                value: DEFAULT_CONFIG.DISABLED.value,
                supressEvent: DEFAULT_CONFIG.DISABLED.suppressEvent
            });

            /**
            * Specifies the XY offset from the mouse position, where the tooltip should be displayed, specified
            * as a 2 element array (e.g. [10, 20]);
            *
            * @config xyoffset
            * @type Array
            * @default [0, 25]
            */
            this.cfg.addProperty(DEFAULT_CONFIG.XY_OFFSET.key, {
                value: DEFAULT_CONFIG.XY_OFFSET.value.concat(),
                supressEvent: DEFAULT_CONFIG.XY_OFFSET.suppressEvent
            });

            /**
            * Specifies the element or elements that the Tooltip should be
            * anchored to on mouseover.
            * @config context
            * @type HTMLElement[]/String[]
            * @default null
            */

            /**
            * String representing the width of the Tooltip.  <em>Please note:
            * </em> As of version 2.3 if either no value or a value of "auto"
            * is specified, and the Toolip's "container" configuration property
            * is set to something other than <code>document.body</code> or
            * its "context" element resides outside the immediately visible
            * portion of the document, the width of the Tooltip will be
            * calculated based on the offsetWidth of its root HTML and set just
            * before it is made visible.  The original value will be
            * restored when the Tooltip is hidden. This ensures the Tooltip is
            * rendered at a usable width.  For more information see
            * YUILibrary bug #1685496 and YUILibrary
            * bug #1735423.
            * @config width
            * @type String
            * @default null
            */

        },

        // BEGIN BUILT-IN PROPERTY EVENT HANDLERS //

        /**
        * The default event handler fired when the "text" property is changed.
        * @method configText
        * @param {String} type The CustomEvent type (usually the property name)
        * @param {Object[]} args The CustomEvent arguments. For configuration
        * handlers, args[0] will equal the newly applied value for the property.
        * @param {Object} obj The scope object. For configuration handlers,
        * this will usually equal the owner.
        */
        configText: function (type, args, obj) {
            var text = args[0];
            if (text) {
                this.setBody(text);
            }
        },

        /**
        * The default event handler fired when the "container" property
        * is changed.
        * @method configContainer
        * @param {String} type The CustomEvent type (usually the property name)
        * @param {Object[]} args The CustomEvent arguments. For
        * configuration handlers, args[0] will equal the newly applied value
        * for the property.
        * @param {Object} obj The scope object. For configuration handlers,
        * this will usually equal the owner.
        */
        configContainer: function (type, args, obj) {
            var container = args[0];

            if (typeof container == 'string') {
                this.cfg.setProperty("container", document.getElementById(container), true);
            }
        },

        /**
        * @method _removeEventListeners
        * @description Removes all of the DOM event handlers from the HTML
        *  element(s) that trigger the display of the tooltip.
        * @protected
        */
        _removeEventListeners: function () {

            var aElements = this._context,
                nElements,
                oElement,
                i;

            if (aElements) {
                nElements = aElements.length;
                if (nElements > 0) {
                    i = nElements - 1;
                    do {
                        oElement = aElements[i];
                        Event.removeListener(oElement, "mouseover", this.onContextMouseOver);
                        Event.removeListener(oElement, "mousemove", this.onContextMouseMove);
                        Event.removeListener(oElement, "mouseout", this.onContextMouseOut);
                    }
                    while (i--);
                }
            }
        },

        /**
        * The default event handler fired when the "context" property
        * is changed.
        * @method configContext
        * @param {String} type The CustomEvent type (usually the property name)
        * @param {Object[]} args The CustomEvent arguments. For configuration
        * handlers, args[0] will equal the newly applied value for the property.
        * @param {Object} obj The scope object. For configuration handlers,
        * this will usually equal the owner.
        */
        configContext: function (type, args, obj) {

            var context = args[0],
                aElements,
                nElements,
                oElement,
                i;

            if (context) {

                // Normalize parameter into an array
                if (! (context instanceof Array)) {
                    if (typeof context == "string") {
                        this.cfg.setProperty("context", [document.getElementById(context)], true);
                    } else { // Assuming this is an element
                        this.cfg.setProperty("context", [context], true);
                    }
                    context = this.cfg.getProperty("context");
                }

                // Remove any existing mouseover/mouseout listeners
                this._removeEventListeners();

                // Add mouseover/mouseout listeners to context elements
                this._context = context;

                aElements = this._context;

                if (aElements) {
                    nElements = aElements.length;
                    if (nElements > 0) {
                        i = nElements - 1;
                        do {
                            oElement = aElements[i];
                            Event.on(oElement, "mouseover", this.onContextMouseOver, this);
                            Event.on(oElement, "mousemove", this.onContextMouseMove, this);
                            Event.on(oElement, "mouseout", this.onContextMouseOut, this);
                        }
                        while (i--);
                    }
                }
            }
        },

        // END BUILT-IN PROPERTY EVENT HANDLERS //

        // BEGIN BUILT-IN DOM EVENT HANDLERS //

        /**
        * The default event handler fired when the user moves the mouse while
        * over the context element.
        * @method onContextMouseMove
        * @param {DOMEvent} e The current DOM event
        * @param {Object} obj The object argument
        */
        onContextMouseMove: function (e, obj) {
            obj.pageX = Event.getPageX(e);
            obj.pageY = Event.getPageY(e);
        },

        /**
        * The default event handler fired when the user mouses over the
        * context element.
        * @method onContextMouseOver
        * @param {DOMEvent} e The current DOM event
        * @param {Object} obj The object argument
        */
        onContextMouseOver: function (e, obj) {
            var context = this;

            if (context.title) {
                obj._tempTitle = context.title;
                context.title = "";
            }

            // Fire first, to honor disabled set in the listner
            if (obj.fireEvent("contextMouseOver", context, e) !== false
                    && !obj.cfg.getProperty("disabled")) {

                // Stop the tooltip from being hidden (set on last mouseout)
                if (obj.hideProcId) {
                    clearTimeout(obj.hideProcId);
                    obj.hideProcId = null;
                }

                Event.on(context, "mousemove", obj.onContextMouseMove, obj);

                /**
                * The unique process ID associated with the thread responsible
                * for showing the Tooltip.
                * @type int
                */
                obj.showProcId = obj.doShow(e, context);
            }
        },

        /**
        * The default event handler fired when the user mouses out of
        * the context element.
        * @method onContextMouseOut
        * @param {DOMEvent} e The current DOM event
        * @param {Object} obj The object argument
        */
        onContextMouseOut: function (e, obj) {
            var el = this;

            if (obj._tempTitle) {
                el.title = obj._tempTitle;
                obj._tempTitle = null;
            }

            if (obj.showProcId) {
                clearTimeout(obj.showProcId);
                obj.showProcId = null;
            }

            if (obj.hideProcId) {
                clearTimeout(obj.hideProcId);
                obj.hideProcId = null;
            }

            obj.fireEvent("contextMouseOut", el, e);

            obj.hideProcId = setTimeout(function () {
                obj.hide();
            }, obj.cfg.getProperty("hidedelay"));
        },

        // END BUILT-IN DOM EVENT HANDLERS //

        /**
        * Processes the showing of the Tooltip by setting the timeout delay
        * and offset of the Tooltip.
        * @method doShow
        * @param {DOMEvent} e The current DOM event
        * @param {HTMLElement} context The current context element
        * @return {Number} The process ID of the timeout function associated
        * with doShow
        */
        doShow: function (e, context) {

            var offset = this.cfg.getProperty("xyoffset"),
                xOffset = offset[0],
                yOffset = offset[1],
                me = this;

            if (UA.opera && context.tagName &&
                context.tagName.toUpperCase() == "A") {
                yOffset += 12;
            }

            return setTimeout(function () {

                var txt = me.cfg.getProperty("text");

                // title does not over-ride text
                if (me._tempTitle && (txt === "" || YAHOO.lang.isUndefined(txt) || YAHOO.lang.isNull(txt))) {
                    me.setBody(me._tempTitle);
                } else {
                    me.cfg.refireEvent("text");
                }

                me.moveTo(me.pageX + xOffset, me.pageY + yOffset);

                if (me.cfg.getProperty("preventoverlap")) {
                    me.preventOverlap(me.pageX, me.pageY);
                }

                Event.removeListener(context, "mousemove", me.onContextMouseMove);

                me.contextTriggerEvent.fire(context);

                me.show();

                me.hideProcId = me.doHide();

            }, this.cfg.getProperty("showdelay"));
        },

        /**
        * Sets the timeout for the auto-dismiss delay, which by default is 5
        * seconds, meaning that a tooltip will automatically dismiss itself
        * after 5 seconds of being displayed.
        * @method doHide
        */
        doHide: function () {

            var me = this;


            return setTimeout(function () {

                me.hide();

            }, this.cfg.getProperty("autodismissdelay"));

        },

        /**
        * Fired when the Tooltip is moved, this event handler is used to
        * prevent the Tooltip from overlapping with its context element.
        * @method preventOverlay
        * @param {Number} pageX The x coordinate position of the mouse pointer
        * @param {Number} pageY The y coordinate position of the mouse pointer
        */
        preventOverlap: function (pageX, pageY) {

            var height = this.element.offsetHeight,
                mousePoint = new YAHOO.util.Point(pageX, pageY),
                elementRegion = Dom.getRegion(this.element);

            elementRegion.top -= 5;
            elementRegion.left -= 5;
            elementRegion.right += 5;
            elementRegion.bottom += 5;


            if (elementRegion.contains(mousePoint)) {
                this.cfg.setProperty("y", (pageY - height - 5));
            }
        },


        /**
        * @method onRender
        * @description "render" event handler for the Tooltip.
        * @param {String} p_sType String representing the name of the event
        * that was fired.
        * @param {Array} p_aArgs Array of arguments sent when the event
        * was fired.
        */
        onRender: function (p_sType, p_aArgs) {

            function sizeShadow() {

                var oElement = this.element,
                    oShadow = this.underlay;

                if (oShadow) {
                    oShadow.style.width = (oElement.offsetWidth + 6) + "px";
                    oShadow.style.height = (oElement.offsetHeight + 1) + "px";
                }

            }

            function addShadowVisibleClass() {
                Dom.addClass(this.underlay, "yui-tt-shadow-visible");

                if (UA.ie) {
                    this.forceUnderlayRedraw();
                }
            }

            function removeShadowVisibleClass() {
                Dom.removeClass(this.underlay, "yui-tt-shadow-visible");
            }

            function createShadow() {

                var oShadow = this.underlay,
                    oElement,
                    Module,
                    nIE,
                    me;

                if (!oShadow) {

                    oElement = this.element;
                    Module = YAHOO.widget.Module;
                    nIE = UA.ie;
                    me = this;

                    if (!m_oShadowTemplate) {
                        m_oShadowTemplate = document.createElement("div");
                        m_oShadowTemplate.className = "yui-tt-shadow";
                    }

                    oShadow = m_oShadowTemplate.cloneNode(false);

                    oElement.appendChild(oShadow);

                    this.underlay = oShadow;

                    // Backward compatibility, even though it's probably
                    // intended to be "private", it isn't marked as such in the api docs
                    this._shadow = this.underlay;

                    addShadowVisibleClass.call(this);

                    this.subscribe("beforeShow", addShadowVisibleClass);
                    this.subscribe("hide", removeShadowVisibleClass);

                    if (bIEQuirks) {
                        window.setTimeout(function () {
                            sizeShadow.call(me);
                        }, 0);

                        this.cfg.subscribeToConfigEvent("width", sizeShadow);
                        this.cfg.subscribeToConfigEvent("height", sizeShadow);
                        this.subscribe("changeContent", sizeShadow);

                        Module.textResizeEvent.subscribe(sizeShadow, this, true);
                        this.subscribe("destroy", function () {
                            Module.textResizeEvent.unsubscribe(sizeShadow, this);
                        });
                    }
                }
            }

            function onBeforeShow() {
                createShadow.call(this);
                this.unsubscribe("beforeShow", onBeforeShow);
            }

            if (this.cfg.getProperty("visible")) {
                createShadow.call(this);
            } else {
                this.subscribe("beforeShow", onBeforeShow);
            }

        },

        /**
         * Forces the underlay element to be repainted, through the application/removal
         * of a yui-force-redraw class to the underlay element.
         *
         * @method forceUnderlayRedraw
         */
        forceUnderlayRedraw : function() {
            var tt = this;
            Dom.addClass(tt.underlay, "yui-force-redraw");
            setTimeout(function() {Dom.removeClass(tt.underlay, "yui-force-redraw");}, 0);
        },

        /**
        * Removes the Tooltip element from the DOM and sets all child
        * elements to null.
        * @method destroy
        */
        destroy: function () {

            // Remove any existing mouseover/mouseout listeners
            this._removeEventListeners();

            Tooltip.superclass.destroy.call(this);

        },

        /**
        * Returns a string representation of the object.
        * @method toString
        * @return {String} The string representation of the Tooltip
        */
        toString: function () {
            return "Tooltip " + this.id;
        }

    });

}());
(function () {

    /**
    * Panel is an implementation of Overlay that behaves like an OS window,
    * with a draggable header and an optional close icon at the top right.
    * @namespace YAHOO.widget
    * @class Panel
    * @extends YAHOO.widget.Overlay
    * @constructor
    * @param {String} el The element ID representing the Panel <em>OR</em>
    * @param {HTMLElement} el The element representing the Panel
    * @param {Object} userConfig The configuration object literal containing
    * the configuration that should be set for this Panel. See configuration
    * documentation for more details.
    */
    YAHOO.widget.Panel = function (el, userConfig) {
        YAHOO.widget.Panel.superclass.constructor.call(this, el, userConfig);
    };

    var _currentModal = null;

    var Lang = YAHOO.lang,
        Util = YAHOO.util,
        Dom = Util.Dom,
        Event = Util.Event,
        CustomEvent = Util.CustomEvent,
        KeyListener = YAHOO.util.KeyListener,
        Config = Util.Config,
        Overlay = YAHOO.widget.Overlay,
        Panel = YAHOO.widget.Panel,
        UA = YAHOO.env.ua,

        bIEQuirks = (UA.ie && (UA.ie <= 6 || document.compatMode == "BackCompat")),

        m_oMaskTemplate,
        m_oUnderlayTemplate,
        m_oCloseIconTemplate,

        /**
        * Constant representing the name of the Panel's events
        * @property EVENT_TYPES
        * @private
        * @final
        * @type Object
        */
        EVENT_TYPES = {
            "SHOW_MASK": "showMask",
            "HIDE_MASK": "hideMask",
            "DRAG": "drag"
        },

        /**
        * Constant representing the Panel's configuration properties
        * @property DEFAULT_CONFIG
        * @private
        * @final
        * @type Object
        */
        DEFAULT_CONFIG = {

            "CLOSE": {
                key: "close",
                value: true,
                validator: Lang.isBoolean,
                supercedes: ["visible"]
            },

            "DRAGGABLE": {
                key: "draggable",
                value: (Util.DD ? true : false),
                validator: Lang.isBoolean,
                supercedes: ["visible"]
            },

            "DRAG_ONLY" : {
                key: "dragonly",
                value: false,
                validator: Lang.isBoolean,
                supercedes: ["draggable"]
            },

            "UNDERLAY": {
                key: "underlay",
                value: "shadow",
                supercedes: ["visible"]
            },

            "MODAL": {
                key: "modal",
                value: false,
                validator: Lang.isBoolean,
                supercedes: ["visible", "zindex"]
            },

            "KEY_LISTENERS": {
                key: "keylisteners",
                suppressEvent: true,
                supercedes: ["visible"]
            },

            "STRINGS" : {
                key: "strings",
                supercedes: ["close"],
                validator: Lang.isObject,
                value: {
                    close: "Close"
                }
            }
        };

    /**
    * Constant representing the default CSS class used for a Panel
    * @property YAHOO.widget.Panel.CSS_PANEL
    * @static
    * @final
    * @type String
    */
    Panel.CSS_PANEL = "yui-panel";

    /**
    * Constant representing the default CSS class used for a Panel's
    * wrapping container
    * @property YAHOO.widget.Panel.CSS_PANEL_CONTAINER
    * @static
    * @final
    * @type String
    */
    Panel.CSS_PANEL_CONTAINER = "yui-panel-container";

    /**
     * Constant representing the default set of focusable elements
     * on the pagewhich Modal Panels will prevent access to, when
     * the modal mask is displayed
     *
     * @property YAHOO.widget.Panel.FOCUSABLE
     * @static
     * @type Array
     */
    Panel.FOCUSABLE = [
        "a",
        "button",
        "select",
        "textarea",
        "input",
        "iframe"
    ];

    // Private CustomEvent listeners

    /*
        "beforeRender" event handler that creates an empty header for a Panel
        instance if its "draggable" configuration property is set to "true"
        and no header has been created.
    */

    function createHeader(p_sType, p_aArgs) {
        if (!this.header && this.cfg.getProperty("draggable")) {
            this.setHeader("&#160;");
        }
    }

    /*
        "hide" event handler that sets a Panel instance's "width"
        configuration property back to its original value before
        "setWidthToOffsetWidth" was called.
    */

    function restoreOriginalWidth(p_sType, p_aArgs, p_oObject) {

        var sOriginalWidth = p_oObject[0],
            sNewWidth = p_oObject[1],
            oConfig = this.cfg,
            sCurrentWidth = oConfig.getProperty("width");

        if (sCurrentWidth == sNewWidth) {
            oConfig.setProperty("width", sOriginalWidth);
        }

        this.unsubscribe("hide", restoreOriginalWidth, p_oObject);
    }

    /*
        "beforeShow" event handler that sets a Panel instance's "width"
        configuration property to the value of its root HTML
        elements's offsetWidth
    */

    function setWidthToOffsetWidth(p_sType, p_aArgs) {

        var oConfig,
            sOriginalWidth,
            sNewWidth;

        if (bIEQuirks) {

            oConfig = this.cfg;
            sOriginalWidth = oConfig.getProperty("width");

            if (!sOriginalWidth || sOriginalWidth == "auto") {

                sNewWidth = (this.element.offsetWidth + "px");

                oConfig.setProperty("width", sNewWidth);

                this.subscribe("hide", restoreOriginalWidth,
                    [(sOriginalWidth || ""), sNewWidth]);

            }
        }
    }

    YAHOO.extend(Panel, Overlay, {

        /**
        * The Overlay initialization method, which is executed for Overlay and
        * all of its subclasses. This method is automatically called by the
        * constructor, and  sets up all DOM references for pre-existing markup,
        * and creates required markup if it is not already present.
        * @method init
        * @param {String} el The element ID representing the Overlay <em>OR</em>
        * @param {HTMLElement} el The element representing the Overlay
        * @param {Object} userConfig The configuration object literal
        * containing the configuration that should be set for this Overlay.
        * See configuration documentation for more details.
        */
        init: function (el, userConfig) {
            /*
                 Note that we don't pass the user config in here yet because
                 we only want it executed once, at the lowest subclass level
            */

            Panel.superclass.init.call(this, el/*, userConfig*/);

            this.beforeInitEvent.fire(Panel);

            Dom.addClass(this.element, Panel.CSS_PANEL);

            this.buildWrapper();

            if (userConfig) {
                this.cfg.applyConfig(userConfig, true);
            }

            this.subscribe("showMask", this._addFocusHandlers);
            this.subscribe("hideMask", this._removeFocusHandlers);
            this.subscribe("beforeRender", createHeader);

            this.subscribe("render", function() {
                this.setFirstLastFocusable();
                this.subscribe("changeContent", this.setFirstLastFocusable);
            });

            this.subscribe("show", this.focusFirst);

            this.initEvent.fire(Panel);
        },

        /**
         * @method _onElementFocus
         * @private
         *
         * "focus" event handler for a focuable element. Used to automatically
         * blur the element when it receives focus to ensure that a Panel
         * instance's modality is not compromised.
         *
         * @param {Event} e The DOM event object
         */
        _onElementFocus : function(e){

            if(_currentModal === this) {

                var target = Event.getTarget(e),
                    doc = document.documentElement,
                    insideDoc = (target !== doc && target !== window);

                // mask and documentElement checks added for IE, which focuses on the mask when it's clicked on, and focuses on
                // the documentElement, when the document scrollbars are clicked on
                if (insideDoc && target !== this.element && target !== this.mask && !Dom.isAncestor(this.element, target)) {
                    try {
                        if (this.firstElement) {
                            this.firstElement.focus();
                        } else {
                            if (this._modalFocus) {
                                this._modalFocus.focus();
                            } else {
                                this.innerElement.focus();
                            }
                        }
                    } catch(err){
                        // Just in case we fail to focus
                        try {
                            if (insideDoc && target !== document.body) {
                                target.blur();
                            }
                        } catch(err2) { }
                    }
                }
            }
        },

        /**
         *  @method _addFocusHandlers
         *  @protected
         *
         *  "showMask" event handler that adds a "focus" event handler to all
         *  focusable elements in the document to enforce a Panel instance's
         *  modality from being compromised.
         *
         *  @param p_sType {String} Custom event type
         *  @param p_aArgs {Array} Custom event arguments
         */
        _addFocusHandlers: function(p_sType, p_aArgs) {
            if (!this.firstElement) {
                if (UA.webkit || UA.opera) {
                    if (!this._modalFocus) {
                        this._createHiddenFocusElement();
                    }
                } else {
                    this.innerElement.tabIndex = 0;
                }
            }
            this.setTabLoop(this.firstElement, this.lastElement);
            Event.onFocus(document.documentElement, this._onElementFocus, this, true);
            _currentModal = this;
        },

        /**
         * Creates a hidden focusable element, used to focus on,
         * to enforce modality for browsers in which focus cannot
         * be applied to the container box.
         *
         * @method _createHiddenFocusElement
         * @private
         */
        _createHiddenFocusElement : function() {
            var e = document.createElement("button");
            e.style.height = "1px";
            e.style.width = "1px";
            e.style.position = "absolute";
            e.style.left = "-10000em";
            e.style.opacity = 0;
            e.tabIndex = -1;
            this.innerElement.appendChild(e);
            this._modalFocus = e;
        },

        /**
         *  @method _removeFocusHandlers
         *  @protected
         *
         *  "hideMask" event handler that removes all "focus" event handlers added
         *  by the "addFocusEventHandlers" method.
         *
         *  @param p_sType {String} Event type
         *  @param p_aArgs {Array} Event Arguments
         */
        _removeFocusHandlers: function(p_sType, p_aArgs) {
            Event.removeFocusListener(document.documentElement, this._onElementFocus, this);

            if (_currentModal == this) {
                _currentModal = null;
            }
        },

        /**
         * Sets focus to the first element in the Panel.
         *
         * @method focusFirst
         */
        focusFirst: function (type, args, obj) {
            var el = this.firstElement;

            if (args && args[1]) {
                Event.stopEvent(args[1]);
            }

            if (el) {
                try {
                    el.focus();
                } catch(err) {
                    // Ignore
                }
            }
        },

        /**
         * Sets focus to the last element in the Panel.
         *
         * @method focusLast
         */
        focusLast: function (type, args, obj) {
            var el = this.lastElement;

            if (args && args[1]) {
                Event.stopEvent(args[1]);
            }

            if (el) {
                try {
                    el.focus();
                } catch(err) {
                    // Ignore
                }
            }
        },

        /**
         * Sets up a tab, shift-tab loop between the first and last elements
         * provided. NOTE: Sets up the preventBackTab and preventTabOut KeyListener
         * instance properties, which are reset everytime this method is invoked.
         *
         * @method setTabLoop
         * @param {HTMLElement} firstElement
         * @param {HTMLElement} lastElement
         *
         */
        setTabLoop : function(firstElement, lastElement) {

            var backTab = this.preventBackTab, tab = this.preventTabOut,
                showEvent = this.showEvent, hideEvent = this.hideEvent;

            if (backTab) {
                backTab.disable();
                showEvent.unsubscribe(backTab.enable, backTab);
                hideEvent.unsubscribe(backTab.disable, backTab);
                backTab = this.preventBackTab = null;
            }

            if (tab) {
                tab.disable();
                showEvent.unsubscribe(tab.enable, tab);
                hideEvent.unsubscribe(tab.disable,tab);
                tab = this.preventTabOut = null;
            }

            if (firstElement) {
                this.preventBackTab = new KeyListener(firstElement,
                    {shift:true, keys:9},
                    {fn:this.focusLast, scope:this, correctScope:true}
                );
                backTab = this.preventBackTab;

                showEvent.subscribe(backTab.enable, backTab, true);
                hideEvent.subscribe(backTab.disable,backTab, true);
            }

            if (lastElement) {
                this.preventTabOut = new KeyListener(lastElement,
                    {shift:false, keys:9},
                    {fn:this.focusFirst, scope:this, correctScope:true}
                );
                tab = this.preventTabOut;

                showEvent.subscribe(tab.enable, tab, true);
                hideEvent.subscribe(tab.disable,tab, true);
            }
        },

        /**
         * Returns an array of the currently focusable items which reside within
         * Panel. The set of focusable elements the method looks for are defined
         * in the Panel.FOCUSABLE static property
         *
         * @method getFocusableElements
         * @param {HTMLElement} root element to start from.
         */
        getFocusableElements : function(root) {

            root = root || this.innerElement;

            var focusable = {};
            for (var i = 0; i < Panel.FOCUSABLE.length; i++) {
                focusable[Panel.FOCUSABLE[i]] = true;
            }

            function isFocusable(el) {
                if (el.focus && el.type !== "hidden" && !el.disabled && focusable[el.tagName.toLowerCase()]) {
                    return true;
                }
                return false;
            }

            // Not looking by Tag, since we want elements in DOM order
            return Dom.getElementsBy(isFocusable, null, root);
        },

        /**
         * Sets the firstElement and lastElement instance properties
         * to the first and last focusable elements in the Panel.
         *
         * @method setFirstLastFocusable
         */
        setFirstLastFocusable : function() {

            this.firstElement = null;
            this.lastElement = null;

            var elements = this.getFocusableElements();
            this.focusableElements = elements;

            if (elements.length > 0) {
                this.firstElement = elements[0];
                this.lastElement = elements[elements.length - 1];
            }

            if (this.cfg.getProperty("modal")) {
                this.setTabLoop(this.firstElement, this.lastElement);
            }
        },

        /**
         * Initializes the custom events for Module which are fired
         * automatically at appropriate times by the Module class.
         */
        initEvents: function () {
            Panel.superclass.initEvents.call(this);

            var SIGNATURE = CustomEvent.LIST;

            /**
            * CustomEvent fired after the modality mask is shown
            * @event showMaskEvent
            */
            this.showMaskEvent = this.createEvent(EVENT_TYPES.SHOW_MASK);
            this.showMaskEvent.signature = SIGNATURE;

            /**
            * CustomEvent fired after the modality mask is hidden
            * @event hideMaskEvent
            */
            this.hideMaskEvent = this.createEvent(EVENT_TYPES.HIDE_MASK);
            this.hideMaskEvent.signature = SIGNATURE;

            /**
            * CustomEvent when the Panel is dragged
            * @event dragEvent
            */
            this.dragEvent = this.createEvent(EVENT_TYPES.DRAG);
            this.dragEvent.signature = SIGNATURE;
        },

        /**
         * Initializes the class's configurable properties which can be changed
         * using the Panel's Config object (cfg).
         * @method initDefaultConfig
         */
        initDefaultConfig: function () {
            Panel.superclass.initDefaultConfig.call(this);

            // Add panel config properties //

            /**
            * True if the Panel should display a "close" button
            * @config close
            * @type Boolean
            * @default true
            */
            this.cfg.addProperty(DEFAULT_CONFIG.CLOSE.key, {
                handler: this.configClose,
                value: DEFAULT_CONFIG.CLOSE.value,
                validator: DEFAULT_CONFIG.CLOSE.validator,
                supercedes: DEFAULT_CONFIG.CLOSE.supercedes
            });

            /**
            * Boolean specifying if the Panel should be draggable.  The default
            * value is "true" if the Drag and Drop utility is included,
            * otherwise it is "false." <strong>PLEASE NOTE:</strong> There is a
            * known issue in IE 6 (Strict Mode and Quirks Mode) and IE 7
            * (Quirks Mode) where Panels that either don't have a value set for
            * their "width" configuration property, or their "width"
            * configuration property is set to "auto" will only be draggable by
            * placing the mouse on the text of the Panel's header element.
            * To fix this bug, draggable Panels missing a value for their
            * "width" configuration property, or whose "width" configuration
            * property is set to "auto" will have it set to the value of
            * their root HTML element's offsetWidth before they are made
            * visible.  The calculated width is then removed when the Panel is
            * hidden. <em>This fix is only applied to draggable Panels in IE 6
            * (Strict Mode and Quirks Mode) and IE 7 (Quirks Mode)</em>. For
            * more information on this issue see:
            * YUILibrary bugs #1726972 and #1589210.
            * @config draggable
            * @type Boolean
            * @default true
            */
            this.cfg.addProperty(DEFAULT_CONFIG.DRAGGABLE.key, {
                handler: this.configDraggable,
                value: (Util.DD) ? true : false,
                validator: DEFAULT_CONFIG.DRAGGABLE.validator,
                supercedes: DEFAULT_CONFIG.DRAGGABLE.supercedes
            });

            /**
            * Boolean specifying if the draggable Panel should be drag only, not interacting with drop
            * targets on the page.
            * <p>
            * When set to true, draggable Panels will not check to see if they are over drop targets,
            * or fire the DragDrop events required to support drop target interaction (onDragEnter,
            * onDragOver, onDragOut, onDragDrop etc.).
            * If the Panel is not designed to be dropped on any target elements on the page, then this
            * flag can be set to true to improve performance.
            * </p>
            * <p>
            * When set to false, all drop target related events will be fired.
            * </p>
            * <p>
            * The property is set to false by default to maintain backwards compatibility but should be
            * set to true if drop target interaction is not required for the Panel, to improve performance.</p>
            *
            * @config dragOnly
            * @type Boolean
            * @default false
            */
            this.cfg.addProperty(DEFAULT_CONFIG.DRAG_ONLY.key, {
                value: DEFAULT_CONFIG.DRAG_ONLY.value,
                validator: DEFAULT_CONFIG.DRAG_ONLY.validator,
                supercedes: DEFAULT_CONFIG.DRAG_ONLY.supercedes
            });

            /**
            * Sets the type of underlay to display for the Panel. Valid values
            * are "shadow," "matte," and "none".  <strong>PLEASE NOTE:</strong>
            * The creation of the underlay element is deferred until the Panel
            * is initially made visible.  For Gecko-based browsers on Mac
            * OS X the underlay elment is always created as it is used as a
            * shim to prevent Aqua scrollbars below a Panel instance from poking
            * through it (See YUILibrary bug #1723530).
            * @config underlay
            * @type String
            * @default shadow
            */
            this.cfg.addProperty(DEFAULT_CONFIG.UNDERLAY.key, {
                handler: this.configUnderlay,
                value: DEFAULT_CONFIG.UNDERLAY.value,
                supercedes: DEFAULT_CONFIG.UNDERLAY.supercedes
            });

            /**
            * True if the Panel should be displayed in a modal fashion,
            * automatically creating a transparent mask over the document that
            * will not be removed until the Panel is dismissed.
            * @config modal
            * @type Boolean
            * @default false
            */
            this.cfg.addProperty(DEFAULT_CONFIG.MODAL.key, {
                handler: this.configModal,
                value: DEFAULT_CONFIG.MODAL.value,
                validator: DEFAULT_CONFIG.MODAL.validator,
                supercedes: DEFAULT_CONFIG.MODAL.supercedes
            });

            /**
            * A KeyListener (or array of KeyListeners) that will be enabled
            * when the Panel is shown, and disabled when the Panel is hidden.
            * @config keylisteners
            * @type YAHOO.util.KeyListener[]
            * @default null
            */
            this.cfg.addProperty(DEFAULT_CONFIG.KEY_LISTENERS.key, {
                handler: this.configKeyListeners,
                suppressEvent: DEFAULT_CONFIG.KEY_LISTENERS.suppressEvent,
                supercedes: DEFAULT_CONFIG.KEY_LISTENERS.supercedes
            });

            /**
            * UI Strings used by the Panel
            *
            * @config strings
            * @type Object
            * @default An object literal with the properties shown below:
            *     <dl>
            *         <dt>close</dt><dd><em>String</em> : The string to use for the close icon. Defaults to "Close".</dd>
            *     </dl>
            */
            this.cfg.addProperty(DEFAULT_CONFIG.STRINGS.key, {
                value:DEFAULT_CONFIG.STRINGS.value,
                handler:this.configStrings,
                validator:DEFAULT_CONFIG.STRINGS.validator,
                supercedes:DEFAULT_CONFIG.STRINGS.supercedes
            });
        },

        // BEGIN BUILT-IN PROPERTY EVENT HANDLERS //

        /**
        * The default event handler fired when the "close" property is changed.
        * The method controls the appending or hiding of the close icon at the
        * top right of the Panel.
        * @method configClose
        * @param {String} type The CustomEvent type (usually the property name)
        * @param {Object[]} args The CustomEvent arguments. For configuration
        * handlers, args[0] will equal the newly applied value for the property.
        * @param {Object} obj The scope object. For configuration handlers,
        * this will usually equal the owner.
        */
        configClose: function (type, args, obj) {

            var val = args[0],
                oClose = this.close,
                strings = this.cfg.getProperty("strings");

            if (val) {
                if (!oClose) {

                    if (!m_oCloseIconTemplate) {
                        m_oCloseIconTemplate = document.createElement("a");
                        m_oCloseIconTemplate.className = "container-close";
                        m_oCloseIconTemplate.href = "#";
                    }

                    oClose = m_oCloseIconTemplate.cloneNode(true);
                    this.innerElement.appendChild(oClose);

                    oClose.innerHTML = (strings && strings.close) ? strings.close : "&#160;";

                    Event.on(oClose, "click", this._doClose, this, true);

                    this.close = oClose;

                } else {
                    oClose.style.display = "block";
                }

            } else {
                if (oClose) {
                    oClose.style.display = "none";
                }
            }

        },

        /**
         * Event handler for the close icon
         *
         * @method _doClose
         * @protected
         *
         * @param {DOMEvent} e
         */
        _doClose : function (e) {
            Event.preventDefault(e);
            this.hide();
        },

        /**
        * The default event handler fired when the "draggable" property
        * is changed.
        * @method configDraggable
        * @param {String} type The CustomEvent type (usually the property name)
        * @param {Object[]} args The CustomEvent arguments. For configuration
        * handlers, args[0] will equal the newly applied value for the property.
        * @param {Object} obj The scope object. For configuration handlers,
        * this will usually equal the owner.
        */
        configDraggable: function (type, args, obj) {
            var val = args[0];

            if (val) {
                if (!Util.DD) {
                    this.cfg.setProperty("draggable", false);
                    return;
                }

                if (this.header) {
                    Dom.setStyle(this.header, "cursor", "move");
                    this.registerDragDrop();
                }

                this.subscribe("beforeShow", setWidthToOffsetWidth);

            } else {

                if (this.dd) {
                    this.dd.unreg();
                }

                if (this.header) {
                    Dom.setStyle(this.header,"cursor","auto");
                }

                this.unsubscribe("beforeShow", setWidthToOffsetWidth);
            }
        },

        /**
        * The default event handler fired when the "underlay" property
        * is changed.
        * @method configUnderlay
        * @param {String} type The CustomEvent type (usually the property name)
        * @param {Object[]} args The CustomEvent arguments. For configuration
        * handlers, args[0] will equal the newly applied value for the property.
        * @param {Object} obj The scope object. For configuration handlers,
        * this will usually equal the owner.
        */
        configUnderlay: function (type, args, obj) {

            var bMacGecko = (this.platform == "mac" && UA.gecko),
                sUnderlay = args[0].toLowerCase(),
                oUnderlay = this.underlay,
                oElement = this.element;

            function createUnderlay() {
                var bNew = false;
                if (!oUnderlay) { // create if not already in DOM

                    if (!m_oUnderlayTemplate) {
                        m_oUnderlayTemplate = document.createElement("div");
                        m_oUnderlayTemplate.className = "underlay";
                    }

                    oUnderlay = m_oUnderlayTemplate.cloneNode(false);
                    this.element.appendChild(oUnderlay);

                    this.underlay = oUnderlay;

                    if (bIEQuirks) {
                        this.sizeUnderlay();
                        this.cfg.subscribeToConfigEvent("width", this.sizeUnderlay);
                        this.cfg.subscribeToConfigEvent("height", this.sizeUnderlay);

                        this.changeContentEvent.subscribe(this.sizeUnderlay);
                        YAHOO.widget.Module.textResizeEvent.subscribe(this.sizeUnderlay, this, true);
                    }

                    if (UA.webkit && UA.webkit < 420) {
                        this.changeContentEvent.subscribe(this.forceUnderlayRedraw);
                    }

                    bNew = true;
                }
            }

            function onBeforeShow() {
                var bNew = createUnderlay.call(this);
                if (!bNew && bIEQuirks) {
                    this.sizeUnderlay();
                }
                this._underlayDeferred = false;
                this.beforeShowEvent.unsubscribe(onBeforeShow);
            }

            function destroyUnderlay() {
                if (this._underlayDeferred) {
                    this.beforeShowEvent.unsubscribe(onBeforeShow);
                    this._underlayDeferred = false;
                }

                if (oUnderlay) {
                    this.cfg.unsubscribeFromConfigEvent("width", this.sizeUnderlay);
                    this.cfg.unsubscribeFromConfigEvent("height",this.sizeUnderlay);
                    this.changeContentEvent.unsubscribe(this.sizeUnderlay);
                    this.changeContentEvent.unsubscribe(this.forceUnderlayRedraw);
                    YAHOO.widget.Module.textResizeEvent.unsubscribe(this.sizeUnderlay, this, true);

                    this.element.removeChild(oUnderlay);

                    this.underlay = null;
                }
            }

            switch (sUnderlay) {
                case "shadow":
                    Dom.removeClass(oElement, "matte");
                    Dom.addClass(oElement, "shadow");
                    break;
                case "matte":
                    if (!bMacGecko) {
                        destroyUnderlay.call(this);
                    }
                    Dom.removeClass(oElement, "shadow");
                    Dom.addClass(oElement, "matte");
                    break;
                default:
                    if (!bMacGecko) {
                        destroyUnderlay.call(this);
                    }
                    Dom.removeClass(oElement, "shadow");
                    Dom.removeClass(oElement, "matte");
                    break;
            }

            if ((sUnderlay == "shadow") || (bMacGecko && !oUnderlay)) {
                if (this.cfg.getProperty("visible")) {
                    var bNew = createUnderlay.call(this);
                    if (!bNew && bIEQuirks) {
                        this.sizeUnderlay();
                    }
                } else {
                    if (!this._underlayDeferred) {
                        this.beforeShowEvent.subscribe(onBeforeShow);
                        this._underlayDeferred = true;
                    }
                }
            }
        },

        /**
        * The default event handler fired when the "modal" property is
        * changed. This handler subscribes or unsubscribes to the show and hide
        * events to handle the display or hide of the modality mask.
        * @method configModal
        * @param {String} type The CustomEvent type (usually the property name)
        * @param {Object[]} args The CustomEvent arguments. For configuration
        * handlers, args[0] will equal the newly applied value for the property.
        * @param {Object} obj The scope object. For configuration handlers,
        * this will usually equal the owner.
        */
        configModal: function (type, args, obj) {

            var modal = args[0];
            if (modal) {
                if (!this._hasModalityEventListeners) {

                    this.subscribe("beforeShow", this.buildMask);
                    this.subscribe("beforeShow", this.bringToTop);
                    this.subscribe("beforeShow", this.showMask);
                    this.subscribe("hide", this.hideMask);

                    Overlay.windowResizeEvent.subscribe(this.sizeMask,
                        this, true);

                    this._hasModalityEventListeners = true;
                }
            } else {
                if (this._hasModalityEventListeners) {

                    if (this.cfg.getProperty("visible")) {
                        this.hideMask();
                        this.removeMask();
                    }

                    this.unsubscribe("beforeShow", this.buildMask);
                    this.unsubscribe("beforeShow", this.bringToTop);
                    this.unsubscribe("beforeShow", this.showMask);
                    this.unsubscribe("hide", this.hideMask);

                    Overlay.windowResizeEvent.unsubscribe(this.sizeMask, this);

                    this._hasModalityEventListeners = false;
                }
            }
        },

        /**
        * Removes the modality mask.
        * @method removeMask
        */
        removeMask: function () {

            var oMask = this.mask,
                oParentNode;

            if (oMask) {
                /*
                    Hide the mask before destroying it to ensure that DOM
                    event handlers on focusable elements get removed.
                */
                this.hideMask();

                oParentNode = oMask.parentNode;
                if (oParentNode) {
                    oParentNode.removeChild(oMask);
                }

                this.mask = null;
            }
        },

        /**
        * The default event handler fired when the "keylisteners" property
        * is changed.
        * @method configKeyListeners
        * @param {String} type The CustomEvent type (usually the property name)
        * @param {Object[]} args The CustomEvent arguments. For configuration
        * handlers, args[0] will equal the newly applied value for the property.
        * @param {Object} obj The scope object. For configuration handlers,
        * this will usually equal the owner.
        */
        configKeyListeners: function (type, args, obj) {

            var listeners = args[0],
                listener,
                nListeners,
                i;

            if (listeners) {

                if (listeners instanceof Array) {

                    nListeners = listeners.length;

                    for (i = 0; i < nListeners; i++) {

                        listener = listeners[i];

                        if (!Config.alreadySubscribed(this.showEvent,
                            listener.enable, listener)) {

                            this.showEvent.subscribe(listener.enable,
                                listener, true);

                        }

                        if (!Config.alreadySubscribed(this.hideEvent,
                            listener.disable, listener)) {

                            this.hideEvent.subscribe(listener.disable,
                                listener, true);

                            this.destroyEvent.subscribe(listener.disable,
                                listener, true);
                        }
                    }

                } else {

                    if (!Config.alreadySubscribed(this.showEvent,
                        listeners.enable, listeners)) {

                        this.showEvent.subscribe(listeners.enable,
                            listeners, true);
                    }

                    if (!Config.alreadySubscribed(this.hideEvent,
                        listeners.disable, listeners)) {

                        this.hideEvent.subscribe(listeners.disable,
                            listeners, true);

                        this.destroyEvent.subscribe(listeners.disable,
                            listeners, true);

                    }

                }

            }

        },

        /**
        * The default handler for the "strings" property
        * @method configStrings
        */
        configStrings : function(type, args, obj) {
            var val = Lang.merge(DEFAULT_CONFIG.STRINGS.value, args[0]);
            this.cfg.setProperty(DEFAULT_CONFIG.STRINGS.key, val, true);
        },

        /**
        * The default event handler fired when the "height" property is changed.
        * @method configHeight
        * @param {String} type The CustomEvent type (usually the property name)
        * @param {Object[]} args The CustomEvent arguments. For configuration
        * handlers, args[0] will equal the newly applied value for the property.
        * @param {Object} obj The scope object. For configuration handlers,
        * this will usually equal the owner.
        */
        configHeight: function (type, args, obj) {
            var height = args[0],
                el = this.innerElement;

            Dom.setStyle(el, "height", height);
            this.cfg.refireEvent("iframe");
        },

        /**
         * The default custom event handler executed when the Panel's height is changed,
         * if the autofillheight property has been set.
         *
         * @method _autoFillOnHeightChange
         * @protected
         * @param {String} type The event type
         * @param {Array} args The array of arguments passed to event subscribers
         * @param {HTMLElement} el The header, body or footer element which is to be resized to fill
         * out the containers height
         */
        _autoFillOnHeightChange : function(type, args, el) {
            Panel.superclass._autoFillOnHeightChange.apply(this, arguments);
            if (bIEQuirks) {
                var panel = this;
                setTimeout(function() {
                    panel.sizeUnderlay();
                },0);
            }
        },

        /**
        * The default event handler fired when the "width" property is changed.
        * @method configWidth
        * @param {String} type The CustomEvent type (usually the property name)
        * @param {Object[]} args The CustomEvent arguments. For configuration
        * handlers, args[0] will equal the newly applied value for the property.
        * @param {Object} obj The scope object. For configuration handlers,
        * this will usually equal the owner.
        */
        configWidth: function (type, args, obj) {

            var width = args[0],
                el = this.innerElement;

            Dom.setStyle(el, "width", width);
            this.cfg.refireEvent("iframe");

        },

        /**
        * The default event handler fired when the "zIndex" property is changed.
        * @method configzIndex
        * @param {String} type The CustomEvent type (usually the property name)
        * @param {Object[]} args The CustomEvent arguments. For configuration
        * handlers, args[0] will equal the newly applied value for the property.
        * @param {Object} obj The scope object. For configuration handlers,
        * this will usually equal the owner.
        */
        configzIndex: function (type, args, obj) {
            Panel.superclass.configzIndex.call(this, type, args, obj);

            if (this.mask || this.cfg.getProperty("modal") === true) {
                var panelZ = Dom.getStyle(this.element, "zIndex");
                if (!panelZ || isNaN(panelZ)) {
                    panelZ = 0;
                }

                if (panelZ === 0) {
                    // Recursive call to configzindex (which should be stopped
                    // from going further because panelZ should no longer === 0)
                    this.cfg.setProperty("zIndex", 1);
                } else {
                    this.stackMask();
                }
            }
        },

        // END BUILT-IN PROPERTY EVENT HANDLERS //
        /**
        * Builds the wrapping container around the Panel that is used for
        * positioning the shadow and matte underlays. The container element is
        * assigned to a  local instance variable called container, and the
        * element is reinserted inside of it.
        * @method buildWrapper
        */
        buildWrapper: function () {

            var elementParent = this.element.parentNode,
                originalElement = this.element,
                wrapper = document.createElement("div");

            wrapper.className = Panel.CSS_PANEL_CONTAINER;
            wrapper.id = originalElement.id + "_c";

            if (elementParent) {
                elementParent.insertBefore(wrapper, originalElement);
            }

            wrapper.appendChild(originalElement);

            this.element = wrapper;
            this.innerElement = originalElement;

            Dom.setStyle(this.innerElement, "visibility", "inherit");
        },

        /**
        * Adjusts the size of the shadow based on the size of the element.
        * @method sizeUnderlay
        */
        sizeUnderlay: function () {
            var oUnderlay = this.underlay,
                oElement;

            if (oUnderlay) {
                oElement = this.element;
                oUnderlay.style.width = oElement.offsetWidth + "px";
                oUnderlay.style.height = oElement.offsetHeight + "px";
            }
        },

        /**
        * Registers the Panel's header for drag & drop capability.
        * @method registerDragDrop
        */
        registerDragDrop: function () {

            var me = this;

            if (this.header) {

                if (!Util.DD) {
                    return;
                }

                var bDragOnly = (this.cfg.getProperty("dragonly") === true);

                /**
                 * The YAHOO.util.DD instance, used to implement the draggable header for the panel if draggable is enabled
                 *
                 * @property dd
                 * @type YAHOO.util.DD
                 */
                this.dd = new Util.DD(this.element.id, this.id, {dragOnly: bDragOnly});

                if (!this.header.id) {
                    this.header.id = this.id + "_h";
                }

                this.dd.startDrag = function () {

                    var offsetHeight,
                        offsetWidth,
                        viewPortWidth,
                        viewPortHeight,
                        scrollX,
                        scrollY;

                    if (YAHOO.env.ua.ie == 6) {
                        Dom.addClass(me.element,"drag");
                    }

                    if (me.cfg.getProperty("constraintoviewport")) {

                        var nViewportOffset = Overlay.VIEWPORT_OFFSET;

                        offsetHeight = me.element.offsetHeight;
                        offsetWidth = me.element.offsetWidth;

                        viewPortWidth = Dom.getViewportWidth();
                        viewPortHeight = Dom.getViewportHeight();

                        scrollX = Dom.getDocumentScrollLeft();
                        scrollY = Dom.getDocumentScrollTop();

                        if (offsetHeight + nViewportOffset < viewPortHeight) {
                            this.minY = scrollY + nViewportOffset;
                            this.maxY = scrollY + viewPortHeight - offsetHeight - nViewportOffset;
                        } else {
                            this.minY = scrollY + nViewportOffset;
                            this.maxY = scrollY + nViewportOffset;
                        }

                        if (offsetWidth + nViewportOffset < viewPortWidth) {
                            this.minX = scrollX + nViewportOffset;
                            this.maxX = scrollX + viewPortWidth - offsetWidth - nViewportOffset;
                        } else {
                            this.minX = scrollX + nViewportOffset;
                            this.maxX = scrollX + nViewportOffset;
                        }

                        this.constrainX = true;
                        this.constrainY = true;
                    } else {
                        this.constrainX = false;
                        this.constrainY = false;
                    }

                    me.dragEvent.fire("startDrag", arguments);
                };

                this.dd.onDrag = function () {
                    me.syncPosition();
                    me.cfg.refireEvent("iframe");
                    if (this.platform == "mac" && YAHOO.env.ua.gecko) {
                        this.showMacGeckoScrollbars();
                    }

                    me.dragEvent.fire("onDrag", arguments);
                };

                this.dd.endDrag = function () {

                    if (YAHOO.env.ua.ie == 6) {
                        Dom.removeClass(me.element,"drag");
                    }

                    me.dragEvent.fire("endDrag", arguments);
                    me.moveEvent.fire(me.cfg.getProperty("xy"));

                };

                this.dd.setHandleElId(this.header.id);
                this.dd.addInvalidHandleType("INPUT");
                this.dd.addInvalidHandleType("SELECT");
                this.dd.addInvalidHandleType("TEXTAREA");
            }
        },

        /**
        * Builds the mask that is laid over the document when the Panel is
        * configured to be modal.
        * @method buildMask
        */
        buildMask: function () {
            var oMask = this.mask;
            if (!oMask) {
                if (!m_oMaskTemplate) {
                    m_oMaskTemplate = document.createElement("div");
                    m_oMaskTemplate.className = "mask";
                    m_oMaskTemplate.innerHTML = "&#160;";
                }
                oMask = m_oMaskTemplate.cloneNode(true);
                oMask.id = this.id + "_mask";

                document.body.insertBefore(oMask, document.body.firstChild);

                this.mask = oMask;

                if (YAHOO.env.ua.gecko && this.platform == "mac") {
                    Dom.addClass(this.mask, "block-scrollbars");
                }

                // Stack mask based on the element zindex
                this.stackMask();
            }
        },

        /**
        * Hides the modality mask.
        * @method hideMask
        */
        hideMask: function () {
            if (this.cfg.getProperty("modal") && this.mask) {
                this.mask.style.display = "none";
                Dom.removeClass(document.body, "masked");
                this.hideMaskEvent.fire();
            }
        },

        /**
        * Shows the modality mask.
        * @method showMask
        */
        showMask: function () {
            if (this.cfg.getProperty("modal") && this.mask) {
                Dom.addClass(document.body, "masked");
                this.sizeMask();
                this.mask.style.display = "block";
                this.showMaskEvent.fire();
            }
        },

        /**
        * Sets the size of the modality mask to cover the entire scrollable
        * area of the document
        * @method sizeMask
        */
        sizeMask: function () {
            if (this.mask) {

                // Shrink mask first, so it doesn't affect the document size.
                var mask = this.mask,
                    viewWidth = Dom.getViewportWidth(),
                    viewHeight = Dom.getViewportHeight();

                if (mask.offsetHeight > viewHeight) {
                    mask.style.height = viewHeight + "px";
                }

                if (mask.offsetWidth > viewWidth) {
                    mask.style.width = viewWidth + "px";
                }

                // Then size it to the document
                mask.style.height = Dom.getDocumentHeight() + "px";
                mask.style.width = Dom.getDocumentWidth() + "px";
            }
        },

        /**
         * Sets the zindex of the mask, if it exists, based on the zindex of
         * the Panel element. The zindex of the mask is set to be one less
         * than the Panel element's zindex.
         *
         * <p>NOTE: This method will not bump up the zindex of the Panel
         * to ensure that the mask has a non-negative zindex. If you require the
         * mask zindex to be 0 or higher, the zindex of the Panel
         * should be set to a value higher than 0, before this method is called.
         * </p>
         * @method stackMask
         */
        stackMask: function() {
            if (this.mask) {
                var panelZ = Dom.getStyle(this.element, "zIndex");
                if (!YAHOO.lang.isUndefined(panelZ) && !isNaN(panelZ)) {
                    Dom.setStyle(this.mask, "zIndex", panelZ - 1);
                }
            }
        },

        /**
        * Renders the Panel by inserting the elements that are not already in
        * the main Panel into their correct places. Optionally appends the
        * Panel to the specified node prior to the render's execution. NOTE:
        * For Panels without existing markup, the appendToNode argument is
        * REQUIRED. If this argument is ommitted and the current element is
        * not present in the document, the function will return false,
        * indicating that the render was a failure.
        * @method render
        * @param {String} appendToNode The element id to which the Module
        * should be appended to prior to rendering <em>OR</em>
        * @param {HTMLElement} appendToNode The element to which the Module
        * should be appended to prior to rendering
        * @return {boolean} Success or failure of the render
        */
        render: function (appendToNode) {
            return Panel.superclass.render.call(this, appendToNode, this.innerElement);
        },

        /**
         * Renders the currently set header into it's proper position under the
         * module element. If the module element is not provided, "this.innerElement"
         * is used.
         *
         * @method _renderHeader
         * @protected
         * @param {HTMLElement} moduleElement Optional. A reference to the module element
         */
        _renderHeader: function(moduleElement){
            moduleElement = moduleElement || this.innerElement;
			Panel.superclass._renderHeader.call(this, moduleElement);
        },

        /**
         * Renders the currently set body into it's proper position under the
         * module element. If the module element is not provided, "this.innerElement"
         * is used.
         *
         * @method _renderBody
         * @protected
         * @param {HTMLElement} moduleElement Optional. A reference to the module element.
         */
        _renderBody: function(moduleElement){
            moduleElement = moduleElement || this.innerElement;
            Panel.superclass._renderBody.call(this, moduleElement);
        },

        /**
         * Renders the currently set footer into it's proper position under the
         * module element. If the module element is not provided, "this.innerElement"
         * is used.
         *
         * @method _renderFooter
         * @protected
         * @param {HTMLElement} moduleElement Optional. A reference to the module element
         */
        _renderFooter: function(moduleElement){
            moduleElement = moduleElement || this.innerElement;
            Panel.superclass._renderFooter.call(this, moduleElement);
        },

        /**
        * Removes the Panel element from the DOM and sets all child elements
        * to null.
        * @method destroy
        */
        destroy: function () {
            Overlay.windowResizeEvent.unsubscribe(this.sizeMask, this);
            this.removeMask();
            if (this.close) {
                Event.purgeElement(this.close);
            }
            Panel.superclass.destroy.call(this);
        },

        /**
         * Forces the underlay element to be repainted through the application/removal
         * of a yui-force-redraw class to the underlay element.
         *
         * @method forceUnderlayRedraw
         */
        forceUnderlayRedraw : function () {
            var u = this.underlay;
            Dom.addClass(u, "yui-force-redraw");
            setTimeout(function(){Dom.removeClass(u, "yui-force-redraw");}, 0);
        },

        /**
        * Returns a String representation of the object.
        * @method toString
        * @return {String} The string representation of the Panel.
        */
        toString: function () {
            return "Panel " + this.id;
        }

    });

}());
(function () {

    /**
    * <p>
    * Dialog is an implementation of Panel that can be used to submit form
    * data.
    * </p>
    * <p>
    * Built-in functionality for buttons with event handlers is included.
    * If the optional YUI Button dependancy is included on the page, the buttons
    * created will be instances of YAHOO.widget.Button, otherwise regular HTML buttons
    * will be created.
    * </p>
    * <p>
    * Forms can be processed in 3 ways -- via an asynchronous Connection utility call,
    * a simple form POST or GET, or manually. The YUI Connection utility should be
    * included if you're using the default "async" postmethod, but is not required if
    * you're using any of the other postmethod values.
    * </p>
    * @namespace YAHOO.widget
    * @class Dialog
    * @extends YAHOO.widget.Panel
    * @constructor
    * @param {String} el The element ID representing the Dialog <em>OR</em>
    * @param {HTMLElement} el The element representing the Dialog
    * @param {Object} userConfig The configuration object literal containing
    * the configuration that should be set for this Dialog. See configuration
    * documentation for more details.
    */
    YAHOO.widget.Dialog = function (el, userConfig) {
        YAHOO.widget.Dialog.superclass.constructor.call(this, el, userConfig);
    };

    var Event = YAHOO.util.Event,
        CustomEvent = YAHOO.util.CustomEvent,
        Dom = YAHOO.util.Dom,
        Dialog = YAHOO.widget.Dialog,
        Lang = YAHOO.lang,

        /**
         * Constant representing the name of the Dialog's events
         * @property EVENT_TYPES
         * @private
         * @final
         * @type Object
         */
        EVENT_TYPES = {
            "BEFORE_SUBMIT": "beforeSubmit",
            "SUBMIT": "submit",
            "MANUAL_SUBMIT": "manualSubmit",
            "ASYNC_SUBMIT": "asyncSubmit",
            "FORM_SUBMIT": "formSubmit",
            "CANCEL": "cancel"
        },

        /**
        * Constant representing the Dialog's configuration properties
        * @property DEFAULT_CONFIG
        * @private
        * @final
        * @type Object
        */
        DEFAULT_CONFIG = {

            "POST_METHOD": {
                key: "postmethod",
                value: "async"
            },

            "POST_DATA" : {
                key: "postdata",
                value: null
            },

            "BUTTONS": {
                key: "buttons",
                value: "none",
                supercedes: ["visible"]
            },

            "HIDEAFTERSUBMIT" : {
                key: "hideaftersubmit",
                value: true
            }

        };

    /**
    * Constant representing the default CSS class used for a Dialog
    * @property YAHOO.widget.Dialog.CSS_DIALOG
    * @static
    * @final
    * @type String
    */
    Dialog.CSS_DIALOG = "yui-dialog";

    function removeButtonEventHandlers() {

        var aButtons = this._aButtons,
            nButtons,
            oButton,
            i;

        if (Lang.isArray(aButtons)) {
            nButtons = aButtons.length;

            if (nButtons > 0) {
                i = nButtons - 1;
                do {
                    oButton = aButtons[i];

                    if (YAHOO.widget.Button && oButton instanceof YAHOO.widget.Button) {
                        oButton.destroy();
                    }
                    else if (oButton.tagName.toUpperCase() == "BUTTON") {
                        Event.purgeElement(oButton);
                        Event.purgeElement(oButton, false);
                    }
                }
                while (i--);
            }
        }
    }

    YAHOO.extend(Dialog, YAHOO.widget.Panel, {

        /**
        * @property form
        * @description Object reference to the Dialog's
        * <code>&#60;form&#62;</code> element.
        * @default null
        * @type <a href="http://www.w3.org/TR/2000/WD-DOM-Level-1-20000929/
        * level-one-html.html#ID-40002357">HTMLFormElement</a>
        */
        form: null,

        /**
        * Initializes the class's configurable properties which can be changed
        * using the Dialog's Config object (cfg).
        * @method initDefaultConfig
        */
        initDefaultConfig: function () {
            Dialog.superclass.initDefaultConfig.call(this);

            /**
            * The internally maintained callback object for use with the
            * Connection utility. The format of the callback object is
            * similar to Connection Manager's callback object and is
            * simply passed through to Connection Manager when the async
            * request is made.
            * @property callback
            * @type Object
            */
            this.callback = {

                /**
                * The function to execute upon success of the
                * Connection submission (when the form does not
                * contain a file input element).
                *
                * @property callback.success
                * @type Function
                */
                success: null,

                /**
                * The function to execute upon failure of the
                * Connection submission
                * @property callback.failure
                * @type Function
                */
                failure: null,

                /**
                *<p>
                * The function to execute upon success of the
                * Connection submission, when the form contains
                * a file input element.
                * </p>
                * <p>
                * <em>NOTE:</em> Connection manager will not
                * invoke the success or failure handlers for the file
                * upload use case. This will be the only callback
                * handler invoked.
                * </p>
                * <p>
                * For more information, see the <a href="http://developer.yahoo.com/yui/connection/#file">
                * Connection Manager documenation on file uploads</a>.
                * </p>
                * @property callback.upload
                * @type Function
                */

                /**
                * The arbitraty argument or arguments to pass to the Connection
                * callback functions
                * @property callback.argument
                * @type Object
                */
                argument: null

            };

            // Add form dialog config properties //
            /**
            * The method to use for posting the Dialog's form. Possible values
            * are "async", "form", and "manual".
            * @config postmethod
            * @type String
            * @default async
            */
            this.cfg.addProperty(DEFAULT_CONFIG.POST_METHOD.key, {
                handler: this.configPostMethod,
                value: DEFAULT_CONFIG.POST_METHOD.value,
                validator: function (val) {
                    if (val != "form" && val != "async" && val != "none" &&
                        val != "manual") {
                        return false;
                    } else {
                        return true;
                    }
                }
            });

            /**
            * Any additional post data which needs to be sent when using the
            * <a href="#config_postmethod">async</a> postmethod for dialog POST submissions.
            * The format for the post data string is defined by Connection Manager's
            * <a href="YAHOO.util.Connect.html#method_asyncRequest">asyncRequest</a>
            * method.
            * @config postdata
            * @type String
            * @default null
            */
            this.cfg.addProperty(DEFAULT_CONFIG.POST_DATA.key, {
                value: DEFAULT_CONFIG.POST_DATA.value
            });

            /**
            * This property is used to configure whether or not the
            * dialog should be automatically hidden after submit.
            *
            * @config hideaftersubmit
            * @type Boolean
            * @default true
            */
            this.cfg.addProperty(DEFAULT_CONFIG.HIDEAFTERSUBMIT.key, {
                value: DEFAULT_CONFIG.HIDEAFTERSUBMIT.value
            });

            /**
            * Array of object literals, each containing a set of properties
            * defining a button to be appended into the Dialog's footer.
            *
            * <p>Each button object in the buttons array can have three properties:</p>
            * <dl>
            *    <dt>text:</dt>
            *    <dd>
            *       The text that will display on the face of the button. The text can
            *       include HTML, as long as it is compliant with HTML Button specifications.
            *    </dd>
            *    <dt>handler:</dt>
            *    <dd>Can be either:
            *    <ol>
            *       <li>A reference to a function that should fire when the
            *       button is clicked.  (In this case scope of this function is
            *       always its Dialog instance.)</li>
            *
            *       <li>An object literal representing the code to be
            *       executed when the button is clicked.
            *
            *       <p>Format:</p>
            *
            *       <p>
            *       <code>{
            *       <br>
            *       <strong>fn:</strong> Function, &#47;&#47;
            *       The handler to call when  the event fires.
            *       <br>
            *       <strong>obj:</strong> Object, &#47;&#47;
            *       An  object to pass back to the handler.
            *       <br>
            *       <strong>scope:</strong> Object &#47;&#47;
            *       The object to use for the scope of the handler.
            *       <br>
            *       }</code>
            *       </p>
            *       </li>
            *     </ol>
            *     </dd>
            *     <dt>isDefault:</dt>
            *     <dd>
            *        An optional boolean value that specifies that a button
            *        should be highlighted and focused by default.
            *     </dd>
            * </dl>
            *
            * <em>NOTE:</em>If the YUI Button Widget is included on the page,
            * the buttons created will be instances of YAHOO.widget.Button.
            * Otherwise, HTML Buttons (<code>&#60;BUTTON&#62;</code>) will be
            * created.
            *
            * @config buttons
            * @type {Array|String}
            * @default "none"
            */
            this.cfg.addProperty(DEFAULT_CONFIG.BUTTONS.key, {
                handler: this.configButtons,
                value: DEFAULT_CONFIG.BUTTONS.value,
                supercedes : DEFAULT_CONFIG.BUTTONS.supercedes
            });

        },

        /**
        * Initializes the custom events for Dialog which are fired
        * automatically at appropriate times by the Dialog class.
        * @method initEvents
        */
        initEvents: function () {
            Dialog.superclass.initEvents.call(this);

            var SIGNATURE = CustomEvent.LIST;

            /**
            * CustomEvent fired prior to submission
            * @event beforeSubmitEvent
            */
            this.beforeSubmitEvent =
                this.createEvent(EVENT_TYPES.BEFORE_SUBMIT);
            this.beforeSubmitEvent.signature = SIGNATURE;

            /**
            * CustomEvent fired after submission
            * @event submitEvent
            */
            this.submitEvent = this.createEvent(EVENT_TYPES.SUBMIT);
            this.submitEvent.signature = SIGNATURE;

            /**
            * CustomEvent fired for manual submission, before the generic submit event is fired
            * @event manualSubmitEvent
            */
            this.manualSubmitEvent =
                this.createEvent(EVENT_TYPES.MANUAL_SUBMIT);
            this.manualSubmitEvent.signature = SIGNATURE;

            /**
            * CustomEvent fired after asynchronous submission, before the generic submit event is fired
            *
            * @event asyncSubmitEvent
            * @param {Object} conn The connection object, returned by YAHOO.util.Connect.asyncRequest
            */
            this.asyncSubmitEvent = this.createEvent(EVENT_TYPES.ASYNC_SUBMIT);
            this.asyncSubmitEvent.signature = SIGNATURE;

            /**
            * CustomEvent fired after form-based submission, before the generic submit event is fired
            * @event formSubmitEvent
            */
            this.formSubmitEvent = this.createEvent(EVENT_TYPES.FORM_SUBMIT);
            this.formSubmitEvent.signature = SIGNATURE;

            /**
            * CustomEvent fired after cancel
            * @event cancelEvent
            */
            this.cancelEvent = this.createEvent(EVENT_TYPES.CANCEL);
            this.cancelEvent.signature = SIGNATURE;

        },

        /**
        * The Dialog initialization method, which is executed for Dialog and
        * all of its subclasses. This method is automatically called by the
        * constructor, and  sets up all DOM references for pre-existing markup,
        * and creates required markup if it is not already present.
        *
        * @method init
        * @param {String} el The element ID representing the Dialog <em>OR</em>
        * @param {HTMLElement} el The element representing the Dialog
        * @param {Object} userConfig The configuration object literal
        * containing the configuration that should be set for this Dialog.
        * See configuration documentation for more details.
        */
        init: function (el, userConfig) {

            /*
                 Note that we don't pass the user config in here yet because
                 we only want it executed once, at the lowest subclass level
            */

            Dialog.superclass.init.call(this, el/*, userConfig*/);

            this.beforeInitEvent.fire(Dialog);

            Dom.addClass(this.element, Dialog.CSS_DIALOG);

            this.cfg.setProperty("visible", false);

            if (userConfig) {
                this.cfg.applyConfig(userConfig, true);
            }

            this.showEvent.subscribe(this.focusFirst, this, true);
            this.beforeHideEvent.subscribe(this.blurButtons, this, true);

            this.subscribe("changeBody", this.registerForm);

            this.initEvent.fire(Dialog);
        },

        /**
        * Submits the Dialog's form depending on the value of the
        * "postmethod" configuration property.  <strong>Please note:
        * </strong> As of version 2.3 this method will automatically handle
        * asyncronous file uploads should the Dialog instance's form contain
        * <code>&#60;input type="file"&#62;</code> elements.  If a Dialog
        * instance will be handling asyncronous file uploads, its
        * <code>callback</code> property will need to be setup with a
        * <code>upload</code> handler rather than the standard
        * <code>success</code> and, or <code>failure</code> handlers.  For more
        * information, see the <a href="http://developer.yahoo.com/yui/
        * connection/#file">Connection Manager documenation on file uploads</a>.
        * @method doSubmit
        */
        doSubmit: function () {

            var Connect = YAHOO.util.Connect,
                oForm = this.form,
                bUseFileUpload = false,
                bUseSecureFileUpload = false,
                aElements,
                nElements,
                i,
                formAttrs;

            switch (this.cfg.getProperty("postmethod")) {

                case "async":
                    aElements = oForm.elements;
                    nElements = aElements.length;

                    if (nElements > 0) {
                        i = nElements - 1;
                        do {
                            if (aElements[i].type == "file") {
                                bUseFileUpload = true;
                                break;
                            }
                        }
                        while(i--);
                    }

                    if (bUseFileUpload && YAHOO.env.ua.ie && this.isSecure) {
                        bUseSecureFileUpload = true;
                    }

                    formAttrs = this._getFormAttributes(oForm);

                    Connect.setForm(oForm, bUseFileUpload, bUseSecureFileUpload);

                    var postData = this.cfg.getProperty("postdata");
                    var c = Connect.asyncRequest(formAttrs.method, formAttrs.action, this.callback, postData);

                    this.asyncSubmitEvent.fire(c);

                    break;

                case "form":
                    oForm.submit();
                    this.formSubmitEvent.fire();
                    break;

                case "none":
                case "manual":
                    this.manualSubmitEvent.fire();
                    break;
            }
        },

        /**
         * Retrieves important attributes (currently method and action) from
         * the form element, accounting for any elements which may have the same name
         * as the attributes. Defaults to "POST" and "" for method and action respectively
         * if the attribute cannot be retrieved.
         *
         * @method _getFormAttributes
         * @protected
         * @param {HTMLFormElement} oForm The HTML Form element from which to retrieve the attributes
         * @return {Object} Object literal, with method and action String properties.
         */
        _getFormAttributes : function(oForm){
            var attrs = {
                method : null,
                action : null
            };

            if (oForm) {
                if (oForm.getAttributeNode) {
                    var action = oForm.getAttributeNode("action");
                    var method = oForm.getAttributeNode("method");

                    if (action) {
                        attrs.action = action.value;
                    }

                    if (method) {
                        attrs.method = method.value;
                    }

                } else {
                    attrs.action = oForm.getAttribute("action");
                    attrs.method = oForm.getAttribute("method");
                }
            }

            attrs.method = (Lang.isString(attrs.method) ? attrs.method : "POST").toUpperCase();
            attrs.action = Lang.isString(attrs.action) ? attrs.action : "";

            return attrs;
        },

        /**
        * Prepares the Dialog's internal FORM object, creating one if one is
        * not currently present.
        * @method registerForm
        */
        registerForm: function() {

            var form = this.element.getElementsByTagName("form")[0];

            if (this.form) {
                if (this.form == form && Dom.isAncestor(this.element, this.form)) {
                    return;
                } else {
                    Event.purgeElement(this.form);
                    this.form = null;
                }
            }

            if (!form) {
                form = document.createElement("form");
                form.name = "frm_" + this.id;
                this.body.appendChild(form);
            }

            if (form) {
                this.form = form;
                Event.on(form, "submit", this._submitHandler, this, true);
            }
        },

        /**
         * Internal handler for the form submit event
         *
         * @method _submitHandler
         * @protected
         * @param {DOMEvent} e The DOM Event object
         */
        _submitHandler : function(e) {
            Event.stopEvent(e);
            this.submit();
            this.form.blur();
        },

        /**
         * Sets up a tab, shift-tab loop between the first and last elements
         * provided. NOTE: Sets up the preventBackTab and preventTabOut KeyListener
         * instance properties, which are reset everytime this method is invoked.
         *
         * @method setTabLoop
         * @param {HTMLElement} firstElement
         * @param {HTMLElement} lastElement
         *
         */
        setTabLoop : function(firstElement, lastElement) {

            firstElement = firstElement || this.firstButton;
            lastElement = this.lastButton || lastElement;

            Dialog.superclass.setTabLoop.call(this, firstElement, lastElement);
        },

        /**
         * Configures instance properties, pointing to the
         * first and last focusable elements in the Dialog's form.
         *
         * @method setFirstLastFocusable
         */
        setFirstLastFocusable : function() {

            Dialog.superclass.setFirstLastFocusable.call(this);

            var i, l, el, elements = this.focusableElements;

            this.firstFormElement = null;
            this.lastFormElement = null;

            if (this.form && elements && elements.length > 0) {
                l = elements.length;

                for (i = 0; i < l; ++i) {
                    el = elements[i];
                    if (this.form === el.form) {
                        this.firstFormElement = el;
                        break;
                    }
                }

                for (i = l-1; i >= 0; --i) {
                    el = elements[i];
                    if (this.form === el.form) {
                        this.lastFormElement = el;
                        break;
                    }
                }
            }
        },

        // BEGIN BUILT-IN PROPERTY EVENT HANDLERS //
        /**
        * The default event handler fired when the "close" property is
        * changed. The method controls the appending or hiding of the close
        * icon at the top right of the Dialog.
        * @method configClose
        * @param {String} type The CustomEvent type (usually the property name)
        * @param {Object[]} args The CustomEvent arguments. For
        * configuration handlers, args[0] will equal the newly applied value
        * for the property.
        * @param {Object} obj The scope object. For configuration handlers,
        * this will usually equal the owner.
        */
        configClose: function (type, args, obj) {
            Dialog.superclass.configClose.apply(this, arguments);
        },

        /**
         * Event handler for the close icon
         *
         * @method _doClose
         * @protected
         *
         * @param {DOMEvent} e
         */
         _doClose : function(e) {
            Event.preventDefault(e);
            this.cancel();
        },

        /**
        * The default event handler for the "buttons" configuration property
        * @method configButtons
        * @param {String} type The CustomEvent type (usually the property name)
        * @param {Object[]} args The CustomEvent arguments. For configuration
        * handlers, args[0] will equal the newly applied value for the property.
        * @param {Object} obj The scope object. For configuration handlers,
        * this will usually equal the owner.
        */
        configButtons: function (type, args, obj) {

            var Button = YAHOO.widget.Button,
                aButtons = args[0],
                oInnerElement = this.innerElement,
                oButton,
                oButtonEl,
                oYUIButton,
                nButtons,
                oSpan,
                oFooter,
                i;

            removeButtonEventHandlers.call(this);

            this._aButtons = null;

            if (Lang.isArray(aButtons)) {

                oSpan = document.createElement("span");
                oSpan.className = "button-group";
                nButtons = aButtons.length;

                this._aButtons = [];
                this.defaultHtmlButton = null;

                for (i = 0; i < nButtons; i++) {
                    oButton = aButtons[i];

                    if (Button) {
                        oYUIButton = new Button({ label: oButton.text});
                        oYUIButton.appendTo(oSpan);

                        oButtonEl = oYUIButton.get("element");

                        if (oButton.isDefault) {
                            oYUIButton.addClass("default");
                            this.defaultHtmlButton = oButtonEl;
                        }

                        if (Lang.isFunction(oButton.handler)) {

                            oYUIButton.set("onclick", {
                                fn: oButton.handler,
                                obj: this,
                                scope: this
                            });

                        } else if (Lang.isObject(oButton.handler) && Lang.isFunction(oButton.handler.fn)) {

                            oYUIButton.set("onclick", {
                                fn: oButton.handler.fn,
                                obj: ((!Lang.isUndefined(oButton.handler.obj)) ? oButton.handler.obj : this),
                                scope: (oButton.handler.scope || this)
                            });

                        }

                        this._aButtons[this._aButtons.length] = oYUIButton;

                    } else {

                        oButtonEl = document.createElement("button");
                        oButtonEl.setAttribute("type", "button");

                        if (oButton.isDefault) {
                            oButtonEl.className = "default";
                            this.defaultHtmlButton = oButtonEl;
                        }

                        oButtonEl.innerHTML = oButton.text;

                        if (Lang.isFunction(oButton.handler)) {
                            Event.on(oButtonEl, "click", oButton.handler, this, true);
                        } else if (Lang.isObject(oButton.handler) &&
                            Lang.isFunction(oButton.handler.fn)) {

                            Event.on(oButtonEl, "click",
                                oButton.handler.fn,
                                ((!Lang.isUndefined(oButton.handler.obj)) ? oButton.handler.obj : this),
                                (oButton.handler.scope || this));
                        }

                        oSpan.appendChild(oButtonEl);
                        this._aButtons[this._aButtons.length] = oButtonEl;
                    }

                    oButton.htmlButton = oButtonEl;

                    if (i === 0) {
                        this.firstButton = oButtonEl;
                    }

                    if (i == (nButtons - 1)) {
                        this.lastButton = oButtonEl;
                    }
                }

                this.setFooter(oSpan);

                oFooter = this.footer;

                if (Dom.inDocument(this.element) && !Dom.isAncestor(oInnerElement, oFooter)) {
                    oInnerElement.appendChild(oFooter);
                }

                this.buttonSpan = oSpan;

            } else { // Do cleanup
                oSpan = this.buttonSpan;
                oFooter = this.footer;
                if (oSpan && oFooter) {
                    oFooter.removeChild(oSpan);
                    this.buttonSpan = null;
                    this.firstButton = null;
                    this.lastButton = null;
                    this.defaultHtmlButton = null;
                }
            }

            this.changeContentEvent.fire();
        },

        /**
        * @method getButtons
        * @description Returns an array containing each of the Dialog's
        * buttons, by default an array of HTML <code>&#60;BUTTON&#62;</code>
        * elements.  If the Dialog's buttons were created using the
        * YAHOO.widget.Button class (via the inclusion of the optional Button
        * dependancy on the page), an array of YAHOO.widget.Button instances
        * is returned.
        * @return {Array}
        */
        getButtons: function () {
            return this._aButtons || null;
        },

        /**
         * <p>
         * Sets focus to the first focusable element in the Dialog's form if found,
         * else, the default button if found, else the first button defined via the
         * "buttons" configuration property.
         * </p>
         * <p>
         * This method is invoked when the Dialog is made visible.
         * </p>
         * @method focusFirst
         */
        focusFirst: function (type, args, obj) {

            var el = this.firstFormElement;

            if (args && args[1]) {
                Event.stopEvent(args[1]);
            }

            if (el) {
                try {
                    el.focus();
                } catch(oException) {
                    // Ignore
                }
            } else {
                if (this.defaultHtmlButton) {
                    this.focusDefaultButton();
                } else {
                    this.focusFirstButton();
                }
            }
        },

        /**
        * Sets focus to the last element in the Dialog's form or the last
        * button defined via the "buttons" configuration property.
        * @method focusLast
        */
        focusLast: function (type, args, obj) {

            var aButtons = this.cfg.getProperty("buttons"),
                el = this.lastFormElement;

            if (args && args[1]) {
                Event.stopEvent(args[1]);
            }

            if (aButtons && Lang.isArray(aButtons)) {
                this.focusLastButton();
            } else {
                if (el) {
                    try {
                        el.focus();
                    } catch(oException) {
                        // Ignore
                    }
                }
            }
        },

        /**
         * Helper method to normalize button references. It either returns the
         * YUI Button instance for the given element if found,
         * or the passes back the HTMLElement reference if a corresponding YUI Button
         * reference is not found or YAHOO.widget.Button does not exist on the page.
         *
         * @method _getButton
         * @private
         * @param {HTMLElement} button
         * @return {YAHOO.widget.Button|HTMLElement}
         */
        _getButton : function(button) {
            var Button = YAHOO.widget.Button;

            // If we have an HTML button and YUI Button is on the page,
            // get the YUI Button reference if available.
            if (Button && button && button.nodeName && button.id) {
                button = Button.getButton(button.id) || button;
            }

            return button;
        },

        /**
        * Sets the focus to the button that is designated as the default via
        * the "buttons" configuration property. By default, this method is
        * called when the Dialog is made visible.
        * @method focusDefaultButton
        */
        focusDefaultButton: function () {
            var button = this._getButton(this.defaultHtmlButton);
            if (button) {
                /*
                    Place the call to the "focus" method inside a try/catch
                    block to prevent IE from throwing JavaScript errors if
                    the element is disabled or hidden.
                */
                try {
                    button.focus();
                } catch(oException) {
                }
            }
        },

        /**
        * Blurs all the buttons defined via the "buttons"
        * configuration property.
        * @method blurButtons
        */
        blurButtons: function () {

            var aButtons = this.cfg.getProperty("buttons"),
                nButtons,
                oButton,
                oElement,
                i;

            if (aButtons && Lang.isArray(aButtons)) {
                nButtons = aButtons.length;
                if (nButtons > 0) {
                    i = (nButtons - 1);
                    do {
                        oButton = aButtons[i];
                        if (oButton) {
                            oElement = this._getButton(oButton.htmlButton);
                            if (oElement) {
                                /*
                                    Place the call to the "blur" method inside
                                    a try/catch block to prevent IE from
                                    throwing JavaScript errors if the element
                                    is disabled or hidden.
                                */
                                try {
                                    oElement.blur();
                                } catch(oException) {
                                    // ignore
                                }
                            }
                        }
                    } while(i--);
                }
            }
        },

        /**
        * Sets the focus to the first button created via the "buttons"
        * configuration property.
        * @method focusFirstButton
        */
        focusFirstButton: function () {

            var aButtons = this.cfg.getProperty("buttons"),
                oButton,
                oElement;

            if (aButtons && Lang.isArray(aButtons)) {
                oButton = aButtons[0];
                if (oButton) {
                    oElement = this._getButton(oButton.htmlButton);
                    if (oElement) {
                        /*
                            Place the call to the "focus" method inside a
                            try/catch block to prevent IE from throwing
                            JavaScript errors if the element is disabled
                            or hidden.
                        */
                        try {
                            oElement.focus();
                        } catch(oException) {
                            // ignore
                        }
                    }
                }
            }
        },

        /**
        * Sets the focus to the last button created via the "buttons"
        * configuration property.
        * @method focusLastButton
        */
        focusLastButton: function () {

            var aButtons = this.cfg.getProperty("buttons"),
                nButtons,
                oButton,
                oElement;

            if (aButtons && Lang.isArray(aButtons)) {
                nButtons = aButtons.length;
                if (nButtons > 0) {
                    oButton = aButtons[(nButtons - 1)];

                    if (oButton) {
                        oElement = this._getButton(oButton.htmlButton);
                        if (oElement) {
                            /*
                                Place the call to the "focus" method inside a
                                try/catch block to prevent IE from throwing
                                JavaScript errors if the element is disabled
                                or hidden.
                            */

                            try {
                                oElement.focus();
                            } catch(oException) {
                                // Ignore
                            }
                        }
                    }
                }
            }
        },

        /**
        * The default event handler for the "postmethod" configuration property
        * @method configPostMethod
        * @param {String} type The CustomEvent type (usually the property name)
        * @param {Object[]} args The CustomEvent arguments. For
        * configuration handlers, args[0] will equal the newly applied value
        * for the property.
        * @param {Object} obj The scope object. For configuration handlers,
        * this will usually equal the owner.
        */
        configPostMethod: function (type, args, obj) {
            this.registerForm();
        },

        // END BUILT-IN PROPERTY EVENT HANDLERS //

        /**
        * Built-in function hook for writing a validation function that will
        * be checked for a "true" value prior to a submit. This function, as
        * implemented by default, always returns true, so it should be
        * overridden if validation is necessary.
        * @method validate
        */
        validate: function () {
            return true;
        },

        /**
        * Executes a submit of the Dialog if validation
        * is successful. By default the Dialog is hidden
        * after submission, but you can set the "hideaftersubmit"
        * configuration property to false, to prevent the Dialog
        * from being hidden.
        *
        * @method submit
        */
        submit: function () {
            if (this.validate()) {
                if (this.beforeSubmitEvent.fire()) {
                    this.doSubmit();
                    this.submitEvent.fire();

                    if (this.cfg.getProperty("hideaftersubmit")) {
                        this.hide();
                    }

                    return true;
                } else {
                    return false;
                }
            } else {
                return false;
            }
        },

        /**
        * Executes the cancel of the Dialog followed by a hide.
        * @method cancel
        */
        cancel: function () {
            this.cancelEvent.fire();
            this.hide();
        },

        /**
        * Returns a JSON-compatible data structure representing the data
        * currently contained in the form.
        * @method getData
        * @return {Object} A JSON object reprsenting the data of the
        * current form.
        */
        getData: function () {

            var oForm = this.form,
                aElements,
                nTotalElements,
                oData,
                sName,
                oElement,
                nElements,
                sType,
                sTagName,
                aOptions,
                nOptions,
                aValues,
                oOption,
                oRadio,
                oCheckbox,
                valueAttr,
                i,
                n;

            function isFormElement(p_oElement) {
                var sTag = p_oElement.tagName.toUpperCase();
                return ((sTag == "INPUT" || sTag == "TEXTAREA" ||
                        sTag == "SELECT") && p_oElement.name == sName);
            }

            if (oForm) {

                aElements = oForm.elements;
                nTotalElements = aElements.length;
                oData = {};

                for (i = 0; i < nTotalElements; i++) {
                    sName = aElements[i].name;

                    /*
                        Using "Dom.getElementsBy" to safeguard user from JS
                        errors that result from giving a form field (or set of
                        fields) the same name as a native method of a form
                        (like "submit") or a DOM collection (such as the "item"
                        method). Originally tried accessing fields via the
                        "namedItem" method of the "element" collection, but
                        discovered that it won't return a collection of fields
                        in Gecko.
                    */

                    oElement = Dom.getElementsBy(isFormElement, "*", oForm);
                    nElements = oElement.length;

                    if (nElements > 0) {
                        if (nElements == 1) {
                            oElement = oElement[0];

                            sType = oElement.type;
                            sTagName = oElement.tagName.toUpperCase();

                            switch (sTagName) {
                                case "INPUT":
                                    if (sType == "checkbox") {
                                        oData[sName] = oElement.checked;
                                    } else if (sType != "radio") {
                                        oData[sName] = oElement.value;
                                    }
                                    break;

                                case "TEXTAREA":
                                    oData[sName] = oElement.value;
                                    break;

                                case "SELECT":
                                    aOptions = oElement.options;
                                    nOptions = aOptions.length;
                                    aValues = [];

                                    for (n = 0; n < nOptions; n++) {
                                        oOption = aOptions[n];
                                        if (oOption.selected) {
                                            valueAttr = oOption.attributes.value;
                                            aValues[aValues.length] = (valueAttr && valueAttr.specified) ? oOption.value : oOption.text;
                                        }
                                    }
                                    oData[sName] = aValues;
                                    break;
                            }

                        } else {
                            sType = oElement[0].type;
                            switch (sType) {
                                case "radio":
                                    for (n = 0; n < nElements; n++) {
                                        oRadio = oElement[n];
                                        if (oRadio.checked) {
                                            oData[sName] = oRadio.value;
                                            break;
                                        }
                                    }
                                    break;

                                case "checkbox":
                                    aValues = [];
                                    for (n = 0; n < nElements; n++) {
                                        oCheckbox = oElement[n];
                                        if (oCheckbox.checked) {
                                            aValues[aValues.length] =  oCheckbox.value;
                                        }
                                    }
                                    oData[sName] = aValues;
                                    break;
                            }
                        }
                    }
                }
            }

            return oData;
        },

        /**
        * Removes the Panel element from the DOM and sets all child elements
        * to null.
        * @method destroy
        */
        destroy: function () {
            removeButtonEventHandlers.call(this);

            this._aButtons = null;

            var aForms = this.element.getElementsByTagName("form"),
                oForm;

            if (aForms.length > 0) {
                oForm = aForms[0];

                if (oForm) {
                    Event.purgeElement(oForm);
                    if (oForm.parentNode) {
                        oForm.parentNode.removeChild(oForm);
                    }
                    this.form = null;
                }
            }
            Dialog.superclass.destroy.call(this);
        },

        /**
        * Returns a string representation of the object.
        * @method toString
        * @return {String} The string representation of the Dialog
        */
        toString: function () {
            return "Dialog " + this.id;
        }

    });

}());
(function () {

    /**
    * SimpleDialog is a simple implementation of Dialog that can be used to
    * submit a single value. Forms can be processed in 3 ways -- via an
    * asynchronous Connection utility call, a simple form POST or GET,
    * or manually.
    * @namespace YAHOO.widget
    * @class SimpleDialog
    * @extends YAHOO.widget.Dialog
    * @constructor
    * @param {String} el The element ID representing the SimpleDialog
    * <em>OR</em>
    * @param {HTMLElement} el The element representing the SimpleDialog
    * @param {Object} userConfig The configuration object literal containing
    * the configuration that should be set for this SimpleDialog. See
    * configuration documentation for more details.
    */
    YAHOO.widget.SimpleDialog = function (el, userConfig) {

        YAHOO.widget.SimpleDialog.superclass.constructor.call(this,
            el, userConfig);

    };

    var Dom = YAHOO.util.Dom,
        SimpleDialog = YAHOO.widget.SimpleDialog,

        /**
        * Constant representing the SimpleDialog's configuration properties
        * @property DEFAULT_CONFIG
        * @private
        * @final
        * @type Object
        */
        DEFAULT_CONFIG = {

            "ICON": {
                key: "icon",
                value: "none",
                suppressEvent: true
            },

            "TEXT": {
                key: "text",
                value: "",
                suppressEvent: true,
                supercedes: ["icon"]
            }

        };

    /**
    * Constant for the standard network icon for a blocking action
    * @property YAHOO.widget.SimpleDialog.ICON_BLOCK
    * @static
    * @final
    * @type String
    */
    SimpleDialog.ICON_BLOCK = "blckicon";

    /**
    * Constant for the standard network icon for alarm
    * @property YAHOO.widget.SimpleDialog.ICON_ALARM
    * @static
    * @final
    * @type String
    */
    SimpleDialog.ICON_ALARM = "alrticon";

    /**
    * Constant for the standard network icon for help
    * @property YAHOO.widget.SimpleDialog.ICON_HELP
    * @static
    * @final
    * @type String
    */
    SimpleDialog.ICON_HELP  = "hlpicon";

    /**
    * Constant for the standard network icon for info
    * @property YAHOO.widget.SimpleDialog.ICON_INFO
    * @static
    * @final
    * @type String
    */
    SimpleDialog.ICON_INFO  = "infoicon";

    /**
    * Constant for the standard network icon for warn
    * @property YAHOO.widget.SimpleDialog.ICON_WARN
    * @static
    * @final
    * @type String
    */
    SimpleDialog.ICON_WARN  = "warnicon";

    /**
    * Constant for the standard network icon for a tip
    * @property YAHOO.widget.SimpleDialog.ICON_TIP
    * @static
    * @final
    * @type String
    */
    SimpleDialog.ICON_TIP   = "tipicon";

    /**
    * Constant representing the name of the CSS class applied to the element
    * created by the "icon" configuration property.
    * @property YAHOO.widget.SimpleDialog.ICON_CSS_CLASSNAME
    * @static
    * @final
    * @type String
    */
    SimpleDialog.ICON_CSS_CLASSNAME = "yui-icon";

    /**
    * Constant representing the default CSS class used for a SimpleDialog
    * @property YAHOO.widget.SimpleDialog.CSS_SIMPLEDIALOG
    * @static
    * @final
    * @type String
    */
    SimpleDialog.CSS_SIMPLEDIALOG = "yui-simple-dialog";


    YAHOO.extend(SimpleDialog, YAHOO.widget.Dialog, {

        /**
        * Initializes the class's configurable properties which can be changed
        * using the SimpleDialog's Config object (cfg).
        * @method initDefaultConfig
        */
        initDefaultConfig: function () {

            SimpleDialog.superclass.initDefaultConfig.call(this);

            // Add dialog config properties //

            /**
            * Sets the informational icon for the SimpleDialog
            * @config icon
            * @type String
            * @default "none"
            */
            this.cfg.addProperty(DEFAULT_CONFIG.ICON.key, {
                handler: this.configIcon,
                value: DEFAULT_CONFIG.ICON.value,
                suppressEvent: DEFAULT_CONFIG.ICON.suppressEvent
            });

            /**
            * Sets the text for the SimpleDialog
            * @config text
            * @type String
            * @default ""
            */
            this.cfg.addProperty(DEFAULT_CONFIG.TEXT.key, {
                handler: this.configText,
                value: DEFAULT_CONFIG.TEXT.value,
                suppressEvent: DEFAULT_CONFIG.TEXT.suppressEvent,
                supercedes: DEFAULT_CONFIG.TEXT.supercedes
            });

        },


        /**
        * The SimpleDialog initialization method, which is executed for
        * SimpleDialog and all of its subclasses. This method is automatically
        * called by the constructor, and  sets up all DOM references for
        * pre-existing markup, and creates required markup if it is not
        * already present.
        * @method init
        * @param {String} el The element ID representing the SimpleDialog
        * <em>OR</em>
        * @param {HTMLElement} el The element representing the SimpleDialog
        * @param {Object} userConfig The configuration object literal
        * containing the configuration that should be set for this
        * SimpleDialog. See configuration documentation for more details.
        */
        init: function (el, userConfig) {

            /*
                Note that we don't pass the user config in here yet because we
                only want it executed once, at the lowest subclass level
            */

            SimpleDialog.superclass.init.call(this, el/*, userConfig*/);

            this.beforeInitEvent.fire(SimpleDialog);

            Dom.addClass(this.element, SimpleDialog.CSS_SIMPLEDIALOG);

            this.cfg.queueProperty("postmethod", "manual");

            if (userConfig) {
                this.cfg.applyConfig(userConfig, true);
            }

            this.beforeRenderEvent.subscribe(function () {
                if (! this.body) {
                    this.setBody("");
                }
            }, this, true);

            this.initEvent.fire(SimpleDialog);

        },

        /**
        * Prepares the SimpleDialog's internal FORM object, creating one if one
        * is not currently present, and adding the value hidden field.
        * @method registerForm
        */
        registerForm: function () {

            SimpleDialog.superclass.registerForm.call(this);

            this.form.innerHTML += "<input type=\"hidden\" name=\"" +
                this.id + "\" value=\"\"/>";

        },

        // BEGIN BUILT-IN PROPERTY EVENT HANDLERS //

        /**
        * Fired when the "icon" property is set.
        * @method configIcon
        * @param {String} type The CustomEvent type (usually the property name)
        * @param {Object[]} args The CustomEvent arguments. For configuration
        * handlers, args[0] will equal the newly applied value for the property.
        * @param {Object} obj The scope object. For configuration handlers,
        * this will usually equal the owner.
        */
        configIcon: function (type,args,obj) {

            var sIcon = args[0],
                oBody = this.body,
                sCSSClass = SimpleDialog.ICON_CSS_CLASSNAME,
				aElements,
                oIcon,
                oIconParent;

            if (sIcon && sIcon != "none") {

                aElements = Dom.getElementsByClassName(sCSSClass, "*" , oBody);

				if (aElements.length === 1) {

					oIcon = aElements[0];
                    oIconParent = oIcon.parentNode;

                    if (oIconParent) {

                        oIconParent.removeChild(oIcon);

                        oIcon = null;

                    }

				}


                if (sIcon.indexOf(".") == -1) {

                    oIcon = document.createElement("span");
                    oIcon.className = (sCSSClass + " " + sIcon);
                    oIcon.innerHTML = "&#160;";

                } else {

                    oIcon = document.createElement("img");
                    oIcon.src = (this.imageRoot + sIcon);
                    oIcon.className = sCSSClass;

                }


                if (oIcon) {

                    oBody.insertBefore(oIcon, oBody.firstChild);

                }

            }

        },

        /**
        * Fired when the "text" property is set.
        * @method configText
        * @param {String} type The CustomEvent type (usually the property name)
        * @param {Object[]} args The CustomEvent arguments. For configuration
        * handlers, args[0] will equal the newly applied value for the property.
        * @param {Object} obj The scope object. For configuration handlers,
        * this will usually equal the owner.
        */
        configText: function (type,args,obj) {
            var text = args[0];
            if (text) {
                this.setBody(text);
                this.cfg.refireEvent("icon");
            }
        },

        // END BUILT-IN PROPERTY EVENT HANDLERS //

        /**
        * Returns a string representation of the object.
        * @method toString
        * @return {String} The string representation of the SimpleDialog
        */
        toString: function () {
            return "SimpleDialog " + this.id;
        }

        /**
        * <p>
        * Sets the SimpleDialog's body content to the HTML specified.
        * If no body is present, one will be automatically created.
        * An empty string can be passed to the method to clear the contents of the body.
        * </p>
        * <p><strong>NOTE:</strong> SimpleDialog provides the <a href="#config_text">text</a>
        * and <a href="#config_icon">icon</a> configuration properties to set the contents
        * of it's body element in accordance with the UI design for a SimpleDialog (an
        * icon and message text). Calling setBody on the SimpleDialog will not enforce this
        * UI design constraint and will replace the entire contents of the SimpleDialog body.
        * It should only be used if you wish the replace the default icon/text body structure
        * of a SimpleDialog with your own custom markup.</p>
        *
        * @method setBody
        * @param {String} bodyContent The HTML used to set the body.
        * As a convenience, non HTMLElement objects can also be passed into
        * the method, and will be treated as strings, with the body innerHTML
        * set to their default toString implementations.
        * <em>OR</em>
        * @param {HTMLElement} bodyContent The HTMLElement to add as the first and only child of the body element.
        * <em>OR</em>
        * @param {DocumentFragment} bodyContent The document fragment
        * containing elements which are to be added to the body
        */
    });

}());
(function () {

    /**
    * ContainerEffect encapsulates animation transitions that are executed when
    * an Overlay is shown or hidden.
    * @namespace YAHOO.widget
    * @class ContainerEffect
    * @constructor
    * @param {YAHOO.widget.Overlay} overlay The Overlay that the animation
    * should be associated with
    * @param {Object} attrIn The object literal representing the animation
    * arguments to be used for the animate-in transition. The arguments for
    * this literal are: attributes(object, see YAHOO.util.Anim for description),
    * duration(Number), and method(i.e. Easing.easeIn).
    * @param {Object} attrOut The object literal representing the animation
    * arguments to be used for the animate-out transition. The arguments for
    * this literal are: attributes(object, see YAHOO.util.Anim for description),
    * duration(Number), and method(i.e. Easing.easeIn).
    * @param {HTMLElement} targetElement Optional. The target element that
    * should be animated during the transition. Defaults to overlay.element.
    * @param {class} Optional. The animation class to instantiate. Defaults to
    * YAHOO.util.Anim. Other options include YAHOO.util.Motion.
    */
    YAHOO.widget.ContainerEffect = function (overlay, attrIn, attrOut, targetElement, animClass) {

        if (!animClass) {
            animClass = YAHOO.util.Anim;
        }

        /**
        * The overlay to animate
        * @property overlay
        * @type YAHOO.widget.Overlay
        */
        this.overlay = overlay;

        /**
        * The animation attributes to use when transitioning into view
        * @property attrIn
        * @type Object
        */
        this.attrIn = attrIn;

        /**
        * The animation attributes to use when transitioning out of view
        * @property attrOut
        * @type Object
        */
        this.attrOut = attrOut;

        /**
        * The target element to be animated
        * @property targetElement
        * @type HTMLElement
        */
        this.targetElement = targetElement || overlay.element;

        /**
        * The animation class to use for animating the overlay
        * @property animClass
        * @type class
        */
        this.animClass = animClass;

    };


    var Dom = YAHOO.util.Dom,
        CustomEvent = YAHOO.util.CustomEvent,
        ContainerEffect = YAHOO.widget.ContainerEffect;


    /**
    * A pre-configured ContainerEffect instance that can be used for fading
    * an overlay in and out.
    * @method FADE
    * @static
    * @param {YAHOO.widget.Overlay} overlay The Overlay object to animate
    * @param {Number} dur The duration of the animation
    * @return {YAHOO.widget.ContainerEffect} The configured ContainerEffect object
    */
    ContainerEffect.FADE = function (overlay, dur) {

        var Easing = YAHOO.util.Easing,
            fin = {
                attributes: {opacity:{from:0, to:1}},
                duration: dur,
                method: Easing.easeIn
            },
            fout = {
                attributes: {opacity:{to:0}},
                duration: dur,
                method: Easing.easeOut
            },
            fade = new ContainerEffect(overlay, fin, fout, overlay.element);

        fade.handleUnderlayStart = function() {
            var underlay = this.overlay.underlay;
            if (underlay && YAHOO.env.ua.ie) {
                var hasFilters = (underlay.filters && underlay.filters.length > 0);
                if(hasFilters) {
                    Dom.addClass(overlay.element, "yui-effect-fade");
                }
            }
        };

        fade.handleUnderlayComplete = function() {
            var underlay = this.overlay.underlay;
            if (underlay && YAHOO.env.ua.ie) {
                Dom.removeClass(overlay.element, "yui-effect-fade");
            }
        };

        fade.handleStartAnimateIn = function (type, args, obj) {
            Dom.addClass(obj.overlay.element, "hide-select");

            if (!obj.overlay.underlay) {
                obj.overlay.cfg.refireEvent("underlay");
            }

            obj.handleUnderlayStart();

            obj.overlay._setDomVisibility(true);
            Dom.setStyle(obj.overlay.element, "opacity", 0);
        };

        fade.handleCompleteAnimateIn = function (type,args,obj) {
            Dom.removeClass(obj.overlay.element, "hide-select");

            if (obj.overlay.element.style.filter) {
                obj.overlay.element.style.filter = null;
            }

            obj.handleUnderlayComplete();

            obj.overlay.cfg.refireEvent("iframe");
            obj.animateInCompleteEvent.fire();
        };

        fade.handleStartAnimateOut = function (type, args, obj) {
            Dom.addClass(obj.overlay.element, "hide-select");
            obj.handleUnderlayStart();
        };

        fade.handleCompleteAnimateOut =  function (type, args, obj) {
            Dom.removeClass(obj.overlay.element, "hide-select");
            if (obj.overlay.element.style.filter) {
                obj.overlay.element.style.filter = null;
            }
            obj.overlay._setDomVisibility(false);
            Dom.setStyle(obj.overlay.element, "opacity", 1);

            obj.handleUnderlayComplete();

            obj.overlay.cfg.refireEvent("iframe");
            obj.animateOutCompleteEvent.fire();
        };

        fade.init();
        return fade;
    };


    /**
    * A pre-configured ContainerEffect instance that can be used for sliding an
    * overlay in and out.
    * @method SLIDE
    * @static
    * @param {YAHOO.widget.Overlay} overlay The Overlay object to animate
    * @param {Number} dur The duration of the animation
    * @return {YAHOO.widget.ContainerEffect} The configured ContainerEffect object
    */
    ContainerEffect.SLIDE = function (overlay, dur) {
        var Easing = YAHOO.util.Easing,

            x = overlay.cfg.getProperty("x") || Dom.getX(overlay.element),
            y = overlay.cfg.getProperty("y") || Dom.getY(overlay.element),
            clientWidth = Dom.getClientWidth(),
            offsetWidth = overlay.element.offsetWidth,

            sin =  {
                attributes: { points: { to: [x, y] } },
                duration: dur,
                method: Easing.easeIn
            },

            sout = {
                attributes: { points: { to: [(clientWidth + 25), y] } },
                duration: dur,
                method: Easing.easeOut
            },

            slide = new ContainerEffect(overlay, sin, sout, overlay.element, YAHOO.util.Motion);

        slide.handleStartAnimateIn = function (type,args,obj) {
            obj.overlay.element.style.left = ((-25) - offsetWidth) + "px";
            obj.overlay.element.style.top  = y + "px";
        };

        slide.handleTweenAnimateIn = function (type, args, obj) {

            var pos = Dom.getXY(obj.overlay.element),
                currentX = pos[0],
                currentY = pos[1];

            if (Dom.getStyle(obj.overlay.element, "visibility") ==
                "hidden" && currentX < x) {

                obj.overlay._setDomVisibility(true);

            }

            obj.overlay.cfg.setProperty("xy", [currentX, currentY], true);
            obj.overlay.cfg.refireEvent("iframe");
        };

        slide.handleCompleteAnimateIn = function (type, args, obj) {
            obj.overlay.cfg.setProperty("xy", [x, y], true);
            obj.startX = x;
            obj.startY = y;
            obj.overlay.cfg.refireEvent("iframe");
            obj.animateInCompleteEvent.fire();
        };

        slide.handleStartAnimateOut = function (type, args, obj) {

            var vw = Dom.getViewportWidth(),
                pos = Dom.getXY(obj.overlay.element),
                yso = pos[1];

            obj.animOut.attributes.points.to = [(vw + 25), yso];
        };

        slide.handleTweenAnimateOut = function (type, args, obj) {

            var pos = Dom.getXY(obj.overlay.element),
                xto = pos[0],
                yto = pos[1];

            obj.overlay.cfg.setProperty("xy", [xto, yto], true);
            obj.overlay.cfg.refireEvent("iframe");
        };

        slide.handleCompleteAnimateOut = function (type, args, obj) {
            obj.overlay._setDomVisibility(false);

            obj.overlay.cfg.setProperty("xy", [x, y]);
            obj.animateOutCompleteEvent.fire();
        };

        slide.init();
        return slide;
    };

    ContainerEffect.prototype = {

        /**
        * Initializes the animation classes and events.
        * @method init
        */
        init: function () {

            this.beforeAnimateInEvent = this.createEvent("beforeAnimateIn");
            this.beforeAnimateInEvent.signature = CustomEvent.LIST;

            this.beforeAnimateOutEvent = this.createEvent("beforeAnimateOut");
            this.beforeAnimateOutEvent.signature = CustomEvent.LIST;

            this.animateInCompleteEvent = this.createEvent("animateInComplete");
            this.animateInCompleteEvent.signature = CustomEvent.LIST;

            this.animateOutCompleteEvent =
                this.createEvent("animateOutComplete");
            this.animateOutCompleteEvent.signature = CustomEvent.LIST;

            this.animIn = new this.animClass(this.targetElement,
                this.attrIn.attributes, this.attrIn.duration,
                this.attrIn.method);

            this.animIn.onStart.subscribe(this.handleStartAnimateIn, this);
            this.animIn.onTween.subscribe(this.handleTweenAnimateIn, this);

            this.animIn.onComplete.subscribe(this.handleCompleteAnimateIn,
                this);

            this.animOut = new this.animClass(this.targetElement,
                this.attrOut.attributes, this.attrOut.duration,
                this.attrOut.method);

            this.animOut.onStart.subscribe(this.handleStartAnimateOut, this);
            this.animOut.onTween.subscribe(this.handleTweenAnimateOut, this);
            this.animOut.onComplete.subscribe(this.handleCompleteAnimateOut,
                this);

        },

        /**
        * Triggers the in-animation.
        * @method animateIn
        */
        animateIn: function () {
            this.beforeAnimateInEvent.fire();
            this.animIn.animate();
        },

        /**
        * Triggers the out-animation.
        * @method animateOut
        */
        animateOut: function () {
            this.beforeAnimateOutEvent.fire();
            this.animOut.animate();
        },

        /**
        * The default onStart handler for the in-animation.
        * @method handleStartAnimateIn
        * @param {String} type The CustomEvent type
        * @param {Object[]} args The CustomEvent arguments
        * @param {Object} obj The scope object
        */
        handleStartAnimateIn: function (type, args, obj) { },

        /**
        * The default onTween handler for the in-animation.
        * @method handleTweenAnimateIn
        * @param {String} type The CustomEvent type
        * @param {Object[]} args The CustomEvent arguments
        * @param {Object} obj The scope object
        */
        handleTweenAnimateIn: function (type, args, obj) { },

        /**
        * The default onComplete handler for the in-animation.
        * @method handleCompleteAnimateIn
        * @param {String} type The CustomEvent type
        * @param {Object[]} args The CustomEvent arguments
        * @param {Object} obj The scope object
        */
        handleCompleteAnimateIn: function (type, args, obj) { },

        /**
        * The default onStart handler for the out-animation.
        * @method handleStartAnimateOut
        * @param {String} type The CustomEvent type
        * @param {Object[]} args The CustomEvent arguments
        * @param {Object} obj The scope object
        */
        handleStartAnimateOut: function (type, args, obj) { },

        /**
        * The default onTween handler for the out-animation.
        * @method handleTweenAnimateOut
        * @param {String} type The CustomEvent type
        * @param {Object[]} args The CustomEvent arguments
        * @param {Object} obj The scope object
        */
        handleTweenAnimateOut: function (type, args, obj) { },

        /**
        * The default onComplete handler for the out-animation.
        * @method handleCompleteAnimateOut
        * @param {String} type The CustomEvent type
        * @param {Object[]} args The CustomEvent arguments
        * @param {Object} obj The scope object
        */
        handleCompleteAnimateOut: function (type, args, obj) { },

        /**
        * Returns a string representation of the object.
        * @method toString
        * @return {String} The string representation of the ContainerEffect
        */
        toString: function () {
            var output = "ContainerEffect";
            if (this.overlay) {
                output += " [" + this.overlay.toString() + "]";
            }
            return output;
        }
    };

    YAHOO.lang.augmentProto(ContainerEffect, YAHOO.util.EventProvider);

})();
YAHOO.register("container", YAHOO.widget.Module, {version: "2.8.0r4", build: "2449"});


/*
Copyright (c) 2009, Yahoo! Inc. All rights reserved.
Code licensed under the BSD License:
http://developer.yahoo.net/yui/license.txt
version: 2.8.0r4
*/


/**
* @module menu
* @description <p>The Menu family of components features a collection of 
* controls that make it easy to add menus to your website or web application.  
* With the Menu Controls you can create website fly-out menus, customized 
* context menus, or application-style menu bars with just a small amount of 
* scripting.</p><p>The Menu family of controls features:</p>
* <ul>
*    <li>Keyboard and mouse navigation.</li>
*    <li>A rich event model that provides access to all of a menu's 
*    interesting moments.</li>
*    <li>Support for 
*    <a href="http://en.wikipedia.org/wiki/Progressive_Enhancement">Progressive
*    Enhancement</a>; Menus can be created from simple, 
*    semantic markup on the page or purely through JavaScript.</li>
* </ul>
* @title Menu
* @namespace YAHOO.widget
* @requires Event, Dom, Container
*/
(function () {

    var UA = YAHOO.env.ua,
		Dom = YAHOO.util.Dom,
	    Event = YAHOO.util.Event,
	    Lang = YAHOO.lang,

		_DIV = "DIV",
    	_HD = "hd",
    	_BD = "bd",
    	_FT = "ft",
    	_LI = "LI",
    	_DISABLED = "disabled",
		_MOUSEOVER = "mouseover",
		_MOUSEOUT = "mouseout",
		_MOUSEDOWN = "mousedown",
		_MOUSEUP = "mouseup",
		_CLICK = "click",
		_KEYDOWN = "keydown",
		_KEYUP = "keyup",
		_KEYPRESS = "keypress",
		_CLICK_TO_HIDE = "clicktohide",
		_POSITION = "position", 
		_DYNAMIC = "dynamic",
		_SHOW_DELAY = "showdelay",
		_SELECTED = "selected",
		_VISIBLE = "visible",
		_UL = "UL",
		_MENUMANAGER = "MenuManager";


    /**
    * Singleton that manages a collection of all menus and menu items.  Listens 
    * for DOM events at the document level and dispatches the events to the 
    * corresponding menu or menu item.
    *
    * @namespace YAHOO.widget
    * @class MenuManager
    * @static
    */
    YAHOO.widget.MenuManager = function () {
    
        // Private member variables
    
    
        // Flag indicating if the DOM event handlers have been attached
    
        var m_bInitializedEventHandlers = false,
    
    
        // Collection of menus

        m_oMenus = {},


        // Collection of visible menus
    
        m_oVisibleMenus = {},
    
    
        //  Collection of menu items 

        m_oItems = {},


        // Map of DOM event types to their equivalent CustomEvent types
        
        m_oEventTypes = {
            "click": "clickEvent",
            "mousedown": "mouseDownEvent",
            "mouseup": "mouseUpEvent",
            "mouseover": "mouseOverEvent",
            "mouseout": "mouseOutEvent",
            "keydown": "keyDownEvent",
            "keyup": "keyUpEvent",
            "keypress": "keyPressEvent",
            "focus": "focusEvent",
            "focusin": "focusEvent",
            "blur": "blurEvent",
            "focusout": "blurEvent"
        },
    
    
        m_oFocusedMenuItem = null;
    
    
    
        // Private methods
    
    
        /**
        * @method getMenuRootElement
        * @description Finds the root DIV node of a menu or the root LI node of 
        * a menu item.
        * @private
        * @param {<a href="http://www.w3.org/TR/2000/WD-DOM-Level-1-20000929/
        * level-one-html.html#ID-58190037">HTMLElement</a>} p_oElement Object 
        * specifying an HTML element.
        */
        function getMenuRootElement(p_oElement) {
        
            var oParentNode,
            	returnVal;
    
            if (p_oElement && p_oElement.tagName) {
            
                switch (p_oElement.tagName.toUpperCase()) {
                        
                case _DIV:
    
                    oParentNode = p_oElement.parentNode;
    
                    // Check if the DIV is the inner "body" node of a menu

                    if ((
                            Dom.hasClass(p_oElement, _HD) ||
                            Dom.hasClass(p_oElement, _BD) ||
                            Dom.hasClass(p_oElement, _FT)
                        ) && 
                        oParentNode && 
                        oParentNode.tagName && 
                        oParentNode.tagName.toUpperCase() == _DIV) {
                    
                        returnVal = oParentNode;
                    
                    }
                    else {
                    
                        returnVal = p_oElement;
                    
                    }
                
                    break;

                case _LI:
    
                    returnVal = p_oElement;
                    
                    break;

                default:
    
                    oParentNode = p_oElement.parentNode;
    
                    if (oParentNode) {
                    
                        returnVal = getMenuRootElement(oParentNode);
                    
                    }
                
                    break;
                
                }
    
            }
            
            return returnVal;
            
        }
    
    
    
        // Private event handlers
    
    
        /**
        * @method onDOMEvent
        * @description Generic, global event handler for all of a menu's 
        * DOM-based events.  This listens for events against the document 
        * object.  If the target of a given event is a member of a menu or 
        * menu item's DOM, the instance's corresponding Custom Event is fired.
        * @private
        * @param {Event} p_oEvent Object representing the DOM event object  
        * passed back by the event utility (YAHOO.util.Event).
        */
        function onDOMEvent(p_oEvent) {
    
            // Get the target node of the DOM event
        
            var oTarget = Event.getTarget(p_oEvent),
                
            // See if the target of the event was a menu, or a menu item
    
            oElement = getMenuRootElement(oTarget),
			bFireEvent = true,
			sEventType = p_oEvent.type,
            sCustomEventType,
            sTagName,
            sId,
            oMenuItem,
            oMenu; 
    
    
            if (oElement) {
    
                sTagName = oElement.tagName.toUpperCase();
        
                if (sTagName == _LI) {
            
                    sId = oElement.id;
            
                    if (sId && m_oItems[sId]) {
            
                        oMenuItem = m_oItems[sId];
                        oMenu = oMenuItem.parent;
            
                    }
                
                }
                else if (sTagName == _DIV) {
                
                    if (oElement.id) {
                    
                        oMenu = m_oMenus[oElement.id];
                    
                    }
                
                }
    
            }
    
    
            if (oMenu) {
    
                sCustomEventType = m_oEventTypes[sEventType];

				/*
					There is an inconsistency between Firefox for Mac OS X and 
					Firefox Windows & Linux regarding the triggering of the 
					display of the browser's context menu and the subsequent 
					firing of the "click" event. In Firefox for Windows & Linux, 
					when the user triggers the display of the browser's context 
					menu the "click" event also fires for the document object, 
					even though the "click" event did not fire for the element 
					that was the original target of the "contextmenu" event. 
					This is unique to Firefox on Windows & Linux.  For all 
					other A-Grade browsers, including Firefox for Mac OS X, the 
					"click" event doesn't fire for the document object. 

					This bug in Firefox for Windows affects Menu, as Menu 
					instances listen for events at the document level and 
					dispatches Custom Events of the same name.  Therefore users
					of Menu will get an unwanted firing of the "click" 
					custom event.  The following line fixes this bug.
				*/
				


				if (sEventType == "click" && 
					(UA.gecko && oMenu.platform != "mac") && 
					p_oEvent.button > 0) {

					bFireEvent = false;

				}
    
                // Fire the Custom Event that corresponds the current DOM event    
        
                if (bFireEvent && oMenuItem && !oMenuItem.cfg.getProperty(_DISABLED)) {
                    oMenuItem[sCustomEventType].fire(p_oEvent);                   
                }
        
				if (bFireEvent) {
                	oMenu[sCustomEventType].fire(p_oEvent, oMenuItem);
				}
            
            }
            else if (sEventType == _MOUSEDOWN) {
    
                /*
                    If the target of the event wasn't a menu, hide all 
                    dynamically positioned menus
                */
                
                for (var i in m_oVisibleMenus) {
        
                    if (Lang.hasOwnProperty(m_oVisibleMenus, i)) {
        
                        oMenu = m_oVisibleMenus[i];

                        if (oMenu.cfg.getProperty(_CLICK_TO_HIDE) && 
                            !(oMenu instanceof YAHOO.widget.MenuBar) && 
                            oMenu.cfg.getProperty(_POSITION) == _DYNAMIC) {

                            oMenu.hide();

							//	In IE when the user mouses down on a focusable 
							//	element that element will be focused and become 
							//	the "activeElement".
							//	(http://msdn.microsoft.com/en-us/library/ms533065(VS.85).aspx)
							//	However, there is a bug in IE where if there is 
							//	a positioned element with a focused descendant 
							//	that is hidden in response to the mousedown 
							//	event, the target of the mousedown event will 
							//	appear to have focus, but will not be set as 
							//	the activeElement.  This will result in the 
							//	element not firing key events, even though it
							//	appears to have focus.  The following call to 
							//	"setActive" fixes this bug.

							if (UA.ie && oTarget.focus) {
								oTarget.setActive();
							}
        
                        }
                        else {
                            
							if (oMenu.cfg.getProperty(_SHOW_DELAY) > 0) {
							
								oMenu._cancelShowDelay();
							
							}


							if (oMenu.activeItem) {
						
								oMenu.activeItem.blur();
								oMenu.activeItem.cfg.setProperty(_SELECTED, false);
						
								oMenu.activeItem = null;            
						
							}
        
                        }
        
                    }
        
                } 
    
            }
            
        }
    
    
        /**
        * @method onMenuDestroy
        * @description "destroy" event handler for a menu.
        * @private
        * @param {String} p_sType String representing the name of the event 
        * that was fired.
        * @param {Array} p_aArgs Array of arguments sent when the event 
        * was fired.
        * @param {YAHOO.widget.Menu} p_oMenu The menu that fired the event.
        */
        function onMenuDestroy(p_sType, p_aArgs, p_oMenu) {
    
            if (m_oMenus[p_oMenu.id]) {
    
                this.removeMenu(p_oMenu);
    
            }
    
        }
    
    
        /**
        * @method onMenuFocus
        * @description "focus" event handler for a MenuItem instance.
        * @private
        * @param {String} p_sType String representing the name of the event 
        * that was fired.
        * @param {Array} p_aArgs Array of arguments sent when the event 
        * was fired.
        */
        function onMenuFocus(p_sType, p_aArgs) {
    
            var oItem = p_aArgs[1];
    
            if (oItem) {
    
                m_oFocusedMenuItem = oItem;
            
            }
    
        }
    
    
        /**
        * @method onMenuBlur
        * @description "blur" event handler for a MenuItem instance.
        * @private
        * @param {String} p_sType String representing the name of the event  
        * that was fired.
        * @param {Array} p_aArgs Array of arguments sent when the event 
        * was fired.
        */
        function onMenuBlur(p_sType, p_aArgs) {
    
            m_oFocusedMenuItem = null;
    
        }

    
        /**
        * @method onMenuVisibleConfigChange
        * @description Event handler for when the "visible" configuration  
        * property of a Menu instance changes.
        * @private
        * @param {String} p_sType String representing the name of the event  
        * that was fired.
        * @param {Array} p_aArgs Array of arguments sent when the event 
        * was fired.
        */
        function onMenuVisibleConfigChange(p_sType, p_aArgs) {
    
            var bVisible = p_aArgs[0],
                sId = this.id;
            
            if (bVisible) {
    
                m_oVisibleMenus[sId] = this;
                
            
            }
            else if (m_oVisibleMenus[sId]) {
            
                delete m_oVisibleMenus[sId];
                
            
            }
        
        }
    
    
        /**
        * @method onItemDestroy
        * @description "destroy" event handler for a MenuItem instance.
        * @private
        * @param {String} p_sType String representing the name of the event  
        * that was fired.
        * @param {Array} p_aArgs Array of arguments sent when the event 
        * was fired.
        */
        function onItemDestroy(p_sType, p_aArgs) {
    
            removeItem(this);
    
        }


        /**
        * @method removeItem
        * @description Removes a MenuItem instance from the MenuManager's collection of MenuItems.
        * @private
        * @param {MenuItem} p_oMenuItem The MenuItem instance to be removed.
        */    
        function removeItem(p_oMenuItem) {

            var sId = p_oMenuItem.id;
    
            if (sId && m_oItems[sId]) {
    
                if (m_oFocusedMenuItem == p_oMenuItem) {
    
                    m_oFocusedMenuItem = null;
    
                }
    
                delete m_oItems[sId];
                
                p_oMenuItem.destroyEvent.unsubscribe(onItemDestroy);
    
    
            }

        }
    
    
        /**
        * @method onItemAdded
        * @description "itemadded" event handler for a Menu instance.
        * @private
        * @param {String} p_sType String representing the name of the event  
        * that was fired.
        * @param {Array} p_aArgs Array of arguments sent when the event 
        * was fired.
        */
        function onItemAdded(p_sType, p_aArgs) {
    
            var oItem = p_aArgs[0],
                sId;
    
            if (oItem instanceof YAHOO.widget.MenuItem) { 
    
                sId = oItem.id;
        
                if (!m_oItems[sId]) {
            
                    m_oItems[sId] = oItem;
        
                    oItem.destroyEvent.subscribe(onItemDestroy);
        
        
                }
    
            }
        
        }
    
    
        return {
    
            // Privileged methods
    
    
            /**
            * @method addMenu
            * @description Adds a menu to the collection of known menus.
            * @param {YAHOO.widget.Menu} p_oMenu Object specifying the Menu  
            * instance to be added.
            */
            addMenu: function (p_oMenu) {
    
                var oDoc;
    
                if (p_oMenu instanceof YAHOO.widget.Menu && p_oMenu.id && 
                    !m_oMenus[p_oMenu.id]) {
        
                    m_oMenus[p_oMenu.id] = p_oMenu;
                
            
                    if (!m_bInitializedEventHandlers) {
            
                        oDoc = document;
                
                        Event.on(oDoc, _MOUSEOVER, onDOMEvent, this, true);
                        Event.on(oDoc, _MOUSEOUT, onDOMEvent, this, true);
                        Event.on(oDoc, _MOUSEDOWN, onDOMEvent, this, true);
                        Event.on(oDoc, _MOUSEUP, onDOMEvent, this, true);
                        Event.on(oDoc, _CLICK, onDOMEvent, this, true);
                        Event.on(oDoc, _KEYDOWN, onDOMEvent, this, true);
                        Event.on(oDoc, _KEYUP, onDOMEvent, this, true);
                        Event.on(oDoc, _KEYPRESS, onDOMEvent, this, true);
    
						Event.onFocus(oDoc, onDOMEvent, this, true);
						Event.onBlur(oDoc, onDOMEvent, this, true);						
    
                        m_bInitializedEventHandlers = true;
                        
            
                    }
            
                    p_oMenu.cfg.subscribeToConfigEvent(_VISIBLE, onMenuVisibleConfigChange);
                    p_oMenu.destroyEvent.subscribe(onMenuDestroy, p_oMenu, this);
                    p_oMenu.itemAddedEvent.subscribe(onItemAdded);
                    p_oMenu.focusEvent.subscribe(onMenuFocus);
                    p_oMenu.blurEvent.subscribe(onMenuBlur);
        
        
                }
        
            },
    
        
            /**
            * @method removeMenu
            * @description Removes a menu from the collection of known menus.
            * @param {YAHOO.widget.Menu} p_oMenu Object specifying the Menu  
            * instance to be removed.
            */
            removeMenu: function (p_oMenu) {
    
                var sId,
                    aItems,
                    sMenus,
                    i;
        
                if (p_oMenu) {
    
                    sId = p_oMenu.id;
        
                    if ((sId in m_oMenus) && (m_oMenus[sId] == p_oMenu)) {

                        // Unregister each menu item

                        aItems = p_oMenu.getItems();

                        if (aItems && aItems.length > 0) {

                            i = aItems.length - 1;

                            do {

                                removeItem(aItems[i]);

                            }
                            while (i--);

                        }
                        
                        sMenus = p_oMenu.getSubmenus();

                        // Unregister submenus
                        for(var x in sMenus) {
                            this.removeMenu(sMenus[x]);
                        }


                        // Unregister the menu

                        delete m_oMenus[sId];
            
        

                        /*
                             Unregister the menu from the collection of 
                             visible menus
                        */

                        if ((sId in m_oVisibleMenus) && (m_oVisibleMenus[sId] == p_oMenu)) {
            
                            delete m_oVisibleMenus[sId];
                            
       
                        }


                        // Unsubscribe event listeners

                        if (p_oMenu.cfg) {

                            p_oMenu.cfg.unsubscribeFromConfigEvent(_VISIBLE, 
                                onMenuVisibleConfigChange);
                            
                        }

                        p_oMenu.destroyEvent.unsubscribe(onMenuDestroy, 
                            p_oMenu);
                
                        p_oMenu.itemAddedEvent.unsubscribe(onItemAdded);
                        p_oMenu.focusEvent.unsubscribe(onMenuFocus);
                        p_oMenu.blurEvent.unsubscribe(onMenuBlur);

                    }
                
                }
    
            },

            removeMenuWithId: function(menuId) {
                this.removeMenu(m_oMenus[menuId]);
            },
        
            /**
            * @method hideVisible
            * @description Hides all visible, dynamically positioned menus 
            * (excluding instances of YAHOO.widget.MenuBar).
            */
            hideVisible: function () {
        
                var oMenu;
        
                for (var i in m_oVisibleMenus) {
        
                    if (Lang.hasOwnProperty(m_oVisibleMenus, i)) {
        
                        oMenu = m_oVisibleMenus[i];
        
                        if (!(oMenu instanceof YAHOO.widget.MenuBar) && 
                            oMenu.cfg.getProperty(_POSITION) == _DYNAMIC) {
        
                            oMenu.hide();
        
                        }
        
                    }
        
                }        
    
            },


            /**
            * @method getVisible
            * @description Returns a collection of all visible menus registered
            * with the menu manger.
            * @return {Object}
            */
            getVisible: function () {
            
                return m_oVisibleMenus;
            
            },

    
            /**
            * @method getMenus
            * @description Returns a collection of all menus registered with the 
            * menu manger.
            * @return {Object}
            */
            getMenus: function () {
    
                return m_oMenus;
            
            },
    
    
            /**
            * @method getMenu
            * @description Returns a menu with the specified id.
            * @param {String} p_sId String specifying the id of the 
            * <code>&#60;div&#62;</code> element representing the menu to
            * be retrieved.
            * @return {YAHOO.widget.Menu}
            */
            getMenu: function (p_sId) {
                
                var returnVal;
                
                if (p_sId in m_oMenus) {
                
					returnVal = m_oMenus[p_sId];
				
				}
            
            	return returnVal;
            
            },
    
    
            /**
            * @method getMenuItem
            * @description Returns a menu item with the specified id.
            * @param {String} p_sId String specifying the id of the 
            * <code>&#60;li&#62;</code> element representing the menu item to
            * be retrieved.
            * @return {YAHOO.widget.MenuItem}
            */
            getMenuItem: function (p_sId) {
    
    			var returnVal;
    
    			if (p_sId in m_oItems) {
    
					returnVal = m_oItems[p_sId];
				
				}
				
				return returnVal;
            
            },


            /**
            * @method getMenuItemGroup
            * @description Returns an array of menu item instances whose 
            * corresponding <code>&#60;li&#62;</code> elements are child 
            * nodes of the <code>&#60;ul&#62;</code> element with the 
            * specified id.
            * @param {String} p_sId String specifying the id of the 
            * <code>&#60;ul&#62;</code> element representing the group of 
            * menu items to be retrieved.
            * @return {Array}
            */
            getMenuItemGroup: function (p_sId) {

                var oUL = Dom.get(p_sId),
                    aItems,
                    oNode,
                    oItem,
                    sId,
                    returnVal;
    

                if (oUL && oUL.tagName && oUL.tagName.toUpperCase() == _UL) {

                    oNode = oUL.firstChild;

                    if (oNode) {

                        aItems = [];
                        
                        do {

                            sId = oNode.id;

                            if (sId) {
                            
                                oItem = this.getMenuItem(sId);
                                
                                if (oItem) {
                                
                                    aItems[aItems.length] = oItem;
                                
                                }
                            
                            }
                        
                        }
                        while ((oNode = oNode.nextSibling));


                        if (aItems.length > 0) {

                            returnVal = aItems;
                        
                        }

                    }
                
                }

				return returnVal;
            
            },

    
            /**
            * @method getFocusedMenuItem
            * @description Returns a reference to the menu item that currently 
            * has focus.
            * @return {YAHOO.widget.MenuItem}
            */
            getFocusedMenuItem: function () {
    
                return m_oFocusedMenuItem;
    
            },
    
    
            /**
            * @method getFocusedMenu
            * @description Returns a reference to the menu that currently 
            * has focus.
            * @return {YAHOO.widget.Menu}
            */
            getFocusedMenu: function () {

				var returnVal;
    
                if (m_oFocusedMenuItem) {
    
                    returnVal = m_oFocusedMenuItem.parent.getRoot();
                
                }
    
    			return returnVal;
    
            },
    
        
            /**
            * @method toString
            * @description Returns a string representing the menu manager.
            * @return {String}
            */
            toString: function () {
            
                return _MENUMANAGER;
            
            }
    
        };
    
    }();

})();



(function () {

	var Lang = YAHOO.lang,

	// String constants
	
		_MENU = "Menu",
		_DIV_UPPERCASE = "DIV",
		_DIV_LOWERCASE = "div",
		_ID = "id",
		_SELECT = "SELECT",
		_XY = "xy",
		_Y = "y",
		_UL_UPPERCASE = "UL",
		_UL_LOWERCASE = "ul",
		_FIRST_OF_TYPE = "first-of-type",
		_LI = "LI",
		_OPTGROUP = "OPTGROUP",
		_OPTION = "OPTION",
		_DISABLED = "disabled",
		_NONE = "none",
		_SELECTED = "selected",
		_GROUP_INDEX = "groupindex",
		_INDEX = "index",
		_SUBMENU = "submenu",
		_VISIBLE = "visible",
		_HIDE_DELAY = "hidedelay",
		_POSITION = "position",
		_DYNAMIC = "dynamic",
		_STATIC = "static",
		_DYNAMIC_STATIC = _DYNAMIC + "," + _STATIC,
		_URL = "url",
		_HASH = "#",
		_TARGET = "target",
		_MAX_HEIGHT = "maxheight",
        _TOP_SCROLLBAR = "topscrollbar",
        _BOTTOM_SCROLLBAR = "bottomscrollbar",
        _UNDERSCORE = "_",
		_TOP_SCROLLBAR_DISABLED = _TOP_SCROLLBAR + _UNDERSCORE + _DISABLED,
		_BOTTOM_SCROLLBAR_DISABLED = _BOTTOM_SCROLLBAR + _UNDERSCORE + _DISABLED,
		_MOUSEMOVE = "mousemove",
		_SHOW_DELAY = "showdelay",
		_SUBMENU_HIDE_DELAY = "submenuhidedelay",
		_IFRAME = "iframe",
		_CONSTRAIN_TO_VIEWPORT = "constraintoviewport",
		_PREVENT_CONTEXT_OVERLAP = "preventcontextoverlap",
		_SUBMENU_ALIGNMENT = "submenualignment",
		_AUTO_SUBMENU_DISPLAY = "autosubmenudisplay",
		_CLICK_TO_HIDE = "clicktohide",
		_CONTAINER = "container",
		_SCROLL_INCREMENT = "scrollincrement",
		_MIN_SCROLL_HEIGHT = "minscrollheight",
		_CLASSNAME = "classname",
		_SHADOW = "shadow",
		_KEEP_OPEN = "keepopen",
		_HD = "hd",
		_HAS_TITLE = "hastitle",
		_CONTEXT = "context",
		_EMPTY_STRING = "",
		_MOUSEDOWN = "mousedown",
		_KEYDOWN = "keydown",
		_HEIGHT = "height",
		_WIDTH = "width",
		_PX = "px",
		_EFFECT = "effect",
		_MONITOR_RESIZE = "monitorresize",
		_DISPLAY = "display",
		_BLOCK = "block",
		_VISIBILITY = "visibility",
		_ABSOLUTE = "absolute",
		_ZINDEX = "zindex",
		_YUI_MENU_BODY_SCROLLED = "yui-menu-body-scrolled",
		_NON_BREAKING_SPACE = "&#32;",
		_SPACE = " ",
		_MOUSEOVER = "mouseover",
		_MOUSEOUT = "mouseout",
        _ITEM_ADDED = "itemAdded",
        _ITEM_REMOVED = "itemRemoved",
        _HIDDEN = "hidden",
        _YUI_MENU_SHADOW = "yui-menu-shadow",
        _YUI_MENU_SHADOW_VISIBLE = _YUI_MENU_SHADOW + "-visible",
        _YUI_MENU_SHADOW_YUI_MENU_SHADOW_VISIBLE = _YUI_MENU_SHADOW + _SPACE + _YUI_MENU_SHADOW_VISIBLE;


/**
* The Menu class creates a container that holds a vertical list representing 
* a set of options or commands.  Menu is the base class for all 
* menu containers. 
* @param {String} p_oElement String specifying the id attribute of the 
* <code>&#60;div&#62;</code> element of the menu.
* @param {String} p_oElement String specifying the id attribute of the 
* <code>&#60;select&#62;</code> element to be used as the data source 
* for the menu.
* @param {<a href="http://www.w3.org/TR/2000/WD-DOM-Level-1-20000929/
* level-one-html.html#ID-22445964">HTMLDivElement</a>} p_oElement Object 
* specifying the <code>&#60;div&#62;</code> element of the menu.
* @param {<a href="http://www.w3.org/TR/2000/WD-DOM-Level-1-20000929/
* level-one-html.html#ID-94282980">HTMLSelectElement</a>} p_oElement 
* Object specifying the <code>&#60;select&#62;</code> element to be used as 
* the data source for the menu.
* @param {Object} p_oConfig Optional. Object literal specifying the 
* configuration for the menu. See configuration class documentation for 
* more details.
* @namespace YAHOO.widget
* @class Menu
* @constructor
* @extends YAHOO.widget.Overlay
*/
YAHOO.widget.Menu = function (p_oElement, p_oConfig) {

    if (p_oConfig) {

        this.parent = p_oConfig.parent;
        this.lazyLoad = p_oConfig.lazyLoad || p_oConfig.lazyload;
        this.itemData = p_oConfig.itemData || p_oConfig.itemdata;

    }


    YAHOO.widget.Menu.superclass.constructor.call(this, p_oElement, p_oConfig);

};



/**
* @method checkPosition
* @description Checks to make sure that the value of the "position" property 
* is one of the supported strings. Returns true if the position is supported.
* @private
* @param {Object} p_sPosition String specifying the position of the menu.
* @return {Boolean}
*/
function checkPosition(p_sPosition) {

	var returnVal = false;

    if (Lang.isString(p_sPosition)) {

        returnVal = (_DYNAMIC_STATIC.indexOf((p_sPosition.toLowerCase())) != -1);

    }

	return returnVal;

}


var Dom = YAHOO.util.Dom,
    Event = YAHOO.util.Event,
    Module = YAHOO.widget.Module,
    Overlay = YAHOO.widget.Overlay,
    Menu = YAHOO.widget.Menu,
    MenuManager = YAHOO.widget.MenuManager,
    CustomEvent = YAHOO.util.CustomEvent,
    UA = YAHOO.env.ua,
    
    m_oShadowTemplate,

	bFocusListenerInitialized = false,

	oFocusedElement,

	EVENT_TYPES = [
    
		["mouseOverEvent", _MOUSEOVER],
		["mouseOutEvent", _MOUSEOUT],
		["mouseDownEvent", _MOUSEDOWN],
		["mouseUpEvent", "mouseup"],
		["clickEvent", "click"],
		["keyPressEvent", "keypress"],
		["keyDownEvent", _KEYDOWN],
		["keyUpEvent", "keyup"],
		["focusEvent", "focus"],
		["blurEvent", "blur"],
		["itemAddedEvent", _ITEM_ADDED],
		["itemRemovedEvent", _ITEM_REMOVED]

	],

	VISIBLE_CONFIG =  { 
		key: _VISIBLE, 
		value: false, 
		validator: Lang.isBoolean
	}, 

	CONSTRAIN_TO_VIEWPORT_CONFIG =  {
		key: _CONSTRAIN_TO_VIEWPORT, 
		value: true, 
		validator: Lang.isBoolean, 
		supercedes: [_IFRAME,"x",_Y,_XY]
	}, 

	PREVENT_CONTEXT_OVERLAP_CONFIG =  {
		key: _PREVENT_CONTEXT_OVERLAP,
		value: true,
		validator: Lang.isBoolean,  
		supercedes: [_CONSTRAIN_TO_VIEWPORT]
	},

	POSITION_CONFIG =  { 
		key: _POSITION, 
		value: _DYNAMIC, 
		validator: checkPosition, 
		supercedes: [_VISIBLE, _IFRAME]
	}, 

	SUBMENU_ALIGNMENT_CONFIG =  { 
		key: _SUBMENU_ALIGNMENT, 
		value: ["tl","tr"]
	},

	AUTO_SUBMENU_DISPLAY_CONFIG =  { 
		key: _AUTO_SUBMENU_DISPLAY, 
		value: true, 
		validator: Lang.isBoolean,
		suppressEvent: true
	}, 

	SHOW_DELAY_CONFIG =  { 
		key: _SHOW_DELAY, 
		value: 250, 
		validator: Lang.isNumber, 
		suppressEvent: true
	}, 

	HIDE_DELAY_CONFIG =  { 
		key: _HIDE_DELAY, 
		value: 0, 
		validator: Lang.isNumber, 
		suppressEvent: true
	}, 

	SUBMENU_HIDE_DELAY_CONFIG =  { 
		key: _SUBMENU_HIDE_DELAY, 
		value: 250, 
		validator: Lang.isNumber,
		suppressEvent: true
	}, 

	CLICK_TO_HIDE_CONFIG =  { 
		key: _CLICK_TO_HIDE, 
		value: true, 
		validator: Lang.isBoolean,
		suppressEvent: true
	},

	CONTAINER_CONFIG =  { 
		key: _CONTAINER,
		suppressEvent: true
	}, 

	SCROLL_INCREMENT_CONFIG =  { 
		key: _SCROLL_INCREMENT, 
		value: 1, 
		validator: Lang.isNumber,
		supercedes: [_MAX_HEIGHT],
		suppressEvent: true
	},

	MIN_SCROLL_HEIGHT_CONFIG =  { 
		key: _MIN_SCROLL_HEIGHT, 
		value: 90, 
		validator: Lang.isNumber,
		supercedes: [_MAX_HEIGHT],
		suppressEvent: true
	},    

	MAX_HEIGHT_CONFIG =  { 
		key: _MAX_HEIGHT, 
		value: 0, 
		validator: Lang.isNumber,
		supercedes: [_IFRAME],
		suppressEvent: true
	}, 

	CLASS_NAME_CONFIG =  { 
		key: _CLASSNAME, 
		value: null, 
		validator: Lang.isString,
		suppressEvent: true
	}, 

	DISABLED_CONFIG =  { 
		key: _DISABLED, 
		value: false, 
		validator: Lang.isBoolean,
		suppressEvent: true
	},
	
	SHADOW_CONFIG =  { 
		key: _SHADOW, 
		value: false, 
		validator: Lang.isBoolean,
		suppressEvent: true,
		supercedes: [_VISIBLE]
	},
	
	KEEP_OPEN_CONFIG = {
		key: _KEEP_OPEN, 
		value: false, 
		validator: Lang.isBoolean
	};


function onDocFocus(event) {

	oFocusedElement = Event.getTarget(event);

}



YAHOO.lang.extend(Menu, Overlay, {


// Constants


/**
* @property CSS_CLASS_NAME
* @description String representing the CSS class(es) to be applied to the 
* menu's <code>&#60;div&#62;</code> element.
* @default "yuimenu"
* @final
* @type String
*/
CSS_CLASS_NAME: "ui-menu ui-widget ui-widget-content ui-corner-all",


/**
* @property ITEM_TYPE
* @description Object representing the type of menu item to instantiate and 
* add when parsing the child nodes (either <code>&#60;li&#62;</code> element, 
* <code>&#60;optgroup&#62;</code> element or <code>&#60;option&#62;</code>) 
* of the menu's source HTML element.
* @default YAHOO.widget.MenuItem
* @final
* @type YAHOO.widget.MenuItem
*/
ITEM_TYPE: null,


/**
* @property GROUP_TITLE_TAG_NAME
* @description String representing the tagname of the HTML element used to 
* title the menu's item groups.
* @default H6
* @final
* @type String
*/
GROUP_TITLE_TAG_NAME: "h6",


/**
* @property OFF_SCREEN_POSITION
* @description Array representing the default x and y position that a menu 
* should have when it is positioned outside the viewport by the 
* "poistionOffScreen" method.
* @default "-999em"
* @final
* @type String
*/
OFF_SCREEN_POSITION: "-999em",


// Private properties


/** 
* @property _useHideDelay
* @description Boolean indicating if the "mouseover" and "mouseout" event 
* handlers used for hiding the menu via a call to "YAHOO.lang.later" have 
* already been assigned.
* @default false
* @private
* @type Boolean
*/
_useHideDelay: false,


/**
* @property _bHandledMouseOverEvent
* @description Boolean indicating the current state of the menu's 
* "mouseover" event.
* @default false
* @private
* @type Boolean
*/
_bHandledMouseOverEvent: false,


/**
* @property _bHandledMouseOutEvent
* @description Boolean indicating the current state of the menu's
* "mouseout" event.
* @default false
* @private
* @type Boolean
*/
_bHandledMouseOutEvent: false,


/**
* @property _aGroupTitleElements
* @description Array of HTML element used to title groups of menu items.
* @default []
* @private
* @type Array
*/
_aGroupTitleElements: null,


/**
* @property _aItemGroups
* @description Multi-dimensional Array representing the menu items as they
* are grouped in the menu.
* @default []
* @private
* @type Array
*/
_aItemGroups: null,


/**
* @property _aListElements
* @description Array of <code>&#60;ul&#62;</code> elements, each of which is 
* the parent node for each item's <code>&#60;li&#62;</code> element.
* @default []
* @private
* @type Array
*/
_aListElements: null,


/**
* @property _nCurrentMouseX
* @description The current x coordinate of the mouse inside the area of 
* the menu.
* @default 0
* @private
* @type Number
*/
_nCurrentMouseX: 0,


/**
* @property _bStopMouseEventHandlers
* @description Stops "mouseover," "mouseout," and "mousemove" event handlers 
* from executing.
* @default false
* @private
* @type Boolean
*/
_bStopMouseEventHandlers: false,


/**
* @property _sClassName
* @description The current value of the "classname" configuration attribute.
* @default null
* @private
* @type String
*/
_sClassName: null,



// Public properties


/**
* @property lazyLoad
* @description Boolean indicating if the menu's "lazy load" feature is 
* enabled.  If set to "true," initialization and rendering of the menu's 
* items will be deferred until the first time it is made visible.  This 
* property should be set via the constructor using the configuration 
* object literal.
* @default false
* @type Boolean
*/
lazyLoad: false,


/**
* @property itemData
* @description Array of items to be added to the menu.  The array can contain 
* strings representing the text for each item to be created, object literals 
* representing the menu item configuration properties, or MenuItem instances.  
* This property should be set via the constructor using the configuration 
* object literal.
* @default null
* @type Array
*/
itemData: null,


/**
* @property activeItem
* @description Object reference to the item in the menu that has is selected.
* @default null
* @type YAHOO.widget.MenuItem
*/
activeItem: null,


/**
* @property parent
* @description Object reference to the menu's parent menu or menu item.  
* This property can be set via the constructor using the configuration 
* object literal.
* @default null
* @type YAHOO.widget.MenuItem
*/
parent: null,


/**
* @property srcElement
* @description Object reference to the HTML element (either 
* <code>&#60;select&#62;</code> or <code>&#60;div&#62;</code>) used to 
* create the menu.
* @default null
* @type <a href="http://www.w3.org/TR/2000/WD-DOM-Level-1-20000929/
* level-one-html.html#ID-94282980">HTMLSelectElement</a>|<a 
* href="http://www.w3.org/TR/2000/WD-DOM-Level-1-20000929/level-one-html.
* html#ID-22445964">HTMLDivElement</a>
*/
srcElement: null,



// Events


/**
* @event mouseOverEvent
* @description Fires when the mouse has entered the menu.  Passes back 
* the DOM Event object as an argument.
*/


/**
* @event mouseOutEvent
* @description Fires when the mouse has left the menu.  Passes back the DOM 
* Event object as an argument.
* @type YAHOO.util.CustomEvent
*/


/**
* @event mouseDownEvent
* @description Fires when the user mouses down on the menu.  Passes back the 
* DOM Event object as an argument.
* @type YAHOO.util.CustomEvent
*/


/**
* @event mouseUpEvent
* @description Fires when the user releases a mouse button while the mouse is 
* over the menu.  Passes back the DOM Event object as an argument.
* @type YAHOO.util.CustomEvent
*/


/**
* @event clickEvent
* @description Fires when the user clicks the on the menu.  Passes back the 
* DOM Event object as an argument.
* @type YAHOO.util.CustomEvent
*/


/**
* @event keyPressEvent
* @description Fires when the user presses an alphanumeric key when one of the
* menu's items has focus.  Passes back the DOM Event object as an argument.
* @type YAHOO.util.CustomEvent
*/


/**
* @event keyDownEvent
* @description Fires when the user presses a key when one of the menu's items 
* has focus.  Passes back the DOM Event object as an argument.
* @type YAHOO.util.CustomEvent
*/


/**
* @event keyUpEvent
* @description Fires when the user releases a key when one of the menu's items 
* has focus.  Passes back the DOM Event object as an argument.
* @type YAHOO.util.CustomEvent
*/


/**
* @event itemAddedEvent
* @description Fires when an item is added to the menu.
* @type YAHOO.util.CustomEvent
*/


/**
* @event itemRemovedEvent
* @description Fires when an item is removed to the menu.
* @type YAHOO.util.CustomEvent
*/


/**
* @method init
* @description The Menu class's initialization method. This method is 
* automatically called by the constructor, and sets up all DOM references 
* for pre-existing markup, and creates required markup if it is not 
* already present.
* @param {String} p_oElement String specifying the id attribute of the 
* <code>&#60;div&#62;</code> element of the menu.
* @param {String} p_oElement String specifying the id attribute of the 
* <code>&#60;select&#62;</code> element to be used as the data source 
* for the menu.
* @param {<a href="http://www.w3.org/TR/2000/WD-DOM-Level-1-20000929/
* level-one-html.html#ID-22445964">HTMLDivElement</a>} p_oElement Object 
* specifying the <code>&#60;div&#62;</code> element of the menu.
* @param {<a href="http://www.w3.org/TR/2000/WD-DOM-Level-1-20000929/
* level-one-html.html#ID-94282980">HTMLSelectElement</a>} p_oElement 
* Object specifying the <code>&#60;select&#62;</code> element to be used as 
* the data source for the menu.
* @param {Object} p_oConfig Optional. Object literal specifying the 
* configuration for the menu. See configuration class documentation for 
* more details.
*/
init: function (p_oElement, p_oConfig) {

    this._aItemGroups = [];
    this._aListElements = [];
    this._aGroupTitleElements = [];

    if (!this.ITEM_TYPE) {

        this.ITEM_TYPE = YAHOO.widget.MenuItem;

    }


    var oElement;

    if (Lang.isString(p_oElement)) {

        oElement = Dom.get(p_oElement);

    }
    else if (p_oElement.tagName) {

        oElement = p_oElement;

    }


    if (oElement && oElement.tagName) {

        switch(oElement.tagName.toUpperCase()) {
    
            case _DIV_UPPERCASE:

                this.srcElement = oElement;

                if (!oElement.id) {

                    oElement.setAttribute(_ID, Dom.generateId());

                }


                /* 
                    Note: we don't pass the user config in here yet 
                    because we only want it executed once, at the lowest 
                    subclass level.
                */ 
            
                Menu.superclass.init.call(this, oElement);

                this.beforeInitEvent.fire(Menu);

    
            break;
    
            case _SELECT:
    
                this.srcElement = oElement;

    
                /*
                    The source element is not something that we can use 
                    outright, so we need to create a new Overlay

                    Note: we don't pass the user config in here yet 
                    because we only want it executed once, at the lowest 
                    subclass level.
                */ 

                Menu.superclass.init.call(this, Dom.generateId());

                this.beforeInitEvent.fire(Menu);


            break;

        }

    }
    else {

        /* 
            Note: we don't pass the user config in here yet 
            because we only want it executed once, at the lowest 
            subclass level.
        */ 
    
        Menu.superclass.init.call(this, p_oElement);

        this.beforeInitEvent.fire(Menu);


    }


    if (this.element) {

        Dom.addClass(this.element, this.CSS_CLASS_NAME);


        // Subscribe to Custom Events

        this.initEvent.subscribe(this._onInit);
        this.beforeRenderEvent.subscribe(this._onBeforeRender);
        this.renderEvent.subscribe(this._onRender);
        this.beforeShowEvent.subscribe(this._onBeforeShow);
		this.hideEvent.subscribe(this._onHide);
        this.showEvent.subscribe(this._onShow);
		this.beforeHideEvent.subscribe(this._onBeforeHide);
        this.mouseOverEvent.subscribe(this._onMouseOver);
        this.mouseOutEvent.subscribe(this._onMouseOut);
        this.clickEvent.subscribe(this._onClick);
        this.keyDownEvent.subscribe(this._onKeyDown);
        this.keyPressEvent.subscribe(this._onKeyPress);
        this.blurEvent.subscribe(this._onBlur);


		if (!bFocusListenerInitialized) {
			Event.onFocus(document, onDocFocus);
			bFocusListenerInitialized = true;
		}


		//	Fixes an issue in Firefox 2 and Webkit where Dom's "getX" and "getY" 
		//	methods return values that don't take scrollTop into consideration 

        if ((UA.gecko && UA.gecko < 1.9) || UA.webkit) {

            this.cfg.subscribeToConfigEvent(_Y, this._onYChange);

        }


        if (p_oConfig) {
    
            this.cfg.applyConfig(p_oConfig, true);
    
        }


        // Register the Menu instance with the MenuManager

        MenuManager.addMenu(this);


        this.initEvent.fire(Menu);

    }

},



// Private methods


/**
* @method _initSubTree
* @description Iterates the childNodes of the source element to find nodes 
* used to instantiate menu and menu items.
* @private
*/
_initSubTree: function () {

    var oSrcElement = this.srcElement,
        sSrcElementTagName,
        nGroup,
        sGroupTitleTagName,
        oNode,
        aListElements,
        nListElements,
        i;


    if (oSrcElement) {
    
        sSrcElementTagName = 
            (oSrcElement.tagName && oSrcElement.tagName.toUpperCase());


        if (sSrcElementTagName == _DIV_UPPERCASE) {
    
            //  Populate the collection of item groups and item group titles
    
            oNode = this.body.firstChild;
    

            if (oNode) {
    
                nGroup = 0;
                sGroupTitleTagName = this.GROUP_TITLE_TAG_NAME.toUpperCase();
        
                do {
        

                    if (oNode && oNode.tagName) {
        
                        switch (oNode.tagName.toUpperCase()) {
        
                            case sGroupTitleTagName:
                            
                                this._aGroupTitleElements[nGroup] = oNode;
        
                            break;
        
                            case _UL_UPPERCASE:
        
                                this._aListElements[nGroup] = oNode;
                                this._aItemGroups[nGroup] = [];
                                nGroup++;
        
                            break;
        
                        }
                    
                    }
        
                }
                while ((oNode = oNode.nextSibling));
        
        
                /*
                    Apply the "first-of-type" class to the first UL to mimic 
                    the ":first-of-type" CSS3 psuedo class.
                */
        
                if (this._aListElements[0]) {
        
                    Dom.addClass(this._aListElements[0], _FIRST_OF_TYPE);
        
                }
            
            }
    
        }
    
    
        oNode = null;
    
    

        if (sSrcElementTagName) {
    
            switch (sSrcElementTagName) {
        
                case _DIV_UPPERCASE:

                    aListElements = this._aListElements;
                    nListElements = aListElements.length;
        
                    if (nListElements > 0) {
        
        
                        i = nListElements - 1;
        
                        do {
        
                            oNode = aListElements[i].firstChild;
            
                            if (oNode) {

            
                                do {
                
                                    if (oNode && oNode.tagName && 
                                        oNode.tagName.toUpperCase() == _LI) {
                
        
                                        this.addItem(new this.ITEM_TYPE(oNode, 
                                                    { parent: this }), i);
            
                                    }
                        
                                }
                                while ((oNode = oNode.nextSibling));
                            
                            }
                    
                        }
                        while (i--);
        
                    }
        
                break;
        
                case _SELECT:
        
        
                    oNode = oSrcElement.firstChild;
        
                    do {
        
                        if (oNode && oNode.tagName) {
                        
                            switch (oNode.tagName.toUpperCase()) {
            
                                case _OPTGROUP:
                                case _OPTION:
            
            
                                    this.addItem(
                                            new this.ITEM_TYPE(
                                                    oNode, 
                                                    { parent: this }
                                                )
                                            );
            
                                break;
            
                            }
    
                        }
        
                    }
                    while ((oNode = oNode.nextSibling));
        
                break;
        
            }
    
        }    
    
    }

},


/**
* @method _getFirstEnabledItem
* @description Returns the first enabled item in the menu.
* @return {YAHOO.widget.MenuItem}
* @private
*/
_getFirstEnabledItem: function () {

    var aItems = this.getItems(),
        nItems = aItems.length,
        oItem,
        returnVal;
    

    for(var i=0; i<nItems; i++) {

        oItem = aItems[i];

        if (oItem && !oItem.cfg.getProperty(_DISABLED) && oItem.element.style.display != _NONE) {

            returnVal = oItem;
            break;

        }
    
    }
    
    return returnVal;
    
},


/**
* @method _addItemToGroup
* @description Adds a menu item to a group.
* @private
* @param {Number} p_nGroupIndex Number indicating the group to which the 
* item belongs.
* @param {YAHOO.widget.MenuItem} p_oItem Object reference for the MenuItem 
* instance to be added to the menu.
* @param {String} p_oItem String specifying the text of the item to be added 
* to the menu.
* @param {Object} p_oItem Object literal containing a set of menu item 
* configuration properties.
* @param {Number} p_nItemIndex Optional. Number indicating the index at 
* which the menu item should be added.
* @return {YAHOO.widget.MenuItem}
*/
_addItemToGroup: function (p_nGroupIndex, p_oItem, p_nItemIndex) {

    var oItem,
        nGroupIndex,
        aGroup,
        oGroupItem,
        bAppend,
        oNextItemSibling,
        nItemIndex,
        returnVal;


    function getNextItemSibling(p_aArray, p_nStartIndex) {

        return (p_aArray[p_nStartIndex] || getNextItemSibling(p_aArray, (p_nStartIndex+1)));

    }


    if (p_oItem instanceof this.ITEM_TYPE) {

        oItem = p_oItem;
        oItem.parent = this;

    }
    else if (Lang.isString(p_oItem)) {

        oItem = new this.ITEM_TYPE(p_oItem, { parent: this });
    
    }
    else if (Lang.isObject(p_oItem)) {

        p_oItem.parent = this;

        oItem = new this.ITEM_TYPE(p_oItem.text, p_oItem);

    }


    if (oItem) {

        if (oItem.cfg.getProperty(_SELECTED)) {

            this.activeItem = oItem;
        
        }


        nGroupIndex = Lang.isNumber(p_nGroupIndex) ? p_nGroupIndex : 0;
        aGroup = this._getItemGroup(nGroupIndex);



        if (!aGroup) {

            aGroup = this._createItemGroup(nGroupIndex);

        }


        if (Lang.isNumber(p_nItemIndex)) {

            bAppend = (p_nItemIndex >= aGroup.length);            


            if (aGroup[p_nItemIndex]) {
    
                aGroup.splice(p_nItemIndex, 0, oItem);
    
            }
            else {
    
                aGroup[p_nItemIndex] = oItem;
    
            }


            oGroupItem = aGroup[p_nItemIndex];

            if (oGroupItem) {

                if (bAppend && (!oGroupItem.element.parentNode || 
                        oGroupItem.element.parentNode.nodeType == 11)) {
        
                    this._aListElements[nGroupIndex].appendChild(oGroupItem.element);
    
                }
                else {
    
                    oNextItemSibling = getNextItemSibling(aGroup, (p_nItemIndex+1));
    
                    if (oNextItemSibling && (!oGroupItem.element.parentNode || 
                            oGroupItem.element.parentNode.nodeType == 11)) {
            
                        this._aListElements[nGroupIndex].insertBefore(
                                oGroupItem.element, oNextItemSibling.element);
        
                    }
    
                }
    

                oGroupItem.parent = this;
        
                this._subscribeToItemEvents(oGroupItem);
    
                this._configureSubmenu(oGroupItem);
                
                this._updateItemProperties(nGroupIndex);
        

                this.itemAddedEvent.fire(oGroupItem);
                this.changeContentEvent.fire();

                returnVal = oGroupItem;
    
            }

        }
        else {
    
            nItemIndex = aGroup.length;
    
            aGroup[nItemIndex] = oItem;

            oGroupItem = aGroup[nItemIndex];
    

            if (oGroupItem) {
    
                if (!Dom.isAncestor(this._aListElements[nGroupIndex], oGroupItem.element)) {
    
                    this._aListElements[nGroupIndex].appendChild(oGroupItem.element);
    
                }
    
                oGroupItem.element.setAttribute(_GROUP_INDEX, nGroupIndex);
                oGroupItem.element.setAttribute(_INDEX, nItemIndex);
        
                oGroupItem.parent = this;
    
                oGroupItem.index = nItemIndex;
                oGroupItem.groupIndex = nGroupIndex;
        
                this._subscribeToItemEvents(oGroupItem);
    
                this._configureSubmenu(oGroupItem);
    
                if (nItemIndex === 0) {
        
                    Dom.addClass(oGroupItem.element, _FIRST_OF_TYPE);
        
                }

        

                this.itemAddedEvent.fire(oGroupItem);
                this.changeContentEvent.fire();

                returnVal = oGroupItem;
    
            }
    
        }

    }
    
    return returnVal;
    
},


/**
* @method _removeItemFromGroupByIndex
* @description Removes a menu item from a group by index.  Returns the menu 
* item that was removed.
* @private
* @param {Number} p_nGroupIndex Number indicating the group to which the menu 
* item belongs.
* @param {Number} p_nItemIndex Number indicating the index of the menu item 
* to be removed.
* @return {YAHOO.widget.MenuItem}
*/
_removeItemFromGroupByIndex: function (p_nGroupIndex, p_nItemIndex) {

    var nGroupIndex = Lang.isNumber(p_nGroupIndex) ? p_nGroupIndex : 0,
        aGroup = this._getItemGroup(nGroupIndex),
        aArray,
        oItem,
        oUL;

    if (aGroup) {

        aArray = aGroup.splice(p_nItemIndex, 1);
        oItem = aArray[0];
    
        if (oItem) {
    
            // Update the index and className properties of each member        
            
            this._updateItemProperties(nGroupIndex);
    
            if (aGroup.length === 0) {
    
                // Remove the UL
    
                oUL = this._aListElements[nGroupIndex];
    
                if (this.body && oUL) {
    
                    this.body.removeChild(oUL);
    
                }
    
                // Remove the group from the array of items
    
                this._aItemGroups.splice(nGroupIndex, 1);
    
    
                // Remove the UL from the array of ULs
    
                this._aListElements.splice(nGroupIndex, 1);
    
    
                /*
                     Assign the "first-of-type" class to the new first UL 
                     in the collection
                */
    
                oUL = this._aListElements[0];
    
                if (oUL) {
    
                    Dom.addClass(oUL, _FIRST_OF_TYPE);
    
                }            
    
            }
    

            this.itemRemovedEvent.fire(oItem);
            this.changeContentEvent.fire();
    
        }

    }

	// Return a reference to the item that was removed

	return oItem;
    
},


/**
* @method _removeItemFromGroupByValue
* @description Removes a menu item from a group by reference.  Returns the 
* menu item that was removed.
* @private
* @param {Number} p_nGroupIndex Number indicating the group to which the
* menu item belongs.
* @param {YAHOO.widget.MenuItem} p_oItem Object reference for the MenuItem 
* instance to be removed.
* @return {YAHOO.widget.MenuItem}
*/    
_removeItemFromGroupByValue: function (p_nGroupIndex, p_oItem) {

    var aGroup = this._getItemGroup(p_nGroupIndex),
        nItems,
        nItemIndex,
        returnVal,
        i;

    if (aGroup) {

        nItems = aGroup.length;
        nItemIndex = -1;
    
        if (nItems > 0) {
    
            i = nItems-1;
        
            do {
        
                if (aGroup[i] == p_oItem) {
        
                    nItemIndex = i;
                    break;    
        
                }
        
            }
            while (i--);
        
            if (nItemIndex > -1) {
        
                returnVal = this._removeItemFromGroupByIndex(p_nGroupIndex, nItemIndex);
        
            }
    
        }
    
    }
    
    return returnVal;

},


/**
* @method _updateItemProperties
* @description Updates the "index," "groupindex," and "className" properties 
* of the menu items in the specified group. 
* @private
* @param {Number} p_nGroupIndex Number indicating the group of items to update.
*/
_updateItemProperties: function (p_nGroupIndex) {

    var aGroup = this._getItemGroup(p_nGroupIndex),
        nItems = aGroup.length,
        oItem,
        oLI,
        i;


    if (nItems > 0) {

        i = nItems - 1;

        // Update the index and className properties of each member
    
        do {

            oItem = aGroup[i];

            if (oItem) {
    
                oLI = oItem.element;

                oItem.index = i;
                oItem.groupIndex = p_nGroupIndex;

                oLI.setAttribute(_GROUP_INDEX, p_nGroupIndex);
                oLI.setAttribute(_INDEX, i);

                Dom.removeClass(oLI, _FIRST_OF_TYPE);

            }
    
        }
        while (i--);


        if (oLI) {

            Dom.addClass(oLI, _FIRST_OF_TYPE);

        }

    }

},


/**
* @method _createItemGroup
* @description Creates a new menu item group (array) and its associated 
* <code>&#60;ul&#62;</code> element. Returns an aray of menu item groups.
* @private
* @param {Number} p_nIndex Number indicating the group to create.
* @return {Array}
*/
_createItemGroup: function (p_nIndex) {

    var oUL,
    	returnVal;

    if (!this._aItemGroups[p_nIndex]) {

        this._aItemGroups[p_nIndex] = [];

        oUL = document.createElement(_UL_LOWERCASE);

        this._aListElements[p_nIndex] = oUL;

        returnVal = this._aItemGroups[p_nIndex];

    }
    
    return returnVal;

},


/**
* @method _getItemGroup
* @description Returns the menu item group at the specified index.
* @private
* @param {Number} p_nIndex Number indicating the index of the menu item group 
* to be retrieved.
* @return {Array}
*/
_getItemGroup: function (p_nIndex) {

    var nIndex = Lang.isNumber(p_nIndex) ? p_nIndex : 0,
    	aGroups = this._aItemGroups,
    	returnVal;

	if (nIndex in aGroups) {

	    returnVal = aGroups[nIndex];

	}
	
	return returnVal;

},


/**
* @method _configureSubmenu
* @description Subscribes the menu item's submenu to its parent menu's events.
* @private
* @param {YAHOO.widget.MenuItem} p_oItem Object reference for the MenuItem 
* instance with the submenu to be configured.
*/
_configureSubmenu: function (p_oItem) {

    var oSubmenu = p_oItem.cfg.getProperty(_SUBMENU);

    if (oSubmenu) {
            
        /*
            Listen for configuration changes to the parent menu 
            so they they can be applied to the submenu.
        */

        this.cfg.configChangedEvent.subscribe(this._onParentMenuConfigChange, oSubmenu, true);

        this.renderEvent.subscribe(this._onParentMenuRender, oSubmenu, true);

    }

},




/**
* @method _subscribeToItemEvents
* @description Subscribes a menu to a menu item's event.
* @private
* @param {YAHOO.widget.MenuItem} p_oItem Object reference for the MenuItem 
* instance whose events should be subscribed to.
*/
_subscribeToItemEvents: function (p_oItem) {

    p_oItem.destroyEvent.subscribe(this._onMenuItemDestroy, p_oItem, this);
    p_oItem.cfg.configChangedEvent.subscribe(this._onMenuItemConfigChange, p_oItem, this);

},


/**
* @method _onVisibleChange
* @description Change event handler for the the menu's "visible" configuration
* property.
* @private
* @param {String} p_sType String representing the name of the event that 
* was fired.
* @param {Array} p_aArgs Array of arguments sent when the event was fired.
*/
_onVisibleChange: function (p_sType, p_aArgs) {

    var bVisible = p_aArgs[0];
    
    if (bVisible) {

        Dom.addClass(this.element, _VISIBLE);

    }
    else {

        Dom.removeClass(this.element, _VISIBLE);

    }

},


/**
* @method _cancelHideDelay
* @description Cancels the call to "hideMenu."
* @private
*/
_cancelHideDelay: function () {

    var oTimer = this.getRoot()._hideDelayTimer;

    if (oTimer) {

		oTimer.cancel();

    }

},


/**
* @method _execHideDelay
* @description Hides the menu after the number of milliseconds specified by 
* the "hidedelay" configuration property.
* @private
*/
_execHideDelay: function () {

    this._cancelHideDelay();

    var oRoot = this.getRoot();
        
	oRoot._hideDelayTimer = Lang.later(oRoot.cfg.getProperty(_HIDE_DELAY), this, function () {
    
        if (oRoot.activeItem) {

			if (oRoot.hasFocus()) {

				oRoot.activeItem.focus();
			
			}
			
            oRoot.clearActiveItem();

        }

        if (oRoot == this && !(this instanceof YAHOO.widget.MenuBar) && 
            this.cfg.getProperty(_POSITION) == _DYNAMIC) {

            this.hide();
        
        }
    
    });

},


/**
* @method _cancelShowDelay
* @description Cancels the call to the "showMenu."
* @private
*/
_cancelShowDelay: function () {

    var oTimer = this.getRoot()._showDelayTimer;

    if (oTimer) {

        oTimer.cancel();

    }

},


/**
* @method _execSubmenuHideDelay
* @description Hides a submenu after the number of milliseconds specified by 
* the "submenuhidedelay" configuration property have ellapsed.
* @private
* @param {YAHOO.widget.Menu} p_oSubmenu Object specifying the submenu that  
* should be hidden.
* @param {Number} p_nMouseX The x coordinate of the mouse when it left 
* the specified submenu's parent menu item.
* @param {Number} p_nHideDelay The number of milliseconds that should ellapse
* before the submenu is hidden.
*/
_execSubmenuHideDelay: function (p_oSubmenu, p_nMouseX, p_nHideDelay) {

	p_oSubmenu._submenuHideDelayTimer = Lang.later(50, this, function () {

        if (this._nCurrentMouseX > (p_nMouseX + 10)) {

            p_oSubmenu._submenuHideDelayTimer = Lang.later(p_nHideDelay, p_oSubmenu, function () {
        
                this.hide();

            });

        }
        else {

            p_oSubmenu.hide();
        
        }
	
	});

},



// Protected methods


/**
* @method _disableScrollHeader
* @description Disables the header used for scrolling the body of the menu.
* @protected
*/
_disableScrollHeader: function () {

    if (!this._bHeaderDisabled) {

        Dom.addClass(this.header, _TOP_SCROLLBAR_DISABLED);
        this._bHeaderDisabled = true;

    }

},


/**
* @method _disableScrollFooter
* @description Disables the footer used for scrolling the body of the menu.
* @protected
*/
_disableScrollFooter: function () {

    if (!this._bFooterDisabled) {

        Dom.addClass(this.footer, _BOTTOM_SCROLLBAR_DISABLED);
        this._bFooterDisabled = true;

    }

},


/**
* @method _enableScrollHeader
* @description Enables the header used for scrolling the body of the menu.
* @protected
*/
_enableScrollHeader: function () {

    if (this._bHeaderDisabled) {

        Dom.removeClass(this.header, _TOP_SCROLLBAR_DISABLED);
        this._bHeaderDisabled = false;

    }

},


/**
* @method _enableScrollFooter
* @description Enables the footer used for scrolling the body of the menu.
* @protected
*/
_enableScrollFooter: function () {

    if (this._bFooterDisabled) {

        Dom.removeClass(this.footer, _BOTTOM_SCROLLBAR_DISABLED);
        this._bFooterDisabled = false;

    }

},


/**
* @method _onMouseOver
* @description "mouseover" event handler for the menu.
* @protected
* @param {String} p_sType String representing the name of the event that 
* was fired.
* @param {Array} p_aArgs Array of arguments sent when the event was fired.
*/
_onMouseOver: function (p_sType, p_aArgs) {

    var oEvent = p_aArgs[0],
        oItem = p_aArgs[1],
        oTarget = Event.getTarget(oEvent),
        oRoot = this.getRoot(),
        oSubmenuHideDelayTimer = this._submenuHideDelayTimer,
        oParentMenu,
        nShowDelay,
        bShowDelay,
        oActiveItem,
        oItemCfg,
        oSubmenu;


    var showSubmenu = function () {

        if (this.parent.cfg.getProperty(_SELECTED)) {

            this.show();

        }

    };


    if (!this._bStopMouseEventHandlers) {
    
		if (!this._bHandledMouseOverEvent && (oTarget == this.element || 
				Dom.isAncestor(this.element, oTarget))) {
	
			// Menu mouseover logic

	        if (this._useHideDelay) {
	        	this._cancelHideDelay();
	        }
	
			this._nCurrentMouseX = 0;
	
			Event.on(this.element, _MOUSEMOVE, this._onMouseMove, this, true);


			/*
				If the mouse is moving from the submenu back to its corresponding menu item, 
				don't hide the submenu or clear the active MenuItem.
			*/

			if (!(oItem && Dom.isAncestor(oItem.element, Event.getRelatedTarget(oEvent)))) {

				this.clearActiveItem();

			}
	

			if (this.parent && oSubmenuHideDelayTimer) {
	
				oSubmenuHideDelayTimer.cancel();
	
				this.parent.cfg.setProperty(_SELECTED, true);
	
				oParentMenu = this.parent.parent;
	
				oParentMenu._bHandledMouseOutEvent = true;
				oParentMenu._bHandledMouseOverEvent = false;
	
			}
	
	
			this._bHandledMouseOverEvent = true;
			this._bHandledMouseOutEvent = false;
		
		}
	
	
		if (oItem && !oItem.handledMouseOverEvent && !oItem.cfg.getProperty(_DISABLED) && 
			(oTarget == oItem.element || Dom.isAncestor(oItem.element, oTarget))) {
	
			// Menu Item mouseover logic
	
			nShowDelay = this.cfg.getProperty(_SHOW_DELAY);
			bShowDelay = (nShowDelay > 0);
	
	
			if (bShowDelay) {
			
				this._cancelShowDelay();
			
			}
	
	
			oActiveItem = this.activeItem;
		
			if (oActiveItem) {
		
				oActiveItem.cfg.setProperty(_SELECTED, false);
		
			}
	
	
			oItemCfg = oItem.cfg;
		
			// Select and focus the current menu item
		
			oItemCfg.setProperty(_SELECTED, true);
	
	
			if (this.hasFocus() || oRoot._hasFocus) {
			
				oItem.focus();
				
				oRoot._hasFocus = false;
			
			}
	
	
			if (this.cfg.getProperty(_AUTO_SUBMENU_DISPLAY)) {
	
				// Show the submenu this menu item
	
				oSubmenu = oItemCfg.getProperty(_SUBMENU);
			
				if (oSubmenu) {
			
					if (bShowDelay) {
	
						oRoot._showDelayTimer = 
							Lang.later(oRoot.cfg.getProperty(_SHOW_DELAY), oSubmenu, showSubmenu);
			
					}
					else {
	
						oSubmenu.show();
	
					}
	
				}
	
			}                        
	
			oItem.handledMouseOverEvent = true;
			oItem.handledMouseOutEvent = false;
	
		}
    
    }

},


/**
* @method _onMouseOut
* @description "mouseout" event handler for the menu.
* @protected
* @param {String} p_sType String representing the name of the event that 
* was fired.
* @param {Array} p_aArgs Array of arguments sent when the event was fired.
*/
_onMouseOut: function (p_sType, p_aArgs) {

    var oEvent = p_aArgs[0],
        oItem = p_aArgs[1],
        oRelatedTarget = Event.getRelatedTarget(oEvent),
        bMovingToSubmenu = false,
        oItemCfg,
        oSubmenu,
        nSubmenuHideDelay,
        nShowDelay;


    if (!this._bStopMouseEventHandlers) {
    
		if (oItem && !oItem.cfg.getProperty(_DISABLED)) {
	
			oItemCfg = oItem.cfg;
			oSubmenu = oItemCfg.getProperty(_SUBMENU);
	
	
			if (oSubmenu && (oRelatedTarget == oSubmenu.element ||
					Dom.isAncestor(oSubmenu.element, oRelatedTarget))) {
	
				bMovingToSubmenu = true;
	
			}
	
	
			if (!oItem.handledMouseOutEvent && ((oRelatedTarget != oItem.element &&  
				!Dom.isAncestor(oItem.element, oRelatedTarget)) || bMovingToSubmenu)) {
	
				// Menu Item mouseout logic
	
				if (!bMovingToSubmenu) {
	
					oItem.cfg.setProperty(_SELECTED, false);
	
	
					if (oSubmenu) {
	
						nSubmenuHideDelay = this.cfg.getProperty(_SUBMENU_HIDE_DELAY);
	
						nShowDelay = this.cfg.getProperty(_SHOW_DELAY);
	
						if (!(this instanceof YAHOO.widget.MenuBar) && nSubmenuHideDelay > 0 && 
							nShowDelay >= nSubmenuHideDelay) {
	
							this._execSubmenuHideDelay(oSubmenu, Event.getPageX(oEvent),
									nSubmenuHideDelay);
	
						}
						else {
	
							oSubmenu.hide();
	
						}
	
					}
	
				}
	
	
				oItem.handledMouseOutEvent = true;
				oItem.handledMouseOverEvent = false;
		
			}
	
		}


		if (!this._bHandledMouseOutEvent && ((oRelatedTarget != this.element &&  
			!Dom.isAncestor(this.element, oRelatedTarget)) || bMovingToSubmenu)) {
	
			// Menu mouseout logic

	        if (this._useHideDelay) {
	        	this._execHideDelay();
	        }

			Event.removeListener(this.element, _MOUSEMOVE, this._onMouseMove);
	
			this._nCurrentMouseX = Event.getPageX(oEvent);
	
			this._bHandledMouseOutEvent = true;
			this._bHandledMouseOverEvent = false;
	
		}
    
    }

},


/**
* @method _onMouseMove
* @description "click" event handler for the menu.
* @protected
* @param {Event} p_oEvent Object representing the DOM event object passed 
* back by the event utility (YAHOO.util.Event).
* @param {YAHOO.widget.Menu} p_oMenu Object representing the menu that 
* fired the event.
*/
_onMouseMove: function (p_oEvent, p_oMenu) {

    if (!this._bStopMouseEventHandlers) {
    
	    this._nCurrentMouseX = Event.getPageX(p_oEvent);
    
    }

},


/**
* @method _onClick
* @description "click" event handler for the menu.
* @protected
* @param {String} p_sType String representing the name of the event that 
* was fired.
* @param {Array} p_aArgs Array of arguments sent when the event was fired.
*/
_onClick: function (p_sType, p_aArgs) {

	var oEvent = p_aArgs[0],
		oItem = p_aArgs[1],
		bInMenuAnchor = false,
		oSubmenu,
		oMenu,
		oRoot,
		sId,
		sURL,
		nHashPos,
		nLen;


	var hide = function () {
		
		oRoot = this.getRoot();

		if (oRoot instanceof YAHOO.widget.MenuBar || 
			oRoot.cfg.getProperty(_POSITION) == _STATIC) {

			oRoot.clearActiveItem();

		}
		else {

			oRoot.hide();
		
		}
	
	};


	if (oItem) {
	
		if (oItem.cfg.getProperty(_DISABLED)) {
		
			Event.preventDefault(oEvent);

			hide.call(this);

		}
		else {

			oSubmenu = oItem.cfg.getProperty(_SUBMENU);
	
			
			/*
				 Check if the URL of the anchor is pointing to an element that is 
				 a child of the menu.
			*/
			
			sURL = oItem.cfg.getProperty(_URL);

		
			if (sURL) {
	
				nHashPos = sURL.indexOf(_HASH);
	
				nLen = sURL.length;
	
	
				if (nHashPos != -1) {
	
					sURL = sURL.substr(nHashPos, nLen);
		
					nLen = sURL.length;
	
	
					if (nLen > 1) {
	
						sId = sURL.substr(1, nLen);
	
						oMenu = YAHOO.widget.MenuManager.getMenu(sId);
						
						if (oMenu) {

							bInMenuAnchor = 
								(this.getRoot() === oMenu.getRoot());

						}
						
					}
					else if (nLen === 1) {
	
						bInMenuAnchor = true;
					
					}
	
				}
			
			}

	
			if (bInMenuAnchor && !oItem.cfg.getProperty(_TARGET)) {
	
				Event.preventDefault(oEvent);
				

				if (UA.webkit) {
				
					oItem.focus();
				
				}
				else {

					oItem.focusEvent.fire();
				
				}
			
			}
	
	
			if (!oSubmenu && !this.cfg.getProperty(_KEEP_OPEN)) {
	
				hide.call(this);
	
			}
			
		}
	
	}

},


/**
* @method _onKeyDown
* @description "keydown" event handler for the menu.
* @protected
* @param {String} p_sType String representing the name of the event that 
* was fired.
* @param {Array} p_aArgs Array of arguments sent when the event was fired.
*/
_onKeyDown: function (p_sType, p_aArgs) {

    var oEvent = p_aArgs[0],
        oItem = p_aArgs[1],
        oSubmenu,
        oItemCfg,
        oParentItem,
        oRoot,
        oNextItem,
        oBody,
        nBodyScrollTop,
        nBodyOffsetHeight,
        aItems,
        nItems,
        nNextItemOffsetTop,
        nScrollTarget,
        oParentMenu,
		oFocusedEl;


	if (this._useHideDelay) {
		this._cancelHideDelay();
	}


    /*
        This function is called to prevent a bug in Firefox.  In Firefox,
        moving a DOM element into a stationary mouse pointer will cause the 
        browser to fire mouse events.  This can result in the menu mouse
        event handlers being called uncessarily, especially when menus are 
        moved into a stationary mouse pointer as a result of a 
        key event handler.
    */
    function stopMouseEventHandlers() {

        this._bStopMouseEventHandlers = true;
        
        Lang.later(10, this, function () {

            this._bStopMouseEventHandlers = false;
        
        });

    }


    if (oItem && !oItem.cfg.getProperty(_DISABLED)) {

        oItemCfg = oItem.cfg;
        oParentItem = this.parent;

        switch(oEvent.keyCode) {
    
            case 38:    // Up arrow
            case 40:    // Down arrow
    
                oNextItem = (oEvent.keyCode == 38) ? 
                    oItem.getPreviousEnabledSibling() : 
                    oItem.getNextEnabledSibling();
        
                if (oNextItem) {

                    this.clearActiveItem();

                    oNextItem.cfg.setProperty(_SELECTED, true);
                    oNextItem.focus();


                    if (this.cfg.getProperty(_MAX_HEIGHT) > 0) {

                        oBody = this.body;
                        nBodyScrollTop = oBody.scrollTop;
                        nBodyOffsetHeight = oBody.offsetHeight;
                        aItems = this.getItems();
                        nItems = aItems.length - 1;
                        nNextItemOffsetTop = oNextItem.element.offsetTop;


                        if (oEvent.keyCode == 40 ) {    // Down
                       
                            if (nNextItemOffsetTop >= (nBodyOffsetHeight + nBodyScrollTop)) {

                                oBody.scrollTop = nNextItemOffsetTop - nBodyOffsetHeight;

                            }
                            else if (nNextItemOffsetTop <= nBodyScrollTop) {
                            
                                oBody.scrollTop = 0;
                            
                            }


                            if (oNextItem == aItems[nItems]) {

                                oBody.scrollTop = oNextItem.element.offsetTop;

                            }

                        }
                        else {  // Up

                            if (nNextItemOffsetTop <= nBodyScrollTop) {

                                oBody.scrollTop = nNextItemOffsetTop - oNextItem.element.offsetHeight;
                            
                            }
                            else if (nNextItemOffsetTop >= (nBodyScrollTop + nBodyOffsetHeight)) {
                            
                                oBody.scrollTop = nNextItemOffsetTop;
                            
                            }


                            if (oNextItem == aItems[0]) {
                            
                                oBody.scrollTop = 0;
                            
                            }

                        }


                        nBodyScrollTop = oBody.scrollTop;
                        nScrollTarget = oBody.scrollHeight - oBody.offsetHeight;

                        if (nBodyScrollTop === 0) {

                            this._disableScrollHeader();
                            this._enableScrollFooter();

                        }
                        else if (nBodyScrollTop == nScrollTarget) {

                             this._enableScrollHeader();
                             this._disableScrollFooter();

                        }
                        else {

                            this._enableScrollHeader();
                            this._enableScrollFooter();

                        }

                    }

                }

    
                Event.preventDefault(oEvent);

                stopMouseEventHandlers();
    
            break;
            
    
            case 39:    // Right arrow
    
                oSubmenu = oItemCfg.getProperty(_SUBMENU);
    
                if (oSubmenu) {
    
                    if (!oItemCfg.getProperty(_SELECTED)) {
        
                        oItemCfg.setProperty(_SELECTED, true);
        
                    }
    
                    oSubmenu.show();
                    oSubmenu.setInitialFocus();
                    oSubmenu.setInitialSelection();
    
                }
                else {
    
                    oRoot = this.getRoot();
                    
                    if (oRoot instanceof YAHOO.widget.MenuBar) {
    
                        oNextItem = oRoot.activeItem.getNextEnabledSibling();
    
                        if (oNextItem) {
                        
                            oRoot.clearActiveItem();
    
                            oNextItem.cfg.setProperty(_SELECTED, true);
    
                            oSubmenu = oNextItem.cfg.getProperty(_SUBMENU);
    
                            if (oSubmenu) {
    
                                oSubmenu.show();
                                oSubmenu.setInitialFocus();
                            
                            }
                            else {
    
                            	oNextItem.focus();
                            
                            }
                        
                        }
                    
                    }
                
                }
    
    
                Event.preventDefault(oEvent);

                stopMouseEventHandlers();

            break;
    
    
            case 37:    // Left arrow
    
                if (oParentItem) {
    
                    oParentMenu = oParentItem.parent;
    
                    if (oParentMenu instanceof YAHOO.widget.MenuBar) {
    
                        oNextItem = 
                            oParentMenu.activeItem.getPreviousEnabledSibling();
    
                        if (oNextItem) {
                        
                            oParentMenu.clearActiveItem();
    
                            oNextItem.cfg.setProperty(_SELECTED, true);
    
                            oSubmenu = oNextItem.cfg.getProperty(_SUBMENU);
    
                            if (oSubmenu) {
                            
                                oSubmenu.show();
								oSubmenu.setInitialFocus();                                
                            
                            }
                            else {
    
                            	oNextItem.focus();
                            
                            }
                        
                        } 
                    
                    }
                    else {
    
                        this.hide();
    
                        oParentItem.focus();
                    
                    }
    
                }
    
                Event.preventDefault(oEvent);

                stopMouseEventHandlers();

            break;        
    
        }


    }


    if (oEvent.keyCode == 27) { // Esc key

        if (this.cfg.getProperty(_POSITION) == _DYNAMIC) {
        
            this.hide();

            if (this.parent) {

                this.parent.focus();
            
            }
			else {
				// Focus the element that previously had focus

				oFocusedEl = this._focusedElement;

				if (oFocusedEl && oFocusedEl.focus) {

					try {
						oFocusedEl.focus();
					}
					catch(ex) {
					}

				}
				
			}

        }
        else if (this.activeItem) {

            oSubmenu = this.activeItem.cfg.getProperty(_SUBMENU);

            if (oSubmenu && oSubmenu.cfg.getProperty(_VISIBLE)) {
            
                oSubmenu.hide();
                this.activeItem.focus();
            
            }
            else {

                this.activeItem.blur();
                this.activeItem.cfg.setProperty(_SELECTED, false);
        
            }
        
        }


        Event.preventDefault(oEvent);
    
    }
    
},


/**
* @method _onKeyPress
* @description "keypress" event handler for a Menu instance.
* @protected
* @param {String} p_sType The name of the event that was fired.
* @param {Array} p_aArgs Collection of arguments sent when the event 
* was fired.
*/
_onKeyPress: function (p_sType, p_aArgs) {
    
    var oEvent = p_aArgs[0];


    if (oEvent.keyCode == 40 || oEvent.keyCode == 38) {

        Event.preventDefault(oEvent);

    }

},


/**
* @method _onBlur
* @description "blur" event handler for a Menu instance.
* @protected
* @param {String} p_sType The name of the event that was fired.
* @param {Array} p_aArgs Collection of arguments sent when the event 
* was fired.
*/
_onBlur: function (p_sType, p_aArgs) {
        
	if (this._hasFocus) {
		this._hasFocus = false;
	}

},

/**
* @method _onYChange
* @description "y" event handler for a Menu instance.
* @protected
* @param {String} p_sType The name of the event that was fired.
* @param {Array} p_aArgs Collection of arguments sent when the event 
* was fired.
*/
_onYChange: function (p_sType, p_aArgs) {

    var oParent = this.parent,
        nScrollTop,
        oIFrame,
        nY;


    if (oParent) {

        nScrollTop = oParent.parent.body.scrollTop;


        if (nScrollTop > 0) {
    
            nY = (this.cfg.getProperty(_Y) - nScrollTop);
            
            Dom.setY(this.element, nY);

            oIFrame = this.iframe;            
    

            if (oIFrame) {
    
                Dom.setY(oIFrame, nY);
    
            }
            
            this.cfg.setProperty(_Y, nY, true);
        
        }
    
    }

},


/**
* @method _onScrollTargetMouseOver
* @description "mouseover" event handler for the menu's "header" and "footer" 
* elements.  Used to scroll the body of the menu up and down when the 
* menu's "maxheight" configuration property is set to a value greater than 0.
* @protected
* @param {Event} p_oEvent Object representing the DOM event object passed 
* back by the event utility (YAHOO.util.Event).
* @param {YAHOO.widget.Menu} p_oMenu Object representing the menu that 
* fired the event.
*/
_onScrollTargetMouseOver: function (p_oEvent, p_oMenu) {

	var oBodyScrollTimer = this._bodyScrollTimer;


	if (oBodyScrollTimer) {

		oBodyScrollTimer.cancel();

	}


	this._cancelHideDelay();


    var oTarget = Event.getTarget(p_oEvent),
        oBody = this.body,
        nScrollIncrement = this.cfg.getProperty(_SCROLL_INCREMENT),
        nScrollTarget,
        fnScrollFunction;


    function scrollBodyDown() {

        var nScrollTop = oBody.scrollTop;


        if (nScrollTop < nScrollTarget) {

            oBody.scrollTop = (nScrollTop + nScrollIncrement);

            this._enableScrollHeader();

        }
        else {

            oBody.scrollTop = nScrollTarget;

            this._bodyScrollTimer.cancel();

            this._disableScrollFooter();

        }

    }


    function scrollBodyUp() {

        var nScrollTop = oBody.scrollTop;


        if (nScrollTop > 0) {

            oBody.scrollTop = (nScrollTop - nScrollIncrement);

            this._enableScrollFooter();

        }
        else {

            oBody.scrollTop = 0;

			this._bodyScrollTimer.cancel();

            this._disableScrollHeader();

        }

    }

    
    if (Dom.hasClass(oTarget, _HD)) {

        fnScrollFunction = scrollBodyUp;
    
    }
    else {

        nScrollTarget = oBody.scrollHeight - oBody.offsetHeight;

        fnScrollFunction = scrollBodyDown;
    
    }
    

    this._bodyScrollTimer = Lang.later(10, this, fnScrollFunction, null, true);

},


/**
* @method _onScrollTargetMouseOut
* @description "mouseout" event handler for the menu's "header" and "footer" 
* elements.  Used to stop scrolling the body of the menu up and down when the 
* menu's "maxheight" configuration property is set to a value greater than 0.
* @protected
* @param {Event} p_oEvent Object representing the DOM event object passed 
* back by the event utility (YAHOO.util.Event).
* @param {YAHOO.widget.Menu} p_oMenu Object representing the menu that 
* fired the event.
*/
_onScrollTargetMouseOut: function (p_oEvent, p_oMenu) {

	var oBodyScrollTimer = this._bodyScrollTimer;

	if (oBodyScrollTimer) {

		oBodyScrollTimer.cancel();

	}
	
    this._cancelHideDelay();

},



// Private methods


/**
* @method _onInit
* @description "init" event handler for the menu.
* @private
* @param {String} p_sType String representing the name of the event that 
* was fired.
* @param {Array} p_aArgs Array of arguments sent when the event was fired.
*/
_onInit: function (p_sType, p_aArgs) {

    this.cfg.subscribeToConfigEvent(_VISIBLE, this._onVisibleChange);

    var bRootMenu = !this.parent,
        bLazyLoad = this.lazyLoad;


    /*
        Automatically initialize a menu's subtree if:

        1) This is the root menu and lazyload is off
        
        2) This is the root menu, lazyload is on, but the menu is 
           already visible

        3) This menu is a submenu and lazyload is off
    */



    if (((bRootMenu && !bLazyLoad) || 
        (bRootMenu && (this.cfg.getProperty(_VISIBLE) || 
        this.cfg.getProperty(_POSITION) == _STATIC)) || 
        (!bRootMenu && !bLazyLoad)) && this.getItemGroups().length === 0) {

        if (this.srcElement) {

            this._initSubTree();
        
        }


        if (this.itemData) {

            this.addItems(this.itemData);

        }
    
    }
    else if (bLazyLoad) {

        this.cfg.fireQueue();
    
    }

},


/**
* @method _onBeforeRender
* @description "beforerender" event handler for the menu.  Appends all of the 
* <code>&#60;ul&#62;</code>, <code>&#60;li&#62;</code> and their accompanying 
* title elements to the body element of the menu.
* @private
* @param {String} p_sType String representing the name of the event that 
* was fired.
* @param {Array} p_aArgs Array of arguments sent when the event was fired.
*/
_onBeforeRender: function (p_sType, p_aArgs) {

    var oEl = this.element,
        nListElements = this._aListElements.length,
        bFirstList = true,
        i = 0,
        oUL,
        oGroupTitle;

    if (nListElements > 0) {

        do {

            oUL = this._aListElements[i];

            if (oUL) {

                if (bFirstList) {
        
                    Dom.addClass(oUL, _FIRST_OF_TYPE);
                    bFirstList = false;
        
                }


                if (!Dom.isAncestor(oEl, oUL)) {

                    this.appendToBody(oUL);

                }


                oGroupTitle = this._aGroupTitleElements[i];

                if (oGroupTitle) {

                    if (!Dom.isAncestor(oEl, oGroupTitle)) {

                        oUL.parentNode.insertBefore(oGroupTitle, oUL);

                    }


                    Dom.addClass(oUL, _HAS_TITLE);

                }

            }

            i++;

        }
        while (i < nListElements);

    }

},


/**
* @method _onRender
* @description "render" event handler for the menu.
* @private
* @param {String} p_sType String representing the name of the event that 
* was fired.
* @param {Array} p_aArgs Array of arguments sent when the event was fired.
*/
_onRender: function (p_sType, p_aArgs) {

    if (this.cfg.getProperty(_POSITION) == _DYNAMIC) { 

        if (!this.cfg.getProperty(_VISIBLE)) {

            this.positionOffScreen();

        }
    
    }

},





/**
* @method _onBeforeShow
* @description "beforeshow" event handler for the menu.
* @private
* @param {String} p_sType String representing the name of the event that 
* was fired.
* @param {Array} p_aArgs Array of arguments sent when the event was fired.
*/
_onBeforeShow: function (p_sType, p_aArgs) {

    var nOptions,
        n,
        oSrcElement,
        oContainer = this.cfg.getProperty(_CONTAINER);


    if (this.lazyLoad && this.getItemGroups().length === 0) {

        if (this.srcElement) {
        
            this._initSubTree();

        }


        if (this.itemData) {

            if (this.parent && this.parent.parent && 
                this.parent.parent.srcElement && 
                this.parent.parent.srcElement.tagName.toUpperCase() == 
                _SELECT) {

                nOptions = this.itemData.length;
    
                for(n=0; n<nOptions; n++) {

                    if (this.itemData[n].tagName) {

                        this.addItem((new this.ITEM_TYPE(this.itemData[n])));
    
                    }
    
                }
            
            }
            else {

                this.addItems(this.itemData);
            
            }
        
        }


        oSrcElement = this.srcElement;

        if (oSrcElement) {

            if (oSrcElement.tagName.toUpperCase() == _SELECT) {

                if (Dom.inDocument(oSrcElement)) {

                    this.render(oSrcElement.parentNode);
                
                }
                else {
                
                    this.render(oContainer);
                
                }

            }
            else {

                this.render();

            }

        }
        else {

            if (this.parent) {

                this.render(this.parent.element);     

            }
            else {

                this.render(oContainer);

            }                

        }

    }



    var oParent = this.parent,
		aAlignment;


    if (!oParent && this.cfg.getProperty(_POSITION) == _DYNAMIC) {

        this.cfg.refireEvent(_XY);
   
    }


	if (oParent) {

		aAlignment = oParent.parent.cfg.getProperty(_SUBMENU_ALIGNMENT);
		
		this.cfg.setProperty(_CONTEXT, [oParent.element, aAlignment[0], aAlignment[1]]);
		this.align();
	
	}

},


getConstrainedY: function (y) {

	var oMenu = this,
	
		aContext = oMenu.cfg.getProperty(_CONTEXT),
		nInitialMaxHeight = oMenu.cfg.getProperty(_MAX_HEIGHT),

		nMaxHeight,

		oOverlapPositions = {

			"trbr": true,
			"tlbl": true,
			"bltl": true,
			"brtr": true

		},

		bPotentialContextOverlap = (aContext && oOverlapPositions[aContext[1] + aContext[2]]),
	
		oMenuEl = oMenu.element,
		nMenuOffsetHeight = oMenuEl.offsetHeight,
	
		nViewportOffset = Overlay.VIEWPORT_OFFSET,
		viewPortHeight = Dom.getViewportHeight(),
		scrollY = Dom.getDocumentScrollTop(),

		bCanConstrain = 
			(oMenu.cfg.getProperty(_MIN_SCROLL_HEIGHT) + nViewportOffset < viewPortHeight),

		nAvailableHeight,

		oContextEl,
		nContextElY,
		nContextElHeight,

		bFlipped = false,

		nTopRegionHeight,
		nBottomRegionHeight,

		topConstraint = scrollY + nViewportOffset,
		bottomConstraint = scrollY + viewPortHeight - nMenuOffsetHeight - nViewportOffset,

		yNew = y;
		

	var flipVertical = function () {

		var nNewY;
	
		// The Menu is below the context element, flip it above
		if ((oMenu.cfg.getProperty(_Y) - scrollY) > nContextElY) { 
			nNewY = (nContextElY - nMenuOffsetHeight);
		}
		else {	// The Menu is above the context element, flip it below
			nNewY = (nContextElY + nContextElHeight);
		}

		oMenu.cfg.setProperty(_Y, (nNewY + scrollY), true);
		
		return nNewY;
	
	};


	/*
		 Uses the context element's position to calculate the availble height 
		 above and below it to display its corresponding Menu.
	*/

	var getDisplayRegionHeight = function () {

		// The Menu is below the context element
		if ((oMenu.cfg.getProperty(_Y) - scrollY) > nContextElY) {
			return (nBottomRegionHeight - nViewportOffset);				
		}
		else {	// The Menu is above the context element
			return (nTopRegionHeight - nViewportOffset);				
		}

	};


	/*
		Sets the Menu's "y" configuration property to the correct value based on its
		current orientation.
	*/ 

	var alignY = function () {

		var nNewY;

		if ((oMenu.cfg.getProperty(_Y) - scrollY) > nContextElY) { 
			nNewY = (nContextElY + nContextElHeight);
		}
		else {	
			nNewY = (nContextElY - oMenuEl.offsetHeight);
		}

		oMenu.cfg.setProperty(_Y, (nNewY + scrollY), true);
	
	};


	//	Resets the maxheight of the Menu to the value set by the user

	var resetMaxHeight = function () {

		oMenu._setScrollHeight(this.cfg.getProperty(_MAX_HEIGHT));

		oMenu.hideEvent.unsubscribe(resetMaxHeight);
	
	};


	/*
		Trys to place the Menu in the best possible position (either above or 
		below its corresponding context element).
	*/

	var setVerticalPosition = function () {

		var nDisplayRegionHeight = getDisplayRegionHeight(),
			bMenuHasItems = (oMenu.getItems().length > 0),
			nMenuMinScrollHeight,
			fnReturnVal;


		if (nMenuOffsetHeight > nDisplayRegionHeight) {

			nMenuMinScrollHeight = 
				bMenuHasItems ? oMenu.cfg.getProperty(_MIN_SCROLL_HEIGHT) : nMenuOffsetHeight;


			if ((nDisplayRegionHeight > nMenuMinScrollHeight) && bMenuHasItems) {
				nMaxHeight = nDisplayRegionHeight;
			}
			else {
				nMaxHeight = nInitialMaxHeight;
			}


			oMenu._setScrollHeight(nMaxHeight);
			oMenu.hideEvent.subscribe(resetMaxHeight);
			

			// Re-align the Menu since its height has just changed
			// as a result of the setting of the maxheight property.

			alignY();
			

			if (nDisplayRegionHeight < nMenuMinScrollHeight) {

				if (bFlipped) {
	
					/*
						 All possible positions and values for the "maxheight" 
						 configuration property have been tried, but none were 
						 successful, so fall back to the original size and position.
					*/

					flipVertical();
					
				}
				else {
	
					flipVertical();

					bFlipped = true;
	
					fnReturnVal = setVerticalPosition();
	
				}
				
			}
		
		}
		else if (nMaxHeight && (nMaxHeight !== nInitialMaxHeight)) {
		
			oMenu._setScrollHeight(nInitialMaxHeight);
			oMenu.hideEvent.subscribe(resetMaxHeight);

			// Re-align the Menu since its height has just changed
			// as a result of the setting of the maxheight property.

			alignY();
		
		}

		return fnReturnVal;

	};


	// Determine if the current value for the Menu's "y" configuration property will
	// result in the Menu being positioned outside the boundaries of the viewport

	if (y < topConstraint || y  > bottomConstraint) {

		// The current value for the Menu's "y" configuration property WILL
		// result in the Menu being positioned outside the boundaries of the viewport

		if (bCanConstrain) {

			if (oMenu.cfg.getProperty(_PREVENT_CONTEXT_OVERLAP) && bPotentialContextOverlap) {
		
				//	SOLUTION #1:
				//	If the "preventcontextoverlap" configuration property is set to "true", 
				//	try to flip and/or scroll the Menu to both keep it inside the boundaries of the 
				//	viewport AND from overlaping its context element (MenuItem or MenuBarItem).

				oContextEl = aContext[0];
				nContextElHeight = oContextEl.offsetHeight;
				nContextElY = (Dom.getY(oContextEl) - scrollY);
	
				nTopRegionHeight = nContextElY;
				nBottomRegionHeight = (viewPortHeight - (nContextElY + nContextElHeight));
	
				setVerticalPosition();
				
				yNew = oMenu.cfg.getProperty(_Y);
		
			}
			else if (!(oMenu instanceof YAHOO.widget.MenuBar) && 
				nMenuOffsetHeight >= viewPortHeight) {

				//	SOLUTION #2:
				//	If the Menu exceeds the height of the viewport, introduce scroll bars
				//	to keep the Menu inside the boundaries of the viewport

				nAvailableHeight = (viewPortHeight - (nViewportOffset * 2));
		
				if (nAvailableHeight > oMenu.cfg.getProperty(_MIN_SCROLL_HEIGHT)) {
		
					oMenu._setScrollHeight(nAvailableHeight);
					oMenu.hideEvent.subscribe(resetMaxHeight);
		
					alignY();
					
					yNew = oMenu.cfg.getProperty(_Y);
				
				}
		
			}	
			else {

				//	SOLUTION #3:
			
				if (y < topConstraint) {
					yNew  = topConstraint;
				} else if (y  > bottomConstraint) {
					yNew  = bottomConstraint;
				}				
			
			}

		}
		else {
			//	The "y" configuration property cannot be set to a value that will keep
			//	entire Menu inside the boundary of the viewport.  Therefore, set  
			//	the "y" configuration property to scrollY to keep as much of the 
			//	Menu inside the viewport as possible.
			yNew = nViewportOffset + scrollY;
		}	

	}

	return yNew;

},


/**
* @method _onHide
* @description "hide" event handler for the menu.
* @private
* @param {String} p_sType String representing the name of the event that 
* was fired.
* @param {Array} p_aArgs Array of arguments sent when the event was fired.
*/
_onHide: function (p_sType, p_aArgs) {

	if (this.cfg.getProperty(_POSITION) === _DYNAMIC) {
	
		this.positionOffScreen();
	
	}

},


/**
* @method _onShow
* @description "show" event handler for the menu.
* @private
* @param {String} p_sType String representing the name of the event that 
* was fired.
* @param {Array} p_aArgs Array of arguments sent when the event was fired.
*/
_onShow: function (p_sType, p_aArgs) {

    var oParent = this.parent,
        oParentMenu,
		oElement,
		nOffsetWidth,
		sWidth;        


    function disableAutoSubmenuDisplay(p_oEvent) {

        var oTarget;

        if (p_oEvent.type == _MOUSEDOWN || (p_oEvent.type == _KEYDOWN && p_oEvent.keyCode == 27)) {

            /*  
                Set the "autosubmenudisplay" to "false" if the user
                clicks outside the menu bar.
            */

            oTarget = Event.getTarget(p_oEvent);

            if (oTarget != oParentMenu.element || !Dom.isAncestor(oParentMenu.element, oTarget)) {

                oParentMenu.cfg.setProperty(_AUTO_SUBMENU_DISPLAY, false);

                Event.removeListener(document, _MOUSEDOWN, disableAutoSubmenuDisplay);
                Event.removeListener(document, _KEYDOWN, disableAutoSubmenuDisplay);

            }
        
        }

    }


	function onSubmenuHide(p_sType, p_aArgs, p_sWidth) {
	
		this.cfg.setProperty(_WIDTH, _EMPTY_STRING);
		this.hideEvent.unsubscribe(onSubmenuHide, p_sWidth);
	
	}


    if (oParent) {

        oParentMenu = oParent.parent;


        if (!oParentMenu.cfg.getProperty(_AUTO_SUBMENU_DISPLAY) && 
            (oParentMenu instanceof YAHOO.widget.MenuBar || 
            oParentMenu.cfg.getProperty(_POSITION) == _STATIC)) {

            oParentMenu.cfg.setProperty(_AUTO_SUBMENU_DISPLAY, true);

            Event.on(document, _MOUSEDOWN, disableAutoSubmenuDisplay);                             
            Event.on(document, _KEYDOWN, disableAutoSubmenuDisplay);

        }


		//	The following fixes an issue with the selected state of a MenuItem 
		//	not rendering correctly when a submenu is aligned to the left of
		//	its parent Menu instance.

		if ((this.cfg.getProperty("x") < oParentMenu.cfg.getProperty("x")) && 
			(UA.gecko && UA.gecko < 1.9) && !this.cfg.getProperty(_WIDTH)) {

			oElement = this.element;
			nOffsetWidth = oElement.offsetWidth;
			
			/*
				Measuring the difference of the offsetWidth before and after
				setting the "width" style attribute allows us to compute the 
				about of padding and borders applied to the element, which in 
				turn allows us to set the "width" property correctly.
			*/
			
			oElement.style.width = nOffsetWidth + _PX;
			
			sWidth = (nOffsetWidth - (oElement.offsetWidth - nOffsetWidth)) + _PX;
			
			this.cfg.setProperty(_WIDTH, sWidth);
		
			this.hideEvent.subscribe(onSubmenuHide, sWidth);
		
		}

    }


	/*
		Dynamically positioned, root Menus focus themselves when visible, and 
		will then, when hidden, restore focus to the UI control that had focus 
		before the Menu was made visible.
	*/ 

	if (this === this.getRoot() && this.cfg.getProperty(_POSITION) === _DYNAMIC) {

		this._focusedElement = oFocusedElement;
		
		this.focus();
	
	}


},


/**
* @method _onBeforeHide
* @description "beforehide" event handler for the menu.
* @private
* @param {String} p_sType String representing the name of the event that 
* was fired.
* @param {Array} p_aArgs Array of arguments sent when the event was fired.
*/
_onBeforeHide: function (p_sType, p_aArgs) {

    var oActiveItem = this.activeItem,
        oRoot = this.getRoot(),
        oConfig,
        oSubmenu;


    if (oActiveItem) {

        oConfig = oActiveItem.cfg;

        oConfig.setProperty(_SELECTED, false);

        oSubmenu = oConfig.getProperty(_SUBMENU);

        if (oSubmenu) {

            oSubmenu.hide();

        }

    }


	/*
		Focus can get lost in IE when the mouse is moving from a submenu back to its parent Menu.  
		For this reason, it is necessary to maintain the focused state in a private property 
		so that the _onMouseOver event handler is able to determined whether or not to set focus
		to MenuItems as the user is moving the mouse.
	*/ 

	if (UA.ie && this.cfg.getProperty(_POSITION) === _DYNAMIC && this.parent) {

		oRoot._hasFocus = this.hasFocus();
	
	}


    if (oRoot == this) {

        oRoot.blur();
    
    }

},


/**
* @method _onParentMenuConfigChange
* @description "configchange" event handler for a submenu.
* @private
* @param {String} p_sType String representing the name of the event that 
* was fired.
* @param {Array} p_aArgs Array of arguments sent when the event was fired.
* @param {YAHOO.widget.Menu} p_oSubmenu Object representing the submenu that 
* subscribed to the event.
*/
_onParentMenuConfigChange: function (p_sType, p_aArgs, p_oSubmenu) {
    
    var sPropertyName = p_aArgs[0][0],
        oPropertyValue = p_aArgs[0][1];

    switch(sPropertyName) {

        case _IFRAME:
        case _CONSTRAIN_TO_VIEWPORT:
        case _HIDE_DELAY:
        case _SHOW_DELAY:
        case _SUBMENU_HIDE_DELAY:
        case _CLICK_TO_HIDE:
        case _EFFECT:
        case _CLASSNAME:
        case _SCROLL_INCREMENT:
        case _MAX_HEIGHT:
        case _MIN_SCROLL_HEIGHT:
        case _MONITOR_RESIZE:
        case _SHADOW:
        case _PREVENT_CONTEXT_OVERLAP:
		case _KEEP_OPEN:

            p_oSubmenu.cfg.setProperty(sPropertyName, oPropertyValue);
                
        break;
        
        case _SUBMENU_ALIGNMENT:

			if (!(this.parent.parent instanceof YAHOO.widget.MenuBar)) {
		
				p_oSubmenu.cfg.setProperty(sPropertyName, oPropertyValue);
		
			}
        
        break;
        
    }
    
},


/**
* @method _onParentMenuRender
* @description "render" event handler for a submenu.  Renders a  
* submenu in response to the firing of its parent's "render" event.
* @private
* @param {String} p_sType String representing the name of the event that 
* was fired.
* @param {Array} p_aArgs Array of arguments sent when the event was fired.
* @param {YAHOO.widget.Menu} p_oSubmenu Object representing the submenu that 
* subscribed to the event.
*/
_onParentMenuRender: function (p_sType, p_aArgs, p_oSubmenu) {

    var oParentMenu = p_oSubmenu.parent.parent,
    	oParentCfg = oParentMenu.cfg,

        oConfig = {

            constraintoviewport: oParentCfg.getProperty(_CONSTRAIN_TO_VIEWPORT),

            xy: [0,0],

            clicktohide: oParentCfg.getProperty(_CLICK_TO_HIDE),
                
            effect: oParentCfg.getProperty(_EFFECT),

            showdelay: oParentCfg.getProperty(_SHOW_DELAY),
            
            hidedelay: oParentCfg.getProperty(_HIDE_DELAY),

            submenuhidedelay: oParentCfg.getProperty(_SUBMENU_HIDE_DELAY),

            classname: oParentCfg.getProperty(_CLASSNAME),
            
            scrollincrement: oParentCfg.getProperty(_SCROLL_INCREMENT),
            
			maxheight: oParentCfg.getProperty(_MAX_HEIGHT),

            minscrollheight: oParentCfg.getProperty(_MIN_SCROLL_HEIGHT),
            
            iframe: oParentCfg.getProperty(_IFRAME),
            
            shadow: oParentCfg.getProperty(_SHADOW),

			preventcontextoverlap: oParentCfg.getProperty(_PREVENT_CONTEXT_OVERLAP),
            
            monitorresize: oParentCfg.getProperty(_MONITOR_RESIZE),

			keepopen: oParentCfg.getProperty(_KEEP_OPEN)

        },
        
        oLI;


	
	if (!(oParentMenu instanceof YAHOO.widget.MenuBar)) {

		oConfig[_SUBMENU_ALIGNMENT] = oParentCfg.getProperty(_SUBMENU_ALIGNMENT);

	}


    p_oSubmenu.cfg.applyConfig(oConfig);


    if (!this.lazyLoad) {

        oLI = this.parent.element;

        if (this.element.parentNode == oLI) {
    
            this.render();
    
        }
        else {

            this.render(oLI);
    
        }

    }
    
},


/**
* @method _onMenuItemDestroy
* @description "destroy" event handler for the menu's items.
* @private
* @param {String} p_sType String representing the name of the event 
* that was fired.
* @param {Array} p_aArgs Array of arguments sent when the event was fired.
* @param {YAHOO.widget.MenuItem} p_oItem Object representing the menu item 
* that fired the event.
*/
_onMenuItemDestroy: function (p_sType, p_aArgs, p_oItem) {

    this._removeItemFromGroupByValue(p_oItem.groupIndex, p_oItem);

},


/**
* @method _onMenuItemConfigChange
* @description "configchange" event handler for the menu's items.
* @private
* @param {String} p_sType String representing the name of the event that 
* was fired.
* @param {Array} p_aArgs Array of arguments sent when the event was fired.
* @param {YAHOO.widget.MenuItem} p_oItem Object representing the menu item 
* that fired the event.
*/
_onMenuItemConfigChange: function (p_sType, p_aArgs, p_oItem) {

    var sPropertyName = p_aArgs[0][0],
        oPropertyValue = p_aArgs[0][1],
        oSubmenu;


    switch(sPropertyName) {

        case _SELECTED:

            if (oPropertyValue === true) {

                this.activeItem = p_oItem;
            
            }

        break;

        case _SUBMENU:

            oSubmenu = p_aArgs[0][1];

            if (oSubmenu) {

                this._configureSubmenu(p_oItem);

            }

        break;

    }

},



// Public event handlers for configuration properties


/**
* @method configVisible
* @description Event handler for when the "visible" configuration property 
* the menu changes.
* @param {String} p_sType String representing the name of the event that 
* was fired.
* @param {Array} p_aArgs Array of arguments sent when the event was fired.
* @param {YAHOO.widget.Menu} p_oMenu Object representing the menu that 
* fired the event.
*/
configVisible: function (p_sType, p_aArgs, p_oMenu) {

    var bVisible,
        sDisplay;

    if (this.cfg.getProperty(_POSITION) == _DYNAMIC) {

        Menu.superclass.configVisible.call(this, p_sType, p_aArgs, p_oMenu);

    }
    else {

        bVisible = p_aArgs[0];
        sDisplay = Dom.getStyle(this.element, _DISPLAY);

        Dom.setStyle(this.element, _VISIBILITY, _VISIBLE);

        if (bVisible) {

            if (sDisplay != _BLOCK) {
                this.beforeShowEvent.fire();
                Dom.setStyle(this.element, _DISPLAY, _BLOCK);
                this.showEvent.fire();
            }
        
        }
        else {

			if (sDisplay == _BLOCK) {
				this.beforeHideEvent.fire();
				Dom.setStyle(this.element, _DISPLAY, _NONE);
				this.hideEvent.fire();
			}
        
        }

    }

},


/**
* @method configPosition
* @description Event handler for when the "position" configuration property 
* of the menu changes.
* @param {String} p_sType String representing the name of the event that 
* was fired.
* @param {Array} p_aArgs Array of arguments sent when the event was fired.
* @param {YAHOO.widget.Menu} p_oMenu Object representing the menu that 
* fired the event.
*/
configPosition: function (p_sType, p_aArgs, p_oMenu) {

    var oElement = this.element,
        sCSSPosition = p_aArgs[0] == _STATIC ? _STATIC : _ABSOLUTE,
        oCfg = this.cfg,
        nZIndex;


    Dom.setStyle(oElement, _POSITION, sCSSPosition);


    if (sCSSPosition == _STATIC) {

        // Statically positioned menus are visible by default
        
        Dom.setStyle(oElement, _DISPLAY, _BLOCK);

        oCfg.setProperty(_VISIBLE, true);

    }
    else {

        /*
            Even though the "visible" property is queued to 
            "false" by default, we need to set the "visibility" property to 
            "hidden" since Overlay's "configVisible" implementation checks the 
            element's "visibility" style property before deciding whether 
            or not to show an Overlay instance.
        */

        Dom.setStyle(oElement, _VISIBILITY, _HIDDEN);
    
    }

  	 
     if (sCSSPosition == _ABSOLUTE) { 	 
  	 
         nZIndex = oCfg.getProperty(_ZINDEX);
  	 
         if (!nZIndex || nZIndex === 0) { 	 
  	 
             oCfg.setProperty(_ZINDEX, 1); 	 
  	 
         } 	 
  	 
     }

},


/**
* @method configIframe
* @description Event handler for when the "iframe" configuration property of 
* the menu changes.
* @param {String} p_sType String representing the name of the event that 
* was fired.
* @param {Array} p_aArgs Array of arguments sent when the event was fired.
* @param {YAHOO.widget.Menu} p_oMenu Object representing the menu that 
* fired the event.
*/
configIframe: function (p_sType, p_aArgs, p_oMenu) {    

    if (this.cfg.getProperty(_POSITION) == _DYNAMIC) {

        Menu.superclass.configIframe.call(this, p_sType, p_aArgs, p_oMenu);

    }

},


/**
* @method configHideDelay
* @description Event handler for when the "hidedelay" configuration property 
* of the menu changes.
* @param {String} p_sType String representing the name of the event that 
* was fired.
* @param {Array} p_aArgs Array of arguments sent when the event was fired.
* @param {YAHOO.widget.Menu} p_oMenu Object representing the menu that 
* fired the event.
*/
configHideDelay: function (p_sType, p_aArgs, p_oMenu) {

    var nHideDelay = p_aArgs[0];

	this._useHideDelay = (nHideDelay > 0);

},


/**
* @method configContainer
* @description Event handler for when the "container" configuration property 
* of the menu changes.
* @param {String} p_sType String representing the name of the event that 
* was fired.
* @param {Array} p_aArgs Array of arguments sent when the event was fired.
* @param {YAHOO.widget.Menu} p_oMenu Object representing the menu that 
* fired the event.
*/
configContainer: function (p_sType, p_aArgs, p_oMenu) {

	var oElement = p_aArgs[0];

	if (Lang.isString(oElement)) {

        this.cfg.setProperty(_CONTAINER, Dom.get(oElement), true);

	}

},


/**
* @method _clearSetWidthFlag
* @description Change event listener for the "width" configuration property.  This listener is 
* added when a Menu's "width" configuration property is set by the "_setScrollHeight" method, and 
* is used to set the "_widthSetForScroll" property to "false" if the "width" configuration property 
* is changed after it was set by the "_setScrollHeight" method.  If the "_widthSetForScroll" 
* property is set to "false", and the "_setScrollHeight" method is in the process of tearing down 
* scrolling functionality, it will maintain the Menu's new width rather than reseting it.
* @private
*/
_clearSetWidthFlag: function () {

	this._widthSetForScroll = false;
	
	this.cfg.unsubscribeFromConfigEvent(_WIDTH, this._clearSetWidthFlag);

},


/**
* @method _setScrollHeight
* @description 
* @param {String} p_nScrollHeight Number representing the scrolling height of the Menu.
* @private
*/
_setScrollHeight: function (p_nScrollHeight) {

    var nScrollHeight = p_nScrollHeight,
		bRefireIFrameAndShadow = false,
		bSetWidth = false,
        oElement,
        oBody,
        oHeader,
        oFooter,
        fnMouseOver,
        fnMouseOut,
        nMinScrollHeight,
        nHeight,
        nOffsetWidth,
        sWidth;


	if (this.getItems().length > 0) {
	
        oElement = this.element;
        oBody = this.body;
        oHeader = this.header;
        oFooter = this.footer;
        fnMouseOver = this._onScrollTargetMouseOver;
        fnMouseOut = this._onScrollTargetMouseOut;
        nMinScrollHeight = this.cfg.getProperty(_MIN_SCROLL_HEIGHT);


		if (nScrollHeight > 0 && nScrollHeight < nMinScrollHeight) {
		
			nScrollHeight = nMinScrollHeight;
		
		}


		Dom.setStyle(oBody, _HEIGHT, _EMPTY_STRING);
		Dom.removeClass(oBody, _YUI_MENU_BODY_SCROLLED);
		oBody.scrollTop = 0;


		//	Need to set a width for the Menu to fix the following problems in 
		//	Firefox 2 and IE:

		//	#1) Scrolled Menus will render at 1px wide in Firefox 2

		//	#2) There is a bug in gecko-based browsers where an element whose 
		//	"position" property is set to "absolute" and "overflow" property is 
		//	set to "hidden" will not render at the correct width when its 
		//	offsetParent's "position" property is also set to "absolute."  It is 
		//	possible to work around this bug by specifying a value for the width 
		//	property in addition to overflow.

		//	#3) In IE it is necessary to give the Menu a width before the 
		//	scrollbars are rendered to prevent the Menu from rendering with a 
		//	width that is 100% of the browser viewport.
	
		bSetWidth = ((UA.gecko && UA.gecko < 1.9) || UA.ie);

		if (nScrollHeight > 0 && bSetWidth && !this.cfg.getProperty(_WIDTH)) {

			nOffsetWidth = oElement.offsetWidth;
	
			/*
				Measuring the difference of the offsetWidth before and after
				setting the "width" style attribute allows us to compute the 
				about of padding and borders applied to the element, which in 
				turn allows us to set the "width" property correctly.
			*/
			
			oElement.style.width = nOffsetWidth + _PX;
	
			sWidth = (nOffsetWidth - (oElement.offsetWidth - nOffsetWidth)) + _PX;


			this.cfg.unsubscribeFromConfigEvent(_WIDTH, this._clearSetWidthFlag);


			this.cfg.setProperty(_WIDTH, sWidth);


			/*
				Set a flag (_widthSetForScroll) to maintain some history regarding how the 
				"width" configuration property was set.  If the "width" configuration property 
				is set by something other than the "_setScrollHeight" method, it will be 
				necessary to maintain that new value and not clear the width if scrolling 
				is turned off.
			*/

			this._widthSetForScroll = true;

			this.cfg.subscribeToConfigEvent(_WIDTH, this._clearSetWidthFlag);
	
		}
	
	
		if (nScrollHeight > 0 && (!oHeader && !oFooter)) {
	
	
			this.setHeader(_NON_BREAKING_SPACE);
			this.setFooter(_NON_BREAKING_SPACE);
	
			oHeader = this.header;
			oFooter = this.footer;
	
			Dom.addClass(oHeader, _TOP_SCROLLBAR);
			Dom.addClass(oFooter, _BOTTOM_SCROLLBAR);
			
			oElement.insertBefore(oHeader, oBody);
			oElement.appendChild(oFooter);
		
		}
	
	
		nHeight = nScrollHeight;
	
	
		if (oHeader && oFooter) {
			nHeight = (nHeight - (oHeader.offsetHeight + oFooter.offsetHeight));
		}
	
	
		if ((nHeight > 0) && (oBody.offsetHeight > nScrollHeight)) {

	
			Dom.addClass(oBody, _YUI_MENU_BODY_SCROLLED);
			Dom.setStyle(oBody, _HEIGHT, (nHeight + _PX));

			if (!this._hasScrollEventHandlers) {
	
				Event.on(oHeader, _MOUSEOVER, fnMouseOver, this, true);
				Event.on(oHeader, _MOUSEOUT, fnMouseOut, this, true);
				Event.on(oFooter, _MOUSEOVER, fnMouseOver, this, true);
				Event.on(oFooter, _MOUSEOUT, fnMouseOut, this, true);
	
				this._hasScrollEventHandlers = true;
	
			}
	
			this._disableScrollHeader();
			this._enableScrollFooter();
			
			bRefireIFrameAndShadow = true;			
	
		}
		else if (oHeader && oFooter) {

	

			/*
				Only clear the the "width" configuration property if it was set the 
				"_setScrollHeight" method and wasn't changed by some other means after it was set.
			*/	
	
			if (this._widthSetForScroll) {
	

				this._widthSetForScroll = false;

				this.cfg.unsubscribeFromConfigEvent(_WIDTH, this._clearSetWidthFlag);
	
				this.cfg.setProperty(_WIDTH, _EMPTY_STRING);
			
			}
	
	
			this._enableScrollHeader();
			this._enableScrollFooter();
	
			if (this._hasScrollEventHandlers) {
	
				Event.removeListener(oHeader, _MOUSEOVER, fnMouseOver);
				Event.removeListener(oHeader, _MOUSEOUT, fnMouseOut);
				Event.removeListener(oFooter, _MOUSEOVER, fnMouseOver);
				Event.removeListener(oFooter, _MOUSEOUT, fnMouseOut);

				this._hasScrollEventHandlers = false;
	
			}

			oElement.removeChild(oHeader);
			oElement.removeChild(oFooter);
	
			this.header = null;
			this.footer = null;
			
			bRefireIFrameAndShadow = true;
		
		}


		if (bRefireIFrameAndShadow) {
	
			this.cfg.refireEvent(_IFRAME);
			this.cfg.refireEvent(_SHADOW);
		
		}
	
	}

},


/**
* @method _setMaxHeight
* @description "renderEvent" handler used to defer the setting of the 
* "maxheight" configuration property until the menu is rendered in lazy 
* load scenarios.
* @param {String} p_sType The name of the event that was fired.
* @param {Array} p_aArgs Collection of arguments sent when the event 
* was fired.
* @param {Number} p_nMaxHeight Number representing the value to set for the 
* "maxheight" configuration property.
* @private
*/
_setMaxHeight: function (p_sType, p_aArgs, p_nMaxHeight) {

    this._setScrollHeight(p_nMaxHeight);
    this.renderEvent.unsubscribe(this._setMaxHeight);

},


/**
* @method configMaxHeight
* @description Event handler for when the "maxheight" configuration property of 
* a Menu changes.
* @param {String} p_sType The name of the event that was fired.
* @param {Array} p_aArgs Collection of arguments sent when the event 
* was fired.
* @param {YAHOO.widget.Menu} p_oMenu The Menu instance fired
* the event.
*/
configMaxHeight: function (p_sType, p_aArgs, p_oMenu) {

	var nMaxHeight = p_aArgs[0];

	if (this.lazyLoad && !this.body && nMaxHeight > 0) {
	
		this.renderEvent.subscribe(this._setMaxHeight, nMaxHeight, this);

	}
	else {

		this._setScrollHeight(nMaxHeight);
	
	}

},


/**
* @method configClassName
* @description Event handler for when the "classname" configuration property of 
* a menu changes.
* @param {String} p_sType The name of the event that was fired.
* @param {Array} p_aArgs Collection of arguments sent when the event was fired.
* @param {YAHOO.widget.Menu} p_oMenu The Menu instance fired the event.
*/
configClassName: function (p_sType, p_aArgs, p_oMenu) {

    var sClassName = p_aArgs[0];

    if (this._sClassName) {

        Dom.removeClass(this.element, this._sClassName);

    }

    Dom.addClass(this.element, sClassName);
    this._sClassName = sClassName;

},


/**
* @method _onItemAdded
* @description "itemadded" event handler for a Menu instance.
* @private
* @param {String} p_sType The name of the event that was fired.
* @param {Array} p_aArgs Collection of arguments sent when the event 
* was fired.
*/
_onItemAdded: function (p_sType, p_aArgs) {

    var oItem = p_aArgs[0];
    
    if (oItem) {

        oItem.cfg.setProperty(_DISABLED, true);
    
    }

},


/**
* @method configDisabled
* @description Event handler for when the "disabled" configuration property of 
* a menu changes.
* @param {String} p_sType The name of the event that was fired.
* @param {Array} p_aArgs Collection of arguments sent when the event was fired.
* @param {YAHOO.widget.Menu} p_oMenu The Menu instance fired the event.
*/
configDisabled: function (p_sType, p_aArgs, p_oMenu) {

    var bDisabled = p_aArgs[0],
        aItems = this.getItems(),
        nItems,
        i;

    if (Lang.isArray(aItems)) {

        nItems = aItems.length;
    
        if (nItems > 0) {
        
            i = nItems - 1;
    
            do {
    
                aItems[i].cfg.setProperty(_DISABLED, bDisabled);
            
            }
            while (i--);
        
        }


        if (bDisabled) {

            this.clearActiveItem(true);

            Dom.addClass(this.element, _DISABLED);

            this.itemAddedEvent.subscribe(this._onItemAdded);

        }
        else {

            Dom.removeClass(this.element, _DISABLED);

            this.itemAddedEvent.unsubscribe(this._onItemAdded);

        }
        
    }

},


/**
* @method configShadow
* @description Event handler for when the "shadow" configuration property of 
* a menu changes.
* @param {String} p_sType The name of the event that was fired.
* @param {Array} p_aArgs Collection of arguments sent when the event was fired.
* @param {YAHOO.widget.Menu} p_oMenu The Menu instance fired the event.
*/
configShadow: function (p_sType, p_aArgs, p_oMenu) {

    var sizeShadow = function () {

        var oElement = this.element,
            oShadow = this._shadow;
    
        if (oShadow && oElement) {

			// Clear the previous width

			if (oShadow.style.width && oShadow.style.height) {
			
				oShadow.style.width = _EMPTY_STRING;
				oShadow.style.height = _EMPTY_STRING;
			
			}

            oShadow.style.width = (oElement.offsetWidth + 6) + _PX;
            oShadow.style.height = (oElement.offsetHeight + 1) + _PX;
            
        }
    
    };


    var replaceShadow = function () {

        this.element.appendChild(this._shadow);

    };


    var addShadowVisibleClass = function () {
    
        Dom.addClass(this._shadow, _YUI_MENU_SHADOW_VISIBLE);
    
    };
    

    var removeShadowVisibleClass = function () {

        Dom.removeClass(this._shadow, _YUI_MENU_SHADOW_VISIBLE);
    
    };


    var createShadow = function () {

        var oShadow = this._shadow,
            oElement;

        if (!oShadow) {

            oElement = this.element;


            if (!m_oShadowTemplate) {

                m_oShadowTemplate = document.createElement(_DIV_LOWERCASE);
                m_oShadowTemplate.className = _YUI_MENU_SHADOW_YUI_MENU_SHADOW_VISIBLE;
            
            }

            oShadow = m_oShadowTemplate.cloneNode(false);

            oElement.appendChild(oShadow);
            
            this._shadow = oShadow;

            this.beforeShowEvent.subscribe(addShadowVisibleClass);
            this.beforeHideEvent.subscribe(removeShadowVisibleClass);


            if (UA.ie) {
        
                /*
                     Need to call sizeShadow & syncIframe via setTimeout for 
                     IE 7 Quirks Mode and IE 6 Standards Mode and Quirks Mode 
                     or the shadow and iframe shim will not be sized and 
                     positioned properly.
                */
        
				Lang.later(0, this, function () {

                    sizeShadow.call(this); 
                    this.syncIframe();
				
				});


                this.cfg.subscribeToConfigEvent(_WIDTH, sizeShadow);
                this.cfg.subscribeToConfigEvent(_HEIGHT, sizeShadow);
                this.cfg.subscribeToConfigEvent(_MAX_HEIGHT, sizeShadow);
                this.changeContentEvent.subscribe(sizeShadow);

                Module.textResizeEvent.subscribe(sizeShadow, this, true);
                
                this.destroyEvent.subscribe(function () {
                
                    Module.textResizeEvent.unsubscribe(sizeShadow, this);
                
                });
        
            }

            this.cfg.subscribeToConfigEvent(_MAX_HEIGHT, replaceShadow);

        }

    };


    var onBeforeShow = function () {

    	if (this._shadow) {

			// If called because the "shadow" event was refired - just append again and resize
			
			replaceShadow.call(this);
			
			if (UA.ie) {
				sizeShadow.call(this);
			}
    	
    	}
    	else {
    
        	createShadow.call(this);
        
        }

        this.beforeShowEvent.unsubscribe(onBeforeShow);
    
    };


	var bShadow = p_aArgs[0];


    if (bShadow && this.cfg.getProperty(_POSITION) == _DYNAMIC) {

        if (this.cfg.getProperty(_VISIBLE)) {

			if (this._shadow) {

				// If the "shadow" event was refired - just append again and resize
				
				replaceShadow.call(this);
				
				if (UA.ie) {
					sizeShadow.call(this);
				}
				
			} 
			else {
            	createShadow.call(this);
            }
        
        }
        else {

            this.beforeShowEvent.subscribe(onBeforeShow);
        
        }
    
    }
    
},



// Public methods


/**
* @method initEvents
* @description Initializes the custom events for the menu.
*/
initEvents: function () {

	Menu.superclass.initEvents.call(this);

    // Create custom events

	var i = EVENT_TYPES.length - 1,
		aEventData,
		oCustomEvent;


	do {

		aEventData = EVENT_TYPES[i];

		oCustomEvent = this.createEvent(aEventData[1]);
		oCustomEvent.signature = CustomEvent.LIST;
		
		this[aEventData[0]] = oCustomEvent;

	}
	while (i--);

},


/**
* @method positionOffScreen
* @description Positions the menu outside of the boundaries of the browser's 
* viewport.  Called automatically when a menu is hidden to ensure that 
* it doesn't force the browser to render uncessary scrollbars.
*/
positionOffScreen: function () {

    var oIFrame = this.iframe,
    	oElement = this.element,
        sPos = this.OFF_SCREEN_POSITION;
    
    oElement.style.top = _EMPTY_STRING;
    oElement.style.left = _EMPTY_STRING;
    
    if (oIFrame) {

		oIFrame.style.top = sPos;
		oIFrame.style.left = sPos;
    
    }

},


/**
* @method getRoot
* @description Finds the menu's root menu.
*/
getRoot: function () {

    var oItem = this.parent,
        oParentMenu,
        returnVal;

    if (oItem) {

        oParentMenu = oItem.parent;

        returnVal = oParentMenu ? oParentMenu.getRoot() : this;

    }
    else {
    
        returnVal = this;
    
    }
    
    return returnVal;

},


/**
* @method toString
* @description Returns a string representing the menu.
* @return {String}
*/
toString: function () {

    var sReturnVal = _MENU,
        sId = this.id;

    if (sId) {

        sReturnVal += (_SPACE + sId);
    
    }

    return sReturnVal;

},


/**
* @method setItemGroupTitle
* @description Sets the title of a group of menu items.
* @param {String} p_sGroupTitle String specifying the title of the group.
* @param {Number} p_nGroupIndex Optional. Number specifying the group to which
* the title belongs.
*/
setItemGroupTitle: function (p_sGroupTitle, p_nGroupIndex) {

    var nGroupIndex,
        oTitle,
        i,
        nFirstIndex;
        
    if (Lang.isString(p_sGroupTitle) && p_sGroupTitle.length > 0) {

        nGroupIndex = Lang.isNumber(p_nGroupIndex) ? p_nGroupIndex : 0;
        oTitle = this._aGroupTitleElements[nGroupIndex];


        if (oTitle) {

            oTitle.innerHTML = p_sGroupTitle;
            
        }
        else {

            oTitle = document.createElement(this.GROUP_TITLE_TAG_NAME);
                    
            oTitle.innerHTML = p_sGroupTitle;

            this._aGroupTitleElements[nGroupIndex] = oTitle;

        }


        i = this._aGroupTitleElements.length - 1;

        do {

            if (this._aGroupTitleElements[i]) {

                Dom.removeClass(this._aGroupTitleElements[i], _FIRST_OF_TYPE);

                nFirstIndex = i;

            }

        }
        while (i--);


        if (nFirstIndex !== null) {

            Dom.addClass(this._aGroupTitleElements[nFirstIndex], 
                _FIRST_OF_TYPE);

        }

        this.changeContentEvent.fire();

    }

},



/**
* @method addItem
* @description Appends an item to the menu.
* @param {YAHOO.widget.MenuItem} p_oItem Object reference for the MenuItem 
* instance to be added to the menu.
* @param {String} p_oItem String specifying the text of the item to be added 
* to the menu.
* @param {Object} p_oItem Object literal containing a set of menu item 
* configuration properties.
* @param {Number} p_nGroupIndex Optional. Number indicating the group to
* which the item belongs.
* @return {YAHOO.widget.MenuItem}
*/
addItem: function (p_oItem, p_nGroupIndex) {

	return this._addItemToGroup(p_nGroupIndex, p_oItem);

},


/**
* @method addItems
* @description Adds an array of items to the menu.
* @param {Array} p_aItems Array of items to be added to the menu.  The array 
* can contain strings specifying the text for each item to be created, object
* literals specifying each of the menu item configuration properties, 
* or MenuItem instances.
* @param {Number} p_nGroupIndex Optional. Number specifying the group to 
* which the items belongs.
* @return {Array}
*/
addItems: function (p_aItems, p_nGroupIndex) {

    var nItems,
        aItems,
        oItem,
        i,
        returnVal;


    if (Lang.isArray(p_aItems)) {

        nItems = p_aItems.length;
        aItems = [];

        for(i=0; i<nItems; i++) {

            oItem = p_aItems[i];

            if (oItem) {

                if (Lang.isArray(oItem)) {
    
                    aItems[aItems.length] = this.addItems(oItem, i);
    
                }
                else {
    
                    aItems[aItems.length] = this._addItemToGroup(p_nGroupIndex, oItem);
                
                }

            }
    
        }


        if (aItems.length) {
        
            returnVal = aItems;
        
        }

    }

	return returnVal;

},


/**
* @method insertItem
* @description Inserts an item into the menu at the specified index.
* @param {YAHOO.widget.MenuItem} p_oItem Object reference for the MenuItem 
* instance to be added to the menu.
* @param {String} p_oItem String specifying the text of the item to be added 
* to the menu.
* @param {Object} p_oItem Object literal containing a set of menu item 
* configuration properties.
* @param {Number} p_nItemIndex Number indicating the ordinal position at which
* the item should be added.
* @param {Number} p_nGroupIndex Optional. Number indicating the group to which 
* the item belongs.
* @return {YAHOO.widget.MenuItem}
*/
insertItem: function (p_oItem, p_nItemIndex, p_nGroupIndex) {
    
	return this._addItemToGroup(p_nGroupIndex, p_oItem, p_nItemIndex);

},


/**
* @method removeItem
* @description Removes the specified item from the menu.
* @param {YAHOO.widget.MenuItem} p_oObject Object reference for the MenuItem 
* instance to be removed from the menu.
* @param {Number} p_oObject Number specifying the index of the item 
* to be removed.
* @param {Number} p_nGroupIndex Optional. Number specifying the group to 
* which the item belongs.
* @return {YAHOO.widget.MenuItem}
*/
removeItem: function (p_oObject, p_nGroupIndex) {

    var oItem,
    	returnVal;
    
    if (!Lang.isUndefined(p_oObject)) {

        if (p_oObject instanceof YAHOO.widget.MenuItem) {

            oItem = this._removeItemFromGroupByValue(p_nGroupIndex, p_oObject);           

        }
        else if (Lang.isNumber(p_oObject)) {

            oItem = this._removeItemFromGroupByIndex(p_nGroupIndex, p_oObject);

        }

        if (oItem) {

            oItem.destroy();


            returnVal = oItem;

        }

    }

	return returnVal;

},


/**
* @method getItems
* @description Returns an array of all of the items in the menu.
* @return {Array}
*/
getItems: function () {

    var aGroups = this._aItemGroups,
        nGroups,
        returnVal,
        aItems = [];


    if (Lang.isArray(aGroups)) {

        nGroups = aGroups.length;

        returnVal = ((nGroups == 1) ? aGroups[0] : (Array.prototype.concat.apply(aItems, aGroups)));

    }

	return returnVal;

},


/**
* @method getItemGroups
* @description Multi-dimensional Array representing the menu items as they 
* are grouped in the menu.
* @return {Array}
*/        
getItemGroups: function () {

    return this._aItemGroups;

},


/**
* @method getItem
* @description Returns the item at the specified index.
* @param {Number} p_nItemIndex Number indicating the ordinal position of the 
* item to be retrieved.
* @param {Number} p_nGroupIndex Optional. Number indicating the group to which 
* the item belongs.
* @return {YAHOO.widget.MenuItem}
*/
getItem: function (p_nItemIndex, p_nGroupIndex) {
    
    var aGroup,
    	returnVal;
    
    if (Lang.isNumber(p_nItemIndex)) {

        aGroup = this._getItemGroup(p_nGroupIndex);

        if (aGroup) {

            returnVal = aGroup[p_nItemIndex];
        
        }

    }
    
    return returnVal;
    
},


/**
* @method getSubmenus
* @description Returns an array of all of the submenus that are immediate 
* children of the menu.
* @return {Array}
*/
getSubmenus: function () {

    var aItems = this.getItems(),
        nItems = aItems.length,
        aSubmenus,
        oSubmenu,
        oItem,
        i;


    if (nItems > 0) {
        
        aSubmenus = [];

        for(i=0; i<nItems; i++) {

            oItem = aItems[i];
            
            if (oItem) {

                oSubmenu = oItem.cfg.getProperty(_SUBMENU);
                
                if (oSubmenu) {

                    aSubmenus[aSubmenus.length] = oSubmenu;

                }
            
            }
        
        }
    
    }

    return aSubmenus;

},


/**
* @method clearContent
* @description Removes all of the content from the menu, including the menu 
* items, group titles, header and footer.
*/
clearContent: function () {

    var aItems = this.getItems(),
        nItems = aItems.length,
        oElement = this.element,
        oBody = this.body,
        oHeader = this.header,
        oFooter = this.footer,
        oItem,
        oSubmenu,
        i;


    if (nItems > 0) {

        i = nItems - 1;

        do {

            oItem = aItems[i];

            if (oItem) {

                oSubmenu = oItem.cfg.getProperty(_SUBMENU);

                if (oSubmenu) {

                    this.cfg.configChangedEvent.unsubscribe(
                        this._onParentMenuConfigChange, oSubmenu);

                    this.renderEvent.unsubscribe(this._onParentMenuRender, 
                        oSubmenu);

                }
                
                this.removeItem(oItem, oItem.groupIndex);

            }
        
        }
        while (i--);

    }


    if (oHeader) {

        Event.purgeElement(oHeader);
        oElement.removeChild(oHeader);

    }
    

    if (oFooter) {

        Event.purgeElement(oFooter);
        oElement.removeChild(oFooter);
    }


    if (oBody) {

        Event.purgeElement(oBody);

        oBody.innerHTML = _EMPTY_STRING;

    }

    this.activeItem = null;

    this._aItemGroups = [];
    this._aListElements = [];
    this._aGroupTitleElements = [];

    this.cfg.setProperty(_WIDTH, null);

},


/**
* @method destroy
* @description Removes the menu's <code>&#60;div&#62;</code> element 
* (and accompanying child nodes) from the document.
*/
destroy: function () {

    // Remove all items

    this.clearContent();

    this._aItemGroups = null;
    this._aListElements = null;
    this._aGroupTitleElements = null;


    // Continue with the superclass implementation of this method

    Menu.superclass.destroy.call(this);
    

},


/**
* @method setInitialFocus
* @description Sets focus to the menu's first enabled item.
*/
setInitialFocus: function () {

    var oItem = this._getFirstEnabledItem();
    
    if (oItem) {

        oItem.focus();

    }
    
},


/**
* @method setInitialSelection
* @description Sets the "selected" configuration property of the menu's first 
* enabled item to "true."
*/
setInitialSelection: function () {

    var oItem = this._getFirstEnabledItem();
    
    if (oItem) {
    
        oItem.cfg.setProperty(_SELECTED, true);
    }        

},


/**
* @method clearActiveItem
* @description Sets the "selected" configuration property of the menu's active
* item to "false" and hides the item's submenu.
* @param {Boolean} p_bBlur Boolean indicating if the menu's active item 
* should be blurred.  
*/
clearActiveItem: function (p_bBlur) {

    if (this.cfg.getProperty(_SHOW_DELAY) > 0) {
    
        this._cancelShowDelay();
    
    }


    var oActiveItem = this.activeItem,
        oConfig,
        oSubmenu;

    if (oActiveItem) {

        oConfig = oActiveItem.cfg;

        if (p_bBlur) {

            oActiveItem.blur();
            
            this.getRoot()._hasFocus = true;
        
        }

        oConfig.setProperty(_SELECTED, false);

        oSubmenu = oConfig.getProperty(_SUBMENU);


        if (oSubmenu) {

            oSubmenu.hide();

        }

        this.activeItem = null;  

    }

},


/**
* @method focus
* @description Causes the menu to receive focus and fires the "focus" event.
*/
focus: function () {

    if (!this.hasFocus()) {

        this.setInitialFocus();
    
    }

},


/**
* @method blur
* @description Causes the menu to lose focus and fires the "blur" event.
*/    
blur: function () {

    var oItem;

    if (this.hasFocus()) {
    
        oItem = MenuManager.getFocusedMenuItem();
        
        if (oItem) {

            oItem.blur();

        }

    }

},


/**
* @method hasFocus
* @description Returns a boolean indicating whether or not the menu has focus.
* @return {Boolean}
*/
hasFocus: function () {

    return (MenuManager.getFocusedMenu() == this.getRoot());

},


_doItemSubmenuSubscribe: function (p_sType, p_aArgs, p_oObject) {

    var oItem = p_aArgs[0],
        oSubmenu = oItem.cfg.getProperty(_SUBMENU);

    if (oSubmenu) {
        oSubmenu.subscribe.apply(oSubmenu, p_oObject);
    }

},


_doSubmenuSubscribe: function (p_sType, p_aArgs, p_oObject) { 

    var oSubmenu = this.cfg.getProperty(_SUBMENU);
    
    if (oSubmenu) {
        oSubmenu.subscribe.apply(oSubmenu, p_oObject);
    }

},


/**
* Adds the specified CustomEvent subscriber to the menu and each of 
* its submenus.
* @method subscribe
* @param p_type     {string}   the type, or name of the event
* @param p_fn       {function} the function to exectute when the event fires
* @param p_obj      {Object}   An object to be passed along when the event 
*                              fires
* @param p_override {boolean}  If true, the obj passed in becomes the 
*                              execution scope of the listener
*/
subscribe: function () {

	//	Subscribe to the event for this Menu instance
    Menu.superclass.subscribe.apply(this, arguments);

	//	Subscribe to the "itemAdded" event so that all future submenus
	//	also subscribe to this event
    Menu.superclass.subscribe.call(this, _ITEM_ADDED, this._doItemSubmenuSubscribe, arguments);


    var aItems = this.getItems(),
        nItems,
        oItem,
        oSubmenu,
        i;
        

    if (aItems) {

        nItems = aItems.length;
        
        if (nItems > 0) {
        
            i = nItems - 1;
            
            do {

                oItem = aItems[i];
                oSubmenu = oItem.cfg.getProperty(_SUBMENU);
                
                if (oSubmenu) {
                    oSubmenu.subscribe.apply(oSubmenu, arguments);
                }
                else {
                    oItem.cfg.subscribeToConfigEvent(_SUBMENU, this._doSubmenuSubscribe, arguments);
                }

            }
            while (i--);
        
        }

    }

},


unsubscribe: function () {

	//	Remove the event for this Menu instance
    Menu.superclass.unsubscribe.apply(this, arguments);

	//	Remove the "itemAdded" event so that all future submenus don't have 
	//	the event handler
    Menu.superclass.unsubscribe.call(this, _ITEM_ADDED, this._doItemSubmenuSubscribe, arguments);


    var aItems = this.getItems(),
        nItems,
        oItem,
        oSubmenu,
        i;
        

    if (aItems) {

        nItems = aItems.length;
        
        if (nItems > 0) {
        
            i = nItems - 1;
            
            do {

                oItem = aItems[i];
                oSubmenu = oItem.cfg.getProperty(_SUBMENU);
                
                if (oSubmenu) {
                    oSubmenu.unsubscribe.apply(oSubmenu, arguments);
                }
                else {
                    oItem.cfg.unsubscribeFromConfigEvent(_SUBMENU, this._doSubmenuSubscribe, arguments);
                }

            }
            while (i--);
        
        }

    }

},


/**
* @description Initializes the class's configurable properties which can be
* changed using the menu's Config object ("cfg").
* @method initDefaultConfig
*/
initDefaultConfig: function () {

    Menu.superclass.initDefaultConfig.call(this);

    var oConfig = this.cfg;


    // Module documentation overrides

    /**
    * @config effect
    * @description Object or array of objects representing the ContainerEffect 
    * classes that are active for animating the container.  When set this 
    * property is automatically applied to all submenus.
    * @type Object
    * @default null
    */

    // Overlay documentation overrides


    /**
    * @config x
    * @description Number representing the absolute x-coordinate position of 
    * the Menu.  This property is only applied when the "position" 
    * configuration property is set to dynamic.
    * @type Number
    * @default null
    */
    

    /**
    * @config y
    * @description Number representing the absolute y-coordinate position of 
    * the Menu.  This property is only applied when the "position" 
    * configuration property is set to dynamic.
    * @type Number
    * @default null
    */


    /**
    * @description Array of the absolute x and y positions of the Menu.  This 
    * property is only applied when the "position" configuration property is 
    * set to dynamic.
    * @config xy
    * @type Number[]
    * @default null
    */
    

    /**
    * @config context
    * @description Array of context arguments for context-sensitive positioning.  
    * The format is: [id or element, element corner, context corner]. 
    * For example, setting this property to ["img1", "tl", "bl"] would 
    * align the Menu's top left corner to the context element's 
    * bottom left corner.  This property is only applied when the "position" 
    * configuration property is set to dynamic.
    * @type Array
    * @default null
    */
    
    
    /**
    * @config fixedcenter
    * @description Boolean indicating if the Menu should be anchored to the 
    * center of the viewport.  This property is only applied when the 
    * "position" configuration property is set to dynamic.
    * @type Boolean
    * @default false
    */
    
    
    /**
    * @config iframe
    * @description Boolean indicating whether or not the Menu should 
    * have an IFRAME shim; used to prevent SELECT elements from 
    * poking through an Overlay instance in IE6.  When set to "true", 
    * the iframe shim is created when the Menu instance is intially
    * made visible.  This property is only applied when the "position" 
    * configuration property is set to dynamic and is automatically applied 
    * to all submenus.
    * @type Boolean
    * @default true for IE6 and below, false for all other browsers.
    */


	// Add configuration attributes

    /*
        Change the default value for the "visible" configuration 
        property to "false" by re-adding the property.
    */

    /**
    * @config visible
    * @description Boolean indicating whether or not the menu is visible.  If 
    * the menu's "position" configuration property is set to "dynamic" (the 
    * default), this property toggles the menu's <code>&#60;div&#62;</code> 
    * element's "visibility" style property between "visible" (true) or 
    * "hidden" (false).  If the menu's "position" configuration property is 
    * set to "static" this property toggles the menu's 
    * <code>&#60;div&#62;</code> element's "display" style property 
    * between "block" (true) or "none" (false).
    * @default false
    * @type Boolean
    */
    oConfig.addProperty(
        VISIBLE_CONFIG.key, 
        {
            handler: this.configVisible, 
            value: VISIBLE_CONFIG.value, 
            validator: VISIBLE_CONFIG.validator
        }
     );


    /*
        Change the default value for the "constraintoviewport" configuration 
        property (inherited by YAHOO.widget.Overlay) to "true" by re-adding the property.
    */

    /**
    * @config constraintoviewport
    * @description Boolean indicating if the menu will try to remain inside 
    * the boundaries of the size of viewport.  This property is only applied 
    * when the "position" configuration property is set to dynamic and is 
    * automatically applied to all submenus.
    * @default true
    * @type Boolean
    */
    oConfig.addProperty(
        CONSTRAIN_TO_VIEWPORT_CONFIG.key, 
        {
            handler: this.configConstrainToViewport, 
            value: CONSTRAIN_TO_VIEWPORT_CONFIG.value, 
            validator: CONSTRAIN_TO_VIEWPORT_CONFIG.validator, 
            supercedes: CONSTRAIN_TO_VIEWPORT_CONFIG.supercedes 
        } 
    );


    /*
        Change the default value for the "preventcontextoverlap" configuration 
        property (inherited by YAHOO.widget.Overlay) to "true" by re-adding the property.
    */

	/**
	* @config preventcontextoverlap
	* @description Boolean indicating whether or not a submenu should overlap its parent MenuItem 
	* when the "constraintoviewport" configuration property is set to "true".
	* @type Boolean
	* @default true
	*/
	oConfig.addProperty(PREVENT_CONTEXT_OVERLAP_CONFIG.key, {

		value: PREVENT_CONTEXT_OVERLAP_CONFIG.value, 
		validator: PREVENT_CONTEXT_OVERLAP_CONFIG.validator, 
		supercedes: PREVENT_CONTEXT_OVERLAP_CONFIG.supercedes

	});


    /**
    * @config position
    * @description String indicating how a menu should be positioned on the 
    * screen.  Possible values are "static" and "dynamic."  Static menus are 
    * visible by default and reside in the normal flow of the document 
    * (CSS position: static).  Dynamic menus are hidden by default, reside 
    * out of the normal flow of the document (CSS position: absolute), and 
    * can overlay other elements on the screen.
    * @default dynamic
    * @type String
    */
    oConfig.addProperty(
        POSITION_CONFIG.key, 
        {
            handler: this.configPosition,
            value: POSITION_CONFIG.value, 
            validator: POSITION_CONFIG.validator,
            supercedes: POSITION_CONFIG.supercedes
        }
    );


    /**
    * @config submenualignment
    * @description Array defining how submenus should be aligned to their 
    * parent menu item. The format is: [itemCorner, submenuCorner]. By default
    * a submenu's top left corner is aligned to its parent menu item's top 
    * right corner.
    * @default ["tl","tr"]
    * @type Array
    */
    oConfig.addProperty(
        SUBMENU_ALIGNMENT_CONFIG.key, 
        { 
            value: SUBMENU_ALIGNMENT_CONFIG.value,
            suppressEvent: SUBMENU_ALIGNMENT_CONFIG.suppressEvent
        }
    );


    /**
    * @config autosubmenudisplay
    * @description Boolean indicating if submenus are automatically made 
    * visible when the user mouses over the menu's items.
    * @default true
    * @type Boolean
    */
	oConfig.addProperty(
	   AUTO_SUBMENU_DISPLAY_CONFIG.key, 
	   { 
	       value: AUTO_SUBMENU_DISPLAY_CONFIG.value, 
	       validator: AUTO_SUBMENU_DISPLAY_CONFIG.validator,
	       suppressEvent: AUTO_SUBMENU_DISPLAY_CONFIG.suppressEvent
       } 
    );


    /**
    * @config showdelay
    * @description Number indicating the time (in milliseconds) that should 
    * expire before a submenu is made visible when the user mouses over 
    * the menu's items.  This property is only applied when the "position" 
    * configuration property is set to dynamic and is automatically applied 
    * to all submenus.
    * @default 250
    * @type Number
    */
	oConfig.addProperty(
	   SHOW_DELAY_CONFIG.key, 
	   { 
	       value: SHOW_DELAY_CONFIG.value, 
	       validator: SHOW_DELAY_CONFIG.validator,
	       suppressEvent: SHOW_DELAY_CONFIG.suppressEvent
       } 
    );


    /**
    * @config hidedelay
    * @description Number indicating the time (in milliseconds) that should 
    * expire before the menu is hidden.  This property is only applied when 
    * the "position" configuration property is set to dynamic and is 
    * automatically applied to all submenus.
    * @default 0
    * @type Number
    */
	oConfig.addProperty(
	   HIDE_DELAY_CONFIG.key, 
	   { 
	       handler: this.configHideDelay,
	       value: HIDE_DELAY_CONFIG.value, 
	       validator: HIDE_DELAY_CONFIG.validator, 
	       suppressEvent: HIDE_DELAY_CONFIG.suppressEvent
       } 
    );


    /**
    * @config submenuhidedelay
    * @description Number indicating the time (in milliseconds) that should 
    * expire before a submenu is hidden when the user mouses out of a menu item 
    * heading in the direction of a submenu.  The value must be greater than or 
    * equal to the value specified for the "showdelay" configuration property.
    * This property is only applied when the "position" configuration property 
    * is set to dynamic and is automatically applied to all submenus.
    * @default 250
    * @type Number
    */
	oConfig.addProperty(
	   SUBMENU_HIDE_DELAY_CONFIG.key, 
	   { 
	       value: SUBMENU_HIDE_DELAY_CONFIG.value, 
	       validator: SUBMENU_HIDE_DELAY_CONFIG.validator,
	       suppressEvent: SUBMENU_HIDE_DELAY_CONFIG.suppressEvent
       } 
    );


    /**
    * @config clicktohide
    * @description Boolean indicating if the menu will automatically be 
    * hidden if the user clicks outside of it.  This property is only 
    * applied when the "position" configuration property is set to dynamic 
    * and is automatically applied to all submenus.
    * @default true
    * @type Boolean
    */
    oConfig.addProperty(
        CLICK_TO_HIDE_CONFIG.key,
        {
            value: CLICK_TO_HIDE_CONFIG.value,
            validator: CLICK_TO_HIDE_CONFIG.validator,
            suppressEvent: CLICK_TO_HIDE_CONFIG.suppressEvent
        }
    );


	/**
	* @config container
	* @description HTML element reference or string specifying the id 
	* attribute of the HTML element that the menu's markup should be 
	* rendered into.
	* @type <a href="http://www.w3.org/TR/2000/WD-DOM-Level-1-20000929/
	* level-one-html.html#ID-58190037">HTMLElement</a>|String
	* @default document.body
	*/
	oConfig.addProperty(
	   CONTAINER_CONFIG.key, 
	   { 
	       handler: this.configContainer,
	       value: document.body,
           suppressEvent: CONTAINER_CONFIG.suppressEvent
       } 
   );


    /**
    * @config scrollincrement
    * @description Number used to control the scroll speed of a menu.  Used to 
    * increment the "scrollTop" property of the menu's body by when a menu's 
    * content is scrolling.  When set this property is automatically applied 
    * to all submenus.
    * @default 1
    * @type Number
    */
    oConfig.addProperty(
        SCROLL_INCREMENT_CONFIG.key, 
        { 
            value: SCROLL_INCREMENT_CONFIG.value, 
            validator: SCROLL_INCREMENT_CONFIG.validator,
            supercedes: SCROLL_INCREMENT_CONFIG.supercedes,
            suppressEvent: SCROLL_INCREMENT_CONFIG.suppressEvent
        }
    );


    /**
    * @config minscrollheight
    * @description Number defining the minimum threshold for the "maxheight" 
    * configuration property.  When set this property is automatically applied 
    * to all submenus.
    * @default 90
    * @type Number
    */
    oConfig.addProperty(
        MIN_SCROLL_HEIGHT_CONFIG.key, 
        { 
            value: MIN_SCROLL_HEIGHT_CONFIG.value, 
            validator: MIN_SCROLL_HEIGHT_CONFIG.validator,
            supercedes: MIN_SCROLL_HEIGHT_CONFIG.supercedes,
            suppressEvent: MIN_SCROLL_HEIGHT_CONFIG.suppressEvent
        }
    );


    /**
    * @config maxheight
    * @description Number defining the maximum height (in pixels) for a menu's 
    * body element (<code>&#60;div class="bd"&#62;</code>).  Once a menu's body 
    * exceeds this height, the contents of the body are scrolled to maintain 
    * this value.  This value cannot be set lower than the value of the 
    * "minscrollheight" configuration property.
    * @default 0
    * @type Number
    */
    oConfig.addProperty(
       MAX_HEIGHT_CONFIG.key, 
       {
            handler: this.configMaxHeight,
            value: MAX_HEIGHT_CONFIG.value,
            validator: MAX_HEIGHT_CONFIG.validator,
            suppressEvent: MAX_HEIGHT_CONFIG.suppressEvent,
            supercedes: MAX_HEIGHT_CONFIG.supercedes            
       } 
    );


    /**
    * @config classname
    * @description String representing the CSS class to be applied to the 
    * menu's root <code>&#60;div&#62;</code> element.  The specified class(es)  
    * are appended in addition to the default class as specified by the menu's
    * CSS_CLASS_NAME constant. When set this property is automatically 
    * applied to all submenus.
    * @default null
    * @type String
    */
    oConfig.addProperty(
        CLASS_NAME_CONFIG.key, 
        { 
            handler: this.configClassName,
            value: CLASS_NAME_CONFIG.value, 
            validator: CLASS_NAME_CONFIG.validator,
            supercedes: CLASS_NAME_CONFIG.supercedes      
        }
    );


    /**
    * @config disabled
    * @description Boolean indicating if the menu should be disabled.  
    * Disabling a menu disables each of its items.  (Disabled menu items are 
    * dimmed and will not respond to user input or fire events.)  Disabled
    * menus have a corresponding "disabled" CSS class applied to their root
    * <code>&#60;div&#62;</code> element.
    * @default false
    * @type Boolean
    */
    oConfig.addProperty(
        DISABLED_CONFIG.key, 
        { 
            handler: this.configDisabled,
            value: DISABLED_CONFIG.value, 
            validator: DISABLED_CONFIG.validator,
            suppressEvent: DISABLED_CONFIG.suppressEvent
        }
    );


    /**
    * @config shadow
    * @description Boolean indicating if the menu should have a shadow.
    * @default true
    * @type Boolean
    */
    oConfig.addProperty(
        SHADOW_CONFIG.key, 
        { 
            handler: this.configShadow,
            value: SHADOW_CONFIG.value, 
            validator: SHADOW_CONFIG.validator
        }
    );


    /**
    * @config keepopen
    * @description Boolean indicating if the menu should remain open when clicked.
    * @default false
    * @type Boolean
    */
    oConfig.addProperty(
        KEEP_OPEN_CONFIG.key, 
        { 
            value: KEEP_OPEN_CONFIG.value, 
            validator: KEEP_OPEN_CONFIG.validator
        }
    );

}

}); // END YAHOO.lang.extend

})();



(function () {

/**
* Creates an item for a menu.
* 
* @param {String} p_oObject String specifying the text of the menu item.
* @param {<a href="http://www.w3.org/TR/2000/WD-DOM-Level-1-20000929/level-
* one-html.html#ID-74680021">HTMLLIElement</a>} p_oObject Object specifying 
* the <code>&#60;li&#62;</code> element of the menu item.
* @param {<a href="http://www.w3.org/TR/2000/WD-DOM-Level-1-20000929/level-
* one-html.html#ID-38450247">HTMLOptGroupElement</a>} p_oObject Object 
* specifying the <code>&#60;optgroup&#62;</code> element of the menu item.
* @param {<a href="http://www.w3.org/TR/2000/WD-DOM-Level-1-20000929/level-
* one-html.html#ID-70901257">HTMLOptionElement</a>} p_oObject Object 
* specifying the <code>&#60;option&#62;</code> element of the menu item.
* @param {Object} p_oConfig Optional. Object literal specifying the 
* configuration for the menu item. See configuration class documentation 
* for more details.
* @class MenuItem
* @constructor
*/
YAHOO.widget.MenuItem = function (p_oObject, p_oConfig) {

    if (p_oObject) {

        if (p_oConfig) {
    
            this.parent = p_oConfig.parent;
            this.value = p_oConfig.value;
            this.id = p_oConfig.id;

        }

        this.init(p_oObject, p_oConfig);

    }

};


var Dom = YAHOO.util.Dom,
    Module = YAHOO.widget.Module,
    Menu = YAHOO.widget.Menu,
    MenuItem = YAHOO.widget.MenuItem,
    CustomEvent = YAHOO.util.CustomEvent,
    UA = YAHOO.env.ua,
    Lang = YAHOO.lang,

	// Private string constants

	_TEXT = "text",
	_HASH = "#",
	_HYPHEN = "-",
	_HELP_TEXT = "helptext",
	_URL = "url",
	_TARGET = "target",
	_EMPHASIS = "emphasis",
	_STRONG_EMPHASIS = "strongemphasis",
	_CHECKED = "checked",
	_SUBMENU = "submenu",
	_DISABLED = "disabled",
	_SELECTED = "selected",
	_HAS_SUBMENU = "hassubmenu",
	_CHECKED_DISABLED = "checked-disabled",
	_HAS_SUBMENU_DISABLED = "hassubmenu-disabled",
	_HAS_SUBMENU_SELECTED = "hassubmenu-selected",
	_CHECKED_SELECTED = "checked-selected",
	_ONCLICK = "onclick",
	_CLASSNAME = "classname",
	_EMPTY_STRING = "",
	_OPTION = "OPTION",
	_OPTGROUP = "OPTGROUP",
	_LI_UPPERCASE = "LI",
	_HREF = "href",
	_SELECT = "SELECT",
	_DIV = "DIV",
	_START_HELP_TEXT = "<em class=\"helptext\">",
	_START_EM = "<em>",
	_END_EM = "</em>",
	_START_STRONG = "<strong>",
	_END_STRONG = "</strong>",
	_PREVENT_CONTEXT_OVERLAP = "preventcontextoverlap",
	_OBJ = "obj",
	_SCOPE = "scope",
	_NONE = "none",
	_VISIBLE = "visible",
	_SPACE = " ",
	_MENUITEM = "MenuItem",
	_CLICK = "click",
	_SHOW = "show",
	_HIDE = "hide",
	_LI_LOWERCASE = "li",
	_ANCHOR_TEMPLATE = "<a href=\"#\"></a>",

    EVENT_TYPES = [
    
        ["mouseOverEvent", "mouseover"],
        ["mouseOutEvent", "mouseout"],
        ["mouseDownEvent", "mousedown"],
        ["mouseUpEvent", "mouseup"],
        ["clickEvent", _CLICK],
        ["keyPressEvent", "keypress"],
        ["keyDownEvent", "keydown"],
        ["keyUpEvent", "keyup"],
        ["focusEvent", "focus"],
        ["blurEvent", "blur"],
        ["destroyEvent", "destroy"]
    
    ],

	TEXT_CONFIG = { 
		key: _TEXT, 
		value: _EMPTY_STRING, 
		validator: Lang.isString, 
		suppressEvent: true 
	}, 

	HELP_TEXT_CONFIG = { 
		key: _HELP_TEXT,
		supercedes: [_TEXT], 
		suppressEvent: true 
	},

	URL_CONFIG = { 
		key: _URL, 
		value: _HASH, 
		suppressEvent: true 
	}, 

	TARGET_CONFIG = { 
		key: _TARGET, 
		suppressEvent: true 
	}, 

	EMPHASIS_CONFIG = { 
		key: _EMPHASIS, 
		value: false, 
		validator: Lang.isBoolean, 
		suppressEvent: true, 
		supercedes: [_TEXT]
	}, 

	STRONG_EMPHASIS_CONFIG = { 
		key: _STRONG_EMPHASIS, 
		value: false, 
		validator: Lang.isBoolean, 
		suppressEvent: true,
		supercedes: [_TEXT]
	},

	CHECKED_CONFIG = { 
		key: _CHECKED, 
		value: false, 
		validator: Lang.isBoolean, 
		suppressEvent: true, 
		supercedes: [_DISABLED, _SELECTED]
	}, 

	SUBMENU_CONFIG = { 
		key: _SUBMENU,
		suppressEvent: true,
		supercedes: [_DISABLED, _SELECTED]
	},

	DISABLED_CONFIG = { 
		key: _DISABLED, 
		value: false, 
		validator: Lang.isBoolean, 
		suppressEvent: true,
		supercedes: [_TEXT, _SELECTED]
	},

	SELECTED_CONFIG = { 
		key: _SELECTED, 
		value: false, 
		validator: Lang.isBoolean, 
		suppressEvent: true
	},

	ONCLICK_CONFIG = { 
		key: _ONCLICK,
		suppressEvent: true
	},

	CLASS_NAME_CONFIG = { 
		key: _CLASSNAME, 
		value: null, 
		validator: Lang.isString,
		suppressEvent: true
	},
    
	KEY_LISTENER_CONFIG = {
		key: "keylistener", 
		value: null, 
		suppressEvent: true
	},

	m_oMenuItemTemplate = null,

    CLASS_NAMES = {};


/**
* @method getClassNameForState
* @description Returns a class name for the specified prefix and state.  If the class name does not 
* yet exist, it is created and stored in the CLASS_NAMES object to increase performance.
* @private
* @param {String} prefix String representing the prefix for the class name
* @param {String} state String representing a state - "disabled," "checked," etc.
*/  
var getClassNameForState = function (prefix, state) {

	var oClassNames = CLASS_NAMES[prefix];
	
	if (!oClassNames) {
		CLASS_NAMES[prefix] = {};
		oClassNames = CLASS_NAMES[prefix];
	}


	var sClassName = oClassNames[state];

	if (!sClassName) {
		sClassName = prefix + _HYPHEN + state;
		oClassNames[state] = sClassName;
	}

	if(state == 'selected' && (prefix == 'ui-menu-item ui-corner-all' || prefix == 'ui-menubar-item')) {
		return "ui-state-hover";
	} else if(state == 'selected' && prefix == 'ui-menu-item-label') {
		return "";
	}

	return sClassName;
};


/**
* @method addClassNameForState
* @description Applies a class name to a MenuItem instance's &#60;LI&#62; and &#60;A&#62; elements
* that represents a MenuItem's state - "disabled," "checked," etc.
* @private
* @param {String} state String representing a state - "disabled," "checked," etc.
*/  
var addClassNameForState = function (state) {

	Dom.addClass(this.element, getClassNameForState(this.CSS_CLASS_NAME, state));
	Dom.addClass(this._oAnchor, getClassNameForState(this.CSS_LABEL_CLASS_NAME, state));

};

/**
* @method removeClassNameForState
* @description Removes a class name from a MenuItem instance's &#60;LI&#62; and &#60;A&#62; elements
* that represents a MenuItem's state - "disabled," "checked," etc.
* @private
* @param {String} state String representing a state - "disabled," "checked," etc.
*/  
var removeClassNameForState = function (state) {

	Dom.removeClass(this.element, getClassNameForState(this.CSS_CLASS_NAME, state));
	Dom.removeClass(this._oAnchor, getClassNameForState(this.CSS_LABEL_CLASS_NAME, state));

};


MenuItem.prototype = {

    /**
    * @property CSS_CLASS_NAME
    * @description String representing the CSS class(es) to be applied to the 
    * <code>&#60;li&#62;</code> element of the menu item.
    * @default "yuimenuitem"
    * @final
    * @type String
    */
    CSS_CLASS_NAME: "ui-menu-item ui-corner-all",


    /**
    * @property CSS_LABEL_CLASS_NAME
    * @description String representing the CSS class(es) to be applied to the 
    * menu item's <code>&#60;a&#62;</code> element.
    * @default "yuimenuitemlabel"
    * @final
    * @type String
    */
    CSS_LABEL_CLASS_NAME: "ui-menu-item-label",


    /**
    * @property SUBMENU_TYPE
    * @description Object representing the type of menu to instantiate and 
    * add when parsing the child nodes of the menu item's source HTML element.
    * @final
    * @type YAHOO.widget.Menu
    */
    SUBMENU_TYPE: null,



    // Private member variables
    

    /**
    * @property _oAnchor
    * @description Object reference to the menu item's 
    * <code>&#60;a&#62;</code> element.
    * @default null 
    * @private
    * @type <a href="http://www.w3.org/TR/2000/WD-DOM-Level-1-20000929/level-
    * one-html.html#ID-48250443">HTMLAnchorElement</a>
    */
    _oAnchor: null,
    
    
    /**
    * @property _oHelpTextEM
    * @description Object reference to the menu item's help text 
    * <code>&#60;em&#62;</code> element.
    * @default null
    * @private
    * @type <a href="http://www.w3.org/TR/2000/WD-DOM-Level-1-20000929/level-
    * one-html.html#ID-58190037">HTMLElement</a>
    */
    _oHelpTextEM: null,
    
    
    /**
    * @property _oSubmenu
    * @description Object reference to the menu item's submenu.
    * @default null
    * @private
    * @type YAHOO.widget.Menu
    */
    _oSubmenu: null,


    /** 
    * @property _oOnclickAttributeValue
    * @description Object reference to the menu item's current value for the 
    * "onclick" configuration attribute.
    * @default null
    * @private
    * @type Object
    */
    _oOnclickAttributeValue: null,


    /**
    * @property _sClassName
    * @description The current value of the "classname" configuration attribute.
    * @default null
    * @private
    * @type String
    */
    _sClassName: null,



    // Public properties


	/**
    * @property constructor
	* @description Object reference to the menu item's constructor function.
    * @default YAHOO.widget.MenuItem
	* @type YAHOO.widget.MenuItem
	*/
	constructor: MenuItem,


    /**
    * @property index
    * @description Number indicating the ordinal position of the menu item in 
    * its group.
    * @default null
    * @type Number
    */
    index: null,


    /**
    * @property groupIndex
    * @description Number indicating the index of the group to which the menu 
    * item belongs.
    * @default null
    * @type Number
    */
    groupIndex: null,


    /**
    * @property parent
    * @description Object reference to the menu item's parent menu.
    * @default null
    * @type YAHOO.widget.Menu
    */
    parent: null,


    /**
    * @property element
    * @description Object reference to the menu item's 
    * <code>&#60;li&#62;</code> element.
    * @default <a href="http://www.w3.org/TR/2000/WD-DOM-Level-1-20000929/level
    * -one-html.html#ID-74680021">HTMLLIElement</a>
    * @type <a href="http://www.w3.org/TR/2000/WD-DOM-Level-1-20000929/level-
    * one-html.html#ID-74680021">HTMLLIElement</a>
    */
    element: null,


    /**
    * @property srcElement
    * @description Object reference to the HTML element (either 
    * <code>&#60;li&#62;</code>, <code>&#60;optgroup&#62;</code> or 
    * <code>&#60;option&#62;</code>) used create the menu item.
    * @default <a href="http://www.w3.org/TR/2000/WD-DOM-Level-1-20000929/
    * level-one-html.html#ID-74680021">HTMLLIElement</a>|<a href="http://www.
    * w3.org/TR/2000/WD-DOM-Level-1-20000929/level-one-html.html#ID-38450247"
    * >HTMLOptGroupElement</a>|<a href="http://www.w3.org/TR/2000/WD-DOM-
    * Level-1-20000929/level-one-html.html#ID-70901257">HTMLOptionElement</a>
    * @type <a href="http://www.w3.org/TR/2000/WD-DOM-Level-1-20000929/level-
    * one-html.html#ID-74680021">HTMLLIElement</a>|<a href="http://www.w3.
    * org/TR/2000/WD-DOM-Level-1-20000929/level-one-html.html#ID-38450247">
    * HTMLOptGroupElement</a>|<a href="http://www.w3.org/TR/2000/WD-DOM-
    * Level-1-20000929/level-one-html.html#ID-70901257">HTMLOptionElement</a>
    */
    srcElement: null,


    /**
    * @property value
    * @description Object reference to the menu item's value.
    * @default null
    * @type Object
    */
    value: null,


	/**
    * @property browser
    * @deprecated Use YAHOO.env.ua
	* @description String representing the browser.
	* @type String
	*/
	browser: Module.prototype.browser,


    /**
    * @property id
    * @description Id of the menu item's root <code>&#60;li&#62;</code> 
    * element.  This property should be set via the constructor using the 
    * configuration object literal.  If an id is not specified, then one will 
    * be created using the "generateId" method of the Dom utility.
    * @default null
    * @type String
    */
    id: null,



    // Events


    /**
    * @event destroyEvent
    * @description Fires when the menu item's <code>&#60;li&#62;</code> 
    * element is removed from its parent <code>&#60;ul&#62;</code> element.
    * @type YAHOO.util.CustomEvent
    */


    /**
    * @event mouseOverEvent
    * @description Fires when the mouse has entered the menu item.  Passes 
    * back the DOM Event object as an argument.
    * @type YAHOO.util.CustomEvent
    */


    /**
    * @event mouseOutEvent
    * @description Fires when the mouse has left the menu item.  Passes back 
    * the DOM Event object as an argument.
    * @type YAHOO.util.CustomEvent
    */


    /**
    * @event mouseDownEvent
    * @description Fires when the user mouses down on the menu item.  Passes 
    * back the DOM Event object as an argument.
    * @type YAHOO.util.CustomEvent
    */


    /**
    * @event mouseUpEvent
    * @description Fires when the user releases a mouse button while the mouse 
    * is over the menu item.  Passes back the DOM Event object as an argument.
    * @type YAHOO.util.CustomEvent
    */


    /**
    * @event clickEvent
    * @description Fires when the user clicks the on the menu item.  Passes 
    * back the DOM Event object as an argument.
    * @type YAHOO.util.CustomEvent
    */


    /**
    * @event keyPressEvent
    * @description Fires when the user presses an alphanumeric key when the 
    * menu item has focus.  Passes back the DOM Event object as an argument.
    * @type YAHOO.util.CustomEvent
    */


    /**
    * @event keyDownEvent
    * @description Fires when the user presses a key when the menu item has 
    * focus.  Passes back the DOM Event object as an argument.
    * @type YAHOO.util.CustomEvent
    */


    /**
    * @event keyUpEvent
    * @description Fires when the user releases a key when the menu item has 
    * focus.  Passes back the DOM Event object as an argument.
    * @type YAHOO.util.CustomEvent
    */


    /**
    * @event focusEvent
    * @description Fires when the menu item receives focus.
    * @type YAHOO.util.CustomEvent
    */


    /**
    * @event blurEvent
    * @description Fires when the menu item loses the input focus.
    * @type YAHOO.util.CustomEvent
    */


    /**
    * @method init
    * @description The MenuItem class's initialization method. This method is 
    * automatically called by the constructor, and sets up all DOM references 
    * for pre-existing markup, and creates required markup if it is not 
    * already present.
    * @param {String} p_oObject String specifying the text of the menu item.
    * @param {<a href="http://www.w3.org/TR/2000/WD-DOM-Level-1-20000929/level-
    * one-html.html#ID-74680021">HTMLLIElement</a>} p_oObject Object specifying 
    * the <code>&#60;li&#62;</code> element of the menu item.
    * @param {<a href="http://www.w3.org/TR/2000/WD-DOM-Level-1-20000929/level-
    * one-html.html#ID-38450247">HTMLOptGroupElement</a>} p_oObject Object 
    * specifying the <code>&#60;optgroup&#62;</code> element of the menu item.
    * @param {<a href="http://www.w3.org/TR/2000/WD-DOM-Level-1-20000929/level-
    * one-html.html#ID-70901257">HTMLOptionElement</a>} p_oObject Object 
    * specifying the <code>&#60;option&#62;</code> element of the menu item.
    * @param {Object} p_oConfig Optional. Object literal specifying the 
    * configuration for the menu item. See configuration class documentation 
    * for more details.
    */
    init: function (p_oObject, p_oConfig) {


        if (!this.SUBMENU_TYPE) {
    
            this.SUBMENU_TYPE = Menu;
    
        }


        // Create the config object

        this.cfg = new YAHOO.util.Config(this);

        this.initDefaultConfig();

        var oConfig = this.cfg,
            sURL = _HASH,
            oCustomEvent,
			aEventData,
            oAnchor,
            sTarget,
            sText,
            sId,
            i;


        if (Lang.isString(p_oObject)) {

            this._createRootNodeStructure();

            oConfig.queueProperty(_TEXT, p_oObject);

        }
        else if (p_oObject && p_oObject.tagName) {

            switch(p_oObject.tagName.toUpperCase()) {

                case _OPTION:

                    this._createRootNodeStructure();

                    oConfig.queueProperty(_TEXT, p_oObject.text);
                    oConfig.queueProperty(_DISABLED, p_oObject.disabled);

                    this.value = p_oObject.value;

                    this.srcElement = p_oObject;

                break;

                case _OPTGROUP:

                    this._createRootNodeStructure();

                    oConfig.queueProperty(_TEXT, p_oObject.label);
                    oConfig.queueProperty(_DISABLED, p_oObject.disabled);

                    this.srcElement = p_oObject;

                    this._initSubTree();

                break;

                case _LI_UPPERCASE:

                    // Get the anchor node (if it exists)
                    
                    oAnchor = Dom.getFirstChild(p_oObject);


                    // Capture the "text" and/or the "URL"

                    if (oAnchor) {

                        sURL = oAnchor.getAttribute(_HREF, 2);
                        sTarget = oAnchor.getAttribute(_TARGET);

                        sText = oAnchor.innerHTML;

                    }

                    this.srcElement = p_oObject;
                    this.element = p_oObject;
                    this._oAnchor = oAnchor;

                    /*
                        Set these properties silently to sync up the 
                        configuration object without making changes to the 
                        element's DOM
                    */ 

                    oConfig.setProperty(_TEXT, sText, true);
                    oConfig.setProperty(_URL, sURL, true);
                    oConfig.setProperty(_TARGET, sTarget, true);

                    this._initSubTree();

                break;

            }            

        }


        if (this.element) {

            sId = (this.srcElement || this.element).id;

            if (!sId) {

                sId = this.id || Dom.generateId();

                this.element.id = sId;

            }

            this.id = sId;


            Dom.addClass(this.element, this.CSS_CLASS_NAME);
            Dom.addClass(this._oAnchor, this.CSS_LABEL_CLASS_NAME);


			i = EVENT_TYPES.length - 1;

			do {

				aEventData = EVENT_TYPES[i];

				oCustomEvent = this.createEvent(aEventData[1]);
				oCustomEvent.signature = CustomEvent.LIST;
				
				this[aEventData[0]] = oCustomEvent;

			}
			while (i--);


            if (p_oConfig) {
    
                oConfig.applyConfig(p_oConfig);
    
            }        

            oConfig.fireQueue();

        }

    },



    // Private methods

    /**
    * @method _createRootNodeStructure
    * @description Creates the core DOM structure for the menu item.
    * @private
    */
    _createRootNodeStructure: function () {

        var oElement,
            oAnchor;

        if (!m_oMenuItemTemplate) {

            m_oMenuItemTemplate = document.createElement(_LI_LOWERCASE);
            m_oMenuItemTemplate.innerHTML = _ANCHOR_TEMPLATE;

        }

        oElement = m_oMenuItemTemplate.cloneNode(true);
        oElement.className = this.CSS_CLASS_NAME;

        oAnchor = oElement.firstChild;
        oAnchor.className = this.CSS_LABEL_CLASS_NAME;

        this.element = oElement;
        this._oAnchor = oAnchor;

    },


    /**
    * @method _initSubTree
    * @description Iterates the source element's childNodes collection and uses 
    * the child nodes to instantiate other menus.
    * @private
    */
    _initSubTree: function () {

        var oSrcEl = this.srcElement,
            oConfig = this.cfg,
            oNode,
            aOptions,
            nOptions,
            oMenu,
            n;


        if (oSrcEl.childNodes.length > 0) {

            if (this.parent.lazyLoad && this.parent.srcElement && 
                this.parent.srcElement.tagName.toUpperCase() == _SELECT) {

                oConfig.setProperty(
                        _SUBMENU, 
                        { id: Dom.generateId(), itemdata: oSrcEl.childNodes }
                    );

            }
            else {

                oNode = oSrcEl.firstChild;
                aOptions = [];
    
                do {
    
                    if (oNode && oNode.tagName) {
    
                        switch(oNode.tagName.toUpperCase()) {
                
                            case _DIV:
                
                                oConfig.setProperty(_SUBMENU, oNode);
                
                            break;
         
                            case _OPTION:
        
                                aOptions[aOptions.length] = oNode;
        
                            break;
               
                        }
                    
                    }
                
                }        
                while((oNode = oNode.nextSibling));
    
    
                nOptions = aOptions.length;
    
                if (nOptions > 0) {
    
                    oMenu = new this.SUBMENU_TYPE(Dom.generateId());
                    
                    oConfig.setProperty(_SUBMENU, oMenu);
    
                    for(n=0; n<nOptions; n++) {
        
                        oMenu.addItem((new oMenu.ITEM_TYPE(aOptions[n])));
        
                    }
        
                }
            
            }

        }

    },



    // Event handlers for configuration properties


    /**
    * @method configText
    * @description Event handler for when the "text" configuration property of 
    * the menu item changes.
    * @param {String} p_sType String representing the name of the event that 
    * was fired.
    * @param {Array} p_aArgs Array of arguments sent when the event was fired.
    * @param {YAHOO.widget.MenuItem} p_oItem Object representing the menu item
    * that fired the event.
    */
    configText: function (p_sType, p_aArgs, p_oItem) {

        var sText = p_aArgs[0],
            oConfig = this.cfg,
            oAnchor = this._oAnchor,
            sHelpText = oConfig.getProperty(_HELP_TEXT),
            sHelpTextHTML = _EMPTY_STRING,
            sEmphasisStartTag = _EMPTY_STRING,
            sEmphasisEndTag = _EMPTY_STRING;


        if (sText) {


            if (sHelpText) {
                    
                sHelpTextHTML = _START_HELP_TEXT + sHelpText + _END_EM;
            
            }


            if (oConfig.getProperty(_EMPHASIS)) {

                sEmphasisStartTag = _START_EM;
                sEmphasisEndTag = _END_EM;

            }


            if (oConfig.getProperty(_STRONG_EMPHASIS)) {

                sEmphasisStartTag = _START_STRONG;
                sEmphasisEndTag = _END_STRONG;
            
            }


            oAnchor.innerHTML = (sEmphasisStartTag + sText + sEmphasisEndTag + sHelpTextHTML);

        }

    },


    /**
    * @method configHelpText
    * @description Event handler for when the "helptext" configuration property 
    * of the menu item changes.
    * @param {String} p_sType String representing the name of the event that 
    * was fired.
    * @param {Array} p_aArgs Array of arguments sent when the event was fired.
    * @param {YAHOO.widget.MenuItem} p_oItem Object representing the menu item
    * that fired the event.
    */    
    configHelpText: function (p_sType, p_aArgs, p_oItem) {

        this.cfg.refireEvent(_TEXT);

    },


    /**
    * @method configURL
    * @description Event handler for when the "url" configuration property of 
    * the menu item changes.
    * @param {String} p_sType String representing the name of the event that 
    * was fired.
    * @param {Array} p_aArgs Array of arguments sent when the event was fired.
    * @param {YAHOO.widget.MenuItem} p_oItem Object representing the menu item
    * that fired the event.
    */    
    configURL: function (p_sType, p_aArgs, p_oItem) {

        var sURL = p_aArgs[0];

        if (!sURL) {

            sURL = _HASH;

        }

        var oAnchor = this._oAnchor;

        if (UA.opera) {

            oAnchor.removeAttribute(_HREF);
        
        }

        oAnchor.setAttribute(_HREF, sURL);

    },


    /**
    * @method configTarget
    * @description Event handler for when the "target" configuration property 
    * of the menu item changes.  
    * @param {String} p_sType String representing the name of the event that 
    * was fired.
    * @param {Array} p_aArgs Array of arguments sent when the event was fired.
    * @param {YAHOO.widget.MenuItem} p_oItem Object representing the menu item
    * that fired the event.
    */    
    configTarget: function (p_sType, p_aArgs, p_oItem) {

        var sTarget = p_aArgs[0],
            oAnchor = this._oAnchor;

        if (sTarget && sTarget.length > 0) {

            oAnchor.setAttribute(_TARGET, sTarget);

        }
        else {

            oAnchor.removeAttribute(_TARGET);
        
        }

    },


    /**
    * @method configEmphasis
    * @description Event handler for when the "emphasis" configuration property
    * of the menu item changes.
    * @param {String} p_sType String representing the name of the event that 
    * was fired.
    * @param {Array} p_aArgs Array of arguments sent when the event was fired.
    * @param {YAHOO.widget.MenuItem} p_oItem Object representing the menu item
    * that fired the event.
    */    
    configEmphasis: function (p_sType, p_aArgs, p_oItem) {

        var bEmphasis = p_aArgs[0],
            oConfig = this.cfg;


        if (bEmphasis && oConfig.getProperty(_STRONG_EMPHASIS)) {

            oConfig.setProperty(_STRONG_EMPHASIS, false);

        }


        oConfig.refireEvent(_TEXT);

    },


    /**
    * @method configStrongEmphasis
    * @description Event handler for when the "strongemphasis" configuration 
    * property of the menu item changes.
    * @param {String} p_sType String representing the name of the event that 
    * was fired.
    * @param {Array} p_aArgs Array of arguments sent when the event was fired.
    * @param {YAHOO.widget.MenuItem} p_oItem Object representing the menu item
    * that fired the event.
    */    
    configStrongEmphasis: function (p_sType, p_aArgs, p_oItem) {

        var bStrongEmphasis = p_aArgs[0],
            oConfig = this.cfg;


        if (bStrongEmphasis && oConfig.getProperty(_EMPHASIS)) {

            oConfig.setProperty(_EMPHASIS, false);

        }

        oConfig.refireEvent(_TEXT);

    },


    /**
    * @method configChecked
    * @description Event handler for when the "checked" configuration property 
    * of the menu item changes. 
    * @param {String} p_sType String representing the name of the event that 
    * was fired.
    * @param {Array} p_aArgs Array of arguments sent when the event was fired.
    * @param {YAHOO.widget.MenuItem} p_oItem Object representing the menu item
    * that fired the event.
    */    
    configChecked: function (p_sType, p_aArgs, p_oItem) {

        var bChecked = p_aArgs[0],
            oConfig = this.cfg;


        if (bChecked) {

            addClassNameForState.call(this, _CHECKED);

        }
        else {

            removeClassNameForState.call(this, _CHECKED);
        }


        oConfig.refireEvent(_TEXT);


        if (oConfig.getProperty(_DISABLED)) {

            oConfig.refireEvent(_DISABLED);

        }


        if (oConfig.getProperty(_SELECTED)) {

            oConfig.refireEvent(_SELECTED);

        }

    },



    /**
    * @method configDisabled
    * @description Event handler for when the "disabled" configuration property 
    * of the menu item changes. 
    * @param {String} p_sType String representing the name of the event that 
    * was fired.
    * @param {Array} p_aArgs Array of arguments sent when the event was fired.
    * @param {YAHOO.widget.MenuItem} p_oItem Object representing the menu item
    * that fired the event.
    */    
    configDisabled: function (p_sType, p_aArgs, p_oItem) {

        var bDisabled = p_aArgs[0],
            oConfig = this.cfg,
            oSubmenu = oConfig.getProperty(_SUBMENU),
            bChecked = oConfig.getProperty(_CHECKED);


        if (bDisabled) {

            if (oConfig.getProperty(_SELECTED)) {

                oConfig.setProperty(_SELECTED, false);

            }


			addClassNameForState.call(this, _DISABLED);


            if (oSubmenu) {

				addClassNameForState.call(this, _HAS_SUBMENU_DISABLED);
            
            }
            

            if (bChecked) {

				addClassNameForState.call(this, _CHECKED_DISABLED);

            }

        }
        else {

			removeClassNameForState.call(this, _DISABLED);


            if (oSubmenu) {

				removeClassNameForState.call(this, _HAS_SUBMENU_DISABLED);
            
            }
            

            if (bChecked) {

				removeClassNameForState.call(this, _CHECKED_DISABLED);

            }

        }

    },


    /**
    * @method configSelected
    * @description Event handler for when the "selected" configuration property 
    * of the menu item changes. 
    * @param {String} p_sType String representing the name of the event that 
    * was fired.
    * @param {Array} p_aArgs Array of arguments sent when the event was fired.
    * @param {YAHOO.widget.MenuItem} p_oItem Object representing the menu item
    * that fired the event.
    */    
    configSelected: function (p_sType, p_aArgs, p_oItem) {

        var oConfig = this.cfg,
        	oAnchor = this._oAnchor,
        	
            bSelected = p_aArgs[0],
            bChecked = oConfig.getProperty(_CHECKED),
            oSubmenu = oConfig.getProperty(_SUBMENU);


        if (UA.opera) {

            oAnchor.blur();
        
        }


        if (bSelected && !oConfig.getProperty(_DISABLED)) {

			addClassNameForState.call(this, _SELECTED);


            if (oSubmenu) {

				addClassNameForState.call(this, _HAS_SUBMENU_SELECTED);
            
            }


            if (bChecked) {

				addClassNameForState.call(this, _CHECKED_SELECTED);

            }

        }
        else {

			removeClassNameForState.call(this, _SELECTED);


            if (oSubmenu) {

				removeClassNameForState.call(this, _HAS_SUBMENU_SELECTED);
            
            }


            if (bChecked) {

				removeClassNameForState.call(this, _CHECKED_SELECTED);

            }

        }


        if (this.hasFocus() && UA.opera) {
        
            oAnchor.focus();
        
        }

    },


    /**
    * @method _onSubmenuBeforeHide
    * @description "beforehide" Custom Event handler for a submenu.
    * @private
    * @param {String} p_sType String representing the name of the event that 
    * was fired.
    * @param {Array} p_aArgs Array of arguments sent when the event was fired.
    */
    _onSubmenuBeforeHide: function (p_sType, p_aArgs) {

        var oItem = this.parent,
            oMenu;

        function onHide() {

            oItem._oAnchor.blur();
            oMenu.beforeHideEvent.unsubscribe(onHide);
        
        }


        if (oItem.hasFocus()) {

            oMenu = oItem.parent;

            oMenu.beforeHideEvent.subscribe(onHide);
        
        }
    
    },


    /**
    * @method configSubmenu
    * @description Event handler for when the "submenu" configuration property 
    * of the menu item changes. 
    * @param {String} p_sType String representing the name of the event that 
    * was fired.
    * @param {Array} p_aArgs Array of arguments sent when the event was fired.
    * @param {YAHOO.widget.MenuItem} p_oItem Object representing the menu item
    * that fired the event.
    */
    configSubmenu: function (p_sType, p_aArgs, p_oItem) {

        var oSubmenu = p_aArgs[0],
            oConfig = this.cfg,
            bLazyLoad = this.parent && this.parent.lazyLoad,
            oMenu,
            sSubmenuId,
            oSubmenuConfig;


        if (oSubmenu) {

            if (oSubmenu instanceof Menu) {

                oMenu = oSubmenu;
                oMenu.parent = this;
                oMenu.lazyLoad = bLazyLoad;

            }
            else if (Lang.isObject(oSubmenu) && oSubmenu.id && !oSubmenu.nodeType) {

                sSubmenuId = oSubmenu.id;
                oSubmenuConfig = oSubmenu;

                oSubmenuConfig.lazyload = bLazyLoad;
                oSubmenuConfig.parent = this;

                oMenu = new this.SUBMENU_TYPE(sSubmenuId, oSubmenuConfig);


                // Set the value of the property to the Menu instance

                oConfig.setProperty(_SUBMENU, oMenu, true);

            }
            else {

                oMenu = new this.SUBMENU_TYPE(oSubmenu, { lazyload: bLazyLoad, parent: this });


                // Set the value of the property to the Menu instance
                
                oConfig.setProperty(_SUBMENU, oMenu, true);

            }


            if (oMenu) {

				oMenu.cfg.setProperty(_PREVENT_CONTEXT_OVERLAP, true);

                addClassNameForState.call(this, _HAS_SUBMENU);


				if (oConfig.getProperty(_URL) === _HASH) {
				
					oConfig.setProperty(_URL, (_HASH + oMenu.id));
				
				}


                this._oSubmenu = oMenu;


                if (UA.opera) {
                
                    oMenu.beforeHideEvent.subscribe(this._onSubmenuBeforeHide);               
                
                }
            
            }

        }
        else {

			removeClassNameForState.call(this, _HAS_SUBMENU);

            if (this._oSubmenu) {

                this._oSubmenu.destroy();

            }

        }


        if (oConfig.getProperty(_DISABLED)) {

            oConfig.refireEvent(_DISABLED);

        }


        if (oConfig.getProperty(_SELECTED)) {

            oConfig.refireEvent(_SELECTED);

        }

    },


    /**
    * @method configOnClick
    * @description Event handler for when the "onclick" configuration property 
    * of the menu item changes. 
    * @param {String} p_sType String representing the name of the event that 
    * was fired.
    * @param {Array} p_aArgs Array of arguments sent when the event was fired.
    * @param {YAHOO.widget.MenuItem} p_oItem Object representing the menu item
    * that fired the event.
    */
    configOnClick: function (p_sType, p_aArgs, p_oItem) {

        var oObject = p_aArgs[0];

        /*
            Remove any existing listeners if a "click" event handler has 
            already been specified.
        */

        if (this._oOnclickAttributeValue && (this._oOnclickAttributeValue != oObject)) {

            this.clickEvent.unsubscribe(this._oOnclickAttributeValue.fn, 
                                this._oOnclickAttributeValue.obj);

            this._oOnclickAttributeValue = null;

        }


        if (!this._oOnclickAttributeValue && Lang.isObject(oObject) && 
            Lang.isFunction(oObject.fn)) {
            
            this.clickEvent.subscribe(oObject.fn, 
                ((_OBJ in oObject) ? oObject.obj : this), 
                ((_SCOPE in oObject) ? oObject.scope : null) );

            this._oOnclickAttributeValue = oObject;

        }
    
    },


    /**
    * @method configClassName
    * @description Event handler for when the "classname" configuration 
    * property of a menu item changes.
    * @param {String} p_sType String representing the name of the event that 
    * was fired.
    * @param {Array} p_aArgs Array of arguments sent when the event was fired.
    * @param {YAHOO.widget.MenuItem} p_oItem Object representing the menu item
    * that fired the event.
    */
    configClassName: function (p_sType, p_aArgs, p_oItem) {
    
        var sClassName = p_aArgs[0];
    
        if (this._sClassName) {
    
            Dom.removeClass(this.element, this._sClassName);
    
        }
    
        Dom.addClass(this.element, sClassName);
        this._sClassName = sClassName;
    
    },


    /**
    * @method _dispatchClickEvent
    * @description Dispatches a DOM "click" event to the anchor element of a 
	* MenuItem instance.
	* @private	
    */
	_dispatchClickEvent: function () {

		var oMenuItem = this,
			oAnchor,
			oEvent;

		if (!oMenuItem.cfg.getProperty(_DISABLED)) {

			oAnchor = Dom.getFirstChild(oMenuItem.element);

			//	Dispatch a "click" event to the MenuItem's anchor so that its
			//	"click" event handlers will get called in response to the user 
			//	pressing the keyboard shortcut defined by the "keylistener"
			//	configuration property.

			if (UA.ie) {
				oAnchor.fireEvent(_ONCLICK);
			}
			else {

				if ((UA.gecko && UA.gecko >= 1.9) || UA.opera || UA.webkit) {

					oEvent = document.createEvent("HTMLEvents");
					oEvent.initEvent(_CLICK, true, true);

				}
				else {

					oEvent = document.createEvent("MouseEvents");
					oEvent.initMouseEvent(_CLICK, true, true, window, 0, 0, 0, 
						0, 0, false, false, false, false, 0, null);

				}

				oAnchor.dispatchEvent(oEvent);

			}

		}

	},


    /**
    * @method _createKeyListener
    * @description "show" event handler for a Menu instance - responsible for 
	* setting up the KeyListener instance for a MenuItem.
	* @private	
    * @param {String} type String representing the name of the event that 
    * was fired.
    * @param {Array} args Array of arguments sent when the event was fired.
    * @param {Array} keyData Array of arguments sent when the event was fired.
    */
	_createKeyListener: function (type, args, keyData) {

		var oMenuItem = this,
			oMenu = oMenuItem.parent;

		var oKeyListener = new YAHOO.util.KeyListener(
										oMenu.element.ownerDocument, 
										keyData, 
										{
											fn: oMenuItem._dispatchClickEvent, 
											scope: oMenuItem, 
											correctScope: true });


		if (oMenu.cfg.getProperty(_VISIBLE)) {
			oKeyListener.enable();
		}


		oMenu.subscribe(_SHOW, oKeyListener.enable, null, oKeyListener);
		oMenu.subscribe(_HIDE, oKeyListener.disable, null, oKeyListener);
		
		oMenuItem._keyListener = oKeyListener;
		
		oMenu.unsubscribe(_SHOW, oMenuItem._createKeyListener, keyData);
		
	},


    /**
    * @method configKeyListener
    * @description Event handler for when the "keylistener" configuration 
    * property of a menu item changes.
    * @param {String} p_sType String representing the name of the event that 
    * was fired.
    * @param {Array} p_aArgs Array of arguments sent when the event was fired.
    */
    configKeyListener: function (p_sType, p_aArgs) {

		var oKeyData = p_aArgs[0],
			oMenuItem = this,
			oMenu = oMenuItem.parent;

		if (oMenuItem._keyData) {

			//	Unsubscribe from the "show" event in case the keylistener 
			//	config was changed before the Menu was ever made visible.

			oMenu.unsubscribe(_SHOW, 
					oMenuItem._createKeyListener, oMenuItem._keyData);

			oMenuItem._keyData = null;					
					
		}


		//	Tear down for the previous value of the "keylistener" property

		if (oMenuItem._keyListener) {

			oMenu.unsubscribe(_SHOW, oMenuItem._keyListener.enable);
			oMenu.unsubscribe(_HIDE, oMenuItem._keyListener.disable);

			oMenuItem._keyListener.disable();
			oMenuItem._keyListener = null;

		}


    	if (oKeyData) {
	
			oMenuItem._keyData = oKeyData;

			//	Defer the creation of the KeyListener instance until the 
			//	parent Menu is visible.  This is necessary since the 
			//	KeyListener instance needs to be bound to the document the 
			//	Menu has been rendered into.  Deferring creation of the 
			//	KeyListener instance also improves performance.

			oMenu.subscribe(_SHOW, oMenuItem._createKeyListener, 
				oKeyData, oMenuItem);
		}
    
    },


    // Public methods


	/**
    * @method initDefaultConfig
	* @description Initializes an item's configurable properties.
	*/
	initDefaultConfig : function () {

        var oConfig = this.cfg;


        // Define the configuration attributes

        /**
        * @config text
        * @description String specifying the text label for the menu item.  
        * When building a menu from existing HTML the value of this property
        * will be interpreted from the menu's markup.
        * @default ""
        * @type String
        */
        oConfig.addProperty(
            TEXT_CONFIG.key, 
            { 
                handler: this.configText, 
                value: TEXT_CONFIG.value, 
                validator: TEXT_CONFIG.validator, 
                suppressEvent: TEXT_CONFIG.suppressEvent 
            }
        );
        

        /**
        * @config helptext
        * @description String specifying additional instructional text to 
        * accompany the text for the menu item.
        * @deprecated Use "text" configuration property to add help text markup.  
        * For example: <code>oMenuItem.cfg.setProperty("text", "Copy &#60;em 
        * class=\"helptext\"&#62;Ctrl + C&#60;/em&#62;");</code>
        * @default null
        * @type String|<a href="http://www.w3.org/TR/
        * 2000/WD-DOM-Level-1-20000929/level-one-html.html#ID-58190037">
        * HTMLElement</a>
        */
        oConfig.addProperty(
            HELP_TEXT_CONFIG.key,
            {
                handler: this.configHelpText, 
                supercedes: HELP_TEXT_CONFIG.supercedes,
                suppressEvent: HELP_TEXT_CONFIG.suppressEvent 
            }
        );


        /**
        * @config url
        * @description String specifying the URL for the menu item's anchor's 
        * "href" attribute.  When building a menu from existing HTML the value 
        * of this property will be interpreted from the menu's markup.
        * @default "#"
        * @type String
        */        
        oConfig.addProperty(
            URL_CONFIG.key, 
            {
                handler: this.configURL, 
                value: URL_CONFIG.value, 
                suppressEvent: URL_CONFIG.suppressEvent
            }
        );


        /**
        * @config target
        * @description String specifying the value for the "target" attribute 
        * of the menu item's anchor element. <strong>Specifying a target will 
        * require the user to click directly on the menu item's anchor node in
        * order to cause the browser to navigate to the specified URL.</strong> 
        * When building a menu from existing HTML the value of this property 
        * will be interpreted from the menu's markup.
        * @default null
        * @type String
        */        
        oConfig.addProperty(
            TARGET_CONFIG.key, 
            {
                handler: this.configTarget, 
                suppressEvent: TARGET_CONFIG.suppressEvent
            }
        );


        /**
        * @config emphasis
        * @description Boolean indicating if the text of the menu item will be 
        * rendered with emphasis.
        * @deprecated Use the "text" configuration property to add emphasis.  
        * For example: <code>oMenuItem.cfg.setProperty("text", "&#60;em&#62;Some 
        * Text&#60;/em&#62;");</code>
        * @default false
        * @type Boolean
        */
        oConfig.addProperty(
            EMPHASIS_CONFIG.key, 
            { 
                handler: this.configEmphasis, 
                value: EMPHASIS_CONFIG.value, 
                validator: EMPHASIS_CONFIG.validator, 
                suppressEvent: EMPHASIS_CONFIG.suppressEvent,
                supercedes: EMPHASIS_CONFIG.supercedes
            }
        );


        /**
        * @config strongemphasis
        * @description Boolean indicating if the text of the menu item will be 
        * rendered with strong emphasis.
        * @deprecated Use the "text" configuration property to add strong emphasis.  
        * For example: <code>oMenuItem.cfg.setProperty("text", "&#60;strong&#62; 
        * Some Text&#60;/strong&#62;");</code>
        * @default false
        * @type Boolean
        */
        oConfig.addProperty(
            STRONG_EMPHASIS_CONFIG.key,
            {
                handler: this.configStrongEmphasis,
                value: STRONG_EMPHASIS_CONFIG.value,
                validator: STRONG_EMPHASIS_CONFIG.validator,
                suppressEvent: STRONG_EMPHASIS_CONFIG.suppressEvent,
                supercedes: STRONG_EMPHASIS_CONFIG.supercedes
            }
        );


        /**
        * @config checked
        * @description Boolean indicating if the menu item should be rendered 
        * with a checkmark.
        * @default false
        * @type Boolean
        */
        oConfig.addProperty(
            CHECKED_CONFIG.key, 
            {
                handler: this.configChecked, 
                value: CHECKED_CONFIG.value, 
                validator: CHECKED_CONFIG.validator, 
                suppressEvent: CHECKED_CONFIG.suppressEvent,
                supercedes: CHECKED_CONFIG.supercedes
            } 
        );


        /**
        * @config disabled
        * @description Boolean indicating if the menu item should be disabled.  
        * (Disabled menu items are  dimmed and will not respond to user input 
        * or fire events.)
        * @default false
        * @type Boolean
        */
        oConfig.addProperty(
            DISABLED_CONFIG.key,
            {
                handler: this.configDisabled,
                value: DISABLED_CONFIG.value,
                validator: DISABLED_CONFIG.validator,
                suppressEvent: DISABLED_CONFIG.suppressEvent
            }
        );


        /**
        * @config selected
        * @description Boolean indicating if the menu item should 
        * be highlighted.
        * @default false
        * @type Boolean
        */
        oConfig.addProperty(
            SELECTED_CONFIG.key,
            {
                handler: this.configSelected,
                value: SELECTED_CONFIG.value,
                validator: SELECTED_CONFIG.validator,
                suppressEvent: SELECTED_CONFIG.suppressEvent
            }
        );


        /**
        * @config submenu
        * @description Object specifying the submenu to be appended to the 
        * menu item.  The value can be one of the following: <ul><li>Object 
        * specifying a Menu instance.</li><li>Object literal specifying the
        * menu to be created.  Format: <code>{ id: [menu id], itemdata: 
        * [<a href="YAHOO.widget.Menu.html#itemData">array of values for 
        * items</a>] }</code>.</li><li>String specifying the id attribute 
        * of the <code>&#60;div&#62;</code> element of the menu.</li><li>
        * Object specifying the <code>&#60;div&#62;</code> element of the 
        * menu.</li></ul>
        * @default null
        * @type Menu|String|Object|<a href="http://www.w3.org/TR/2000/
        * WD-DOM-Level-1-20000929/level-one-html.html#ID-58190037">
        * HTMLElement</a>
        */
        oConfig.addProperty(
            SUBMENU_CONFIG.key, 
            {
                handler: this.configSubmenu, 
                supercedes: SUBMENU_CONFIG.supercedes,
                suppressEvent: SUBMENU_CONFIG.suppressEvent
            }
        );


        /**
        * @config onclick
        * @description Object literal representing the code to be executed when 
        * the item is clicked.  Format:<br> <code> {<br> 
        * <strong>fn:</strong> Function,   &#47;&#47; The handler to call when 
        * the event fires.<br> <strong>obj:</strong> Object, &#47;&#47; An 
        * object to  pass back to the handler.<br> <strong>scope:</strong> 
        * Object &#47;&#47; The object to use for the scope of the handler.
        * <br> } </code>
        * @type Object
        * @default null
        */
        oConfig.addProperty(
            ONCLICK_CONFIG.key, 
            {
                handler: this.configOnClick, 
                suppressEvent: ONCLICK_CONFIG.suppressEvent 
            }
        );


        /**
        * @config classname
        * @description CSS class to be applied to the menu item's root 
        * <code>&#60;li&#62;</code> element.  The specified class(es) are 
        * appended in addition to the default class as specified by the menu 
        * item's CSS_CLASS_NAME constant.
        * @default null
        * @type String
        */
        oConfig.addProperty(
            CLASS_NAME_CONFIG.key, 
            { 
                handler: this.configClassName,
                value: CLASS_NAME_CONFIG.value, 
                validator: CLASS_NAME_CONFIG.validator,
                suppressEvent: CLASS_NAME_CONFIG.suppressEvent 
            }
        );


        /**
        * @config keylistener
        * @description Object literal representing the key(s) that can be used 
 		* to trigger the MenuItem's "click" event.  Possible attributes are 
		* shift (boolean), alt (boolean), ctrl (boolean) and keys (either an int 
		* or an array of ints representing keycodes).
        * @default null
        * @type Object
        */
        oConfig.addProperty(
            KEY_LISTENER_CONFIG.key, 
            { 
                handler: this.configKeyListener,
                value: KEY_LISTENER_CONFIG.value, 
                suppressEvent: KEY_LISTENER_CONFIG.suppressEvent 
            }
        );

	},

    /**
    * @method getNextSibling
    * @description Finds the menu item's next sibling.
    * @return YAHOO.widget.MenuItem
    */
	getNextSibling: function () {
	
		var isUL = function (el) {
				return (el.nodeName.toLowerCase() === "ul");
			},
	
			menuitemEl = this.element,
			next = Dom.getNextSibling(menuitemEl),
			parent,
			sibling,
			list;
		
		if (!next) {
			
			parent = menuitemEl.parentNode;
			sibling = Dom.getNextSiblingBy(parent, isUL);
			
			if (sibling) {
				list = sibling;
			}
			else {
				list = Dom.getFirstChildBy(parent.parentNode, isUL);
			}
			
			next = Dom.getFirstChild(list);
			
		}

		return YAHOO.widget.MenuManager.getMenuItem(next.id);

	},

    /**
    * @method getNextEnabledSibling
    * @description Finds the menu item's next enabled sibling.
    * @return YAHOO.widget.MenuItem
    */
	getNextEnabledSibling: function () {
		
		var next = this.getNextSibling();
		
        return (next.cfg.getProperty(_DISABLED) || next.element.style.display == _NONE) ? next.getNextEnabledSibling() : next;
		
	},


    /**
    * @method getPreviousSibling
    * @description Finds the menu item's previous sibling.
    * @return {YAHOO.widget.MenuItem}
    */	
	getPreviousSibling: function () {

		var isUL = function (el) {
				return (el.nodeName.toLowerCase() === "ul");
			},

			menuitemEl = this.element,
			next = Dom.getPreviousSibling(menuitemEl),
			parent,
			sibling,
			list;
		
		if (!next) {
			
			parent = menuitemEl.parentNode;
			sibling = Dom.getPreviousSiblingBy(parent, isUL);
			
			if (sibling) {
				list = sibling;
			}
			else {
				list = Dom.getLastChildBy(parent.parentNode, isUL);
			}
			
			next = Dom.getLastChild(list);
			
		}

		return YAHOO.widget.MenuManager.getMenuItem(next.id);
		
	},


    /**
    * @method getPreviousEnabledSibling
    * @description Finds the menu item's previous enabled sibling.
    * @return {YAHOO.widget.MenuItem}
    */
	getPreviousEnabledSibling: function () {
		
		var next = this.getPreviousSibling();
		
        return (next.cfg.getProperty(_DISABLED) || next.element.style.display == _NONE) ? next.getPreviousEnabledSibling() : next;
		
	},


    /**
    * @method focus
    * @description Causes the menu item to receive the focus and fires the 
    * focus event.
    */
    focus: function () {

        var oParent = this.parent,
            oAnchor = this._oAnchor,
            oActiveItem = oParent.activeItem;


        function setFocus() {

            try {

                if (!(UA.ie && !document.hasFocus())) {
                
					if (oActiveItem) {
		
						oActiveItem.blurEvent.fire();
		
					}
	
					oAnchor.focus();
					
					this.focusEvent.fire();
                
                }

            }
            catch(e) {
            
            }

        }


        if (!this.cfg.getProperty(_DISABLED) && oParent && oParent.cfg.getProperty(_VISIBLE) && 
            this.element.style.display != _NONE) {


            /*
                Setting focus via a timer fixes a race condition in Firefox, IE 
                and Opera where the browser viewport jumps as it trys to 
                position and focus the menu.
            */

            Lang.later(0, this, setFocus);

        }

    },


    /**
    * @method blur
    * @description Causes the menu item to lose focus and fires the 
    * blur event.
    */    
    blur: function () {

        var oParent = this.parent;

        if (!this.cfg.getProperty(_DISABLED) && oParent && oParent.cfg.getProperty(_VISIBLE)) {

            Lang.later(0, this, function () {

                try {
    
                    this._oAnchor.blur();
                    this.blurEvent.fire();    

                } 
                catch (e) {
                
                }
                
            }, 0);

        }

    },


    /**
    * @method hasFocus
    * @description Returns a boolean indicating whether or not the menu item
    * has focus.
    * @return {Boolean}
    */
    hasFocus: function () {
    
        return (YAHOO.widget.MenuManager.getFocusedMenuItem() == this);
    
    },


	/**
    * @method destroy
	* @description Removes the menu item's <code>&#60;li&#62;</code> element 
	* from its parent <code>&#60;ul&#62;</code> element.
	*/
    destroy: function () {

        var oEl = this.element,
            oSubmenu,
            oParentNode,
            aEventData,
            i;


        if (oEl) {


            // If the item has a submenu, destroy it first

            oSubmenu = this.cfg.getProperty(_SUBMENU);

            if (oSubmenu) {
            
                oSubmenu.destroy();
            
            }


            // Remove the element from the parent node

            oParentNode = oEl.parentNode;

            if (oParentNode) {

                oParentNode.removeChild(oEl);

                this.destroyEvent.fire();

            }


            // Remove CustomEvent listeners

			i = EVENT_TYPES.length - 1;

			do {

				aEventData = EVENT_TYPES[i];
				
				this[aEventData[0]].unsubscribeAll();

			}
			while (i--);
            
            
            this.cfg.configChangedEvent.unsubscribeAll();

        }

    },


    /**
    * @method toString
    * @description Returns a string representing the menu item.
    * @return {String}
    */
    toString: function () {

        var sReturnVal = _MENUITEM,
            sId = this.id;

        if (sId) {
    
            sReturnVal += (_SPACE + sId);
        
        }

        return sReturnVal;
    
    }

};

Lang.augmentProto(MenuItem, YAHOO.util.EventProvider);

})();
(function () {

	var _XY = "xy",
		_MOUSEDOWN = "mousedown",
		_CONTEXTMENU = "ContextMenu",
		_SPACE = " ";

/**
* Creates a list of options or commands which are made visible in response to 
* an HTML element's "contextmenu" event ("mousedown" for Opera).
*
* @param {String} p_oElement String specifying the id attribute of the 
* <code>&#60;div&#62;</code> element of the context menu.
* @param {String} p_oElement String specifying the id attribute of the 
* <code>&#60;select&#62;</code> element to be used as the data source for the 
* context menu.
* @param {<a href="http://www.w3.org/TR/2000/WD-DOM-Level-1-20000929/level-one-
* html.html#ID-22445964">HTMLDivElement</a>} p_oElement Object specifying the 
* <code>&#60;div&#62;</code> element of the context menu.
* @param {<a href="http://www.w3.org/TR/2000/WD-DOM-Level-1-20000929/level-one-
* html.html#ID-94282980">HTMLSelectElement</a>} p_oElement Object specifying 
* the <code>&#60;select&#62;</code> element to be used as the data source for 
* the context menu.
* @param {Object} p_oConfig Optional. Object literal specifying the 
* configuration for the context menu. See configuration class documentation 
* for more details.
* @class ContextMenu
* @constructor
* @extends YAHOO.widget.Menu
* @namespace YAHOO.widget
*/
YAHOO.widget.ContextMenu = function(p_oElement, p_oConfig) {

    YAHOO.widget.ContextMenu.superclass.constructor.call(this, p_oElement, p_oConfig);

};


var Event = YAHOO.util.Event,
	UA = YAHOO.env.ua,
    ContextMenu = YAHOO.widget.ContextMenu,



    /**
    * Constant representing the name of the ContextMenu's events
    * @property EVENT_TYPES
    * @private
    * @final
    * @type Object
    */
    EVENT_TYPES = {

        "TRIGGER_CONTEXT_MENU": "triggerContextMenu",
        "CONTEXT_MENU": (UA.opera ? _MOUSEDOWN : "contextmenu"),
        "CLICK": "click"

    },
    
    
    /**
    * Constant representing the ContextMenu's configuration properties
    * @property DEFAULT_CONFIG
    * @private
    * @final
    * @type Object
    */
    TRIGGER_CONFIG = { 
		key: "trigger",
		suppressEvent: true
    };


/**
* @method position
* @description "beforeShow" event handler used to position the contextmenu.
* @private
* @param {String} p_sType String representing the name of the event that 
* was fired.
* @param {Array} p_aArgs Array of arguments sent when the event was fired.
* @param {Array} p_aPos Array representing the xy position for the context menu.
*/
function position(p_sType, p_aArgs, p_aPos) {

    this.cfg.setProperty(_XY, p_aPos);
    
    this.beforeShowEvent.unsubscribe(position, p_aPos);

}


YAHOO.lang.extend(ContextMenu, YAHOO.widget.Menu, {



// Private properties


/**
* @property _oTrigger
* @description Object reference to the current value of the "trigger" 
* configuration property.
* @default null
* @private
* @type String|<a href="http://www.w3.org/TR/2000/WD-DOM-Level-1-20000929/leve
* l-one-html.html#ID-58190037">HTMLElement</a>|Array
*/
_oTrigger: null,


/**
* @property _bCancelled
* @description Boolean indicating if the display of the context menu should 
* be cancelled.
* @default false
* @private
* @type Boolean
*/
_bCancelled: false,



// Public properties


/**
* @property contextEventTarget
* @description Object reference for the HTML element that was the target of the
* "contextmenu" DOM event ("mousedown" for Opera) that triggered the display of 
* the context menu.
* @default null
* @type <a href="http://www.w3.org/TR/2000/WD-DOM-Level-1-20000929/level-one-
* html.html#ID-58190037">HTMLElement</a>
*/
contextEventTarget: null,



// Events


/**
* @event triggerContextMenuEvent
* @description Custom Event wrapper for the "contextmenu" DOM event 
* ("mousedown" for Opera) fired by the element(s) that trigger the display of 
* the context menu.
*/
triggerContextMenuEvent: null,



/**
* @method init
* @description The ContextMenu class's initialization method. This method is 
* automatically called by the constructor, and sets up all DOM references for 
* pre-existing markup, and creates required markup if it is not already present.
* @param {String} p_oElement String specifying the id attribute of the 
* <code>&#60;div&#62;</code> element of the context menu.
* @param {String} p_oElement String specifying the id attribute of the 
* <code>&#60;select&#62;</code> element to be used as the data source for 
* the context menu.
* @param {<a href="http://www.w3.org/TR/2000/WD-DOM-Level-1-20000929/level-one-
* html.html#ID-22445964">HTMLDivElement</a>} p_oElement Object specifying the 
* <code>&#60;div&#62;</code> element of the context menu.
* @param {<a href="http://www.w3.org/TR/2000/WD-DOM-Level-1-20000929/level-one-
* html.html#ID-94282980">HTMLSelectElement</a>} p_oElement Object specifying 
* the <code>&#60;select&#62;</code> element to be used as the data source for 
* the context menu.
* @param {Object} p_oConfig Optional. Object literal specifying the 
* configuration for the context menu. See configuration class documentation 
* for more details.
*/
init: function(p_oElement, p_oConfig) {


    // Call the init of the superclass (YAHOO.widget.Menu)

    ContextMenu.superclass.init.call(this, p_oElement);


    this.beforeInitEvent.fire(ContextMenu);


    if (p_oConfig) {

        this.cfg.applyConfig(p_oConfig, true);

    }
    
    this.initEvent.fire(ContextMenu);
    
},


/**
* @method initEvents
* @description Initializes the custom events for the context menu.
*/
initEvents: function() {

	ContextMenu.superclass.initEvents.call(this);

    // Create custom events

    this.triggerContextMenuEvent = this.createEvent(EVENT_TYPES.TRIGGER_CONTEXT_MENU);

    this.triggerContextMenuEvent.signature = YAHOO.util.CustomEvent.LIST;

},


/**
* @method cancel
* @description Cancels the display of the context menu.
*/
cancel: function() {

    this._bCancelled = true;

},



// Private methods


/**
* @method _removeEventHandlers
* @description Removes all of the DOM event handlers from the HTML element(s) 
* whose "context menu" event ("click" for Opera) trigger the display of 
* the context menu.
* @private
*/
_removeEventHandlers: function() {

    var oTrigger = this._oTrigger;


    // Remove the event handlers from the trigger(s)

    if (oTrigger) {

        Event.removeListener(oTrigger, EVENT_TYPES.CONTEXT_MENU, this._onTriggerContextMenu);    
        
        if (UA.opera) {
        
            Event.removeListener(oTrigger, EVENT_TYPES.CLICK, this._onTriggerClick);
    
        }

    }

},



// Private event handlers



/**
* @method _onTriggerClick
* @description "click" event handler for the HTML element(s) identified as the 
* "trigger" for the context menu.  Used to cancel default behaviors in Opera.
* @private
* @param {Event} p_oEvent Object representing the DOM event object passed back 
* by the event utility (YAHOO.util.Event).
* @param {YAHOO.widget.ContextMenu} p_oMenu Object representing the context 
* menu that is handling the event.
*/
_onTriggerClick: function(p_oEvent, p_oMenu) {

    if (p_oEvent.ctrlKey) {
    
        Event.stopEvent(p_oEvent);

    }
    
},


/**
* @method _onTriggerContextMenu
* @description "contextmenu" event handler ("mousedown" for Opera) for the HTML 
* element(s) that trigger the display of the context menu.
* @private
* @param {Event} p_oEvent Object representing the DOM event object passed back 
* by the event utility (YAHOO.util.Event).
* @param {YAHOO.widget.ContextMenu} p_oMenu Object representing the context 
* menu that is handling the event.
*/
_onTriggerContextMenu: function(p_oEvent, p_oMenu) {

    var aXY;

    if (!(p_oEvent.type == _MOUSEDOWN && !p_oEvent.ctrlKey)) {
	
		this.contextEventTarget = Event.getTarget(p_oEvent);
	
		this.triggerContextMenuEvent.fire(p_oEvent);
		
	
		if (!this._bCancelled) {

			/*
				Prevent the browser's default context menu from appearing and 
				stop the propagation of the "contextmenu" event so that 
				other ContextMenu instances are not displayed.
			*/

			Event.stopEvent(p_oEvent);


			// Hide any other Menu instances that might be visible

			YAHOO.widget.MenuManager.hideVisible();
			
	

			// Position and display the context menu
	
			aXY = Event.getXY(p_oEvent);
	
	
			if (!YAHOO.util.Dom.inDocument(this.element)) {
	
				this.beforeShowEvent.subscribe(position, aXY);
	
			}
			else {
	
				this.cfg.setProperty(_XY, aXY);
			
			}
	
	
			this.show();
	
		}
	
		this._bCancelled = false;

    }

},



// Public methods


/**
* @method toString
* @description Returns a string representing the context menu.
* @return {String}
*/
toString: function() {

    var sReturnVal = _CONTEXTMENU,
        sId = this.id;

    if (sId) {

        sReturnVal += (_SPACE + sId);
    
    }

    return sReturnVal;

},


/**
* @method initDefaultConfig
* @description Initializes the class's configurable properties which can be 
* changed using the context menu's Config object ("cfg").
*/
initDefaultConfig: function() {

    ContextMenu.superclass.initDefaultConfig.call(this);

    /**
    * @config trigger
    * @description The HTML element(s) whose "contextmenu" event ("mousedown" 
    * for Opera) trigger the display of the context menu.  Can be a string 
    * representing the id attribute of the HTML element, an object reference 
    * for the HTML element, or an array of strings or HTML element references.
    * @default null
    * @type String|<a href="http://www.w3.org/TR/2000/WD-DOM-Level-1-20000929/
    * level-one-html.html#ID-58190037">HTMLElement</a>|Array
    */
    this.cfg.addProperty(TRIGGER_CONFIG.key, 
        {
            handler: this.configTrigger, 
            suppressEvent: TRIGGER_CONFIG.suppressEvent 
        }
    );

},


/**
* @method destroy
* @description Removes the context menu's <code>&#60;div&#62;</code> element 
* (and accompanying child nodes) from the document.
*/
destroy: function() {

    // Remove the DOM event handlers from the current trigger(s)

    this._removeEventHandlers();


    // Continue with the superclass implementation of this method

    ContextMenu.superclass.destroy.call(this);

},



// Public event handlers for configuration properties


/**
* @method configTrigger
* @description Event handler for when the value of the "trigger" configuration 
* property changes. 
* @param {String} p_sType String representing the name of the event that 
* was fired.
* @param {Array} p_aArgs Array of arguments sent when the event was fired.
* @param {YAHOO.widget.ContextMenu} p_oMenu Object representing the context 
* menu that fired the event.
*/
configTrigger: function(p_sType, p_aArgs, p_oMenu) {
    
    var oTrigger = p_aArgs[0];

    if (oTrigger) {

        /*
            If there is a current "trigger" - remove the event handlers 
            from that element(s) before assigning new ones
        */

        if (this._oTrigger) {
        
            this._removeEventHandlers();

        }

        this._oTrigger = oTrigger;


        /*
            Listen for the "mousedown" event in Opera b/c it does not 
            support the "contextmenu" event
        */ 
  
        Event.on(oTrigger, EVENT_TYPES.CONTEXT_MENU, this._onTriggerContextMenu, this, true);


        /*
            Assign a "click" event handler to the trigger element(s) for
            Opera to prevent default browser behaviors.
        */

        if (UA.opera) {
        
            Event.on(oTrigger, EVENT_TYPES.CLICK, this._onTriggerClick, this, true);

        }

    }
    else {
   
        this._removeEventHandlers();
    
    }
    
}

}); // END YAHOO.lang.extend

}());



/**
* Creates an item for a context menu.
* 
* @param {String} p_oObject String specifying the text of the context menu item.
* @param {<a href="http://www.w3.org/TR/2000/WD-DOM-Level-1-20000929/level-
* one-html.html#ID-74680021">HTMLLIElement</a>} p_oObject Object specifying the 
* <code>&#60;li&#62;</code> element of the context menu item.
* @param {<a href="http://www.w3.org/TR/2000/WD-DOM-Level-1-20000929/level-
* one-html.html#ID-38450247">HTMLOptGroupElement</a>} p_oObject Object 
* specifying the <code>&#60;optgroup&#62;</code> element of the context 
* menu item.
* @param {<a href="http://www.w3.org/TR/2000/WD-DOM-Level-1-20000929/level-
* one-html.html#ID-70901257">HTMLOptionElement</a>} p_oObject Object specifying 
* the <code>&#60;option&#62;</code> element of the context menu item.
* @param {Object} p_oConfig Optional. Object literal specifying the 
* configuration for the context menu item. See configuration class 
* documentation for more details.
* @class ContextMenuItem
* @constructor
* @extends YAHOO.widget.MenuItem
* @deprecated As of version 2.4.0 items for YAHOO.widget.ContextMenu instances
* are of type YAHOO.widget.MenuItem.
*/
YAHOO.widget.ContextMenuItem = YAHOO.widget.MenuItem;
(function () {

	var Lang = YAHOO.lang,

		// String constants
	
		_STATIC = "static",
		_DYNAMIC_STATIC = "dynamic," + _STATIC,
		_DISABLED = "disabled",
		_SELECTED = "selected",
		_AUTO_SUBMENU_DISPLAY = "autosubmenudisplay",
		_SUBMENU = "submenu",
		_VISIBLE = "visible",
		_SPACE = " ",
		_SUBMENU_TOGGLE_REGION = "submenutoggleregion",
		_MENUBAR = "MenuBar";

/**
* Horizontal collection of items, each of which can contain a submenu.
* 
* @param {String} p_oElement String specifying the id attribute of the 
* <code>&#60;div&#62;</code> element of the menu bar.
* @param {String} p_oElement String specifying the id attribute of the 
* <code>&#60;select&#62;</code> element to be used as the data source for the 
* menu bar.
* @param {<a href="http://www.w3.org/TR/2000/WD-DOM-Level-1-20000929/level-
* one-html.html#ID-22445964">HTMLDivElement</a>} p_oElement Object specifying 
* the <code>&#60;div&#62;</code> element of the menu bar.
* @param {<a href="http://www.w3.org/TR/2000/WD-DOM-Level-1-20000929/level-
* one-html.html#ID-94282980">HTMLSelectElement</a>} p_oElement Object 
* specifying the <code>&#60;select&#62;</code> element to be used as the data 
* source for the menu bar.
* @param {Object} p_oConfig Optional. Object literal specifying the 
* configuration for the menu bar. See configuration class documentation for
* more details.
* @class MenuBar
* @constructor
* @extends YAHOO.widget.Menu
* @namespace YAHOO.widget
*/
YAHOO.widget.MenuBar = function(p_oElement, p_oConfig) {

    YAHOO.widget.MenuBar.superclass.constructor.call(this, p_oElement, p_oConfig);

};


/**
* @method checkPosition
* @description Checks to make sure that the value of the "position" property 
* is one of the supported strings. Returns true if the position is supported.
* @private
* @param {Object} p_sPosition String specifying the position of the menu.
* @return {Boolean}
*/
function checkPosition(p_sPosition) {

	var returnVal = false;

    if (Lang.isString(p_sPosition)) {

        returnVal = (_DYNAMIC_STATIC.indexOf((p_sPosition.toLowerCase())) != -1);

    }
    
    return returnVal;

}


var Event = YAHOO.util.Event,
    MenuBar = YAHOO.widget.MenuBar,

    POSITION_CONFIG =  { 
		key: "position", 
		value: _STATIC, 
		validator: checkPosition, 
		supercedes: [_VISIBLE] 
	}, 

	SUBMENU_ALIGNMENT_CONFIG =  { 
		key: "submenualignment", 
		value: ["tl","bl"]
	},

	AUTO_SUBMENU_DISPLAY_CONFIG =  { 
		key: _AUTO_SUBMENU_DISPLAY, 
		value: false, 
		validator: Lang.isBoolean,
		suppressEvent: true
	},
	
	SUBMENU_TOGGLE_REGION_CONFIG = {
		key: _SUBMENU_TOGGLE_REGION, 
		value: false, 
		validator: Lang.isBoolean
	};



Lang.extend(MenuBar, YAHOO.widget.Menu, {

/**
* @method init
* @description The MenuBar class's initialization method. This method is 
* automatically called by the constructor, and sets up all DOM references for 
* pre-existing markup, and creates required markup if it is not already present.
* @param {String} p_oElement String specifying the id attribute of the 
* <code>&#60;div&#62;</code> element of the menu bar.
* @param {String} p_oElement String specifying the id attribute of the 
* <code>&#60;select&#62;</code> element to be used as the data source for the 
* menu bar.
* @param {<a href="http://www.w3.org/TR/2000/WD-DOM-Level-1-20000929/level-
* one-html.html#ID-22445964">HTMLDivElement</a>} p_oElement Object specifying 
* the <code>&#60;div&#62;</code> element of the menu bar.
* @param {<a href="http://www.w3.org/TR/2000/WD-DOM-Level-1-20000929/level-
* one-html.html#ID-94282980">HTMLSelectElement</a>} p_oElement Object 
* specifying the <code>&#60;select&#62;</code> element to be used as the data 
* source for the menu bar.
* @param {Object} p_oConfig Optional. Object literal specifying the 
* configuration for the menu bar. See configuration class documentation for
* more details.
*/
init: function(p_oElement, p_oConfig) {

    if(!this.ITEM_TYPE) {

        this.ITEM_TYPE = YAHOO.widget.MenuBarItem;

    }


    // Call the init of the superclass (YAHOO.widget.Menu)

    MenuBar.superclass.init.call(this, p_oElement);


    this.beforeInitEvent.fire(MenuBar);


    if(p_oConfig) {

        this.cfg.applyConfig(p_oConfig, true);

    }

    this.initEvent.fire(MenuBar);

},



// Constants


/**
* @property CSS_CLASS_NAME
* @description String representing the CSS class(es) to be applied to the menu 
* bar's <code>&#60;div&#62;</code> element.
* @default "yuimenubar"
* @final
* @type String
*/
CSS_CLASS_NAME: "ui-menubar",


/**
* @property SUBMENU_TOGGLE_REGION_WIDTH
* @description Width (in pixels) of the area of a MenuBarItem that, when pressed, will toggle the
* display of the MenuBarItem's submenu.
* @default 20
* @final
* @type Number
*/
SUBMENU_TOGGLE_REGION_WIDTH: 20,


// Protected event handlers


/**
* @method _onKeyDown
* @description "keydown" Custom Event handler for the menu bar.
* @private
* @param {String} p_sType String representing the name of the event that 
* was fired.
* @param {Array} p_aArgs Array of arguments sent when the event was fired.
* @param {YAHOO.widget.MenuBar} p_oMenuBar Object representing the menu bar 
* that fired the event.
*/
_onKeyDown: function(p_sType, p_aArgs, p_oMenuBar) {

    var oEvent = p_aArgs[0],
        oItem = p_aArgs[1],
        oSubmenu,
        oItemCfg,
        oNextItem;


    if(oItem && !oItem.cfg.getProperty(_DISABLED)) {

        oItemCfg = oItem.cfg;

        switch(oEvent.keyCode) {
    
            case 37:    // Left arrow
            case 39:    // Right arrow
    
                if(oItem == this.activeItem && !oItemCfg.getProperty(_SELECTED)) {
    
                    oItemCfg.setProperty(_SELECTED, true);
    
                }
                else {
    
                    oNextItem = (oEvent.keyCode == 37) ? 
                        oItem.getPreviousEnabledSibling() : 
                        oItem.getNextEnabledSibling();
            
                    if(oNextItem) {
    
                        this.clearActiveItem();
    
                        oNextItem.cfg.setProperty(_SELECTED, true);
                        
						oSubmenu = oNextItem.cfg.getProperty(_SUBMENU);
						
						if(oSubmenu) {
					
							oSubmenu.show();
							oSubmenu.setInitialFocus();
						
						}
						else {
							oNextItem.focus();  
						}
    
                    }
    
                }
    
                Event.preventDefault(oEvent);
    
            break;
    
            case 40:    // Down arrow
    
                if(this.activeItem != oItem) {
    
                    this.clearActiveItem();
    
                    oItemCfg.setProperty(_SELECTED, true);
                    oItem.focus();
                
                }
    
                oSubmenu = oItemCfg.getProperty(_SUBMENU);
    
                if(oSubmenu) {
    
                    if(oSubmenu.cfg.getProperty(_VISIBLE)) {
    
                        oSubmenu.setInitialSelection();
                        oSubmenu.setInitialFocus();
                    
                    }
                    else {
    
                        oSubmenu.show();
                        oSubmenu.setInitialFocus();
                    
                    }
    
                }
    
                Event.preventDefault(oEvent);
    
            break;
    
        }

    }


    if(oEvent.keyCode == 27 && this.activeItem) { // Esc key

        oSubmenu = this.activeItem.cfg.getProperty(_SUBMENU);

        if(oSubmenu && oSubmenu.cfg.getProperty(_VISIBLE)) {
        
            oSubmenu.hide();
            this.activeItem.focus();
        
        }
        else {

            this.activeItem.cfg.setProperty(_SELECTED, false);
            this.activeItem.blur();
    
        }

        Event.preventDefault(oEvent);
    
    }

},


/**
* @method _onClick
* @description "click" event handler for the menu bar.
* @protected
* @param {String} p_sType String representing the name of the event that 
* was fired.
* @param {Array} p_aArgs Array of arguments sent when the event was fired.
* @param {YAHOO.widget.MenuBar} p_oMenuBar Object representing the menu bar 
* that fired the event.
*/
_onClick: function(p_sType, p_aArgs, p_oMenuBar) {

    MenuBar.superclass._onClick.call(this, p_sType, p_aArgs, p_oMenuBar);

    var oItem = p_aArgs[1],
        bReturnVal = true,
    	oItemEl,
        oEvent,
        oTarget,
        oActiveItem,
        oConfig,
        oSubmenu,
        nMenuItemX,
        nToggleRegion;


	var toggleSubmenuDisplay = function () {

		if(oSubmenu.cfg.getProperty(_VISIBLE)) {
		
			oSubmenu.hide();
		
		}
		else {
		
			oSubmenu.show();                    
		
		}
	
	};
    

    if(oItem && !oItem.cfg.getProperty(_DISABLED)) {

        oEvent = p_aArgs[0];
        oTarget = Event.getTarget(oEvent);
        oActiveItem = this.activeItem;
        oConfig = this.cfg;


        // Hide any other submenus that might be visible
    
        if(oActiveItem && oActiveItem != oItem) {
    
            this.clearActiveItem();
    
        }

    
        oItem.cfg.setProperty(_SELECTED, true);
    

        // Show the submenu for the item
    
        oSubmenu = oItem.cfg.getProperty(_SUBMENU);


        if(oSubmenu) {

			oItemEl = oItem.element;
			nMenuItemX = YAHOO.util.Dom.getX(oItemEl);
			nToggleRegion = nMenuItemX + (oItemEl.offsetWidth - this.SUBMENU_TOGGLE_REGION_WIDTH);

			if (oConfig.getProperty(_SUBMENU_TOGGLE_REGION)) {

				if (Event.getPageX(oEvent) > nToggleRegion) {

					toggleSubmenuDisplay();

					Event.preventDefault(oEvent);

					/*
						 Return false so that other click event handlers are not called when the 
						 user clicks inside the toggle region.
					*/
					bReturnVal = false;
				
				}
        
        	}
			else {

				toggleSubmenuDisplay();
            
            }
        
        }
    
    }


	return bReturnVal;

},



// Public methods

/**
* @method configSubmenuToggle
* @description Event handler for when the "submenutoggleregion" configuration property of 
* a MenuBar changes.
* @param {String} p_sType The name of the event that was fired.
* @param {Array} p_aArgs Collection of arguments sent when the event was fired.
*/
configSubmenuToggle: function (p_sType, p_aArgs) {

	var bSubmenuToggle = p_aArgs[0];
	
	if (bSubmenuToggle) {
	
		this.cfg.setProperty(_AUTO_SUBMENU_DISPLAY, false);
	
	}

},


/**
* @method toString
* @description Returns a string representing the menu bar.
* @return {String}
*/
toString: function() {

    var sReturnVal = _MENUBAR,
        sId = this.id;

    if(sId) {

        sReturnVal += (_SPACE + sId);
    
    }

    return sReturnVal;

},


/**
* @description Initializes the class's configurable properties which can be
* changed using the menu bar's Config object ("cfg").
* @method initDefaultConfig
*/
initDefaultConfig: function() {

    MenuBar.superclass.initDefaultConfig.call(this);

    var oConfig = this.cfg;

	// Add configuration properties


    /*
        Set the default value for the "position" configuration property
        to "static" by re-adding the property.
    */


    /**
    * @config position
    * @description String indicating how a menu bar should be positioned on the 
    * screen.  Possible values are "static" and "dynamic."  Static menu bars 
    * are visible by default and reside in the normal flow of the document 
    * (CSS position: static).  Dynamic menu bars are hidden by default, reside
    * out of the normal flow of the document (CSS position: absolute), and can 
    * overlay other elements on the screen.
    * @default static
    * @type String
    */
    oConfig.addProperty(
        POSITION_CONFIG.key, 
        {
            handler: this.configPosition, 
            value: POSITION_CONFIG.value, 
            validator: POSITION_CONFIG.validator,
            supercedes: POSITION_CONFIG.supercedes
        }
    );


    /*
        Set the default value for the "submenualignment" configuration property
        to ["tl","bl"] by re-adding the property.
    */

    /**
    * @config submenualignment
    * @description Array defining how submenus should be aligned to their 
    * parent menu bar item. The format is: [itemCorner, submenuCorner].
    * @default ["tl","bl"]
    * @type Array
    */
    oConfig.addProperty(
        SUBMENU_ALIGNMENT_CONFIG.key, 
        {
            value: SUBMENU_ALIGNMENT_CONFIG.value,
            suppressEvent: SUBMENU_ALIGNMENT_CONFIG.suppressEvent
        }
    );


    /*
        Change the default value for the "autosubmenudisplay" configuration 
        property to "false" by re-adding the property.
    */

    /**
    * @config autosubmenudisplay
    * @description Boolean indicating if submenus are automatically made 
    * visible when the user mouses over the menu bar's items.
    * @default false
    * @type Boolean
    */
	oConfig.addProperty(
	   AUTO_SUBMENU_DISPLAY_CONFIG.key, 
	   {
	       value: AUTO_SUBMENU_DISPLAY_CONFIG.value, 
	       validator: AUTO_SUBMENU_DISPLAY_CONFIG.validator,
	       suppressEvent: AUTO_SUBMENU_DISPLAY_CONFIG.suppressEvent
       } 
    );


    /**
    * @config submenutoggleregion
    * @description Boolean indicating if only a specific region of a MenuBarItem should toggle the 
    * display of a submenu.  The default width of the region is determined by the value of the
    * SUBMENU_TOGGLE_REGION_WIDTH property.  If set to true, the autosubmenudisplay 
    * configuration property will be set to false, and any click event listeners will not be 
    * called when the user clicks inside the submenu toggle region of a MenuBarItem.  If the 
    * user clicks outside of the submenu toggle region, the MenuBarItem will maintain its 
    * standard behavior.
    * @default false
    * @type Boolean
    */
	oConfig.addProperty(
	   SUBMENU_TOGGLE_REGION_CONFIG.key, 
	   {
	       value: SUBMENU_TOGGLE_REGION_CONFIG.value, 
	       validator: SUBMENU_TOGGLE_REGION_CONFIG.validator,
	       handler: this.configSubmenuToggle
       } 
    );

}
 
}); // END YAHOO.lang.extend

}());



/**
* Creates an item for a menu bar.
* 
* @param {String} p_oObject String specifying the text of the menu bar item.
* @param {<a href="http://www.w3.org/TR/2000/WD-DOM-Level-1-20000929/level-
* one-html.html#ID-74680021">HTMLLIElement</a>} p_oObject Object specifying the 
* <code>&#60;li&#62;</code> element of the menu bar item.
* @param {<a href="http://www.w3.org/TR/2000/WD-DOM-Level-1-20000929/level-
* one-html.html#ID-38450247">HTMLOptGroupElement</a>} p_oObject Object 
* specifying the <code>&#60;optgroup&#62;</code> element of the menu bar item.
* @param {<a href="http://www.w3.org/TR/2000/WD-DOM-Level-1-20000929/level-
* one-html.html#ID-70901257">HTMLOptionElement</a>} p_oObject Object specifying 
* the <code>&#60;option&#62;</code> element of the menu bar item.
* @param {Object} p_oConfig Optional. Object literal specifying the 
* configuration for the menu bar item. See configuration class documentation 
* for more details.
* @class MenuBarItem
* @constructor
* @extends YAHOO.widget.MenuItem
*/
YAHOO.widget.MenuBarItem = function(p_oObject, p_oConfig) {

    YAHOO.widget.MenuBarItem.superclass.constructor.call(this, p_oObject, p_oConfig);

};

YAHOO.lang.extend(YAHOO.widget.MenuBarItem, YAHOO.widget.MenuItem, {



/**
* @method init
* @description The MenuBarItem class's initialization method. This method is 
* automatically called by the constructor, and sets up all DOM references for 
* pre-existing markup, and creates required markup if it is not already present.
* @param {String} p_oObject String specifying the text of the menu bar item.
* @param {<a href="http://www.w3.org/TR/2000/WD-DOM-Level-1-20000929/level-
* one-html.html#ID-74680021">HTMLLIElement</a>} p_oObject Object specifying the 
* <code>&#60;li&#62;</code> element of the menu bar item.
* @param {<a href="http://www.w3.org/TR/2000/WD-DOM-Level-1-20000929/level-
* one-html.html#ID-38450247">HTMLOptGroupElement</a>} p_oObject Object 
* specifying the <code>&#60;optgroup&#62;</code> element of the menu bar item.
* @param {<a href="http://www.w3.org/TR/2000/WD-DOM-Level-1-20000929/level-
* one-html.html#ID-70901257">HTMLOptionElement</a>} p_oObject Object specifying 
* the <code>&#60;option&#62;</code> element of the menu bar item.
* @param {Object} p_oConfig Optional. Object literal specifying the 
* configuration for the menu bar item. See configuration class documentation 
* for more details.
*/
init: function(p_oObject, p_oConfig) {

    if(!this.SUBMENU_TYPE) {

        this.SUBMENU_TYPE = YAHOO.widget.Menu;

    }


    /* 
        Call the init of the superclass (YAHOO.widget.MenuItem)
        Note: We don't pass the user config in here yet 
        because we only want it executed once, at the lowest 
        subclass level.
    */ 

    YAHOO.widget.MenuBarItem.superclass.init.call(this, p_oObject);  


    var oConfig = this.cfg;

    if(p_oConfig) {

        oConfig.applyConfig(p_oConfig, true);

    }

    oConfig.fireQueue();

},



// Constants


/**
* @property CSS_CLASS_NAME
* @description String representing the CSS class(es) to be applied to the 
* <code>&#60;li&#62;</code> element of the menu bar item.
* @default "yuimenubaritem"
* @final
* @type String
*/
CSS_CLASS_NAME: "ui-menubar-item",


/**
* @property CSS_LABEL_CLASS_NAME
* @description String representing the CSS class(es) to be applied to the 
* menu bar item's <code>&#60;a&#62;</code> element.
* @default "yuimenubaritemlabel"
* @final
* @type String
*/
CSS_LABEL_CLASS_NAME: "ui-menubar-item-label",



// Public methods


/**
* @method toString
* @description Returns a string representing the menu bar item.
* @return {String}
*/
toString: function() {

    var sReturnVal = "MenuBarItem";

    if(this.cfg && this.cfg.getProperty("text")) {

        sReturnVal += (": " + this.cfg.getProperty("text"));

    }

    return sReturnVal;

}
    
}); // END YAHOO.lang.extend
YAHOO.register("menu", YAHOO.widget.Menu, {version: "2.8.0r4", build: "2449"});

/**
 * PrimeFaces Menu Widget
 */
PrimeFaces.widget.Menu = function(id, cfg) {
    var manager = YAHOO.widget.MenuManager;
    if(manager.getMenu(id)) {
        manager.removeMenuWithId(id);
    }

    PrimeFaces.widget.Menu.superclass.constructor.call(this, id, cfg);
 }

YAHOO.lang.extend(PrimeFaces.widget.Menu, YAHOO.widget.Menu,
{

});

/**
 * PrimeFaces ContextMenu Widget
 */
PrimeFaces.widget.ContextMenu = function(id, cfg) {
    var manager = YAHOO.widget.MenuManager;
    if(manager.getMenu(id)) {
        manager.removeMenuWithId(id);
    }

	PrimeFaces.widget.ContextMenu.superclass.constructor.call(this, id, cfg);
}

YAHOO.lang.extend(PrimeFaces.widget.ContextMenu, YAHOO.widget.ContextMenu,
{

});

/**
 * PrimeFaces Menubar Widget
 */
PrimeFaces.widget.MenuBar = function(id, cfg) {
    var manager = YAHOO.widget.MenuManager;
    if(manager.getMenu(id)) {
        manager.removeMenuWithId(id);
    }

	PrimeFaces.widget.MenuBar.superclass.constructor.call(this, id, cfg);
}

YAHOO.lang.extend(PrimeFaces.widget.MenuBar, YAHOO.widget.MenuBar,
{

});
