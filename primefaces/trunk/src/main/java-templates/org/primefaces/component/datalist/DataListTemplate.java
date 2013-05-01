import java.util.Collection;
import java.util.Collections;
import java.util.Arrays;
import java.util.Map;
import javax.faces.FacesException;
import javax.faces.event.PhaseId;
import javax.faces.component.UIColumn;
import javax.faces.component.behavior.Behavior;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.event.BehaviorEvent;
import javax.faces.event.FacesEvent;
import javax.faces.event.FacesListener;
import javax.faces.model.DataModel;
import org.primefaces.context.RequestContext;
import org.primefaces.event.data.PageEvent;
import org.primefaces.model.LazyDataModel;
import org.primefaces.util.Constants;

	public static final String DATALIST_CLASS = "ui-datalist ui-widget";
    public static final String CONTENT_CLASS = "ui-datalist-content ui-widget-content";
	public static final String LIST_CLASS = "ui-datalist-data";
	public static final String LIST_ITEM_CLASS = "ui-datalist-item";
    public static final String HEADER_CLASS = "ui-datalist-header ui-widget-header ui-corner-top";
    public static final String FOOTER_CLASS = "ui-datalist-footer ui-widget-header ui-corner-top";
	public static final String DATALIST_EMPTYMESSAGE_CLASS = "ui-datalist-empty-message";

    private static final Collection<String> EVENT_NAMES = Collections.unmodifiableCollection(Arrays.asList("page"));

    public String getListTag() {
		String type = getType();
		
		if(type.equalsIgnoreCase("unordered"))
			return "ul";
		else if(type.equalsIgnoreCase("ordered"))
			return "ol";
		else if(type.equalsIgnoreCase("definition"))
			return "dl";
        else if(type.equalsIgnoreCase("none"))
            return null;
        else
			throw new FacesException("DataList '" + this.getClientId() + "' has invalid list type:'" + type + "'");
	}
	
	public boolean isDefinition() {
		return getType().equalsIgnoreCase("definition");
	}

    @Override
    public void processDecodes(FacesContext context) {
        if(!isRendered()) {
            return;
        }

		if(getVar() == null) {
            pushComponentToEL(context, this);
            iterateChildren(context, PhaseId.APPLY_REQUEST_VALUES);
            decode(context);
            popComponentFromEL(context);
        } 
        else {
            super.processDecodes(context);
        }
	}

    @Override
    public void processValidators(FacesContext context) {
        if(!isRendered()) {
            return;
        }

		if(getVar() == null) {
            pushComponentToEL(context, this);
            iterateChildren(context, PhaseId.PROCESS_VALIDATIONS);
            popComponentFromEL(context);
        } 
        else {
            super.processValidators(context);
        }
	}
    
    @Override
    public void processUpdates(FacesContext context) {
        if(!isRendered()) {
            return;
        }

		if(getVar() == null) {
            pushComponentToEL(context, this);
            iterateChildren(context, PhaseId.UPDATE_MODEL_VALUES);
            popComponentFromEL(context);
        } 
        else {
            super.processUpdates(context);
        }
	}

    protected void iterateChildren(FacesContext context, PhaseId phaseId) {
        setRowIndex(-1);
        if(getChildCount() > 0) {
            for(UIComponent kid : this.getChildren()) {
                if(phaseId == PhaseId.APPLY_REQUEST_VALUES)
                    kid.processDecodes(context);
                else if (phaseId == PhaseId.PROCESS_VALIDATIONS)
                    kid.processValidators(context);
                else if (phaseId == PhaseId.UPDATE_MODEL_VALUES)
                    kid.processUpdates(context);
                else
                    throw new IllegalArgumentException();
            }
        }
    }

    public void queueEvent(FacesEvent event) {
        if(getVar() != null) {
            super.queueEvent(event);
        }
        else {
            if(event == null) {
                throw new NullPointerException();
            }
            
            UIComponent parent = getParent();
            if(parent == null)
                throw new IllegalStateException();
            else
                parent.queueEvent(event);
        }
            FacesContext context = getFacesContext();

        if(isRequestSource(context) && event instanceof AjaxBehaviorEvent) {
            setRowIndex(-1);
            Map<String,String> params = context.getExternalContext().getRequestParameterMap();
            String eventName = params.get(Constants.PARTIAL_BEHAVIOR_EVENT_PARAM);
            String clientId = this.getClientId(context);
            FacesEvent wrapperEvent = null;

            AjaxBehaviorEvent behaviorEvent = (AjaxBehaviorEvent) event;

            if(eventName.equals("page")) {
                int rows = this.getRowsToRender();
                int first = Integer.parseInt(params.get(clientId + "_first"));
                int page = rows > 0 ? (int) (first / rows) : 0;
        
                wrapperEvent = new PageEvent(this, behaviorEvent.getBehavior(), page);
            }
        }
    }


    public void broadcast(FacesEvent event) throws AbortProcessingException {
        if(getVar() != null) {
            super.broadcast(event);
        }
        else {

            if(event == null) {
                throw new NullPointerException();
            }
            if(event instanceof BehaviorEvent) {
                BehaviorEvent behaviorEvent = (BehaviorEvent) event;
                Behavior behavior = behaviorEvent.getBehavior();
                behavior.broadcast(behaviorEvent);
            }
        }
    }

    public void loadLazyData() {
        DataModel model = getDataModel();
        
        if(model != null && model instanceof LazyDataModel) {            
            LazyDataModel lazyModel = (LazyDataModel) model;

            List<?> data = lazyModel.load(getFirst(), getRows(), null, null, null);
            
            lazyModel.setPageSize(getRows());
            lazyModel.setWrappedData(data);

            //Update paginator for callback
            if(this.isPaginator()) {
                RequestContext requestContext = RequestContext.getCurrentInstance();

                if(requestContext != null) {
                    requestContext.addCallbackParam("totalRecords", lazyModel.getRowCount());
                }
            }
        }
    }

    public boolean isRequestSource(FacesContext context) {
        String partialSource = context.getExternalContext().getRequestParameterMap().get(Constants.PARTIAL_SOURCE_PARAM);

        return partialSource != null && this.getClientId(context).equals(partialSource);
    }

    @Override
    public Collection<String> getEventNames() {
        return EVENT_NAMES;
    }
