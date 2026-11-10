/**
 * Minimal types of the parts of the Google Maps API used by this widget. The API is a global loaded by the page (or by
 * {@link GMap.ensureGoogleMapsLoaded}), the project has no `@types/google.maps`.
 */
declare namespace google.maps {
    /** A point in geographical coordinates. */
    class LatLng {
        /**
         * @param lat Latitude in degrees, the API also accepts numeric strings.
         * @param lng Longitude in degrees, the API also accepts numeric strings.
         */
        constructor(lat: number | string, lng: number | string);
        /** @returns The latitude in degrees. */
        lat(): number;
        /** @returns The longitude in degrees. */
        lng(): number;
    }

    /** A point in geographical coordinates as object literal. */
    interface LatLngLiteral {
        /** Latitude in degrees. */
        lat: number;
        /** Longitude in degrees. */
        lng: number;
    }

    /** A rectangle in geographical coordinates. */
    class LatLngBounds {
        /**
         * Extends these bounds to contain the given point.
         * @param point The point to include.
         * @returns These bounds.
         */
        extend(point: LatLng | LatLngLiteral): LatLngBounds;
        /**
         * Extends these bounds to contain the union of these and the given bounds.
         * @param other The bounds to include.
         * @returns These bounds.
         */
        union(other: LatLngBounds | null): LatLngBounds;
        /** @returns The center of these bounds. */
        getCenter(): LatLng;
        /** @returns The north-east corner of these bounds. */
        getNorthEast(): LatLng;
        /** @returns The south-west corner of these bounds. */
        getSouthWest(): LatLng;
    }

    /** A rectangle in geographical coordinates as object literal. */
    interface LatLngBoundsLiteral {
        /** East longitude in degrees. */
        east: number;
        /** North latitude in degrees. */
        north: number;
        /** South latitude in degrees. */
        south: number;
        /** West longitude in degrees. */
        west: number;
    }

    /** Padding in pixels. */
    interface Padding {
        /** Padding at the bottom. */
        bottom?: number;
        /** Padding at the left. */
        left?: number;
        /** Padding at the right. */
        right?: number;
        /** Padding at the top. */
        top?: number;
    }

    /** A map. */
    class Map {
        /**
         * @param mapDiv The element the map is rendered in.
         * @param opts The map options.
         */
        constructor(mapDiv: HTMLElement | null, opts?: object);
        /**
         * Sets the viewport to contain the given bounds.
         * @param bounds The bounds to show.
         * @param padding Padding around the bounds.
         */
        fitBounds(bounds: LatLngBounds | LatLngBoundsLiteral, padding?: number | Padding): void;
        /** @returns The bounds of the viewport, `undefined` before the map is initialized. */
        getBounds(): LatLngBounds | undefined;
        /** @returns The zoom level. */
        getZoom(): number | undefined;
        /**
         * Sets the zoom level.
         * @param zoom The zoom level.
         */
        setZoom(zoom: number): void;
    }

    /** The map types, such as `ROADMAP`. */
    const MapTypeId: Record<string, string>;

    /** The event of a mouse click on the map or on an overlay. */
    interface MapMouseEvent {
        /** The position of the mouse. */
        latLng: LatLng | null;
    }

    /** A popup window over the map, created by the renderer with the ID of the `p:gmapInfoWindow`. */
    class InfoWindow {
        /** The client ID of the `p:gmapInfoWindow`. */
        id: string;
        /**
         * Sets the content of this window.
         * @param content The HTML content.
         */
        setContent(content: string): void;
        /**
         * Opens this window.
         * @param map The map to open this window on.
         * @param anchor The overlay to which this window is anchored.
         */
        open(map?: Map, anchor?: object | null): void;
    }

    /** A circle overlay. */
    class Circle {
        /** @returns The bounds of this circle. */
        getBounds(): LatLngBounds | null;
        /**
         * Shows this circle on the given map.
         * @param map The map, `null` removes the circle from the map.
         */
        setMap(map: Map | null): void;
    }

    /** A rectangle overlay. */
    class Rectangle {
        /** @returns The bounds of this rectangle. */
        getBounds(): LatLngBounds | null;
        /**
         * Shows this rectangle on the given map.
         * @param map The map, `null` removes the rectangle from the map.
         */
        setMap(map: Map | null): void;
    }

    /** A polyline overlay. */
    class Polyline {
        /** @returns The points of this polyline. */
        getPath(): MVCArray<LatLng>;
        /**
         * Shows this polyline on the given map.
         * @param map The map, `null` removes the polyline from the map.
         */
        setMap(map: Map | null): void;
    }

