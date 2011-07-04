import org.primefaces.component.calendar.Calendar;
import org.primefaces.event.DateSelectEvent;
import org.primefaces.util.HTML;
import org.primefaces.util.ArrayUtils;
import org.primefaces.util.Constants;
import java.util.Iterator;
import java.util.Map;
import java.util.HashMap;
import java.util.Date;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.event.FacesEvent;
import javax.faces.event.PhaseId;

    public final static String INPUT_STYLE_CLASS = "ui-inputfield ui-widget ui-state-default ui-corner-all";

	public static String POPUP_ICON = "calendar/calendar_icon.png";

    private static final Collection<String> EVENT_NAMES = Collections.unmodifiableCollection(Arrays.asList("blur","change","valueChange","click","dblclick","focus","keydown","keypress","keyup","mousedown","mousemove","mouseout","mouseover","mouseup","select", "dateSelect"));

    private Map<String,AjaxBehaviorEvent> customEvents = new HashMap<String,AjaxBehaviorEvent>();

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

    public boolean hasTime() {
        String pattern = getPattern();

        return (pattern != null && pattern.indexOf(":") != -1);
    }

    @Override
    public Collection<String> getEventNames() {
        return EVENT_NAMES;
    }

    @Override
    public void queueEvent(FacesEvent event) {
        FacesContext context = FacesContext.getCurrentInstance();
        String eventName = context.getExternalContext().getRequestParameterMap().get(Constants.PARTIAL_BEHAVIOR_EVENT_PARAM);
        
        if(eventName != null && eventName.equals("dateSelect") && event instanceof AjaxBehaviorEvent) {
            customEvents.put("dateSelect", (AjaxBehaviorEvent) event);
        } else {
            super.queueEvent(event);
        }
    }

    @Override
    public void validate(FacesContext context) {
        super.validate(context);
       
        if(isValid()) {
            for(Iterator<String> customEventIter = customEvents.keySet().iterator(); customEventIter.hasNext();) {
                AjaxBehaviorEvent behaviorEvent = customEvents.get(customEventIter.next());
                DateSelectEvent dateSelectEvent = new DateSelectEvent(this, behaviorEvent.getBehavior(), (Date) getValue());

                if(behaviorEvent.getPhaseId().equals(PhaseId.APPLY_REQUEST_VALUES)) {
                    dateSelectEvent.setPhaseId(PhaseId.PROCESS_VALIDATIONS);
                }

                super.queueEvent(dateSelectEvent);
            }
        }
    }