import "./src/raphael.connection.js";
import { Mindmap } from "./src/mindmap.js";

PrimeFaces.widget.Mindmap = Mindmap;

declare global {
    namespace PrimeFacesTypes {
        export interface WidgetRegistry {
            Mindmap: typeof Mindmap;
        }
    }

    namespace PrimeFacesTypes.widget {
        export type MindmapCfg = import("./src/mindmap.js").MindmapCfg;
    }

    namespace PrimeFacesTypes.widget.Mindmap {
        /**
         * Model that describes a node of the mindmap, such as its parent and children, its label, its geometry etc.
         */
        export interface MindmapNode {
            /** The children of this node. */
            children: MindmapNode[];
            /** The parent of this node, or `null` if it has got no parent. */
            parent: MindmapNode | null;
            /** The label text of this node. */
            label: string;
            /** The unique ID of this node. */
            key?: string;
            /** The fill color of this node. */
            fill?: string;
            /** `true` if this node can be selected, or `false` otherwise. */
            selectable?: boolean;
        }

        /**
         * A drawn mindmap node (ellipse) or text label, with the properties the widget sets while dragging.
         */
        export type MindmapElement = import("raphael").RaphaelElement & {
            /** Whether the node was dragged, so that the click after the drag does not select the node. */
            dragged?: boolean;
            /** The horizontal position when the drag started. */
            ox?: number;
            /** The vertical position when the drag started. */
            oy?: number;
        };
    }
}
