PrimeFaces.widget.SpeechRecognition = PrimeFaces.widget.BaseWidget.extend({
	
	langs:
		[
		 ['Afrikaans',       [['af-ZA']]],
		 ['Bahasa Indonesia',[['id-ID']]],
		 ['Bahasa Melayu',   [['ms-MY']]],
		 ['Català',          [['ca-ES']]],
		 ['Čeština',         [['cs-CZ']]],
		 ['Deutsch',         [['de-DE']]],
		 ['English',         [['en-AU', 'Australia'],
		                     ['en-CA', 'Canada'],
		                     ['en-IN', 'India'],
		                     ['en-NZ', 'New Zealand'],
		                     ['en-ZA', 'South Africa'],
		                     ['en-GB', 'United Kingdom'],
		                     ['en-US', 'United States']]],
		 ['Español',         [['es-AR', 'Argentina'],
		                     ['es-BO', 'Bolivia'],
		                     ['es-CL', 'Chile'],
		                     ['es-CO', 'Colombia'],
		                     ['es-CR', 'Costa Rica'],
		                     ['es-EC', 'Ecuador'],
		                     ['es-SV', 'El Salvador'],
		                     ['es-ES', 'España'],
		                     ['es-US', 'Estados Unidos'],
		                     ['es-GT', 'Guatemala'],
		                     ['es-HN', 'Honduras'],
		                     ['es-MX', 'México'],
		                     ['es-NI', 'Nicaragua'],
		                     ['es-PA', 'Panamá'],
		                     ['es-PY', 'Paraguay'],
		                     ['es-PE', 'Perú'],
		                     ['es-PR', 'Puerto Rico'],
		                     ['es-DO', 'República Dominicana'],
		                     ['es-UY', 'Uruguay'],
		                     ['es-VE', 'Venezuela']]],
		 ['Euskara',         [['eu-ES']]],
		 ['Français',        [['fr-FR']]],
		 ['Galego',          [['gl-ES']]],
		 ['Hrvatski',        [['hr_HR']]],
		 ['IsiZulu',         [['zu-ZA']]],
		 ['Íslenska',        [['is-IS']]],
		 ['Italiano',        [['it-IT', 'Italia'],
		                     ['it-CH', 'Svizzera']]],
		 ['Magyar',          [['hu-HU']]],
		 ['Nederlands',      [['nl-NL']]],
		 ['Norsk bokmål',    [['nb-NO']]],
		 ['Polski',          [['pl-PL']]],
		 ['Português',       [['pt-BR', 'Brasil'],
		                     ['pt-PT', 'Portugal']]],
		 ['Română',          [['ro-RO']]],
		 ['Slovenčina',      [['sk-SK']]],
		 ['Suomi',           [['fi-FI']]],
		 ['Svenska',         [['sv-SE']]],
		 ['Türkçe',          [['tr-TR']]],
		 ['български',       [['bg-BG']]],
		 ['Pусский',         [['ru-RU']]],
		 ['Српски',          [['sr-RS']]],
		 ['한국어',            [['ko-KR']]],
		 ['中文',             [['cmn-Hans-CN', '普通话 (中国大陆)'],
		                     ['cmn-Hans-HK', '普通话 (香港)'],
		                     ['cmn-Hant-TW', '中文 (台灣)'],
		                     ['yue-Hant-HK', '粵語 (香港)']]],
		 ['日本語',           [['ja-JP']]],
		 ['Lingua latīna',   [['la']]]
	   ],

	init: function(cfg) {
    	this._super(cfg);
        if(this.cfg.disabled) {
            return;
        }
        
        this.cfg.language = this.cfg.language || 'en-US';
        
        var SpeechRecognition = SpeechRecognition || webkitSpeechRecognition;
        
        if (SpeechRecognition) {
        	this.recognition = new SpeechRecognition();
        	this.recognition.continuous = true;
        	this.recognition.lang = this.cfg.language;
            
            if(this.cfg.speechHandler) {
            	$this = this;
            	this.recognition.onresult = function(event) {
            		var speech = event.results;
            		console.log("onresult", speech);
            		$this.cfg.speechHandler(speech);
	            }
            }

            if(this.cfg.speechErrorHandler) {
            	$this = this;
            	this.recognition.onerror = function(event) {
            		console.log("onerror", event);
            		$this.cfg.speechErrorHandler(event.error);
	            }
            }
            
        } else {
        	console.log("browser doesn't support SpeechRecognition");
        }

    },
    
    start: function() {
        
        if(this.recognition) {
        	this.recognition.start();
        }
        
    },
    
    stop: function() {
        
        if(this.recognition) {
        	this.recognition.stop();
        }
        
    },
    
    languages: function() {
    	var result = new Array();
    	this.langs.forEach(elem => result.push(elem[0]));
    	return result;
    },
    
    langVariations: function(languageIndex) {
    	return this.langs[languageIndex][1];
    },
    
    setLanguage(lang) {
    	this.recognition.lang = lang;
    }

});