PrimeFaces.widget.Schedule = function(id, cfg) {
	this.clientId = id;
	this.cfg = cfg;
	this.setupEventSource();
	
	if(this.cfg.language)
	    this.applyLocale();
	
	if(this.cfg.hasEventDialog) 
		this.createEventDialog();

	if(this.cfg.editable)
		this.setupEventHandlers();
	
	jQuery(PrimeFaces.escapeClientId(this.clientId) + "_container").fullCalendar(this.cfg);
}

PrimeFaces.widget.Schedule.prototype.applyLocale = function() {
	var lang = PrimeFaces.widget.ScheduleResourceBundle[this.cfg.language];
	                                                    
	if(lang) {
		this.cfg.monthNames = lang.monthNames;
		this.cfg.monthNamesShort = lang.monthNamesShort;
		this.cfg.dayNames = lang.dayNames;
		this.cfg.dayNamesShort = lang.dayNamesShort;
		this.cfg.buttonText = { today: lang.today, month: lang.month, week: lang.week, day: lang.day };
		this.cfg.allDayText = lang.allDayText;
	}
}

PrimeFaces.widget.Schedule.prototype.setupEventHandlers = function() {
	var scope = this;

	this.cfg.dayClick = function(dayDate, allDay, jsEvent, view) {
		var params = {};
		params[scope.clientId] = scope.clientId;
		params[PrimeFaces.PARTIAL_PROCESS_PARAM] = scope.clientId;
		params[scope.clientId + '_selectedDate'] = dayDate.getTime();
		
		if(scope.cfg.onDateSelectUpdate) params[PrimeFaces.PARTIAL_UPDATE_PARAM] = scope.cfg.onDateSelectUpdate;
		else if(scope.cfg.hasEventDialog) params[PrimeFaces.PARTIAL_UPDATE_PARAM] = scope.cfg.dialogClientId;
		
		var config = {formId:scope.cfg.formId};
		
		if(scope.cfg.hasEventDialog && scope.cfg.onDateSelectUpdate == undefined) {
			config.oncomplete = function() { 
				scope.dialog.show();
			};
		}
		
		PrimeFaces.ajax.AjaxRequest(scope.cfg.url, config, params);
	}

	this.cfg.eventClick = function(calEvent, jsEvent, view) {
		var params = {};
		params[scope.clientId] = scope.clientId;
		params[PrimeFaces.PARTIAL_PROCESS_PARAM] = scope.clientId;
		params[scope.clientId + '_selectedEventId'] = calEvent.id;
		
		if(scope.cfg.onEventSelectUpdate) params[PrimeFaces.PARTIAL_UPDATE_PARAM] = scope.cfg.onEventSelectUpdate;
		else if(scope.cfg.hasEventDialog) params[PrimeFaces.PARTIAL_UPDATE_PARAM] = scope.cfg.dialogClientId;
		
		var config = {formId:scope.cfg.formId};
		
		if(scope.cfg.hasEventDialog && scope.cfg.onEventSelectUpdate == undefined) {
			config.oncomplete = function() { 
				scope.dialog.show();
			};
		}
		
		PrimeFaces.ajax.AjaxRequest(scope.cfg.url, config, params);
	}
	
	this.cfg.eventDrop = function(calEvent, dayDelta, minuteDelta, allDay, revertFunc, jsEvent, ui, view) {
		var params = {};
		params[scope.clientId] = scope.clientId;
		params[PrimeFaces.PARTIAL_PROCESS_PARAM] = scope.clientId;
		params[scope.clientId + '_changedEventId'] = calEvent.id;
		params[scope.clientId + '_dayDelta'] = dayDelta;
		params[scope.clientId + '_minuteDelta'] = minuteDelta;
		
		if(scope.cfg.onEventMoveUpdate) 
			params[PrimeFaces.PARTIAL_UPDATE_PARAM] = scope.cfg.onEventMoveUpdate;
		
		PrimeFaces.ajax.AjaxRequest(scope.cfg.url, {formId:scope.cfg.formId}, params);
	}
	
	this.cfg.eventResize = function(calEvent, dayDelta, minuteDelta, revertFunc, jsEvent, ui, view) {
		var params = {};
		params[scope.clientId] = scope.clientId;
		params[PrimeFaces.PARTIAL_PROCESS_PARAM] = scope.clientId;
		params[scope.clientId + '_changedEventId'] = calEvent.id;
		params[scope.clientId + '_dayDelta'] = dayDelta;
		params[scope.clientId + '_minuteDelta'] = minuteDelta;
		params[scope.clientId + '_resized'] = true;
		
		if(scope.cfg.onEventResizeUpdate) 
			params[PrimeFaces.PARTIAL_UPDATE_PARAM] = scope.cfg.onEventResizeUpdate;
		
		PrimeFaces.ajax.AjaxRequest(scope.cfg.url, {formId:scope.cfg.formId}, params);
	}
}

