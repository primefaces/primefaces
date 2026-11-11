import { core } from "./core.js";
import { utils } from "./core.utils.js";

/**
 * The configuration for the {@link BaseComponent | BaseComponent component}.
 * You can access this configuration via {@link PrimeFaces.component.BaseComponent.cfg | BaseComponent.cfg}. Please note that this
 * configuration is usually meant to be read-only and should not be modified. This configuration is
 * always accessible via the `cfg` property of a component and consists of key-value pairs. Please note that, in order
 * to save bandwidth, the server only sends a value for a given configuration key when the value differs from the
 * default value. That is, you must expect any configuration value to be absent and make sure you check for its
 * presence before accessing it.
 */
export interface BaseComponentCfg {
    /**
     * A map with all behaviors that
     * were defined for this component. The key is the name of the behavior, the value is the callback function that is
     * invoked when the behavior is called.
     */
    behaviors: Record<string, PrimeFacesTypes.component.Behavior>;

    /**
     * `true` to block scrolling when a certain condition is satisfied, or `false` otherwise.
     */
    blockScroll: boolean;

    /**
     * Whether the component was disabled on the server-side, usually via the `disabled` attribute on the Faces component.
     */
    disabled: boolean;

    /**
     * Whether the component should be disabled during AJAX postback requests.
     * E.g. a button could get disabled so that it cannot be pressed
     * again until the request finishes. (Since requests need to be
     * queued as mandated by the Faces spec, the user would have to wait
     * anyway.)
     */
    disableOnAjax: boolean;

    /**
     * False to re-enable after disabled by an AJAX event.
     */
    disabledAttr: boolean;

    /**
     * ID of the form to use for AJAX requests.
     */
    formId: string;

    /**
     * The client-side ID of the component, with all parent naming containers, such as
     * `myForm:myWidget`. This is also the ID of the container HTML element for this component. In case the component needs
     * multiple container elements (such as {@link Paginator}), this may also be an array if IDs.
     */
    id: string | string[];

    /**
     * Optional reference to an iframe, e.g. the iframe inside which to
     * place the component.
     */
    iframe: JQuery<HTMLIFrameElement>;

    /**
     * List of localized labels for the component. Labels are normally rendered server-side. This property is set by the
     * server when e.g. the client needs to change the label dynamically. The key is the ID of the label, the value the
     * localized label text.
     */
    labels: {
        aria: Record<string, string>;
        [key: string]: string | Record<string, string>;
    };

    /**
     * An optional callback that is invoked
     * before the component is created, at the start of the {@link BaseComponent.init | init} method. This is
     * usually specified via the `componentPreConstruct` attribute on the Faces component. Note that this is also called
     * during a `refresh` (AJAX update).
     */
    preConstruct: PrimeFacesTypes.component.PreConstructCallback;

    /**
     * An optional callback that is invoked
     * after this component was created successfully, at the end of the {@link BaseComponent.init | init} method. This is
     * usually specified via the `componentPostConstruct` attribute on the Faces component. Note that this is also called
     * during a `refresh` (AJAX update).
     */
    postConstruct: PrimeFacesTypes.component.PostConstructCallback;

    /**
     * An optional callback that is invoked after
     * this component was refreshed after an AJAX update, at the end of the {@link BaseComponent.refresh | refresh} method.
     * This is usually specified via the `componentPostRefresh` attribute on the Faces component.
     */
    postRefresh: PrimeFacesTypes.component.PostRefreshCallback;

    /**
     * An optional callback that is invoked before
     * this component is about to be destroyed, e.g., when the component was removed at the end of an AJAX update. This is
     * called at the beginning of the {@link BaseComponent.destroy | destroy} method. This is usually specified via the
     * `componentPreDestroy` attribute on the Faces component.
     */
    preDestroy: PrimeFacesTypes.component.PreDestroyCallback;

    /**
     * Whether the component supports touch devices (usually via pointer events etc.).
     */
    touchable: boolean;

    /**
     * The name of the component variables of this component. The component variable can be used to
     * access a component instance by calling `PF("myWidgetVar")`.
     */
    widgetVar: string;
}

/**
 * The configuration for the {@link DynamicOverlayComponent}.
 * 
 * You can access this configuration via {@link BaseComponent.cfg}. Please note
 * that this configuration is usually meant to be read-only and should not be
 * modified.
 */