    /** A polygon overlay. */
    class Polygon {
        /** @returns The first path of this polygon. */
        getPath(): MVCArray<LatLng>;
        /**
         * Shows this polygon on the given map.
         * @param map The map, `null` removes the polygon from the map.
         */
        setMap(map: Map | null): void;
    }

    /** An observable array. */
    class MVCArray<T> {
        /**
         * Calls the callback for each element.
         * @param callback The callback.
         */
        forEach(callback: (elem: T, i: number) => void): void;
    }

    /** A service for converting between addresses and points. */
    class Geocoder {
        /**
         * Finds the points for an address or the addresses for a point.
         * @param request The address or the point.
         * @param callback Called with the results.
         */
        geocode(request: GeocoderRequest, callback: (results: GeocoderResult[] | null, status: string) => void): void;
    }

    /** The request of {@link Geocoder.geocode}. */
    interface GeocoderRequest {
        /** The address to find. */
        address?: string;
        /** The point to find. */
        latLng?: LatLng;
    }

    /** A result of {@link Geocoder.geocode}. */
    interface GeocoderResult {
        /** The address. */
        formatted_address: string;
        /** The location. */
        geometry: {
            /** The point. */
            location: LatLng;
        };
    }

    /** The status of {@link Geocoder.geocode}. */
    const GeocoderStatus: {
        /** The request succeeded. */
        OK: string;
    };

    /** A listener added via {@link event.addListener}. */
    interface MapsEventListener {
        /** Removes this listener. */
        remove(): void;
    }

    /** Functions for the events of the maps API. */
    namespace event {
        /**
         * Adds a listener for an event of a map, an overlay or an info window.
         * @param instance The object that triggers the event.
         * @param eventName The name of the event.
         * @param handler The listener, called with the object as `this`.
         * @returns The added listener.
         */
        function addListener<T extends object>(instance: T, eventName: string, handler: (this: T, event: MapMouseEvent) => void): MapsEventListener;
        /**
         * Triggers an event.
         * @param instance The object that triggers the event.
         * @param eventName The name of the event.
         */
        function trigger(instance: object, eventName: string): void;
    }

    /** The `marker` library. */
    namespace marker {
        /** The options of an {@link AdvancedMarkerElement}. */
        interface AdvancedMarkerElementOptions {
            /** The DOM element shown as marker. */
            content?: Node | null;
            /** Whether the marker can be dragged. */
            gmpDraggable?: boolean;
            /** The map, `null` hides the marker. */
            map?: Map | null;
            /** The position of the marker. */
            position?: LatLng | LatLngLiteral | null;
            /** The title of the marker. */
            title?: string;
            /** The z-index of the marker. */
            zIndex?: number | null;
        }

        /** A marker, it is a DOM element. */
        class AdvancedMarkerElement extends HTMLElement {
            /**
             * @param options The options of the marker.
             */
            constructor(options?: AdvancedMarkerElementOptions);
            /** The map, `null` hides the marker. */
            map: Map | null | undefined;
            /** The position of the marker. */
            position: LatLng | LatLngLiteral | null | undefined;
            /** Markers can't be added via `setMap`, see {@link GMap.addOverlay}. */
            setMap?: undefined;
        }

        /** The options of a {@link PinElement}. */
        interface PinElementOptions {
            /** The text in the pin. */
            glyph?: string;
            /** The color of the text in the pin. */
            glyphColor?: string;
        }

        /** The default pin shown as marker. */
        class PinElement {
            /**
             * @param options The options of the pin.
             */
            constructor(options?: PinElementOptions);
            /** The DOM element of the pin. */
            element: HTMLElement;
        }
    }

    /**
     * Loads the `marker` library.
     * @param libraryName `marker`.
     * @returns The classes of the library.
     */
    function importLibrary(libraryName: "marker"): Promise<{ AdvancedMarkerElement: typeof marker.AdvancedMarkerElement; PinElement: typeof marker.PinElement }>;
    /**
     * Loads a library.
     * @param libraryName The name of the library, e.g. `maps`.
     * @returns The classes of the library.
     */
    function importLibrary(libraryName: string): Promise<unknown>;
}

/**
 * The global `google` object, the maps API or only its bootstrap loader.
 */
interface GoogleGlobal {
    /** The maps API, its parts are present once it was loaded. */
    maps?: Partial<typeof google.maps> & {
        /** The promise of the bootstrap loader while it loads the API. */
        __ib__?: Promise<void>;
    };
}

declare global {
    interface Window {
        /** The Google Maps API, once it is (being) loaded. */
        google?: GoogleGlobal;
    }
}

