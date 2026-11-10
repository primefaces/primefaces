import {
    jsPlumb,
    type ConnectorSpec,
    type ConnectParams,
    type Defaults,
    type ElementGroupRef,
    type Endpoint,
    type EndpointOptions,
    type jsPlumbInstance,
    type OverlaySpec,
    type PaintStyle
} from "jsplumb";

/**
 * An endpoint, with the methods that exist at runtime but are missing in the jsPlumb typings.
 */
export type DiagramEndpoint = Endpoint & {
    /** @returns The UUID of the endpoint. */
    getUuid(): string;
};

/**
 * The jsPlumb instance of a diagram, with the methods that exist at runtime but are missing or typed differently in
 * the jsPlumb typings.
 */
type DiagramCanvas = Omit<jsPlumbInstance, "doWhileSuspended" | "bind"> & {
    /**
     * Suspends drawing, runs the function and then repaints everything.
     * @param fn The function to run while drawing is suspended.
     */
    doWhileSuspended(fn: () => void): void;
    /**
     * Binds a listener to a connection being established or dissolved.
     * @param event The name of the event.
     * @param callback The listener.
     */
    bind(event: "connection" | "connectionDetached", callback: (info: PrimeFacesTypes.widget.Diagram.ConnectionInfo) => void): void;
    /**
     * Binds a listener to a connection being moved.
     * @param event The name of the event.
     * @param callback The listener.
     */
    bind(event: "connectionMoved", callback: (info: PrimeFacesTypes.widget.Diagram.UpdateConnectionInfo) => void): void;
};

/**
 * The jsPlumb default instance, with the methods that exist at runtime but are missing or typed differently in the
 * jsPlumb typings.
 */
const jsPlumbApi = jsPlumb as unknown as {
    /**
     * Runs the function once jsPlumb is ready.
     * @param fn The function to run.
     */
    ready(fn: () => void): void;
    /**
     * Creates a new jsPlumb instance.
     * @param defaults The defaults of the new instance.
     * @returns The new instance.
     */
    getInstance(defaults: Defaults & { MaxConnections?: number }): DiagramCanvas;
};

/**
 * The configuration for the {@link Diagram} widget.
 */
export interface DiagramCfg extends PrimeFacesTypes.widget.DeferredWidgetCfg {
    /** List of initial connections to be established between nodes. */
    connections: ConnectParams[];
    /** Whether connections can be severed by the user. */
    connectionsDetachable: boolean;
    /** Overlays for the connections (arrows, labels, etc.) */
    connectionOverlays: OverlaySpec[];
    /** Constrains dragging to within the bounds of the specified element or region. */
    containment: string;
    /** Connector (straight lines, bezier curves, etc.) to use by default. */
    defaultConnector: ConnectorSpec;
    /** A list of endpoints (ports) of all diagram nodes. */
    endPoints: (EndpointOptions & { element: ElementGroupRef })[];
    /** Paint style to use when hovering. */
    hoverPaintStyle: PaintStyle;
    /** Maximum number of allowed connections (per node). */
    maxConnections: number;
    /** Paint style to use when not hovering. */
    paintStyle: PaintStyle;
}

/**
 * __PrimeFaces Diagram Widget__
 *
 * Diagram is generic component to create visual elements and connect them on a web page. SVG is used on modern browsers
 * and VML on IE 8 and below. Component is highly flexible in terms of api, events and theming.
 */
export class Diagram<Cfg extends DiagramCfg = DiagramCfg> extends PrimeFaces.widget.DeferredWidget<Cfg> {
    /** The JSPlumb instance for this diagram. */
    canvas?: DiagramCanvas;
    /** Internal state whether the connection was changed before a connect event. */
    connectionChanged?: boolean;

    override init(cfg: PrimeFacesTypes.widget.PartialWidgetCfg<Cfg>): void {
        super.init(cfg);

        this.renderDeferred();
    }

    protected override _render(): void {
        jsPlumbApi.ready(() => {
            this.canvas = jsPlumbApi.getInstance({
                Container: this.jq.attr('id'),
                Connector: this.cfg.defaultConnector,
                PaintStyle: this.cfg.paintStyle,
                HoverPaintStyle: this.cfg.hoverPaintStyle,
                ConnectionOverlays: this.cfg.connectionOverlays,
                ConnectionsDetachable: this.cfg.connectionsDetachable !== false,
                MaxConnections: this.cfg.maxConnections
            });

            this.canvas.doWhileSuspended(() => {
                this.initEndPoints();
                this.initConnections();

                this.canvas!.draggable(this.jq.children('.p-diagram-draggable'), {
                    containment: this.cfg.containment,
                    stop: (p) => {
                        this.onUpdateElementPosition({
                            elementId: p.el.id,
                            x: parseInt(p.el.style.left),
                            y: parseInt(p.el.style.top)
                        });
                    }
                });
            });

            this.bindEvents();
        });
    }

