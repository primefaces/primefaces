/*
 * eros@recording.it
 * jqprint 0.3
 */
(function($) {
    var opt;

    $.fn.jqprint = function (options) {
        opt = $.extend({}, $.fn.jqprint.defaults, options);

        var $element = (this instanceof jQuery) ? this : $(this);

        if (opt.operaSupport && $.browser.opera)
        {
            var tab = window.open("","jqPrint-preview");
            tab.document.open();

            var doc = tab.document;
        }
        else
        {
            var $iframe = $("<iframe  />");

            if (!opt.debug) { $iframe.css({ position: "absolute", width: "0px", height: "0px", left: "-600px", top: "-600px" }); }

            $iframe.appendTo("body");
            var doc = $iframe[0].contentWindow.document;
        }

        if (opt.importCSS)
        {
            if ($("link[media=print]").length > 0)
            {
                $("link[media=print]").each( function() {
                    doc.write("<link type='text/css' rel='stylesheet' href='" + PrimeFaces.escapeHTML($(this).attr("href")) + "' media='print' />");
                });
            }
            else
            {
                $("link").each( function() {
                    doc.write("<link type='text/css' rel='stylesheet' href='" + PrimeFaces.escapeHTML($(this).attr("href")) + "' />");
                });
            }
        }

        if (opt.printContainer) { doc.write($element.outer()); }
        else { $element.each( function() { doc.write($(this).html()); }); }

        doc.close();

        (opt.operaSupport && $.browser.opera ? tab : $iframe[0].contentWindow).focus();
        setTimeout(function() { 
            var frameWindow = (opt.operaSupport && $.browser.opera ? tab : $iframe[0].contentWindow);
            try {
                // fix for IE; http://stackoverflow.com/a/21336448/937891
                if(!frameWindow.document.execCommand('print', false, null)) { 
                    frameWindow.print();
                }
            }
            catch(e) {
                frameWindow.print();
            }
            if (tab) { tab.close(); } 
        }, 1000);
    }

    $.fn.jqprint.defaults = {
		debug: false,
		importCSS: true,
		printContainer: true,
		operaSupport: true
	};

    // Thanks to 9__, found at http://users.livejournal.com/9__/380664.html
    jQuery.fn.outer = function() {
      return $($('<div></div>').html(this.clone())).html();
    }
})(jQuery);