PrimeFaces.widget.Schedule.prototype.setupEventSource = function() {
	var clientId = this.clientId,
	cfg = this.cfg;
	
	this.cfg.events = function(start, end, callback) {
		var requestParams = "";
		requestParams = jQuery(PrimeFaces.escapeClientId(cfg.formId)).serialize();
		
		var params = {};
		params[PrimeFaces.PARTIAL_SOURCE_PARAM] = clientId;
		params[PrimeFaces.PARTIAL_REQUEST_PARAM] = true;
		params[clientId + "_start"] = start.getTime();
		params[clientId + "_end"] = end.getTime();
		
		requestParams = requestParams + PrimeFaces.ajax.AjaxUtils.serialize(params); 
		
		jQuery.ajax({
			   type: "POST",
			   url: cfg.url,
			   data: requestParams,
			   dataType: "json",
			   success: function(response){
				 var events = response.events;
				
			     for(var i=0; i < events.length; i++) {
			    	 events[i].start = new Date(events[i].start);
			    	 events[i].end = new Date(events[i].end);
			     }
			     
			     callback(events);
			   }
			 });
	}
}

PrimeFaces.widget.Schedule.prototype.update = function() {
	jQuery(PrimeFaces.escapeClientId(this.clientId) + "_container").fullCalendar('refetchEvents');
	this.dialog.hide();
}

PrimeFaces.widget.Schedule.prototype.createEventDialog = function() {
	this.dialog = new YAHOO.widget.Panel(this.clientId + "_dialogContainer", {fixedcenter:true, visible:false, zindex:1000});
	this.dialog.render();
}

