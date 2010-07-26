import org.primefaces.component.calendar.Calendar;
import org.primefaces.event.DateSelectEvent;

	public static String POPUP_ICON = "/primefaces/calendar/calendar_icon.png";

	private java.util.Locale appropriateLocale;
	private java.util.TimeZone appropriateTimeZone;
	
	public java.util.Locale calculateLocale(FacesContext facesContext) {
		if(appropriateLocale == null) {
			Object userLocale = getLocale();
			if(userLocale != null) {
				if(userLocale instanceof String) {
					String[] tokens = ((String) userLocale).split("_");
					if(tokens.length == 1)
						appropriateLocale = new java.util.Locale(tokens[0], "");
					else
						appropriateLocale = new java.util.Locale(tokens[0], tokens[1]);
				}
				else if(userLocale instanceof java.util.Locale)
					appropriateLocale = (java.util.Locale) userLocale;
				else
					throw new IllegalArgumentException("Type:" + userLocale.getClass() + " is not a valid locale type for calendar:" + this.getClientId(facesContext));
			} else {
				appropriateLocale = facesContext.getViewRoot().getLocale();
			}
		}
		
		return appropriateLocale;
	}
	
	public java.util.TimeZone calculateTimeZone() {
		if(appropriateTimeZone == null) {
			Object usertimeZone = getTimeZone();
			if(usertimeZone != null) {
				if(usertimeZone instanceof String)
					appropriateTimeZone =  java.util.TimeZone.getTimeZone((String) usertimeZone);
				else if(usertimeZone instanceof java.util.TimeZone)
					appropriateTimeZone = (java.util.TimeZone) usertimeZone;
				else
					throw new IllegalArgumentException("TimeZone could be either String or java.util.TimeZone");
			} else {
				appropriateTimeZone = java.util.TimeZone.getDefault();
			}
		}
		
		return appropriateTimeZone;
	}
	
	public boolean isPopup() {
		return getMode().equalsIgnoreCase("popup");
	}

	public void broadcast(javax.faces.event.FacesEvent event) throws javax.faces.event.AbortProcessingException {
		super.broadcast(event);
		
		FacesContext facesContext = FacesContext.getCurrentInstance();
		MethodExpression me = getSelectListener();
		
		if (me != null && event instanceof org.primefaces.event.DateSelectEvent) {
			me.invoke(facesContext.getELContext(), new Object[] {event});
		}
	}