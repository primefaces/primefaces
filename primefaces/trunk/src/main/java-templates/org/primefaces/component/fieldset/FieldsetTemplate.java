
    public static final String FIELDSET_CLASS = "ui-fieldset ui-widget ui-widget-content ui-corner-all";
    public static final String TOGGLEABLE_FIELDSET_CLASS = FIELDSET_CLASS + " ui-fieldset-toggleable";
    public static final String CONTENT_CLASS = "ui-fieldset-content";
    public static final String LEGEND_CLASS = "ui-fieldset-legend ui-corner-all ui-state-default";
    public static final String TOGGLER_CLASS = "ui-fieldset-toggler ui-icon";
    public static final String TOGGLER_MINUS_CLASS = "ui-icon-minusthick";
    public static final String TOGGLER_PLUS_CLASS = "ui-icon-plusthick";


	public void broadcast(javax.faces.event.FacesEvent event) throws javax.faces.event.AbortProcessingException {
		super.broadcast(event);

		FacesContext facesContext = FacesContext.getCurrentInstance();

		if(event instanceof org.primefaces.event.ToggleEvent) {
			MethodExpression toggleMe = getToggleListener();

			if(toggleMe != null) {
				toggleMe.invoke(facesContext.getELContext(), new Object[] {event});
			}
		}
	}