import { GMap } from "./src/gmap.js";

PrimeFaces.widget.GMap = GMap;

declare global {
    namespace PrimeFacesTypes {
        export interface WidgetRegistry {
            GMap: typeof GMap;
        }
    }

    namespace PrimeFacesTypes.widget {
        export type GMapCfg = import("./src/gmap.js").GMapCfg;
    }

    namespace PrimeFacesTypes.widget.GMap {
        /**
         * Interface for objects that provide an ID that uniquely identifies the object.
         */
        export interface IdProviding {
            /** The ID that uniquely identifies this object. */
            id: string;
        }

        /**
         * A marker, its ID is the ID of the marker in the model (it is also the ID of the marker element).
         */
        export type Marker = import("./src/gmap.js").google.maps.marker.AdvancedMarkerElement & IdProviding;

        /**
         * An overlay shape with an ID property for identifying the shape.
         */
        export type Shape = (
            import("./src/gmap.js").google.maps.Circle
            | import("./src/gmap.js").google.maps.Polyline
            | import("./src/gmap.js").google.maps.Polygon
            | import("./src/gmap.js").google.maps.Rectangle
        ) & IdProviding;

        /**
         * An overlay shape or marker that extends the shapes and markers as defined by the maps API. Adds an ID
         * property for identifying the shape or marker.
         */
        export type Overlay = Marker | Shape;

        /**
         * Javascript callback to execute when a point on map is clicked. See also {@link GMapCfg.onPointClick}.
         */
        export type OnPointClickCallback = (event: import("./src/gmap.js").google.maps.MapMouseEvent) => void;

        /**
         * A symbol (SVG path) used as marker icon.
         */
        export interface Symbol {
            /** The SVG path. */
            path: string;
            /** The position of the symbol relative to the marker position. */
            anchor?: {
                /** Horizontal position. */
                x: number;
                /** Vertical position. */
                y: number;
            };
            /** The fill color. */
            fillColor?: string;
            /** The fill opacity. */
            fillOpacity?: number;
            /** The rotation in degrees. */
            rotation?: number;
            /** The scale. */
            scale?: number;
            /** The stroke color. */
            strokeColor?: string;
            /** The stroke opacity. */
            strokeOpacity?: number;
            /** The stroke width. */
            strokeWeight?: number;
        }

        /**
         * The options of a marker, as rendered by the server.
         */
        export interface MarkerCfg {
            /** The ID of the marker. */
            id: string;
            /** The position of the marker. */
            position: import("./src/gmap.js").google.maps.LatLngLiteral;
            /** The title of the marker. */
            title?: string;
            /** The URL of an image, or a symbol. */
            icon?: string | Symbol;
            /** Whether the marker can be dragged. */
            gmpDraggable?: boolean;
            /** `false` if the marker is hidden. */
            visible?: boolean;
            /** The z-index of the marker. */
            zIndex?: number;
            /** The label shown in the pin of the marker. */
            label?: {
                /** The text of the label. */
                text: string;
                /** The color of the text of the label. */
                color?: string;
                /** The style class of the label (not supported by the marker). */
                className?: string;
                /** The font family of the label (not supported by the marker). */
                fontFamily?: string;
                /** The font size of the label (not supported by the marker). */
                fontSize?: string;
                /** The font weight of the label (not supported by the marker). */
                fontWeight?: string;
            };
        }
    }
}
