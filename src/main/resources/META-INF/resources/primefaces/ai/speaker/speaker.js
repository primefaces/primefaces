PrimeFaces.widget.Speaker = PrimeFaces.widget.BaseWidget.extend({
	
	init: function(cfg) {
    	this._super(cfg);
        if(this.cfg.disabled) {
            return;
        }
        
        this.cfg.voice = this.cfg.voice || 'en-US';
        this.cfg.pitch = this.cfg.pitch || 1;
        this.cfg.rate = this.cfg.rate || 1;
        
	},
	
	speak: function(txt, voice){
		
        if (!txt) {
        	return;
        }
        
        var synth = window.speechSynthesis;
        
        if (!synth) {
        	return;
        }
        if (synth.speaking) {
        	return;
        }
        
        var speech = new SpeechSynthesisUtterance(txt);
        
        speech.onend = function (event) {
            console.log('SpeechSynthesisUtterance.onend');
        }
        
        speech.onerror = function (event) {
            console.error('SpeechSynthesisUtterance.onerror');
        }
        
        speech.voice = voice;
        speech.pitch = this.cfg.pitch;
        speech.rate = this.cfg.rate;
        
        synth.speak(speech);
        
    },
    
    getVoices: function() {
    	return new Promise(
	        function (resolve, reject) {
	            var synth = window.speechSynthesis;
	            var call = setInterval(() => {
	                if (synth.getVoices().length !== 0) {
	                    resolve(synth.getVoices());
	                    clearInterval(call);
	                }
	            }, 10);
	        }
	    );
    },
	
	getVoice: function(voices, identifier) {
		var result = null;
		if(identifier && voices) {
			var i = 0, size = voices.length;
    		while(!result && i < size) {
    			var voice = voices[i];
    			if(voice.name == identifier || voice.lang == identifier) {
    				result = voice;
    			}
    			i++;
    		}
		}
		return result;
	}

});