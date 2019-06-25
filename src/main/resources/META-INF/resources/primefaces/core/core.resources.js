if (!PrimeFaces.resources) {

    PrimeFaces.resources = {

          /**
           * Builds a resource URL for given parameters.
           *
           * @param {string} name The name of the resource. For example: primefaces.js
           * @param {string} library The library of the resource. For example: primefaces
           * @param {string} version The version of the library. For example: 5.1
           * @returns {string} The resource URL.
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
           * Checks if the FacesServlet is mapped with extension mapping. For example:
           * .jsf/.xhtml.
           * 
           * @returns {boolean} If mapped with extension mapping.
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
           * Gets the URL extensions of current included resources. For example: jsf or
           * xhtml. This should only be used if extensions mapping is used.
           * 
           * @returns {string} The URL extension.
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
           * For a URI parses out the name of the script like primefaces-extensions.js
           * 
           * @param scriptURI the URI of the script
           * @returns {string} The script name.
           */
          getResourceScriptName : function(scriptURI) {
             // find script...normal is '/core.js' and portlets are '=core.js'
             var scriptRegex = new RegExp('\\/?' + PrimeFaces.RESOURCE_IDENTIFIER + '(\\/|=)(.*?)\\.js');
             return scriptRegex.exec(scriptURI)[2] + '.js';
          },

          /**
           * Gets the resource URI of the first Javascript JS file served as a JSF resource.
           * 
           * @returns {string} The resource URI.
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