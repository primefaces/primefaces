PrimeFaces.widget.SmartCam = PrimeFaces.widget.BaseWidget.extend({

    init: function(cfg) {
    	this._super(cfg);
        if(this.cfg.disabled) {
            return;
        }
        
        this.cfg.width = this.cfg.width || 320;
        this.cfg.height = this.cfg.height || 240;
        this.cfg.renderTimeout = this.cfg.renderTimeout || 50;
        
        if (!("autoStart" in this.cfg)) {
            this.cfg.autoStart = true;
        }

        if ("doDetection" in this.cfg) {
            this.cfg.doDetection = true;
        }
        
        if(this.cfg.doDetection) {
        	this.initializeDetectionModel();
        }
        
        if(this.cfg.imageHandler) {
        	this.imageHandler = this.cfg.imageHandler;
        } else {
        	this.imageHandler = function(video, canvasContext, detections) {
        		canvasContext.drawImage(video, 0, 0, video.width, video.height);
        	}
        }
        
        this.loop();
        
        if (this.cfg.autoStart) {
            this.play();
        }

    },
    
    play: function() {
        var div = document.getElementById(this.cfg.id);
        
        if(div) {
        	this.video = document.createElement("video");
        	this.video.width = this.cfg.width;
        	this.video.height = this.cfg.height;
        	this.canvas = document.createElement("canvas");
        	this.canvas.width = this.cfg.width;
        	this.canvas.height = this.cfg.height;
        	div.appendChild(this.canvas);
        	this.context = this.canvas.getContext('2d');
        	
        	$this = this;
        
	        if(navigator.mediaDevices && navigator.mediaDevices.getUserMedia) {
				navigator.mediaDevices.getUserMedia({ video: true }).then(function(stream) {
					$this.video.srcObject = stream;
					$this.video.play();
				});
			}
        }
    },
    
    stop: function() {
        this.context = null;
        if(this.canvas) {
	        var div = document.getElementById(this.cfg.id);
	        if(div) {
		        div.removeChild(this.canvas);
		        this.canvas = null;
	        }
        }
        if(this.video) {
	        this.video.srcObject.getTracks().forEach(function(track) {
	      	  track.stop();
	      	});
	        this.video = null;
        }
    },

    detect: async function() {
    	return await this.model.estimateFaces(this.video);
    },

    handleVideo: function() {
    	if(this.video && this.video.readyState && this.context) {
    		var detections = null;
    		if (this.modelInitialized) {
            	detectionsCall = this.detect();
            	detectionsCall.then(detections => this.imageHandler(this.video, this.context, detections));
            } else {
            	this.imageHandler(this.video, this.context, null);
            }
    	}
    },
    
    loop: function() {
    	var $this=this;
        this.imageRenderTimeout = setInterval(function(){
			$this.handleVideo();
        }, this.cfg.renderTimeout);
    },
    
    initializeDetectionModel: async function() {
    	this.model = await facemesh.load();
        //warm-up
    	this.model.estimateFaces(tf.zeros([640, 480, 3]));
    	this.modelInitialized = true;
    }

});