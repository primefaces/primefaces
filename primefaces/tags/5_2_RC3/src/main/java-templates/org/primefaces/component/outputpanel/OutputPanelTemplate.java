import javax.faces.context.FacesContext;
    
    public static final String CONTAINER_CLASS = "ui-outputpanel ui-widget";
    public static final String LOADING_CLASS = "ui-outputpanel-loading ui-widget";

    public boolean isContentLoad(FacesContext context) {
        String clientId = this.getClientId(context);

        return context.getExternalContext().getRequestParameterMap().containsKey(clientId + "_load");
    }