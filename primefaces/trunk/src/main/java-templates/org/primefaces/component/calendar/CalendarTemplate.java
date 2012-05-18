import org.primefaces.component.calendar.Calendar;
import org.primefaces.event.DateSelectEvent;
import org.primefaces.util.HTML;
import org.primefaces.util.ArrayUtils;
import org.primefaces.util.Constants;
import org.primefaces.util.ComponentUtils;
import java.util.Iterator;
import java.util.Map;
import java.util.HashMap;
import java.util.Date;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.event.FacesEvent;
import javax.faces.event.PhaseId;

    public final static String INPUT_STYLE_CLASS = "ui-inputfield ui-widget ui-state-default ui-corner-all";

    private static final Collection<String> EVENT_NAMES = Collections.unmodifiableCollection(Arrays.asList("blur","change","valueChange","click","dblclick","focus","keydown","keypress","keyup","mousedown","mousemove","mouseout","mouseover","mouseup","select", "dateSelect"));

    private Map<String,AjaxBehaviorEvent> customEvents = new HashMap<String,AjaxBehaviorEvent>();

	private java.util.Locale calculatedLocale;
	private java.util.TimeZone appropriateTimeZone;
	
	public java.util.Locale calculateLocale(FacesContext facesContext) {
		if(calculatedLocale == null) {
			Object userLocale = getLocale();
			if(userLocale != null) {
				if(userLocale instanceof String) {
					calculatedLocale = ComponentUtils.toLocale((String) userLocale);
				}
				else if(userLocale instanceof java.util.Locale)
					calculatedLocale = (java.util.Locale) userLocale;
				else
					throw new IllegalArgumentException("Type:" + userLocale.getClass() + " is not a valid locale type for calendar:" + this.getClientId(facesContext));
			} else {
				calculatedLocale = facesContext.getViewRoot().getLocale();
			}
		}
		
		return calculatedLocale;
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
                else {
                    dateSelectEvent.setPhaseId(PhaseId.INVOKE_APPLICATION);
                }

                super.queueEvent(dateSelectEvent);
            }
        }
    }

    private String calculatedPattern = null;

    public String calculatePattern() {
        if(calculatedPattern == null) {
            String pattern = this.getPattern();
            Locale locale = this.calculateLocale(FacesContext.getCurrentInstance());
            if(pattern == null) {
                calculatedPattern = ((SimpleDateFormat) DateFormat.getDateInstance(DateFormat.SHORT, locale)).toPattern();
            }
            else {
                calculatedPattern = pattern;
            }
            
        }

        return calculatedPattern;
    }

    private String timeOnlyPattern = null;

    public String calculateTimeOnlyPattern() {
        if(timeOnlyPattern == null) {
            String localePattern = ((SimpleDateFormat) DateFormat.getDateInstance(DateFormat.SHORT, calculateLocale(FacesContext.getCurrentInstance()))).toPattern();
            String userTimePattern = getPattern();

            timeOnlyPattern = localePattern + " " + userTimePattern;
        }

        return timeOnlyPattern;
    }

    private boolean conversionFailed = false;

    public void setConversionFailed(boolean value) {
        this.conversionFailed = value;
    }

    public boolean isConversionFailed() {
        return this.conversionFailed;
    }

    public String getInputClientId() {
        return this.getClientId(getFacesContext()) + "_input";
    }