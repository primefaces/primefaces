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
        return this.init(cfg);
    },
    
    //will be called when the widget after a ajax request if the widget is detached
    destroy: function() {
    	PrimeFaces.debug("Destroyed detached widget: " + this.widgetVar);
    },

    //checks if the given widget is detached
    isDetached: function() {
    	return document.getElementById(this.id) === null;
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