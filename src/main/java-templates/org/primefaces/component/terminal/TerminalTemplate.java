
    public static final String CONTAINER_CLASS = "ui-terminal ui-widget ui-widget-content ui-corner-all";
    public static final String WELCOME_MESSAGE_CLASS = "ui-terminal-welcome";
    public static final String CONTENT_CLASS = "ui-terminal-content";
    public static final String PROMPT_CLASS = "ui-terminal-prompt";
    public static final String INPUT_CLASS = "ui-terminal-input";

    public boolean isCommandRequest() {
        FacesContext context = getFacesContext();
        return context.getExternalContext().getRequestParameterMap().containsKey(this.getClientId(context) + "_command");
    }