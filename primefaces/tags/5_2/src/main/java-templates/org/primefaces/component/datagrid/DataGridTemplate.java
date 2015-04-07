import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.event.FacesEvent;
import javax.faces.model.DataModel;
import org.primefaces.context.RequestContext;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.UnselectEvent;
import org.primefaces.event.data.PageEvent;
import org.primefaces.model.LazyDataModel;
import org.primefaces.util.Constants;

	public static final String DATAGRID_CLASS = "ui-datagrid ui-widget";
    public static final String HEADER_CLASS = "ui-datagrid-header ui-widget-header ui-corner-top";
    public static final String FOOTER_CLASS = "ui-datagrid-footer ui-widget-header ui-corner-bottom";
    public static final String TABLE_CONTENT_CLASS = "ui-datagrid-content ui-widget-content";
    public static final String EMPTY_CONTENT_CLASS = "ui-datagrid-content ui-datagrid-content-empty ui-widget-content";
	public static final String TABLE_CLASS = "ui-datagrid-data";
	public static final String TABLE_ROW_CLASS = "ui-datagrid-row";
    public static final String GRID_CONTENT_CLASS = "ui-datagrid-content ui-widget-content ui-grid ui-grid-responsive";
    public static final String GRID_ROW_CLASS = "ui-grid-row";
    public static final String COLUMN_CLASS = "ui-datagrid-column";

    public static final String MOBILE_DATAGRID_CLASS = "ui-datagrid";
    public static final String MOBILE_HEADER_CLASS = "ui-datagrid-header ui-bar ui-bar-b";
    public static final String MOBILE_FOOTER_CLASS = "ui-datagrid-footer ui-bar ui-bar-b";
    public static final String MOBILE_CONTENT_CLASS = "ui-datagrid-content ui-responsive";
    public static final String MOBILE_EMPTY_CONTENT_CLASS = "ui-datagrid-content ui-datagrid-content-empty";

    private static final Collection<String> EVENT_NAMES = Collections.unmodifiableCollection(Arrays.asList("page"));

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
    public Collection<String> getEventNames() {
        return EVENT_NAMES;
    }

    @Override
    public void queueEvent(FacesEvent event) {
        FacesContext context = getFacesContext();

        if(isRequestSource(context) && event instanceof AjaxBehaviorEvent) {
            setRowIndex(-1);
            Map<String,String> params = context.getExternalContext().getRequestParameterMap();
            String eventName = params.get(Constants.RequestParams.PARTIAL_BEHAVIOR_EVENT_PARAM);

            if(eventName.equals("page")) {
                AjaxBehaviorEvent behaviorEvent = (AjaxBehaviorEvent) event;
                String clientId = this.getClientId(context);
                int rows = this.getRowsToRender();
                int first = Integer.parseInt(params.get(clientId + "_first"));
                int page = rows > 0 ? (int) (first / rows) : 0;
        
                PageEvent pageEvent = new PageEvent(this, behaviorEvent.getBehavior(), page);
                pageEvent.setPhaseId(behaviorEvent.getPhaseId());

                super.queueEvent(pageEvent);
            }
        }
        else {
            super.queueEvent(event);
        }
    }