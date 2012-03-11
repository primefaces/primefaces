/**
* jQuery webcam
* Copyright (c) 2010, Robert Eisele (robert@xarg.org)
* Dual licensed under the MIT or GPL Version 2 licenses.
* Date: 09/12/2010
*
* @author Robert Eisele
* @version 1.0
*
* @see http://www.xarg.org/project/jquery-webcam-plugin/
**/

(function ($) {

    var webcam = {

	extern: null, // external select token to support jQuery dialogs
	append: true, // append object instead of overwriting

	width: 320,
	height: 240,

	mode: "callback", // callback | save | stream

	swffile: "jscam.swf",
	quality: 85,

	debug:	    function () {},
	onCapture:  function () {},
	onTick:	    function () {},
	onSave:	    function () {},
	onLoad:	    function () {}
    };

    window.webcam = webcam;

    $.fn.webcam = function(options) {

	if (typeof options === "object") {
	    for (var ndx in webcam) {
		if (options[ndx] !== undefined) {
		    webcam[ndx] = options[ndx];
		}
	    }
	}

	var source = '<object id="XwebcamXobjectX" type="application/x-shockwave-flash" data="'+webcam.swffile+'" width="'+webcam.width+'" height="'+webcam.height+'"><param name="movie" value="'+webcam.swffile+'" /><param name="FlashVars" value="mode='+webcam.mode+'&amp;quality='+webcam.quality+'" /><param name="allowScriptAccess" value="always" /></object>';

	if (null !== webcam.extern) {
	    $(webcam.extern)[webcam.append ? "append" : "html"](source);
	} else {
	    this[webcam.append ? "append" : "html"](source);
	}

	(_register = function(run) {

	    var cam = document.getElementById('XwebcamXobjectX');

	    if (cam.capture !== undefined) {

		/* Simple callback methods are not allowed :-/ */
		webcam.capture = function(x) {
		    try {
			return cam.capture(x);
		    } catch(e) {}
		}
		webcam.save = function(x) {
		    try {
			return cam.save(x);
		    } catch(e) {}
		}
		webcam.setCamera = function(x) {
		    try {
			return cam.setCamera(x);
		    } catch(e) {}
		}
		webcam.getCameraList = function() {
		    try {
			return cam.getCameraList();
		    } catch(e) {}
		}

		webcam.onLoad();
	    } else if (0 == run) {
		webcam.debug("error", "Flash movie not yet registered!");
	    } else {
		/* Flash interface not ready yet */
		window.setTimeout(_register, 1000 * (4 - run), run - 1);
	    }
	})(3);
    }

})(jQuery);

/**
* PrimeFaces PhotoCam Widget
*/
PrimeFaces.widget.PhotoCam = PrimeFaces.widget.BaseWidget.extend({
    
    init: function(cfg) {
        this._super(cfg);
        
        this.form = this.jq.parents('form:first');
        this.url = this.form.attr('action');
        this.canvas = document.createElement("canvas");
        this.canvas.setAttribute('width', 320);
        this.canvas.setAttribute('height', 240);
        this.ctx = this.canvas.getContext("2d"),
        this.image = this.ctx.getImageData(0, 0, 320, 240);
        this.pos = 0;
        var _self = this;

        this.jq.webcam({
            width: 320,
            height: 240,
            mode: "callback",
            swffile: this.cfg.camera,
            onSave: function(data) {
                _self.save(data)
            },
            onCapture: function () {
                webcam.save();
            },
            debug: function (type, string) {
                console.log(type + ": " + string);
            }
        });
    },
    
    save: function(data) {
        var col = data.split(";");

        for(var i = 0; i < 320; i++) {
                var tmp = parseInt(col[i]);
                this.image.data[this.pos + 0] = (tmp >> 16) & 0xff;
                this.image.data[this.pos + 1] = (tmp >> 8) & 0xff;
                this.image.data[this.pos + 2] = tmp & 0xff;
                this.image.data[this.pos + 3] = 0xff;
                this.pos+= 4;
        }

        if(this.pos >= 4 * 320 * 240) {
            this.ctx.putImageData(this.image, 0, 0);

            $.ajax({
                url:this.url,
                type : "POST",
                cache : false,
                dataType : "xml",
                data: this.createPostData(),
                success : function(data, status, xhr) {
                    PrimeFaces.ajax.AjaxResponse.call(this, data, status, xhr);
                }
            });

            this.pos = 0;
        }
    },
    
    createPostData: function() {
        var data = this.form.serialize(),
        params = {},
        process = this.cfg.process ? this.cfg.process + ' ' + this.id : this.id;

        params[PrimeFaces.PARTIAL_REQUEST_PARAM] = true;
        params[PrimeFaces.PARTIAL_PROCESS_PARAM] = process;
        params[PrimeFaces.PARTIAL_SOURCE_PARAM] = this.id;
        params[this.id] = this.canvas.toDataURL('image/png');

        if(this.cfg.update) {
            params[PrimeFaces.PARTIAL_UPDATE_PARAM] = this.cfg.update;
        }

        data = data + '&' + $.param(params);

        return data; 
    },
    
    capture: function() {
        webcam.capture();
    }
    
});