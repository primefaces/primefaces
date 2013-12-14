
    public final static String CONTAINER_CLASS = "ui-datascroller ui-widget ui-widget-content ui-corner-all";
    public final static String LIST_CLASS = "ui-datascroller-list";
    public final static String ITEM_CLASS = "ui-datascroller-item";

    public boolean isLoadRequest() {
        FacesContext context = getFacesContext();
        return context.getExternalContext().getRequestParameterMap().containsKey(this.getClientId(context) + "_load");
    }