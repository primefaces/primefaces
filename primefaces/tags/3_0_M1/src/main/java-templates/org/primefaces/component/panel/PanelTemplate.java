import org.primefaces.component.menu.Menu;
import javax.faces.component.UIComponent;

	public static final String PANEL_CLASS = "ui-panel ui-widget ui-widget-content ui-corner-all";
	public static final String PANEL_TITLEBAR_CLASS = "ui-panel-titlebar ui-widget-header ui-corner-all";
	public static final String PANEL_TITLE_CLASS = "ui-panel-title";
	public static final String PANEL_TITLE_ICON_CLASS = "ui-panel-titlebar-icon ui-corner-all ui-state-default";
	public static final String PANEL_CONTENT_CLASS = "ui-panel-content ui-widget-content";
	public static final String PANEL_FOOTER_CLASS = "ui-panel-footer ui-widget-content";
	
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
			if(optionsFacet != null) {
                if(optionsFacet instanceof Menu)
                    optionsMenu = (Menu) optionsFacet;
                else
                    optionsMenu = (Menu) optionsFacet.getChildren().get(0);
            }

		}

		return optionsMenu;
	}