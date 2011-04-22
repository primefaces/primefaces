import javax.faces.component.UIComponent;
import org.primefaces.component.layout.LayoutUnit;

    public final static String UNIT_CLASS = "ui-layout-unit ui-widget ui-widget-content ui-corner-all";
    public final static String UNIT_HEADER_CLASS = "ui-layout-unit-header ui-widget-header ui-corner-all";
    public final static String UNIT_CONTENT_CLASS = "ui-layout-unit-content ui-widget-content";
    public final static String UNIT_FOOTER_CLASS = "ui-layout-unit-footer ui-widget-header ui-corner-all";
    public final static String UNIT_HEADER_TITLE_CLASS = "ui-layout-unit-header-title";
    public final static String UNIT_FOOTER_TITLE_CLASS = "ui-layout-unit-footer-title";
    public final static String UNIT_HEADER_ICON_CLASS = "ui-layout-unit-header-icon ui-state-default ui-corner-all";

	protected LayoutUnit getLayoutUnitByPosition(String name) {
		for(UIComponent child : getChildren()) {
			if(child instanceof LayoutUnit) {
				LayoutUnit layoutUnit = (LayoutUnit) child;
				
				if(layoutUnit.getPosition().equalsIgnoreCase(name))
					return layoutUnit;
			}
		}
		
		return null;
	}
	
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
		} else if(event instanceof org.primefaces.event.ResizeEvent) {
			MethodExpression resizeMe = getResizeListener();
			
			if(resizeMe != null) {
				resizeMe.invoke(facesContext.getELContext(), new Object[] {event});
			}
		}
	}

    public boolean isNested() {
        return this.getParent() instanceof LayoutUnit;
    }

    public boolean isElementLayout() {
        return !isNested() && !isFullPage();
    }