/*!
 * jQuery Browser Plugin 0.2.0
 * https://github.com/gabceb/jquery-browser-plugin
 *
 * Original jquery-browser code Copyright 2005, 2015 jQuery Foundation, Inc. and other contributors
 * http://jquery.org/license
 *
 * Modifications Copyright 2015 Gabriel Cebrian
 * https://github.com/gabceb
 *
 * Released under the MIT license
 *
 * Date: 12-05-2025
 */
/*global window: false */

(function(factory) {
  if (typeof define === 'function' && define.amd) {
    // AMD. Register as an anonymous module.
    define(['jquery'], function($) {
      return factory($);
    });
  } else if (typeof module === 'object' && typeof module.exports === 'object') {
    // Node-like environment
    module.exports = factory(require('jquery'));
  } else {
    // Browser globals
    factory(window.jQuery);
  }
}(function(jQuery) {
  "use strict";

  function uaMatch(ua) {
    // If an UA is not provided, default to the current browser UA.
    if (ua === undefined) {
      ua = window.navigator.userAgent;
    }
    ua = ua.toLowerCase();

    var match =
      // NEW - Chromium Edge first
      /(edg|edga|edgios)\/([\w.]+)/.exec(ua) ||

      // Legacy Edge (EdgeHTML)
      /(edge)\/([\w.]+)/.exec(ua) ||

      /(opr)[\/]([\w.]+)/.exec(ua) ||
      /(chrome)[ \/]([\w.]+)/.exec(ua) ||
      /(iemobile)[\/]([\w.]+)/.exec(ua) ||
      /(version)(applewebkit)[ \/]([\w.]+).*(safari)[ \/]([\w.]+)/.exec(ua) ||
      /(webkit)[ \/]([\w.]+).*(version)[ \/]([\w.]+).*(safari)[ \/]([\w.]+)/.exec(ua) ||
      /(webkit)[ \/]([\w.]+)/.exec(ua) ||
      /(opera)(?:.*version|)[ \/]([\w.]+)/.exec(ua) ||
      /(msie) ([\w.]+)/.exec(ua) ||
      ua.indexOf("trident") >= 0 && /(rv)(?::| )([\w.]+)/.exec(ua) ||
      ua.indexOf("compatible") < 0 && /(mozilla)(?:.*? rv:([\w.]+)|)/.exec(ua) || [];

    var platform_match =
      /(ipad)/.exec(ua) ||
      /(ipod)/.exec(ua) ||
      /(windows phone)/.exec(ua) ||
      /(iphone)/.exec(ua) ||
      /(kindle)/.exec(ua) ||
      /(silk)/.exec(ua) ||
      /(android)/.exec(ua) ||
      /(win)/.exec(ua) ||
      /(mac)/.exec(ua) ||
      /(linux)/.exec(ua) ||
      /(cros)/.exec(ua) ||
      /(playbook)/.exec(ua) ||
      /(bb)/.exec(ua) ||
      /(blackberry)/.exec(ua) || [];

    var browser = {},

      // Normalize new Chromium Edge naming
      browserName =
      match[1] === "edg" ||
      match[1] === "edga" ||
      match[1] === "edgios" ?
      "msedge" :
      match[5] || match[3] || match[1] || "",

      matched = {
        browser: browserName,
        version: match[2] || match[4] || "0",
        versionNumber: match[4] || match[2] || "0",
        platform: platform_match[0] || ""
      };

    if (matched.browser) {
      browser[matched.browser] = true;
      browser.version = matched.version;
      browser.versionNumber = parseInt(matched.versionNumber, 10);
    }

    if (matched.platform) {
      browser[matched.platform] = true;
    }

    // iOS platform detection (iphone, ipad, ipod)
    if (browser.ipad || browser.iphone || browser.ipod) {
      browser.ios = true;
    }

    // These are all considered mobile platforms, meaning they run a mobile browser
    if (browser.android || browser.bb || browser.blackberry || browser.ipad || browser.iphone ||
      browser.ipod || browser.kindle || browser.playbook || browser.silk || browser["windows phone"]) {
      browser.mobile = true;
    }

    // These are all considered desktop platforms
    if (browser.cros || browser.mac || browser.linux || browser.win) {
      browser.desktop = true;
    }

    // Chrome, Opera 15+ and Safari are webkit based browsers
    if (browser.chrome || browser.opr || browser.safari || browser.msedge) {
      browser.webkit = true;
    }

    // IE11 has a new token so we will assign it msie to avoid breaking changes
    if (browser.rv || browser.iemobile) {
      var ie = "msie";

      matched.browser = ie;
      browser[ie] = true;
    }

    // Legacy Edge is officially known as Microsoft Edge, so rewrite the key
    if (browser.edge) {
      delete browser.edge;
      var msedge_legacy = "msedge";
      matched.browser = msedge_legacy;
      browser[msedge_legacy] = true;
    }

    // Assign name & platform
    browser.name = matched.browser;
    browser.platform = matched.platform;

    return browser;
  }


  // Run the matching process, also assign the function to the returned object
  // for manual, jQuery-free use if desired
  window.jQBrowser = uaMatch(window.navigator.userAgent);
  window.jQBrowser.uaMatch = uaMatch;

  // Only assign to jQuery.browser if jQuery is loaded
  if (jQuery) {
    jQuery.browser = window.jQBrowser;
  }

  return window.jQBrowser;
}));
