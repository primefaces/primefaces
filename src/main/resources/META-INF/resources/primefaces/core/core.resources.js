if (!PrimeFaces.resources) {

   /**
    * The object with functionality related to handling resources on the server, such as CSS and JavaScript files.
    * 
    * @namespace
    */
    PrimeFaces.resources = {
          /**
           * Builds a JSF resource URL for given resource.
           * 
           * ```javascript
           * getFacesResource("main.css", "pf", "4.2.0") // => "https://www.primefaces.org/showcase/javax.faces.resource/main.css.xhtml?ln=pf&v=4.2.0"
           * ```
           *
           * @param {string} name The name of the resource, such as `primefaces.js`.
           * @param {string} library The library of the resource, such as `primefaces`.
           * @param {string} version The version of the library, such as `5.1`.
           * @return {string} The JSF resource URL for loading the resource.
           */
          getFacesResource : function(name, library, version) {
             // just get sure - name shoudln't start with a slash
             if (name.indexOf('/') === 0) {
                name = name.substring(1, name.length);
             }

             // find any JS served JSF resource
             var scriptURI = PrimeFaces.resources.getResourceScriptURI();
             var scriptName = PrimeFaces.resources.getResourceScriptName(scriptURI);

             // replace core.js with our custom name
             scriptURI = scriptURI.replace(scriptName, name);

             // find the library like ln=primefaces
             var libraryRegex = new RegExp('[?&]([^&=]*)ln=(.*?)(&|$)');

             // find library to replace e.g. 'ln=primefaces'
             var currentLibraryName = 'ln=' + libraryRegex.exec(scriptURI)[2];

             // In a portlet environment, url parameters may be namespaced.
             var namespace = '';
             var urlParametersAreNamespaced = !(scriptURI.indexOf('?' + currentLibraryName) > -1 || 
                   scriptURI.indexOf('&'+ currentLibraryName) > -1);

             if (urlParametersAreNamespaced) {
                namespace = new RegExp('[?&]([^&=]+)' + currentLibraryName + '($|&)').exec(scriptURI)[1];
             }

             // If the parameters are namespaced, the namespace must be included
             // when replacing parameters.
             scriptURI = scriptURI.replace(namespace + currentLibraryName, namespace + 'ln=' + library);

             if (version) {
                var extractedVersion = new RegExp('[?&]' + namespace + 'v=([^&]*)').exec(scriptURI)[1];
                scriptURI = scriptURI.replace(namespace + 'v=' + extractedVersion, namespace + 'v=' + version);
             }

             var prefix = window.location.protocol + '//' + window.location.host;
             return scriptURI.indexOf(prefix) >= 0 ? scriptURI : prefix + scriptURI;
          },

          /**
           * Checks if the FacesServlet is mapped with an extension mapping. Common extension mapping are for example:
           * 
           * - .jsf
           * - .xhtml
           * 
           * @return {boolean} `true` if the FacesServlet is mapped with an extension mapping, `false` otherwise.
           */
          isExtensionMapping : function() {
             if (!PrimeFaces.resources.IS_EXTENSION_MAPPING) {
                var scriptURI = PrimeFaces.resources.getResourceScriptURI();
                var scriptName = PrimeFaces.resources.getResourceScriptName(scriptURI);
                PrimeFaces.resources.IS_EXTENSION_MAPPING = scriptURI.charAt(scriptURI.indexOf(scriptName) + scriptName.length) === '.';
             }

             return PrimeFaces.IS_EXTENSION_MAPPING;
          },

          /**
           * Finds the URL extension of currently included resources, such as `jsf` or `xhtml`.
           * 
           * This should only be used if extensions mapping is used, see `PrimeFaces.isExtensionMapping`.
           * 
           * @return {string} The URL extension.
           */
          getResourceUrlExtension : function() {
             if (!PrimeFaces.resources.RESOURCE_URL_EXTENSION) {
                var scriptURI = PrimeFaces.resources.getResourceScriptURI();
                var scriptName = PrimeFaces.resources.getResourceScriptName(scriptURI);
                PrimeFaces.resources.RESOURCE_URL_EXTENSION = RegExp(scriptName + '.([^?]*)').exec(scriptURI)[1];
             }

             return PrimeFaces.resources.RESOURCE_URL_EXTENSION;
          },

          /**
           * Given a URI, find the name of the script, such as `primefaces-extensions.js`.
           * 
           * @param {string} scriptURI The URI of a script
           * @return {string} The name of the script.
           */
          getResourceScriptName : function(scriptURI) {
             // find script...normal is '/core.js' and portlets are '=core.js'
             var scriptRegex = new RegExp('\\/?' + PrimeFaces.RESOURCE_IDENTIFIER + '(\\/|=)(.*?)\\.js');
             return scriptRegex.exec(scriptURI)[2] + '.js';
          },

          /**
           * Gets the resource URI of the first Javascript JS file served as a JSF resource.
           * 
           * @return {string} The first JavasScript resource URI.
           */
          getResourceScriptURI : function() {
             if (!PrimeFaces.resources.SCRIPT_URI) {
                PrimeFaces.resources.SCRIPT_URI =
                   $('script[src*="/' + PrimeFaces.RESOURCE_IDENTIFIER + '/"]').first().attr('src');

                // portlet
                if (!PrimeFaces.resources.SCRIPT_URI) {
                   PrimeFaces.resources.SCRIPT_URI = $('script[src*="' + PrimeFaces.RESOURCE_IDENTIFIER + '="]').first().attr('src');
                }
             }
             return PrimeFaces.resources.SCRIPT_URI;
          }
    };

}