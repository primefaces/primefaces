if(PrimeFaces == undefined) var PrimeFaces = {};
if(PrimeFaces.widget == undefined) PrimeFaces.widget = {};

PrimeFaces.widget.CalendarUtils = {
		
	selectEvent : function(type,args,cfg) {

		if(cfg.selection == "single") {	
			var dates = cfg.calendar.getSelectedDates(); 
			var date = dates[0];
			
			var value = PrimeFaces.widget.CalendarUtils.formatDate(date,cfg);
				
			document.getElementById(cfg.inputId).value = value;
		} else if(cfg.selection == "multiple") {
			var dates = cfg.calendar.getSelectedDates();
			
			if(dates.length == 0) {
				document.getElementById(cfg.inputId).value = null;
			}
			else {
				for(var i=0; i < dates.length;i++) {
					var date = dates[i];
					
					var value = PrimeFaces.widget.CalendarUtils.formatDate(date, cfg);
					
					if(i == 0)
						document.getElementById(cfg.inputId).value = value;
					else
						document.getElementById(cfg.inputId).value = document.getElementById(cfg.inputId).value + "," + value;
				}
			}
		}
		
		if(cfg.popupOverlay != undefined && cfg.selection == "single")
			cfg.popupOverlay.hide();
		
	},
	
	formatDate : function(date, cfg) {
		var month = date.getMonth() + 1;
		var day = date.getDate();
		var year = date.getFullYear();
		var value = "";
		
		var fieldValues = [];
		fieldValues[cfg.monthFieldIndex -1] = month;
		fieldValues[cfg.dayFieldIndex -1] = day;
		fieldValues[cfg.yearFieldIndex -1] = year;
		
		for(var i=0;i < 3;i++) {
			if(i == 2)
				value = value + fieldValues[i];
			else
				value = value + fieldValues[i] + cfg.delimiter;
		}
		
		return value;
	},
	
	applyLocale : function(calendar,language) {
		if(this.languageSettings[language] != undefined) {
			calendar.cfg.setProperty("MONTHS_SHORT", this.languageSettings[language].MONTHS_SHORT);
			calendar.cfg.setProperty("MONTHS_LONG", this.languageSettings[language].MONTHS_LONG);
			calendar.cfg.setProperty("WEEKDAYS_1CHAR", this.languageSettings[language].WEEKDAYS_1CHAR);
			calendar.cfg.setProperty("WEEKDAYS_SHORT", this.languageSettings[language].WEEKDAYS_SHORT);
			calendar.cfg.setProperty("WEEKDAYS_MEDIUM", this.languageSettings[language].WEEKDAYS_MEDIUM);
			calendar.cfg.setProperty("WEEKDAYS_LONG", this.languageSettings[language].WEEKDAYS_LONG);
		}
	},
	
	languageSettings : {
	    tr : {
	    	MONTHS_SHORT : ["Oca", "\u015eub", "Mar", "Nis", "May", "Haz", "Tem", "A\u011fu", "Eyl", "Eki", "Kas", "Ara"],
	    	MONTHS_LONG : ["Ocak", "\u015eubat", "Mart", "Nisan", "May\u0131s", "Haziran", "Temmuz", "A\u011fustos", "Eyl\u00fcl", "Ekim", "Kas\u0131m", "Aral\u0131k"],
	    	WEEKDAYS_1CHAR : ["P", "P", "S", "\u00c7", "P", "C", "C"],
	    	WEEKDAYS_SHORT : ["Pz", "Pz", "Sa", "\u00c7a", "Pe", "Cu", "Ct"],
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
		}
	}
};