export interface DynamicOverlayComponentCfg extends BaseComponentCfg, PrimeFacesTypes.component.DynamicOverlayFeatureComponentCfg {
}

/**
 * The configuration for the {@link DeferredComponent}.
 * 
 * You can access this configuration via {@link BaseComponent.cfg}. Please note
 * that this configuration is usually meant to be read-only and should not be
 * modified.
 */
export interface DeferredComponentCfg extends BaseComponentCfg {
}

/**
 * __PrimeFaces Base Component__
 *
 * BaseComponent for the PrimeFaces components framework.
 *
 * It provides some common functionality for other components. All components should inherit from this class, or an
 * appropriate subclass in the following manner:
 *
 * ```js
 * class MyWidget extends PrimeFaces.component.BaseComponent {
 *
 *   init(cfg) {
 *     super.init(cfg);
 *     // custom initialization
 *   }
 *
 *   // more methods required by your component
 * }
 * ```
 *
 * If your component needs to be visible before it can be rendered, consider using the {@link DeferredComponent} as a
 * base class instead.
 *
 * @typeParam Cfg Type of the configuration object for this component.
 *
 * @prop {string} key The key of the JSON object.
 */
export class BaseComponent<Cfg extends BaseComponentCfg = BaseComponentCfg> {
    /**
     * Number of concurrent active Ajax requests.
     */
    ajaxCount: number = 0;

    /**
     * Keeps track of when the AJAX request started.
     */
    ajaxStart: number | null = null;

    /**
     * The configuration of this component instance. Please note that
     * no property is guaranteed to be present, you should always check for `undefined` before accessing a property.
     * This is partly because the value of a property is not transmitted from the server to the client when it equals
     * the default.
     */
    cfg: PrimeFacesTypes.component.PartialComponentCfg<Cfg> = { id: "", widgetVar: "" } as PrimeFacesTypes.component.PartialComponentCfg<Cfg>;

    /**
     * Array of registered listeners invoked when this component is destroyed. You should normally not use modify this
     * directly, use {@link addDestroyListener} instead.
     */
    // Need to use any as the type parameter as TypeScript does not have existential types / wildcards (DestroyListener<?>)
    destroyListeners: PrimeFacesTypes.component.DestroyListener<any>[] = [];

    /**
     * The client-side ID of this component, with all parent naming containers, such as
     * `myForm:myWidget`. This is also the ID of the container HTML element for this component. In case the component needs
     * multiple container elements (such as `Paginator`), this may also be an array if IDs.
     */
    id: string | string[] = "";

    /**
     * The jQuery instance of the container element of this component. In case {@link id} is an array, it
     * will contain multiple elements. Please note that some components have got not DOM elements at all, in this case this
     * will be an empty jQuery instance.
     */
    jq: JQuery = $();

    /**
     * For a component without own markup (e.g. `p:draggable` or `p:keyFilter`), the element it works on, see
     * {@link bindLifecycleTo}. An element, not a jQuery instance, so {@link destroy} does not unbind its events.
     */
    private lifecycleElement: Element | null = null;

    /**
     * A CSS selector for the container element (or elements, in case {@link id} is an array) of
     * this component, This is usually an ID selector (that is properly escaped). You can select the container element or
     * elements like this: `$(component.jqId)`.
     */
    jqId: string = "";

    /**
     * refreshListeners Array of registered listeners invoked when this component is refreshed. You should normally not use
     * modify this directly, use {@link addRefreshListener} instead.
     */
    // Need to use any as the type parameter as TypeScript does not have existential types / wildcards (RefreshListener<?>)
    refreshListeners: PrimeFacesTypes.component.RefreshListener<any>[] = [];

    /**
     * The name of the component variables of this component. The component variable can be used to
     * access a component instance by calling `PF('myWidgetVar')`.
     */
    widgetVar: string = "";

    /**
     * Creates a new instance of this component. Please note that you should __NOT__ override this constructor.
     * Instead, override the {@link init} method, which is called by the framework once the component instance was
     * created.
     * 
     * In addition, the `init` method is also called when the page is refreshed via AJAX. In that case, the
     * component instance is reused, and only its `init` method gets called again.
     */
    constructor() { }