/** Exported for the types in the index. */
export type { google };

/**
 * The configuration for the {@link GMap} widget. Besides these properties, it contains the map options, they are
 * passed to the `google.maps.Map`.
 */
export interface GMapCfg extends PrimeFacesTypes.widget.DeferredWidgetCfg {
    /** Google Maps API key. Required for asynchronous loading if Google Maps is not already loaded. */
    apiKey: string;
    /** Google Maps API version. Defaults to 'weekly'. Only used for asynchronous loading. */
    apiVersion: string;
    /** The center of the map: `lat,lng` as rendered, replaced with a `google.maps.LatLng` when the map is rendered. */
    center: string | google.maps.LatLng;
    /** List of overlay circular shapes added to this map. */
    circles: (google.maps.Circle & PrimeFacesTypes.widget.GMap.IdProviding)[];
    /** Whether the default UI of the map is disabled. */
    disableDefaultUI: boolean;
    /** Whether zooming by double click is disabled. */
    disableDoubleClickZoom: boolean;
    /** Whether the map can be dragged. */
    draggable: boolean;
    /**
     * Defines if center and zoom should be calculated automatically to contain all markers on the map.
     */
    fitBounds: boolean;
    /** The current info window instance, if any info window was created yet. */
    infoWindow: google.maps.InfoWindow;
    /** HTML string with the contents of the info window, as fetched from the server. */
    infoWindowContent: string;
    /**
     * Additional Google Maps libraries to load (e.g., ['places', 'geometry']). Only used for asynchronous loading.
     */
    libraries: string[];
    /** Google Maps map ID. Required by `AdvancedMarkerElement`, defaults to `DEMO_MAP_ID`. */
    mapId: string;
    /** Whether the map type control is shown. */
    mapTypeControl: boolean;
    /** The map type, e.g. `ROADMAP` as rendered, replaced with the `google.maps.MapTypeId` when the map is rendered. */
    mapTypeId: string;
    /**
     * The markers to display on the map: the options as rendered, replaced with the created markers when the map is
     * rendered.
     */
    markers: PrimeFacesTypes.widget.GMap.MarkerCfg[] | PrimeFacesTypes.widget.GMap.Marker[];
    /** Whether the navigation control is shown. */
    navigationControl: boolean;
    /** Javascript callback to execute when a point on map is clicked. */
    onPointClick: PrimeFacesTypes.widget.GMap.OnPointClickCallback;
    /** List of overlay polygonal shapes added to this map. */
    polygons: (google.maps.Polygon & PrimeFacesTypes.widget.GMap.IdProviding)[];
    /** List of overlay polyline shapes added to this map. */
    polylines: (google.maps.Polyline & PrimeFacesTypes.widget.GMap.IdProviding)[];
    /** List of overlay rectangular shapes added to this map. */
    rectangles: (google.maps.Rectangle & PrimeFacesTypes.widget.GMap.IdProviding)[];
    /** Whether the map can be zoomed with the mouse wheel. */
    scrollwheel: boolean;
    /** Whether the street view control is shown. */
    streetViewControl: boolean;
    /** The zoom level. */
    zoom: number;
}

/**
 * __PrimeFaces Google Maps Widget__
 *
 * GMap is a map component integrated with Google Maps API V3.
 */
export class GMap<Cfg extends GMapCfg = GMapCfg> extends PrimeFaces.widget.DeferredWidget<Cfg> {
    /** The current google maps instance. */
    map!: google.maps.Map;
    /** The currently selected and active overlay shape. */
    selectedOverlay?: PrimeFacesTypes.widget.GMap.Overlay;
    /**
     * The spherical portion of the earth's surface that is currently shown in the map viewport.
     */
    viewport?: google.maps.LatLngBounds;

    override init(cfg: PrimeFacesTypes.widget.PartialWidgetCfg<Cfg>): void {
        super.init(cfg);

        this.renderDeferred();
    }

    /**
     * Override to handle async _render() method.
     */
    override renderDeferred(): void {
        if (this.jq.is(':visible')) {
            // Call async _render and handle postRender after it completes
            Promise.resolve(this._render()).then(() => {
                this.postRender();
            }).catch((error) => {
                PrimeFaces.error('Error rendering GMap widget: ' + (error.message || error));
            });
        }
        else if (this.jq[0]) {
            const container = this.jq[0].closest('.p-hidden-container');
            if (container instanceof HTMLElement) {
                const $container = $(container);
                if ($container.length) {
                    const $this = this;
                    this.addDeferredRender(this.id, $container, () => {
                        // the callback should return whether the widget was rendered, the (truthy) promise is returned
                        return Promise.resolve($this._render()).then(() => true).catch(() => false) as unknown as boolean;
                    });
                }
            }
        }
    }

