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

    /**
     * This object contains the  widget classes that are currently available. The key is the name of the widget, the
     * value the class (constructor) of the widget. Please note that widgets are usually created by the PrimeFaces
     * framework and should not be created manually.
     *
     * There are a few base classes defined by PrimeFaces that you can use when writing the client-side part of your
     * custom widget:
     *
     * - {@link BaseWidget}: Base class that you should extend if you do not required any advanced functionality.
     * - {@link DeferredWidget}: When you widget needs to be initialized on the client in a way does required the
     * element to be visible, you can use this class as a base. A widget may not be visible, for example, when it is
     * inside a dialog or tab. The deferred widget provides the the method {@link DeferredWidget.addDeferredRender}
     * (to register a listener) and {@link DeferredWidget.renderDeferred} (to render the widget once it is visible).
     * - {@link DynamicOverlayWidget}: When your widget is an overlay with dynamically loaded content, you can use this
     * base class.
     *
     * Note to TypeScript users: you could use these widget classes to check whether a widget instance is of a certain
     * type:
     *
     * <details>
     *
     * <summary>Click to view</summary>
     *
     * ```typescript
     * type Constructor<T> = new (...args: any) => T;
     *
     * function getWidgetName(
     *   widgetType:
     *     PrimeFaces.widget.BaseWidget
     *     | Constructor<PrimeFaces.widget.BaseWidget>
     * ): string {
     *   if (typeof widgetType === "function") {
     *     for (const [name, type] of Object.entries(PrimeFaces.widget)) {
     *       if (type === widgetType) {
     *         return name;
     *       }
     *     }
     *   }
     *   else {
     *     const widgetClass = Object.getPrototypeOf(widgetType);
     *     for (const [name, type] of Object.entries(PrimeFaces.widget)) {
     *       if (
     *         "prototype" in type && widgetClass === type.prototype
     *         || widgetClass === type
     *       ) {
     *         return name;
     *       }
     *     }
     *   }
     *   return "BaseWidget";
     * }
     *
     * function getWidgetOfType<
     *   C extends Constructor<any> = Constructor<PrimeFaces.widget.BaseWidget>
     * >(widgetVar: string, widgetType: C): InstanceType<C> | undefined {
     *   const widget = PF(widgetVar);
     *   if (widget !== undefined && widget !== null) {
     *     if (widget instanceof widgetType) {
     *       // @ts-ignore
     *       return widget;
     *     }
     *     else {
     *       PrimeFaces.error([
     *         `Widget for var '${widgetVar}' of type '${getWidgetName(widget)}'`,
     *         `was found, but expected type '${getWidgetName(widgetType)}'!`
     *       ].join(" "));
     *       return undefined;
     *     }
     *   }
     *   else {
     *     return undefined;
     *   }
     * }
     * ```
     *
     * </details>
     *
     * This function could then be called like this:
     *
     * ```typescript
     * // Automatically inferred to be of type "PrimeFaces.widget.Chart | undefined"
     * const chart = getWidgetByVar("charWidgetVar", PrimeFaces.widget.Chart);
     * ```
     *
     * @namespace
     */
    PrimeFaces.widget = {};

    /**
     * __PrimeFaces Base Widget__
     *
     * BaseWidget for the PrimeFaces widgets framework.
     *
     * It provides some common functionality for other widgets. All widgets should inherit from this class, or an
     * appropriate sub class in the following manner:
     *
     * ```javascript
     * class MyWidget extends PrimeFaces.widget.BaseWidget {
     *
     *   init(cfg) {
     *     super.init(cfg);
     *     // custom initialization
     *   }
     *
     *   // more methods required by your widget
     *
     * }
     * ```
     *
     * Or, alternatively, if you need to support old browsers and do not wish to transpile your code:
     *
     * ```javascript
     * PrimeFaces.widget.MyWidget = PrimeFaces.widget.BaseWidget.extend({
     *   init: function(cfg) {
     *     this._super(cfg);
     *   }
     * });
     * ```
     *
     * If your widget needs to be visible before it can be rendered, consider using the {@link DeferredWidget} as a
     * base class instead.
     *
     * @typedef PrimeFaces.widget.RefreshListener A refresh listener for a PrimeFaces widget. It is invoked when the
     * widget is reloaded, such as during AJAX updates. Use {@link BaseWidget.addRefreshListener} to add a refresh
     * listener.
     * @template PrimeFaces.widget.RefreshListener.TWidget The type of the widget that is being refreshed.
     * @this {TWidget} PrimeFaces.widget.RefreshListener
     * @param {TWidget} PrimeFaces.widget.RefreshListener.widget The widget that is being refreshed.
     *
     * @typedef PrimeFaces.widget.DestroyListener A destroy listener for a PrimeFaces widget. It is invoked when the
     * widget is removed, such as during AJAX updates. Use {@link BaseWidget.addDestroyListener} to add a destroy
     * listener.
     * @template PrimeFaces.widget.DestroyListener.TWidget The type of the widget that is being destroyed.
     * @this {TWidget} PrimeFaces.widget.DestroyListener
     * @param {TWidget} PrimeFaces.widget.DestroyListener.widget The widget that is being destroyed.

     * @typedef PrimeFaces.widget.PostConstructCallback A callback for a PrimeFaces widget. An optional callback that is
     * invoked after a widget was created successfully, at the end of the {@link BaseWidget.init | init} method. This is
     * usually specified via the `widgetPostConstruct` attribute on the JSF component. Note that this is also called
     * during a `refresh` (AJAX update).
     * @this {BaseWidget} PrimeFaces.widget.PostConstructCallback
     * @param {BaseWidget} PrimeFaces.widget.PostConstructCallback.widget The widget that was constructed.
     * 
     * @typedef PrimeFaces.widget.PostRefreshCallback An optional callback that is invoked after a widget was refreshed
     * after an AJAX update, at the end of the {@link BaseWidget.refresh | refresh} method. This is usually specified
     * via the `widgetPostRefresh` attribute on the JSF component.
     * @this {BaseWidget} PrimeFaces.widget.PostRefreshCallback
     * @param {BaseWidget} PrimeFaces.widget.PostRefreshCallback.widget The widget that was refreshed.
     * 
     * @typedef PrimeFaces.widget.PreDestroyCallback An optional callback that is invoked before a widget is about to be
     * destroyed, e.g. when the component was removed at the end of an AJAX update. This is called at the beginning
     * of the {@link BaseWidget.destroy | destroy} method. This is usually specified via the `widgetPreDestroy`
     * attribute on the JSF component.
     * @this {BaseWidget} PrimeFaces.widget.PreDestroyCallback
     * @param {BaseWidget} PrimeFaces.widget.PreDestroyCallback.widget The widget that is about to be destroyed.
     * 
     * @template {PrimeFaces.widget.BaseWidgetCfg} [TCfg=PrimeFaces.widget.BaseWidgetCfg] Type of the configuration
     * object for this widget.
     *
     * @prop {PrimeFaces.PartialWidgetCfg<TCfg>} cfg The configuration of this widget instance. Please note that
     * no property is guaranteed to be present, you should always check for `undefined` before accessing a property.
     * This is partly because the value of a property is not transmitted from the server to the client when it equals
     * the default.
     * @prop {PrimeFaces.widget.DestroyListener<BaseWidget>[]} destroyListeners Array of registered listeners invoked
     * when this widget is destroyed. You should normally not use modify this directly, use {@link addDestroyListener}
     * instead.
     * @prop {string | string[]} id The client-side ID of this widget, with all parent naming containers, such as
     * `myForm:myWidget`. This is also the ID of the container HTML element for this widget. In case the widget needs
     * multiple container elements (such as {@link Paginator}), this may also be an array if IDs.
     * @prop {JQuery} jq The jQuery instance of the container element of this widget. In case {@link id} is an array, it
     * will contain multiple elements. Please note that some widgets have got not DOM elements at all, in this case this
     * will be an empty jQuery instance.
     * @prop {string} jqId A CSS selector for the container element (or elements, in case {@link id} is an array) of
     * this widget, This is usually an ID selector (that is properly escaped). You can select the container element or
     * elements like this: `$(widget.jqId)`.
     * @prop {PrimeFaces.widget.RefreshListener<BaseWidget>[]} refreshListeners Array of registered listeners invoked
     * when this widget is refreshed. You should normally not use modify this directly, use {@link addRefreshListener}
     * instead.
     * @prop {string} widgetVar The name of the widget variables of this widget. The widget variable can be used to
     * access a widget instance by calling `PF('myWidgetVar')`.
     * 
     * @method constructor Creates a new instance of this widget. Please note that you should __NOT__ override this
     * constructor. Instead, override the {@link init} method, which is called at the end of the constructor once the
     * instance is created.
     * @constructor constructor
     * @param {PrimeFaces.PartialWidgetCfg<TCfg>} constructor.cfg The widget configuration to be used for this widget
     * instance. This widget configuration is usually created on the server by the `javax.faces.render.Renderer` for
     * this component.
     *
     * @interface {PrimeFaces.widget.BaseWidgetCfg} cfg The configuration for the {@link  BaseWidget| BaseWidget widget}.
     * You can access this configuration via {@link PrimeFaces.widget.BaseWidget.cfg|BaseWidget.cfg}. Please note that this
     * configuration is usually meant to be read-only and should not be modified. This configuration is
     * always accessible via the `cfg` property of a widget and consists of key-value pairs. Please note that, in order
     * to save bandwidth, the server only sends a value for a given configuration key when the value differs from the
     * default value. That is, you must expect any configuration value to be absent and make sure you check for its
     * presence before accessing it.
     *
     * @prop {Record<string, PrimeFaces.Behavior>} cfg.behaviors A map with all behaviors that
     * were defined for this widget. The key is the name of the behavior, the value is the callback function that is
     * invoked when the behavior is called.
     * @prop {string | string[]} cfg.id The client-side ID of the widget, with all parent naming containers, such as
     * `myForm:myWidget`. This is also the ID of the container HTML element for this widget. In case the widget needs
     * multiple container elements (such as {@link Paginator}), this may also be an array if IDs.
     * @prop {PrimeFaces.widget.PostConstructCallback} cfg.postConstruct An optional callback that is invoked
     * after this widget was created successfully, at the end of the {@link BaseWidget.init | init} method. This is
     * usually specified via the `widgetPostConstruct` attribute on the JSF component. Note that this is also called
     * during a `refresh` (AJAX update).
     * @prop {PrimeFaces.widget.PostRefreshCallback} cfg.postRefresh An optional callback that is invoked after
     * this widget was refreshed after an AJAX update, at the end of the {@link BaseWidget.refresh | refresh} method.
     * This is usually specified via the `widgetPostRefresh` attribute on the JSF component.
     * @prop {PrimeFaces.widget.PreDestroyCallback} cfg.preDestroy An optional callback that is invoked before
     * this widget is about to be destroyed, e.g. when the component was removed at the end of an AJAX update. This is
     * called at the beginning of the {@link BaseWidget.destroy | destroy} method. This is usually specified via the
     * `widgetPreDestroy` attribute on the JSF component.
     * @prop {string} cfg.widgetVar The name of the widget variables of this widget. The widget variable can be used to
     * access a widget instance by calling `PF("myWidgetVar")`.
     */
    PrimeFaces.widget.BaseWidget = Class.extend({

        /**
         * A widget class should not declare an explicit constructor, the default constructor provided by this base
         * widget should be used. Instead, override this initialize method which is called after the widget instance
         * was constructed. You can use this method to perform any initialization that is required. For widgets that
         * need to create custom HTML on the client-side this is also the place where you should call your render
         * method.
         *
         * Please make sure to call the super method first before adding your own custom logic to the init method:
         *
         * ```javascript
         * PrimeFaces.widget.MyWidget = PrimeFaces.widget.BaseWidget.extend({
         *   init: function(cfg) {
         *     this._super(cfg);
         *     // custom initialization
         *   }
         * });
         * ```
         *
         * @param {PrimeFaces.PartialWidgetCfg<TCfg>} cfg The widget configuration to be used for this widget instance.
         * This widget configuration is usually created on the server by the `javax.faces.render.Renderer` for this
         * component.
         */
        init: function(cfg) {
            this.cfg = cfg;
            this.id = cfg.id;
            if (Array.isArray(this.id)) {
                this.jqId = $.map(this.id, function(id) {
                    return PrimeFaces.escapeClientId(id);
                }).join(",");
            }
            else {
                this.jqId = PrimeFaces.escapeClientId(this.id);
            }
            this.jq = $(this.jqId);
            this.widgetVar = cfg.widgetVar;
            this.destroyListeners = [];
            this.refreshListeners = [];

            //remove script tag
            this.removeScriptElement(this.id);

            if (this.widgetVar) {
                var $this = this;
                this.jq.on("remove", function() {
                    PrimeFaces.detachedWidgets.push($this.widgetVar);
                });
            }

            if (this.cfg.postConstruct) {
                this.cfg.postConstruct.call(this, this);
            }
        },

        /**
         * Used in ajax updates, reloads the widget configuration.
         *
         * When an AJAX call is made and this component is updated, the DOM element is replaced with the newly rendered
         * content. However, no new instance of the widget is created. Instead, after the DOM element was replaced, this
         * method is called with the new widget configuration from the server. This makes it possible to persist
         * client-side state during an update, such as the currently selected tab.
         *
         * Please note that instead of overriding this method, you should consider adding a refresh listener instead
         * via {@link addRefreshListener}. This has the advantage of letting you add multiple listeners, and makes it
         * possible to add additional listeners from code outside this widget.
         *
         * By default, this method calls all refresh listeners, then reinitializes the widget by calling the `init`
         * method.
         *
         * @param {PrimeFaces.PartialWidgetCfg<TCfg>} cfg The new widget configuration from the server.
         * @return {unknown} The value as returned by the `init` method, which is often `undefined`.
         */
        refresh: function(cfg) {
            this.destroyListeners = [];

            if (this.refreshListeners) {
                for (var i = 0; i < this.refreshListeners.length; i++) {
                    var refreshListener = this.refreshListeners[i];
                    refreshListener.call(this, this);
                }
            }
            this.refreshListeners = [];

            var returnValue = this.init(cfg);

            if (this.cfg.postRefresh) {
                this.cfg.postRefresh.call(this, this);
            }

            return returnValue;
        },

        /**
         * Will be called after an AJAX request if the widget container will be detached.
         *
         * When an AJAX call is made and this component is updated, the DOM element is replaced with the newly rendered
         * content. When the element is removed from the DOM by the update, the DOM element is detached from the DOM and
         * this method gets called.
         *
         * Please note that instead of overriding this method, you should consider adding a destroy listener instead
         * via {@link addDestroyListener}. This has the advantage of letting you add multiple listeners, and makes it
         * possible to add additional listeners from code outside this widget.
         *
         * By default, this method just calls all destroy listeners.
         */
        destroy: function() {
            if (this.cfg.preDestroy) {
                this.cfg.preDestroy.call(this, this);
            }

            PrimeFaces.debug("Destroyed detached widget: " + this.widgetVar);

            if (this.destroyListeners) {
                for (var i = 0; i < this.destroyListeners.length; i++) {
                    var destroyListener = this.destroyListeners[i];
                    destroyListener.call(this, this);
                }
            }
            this.destroyListeners = [];
        },

        /**
         * Checks if this widget is detached, ie whether the HTML element of this widget is currently contained within
         * the DOM (the HTML body element). A widget may become detached during an AJAX update, and it may remain
         * detached in case the update removed this component from the component tree.
         * @return {boolean} `true` if this widget is currently detached, or `false` otherwise.
         */
        isDetached: function() {
            var element = document.getElementById(this.id);
            if (typeof(element) !== 'undefined' && element !== null) {
                return false;
            }

            return true;
        },

        /**
         * Each widget has got a container element, this method returns that container. This container element is
         * usually also the element whose ID is the client-side ID of the JSF component.
         * @return {JQuery} The jQuery instance representing the main HTML container element of this widget.
         */
        getJQ: function(){
            return this.jq;
        },

        /**
         * Removes the widget's script block from the DOM. Currently, the ID of this script block consists of the
         * client-side ID of this widget with the prefix `_s`, but this is subject to change.
         *
         * @param {string | string[]} clientId The client-side ID of the widget.
         */
        removeScriptElement: function(clientId) {
            if (Array.isArray(clientId)) {
                $.each(clientId, function(_, id) {
                    $(PrimeFaces.escapeClientId(id) + '_s').remove();
                });
            }
            else {
                $(PrimeFaces.escapeClientId(clientId) + '_s').remove();
            }
        },

        /**
         * Each widget may have one or several behaviors attached to it. This method checks whether this widget has got
         * at least one behavior associated with given event name.
         *
         * A behavior is a way for associating client-side scripts with UI components that opens all sorts of
         * possibilities, including client-side validation, DOM and style manipulation, keyboard handling, and more.
         * When the behavior is triggered, the configured JavaScript gets executed.
         *
         * Behaviors are often, but not necessarily, AJAX behavior. When triggered, it initiates a request the server
         * and processes the response once it is received. This enables several features such as updating or replacing
         * elements dynamically. You can add an AJAX behavior via
         * `<p:ajax event="name" actionListener="#{...}" onstart="..." />`.
         *
         * @param {string} event The name of an event to check.
         * @return {boolean} `true` if this widget has the given behavior, `false` otherwise.
         */
        hasBehavior: function(event) {
            if(this.cfg.behaviors) {
                return this.cfg.behaviors[event] != undefined;
            }

            return false;
        },

        /**
         * Each widget may have one or several behaviors attached to it. This method calls all attached behaviors for
         * the given event name. In case no such behavior exists, this method does nothing and returns immediately.
         *
         * A behavior is a way for associating client-side scripts with UI components that opens all sorts of
         * possibilities, including client-side validation, DOM and style manipulation, keyboard handling, and more.
         * When the behavior is triggered, the configured JavaScript gets executed.
         *
         * Behaviors are often, but not necessarily, AJAX behavior. When triggered, it initiates a request the server
         * and processes the response once it is received. This enables several features such as updating or replacing
         * elements dynamically. You can add an AJAX behavior via
         * `<p:ajax event="name" actionListener="#{...}" onstart="..." />`.
         *
         * @param {string} event The name of an event to call.
         * @param {Partial<PrimeFaces.ajax.ConfigurationExtender>} [ext] Additional configuration that is passed to the
         * AJAX request for the server-side callback.
         * @since 7.0
         */
        callBehavior: function(event, ext) {
            if(this.hasBehavior(event)) {
                this.cfg.behaviors[event].call(this, ext);
            }
        },

        /**
         * Each widget may have one or several behaviors attached to it. This method returns the callback function for
         * the given event.
         *
         * __Note__: Do not call the method directly, the recommended way to invoke a behavior is via
         * {@link callBehavior}.
         *
         * A behavior is a way for associating client-side scripts with UI components that opens all sorts of
         * possibilities, including client-side validation, DOM and style manipulation, keyboard handling, and more.
         * When the behavior is triggered, the configured JavaScript gets executed.
         *
         * Behaviors are often, but not necessarily, AJAX behavior. When triggered, it initiates a request the server
         * and processes the response once it is received. This enables several features such as updating or replacing
         * elements dynamically. You can add an AJAX behavior via
         * `<p:ajax event="name" actionListener="#{...}" onstart="..." />`.
         *
         * @param {string} name The name of an event for which to retrieve the behavior.
         * @return {PrimeFaces.Behavior | null} The behavior with the given name, or `null` if no such behavior
         * exists.
         */
        getBehavior: function(name) {
            return this.cfg.behaviors ? this.cfg.behaviors[name] : null;
        },

        /**
         * Lets you register a listener that is called before the component is destroyed.
         *
         * When an AJAX call is made and this component is updated, the DOM element is replaced with the newly rendered
         * content. When the element is removed from the DOM by the update, the DOM element is detached from the DOM and
         * all destroy listeners are called. This makes it possible to add listeners from outside the widget code.
         *
         * If you call this method twice with the same listener, it will be registered twice and later also called
         * twice.
         *
         * Note that for this to work, you must not override the `destroy` method; or if you do, call `super`.
         *
         * Also, after this widget was detached is done, all destroy listeners will be unregistered.
         *
         * @param {PrimeFaces.widget.DestroyListener<this>} listener A destroy listener to be registered.
         * @since 7.0
         */
        addDestroyListener: function(listener) {
            if (!this.destroyListeners) {
                this.destroyListeners = [];
            }
            this.destroyListeners.push(listener);
        },

        /**
         * When an AJAX call is made and this component is updated, the DOM element is replaced with the newly rendered
         * content. However, no new instance of the widget is created. Instead, after the DOM element was replaced, all
         * refresh listeners are called. This makes it possible to add listeners from outside the widget code.
         *
         * If you call this method twice with the same listener, it will be registered twice and later also called
         * twice.
         *
         * Note that for this to work, you must not override the `refresh` method; or if you do, call `super`.
         *
         * Also, after the refresh is done, all refresh listeners will be deregistered. If you added the listeners from
         * within this widget, consider adding the refresh listeners not only in the `init` method, but also again in
         * the `refresh` method after calling `super`.
         *
         * @param {PrimeFaces.widget.RefreshListener<this>} listener A refresh listener to be registered.
         * @since 7.0.0
         */
        addRefreshListener: function(listener) {
            if (!this.refreshListeners) {
                this.refreshListeners = [];
            }
            this.refreshListeners.push(listener);
        }
    });

    /**
     * __PrimeFaces DynamicOverlay Widget__
     *
     * Base class for widgets that are displayed as an overlay. At any given time, several overlays may be active. This
     * requires that the z-index of the overlays is managed globally. This base class takes care of that.
     *
     * @prop {string | null} appendTo The search expression for the element to which the overlay panel should be appended.
     * @prop {boolean} blockScroll `true` to prevent the body from being scrolled, `false` otherwise.
     * @prop {JQuery} modalOverlay The DOM element that is displayed as an overlay with the appropriate `z-index` and
     * `position`. It is usually a child of the `body` element.
     *
     * @interface {PrimeFaces.widget.DynamicOverlayWidgetCfg} cfg The configuration for the {@link  DynamicOverlayWidget| DynamicOverlayWidget widget}.
     * You can access this configuration via {@link PrimeFaces.widget.BaseWidget.cfg|BaseWidget.cfg}. Please note that this
     * configuration is usually meant to be read-only and should not be modified.
     * @extends {PrimeFaces.widget.BaseWidgetCfg} cfg
     * 
     */
    PrimeFaces.widget.DynamicOverlayWidget = PrimeFaces.widget.BaseWidget.extend({

	    /**
	     * @override
    	 * @inheritdoc
         * @param {PrimeFaces.PartialWidgetCfg<TCfg>} cfg
    	 */
        init: function(cfg) {
            this._super(cfg);

            PrimeFaces.utils.registerDynamicOverlay(this, this.jq, this.id);
        },


        /**
         * @override
         * @inheritdoc
         * @param {PrimeFaces.PartialWidgetCfg<TCfg>} cfg
         */
        refresh: function(cfg) {
            PrimeFaces.utils.removeModal(this, this.modalOverlay);

            this.appendTo = null;
            this.modalOverlay = null;

            this._super(cfg);
        },

        /**
         * @override
         * @inheritdoc
         */
        destroy: function() {
            this._super();

            PrimeFaces.utils.removeModal(this);

            this.appendTo = null;
            this.modalOverlay = null;
        },

        /**
         * Enables modality for this widget and creates the modal overlay element, but does not change whether the
         * overlay is currently displayed.
         * @param {JQuery | null} [overlay] The target overlay, if not given default to
         * {@link PrimeFaces.widget.BaseWidget.jq | this.jq}.
         */
        enableModality: function(overlay) {
            var target = overlay||this.jq;
            this.modalOverlay = PrimeFaces.utils.addModal(this,
                target,
                $.proxy(function() {
                    return this.getModalTabbables();
                }, this));
        },

        /**
         * Disabled modality for this widget and removes the modal overlay element, but does not change whether the
         * overlay is currently displayed.
         * @param {JQuery | null} [overlay] The target overlay, if not given default to
         * {@link PrimeFaces.widget.BaseWidget.jq | this.jq}.
         */
        disableModality: function(overlay){
            var target = overlay||this.jq;
            PrimeFaces.utils.removeModal(this, target);
            this.modalOverlay = null;
        },

        /**
         * This class makes sure a user cannot tab out of the modal and it stops events from targets outside of the
         * overlay element. This requires that we switch back to the modal in case a user tabs out of it. What must
         * be returned by this method are the elements to which the user may switch via tabbing.
         * @protected
         * @return {JQuery} The DOM elements which are allowed to be focused via tabbing. May be an empty `jQuery`
         * instance when the modal contains no tabbable elements, but must not be `undefined`.
         */
        getModalTabbables: function(){
            return null;
        }
    });

    /**
     * __PrimeFaces Deferred Widget__
     *
     * Base class for widgets that require their container to be visible to initialize properly.
     *
     * For example, a widget may need to know the width and height of its container so that it can resize itself
     * properly.
     *
     * Do not call the {@link render} or {@link _render} method directly in the {@link init} method. Instead, call
     * {@link renderDeferred}. PrimeFaces will then check whether the widget is visible and call the {@link _render}
     * method once it is. Make sure you actually override the {@link _render} method, as the default implementation
     * throws an error.
     *
     * ```javascript
     * class MyWidget extends PrimeFaces.widget.DeferredWidget {
     *   init(cfg) {
     *     super.init(cfg);
     *
     *     // more code if needed
     *     // ...
     *
     *     // Render this widget once its container is visible.
     *     this.renderDeferred();
     *   }
     *
     *   _render() {
     *     // Perform your render logic here, create DOM elements etc.
     *   }
     * }
     * ```
     *
     * @interface {PrimeFaces.widget.DeferredWidgetCfg} cfg The configuration for the {@link  DeferredWidget| DeferredWidget widget}.
     * You can access this configuration via {@link PrimeFaces.widget.BaseWidget.cfg|BaseWidget.cfg}. Please note that
     * this configuration is usually meant to be read-only and should not be modified.
     * @extends {PrimeFaces.widget.BaseWidgetCfg} cfg
     */
    PrimeFaces.widget.DeferredWidget = PrimeFaces.widget.BaseWidget.extend({

        /**
         * Call this method in the {@link init} method if you want deferred rendering support. This method checks
         * whether the container of this widget is visible and call {@link _render} only once it is.
         */
        renderDeferred: function() {
            if(this.jq.is(':visible')) {
                this._render();
                this.postRender();
            }
            else {
                var container = this.jq[0].closest('.ui-hidden-container');
                if (container) {
                    var $container = $(container);
                    if($container.length) {
                        var $this = this;
                        this.addDeferredRender(this.id, $container, function() {
                            return $this.render();
                        });
                    }
                }
            }
        },

        /**
         * This render method to check whether the widget container is visible. Do not override this method, or the
         * deferred widget functionality may not work properly anymore.
         *
         * @return {PrimeFaces.ReturnOrVoid<boolean|undefined>} `true` if the widget container is visible, `false` or
         * `undefined` otherwise.
         */
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
         * This render method is called by this deferred widget once the widget container has become visible. You may
         * now proceed with widget initialization.
         *
         * __Must be overridden__, or an error will be thrown.
         *
         * @include
         * @protected
         */
        _render: function() {
            throw 'Unsupported Operation';
        },

        /**
         * Called after the widget has become visible and after it was rendered. May be overridden, the default
         * implementation is a no-op.
         * @protected
         */
        postRender: function() {

        },

        /**
         * Cleans up deferred render tasks. When you extend this class and override this method, make sure to call
         * `super`.
         * @override
         */
        destroy: function() {
            this._super();
            PrimeFaces.removeDeferredRenders(this.id);
        },

        /**
         * Adds a deferred rendering task for the given widget to the queue.
         * @protected
         * @param {string} widgetId The ID of a deferred widget.
         * @param {JQuery} container The container element that should be visible.
         * @param {() => boolean} callback Callback that is invoked when the widget _may_ possibly have become visible.
         * Should return `true` when the widget was rendered, or `false` when the widget still needs to be rendered
         * later.
         */
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