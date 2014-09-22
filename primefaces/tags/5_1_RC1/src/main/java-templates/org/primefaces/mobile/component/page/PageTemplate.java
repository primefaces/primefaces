import javax.faces.context.FacesContext;

    @Override
    public void processDecodes(FacesContext context) {
        if(!isLazyloadRequest(context)) {
            super.processDecodes(context);
        }
    }

    @Override
    public void processValidators(FacesContext context) {
        if(!isLazyloadRequest(context)) {
            super.processValidators(context);
        }
    }

    @Override
    public void processUpdates(FacesContext context) {
        if(!isLazyloadRequest(context)) {
            super.processUpdates(context);
        }
    }

    public boolean isLazyloadRequest(FacesContext context) {
        return context.getExternalContext().getRequestParameterMap().containsKey(this.getClientId(context) + "_lazyload");
    }