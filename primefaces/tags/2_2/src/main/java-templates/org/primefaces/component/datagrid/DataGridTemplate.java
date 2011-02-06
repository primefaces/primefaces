	public static final String DATAGRID_CLASS = "ui-datagrid ui-widget";
    public static final String CONTENT_CLASS = "ui-datagrid-content";
	public static final String TABLE_CLASS = "ui-datagrid-data";
	public static final String TABLE_ROW_CLASS = "ui-datagrid-row";
	public static final String TABLE_COLUMN_CLASS = "ui-datagrid-column";

    public boolean isPagingRequest(FacesContext context) {
        return context.getExternalContext().getRequestParameterMap().containsKey(getClientId(context) + "_ajaxPaging");
    }