    /**
     * Ensures Google Maps API is loaded. Supports both static loading (already loaded) and dynamic async loading.
     * @returns Promise that resolves when Google Maps is ready to use.
     */
    private async ensureGoogleMapsLoaded(): Promise<void> {
        // Check if Google Maps is already loaded (static loading scenario)
        if (window.google && window.google.maps && window.google.maps.Map) {
            return Promise.resolve();
        }

        // Check if bootstrap loader is already present and use it
        if (window.google && window.google.maps && window.google.maps.importLibrary) {
            try {
                await window.google.maps.importLibrary("maps");
                // Load additional libraries if specified
                if (this.cfg.libraries && Array.isArray(this.cfg.libraries)) {
                    await Promise.all(this.cfg.libraries.map(lib =>
                        window.google!.maps!.importLibrary!(lib)
                    ));
                }
                return Promise.resolve();
            } catch (error) {
                return Promise.reject(new Error('Failed to load Google Maps library: ' + (error as Error).message));
            }
        }

        // If bootstrap is loading, wait for it
        if (window.google && window.google.maps && window.google.maps.__ib__) {
            await window.google.maps.__ib__;
            return this.ensureGoogleMapsLoaded(); // Retry after bootstrap loads
        }

        // Need to load Google Maps dynamically - check for API key
        if (!this.cfg.apiKey) {
            return Promise.reject(new Error('Google Maps API key is required. Either load Google Maps statically via script tag, or provide apiKey in widget configuration.'));
        }

        // Load Google Maps using the dynamic library import bootstrap loader
        return new Promise<void>((resolve, reject) => {
            const version = this.cfg.apiVersion || 'weekly';
            const libraries = this.cfg.libraries && Array.isArray(this.cfg.libraries) ? this.cfg.libraries : [];

            // Create bootstrap loader inline script (from Google Maps documentation)
            const librariesParam = libraries.length > 0 ? `,libraries: "${libraries.join(',')}"` : '';
            const bootstrapCode = `(g=>{var h,a,k,p="The Google Maps JavaScript API",c="google",l="importLibrary",q="__ib__",m=document,b=window;b=b[c]||(b[c]={});var d=b.maps||(b.maps={}),r=new Set,e=new URLSearchParams,u=()=>h||(h=new Promise(async(f,n)=>{await (a=m.createElement("script"));e.set("libraries",[...r]+"");for(k in g)e.set(k.replace(/[A-Z]/g,t=>"_"+t[0].toLowerCase()),g[k]);e.set("callback",c+".maps."+q);a.src=\`https://maps.\${c}apis.com/maps/api/js?\`+e;d[q]=f;a.onerror=()=>h=n(Error(p+" could not load."));a.nonce=m.querySelector("script[nonce]")?.nonce||"";m.head.append(a)}));d[l]?console.warn(p+" only loads once. Ignoring:",g):d[l]=(f,...n)=>r.add(f)&&u().then(()=>d[l](f,...n))})({key:"${this.cfg.apiKey}",v:"${version}"${librariesParam}});`;

            const script = document.createElement('script');
            script.textContent = bootstrapCode;
            script.onerror = () => reject(new Error('Failed to load Google Maps bootstrap loader'));

            // Wait for the bootstrap to initialize
            const checkReady = (): void => {
                if (window.google && window.google.maps && window.google.maps.importLibrary) {
                    // Bootstrap ready, load the maps library
                    window.google.maps.importLibrary("maps")
                        .then(() => {
                            // Load additional libraries if specified
                            if (libraries.length > 0) {
                                return Promise.all(libraries.map(lib =>
                                    window.google!.maps!.importLibrary!(lib)
                                ));
                            }
                            return undefined;
                        })
                        .then(() => resolve())
                        .catch(reject);
                } else if (window.google && window.google.maps && window.google.maps.__ib__) {
                    // Bootstrap is loading, wait for it
                    window.google.maps.__ib__.then(() => {
                        setTimeout(checkReady, 10);
                    }).catch(reject);
                } else {
                    // Keep checking
                    setTimeout(checkReady, 50);
                }
            };

            document.head.appendChild(script);
            // Start checking after script execution
            setTimeout(checkReady, 10);
        });
    }

