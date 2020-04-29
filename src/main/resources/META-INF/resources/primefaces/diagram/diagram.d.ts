// Type definitions for jsPlumb 2.1.0
// Ron Newcomb
// Note: The newer versions of jsPlumb (since 2.5) ship with a type declaration file
// Use that when upgrading jsPlumb (npm i jsplumb and include it via --includemodules when generating the declarations)



declare namespace JsPlumb {

    export interface PaintStyle {
        strokeStyle?: string;
        fillStyle?: string;
        lineWidth?: number;
        outlineWidth: number;
        outlineColor: string;
    }

    export type Selector = string;
    export type UUID = string;
    export type ElementId = string;
    export type ElementRef = ElementId | Element;
    export type ElementGroupRef = ElementId | Element | ElementId[] | Element[];

    export class jsPlumbInstance {

        addEndpoint(el: ElementGroupRef, params?: EndpointOptions, referenceParams?: EndpointOptions): Endpoint | Endpoint[];

        addEndpoints(target: ElementGroupRef, endpoints: EndpointOptions[], referenceParams?: EndpointOptions): Endpoint[];

        animate(el: ElementRef, properties?: Record<string, any>, options?: Record<string, any>): void;

        batch(fn: (...args: any) => any, doNotRepaintAfterwards?: boolean/* =false */): void;

        bind(event: "connection", callback: (info: ConnectionMadeEventInfo, originalEvent: Event) => void, insertAtStart?: boolean/* =false */): void;
        bind(event: "click", callback: (info: Connection, originalEvent: Event) => void, insertAtStart?: boolean/* =false */): void;
        bind(event: string, callback: (info: OnConnectionBindInfo, originalEvent: Event) => void, insertAtStart?: boolean/* =false */): void;

        cleanupListeners(): void;

        connect(params: ConnectParams, referenceParams?: Partial<ConnectParams>): Connection;

        deleteEndpoint(object: UUID | Endpoint, doNotRepaintAfterwards?: boolean/* =false */): jsPlumbInstance;

        deleteEveryEndpoint(): jsPlumbInstance;

        detach(conn: Connection): void;

        detachEveryConnection(): void;

        detachAllConnections(element: string): void;

        doWhileSuspended(): jsPlumbInstance;

        draggable(el: string | HTMLElement | ArrayLike<string | HTMLElement>, options?: DragOptions): jsPlumbInstance;

        empty(el: string | Element | Selector): void;

        fire(event: string, value: any, originalEvent: Event): void;

        getAllConnections(): Connection[];

        getConnections(scope: string, options: any, scope2?: string | string, source?: string | string | Selector, target?: string | string | Selector, flat?: boolean/* =false */): any[] | Map<any, any>;

        getContainer(): Element;

        getDefaultScope(): string;

        getEndpoint(uuid: string): Endpoint;

        getEndpoints(element:string | Element): Endpoint[] | null;

        getInstance(_defaults?: Defaults): any;

        getScope(Element: Element | string): string;

        getSelector(context?: Element | Selector, spec?: string): void;

        getSourceScope(Element: Element | string): string;

        getTargetScope(Element: Element | string): string;

        getType(id: string, typeDescriptor: string): any;

        hide(el: string | Element | Selector, changeEndpoints?: boolean/* =false */): jsPlumbInstance;

        importDefaults(defaults: Defaults): jsPlumbInstance;

        isHoverSuspended(): boolean;

        isSource(el: string | Element | Selector): boolean;

        isSourceEnabled(el: string | Element | Selector, connectionType?: string): boolean;

        isSuspendDrawing(): boolean;

        isSuspendEvents(): boolean;

        isTarget(el: string | Element | Selector): boolean;

        isTargetEnabled(el: string | Element | Selector): boolean;

        makeSource(el: string | Element | Selector, params: Record<string, any>, endpoint?: string | any[], parent?: string | Element, scope?: string, dragOptions?: Record<string, any>, deleteEndpointsOnDetach?: boolean/* =false */, filter?: (...args: any) => any): void;

        makeTarget(el: string | Element | Selector, params: Record<string, any>, endpoint?: string | any[], scope?: string, dropOptions?: Record<string, any>, deleteEndpointsOnDetach?: boolean/* =true */, maxConnections?: number/* =-1 */, onMaxConnections?: (...args: any) => any): void;

        off(el: Element | Element | string, event: string, fn: (...args: any) => any): jsPlumbInstance;

        on(el: Element | Element | string, children?: string, event?: string, fn?: (...args: any) => any): jsPlumbInstance;

        ready(fn: (...args: any) => any): void;

        recalculateOffsets(el: string | Element | Selector): void;

        registerConnectionType(typeId: string, type: any): void;

        registerConnectionTypes(types: any[]): void;

        registerEndpointType(typeId: string, type: any): void;

        registerEndpointTypes(types: any[]): void;

