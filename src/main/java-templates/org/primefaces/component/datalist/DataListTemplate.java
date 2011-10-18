import javax.faces.FacesException;

	public static final String DATALIST_CLASS = "ui-datalist ui-widget";
    public static final String CONTENT_CLASS = "ui-datalist-content ui-widget";
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

    public boolean isPagingRequest(FacesContext context) {
        return context.getExternalContext().getRequestParameterMap().containsKey(getClientId(context) + "_ajaxPaging");
    }

    protected void updatePaginationMetadata(FacesContext context) {
        ValueExpression firstVe = this.getValueExpression("first");
        ValueExpression rowsVe = this.getValueExpression("rows");
        ValueExpression pageVE = this.getValueExpression("page");

        if(firstVe != null)
            firstVe.setValue(context.getELContext(), getFirst());
        if(rowsVe != null)
            rowsVe.setValue(context.getELContext(), getRows());
        if(pageVE != null)
            pageVE.setValue(context.getELContext(), getPage());
    }

    @Override
    public void processDecodes(FacesContext context) {
		if(isPagingRequest(context)) {
            this.decode(context);

            updatePaginationMetadata(context);

            context.renderResponse();
        }
        else {
            super.processDecodes(context);
        }
	}

    public void calculatePage() {
        int rows = this.getRows();
        int currentPage = this.getPage();
        int numberOfPages = (int) Math.ceil(this.getRowCount() * 1d / rows);

        if(currentPage > numberOfPages && numberOfPages > 0) {
            currentPage = numberOfPages;

            this.setPage(currentPage);
            this.setFirst((currentPage-1) * rows);
        }
    }