    /**
     * A component class should not declare an explicit constructor, the default constructor provided by this base
     * component should be used. Instead, override this initialize method which is called after the component instance
     * was constructed. You can use this method to perform any initialization that is required. For components that
     * need to create custom HTML on the client-side this is also the place where you should call your render
     * method.
     *
     * Please make sure to call the super method first before adding your own custom logic to the init method:
     *
     * ```javascript
     * class MyWidget extends PrimeFaces.component.BaseComponent {
     *   init: function(cfg) {
     *     super.init(cfg);
     *     // custom initialization
     *   }
     * }
     * ```
     *
     * @param cfg The component configuration to be used for this component instance. This component configuration is usually
     * created on the server by the `jakarta.faces.render.Renderer` for this component.
     */
    init(cfg: PrimeFacesTypes.component.PartialComponentCfg<Cfg>): void {
        this.cfg = cfg;
        this.id = cfg.id;
        if (Array.isArray(this.id)) {
            this.jqId = $.map(this.id, function (id) {
                return core.escapeClientId(id);
            }).join(",");
        }
        else {
            this.jqId = core.escapeClientId(this.id);
        }
        this.jq = $(this.jqId);
        this.widgetVar = cfg.widgetVar;
        this.destroyListeners = [];
        this.refreshListeners = [];

        this.lifecycleElement = null;

        //remove script tag
        this.removeScriptElement(this.id);

        if (this.widgetVar) {
            this.jq.on("remove", () => this.markDetached());
        }
    }

    /**
     * Ties a component without own markup (e.g. `p:draggable` or `p:keyFilter`) to the element it works on: the component is
     * detached when that element is removed, and destroyed after the AJAX update unless the update rendered it again
     * (then it is bound to the new element). Call it in {@link init}. A component removed while the element stays can't
     * be detected, it has no element of its own.
     * @param element The element the component works on, e.g. the target of `p:draggable`.
     */
    protected bindLifecycleTo(element: JQuery<any> | Element | null | undefined): void {
        const el = element instanceof Element ? element : element?.get(0);
        if (!el || !this.widgetVar) {
            return;
        }
        this.lifecycleElement = el;
        // the element may stay when only the component is refreshed
        $(el).off(this.lifecycleNamespace()).on(this.lifecycleNamespace(), () => this.markDetached());
    }

    /**
     * Marks this component as detached, it is destroyed after the AJAX update unless it was rendered again.
     */
    private markDetached(): void {
        if (!core.detachedComponents.includes(this.widgetVar)) {
            core.detachedComponents.push(this.widgetVar);
        }
    }

    /**
     * The event namespace of the `remove` listener on the {@link lifecycleElement}.
     * @returns The event type with the namespace of this component.
     */
    private lifecycleNamespace(): string {
        return "remove.pfwidget_" + String(this.widgetVar).replace(/\./g, "_");
    }

    /**
     * Used in ajax updates, reloads the component configuration.
     *
     * When an AJAX call is made and this component is updated, the DOM element is replaced with the newly rendered
     * content. However, no new instance of the component is created. Instead, after the DOM element was replaced, this
     * method is called with the new component configuration from the server. This makes it possible to persist
     * client-side state during an update, such as the currently selected tab.
     *
     * Please note that instead of overriding this method, you should consider adding a refresh listener instead
     * via {@link addRefreshListener}. This has the advantage of letting you add multiple listeners, and makes it
     * possible to add additional listeners from code outside this component.
     *
     * By default, this method calls all refresh listeners, then reinitializes the component by calling the `init`
     * method.
     *
     * @param cfg The new component configuration from the server.
     * @return {unknown} The value as returned by the `init` method, which is often `undefined`.
     */
    refresh(cfg: PrimeFacesTypes.component.PartialComponentCfg<Cfg>): unknown {
        this.destroyListeners = [];

        if (this.refreshListeners) {
            for (const refreshListener of this.refreshListeners) {
                refreshListener.call(this, this);
            }
        }
        this.refreshListeners = [];

        var returnValue = this.init(cfg);
        return returnValue;
    }