    /**
     * Initializes the end points of the lines connection diagram nodes.
     */
    private initEndPoints(): void {
        for (let i = 0; i < this.cfg.endPoints!.length; i++) {
            const endPoint = this.cfg.endPoints![i]!;

            this.canvas!.addEndpoint(endPoint.element, endPoint);
        }
    }

    /**
     * Initializes the lines connecting the diagram nodes.
     */
    private initConnections(): void {
        if (this.cfg.connections) {
            for (let i = 0; i < this.cfg.connections.length; i++) {
                this.canvas!.connect(this.cfg.connections[i]!);
            }
        }
    }

    /**
     * Sets up all event listeners for this widget.
     */
    private bindEvents(): void {
        this.canvas!.bind('connection', (info) => {
            this.onConnect(info);
        });

        this.canvas!.bind('connectionDetached', (info) => {
            if (info.targetId && info.targetId.indexOf(this.getId()) === 0) {
                this.onDisconnect(info);
            }
        });

        this.canvas!.bind('connectionMoved', (info) => {
            this.onConnectionChange(info);
        });
    }

    /**
     * Callback for the event when two nodes are connected with each other.
     * @param info Details about the connection that was made.
     */
    private onConnect(info: PrimeFacesTypes.widget.Diagram.ConnectionInfo): void {
        const options = {
            source: this.getId(),
            process: this.getId(),
            params: [
                { name: this.id + '_connect', value: true },
                { name: this.id + '_sourceId', value: info.sourceId.substring(this.id.length + 1) },
                { name: this.id + '_targetId', value: info.targetId.substring(this.id.length + 1) },
                { name: this.id + '_sourceEndPointId', value: info.sourceEndpoint.getUuid() },
                { name: this.id + '_targetEndPointId', value: info.targetEndpoint.getUuid() }
            ]
        };

        if (this.connectionChanged) {
            options.params.push({ name: this.id + '_connectionChanged', value: true });
        }
        this.connectionChanged = false;

        if (this.hasBehavior('connect')) {
            this.callBehavior('connect', options);
        }
        else {
            PrimeFaces.ajax.Request.handle(options);
        }
    }

    /**
     * Callback for the event when two nodes are disconnected from each other.
     * @param info Details about the connection that was severed.
     */
    private onDisconnect(info: PrimeFacesTypes.widget.Diagram.ConnectionInfo): void {
        const options = {
            source: this.getId(),
            process: this.getId(),
            params: [
                { name: this.id + '_disconnect', value: true },
                { name: this.id + '_sourceId', value: info.sourceId.substring(this.id.length + 1) },
                { name: this.id + '_targetId', value: info.targetId.substring(this.id.length + 1) },
                { name: this.id + '_sourceEndPointId', value: info.sourceEndpoint.getUuid() },
                { name: this.id + '_targetEndPointId', value: info.targetEndpoint.getUuid() }
            ]
        };

        if (this.hasBehavior('disconnect')) {
            this.callBehavior('disconnect', options);
        }
        else {
            PrimeFaces.ajax.Request.handle(options);
        }
    }

    /**
     * Callback for the event when the connection between two nodes was changed.
     * @param info Details about the connection that was changed.
     */
    private onConnectionChange(info: PrimeFacesTypes.widget.Diagram.UpdateConnectionInfo): void {
        this.connectionChanged = true;

        const options = {
            source: this.getId(),
            process: this.getId(),
            params: [
                { name: this.id + '_connectionChange', value: true },
                { name: this.id + '_originalSourceId', value: info.originalSourceId.substring(this.id.length + 1) },
                { name: this.id + '_newSourceId', value: info.newSourceId.substring(this.id.length + 1) },
                { name: this.id + '_originalTargetId', value: info.originalTargetId.substring(this.id.length + 1) },
                { name: this.id + '_newTargetId', value: info.newTargetId.substring(this.id.length + 1) },
                { name: this.id + '_originalSourceEndPointId', value: info.originalSourceEndpoint.getUuid() },
                { name: this.id + '_newSourceEndPointId', value: info.newSourceEndpoint.getUuid() },
                { name: this.id + '_originalTargetEndPointId', value: info.originalTargetEndpoint.getUuid() },
                { name: this.id + '_newTargetEndPointId', value: info.newTargetEndpoint.getUuid() }
            ]
        };

        if (this.hasBehavior('connectionChange')) {
            this.callBehavior('connectionChange', options);
        }
        else {
            PrimeFaces.ajax.Request.handle(options);
        }
    }

    /**
     * Callback for when the location of a diagram element has changed.
     * @param info Details about the element that was changed.
     */
    private onUpdateElementPosition(info: PrimeFacesTypes.widget.Diagram.UpdateElementInfo): void {
        const options = {
            source: this.getId(),
            process: this.getId(),
            params: [
                { name: this.id + '_positionChange', value: true },
                { name: this.id + '_elementId', value: info.elementId.substring(this.id.length + 1) },
                { name: this.id + '_position', value: info.x + ',' + info.y }
            ]
        };

        if (this.hasBehavior('positionChange')) {
            this.callBehavior('positionChange', options);
        }
        else {
            PrimeFaces.ajax.Request.handle(options);
        }
    }

}
