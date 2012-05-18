import org.primefaces.component.calendar.Calendar;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.UnselectEvent;
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
import javax.el.ValueExpression;
import javax.faces.convert.Converter;
import javax.faces.component.behavior.Behavior;

    private static final Collection<String> EVENT_NAMES = Collections.unmodifiableCollection(Arrays.asList("blur","change","valueChange","click","dblclick","focus","keydown","keypress","keyup","mousedown","mousemove","mouseout","mouseover","mouseup","select", "itemSelect", "itemUnselect"));
    
    public final static String STYLE_CLASS = "ui-autocomplete";
    public final static String MULTIPLE_STYLE_CLASS = "ui-autocomplete-multiple";
    public final static String INPUT_CLASS = "ui-autocomplete-input ui-inputfield ui-widget ui-state-default ui-corner-all";
    public final static String INPUT_WITH_DROPDOWN_CLASS = "ui-autocomplete-input ui-inputfield ui-widget ui-state-default ui-corner-left";
    public final static String DROPDOWN_ICON_CLASS = "ui-autocomplete-dropdown ui-state-default ui-corner-right";
    public final static String PANEL_CLASS = "ui-autocomplete-panel ui-widget-content ui-corner-all ui-helper-hidden ui-shadow";
    public final static String LIST_CLASS = "ui-autocomplete-items ui-autocomplete-list ui-widget-content ui-widget ui-corner-all ui-helper-reset";
    public final static String TABLE_CLASS = "ui-autocomplete-items ui-autocomplete-table ui-widget-content ui-widget ui-corner-all ui-helper-reset";
    public final static String ITEM_CLASS = "ui-autocomplete-item ui-autocomplete-list-item ui-corner-all";
    public final static String ROW_CLASS = "ui-autocomplete-item ui-autocomplete-row ui-corner-all";
    public final static String TOKEN_DISPLAY_CLASS = "ui-autocomplete-token ui-state-active ui-corner-all";
    public final static String TOKEN_LABEL_CLASS = "ui-autocomplete-token-label";
    public final static String TOKEN_ICON_CLASS = "ui-autocomplete-token-icon ui-icon ui-icon-close";
    public final static String TOKEN_INPUT_CLASS = "ui-autocomplete-input-token";
    public final static String MULTIPLE_CONTAINER_CLASS = "ui-autocomplete-multiple-container ui-widget ui-inputfield ui-state-default ui-corner-all";
    public final static String ITEMTIP_CONTENT_CLASS = "ui-autocomplete-itemtip-content";

    @Override
    public Collection<String> getEventNames() {
        return EVENT_NAMES;
    }

    @Override
    public void queueEvent(FacesEvent event) {
        FacesContext context = FacesContext.getCurrentInstance();
        Map<String,String> params = context.getExternalContext().getRequestParameterMap();
        String eventName = params.get(Constants.PARTIAL_BEHAVIOR_EVENT_PARAM);

        if(eventName != null && event instanceof AjaxBehaviorEvent) {
            AjaxBehaviorEvent ajaxBehaviorEvent = (AjaxBehaviorEvent) event;

            if(eventName.equals("itemSelect")) {
                Object selectedItemValue = convertValue(context, params.get(this.getClientId(context) + "_itemSelect"));
                SelectEvent selectEvent = new SelectEvent(this, (Behavior) ajaxBehaviorEvent.getBehavior(), selectedItemValue);
                selectEvent.setPhaseId(ajaxBehaviorEvent.getPhaseId());
                super.queueEvent(selectEvent);
            }
            else if(eventName.equals("itemUnselect")) {
                Object unselectedItemValue = convertValue(context, params.get(this.getClientId(context) + "_itemUnselect"));
                UnselectEvent unselectEvent = new UnselectEvent(this, (Behavior) ajaxBehaviorEvent.getBehavior(), unselectedItemValue);
                unselectEvent.setPhaseId(ajaxBehaviorEvent.getPhaseId());
                super.queueEvent(unselectEvent);
            }
            else {
                //e.g. blur, focus, change
                super.queueEvent(event);
            }
        }
        else {
            //e.g. valueChange, autoCompleteEvent
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

    private Converter getConverter(FacesContext context) {
        Converter converter = this.getConverter();

        if(converter != null) {
            return converter;
        } else {
            ValueExpression ve = this.getValueExpression("value");

            if(ve != null) {
                Class<?> valueType = ve.getType(context.getELContext());
                
                if(valueType != null)
                    return context.getApplication().createConverter(valueType);
            }
        }

        return null;
    }

    private Object convertValue(FacesContext context, String submittedItemValue) {
        Converter converter = getConverter();

        if(converter == null)
            return submittedItemValue;
        else 
            return converter.getAsObject(context, this, submittedItemValue);
    }

    public String getInputClientId() {
        return this.getClientId(getFacesContext()) + "_input";
    }
