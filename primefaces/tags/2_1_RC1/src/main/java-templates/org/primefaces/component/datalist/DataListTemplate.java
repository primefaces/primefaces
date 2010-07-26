import javax.faces.FacesException;

	public static final String CONTAINER_CLASS = "ui-datalist ui-widget";
	public static final String LIST_CLASS = "ui-datalist-data ui-widget-content ui-corner-all";
	public static final String LIST_ITEM_CLASS = "ui-datalist-item";
	
	public String getListTag() {
		String type = getType();
		
		if(type.equalsIgnoreCase("unordered"))
			return "ul";
		else if(type.equalsIgnoreCase("ordered"))
			return "ol";
		else if(type.equalsIgnoreCase("definition"))
			return "dl";
		else
			throw new FacesException("DataList '" + this.getClientId() + "' has invalid list type:'" + type + "'");
	}
	
	public boolean isDefinition() {
		return getType().equalsIgnoreCase("definition");
	}