    /**
     * Will be called after an AJAX request if the component container will be detached.
     *
     * When an AJAX call is made and this component is updated, the DOM element is replaced with the newly rendered
     * content. When the element is removed from the DOM by the update, the DOM element is detached from the DOM and
     * this method gets called.
     *
     * Please note that instead of overriding this method, you should consider adding a destroy listener instead
     * via {@link addDestroyListener}. This has the advantage of letting you add multiple listeners, and makes it
     * possible to add additional listeners from code outside this component.
     *
     * By default, this method just calls all destroy listeners.
     */
    destroy(): void {
        if (this.cfg.preDestroy) {
            this.cfg.preDestroy.call(this, this, this);
        }

        core.debug("Destroyed detached widget: " + this.widgetVar);

        if (this.destroyListeners) {
            for (const destroyListener of this.destroyListeners) {
                destroyListener.call(this, this);
            }
        }
        this.destroyListeners = [];

        if (this.lifecycleElement) {
            // a component without own markup only references elements of others (e.g. the scope of p:defaultCommand),
            // which may stay: it removes its own listeners in its destroy listeners, unbinding all events would break
            // the other components
            $(this.lifecycleElement).off(this.lifecycleNamespace());
            return;
        }

        // Iterate through all stored variables within this component. If any of them are jQuery objects, 
        // it is imperative to unbind their event listeners to avoid memory leaks in the DOM.
        for (var key in this) {
            var jq = this[key];
            // never strip the global targets which are shared with all other components, e.g. $(window) or $(document)
            var element = jq instanceof jQuery ? (jq as unknown as JQuery<any>).get(0) : undefined;
            if (jq instanceof jQuery && element !== window && element !== document
                    && element !== document.documentElement && element !== document.body) {
                // remove events on all descendants
                (jq as unknown as JQuery).children().off();
                // remove events from element
                (jq as unknown as JQuery).off();
                // NOTE: do not null out the value here as is it needed sometimes still after destroy happens
                // this[key] = null; DO NOT ENABLE THIS
            }
        }
    }

    /**
     * Disables this component, so that the user cannot interact with it anymore.
     */
    disable?(): void;

    /**
     * Enables this component, so that the user can interact with it.
     */
    enable?(): void;

    /**
     * Checks if this component is detached, ie whether the HTML element of this component is currently contained within
     * the DOM (the HTML body element). A component may become detached during an AJAX update, and it may remain
     * detached in case the update removed this component from the component tree. A component without own markup (e.g.
     * `p:draggable`) is detached when the element it works on was removed, see {@link bindLifecycleTo}.
     * @return `true` if this component is currently detached, or `false` otherwise.
     */
    isDetached(): boolean {
        if (this.lifecycleElement) {
            // refreshed by the update, the component is bound to the new element already
            return !this.lifecycleElement.isConnected;
        }
        const id = Array.isArray(this.id) ? this.id[0] : this.id;
        return id === undefined || document.getElementById(id) === null;
    }

    /**
     * Each component has got a container element, this method returns that container. This container element is
     * usually also the element whose ID is the client-side ID of the Faces component.
     * @return The jQuery instance representing the main HTML container element of this component.
     */
    getJQ(): JQuery {
        return this.jq;
    }

    /**
     * Gets the ID of this component's first container element. This is identical
     * to {@link id} in case the component only has one container element.  
     * @returns The main ID of this component.
     */
    getId(): string {
        return typeof this.id === "string" ? this.id : (this.id[0] ?? "");
    }

    /**
     * Gets the IDs of each of this component's container elements, as an array.
     * This is identical to {@link id} in case the component has multiple container
     * elements.
     * @returns The IDs of this component's container elements.
     */
    getIds(): string[] {
        return typeof this.id === "string" ? [this.id] : this.id;
    }

    /**
     * Removes the component's script block from the DOM. Currently, the ID of this script block consists of the
     * client-side ID of this component with the prefix `_s`, but this is subject to change.
     *
     * @param clientId The client-side ID of the component.
     */
    removeScriptElement(clientId: string | string[]): void {
        if (Array.isArray(clientId)) {
            $.each(clientId, function (_, id) {
                $(core.escapeClientId(id) + '_s').remove();
            });
        }
        else {
            $(core.escapeClientId(clientId) + '_s').remove();
        }
    }

