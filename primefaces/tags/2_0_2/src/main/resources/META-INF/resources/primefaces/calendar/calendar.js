PrimeFaces.widget.Calendar = function(id, container, cfg) {
	PrimeFaces.widget.Calendar.superclass.constructor.call(this, id, container, cfg);
	
	this.initialize();
}

PrimeFaces.widget.CalendarGroup = function(id, container, cfg) {
	PrimeFaces.widget.CalendarGroup.superclass.constructor.call(this, id, container, cfg);
	
	this.initialize();
}

PrimeFaces.widget.CalendarExtensions = {
		
	initialize : function() {
		this.selectEvent.subscribe(this.handleSelectEvent, this, true);
		this.deselectEvent.subscribe(this.handleSelectEvent, this, true);
		
		this.applyLocale();
		
		if(this.isPopup()) {
			this.setupPopupDialog();
		} else {
			this.render();
		}
	},

	handleSelectEvent : function(type, args) {
		if(!this.cfg.getProperty('MULTI_SELECT')) {
			var selectedDate = this.getSelectedDates()[0];
			
			this.setInputValue(this.formatDate(selectedDate));
		} else {
			var selectedDates = new Array(this.getSelectedDates().length);
			
			for(var i=0; i < selectedDates.length; i++) {
				selectedDates[i] = this.formatDate(this.getSelectedDates()[i]);
			}
			
			this.setInputValue(selectedDates.join(','));
		}
		
		if(this.cfg.initialConfig.popup && !this.cfg.getProperty('MULTI_SELECT'))
			this.cfg.initialConfig.popup.hide();
		
		if(this.isAjaxSelectionEnabled()) {
			var params = {};
			params[PrimeFaces.PARTIAL_PROCESS_PARAM] = this.id;
			
			if(this.cfg.initialConfig.onselectupdate) {
				params[PrimeFaces.PARTIAL_UPDATE_PARAM] = this.cfg.initialConfig.onselectupdate;
			}
			
			PrimeFaces.ajax.AjaxRequest(this.cfg.initialConfig.actionurl, {
					formId:this.cfg.initialConfig.formid,
					global:true
				},
				params);
		}
	},
	
	formatDate : function(date) {
		var year = date.getFullYear(),
		month = date.getMonth() + 1,
		day = date.getDate();
		
		if(day < 10) 
			day = '0' + day;
		if(month < 10)
			month = '0' + month;
		
		var fieldValues = [];
		fieldValues[this.cfg.getProperty('MDY_DAY_POSITION') - 1] = day;
		fieldValues[this.cfg.getProperty('MDY_MONTH_POSITION') - 1] = month;
		fieldValues[this.cfg.getProperty('MDY_YEAR_POSITION') - 1] = year;
		
		return fieldValues.join(this.cfg.getProperty('DATE_FIELD_DELIMITER'));
	},

	setupPopupDialog : function() {
		YAHOO.util.Event.addListener(this.id + '_popupButton', 'click', function(e){
			if(!this.rendered) {
				this.render();
			} else {
				this.rendered = true;
			}

			this.show();
			this.cfg.initialConfig.popup.show();
		}, this, true);
		
		this.beforeHideEvent.subscribe(function(e) {
			this.cfg.initialConfig.popup.hide();
		}, this, true);
		
	    YAHOO.util.Event.on(document, 'click', function(e) {
	        var el = YAHOO.util.Event.getTarget(e),
	        popupEl = this.getPopup().element,
	        showImg = YAHOO.util.Dom.get(this.id + '_popupButton');
	        
	        if(el != popupEl && !YAHOO.util.Dom.isAncestor(popupEl, el) && el != showImg && !YAHOO.util.Dom.isAncestor(showImg, el)) {
	        	this.getPopup().hide();
	        }
	    }, this, true);
	},
	
	applyLocale : function() {
		//L10N
		this.cfg.setProperty('DATE_FIELD_DELIMITER', this.cfg.initialConfig.date_delim);
		this.cfg.setProperty('MDY_YEAR_POSITION', this.cfg.initialConfig.year_pos);
		this.cfg.setProperty('MDY_MONTH_POSITION', this.cfg.initialConfig.month_pos);
		this.cfg.setProperty('MDY_DAY_POSITION', this.cfg.initialConfig.day_pos);
		
		//I18N
		var language = this.cfg.initialConfig.language;
		if(this.languageSettings[language]) {
			this.cfg.setProperty("MONTHS_SHORT", this.languageSettings[language].MONTHS_SHORT);
			this.cfg.setProperty("MONTHS_LONG", this.languageSettings[language].MONTHS_LONG);
			this.cfg.setProperty("WEEKDAYS_1CHAR", this.languageSettings[language].WEEKDAYS_1CHAR);
			this.cfg.setProperty("WEEKDAYS_SHORT", this.languageSettings[language].WEEKDAYS_SHORT);
			this.cfg.setProperty("WEEKDAYS_MEDIUM", this.languageSettings[language].WEEKDAYS_MEDIUM);
			this.cfg.setProperty("WEEKDAYS_LONG", this.languageSettings[language].WEEKDAYS_LONG);
		}
	},
	
	languageSettings : {
	    tr : {
	    	MONTHS_SHORT : ["Oca", "\u015eub", "Mar", "Nis", "May", "Haz", "Tem", "A\u011fu", "Eyl", "Eki", "Kas", "Ara"],
	    	MONTHS_LONG : ["Ocak", "\u015eubat", "Mart", "Nisan", "May\u0131s", "Haziran", "Temmuz", "A\u011fustos", "Eyl\u00fcl", "Ekim", "Kas\u0131m", "Aral\u0131k"],
	    	WEEKDAYS_1CHAR : ["P", "P", "S", "\u00c7", "P", "C", "C"],
	    	WEEKDAYS_SHORT : ["Pz", "Pt", "Sa", "\u00c7a", "Pe", "Cu", "Ct"],
	    	WEEKDAYS_MEDIUM : ["Paz", "Pzt", "Sal", "\u00c7ar", "Per", "Cum", "Cts"],
	    	WEEKDAYS_LONG : ["Pazar", "Pazartesi", "Sal\u0131", "\u00c7ar\u015famba", "Per\u015fembe", "Cuma", "Cumartesi"]              
		},
		ca :{
			MONTHS_SHORT : ["Gen", "Feb", "Mar", "Abr", "Mai", "Jun", "Jul", "Ago", "Set", "Oct", "Nov", "Des"],
			MONTHS_LONG : ["Gener", "Febrer", "Mar\u00E7", "Abril", "Maig", "Juny", "Juliol", "Agost", "Setembre", "Octubre", "Novembre", "Desembre"],
			WEEKDAYS_1CHAR : ["D", "D", "D", "D", "D", "D", "D"],
			WEEKDAYS_SHORT : ["Di", "Di", "Di", "Di", "Di", "Di", "Di"],
			WEEKDAYS_MEDIUM : [ "Diu", "Dil", "Dim", "Dim", "Dij", "Div", "Dis"],
			WEEKDAYS_LONG : ["Diumenge", "Dilluns", "Dimarts", "Dimecres", "Dijous", "Divendres", "Dissabte"]              
		},
		pt :{
			MONTHS_SHORT : ["Jan", "Fev", "Mar", "Abr", "Mai", "Jun", "Jul", "Ago", "Set", "Out", "Nov", "Dez"],
			MONTHS_LONG : ["Janeiro", "Fevereiro", "Mar\u00E7o", "Abril", "Maio", "Junho", "Julho", "Agosto", "Setembro", "Outubro", "Novembro", "Dezembro"],
			WEEKDAYS_1CHAR : ["D", "S", "T", "Q", "Q", "S", "S"],
			WEEKDAYS_SHORT : ["Do", "Se", "Te", "Qu", "Qu", "Se", "S\u00E1"],
			WEEKDAYS_MEDIUM : ["Dom", "Seg", "Ter", "Qua", "Qui", "Sexta", "S\u00E1b"],
			WEEKDAYS_LONG : ["Domingo", "Segunda-feira", "Ter\u00E7a-feira", "Quarta-feira", "Quinta-feira", "Sexta-feira", "S\u00E1bado"]              
		},
		it :{
			MONTHS_SHORT : ["Gen", "Feb", "Mar", "Apr", "Mag", "Giu", "Lug", "Ago", "Set", "Ott", "Nov", "Dic"],
			MONTHS_LONG : ["Gennaio", "Febbraio", "Marzo", "Aprile", "Maggio", "Giugno", "Luglio", "Agosto", "Settembre", "Ottobre", "Novembre", "Dicembre"],
			WEEKDAYS_1CHAR : ["D", "L", "M", "M", "G", "V", "S"],
			WEEKDAYS_SHORT : ["Do", "Lu", "Ma", "Me", "Gi", "V", "Sa"],
			WEEKDAYS_MEDIUM : ["Dom", "Lun", "Mar", "Mer", "Gio", "Ven", "Sab"],
			WEEKDAYS_LONG : ["Domenica", "Luned\u00EC", "Marted\u00EC", "Mercoled\u00EC", "Gioved\u00EC", "Venerd\u00EC", "Sabato"]              
		},
		fr :{
			MONTHS_SHORT : ["Jan", "F\u00E9v", "Mar", "Avr", "Mai", "Jui", "Jui", "Ao\u00FB", "Sep", "Oct", "Nov", "D\u00E9c"],
			MONTHS_LONG : ["Janvier", "F\u00E9vrier", "Mars", "Avril", "Mai", "Juin", "Juillet", "Ao\u00FBt", "Septembre", "Octobre", "Novembre", "D\u00E9cembre"],
			WEEKDAYS_1CHAR : ["D", "L", "M", "M", "J", "V", "S"],
			WEEKDAYS_SHORT : ["Di", "Lu", "Ma", "Me", "Je", "Ve", "Sa"],
			WEEKDAYS_MEDIUM : ["Dim", "Lun", "Mar", "Mer", "Jeu", "Ven", "Sam"],
			WEEKDAYS_LONG : ["Dimanche", "Lundi", "Mardi", "Mercredi", "Jeudi", "Vendredi", "Samedi"]              
		},
		es :{
			MONTHS_SHORT : ["Ene", "Feb", "Mar", "Abr", "May", "Jun", "Jul", "Ago", "Sep", "Oct", "Nov", "Dic"],
			MONTHS_LONG : ["Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio", "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"],
			WEEKDAYS_1CHAR : ["D", "L", "M", "M", "J", "V", "S"],
			WEEKDAYS_SHORT : ["Do", "Lu", "Ma", "Mi", "Ju", "Vi", "S\u00E1"],
			WEEKDAYS_MEDIUM : ["Dom", "Lun", "Mar", "Mi\u00E9", "Jue", "Vie", "S\u00E1b"],
			WEEKDAYS_LONG : ["Domingo", "Lunes", "Martes", "Mi\00E9rcoles", "Jueves", "Viernes", "S\u00E1bado"]              
		},
		de :{
			MONTHS_SHORT : ["Jan", "Feb", "M\u00E4r", "Apr", "Mai", "Jun", "Jul", "Aug", "Sep", "Okt", "Nov", "Dez"],
			MONTHS_LONG : ["Januar", "Februar", "M\u00E4rz", "April", "Mai", "Juni", "Juli", "August", "September", "Oktober", "November", "Dezember"],
			WEEKDAYS_1CHAR : ["S", "M", "D", "M", "D", "F", "S"],
			WEEKDAYS_SHORT : ["So", "Mo", "Di", "Mi", "Do", "Fr", "Sa"],
			WEEKDAYS_MEDIUM : ["Son", "Mon", "Die", "Mit", "Don", "Fre", "Sam"],
			WEEKDAYS_LONG : ["Sonntag", "Montag", "Dienstag", "Mittwoch", "Donnerstag", "Freitag", "Samstag"]              
		},
		ja :{
			id : "ja",
			MONTHS_SHORT : ["1\u6708", "2\u6708", "3\u6708", "4\u6708", "5\u6708", "6\u6708", "7\u6708", "8\u6708", "9\u6708", "10\u6708", "11\u6708", "12\u6708"],
			MONTHS_LONG : ["1\u6708", "2\u6708", "3\u6708", "4\u6708", "5\u6708", "6\u6708", "7\u6708", "8\u6708", "9\u6708", "10\u6708", "11\u6708", "12\u6708"],
			WEEKDAYS_1CHAR : ["\u65E5", "\u6708", "\u706B", "\u6C34", "\u6728", "\u91D1", "\u571F"],
			WEEKDAYS_SHORT : ["\u65E5", "\u6708", "\u706B", "\u6C34", "\u6728", "\u91D1", "\u571F"],
			WEEKDAYS_MEDIUM : ["\u65E5", "\u6708", "\u706B", "\u6C34", "\u6728", "\u91D1", "\u571F"],
			WEEKDAYS_LONG : ["\u65E5", "\u6708", "\u706B", "\u6C34", "\u6728", "\u91D1", "\u571F"]              
		},
		fi :{
			MONTHS_SHORT : ["Tammi", "Helmi", "Maalis", "Huhti", "Touko", "Kes\u00E4", "Hein\u00E4", "Elo", "Syys", "Loka", "Marras", "Joulu"],
			MONTHS_LONG : ["Tammikuu", "Helmikuu", "Maaliskuu", "Huhtikuu", "Toukokuu", "Kes\u00E4kuu", "Hein\u00E4kuu", "Elokuu", "Syyskuu", "Lokakuu", "Marraskuu", "Joulukuu"],
			WEEKDAYS_1CHAR : ["S", "M", "T", "K", "T", "P", "L"],
			WEEKDAYS_SHORT : ["Su", "Ma", "Ti", "Ke", "To", "Pe", "La"],
			WEEKDAYS_MEDIUM : ["Su", "Ma", "Ti", "Ke", "To", "Pe", "La"],
			WEEKDAYS_LONG : ["Sunnuntai", "Maanantai", "Tiistai", "Keskiviikko", "Torstai", "Perjantai", "Lauantai"]
		},
		pl :{
			MONTHS_SHORT : ["Sty", "Lut", "Marz", "Kwie", "Maj", "Czer", "Lip", "Sier", "Wrze", "Pa\u017a", "List", "Gru"],
			MONTHS_LONG : ["Stycze\u0144", "Luty", "Marzec", "Kwiecie\u0144", "Maj", "Czerwiec", "Lipiec", "Sierpie\u0144", "Wrzesie\u0144", "Pa\u017adziernik", "Listopad", "Grudzie\u0144"],
			WEEKDAYS_1CHAR : ["N", "P", "W", "\u015a", "C", "P", "S"],
			WEEKDAYS_SHORT : ["Nie", "Pon", "Wt", "\u015ar", "Czw", "Pi", "So"],
			WEEKDAYS_MEDIUM : ["Niedz", "Ponie", "Wtor", "\u015arod", "Czwar", "Pi\u0105", "Sob"],
			WEEKDAYS_LONG : ["Niedziela", "Poniedzia\u0142ek", "Wtorek", "\u015aroda", "Czwartek", "Pi\u0105tek", "Sobota"]
		}
	},
	
	setInputValue : function(value) {
		document.getElementById(this.id + '_input').value = value;
	},
	
	isPopup : function() {
		return this.cfg.initialConfig.popup != undefined;
	},
	
	isAjaxSelectionEnabled : function() {
		return this.cfg.initialConfig.hasselectlistener;
	},
	
	getPopup : function() {
		return this.cfg.initialConfig.popup;
	}
};

YAHOO.lang.extend(PrimeFaces.widget.Calendar, YAHOO.widget.Calendar, PrimeFaces.widget.CalendarExtensions);

YAHOO.lang.extend(PrimeFaces.widget.CalendarGroup, YAHOO.widget.CalendarGroup, PrimeFaces.widget.CalendarExtensions);