import java.util.List;
import javax.faces.model.DataModel;
import org.primefaces.context.RequestContext;
import org.primefaces.model.LazyDataModel;

	public static final String DATAGRID_CLASS = "ui-datagrid ui-widget";
    public static final String CONTENT_CLASS = "ui-datagrid-content ui-widget-content";
    public static final String EMPTY_CONTENT_CLASS = "ui-datagrid-content  ui-datagrid-content-empty ui-widget-content";
	public static final String TABLE_CLASS = "ui-datagrid-data";
	public static final String TABLE_ROW_CLASS = "ui-datagrid-row";
	public static final String TABLE_COLUMN_CLASS = "ui-datagrid-column";
    public static final String HEADER_CLASS = "ui-datagrid-header ui-widget-header ui-corner-top";
    public static final String FOOTER_CLASS = "ui-datagrid-footer ui-widget-header ui-corner-bottom";

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