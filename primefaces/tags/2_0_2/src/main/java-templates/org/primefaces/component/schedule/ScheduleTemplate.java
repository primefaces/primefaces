import java.util.Iterator;
import javax.faces.component.UIComponent;
import org.primefaces.component.schedule.Schedule;
import org.primefaces.component.schedule.ScheduleEventDialog;
import org.primefaces.event.ScheduleEntrySelectEvent;
import org.primefaces.event.DateSelectEvent;
import org.primefaces.event.ScheduleEntryMoveEvent;
import org.primefaces.event.ScheduleEntryResizeEvent;

	private java.util.Locale appropriateLocale;
	private ScheduleEventDialog eventDialog;
	
	java.util.Locale calculateLocale(FacesContext facesContext) {
		if(appropriateLocale == null) {
			Object userLocale = getLocale();
			if(userLocale != null) {
				if(userLocale instanceof String)
					appropriateLocale = new java.util.Locale((String) userLocale, "");
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

	public void broadcast(javax.faces.event.FacesEvent event) throws javax.faces.event.AbortProcessingException {
		super.broadcast(event);
		
		FacesContext facesContext = FacesContext.getCurrentInstance();
		MethodExpression me = null;
		
		if(event instanceof ScheduleEntrySelectEvent) me = getEventSelectListener();
		else if(event instanceof DateSelectEvent) me = getDateSelectListener();
		else if(event instanceof ScheduleEntryMoveEvent) me = getEventMoveListener();
		else if(event instanceof ScheduleEntryResizeEvent) me = getEventResizeListener();
		
		if (me != null) {
			me.invoke(facesContext.getELContext(), new Object[] {event});
		}
	}
	
	public ScheduleEventDialog getEventDialog() {
		if(eventDialog == null) {
			for(Iterator<UIComponent> iterator = getChildren().iterator(); iterator.hasNext();) {
				UIComponent kid = iterator.next();
				if(kid instanceof ScheduleEventDialog) {
					eventDialog = (ScheduleEventDialog) kid;
				}
			}
		}
		
		return eventDialog;	
	}
	
	public void processDecodes(FacesContext facesContext) {
		if(isScheduleRequest(facesContext)) {
			decode(facesContext);
			facesContext.renderResponse();
		} else {
			super.processDecodes(facesContext);
		}
    }
	
	private boolean isScheduleRequest(FacesContext facesContext) {
		return facesContext.getExternalContext().getRequestParameterMap().containsKey(getClientId(facesContext));
	}