/* Simple JavaScript Inheritance
 * By John Resig http://ejohn.org/
 * MIT Licensed.
 */
// Inspired by base2 and Prototype
(function(){
  var initializing = false, fnTest = /xyz/.test(function(){xyz;}) ? /\b_super\b/ : /.*/;
  // The base Class implementation (does nothing)
  this.Class = function(){};

  // Create a new Class that inherits from this class
  Class.extend = function(prop) {
    var _super = this.prototype;

    // Instantiate a base class (but only create the instance,
    // don't run the init constructor)
    initializing = true;
    var prototype = new this();
    initializing = false;

    // Copy the properties over onto the new prototype
    for (var name in prop) {
      // Check if we're overwriting an existing function
      prototype[name] = typeof prop[name] == "function" &&
        typeof _super[name] == "function" && fnTest.test(prop[name]) ?
        (function(name, fn){
          return function() {
            var tmp = this._super;

            // Add a new ._super() method that is the same method
            // but on the super-class
            this._super = _super[name];

            // The method only need to be bound temporarily, so we
            // remove it when we're done executing
            var ret = fn.apply(this, arguments);
            this._super = tmp;

            return ret;
          };
        })(name, prop[name]) :
        prop[name];
    }

    // The dummy class constructor
    function Class() {
      // All construction is actually done in the init method
      if ( !initializing && this.init )
        this.init.apply(this, arguments);
    }

    // Populate our constructed prototype object
    Class.prototype = prototype;

    // Enforce the constructor to be what we expect
    Class.prototype.constructor = Class;

    // And make this class extendable
    Class.extend = arguments.callee;

    return Class;
  };
})();

if (!PrimeFaces.widget) {

    PrimeFaces.widget = {};

    /**
     * BaseWidget for PrimeFaces Widgets
     */
    PrimeFaces.widget.BaseWidget = Class.extend({

        init: function(cfg) {
            this.cfg = cfg;
            this.id = cfg.id;
            this.jqId = PrimeFaces.escapeClientId(this.id);
            this.jq = $(this.jqId);
            this.widgetVar = cfg.widgetVar;
            this.destroyListeners = [];
            this.refreshListeners = [];

            //remove script tag
            $(this.jqId + '_s').remove();

            if (this.widgetVar) {
                var $this = this;
                this.jq.on("remove", function() {
                    PrimeFaces.detachedWidgets.push($this.widgetVar);
                });
            }
        },

        //used in ajax updates, reloads the widget configuration
        refresh: function(cfg) {            
            this.destroyListeners = [];

            if (this.refreshListeners) {
                for (var i = 0; i < this.refreshListeners.length; i++) {
                    var refreshListener = this.refreshListeners[i];
                    refreshListener.call(this, this);
                }
            }
            this.refreshListeners = [];

            return this.init(cfg);
        },

        //will be called when the widget after a ajax request if the widget is detached
        destroy: function() {            
            PrimeFaces.debug("Destroyed detached widget: " + this.widgetVar);

            if (this.destroyListeners) {
                for (var i = 0; i < this.destroyListeners.length; i++) {
                    var destroyListener = this.destroyListeners[i];
                    destroyListener.call(this, this);
                }
            }
            this.destroyListeners = [];
        },

        //checks if the given widget is detached
        isDetached: function() {
            var element = document.getElementById(this.id);
            if (typeof(element) !== 'undefined' && element !== null) {
                return false;
            }

            return true;
        },

        //returns jquery object representing the main dom element related to the widget
        getJQ: function(){
            return this.jq;
        },

        /**
         * Removes the widget's script block from the DOM.
         *
         * @param {string} clientId The id of the widget.
         */
        removeScriptElement: function(clientId) {
            $(PrimeFaces.escapeClientId(clientId) + '_s').remove();
        },

        hasBehavior: function(event) {
            if(this.cfg.behaviors) {
                return this.cfg.behaviors[event] != undefined;
            }

            return false;
        },

        callBehavior: function(event, ext) {
            if(this.hasBehavior(event)) {
                this.cfg.behaviors[event].call(this, ext);
            }
        },

        /**
         * Gets behavior callback by name or null.
         *
         * @param name behavior name
         * @return {Function}
         */
        getBehavior: function(name) {
            return this.cfg.behaviors ? this.cfg.behaviors[name] : null;
        },

        addDestroyListener: function(listener) {
            if (!this.destroyListeners) {
                this.destroyListeners = [];
            }
            this.destroyListeners.push(listener);
        },

        addRefreshListener: function(listener) {
            if (!this.refreshListeners) {
                this.refreshListeners = [];
            }
            this.refreshListeners.push(listener);
        }
    });

    PrimeFaces.widget.DynamicOverlayWidget = PrimeFaces.widget.BaseWidget.extend({

        //@Override
        init: function(cfg) {
            this._super(cfg);

            PrimeFaces.utils.registerDynamicOverlay(this, this.jq, this.id);
        },

        //@Override
        refresh: function(cfg) {
            PrimeFaces.utils.removeModal(this);

            this.appendTo = null;
            this.modalOverlay = null;
            
            this._super(cfg);
        },

        //@Override
        destroy: function() {
            this._super();

            PrimeFaces.utils.removeModal(this);

            this.appendTo = null;
            this.modalOverlay = null;
        },

        enableModality: function() {
            this.modalOverlay = PrimeFaces.utils.addModal(this,
                this.jq.css('z-index') - 1,
                $.proxy(function() {
                    return this.getModalTabbables();
                }, this));
        },

        disableModality: function(){
            PrimeFaces.utils.removeModal(this);
            this.modalOverlay = null;
        },

        getModalTabbables: function(){
            return null;
        }
    });

    /**
     * Widgets that require to be visible to initialize properly for hidden container support
     */
    PrimeFaces.widget.DeferredWidget = PrimeFaces.widget.BaseWidget.extend({

        renderDeferred: function() {
            if(this.jq.is(':visible')) {
                this._render();
                this.postRender();
            }
            else {
                var container = this.jq.closest('.ui-hidden-container'),
                $this = this;

                if(container.length) {
                    this.addDeferredRender(this.id, container, function() {
                        return $this.render();
                    });
                }
            }
        },

        render: function() {
            if(this.jq.is(':visible')) {
                this._render();
                this.postRender();
                return true;
            }
            else {
                return false;
            }
        },

        /**
         * Must be overriden
         */
        _render: function() {
            throw 'Unsupported Operation';
        },

        postRender: function() {

        },

        //@Override
        destroy: function() {
            this._super();
            PrimeFaces.removeDeferredRenders(this.id);
        },

        addDeferredRender: function(widgetId, container, callback) {
            PrimeFaces.addDeferredRender(widgetId, container.attr('id'), callback);

            if(container.is(':hidden')) {
                var parentContainer = this.jq.closest('.ui-hidden-container');

                if(parentContainer.length) {
                    this.addDeferredRender(widgetId, container.parent().closest('.ui-hidden-container'), callback);
                }
            }
        }
    });
}