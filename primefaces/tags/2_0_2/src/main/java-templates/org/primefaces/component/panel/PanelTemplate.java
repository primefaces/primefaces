import org.primefaces.component.menu.Menu;
import javax.faces.component.UIComponent;

	public static final String PANEL_CLASS = "pf-panel";
	public static final String PANEL_HEADER_CLASS = "pf-panel-hd";
	public static final String PANEL_HEADER_CONTROLS_CLASS = "pf-panel-hd-controls";
	public static final String PANEL_BODY_CLASS = "pf-panel-bd";
	public static final String PANEL_FOOTER_CLASS = "pf-panel-ft";
	public static final String PANEL_CLOSER_CLASS = "pf-panel-closer";
	public static final String PANEL_TOGGLER_EXPANDED_CLASS = "pf-panel-toggler-expanded";
	public static final String PANEL_TOGGLER_COLLAPSED_CLASS = "pf-panel-toggler-collapsed";
	public static final String PANEL_OPTIONS_CLASS = "pf-panel-options";
	
	public void broadcast(javax.faces.event.FacesEvent event) throws javax.faces.event.AbortProcessingException {
		super.broadcast(event);
		
		FacesContext facesContext = FacesContext.getCurrentInstance();
		
		if(event instanceof org.primefaces.event.CloseEvent) {
			MethodExpression closeMe = getCloseListener();
			
			if(closeMe != null) {
				closeMe.invoke(facesContext.getELContext(), new Object[] {event});
			}
		} else if(event instanceof org.primefaces.event.ToggleEvent) {
			MethodExpression toggleMe = getToggleListener();
			
			if(toggleMe != null) {
				toggleMe.invoke(facesContext.getELContext(), new Object[] {event});
			}
		}
	}
	
	private Menu optionsMenu;
	
	public Menu  getOptionsMenu() {
		if(optionsMenu == null) {
			UIComponent optionsFacet = getFacet("options");
			if(optionsFacet != null)
				optionsMenu = (Menu) optionsFacet;
		}
		
		return optionsMenu;
	}