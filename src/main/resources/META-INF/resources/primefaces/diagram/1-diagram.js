/**
 * __PrimeFaces Diagram Widget__
 * 
 * Diagram is generic component to create visual elements and connect them on a web page. SVG is used on modern browsers
 * and VML on IE 8 and below. Component is highly flexible in terms of api, events and theming.
 * 
 * @interface {PrimeFaces.widget.Diagram.ConnectionInfo} ConnectionInfo Details about a connection between two nodes that
 * was either established or dissolved.
 * @prop {string} ConnectionInfo.sourceId ID of the source node where the connection starts.
 * @prop {string} ConnectionInfo.targetId ID of the target node where the connection end.
 * @prop {string} ConnectionInfo.sourceEndpoint UUID of the point (port) where the connections starts.
 * @prop {string} ConnectionInfo.targetEndpoint UUID of the point (port) where the connections ends.
 * 
 * @interface {PrimeFaces.widget.Diagram.UpdateConnectionInfo} UpdateConnectionInfo Details about a connection between
 * two nodes that was changed.
 * @extends {PrimeFaces.widget.Diagram.ConnectionInfo} UpdateConnectionInfo
 * @prop {string} UpdateConnectionInfo.originalSourceId Previous ID of the source node where the connection started.
 * @prop {string} UpdateConnectionInfo.originalTargetId Previous ID of the target node where the connection ended.
 * @prop {string} UpdateConnectionInfo.originalSourceEndpoint Previous UUID of the point (port) where the connections
 * started.
 * @prop {string} UpdateConnectionInfo.originalTargetEndpoint Previous UUID of the point (port) where the connections
 * ended.
 * 
 * @interface {PrimeFaces.widget.Diagram.UpdateElementInfo} UpdateElementInfo Details about an element when its location
 * was changed.
 * @prop {string} UpdateElementInfo.elementId ID of the element that was changed.
 * @prop {number} UpdateElementInfo.x New horizontal position of the element.
 * @prop {number} UpdateElementInfo.y New vertical position of the element.
 * 
 * @prop {import("jsplumb").jsPlumbInstance} canvas The JSPlumb instance for this diagram.
 * @prop {boolean} connectionChanged Internal state whether the connection was changed before a connect event.
 * 
 * @interface {PrimeFaces.widget.DiagramCfg} cfg The configuration for the {@link Diagram|diagram widget}. You can
 * access this configuration via {@link PrimeFaces.widget.BaseWidget.cfg|BaseWidget.cfg}. Please note that this
 * configuration is usually meant to be read-only and should not be modified.
 * @extends {PrimeFaces.widget.DeferredWidgetCfg} cfg
 * 
 * @prop {{uuids: string[]}[]} cfg.connections List of initial connections to be established between nodes.
 * @prop {boolean} cfg.connectionsDetachable Whether connections can be severed by the user.
 * @prop {import("jsplumb").OverlaySpec[]} cfg.connectionOverlays Overlays for the connections (arrows, labels, etc.)
 * @prop {string} cfg.containment Constrains dragging to within the bounds of the specified element or region.
 * @prop {import("jsplumb").ConnectorSpec} cfg.defaultConnector Connector (straight lines, bezier curves, etc.) to use
 * by default.
 * @prop {(import("jsplumb").EndpointOptions & {element: import("jsplumb").ElementGroupRef})[]} cfg.endPoints A list of
 * endpoints (ports) of all diagram nodes.
 * @prop {import("jsplumb").PaintStyle} cfg.hoverPaintStyle Paint style to use when hovering.
 * @prop {number} cfg.maxConnections Maximum number of allowed connections (per node).
 * @prop {import("jsplumb").PaintStyle} cfg.paintStyle Paint style to use when not hovering.
 */
