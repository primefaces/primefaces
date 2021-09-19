/**
 * Namespace for the rangy jQuery UI Signature plugin.
 * 
 * Contains some additional types and interfaces required for the typings.
 * 
 * A jQuery UI plugin that captures or draws a signature. It requires the jQuery UI widget and mouse modules and needs
 * the excanvas.js add-in for older IE versions.
 * 
 * See http://keith-wood.name/signature.html
 */
declare namespace JQuerySignature {
    /**
     * A list of possible data types for storing a drawn signature.
     */
    export type SyncFormat = "JSON" | "SVG" | "PNG" | "JPEG";

    /**
     * Represents a two dimensional point with an x and a coordinate, in that order. 
     */
    export type Point = [number, number];

    /**
     * Represents a two dimensional line through the given points. 
     */
    export type Polyline = Point[];

    /**
     * A callback for when the signature changes.
     */
    export type OnChangeListener =
        /**
         * @param event The event that triggered the signature change.
         * @param ui An empty UI object.
         */
        (event: JQuery.TriggeredEvent, ui: Record<string, never>) => void;

    /**
     * Describes a signature comprised of several polylines.
     */
    export interface SignatureJson {
        /**
         * The polylines of the signature. 
         */
        lines: Polyline[];
    }

    /**
     * Optional settings that can be passed when creating a new signature instance to customize its behavior.
     */
    export interface SignatureSettings {
        /**
         * The minimum distance (pixels) of movement to start a drag operation.
         * 
         * ```javascript
         * $(selector).signature({distance: 5});
         * ```
         */
        distance: number;

        /**
         * The background colour of the signature area. Specify this as a six character hexadecimal value prefixed by a
         * hash (#), as a RGB triplet, or as a named colour.
         * 
         * ```javascript
         * $(selector).signature({background: "#0000ff"}); 
         * $(selector).signature({background: "rgb(0,0,255)"}); 
         * $(selector).signature({background: "blue"});
         * ```
         */
        background: string;

        /**
         * The colour of the signature lines. Specify this as a six character hexadecimal value prefixed by a hash (#),
         * as a RGB triplet, or as a named colour.
         * 
         * ```javascript
         * $(selector).signature({color: "#0000ff"}); 
         * $(selector).signature({color: "rgb(0,0,255)"}); 
         * $(selector).signature({color: "blue"});
         * ```
         */
        color: string;

        /**
         * The thickness of the lines used to draw the signature.
         */
        thickness: number;

        /**
         * Set to true to draw a horizontal guideline within the signature area.
         */
        guideline: boolean;

        /**
         * The colour of the guideline (if present). Specify this as a six character hexadecimal value prefixed by a
         * hash (#), as a RGB triplet, or as a named colour.
         */
        guidelineColor: string;

        /**
         * The offset (pixels) of the guideline (if present) from the bottom of the signature area.
         */
        guidelineOffset: number;

        /**
         * The indent (pixels) of the guideline (if present) from the sides of the signature area.
         */
        guidelineIndent: number;

        /**
         * The text to display in the signature area if the browser doesn"t support the canvas element.
         */
        notAvailable: string;

        /**
         * Set to true to disable the signature capture functionality. You can also use the disable and enable commands
         * for this.
         */
        disabled: boolean;

        /**
         * The element to keep synchronised with the JSON text for the signature. The value may be a string selector, a
         * DOM element, or a jQuery object.
         * 
         * ```javascript
         * $(selector).signature({syncField: "#jsonSignature"});
         * ```
         */
        syncField: null | string | HTMLElement | JQuery;

        /**
         * The output representation that is automatically generated into the syncField. PNG and JPEG output are
         * generated as data URIs.
         * 
         * @since 1.2.0
         */
        syncFormat: SyncFormat;

        /**
         * Set to `true` to apply styles in SVG using the style attribute, rather than using individual inline
         * attributes.
         * 
         * ```javascript
         * $(selector).signature({svgStyles: true});
         * ```
         * 
         * @since 1.2.0
         */
        svgStyles: boolean;

        /**
         * This function is called when the signature is changed: either by drawing a new line on it with the mouse, by
         * clearing it, or by re-drawing an entire signature. The function receives two parameters: the triggering event
         * and an empty UI object. You can also bind a handler to this event using its full name `signaturechange`.
         * 
         * ```javascript
         * $(selector).signature({ 
         *   change: (event, ui) => { 
         *     alert("The signature has changed"); 
         *   } 
         * }); 
         * 
         * $(selector).signature().on("signaturechange", (event, ui) => { 
         *   alert("The signature has changed"); 
         * });
         * ```
         */
        change: null | OnChangeListener;
    }

    /**
     * Static object for the jQuery UI Signature plugin, storing the default values.
     */
    export interface SignatureStatics {
        /**
         * The default settings to use with all signature instances. 
         */
        options: JQuerySignature.SignatureSettings;
    }

    /**
     * Interface for the namespace for static properties of jQuery plugins written by Keith Wood,
     * see http://keith-wood.name/
     */
    export interface KeithBWoodNamespace {
        /**
         * Namespace in the static jQuery object for the jQuery UI Signature plugin. 
         */
        signature: JQuerySignature.SignatureStatics;
    }    
}

interface JQuery {
    /**
     * Converts each input field in this selection into a signature input field
     * @param settings Settings for customizing the signature widget.
     * @return this jQuery instance for chaining.
     */
    signature(settings: Partial<JQuerySignature.SignatureSettings>): this;