    protected override async _render(): Promise<void> {
        // Ensure Google Maps is loaded before rendering
        try {
            await this.ensureGoogleMapsLoaded();
        } catch (error) {
            PrimeFaces.error('Failed to load Google Maps: ' + (error as Error).message);
            return;
        }

        if (this.cfg.mapTypeId) {
            this.cfg.mapTypeId = google.maps.MapTypeId[this.cfg.mapTypeId];
        }

        if (this.cfg.center) {
            const center = (this.cfg.center as string).split(',');
            this.cfg.center = new google.maps.LatLng(center[0]!, center[1]!);
        }
        this.map = new google.maps.Map(document.getElementById(this.getId()), this.cfg);
        this.cfg.fitBounds = this.cfg.fitBounds !== false;
        this.viewport = this.map.getBounds();

        //conf markers
        if(this.cfg.markers) {
            await this.createMarkers();
            this.configureMarkers();
        }

        //add polylines
        if(this.cfg.polylines) {
            this.configurePolylines();
        }

        //add polygons
        if(this.cfg.polygons) {
            this.configurePolygons();
        }

        //add circles
        if(this.cfg.circles) {
            this.configureCircles();
        }

        //add rectangles
        if(this.cfg.rectangles) {
            this.configureRectangles();
        }

        //general map events
        this.configureEventListeners();

        //fit auto bounds
        if(this.cfg.fitBounds && this.viewport)
            this.map.fitBounds(this.viewport);

        //bind infowindow domready for dynamic content.
        if(this.cfg.infoWindow){
            const _self = this;
            google.maps.event.addListener(this.cfg.infoWindow, 'domready', function() {
                _self.loadWindow(_self.cfg.infoWindowContent!);
            });
        }
    }

    /**
     * Returns the current google maps instance.
     * @returns The current map instance.
     */
    getMap(): google.maps.Map {
        return this.map;
    }

    /**
     * The info window that can be displayed to provide detailed information when a marker is selected.
     * @returns The current info window instance, if any exists.
     */
    getInfoWindow(): google.maps.InfoWindow | undefined {
        return this.cfg.infoWindow;
    }

    /**
     * Writes the given HTML content into the info window.
     * @param content HTML content for the info window.
     */
    private loadWindow(content: string): void {
        this.jq.find(PrimeFaces.escapeClientId(this.getInfoWindow()!.id + '_content')).html(content||'');
    }

    /**
     * Loads the contents of the info window from the server and open the info window.
     * @param responseXML The XML that was returned by the AJAX request made to fetch the contents of the
     * info window.
     * @returns `true` if the info window load was initiated successfully, or `false` otherwise.
     */
    openWindow(responseXML: XMLDocument): boolean {
        const infoWindow = this.getInfoWindow()!;
        const $this = this;

        // the info window is no widget, only its ID is used; status and XHR are not needed
        PrimeFaces.ajax.Response.handle(responseXML, null as unknown as JQuery.Ajax.SuccessTextStatus, null as unknown as PrimeFacesTypes.ajax.pfXHR, {
            widget: infoWindow as unknown as PrimeFacesTypes.Widget<"BaseWidget">,
            handle: function(content) {
                $this.cfg.infoWindowContent = content;
                infoWindow.setContent('<div id="' + infoWindow.id + '_content">' + content + '</div>');

                infoWindow.open($this.getMap(), $this.selectedOverlay);
            }
        });

        return true;
    }

    /**
     * Replaces the rendered marker options with `google.maps.marker.AdvancedMarkerElement` instances.
     * @returns Promise that resolves when all markers were created.
     */
    private async createMarkers(): Promise<void> {
        const { AdvancedMarkerElement, PinElement } = await google.maps.importLibrary('marker');

        this.cfg.markers = (this.cfg.markers as PrimeFacesTypes.widget.GMap.MarkerCfg[]).map((cfg) => {
            const options: google.maps.marker.AdvancedMarkerElementOptions = { position: cfg.position, map: cfg.visible === false ? null : this.map };
            if (cfg.title) {
                options.title = cfg.title;
            }
            if (cfg.gmpDraggable) {
                options.gmpDraggable = true;
            }
            if (cfg.zIndex !== undefined) {
                options.zIndex = cfg.zIndex;
            }
            if (typeof cfg.icon === 'string') {
                options.content = document.createElement('img');
                (options.content as HTMLImageElement).src = cfg.icon;
            }
            else if (cfg.icon) {
                options.content = this.createSymbolContent(cfg.icon);
            }
            else if (cfg.label) {
                const pin: google.maps.marker.PinElementOptions = { glyph: cfg.label.text };
                if (cfg.label.color) {
                    pin.glyphColor = cfg.label.color;
                }
                options.content = new PinElement(pin).element;
            }

            const marker = new AdvancedMarkerElement(options);
            marker.id = cfg.id;
            return marker;
        });
    }