        remove(el: string | Element | Selector): void;

        removeAllEndpoints(el: string | Element | Selector, recurse?: boolean/* =false */): jsPlumbInstance;

        repaint(el: string | Element | Selector): jsPlumbInstance;

        repaintEverything(clearEdits?: boolean/* =false */): jsPlumbInstance;

        reset(): void;

        restoreDefaults(): jsPlumbInstance;

        revalidate(el: string | Element | Selector): void;

        select(params?: Record<string, any>, scope?: string | string, source?: string | string, target?: string | string, connections?: Connection[]): { each(fn: (conn: Connection) => void): void };

        getHoverPaintStyle(params?: Record<string, any>, scope?: string, source?: string | Element | Selector | any[], target?: string | Element | Selector | any[], element?: string | Element | Selector | any[]): Selection;

        setHover(container: string | Element | Selector): void;

        setDefaultScope(scope: string): jsPlumbInstance;

        setDraggable(el: string | Record<string, any> | any[], draggable: boolean): void;

        setHoverSuspended(hover: boolean): void;

        setIdChanged(oldId: string, newId: string): void;

        setParent(el: Selector | Element, newParent: Selector | Element | string): void;

        setScope(el: Element | string, scope: string): void;

        setSource(connection: Connection, source: string | Element | Endpoint, doNotRepaint?: boolean/* =false */): jsPlumbInstance;

        setSourceEnabled(el: string | Element | Selector, state: boolean): jsPlumbInstance;

        setSourceScope(el: Element | string, scope: string, connectionType?: string): void;

        setSuspendDrawing(val: boolean, repaintAfterwards?: boolean/* =false */): boolean;

        setSuspendEvents(val: boolean): void;

        setTarget(connection: Connection, target: string | Element | Endpoint, doNotRepaint?: boolean/* =false */): jsPlumbInstance;

        setTargetEnabled(el: string | Element | Selector, state: boolean): jsPlumbInstance;

        setTargetScope(el: Element | string, scope: string, connectionType?: string): void;

        show(el: string | Element | Selector, changeEndpoints?: boolean/* =false */): jsPlumbInstance;

        toggleDraggable(el: string | Element | Selector): boolean;

        toggleSourceEnabled(el: string | Element | Selector): boolean;

        toggleTargetEnabled(el: string | Element | Selector): boolean;

        toggleVisible(el: string | Element | Selector, changeEndpoints?: boolean/* =false */): void;

        unbind(eventOrListener?: string | ((...args: any) => any), listener?: (...args: any) => any): void;

        unmakeEverySource(): jsPlumbInstance;

        unmakeEveryTarget(): jsPlumbInstance;

        unmakeSource(el: string | Element | Selector): jsPlumbInstance;

        unmakeTarget(el: string | Element | Selector): jsPlumbInstance;
    }

    export interface ConnectionMadeEventInfo {
        connection: Connection;
        source: HTMLDivElement;
        sourceEndpoint: Endpoint;
        sourceId: string;
        target: HTMLDivElement;
        targetEndpoint: Endpoint;
        targetId: string;
    }

    export interface OnConnectionBindInfo {
        connection: Connection;// the new Connection.you can register listeners on this etc.
        sourceId: number;// - id of the source element in the Connection
        originalSourceId: number;
        newSourceId: number;
        targetId: number;// - id of the target element in the Connection
        originalTargetId: number;
        newTargetId: number;
        source: Element;// - the source element in the Connection
        target: Element;//- the target element in the Connection
        sourceEndpoint: Endpoint;//- the source Endpoint in the Connection
        newSourceEndpoint: Endpoint;
        targetEndpoint: Endpoint;//- the targetEndpoint in the Connection
        newTargetEndpoint: Endpoint;
    }

    export interface Defaults {
        Endpoint?: any;
        Endpoints?: any[];
        Anchor?: any;
        Anchors?: any[];
        PaintStyle?: PaintStyle;
        HoverPaintStyle?: PaintStyle;
        ConnectionsDetachable?: boolean;
        ReattachConnections?: boolean;
        ConnectionOverlays?: any[][];
        Container?: any; // string(selector or id) or element
        DragOptions?: DragOptions;
    }

    export interface Connections {
        detach(): void;
        length: number;
        each(e: (c: Connection) => void): void;
    }

    export interface ConnectParams {
        uuids?: [UUID, UUID];
        source?: ElementRef | Endpoint;
        target?: ElementRef | Endpoint;
        detachable?: boolean;
        deleteEndpointsOnDetach?: boolean;
        endpoint?: EndpointSpec;
        anchor?: AnchorSpec;
        anchors?: [AnchorSpec, AnchorSpec];
        label?: string;
    }

    export interface DragOptions {
        containment?: string;
        start?(...args: any): any;
        drag?(...args: any): any;
        stop?(...args: any): any;
    }