    /**
     * Each component may have one or several behaviors attached to it. This method checks whether this component has got
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
     * @param event The name of an event to check.
     * @return `true` if this component has the given behavior, `false` otherwise.
     */
    hasBehavior(event: string): boolean {
        if (this.cfg.behaviors) {
            return this.cfg.behaviors[event] != undefined;
        }

        return false;
    }

    /**
     * Each component may have one or several behaviors attached to it. This method calls all attached behaviors for
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
     * @param event The name of an event to call.
     * @param ext Additional configuration that is passed to the AJAX request for the server-side callback.
     * @param fallbackToDomEvent Whether to fallback to the DOM event if no behavior is defined.
     * @since 7.0
     */
    callBehavior(event: string, ext?: Partial<PrimeFacesTypes.ajax.ConfigurationExtender>, fallbackToDomEvent = true): void {
        if (this.hasBehavior(event)) {
            this.cfg.behaviors?.[event]?.call(this, ext);
        }
        else if (fallbackToDomEvent && this.cfg.behaviors === undefined && this.jq.length > 0) {
            // #12887 if no behavior is defined, try to call the DOM event
            this.jq.trigger(event, ext);
        }
    }

    /**
     * Each component may have one or several behaviors attached to it. This method returns the callback function for
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
     * @param name The name of an event for which to retrieve the behavior.
     * @return The behavior with the given name, `undefined` if no such behavior exists, and `null` if this
     * component does not have a behaviors map configured.
     */
    getBehavior(name: string): PrimeFacesTypes.component.Behavior | null | undefined {
        return this.cfg.behaviors ? this.cfg.behaviors[name] : null;
    }

    /**
     * Lets you register a listener that is called before the component is destroyed.
     *
     * When an AJAX call is made and this component is updated, the DOM element is replaced with the newly rendered
     * content. When the element is removed from the DOM by the update, the DOM element is detached from the DOM and
     * all destroy listeners are called. This makes it possible to add listeners from outside the component code.
     *
     * If you call this method twice with the same listener, it will be registered twice and later also called
     * twice.
     *
     * Note that for this to work, you must not override the `destroy` method; or if you do, call `super`.
     *
     * Also, after this component was detached is done, all destroy listeners will be unregistered.
     *
     * @param listener A destroy listener to be registered.
     * @since 7.0
     */
    addDestroyListener(listener: PrimeFacesTypes.component.DestroyListener<this>): void {
        if (!this.destroyListeners) {
            this.destroyListeners = [];
        }
        this.destroyListeners.push(listener);
    }

    /**
     * When an AJAX call is made and this component is updated, the DOM element is replaced with the newly rendered
     * content. However, no new instance of the component is created. Instead, after the DOM element was replaced, all
     * refresh listeners are called. This makes it possible to add listeners from outside the component code.
     *
     * If you call this method twice with the same listener, it will be registered twice and later also called
     * twice.
     *
     * Note that for this to work, you must not override the `refresh` method; or if you do, call `super`.
     *
     * Also, after the refresh is done, all refresh listeners will be deregistered. If you added the listeners from
     * within this component, consider adding the refresh listeners not only in the `init` method, but also again in
     * the `refresh` method after calling `super`.
     *
     * @param listener A refresh listener to be registered.
     * @since 7.0.0
     */
    addRefreshListener(listener: PrimeFacesTypes.component.RefreshListener<this>): void {
        if (!this.refreshListeners) {
            this.refreshListeners = [];
        }
        this.refreshListeners.push(listener);
    }

    /**
     * Gets the closest parent form for this component.
     *
     * @return A JQuery instance that either contains the form when found, or an empty JQuery instance when
     * the form could not be found.
     * @since 10.0.0
     */
    getParentForm(): JQuery {
        return this.jq.closest('form');
    }

    /**
     * Gets the closest parent form ID for this component lazily so it can be used in AJAX requests.
     *
     * @return Either the form ID or `undefined` if no form can be found.
     * @since 10.0.0
     */
    getParentFormId(): string | undefined {
        if (this.cfg.formId) {
            return this.cfg.formId;
        }

        //look for a parent of source
        var form = this.getParentForm();
        if (form.length > 0) {
            this.cfg.formId = form.attr('id');
        }

        return this.cfg.formId;
    }