PrimeFaces.widget.Diagram = PrimeFaces.widget.DeferredWidget.extend({

    /**
     * @override
     * @inheritdoc
     * @param {PrimeFaces.PartialWidgetCfg<TCfg>} cfg
     */
    init: function(cfg) {
        this._super(cfg);

        this.renderDeferred();
    },

    /**
     * @include
     * @override
     * @protected
     * @inheritdoc
     */
    _render: function() {
        var $this = this;

        jsPlumb.ready(function() {
            $this.canvas = jsPlumb.getInstance({
                Container: $this.jq.attr('id'),
                Connector: $this.cfg.defaultConnector,
                PaintStyle: $this.cfg.paintStyle,
                HoverPaintStyle: $this.cfg.hoverPaintStyle,
                ConnectionOverlays: $this.cfg.connectionOverlays,
                ConnectionsDetachable: ($this.cfg.connectionsDetachable === false) ? false : true,
                MaxConnections: $this.cfg.maxConnections
            });

            $this.canvas.doWhileSuspended(function() {
                $this.initEndPoints();
                $this.initConnections();

                $this.canvas.draggable($this.jq.children('.ui-diagram-draggable'), {
                    containment: $this.cfg.containment,
                    stop: function(p) {
                    	$this.onUpdateElementPosition({
                    		elementId: p.el.id,
                    		x: parseInt(p.el.style.left),
                    		y: parseInt(p.el.style.top)
                    	});
                    }
                });
            });

            $this.bindEvents();
        });
    },

    /**
     * Initializes the end points of the lines connection diagram nodes.
     * @private
     */
    initEndPoints: function() {
        for(var i = 0; i < this.cfg.endPoints.length; i++) {
            var endPoint = this.cfg.endPoints[i];

            this.canvas.addEndpoint(endPoint.element, endPoint);
        }
    },

    /**
     * Initializes the lines connecting the diagram nodes.
     * @private
     */
    initConnections: function() {
        if(this.cfg.connections) {
            for(var i = 0; i < this.cfg.connections.length; i++) {
                this.canvas.connect(this.cfg.connections[i]);
            }
        }
    },

    /**
     * Sets up all event listeners for this widget.
     * @private
     */
    bindEvents: function() {
        var $this = this;

        this.canvas.bind('connection', function(info) {
            $this.onConnect(info);
        });

        this.canvas.bind('connectionDetached', function(info) {
            if(info.targetId && info.targetId.indexOf($this.id) === 0) {
                $this.onDisconnect(info);
            }
        });

        this.canvas.bind('connectionMoved', function(info) {
            $this.onConnectionChange(info);
        });
    },

    /**
     * Callback for the event when two nodes are connected with each other.
     * @private
     * @param {PrimeFaces.widget.Diagram.ConnectionInfo} info Details about the connection that was made.
     */
    onConnect: function(info) {
        var options = {
            source: this.id,
            process: this.id,
            params: [
                {name: this.id + '_connect', value: true},
                {name: this.id + '_sourceId', value: info.sourceId.substring(this.id.length + 1)},
                {name: this.id + '_targetId', value: info.targetId.substring(this.id.length + 1)},
                {name: this.id + '_sourceEndPointId', value: info.sourceEndpoint.getUuid()},
                {name: this.id + '_targetEndPointId', value: info.targetEndpoint.getUuid()}
            ]
        };

        if(this.connectionChanged) {
            options.params.push({name: this.id + '_connectionChanged', value: true});
        }
        this.connectionChanged = false;

        if(this.hasBehavior('connect')) {
            this.callBehavior('connect', options);
        }
        else {
            PrimeFaces.ajax.Request.handle(options);
        }
    },

    /**
     * Callback for the event when two nodes are disconnected from each other.
     * @private
     * @param {PrimeFaces.widget.Diagram.ConnectionInfo} info Details about the connection that was severed.
     */
    onDisconnect: function(info) {
        var options = {
            source: this.id,
            process: this.id,
            params: [
                {name: this.id + '_disconnect', value: true},
                {name: this.id + '_sourceId', value: info.sourceId.substring(this.id.length + 1)},
                {name: this.id + '_targetId', value: info.targetId.substring(this.id.length + 1)},
                {name: this.id + '_sourceEndPointId', value: info.sourceEndpoint.getUuid()},
                {name: this.id + '_targetEndPointId', value: info.targetEndpoint.getUuid()}
            ]
        };

        if(this.hasBehavior('disconnect')) {
            this.callBehavior('disconnect', options);
        }
        else {
            PrimeFaces.ajax.Request.handle(options);
        }
    },

    /**
     * Callback for the event when the connection between two nodes was changed.
     * @private
     * @param {PrimeFaces.widget.Diagram.UpdateConnectionInfo} info Details about the connection that was changed.
     */
    onConnectionChange: function(info) {
        this.connectionChanged = true;

        var options = {
            source: this.id,
            process: this.id,
            params: [
                {name: this.id + '_connectionChange', value: true},
                {name: this.id + '_originalSourceId', value: info.originalSourceId.substring(this.id.length + 1)},
                {name: this.id + '_newSourceId', value: info.newSourceId.substring(this.id.length + 1)},
                {name: this.id + '_originalTargetId', value: info.originalTargetId.substring(this.id.length + 1)},
                {name: this.id + '_newTargetId', value: info.newTargetId.substring(this.id.length + 1)},
                {name: this.id + '_originalSourceEndPointId', value: info.originalSourceEndpoint.getUuid()},
                {name: this.id + '_newSourceEndPointId', value: info.newSourceEndpoint.getUuid()},
                {name: this.id + '_originalTargetEndPointId', value: info.originalTargetEndpoint.getUuid()},
                {name: this.id + '_newTargetEndPointId', value: info.newTargetEndpoint.getUuid()}
            ]
        };

        if(this.hasBehavior('connectionChange')) {
            this.callBehavior('connectionChange', options);
        }
        else {
            PrimeFaces.ajax.Request.handle(options);
        }
    },
    
    /**
     * Callback for when the location of a diagram element has changed.
     * @private
     * @param {PrimeFaces.widget.Diagram.UpdateElementInfo} info Details about the element that was changed.
     */
    onUpdateElementPosition: function(info) {
        var options = {
            source: this.id,
            process: this.id,
            params: [
                {name: this.id + '_positionChange', value: true},
                {name: this.id + '_elementId', value: info.elementId.substring(this.id.length + 1)},
                {name: this.id + '_position', value: info.x + ',' + info.y}
            ]
        };

        if(this.hasBehavior('positionChange')) {
            this.callBehavior('positionChange', options);
        }
        else {
            PrimeFaces.ajax.Request.handle(options);
        }
    }

});