    /**
     * Renders a symbol icon (SVG path) as marker content, with the symbol anchor placed at the marker position.
     * @param symbol The symbol to render.
     * @returns The SVG element to use as marker content.
     */
    private createSymbolContent(symbol: PrimeFacesTypes.widget.GMap.Symbol): SVGSVGElement {
        const ns = 'http://www.w3.org/2000/svg',
            svg = document.createElementNS(ns, 'svg'),
            path = document.createElementNS(ns, 'path'),
            anchor = symbol.anchor || { x: 0, y: 0 };

        // zero-sized SVG: AdvancedMarkerElement puts the content's bottom center, i.e. the origin, at the position
        svg.setAttribute('width', '0');
        svg.setAttribute('height', '0');
        svg.style.overflow = 'visible';

        path.setAttribute('d', symbol.path);
        path.setAttribute('transform', 'rotate(' + (symbol.rotation || 0) + ') scale(' + (symbol.scale || 1) + ') translate(' + (-anchor.x) + ',' + (-anchor.y) + ')');
        path.setAttribute('fill', symbol.fillColor || 'black');
        path.setAttribute('fill-opacity', String(symbol.fillOpacity !== undefined ? symbol.fillOpacity : 0));
        path.setAttribute('stroke', symbol.strokeColor || 'black');
        path.setAttribute('stroke-opacity', String(symbol.strokeOpacity !== undefined ? symbol.strokeOpacity : 1));
        path.setAttribute('stroke-width', String(symbol.strokeWeight !== undefined ? symbol.strokeWeight : 1));
        path.setAttribute('vector-effect', 'non-scaling-stroke');

        svg.appendChild(path);
        return svg;
    }

    /**
     * Adds and sets up all configured markers for the gmap.
     */
    private configureMarkers(): void {
        const _self = this;

        for(let i=0; i < this.cfg.markers!.length; i++) {
            // the markers were created by createMarkers
            const marker = (this.cfg.markers as PrimeFacesTypes.widget.GMap.Marker[])[i]!;

            //extend viewport
            if(this.cfg.fitBounds)
                this.extendView(marker);

            //overlay select
            google.maps.event.addListener(marker, 'click', function(event) {
                _self.fireOverlaySelectEvent(event, this, 1);
            });
            //AdvancedMarkerElement has no dblclick map event, listen on its DOM element instead
            marker.addEventListener('dblclick', function(event) {
                _self.fireOverlaySelectEvent(event, this as PrimeFacesTypes.widget.GMap.Marker, 2);
            });

            //marker drag
            google.maps.event.addListener(marker, 'dragend', function(event) {
                _self.fireMarkerDragEvent(event, this);
            });
        }
    }

    /**
     * Calls the behavior for when a marker was dragged.
     * @param event Event that occurred.
     * @param marker The marker that was dragged.
     */
    private fireMarkerDragEvent(event: google.maps.MapMouseEvent, marker: PrimeFacesTypes.widget.GMap.Marker): void {
        if(this.hasBehavior('markerDrag')) {
            const ext = {
                params: [
                    {name: this.id + '_markerId', value: marker.id},
                    {name: this.id + '_lat', value: event.latLng!.lat()},
                    {name: this.id + '_lng', value: event.latLng!.lng()}
                ]
            };

            this.callBehavior('markerDrag', ext);
        }
    }

    /**
     * Finds the geocode for the given address and calls the server-side `geocode` behavior, if such a behavior exists.
     * Use `<p:ajax event="geocode" listener="#{geocodeView.onGeocode}" update="@this" />` on the component to define a
     * behavior.
     * @param address Address for which to find a geocode.
     */
    geocode(address: string): void {
        const $this = this;

        if(this.hasBehavior('geocode')) {
            const geocoder = new google.maps.Geocoder(),
                lats: number[] = [],
                lngs: number[] = [],
                addresses: string[] = [];

            geocoder.geocode({'address': address}, function(results, status) {

                if (status == google.maps.GeocoderStatus.OK) {
                    for(let i = 0; i < results!.length; i++) {
                        const location = results![i]!.geometry.location;
                        lats.push(location.lat());
                        lngs.push(location.lng());
                        addresses.push(results![i]!.formatted_address);
                    }

                    if(results!.length) {
                        const ext = {
                            params: [
                                {name: $this.id + '_query', value: address},
                                {name: $this.id + '_addresses', value: addresses.join('_primefaces_')},
                                {name: $this.id + '_lat', value: lats.join()},
                                {name: $this.id + '_lng', value: lngs.join()}
                            ]
                        };

                        $this.callBehavior('geocode', ext);
                    }
                }
                else {
                    PrimeFaces.error('Geocode was not found');
                }
            });

        }
    }