    /**
     * Gets a localized label text for this component.
     * @param label The label key to look up
     * @returns The label text - either from the component configuration if specified,
     * or from the PrimeFaces global labels
     */
    getLabel<K extends keyof PrimeFacesTypes.Locale>(label: K): PrimeFacesTypes.Locale[K] {
        if (this.cfg.labels && typeof this.cfg.labels[label] === "string") {
            return this.cfg.labels[label];
        }
        return core.getLocaleLabel(label);
    }

    /**
     * Creates an ARIA label for an element.
     * @param label The label key to look up
     * @returns The ARIA label text - either from the component configuration if specified, 
     * or from the PrimeFaces global ARIA labels
     */
    getAriaLabel(label: keyof PrimeFacesTypes.LocaleAria): string {
        if (this.cfg.labels && this.cfg.labels.aria && this.cfg.labels.aria[label]) {
            return this.cfg.labels.aria[label];
        }
        return core.getAriaLabel(label);
    }

    // TODO: Why are the targetComponent and jqTargetId parameters necessary?
    // We are calling the target component instance, which has this data already
    // (and often won't even need it).
    /**
     * Optional feature that can be implemented by components to add additional support
     * for context menus. When implemented, this is called by the context menu
     * component when it attaches itself to a component.
     * @param contextMenu The context menu component that wishes to attach itself to this component.
     * @param targetComponent This component instance.
     * @param jqTargetId The ID selector of the main element of this component.
     * @param cfg The configuration of the context menu component.
     */
    bindContextMenu?(
        contextMenu: PrimeFacesTypes.component.ContextMenuLikeComponent,
        targetComponent: this,
        jqTargetId: string,
        cfg: PrimeFacesTypes.component.PartialComponentCfg<PrimeFacesTypes.component.ContextMenuLikeComponentCfg>,
    ): void;
}

/**
 * __PrimeFaces DynamicOverlay Component__
 *
 * Base class for components that are displayed as an overlay. At any given time, several overlays may be active. This
 * requires that the z-index of the overlays is managed globally. This base class takes care of that.
 *
 * @typeParam Cfg Type of the configuration object for this component.
 */
export class DynamicOverlayComponent<Cfg extends DynamicOverlayComponentCfg = DynamicOverlayComponentCfg> extends BaseComponent<Cfg> {
    /**
     * The search expression for the element to which the overlay panel should be appended.
     */
    appendTo: string | null = null;

    /**
     * The DOM element that is displayed as an overlay with the appropriate `z-index` and `position`. It is usually a
     * child of the `body` element.
     */
    modalOverlay: JQuery | null = null;

    /**
     * @param The DOM element for the overlay.
     * @param The ID of the overlay, usually the component ID.
     * @param The DOM element that is the target of this overlay
     */
    override init(cfg: PrimeFacesTypes.component.PartialComponentCfg<Cfg>, overlay?: JQuery, overlayId?: string, target?: JQuery): void {
        super.init(cfg);

        // do not bind overlay if component disabled
        if (this.cfg.disabled === true) {
            return;
        }

        if (!overlay) {
            overlay = this.jq;
        }

        if (!overlayId) {
            overlayId = this.getId();
        }

        if (!target) {
            target = this.jq;
        }

        const ignoreAppendTo = core.isComponentOfTypeName(this, "Dialog");
        if (!ignoreAppendTo) {
            this.cfg.appendTo = utils.resolveAppendTo(this, target, overlay);
        }

        utils.registerDynamicOverlay(this, overlay, overlayId);
    }

    override refresh(cfg: PrimeFacesTypes.component.PartialComponentCfg<Cfg>): void {
        utils.removeModal(this, this.modalOverlay);

        this.appendTo = null;
        this.modalOverlay = null;

        super.refresh(cfg);
    }

    override destroy(): void {
        super.destroy();

        utils.removeModal(this);

        this.appendTo = null;
        this.modalOverlay = null;
    }

    /**
     * Enables modality for this component and creates the modal overlay element, but does not change whether the
     * overlay is currently displayed.
     * @param overlay The target overlay, if not given defaults to {@link jq}.
     */
    enableModality(overlay?: JQuery | null): void {
        const target = overlay || this.jq;
        this.modalOverlay = utils.addModal(this, target, () => this.getModalTabbables() ?? $());
    }

