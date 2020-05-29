PrimeFaces.widget.SmartCam = PrimeFaces.widget.BaseWidget.extend({

	defaultLoad: async function() {
		return await facemesh.load();
	},
	
	defaultPredict: async function(model, input) {
		return await this.model.estimateFaces(input);
	},
	
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
        
        this.modelLoader = this.cfg.model || this.defaultLoad;
        this.doPrediction = this.cfg.predict || this.defaultPredict;

        if(this.modelLoader && this.doPrediction) {
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
    	return await this.doPrediction(this.model, this.video);
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
    	$this = this;
    	this.modelLoader.call().then(model => {
    		$this.model = model;
    		$this.doPrediction(model, tf.zeros([this.cfg.width, this.cfg.height, 3], dtype='int32'));
    		$this.modelInitialized = true;
    	});
    	
    }

});