    /**
     * Attempts to find an address for the given lattitude and longitude, and calls the `reverseGeocode` behavior with
     * the result. Use `<p:ajax event="reverseGeocode" listener="#{geocodeView.onReverseGeocode}" update="@this" />` on
     * the component to define a behavior.
     * @param lat Latitude to look up, specified in degrees within the range `[-90, 90]`.
     * @param lng Longitude to look up, specified in degrees within the range `[-180, 180]`.
     */
    reverseGeocode(lat: number, lng: number): void {
        const $this = this;

        if(this.hasBehavior('reverseGeocode')) {
            const geocoder = new google.maps.Geocoder(),
                latlng = new google.maps.LatLng(lat, lng),
                addresses: string[] = [];

            geocoder.geocode({'latLng': latlng}, function(results, status) {

                if (status == google.maps.GeocoderStatus.OK) {
                    for(let i = 0; i < results!.length; i++) {
                        if (results![i]) {
                            addresses[i] = results![i]!.formatted_address;
                        }
                    }

                    if(0 < addresses.length) {
                        const ext = {
                            params: [
                                {name: $this.id + '_address', value: addresses.join('_primefaces_')},
                                {name: $this.id + '_lat', value: lat},
                                {name: $this.id + '_lng', value: lng}
                            ]
                        };

                        $this.callBehavior('reverseGeocode', ext);
                    }
                    else {
                        PrimeFaces.error('No results found');
                    }
                }
                else {
                    PrimeFaces.error('Geocoder failed');
                }
           });

        }
    }

    /**
     * Adds the overlay for a polyline shape.
     */
    private configurePolylines(): void {
        this.addOverlays(this.cfg.polylines!);
    }

    /**
     * Adds the overlay for a circle shape.
     */
    private configureCircles(): void {
        this.addOverlays(this.cfg.circles!);
    }

    /**
     * Adds the overlay for a rectangular shape.
     */
    private configureRectangles(): void {
        this.addOverlays(this.cfg.rectangles!);
    }

    /**
     * Adds the overlay for a polygonal shape.
     */
    private configurePolygons(): void {
        this.addOverlays(this.cfg.polygons!);
    }

    /**
     * Triggers the behavior for when an overlay shape was selected.
     * @param event The event that occurred.
     * @param overlay The shape that was selected.
     * @param clickCount whether it was single or double click
     */
    private fireOverlaySelectEvent(event: google.maps.MapMouseEvent | MouseEvent, overlay: PrimeFacesTypes.widget.GMap.Overlay, clickCount: number): void {
        this.selectedOverlay = overlay;

        const ext = {
                params: [
                    {name: this.id + '_overlayId', value: overlay.id}
                ]
            };

        if (clickCount === 1 && this.hasBehavior('overlaySelect')) {
            this.callBehavior('overlaySelect', ext);
        }
        if (clickCount === 2 && this.hasBehavior('overlayDblSelect')) {
            this.callBehavior('overlayDblSelect', ext);
        }
    }

    /**
     * Adds some event listeners for click events and sets up some behaviors.
     */
    private configureEventListeners(): void {
        const _self = this;

        this.cfg.formId = $(PrimeFaces.escapeClientId(this.getId())).parents('form:first').attr('id');

        //client side events
        if(this.cfg.onPointClick) {
            google.maps.event.addListener(this.map, 'click', function(event) {
                _self.cfg.onPointClick!(event);
            });
        }

        //behaviors
        this.configureStateChangeListener();
        this.configurePointSelectListener();
    }

    /**
     * Sets up the event listeners for when the state of this map has changed.
     */
    private configureStateChangeListener(): void {
        const _self = this,
        onStateChange = function(event: unknown) {
            _self.fireStateChangeEvent(event);
        };

        google.maps.event.addListener(this.map, 'zoom_changed', onStateChange);
        google.maps.event.addListener(this.map, 'dragend', onStateChange);
    }

