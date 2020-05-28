PrimeFaces.widget.SmartCam = PrimeFaces.widget.BaseWidget.extend({

    init: function(cfg) {
    	console.log(cfg);
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
    
    drawImage: function() {
    	this.context.drawImage(this.video, 0, 0, this.cfg.width, this.cfg.height);
    },
    
    loop: function() {
    	var $this=this;
        this.imageRenderTimeout = setInterval(function(){
    		if($this.video && $this.context) {
    			$this.drawImage();
            }
        }, this.renderTimeout);
    		
    }

});