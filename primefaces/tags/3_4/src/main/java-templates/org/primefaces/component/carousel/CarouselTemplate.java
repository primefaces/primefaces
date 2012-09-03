    public final static String CONTAINER_CLASS = "ui-carousel ui-widget ui-widget-content ui-corner-all";
    public final static String ITEM_CLASS = "ui-carousel-item ui-widget-content ui-corner-all";
    public final static String HEADER_CLASS = "ui-carousel-header ui-widget-header ui-corner-all";
    public final static String HEADER_TITLE_CLASS = "ui-carousel-header-title";
    public final static String FOOTER_CLASS = "ui-carousel-footer ui-widget-header ui-corner-all";
    public final static String HORIZONTAL_NEXT_BUTTON = "ui-carousel-button ui-carousel-next-button ui-icon ui-icon-circle-triangle-e";
    public final static String HORIZONTAL_PREV_BUTTON = "ui-carousel-button ui-carousel-prev-button ui-icon ui-icon-circle-triangle-w";
    public final static String VERTICAL_NEXT_BUTTON = "ui-carousel-button ui-carousel-next-button ui-icon ui-icon-circle-triangle-s";
    public final static String VERTICAL_PREV_BUTTON = "ui-carousel-button ui-carousel-prev-button ui-icon ui-icon-circle-triangle-n";
    public final static String VIEWPORT_CLASS = "ui-carousel-viewport";
    public final static String VERTICAL_VIEWPORT_CLASS = "ui-carousel-viewport ui-carousel-vertical-viewport";
    public final static String PAGE_LINKS_CONTAINER_CLASS = "ui-carousel-page-links";
    public final static String PAGE_LINK_CLASS = "ui-icon ui-carousel-page-link ui-icon-radio-off";
    public final static String DROPDOWN_CLASS = "ui-carousel-dropdown ui-widget ui-state-default ui-corner-left";

    public int getRenderedChildCount() {
        int i = 0;
    
        for(UIComponent child : getChildren()) {
            if(child.isRendered()) {
                i++;
            }
        }

        return i;
    }

    public int getFirstVisible() {
		return (java.lang.Integer) getStateHelper().eval("firsVisible", 0);
	}
	public void setFirstVisible(int _firstVisible) {
		getStateHelper().put("firsVisible", _firstVisible);
	}

    /** Following three overrides enables processing of all items **/
    public void processDecodes(FacesContext facesContext) {
        int rows = this.getRows();
		this.setRows(this.getRowCount());
        super.processDecodes(facesContext);
        this.setRows(rows);
    }
	
	public void processValidators(FacesContext facesContext) {
        int rows = this.getRows();
		this.setRows(this.getRowCount());
        super.processValidators(facesContext);
        this.setRows(rows);
    }
	
	public void processUpdates(FacesContext facesContext) {
        int rows = this.getRows();
		this.setRows(this.getRowCount());
        super.processUpdates(facesContext);
        this.setRows(rows);
	}
    