    /**
     * Triggers the behavior for when the state of this map has changed.
     * @param event The event that triggered the state change.
     */
    private fireStateChangeEvent(event: unknown): void {
        if(this.hasBehavior('stateChange')) {
            const bounds = this.map.getBounds()!;

            const ext = {
                params: [
                    {name: this.id + '_northeast', value: bounds.getNorthEast().lat() + ',' + bounds.getNorthEast().lng()},
                    {name: this.id + '_southwest', value: bounds.getSouthWest().lat() + ',' + bounds.getSouthWest().lng()},
                    {name: this.id + '_center', value: bounds.getCenter().lat() + ',' + bounds.getCenter().lng()},
                    {name: this.id + '_zoom', value: this.map.getZoom()}
                ]
            };

            this.callBehavior('stateChange', ext);
        }
    }

    /**
     * Sets up the event listeners for when a point on the map was selected.
     */
    private configurePointSelectListener(): void {
        const _self = this;

        google.maps.event.addListener(this.map, 'click', function(event) {
            _self.firePointSelectEvent(event, 1);
        });
        google.maps.event.addListener(this.map, 'dblclick', function(event) {
            _self.firePointSelectEvent(event, 2);
        });
    }

    /**
     * Triggers the behavior for when a point on the map was selected.
     * @param event The event that triggered the point selection.
     * @param clickCount whether it was single or double click
     */
    private firePointSelectEvent(event: google.maps.MapMouseEvent, clickCount: number): void {
        const ext = {
                params: [
                    {name: this.id + '_pointLatLng', value: event.latLng!.lat() + ',' + event.latLng!.lng()}
                ]
            };

        if (clickCount === 1 && this.hasBehavior('pointSelect')) {
            this.callBehavior('pointSelect', ext);
        }
        if (clickCount === 2 && this.hasBehavior('pointDblSelect')) {
            this.callBehavior('pointDblSelect', ext);
        }
    }

    /**
     * Adds an overlay shape (circle, polyline, or polygon) to this map.
     * @param overlay Overlay shape to add to this map.
     */
    addOverlay(overlay: PrimeFacesTypes.widget.GMap.Overlay): void {
        if(overlay.setMap) {
            overlay.setMap(this.map);
        }
        else {
            overlay.map = this.map; // AdvancedMarkerElement
        }
    }

    /**
     * Adds all overlay shapes (circle, polyline, or polygon) to this map.
     * @param overlays A list of overlay shapes to add to this map.
     */
    addOverlays(overlays: PrimeFacesTypes.widget.GMap.Shape[]): void {
        const _self = this;

        $.each(overlays, (index, item) => {
            item.setMap(_self.map);

            _self.extendView(item);

            //bind overlay click event
            google.maps.event.addListener(item, 'click', function(event) {
                _self.fireOverlaySelectEvent(event, item, 1);
            });

            google.maps.event.addListener(item, 'dblclick', function(event) {
                _self.fireOverlaySelectEvent(event, item, 2);
            });
        })
    }

    /**
     * Adjusts (zooms out) the viewport of this map so that it fully shows the given shape.
     * @param overlay A shape for which to adjust the viewport.
     */
    private extendView(overlay: PrimeFacesTypes.widget.GMap.Overlay): void {
        if( this.cfg.fitBounds && overlay){
            const _self = this;
            this.viewport = this.viewport || new google.maps.LatLngBounds();
            if(google.maps.marker && overlay instanceof google.maps.marker.AdvancedMarkerElement)
                this.viewport.extend(overlay.position!);

            else if(overlay instanceof google.maps.Circle || overlay instanceof google.maps.Rectangle)
                this.viewport.union(overlay.getBounds());

            else if(overlay instanceof google.maps.Polyline || overlay instanceof google.maps.Polygon)
                overlay.getPath().forEach(function(item, index){
                    _self.viewport!.extend(item);
                });
        }
    }

    /**
     * Triggers a resize event and reapplies the current zoom level, redrawing the map. Useful when the browser viewport
     * was resized etc.
     */
    checkResize(): void {
        google.maps.event.trigger(this.map, 'resize');
        this.map.setZoom(this.map.getZoom()!);
    }

    /**
     * Sets the map viewport to contain the given bounds.
     *
     * @see https://developers.google.com/maps/documentation/javascript/reference/map?hl=uk#Map.fitBounds
     *
     * @param bounds The new bounds
     * @param padding Optional padding around the bounds.
     */
    fitBounds(bounds: google.maps.LatLngBounds | google.maps.LatLngBoundsLiteral, padding?: number | google.maps.Padding): void {
        //remember the property set by PrimeFaces
        const original = this.map.fitBounds;

        //replace the code by the one provided by google maps api
        this.map.fitBounds = google.maps.Map.prototype.fitBounds;

        //execute fitBounds function
        this.map.fitBounds(bounds, padding);

        //restore PrimeFaces property
        this.map.fitBounds = original;
    }
}