    /**
     * Disabled modality for this component and removes the modal overlay element, but does not change whether the
     * overlay is currently displayed.
     * @param overlay The target overlay, if not given defaults to {@link jq}.
     */
    disableModality(overlay?: JQuery | null | undefined): void {
        var target = overlay || this.jq;
        utils.removeModal(this, target);
        this.modalOverlay = null;
    }

    /**
     * This class makes sure a user cannot tab out of the modal and it stops events from targets outside of the
     * overlay element. This requires that we switch back to the modal in case a user tabs out of it. What must
     * be returned by this method are the elements to which the user may switch via tabbing.
     * @return The DOM elements which are allowed to be focused via tabbing. May be an empty `jQuery`
     * instance or `null`, when the modal contains no tabbable elements.
     */
    protected getModalTabbables(): JQuery | null {
        return null;
    }
}

/**
 * __PrimeFaces Deferred Component__
 *
 * Base class for components that require their container to be visible to initialize properly.
 *
 * For example, a component may need to know the width and height of its container so that it can resize itself
 * properly.
 *
 * Do not call the {@link render} or {@link _render} method directly in the {@link init} method. Instead, call
 * {@link renderDeferred}. PrimeFaces will then check whether the component is visible and call the {@link _render}
 * method once it is. Make sure you actually override the {@link _render} method, as the default implementation
 * throws an error.
 *
 * ```javascript
 * class MyWidget extends PrimeFaces.component.DeferredComponent {
 *   init(cfg) {
 *     super.init(cfg);
 *
 *     // more code if needed
 *     // ...
 *
 *     // Render this component once its container is visible.
 *     this.renderDeferred();
 *   }
 *
 *   _render() {
 *     // Perform your render logic here, create DOM elements etc.
 *   }
 * }
 * ```
 */
export class DeferredComponent<Cfg extends DeferredComponentCfg = DeferredComponentCfg> extends BaseComponent<Cfg> {

    /**
     * Call this method in the {@link init} method if you want deferred rendering support. This method checks
     * whether the container of this component is visible and call {@link _render} only once it is.
     */
    renderDeferred(): void {
        if (this.jq.is(':visible')) {
            this._render();
            this.postRender();
        }
        else if (this.jq[0]) {
            var container = this.jq[0].closest('.p-hidden-container');
            if (container instanceof HTMLElement) {
                var $container = $(container);
                if ($container.length) {
                    var $this = this;
                    this.addDeferredRender(this.id, $container, () => {
                        return $this.render();
                    });
                }
            }
        }
    }

    /**
     * This render method to check whether the component container is visible. Do not override this method, or the
     * deferred component functionality may not work properly anymore.
     *
     * @return `true` if the component container is visible, `false` or `undefined` otherwise.
     */
    render(): boolean | undefined | void {
        if (this.jq.is(':visible')) {
            this._render();
            this.postRender();
            return true;
        }
        else {
            return false;
        }
    }

    /**
     * This render method is called by this deferred component once the component container has become visible. You may
     * now proceed with component initialization.
     *
     * __Must be overridden__, or an error will be thrown.
     */
    protected _render(): void {
        throw new Error("Unsupported Operation");
    }

    /**
     * Called after the component has become visible and after it was rendered. May be overridden, the default
     * implementation is a no-op.
     */
    protected postRender(): void {
    }

    /**
     * Cleans up deferred render tasks. When you extend this class and override this method, make sure to call
     * `super`.
     */
    override destroy(): void {
        super.destroy();
        core.removeDeferredRenders(this.id);
    }

    /**
     * Adds a deferred rendering task for the given component to the queue.
     * @param componentId The ID of a deferred component.
     * @param container The container element that should be visible.
     * @param callback Callback that is invoked when the component _may_ possibly have become visible.
     * Should return `true` when the component was rendered, or `false` when the component still needs to be rendered
     * later.
     */
    protected addDeferredRender(componentId: string | string[], container: JQuery, callback: () => boolean | undefined | void): void {
        core.addDeferredRender(componentId, container.attr('id') ?? "", callback);

        if (container.is(':hidden')) {
            var parentContainer = this.jq.closest('.p-hidden-container');

            if (parentContainer.length) {
                this.addDeferredRender(componentId, container.parent().closest('.p-hidden-container'), callback);
            }
        }
    }
}
