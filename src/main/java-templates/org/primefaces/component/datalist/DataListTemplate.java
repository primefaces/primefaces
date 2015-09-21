import javax.faces.FacesException;
import javax.faces.event.PhaseId;
import javax.faces.component.UIColumn;
import javax.faces.component.behavior.Behavior;
import javax.faces.event.BehaviorEvent;
import javax.faces.event.FacesEvent;
import javax.faces.event.FacesListener;
import javax.faces.model.DataModel;
import org.primefaces.context.RequestContext;
import org.primefaces.model.LazyDataModel;
import org.primefaces.mobile.event.SwipeEvent;
import org.primefaces.event.SelectEvent;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.primefaces.util.Constants;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.event.FacesEvent;
import org.primefaces.event.data.PageEvent;

	public static final String DATALIST_CLASS = "ui-datalist ui-widget";
    public static final String CONTENT_CLASS = "ui-datalist-content ui-widget-content";
	public static final String LIST_CLASS = "ui-datalist-data";
    public static final String NO_BULLETS_CLASS = "ui-datalist-nobullets";
	public static final String LIST_ITEM_CLASS = "ui-datalist-item";
    public static final String HEADER_CLASS = "ui-datalist-header ui-widget-header ui-corner-top";
    public static final String FOOTER_CLASS = "ui-datalist-footer ui-widget-header ui-corner-bottom";
	public static final String DATALIST_EMPTYMESSAGE_CLASS = "ui-datalist-empty-message";

    public static final String MOBILE_CONTENT_CLASS = "ui-datalist-content";

    private static final Collection<String> EVENT_NAMES = Collections.unmodifiableCollection(Arrays.asList("page","swipeleft","swiperight","tap","taphold"));

    @Override
    public Collection<String> getEventNames() {
        return EVENT_NAMES;
    }

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
        String partialSource = context.getExternalContext().getRequestParameterMap().get(Constants.RequestParams.PARTIAL_SOURCE_PARAM);

        return partialSource != null && this.getClientId(context).equals(partialSource);
    }

    @Override
    public void queueEvent(FacesEvent event) {
        FacesContext context = getFacesContext();

        if(isRequestSource(context) && event instanceof AjaxBehaviorEvent) {
            setRowIndex(-1);
            AjaxBehaviorEvent behaviorEvent = (AjaxBehaviorEvent) event;
            Map<String,String> params = context.getExternalContext().getRequestParameterMap();
            String eventName = params.get(Constants.RequestParams.PARTIAL_BEHAVIOR_EVENT_PARAM);

            if(eventName.equals("page")) {
                String clientId = this.getClientId(context);
                int rows = this.getRowsToRender();
                int first = Integer.parseInt(params.get(clientId + "_first"));
                int page = rows > 0 ? (int) (first / rows) : 0;
        
                PageEvent pageEvent = new PageEvent(this, behaviorEvent.getBehavior(), page);
                pageEvent.setPhaseId(behaviorEvent.getPhaseId());

                super.queueEvent(pageEvent);
            }
            else if(eventName.equals("swipeleft")||eventName.equals("swiperight")) {
                String clientId = this.getClientId(context);
                int index = Integer.parseInt(params.get(clientId + "_item"));
                this.setRowIndex(index);
        
                SwipeEvent swipeEvent = new SwipeEvent(this, behaviorEvent.getBehavior(), this.getRowData());
                swipeEvent.setPhaseId(behaviorEvent.getPhaseId());

                this.setRowIndex(-1);
                super.queueEvent(swipeEvent);
            }
            else if(eventName.equals("tap")||eventName.equals("taphold")) {
                String clientId = this.getClientId(context);
                int index = Integer.parseInt(params.get(clientId + "_item"));
                this.setRowIndex(index);
        
                SelectEvent selectEvent = new SelectEvent(this, behaviorEvent.getBehavior(), this.getRowData());
                selectEvent.setPhaseId(behaviorEvent.getPhaseId());

                this.setRowIndex(-1);
                super.queueEvent(selectEvent);
            }
        }
        else {
            super.queueEvent(event);
        }
    }