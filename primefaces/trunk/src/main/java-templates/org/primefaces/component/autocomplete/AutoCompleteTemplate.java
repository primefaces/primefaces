import org.primefaces.component.calendar.Calendar;
import org.primefaces.event.SelectEvent;
import org.primefaces.util.HTML;
import org.primefaces.util.ArrayUtils;
import org.primefaces.util.Constants;
import org.primefaces.component.column.Column;
import javax.faces.component.UIComponent;
import java.util.Iterator;
import java.util.Map;
import java.util.HashMap;
import java.util.Date;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.event.FacesEvent;
import javax.faces.event.PhaseId;

    private static final Collection<String> EVENT_NAMES = Collections.unmodifiableCollection(Arrays.asList("blur","change","valueChange","click","dblclick","focus","keydown","keypress","keyup","mousedown","mousemove","mouseout","mouseover","mouseup","select", "itemSelect"));

    private Map<String,AjaxBehaviorEvent> customEvents = new HashMap<String,AjaxBehaviorEvent>();
    
    public final static String STYLE_CLASS = "ui-autocomplete";
    public final static String MULTIPLE_STYLE_CLASS = "ui-autocomplete-multiple";
    public final static String INPUT_CLASS = "ui-autocomplete-input ui-inputfield ui-widget ui-state-default ui-corner-all";
    public final static String INPUT_WITH_DROPDOWN_CLASS = "ui-autocomplete-input ui-inputfield ui-widget ui-state-default ui-corner-left";
    public final static String DROPDOWN_ICON_CLASS = "ui-autocomplete-dropdown ui-state-default ui-corner-right";
    public final static String PANEL_CLASS = "ui-autocomplete-panel ui-widget-content ui-corner-all ui-helper-hidden";
    public final static String LIST_CLASS = "ui-autocomplete-items ui-autocomplete-list ui-widget-content ui-widget ui-corner-all ui-helper-reset";
    public final static String TABLE_CLASS = "ui-autocomplete-items ui-autocomplete-table ui-widget-content ui-widget ui-corner-all ui-helper-reset";
    public final static String ITEM_CLASS = "ui-autocomplete-item ui-autocomplete-list-item ui-corner-all";
    public final static String ROW_CLASS = "ui-autocomplete-item ui-autocomplete-row ui-widget-content";
    public final static String TOKEN_DISPLAY_CLASS = "ui-autocomplete-token ui-state-active ui-corner-all";
    public final static String TOKEN_LABEL_CLASS = "ui-autocomplete-token-label";
    public final static String TOKEN_ICON_CLASS = "ui-autocomplete-token-icon ui-icon ui-icon-close";
    public final static String TOKEN_INPUT_CLASS = "ui-autocomplete-input-token";
    public final static String MULTIPLE_CONTAINER_CLASS = "ui-autocomplete-multiple-container ui-widget ui-inputfield ui-state-default ui-corner-all";

    @Override
    public Collection<String> getEventNames() {
        return EVENT_NAMES;
    }

    @Override
    public void queueEvent(FacesEvent event) {
        FacesContext context = FacesContext.getCurrentInstance();
        String eventName = context.getExternalContext().getRequestParameterMap().get(Constants.PARTIAL_BEHAVIOR_EVENT_PARAM);
        
        if(eventName != null && eventName.equals("itemSelect") && event instanceof AjaxBehaviorEvent) {
            customEvents.put("itemSelect", (AjaxBehaviorEvent) event);
        } else {
            super.queueEvent(event);
        }
    }

    private List suggestions = null;

    public void broadcast(javax.faces.event.FacesEvent event) throws javax.faces.event.AbortProcessingException {
		super.broadcast(event);
		
		FacesContext facesContext = FacesContext.getCurrentInstance();
		MethodExpression me = getCompleteMethod();
		
		if(me != null && event instanceof org.primefaces.event.AutoCompleteEvent) {
			suggestions = (List) me.invoke(facesContext.getELContext(), new Object[] {((org.primefaces.event.AutoCompleteEvent) event).getQuery()});
            
            if(suggestions == null) {
                suggestions = new ArrayList();
            }

            facesContext.renderResponse();
		}
	}

    @Override
    public void validate(FacesContext context) {
        super.validate(context);
       
        if(isValid()) {
            for(Iterator<String> customEventIter = customEvents.keySet().iterator(); customEventIter.hasNext();) {
                AjaxBehaviorEvent behaviorEvent = customEvents.get(customEventIter.next());
                SelectEvent selectEvent = new SelectEvent(this, behaviorEvent.getBehavior(), getValue());

                if(behaviorEvent.getPhaseId().equals(PhaseId.APPLY_REQUEST_VALUES)) {
                    selectEvent.setPhaseId(PhaseId.PROCESS_VALIDATIONS);
                }

                super.queueEvent(selectEvent);
            }
        }
    }

    public List<Column> getColums() {
        List<Column> columns = new ArrayList<Column>();
        
        for(UIComponent kid : this.getChildren()) {
            if(kid instanceof Column)
                columns.add((Column) kid);
        }

        return columns;
    }

    public List getSuggestions() {
        return this.suggestions;
    }