PrimeFaces.widget.ScheduleResourceBundle = {
		tr : {
			monthNamesShort : ["Oca", "\u015eub", "Mar", "Nis", "May", "Haz", "Tem", "A\u011fu", "Eyl", "Eki", "Kas", "Ara"],
			monthNames : ["Ocak", "\u015eubat", "Mart", "Nisan", "May\u0131s", "Haziran", "Temmuz", "A\u011fustos", "Eyl\u00fcl", "Ekim", "Kas\u0131m", "Aral\u0131k"],
			dayNamesShort : ["Paz", "Pzt", "Sal", "\u00c7ar", "Per", "Cum", "Cts"],
			dayNames : ["Pazar", "Pazartesi", "Sal\u0131", "\u00c7ar\u015famba", "Per\u015fembe", "Cuma", "Cumartesi"],
			today : "bug\u00fcn",
			month : "ay",
			week : "hafta",
			day : "g\u00fcn",
			allDayText : "t\u00fcm-g\u00fcn"
		},
		ca :{
			monthNamesShort : ["Gen", "Feb", "Mar", "Abr", "Mai", "Jun", "Jul", "Ago", "Set", "Oct", "Nov", "Des"],
			monthNames : ["Gener", "Febrer", "Mar\u00e7", "Abril", "Maig", "Juny", "Juliol", "Agost", "Setembre", "Octubre", "Novembre", "Desembre"],
			dayNamesShort : [ "Diu", "Dil", "Dim", "Dim", "Dij", "Div", "Dis"],
			dayNames : ["Diumenge", "Dilluns", "Dimarts", "Dimecres", "Dijous", "Divendres", "Dissabte"],
			today : "avui",
			month : "mes",
			week : "setmana",
			day : "dia",
			allDayText : "tot-el dia"
		},
		pt :{
			monthNamesShort : ["Jan", "Fev", "Mar", "Abr", "Mai", "Jun", "Jul", "Ago", "Set", "Out", "Nov", "Dez"],
			monthNames : ["Janeiro", "Fevereiro", "Mar\u00e7o", "Abril", "Maio", "Junho", "Julho", "Agosto", "Setembro", "Outubro", "Novembro", "Dezembro"],
			dayNamesShort : ["Dom", "Seg", "Ter", "Qua", "Qui", "Sexta", "S\u00e1b"],
			dayNames : ["Domingo", "Segunda-feira", "Ter\u00e7a-feira", "Quarta-feira", "Quinta-feira", "Sexta-feira", "S\u00e1bado"],
            today : "hoje",
			month : "m\u00eas",
			week : "semana",
			day : "dia",
			allDayText : "todos-os dias"
		},
		it :{
			monthNamesShort : ["Gen", "Feb", "Mar", "Apr", "Mag", "Giu", "Lug", "Ago", "Set", "Ott", "Nov", "Dic"],
			monthNames : ["Gennaio", "Febbraio", "Marzo", "Aprile", "Maggio", "Giugno", "Luglio", "Agosto", "Settembre", "Ottobre", "Novembre", "Dicembre"],
			dayNamesShort : ["Dom", "Lun", "Mar", "Mer", "Gio", "Ven", "Sab"],
			dayNames : ["Domenica", "Luned\u00ec", "Marted\u00ec", "Mercoled\u00ec", "Gioved\u00ec", "Venerd\u00ec", "Sabato"],              
            today : "oggi",
			month : "mese",
			week : "settimana",
			day : "giorno",
			allDayText : "tutto-il giorno"
		},
		fr :{
			monthNamesShort : ["Jan", "F\u00e9v", "Mar", "Avr", "Mai", "Jui", "Jui", "Ao\u00fb", "Sep", "Oct", "Nov", "D\u00e9c"],
			monthNames : ["Janvier", "F\u00e9vrier", "Mars", "Avril", "Mai", "Juin", "Juillet", "Ao\u00fbt", "Septembre", "Octobre", "Novembre", "D\u00e9cembre"],
			dayNamesShort : ["Dim", "Lun", "Mar", "Mer", "Jeu", "Ven", "Sam"],
			dayNames : ["Dimanche", "Lundi", "Mardi", "Mercredi", "Jeudi", "Vendredi", "Samedi"],              
        	today : "aujourd'hui",
            month : "mois",
            week : "semaine",
            day : "jour",
			allDayText : "toute-la journ\u00e9e"
		},
		es :{
			monthNamesShort : ["Ene", "Feb", "Mar", "Abr", "May", "Jun", "Jul", "Ago", "Sep", "Oct", "Nov", "Dic"],
			monthNames : ["Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio", "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"],
			dayNamesShort : ["Dom", "Lun", "Mar", "Mi\u00e9", "Jue", "Vie", "S\u00e1b"],
			dayNames : ["Domingo", "Lunes", "Martes", "Mi\00E9rcoles", "Jueves", "Viernes", "S\u00e1bado"],              
            today : "hoy",
            month : "mes",
            week : "semana",
            day : "d\u00eda",
			allDayText : "todo-el d\u00eda"
		},
		de :{
			monthNamesShort : ["Jan", "Feb", "M\u00e4r", "Apr", "Mai", "Jun", "Jul", "Aug", "Sep", "Okt", "Nov", "Dez"],
			monthNames : ["Januar", "Februar", "M\u00e4rz", "April", "Mai", "Juni", "Juli", "August", "September", "Oktober", "November", "Dezember"],
			dayNamesShort : ["Son", "Mon", "Die", "Mit", "Don", "Fre", "Sam"],
			dayNames : ["Sonntag", "Montag", "Dienstag", "Mittwoch", "Donnerstag", "Freitag", "Samstag"],              
            today : "heute",
	        month : "monat",
	        week : "woche",
	        day : "tag",
			allDayText : "ganzen-tag"
		},
		ja :{
			monthNamesShort : ["1\u6708", "2\u6708", "3\u6708", "4\u6708", "5\u6708", "6\u6708", "7\u6708", "8\u6708", "9\u6708", "10\u6708", "11\u6708", "12\u6708"],
			monthNames : ["1\u6708", "2\u6708", "3\u6708", "4\u6708", "5\u6708", "6\u6708", "7\u6708", "8\u6708", "9\u6708", "10\u6708", "11\u6708", "12\u6708"],
			dayNamesShort : ["\u65e5", "\u6708", "\u706b", "\u6c34", "\u6728", "\u91d1", "\u571f"],
			dayNames : ["\u65e5", "\u6708", "\u706b", "\u6c34", "\u6728", "\u91d1", "\u571f"],              
            today : "\u672c\u65e5",
            month : "\u5148\u6708",
            week : "\u9031",
            day : "\u65e5",
			allDayText : "\u7d42-\u65e5"
		},
		fi :{
			monthNamesShort : ["Tammi", "Helmi", "Maalis", "Huhti", "Touko", "Kes\u00E4", "Hein\u00E4", "Elo", "Syys", "Loka", "Marras", "Joulu"],
			monthNames : ["Tammikuu", "Helmikuu", "Maaliskuu", "Huhtikuu", "Toukokuu", "Kes\u00E4kuu", "Hein\u00E4kuu", "Elokuu", "Syyskuu", "Lokakuu", "Marraskuu", "Joulukuu"],
			dayNamesShort : ["Su", "Ma", "Ti", "Ke", "To", "Pe", "La"],
			dayNames : ["Sunnuntai", "Maanantai", "Tiistai", "Keskiviikko", "Torstai", "Perjantai", "Lauantai"],
			today : "t\u00E4n\u00E4\u00E4n",
			month : "kuukausi",
			week : "viikko",
			day : "p\u00E4iv\u00E4",
			allDayText : "koko p\u00E4iv\u00E4"
		},
		hu :{
			monthNamesShort : ["Jan", "Feb", "M\u00e1r", "\u00c1pr", "M\u00e1j", "J\u00fan", "J\u00fal", "Aug", "Szep", "Okt", "Nov", "Dec"],
			monthNames : ["Janu\u00e1r", "Febru\u00e1r", "M\u00e1rcius", "\u00c1prilis", "M\u00e1jus", "J\u00fanius", "J\u00falius", "Augusztus", "Szeptember", "Okt\u00f3ber", "November", "December"],
			dayNamesShort : [ "Vas", "H\u00e9t", "Ked", "Sze", "Cs\u00fc", "P\u00e9n", "Szo"],
			dayNames : ["Vas\u00e1rnap", "H\u00e9tf\u0151", "Kedd", "Szerda", "Cs\u00fct\u00f6rt\u00f6k", "P\u00e9ntek", "Szombat"],
			today : "ma",
			month : "h\u00f3nap",
			week : "h\u00e9t",
			day : "nap",
			allDayText : "eg\u00e9sz nap"
		}
}