    /**
     * Update the settings for the signature widgets.
     * @param method The method to be called on the signature widget.
     * @param settings The new settings for the signature widget.
     * @return this jQuery instance for chaining.
     */
    signature(method: "option", settings: Partial<JQuerySignature.SignatureSettings>): this;

    /**
     * Updates a particular setting for the signature instances.
     * @typeparam K Name of the setting to update.
     * @param method The method to be called on the signature widget.
     * @param name Name of the setting to update.
     * @param value The new value for the setting.
     * @return this jQuery instance for chaining.
     */
    signature<K extends keyof JQuerySignature.SignatureSettings>(method: "option", name: K, value: JQuerySignature.SignatureSettings[K]): this;

    /**
     * Retrieves one of the current settings for the first signature instance.
     * @typeparam K Name of the setting to retrieve.
     * @param method The method to be called on the signature widget.
     * @param name Name of the setting to retrieve.
     * @return The current value of the given setting.
     */
    signature<K extends keyof JQuerySignature.SignatureSettings>(method: "option", name: K): JQuerySignature.SignatureSettings[K];

    /**
     * Retrieves all of the current settings for the first signature instance.
     * @param method The method to be called on the signature widget.
     * @return The settings of the signature widget.
     */
    signature(method: "option"): JQuerySignature.SignatureSettings;

    /**
     * Enables the signature capture functionality.
     * @param method The method to be called on the signature widget.
     * @return this jQuery instance for chaining.
     */
    signature(method: "enable"): this;

    /**
     * Disables the signature capture functionality.
     * @param method The method to be called on the signature widget.
     * @return this jQuery instance for chaining.
     */
    signature(method: "disable"): this;

    /**
     * Removes the signature functionality.
     * @param method The method to be called on the signature widget.
     * @return this jQuery instance for chaining.
     */
    signature(method: "destroy"): this;

    /**
     * Erase any current signature within the div(s). This triggers the change event.
     * @param method The method to be called on the signature widget.
     * @return this jQuery instance for chaining.
     */
    signature(method: "clear"): this;

    /**
     * Determine whether the first signature area has any signature drawn within it.
     * @param method The method to be called on the signature widget.
     * @return `true` if no signature is currently drawn, or `false` otherwise.
     */
    signature(method: "isEmpty"): this;

    /**
     * Convert the first signature to a JSON string for transferral/storage. A sample follows for a signature with two
     * lines:
     * 
     * ```json
     * {
     *   "lines": [
     *     [
     *       [100, 100],
     *       [180, 50],
     *       [180, 150],
     *       [100, 100]
     *     ],
     *     [
     *       [140, 75],
     *       [100, 50],
     *       [100, 150],
     *       [140, 125]
     *     ]
     *   ]
     * }
     * ```
     * 
     * @param method The method to be called on the signature widget.
     * @return A JSON string with the line data of the signature.
     */
    signature(method: "toJSON"): string;

    /**
     * Convert the first signature to a SVG document for transferral/storage. A sample follows for a signature with
     * two lines:
     * 
     * ```xml
     * <?xml version="1.0"?>
     * <!DOCTYPE svg PUBLIC "-//W3C//DTD SVG 1.1//EN" "http://www.w3.org/Graphics/SVG/1.1/DTD/svg11.dtd">
     * <svg xmlns="http://www.w3.org/2000/svg" width="15cm" height="15cm">
     *   <g fill="#ffffff">
     *     <rect x="0" y="0" width="400" height="200"/> 
     *     <g fill="none" stroke="#000000" stroke-width="2">
     *       <polyline points="100,100 180,50 180,150 100,100"/>
     *       <polyline points="140,75 100,50 100,150 140,125"/>
     *     </g>
     *   </g>
     * </svg>
     * ```
     * 
     * @since 1.1.1
     * @param method The method to be called on the signature widget.
     * @return An SVG string with the line data of the signature.
     */
    signature(method: "toSVG"): string;

    /**
     * Convert the first signature to a data URI for transferral/storage:
     * 
     * ```
     * data:image/png;base64,iVBORw0KGgoAAAAN...AAAASUVORK5CYII=
     * ```
     * 
     * @since 1.2.0
     * @param method The method to be called on the signature widget.
     * @param type The image format to use. Defaults to `image/png`.
     * @return A string with the data URI of an image with the signature. 
     */
    signature(method: "toDataURL", type?: "image/png"): string;

    /**
     * Convert the first signature to a data URI for transferral and storage:
     * 
     * ```
     * data:image/jpeg;base64,iVBORw0KGgoAAAAN...AAAASUVORK5CYII=
     * ```
     * 
     * @since 1.2.0
     * @param method The method to be called on the signature widget.
     * @param type The image format to use. Defaults to `image/png`.
     * @param quality The image quality for JPEG images. A value between 0 and 1, default is `0.92`.
     * @return A string with the data URI of an image with the signature. 
     */
    signature(method: "toDataURL", type: "image/jpeg", quality?: number): string;

    /**
     * Redraws the signature from the supplied definition. This triggers the change event.
     * @param method The method to be called on the signature widget.
     * @param signature The signature data to draw. May be (a) a JSON object or its string representation as obtained
     * from the `toJSON` method); or (b) the SVG string as obtained from the `toSVG` method), or (c) the data URI
     * string as obtained from the `toDataURL` method.
     * @return this jQuery instance for chaining.
     */
    signature(method: "draw", signature: string | JQuerySignature.SignatureJson): this;
}

interface JQueryStatic {
    /**
     * Namespace in the static jQuery object for plugins written by Keith Wood, see http://keith-wood.name/
     */
    kbw: JQuerySignature.KeithBWoodNamespace;
}