import { core } from "./core.js";

/**
 * The class with functionality related to handling resources on the server, such as CSS and JavaScript files.
 */
export class Resources {
    /**
     * Base URL for PrimeFaces resources.
     */
    SCRIPT_URI: string | undefined = undefined;

    /**
     * Whether the Faces resources handler uses the extension mapping. When enabled,
     * Faces resource URLs get the `.xhtml` ending.
     */
    IS_EXTENSION_MAPPING: boolean | undefined = undefined;

    /**
     * When {@link IS_EXTENSION_MAPPING | extension mapping} is enabled, the extension for resource URLs,
     * e.g. `.xhtml`.
     */
    RESOURCE_URL_EXTENSION: string | undefined = undefined;

    /**
    * Builds a Faces resource URL for given resource.
    * 
    * ```javascript
    * getFacesResource("main.css", "pf", "4.2.0") // => "https://www.primefaces.org/showcase/jakarta.faces.resource/main.css.xhtml?ln=pf&v=4.2.0"
    * ```
    *
    * @param name The name of the resource, such as `primefaces.js`.
    * @param library The library of the resource, such as `primefaces`.
    * @param version The version of the library, such as `5.1`.
    * @return The Faces resource URL for loading the resource.
    */
    getFacesResource(name: string, library: string, version: string): string {
        // just get sure - name shouldn't start with a slash
        if (name.indexOf('/') === 0) {
            name = name.substring(1, name.length);
        }
        
        // find any JS served Faces resource
        var scriptURI = this.getResourceScriptURI();
        var scriptName = this.getResourceScriptName(scriptURI);
        
        // replace core.js with our custom name
        scriptURI = scriptURI.replace(scriptName, name);
        
        // find the library like ln=primefaces
        var libraryRegex = /[?&]([^&=]*)ln=(.*?)(&|$)/;
        
        // find library to replace e.g. 'ln=primefaces'
        var currentLibraryName = 'ln=' + libraryRegex.exec(scriptURI)?.[2];
        
        // In a portlet environment, url parameters may be namespaced.
        var namespace = '';
        var urlParametersAreNamespaced = !(scriptURI.indexOf('?' + currentLibraryName) > -1 || 
        scriptURI.indexOf('&'+ currentLibraryName) > -1);
        
        if (urlParametersAreNamespaced) {
            namespace = new RegExp('[?&]([^&=]+)' + currentLibraryName + '($|&)').exec(scriptURI)?.[1] ?? "";
        }
        
        // If the parameters are namespaced, the namespace must be included
        // when replacing parameters.
        scriptURI = scriptURI.replace(namespace + currentLibraryName, namespace + 'ln=' + library);
        
        if (version) {
            var extractedVersion = new RegExp('[?&]' + namespace + 'v=([^&]*)').exec(scriptURI)?.[1] ?? "";
            scriptURI = scriptURI.replace(namespace + 'v=' + extractedVersion, namespace + 'v=' + version);
        }
        
        var prefix = window.location.protocol + '//' + window.location.host;
        return scriptURI.indexOf(prefix) >= 0 ? scriptURI : prefix + scriptURI;
    }
    
    /**
    * Checks if the FacesServlet is mapped with an extension mapping. Common extension mapping are for example:
    * 
    * - .faces
    * - .xhtml
    * 
    * @return `true` if the FacesServlet is mapped with an extension mapping, `false` otherwise.
    */
    isExtensionMapping(): boolean {
        if (this.IS_EXTENSION_MAPPING === undefined) {
            var scriptURI = this.getResourceScriptURI();
            var scriptName = this.getResourceScriptName(scriptURI);
            this.IS_EXTENSION_MAPPING = scriptURI.charAt(scriptURI.indexOf(scriptName) + scriptName.length) === '.';
        }
        
        return this.IS_EXTENSION_MAPPING;
    }
    
    /**
    * Finds the URL extension of currently included resources, such as `faces` or `xhtml`.
    * 
    * This should only be used if extensions mapping is used, see `PrimeFaces.isExtensionMapping`.
    * 
    * @return The URL extension.
    */
    getResourceUrlExtension(): string {
        if (this.RESOURCE_URL_EXTENSION === undefined) {
            var scriptURI = this.getResourceScriptURI();
            var scriptName = this.getResourceScriptName(scriptURI);
            this.RESOURCE_URL_EXTENSION = RegExp(scriptName + '.([^?]*)').exec(scriptURI)?.[1] ?? "";
        }
        
        return this.RESOURCE_URL_EXTENSION;
    }
    
    /**
    * Given a URI, find the name of the script, such as `primefaces-extensions.js`.
    * 
    * @param scriptURI The URI of a script
    * @return The name of the script.
    */
    getResourceScriptName(scriptURI: string): string {
        // find script...normal is '/core.js' and portlets are '=core.js'
        var scriptRegex = new RegExp('\\/?' + core.RESOURCE_IDENTIFIER + '(\\/|=)(.*?)\\.js');
        return scriptRegex.exec(scriptURI)?.[2] + '.js';
    }
    
    /**
    * Gets the resource URI of the first Javascript JS file served as a Faces resource.
    * 
    * @return The first JavasScript resource URI.
    */
    getResourceScriptURI(): string {
        if (this.SCRIPT_URI === undefined) {
            const findScriptWithVersionParam = (scripts: JQuery) => {
                for (const script of scripts) {
                    var src = $(script).attr('src');
                    if (src && src.indexOf('v=') !== -1) {
                        this.SCRIPT_URI = src;
                        break;
                    }
                }
            }

            // normal '/showcase/jakarta.faces.resource/jquery/jquery.js.xhtml?ln=primefaces&v=13.0.5'
            findScriptWithVersionParam($('script[src*="/' + core.RESOURCE_IDENTIFIER + '/"]'));
            
            // portlet 'jakarta.faces.resource=jquery/jquery.js.xhtml?ln=primefaces&v=13.0.5'
            if (!this.SCRIPT_URI) {
                findScriptWithVersionParam($('script[src*="' + core.RESOURCE_IDENTIFIER + '="]'));
            }

            if (!this.SCRIPT_URI) {
                this.SCRIPT_URI = "";
            }
        }

        return this.SCRIPT_URI;
    }
}

/**
 * The object with functionality related to handling resources on the server, such as CSS and JavaScript files.
 */
export const resources: Resources = new Resources();
