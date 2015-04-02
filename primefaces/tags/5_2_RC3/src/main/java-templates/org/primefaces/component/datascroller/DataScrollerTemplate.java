    public final static String CONTAINER_CLASS = "ui-datascroller ui-widget";
    public final static String INLINE_CONTAINER_CLASS = "ui-datascroller ui-datascroller-inline ui-widget";
    public final static String HEADER_CLASS = "ui-datascroller-header ui-widget-header ui-corner-top";
    public final static String CONTENT_CLASS = "ui-datascroller-content ui-widget-content";
    public final static String LIST_CLASS = "ui-datascroller-list";
    public final static String ITEM_CLASS = "ui-datascroller-item";
    public final static String LOADER_CLASS = "ui-datascroller-loader";
    public final static String LOADING_CLASS = "ui-datascroller-loading";

    public boolean isLoadRequest() {
        FacesContext context = getFacesContext();
        return context.getExternalContext().getRequestParameterMap().containsKey(this.getClientId(context) + "_load");
    }