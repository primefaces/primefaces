import javax.faces.FacesException;
import javax.faces.event.PhaseId;
import javax.faces.component.UIColumn;

	public static final String DATALIST_CLASS = "ui-datalist ui-widget";
    public static final String HEADER_CLASS = "ui-datalist-header ui-widget-header ui-corner-top";
    public static final String CONTENT_CLASS = "ui-datalist-content ui-widget-content";
	public static final String LIST_CLASS = "ui-datalist-data";
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

            if(getVar() == null)
                iterateChildren(context, PhaseId.PROCESS_VALIDATIONS);
        }
	}

    @Override
    public void processValidators(FacesContext context) {
		super.processValidators(context);
        
        if(getVar() == null)
            iterateChildren(context, PhaseId.PROCESS_VALIDATIONS);
	}
    
    @Override
    public void processUpdates(FacesContext context) {
		super.processUpdates(context);
        
        if(getVar() == null)
            iterateChildren(context, PhaseId.UPDATE_MODEL_VALUES);
	}

    protected void iterateChildren(FacesContext context, PhaseId phaseId) {
        setRowIndex(-1);
        if(getChildCount() > 0) {
            for(UIComponent column : getChildren()) {
                if(!(column instanceof UIColumn) || !column.isRendered()) {
                    continue;
                }
                
                if(column.getChildCount() > 0) {
                    for(UIComponent grandkid : column.getChildren()) {
                        if(phaseId == PhaseId.APPLY_REQUEST_VALUES)
                            grandkid.processDecodes(context);
                        else if (phaseId == PhaseId.PROCESS_VALIDATIONS)
                            grandkid.processValidators(context);
                        else if (phaseId == PhaseId.UPDATE_MODEL_VALUES)
                            grandkid.processUpdates(context);
                        else
                            throw new IllegalArgumentException();
                    }
                }
            }
        }
    }