    export interface DropOptions {
        hoverClass: string;
    }

    export interface Connection {
        id: string;
        setDetachable(detachable: boolean): void;
        setParameter(name: string, value: any): void;
        endpoints: [Endpoint, Endpoint];
        getOverlay(s: string): Overlay;
        showOverlay(s: string): void;
        hideOverlay(s: string): void;
        setLabel(s: string): void;
        getElement(): Connection;
    }


    /* -------------------------------------------- CONNECTORS ---------------------------------------------------- */

    export interface ConnectorOptions {
    }
    export type UserDefinedConnectorId = string;
    export type ConnectorId = "Bezier" | "StateMachine" | "Flowchart" | "Straight" | UserDefinedConnectorId;
    export type ConnectorSpec = ConnectorId | [ConnectorId, ConnectorOptions];


    /* -------------------------------------------- ENDPOINTS ------------------------------------------------------ */

    export type EndpointId = "Rectangle" | "Dot" | "Blank" | UserDefinedEndpointId;
    export type UserDefinedEndpointId = string;
    export type EndpointSpec = EndpointId | [EndpointId, EndpointOptions];

    export interface EndpointOptions {
        anchor?: AnchorSpec;
        endpoint?: Endpoint;
        enabled?: boolean;//= true
        paintStyle?: PaintStyle;
        hoverPaintStyle?: PaintStyle;
        cssClass?: string;
        hoverClass?: string;
        maxConnections: number;//= 1?
        dragOptions?: DragOptions;
        dropOptions?: DropOptions;
        connectorStyle?: PaintStyle;
        connectorHoverStyle?: PaintStyle;
        connector?: ConnectorSpec;
        connectorOverlays?: OverlaySpec[];
        connectorClass?: string;
        connectorHoverClass?: string;
        connectionsDetachable?: boolean;//= true
        isSource?: boolean;//= false
        isTarget?: boolean;//= false
        reattach?: boolean;//= false
        parameters: Record<string, any>;
        "connector-pointer-events"?: string;
        connectionType?: string;
        dragProxy?: string | string[];
        id: string;
        scope: string;
        reattachConnections: boolean;
        type: string; // "Dot", etc.
    }

    export class Endpoint {
        anchor: Anchor;
        connections?: Connection[];
        maxConnections: number;//= 1?
        id: string;
        scope: string;
        type: EndpointId;

        detachAll(): void;

        detach(spec: EndpointSpec): void;

        setEndpoint(spec: EndpointSpec): void;

        connectorSelector(): Connection;

        isEnabled(): boolean;

        setEnabled(enabled: boolean): void;

        setHover(hover: boolean): void;

        getElement(): Element;

        setElement(el: Element): void;
    }

    /**
     * The actual component that does the rendering.
     */
    export interface EndpointRenderer {
    }

    /* -------------------------------------------- ANCHORS -------------------------------------------------------- */

    export interface AnchorOptions {
    }

    export type AnchorOrientationHint = -1 | 0 | 1;

    export interface Anchor {
        type: AnchorId;
        cssClass: string;
        elementId: string;
        id: string;
        locked: boolean;
        offsets: [number, number];
        orientation: [AnchorOrientationHint, AnchorOrientationHint];
        x: number;
        y: number;
    }

    export type AnchorId =
        "Assign" |
        "AutoDefault" |
        "Bottom" |
        "BottomCenter" |
        "BottomLeft" |
        "BottomRight" |
        "Center" |
        "Continuous" |
        "ContinuousBottom" |
        "ContinuousLeft" |
        "ContinuousRight" |
        "ContinuousTop" |
        "Left" |
        "LeftMiddle" |
        "Perimeter" |
        "Right" |
        "RightMiddle" |
        "Top" |
        "TopCenter" |
        "TopLeft" |
        "TopRight";


    export type AnchorSpec = AnchorId | [AnchorId, AnchorOptions]


    /* --------------------------------------- OVERLAYS ------------------------------------------------------------- */

    export interface OverlayOptions {
    }

    export interface ArrowOverlayOptions extends OverlayOptions {
        width?: number; // 20
        length?: number; // 20
        location?: number; // 0.5
        direction?: number; // 1
        foldback?: number; // 0.623
        paintStyle?: PaintStyle;
    }

    export interface LabelOverlayOptions extends OverlayOptions {
        label: string;
        cssClass?: string;
        location?: number; // 0.5
        labelStyle?: {
            font?: string;
            color?: string;
            fill?: string;
            borderStyle?: string;
            borderWidth?: number;// integer
            padding?: number; //integer
        };
    }

    export type OverlayId = "Label" | "Arrow" | "PlainArrow" | "Custom";

    export type OverlaySpec = OverlayId | [OverlayId, OverlayOptions];

    export interface Overlay { }
}


declare const jsPlumb: JsPlumb.jsPlumbInstance;

